# Конфигурация окружений ChaikaKotlin

## Текущий подход

### Описание

Проект использует переменные окружения операционной системы для конфигурации сборки. Значения передаются через `System.getenv()` на этапе компиляции Gradle и записываются в `BuildConfig`.

### Используемые переменные окружения

| Переменная | Описание | Используется в |
|------------|----------|----------------|
| `REL_ZITADEL_TOKEN` | Client ID production OAuth-клиента Zitadel | debug, release |
| `REL_CHAIKA_SOFT_URL` | Базовый URL production API сервера | debug, release |
| `REL_ZITADEL_URL` | URL production сервера авторизации Zitadel | debug, release |
| `STAGE_ZITADEL_TOKEN` | Client ID stage OAuth-клиента Zitadel | stage |
| `STAGE_CHAIKA_SOFT_URL` | Базовый URL stage API сервера | stage |
| `STAGE_ZITADEL_URL` | URL stage сервера авторизации Zitadel | stage |

### Разделение Stage и Release

`stage` и `release` собирают одно приложение с разными внешними окружениями:

- `stage` использует тестовые API и OAuth-настройки. Такая сборка нужна для проверки интеграций, авторизации и сетевых сценариев без обращения к production-данным.
- `release` использует production API и production OAuth-клиент. Это конфигурация для финальной пользовательской сборки.
- Текущий `stage` build type добавляет `applicationIdSuffix = ".staging"` и `versionNameSuffix = "-STAGING"`, поэтому stage-сборку можно установить рядом с release-сборкой и явно отличать ее по версии.

Плюсы такого разделения:

- меньше риск случайно проверить разработческую гипотезу на production-сервере;
- проще воспроизводить баги интеграций на стабильном тестовом окружении;
- настройки сборки становятся явными: по имени переменной видно, к какому окружению она относится;
- CI и локальная разработка могут использовать разные источники значений, не меняя код приложения.

### Настройка локального окружения

Android Studio не всегда подхватывает переменные окружения, установленные в ОС. Это зависит от способа запуска IDE и операционной системы.

#### Windows

**PowerShell**:

```powershell
# Временно (для текущей сессии)
$env:REL_ZITADEL_TOKEN = "your_client_id"
$env:REL_CHAIKA_SOFT_URL = "https://api.example.com"
$env:REL_ZITADEL_URL = "https://auth.example.com"
$env:STAGE_ZITADEL_TOKEN = "your_stage_client_id"
$env:STAGE_CHAIKA_SOFT_URL = "https://stage-api.example.com"
$env:STAGE_ZITADEL_URL = "https://stage-auth.example.com"

# Постоянно (для пользователя)
[Environment]::SetEnvironmentVariable("REL_ZITADEL_TOKEN", "your_client_id", "User")
[Environment]::SetEnvironmentVariable("REL_CHAIKA_SOFT_URL", "https://api.example.com", "User")
[Environment]::SetEnvironmentVariable("REL_ZITADEL_URL", "https://auth.example.com", "User")
[Environment]::SetEnvironmentVariable("STAGE_ZITADEL_TOKEN", "your_stage_client_id", "User")
[Environment]::SetEnvironmentVariable("STAGE_CHAIKA_SOFT_URL", "https://stage-api.example.com", "User")
[Environment]::SetEnvironmentVariable("STAGE_ZITADEL_URL", "https://stage-auth.example.com", "User")
```

**CMD**:

```cmd
:: Временно
set REL_ZITADEL_TOKEN=your_client_id
set REL_CHAIKA_SOFT_URL=https://api.example.com
set REL_ZITADEL_URL=https://auth.example.com
set STAGE_ZITADEL_TOKEN=your_stage_client_id
set STAGE_CHAIKA_SOFT_URL=https://stage-api.example.com
set STAGE_ZITADEL_URL=https://stage-auth.example.com

:: Постоянно
setx REL_ZITADEL_TOKEN "your_client_id"
setx REL_CHAIKA_SOFT_URL "https://api.example.com"
setx REL_ZITADEL_URL "https://auth.example.com"
setx STAGE_ZITADEL_TOKEN "your_stage_client_id"
setx STAGE_CHAIKA_SOFT_URL "https://stage-api.example.com"
setx STAGE_ZITADEL_URL "https://stage-auth.example.com"
```

