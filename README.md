# Fliq 🚀

**Fliq** is a high-octane, reflex-based Android game designed for the modern mobile era. Built with **Jetpack Compose** and a focus on fluid animations, it challenges players to master their reaction times across multiple innovative game modes.

---

## 🎮 Diverse Game Modes

Fliq offers a variety of challenges to test your focus and speed:

- **💣 Minefield**: High stakes, high reward. A 4x4 grid where half the tiles are bombs. One wrong move and it's game over!
- **⚡ Speed Run**: Race against the clock. How many coins can you tap before time runs out? 
- **🔥 Frenzy**: A fast-paced cascade of rewards. Keep your streak alive as the speed ramps up to extreme levels.
- **🌀 Mirage**: Deceptive patterns. Tiles appear and disappear in confusing sequences to throw off your rhythm.
- **🧘 Zen Mode**: A steady, focused experience. Perfect for finding your flow with slower-scaling, rhythmic gameplay.

---

## 🛠 Technical Stack & Architecture

This project is a showcase of **Modern Android Development (MAD)**, following industry-standard patterns and libraries.

### 🏗 Architecture
- **Multi-Module Design**: 14 cleanly separated modules (Modularization by Feature/Layer) for improved maintainability, scalability, and faster build times.
- **MVVM Pattern**: Robust, reactive state management using `ViewModel`, `StateFlow`, and `SharedFlow`.
- **Dependency Injection**: Fully powered by **Dagger Hilt** for a clean, decoupled, and testable codebase.

### 🚀 Key Technologies
- **UI Framework**: 100% **Jetpack Compose** with **Material 3** design principles.
- **Local Persistence**: **Room** database for low-latency storage of match history and user badges.
- **Cloud Infrastructure**: 
  - **Firebase Realtime Database** for global leaderboards and user profile synchronization.
  - **Firebase Auth** supporting Social (Google) and Anonymous sign-in.
  - **Firebase App Check** (Play Integrity & Debug) to protect backend resources from abuse.
  - **Firebase Crashlytics & Analytics** for real-time performance and usage monitoring.
- **Monetization**: **AdMob Rewarded Interstitials** integrated to offer players "second chances" in-game.
- **Background Tasks**: **WorkManager** ensures reliable data synchronization even under poor network conditions.
- **Animations**: **Lottie** for complex UI interactions and **Compose Animation APIs** for in-game effects.

---

## ✨ Immersive UI/UX Features

- **Fluid Animations**: Custom particle systems, background ripples, and score popups built using Compose's transition APIs.
- **Tactile Feedback**: Integrated **Haptic API** provides distinct vibrations for hits and misses, enhancing the physical feel of the game.
- **Dynamic Audio**: Context-aware background music and sound effects managed via a centralized `SoundRepository`.
- **Achievements & Badges**: A robust **Achievement System** that awards badges based on reaction time, streaks, and "clutch" moments.
- **Interactive Tutorials**: Comprehensive onboarding system for each game mode to guide new players through the mechanics.
- **Landscape Support**: Fully optimized layouts for both Portrait and Landscape orientations across all screens.

---

## 📂 Project Structure

```text
├── app                      # App entry point, Navigation & UI Shell
├── auth                     # Social Authentication logic (Google Sign-In)
├── common                   # Shared Utilities (Ads, Analytics, Difficulty Manager)
├── core                     # Design System, Theme, & Global Models
├── database                 # Room persistence & Repository layer
├── feature
│   ├── frenzy               # 🔥 Frenzy Game Mode
│   ├── leaderboard          # 🏆 Global Rankings & Match History
│   ├── minefield            # 💣 Minefield Game Mode
│   ├── mirage               # 🌀 Mirage Game Mode
│   ├── profile              # 👤 User Profile & Achievements
│   ├── settings             # ⚙️ Game Settings & Preferences
│   ├── speed-run            # ⚡ Speed Run Game Mode
│   └── zen-mode             # 🧘 Zen Game Mode
├── game-engine              # 🛠 Shared Game Logic, Models & UI Components
```

---

## 🚀 Getting Started

1. **Clone**: `git clone https://github.com/your-username/fliq.git`
2. **Setup Firebase**: Add your `google-services.json` to the `app/` directory.
3. **Configure Ads**: Add your AdMob App ID to the manifest placeholders and Unit IDs to `BuildConfig`.
4. **Build**: Open in **Android Studio Ladybug** (or newer) and run the `:app` module.

---

*Developed with ❤️ as a showcase of modern Android capabilities.*
