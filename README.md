# Biorhythms

[![Android CI](https://github.com/StanleyLl0yd/biorhythms/actions/workflows/android-ci.yml/badge.svg?branch=main)](https://github.com/StanleyLl0yd/biorhythms/actions/workflows/android-ci.yml)
[![Latest release](https://img.shields.io/github/v/release/StanleyLl0yd/biorhythms)](https://github.com/StanleyLl0yd/biorhythms/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/StanleyLl0yd/biorhythms/total)](https://github.com/StanleyLl0yd/biorhythms/releases)
[![Android 8+](https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android&logoColor=white)](https://github.com/StanleyLl0yd/biorhythms/releases/latest)

[![en](https://img.shields.io/badge/lang-en-red.svg)](README.md)
[![ru](https://img.shields.io/badge/lang-ru-blue.svg)](README.ru.md)

An Android app for calculating and visualizing classic biorhythm cycles based on a birth date, built with Kotlin, Jetpack Compose and Material 3.

[⬇️ Download the latest APK](https://github.com/StanleyLl0yd/biorhythms/releases/latest)

Current version: **1.2.0** · Min SDK: **26 (Android 8.0)** · Target SDK: **36**

## ✨ Features

- Physical, emotional and intellectual biorhythm cycles
- Daily chart grid with current values
- Birth date selection and persistence
- Light, dark and system themes
- English, Russian and system UI language
- Locale-aware date formatting
- Home-screen widget with current biorhythm values
- Per-widget background opacity setting
- Automatic widget refresh after birth date, theme or language changes
- Widget rendering that adapts to resizing

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

Release signing is configured through environment variables:

- `BIORHYTHMS_KEYSTORE_PATH`
- `BIORHYTHMS_STORE_PASSWORD`
- `BIORHYTHMS_KEY_ALIAS`
- `BIORHYTHMS_KEY_PASSWORD`

The production keystore and passwords are not stored in the repository.

## 🌍 Languages

- English — default
- Русский
- System language mode

## 📊 Changelog

- [English changelog](CHANGELOG.md)
- [Русский changelog](CHANGELOG.ru.md)
- [GitHub Releases](https://github.com/StanleyLl0yd/biorhythms/releases)

## 🤝 Contributing

Contributions and bug reports are welcome. Open an issue or submit a focused pull request.

Please keep changes small, follow Kotlin coding conventions, and include tests for behavior changes where practical.

## 👨‍💻 Author

Stanley Lloyd · [@StanleyLl0yd](https://github.com/StanleyLl0yd)

---

If the project is useful to you, consider giving it a ⭐.
