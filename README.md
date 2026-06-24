# Jetpack Compose Pure Toastify (CustomInfoBar)

[![Release](https://jitpack.io/v/Coderkube-App/jetpack-compose-pure-toastify.svg)](https://jitpack.io/#Coderkube-App/jetpack-compose-pure-toastify)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)

A premium, fully customizable, pure Jetpack Compose Toast/Notification library. Unlike typical toast libraries, **Jetpack Compose Pure Toastify** operates directly within your Compose hierarchy, allowing for rich animations, custom background gradients/drawables, interactive swipe-to-perform actions, scroll-to-hide gestures, and built-in network connectivity monitoring.

---

## Features

- **Pure Jetpack Compose**: Formulated 100% in Jetpack Compose, integrating flawlessly with your composable hierarchy.
- **Rich Themes Out of the Box**: Ready-to-use themed info bars:
  - SuccessInfoBar
  - ErrorInfoBar
  - WarningInfoBar
  - OfflineInfoBar
  - SlideToPerformInfoBar
- **Dynamic Animations**: Slide vertically/horizontally, fade, scale, scale vertically, and expand/shrink.
- **Directional Presentation**: Slide or pop in from either the `Top` or `Bottom` of the screen.
- **Rich Text / Formatting**: Display formatted titles and descriptions using Compose `AnnotatedString` or regular `String`.
- **Advanced Custom Backgrounds**: Apply solid colors, linear/radial/sweep gradients (`Brush`), or image painters (SVG and PNG drawables).
- **Interactive Gestures**: Swipe to dismiss indefinte notifications, and slide-to-perform actions.
- **Scroll-To-Hide Integration**: Link with any `LazyListState` to automatically hide notifications on scroll and show them when scrolling back.
- **Network Monitoring**: Automatically monitors online/offline status and displays an offline banner when network drops.

---

## Installation

### 1. Add the JitPack Repository

#### Kotlin DSL (`settings.gradle.kts` / `build.gradle.kts`)

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

#### Groovy DSL (`settings.gradle` / `build.gradle`)

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### 2. Add the Dependency

Add the following to your application module's `build.gradle.kts` (or `build.gradle`):

```kotlin
dependencies {
    implementation("com.github.Coderkube-App:jetpack-compose-pure-toastify:1.0.1")
}
```

---

## Step-by-Step Implementation Guide

Follow these steps to integrate and display notifications in your application:

### Step 1: Wrap Your Content in `ComposeInfoHost`

The `ComposeInfoHost` manages the state and placement of the notifications overlay. It should wrap your screen content (e.g., inside your theme or screen composable):

```kotlin
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.ckpackage.custominfobar.main.ComposeInfoHost
import com.ckpackage.custominfobar.main.ComposeInfoHostState

@Composable
fun MainScreen() {
    // 1. Initialize and remember the Host State
    val composeInfoHostState = remember { ComposeInfoHostState() }

    Surface(modifier = Modifier.fillMaxSize()) {
        // 2. Wrap your layout inside the Host
        ComposeInfoHost(
            composeHostState = composeInfoHostState,
            enableNetworkMonitoring = true, // Optional: automatically handles offline state banner
        ) {
            // Your screen layout goes here
            MainContent(composeInfoHostState)
        }
    }
}
```

### Step 2: Triggering Notifications

To show a notification, use a Coroutine Scope to call `composeInfoHostState.show()`, or use the extension helper `showComposeInfoBar()`.

```kotlin
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.ckpackage.custominfobar.main.ComposeInfoDuration
import com.ckpackage.custominfobar.main.ComposeInfoHostState
import com.ckpackage.custominfobar.utils.showComposeInfoBar
import com.ckpackage.custominfobar.utils.toTextType

@Composable
fun MainContent(composeInfoHostState: ComposeInfoHostState) {
    val coroutineScope = rememberCoroutineScope()

    Column {
        Button(onClick = {
            // Display a temporary info bar
            coroutineScope.showComposeInfoBar(
                title = "Notification Title".toTextType(),
                description = "This is a detailed description of the event.".toTextType(),
                composeInfoHostState = composeInfoHostState,
                duration = ComposeInfoDuration.Short // Options: Short (4s), Long (10s), Indefinite
            )
        }) {
            Text("Show Notification")
        }
    }
}
```

---

## Detailed Examples

### 1. Themed InfoBars (Success, Error, Warning)

You can customize the layout of the notifications by using the slot overload of `ComposeInfoHost` to choose which InfoBar style to render.

```kotlin
var infoBarType by remember { mutableStateOf("success") }

ComposeInfoHost(
    composeHostState = composeInfoHostState,
    composeInfoBar = { infoData ->
        when (infoBarType) {
            "success" -> SuccessInfoBar(
                successData = infoData,
                onCloseClicked = { composeInfoHostState.dismiss() }
            )
            "error" -> ErrorInfoBar(
                errorData = infoData,
                onCloseClicked = { composeInfoHostState.dismiss() }
            )
            "warning" -> WarningInfoBar(
                warningData = infoData,
                onCloseClicked = { composeInfoHostState.dismiss() }
            )
        }
    }
) {
    // Screen contents...
}
```

### 2. Slide to Perform Action InfoBar

Perfect for double-confirming sensitive actions (like deleting or sending an item).

```kotlin
import com.ckpackage.custominfobar.defaultInfoBars.SlideToPerformInfoBar

ComposeInfoHost(
    composeHostState = composeInfoHostState,
    composeInfoBar = { infoData ->
        SlideToPerformInfoBar(
            actionText = "Swipe to Confirm".toTextType(),
            onActionDoneText = "Completed!".toTextType(),
            onSlideComplete = {
                // Execute logic
                composeInfoHostState.dismiss()
            }
        )
    }
) {
    // Screen contents...
}
```

### 3. Customized Backgrounds (Gradients & Drawables)

Pass a custom `Background` (Solid, Gradient, or Drawable image) to your `ComposeInfoBar`.

```kotlin
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.ckpackage.custominfobar.main.ComposeInfoBar
import com.ckpackage.custominfobar.main.toCustomBackground

// Gradient background InfoBar
ComposeInfoBar(
    title = "Gradient Notice".toTextType(),
    customBackground = Brush.horizontalGradient(
        colors = listOf(Color.Red, Color.Green, Color.Blue)
    ).toCustomBackground()
)

// SVG/PNG background InfoBar
val painter = painterResource(id = R.drawable.my_custom_background)
ComposeInfoBar(
    title = "Custom Artwork".toTextType(),
    customBackground = painter.toCustomBackground()
)
```

### 4. Rich Text (AnnotatedString)

```kotlin
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

val formattedTitle = buildAnnotatedString {
    withStyle(SpanStyle(color = Color.Yellow)) {
        append("Warning: ")
    }
    withStyle(SpanStyle(color = Color.White)) {
        append("System update ready.")
    }
}.toTextType()
```

### 5. Scroll-To-Hide Integration

Provide the `LazyListState` of your scrollable column to `ComposeInfoHost`. The InfoBar will elegantly slide away when scrolling down and reappear when scrolling back up.

```kotlin
val scrollState = rememberLazyListState()

ComposeInfoHost(
    composeHostState = composeInfoHostState,
    contentScrollState = scrollState // Pass your LazyListState here
) {
    LazyColumn(state = scrollState) {
        items(100) { index ->
            Text("List Item $index")
        }
    }
}
```

---

## API Configuration Options

### `ComposeInfoHost` Parameters

| Parameter | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `composeHostState` | `ComposeInfoHostState` | *Required* | State to control notification display lifecycle. |
| `direction` | `ComposeInfoBarDirection` | `Top` | Direction from which InfoBar enters (`Top`, `Bottom`). |
| `animationType` | `AnimationType` | `SlideVertically` | Animation type (`SlideVertically`, `SlideHorizontally`, `Fade`, `Scale`, `ScaleVertically`, `ExpandShrinkVertically`). |
| `contentScrollState` | `LazyListState?` | `null` | Scroll state to trigger the Auto Hide-on-Scroll feature. |
| `enableNetworkMonitoring` | `Boolean` | `false` | If true, auto-displays offline banners when the internet connection drops. |
| `isSwipeToDismissEnabled` | `Boolean` | `false` | Allows swiping to manually close indefinite banners. |

---

## Contributing

Contributions are always welcome! If you find a bug, have an idea for an enhancement, or want to add a feature, please feel free to make a pull request:

1. **Fork** the repository.
2. **Create** a branch for your feature (`git checkout -b feature/amazing-feature`).
3. **Commit** your changes (`git commit -m 'Add amazing feature'`).
4. **Push** to the branch (`git push origin feature/amazing-feature`).
5. **Open** a Pull Request.

Make sure to format code properly and verify all tests compile and pass before submitting.

---

## License

This project is licensed under the **Apache License 2.0**. See the [LICENSE](file:///Users/macbook/Workspace/jetpack-compose-pure-toastify/LICENSE) file for details.

```text
Copyright 2026 Coderkube-App

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
