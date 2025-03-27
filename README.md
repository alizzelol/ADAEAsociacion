## Introduction

This mobile application, developed in Android Studio with Kotlin, utilizes Firestore Database and Firebase Authentication to manage users, events, and communication through a private chat. The app is designed to facilitate interaction between administrators/teachers and parents, enabling event management and direct communication.

## How was the app made?

The application was developed using:

* **Android Studio**: The official integrated development environment (IDE) for Android application development.
* **Kotlin**: The programming language used for the application development.
* **Firebase**:
    * **Firestore Database**: For cloud data storage and management.
    * **Firebase Authentication**: For user authentication and role management (admin/teacher and parent).

## How to access the app?

To configure access to the Firestore database, follow these steps:

1.  Add the Google Services plugin in the project's `build.gradle`:

    ```gradle
    plugins {
        id("com.google.gms.google-services") version "4.4.2" apply false
    }
    ```
2.  Add the necessary plugins and dependencies in the module's `:app` `build.gradle`:

    ```gradle
    plugins {
        id("com.google.gms.google-services")
        id("kotlin-parcelize")
    }

    dependencies {
        implementation(platform("com.google.firebase:firebase-bom:33.10.0")) // Check the latest version
        implementation 'com.google.firebase:firebase-analytics'
        implementation 'com.google.firebase:firebase-auth'
        implementation 'com.google.firebase:firebase-firestore:24.6.0'
    }
    ```
3.  Add the `google-services.json` file:
    * Download the `google-services.json` file from your project's Firebase console.
    * Copy the downloaded file and paste it into the `app` folder of your Android Studio project. Ensure the file name is exactly `google-services.json`.

Access to the application is through a login system that distinguishes between user roles: administrator/teacher and parent.

## Objective

The main objective of this application is:

* Facilitate communication between administrators/teachers and parents through a private chat.
* Manage and visualize events (workshops and courses) through a functional calendar.
* Allow parents to register for events and manage their profile.
* Allow administrators to manage users and events.

## Structure and functionalities

The application is divided into two main sections:

* **Calendar**:
    * Displays scheduled events (workshops and courses) by color.
    * Allows administrators to add, edit, and delete events.
    * Allows parents to register for events and view their registered events.
* **Private Chat**:
    * Enables one-to-one communication between users.
    * Displays a list of active conversations.
    * Allows starting new conversations from a contact list.

### Functionalities by role:

* **Administrator/Teacher**:
    * Full user management (add, edit, list).
    * Full event management (add, edit, delete, list).
    * View parents registered for events.
    * Access to private chat.
* **Parent**:
    * User profile management (password change priority).
    * View courses and workshops.
    * Register for events.
    * View registered events.
    * Access to private chat.

## How to contribute

Contributions are welcome. You can contribute to the project by downloading the [apk](https://drive.google.com/drive/folders/1_rwPKR3B9kORRH8pdzwG72NO8YK61dJI)
