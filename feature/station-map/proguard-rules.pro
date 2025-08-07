# ProGuard rules for feature.station-map module

# Google Maps
-keep class com.google.android.gms.maps.** { *; }
-dontwarn com.google.android.gms.maps.**

# Maps Compose
-keep class com.google.maps.android.compose.** { *; }
-dontwarn com.google.maps.android.compose.**

# Google Places
-keep class com.google.android.libraries.places.** { *; }
-dontwarn com.google.android.libraries.places.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# JUnit5 Testing
-keep class org.junit.jupiter.** { *; }
-keep class org.junit.platform.** { *; }
-dontwarn org.junit.jupiter.**
-dontwarn org.junit.platform.**

# JUnit5 Extensions
-keep class de.mannodermaus.junit5.** { *; }
-dontwarn de.mannodermaus.junit5.**