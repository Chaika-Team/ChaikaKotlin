# Compose UI implementation research

**Статус:** справочник для refactor/review, не обязательный командный стандарт  
**Актуальность:** 27 июня 2026 года  
**Область:** Kotlin + Jetpack Compose UI в ChaikaKotlin  
**Связанный документ:** `docs/compose-style.md`

Этот документ дополняет `compose-style.md`. Style guide фиксирует форму кода, границы `screen`/`component`, preview-стандарт и подход к размерам. Здесь фокус другой: как проектировать и реализовывать Compose UI так, чтобы экраны были устойчивыми к recomposition, lifecycle, длинным строкам, font scale, Paging, side effects, insets, accessibility и тестированию.

Главная рекомендация для текущего проекта: писать Compose как тонкий declarative rendering слой над явным UI state. Экран собирает state и события из `ViewModel`, превращает их в понятный `*Content` API, а компоненты ниже остаются stateless или локально stateful только для чисто UI-состояния.

## 1. Ментальная модель Compose

Compose не является "XML, написанным на Kotlin". Composable-функция описывает, каким должен быть UI при текущем состоянии. Runtime может вызывать её часто, частично и не в том порядке, который удобно представлять как imperative rendering. Поэтому внутри composable нельзя рассчитывать на "один вызов = один раз отрисовали экран".

Практические следствия:

1. Composable должен быть идемпотентным относительно входных параметров.
2. Любая работа с внешним миром должна идти через controlled effect API: `LaunchedEffect`, `DisposableEffect`, `SideEffect`, `produceState`, `snapshotFlow`.
3. Долгая работа, бизнес-логика, сеть, БД, репозитории и use case остаются вне UI.
4. UI должен читать observable state и перерисовываться из него, а не мутировать внешний объект "по пути".
5. `remember` не является persistent storage. Это память composition lifecycle, а не экранного сценария или бизнес-состояния.

Для ChaikaKotlin это означает: `ProductCartView`, `OperationScreen`, `MainTripView`, `TemplateEditView` и похожие screen-level composables могут работать с `ViewModel`, `NavController`, `SnackbarHostState`, `LazyPagingItems`, но всё ниже должно получать готовые значения и callbacks.

## 2. Слои UI-кода

### 2.1. Screen wrapper

Screen wrapper живёт в `ui/screens`. Он имеет право:

- получать `ViewModel` через Hilt или принимать его из `NavGraph`;
- читать `StateFlow` через `collectAsStateWithLifecycle()`;
- читать Paging через `collectAsLazyPagingItems()`;
- создавать `SnackbarHostState`, `LazyListState`, sheet/dialog state;
- запускать UI side effects;
- вызывать navigation callbacks;
- маппить domain/screen state в UI-параметры.

Screen wrapper не должен становиться местом большой разметки. Его цель - собрать зависимости и передать чистому content-компоненту простые значения.

Шаблон:

```kotlin
@Composable
fun ExampleScreen(
    viewModel: ExampleViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ExampleSideEffects(
        event = uiState.event,
        snackbarHostState = snackbarHostState,
        onEventConsumed = viewModel::onEventConsumed
    )

    ExampleContent(
        state = uiState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onRetry = viewModel::retry,
        onItemClick = viewModel::selectItem
    )
}
```

### 2.2. Content composable

`*Content` - основная previewable единица экрана. Она:

- принимает уже собранный state;
- не создаёт ViewModel;
- не собирает Flow;
- не знает route strings;
- не ходит в use case;
- может владеть локальным UI state, если он не нужен бизнес-логике.

Именно `*Content` должен иметь narrow/normal/large-font preview. Если screen wrapper не previewable из-за Hilt, navigation или runtime dependencies, это нормально: preview должен проверять layout, а не DI.

### 2.3. Reusable component

Компонент в `ui/components` должен быть ещё уже:

- данные + callbacks;
- `modifier: Modifier = Modifier` на публичном API;
- root node уважает внешний modifier;
- внутренние helper composables `private`;
- локальный state только для UI-механики: раскрыт dropdown, фокус, локальная анимация, pressed/expanded состояние.

Плохой сигнал: компоненту понадобился `ViewModel`, `NavController`, repository, paging flow или `Context` ради бизнес-действия. Обычно это значит, что компонент пересёк screen boundary.

## 3. State: что где хранить

### 3.1. Screen UI state

Screen UI state - всё, что нужно для отображения экрана и зависит от бизнес-данных:

- список товаров;
- текущая смена/поездка;
- состояние продажи;
- загрузка/ошибка/пустой state;
- selected conductor, selected trip, selected package;
- текст ошибки, который приходит из domain/result mapping.

