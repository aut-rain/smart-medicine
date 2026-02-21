# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# ============================================
# Room 相关规则
# ============================================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ============================================
# Kotlin Serialization 相关规则
# ============================================
-keepattributes Signature
-keepattributes *Annotation*
-keepclassmembers class kotlinx.serialization.json.** {
  *;
}
-keepclassmembers class com.example.smart_medicine_android.data.network.model.** {
  *;
}

# ============================================
# Retrofit 相关规则
# ============================================
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembernames class * {
    @retrofit2.http.*;
}

# ============================================
# OkHttp 相关规则
# ============================================
-dontwarn okhttp3.**
-dontwarn okio.**

# ============================================
# DataStore 相关规则
# ============================================
-keepclassmembers class androidx.datastore.core.** { *; }

# ============================================
# 其他通用规则
# ============================================
-keepclassmembers class * {
    @androidx.compose.** <init>(...);
}

-keepclassmembers class androidx.compose.** {
    *;
}

-dontwarn org.jetbrains.kotlin.**

# 保留 Kotlin 反射
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes EnclosingMethod

# 如果使用反射，保留相关类
-keep class com.example.smart_medicine_android.** { *; }
-keep class com.example.smart_medicine_android.data.** { *; }
-keep class com.example.smart_medicine_android.data.local.** { *; }
-keep class com.example.smart_medicine_android.data.network.** { *; }
