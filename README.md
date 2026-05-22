# Fliq 🚀

**Fliq** is a high-octane, reflex-based Android game designed for the modern mobile era. Built with **Jetpack Compose** and a focus on fluid animations, it challenges players to master their reaction times across multiple innovative game modes.

---

## 🎮 Diverse Game Modes

Fliq offers a variety of challenges to test your focus and speed:

- **💣 Minefield**: High stakes, high reward. A 4x4 grid where half the tiles are bombs. One wrong move and it's game over!
- **⚡ Speed Run**: Race against the clock. How many coins can you tap before time runs out? 
- **🔥 Frenzy**: A fast-paced cascade of rewards. Keep your streak alive as the speed ramps up to extreme levels.
- **🌀 Mirage**: Deceptive patterns. Tiles appear and disappear in confusing sequences to throw off your rhythm.
- **🧘 Zen Mode**: A steady, focused experience. Perfect for finding your flow with constant-speed gameplay.

---

## 🛠 Technical Stack & Architecture

This project is a showcase of **Modern Android Development (MAD)**, following industry-standard patterns and libraries.

### 🏗 Architecture
- **Multi-Module Design**: Cleanly separated features (Modularization by Feature) for improved maintainability, scalability, and faster build times.
- **MVVM Pattern**: Robust, reactive state management using `ViewModel`, `StateFlow`, and `SharedFlow`.
- **Dependency Injection**: Fully powered by **Dagger Hilt** for a clean, decoupled, and testable codebase.

### 🚀 Key Technologies
- **UI Framework**: 100% **Jetpack Compose** with **Material 3** design principles.
- **Local Persistence**: **Room** database for low-latency storage of match history and user badges.
- **Cloud Infrastructure**: 
  - **Firebase Realtime Database** for global leaderboards and user profile synchronization.
  - **Firebase Auth** supporting Social (Google) and Anonymous sign-in.
  - **Firebase Crashlytics & Analytics** for real-time performance monitoring.
- **Monetization**: **AdMob Rewarded Interstitials** integrated to offer players "second chances" in-game.
- **Background Tasks**: **WorkManager** ensures reliable data synchronization even under poor network conditions.

---

## ✨ Immersive UI/UX Features

- **Fluid Animations**: Custom particle systems, background ripples, and score popups built using Compose's transition APIs.
- **Tactile Feedback**: Integrated **Haptic API** provides distinct vibrations for hits and misses, enhancing the physical feel of the game.
- **Dynamic Audio**: Context-aware background music and sound effects managed via a centralized `SoundRepository`.
- **Achievements**: A robust **Achievement System** that awards badges based on reaction time, streaks, and "clutch" moments.

---

## 📂 Project Structure

```text
├── app                      # App entry point, Navigation & UI Shell
├── auth                     # Social Authentication logic
├── common                   # Shared Utilities (Ads, Analytics, Network)
├── core                     # Design System, Theme, & Global Models
├── database                 # Room persistence & Repository layer
├── feature
│   ├── frenzy               # 🔥 Frenzy Game Mode
│   ├── leaderboard          # 🏆 Global Rankings & Match History
│   ├── minefield            # 💣 Minefield Game Mode
│   ├── mirage               # 🌀 Mirage Game Mode
│   ├── speed-run            # ⚡ Speed Run Game Mode
│   └── zen-mode             # 🧘 Zen Game Mode
├── game-engine              # 🛠 Shared Game Logic, Models & Effects
```

---

## 🚀 Getting Started

1. **Clone**: `git clone https://github.com/your-username/fliq.git`
2. **Setup Firebase**: Add your `google-services.json` to the `app/` directory.
3. **Configure Ads**: Add your AdMob IDs to `gradle.properties` or `BuildConfig`.
4. **Build**: Open in **Android Studio Ladybug** (or newer) and run the `:app` module.

---

*Developed with ❤️ as a showcase of modern Android capabilities.*
