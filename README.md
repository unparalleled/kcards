# Kcards
Korean flash cards

This app was originally developed for learning Korean, but now supports the following languages:
* Chinese
* English
* French
* German
* Italian
* Japanese
* Korean

see [Language.java](https://github.com/unparalleled/kcards/blob/master/app/src/main/java/com/mrkevinthomas/kcards/models/Language.java)

## Build Tools
* Android Studio 2.2.3+
* JRE 1.8.0_112+

## Project Setup
1. Download ZIP and unzip OR use terminal: `git clone https://github.com/unparalleled/kcards.git`
2. Open Android Studio to first window titled _Welcome to Android Studio_ 
3. Select __Open an existing Android Studio project__
4. Find the directory and click __OK__
5. You should expect __BUILD FAILED__ with an error _File google-services.json is missing_
6. Setup Firebase

## Firebase Setup
1. Sign in (or sign up) for a Google account
2. [Open the Firebase Console](https://console.firebase.google.com/)
3. Click __CREATE NEW PROJECT__
4. Use anything for the _Project name_
5. Click __CREATE PROJECT__
6. On the overview screen, click __Add Firebase to your Android app__
7. For package name, use `com.mrkevinthomas.kcards`
8. Click __ADD APP__ which downloads `google-services.json`, but delete it because it's missing the api key
9. Don't worry about updating `build.gradle` because the google services plugin is already applied
10. Go to __Firebase Console -> Authentication__, click __SIGN_IN METHOD__ tab, and enable _Anonymous_
11. Go to __Firebase Console -> Settings__ and _Download the latest config file_
12. Place the file here: `app/google-services.json`
13. In Android Studio, go to __Build -> Rebuild Project__
14. Run!
