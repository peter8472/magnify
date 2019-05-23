
05/23 01:48:39: Launching app
$ adb push C:\Users\pmg\source\repos\Magnify\app\build\outputs\apk\debug\app-debug.apk /data/local/tmp/info.p445m.magnify
$ adb shell pm install -t -r "/data/local/tmp/info.p445m.magnify"
	pkg: /data/local/tmp/info.p445m.magnify
Success
APK installed in 49 s 433 ms
$ adb shell am start -n "info.p445m.magnify/info.p445m.magnify.MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
