# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep line numbers for debugging
-keepattributes SourceFile,LineNumberTable

# Kotlinx Serialization (R8 compatible rules)
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep class kotlinx.serialization.json.** { *; }
-keepclassmembers @kotlinx.serialization.Serializable class * {
    public static final ** Companion;
    kotlinx.serialization.KSerializer serializer(...);
    public static ** INSTANCE;
}

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.AndroidEntryPoint { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Splash Screen
-keep class androidx.core.splashscreen.** { *; }

# Material Icons Extended
-keep class androidx.compose.material.icons.** { *; }
-dontwarn androidx.compose.material.icons.**

# Missing annotation classes (used only at compile time)
-dontwarn com.google.j2objc.annotations.**
-dontwarn edu.umd.cs.findbugs.annotations.**

# Guava - suppress j2objc warnings
-dontwarn com.google.common.util.concurrent.**
-dontwarn com.google.common.collect.**
