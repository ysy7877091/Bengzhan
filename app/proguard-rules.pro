# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\JAVA\androidstudiosdk/tools/proguard/proguard-android.txt
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
-optimizationpasses 5
-dontskipnonpubliclibraryclassmembers
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-ignorewarning                   # 忽略警告

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}
-keep public class * extends android.os.IInterface

-dontwarn android.support.v4.**                                             #去掉警告
-keep class android.support.v4.** { *; }                                    #过滤android.support.v4
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment


-dontwarn android.support.v7.**                                             #去掉警告
-keep class android.support.v7.** { *; }                                    #过滤android.support.v7
-keep interface android.support.v7.app.** { *; }
-keep public class * extends android.support.v7.**
-keep public class * extends android.app.Fragment

-keep class org.codehaus.jackson.**{*;}                                     #过滤app/libs/jackson-core-1.9.5.jar

-keep class org.achartengine.internal.**{*;}                                            #过滤app/libs/arcgis-android-api.jar
-keep class com.esri.**{*;}

-keep class com.ant.liao.**{*;}                                                 #过滤app/libs/GifView.jar
-keep class com.github.mikephil.charting.**{*;}
-keep class org.apache.http.**{*;}                                                 #过滤aorg.apache.http

-keep class org.apache.http.**{*;}                                                 #过滤app/libs/httpcore-4.0.jar

-keep class com.dh.DpsdkCore.**{*;}                                                 #过滤IDpsdkCore.jar

-keep class com.company.PlaySDK.**{*;}                                                 #过滤app/libs/IPlaySDK.jar

-keep class org.codehaus.jackson.**{*;}                                                 #过滤app/libs/jackson-mapper-1.9.5.jar

-keep class jcifs.**{*;}                                                 #过滤app/libs/jcifs-1.3.17.jar
-keep class org.**{*;}                                                 #过滤app/libs/ksoap2-android-assembly-2.4-jar-with-dependencies.jar
-keep class com.android.volley.**{*;}                                  #过滤app/libs/volley.jar

-keep class com.com.vanpeng.Adapter.**{*;}                            #过滤掉自己编写的实体类
-keep class com.vanpeng.javabeen.**{*;}                                       #过滤掉自己编写的实体类
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class **.R$* {
 *;
}
-keepclassmembers class * {
    void *(**On*Event);
}

-keepclasseswithmembernames class * { # 保持 native 方法不被混淆
 native <methods>;
}

-keepclasseswithmembers class * { # 保持自定义控件类不被混淆
 public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {# 保持自定义控件类不被混淆
 public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
 public void *(android.view.View);
}

-keepclassmembers enum * { # 保持枚举 enum 类不被混淆
 public static **[] values();
 public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable { # 保持 Parcelable 不被混淆
 public static final android.os.Parcelable$Creator *;
}

