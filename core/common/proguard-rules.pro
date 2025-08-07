# ProGuard rules for core.common module

# Google Play Services Maps
-keep class com.google.android.gms.maps.** { *; }
-dontwarn com.google.android.gms.maps.**

# Maps Compose
-keep class com.google.maps.android.compose.** { *; }
-dontwarn com.google.maps.android.compose.**

# AndroidX Core
-keep class androidx.core.** { *; }
-dontwarn androidx.core.**

# Material Design
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# AppCompat
-keep class androidx.appcompat.** { *; }
-dontwarn androidx.appcompat.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }