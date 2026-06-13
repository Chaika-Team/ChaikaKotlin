# How_to_E2E_tests.md

Ниже — **нормативная инструкция** для команды по организации, написанию и эксплуатации E2E-тестов Android-приложения. Она написана как внутренний стандарт: где есть слова **«обязательно»**, **«запрещено»**, **«допускается»**, их надо трактовать буквально.

# Стандарт E2E для Android-приложения

**Статус:** целевой внутренний стандарт  
**Актуальность:** на 8 апреля 2026 года  
**Область действия:** Kotlin + Compose + Hilt + Retrofit/Room/DataStore + OAuth2/OIDC через AppAuth, запуск в GitHub Actions и локально на эмуляторе.

## 1. Что в нашей команде считается E2E

**E2E в этом стандарте** — это instrumented UI-тест на устройстве или эмуляторе, который проходит через реальный UI, навигацию, DI-граф, локальное хранилище и прикладную логику приложения, но при этом **может и должен оставаться герметичным**, то есть не зависеть от внешних сервисов, если это не специальный environment-класс тестов. Android Developers прямо указывает, что даже большие end-to-end tests выигрывают от test doubles, а hermetic tests повышают reliability и performance. ([developer.android.com](https://developer.android.com/training/testing/fundamentals/test-doubles?hl=en&utm_source=chatgpt.com))

**Отдельно выделяется класс `[ENV]`** — это environment / contract smoke, где тест сознательно зависит от живого stage/IAM/browser flow. Такие тесты допустимы, но они **не являются базовым E2E-гейтом для PR**. Это следует из общей Android-логики: instrumented tests медленнее и дороже, большие тесты более склонны к flaky, а внешние зависимости эту проблему усиливают. ([developer.android.com](https://developer.android.com/training/testing/instrumented-tests?utm_source=chatgpt.com))

## 2. Главные цели стандарта

1. **Приоритет №1 — детерминизм и диагностичность.**  
   Тест должен падать по причине дефекта приложения, а не из-за среды, сети, фоновой гонки или чужого сервиса. Android отдельно рекомендует избегать внешних зависимостей в больших тестах и предупреждает, что асинхронность и неизвестные тестовому фреймворку фоновые операции — основной источник flaky-поведения. ([developer.android.com](https://developer.android.com/training/testing/fundamentals/test-doubles?hl=en&utm_source=chatgpt.com))

2. **E2E-набор должен быть малым.**  
   Пользовательские flow-тесты нужны для common paths и smoke-проверки инициализации, а не для полного покрытия продукта. Большая часть логики должна закрываться unit- и integration-уровнем ниже. ([developer.android.com](https://developer.android.com/training/testing/fundamentals/what-to-test?utm_source=chatgpt.com))

3. **Локальный запуск и CI-запуск должны быть максимально одинаковыми.**  
   Android рекомендует build-managed devices именно потому, что они улучшают consistency, performance и reliability и приводят local/CI окружение к одному паттерну. ([developer.android.com](https://developer.android.com/studio/test/managed-devices?utm_source=chatgpt.com))

## 3. Обязательная архитектура тестового набора

### 3.1. Где живут тесты

**Обязательно разделяем тесты на три слоя:**

- `:app/src/androidTest` — screen UI tests и integration UI tests внутри приложения: один экран, одна фича, один связанный кусок навигации, плотные Hilt-подмены, локальные fake-данные, проверка состояния UI и взаимодействия Compose/View слоя. Android test source set для instrumented tests — штатное место для таких тестов. ([developer.android.com](https://developer.android.com/studio/test/test-in-android-studio?hl=en&utm_source=chatgpt.com))
- `:e2e-tests` на `com.android.test` — только малый smoke-набор критических пользовательских сценариев, которые проходят через несколько экранов и проверяют базовую жизнеспособность приложения. `com.android.test` и `targetProjectPath` официально поддерживаются AGP для отдельного test-модуля. ([developer.android.com](https://developer.android.com/reference/tools/gradle-api/8.0/com/android/build/api/dsl/TestExtension?utm_source=chatgpt.com))
- `[ENV]` suite — отдельный workflow для редких живых stage-проверок. Он организационно отделён от основного E2E-гейта.

### 3.2. Что запрещено

- Запрещено переносить большинство UI/integration тестов в отдельный `com.android.test` модуль только “ради чистоты”. Если тесту нужны плотные app-internal подмены через Hilt, он должен жить рядом с приложением в `androidTest`. Это соответствует стандартной модели instrumented tests и Hilt testing. ([developer.android.com](https://developer.android.com/studio/test/test-in-android-studio?hl=en&utm_source=chatgpt.com))
- Запрещено превращать `[ENV]` smoke в обязательный PR gate.
- Запрещено строить основной E2E-процесс вокруг stage URL, реального IAM и реального браузера.

## 4. Разрешённый и обязательный стек

### 4.1. Базовый стек

**Стандартный стек команды:**

- `AndroidJUnitRunner` как instrumentation runner. Он официально поддерживает Espresso, UI Automator и Compose testing frameworks. ([developer.android.com](https://developer.android.com/training/testing/junit-runner.html?utm_source=chatgpt.com))
- **Compose Test APIs** как основной UI driver для Compose-экранов. Compose testing API использует semantics, даёт встроенную синхронизацию и deterministic waiting до idle-состояния. ([developer.android.com](https://developer.android.com/develop/ui/compose/testing?utm_source=chatgpt.com))
- **Espresso** — только там, где есть View interoperability или это реально проще в гибридном UI. Compose официально совместим с Espresso в гибридных приложениях. ([developer.android.com](https://developer.android.com/develop/ui/compose/testing/interoperability?utm_source=chatgpt.com))
- **UI Automator** — только для cross-app и system UI: браузер, permissions, chooser, внешние интенты, системные диалоги. UI Automator официально предназначен именно для user apps и system apps и работает вне процесса приложения. ([developer.android.com](https://developer.android.com/training/testing/other-components/ui-automator?utm_source=chatgpt.com))
- **Hilt test infrastructure** как базовый механизм замены зависимостей. `@TestInstallIn` — preferred default whenever possible. ([developer.android.com](https://developer.android.com/training/dependency-injection/hilt-testing?utm_source=chatgpt.com))

### 4.2. Позиция по UI Automator версии

Для новых cross-app сценариев ориентируйтесь на **современный подход UI Automator**, который Android Docs рекомендует для нового кода, но в production dependency по умолчанию пингуйте **stable release**, если нет сознательного решения команды взять beta API. По состоянию на 11 марта 2026 года latest stable — 2.3.0, beta — 2.4.0-beta02; документация modern UI Automator отдельно рекомендует новый стиль для нового development. ([developer.android.com](https://developer.android.com/training/testing/other-components/ui-automator?utm_source=chatgpt.com))

### 4.3. Что не является стандартом команды

Внутренний стандарт **не опирается как на primary stack** на внешние обёртки поверх AndroidX test stack. Причина не в том, что они “плохие”, а в том, что для данного приложения официальный Android stack уже покрывает ключевые потребности: Compose, Hilt, instrumented runner, cross-app UI через UI Automator и управляемые устройства через AGP. ([developer.android.com](https://developer.android.com/training/testing/junit-runner.html?utm_source=chatgpt.com))

## 5. Запуск тестов: что является нормой

### 5.1. Основной режим запуска

**По умолчанию все обязательные E2E гоняются на Gradle Managed Devices.** Android Developers прямо пишет, что build-managed devices улучшают consistency, performance и reliability, используют snapshots, возвращают виртуальные устройства в clean state между тестами и подходят для серверов и CI. Для GitHub Actions на серверах без hardware rendering Android отдельно рекомендует флаг `-Pandroid.testoptions.manageddevices.emulator.gpu=swiftshader_indirect`; этот флаг решает только graphics rendering. Для x86/x86_64 emulator на Linux runner также обязателен рабочий VM acceleration/KVM-доступ для пользователя, под которым запускается Gradle. ([developer.android.com](https://developer.android.com/studio/test/managed-devices?utm_source=chatgpt.com))

### 5.2. Исключение для API 26

Managed devices официально доступны для **API 27+**. У проекта `minSdk = 26`, поэтому отдельная проверка API 26 должна идти **в отдельной secondary lane**, а не ломать основную стратегию: либо через `connectedAndroidTest` на выделенном API26-эмуляторе/устройстве, либо через отдельную удалённую инфраструктуру. Основной PR gate остаётся на managed device, потому что он стабильнее. ([developer.android.com](https://developer.android.com/studio/test/managed-devices?utm_source=chatgpt.com))

### 5.3. Test isolation

**Android Test Orchestrator обязателен** для E2E-набора. Он запускает каждый тест в отдельной instrumentation, уменьшает shared state и умеет `clearPackageData`, что снижает протекание состояния между тестами ценой дополнительного времени прогона. Для E2E, где приоритет — надёжность, это правильный trade-off. ([developer.android.com](https://developer.android.com/training/testing/junit-runner.html?utm_source=chatgpt.com))

## 6. Правила проектирования E2E

### 6.1. E2E должен быть герметичным по умолчанию

**Каждый новый E2E по умолчанию обязан быть hermetic.**  
Это означает:

- business API подменяется;
- IAM/OIDC подменяется;
- стартовые данные контролируются тестом;
- локальное состояние приложения контролируется тестом;
- результат не зависит от stage, живой сети, живых аккаунтов и живых токенов.  

Android Developers прямо рекомендует hermetic large tests и указывает, что это улучшает reliability и performance. Кроме того, для больших тестов фреймворк особенно чувствителен к внешним и асинхронным зависимостям. ([developer.android.com](https://developer.android.com/training/testing/fundamentals/test-doubles?hl=en&utm_source=chatgpt.com))

### 6.2. Предпочтительный тип test double

**По умолчанию предпочтительны fakes, а не mocks.** Android docs прямо говорит, что fakes preferred over stubs for simplicity и generally preferred. Это особенно верно для E2E, где важнее поведение и данные, а не interaction verification. ([developer.android.com](https://developer.android.com/training/testing/fundamentals/test-doubles?hl=en&utm_source=chatgpt.com))

### 6.3. Каждый E2E должен владеть своими данными

Каждый тест обязан сам создавать или засеивать входные данные, на которые он рассчитывает. Запрещено:
- зависеть от “уже существующего” аккаунта или сущности;
- использовать общий stage-аккаунт как основной способ подготовки;
- ожидать, что предыдущее выполнение тестов оставило правильное состояние.  

Это прямое следствие требований к hermetic tests, isolated state и Orchestrator-подходу. ([developer.android.com](https://developer.android.com/training/testing/fundamentals/test-doubles?hl=en&utm_source=chatgpt.com))

## 7. Hilt и организация test doubles

### 7.1. Базовое правило

Все заменяемые зависимости, которые нужны E2E, должны проходить через Hilt-граф. Для общих подмен тестового набора надо использовать `@TestInstallIn` whenever possible; `@UninstallModules` оставлять для редких точечных случаев, потому что Hilt сам рекомендует предпочитать `@TestInstallIn`. ([developer.android.com](https://developer.android.com/training/dependency-injection/hilt-testing?utm_source=chatgpt.com))

### 7.2. Что должно быть заменяемым через DI

В проекте обязательно должны быть DI-швы для:
- business API client / repository;
- IAM / auth repository / token storage facade;
- startup initialization coordinator;
- clock / dispatcher / scheduler abstractions там, где это влияет на детерминизм;
- persistent storage access facades, если их состояние нужно seed/reset.  

Это соответствует общей Android-рекомендации проектировать систему так, чтобы даже большие тесты можно было делать с test doubles через DI. ([developer.android.com](https://developer.android.com/training/testing/fundamentals/test-doubles?hl=en&utm_source=chatgpt.com))

## 8. Правила для Compose UI

### 8.1. Селекторы

**Каждый интерактивный и критически проверяемый Compose-элемент обязан иметь стабильный test handle.**  
Базовый способ — semantics / `testTag`. Compose testing использует semantics tree, а `testTag` официально предназначен для поиска нод тестовыми фреймворками. На практике это значит: не искать важные элементы только по тексту. ([developer.android.com](https://developer.android.com/develop/ui/compose/testing/semantics?utm_source=chatgpt.com))

### 8.2. Поиск по тексту

Поиск только по тексту **допускается** лишь для вторичных утверждений, когда текст сам по себе является предметом проверки. Для интерактивных действий — кнопок, полей, списков, загрузчиков, critical CTA — обязателен stable tag. Compose docs подчёркивает, что semantics tree и устойчивые handles — основа надёжных тестов. ([developer.android.com](https://developer.android.com/develop/ui/compose/testing/semantics?utm_source=chatgpt.com))

### 8.3. UI Automator + Compose

Если элемент Compose должен быть доступен из UI Automator, на верхнем уровне соответствующего поддерева надо включить `testTagsAsResourceId`, после чего `Modifier.testTag(tag)` можно матчить через `By.res(tag)`. Это официальный interop-механизм Compose ↔ UI Automator. ([developer.android.com](https://developer.android.com/develop/ui/compose/testing/interoperability?utm_source=chatgpt.com))

## 9. Правила синхронизации и ожиданий

### 9.1. Главный запрет

**`Thread.sleep`, arbitrary delay, magic wait numbers — запрещены.** Android docs прямо говорит, что sleeps делают тесты медленнее и flaky и не решают проблему синхронизации. ([developer.android.com](https://developer.android.com/training/testing/instrumented-tests/stability?utm_source=chatgpt.com))

### 9.2. Что делать вместо sleeps

Разрешённые стратегии ожидания:

- встроенная синхронизация Compose/Espresso до idle; Compose test environment explicitly waits for idle to keep tests deterministic; ([developer.android.com](https://developer.android.com/develop/ui/compose/testing?utm_source=chatgpt.com))
- ожидание **конкретного видимого состояния**, а не времени; Android docs рекомендует ждать specific conditions rather than guessing if an activity is busy; ([developer.android.com](https://developer.android.com/training/testing/instrumented-tests/stability?utm_source=chatgpt.com))
- IdlingResource / tracked background idleness для тех асинхронных операций, которые не видны фреймворку; Android docs прямо приводит этот путь; ([developer.android.com](https://developer.android.com/training/testing/instrumented-tests/stability?utm_source=chatgpt.com))
- подмена фоновых компонентов на test-friendly версии с наблюдаемым idle/readiness состоянием. ([developer.android.com](https://developer.android.com/training/testing/instrumented-tests/stability?utm_source=chatgpt.com))

### 9.3. Что считается завершением шага

После каждого действия тест должен ждать **бизнес-значимый observable result**:
- экран открылся;
- прогресс исчез;
- список показал seeded item;
- snackbar показал успешный статус;
- токен/сессия привели к появлению authenticated home;
- local persisted state восстановилось.  

Ждать “просто секунду” запрещено. ([developer.android.com](https://developer.android.com/training/testing/instrumented-tests/stability?utm_source=chatgpt.com))

## 10. Auth / OAuth2 / OIDC / AppAuth

### 10.1. Что делает AppAuth по своей природе

AppAuth for Android следует best practices RFC 8252, использует Custom Tabs для authorization requests, не поддерживает WebView, работает через browser/custom tabs и возвращает результат через `RedirectUriReceiverActivity`; авторизационное состояние инкапсулируется в `AuthState`. Это означает, что реальный auth flow — по определению cross-app и частично вне процесса приложения. ([github.com](https://github.com/openid/AppAuth-Android?utm_source=chatgpt.com))

### 10.2. Политика команды

**Реальный browser-based login запрещён в обязательном PR E2E-наборе.**  
Для регулярных E2E допускаются только два паттерна:

1. **Fake OIDC provider path** — discovery/token/userinfo/end-session подменены на контролируемые тестовые ответы.  
2. **Session bootstrap path** — тест напрямую создаёт валидное авторизационное состояние через тестовый seam и проверяет поведение приложения после успешной аутентификации.  

Это не “читерство”, а нормальная реализация hermetic E2E для нативного приложения с browser-based auth, потому что Android сам рекомендует большие flow-тесты с doubles, а AppAuth architecture явно отделяет redirect handling и persisted auth state. ([developer.android.com](https://developer.android.com/training/testing/fundamentals/test-doubles?hl=en&utm_source=chatgpt.com))

### 10.3. Что допускается как исключение

Разрешён отдельный `[ENV][AUTH]` smoke-набор из **1–2** сценариев:
- живой stage business API,
- живой IAM,
- живой browser/custom tab redirect.  

Но этот набор запускается **по расписанию, post-deploy или вручную**, а не как merge gate. Причина: cross-app browser auth по природе менее детерминированен, чем hermetic path. ([github.com](https://github.com/openid/AppAuth-Android?utm_source=chatgpt.com))

### 10.4. Что обязательно проверить в кодовой базе

В приложении должен существовать хотя бы один из следующих test seams:
- инжектируемый `AuthRepository` / `SessionManager`, который умеет получить test session;
- тестовый способ записать/подменить `AuthState`;
- подменяемый redirect/result handler;
- подменяемая `AuthorizationService` facade.  

Если этого нет, E2E-архитектура для auth считается незавершённой.

## 11. Startup initialization и launch side effects

### 11.1. Базовое правило

Любая стартовая фоновая инициализация обязана быть вынесена в **инжектируемый coordinator/use case**, который можно:
- выключить;
- перевести в fake-режим;
- сделать наблюдаемым для readiness.  

Android docs отдельно предупреждает, что background operations unknown to the test — основной источник flaky. ([developer.android.com](https://developer.android.com/training/testing/instrumented-tests/stability?utm_source=chatgpt.com))

### 11.2. Что запрещено

Запрещено, чтобы `Application.onCreate()` запускал неконтролируемый refresh, который:
- стучится в сеть без test override;
- стартует параллельно UI и может менять экран под тестом;
- не имеет observable completion signal.  

Такой код допустим в production, но для тестов обязан иметь test mode / injected replacement. Иначе E2E будет структурно flaky. ([developer.android.com](https://developer.android.com/training/testing/instrumented-tests/stability?utm_source=chatgpt.com))

### 11.3. Правильный паттерн

В E2E должен существовать явный **startup contract**:
- `startup disabled`;
- или `startup fake immediate success`;
- или `startup waits until seeded data ready`;
- или `app exposes readiness state`, по которому тест делает первый assert.  

Тест не начинает основной сценарий до выполнения этого контракта. ([developer.android.com](https://developer.android.com/training/testing/instrumented-tests/stability?utm_source=chatgpt.com))

## 12. Как писать E2E-тест

### 12.1. Структура теста

Каждый тест обязан иметь явную структуру:

1. **Arrange** — подменить зависимости через Hilt, засеять данные, указать launch args / mode, определить user state.  
2. **Act** — выполнить пользовательские действия.  
3. **Assert** — проверить observable business result.  
4. **Cleanup** — как правило, неявный через Orchestrator/clearPackageData; ручной teardown допускается только для внешних побочных эффектов. ([developer.android.com](https://developer.android.com/training/testing/junit-runner.html?utm_source=chatgpt.com))

### 12.2. Именование

- Класс тестов описывает экран или flow.
- Метод теста описывает бизнес-смысл: `givenAuthenticatedUser_whenOpenFeed_thenSeededItemsDisplayed`.
- Название должно объяснять намерение без чтения тела теста.

### 12.3. Что должен проверять assert

Assert обязан проверять **пользовательское или бизнес-видимое состояние**, а не внутреннюю реализацию. Android отдельно рекомендует избегать тестов, которые проверяют работу framework/library, а не вашего кода. ([developer.android.com](https://developer.android.com/training/testing/fundamentals/what-to-test?utm_source=chatgpt.com))

### 12.4. Что не должно попадать в E2E

В E2E запрещено уносить:
- валидацию сложных edge cases репозитория;
- все сетевые ошибки по одному;
- сериализацию/десериализацию;
- логику retry/backoff во всех комбинациях;
- чистую domain-логику.  

Это должно жить на нижних слоях. Android docs прямо говорит, что data layer, repositories, domain layer и utility classes в основном должны проверяться unit tests. ([developer.android.com](https://developer.android.com/training/testing/fundamentals/what-to-test?utm_source=chatgpt.com))

## 13. Допустимый размер E2E-набора

### 13.1. PR smoke

Обязательный PR smoke-набор должен быть **малочисленным**. Практическое правило команды:
- 5–10 критических сценариев на приложение;
- каждый сценарий доказывает отдельный пользовательский риск;
- новый E2E добавляется только если риск нельзя дешевле и стабильнее поймать ниже.  

Это соответствует Android-подходу, где user flow tests нужны для most common paths и runtime-crash smoke, а не для тотального покрытия. ([developer.android.com](https://developer.android.com/training/testing/fundamentals/what-to-test?utm_source=chatgpt.com))

### 13.2. Nightly / broader

Более широкий набор допустим в nightly, но тоже должен оставаться hermetic, если это не `[ENV]`.

## 14. Политика по build variants

### 14.1. Основной test target

Основной E2E target должен быть **тестопригодным build variant**, а не build, в котором одновременно смешаны:
- obfuscation/minify,
- environment secrets,
- живые stage endpoints,
- smoke пользовательского пути.

Причина организационная: один тест не должен одновременно проверять и пользовательский flow, и shrinker/obfuscation, и живую среду. Это ухудшает диагностичность.

### 14.2. Где тестировать stage/release

- **Stage** — только `[ENV]` lane.
- **Release/minified build** — отдельный smoke lane перед релизом или на release-candidate.  
Это не основной E2E suite, а compatibility/packaging check.

## 15. CI-стандарт для GitHub Actions

### 15.1. Обязательные workflow’ы

В репозитории должны быть три workflow-класса:

1. **`e2e-pr.yml`** — обязательный PR gate, только hermetic smoke.  
2. **`e2e-nightly.yml`** — расширенный hermetic набор, multi-API/multi-device при необходимости.  
3. **`env-smoke.yml`** — stage/browser/auth/live-service smoke, по расписанию, post-deploy или manual dispatch.  

GitHub Actions официально поддерживает workflow YAML, job dependency graph, matrix jobs, artifacts и cache для CI/CD пайплайнов. ([docs.github.com](https://docs.github.com/en/actions/writing-workflows/workflow-syntax-for-github-actions?utm_source=chatgpt.com))

### 15.2. Что должно быть в `e2e-pr.yml`

- `runs-on: ubuntu-latest`;
- managed emulator;
- KVM access setup/check перед Gradle managed-device task;
- GPU flag `swiftshader_indirect` для GitHub-hosted среды без hardware rendering;
- Gradle task вида `deviceNameVariantAndroidTest` или `groupNameGroupVariantAndroidTest`;
- Orchestrator;
- upload artifacts всегда, даже при failure.  

Task naming для managed devices и GitHub Actions GPU flag официально документированы Android Developers. Артефакты и jobs/matrix — официально документированы GitHub Docs. ([developer.android.com](https://developer.android.com/studio/test/managed-devices?utm_source=chatgpt.com))

### 15.3. Где использовать matrix

Matrix в GitHub Actions допускается только там, где даёт независимую ценность:
- API level;
- device profile;
- build variant;
- suite shard.  

GitHub Docs официально описывает matrix strategy и `max-parallel`. Для PR gate не надо раздувать matrix без необходимости; используйте её дозированно. ([docs.github.com](https://docs.github.com/actions/using-jobs/using-a-build-matrix-for-your-jobs?utm_source=chatgpt.com))

### 15.4. Что кэшировать

В GitHub Actions надо кэшировать **только зависимости и редко меняющиеся inputs**, например Gradle caches и SDK-related dependencies, потому что caching предназначен для reusable files that don’t change often. Нельзя путать cache и artifacts. ([docs.github.com](https://docs.github.com/actions/concepts/workflows-and-actions/dependency-caching?utm_source=chatgpt.com))

### 15.5. Какие артефакты сохранять всегда

Обязательно сохраняются:
- HTML и XML test reports;
- logcat / runner logs;
- screenshots на failure;
- dump window hierarchy при UI failure;
- при необходимости app/test APK и дополнительная диагностика.  

GitHub Docs рекомендует сохранять build/test output, logs, screenshots и другие debug artifacts. UI Automator `UiDevice` умеет dump hierarchy и screenshot-related device interactions. ([docs.github.com](https://docs.github.com/en/actions/how-tos/writing-workflows/choosing-what-your-workflow-does/storing-and-sharing-data-from-a-workflow?azure-portal=true&utm_source=chatgpt.com))

## 16. Правила для `[ENV]` suite

### 16.1. Когда `[ENV]` разрешён

`[ENV]` suite разрешён только если он проверяет один из следующих рисков:
- stage окружение поднялось корректно;
- IAM redirect действительно проходит end-to-end;
- деплой не сломал базовую интеграцию приложений и внешних контуров.

### 16.2. Что обязательно для `[ENV]`

- отдельный workflow;
- отдельные secrets / environments в GitHub Actions;
- отдельная маркировка тестов;
- max 1–2 auth сценария и несколько базовых бизнес-smoke сценариев;
- строгий setup / execution / teardown.  

GitHub Actions официально поддерживает environments, secrets, reusable workflow structure и artifacts для таких задач. ([docs.github.com](https://docs.github.com/en/actions/reference/workflows-and-actions?utm_source=chatgpt.com))

### 16.3. Что запрещено для `[ENV]`

- использовать `[ENV]` suite как единственный E2E signal;
- блокировать каждый PR на живой stage;
- зашивать живые stage значения и токены как обязательную часть обычного E2E-процесса.

## 17. Review checklist для каждого нового E2E

Новый E2E не принимается в кодовую базу, пока на все пункты ниже нет ответа “да”.

1. Тест действительно проверяет critical user risk, а не то, что дешевле закрыть unit/integration тестом. ([developer.android.com](https://developer.android.com/training/testing/fundamentals/what-to-test?utm_source=chatgpt.com))
2. Тест hermetic по умолчанию. ([developer.android.com](https://developer.android.com/training/testing/fundamentals/test-doubles?hl=en&utm_source=chatgpt.com))
3. Все внешние зависимости заменены через Hilt/DI. ([developer.android.com](https://developer.android.com/training/dependency-injection/hilt-testing?utm_source=chatgpt.com))
4. Для Compose-элементов есть stable semantics/test tags. ([developer.android.com](https://developer.android.com/develop/ui/compose/testing/semantics?utm_source=chatgpt.com))
5. В тесте нет `sleep`. ([developer.android.com](https://developer.android.com/training/testing/instrumented-tests/stability?utm_source=chatgpt.com))
6. Startup side effects управляемы. ([developer.android.com](https://developer.android.com/training/testing/instrumented-tests/stability?utm_source=chatgpt.com))
7. Auth не зависит от живого браузерного логина, если это не `[ENV][AUTH]`. ([github.com](https://github.com/openid/AppAuth-Android?utm_source=chatgpt.com))
8. Тест может быть запущен отдельно и в любом порядке. ([developer.android.com](https://developer.android.com/training/testing/junit-runner.html?utm_source=chatgpt.com))
9. При падении сохраняется достаточная диагностика в artifacts. ([docs.github.com](https://docs.github.com/en/actions/how-tos/writing-workflows/choosing-what-your-workflow-does/storing-and-sharing-data-from-a-workflow?azure-portal=true&utm_source=chatgpt.com))

## 18. Анти-паттерны, которые считаются ошибкой архитектуры

- E2E ходит в живой stage по умолчанию.
- E2E логинится через реальный браузер на каждом PR.
- UI ищется только по тексту или локали.
- В тесте есть `sleep`.
- Один тест зависит от результатов другого.
- Startup refresh живёт в `Application` без test override.
- Общий stage-аккаунт используется как состояние по умолчанию.
- E2E падает одновременно из-за продукта, auth, infra и shrinker — то есть тест смешивает несколько классов риска в один сигнал.  

Анти-паттерны выше конфликтуют с hermetic testing, Compose semantics-based testing, Orchestrator isolation и официальными рекомендациями по стабильности больших тестов. ([developer.android.com](https://developer.android.com/training/testing/fundamentals/test-doubles?hl=en&utm_source=chatgpt.com))

## 19. Минимум, который надо проверить в кодовой базе прямо сейчас

Чтобы этот стандарт реально заработал, в кодовой базе нужно в первую очередь проверить:

1. Есть ли отдельная auth abstraction, которую можно подменить без живого browser login.  
2. Можно ли записать или инжектировать authenticated session state без реального IAM.  
3. Вынесен ли startup refresh из `Application` в DI-управляемый coordinator.  
4. Покрыты ли все критические Compose-элементы testTag’ами.  
5. Включаем ли мы `testTagsAsResourceId` там, где нужен UI Automator.  
6. Есть ли общие `@TestInstallIn` модули для business API, auth, startup и storage.  
7. Настроен ли Orchestrator и `clearPackageData`.  
8. Есть ли отдельная lane для API 26, так как managed devices официально поддерживают API 27+. ([developer.android.com](https://developer.android.com/training/dependency-injection/hilt-testing?utm_source=chatgpt.com))

## 20. Итоговое правило “как делать правильно”

**Правильно делать так:**
- основной E2E = **малый, hermetic, deterministic smoke**, на официальном Android test stack;
- Compose = через semantics/test tags;
- Hilt = через `@TestInstallIn` и controlled doubles;
- auth = через fake OIDC или session bootstrap seam;
- startup = через injectable coordinator с readiness contract;
- запуск = **Managed Devices + Orchestrator**;
- API26 = отдельная secondary lane;
- живой stage/browser/IAM = только отдельный `[ENV]` smoke, не PR gate. ([developer.android.com](https://developer.android.com/training/testing/fundamentals/test-doubles?hl=en&utm_source=chatgpt.com))

---

## Appendix: использованные источники

Ниже — первичные источники, на которых основан стандарт.  
**Дата доступа ко всем источникам:** 2026-04-08.

- Android Developers — **Use test doubles in Android**. Основа для hermetic tests, предпочтения fakes и идеи, что даже большие E2E могут и должны использовать doubles. ([developer.android.com](https://developer.android.com/training/testing/fundamentals/test-doubles?hl=en&utm_source=chatgpt.com))
- Android Developers — **What to test in Android**. Основа для малого smoke-набора user flow tests и разграничения E2E против unit/integration уровней. ([developer.android.com](https://developer.android.com/training/testing/fundamentals/what-to-test?utm_source=chatgpt.com))
- Android Developers — **Build instrumented tests**. Основа для понимания instrumented tests как более медленных и дорогих, но более faithful. ([developer.android.com](https://developer.android.com/training/testing/instrumented-tests?utm_source=chatgpt.com))
- Android Developers — **AndroidJUnitRunner**. Основа для runner, Orchestrator, `clearPackageData`, поддержки Espresso/UI Automator/Compose. ([developer.android.com](https://developer.android.com/training/testing/junit-runner.html?utm_source=chatgpt.com))
- Android Developers — **Hilt testing guide**. Основа для `@TestInstallIn` и замены зависимостей через Hilt. ([developer.android.com](https://developer.android.com/training/dependency-injection/hilt-testing?utm_source=chatgpt.com))
- Android Developers — **Test your Compose layout** и **Semantics**. Основа для semantics-driven Compose testing и встроенной синхронизации. ([developer.android.com](https://developer.android.com/develop/ui/compose/testing?utm_source=chatgpt.com))
- Android Developers — **Compose testing interoperability** и `testTag` semantics reference. Основа для `Modifier.testTag`, `testTagsAsResourceId` и UI Automator interop. ([developer.android.com](https://developer.android.com/develop/ui/compose/testing/interoperability?utm_source=chatgpt.com))
- Android Developers — **Big test stability**. Основа для запрета `sleep`, обязательности condition-based waiting и контроля background work. ([developer.android.com](https://developer.android.com/training/testing/instrumented-tests/stability?utm_source=chatgpt.com))
- Android Developers — **Scale your tests with build-managed devices**. Основа для primary запуска через Managed Devices, API 27+ ограничения и GitHub Actions GPU flag. ([developer.android.com](https://developer.android.com/studio/test/managed-devices?utm_source=chatgpt.com))
- Android Developers / AGP API Reference — **TestExtension (`com.android.test`)**. Основа для отдельного test-модуля и `targetProjectPath`. ([developer.android.com](https://developer.android.com/reference/tools/gradle-api/8.0/com/android/build/api/dsl/TestExtension?utm_source=chatgpt.com))
- Android Developers — **Write automated tests with UI Automator** и AndroidX release notes for **test-uiautomator**. Основа для применения UI Automator только в cross-app/system сценариях и статуса современных API. ([developer.android.com](https://developer.android.com/training/testing/other-components/ui-automator?utm_source=chatgpt.com))
- Official AppAuth for Android — **README / site / API docs** (`AuthorizationService`, `RedirectUriReceiverActivity`, `AuthState`). Основа для понимания browser/custom tabs flow, redirect handling и persisted auth state. ([github.com](https://github.com/openid/AppAuth-Android?utm_source=chatgpt.com))
- GitHub Docs — **Workflow syntax**, **matrix jobs**, **dependency caching**, **workflow artifacts**. Основа для CI-структуры, matrix, cache и artifact policy. ([docs.github.com](https://docs.github.com/en/actions/writing-workflows/workflow-syntax-for-github-actions?utm_source=chatgpt.com))
