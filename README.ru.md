# Biorhythms

[![Android CI](https://github.com/StanleyLl0yd/biorhythms/actions/workflows/android-ci.yml/badge.svg?branch=main)](https://github.com/StanleyLl0yd/biorhythms/actions/workflows/android-ci.yml)
[![Latest release](https://img.shields.io/github/v/release/StanleyLl0yd/biorhythms)](https://github.com/StanleyLl0yd/biorhythms/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/StanleyLl0yd/biorhythms/total)](https://github.com/StanleyLl0yd/biorhythms/releases)
[![Android 8+](https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android&logoColor=white)](https://github.com/StanleyLl0yd/biorhythms/releases/latest)
[![License: PolyForm Noncommercial 1.0.0](https://img.shields.io/badge/license-PolyForm%20Noncommercial%201.0.0-blue)](LICENSE)

[![en](https://img.shields.io/badge/lang-en-red.svg)](README.md)
[![ru](https://img.shields.io/badge/lang-ru-blue.svg)](README.ru.md)

Android-приложение для расчёта и визуализации классических биоритмов по дате рождения, написанное на Kotlin с использованием Jetpack Compose и Material 3.

[⬇️ Скачать последнюю версию APK](https://github.com/StanleyLl0yd/biorhythms/releases/latest)

Текущая опубликованная версия: **1.4.0** · Min SDK: **26 (Android 8.0)** · Target SDK: **37**

## ✨ Возможности

- Физический, эмоциональный и интеллектуальный циклы биоритмов
- Первый запуск с выбором даты рождения прямо на главном экране
- Карточки выбранного дня со значениями всех трёх биоритмов
- Интерактивный просмотр графика нажатием или проведением
- Понятная сетка графика по пять дней с выделением выбранного дня
- Выбор и сохранение даты рождения
- Светлая, тёмная и системная темы
- Русский, английский и системный язык интерфейса
- Форматирование дат с учётом выбранного языка
- Виджет домашнего экрана с текущими значениями биоритмов
- Живой предпросмотр виджета и индивидуальная настройка непрозрачности фона
- Автоматическое обновление виджета после изменения даты рождения, темы или языка
- Адаптация виджета к изменению размера без передачи крупных bitmap
- Экран «О приложении» с описанием, автором, лицензией и ссылкой на исходный код в GitHub

> Биоритмы не являются медицинским или научно подтверждённым методом диагностики. Приложение визуализирует классическую модель биоритмов в информационных и развлекательных целях.

## 📦 Установка

Рекомендуемый способ установки — скачать подписанный APK из последнего GitHub Release:

[Скачать последнюю версию](https://github.com/StanleyLl0yd/biorhythms/releases/latest)

Требуется Android 8.0 или новее. Каждый релиз также содержит AAB и файл `SHA256SUMS.txt` для проверки опубликованных бинарных файлов.

## 🛠️ Сборка из исходников

Требования:

- актуальная Android Studio с поддержкой Android Gradle Plugin 9.3
- JDK 17 или новее
- Android SDK 37

```bash
git clone https://github.com/StanleyLl0yd/biorhythms.git
cd biorhythms
./gradlew assembleDebug
```

Проверки, не требующие эмулятора:

```bash
./gradlew testDebugUnitTest compileDebugAndroidTestKotlin lintDebug assembleRelease bundleRelease
```

## 🧱 Технологии

| Категория | Технология |
| --- | --- |
| Язык | Kotlin 2.4.10 |
| UI | Jetpack Compose 1.12 + Material 3 |
| Состояние | ViewModel + Lifecycle |
| Хранение | DataStore Preferences |
| Виджет | Android AppWidget / RemoteViews |
| Сборка | Gradle 9.5.0, Kotlin DSL |
| Android Gradle Plugin | 9.3.2 |

## ✅ Контроль качества и безопасности

GitHub Actions проверяет pull request и push в `main`:

- unit-тестами
- Android Lint
- сборкой debug APK
- неподписанной release APK/AAB с R8 и resource shrinking
- компиляцией Android instrumentation-тестов
- реальным запуском instrumentation- и accessibility-тестов на API 37

Раз в неделю runtime-тесты дополнительно выполняются на минимально поддерживаемом API 26. Отдельный workflow CodeQL анализирует Java/Kotlin-код, SonarQube Cloud применяет настроенный quality gate, а Gradle dependency verification сверяет загруженные зависимости с зафиксированными SHA-256.

Защищённая ветка `main` требует успешных проверок `Verify`, `CodeQL` и `SonarCloud Code Analysis` перед merge.

## 🔐 Подпись release-сборки

Production signing отделён от обычного CI веток и pull request. Подписанные APK/AAB создаются только workflow Android Release для тега версии, например `v1.4.0`, либо при ручном запуске с явным указанием существующего тега версии.

Перед подписью workflow проверяет, что тег соответствует `versionName` в `app/build.gradle.kts`. После успешной подписанной сборки отдельный publish job без signing secrets создаёт GitHub Release и прикладывает подписанные APK, AAB и `SHA256SUMS.txt`.

Подпись использует:

- `ANDROID_KEYSTORE_PATH`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

GitHub Actions восстанавливает `ANDROID_KEYSTORE_PATH` из секрета репозитория `ANDROID_KEYSTORE_BASE64`. Production keystore и пароли в репозитории не хранятся.

## 🔒 Приватность

Приложение не запрашивает доступ в интернет и не содержит рекламных или аналитических SDK. Локальный файл настроек, в котором хранится дата рождения, исключён из облачного резервного копирования Android.

## 🌍 Языки

- English — язык по умолчанию
- Русский
- Системный язык

## 📊 История изменений

- [English changelog](CHANGELOG.md)
- [Русский changelog](CHANGELOG.ru.md)
- [GitHub Releases](https://github.com/StanleyLl0yd/biorhythms/releases)

## 📄 Лицензия

Biorhythms распространяется по лицензии [PolyForm Noncommercial License 1.0.0](LICENSE). Использование, изменение и распространение разрешены для некоммерческих целей в соответствии с условиями лицензии.

Официальная страница лицензии: <https://polyformproject.org/licenses/noncommercial/1.0.0>.

## 🤝 Участие в разработке

Bug report и pull request приветствуются. Создавайте issue или отправляйте небольшие сфокусированные изменения.

Желательно соблюдать Kotlin coding conventions и добавлять тесты для изменений поведения приложения, где это возможно.

## 👨‍💻 Автор

Stanley Lloyd

---

Если проект оказался полезным, можно поставить ему ⭐.
