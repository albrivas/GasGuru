# ProGuard rules for core.ui module

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# AndroidX Core
-keep class androidx.core.** { *; }
-dontwarn androidx.core.**