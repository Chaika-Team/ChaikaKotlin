# Compose review checklist

**Статус:** короткий чеклист для code review  
**Актуальность:** 27 июня 2026 года  
**Область:** Compose UI changes в `ui/screens` и `ui/components`  
**Подробности:** `docs/compose-style.md`, `docs/compose-implementation-research.md`

Этот чеклист нужен для review, а не для полного обучения Compose. Если пункт требует пояснения, смотреть implementation research.

## 1. Boundary

- Screen-level composable владеет `ViewModel`, navigation, Flow/Paging collection и side effects.
- Reusable component в `ui/components` принимает готовые данные и callbacks.
- В `ui/components` нет `ViewModel`, `hiltViewModel`, `NavController`, route strings и repository/use case calls.
- Shared top/bottom bars не принимают navigation controller; наружу уходят callbacks.

## 2. State

- `StateFlow`/`Flow` в Android UI собирается через `collectAsStateWithLifecycle()`.
- Business state хранится во `ViewModel`, а не через `remember`.
- Локальный UI state через `remember`/`rememberSaveable` используется только для UI-механики.
- Mutable collections не используются как Compose state без observable wrapper.
- One-shot snackbar/navigation events имеют identity и controlled consume.

## 3. Components API

- Публичный reusable composable имеет `modifier: Modifier = Modifier`.
- `modifier` применён к root node или явно объяснено, почему нет.
- Внутренние helper composables private.
- Callback имена описывают событие: `onRetry`, `onDismiss`, `onItemClick`, `onQuantityIncrease`.
- Компонент не форматирует бизнес-решение, если это должно быть во ViewModel/mapper.

## 4. Layout

- Screen content применяет `Scaffold` `innerPadding`, если используется `Scaffold`.
- Основной vertical список сделан через `LazyColumn`/lazy container, а не через `Column.verticalScroll` для большого списка.
- Lazy items имеют stable key, если у item есть id.
- Нет fixed width/height на карточках и action areas, которые должны переживать narrow width и large font.
- Для телефонных экранов на `600dp+` используется max content width/no-break подход, а не tablet redesign.
- Bottom actions не перекрывают последний элемент списка.
- Экран с вводом остаётся рабочим с открытой keyboard/IME.

## 5. Text и accessibility

- Пользовательский текст берётся из resources.
- Длинные русские/английские строки не обрезают критичные действия.
- `maxLines = 1`/ellipsis используется только там, где потеря полного текста допустима.
- Icon-only action имеет `contentDescription`; decorative icon использует `null`.
- Custom clickable elements имеют понятный role/semantics или заменены Material component.
- Touch target не уменьшен ради визуальной плотности.

## 6. Side effects

- Snackbar/navigation/suspend work не вызываются напрямую из composable body.
- `LaunchedEffect` имеет осмысленный key: id события, route argument, selected id.
- `LaunchedEffect(Unit)` используется только для настоящего one-time screen-entry действия.
- Долгоживущий effect с callback использует `rememberUpdatedState`, если callback не должен перезапускать effect.
- Business operations запускаются через ViewModel callback, а не через `rememberCoroutineScope` внутри reusable component.

## 7. Paging

- Initial loading, initial error, empty state и content state различимы.
- `append Loading` и `append Error` обработаны отдельно, если экран поддерживает pagination.
- Retry вызывает `LazyPagingItems.retry()` или screen callback, который реально повторяет загрузку.
- Empty state не показывается во время initial refresh.

## 8. Preview и verification

- Нетривиальный screen имеет previewable `*Content`, а wrapper с Hilt/navigation не preview-ится напрямую.
- Для новых screen-level content используется `@PhoneScalablePreviews`, если layout нетривиальный.
- Preview покрывает не только happy path, но и важные edge states: loading, empty, error, long text, disabled/selected.
- Перед merge пройдены `ktlintCheck` и `compileDebugKotlin`.
- Для изменённого workflow выполнен ручной device smoke, если изменение влияет на пользовательское поведение.

## 9. Red flags

- `ViewModel` в `ui/components`.
- `NavController` в reusable component.
- `LazyColumn` без keys для entity list.
- Snackbar event как plain nullable string без consume.
- `.height(...)` на кнопке/карточке с пользовательским текстом.
- Большой screen wrapper, где layout, state collection и side effects смешаны в одном блоке.
- Механическая замена всех `.dp` без конкретного layout bug.