> Переменные, установленные через setx или системные настройки, подхватываются Android Studio только после полного перезапуска IDE.

#### Linux / macOS

```bash
# Временно (для текущей сессии)
export REL_ZITADEL_TOKEN="your_client_id"
export REL_CHAIKA_SOFT_URL="https://api.example.com"
export REL_ZITADEL_URL="https://auth.example.com"
export STAGE_ZITADEL_TOKEN="your_stage_client_id"
export STAGE_CHAIKA_SOFT_URL="https://stage-api.example.com"
export STAGE_ZITADEL_URL="https://stage-auth.example.com"

# Постоянно (добавить в ~/.bashrc, ~/.zshrc или ~/.profile)
echo 'export REL_ZITADEL_TOKEN="your_client_id"' >> ~/.bashrc
echo 'export REL_CHAIKA_SOFT_URL="https://api.example.com"' >> ~/.bashrc
echo 'export REL_ZITADEL_URL="https://auth.example.com"' >> ~/.bashrc
echo 'export STAGE_ZITADEL_TOKEN="your_stage_client_id"' >> ~/.bashrc
echo 'export STAGE_CHAIKA_SOFT_URL="https://stage-api.example.com"' >> ~/.bashrc
echo 'export STAGE_ZITADEL_URL="https://stage-auth.example.com"' >> ~/.bashrc
source ~/.bashrc
```

##### Проблема для macOS

Переменные из ~/.bashrc, ~/.zshrc доступны только в терминальных сессиях. GUI-приложения, включая Android Studio, запущенную через Dock или Spotlight, их не видят.

macOS GUI-приложения не наследуют окружение shell. Они запускаются через launchd, который не читает конфигурацию shell.

**Решения**:

Запускать Android Studio из терминала:

```bash
# После установки переменных в ~/.zshrc
source ~/.zshrc
open -a "Android Studio"
```

Файл `~/Library/LaunchAgents/environment.plist`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>Label</key>
    <string>my.environment</string>
    <key>ProgramArguments</key>
    <array>
        <string>sh</string>
        <string>-c</string>
        <string>
            launchctl setenv REL_ZITADEL_TOKEN "your_client_id"
            launchctl setenv REL_CHAIKA_SOFT_URL "https://api.example.com"
            launchctl setenv REL_ZITADEL_URL "https://auth.example.com"
            launchctl setenv STAGE_ZITADEL_TOKEN "your_stage_client_id"
            launchctl setenv STAGE_CHAIKA_SOFT_URL "https://stage-api.example.com"
            launchctl setenv STAGE_ZITADEL_URL "https://stage-auth.example.com"
        </string>
    </array>
    <key>RunAtLoad</key>
    <true/>
</dict>
</plist>
```

#### Для Linux

Зависит от дистрибутива и способа запуска. Обычно работает корректно, если переменные установлены в `~/.profile` или `~/.bashrc` и сессия перезапущена.

### Настройка в Android Studio

1. Открыть **Run/Debug Configurations**
2. Выбрать конфигурацию приложения
3. В разделе **Environment variables** добавить переменные в формате:
   ```
   REL_ZITADEL_TOKEN=your_client_id;REL_CHAIKA_SOFT_URL=https://api.example.com;REL_ZITADEL_URL=https://auth.example.com;STAGE_ZITADEL_TOKEN=your_stage_client_id;STAGE_CHAIKA_SOFT_URL=https://stage-api.example.com;STAGE_ZITADEL_URL=https://stage-auth.example.com
   ```

Альтернативно, создать файл `.idea/workspace.xml` с конфигурацией.

---

## Предложение по улучшению

Предлагается перейти на подход с использованием `.properties`-файлов для явного разделения окружений Stage и Release.

Такой подход оставляет переменные окружения удобным fallback для CI, но убирает зависимость локальной сборки от того, как была запущена Android Studio. Разработчик хранит значения в локальных `.properties`-файлах, а репозиторий содержит только шаблон без секретов.

В `build.gradle.kts` будет что-то вроде:

```kts
import java.util.Properties

