# Milliscope

Milliscope is a simple Android sample app built with Kotlin and Jetpack Compose.
It tracks how long each item in a scrollable list remains visible on screen and
updates the visible time in real time.

Created as a learning exercise to explore Jetpack Compose and Android development, specifically for
testing the [visibility modifiers released in Compose UI 1.9](https://android-developers.googleblog.com/2025/08/whats-new-in-jetpack-compose-august-25-release.html).

|Light|Dark|
|-|-|
|<img width="1466" height="3101" alt="Screenshot_20250910_180121" src="https://github.com/user-attachments/assets/0002c6f6-b113-4124-b2e2-5f54075eeedd" />|<img width="1466" height="3101" alt="Screenshot_20250910_180127" src="https://github.com/user-attachments/assets/ba1f5b2f-2bff-4338-97f9-c8399dfc1b63" />|

## Branches

The project explores two approaches for tracking item visibility:

- **Snapshot branch** – uses `Snapshot` observers to listen for visibility changes.
- **Visibility modifiers branch** – experiments with the new APIs described in the [August 25, 2025 Jetpack Compose release](https://android-developers.googleblog.com/2025/08/whats-new-in-jetpack-compose-august-25-release.html).  
  These methods skip visibility notifications in my experience here, leading to inconsistent updates.