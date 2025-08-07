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

# Kotlinx Serialization
-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**

# Keep serializable classes
-keepclassmembers class * implements kotlinx.serialization.KSerializer {
    *;
}
-keepclassmembers class * {
    *** kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers @kotlinx.serialization.Serializable class * {
    *;
}