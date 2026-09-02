# Biorhythms

[![Android CI](https://github.com/StanleyLl0yd/biorhythms/actions/workflows/android-ci.yml/badge.svg?branch=main)](https://github.com/StanleyLl0yd/biorhythms/actions/workflows/android-ci.yml)
[![Latest release](https://img.shields.io/github/v/release/StanleyLl0yd/biorhythms)](https://github.com/StanleyLl0yd/biorhythms/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/StanleyLl0yd/biorhythms/total)](https://github.com/StanleyLl0yd/biorhythms/releases)
[![Android 8+](https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android&logoColor=white)](https://github.com/StanleyLl0yd/biorhythms/releases/latest)
[![License: PolyForm Noncommercial 1.0.0](https://img.shields.io/badge/license-PolyForm%20Noncommercial%201.0.0-blue)](LICENSE)

[![en](https://img.shields.io/badge/lang-en-red.svg)](README.md)
[![ru](https://img.shields.io/badge/lang-ru-blue.svg)](README.ru.md)

An Android app for calculating and visualizing classic biorhythm cycles based on a birth date, built with Kotlin, Jetpack Compose and Material 3.

[⬇️ Download the latest APK](https://github.com/StanleyLl0yd/biorhythms/releases/latest)

Current published version: **1.4.3** · Min SDK: **26 (Android 8.0)** · Target SDK: **37** · Compile SDK: **37**

## ✨ Features

- Physical, emotional and intellectual biorhythm cycles
- First-run birth-date onboarding directly on the main screen
- Selected-day summary cards with all three current values
- Interactive chart inspection by tap or drag
- Clear five-day chart grid with a highlighted selected day
- Birth date selection and persistence
- Light, dark and system themes
- English, Russian and system UI language
- Locale-aware date formatting
- Home-screen widget with current biorhythm values
- Live widget preview and per-widget background opacity setting
- Automatic widget refresh after birth date, theme or language changes
- Widget rendering that adapts to resizing without bitmap payloads
- In-app About screen with the app description, author, license and GitHub source link

> Biorhythms are not a medical or scientifically validated diagnostic method. The app visualizes the classic biorhythm model for informational and entertainment purposes.

## 📦 Installation and release formats

The signed **AAB is the primary release format for Google Play**. It is intended for store distribution and is not directly installable by users.

For direct installation and GitHub distribution, each release also includes a signed APK:

[Download latest release](https://github.com/StanleyLl0yd/biorhythms/releases/latest)

Android 8.0 or newer is required. Each release also includes `SHA256SUMS.txt` for verifying the published AAB and APK.

## ▶️ Google Play compatibility

The project currently uses `minSdk = 26`, `targetSdk = 37` and `compileSdk = 37`. API 37 is supported by the current Android Gradle Plugin toolchain and exceeds the Google Play target API requirement for new apps and app updates.

The release contains transitive AndroidX native libraries. Release verification therefore checks the actual generated APK and AAB rather than assuming the project is JVM-only. CI requires:

- `arm64-v8a` whenever native libraries are present;
- 64-bit counterparts for shipped 32-bit ARM/x86 native libraries;
- only the standard `arm64-v8a`, `armeabi-v7a`, `x86_64` and `x86` ABI families;
- matching native-library/ABI sets between APK and AAB;
- at least 16 KB ELF `PT_LOAD` alignment for every packaged native library;
- 16 KB ZIP alignment for uncompressed native libraries in the APK;
- `PAGE_ALIGNMENT_16K` in the AAB `BundleConfig`;
- runtime instrumentation tests on an API 37 emulator whose memory page size is explicitly verified as 16 KB.

No ABI filter is applied because removing currently supported ABIs would reduce compatibility without a meaningful Google Play download-size benefit: App Bundles deliver device-specific ABI splits.

## 🛠️ Build from source

Requirements:

- Current Android Studio with Android Gradle Plugin 9.3 support
- JDK 17 or newer
- Android SDK 37

```bash
git clone https://github.com/StanleyLl0yd/biorhythms.git
cd biorhythms
./gradlew assembleDebug
```

To run the non-emulator project checks:

```bash
./gradlew testDebugUnitTest compileDebugAndroidTestKotlin lintDebug assembleRelease bundleRelease
```

## 🧱 Technology

| Category | Technology |
| --- | --- |
| Language | Kotlin 2.4.10 |
| UI | Jetpack Compose 1.12 + Material 3 |
| State | ViewModel + Lifecycle |
| Storage | DataStore Preferences |
| Widget | Android AppWidget / RemoteViews |
| Build | Gradle 9.7.1, Kotlin DSL |
| Android Gradle Plugin | 9.3.2 |

## ✅ Quality and security checks

GitHub Actions validates pull requests and pushes to `main` with:

- unit tests
- Android Lint
- debug APK assembly
- unsigned release APK/AAB assembly with R8 and resource shrinking
- Google Play SDK/ABI/16 KB release-artifact compatibility verification
- Android instrumentation-test compilation
- Android runtime instrumentation and accessibility tests on API 37 with a verified 16 KB memory page size

A weekly CI run also exercises the runtime test suite on the minimum supported API 26. CodeQL analyzes Java/Kotlin code separately, SonarQube Cloud enforces the configured quality gate, and Gradle dependency verification validates downloaded build artifacts against committed SHA-256 metadata.

The protected `main` branch requires `Verify`, `CodeQL` and `SonarCloud Code Analysis` before changes can be merged.

## 🔐 Release signing

Production signing is isolated from normal branch and pull-request CI. The primary signed AAB and supplementary signed APK are built only by the Android Release workflow for a version tag such as `v1.4.3`, or by a manual workflow run that explicitly names an existing version tag.

Before signing, the workflow verifies that the tag matches `versionName` in `app/build.gradle.kts` and validates Google Play compatibility on unsigned release artifacts. After signing, it verifies the APK signature, AAB signature and Google Play compatibility again. A separate publish job without signing secrets creates the GitHub Release and attaches the signed AAB, supplementary APK and `SHA256SUMS.txt`.

Release signing uses:

- `ANDROID_KEYSTORE_PATH`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

GitHub Actions restores `ANDROID_KEYSTORE_PATH` from the `ANDROID_KEYSTORE_BASE64` repository secret. The production keystore and passwords are not stored in the repository.

## 🔒 Privacy

The app does not request Internet access and does not include advertising or analytics SDKs. The locally stored preferences file containing the birth date is excluded from Android cloud backup.

## 🌍 Languages

- English — default
- Русский
- System language mode

## 📊 Changelog

- [English changelog](CHANGELOG.md)
- [Русский changelog](CHANGELOG.ru.md)
- [GitHub Releases](https://github.com/StanleyLl0yd/biorhythms/releases)

## 📄 License

Biorhythms is licensed under the [PolyForm Noncommercial License 1.0.0](LICENSE). Use, modification and redistribution are permitted for noncommercial purposes under the license terms.

The official license page is available at <https://polyformproject.org/licenses/noncommercial/1.0.0>.

## 🤝 Contributing

Contributions and bug reports are welcome. Open an issue or submit a focused pull request.

Please keep changes small, follow Kotlin coding conventions, and include tests for behavior changes where practical.

## 👨‍💻 Author

Stanley Lloyd

---

If the project is useful to you, consider giving it a ⭐.