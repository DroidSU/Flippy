# Flippy

Flippy is a fast-paced, high-reflex Android game built with modern Android development tools. The game challenges players to tap on coins appearing on a 4x4 grid while avoiding bombs. It features a dynamic difficulty system, real-time feedback through haptics and sound, and a robust profile and leaderboard system.

## 🎮 Features

- **Dynamic Gameplay**: A reactive 4x4 grid where tiles reveal rewards (Coins) and hazards (Bombs).
- **Difficulty Levels**: Three distinct difficulty modes (Easy, Normal, Hard) that adjust the speed and frequency of tile reveals.
- **Real-time Feedback**: Immersive experience with haptic feedback (vibrations) and synchronized sound effects.
- **Visual Effects**: Custom animations including background ripples, score popups, and particle effects.
- **Profile Management**: Personalized user experience with customizable usernames and avatars.
- **Leaderboard & Match History**: Track your progress with detailed match statistics and view top scores.
- **Offline Support**: Local database for match history with automatic synchronization to Firebase using WorkManager for reliable background processing.
- **Analytics & Crashlytics**: Integrated performance monitoring and event tracking.

## 🛠 Technical Specifications

This project follows **Modern Android Development (MAD)** practices and is architected with scalability and maintainability in mind.

### Architecture
- **Clean Architecture**: Decoupled layers (Data, Domain, UI) for better separation of concerns.
- **Multi-module Project**: Organized into specialized modules to improve build times and enforce boundaries.
- **MVVM Pattern**: Robust state management using `ViewModel`, `StateFlow`, and `SharedFlow`.

### Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Dependency Injection**: Dagger Hilt
- **Asynchronous Programming**: Kotlin Coroutines & Flow
- **Local Database**: Room (with KSP)
- **Background Tasks**: WorkManager (for Firebase sync)
- **Backend Services**: 
  - Firebase Authentication (Social & Anonymous Sign-in)
  - Firestore (Global Leaderboard & User Sync)
  - Firebase Crashlytics & Analytics
  - Firebase App Check (Play Integrity)
- **UI Components & Animations**:
  - Material 3 & Extended Icons
  - Lottie Compose
- **Haptics & Audio**: Android Vibrator API and specialized SoundRepository.

## 🏗 Project Structure

```text
├── app                      # Main application module
├── auth                     # Authentication logic
├── common                   # Shared business logic and repositories
├── core                     # Design system and global settings
├── database                 # Local persistence layer
├── feature
│   ├── leaderboard          # Leaderboard UI and logic
│   ├── profile              # User profile management
│   └── settings             # App configuration
└── game-engine              # Core game mechanics and Game VM
```

## 🚀 Getting Started

1. Clone the repository.
2. Open in Android Studio (Ladybug or newer recommended).
3. Set up a Firebase project and add your `google-services.json` to the `app/` directory.
4. Sync Gradle and run the `:app` module.

---

*Developed with ❤️ as a demonstration of modern Android capabilities.*