Такой state должен жить во `ViewModel` или приходить из screen-level holder. UI его только читает.

Рекомендуемый формат:

```kotlin
data class ExampleUiState(
    val isLoading: Boolean = false,
    val items: List<ExampleItemUi> = emptyList(),
    val errorMessageRes: Int? = null,
    val canSubmit: Boolean = false
)
```

Для ChaikaKotlin лучше отдавать в UI уже подготовленные UI-модели, если domain model заставляет компонент знать лишние бизнес-детали. Но не стоит плодить UI-модели механически: если domain object уже малый, стабильный и без лишних зависимостей, его можно передавать до тех пор, пока компонент не начинает принимать решения бизнес-уровня.

### 3.2. UI element state

UI element state - состояние самого виджета:

- раскрыт ли dropdown;
- текущий scroll position;
- выбранная вкладка, если это чисто локальная навигация внутри composable;
- локальный текст поля до подтверждения;
- animation state;
- sheet visibility, если sheet не управляет бизнес-сценарием.

Для него допустимы:

- `remember`;
- `rememberSaveable`;
- `rememberLazyListState`;
- plain state holder class;
- stateful + stateless пары компонента.

Пример пары:

```kotlin
@Composable
fun QuantityPicker(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        IconButton(onClick = { onQuantityChange(quantity - 1) }) { /* ... */ }
        Text(text = quantity.toString())
        IconButton(onClick = { onQuantityChange(quantity + 1) }) { /* ... */ }
    }
}
```

Если quantity влияет на sale/cart business state, он должен быть hoisted. Если expanded dropdown не нужен никому снаружи, его можно оставить внутри через `rememberSaveable`.

### 3.3. `remember` vs `rememberSaveable`

Использовать `remember`, когда:

- значение можно потерять при configuration change;
- значение дешево восстановить;
- это animation object, coroutine scope, snackbar host, локальный interaction state;
- state не нужен после Activity recreation.

Использовать `rememberSaveable`, когда:

- пользователь вводит текст;
- пользователь раскрыл/выбрал что-то в локальном UI и потеря состояния будет раздражать;
- элемент может уйти из composition и вернуться;
- state поддерживается `Bundle` или есть понятный `Saver`.

Не использовать ни то, ни другое для screen business state, который должен пережить process recreation или загрузиться из репозитория. Такой state принадлежит `ViewModel`/repository.

### 3.4. Избегать mutable collections как state

Не хранить в Compose state обычные mutable collections и mutable data classes, если их изменение не наблюдаемо:

```kotlin
// Плохо: мутация списка может не вызвать recomposition.
val items = remember { mutableListOf<ProductUi>() }
items.add(product)
```

Лучше:

```kotlin
var items by remember { mutableStateOf(emptyList<ProductUi>()) }
items = items + product
```

Во `ViewModel` аналогично: наружу отдавать immutable `List`, `Map`, `Set` и новый объект state при изменении.

## 4. Flow, lifecycle и Paging

### 4.1. Flow collection

На Android использовать `collectAsStateWithLifecycle()` для `StateFlow`/`Flow`, потому что collection учитывает lifecycle и не тратит ресурсы, когда UI не активен.

Правильно:

