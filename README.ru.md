# Biorhythms

[![Android CI](https://github.com/StanleyLl0yd/biorhythms/actions/workflows/android-ci.yml/badge.svg?branch=main)](https://github.com/StanleyLl0yd/biorhythms/actions/workflows/android-ci.yml)
[![Latest release](https://img.shields.io/github/v/release/StanleyLl0yd/biorhythms)](https://github.com/StanleyLl0yd/biorhythms/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/StanleyLl0yd/biorhythms/total)](https://github.com/StanleyLl0yd/biorhythms/releases)
[![Android 8+](https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android&logoColor=white)](https://github.com/StanleyLl0yd/biorhythms/releases/latest)
[![License: PolyForm Noncommercial 1.0.0](https://img.shields.io/badge/license-PolyForm%20Noncommercial%201.0.0-blue)](LICENSE)

[![en](https://img.shields.io/badge/lang-en-red.svg)](README.md)
[![ru](https://img.shields.io/badge/lang-ru-blue.svg)](README.ru.md)

Android-приложение для расчёта и визуализации классических биоритмов по дате рождения, написанное на Kotlin с использованием Jetpack Compose и Material 3.

[⬇️ Скачать последнюю стабильную версию APK](https://github.com/StanleyLl0yd/biorhythms/releases/latest)

Последний стабильный релиз: **1.2.0** · Текущая версия разработки: **1.3.0-beta1** · Min SDK: **26 (Android 8.0)** · Target SDK: **36**

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
- Корректное отображение виджета при изменении его размера
- Экран «О приложении» с описанием, автором, лицензией и ссылкой на исходный код в GitHub

> Биоритмы не являются медицинским или научно подтверждённым методом диагностики. Приложение визуализирует классическую модель биоритмов в информационных и развлекательных целях.

## 📦 Установка

Рекомендуемый способ установки стабильной версии — скачать подписанный APK из последнего GitHub Release:

[Скачать последнюю стабильную версию](https://github.com/StanleyLl0yd/biorhythms/releases/latest)

Требуется Android 8.0 или новее.

Тестовые сборки версии разработки создаются GitHub Actions для pull request и не считаются стабильными релизами.

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

Подпись release настраивается через переменные окружения:

- `BIORHYTHMS_KEYSTORE_PATH`
- `BIORHYTHMS_STORE_PASSWORD`
- `BIORHYTHMS_KEY_ALIAS`
- `BIORHYTHMS_KEY_PASSWORD`

Production keystore и пароли в репозитории не хранятся.

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

Stanley Lloyd · [@StanleyLl0yd](https://github.com/StanleyLl0yd)

---

Если проект оказался полезен, можно поставить ему ⭐.
