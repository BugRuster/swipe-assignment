# Swipe Assignment

Overview

This project is an Android application developed using Kotlin and follows the Model-View-ViewModel (MVVM) architectural pattern. It leverages modern Android development practices and Jetpack libraries to create a robust, testable, and maintainable codebase.

## Technologies Used

* **Kotlin:** The primary programming language for the project.
* **MVVM (Model-View-ViewModel):** The architectural pattern used to separate the UI logic from the business logic.
* **Jetpack Libraries:** A suite of libraries to help follow best practices, reduce boilerplate code, and write code that works consistently across Android versions.

## MVVM Architecture

This project is structured around the MVVM pattern, which consists of three main components:

* **Model:** Represents the data and business logic of the application. It's responsible for data access, manipulation, and persistence.
* **View:** The UI layer that displays data to the user and handles user interactions. In Android, this is typically represented by Activities, Fragments, and their associated layouts.
* **ViewModel:** Acts as an intermediary between the View and the Model. It exposes data streams that the View can observe and handles UI-related logic.

### Benefits of MVVM

* **Separation of Concerns:** Clearly separates UI logic from business logic, making the code more organized and easier to understand.
* **Testability:** ViewModels can be easily unit-tested without needing to interact with the UI.
* **Maintainability:** Changes to the UI or business logic are less likely to affect other parts of the application.
* **Reusability:** ViewModels can be reused across different Views, reducing code duplication.

## Folder Structure

The project follows a clear and organized folder structure to enhance maintainability and scalability.

```
└── 📁main
    └── 📁java
        └── 📁com
            └── 📁example
                └── 📁testproject
                    └── App.kt
                    └── 📁data
                        └── 📁local
                            └── AppDatabase.kt
                            └── 📁dao
                                └── ProductDao.kt
                            └── 📁entity
                                └── ProductEntity.kt
                        └── 📁model
                            └── AddProductResponse.kt
                            └── Product.kt
                            └── ProductResponse.kt
                        └── 📁remote
                            └── ProductApi.kt
                        └── 📁repository
                            └── ProductRepository.kt
                    └── 📁di
                        └── DatabaseModule.kt
                        └── NetworkModule.kt
                    └── 📁domain
                        └── 📁model
                            └── Product.kt
                    └── MainActivity.kt
                    └── 📁presentation
                        └── 📁addproduct
                            └── 📁view
                                └── AddProductBottomSheet.kt
                            └── 📁viewmodel
                                └── AddProductViewModel.kt
                        └── 📁home
                            └── 📁adapter
                                └── ProductAdapter.kt
                            └── 📁view
                                └── HomeFragment.kt
                            └── 📁viewmodel
                                └── HomeViewModel.kt
                    └── 📁shared
                        └── SharedViewModel.kt
                    └── 📁ui
                        └── 📁theme
                            └── Color.kt
                            └── Theme.kt
                            └── Type.kt
                    └── 📁utils
                        └── ConnectivityObserver.kt
                        └── FileUtils.kt
                        └── NetworkBoundResource.kt
                        └── NetworkUtils.kt
                        └── Resource.kt
                    └── 📁worker
                        └── SyncWorker.kt
    └── 📁res
        └── 📁drawable
            └── bg_product_type.xml
            └── ic_add.xml
            └── ic_image.xml
            └── ic_launcher_background.xml
            └── ic_launcher_foreground.xml
            └── ic_search.xml
        └── 📁font
            └── inter_medium.ttf
            └── inter_regular.ttf
            └── inter_semibold.ttf
            └── inter.xml
        └── 📁layout
            └── activity_main.xml
            └── bottom_sheet_add_product.xml
            └── fragment_home.xml
            └── item_product.xml
        └── 📁mipmap-anydpi-v26
            └── ic_launcher_round.xml
            └── ic_launcher.xml
        └── 📁mipmap-hdpi
            └── ic_launcher_round.webp
            └── ic_launcher.webp
        └── 📁mipmap-mdpi
            └── ic_launcher_round.webp
            └── ic_launcher.webp
        └── 📁mipmap-xhdpi
            └── ic_launcher_round.webp
            └── ic_launcher.webp
        └── 📁mipmap-xxhdpi
            └── ic_launcher_round.webp
            └── ic_launcher.webp
        └── 📁mipmap-xxxhdpi
            └── ic_launcher_round.webp
            └── ic_launcher.webp
        └── 📁navigation
            └── nav_graph.xml
        └── 📁values
            └── colors.xml
            └── strings.xml
            └── themes.xml
        └── 📁xml
            └── backup_rules.xml
            └── data_extraction_rules.xml
    └── AndroidManifest.xml
```

### Directory Breakdown

* **`app/`**: Contains the main application code.
  * **`src/main/`**: Contains the main source code and resources.
    * **`java/com/example/testproject/`**: The root package for the application's Kotlin code.
      * **`data/`**: Contains classes related to data handling.
        * **`model/`**: Data classes representing the application's data structures (e.g., `User.kt`).
        * **`repository/`**: Classes that handle data access and business logic (e.g., `UserRepository.kt`).
      * **`di/`**: Contains classes related to dependency injection.
        * **`AppModule.kt`**: Module for dependency injection.
      * **`ui/`**: Contains classes related to the user interface.
        * **`activity/`**: Activities (e.g., `MainActivity.kt`).
        * **`fragment/`**: Fragments (e.g., `HomeFragment.kt`).
        * **`viewmodel/`**: ViewModels (e.g., `UserViewModel.kt`).
      * **`utils/`**: Utility classes and helper functions (e.g., `Constants.kt`).
      * **`App.kt`**: Application class.
    * **`res/`**: Resources for the application.
      * **`layout/`**: XML files defining the UI layouts (e.g., `activity_main.xml`, `fragment_home.xml`).
      * **`values/`**: XML files defining resources like colors, strings, and themes.
  * **`src/test/`**: Contains unit tests.
* **`build.gradle.kts`**: Build files for the project and app module.

## Getting Started

1. **Clone the repository:** `git clone https://github.com/BugRuster/swipe-assignment.git`
2. **Open in Android Studio:** Open the `testproject` directory in Android Studio.
3. **Build and Run:** Build the project and run it on an emulator or physical device.
