# Media Cleanup App

An Android application that displays your device's photos and videos in a TikTok-style vertical feed and lets you swipe to delete media you no longer need.

## Features

- **Vertical scrolling feed** — Browse all local photos and videos in a full-screen, swipeable ViewPager2 feed
- **Swipe-to-delete** — Swipe left on any item to permanently delete it from the device
- **Deleted items log** — View a list of everything you've deleted via the Profile screen
- **Video playback** — Videos play automatically and loop inline in the feed
- **Permission handling** — Gracefully requests the appropriate media permissions based on Android version
- **Android 11+ trash support** — Uses the system `MediaStore.createDeleteRequest` dialog on API 30+; falls back to direct trash on older versions

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.0.21 |
| UI | XML layouts, View Binding, ViewPager2 |
| Architecture | MVVM (ViewModel + LiveData) |
| Navigation | Jetpack Navigation Component |
| Async | Kotlin Coroutines |
| Storage | MediaStore API, SharedPreferences |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 35 (Android 15) |

## Project Structure

```
app/src/main/java/com/example/project4/
├── MainActivity.kt             # Single-activity host
├── FeedFragment.kt             # Main feed UI — loads media, handles swipe-to-delete
├── DeletedMediaFragment.kt     # Shows a numbered list of deleted item names
├── MediaCleanupViewModel.kt    # Shared ViewModel — owns media list and deleted log
├── LocalMediaRepository.kt     # Queries MediaStore for images and videos
├── DeletedMediaStore.kt        # Persists deleted item names via SharedPreferences
├── MediaItem.kt                # Data class for a single photo/video entry
├── MediaPagerAdapter.kt        # RecyclerView adapter for the vertical feed
└── ProfileBottomSheet.kt       # (Unused in current nav) Bottom sheet for deleted items
```

## Setup & Build

### Requirements

- Android Studio Hedgehog or later
- Android SDK 35
- A physical device or emulator running Android 7.0+ (API 24+)

### Steps

1. Clone or unzip the project.
2. Open the `Project4v7` folder in Android Studio.
3. Let Gradle sync finish.
4. Run the app on a device or emulator (**Run > Run 'app'**).

> **Note:** The app reads real media from the device. A physical device with photos and videos will give the best experience. The emulator works but its media library is minimal.

## Permissions

| Permission | Purpose |
|---|---|
| `READ_MEDIA_IMAGES` | Read photos (Android 13+) |
| `READ_MEDIA_VIDEO` | Read videos (Android 13+) |
| `READ_EXTERNAL_STORAGE` | Read media on Android 12 and below |

Permissions are requested at runtime the first time the feed loads. If denied, a dialog explains why they are needed.

## How It Works

1. On launch, `FeedFragment` checks for media permissions and calls `MediaCleanupViewModel.loadMedia()`.
2. `LocalMediaRepository` queries `MediaStore.Files` for all images and videos, sorted newest-first.
3. The results populate a vertical `ViewPager2` via `MediaPagerAdapter`. Images are shown with `ImageView`; videos play via `VideoView` with looping enabled.
4. Swiping left triggers deletion:
   - **Android 11+**: The system delete-request dialog is shown; on confirmation, the item is removed from the feed and its name is saved to `DeletedMediaStore`.
   - **Android 10 and below**: The item is moved to the MediaStore trash directly.
5. Tapping the **Profile** button navigates to `DeletedMediaFragment`, which displays a numbered list of all deleted file names pulled from SharedPreferences.

## Dependencies

```toml
androidx-core-ktx          = "1.15.0"
androidx-appcompat         = "1.7.0"
material                   = "1.12.0"
androidx-fragment-ktx      = "1.8.5"
androidx-viewpager2        = "1.1.0"
kotlinx-coroutines-android = "1.10.1"
androidx-lifecycle-*       = "2.8.7"
androidx-navigation-*      = "2.8.5"
androidx-constraintlayout  = "2.2.0"
```
