# ProGuard rules for Transistor Radio
-keepclassmembers class * extends androidx.media3.session.MediaSessionService {
    <init>();
}
-keep class com.transistor.radio.data.local.** { *; }
-keep class com.transistor.radio.domain.model.** { *; }
