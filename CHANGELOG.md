# Changelog / История изменений

## Русский

### 1.2.0

Дата: 26.08.2026

- Добавлен полноценный виджет домашнего экрана с текущими значениями физического, эмоционального и интеллектуального циклов.
- Исправлена конфигурация виджета: он корректно добавляется через launcher и открывает экран настроек.
- Добавлена настройка непрозрачности фона виджета с сохранением значения для каждого экземпляра.
- Виджет теперь корректно обновляется после изменения даты рождения, языка или темы в приложении.
- Исправлено обновление данных виджета при изменении его размера и при периодическом системном обновлении.
- Улучшена поддержка светлой, тёмной и системной темы, в том числе для виджета.
- Русский и английский языки теперь применяются последовательно ко всему интерфейсу, датам и виджету.
- Исправлена работа системной кнопки Back на экране настроек.
- Дата «сегодня» корректно обновляется после возврата приложения из фона и после смены суток.
- Расчёт биоритмов вынесен в единый модуль и используется одинаково в приложении и виджете.
- Унифицирована цветовая индикация значений биоритмов.
- Улучшено хранение настроек: значения темы и языка больше не зависят от порядка элементов enum.
- Исправлены проблемы с ресурсами, темами, локализацией и устаревшими/неиспользуемыми файлами.
- Восстановлен стандартный Gradle Wrapper, поэтому проект собирается из чистого клона без установленного системного Gradle.
- Добавлены unit-тесты и instrumentation-тесты для расчётов, локализации, настроек, выбора даты и параметров виджета.
- Добавлен GitHub Actions CI: unit-тесты, release-сборка, R8/resource shrinking, AAB, компиляция Android-тестов и Android Lint.
- Добавлена безопасная конфигурация release signing через переменные окружения без хранения keystore и паролей в репозитории.
- Версия доведена от `1.2 beta 1` до стабильного релиза `1.2.0`.

### 1.1

Дата: 23.11.2025

- Обновлён дизайн главного экрана, добавлена дневная сетка и легенда с текущими значениями биоритмов.
- Появился экран настроек: выбор даты рождения, темы и языка приложения.
- Уменьшены шрифты и отступы, интерфейс стал компактнее и аккуратнее.
- Улучшена локализация и общая стабильность приложения.

### 1.0.0

Дата: 12.11.2025

- Первая версия приложения.

---

## English

### 1.2.0

Date: 2026-08-26

- Added a full home-screen widget with current physical, emotional, and intellectual biorhythm values.
- Fixed widget configuration so it can be added correctly from the launcher and opens its settings screen.
- Added per-widget background opacity configuration with persistent values.
- Existing widgets now update correctly after birth date, language, or theme changes in the app.
- Fixed widget refresh behavior after resizing and during periodic system updates.
- Improved light, dark, and system theme support, including the widget.
- Russian and English localization is now applied consistently to the UI, date formatting, and widget labels.
- Fixed system Back navigation from the settings screen.
- The current reference date now refreshes correctly when the app resumes and after the day changes.
- Biorhythm calculations were moved into a shared module used by both the app and widget.
- Unified biorhythm value color mapping between the app and widget.
- Improved preference persistence so theme and language values no longer depend on enum ordering.
- Fixed resource, theme, localization, and obsolete/unused file issues.
- Restored the standard Gradle Wrapper so a clean clone can build without a separately installed system Gradle.
- Added unit and instrumentation tests for calculations, localization, preferences, date selection, and widget settings.
- Added GitHub Actions CI covering unit tests, release build, R8/resource shrinking, AAB generation, Android-test compilation, and Android Lint.
- Added secure release-signing configuration through environment variables without storing the keystore or passwords in the repository.
- Promoted the project from `1.2 beta 1` to the stable `1.2.0` release.

### 1.1

Date: 2025-11-23

- Updated the main-screen design with a daily grid and a legend showing current biorhythm values.
- Added a settings screen for birth date, theme, and app language selection.
- Reduced font sizes and spacing for a more compact and polished interface.
- Improved localization and overall application stability.

### 1.0.0

Date: 2025-11-12

- Initial release.
