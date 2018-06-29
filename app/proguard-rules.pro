# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following

# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-ignorewarnings

# MobileLine Core

-keep class com.tencent.qcloud.core.** { *;}
-keep class bolts.** { *;}
-keep class com.tencent.tac.** { *;}
-keep class com.tencent.stat.*{*;}
-keep class com.tencent.mid.*{*;}
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

#ying shi yun
-dontwarn com.ezviz.player.**
-keep class com.ezviz.player.** { *;}

-dontwarn com.ezviz.statistics.**
-keep class com.ezviz.statistics.** { *;}

-dontwarn com.ezviz.stream.**
-keep class com.ezviz.stream.** { *;}

-dontwarn com.ezviz.hcnetsdk.**
-keep class com.ezviz.hcnetsdk.** { *;}


-dontwarn com.ezviz.opensdk.**
-keep class com.ezviz.opensdk.** { *;}

-dontwarn com.hik.**
-keep class com.hik.** { *;}

-dontwarn com.hikvision.**
-keep class com.hikvision.** { *;}

-dontwarn com.videogo.**
-keep class com.videogo.** { *;}

-dontwarn org.MediaPlayer.PlayM4.**
-keep class org.MediaPlayer.PlayM4.** { *;}

-dontwarn com.sun.jna.**
-keep class com.sun.jna.**{*;}

#Gson混淆配置
-keepattributes Annotation
-keep class sun.misc.Unsafe { *; }
-keep class com.idea.fifaalarmclock.entity.*
-keep class com.google.gson.stream.* { *; }

#引用mars的xlog，混淆配置
-keep class com.tencent.mars.** {
 public protected private *;
}