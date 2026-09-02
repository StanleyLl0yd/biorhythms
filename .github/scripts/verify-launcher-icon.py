#!/usr/bin/env python3

import argparse
import hashlib
import sys
import zipfile
from pathlib import Path

ORIGINAL_ARTWORK_SHA256 = "abc878b9b42d05c8195fb6a6c556da66102eda3e4c62c0bf73cb29c1d017b1a2"
OLD_LAUNCHER_HASHES = {
    "08d5e8697b228034cd5f6e40fdea8177daadc4cfdcfeef52f520ab8ec1c61fcd",
    "99d9da236ada54ed912ed308848c8d6e441af0b23708ac009653a63a7a366541",
    "6072f63499837b880f0c842a99d22ec79b784e9839bd69b08feb6b83e199ac02",
}


def fail(message: str) -> None:
    raise SystemExit(f"Launcher icon verification failed: {message}")


def sha256(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def verify_sources(res_dir: Path) -> None:
    artwork = res_dir / "drawable-nodpi" / "ic_launcher_artwork.png"
    if not artwork.is_file():
        fail(f"missing original launcher artwork: {artwork}")

    digest = sha256(artwork)
    if digest != ORIGINAL_ARTWORK_SHA256:
        fail(
            "launcher PNG is not the approved original artwork: "
            f"expected {ORIGINAL_ARTWORK_SHA256}, got {digest}"
        )

    adaptive_files = [
        res_dir / "mipmap-anydpi" / "ic_launcher.xml",
        res_dir / "mipmap-anydpi" / "ic_launcher_round.xml",
        res_dir / "mipmap-anydpi-v33" / "ic_launcher.xml",
        res_dir / "mipmap-anydpi-v33" / "ic_launcher_round.xml",
    ]
    for path in adaptive_files:
        text = path.read_text(encoding="utf-8")
        if "@drawable/ic_launcher_artwork" not in text:
            fail(f"{path} does not use the approved original PNG")
        if "@drawable/ic_launcher_foreground" in text:
            fail(f"{path} still references the hand-drawn vector foreground")
        if "@drawable/ic_launcher_monochrome" in text:
            fail(f"{path} still references the hand-drawn monochrome vector")

    for obsolete in (
        res_dir / "drawable" / "ic_launcher_foreground.xml",
        res_dir / "drawable" / "ic_launcher_monochrome.xml",
    ):
        if obsolete.exists():
            fail(f"obsolete hand-drawn launcher vector is still present: {obsolete}")

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
        fail("legacy launcher artwork is packaged in APK: " + ", ".join(found_old))


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--res-dir", type=Path, default=Path("app/src/main/res"))
    parser.add_argument("--apk", type=Path)
    args = parser.parse_args()

    verify_sources(args.res_dir)
    if args.apk is not None:
        verify_apk(args.apk)
    print("Launcher icon verification passed: approved original PNG is wired and legacy/vector artwork is absent.")


if __name__ == "__main__":
    main()
