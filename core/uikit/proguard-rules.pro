# ProGuard rules for core.uikit module

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# AndroidX Core
-keep class androidx.core.** { *; }
-dontwarn androidx.core.**

# Constraint Layout Compose
-keep class androidx.constraintlayout.compose.** { *; }
-dontwarn androidx.constraintlayout.compose.**

# Lottie Compose
-keep class com.airbnb.lottie.compose.** { *; }
-dontwarn com.airbnb.lottie.compose.**

# JUnit5 Testing
-keep class org.junit.jupiter.** { *; }
-keep class org.junit.platform.** { *; }
-dontwarn org.junit.jupiter.**
-dontwarn org.junit.platform.**

# JUnit5 Extensions
-keep class de.mannodermaus.junit5.** { *; }
-dontwarn de.mannodermaus.junit5.**

# JUnit5 Compose Testing
-keep class androidx.compose.ui.test.** { *; }
-dontwarn androidx.compose.ui.test.**