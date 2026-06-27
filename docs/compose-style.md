# compose-style.md

Ниже — внутренний стандарт команды по организации Compose-кода в Android-приложении. Он фиксирует границы между screen-level кодом и переиспользуемыми UI-компонентами, правила файловой структуры и осторожный подход к динамическим размерам.

# Стандарт Compose UI для Android-приложения

**Статус:** целевой внутренний стандарт  
**Актуальность:** на 25 июня 2026 года  
**Область действия:** Kotlin + Jetpack Compose, пакеты `ui/screens`, `ui/components` и связанные UI helpers.

**Дополнительный справочник:** подробные рекомендации по реализации, state, side effects, Paging, insets, accessibility, performance и testing собраны в `docs/compose-implementation-research.md`. Этот документ остаётся компактным style guide, а implementation research используется как справочник для refactor/review.

## 1. Зачем нужен стандарт

Стандарт нужен, чтобы новые Compose-компоненты писались одинаково, а существующий UI можно было приводить к единому виду небольшими безопасными шагами.

Главные цели:

1. **Чёткая граница ответственности.**  
   Screen-level composable работает с ViewModel, навигацией, StateFlow/Paging state и side effects. UI-компонент получает готовые данные и callbacks.

2. **Читаемая структура файлов.**  
   Kotlin coding conventions допускает несколько деклараций в одном файле, если они семантически связаны и файл остаётся разумного размера. Поэтому в проекте не требуется выносить каждый маленький subcomposable в отдельный файл. ([kotlinlang.org](https://kotlinlang.org/docs/coding-conventions.html#source-file-organization))

3. **Предсказуемая адаптивность.**  
   Контейнеры и карточки должны по возможности принимать ограничения от родителя. При этом фиксированные значения для spacing, icon size, divider thickness и touch target остаются нормальной частью дизайн-системы.

## 2. Поддерживаемые разрешения и размеры окна

Приложение используется на рабочих устройствах проводников для учёта наличия продукции. Поэтому базовая модель UI — телефон в портретной ориентации, быстрые повторяемые действия, читаемые карточки и формы без сложного планшетного сценария.

Поддержку фиксируем не через физический размер экрана и не через модель устройства, а через доступную ширину окна в `dp`. Это совпадает с Android window size classes: `compact` width — меньше `600dp`, `medium` — от `600dp` до `840dp`, `expanded` — от `840dp` до `1200dp`, дальше идут large/extra-large окна. Android также указывает, что `compact` portrait покрывает практически все телефоны в портретной ориентации, а `medium` и шире обычно относится к планшетам, foldable inner displays и desktop/windowing сценариям. ([developer.android.com](https://developer.android.com/develop/ui/compose/layouts/adaptive/use-window-size-classes))

### 2.1. Анализ возможных устройств проводников

Публичного надёжного источника с точным перечнем моделей устройств у проводников ФПК/РЖД не найдено. Поэтому стандарт исходит из консервативной рабочей гипотезы: парк может включать недорогие Android-смартфоны примерно пятилетней давности, личные/корпоративные бюджетные телефоны и специализированные Android mobile computer устройства, которые используются в retail, inventory, field service и transport/logistics сценариях.

Привязка модели к `dp`-viewport является ориентиром, а не паспортной характеристикой: фактическая доступная ширина зависит от OEM density, display size, системной навигации, insets и режима окна. Поэтому модель в таблице отвечает за класс риска, а проверяемое значение фиксируем как preview/device viewport.

Критически важные viewport'ы:

| Viewport для проверки | Примеры моделей | Почему критично |
| --- | --- | --- |
| `320dp` width, portrait | Zebra TC21/TC26, Honeywell ScanPal EDA51 class: 5" Android mobile computer, 1280x720 display; старые/защищённые 5" устройства с крупной системной плотностью или увеличенным display size | Это худший практический fallback для рабочего устройства: маленькая физическая диагональ, возможные системные панели, крупный display/font size, работа одной рукой. UI обязан оставаться функциональным. Zebra TC21/TC26 и Honeywell EDA51 прямо позиционируются как рабочие mobile computer устройства и имеют 5" HD display. ([zebra.com](https://www.zebra.com/us/en/products/spec-sheets/mobile-computers/handheld/tc21-tc26.html), [honeywell.com](https://automation.honeywell.com/us/en/products/productivity-solutions/mobile-computers/handheld-computers/scanpal-eda51-handheld-computer)) |
| `360dp` width, portrait | Samsung Galaxy A10/A10s, Samsung Galaxy A12, Redmi 9A/10A class: 6.2-6.5" budget Android, обычно HD+ 720x1520/1600 | Это наиболее важный реальный класс для бюджетных телефонов 2019-2022, то есть ровно для сценария "устройство могло быть выдано или куплено несколько лет назад". Основной UI должен быть полноценным именно здесь. ([Samsung Galaxy A10](https://en.wikipedia.org/wiki/Samsung_Galaxy_A10), [Samsung Galaxy A12](https://en.wikipedia.org/wiki/Samsung_Galaxy_A12), [Redmi 9A](https://en.wikipedia.org/wiki/Redmi_9A), [Redmi 10A](https://en.wikipedia.org/wiki/Redmi_10A)) |
| `393dp`/`411dp` width, portrait | Современные бюджетные и средние Android-телефоны с 6.4-6.7" FHD+/HD+ дисплеями | Это комфортный основной класс для актуальных телефонов. На нём не должно быть "растянутого телефона"; карточки и формы должны читаться без лишней пустоты и без мелких touch targets. |
| `360dp` width + `fontScale = 1.5f` | Любая из моделей выше при увеличенном системном шрифте | Для рабочего приложения это критично: проводник может пользоваться устройством в движении, при плохом освещении и с увеличенным шрифтом. Проверяем не красоту, а отсутствие обрезанных кнопок, перекрытий и потерянных действий. |

Менее критичные, но возможные viewport'ы:

| Viewport для проверки | Примеры моделей | Какой уровень поддержки нужен |
| --- | --- | --- |
| `600dp` width, portrait | Samsung Galaxy Tab A7 Lite class: 8.7" Android tablet, 1340x800 display | Возможный маленький планшет или wide/multi-window режим. Требование — не ломаться: контент виден, действия доступны, телефонные экраны не растягиваются нелогично. Полноценный tablet UX не обязателен. ([Samsung Galaxy Tab A7](https://en.wikipedia.org/wiki/Samsung_Galaxy_Tab_A7)) |
| `720dp` width, portrait | 8-9" планшеты и foldable inner display в портретном режиме | Проверяем sanity для будущих продаж заказчику с планшетами: максимальная ширина контента, отсутствие огромных карточек на всю ширину, рабочий вертикальный flow. |
| `840dp` width и шире | 10" tablets вроде Samsung Galaxy Tab A7 10.4, expanded/foldable/desktop windowing | Это отложенная поддержка. Экран должен оставаться технически пригодным, но двухпанельные layout'ы, navigation rail и специальные tablet-сценарии вводятся только отдельной фичей. |

Если появится реальный список устройств заказчика, этот блок надо пересчитать по фактическим `adb shell wm size` и `adb shell wm density` для каждой модели. До этого критичными считаем `320dp`, `360dp`, `393/411dp` и `360dp` с `fontScale = 1.5f`; `600dp+` — режим "не ломаться".

### 2.2. Уровни поддержки

1. **Минимальный обязательный fallback: `320-359dp` width, portrait.**  
   Поддерживаем как худший реалистичный случай для старого или бюджетного телефона. Экран не обязан выглядеть идеально, но обязан оставаться рабочим: нет горизонтального скролла всего экрана, основные действия доступны, текст не перекрывает соседние элементы, важные кнопки не обрезаются, контент можно дочитать через вертикальный scroll.

2. **Основной целевой диапазон: `360-599dp` width, portrait.**  
   Это главный продуктовый таргет для актуальных и примерно пятилетних рабочих телефонов. В этом диапазоне основные экраны должны выглядеть полноценно: один вертикальный поток, предсказуемые отступы, карточки на всю доступную ширину с нормальной читаемостью, поддержка длинных русских/английских строк и увеличенного font scale.

3. **Режим "не ломаться": `600-839dp` width.**  
   Это не полноценная планшетная поддержка. Требование: UI не должен нелогично растягиваться, перекрывать элементы или терять доступ к действиям. Для экранов, которые остаются концептуально телефонными, по умолчанию ограничиваем максимальную ширину контента и центрируем его внутри окна. Сложные двухколоночные layout'ы не вводим без отдельного продуктового решения.

4. **Отложенная tablet/wide поддержка: `840dp+` width.**  
   На expanded/large окнах приложение должно оставаться технически пригодным: контент виден, скролл работает, действия доступны. Полноценные сценарии для планшетов, foldables, landscape и desktop windowing считаются отдельной инициативой. Не обещаем оптимальную плотность, двухпанельную навигацию или специальные tablet-компоненты в рамках обычного refactor.

### 2.3. Ориентация

Основные экраны приложения могут оставаться только в портретной ориентации. В текущей архитектуре `MainActivity` программно выставляет portrait для большинства маршрутов, а sensor orientation включается только для статистики и исторической статистики.

При этом стандарт не должен зависеть только от orientation lock. Для приложений, которые target'ят Android 16/API 36 и выше, Android игнорирует ограничения `screenOrientation`, aspect ratio и resizability на больших экранах с `sw >= 600dp`; для API 37 opt-out должен исчезнуть. Поэтому любое новое UI-правило для основных экранов должно как минимум проходить режим "не ломаться" на `600dp+`, даже если продуктово экран считается портретным. ([developer.android.com](https://developer.android.com/develop/ui/compose/layouts/adaptive/app-orientation-aspect-ratio-resizability))

### 2.4. Что делать, когда экран слишком широкий

Для этого проекта порядок решений такой:

1. **Сначала ограничить читаемую ширину.**  
   Для телефонного сценария на wide viewport используем внешний контейнер с `widthIn(max = ...)`, `fillMaxWidth()` и центрированием. Это лучший дефолт для форм, одиночных карточек, main trip flow, профиля, настроек и экранов с одним главным действием. Так мы не создаём новую информационную архитектуру и не растягиваем карточки до нелогичной ширины.

2. **Adaptive grid использовать только для независимых однотипных элементов.**  
   `LazyVerticalGrid` с адаптивными колонками уместен для каталога продуктов, упаковок или других равноправных карточек, где порядок чтения и действия не меняют смысл при переносе в несколько колонок. Для истории поездок на `MainTripView` это пока не целевой дефолт: история связана с активной поездкой, bottom action и навигацией в историю, а две колонки меняют плотность и требуют отдельной проверки UX.

3. **List-detail или supporting pane вводить только как отдельную фичу.**  
   Android рекомендует list-detail для коллекции и подробностей, когда на больших окнах можно показать две панели рядом, а на малых — одну панель за раз. Для ChaikaKotlin это подходит только будущим планшетным сценариям, например "список поездок + подробности" или "каталог + выбранная позиция", но не как механическая замена всех списков. ([developer.android.com](https://developer.android.com/develop/ui/compose/layouts/adaptive/list-detail))

4. **Адаптивную навигацию не смешивать с локальной правкой экрана.**  
   `NavigationSuiteScaffold` может переключать bottom bar / navigation rail по window size class, но это меняет глобальный shell приложения. Для текущего этапа это не часть локального scalable UI refactor. ([developer.android.com](https://developer.android.com/develop/ui/compose/layouts/adaptive/build-adaptive-navigation))

Практическое правило: если wide-экран делает компонент некрасивым, но не добавляет нового рабочего сценария, ограничиваем ширину. Если wide-экран позволяет реально быстрее выполнять задачу с независимыми элементами, рассматриваем adaptive grid. Если wide-экран требует одновременно видеть список и детали, это уже отдельная tablet-фича.

## 3. Где живёт код

### 3.1. Screen-level composables

В `ui/screens/*` живут composable, которые:

- получают или создают ViewModel;
- собирают `StateFlow`, `PagingData`, one-shot events;
- работают с навигацией;
- показывают snackbar/dialog side effects;
- решают, какой state передать ниже в UI-компоненты.

Это соответствует Compose state hoisting: state должен подниматься к lowest common ancestor, а ViewModel обычно остаётся на screen-level boundary, откуда вниз передаются state и events. ([developer.android.com](https://developer.android.com/develop/ui/compose/state-hoisting))

### 3.2. UI components

В `ui/components/<feature>/*` живут переиспользуемые компоненты:

- без прямой зависимости от ViewModel;
- без навигации;
- без сбора Flow/Paging;
- с входными данными и callbacks в параметрах;
- с `modifier: Modifier = Modifier`, если компонент является публичным UI API.

Compose API guidelines отдельно подчёркивает важность предсказуемого API composable-функций и modifier-параметра для внешней настройки layout/behavior. ([android.googlesource.com](https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-api-guidelines.md))

## 4. Правила файловой структуры

### 4.1. Один самостоятельный публичный компонент — один файл

Если composable является самостоятельной UI-сущностью, файл должен называться так же, как компонент:

- `OperationCard.kt` содержит `OperationCard`;
- `CartProductItem.kt` содержит `CartProductItem`;
- `NewTripButton.kt` содержит `NewTripButton`.

### 4.2. Приватные subcomposables остаются рядом

Если subcomposable используется только внутри одного компонента, его надо оставлять в том же файле и делать `private`.

Это предпочтительно для:

- строк внутри карточки;
- локальных секций;
- маленьких helper-компонентов;
- preview-only вспомогательных блоков.

### 4.3. Когда выносить в отдельный файл

Subcomponent надо выносить в отдельный файл, если выполняется хотя бы одно условие:

- используется минимум в двух местах;
- имеет самостоятельный публичный API;
- имеет отдельный meaningful preview;
- содержит собственное состояние или заметную логику;
- исходный файл стабильно становится больше примерно 250-300 строк.

### 4.4. Что запрещено

- Запрещено создавать общие `Util`-файлы для несвязанных UI helpers.
- Запрещено прокидывать ViewModel в `ui/components`, если компонент можно выразить через state и callbacks.
- Запрещено делать helper публичным только потому, что Kotlin top-level declarations по умолчанию public. Если helper не является API компонента, он должен быть `private`.

## 5. Правила API composable-компонентов

### 5.1. Modifier

Публичный composable обязан принимать:

```kotlin
modifier: Modifier = Modifier
```

и применять его к root UI node.

Если у компонента есть обязательные данные, `modifier` ставится после них или первым optional-параметром, в зависимости от уже принятого локального стиля файла. Главное правило: внешний caller должен иметь возможность управлять размером, padding, testTag и layout-поведением root node.

### 5.2. State и callbacks

Компонент должен принимать:

- готовые domain/UI данные;
- простые flags;
- callbacks вида `onClick`, `onRetry`, `onDismiss`, `onQuantityIncrease`.

Компонент не должен сам:

- вызывать методы ViewModel;
- создавать Flow collection;
- решать navigation route;
- читать screen-level event stream.

### 5.3. Preview

Preview держим рядом с компонентом.

- Meaningful preview сохраняем в коде постоянно как пример внешнего вида и API компонента.
- Preview-функцию держим рядом с компонентом, делаем `private` и называем по схеме `ComponentNamePreview`.
- Для screen-level UI preview создаём для чистого `*Content`, принимающего готовый state и callbacks. Wrapper с ViewModel, DI, Flow collection и навигацией напрямую не preview-им.
- Preview оборачиваем в `ChaikaTheme`, если компонент зависит от цветов, typography или shapes приложения.
- Данные должны быть небольшими и детерминированными: без сети, случайных значений, текущего времени и обязательных side effects. Callbacks по умолчанию передаём пустыми.
- Выбираем только состояния, заметно меняющие UI: content, loading, empty, error, selected/disabled, длинный текст и другие реальные граничные случаи.
- Адаптивные компоненты проверяем минимум на compact и wide viewport. Dark theme, увеличенный font scale и другую locale добавляем там, где они действительно могут повлиять на layout.
- Для основных экранов минимальный preview-набор: `320dp` narrow fallback, `360dp` normal phone, `393dp`/`411dp` modern phone, `360dp` с `fontScale = 1.5f`, и `600dp` wide no-break. Для компонентов, которые потенциально используются на планшете или в wide mode, добавляем `840dp` sanity preview.
- Повторяющиеся наборы конфигураций объединяем через MultiPreview-аннотацию. Не создаём полный cross-product тем, размеров и состояний, если он не даёт новой информации.
- Interactive Mode и Animation Preview используем для локального состояния и анимаций, но не ожидаем от них настоящей навигации, backend или полного runtime-окружения приложения.
- Одноразовые и полностью дублирующие preview после работы удаляем.
- Preview не заменяет компиляцию и проверку на реальном устройстве.

Preview fake-data не надо выносить в production API без необходимости. Если fake-data становится большой или повторяется, её можно вынести в test/preview-only helper отдельным решением.

## 6. Правила размеров

### 6.1. Что делаем динамическим

Для reusable containers, карточек и screen-level блоков предпочтительны:

- `fillMaxWidth`;
- `weight`;
- `heightIn` / `widthIn`;
- `defaultMinSize`;
- constraints из `ConstraintLayout`;
- размер, пришедший через внешний `modifier`.

Главное правило: родитель должен иметь возможность управлять шириной/высотой компонента.

Для screen-level контента допускается явный maximum content width. Это не считается "жёсткой шириной компонента", если контейнер всё равно использует `fillMaxWidth()`, не ломает узкие экраны и нужен для читаемости на `600dp+`.

### 6.2. Что можно оставлять фиксированным

Фиксированные `dp` допустимы для:

- padding и spacing;
- icon size;
- divider thickness;
- corner radius;
- minimum touch target;
- Material component minimum height;
- высоты элемента, если она является частью явного дизайн-контракта.

### 6.3. Как менять старый код

Старый UI приводим к динамическим размерам осторожно:

1. Сначала исправляем структуру и API компонента.
2. Затем убираем только те жёсткие размеры, которые явно мешают адаптации или дублируют constraints родителя.
3. После изменения проверяем экран визуально на узком и обычном viewport.
4. Если экран выглядит нелогично на `600dp+`, сначала добавляем ограничение ширины родительского контента, а не переделываем список в две колонки.
5. Не заменяем все `.dp` механически.

## 7. Практический порядок refactor

Для существующего кода используем маленькие проходы:

1. Выбрать один feature-пакет или один общий компонент.
2. Убрать ViewModel из `components`, если она туда попала.
3. Сделать внутренние helpers `private`.
4. Добавить/починить `modifier`.
5. Убрать только очевидно лишние fixed width/height.
6. Прогнать `compileDebugKotlin` и `ktlintCheck`.
7. Проверить затронутые экраны вручную.

## 8. Источники

- Kotlin coding conventions — source file organization:  
  https://kotlinlang.org/docs/coding-conventions.html#source-file-organization
- Jetpack Compose API guidelines:  
  https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-api-guidelines.md
- Jetpack Compose state hoisting:  
  https://developer.android.com/develop/ui/compose/state-hoisting
- Jetpack Compose previews: https://developer.android.com/develop/ui/compose/tooling/previews
- Android window size classes:  
  https://developer.android.com/develop/ui/compose/layouts/adaptive/use-window-size-classes
- Android app orientation, aspect ratio, and resizability:  
  https://developer.android.com/develop/ui/compose/layouts/adaptive/app-orientation-aspect-ratio-resizability
- Android list-detail adaptive layout:  
  https://developer.android.com/develop/ui/compose/layouts/adaptive/list-detail
- Android adaptive navigation:  
  https://developer.android.com/develop/ui/compose/layouts/adaptive/build-adaptive-navigation
- Zebra TC21/TC26 specifications:  
  https://www.zebra.com/us/en/products/spec-sheets/mobile-computers/handheld/tc21-tc26.html
- Honeywell ScanPal EDA51 specifications:  
  https://automation.honeywell.com/us/en/products/productivity-solutions/mobile-computers/handheld-computers/scanpal-eda51-handheld-computer