```kotlin
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

Нежелательно в screen-level Android UI:

```kotlin
val uiState by viewModel.uiState.collectAsState()
```

`collectAsState()` уместен для platform-agnostic Compose-кода, но в этом проекте UI Android-only, поэтому дефолт - lifecycle-aware API.

### 4.2. Paging Compose

Для Paging UI нужно отдельно думать о `refresh`, `append`, `prepend` и `itemCount`. Частая ошибка - обработать только initial loading/error и забыть append error.

Минимальная модель ветвления для списка:

1. `refresh is LoadState.Loading && itemCount == 0` - initial loading.
2. `refresh is LoadState.Error && itemCount == 0` - initial error.
3. `refresh is LoadState.NotLoading && itemCount == 0` - empty state.
4. content items.
5. `append is LoadState.Loading` - loading footer.
6. `append is LoadState.Error` - retry/error footer.

Шаблон:

```kotlin
when {
    items.loadState.refresh is LoadState.Loading && items.itemCount == 0 -> LoadingContent()
    items.loadState.refresh is LoadState.Error && items.itemCount == 0 -> ErrorContent(onRetry = items::retry)
    items.loadState.refresh is LoadState.NotLoading && items.itemCount == 0 -> EmptyContent()
    else -> LazyColumn {
        items(
            count = items.itemCount,
            key = items.itemKey { item -> item.id }
        ) { index ->
            val item = items[index] ?: return@items
            ItemRow(item = item)
        }

        when (items.loadState.append) {
            is LoadState.Loading -> item { LoadingFooter() }
            is LoadState.Error -> item { ErrorFooter(onRetry = items::retry) }
            else -> Unit
        }
    }
}
```

Для ChaikaKotlin это особенно важно в `OperationScreen`, `TemplateSearchView`, `TemplateEditView`, `FindByNumberView`, `AutonomousTripView`: Paging state не должен превращаться в один общий `isLoading`.

### 4.3. Stable keys в Lazy layouts

В `LazyColumn`, `LazyRow`, `LazyVerticalGrid` всегда задавать stable key, если item имеет id. Без key remembered state привязан к позиции, а не к объекту; при reorder/insert/delete можно потерять scroll state вложенных элементов, animation state, remembered input.

Правильно:

```kotlin
LazyColumn {
    items(
        items = products,
        key = { product -> product.id }
    ) { product ->
        ProductItem(product = product)
    }
}
```

Для Paging:

```kotlin
items(
    count = pagingItems.itemCount,
    key = pagingItems.itemKey { item -> item.id }
) { index -> /* ... */ }
```

Key должен быть stable, unique и совместимым с `Bundle`, если внутри item используется `rememberSaveable`.

### 4.4. Не вкладывать scroll без явного решения

`LazyColumn` внутри `Column.verticalScroll`, `LazyColumn` внутри `LazyColumn`, horizontal scroll внутри vertical cards - всё это требует явного UX-решения. Вложенный scroll допустим, но должен быть осознанным:

- есть ли конфликт gestures;
- где показывается loading/error footer;
- как работает nested scroll;
- не теряется ли parent scroll;
- как UI ведёт себя с большим font scale;
- доступна ли последняя кнопка над bottom bar/IME.

Для обычных списков в ChaikaKotlin дефолт: один главный vertical lazy container на экран.

## 5. Side effects

### 5.1. Правило

Composable body должен быть side-effect free. Нельзя прямо в body:

- показывать snackbar;
- делать navigation;
- вызывать suspend-функцию;
- отправлять analytics;
- читать/писать shared preferences;
- мутировать ViewModel state только потому, что state сейчас такой.

Всё это должно идти через effect API или event handler.

### 5.2. `LaunchedEffect`

Использовать для suspend side effects, которые должны стартовать при входе в composition или при изменении key:

```kotlin
LaunchedEffect(stockLimitNotice?.id) {
    val message = stockLimitMessage ?: return@LaunchedEffect
    snackbarHostState.showSnackbar(message)
    onStockLimitNoticeShown()
}
```

Key должен описывать identity события. Для one-shot snackbar в проекте хорошая практика - event object с `id`, чтобы повторный такой же текст мог показаться, если это новое событие.

Осторожно с `LaunchedEffect(Unit)`: это нормально для "один раз при появлении screen wrapper", например `viewModel.loadInitialData()`, но подозрительно, если effect на самом деле зависит от параметра route, selected id или callback. Если зависит - key должен быть конкретным:

```kotlin
LaunchedEffect(templateId) {
    viewModel.loadTemplate(templateId)
}
```

### 5.3. `rememberUpdatedState`

Нужен, когда долгоживущий effect должен видеть последнюю lambda/value, но не должен перезапускаться при её изменении.

Пример:

```kotlin
val currentOnTimeout by rememberUpdatedState(onTimeout)

LaunchedEffect(Unit) {
    delay(SPLASH_TIMEOUT)
    currentOnTimeout()
}
```

Если использовать `onTimeout` напрямую как key, таймер будет перезапускаться при каждой новой lambda.

### 5.4. `rememberCoroutineScope`

Использовать для coroutine, стартующей из event handler:

```kotlin
val scope = rememberCoroutineScope()

