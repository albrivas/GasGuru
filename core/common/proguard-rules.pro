# ProGuard rules for core.common module

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