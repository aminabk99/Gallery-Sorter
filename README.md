<div align="center">

# 📱 Gallery Sorter
### TikTok-Style Android Gallery Cleaner

An Android app that loads your photos and videos into a vertical swipe feed.
Swipe left to delete. Keep what matters, ditch what doesn't.

![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-8.0+-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-blue?style=for-the-badge)

</div>

---

## How It Works

1. App requests photo/video permission on launch
2. All media is loaded from device storage into a vertical ViewPager2 feed (newest first)
3. Swipe **left** on any item to delete it
4. On Android 11+, the system confirmation dialog appears before deletion
5. Deleted item names are saved persistently and viewable from the Profile screen

---

## Features

- Vertical swipe feed — scroll through photos and videos like a TikTok-style UI
- Swipe-to-delete with system-level confirmation (Android 11+)
- Supports both images and videos in the same feed
- Deleted items log persists across app sessions
- Handles runtime permissions correctly for Android 8 through 14+
- Empty state shown when no media is available or permission is denied

---

## Architecture

The app follows **MVVM** (Model-View-ViewModel) with Android Jetpack components.

```
app/src/main/java/
├── MainActivity.kt             # Activity host — sets up navigation graph
├── FeedFragment.kt             # Main feed UI — swipe feed + delete handling
├── DeletedMediaFragment.kt     # Deleted items list screen
├── ProfileBottomSheet.kt       # Bottom sheet showing deleted item names
├── MediaPagerAdapter.kt        # RecyclerView adapter for the media feed
├── MediaCleanupViewModel.kt    # ViewModel — feed state, deletion logic
├── LocalMediaRepository.kt     # MediaStore queries and trash operations
├── DeletedMediaStore.kt        # SharedPreferences persistence for deleted names
└── MediaItem.kt                # Data class representing a photo or video
```

---

## Tech Stack

| Component | Library |
|---|---|
| Language | Kotlin |
| UI | ViewPager2, RecyclerView, Fragments |
| Architecture | MVVM — ViewModel + LiveData |
| Async | Kotlin Coroutines |
| Navigation | Jetpack Navigation Component |
| Storage | MediaStore API + SharedPreferences |
| Permissions | ActivityResult API |

---

## Setup

**Requirements:** Android Studio · Android device or emulator running API 26+

```bash
git clone https://github.com/aminabk99/Gallery-Sorter
```

Open in Android Studio, sync Gradle, and run on a device or emulator. Grant media access when prompted.

> **Note:** Actual deletion requires a physical device with real media. The emulator has limited MediaStore content.

---

## Hardest Part
Getting swipe-to-delete right across Android versions. Android 11+ requires launching a system `IntentSender` for user confirmation, while older versions need a direct `ContentResolver` update — both paths had to be handled cleanly without leaving the UI in a broken state if the user cancelled.

## Most Interesting
The feed UX. Using `ViewPager2` in vertical orientation with `ItemTouchHelper` for horizontal swipe gives the app a satisfying TikTok-like feel entirely within standard Android components — no third-party libraries needed.
