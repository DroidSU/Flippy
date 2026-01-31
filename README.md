# Flippy 🧠🎮

**Flippy** is a high-performance, modularized memory-matching game built with a "Performance-First" mindset. It serves as a showcase for modern Android development, demonstrating advanced Jetpack Compose UI/UX patterns, multi-module architecture, and robust local persistence using Room with KSP.

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack_Compose-Material_3-green.svg)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM_/_Modular-orange.svg)](https://developer.android.com/topic/architecture)

---

## 🏗 Architectural Overview

Flippy is engineered using a **Multi-Module Architecture**, ensuring strict separation of concerns, improved build times, and high testability. The project is divided into specialized layers:

- **`:app`**: The entry point, handling dependency orchestration and global navigation.
- **`:game-engine`**: Contains the core game state machine, tile-matching logic, and timer management.
- **`:auth`**: Encapsulates player identity management and Firebase authentication integrations.
- **`:database`**: A localized persistence layer using Room (via KSP) to manage match history and leaderboards.
- **`:core`**: Design system module containing Material 3 themes, typography, and shared UI components.
- **`:common`**: Shared utilities, constants, and global data models.

## 🚀 Technical Highlights

### 🎨 Advanced Compose UI & UX
- **Custom Canvas Graphics:** Features a `MeshBackground` that utilizes `InfiniteTransition` and `Canvas` to draw hardware-accelerated, animated gradients without the overhead of heavy assets.
- **Declarative Animations:** Heavy use of `animateFloatAsState` and `Spring` specs for haptic-like UI feedback on interactions (e.g., the reactive Play Button).
- **Glassmorphism:** Implements real-time UI blurring using `Modifier.blur()` and semi-transparent surfaces to achieve a modern, layered depth effect.

### ⚙️ Reactive Data Layer
- **Cold Flows & StateFlows:** The game engine exposes a `GameStatus` via `StateFlow` to the UI, ensuring a Unidirectional Data Flow (UDF) pattern.
- **Room + KSP:** Leverages Kotlin Symbol Processing (KSP) for compile-time optimized DAO implementations, reducing annotation processing overhead.
- **Async Streams:** Match history is streamed directly from the SQLite layer to the UI using `Flow<List<MatchHistory>>`, ensuring the leaderboard remains reactive and "live."

### 🛠 Engineering Excellence
- **Modularization:** Strict internal visibility controls to prevent leaking implementation details between feature modules.
- **Clean Code:** Adheres to SOLID principles, specifically focusing on Interface Segregation (DAOs) and Dependency Inversion (Repository patterns).
- **Resource Optimization:** Optimized `LazyVerticalGrid` and `LazyColumn` implementations with stable keys to minimize recompositions during intense gameplay.

## 📱 Features

- **Dynamic Difficulty:** Intelligent grid scaling and life-management based on player selection.
- **Live Leaderboard:** Local high-score tracking with optimized query limiters.
- **Responsive Gameplay:** Zero-latency tile flipping powered by Compose's state-backed Snapshot system.
- **Immersive Visuals:** High-fidelity animations including spring-physics buttons and mesh-gradient backgrounds.

## 🛠 Tech Stack

- **UI:** Jetpack Compose (Material 3)
- **Logic:** Kotlin Coroutines & Flow
- **Persistence:** Room Database
- **Build System:** Gradle Kotlin DSL (KTS)
- **Tooling:** KSP (Kotlin Symbol Processing)
- **Backend (Optional):** Firebase Auth / Firestore (Ready for sync)

---

### 👨‍💻 Author
**Sujoy** - *Android Engineer*

> "Building Flippy wasn't just about creating a game; it was about exploring the boundaries of declarative UI performance and modular scalability in the modern Android ecosystem."
