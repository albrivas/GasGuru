# ProGuard rules for core.network module

# OkHttp (Ktor client engine)
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# Kotlinx Serialization (R8 compatible rules)
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep class kotlinx.serialization.json.** { *; }
-keepclassmembers @kotlinx.serialization.Serializable class * {
    public static final ** Companion;
    kotlinx.serialization.KSerializer serializer(...);
    public static ** INSTANCE;
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}