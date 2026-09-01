# Changelog

[![en](https://img.shields.io/badge/lang-en-red.svg)](CHANGELOG.md)
[![ru](https://img.shields.io/badge/lang-ru-blue.svg)](CHANGELOG.ru.md)

All notable changes to this project are documented in this file.

The project follows [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

---

## [1.4.1] - 2026-09-01

### 🐛 Fixed
- Fixed a crash when opening **Settings → Birth date** by replacing the platform date dialog with the Material 3 Compose date picker while keeping dialog window ownership on the Activity context.
- Birth-date selection now follows the app-selected locale, rejects future dates and preserves calendar dates without timezone or DST shifts.
- Widget title and settings accessibility text now follow the app-selected language instead of falling back to the system locale.
- Widget configuration preview now matches the actual home-screen widget structure instead of showing visual bars that are not rendered by the widget.

### 🧹 Maintenance
- Updated the Gradle Wrapper to 9.5.1 with the distribution checksum pinned.
- Fixed release APK signature verification by resolving `apksigner` from the installed Android Build Tools path and kept signature verification mandatory.
- Hardened release verification so an AAB must be explicitly reported as verified by `jarsigner` before publication.
- Removed unused chart legend code, obsolete widget color resources and an empty custom ProGuard file.
- Added runtime regression coverage for opening the birth-date picker and the other interactive Settings rows, plus widget localization and preview-content coverage.

---

## [1.4.0] - 2026-08-31

### 🔐 Security & reliability
- Moved production APK/AAB signing out of normal `main` pushes into a release-only workflow tied to version tags or an explicit manual release tag.
- Added release tag/version validation before production signing secrets are used.
- Excluded the DataStore file containing the birth date from Android cloud backup.
- Added defensive DataStore read/write handling, rollback behavior and birth-date validation.
- Explicitly disabled cleartext network traffic.
- Added Gradle dependency verification with committed SHA-256 metadata.
- Added CodeQL analysis and SonarQube Cloud quality analysis to the protected branch gates.
- Enabled Dependabot vulnerability alerts and security updates for the repository.

### ✅ Quality
- Added runtime Android instrumentation tests on API 37 and weekly compatibility coverage on API 26.
- Added runtime accessibility semantics checks for the chart and widget preview.
- Protected `main` now requires `Verify`, `CodeQL` and `SonarCloud Code Analysis` before merging.
- Refactored chart rendering, settings state/actions and shared UI components to reduce cognitive complexity and duplication without changing app behavior.
- Reduced duplication on new code below the configured Sonar quality threshold and resolved the actionable Sonar findings.
- Consolidated repeated Android/Gradle CI setup into a local reusable action.
- Reduced Android SDK packages installed by CI to the components each job actually uses.
- Restored Gradle Wrapper patch updates in Dependabot while keeping minor and major wrapper upgrades deliberate.

### ♿ UI & widget
- Replaced bitmap-based widget content with native `RemoteViews` text rows to reduce memory use and improve accessibility.
- Added accessible per-cycle widget descriptions and validated widget configuration IDs against the app's provider.
- Reduced periodic widget refresh from hourly to daily while preserving immediate refresh after relevant app changes.
- Made percentage text use contrast-safe theme colors while retaining cycle colors for visual accents.
- Removed redundant version-specific widget metadata while preserving Android 12+ cell sizing and older-version fallback behavior.

### 📦 Release
- Bumped the app to `versionCode` 6 and `versionName` `1.4.0`.
- Release automation now publishes the signed APK, signed AAB and `SHA256SUMS.txt` as permanent GitHub Release assets after the signed build succeeds.

---

## [1.3.0] - 2026-08-27

### ✨ Added
- First-run birth-date onboarding directly on the main screen.
- Three prominent biorhythm summary cards for the currently selected day.
- Interactive chart inspection: tap or drag across the chart to select another day and update its values immediately.
- Live widget preview on the widget configuration screen.
- In-app About screen with a short app description, current version, author, license and GitHub source link.
- Unit tests for chart position mapping and grid generation.
- Repository license file with the official PolyForm Noncommercial License 1.0.0 terms.

### 🎨 Changed
- Reduced the chart grid from one vertical line per day to clear five-day intervals.
- Increased chart height and added a strong selected-day marker with points on all three curves.
- Biorhythm percentages now use the same color as their corresponding curve instead of a separate red/yellow/green scale.
- Redesigned Settings with larger Material rows, clearer section hierarchy and more readable typography.
- Reworked widget opacity controls for better use on narrow screens.
- Added a checkerboard transparency backdrop to the widget preview so background opacity is immediately visible.
- Increased core typography sizes and spacing for improved readability.
- Project licensing is explicitly PolyForm Noncommercial 1.0.0.
- Promoted the tested 1.3 beta line to the stable `1.3.0` release with `versionCode` 5 and `versionName` `1.3.0`.

---

## [1.2.0] - 2026-08-26

### ✨ Added
- Home-screen widget with current physical, emotional and intellectual biorhythm values.
- Per-widget background opacity configuration with persistent values.
- Unit and instrumentation tests for calculations, localization, preferences, date selection and widget settings.
- GitHub Actions CI covering unit tests, Android Lint, release assembly, R8/resource shrinking, AAB generation and Android-test compilation.
- Secure release-signing configuration through environment variables without storing the keystore or passwords in the repository.

### 🐛 Fixed
- Widget configuration now opens correctly when the widget is added from the launcher.
- Existing widgets refresh immediately after birth date, language or theme changes.
- Widget contents refresh correctly after resizing and during periodic system updates.
- Light, dark and system themes are applied consistently to the app and widget.
- English and Russian localization is applied consistently to UI text, date formatting and widget labels.
- System Back navigation from the settings screen now behaves correctly.
- The current reference date refreshes when the app resumes and after the day changes.
- Preference persistence no longer depends on enum ordering.
- Resource, theme, localization and obsolete-file issues were cleaned up.

### 🏗️ Changed
- Biorhythm calculation logic was moved into a shared module used by both the app and widget.
- Biorhythm value color mapping is now shared between the app and widget.
- Restored the official Gradle Wrapper so the project builds from a clean clone without a separately installed Gradle.
- Promoted the project from `1.2 beta 1` to the stable `1.2.0` release.
- Updated `versionCode` to 4 and `versionName` to `1.2.0`.

---

## [1.1] - 2025-11-23

### ✨ Added
- Settings screen for birth date, theme and app language selection.
- Daily chart grid and a legend with current biorhythm values.

### 🎨 Changed
- Updated the main-screen design.
- Reduced font sizes and spacing for a more compact interface.
- Improved localization and overall application stability.

---

## [1.0.0] - 2025-11-12

### ✨ Added
- Initial release.

---

## Version Naming Convention

- **Major (X.0.0)**: breaking changes or major redesigns
- **Minor (1.X.0)**: new features and significant improvements
- **Patch (1.2.X)**: bug fixes and minor improvements

---

## Links

- [Repository](https://github.com/StanleyLl0yd/biorhythms)
- [Issues](https://github.com/StanleyLl0yd/biorhythms/issues)
- [Releases](https://github.com/StanleyLl0yd/biorhythms/releases)