Button(
    onClick = {
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }
) {
    Text(text = stringResource(R.string.retry))
}
```

Не использовать `rememberCoroutineScope` как способ запустить бизнес-операцию из компонента. Если это продажа, отправка отчёта, поиск поездки, logout - callback должен уйти в ViewModel.

### 5.5. `DisposableEffect`

Использовать там, где нужен cleanup:

- register/unregister listener;
- lifecycle observer;
- external callback;
- sensor/location-style subscription.

В обычном UI ChaikaKotlin это должно встречаться редко.

### 5.6. `snapshotFlow`

Использовать, когда нужно превратить Compose state в Flow, например scroll analytics или реакция на `LazyListState`:

```kotlin
LaunchedEffect(listState) {
    snapshotFlow { listState.firstVisibleItemIndex }
        .distinctUntilChanged()
        .collect { index -> /* UI-related reaction */ }
}
```

Не использовать для business data, которую можно сразу держать во `ViewModel`.

## 6. Modifiers и layout constraints

### 6.1. Публичный modifier

Публичный composable принимает `modifier: Modifier = Modifier` и применяет его к root node:

```kotlin
@Composable
fun ProductSummaryCard(
    product: ProductUi,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        /* ... */
    }
}
```

Не делать:

```kotlin
fun ProductSummaryCard(product: ProductUi) {
    Card(modifier = Modifier.fillMaxWidth()) { /* ... */ }
}
```

Такой компонент нельзя нормально использовать в parent constraints, preview, тестах, списках и wide containers.

### 6.2. Порядок modifiers

Порядок имеет semantic meaning. Например:

```kotlin
Modifier
    .clickable(onClick = onClick)
    .padding(16.dp)
```

делает clickable область больше, включая padding. А:

```kotlin
Modifier
    .padding(16.dp)
    .clickable(onClick = onClick)
