# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/xxx/Library/Android/sdk/tools/proguard/proguard-android.txt

# Keep Gson models
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class com.hastakala.app.data.** { *; }
