# Milliscope

Milliscope is a simple Android sample app built with Kotlin and Jetpack Compose.
It tracks how long each item in a scrollable list remains visible on screen and
updates the visible time in real time.

Created as a learning exercise to explore Jetpack Compose and Android development, specifically for
testing
the [visibility modifiers released in Compose UI 1.9](https://android-developers.googleblog.com/2025/08/whats-new-in-jetpack-compose-august-25-release.html).

| Light                                                                                                                                                     | Dark                                                                                                                                                      |
|-----------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| <img width="1466" height="3101" alt="Screenshot_20250915_115451" src="https://github.com/user-attachments/assets/8bd1ce02-ea24-4529-9f65-71f5582ca3e6" /> | <img width="1466" height="3101" alt="Screenshot_20250915_115457" src="https://github.com/user-attachments/assets/9136abec-a924-4790-921c-038f5592dcc4" /> |


## Branches

The project explores two approaches for tracking item visibility:

- **Snapshot branch** – uses `Snapshot` observers to listen for visibility changes.
- **Visibility modifiers branch** – experiments with the new APIs described in
  the [August 25, 2025 Jetpack Compose release](https://android-developers.googleblog.com/2025/08/whats-new-in-jetpack-compose-august-25-release.html).  
  These methods skip visibility notifications in my experience here, leading to inconsistent
  updates. I didn't manage to make them work consistently, but the code is still worth a look.
  Submit a PR if you know how to make them work!