```

оставляет внешний padding не кликабельным.

Практическое правило:

1. Parent-provided `modifier`.
2. Size/constraint modifiers: `fillMaxWidth`, `widthIn`, `heightIn`.
3. Interaction modifiers, если touch target должен включать внешний размер.
4. Background/clip/border.
5. Internal padding.
6. Semantics/test tags там, где они описывают итоговый node.

Это не абсолютный закон, но порядок должен быть осознанным.

### 6.3. `padding` не является margin

В Compose нет отдельного margin modifier. Внешний отступ - это `padding`, поставленный parent'ом вокруг child, или padding до background/clickable. Внутренний отступ - padding после background/clip/clickable.

Для карточек:

```kotlin
LazyColumn(
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    items(products, key = { it.id }) { product ->
        ProductCard(
            product = product,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
```

Карточка не должна сама знать, какой между ней и соседней карточкой внешний spacing. Это ответственность списка.

### 6.4. Scoped modifiers

`weight`, `align` и похожие scoped modifiers работают только в нужном scope и на прямых children. Не передавать `Modifier.weight(1f)` через несколько уровней, если child уже не прямой ребёнок `Row`/`Column`.

Лучше:

```kotlin
Row {
    ProductInfo(
        product = product,
        modifier = Modifier.weight(1f)
    )
    QuantitySelector(...)
}
```

а не создавать `val sharedModifier = Modifier.weight(1f)` и передавать его в произвольный helper.

### 6.5. Fixed size vs constraints

Фиксированные размеры допустимы для:

- icon size;
- divider thickness;
- minimum touch target;
- corner radius;
- spacing;
- known control height.

Опасны для:

- карточек с текстом;
- строк с кнопками и длинными label;
- screen containers;
- списков;
- bottom action area с несколькими строками;
- элементов, где текст может вырасти при `fontScale = 1.5f`.

Предпочитать:

```kotlin
Modifier
    .fillMaxWidth()
    .heightIn(min = 48.dp)
```

вместо:

```kotlin
Modifier
    .fillMaxWidth()
    .height(48.dp)
```

Если высота является частью дизайна, всё равно проверить large font preview: fixed height часто режет текст.

## 7. Adaptive layout для этого проекта

### 7.1. Phone-first, не tablet-first

Текущий проектный стандарт уже зафиксировал phone-first. Значит, реализация должна сначала быть устойчивой на:

- `320dp` width;
- `360dp`;
- `393dp`/`411dp`;
- `360dp` + `fontScale = 1.5f`;
- `600dp` no-break.

Wide support сейчас означает "не ломаться", а не делать новый tablet UX.

### 7.2. Ограничение ширины вместо механической сетки

Для форм, профиля, настроек, main trip flow, confirmation screens:

```kotlin
Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 520.dp)
            .padding(horizontal = 16.dp)
    ) {
        /* content */
    }
}
```

Это лучше, чем растягивать телефонную карточку на `720dp`.

### 7.3. Adaptive grid только для независимых элементов

`LazyVerticalGrid(GridCells.Adaptive(...))` уместен для каталога товаров или упаковок, если элементы независимые и порядок чтения не ломается. Не использовать grid для связанных сценариев только ради "выглядит шире":

- история текущей смены;
- active trip + history;
- step-by-step формы;
- confirmation summary;
- операции с nested details.

### 7.4. Window size classes

Если появится полноценная tablet/wide feature, решение должно быть на уровне screen/shell:

- compact: один вертикальный flow;
- medium: ограниченная ширина или простая supporting area;
- expanded: возможно list-detail/supporting pane/navigation rail.

Не смешивать глобальную adaptive navigation с локальной правкой отдельной карточки.

## 8. Insets, Scaffold, bottom bars и keyboard

### 8.1. Scaffold padding

Если screen использует `Scaffold`, `innerPadding` должен быть применён к content container:

```kotlin
Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    bottomBar = { BottomBar(...) }
) { innerPadding ->
    ExampleContent(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    )
}
```

Не игнорировать `innerPadding`: иначе content может уйти под top/bottom bar.

### 8.2. Bottom action area

Для экранов продажи/корзины/подтверждения:

- главный content должен скроллиться;
- bottom action не должен закрывать последнюю строку списка;
- `LazyColumn.contentPadding` должен учитывать bottom action height, если action не часть list;
- snackbar не должен перекрывать критичную кнопку без возможности действия;
- при `fontScale = 1.5f` action area должна расти через `heightIn`, а не резать текст.

### 8.3. IME

Для экранов с вводом:

- проверить, что keyboard не закрывает active field;
- использовать `imePadding()`/insets там, где нужно;
- не делать fixed-height root без scroll;
- убедиться, что кнопка подтверждения доступна с открытой клавиатурой.

Если экран имеет много полей, лучше один vertical scroll container, чем попытка удержать всё на fixed screen.

## 9. Text, localization и font scale

### 9.1. Текст должен быть ресурсом

Пользовательский текст - через `stringResource`, а не hardcode. Исключения: debug preview data, internal-only logs, test fake strings.

Для preview длинных строк полезно явно проверять:

- русский длиннее английского;
- английский может иметь длинные слова;
- число + валюта;
- ФИО проводника;
- станция отправления/назначения;
- ошибка backend/domain.

### 9.2. Не решать layout через `maxLines = 1`

`maxLines = 1` + `overflow = Ellipsis` допустимы, если это осознанный UX-контракт: например короткий заголовок карточки, где детали доступны ниже/в другом месте.

Не использовать ellipsis для:

- критичной ошибки;
- суммы;
- кнопки действия;
- выбранной станции, если пользователь должен проверить значение;
- статуса операции.

Для рабочих экранов лучше перенести текст на вторую строку, чем скрыть важную информацию.

### 9.3. Button text

Кнопка с длинным текстом должна:

- иметь `heightIn(min = ...)`, а не fixed height;
- допускать перенос, если это primary action с длинным label;
- не быть единственным носителем критичной информации;
- иметь достаточно широкий parent или fallback layout.

Если две кнопки в `Row` не помещаются на `320dp`/large font, перевести их в vertical stack через `Column` или adaptive branch.

### 9.4. Font scale

Проверять минимум `fontScale = 1.5f` для screen content. Типичные баги:

- fixed-height card режет текст;
- icon button row перекрывает title;
- bottom payment area растёт и закрывает список;
- text field label/placeholder ломает высоту;
- snackbar перекрывает bottom button;
- табличные ячейки становятся нечитаемыми.

Для таблиц и статистики font scale может требовать отдельной стратегии: horizontal scroll, compact labels, details drill-down или explicit "не оптимально, но доступно".

## 10. Accessibility и semantics

### 10.1. Начинать с стандартных компонентов

Material `Button`, `IconButton`, `TextField`, `Checkbox`, `Switch`, `Snackbar`, `Dialog` уже несут много accessibility behavior. Custom clickable `Box`/`Row` требует больше ручной работы.

Если элемент выглядит как кнопка, лучше использовать `Button`/`IconButton` или явно добавить role/semantics.

### 10.2. Icon-only actions

Icon-only кнопка должна иметь понятный `contentDescription`, если действие не продублировано текстом.

```kotlin
IconButton(onClick = onBack) {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = stringResource(R.string.back)
    )
}
```

Decorative icon:

```kotlin
Icon(
    imageVector = Icons.Default.Info,
    contentDescription = null
)
```

### 10.3. Touch target

Не уменьшать interactive target ради визуальной плотности. Для рабочих экранов маленькие цели особенно опасны: устройство может использоваться в движении, одной рукой, на небольшом телефоне.

Если визуальный icon маленький, clickable area всё равно должна быть достаточной:

```kotlin
IconButton(
    modifier = Modifier.size(48.dp),
    onClick = onClick
) {
    Icon(...)
}
```

### 10.4. Semantics для custom components

Custom component должен давать accessibility services смысл:

- role button/switch/tab, если это custom interactive;
- state description для selected/disabled/expanded;
- merged semantics для карточки, если внутренние элементы вместе описывают одну сущность;
- test tag только как test helper, не вместо semantics.

Не добавлять semantics механически. Сначала понять, как пользователь с TalkBack будет проходить экран: по строкам, по карточкам, по actions.

## 11. Performance и recomposition

### 11.1. Сначала correctness, потом micro-optimization

Большинство Compose performance проблем в обычном приложении возникают не из-за "слишком много composable-функций", а из-за:

- unstable или часто пересоздаваемых параметров;
- expensive calculations прямо в composition;
- отсутствия keys в lazy lists;
- чтения быстро меняющегося state слишком высоко;
- heavy image/content work в item без кеширования;
- side effects, которые перезапускаются из-за неправильных keys.

### 11.2. Избегать expensive work в composition

Плохо:

```kotlin
val filtered = products.filter { it.name.contains(query, ignoreCase = true) }
```

если список большой и recomposition частая.

Лучше:

- фильтровать во `ViewModel`;
- использовать `remember(products, query) { ... }`, если это чистая UI-операция;
- использовать `derivedStateOf`, если значение зависит от быстро меняющегося state и должно обновляться реже.

### 11.3. Stable parameters

Передавать вниз immutable data:

- `data class` с `val`;
- immutable collections;
- primitive values;
- stable callbacks.

Не передавать mutable list/map, если UI должен реагировать на изменения.

Если компонент получает много unrelated параметров и часто recomposes, это сигнал к UI model:

```kotlin
data class ProductCardUi(
    val id: Long,
    val title: String,
    val priceText: String,
    val quantityText: String,
    val isEnabled: Boolean
)
```

### 11.4. Не читать state выше, чем нужно

Если быстро меняется только scroll offset, не читать его в root экрана, который содержит всё. Передать state/lambda ниже или использовать lambda modifier.

Плохо:

```kotlin
val isScrolled = listState.firstVisibleItemIndex > 0
Scaffold(topBar = { TopBar(elevated = isScrolled) }) { /* entire screen */ }
```

Может быть нормально для небольшого экрана, но для сложного root лучше локализовать чтение.

### 11.5. Lambda callbacks

Обычно не нужно оборачивать каждый callback в `remember`. Но не создавать тяжёлые объекты и коллекции в параметрах item:

```kotlin
items(products, key = { it.id }) { product ->
    ProductItem(
        product = product,
        onClick = { onProductClick(product.id) }
    )
}
```

Такой callback нормален. Оптимизировать стоит только при измеренной проблеме или очень горячем списке.

## 12. Images

Для product UI:

- размеры image контейнера задавать constraints, а не полагаться на intrinsic bitmap;
- иметь placeholder/error state;
- не блокировать layout загрузкой изображения;
- не пересоздавать image request без необходимости;
- content scale выбирать по смыслу: `Crop` для thumbnails, `Fit` для полного объекта, если важно увидеть весь товар.

Если картинка необязательна для действия, UI должен оставаться рабочим при ошибке загрузки.

## 13. Dialogs, bottom sheets, snackbars

### 13.1. Dialog state

Dialog/bottom sheet, отражающий бизнес-событие, должен управляться screen state:

- sale result;
- finish trip confirmation;
- retry send confirmation;
- logout confirmation.

Local UI-only sheet может быть локальным state, но callback подтверждения всё равно уходит наружу.

### 13.2. One-shot events

Snackbar/navigation one-shot event не должен быть просто nullable string, который повторится при configuration change без контроля. Лучше event object с id и явным consume:

```kotlin
data class UiMessage(
    val id: Long,
    val messageRes: Int
)
```

Screen:

```kotlin
LaunchedEffect(uiMessage?.id) {
    val message = uiMessage ?: return@LaunchedEffect
    snackbarHostState.showSnackbar(context.getString(message.messageRes))
    onMessageShown(message.id)
}
```

Общий `UiMessage`/`OneShotEvent` можно рассматривать как best practice, если в проекте много одинаковых event-сценариев и хочется единый контракт. Для текущего кода ChaikaKotlin локальные event id модели во `ViewModel` остаются приемлемыми, если они дают те же свойства: уникальная identity события, controlled consume после показа и отсутствие повторного snackbar/navigation после recomposition/configuration change.

### 13.3. Snackbar priority

Если на экране может появиться несколько snackbar sources, нужен порядок:

1. Ошибка, блокирующая действие.
2. Stock limit / sold out.
3. Informational success.

Иначе пользователь может получить устаревшее сообщение после нового действия.

## 14. Навигация

### 14.1. Navigation belongs to app/screen layer

Reusable component не должен знать route. Компонент сообщает событие:

```kotlin
ProductItem(
    product = product,
    onClick = { onProductClick(product.id) }
)
```

Screen/NavGraph решает:

```kotlin
onProductClick = { productId ->
    navController.navigate(ProductDetailRoute(productId))
}
```

### 14.2. Avoid duplicated navigation effects

Navigation из `LaunchedEffect` должна быть keyed по state transition/event id, иначе можно получить повторную навигацию после recomposition.

Хорошая модель:

- ViewModel эмитит navigation event;
- screen wrapper делает navigate;
- screen wrapper сообщает `onNavigationHandled(event.id)`.

### 14.3. Parent graph scoped ViewModels

В `NavGraph.kt` уже используется parentEntry для shared flow между product/trip routes. Это допустимо, но route screen должен ясно показывать, почему ViewModel scoped к parent graph. Если shared ViewModel начинает обслуживать несвязанные сценарии, лучше выделить отдельный state holder/use case.

## 15. Testing strategy

### 15.1. Preview не заменяет tests

Preview ловит layout regressions глазами. Он не проверяет:

- navigation;
- lifecycle;
- real Hilt graph;
- Paging source behavior;
- snackbar ordering;
- IME;
- TalkBack traversal;
- device-specific insets.

### 15.2. Что тестировать Compose UI tests

Писать UI test, когда:

- есть критичный workflow: продажа, подтверждение, выбор поездки;
- есть state branching: loading/empty/error/content;
- есть accessibility/test semantics;
- была regression по видимости текста/кнопки;
- нужно проверить, что callback вызывается с правильным id.

Для чистых компонентов тестировать через `createComposeRule()` и fake state. Для whole app flows - `createAndroidComposeRule<MainActivity>()` или existing E2E infrastructure.

### 15.3. Семантика важнее testTag

`testTag` удобен, но тесты через visible text/role/content description ближе к пользовательскому поведению. Для динамических списков допустимы tags, если текст не уникален.

### 15.4. Минимальный test matrix для сложного screen

1. Loading state visible.
2. Empty state visible.
3. Error state visible + retry click.
4. Content state visible.
5. Primary action disabled/enabled.
6. Long text does not remove primary action from semantics tree.
7. Snackbar/dialog appears for event and can be dismissed/consumed.

## 16. Implementation checklist для нового экрана

Перед coding:

1. Назвать screen wrapper: `FeatureScreen`/`FeatureView`.
2. Назвать previewable content: `FeatureContent`.
3. Определить `FeatureUiState`.
4. Разделить business state и UI element state.
5. Определить callbacks: `onRetry`, `onBack`, `onSubmit`, `onItemClick`.
6. Решить, есть ли Paging.
7. Решить, есть ли one-shot events.
8. Решить, нужен ли `Scaffold`.
9. Решить, нужен ли max content width на `600dp+`.
10. Определить preview states.

Во время coding:

1. Сначала screen wrapper без большой разметки.
2. Затем content с pure parameters.
3. Затем private subcomposables.
4. Затем reusable components только если реально переиспользуются.
5. Добавить stable keys в lazy containers.
6. Добавить `modifier` на public components.
7. Убедиться, что `innerPadding` применён.
8. Проверить side effects keys.
9. Проверить large font и narrow preview.
10. Проверить empty/error/loading states.

Перед merge:

1. `./gradlew ktlintCheck`
2. `./gradlew compileDebugKotlin`
3. Preview: `320dp`, `360dp + fontScale 1.5`, `393dp`.
4. Device smoke для изменённого workflow.
5. Если есть Paging - проверить append loading/error.
6. Если есть snackbar/dialog - проверить повторное событие и rotation/back.
7. Если есть ввод - проверить keyboard/IME.

## 17. Refactor checklist для старого экрана

Порядок безопасного refactor:

1. Найти screen boundary: где ViewModel, navigation, Flow/Paging, side effects.
2. Выделить `*Content`, если screen wrapper смешан с layout.
3. Передать state/callbacks вниз.
4. Убрать ViewModel из components.
5. Сделать helper composables `private`.
6. Добавить root `modifier`.
7. Добавить stable keys в lazy containers.
8. Разделить loading/empty/error/content.
9. Проверить `Scaffold` padding и bottom action.
10. Убрать только fixed sizes, которые реально ломают constraints.
11. Добавить/обновить previews.
12. Прогнать compile + ktlint.

Не делать в одном проходе:

- архитектурный split;
- визуальный redesign;
- tablet UX;
- смену domain models;
- замену всех fixed `dp`;
- перенос всего в новые generic components.

Для ChaikaKotlin лучше маленький refactor с визуальной плотностью как раньше, чем большой "правильный" rewrite.

## 18. Частые smells и что с ними делать

| Smell | Почему плохо | Что делать |
| --- | --- | --- |
| Component принимает `ViewModel` | Нельзя preview/test/reuse без runtime graph | Hoist state/callbacks в screen |
| Component принимает `NavController` | UI-компонент знает маршруты | Передать `onClick`/`onBack` |
| `LaunchedEffect(Unit)` вызывает действие, зависящее от id | Не перезапустится при смене id или перезапустится не там | Key по id/event |
| `LazyColumn` без `key` | State привязан к позиции | Добавить stable key |
| `height(48.dp)` на кнопке с текстом | Large font может обрезать текст | `heightIn(min = 48.dp)` |
| `Column.verticalScroll` вокруг большого списка | Все элементы compose сразу, проблемы scroll | `LazyColumn` |
| `stringResource` внутри ViewModel | ViewModel зависит от Android resources | Передавать res id/UI message или маппить в UI |
| Nullable string как one-shot event | Повтор/потеря события | Event id + consume |
| UI state хранит `MutableList` | Recomposition может не сработать | Immutable list + new state |
| Fixed width card | Wide/narrow constraints ломаются | `fillMaxWidth().widthIn(max = ...)` |
| Preview только happy path | Edge states ломаются незаметно | Loading/empty/error/long text/font scale |
| Error и empty выглядят одинаково | Пользователь не понимает действие | Явные тексты и retry, где уместно |
| Кнопки в `Row` не помещаются | 320dp/large font ломается | Adaptive vertical stack |
| Snackbar перекрывает primary action | Рабочий сценарий блокируется | Scaffold snackbar host + content padding/order |

## 19. Минимальные проектные templates

### 19.1. Screen + Content

```kotlin
@Composable
fun FeatureScreen(
    viewModel: FeatureViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    FeatureContent(
        state = state,
        onBack = onBack,
        onRetry = viewModel::retry,
        onSubmit = viewModel::submit
    )
}

