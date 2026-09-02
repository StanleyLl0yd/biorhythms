#!/usr/bin/env python3

import argparse
import hashlib
import sys
import zipfile
from pathlib import Path

OLD_LAUNCHER_HASHES = {
    "08d5e8697b228034cd5f6e40fdea8177daadc4cfdcfeef52f520ab8ec1c61fcd",
    "99d9da236ada54ed912ed308848c8d6e441af0b23708ac009653a63a7a366541",
    "6072f63499837b880f0c842a99d22ec79b784e9839bd69b08feb6b83e199ac02",
}

EXPECTED_COLORS = {"#078EDB", "#E7A30A", "#7F258F"}
EXPECTED_BACKGROUND = "#080347"


def fail(message: str) -> None:
    raise SystemExit(f"Launcher icon verification failed: {message}")


def verify_sources(res_dir: Path) -> None:
    foreground = res_dir / "drawable" / "ic_launcher_foreground.xml"
    monochrome = res_dir / "drawable" / "ic_launcher_monochrome.xml"
    background = res_dir / "values" / "ic_launcher_background.xml"

    for path in (foreground, monochrome, background):
        if not path.is_file():
            fail(f"missing {path}")

    foreground_text = foreground.read_text(encoding="utf-8")
    for color in EXPECTED_COLORS:
        if color not in foreground_text:
            fail(f"new three-wave foreground is missing {color}")

    if EXPECTED_BACKGROUND not in background.read_text(encoding="utf-8"):
        fail(f"launcher background must remain {EXPECTED_BACKGROUND}")

    adaptive_files = [
        res_dir / "mipmap-anydpi" / "ic_launcher.xml",
        res_dir / "mipmap-anydpi" / "ic_launcher_round.xml",
        res_dir / "mipmap-anydpi-v33" / "ic_launcher.xml",
        res_dir / "mipmap-anydpi-v33" / "ic_launcher_round.xml",
    ]
    for path in adaptive_files:
        text = path.read_text(encoding="utf-8")
        if "@drawable/ic_launcher_foreground" not in text:
            fail(f"{path} does not use the verified three-wave foreground")

    for path in adaptive_files[2:]:
        if "@drawable/ic_launcher_monochrome" not in path.read_text(encoding="utf-8"):
            fail(f"{path} does not use the themed monochrome artwork")

    legacy = []
    for directory in res_dir.glob("mipmap-*"):
        if directory.name.startswith("mipmap-anydpi"):
            continue
        legacy.extend(directory.glob("ic_launcher*"))
    if legacy:
        names = ", ".join(str(path) for path in sorted(legacy))
        fail(f"legacy bitmap launcher resources are still present: {names}")


def verify_apk(apk: Path) -> None:
    if not apk.is_file():
        fail(f"APK does not exist: {apk}")

    found_old = []
    with zipfile.ZipFile(apk) as archive:
        for entry in archive.infolist():
            if entry.is_dir():
                continue
            digest = hashlib.sha256(archive.read(entry)).hexdigest()
            if digest in OLD_LAUNCHER_HASHES:
                found_old.append(entry.filename)

    if found_old:
        fail("legacy 1.6.0 launcher artwork is packaged in APK: " + ", ".join(found_old))


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--res-dir", type=Path, default=Path("app/src/main/res"))
    parser.add_argument("--apk", type=Path)
    args = parser.parse_args()

    verify_sources(args.res_dir)
    if args.apk is not None:
        verify_apk(args.apk)
    print("Launcher icon verification passed: three-wave artwork is active and legacy artwork is absent.")


if __name__ == "__main__":
    main()
