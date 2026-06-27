# Compose static analysis proposal

**Статус:** предложение для обсуждения с командой  
**Актуальность:** 27 июня 2026 года  
**Область:** возможные Detekt/ktlint/custom lint проверки для Compose UI  
**Связанные документы:** `docs/compose-style.md`, `docs/compose-implementation-research.md`

Этот план не вводит новые правила в сборку. Его цель - подготовить обсуждение: какие Compose smells можно ловить автоматически, какие лучше оставить code review чеклистом, и в каком порядке безопасно включать enforcement.

## 1. Цели

1. Автоматически ловить повторяющиеся Compose-ошибки, которые трудно удерживать в review вручную.
2. Не блокировать разработку шумными правилами, пока кодовая база не приведена к единому baseline.
3. Начать с warning/report-only режима.
4. Включать blocking enforcement только после пилотной проверки и согласования с командой.

## 2. Кандидаты на правила

| Правило | Что ловим | Риск false positive | Приоритет |
| --- | --- | --- | --- |
| Public composable without `modifier` | Публичный `@Composable` в `ui/components` без `modifier: Modifier = Modifier` | Средний: screen-level composables могут быть исключением | Высокий |
| Modifier not applied to root | `modifier` есть, но не используется или применяется не к root node | Высокий: статически сложно доказать root | Средний |
| ViewModel in `ui/components` | Импорт/параметр `ViewModel`, `hiltViewModel`, `collectAsStateWithLifecycle` в reusable components | Низкий | Высокий |
| NavController in reusable components | `NavController` или route navigation внутри `ui/components` | Низкий | Высокий |
| Flow/Paging collection in components | `collectAsState*`, `collectAsLazyPagingItems` ниже screen layer | Средний: есть legacy exceptions | Средний |
| Lazy items without stable key | `items(...)` без `key` в `LazyColumn`/`LazyRow`/grid | Средний: static detection depends on overload | Средний |
| Hard fixed card/list sizes | `.height(...)`, `.width(...)`, `.requiredSize(...)` в reusable components | Высокий: фиксированный размер иногда valid | Низкий/report-only |
| `LaunchedEffect(Unit)` audit | `LaunchedEffect(Unit)` и `LaunchedEffect(true)` | Высокий: есть valid cases | Низкий/report-only |
| Mutable collection state | `mutableListOf`/`mutableMapOf` внутри `remember`/state | Средний | Средний |

## 3. Recommended route

### Phase 1: Documentation-only checklist

Сделать короткий `docs/compose-review-checklist.md` для ручного review. Включить туда только high-signal пункты:

- reusable component не принимает `ViewModel`;
- reusable component не принимает `NavController`;
- public reusable component имеет root `modifier`;
- lazy list имеет stable key, если у item есть id;
- screen wrapper отделён от previewable `*Content`;
- side effects имеют осмысленные keys;
- loading/empty/error/content states явно различимы.

### Phase 2: Report-only script

Сделать простой Gradle task или standalone script, который печатает кандидатов без падения сборки.

Минимальные checks:

1. `hiltViewModel`, `ViewModel`, `collectAsStateWithLifecycle`, `collectAsLazyPagingItems` в `app/src/main/java/.../ui/components`.
2. `NavController` в `ui/components`.
3. Public `@Composable fun` в `ui/components` без `modifier`.
4. `LaunchedEffect(Unit|true)` список для ручной проверки.

Цель phase 2 - собрать baseline и понять шум.

### Phase 3: Baseline и исключения

Для каждого правила определить:

- package scope;
- allowlist legacy files;
- wording для warning;
- owner для исправления;
- критерий перевода в blocking.

Исключения хранить явно, не в виде неформального знания. Например:

```text
compose_static_analysis_baseline.txt
ui/components/example/LegacyComponent.kt: documented legacy exception and cleanup owner
```

### Phase 4: Blocking rules only for low-noise smells

Первыми можно блокировать:

1. `ViewModel`/`hiltViewModel` в новых `ui/components`.
2. `NavController` в новых `ui/components`.

Остальное оставить warning/report-only до ручной очистки:

- public composable without modifier;
- lazy items without key;
- fixed sizes;
- `LaunchedEffect(Unit)`.

## 4. Tooling options

### Option A: Detekt custom rules

Плюсы:

- Kotlin AST/PSI лучше подходит для composable signatures и imports;
- можно интегрировать в Gradle;
- удобно делать baseline.

Минусы:

- дороже старт;
- нужно поддерживать custom rule module;
- нужно аккуратно настроить false positives.

### Option B: ktlint custom rules

Плюсы:

- хорошо ложится на style-level проверки;
- уже привычен как форматирующий/style инструмент.

Минусы:

- хуже для semantic checks;
- не лучший выбор для imports + composable boundary rules.

### Option C: lightweight script

Плюсы:

- самый быстрый старт;
- можно собрать baseline без инфраструктуры;
- достаточно для `ViewModel`/`NavController`/obvious imports.

Минусы:

- regex-based checks шумят;
- сложно корректно определить public composable без modifier;
- не стоит делать blocking enforcement без AST.

Рекомендация: начать с Option C в report-only режиме, затем переносить low-noise правила в Detekt custom rules, если команда согласует enforcement.

## 5. Proposed first report-only checks

1. **Components must not depend on ViewModel**
   - Scope: `app/src/main/java/com/chaikasoft/app/ui/components/**/*.kt`
   - Match: `ViewModel`, `hiltViewModel`, `collectAsStateWithLifecycle`, `collectAsLazyPagingItems`
   - Severity: warning

2. **Components must not navigate**
   - Scope: `ui/components/**/*.kt`
   - Match: `NavController`, `.navigate(`
   - Severity: warning

3. **Public reusable composables should expose modifier**
   - Scope: `ui/components/**/*.kt`
   - Match: public `@Composable fun`
   - Exempt: private functions, previews, functions returning non-UI content if any
   - Severity: warning

4. **Audit suspicious effects**
   - Scope: `ui/**/*.kt`
   - Match: `LaunchedEffect(Unit)`, `LaunchedEffect(true)`
   - Severity: info

5. **Audit hard fixed sizes**
   - Scope: `ui/**/*.kt`
   - Match: `.requiredSize`, `.requiredWidth`, `.requiredHeight`, `.height(`, `.width(`
   - Severity: info
   - Note: this rule must not be blocking because many fixed sizes are valid design tokens.

## 6. Acceptance criteria before enforcement

Перед переводом любого правила в blocking:

1. Есть baseline по текущему коду.
2. False positives просмотрены вручную.
3. Правило описано в `compose-style.md` или review checklist.
4. Есть понятный способ локально запустить проверку.
5. Есть allowlist для legacy exceptions.
6. Команда согласовала, что новые нарушения блокируют PR.

## 7. Suggested team proposal

Предложение команде:

1. Утвердить research как справочник, не как обязательный стандарт.
2. Сделать короткий Compose review checklist.
3. Добавить report-only scan для high-signal boundary smells.
4. Через 1-2 итерации посмотреть baseline и шум.
5. Первыми включить blocking только для новых `ViewModel`/`NavController` зависимостей в `ui/components`.
