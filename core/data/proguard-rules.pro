# ProGuard rules for core.data module

# Arrow (Functional Programming)
-keep class arrow.core.** { *; }
-dontwarn arrow.core.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Basic Testing (uses core.testing)
-keep class junit.** { *; }
-dontwarn junit.**
-keep class org.junit.** { *; }
-dontwarn org.junit.**