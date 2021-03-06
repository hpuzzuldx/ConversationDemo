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
-dontshrink
-dontoptimize
-keepattributes Signature
-ignorewarnings

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keep class com.ldx.conversationbase.activity.**{*;}
-keep class com.ldx.conversationbase.listener.**{*;}
-keep class com.ldx.conversationbase.db.**{*;}
-keep class com.ldx.conversationbase.common.**{*;}
-keep class com.microsoft.xiaoicesdk.conversationbasebase.widget.**{*;}
-keep class com.ldx.conversationbase.camera.**{*;}
-keep class com.ldx.conversationbase.view.**{*;}
-keep class com.ldx.conversationbase.utils.**{*;}


-keep class **.R$* {*;}

# support-v7-appcompat
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

# support-design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }


# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#4.support-v4
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }

-keepattributes *Annotation*
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
       public void set*(...);
       *** set*(...);
       *** get*();
}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
    *** get*();
}

-dontwarn com.squareup.picasso.**
-dontwarn com.bumptech.glide.**
-dontwarn com.zhihu.matisse.engine.**

### greenDAO 3

-keep class org.greenrobot.greendao.** {*;}
-keep class bingdic.greendao.** { *;}
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
    public static java.lang.String TABLENAME;
 }
-keep class **$Properties

# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
# If you do not use RxJava:
-dontwarn rx.**
-dontwarn net.sqlcipher.database.**
-dontwarn org.greenrobot.greendao.**

-keep class data.db.dao.*$Properties {
    public static <fields>;
}
-keepclassmembers class data.db.dao.** {
    public static final <fields>;
}
