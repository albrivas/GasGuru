# ProGuard rules for core.testing module

# JUnit
-keep class junit.** { *; }
-dontwarn junit.**

# AndroidX Test
-keep class androidx.test.** { *; }
-dontwarn androidx.test.**

# Kotlinx Coroutines Test
-keep class kotlinx.coroutines.test.** { *; }
-dontwarn kotlinx.coroutines.test.**

# Espresso
-keep class androidx.test.espresso.** { *; }
-dontwarn androidx.test.espresso.**

# Hilt Testing
-keep class dagger.hilt.android.testing.** { *; }
-dontwarn dagger.hilt.android.testing.**
-keep class javax.inject.** { *; }

# Compose UI Testing
-keep class androidx.compose.ui.test.** { *; }
-dontwarn androidx.compose.ui.test.**

# Compose Tooling
-keep class androidx.compose.ui.tooling.** { *; }
-dontwarn androidx.compose.ui.tooling.**

# JUnit5
-keep class org.junit.jupiter.** { *; }
-dontwarn org.junit.jupiter.**

# JUnit5 Platform
-keep class org.junit.platform.** { *; }
-dontwarn org.junit.platform.**

# JUnit5 Extensions
-keep class org.junit.platform.runner.** { *; }
-dontwarn org.junit.platform.runner.**