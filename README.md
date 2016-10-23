# Temerarious Thirteens - Project 1 for CSE-4100

## How to start

* For the frontend you can either import the _ThinClientG13-mobile_ project into Android Studio and build the APK or simply install the APK [from this folder](https://git.niksula.hut.fi/cs-e4100/mcc-2016-g13-p1/tree/master/ThinClientG13-mobile/app/build/outputs/apk).


## In this repository you will find

### 'ThinClientG13-mobile'

Includes the Android application source code.

The code uses the *MultiVNC* application as a starting point. In the folder _app/src/main/java/com/_ you will find _antlersoft_ and _coboltforge_ which come from the original *MultiVNC* application. Our code is in the _mccG13_ folder.

Classes named as _\*BW.java_ refer to background async tasks for interacting with the server. The login view and the app selection view are controlled respectively by the classes _MainActivity.java_ and _AppSelectionActivity.java_.

In addition, as the server IP can change, you have the option to set it from the preferences menu on the top right corner of the login view.

### 'Server'
Includes the socket and cloud connections for the application


### 'Documentation'
Includes any other relevant file for the understanding of the application
