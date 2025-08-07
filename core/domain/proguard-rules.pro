# ProGuard rules for core.domain module

# AndroidX Core
-keep class androidx.core.** { *; }
-dontwarn androidx.core.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Basic Testing (uses core.testing)
-keep class junit.** { *; }
-dontwarn junit.**
-keep class org.junit.** { *; }
-dontwarn org.junit.**