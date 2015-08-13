# Rx4Square

Rx4Square is a tiny Android experiment around the Foursquare API using RxJava and Kotlin.

The idea of the app is quite simple, it simply fetches your current location and display venues around you, ordered by distance.

### Dependencies

- RxAndroid: The Android flavored RxJava -> https://github.com/ReactiveX/RxAndroid
- Kotlin as coding language: http://kotlinlang.org
- Foursquare API: https://developer.foursquare.com/
- OkHttp: http://square.github.io/okhttp/
- Picasso: http://square.github.io/picasso/
- Support V4, V7 and RecyclerView
- Google Play Services

### How to build

The only thing you need to configure is your Foursquare API credentials. Simply create a file named _config.gradle_ in the _/Rx4Square/app_ directory out of the _config.gradle.sample_ file provided. Open it and put your client ID and SECRET.

The rest is a simple Android Studio project so you only need to open it and build.
