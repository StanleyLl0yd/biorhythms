#!/usr/bin/env python3
import argparse
import re
import struct
import sys
import zipfile
from pathlib import Path

PAGE_SIZE = 16 * 1024
PAGE_ALIGNMENT_16K = 2
PT_LOAD = 1
ALLOWED_ABIS = {"arm64-v8a", "armeabi-v7a", "x86_64", "x86"}


def fail(message: str) -> None:
    raise RuntimeError(message)


def read_varint(data: bytes, offset: int) -> tuple[int, int]:
    value = 0
    shift = 0
    while True:
        if offset >= len(data):
            fail("Truncated protobuf varint")
        byte = data[offset]
        offset += 1
        value |= (byte & 0x7F) << shift
        if byte < 0x80:
            return value, offset
        shift += 7
        if shift > 63:
            fail("Invalid protobuf varint")


def protobuf_fields(data: bytes):
    offset = 0
    while offset < len(data):
        key, offset = read_varint(data, offset)
        field_number = key >> 3
        wire_type = key & 0x07
        if wire_type == 0:
            value, offset = read_varint(data, offset)
        elif wire_type == 1:
            value = data[offset : offset + 8]
            offset += 8
        elif wire_type == 2:
            length, offset = read_varint(data, offset)
            value = data[offset : offset + length]
            offset += length
        elif wire_type == 5:
            value = data[offset : offset + 4]
            offset += 4
        else:
            fail(f"Unsupported protobuf wire type {wire_type}")
        yield field_number, wire_type, value


def child_message(data: bytes, field_number: int) -> bytes:
    matches = [value for number, wire, value in protobuf_fields(data) if number == field_number and wire == 2]
    if len(matches) != 1:
        fail(f"Expected exactly one protobuf message field {field_number}, found {len(matches)}")
    return matches[0]


def scalar_field(data: bytes, field_number: int, default: int = 0) -> int:
    matches = [value for number, wire, value in protobuf_fields(data) if number == field_number and wire == 0]
    if not matches:
        return default
    if len(matches) != 1:
        fail(f"Expected at most one protobuf scalar field {field_number}, found {len(matches)}")
    return matches[0]


def verify_sdk_config(gradle_file: Path) -> None:
    text = gradle_file.read_text(encoding="utf-8")
    values = {}
    for key in ("compileSdk", "minSdk", "targetSdk"):
        match = re.search(rf"^\s*{key}\s*=\s*(\d+)\s*$", text, re.MULTILINE)
        if not match:
            fail(f"Could not determine {key} from {gradle_file}")
        values[key] = int(match.group(1))

    if values["minSdk"] != 26:
        fail(f"minSdk must be 26, found {values['minSdk']}")
    if values["targetSdk"] < 36:
        fail(f"targetSdk must be at least 36, found {values['targetSdk']}")
    if values["compileSdk"] < 37:
        fail(f"compileSdk must be at least 37, found {values['compileSdk']}")
    if values["targetSdk"] > values["compileSdk"]:
        fail("targetSdk cannot exceed compileSdk")

    print(
        f"SDK config: minSdk={values['minSdk']}, targetSdk={values['targetSdk']}, "
        f"compileSdk={values['compileSdk']}"
    )


def native_entries(archive: zipfile.ZipFile, prefix: str) -> dict[str, list[zipfile.ZipInfo]]:
    result: dict[str, list[zipfile.ZipInfo]] = {}
    pattern = re.compile(rf"^{re.escape(prefix)}/([^/]+)/(.+\.so)$")
    for info in archive.infolist():
        match = pattern.match(info.filename)
        if match:
            result.setdefault(match.group(1), []).append(info)
    return result


def verify_abi_set(native: dict[str, list[zipfile.ZipInfo]], label: str) -> None:
    if not native:
        print(f"{label}: no native libraries; artifact is ABI-independent")
        return

    abis = set(native)
    unexpected = abis - ALLOWED_ABIS
    if unexpected:
        fail(f"{label}: unexpected ABI(s): {', '.join(sorted(unexpected))}")
    if "arm64-v8a" not in abis:
        fail(f"{label}: native code is present but arm64-v8a is missing")

    libraries = {abi: {Path(info.filename).name for info in infos} for abi, infos in native.items()}
    for source, counterpart in (("armeabi-v7a", "arm64-v8a"), ("x86", "x86_64")):
        if source in libraries:
            missing = libraries[source] - libraries.get(counterpart, set())
            if missing:
                fail(
                    f"{label}: {source} libraries lack {counterpart} counterparts: "
                    + ", ".join(sorted(missing))
                )

    print(f"{label}: ABIs={','.join(sorted(abis))}; arm64-v8a present")


