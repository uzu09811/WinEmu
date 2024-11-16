# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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

-dontwarn org.slf4j.impl.StaticLoggerBinder

-keep class org.json.** { *; }

-keep class h2.a { *; }

-keep class io.github.winemu.** {*;}
-keep class com.winlator.** {*;}

-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Keep all classes in the Apache Commons Compress package
-keep class org.apache.commons.compress.** { *; }
-dontwarn org.apache.commons.compress.**
-keep class org.apache.commons.compress.archivers.** { *; }
-keep class org.apache.commons.compress.compressors.** { *; }
