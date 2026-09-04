SMB Gallery (prototype)

What it does
- Browse an SMB share (TripMate) and show thumbnails using a local thumbnail cache (no full sync).
- Tap is stubbed to open full-res (you can extend to stream the file).
- Download to DCIM will be implemented in DownloadManager (see notes) — example stubs included.

Important
- Uses JCIFS (SMB1). If your TripMate requires SMB2/3, ask me to add SMBJ fallback.
- Default TripMate IP set to 10.10.10.254 and default share "USB" (change in Settings).

Build instructions (local)
1. Install Android Studio (recommended).
2. Create a new directory, paste the project files into app/ and top-level Gradle files as shown.
3. Open the folder in Android Studio. Let it sync Gradle and download dependencies.
4. Connect your Pixel 7 Pro, enable developer options and USB debugging, or use a device/emulator.
5. Build → Build Bundle(s) / APK(s) → Build APK(s). The APK will be in app/build/outputs/apk/debug/.

Sideload (install APK on Pixel)
- On Pixel: Settings → Apps → Special app access → Install unknown apps → enable for your file manager or browser.
- Copy the APK to the phone and open it to install, or use `adb install -r app-debug.apk` from your PC.

First-run
- Open app → Settings (gear) → enter TripMate host: 10.10.10.254, share (e.g., USB), username (admin), password (enter password).
- Back to main screen → app will list files and build thumbnails (first indexing will take time; leave the phone plugged in and connected to TripMate Wi‑Fi).

Notes & next steps I can do for you
- Add SMB2/3 fallback (SMBJ) if TripMate enforces SMB2/3.
- Implement streaming full-res image viewer (I can add an Activity that reads InputStream from SmbManager and displays via BitmapFactory.decodeStream).
- Implement download (copy) to DCIM via MediaStore (I can add this if you want direct save-to-Gallery behavior).
- Create a GitHub repo and configure GitHub Actions to build APK automatically — say “Create repo” and I’ll push source + Actions.

If you want the APK built by me and hosted for download, tell me “Create repo + build APK” and I’ll push and enable CI; otherwise build locally using the instructions above.
