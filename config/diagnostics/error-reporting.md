# Error reporting через GlitchTip

## Назначение

Мобильный клиент отправляет crash/error reports в self-hosted GlitchTip через Sentry Android SDK. Sentry SDK используется только как транспорт до нашего GlitchTip endpoint.

## Где разрешён Sentry API

Прямые импорты `io.sentry.*` разрешены только в пакете `com.chaikasoft.app.diagnostics`. Feature, ViewModel, usecase и repository код должны зависеть только от `ErrorReporter` и диагностических моделей приложения.

## Окружения

- `debug`: отправка выключена.
- `stage`: отправка выключена до появления рабочего stage-контура.
- `release`: отправка включается только если при сборке передан непустой `REL_GLITCHTIP_DSN`.

DSN нельзя хранить в репозитории. Значение должно приходить из локального окружения или CI secrets.

## Что отправляем

Автоматически SDK может отправлять fatal crashes и ANR после программной инициализации в `release`.

Non-fatal события отправляются только явно через `ErrorReporter` для неожиданных дефектов приложения:

- `AppError.Unknown`;
- `AppError.Serialization`;
- ошибки восстановления encrypted storage;
- локальные сбои post-auth startup;
- ошибки DTO/domain mapping.

## Что запрещено отправлять

В диагностические события нельзя добавлять:

- ФИО;
- телефоны, email, логины;
- табельные номера;
- access/refresh tokens;
- тела HTTP-запросов и ответов;
- query-параметры URL;
- номера билетов, заказов, рейсов, смен, отчётов;
- координаты;
- свободный пользовательский текст;
- полный JSON бизнес-объектов.

Особенно важно: `AppError.Http.body` никогда не передаётся в диагностику.

## Release mapping

`release` сборка minified. R8/ProGuard сохраняет `SourceFile,LineNumberTable`, а `mapping.txt` нужно хранить как защищённый CI/local artifact. Автоматическую загрузку mapping/source context наружу не включать, пока DevOps не подтвердит согласованный способ загрузки в self-hosted GlitchTip.

## Ручная проверка перед пилотом

После получения `REL_GLITCHTIP_DSN`:

1. Собрать release APK с DSN.
2. На временной ветке добавить test non-fatal и test crash.
3. Убедиться, что события появились в GlitchTip.
4. Проверить payload вручную: нет PII, tokens, HTTP body, query params и бизнес-идентификаторов.
5. Удалить тестовый trigger перед merge в основную ветку.
