<div align="center">

# 📸 Gallery Sorter
### A TikTok-Style Android App for Cleaning Up Your Camera Roll

An Android app built in **Kotlin** that loads your device's photos and videos into a **full-screen vertical swipe feed** — swipe left to delete, swipe right to keep. Finally a painless way to clean your gallery.

![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-Studio-3DDC84?style=for-the-badge&logo=androidstudio&logoColor=white)
![Min SDK](https://img.shields.io/badge/Min_SDK-24-FF6600?style=for-the-badge&logo=android&logoColor=white)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-6A0DAD?style=for-the-badge&logo=buffer&logoColor=white)

</div>

---

## How It Works

1. On launch the app requests media permissions and loads all photos and videos from your device via **MediaStore**
2. Media is displayed in a full-screen **ViewPager2 vertical feed** — images show in `ImageView`, videos play automatically and loop inline
3. **Swipe left** on any item to delete it — on Android 11+ the system confirmation dialog appears, on older versions it moves to trash directly
4. Deleted item names are saved to **SharedPreferences** and viewable any time from the Profile screen
5. The feed updates instantly after each deletion — no refresh needed

**Features:** 📱 Vertical swipe feed · 🗑️ Swipe-to-delete · ▶️ Inline video playback · 📋 Deleted items log · 🔐 Runtime permissions

---

## Setup

**Requirements:** Android Studio Hedgehog+ · Android SDK 35 · Device or emulator running Android 7.0+ (API 24+)

**1. Clone & open**
```bash
git clone https://github.com/aminabk99/Gallery-Sorter
```
Open the project in **Android Studio**, let Gradle sync, then run on a device or emulator.

> **Note:** A physical device with real photos and videos gives the best experience. The emulator works but its media library is minimal.

---

## Project Structure
```
app/src/main/java/com/example/project4/
├── MainActivity.kt              # Single-activity host
├── FeedFragment.kt              # Main feed UI — loads media, handles swipe-to-delete
├── DeletedMediaFragment.kt      # Shows numbered list of deleted item names
├── MediaCleanupViewModel.kt     # Shared ViewModel — owns media list and deleted log
├── LocalMediaRepository.kt      # Queries MediaStore for images and videos
├── DeletedMediaStore.kt         # Persists deleted item names via SharedPreferences
├── MediaItem.kt                 # Data class for a single photo/video entry
├── MediaPagerAdapter.kt         # RecyclerView adapter for the vertical feed
└── ProfileBottomSheet.kt        # Bottom sheet for deleted items
```
---

## Permissions

| Permission | Purpose |
|---|---|
| `READ_MEDIA_IMAGES` | Read photos (Android 13+) |
| `READ_MEDIA_VIDEO` | Read videos (Android 13+) |
| `READ_EXTERNAL_STORAGE` | Read media on Android 12 and below |

Permissions are requested at runtime on first load. If denied, a dialog explains why they are needed.

---

## Hardest Part
**Handling deletion across Android versions** — Android 11+ requires going through the system `MediaStore.createDeleteRequest` dialog which is asynchronous, while older versions handle deletion differently. Getting both paths to behave consistently and update the feed immediately after confirmation took significant testing across API levels.

## Most Interesting
**The TikTok-style feed mechanic for a utility app** — applying a social media UX pattern to a mundane task like gallery cleanup makes the whole experience feel surprisingly satisfying. The swipe gesture turns something tedious into something almost fun.

---

## Future Improvements
- Undo delete within a grace period
- Batch delete mode for selecting multiple items at once
- Sort/filter by date, size, or media type
- Cloud backup check before deletion

---

<div align="center">
  <sub>Built by <a href="https://github.com/aminabk99">Amina Bilal</a> · <a href="https://linkedin.com/in/amina-bilal-926340382">LinkedIn</a></sub>
</div>


