# OpenDrive

A free, open-source, landscape-first Android launcher starter for aftermarket car head units. This is an early prototype inspired by rich automotive dashboards; it is not affiliated with Car Nebula or any other launcher.

## Display target

- Tuned for a 1280×720 (16:9) landscape head-unit display.
- Android 14 is API 34; this starter uses `targetSdk 34` and `compileSdk 35` for Android 14-focused testing.
- Uses adaptive compact spacing when Android reports less than 430dp of screen height, which is common when a physical 720p display uses a higher density.
- Dashboard columns use weighted sizing rather than fixed pixel positions, so the layout can adapt to different density settings.

## Current prototype

- Launcher activity registered as both `HOME` and regular app launcher.
- Dark/light dashboard theme toggle.
- Live clock and date.
- App library that lists launchable apps and starts them when tapped.
- Quick links for settings, music intent, and app library.
- No accounts, analytics, ads, subscription, or paid API dependency.
- `minSdk 26` (Android 8.0) target; real-device compatibility still needs testing.

GPS speed, compass, media-session metadata/control, persistent customization, Home Assistant integration, maps, and widget rearrangement are planned next and are not implemented in this starter.

## Build

1. Install Android Studio and a JDK 17-compatible Android build environment.
2. Open this folder (`OpenDrive`) in Android Studio.
3. Allow Gradle to sync and install Android SDK Platform 35 if prompted. The app targets Android 14 (API 34).
4. Select **Build > Build App Bundle(s) / APK(s) > Build APK(s)**.
5. The debug APK should appear at `app/build/outputs/apk/debug/app-debug.apk`.

The first Gradle sync needs internet to download Android Gradle Plugin and AndroidX dependencies. The app itself does not need a paid service or API key.

## Install on a head unit

Copy the debug APK to the head unit (USB, SD card, or file transfer), enable installation from that source if Android asks, and install it. Press Home and select **OpenDrive**. Choose **Always** only after testing that the head unit's radio, steering-wheel keys, reverse camera, and manufacturer-specific apps still work. To revert, open Android Settings > Apps > Default apps > Home app and choose the previous launcher; menu labels vary by device.

If the unit has no easy file transfer, use Android Debug Bridge from a computer: `adb install -r app/build/outputs/apk/debug/app-debug.apk`.

## Compatibility note

Head units often ship customized Android builds. Some hide app packages, restrict background location, use vendor-specific radio APIs, or do not expose a standard music-player intent. Test on the actual device before making OpenDrive the default home app. Do not use the screen while driving; configure the dashboard while parked.

## License

This project is provided under the Apache License 2.0. See `LICENSE`.
