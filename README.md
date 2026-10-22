# RemainderX ➗

> **"Know What's Left."**
> 
> A modern, high-precision, 100% offline Android utility app for calculating integer division quotients, remainders, mixed fractions, and step-by-step mathematical breakdowns.

---

## 🌟 Key Features

- **Authoritative High-Precision Calculations**: Powered by Java/Kotlin `BigInteger` and `BigDecimal` — supporting numbers up to **500 digits** with zero floating-point precision errors.
- **Quotient & Remainder**: Calculates truncating integer quotient ($Q$), exact remainder ($R$), and absolute remainder ($|R|$).
- **Mixed-Number Representation**: Displays exact fractional / mixed-number format ($Q \frac{R}{D}$).
- **High-Precision Decimal Expansion**: Custom decimal precision (0 to 10 decimal places) with configurable rounding modes (*Half Up*, *Half Even*, *Ceiling*, *Floor*, etc.).
- **Mathematical Verification**: Automatically validates every result against the fundamental theorem of division:
  $$\text{Divisor} \times \text{Quotient} + \text{Remainder} = \text{Dividend}$$
- **Step-by-Step Breakdown**: Generates clear, step-by-step multiplication and subtraction cards for educational clarity.
- **Visual Division Card**: Visual representation of the remainder relative to the divisor.
- **History Management**: Automatically saves past calculations (up to 10 entries) with copy, share, and clear functionality.
- **100% Private & Offline**: Zero internet permission, zero data collection, zero analytics, zero ads.
- **Material 3 Design**: Supports Light Mode, Dark Mode, and Android 12+ Dynamic Color palettes with optional haptic feedback.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.2.10
- **UI Framework**: Jetpack Compose with Material 3 Design
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture principles
- **State Management**: `StateFlow` & `collectAsState`
- **Asynchronous Execution**: Kotlin Coroutines
- **Navigation**: Navigation Compose
- **Data Persistence**: SharedPreferences (Settings & History)
- **Target SDK**: Android 16 (API 37) | **Min SDK**: Android 8.0 (API 26)

---

## 📂 Project Structure

```text
com.example.remainderx
├── data
│   └── repository          # SharedPreferences repositories for history & settings
├── domain
│   ├── math                # BigInteger/BigDecimal calculation engine (RemainderXCalculator)
│   ├── RemainderXFormatter # Formatting helpers
│   ├── RemainderXParser    # String parsing and sanitization
│   └── RemainderXValidator # Input validation logic
├── model                   # AppSettings, History, UI States, Calculation Steps
├── ui
│   ├── about               # About & Privacy Screen
│   ├── history             # Calculation History Screen
│   ├── home                # Main Input, Result Cards & Breakdown Screens
│   │   └── components      # ResultSection, CalculationStepsCard, DivisionVisualCard
│   ├── navigation          # NavHost & Navigation routes
│   ├── settings            # Customization & Preference Screen
│   ├── splash              # Animated Splash Screen
│   └── theme               # Color schemes, Typography & Material 3 Theme setup
└── util                    # Haptic feedback utilities
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- JDK 11 or higher
- Android SDK 37

### Building from Source

1. **Clone the repository**:
   ```bash
   git clone https://github.com/abdullah-ateeq/RemainderX.git
   cd RemainderX
   ```

2. **Open in Android Studio**:
   Open Android Studio and select **Open** -> Navigate to the cloned `RemainderX` directory.

3. **Build the project**:
   Use Gradle to build the debug APK:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run Unit Tests**:
   ```bash
   ./gradlew test
   ```

---

## 🧪 Unit Testing

RemainderX includes unit test suites covering:
- Exact quotient and remainder arithmetic with large integers and negative numbers.
- Verification equation checks.
- Decimal expansion scale and rounding mode behaviors.
- Input parsing and validation logic.

---

## 🔒 Privacy & Permissions

RemainderX takes privacy seriously:
- **No Internet Permission**: The app does not declare `<uses-permission android:name="android.permission.INTERNET"/>`.
- **Local Storage Only**: All user settings and calculation histories remain strictly on the local device.

---

## 📄 License

```text
Copyright (c) 2026 Abdullah Ateeq.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
