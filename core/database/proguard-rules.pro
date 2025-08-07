# ProGuard rules for core.database module

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep @androidx.room.Database class * { *; }
-dontwarn androidx.room.paging.**

# Moshi (used for JSON converters in Room)
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier interface *
-keepclassmembers @com.squareup.moshi.JsonClass class * extends java.lang.Enum {
    <fields>;
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# JUnit5 Testing (androidTestImplementation)
-keep class org.junit.jupiter.** { *; }
-keep class org.junit.platform.** { *; }
-dontwarn org.junit.jupiter.**
-dontwarn org.junit.platform.**

# JUnit5 Extensions
-keep class de.mannodermaus.junit5.** { *; }
-dontwarn de.mannodermaus.junit5.**

# AndroidX Test
-keep class androidx.test.** { *; }
-dontwarn androidx.test.**

# Coroutines Test
-keep class kotlinx.coroutines.test.** { *; }
-dontwarn kotlinx.coroutines.test.**

# Turbine (testing library)
-keep class app.cash.turbine.** { *; }
-dontwarn app.cash.turbine.**