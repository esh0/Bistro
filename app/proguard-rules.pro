# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontpreverify

-optimizationpasses 5
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable


# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.

-keepattributes *Annotation*,EnclosingMethod
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.content.Context
-keep public class * extends android.app.backup.BackupAgent
-keep public class * extends android.preference.Preference
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v4.app.DialogFragment
-keep public class * extends android.support.v4.widget.DrawerLayout
-keep public class android.content.Context
-keep public class android.util.attributeset
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService


-keepclassmembers class org.apache.cordova.** { *; }
-keepclassmembers interface org.apache.cordova.** { *; }
-keepclassmembers enum org.apache.cordova.** { *; }


-keepclassmembers class de.swm.** { *; }
-keepclassmembers interface de.swm.** { *; }
-keepclassmembers enum de.swm.** { *; }


-keepclassmembers class org.geojson.** { *; }
-keepclassmembers interface org.geojson.** { *; }
-keepclassmembers enum org.geojson.** { *; }


-keepclassmembers class javax.persistence.** { *; }
-keepclassmembers interface javax.persistence.** { *; }
-keepclassmembers enum javax.persistence.** { *; }


# OrmLite uses reflection
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keepclassmembers class * { public <init>(android.content.Context); }


# Crittercism
-keep public class com.crittercism.**
-keepclassmembers public class com.crittercism.*
{
    *;
}
-keepattributes SourceFile, LineNumberTable


# Jackson allow Obfuscating
-keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility {
    public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *;
}

-keepnames class com.fasterxml.jackson.** { *; }
-keepnames interface com.fasterxml.jackson.** { *; }
-keep class org.codehaus.** { *; }


# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keep public class * extends android.view.View {
 public <init>(android.content.Context);
 public <init>(android.content.Context, android.util.AttributeSet);
 public <init>(android.content.Context, android.util.AttributeSet, int);
 public void set*(...);
}

-keep public class * {
 public <init>(android.content.Context);
 public <init>(android.content.Context, android.util.AttributeSet);
 public <init>(android.content.Context, android.util.AttributeSet, int);
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**
-dontwarn com.robotium.**
-dontwarn com.fasterxml.**
-dontwarn javax.persistence.spi.**

# JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepattributes JavascriptInterface
-keep public class com.mypackage.MyClass$MyJavaScriptInterface
-keep public class * implements com.mypackage.MyClass$MyJavaScriptInterface
-keepclassmembers class com.mypackage.MyClass$MyJavaScriptInterface {
    <methods>;
}

# Lombok
-dontwarn lombok.core.configuration.**
-dontwarn java.beans.ConstructorProperties
-keep class lombok.core.configuration.** { *; }

#ButterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# LeakCanary
-keep class org.eclipse.mat.** { *; }
-keep class com.squareup.leakcanary.** { *; }

-keep class rx.internal.util.** { *; }
-dontwarn org.apache.http.**
-dontwarn sun.misc.Unsafe
-dontwarn android.app.Notification

-dontwarn org.joda.convert.**
-keep class org.apache.lucene.util.RamUsageEstimator
-dontwarn javax.**
-dontwarn java.lang.management.**

-keep class javax.** { *; }
-keep class java.lang.management.** { *; }

-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

-keepattributes *Annotation*, Signature

-keep public class com.coresuite.android.entities.dto.** {
  public protected *;
}
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }