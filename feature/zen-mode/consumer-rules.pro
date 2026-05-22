# Zen Mode Feature module consumer rules

# Keep ViewModels and their constructors for Hilt
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep any state classes or models if they were defined in this module
-keepclassmembers class com.fliq.zen_mode.** {
    *** get*();
    void set*(***);
}
