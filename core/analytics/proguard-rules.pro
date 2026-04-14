# Mixpanel Android SDK
-keep class com.mixpanel.android.** { *; }
-dontwarn com.mixpanel.android.**

# AnalyticsEvent model
-keepclassmembers class com.gasguru.core.analytics.AnalyticsEvent { *; }
-keepclassmembers class com.gasguru.core.analytics.AnalyticsEvent$* { *; }

# Implementaciones de AnalyticsHelper (instanciadas vía Koin)
-keep class com.gasguru.core.analytics.MixpanelAnalyticsHelper { <init>(...); }
-keep class com.gasguru.core.analytics.LogcatAnalyticsHelper { <init>(); }
-keep class com.gasguru.core.analytics.NoOpAnalyticsHelper { <init>(); }
