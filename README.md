OlydorfApp - Android version
===========================

In this repository we keep the source code for our Android version of the OlydorfApp.

Development
-----------

The current version is developed on Android Studio 2.1.3 with a targeted SDK of 23 and a minimum required SDK version of 16 (Android 4.1).
It uses the version 2.1.3 of the Android Gradle plugin.

Build
-----
For a local build you need to place a valid client certificate file (do **not** push it) into the `assets` folder.
The name of this file as well as its decryption key need to be placed within `eu.olynet.olydorfapp.resource.Configuration`.

For a release build you also need the contents of the `olydorfapp-android-build` repository.
It takes care of automatically generating a valid client certificate as well as the Rest configuration.

License
-------
This project is released under version 3 of the GNU General Public License.
