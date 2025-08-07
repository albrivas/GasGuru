# ProGuard rules for feature.onboarding module

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# JUnit5
-keep class org.junit.jupiter.** { *; }
-dontwarn org.junit.jupiter.**

# JUnit5 Extensions
-keep class org.junit.platform.** { *; }
-dontwarn org.junit.platform.**

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**