def load_alignments(elf: bytes) -> list[int]:
    if elf[:4] != b"\x7fELF":
        fail("Native library is not an ELF file")

    elf_class = elf[4]
    endianness = elf[5]
    if endianness == 1:
        endian = "<"
    elif endianness == 2:
        endian = ">"
    else:
        fail("Unsupported ELF endianness")

    if elf_class == 1:
        phoff = struct.unpack_from(endian + "I", elf, 28)[0]
        phentsize = struct.unpack_from(endian + "H", elf, 42)[0]
        phnum = struct.unpack_from(endian + "H", elf, 44)[0]
        fmt = endian + "IIIIIIII"
    elif elf_class == 2:
        phoff = struct.unpack_from(endian + "Q", elf, 32)[0]
        phentsize = struct.unpack_from(endian + "H", elf, 54)[0]
        phnum = struct.unpack_from(endian + "H", elf, 56)[0]
        fmt = endian + "IIQQQQQQ"
    else:
        fail(f"Unsupported ELF class {elf_class}")

    if phentsize < struct.calcsize(fmt):
        fail("ELF program header is smaller than expected")

    alignments = []
    for index in range(phnum):
        header = struct.unpack_from(fmt, elf, phoff + index * phentsize)
        if header[0] == PT_LOAD:
            alignments.append(header[7])
    if not alignments:
        fail("ELF file has no PT_LOAD segments")
    return alignments


def verify_elf_alignment(archive: zipfile.ZipFile, native: dict[str, list[zipfile.ZipInfo]], label: str) -> None:
    for infos in native.values():
        for info in infos:
            alignments = load_alignments(archive.read(info))
            if any(alignment < PAGE_SIZE for alignment in alignments):
                fail(
                    f"{label}: {info.filename} has PT_LOAD alignment below 16 KB: "
                    + ", ".join(hex(value) for value in alignments)
                )
    if native:
        print(f"{label}: every native ELF PT_LOAD segment is aligned to at least 16 KB")


def zip_data_offset(handle, info: zipfile.ZipInfo) -> int:
    handle.seek(info.header_offset)
    header = handle.read(30)
    if len(header) != 30:
        fail(f"Truncated ZIP local header for {info.filename}")
    values = struct.unpack("<IHHHHHIIIHH", header)
    if values[0] != 0x04034B50:
        fail(f"Invalid ZIP local header for {info.filename}")
    return info.header_offset + 30 + values[-2] + values[-1]


def verify_apk_zip_alignment(
    apk_path: Path,
    archive: zipfile.ZipFile,
    native: dict[str, list[zipfile.ZipInfo]],
) -> None:
    if not native:
        return
    with apk_path.open("rb") as handle:
        for infos in native.values():
            for info in infos:
                if info.compress_type != zipfile.ZIP_STORED:
                    fail(f"APK: {info.filename} must be stored uncompressed for direct loading")
                offset = zip_data_offset(handle, info)
                if offset % PAGE_SIZE != 0:
                    fail(f"APK: {info.filename} data offset {offset} is not 16 KB aligned")
    print("APK: all uncompressed native libraries are 16 KB ZIP-aligned")


def verify_bundle_config(aab: zipfile.ZipFile, has_native: bool) -> None:
    if not has_native:
        return
    try:
        config = aab.read("BundleConfig.pb")
    except KeyError:
        fail("AAB: BundleConfig.pb is missing")

    optimizations = child_message(config, 2)
    native_config = child_message(optimizations, 2)
    if scalar_field(native_config, 1) != 1:
        fail("AAB: uncompressed native libraries are not enabled")
    alignment = scalar_field(native_config, 2)
    if alignment != PAGE_ALIGNMENT_16K:
        fail(f"AAB: expected PAGE_ALIGNMENT_16K, found enum value {alignment}")
    print("AAB: BundleConfig requests PAGE_ALIGNMENT_16K for uncompressed native libraries")


def verify_native_sets_match(apk_native, aab_native) -> None:
    def normalized(native):
        return {
            (abi, Path(info.filename).name)
            for abi, infos in native.items()
            for info in infos
        }

    if normalized(apk_native) != normalized(aab_native):
        fail("APK and AAB contain different native library/ABI sets")
    print("APK/AAB: native library and ABI sets match")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--gradle-file", type=Path, required=True)
    parser.add_argument("--apk", type=Path, required=True)
    parser.add_argument("--aab", type=Path, required=True)
    args = parser.parse_args()

    verify_sdk_config(args.gradle_file)

    with zipfile.ZipFile(args.apk) as apk, zipfile.ZipFile(args.aab) as aab:
        apk_native = native_entries(apk, "lib")
        aab_native = native_entries(aab, "base/lib")
        verify_abi_set(apk_native, "APK")
        verify_abi_set(aab_native, "AAB")
        verify_native_sets_match(apk_native, aab_native)
        verify_elf_alignment(apk, apk_native, "APK")
        verify_elf_alignment(aab, aab_native, "AAB")
        verify_apk_zip_alignment(args.apk, apk, apk_native)
        verify_bundle_config(aab, bool(aab_native))

    print("Google Play release compatibility checks passed")
    return 0


if __name__ == "__main__":
    try:
        sys.exit(main())
    except Exception as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        sys.exit(1)
