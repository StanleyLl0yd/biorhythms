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

Текущая версия: **1.6.0** · Min SDK: **26 (Android 8.0)** · Target SDK: **37** · Compile SDK: **37**

## ✨ Возможности

- Физический, эмоциональный и интеллектуальный циклы биоритмов
- Первый запуск с выбором даты рождения прямо на главном экране
- Карточки выбранного дня со значениями всех трёх биоритмов
- Прогноз на семь дней с индикаторами роста, снижения и стабильного направления
- Определение критических дней, пиков и минимумов каждого классического цикла
- Отдельное предупреждение, когда все три цикла одновременно находятся не ниже +80% или не выше −80%
- Включаемые пользователем уведомления с настраиваемым ежедневным временем
- Ежедневная сводка с независимым выбором физического, эмоционального и интеллектуального циклов
- Уведомления о важных событиях: критических точках, пиках, минимумах и синхронных экстремумах ±80%, включая режим только важных событий
- Автоматическое восстановление расписания уведомлений после перезагрузки, изменения времени, часового пояса, локали и обновления приложения без специального доступа к точным будильникам
- Интерактивный просмотр графика нажатием или проведением
- Понятная сетка графика по пять дней с выделением выбранного дня
- Выбор и сохранение даты рождения
- Светлая, тёмная и системная темы
- Русский, английский и системный язык интерфейса
- Форматирование дат с учётом выбранного языка
- Виджет домашнего экрана с текущими значениями, направлением циклов и предупреждением о синхронном экстремуме
- Живой предпросмотр виджета и индивидуальная настройка непрозрачности фона
- Автоматическое обновление виджета после изменения даты рождения, темы или языка, а также после смены суток, времени, часового пояса или системной локали
- Адаптация виджета к изменению размера без передачи крупных bitmap
- Экран «О приложении» с описанием, автором, лицензией и ссылкой на исходный код в GitHub

> Биоритмы не являются медицинским или научно подтверждённым методом диагностики. Приложение визуализирует классическую модель биоритмов в информационных и развлекательных целях.

## 📦 Установка и форматы релиза

Подписанный **AAB является основным release-форматом для Google Play**. Он предназначен для публикации через магазин и напрямую пользователем не устанавливается.

Для прямой установки и распространения через GitHub каждый релиз дополнительно содержит подписанный APK:

[Скачать последнюю версию](https://github.com/StanleyLl0yd/biorhythms/releases/latest)

Требуется Android 8.0 или новее. Каждый релиз также содержит `SHA256SUMS.txt` для проверки опубликованных AAB и APK.

## ▶️ Совместимость с Google Play

Проект использует `minSdk = 26`, `targetSdk = 37` и `compileSdk = 37`. API 37 поддерживается текущим Android Gradle Plugin toolchain и превышает действующее требование Google Play к target API для новых приложений и обновлений.

В release-сборке присутствуют транзитивные native-библиотеки AndroidX, поэтому проверяется не только Gradle-конфигурация, но и фактически собранные APK/AAB. CI требует:

- наличия `arm64-v8a`, если в приложении присутствуют native-библиотеки;
- 64-битных аналогов для поставляемых 32-битных ARM/x86 native-библиотек;
- только стандартных ABI `arm64-v8a`, `armeabi-v7a`, `x86_64` и `x86`;
- одинакового набора native-библиотек и ABI в APK и AAB;
- выравнивания каждого ELF `PT_LOAD` не менее чем на 16 KB;
- 16 KB ZIP-выравнивания несжатых native-библиотек в APK;
- `PAGE_ALIGNMENT_16K` в `BundleConfig` AAB;
- runtime instrumentation-тестов на API 37 в эмуляторе, для которого размер memory page явно проверяется как 16 KB.

ABI-фильтр намеренно не добавлен: удаление уже поддерживаемых архитектур уменьшило бы совместимость без заметной выгоды для загрузки из Google Play, поскольку App Bundle выдаёт устройству только подходящий ABI split.

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
| Уведомления | Android NotificationManager + неточное планирование AlarmManager |
| Виджет | Android AppWidget / RemoteViews |
| Сборка | Gradle 9.7.1, Kotlin DSL |
| Android Gradle Plugin | 9.3.2 |

## ✅ Контроль качества и безопасности

GitHub Actions проверяет pull request и push в `main`:

- unit-тестами
- Android Lint
- сборкой debug APK
- неподписанной release APK/AAB с R8 и resource shrinking
- проверкой Google Play-совместимости SDK/ABI/16 KB для release-артефактов
- компиляцией Android instrumentation-тестов
- реальным запуском instrumentation- и accessibility-тестов на API 37 с подтверждённым размером memory page 16 KB

Раз в неделю runtime-тесты дополнительно выполняются на минимально поддерживаемом API 26. Отдельный workflow CodeQL анализирует Java/Kotlin-код, SonarQube Cloud применяет настроенный quality gate, а Gradle dependency verification сверяет загруженные зависимости с зафиксированными SHA-256.

Защищённая ветка `main` требует успешных проверок `Verify`, `CodeQL` и `SonarCloud Code Analysis` перед merge.

## 🔐 Подпись release-сборки

Production signing отделён от обычного CI веток и pull request. Основной подписанный AAB и дополнительный подписанный APK создаются только workflow Android Release для тега версии, например `v1.6.0`, соответствующей ветки `release/v1.6.0` либо при ручном запуске с явным указанием существующего тега версии.

Перед подписью workflow проверяет соответствие release ref значению `versionName` в `app/build.gradle.kts` и Google Play-совместимость неподписанных release-артефактов. После подписания отдельно проверяются подпись APK, подпись AAB и Google Play-совместимость финальных файлов. Затем отдельный publish job без signing secrets гарантирует, что неизменяемый тег версии указывает на проверенный release-коммит, создаёт GitHub Release и прикладывает подписанный AAB, дополнительный APK и `SHA256SUMS.txt`.

Подпись использует:

- `ANDROID_KEYSTORE_PATH`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

GitHub Actions восстанавливает `ANDROID_KEYSTORE_PATH` из секрета репозитория `ANDROID_KEYSTORE_BASE64`. Production keystore и пароли в репозитории не хранятся.

## 🔒 Приватность

Приложение не запрашивает доступ в интернет и не содержит рекламных или аналитических SDK. Уведомления работают полностью локально и включаются пользователем; на Android 13+ разрешение на уведомления запрашивается только при включении функции. Локальный файл настроек, в котором хранится дата рождения, исключён из облачного резервного копирования Android.

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
