# Biorhythms

[Русский](README.md) · [Changelog](CHANGELOG.md)

An Android app for calculating and visualizing classic biorhythm cycles based on a birth date.

The app calculates three cycles:

- physical — 23 days;
- emotional — 28 days;
- intellectual — 33 days.

> Biorhythms are not a medical or scientifically validated diagnostic method. This app is intended to visualize the classic biorhythm model.

## Features

- Biorhythm chart with a daily grid and current values.
- Birth date selection and persistence.
- Light, dark, and system themes.
- Russian, English, and system UI language.
- Date formatting that follows the selected language.
- Home-screen widget with current biorhythm values.
- Configurable widget background opacity.
- Existing widgets update automatically after birth date, theme, or language changes.
- Widget rendering adapts to widget size changes.

## Requirements

- Android 8.0 (API 26) or newer.

## Build

The project includes the Gradle Wrapper, so a separate Gradle installation is not required.

```bash
./gradlew assembleDebug
```

Release build:

```bash
./gradlew assembleRelease
```

AAB:

```bash
./gradlew bundleRelease
```

## Checks

```bash
./gradlew testDebugUnitTest
./gradlew compileDebugAndroidTestKotlin
./gradlew lintDebug
```

GitHub Actions automatically runs unit tests, the release build with R8/resource shrinking, instrumentation-test compilation, and Android Lint.

## Release signing

Release signing can be configured through environment variables:

- `BIORHYTHMS_KEYSTORE_PATH`
- `BIORHYTHMS_STORE_PASSWORD`
- `BIORHYTHMS_KEY_ALIAS`
- `BIORHYTHMS_KEY_PASSWORD`

The keystore and passwords should never be stored in the repository.

## Current version

**1.2.0** (`versionCode 4`)

See [CHANGELOG.md](CHANGELOG.md) for details.