@Composable
private fun FeatureContent(
    state: FeatureUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp)
        ) {
            when {
                state.isLoading -> LoadingScreen()
                state.errorMessageRes != null -> ErrorScreen(onRetry = onRetry)
                state.items.isEmpty() -> EmptyState()
                else -> FeatureList(items = state.items)
            }
        }
    }
}
```

### 19.2. Lazy list

```kotlin
@Composable
private fun ProductList(
    products: List<ProductUi>,
    onProductClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = products,
            key = { product -> product.id }
        ) { product ->
            ProductItem(
                product = product,
                onClick = { onProductClick(product.id) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
```

### 19.3. Preview

```kotlin
@PhoneScalablePreviews
@Composable
private fun FeatureContentPreview() {
    ChaikaTheme {
        FeatureContent(
            state = FeatureUiState(
                items = listOf(
                    ProductUi(
                        id = 1,
                        title = "Очень длинное название товара для проверки переноса",
                        priceText = "1250 ₽"
                    )
                )
            ),
            onBack = {},
            onRetry = {},
            onSubmit = {}
        )
    }
}
```

## 20. Источники

Официальные источники, на которых основан research:

- State and Jetpack Compose: https://developer.android.com/develop/ui/compose/state
- State hoisting: https://developer.android.com/develop/ui/compose/state-hoisting
- Side-effects in Compose: https://developer.android.com/develop/ui/compose/side-effects
- Compose modifiers: https://developer.android.com/develop/ui/compose/modifiers
- Lazy lists and grids: https://developer.android.com/develop/ui/compose/lists
- Compose performance: https://developer.android.com/develop/ui/compose/performance
- Compose accessibility: https://developer.android.com/develop/ui/compose/accessibility
- Compose testing: https://developer.android.com/develop/ui/compose/testing
- Compose previews: https://developer.android.com/develop/ui/compose/tooling/previews
- Window size classes: https://developer.android.com/develop/ui/compose/layouts/adaptive/use-window-size-classes
- Support different display sizes: https://developer.android.com/develop/ui/compose/layouts/adaptive/support-different-display-sizes
- Compose API guidelines: https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-api-guidelines.md
- Kotlin coding conventions: https://kotlinlang.org/docs/coding-conventions.html

## 21. Вопросы для уточнения

1. Где проводим границу между domain model и UI model для компонентов: вводим UI model для каждого сложного компонента или только при явном smell?
