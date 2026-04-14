############################################
# CORE: сохранить дженерики и все аннотации
############################################
-keepattributes Signature,InnerClasses,EnclosingMethod,Exceptions,
RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,
RuntimeVisibleParameterAnnotations,RuntimeInvisibleParameterAnnotations,AnnotationDefault

# Kotlin metadata (нужно Moshi/Kotlin)
-keep class kotlin.Metadata { *; }

############################################
# Retrofit: интерфейсы/классы по аннотациям
############################################
# ВАЖНО: для интерфейсов — именно -keep
-keep interface * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Явно фиксируем ваши API-интерфейсы (на случай наследования/делегаций)
-keep interface com.chaikasoft.app.data.datasource.apiservice.** { *; }

############################################
# Moshi
############################################
# Если используете @JsonClass(generateAdapter = true) — держим имена классов,
# чтобы Moshi нашёл сгенерированные *JsonAdapter
-keepnames @com.squareup.moshi.JsonClass class *

# Если есть ручные @FromJson/@ToJson — оставим их
-keepclassmembers class ** {
    @com.squareup.moshi.FromJson <methods>;
    @com.squareup.moshi.ToJson <methods>;
}
-dontwarn com.squareup.moshi.**

############################################
# DTO из сети (пока не всё покрыто @Json/@SerializedName)
############################################
-keep class com.chaikasoft.app.data.datasource.dto.** { *; }

############################################
# ДОМЕННЫЕ МОДЕЛИ, которые Moshi парсит через рефлексию
# (из стека видим пакет report — фиксируем его полностью)
############################################
-keep class com.chaikasoft.app.domain.models.report.** { *; }

############################################
# Gson (только если реально используете вне Moshi)
############################################
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * extends com.google.gson.TypeAdapter



# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
