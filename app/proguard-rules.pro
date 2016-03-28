# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/kepa/tools/android-sdk-linux/tools/proguard/proguard-android.txt
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

-dontwarn javax.annotation.**
-dontwarn sun.misc.Unsafe
-dontwarn org.junit.**
-dontwarn android.test.**

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-dump class_files.txt
-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*


-allowaccessmodification
-keepattributes *Annotation*
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-repackageclasses ''

#-keep public class * extends  android.content.Context
#-keep public class * extends  android.content.SharedPreferences
#-keep public class * extends  android.os.AsyncTask
#-keep public class * extends  android.os.Bundle
#-keep public class * extends  android.support.annotation.NonNull
#-keep public class * extends  android.support.design.widget.FloatingActionButton
#-keep public class * extends  android.support.design.widget.Snackbar
#-keep public class * extends  android.view.SubMenu
#-keep public class * extends  android.view.View
#-keep public class * extends  android.support.design.widget.NavigationView
#-keep public class * extends  android.support.v4.view.GravityCompat
#-keep public class * extends  android.support.v4.widget.DrawerLayout
#-keep public class * extends  android.support.v7.app.ActionBarDrawerToggle
#-keep public class * extends  android.support.v7.app.AppCompatActivity
#-keep public class * extends  android.support.v7.widget.Toolbar
#-keep class android.support.v7.widget.LinearLayoutManager { *; }
#-keep public class * extends  android.view.Menu
#-keep public class * extends  android.view.MenuItem
#-keep public class * extends  android.view.ViewGroup
#-keep public class * extends  android.widget.AdapterView
#-keep public class * extends  android.widget.ArrayAdapter
#-keep public class * extends  android.widget.ListView
#-keep public class * extends  android.widget.TextView





# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}


# Preserve all native method names and the names of their classes.
-keepclasseswithmembernames class * {
    native <methods>;
}


-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}


-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}


# Preserve static fields of inner classes of R classes that might be accessed
# through introspection.
-keepclassmembers class **.R$* {
  public static <fields>;
}


# Preserve the special static methods that are required in all enumeration classes.
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


-keep public class * {
    public protected *;
}


-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
##---------------End: proguard configuration common for all Android apps ----------


##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature


# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }


# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }