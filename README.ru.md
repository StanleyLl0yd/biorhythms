# Biorhythms

[![Android CI](https://github.com/StanleyLl0yd/biorhythms/actions/workflows/android-ci.yml/badge.svg?branch=main)](https://github.com/StanleyLl0yd/biorhythms/actions/workflows/android-ci.yml)
[![Latest release](https://img.shields.io/github/v/release/StanleyLl0yd/biorhythms)](https://github.com/StanleyLl0yd/biorhythms/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/StanleyLl0yd/biorhythms/total)](https://github.com/StanleyLl0yd/biorhythms/releases)
[![Android 8+](https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android&logoColor=white)](https://github.com/StanleyLl0yd/biorhythms/releases/latest)

[![en](https://img.shields.io/badge/lang-en-red.svg)](README.md)
[![ru](https://img.shields.io/badge/lang-ru-blue.svg)](README.ru.md)

Android-приложение для расчёта и визуализации классических биоритмов по дате рождения, написанное на Kotlin с использованием Jetpack Compose и Material 3.

[⬇️ Скачать последнюю версию APK](https://github.com/StanleyLl0yd/biorhythms/releases/latest)

Текущая версия: **1.2.0** · Min SDK: **26 (Android 8.0)** · Target SDK: **36**

## ✨ Возможности

- Физический, эмоциональный и интеллектуальный циклы биоритмов
- График с дневной сеткой и текущими значениями
- Выбор и сохранение даты рождения
- Светлая, тёмная и системная темы
- Русский, английский и системный язык интерфейса
- Форматирование дат с учётом выбранного языка
- Виджет домашнего экрана с текущими значениями биоритмов
- Индивидуальная настройка непрозрачности фона каждого виджета
- Автоматическое обновление виджета после изменения даты рождения, темы или языка
- Корректное отображение виджета при изменении его размера

> Биоритмы не являются медицинским или научно подтверждённым методом диагностики. Приложение визуализирует классическую модель биоритмов в информационных и развлекательных целях.

## 📦 Установка

Рекомендуемый способ установки — скачать подписанный APK из последнего GitHub Release:

[Скачать последнюю версию](https://github.com/StanleyLl0yd/biorhythms/releases/latest)

Требуется Android 8.0 или новее.

## 🛠️ Сборка из исходников

Требования:

- актуальная Android Studio с поддержкой Android Gradle Plugin 8.13
- JDK 17 или новее
- Android SDK 36

```bash
git clone https://github.com/StanleyLl0yd/biorhythms.git
cd biorhythms
./gradlew assembleDebug
```

Полный набор проверок:

```bash
./gradlew testDebugUnitTest compileDebugAndroidTestKotlin lintDebug assembleRelease bundleRelease
```

## 🧱 Технологии

| Категория | Технология |
| --- | --- |
| Язык | Kotlin 2.0.21 |
| UI | Jetpack Compose + Material 3 |
| Состояние | ViewModel + Lifecycle |
| Хранение | DataStore Preferences |
| Виджет | Android AppWidget / RemoteViews |
| Сборка | Gradle 8.13, Kotlin DSL |
| Android Gradle Plugin | 8.13.1 |

## ✅ Контроль качества

GitHub Actions автоматически проверяет pull request и push в `main`:

- unit-тесты
- Android Lint
- сборку debug APK
- release APK с R8/resource shrinking
- release AAB
- компиляцию Android instrumentation-тестов

## 🔐 Подпись release-сборки

Release signing настраивается через переменные окружения:

- `BIORHYTHMS_KEYSTORE_PATH`
- `BIORHYTHMS_STORE_PASSWORD`
- `BIORHYTHMS_KEY_ALIAS`
- `BIORHYTHMS_KEY_PASSWORD`

Production keystore и пароли не хранятся в репозитории.

## 🌍 Языки

- English — по умолчанию
- Русский
- системный язык

## 📊 История изменений

- [English changelog](CHANGELOG.md)
- [Русский changelog](CHANGELOG.ru.md)
- [GitHub Releases](https://github.com/StanleyLl0yd/biorhythms/releases)

## 🤝 Участие в разработке

Баг-репорты и pull request приветствуются. Изменения лучше делать небольшими и сфокусированными, соблюдать Kotlin coding conventions и по возможности добавлять тесты для изменённого поведения.

## 👨‍💻 Автор

Stanley Lloyd · [@StanleyLl0yd](https://github.com/StanleyLl0yd)

---

Если проект оказался полезен, можно поставить ему ⭐.