fun loadProperties(path: String): Properties {
    val file = rootProject.file(path)
    return Properties().apply {
        if (file.exists()) {
            file.inputStream().use(::load)
        }
    }
}

val stageProperties = loadProperties("config/stage.properties")
val releaseProperties = loadProperties("config/release.properties")

fun config(properties: Properties, key: String): String {
    return properties.getProperty(key)
        ?: System.getenv(key)
        ?: ""
}

buildTypes {
    getByName("debug") {
        isMinifyEnabled = false
        buildConfigField("String", "CLIENT_ID", "\"${config(releaseProperties, "REL_ZITADEL_TOKEN")}\"")
        buildConfigField("String", "CHAIKA_SOFT_URL", "\"${config(releaseProperties, "REL_CHAIKA_SOFT_URL")}\"")
        buildConfigField("String", "ZITADEL_URL", "\"${config(releaseProperties, "REL_ZITADEL_URL")}\"")
    }

    getByName("release") {
        buildConfigField("String", "CLIENT_ID", "\"${config(releaseProperties, "REL_ZITADEL_TOKEN")}\"")
        buildConfigField("String", "CHAIKA_SOFT_URL", "\"${config(releaseProperties, "REL_CHAIKA_SOFT_URL")}\"")
        buildConfigField("String", "ZITADEL_URL", "\"${config(releaseProperties, "REL_ZITADEL_URL")}\"")
    }

    create("stage") {
        applicationIdSuffix = ".staging"
        versionNameSuffix = "-STAGING"
        buildConfigField("String", "CLIENT_ID", "\"${config(stageProperties, "STAGE_ZITADEL_TOKEN")}\"")
        buildConfigField("String", "CHAIKA_SOFT_URL", "\"${config(stageProperties, "STAGE_CHAIKA_SOFT_URL")}\"")
        buildConfigField("String", "ZITADEL_URL", "\"${config(stageProperties, "STAGE_ZITADEL_URL")}\"")
    }
}
```

`config/stage.properties`:

```properties
STAGE_ZITADEL_TOKEN=your_stage_client_id
STAGE_CHAIKA_SOFT_URL=https://stage-api.example.com
STAGE_ZITADEL_URL=https://stage-auth.example.com
```

`config/release.properties`:

```properties
REL_ZITADEL_TOKEN=your_client_id
REL_CHAIKA_SOFT_URL=https://api.example.com
REL_ZITADEL_URL=https://auth.example.com
```

Структура:

```text
project/
├── config/
│   ├── stage.properties          # Stage-конфигурация (в .gitignore)
│   ├── release.properties        # Release-конфигурация (в .gitignore)
│   └── properties.example        # Шаблон для properties (в репозитории)
├── app/
│   └── build.gradle.kts          # buildTypes
└── .gitignore                    # config/*.properties
```

### Референсы

- [Android Developers: Configure build variants](https://developer.android.com/build/build-variants) - официальная документация по build variants, build types и настройке разных версий приложения в `build.gradle.kts`.
- [Gradle: Build Environment Configuration](https://docs.gradle.org/current/userguide/build_environment.html) - официальное описание `gradle.properties`, project properties, переменных окружения и приоритета источников конфигурации.
- [Android Gradle Plugin API: buildConfigField](https://developer.android.com/reference/tools/gradle-api/7.0/com/android/build/api/dsl/VariantDimension#buildConfigField(kotlin.String,kotlin.String,kotlin.String)) - описание генерации полей `BuildConfig`.
- [Now in Android: app/build.gradle.kts](https://github.com/android/nowinandroid/blob/main/app/build.gradle.kts) - open-source пример Android-проекта на Kotlin DSL с `buildTypes`, `applicationIdSuffix` и чтением Gradle properties для поведения сборки.
