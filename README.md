# cordova-support-android-plugin<br>[![NPM version][npm-version]][npm-url] [![NPM downloads][npm-downloads]][npm-url] [![Twitter][twitter-follow]][twitter-url]
<b>NOS</b>: Dependency of [cordova-plugin-firebase-analytics-os](https://github.com/OutsystemsNOS/cordova-plugin-firebase-analytics-os)</br></br>
The plugin introduces new base class for Android Cordova plugins called `ReflectiveCordovaPlugin` that extends `CordovaPlugin` and allows to reduce boilerplate code. Please read links below to understand new capabilities:
* [Default implementation of `execute`](https://github.com/chemerisuk/cordova-support-android-plugin/wiki/Default-implementation-of-execute)
* [Argument binding](https://github.com/chemerisuk/cordova-support-android-plugin/wiki/Argument-binding)
* [Asynchronous execution](https://github.com/chemerisuk/cordova-support-android-plugin/wiki/Asynchronous-execution)

## ProGuard notes
If you obfuscate app with ProGuard then `proguard-rules.pro` usually contains rules:

```
-keep class org.apache.cordova.* { *; }
-keep class org.apache.cordova.engine.* { *; }
-keep public class * extends org.apache.cordova.CordovaPlugin
```

`ReflectiveCordovaPlugin` uses method names to match an appropriate action. Therefore you should keep names for methods with `@CordovaMethod` annotation:

```
-keepclassmembers class ** {
  @by.chemerisuk.cordova.support.CordovaMethod *;
}
-keep public enum by.chemerisuk.cordova.support.ReflectiveCordovaPlugin$** {
    **[] $VALUES;
    public *;
}
```

[npm-url]: https://www.npmjs.com/package/cordova-support-android-plugin
[npm-version]: https://img.shields.io/npm/v/cordova-support-android-plugin.svg
[npm-downloads]: https://img.shields.io/npm/dm/cordova-support-android-plugin.svg
[twitter-url]: https://twitter.com/chemerisuk
[twitter-follow]: https://img.shields.io/twitter/follow/chemerisuk.svg?style=social&label=Follow%20me
