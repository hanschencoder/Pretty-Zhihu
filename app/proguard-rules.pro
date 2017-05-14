# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

# 不使用大小写混合类名，注意，windows用户必须为ProGuard指定该选项
-dontusemixedcaseclassnames
# 不忽略library里面非public修饰的类
-dontskipnonpubliclibraryclasses
# 把所有信息都输出，而不仅仅是输出出错信息
-verbose

# 不对dex进行优化和预检
-dontoptimize
-dontpreverify

# 保留Annotation不混淆
-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# 保留含有native方法的类不混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留继承于View的类的get和set方法不被混淆
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# 保留Activity中定义的onClick事件
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# 保留枚举的方法
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留Parcelable的CREATOR成员
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

# 保留R资源类内部类的静态成员变量不被混淆
-keepclassmembers class **.R$* {
    public static <fields>;
}

-keep class android.support.**
-dontwarn android.support.**

# Keep注解的支持
-keep @android.support.annotation.Keep class * {*;}
-keep class android.support.annotation.Keep
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}

# ---------------------------------------------------------
# 保留四大组件，自定义的Application等不被混淆，实际测试中发现manifests中注册的组件会自动保留
#-keep public class * extends android.app.Activity
#-keep public class * extends android.app.Application
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#-keep public class * extends android.app.backup.BackupAgentHelper
#-keep public class * extends android.preference.Preference
#-keep public class * extends android.view.View

-keep class org.** { *; }
-dontwarn org.**

-keep class io.** { *; }
-dontwarn io.**

-keep class javax.** { *; }
-dontwarn javax.**

-keep class org.greenrobot.greendao.** { *; }
-dontwarn org.greenrobot.greendao.**
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
    public static java.lang.String TABLENAME;
}
-keep class **$Properties { *; }

-keep class butterknife.** { *; }
-dontwarn butterknife.**

-keep class com.afollestad.materialdialogs.** { *; }
-dontwarn com.afollestad.materialdialogs.**

-keep class com.roughike.bottombar.** { *; }
-dontwarn com.roughike.bottombar.**

-keep class com.squareup.** { *; }
-dontwarn com.squareup.**

-keep class com.tbruyelle.** { *; }
-dontwarn com.tbruyelle.**

-keep class com.wang.avi.** { *; }
-dontwarn com.wang.avi.**

-keep class com.wdullaer.materialdatetimepicker.** { *; }
-dontwarn com.wdullaer.materialdatetimepicker.**

-keep class de.hdodenhof.circleimageview.** { *; }
-dontwarn de.hdodenhof.circleimageview.**

-keep class dagger.** { *; }
-dontwarn dagger.**

-keep class io.reactivex.** { *; }
-dontwarn io.reactivex.**

-keep class me.zhanghai.android.materialprogressbar.** { *; }
-dontwarn me.zhanghai.android.materialprogressbar.**

-keep class org.reactivestreams.** { *; }
-dontwarn org.reactivestreams.**

-keep class com.amap.** { *; }
-dontwarn com.amap.**

-keep class com.autonavi.** { *; }
-dontwarn com.autonavi.**

-keep class com.bumptech.glide.** { *; }
-dontwarn com.bumptech.glide.**

-keep class com.google.** { *; }
-dontwarn com.google.**

-keep class com.maploc.** { *; }
-dontwarn com.maploc.**

-keep class com.nineoldandroids.** { *; }
-dontwarn com.nineoldandroids.**

-keep class com.rengwuxian.** { *; }
-dontwarn com.rengwuxian.**

-keep class com.tbruyelle.** { *; }
-dontwarn com.tbruyelle.**

-keep class com.vansuita.** { *; }
-dontwarn com.vansuita.**

-keep class okio.** { *; }
-dontwarn okio.**

-keep public class * extends com.google.protobuf.** { *; }

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keep class com.amulyakhare.textdrawable.** { *; }
-dontwarn com.amulyakhare.textdrawable.**

-keep class com.github.chrisbanes.photoview.** { *; }
-dontwarn com.github.chrisbanes.photoview.**

-keep class com.google.** { *; }
-dontwarn com.google.**

-keep class com.pnikosis.materialishprogress.** { *; }
-dontwarn com.pnikosis.materialishprogress.**

-keep class okhttp3.** { *; }
-dontwarn okhttp3.**