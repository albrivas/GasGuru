# ProGuard rules for core.supabase module

# Supabase KMP
-keep class io.github.jan.supabase.** { *; }
-dontwarn io.github.jan.supabase.**

# Ktor Client
-keep class io.ktor.client.** { *; }
-dontwarn io.ktor.**

# Arrow Core
-dontwarn arrow.**
-keep class arrow.core.** { *; }

# kotlinx-serialization (SupabaseFuelStation, PriceAlertSupabase)
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers @kotlinx.serialization.Serializable class * {
    public static final ** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class com.gasguru.core.supabase.model.** {
    *;
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
