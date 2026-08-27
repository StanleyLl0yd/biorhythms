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

Current version: **1.3.0** · Min SDK: **26 (Android 8.0)** · Target SDK: **36**

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
- Widget rendering that adapts to resizing
- In-app About screen with the app description, author, license and GitHub source link

> Biorhythms are not a medical or scientifically validated diagnostic method. The app visualizes the classic biorhythm model for informational and entertainment purposes.

## 📦 Installation

The recommended way to install the app is to download the signed APK from the latest GitHub Release:

[Download latest release](https://github.com/StanleyLl0yd/biorhythms/releases/latest)

Android 8.0 or newer is required.

## 🛠️ Build from source

Requirements:

- Current Android Studio with Android Gradle Plugin 8.13 support
- JDK 17 or newer
- Android SDK 36

```bash
git clone https://github.com/StanleyLl0yd/biorhythms.git
cd biorhythms
./gradlew assembleDebug
```

To run the project checks:

```bash
./gradlew testDebugUnitTest compileDebugAndroidTestKotlin lintDebug assembleRelease bundleRelease
```

## 🧱 Technology

| Category | Technology |
| --- | --- |
| Language | Kotlin 2.0.21 |
| UI | Jetpack Compose + Material 3 |
| State | ViewModel + Lifecycle |
| Storage | DataStore Preferences |
| Widget | Android AppWidget / RemoteViews |
| Build | Gradle 8.13, Kotlin DSL |
| Android Gradle Plugin | 8.13.1 |

## ✅ Quality checks

GitHub Actions automatically validates pull requests and pushes to `main` with:

- unit tests
- Android Lint
- debug APK assembly
- release APK assembly with R8/resource shrinking
- release AAB assembly
- Android instrumentation-test compilation

## 🔐 Release signing

Release signing uses the standard Android signing configuration:

- `ANDROID_KEYSTORE_PATH`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

GitHub Actions restores `ANDROID_KEYSTORE_PATH` from the `ANDROID_KEYSTORE_BASE64` repository secret. The production keystore and passwords are not stored in the repository.

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
