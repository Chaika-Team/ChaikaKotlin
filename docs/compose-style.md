# compose-style.md

Ниже — внутренний стандарт команды по организации Compose-кода в Android-приложении. Он фиксирует границы между screen-level кодом и переиспользуемыми UI-компонентами, правила файловой структуры и осторожный подход к динамическим размерам.

# Стандарт Compose UI для Android-приложения

**Статус:** целевой внутренний стандарт  
**Актуальность:** на 13 июня 2026 года  
**Область действия:** Kotlin + Jetpack Compose, пакеты `ui/screens`, `ui/components` и связанные UI helpers.

## 1. Зачем нужен стандарт

Стандарт нужен, чтобы новые Compose-компоненты писались одинаково, а существующий UI можно было приводить к единому виду небольшими безопасными шагами.

Главные цели:

1. **Чёткая граница ответственности.**  
   Screen-level composable работает с ViewModel, навигацией, StateFlow/Paging state и side effects. UI-компонент получает готовые данные и callbacks.

2. **Читаемая структура файлов.**  
   Kotlin coding conventions допускает несколько деклараций в одном файле, если они семантически связаны и файл остаётся разумного размера. Поэтому в проекте не требуется выносить каждый маленький subcomposable в отдельный файл. ([kotlinlang.org](https://kotlinlang.org/docs/coding-conventions.html#source-file-organization))

3. **Предсказуемая адаптивность.**  
   Контейнеры и карточки должны по возможности принимать ограничения от родителя. При этом фиксированные значения для spacing, icon size, divider thickness и touch target остаются нормальной частью дизайн-системы.

## 2. Где живёт код

### 2.1. Screen-level composables

В `ui/screens/*` живут composable, которые:

- получают или создают ViewModel;
- собирают `StateFlow`, `PagingData`, one-shot events;
- работают с навигацией;
- показывают snackbar/dialog side effects;
- решают, какой state передать ниже в UI-компоненты.

Это соответствует Compose state hoisting: state должен подниматься к lowest common ancestor, а ViewModel обычно остаётся на screen-level boundary, откуда вниз передаются state и events. ([developer.android.com](https://developer.android.com/develop/ui/compose/state-hoisting))

### 2.2. UI components

В `ui/components/<feature>/*` живут переиспользуемые компоненты:

- без прямой зависимости от ViewModel;
- без навигации;
- без сбора Flow/Paging;
- с входными данными и callbacks в параметрах;
- с `modifier: Modifier = Modifier`, если компонент является публичным UI API.

Compose API guidelines отдельно подчёркивает важность предсказуемого API composable-функций и modifier-параметра для внешней настройки layout/behavior. ([android.googlesource.com](https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-api-guidelines.md))

## 3. Правила файловой структуры

### 3.1. Один самостоятельный публичный компонент — один файл

Если composable является самостоятельной UI-сущностью, файл должен называться так же, как компонент:

- `OperationCard.kt` содержит `OperationCard`;
- `CartProductItem.kt` содержит `CartProductItem`;
- `NewTripButton.kt` содержит `NewTripButton`.

### 3.2. Приватные subcomposables остаются рядом

Если subcomposable используется только внутри одного компонента, его надо оставлять в том же файле и делать `private`.

Это предпочтительно для:

- строк внутри карточки;
- локальных секций;
- маленьких helper-компонентов;
- preview-only вспомогательных блоков.

### 3.3. Когда выносить в отдельный файл

Subcomponent надо выносить в отдельный файл, если выполняется хотя бы одно условие:

- используется минимум в двух местах;
- имеет самостоятельный публичный API;
- имеет отдельный meaningful preview;
- содержит собственное состояние или заметную логику;
- исходный файл стабильно становится больше примерно 250-300 строк.

### 3.4. Что запрещено

- Запрещено создавать общие `Util`-файлы для несвязанных UI helpers.
- Запрещено прокидывать ViewModel в `ui/components`, если компонент можно выразить через state и callbacks.
- Запрещено делать helper публичным только потому, что Kotlin top-level declarations по умолчанию public. Если helper не является API компонента, он должен быть `private`.

## 4. Правила API composable-компонентов

### 4.1. Modifier

Публичный composable обязан принимать:

```kotlin
modifier: Modifier = Modifier
```

и применять его к root UI node.

Если у компонента есть обязательные данные, `modifier` ставится после них или первым optional-параметром, в зависимости от уже принятого локального стиля файла. Главное правило: внешний caller должен иметь возможность управлять размером, padding, testTag и layout-поведением root node.

### 4.2. State и callbacks

Компонент должен принимать:

- готовые domain/UI данные;
- простые flags;
- callbacks вида `onClick`, `onRetry`, `onDismiss`, `onQuantityIncrease`.

Компонент не должен сам:

- вызывать методы ViewModel;
- создавать Flow collection;
- решать navigation route;
- читать screen-level event stream.

### 4.3. Preview

Preview держим рядом с компонентом.

- Meaningful preview сохраняем в коде постоянно как пример внешнего вида и API компонента.
- Preview-функцию держим рядом с компонентом, делаем `private` и называем по схеме `ComponentNamePreview`.
- Для screen-level UI preview создаём для чистого `*Content`, принимающего готовый state и callbacks. Wrapper с ViewModel, DI, Flow collection и навигацией напрямую не preview-им.
- Preview оборачиваем в `ChaikaTheme`, если компонент зависит от цветов, typography или shapes приложения.
- Данные должны быть небольшими и детерминированными: без сети, случайных значений, текущего времени и обязательных side effects. Callbacks по умолчанию передаём пустыми.
- Выбираем только состояния, заметно меняющие UI: content, loading, empty, error, selected/disabled, длинный текст и другие реальные граничные случаи.
- Адаптивные компоненты проверяем минимум на compact и wide viewport. Dark theme, увеличенный font scale и другую locale добавляем там, где они действительно могут повлиять на layout.
- Повторяющиеся наборы конфигураций объединяем через MultiPreview-аннотацию. Не создаём полный cross-product тем, размеров и состояний, если он не даёт новой информации.
- Interactive Mode и Animation Preview используем для локального состояния и анимаций, но не ожидаем от них настоящей навигации, backend или полного runtime-окружения приложения.
- Одноразовые и полностью дублирующие preview после работы удаляем.
- Preview не заменяет компиляцию и проверку на реальном устройстве.

Preview fake-data не надо выносить в production API без необходимости. Если fake-data становится большой или повторяется, её можно вынести в test/preview-only helper отдельным решением.

## 5. Правила размеров

### 5.1. Что делаем динамическим

Для reusable containers, карточек и screen-level блоков предпочтительны:

- `fillMaxWidth`;
- `weight`;
- `heightIn` / `widthIn`;
- `defaultMinSize`;
- constraints из `ConstraintLayout`;
- размер, пришедший через внешний `modifier`.

Главное правило: родитель должен иметь возможность управлять шириной/высотой компонента.

### 5.2. Что можно оставлять фиксированным

Фиксированные `dp` допустимы для:

- padding и spacing;
- icon size;
- divider thickness;
- corner radius;
- minimum touch target;
- Material component minimum height;
- высоты элемента, если она является частью явного дизайн-контракта.

### 5.3. Как менять старый код

Старый UI приводим к динамическим размерам осторожно:

1. Сначала исправляем структуру и API компонента.
2. Затем убираем только те жёсткие размеры, которые явно мешают адаптации или дублируют constraints родителя.
3. После изменения проверяем экран визуально на узком и обычном viewport.
4. Не заменяем все `.dp` механически.

## 6. Практический порядок refactor

Для существующего кода используем маленькие проходы:

1. Выбрать один feature-пакет или один общий компонент.
2. Убрать ViewModel из `components`, если она туда попала.
3. Сделать внутренние helpers `private`.
4. Добавить/починить `modifier`.
5. Убрать только очевидно лишние fixed width/height.
6. Прогнать `compileDebugKotlin` и `ktlintCheck`.
7. Проверить затронутые экраны вручную.

## 7. Источники

- Kotlin coding conventions — source file organization:  
  https://kotlinlang.org/docs/coding-conventions.html#source-file-organization
- Jetpack Compose API guidelines:  
  https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-api-guidelines.md
- Jetpack Compose state hoisting:  
  https://developer.android.com/develop/ui/compose/state-hoisting
- Jetpack Compose previews: https://developer.android.com/develop/ui/compose/tooling/previews
