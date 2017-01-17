# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/xpyct/Android/Sdk/tools/proguard/proguard-android.txt
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

## --------------- Start Project specifics --------------- ##
-useuniqueclassmembernames
-keepattributes SourceFile,LineNumberTable

# Keep the BuildConfig
-keep class com.xpyct.apps.anilab.BuildConfig { *; }

# Keep the support library
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }

# Application classes that will be serialized/deserialized over Gson
# or have been blown up by ProGuard in the past
-keep public class * implements com.bumptech.glide.module.GlideModule

# Ignore warnings: https://github.com/square/okhttp/wiki/FAQs
-dontwarn com.squareup.okhttp.internal.huc.**
# Ignore warnings: https://github.com/square/retrofit/issues/435
-dontwarn com.google.appengine.api.urlfetch.**

-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe

# Keep the pojos used by GSON or Jackson
-keep class com.xpyct.apps.anilab.models.** { *; }

# about libraries
-keep class .R
-keep class **.R$* {
    <fields>;
}

## ---------------- End Project specifics ---------------- ##
