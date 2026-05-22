# Speed Run Feature module consumer rules

# Keep ViewModels and their constructors for Hilt
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep any state classes or models if they were defined in this module
-keepclassmembers class com.fliq.speed_run.** {
    *** get*();
    void set*(***);
}
