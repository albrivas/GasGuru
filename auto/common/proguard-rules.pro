# ProGuard rules for auto.common module

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# AndroidX Auto
-keep class androidx.car.app.** { *; }
-dontwarn androidx.car.app.**

# AndroidX Car App
-keep class androidx.car.app.model.** { *; }
-keep class androidx.car.app.navigation.** { *; }
-dontwarn androidx.car.app.model.**
-dontwarn androidx.car.app.navigation.**

# Kotlinx Serialization (R8 compatible rules)
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep class kotlinx.serialization.json.** { *; }
-keepclassmembers @kotlinx.serialization.Serializable class * {
    public static final ** Companion;
    kotlinx.serialization.KSerializer serializer(...);
    public static ** INSTANCE;
}