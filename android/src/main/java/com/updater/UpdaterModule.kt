package com.updater

import com.facebook.react.bridge.Promise
import android.app.Activity
import android.content.Intent
import com.facebook.react.bridge.ReactApplicationContext
import android.content.pm.PackageManager.NameNotFoundException

class UpdaterModule(reactContext: ReactApplicationContext) :
  NativeUpdaterSpec(reactContext) {


  override fun getVersionCode(promise: Promise) {
    try {
      val reactContexte = getReactApplicationContext()
    val versionCode = reactContexte.getPackageManager().getPackageInfo(reactContexte.getPackageName(), 0).versionCode
    promise.resolve(versionCode)
    }
    catch(e: NameNotFoundException) {
     promise.reject("NameNotFoundException", e)
    }
  }

  override fun downloadApp(apkUrl: String) {
     val activity: Activity? = getCurrentActivity()

    if (activity == null) {
        return
    }
    val intent = Intent(getReactApplicationContext(), DownloadService::class.java)
    intent.putExtra("url", apkUrl)
    activity.startService(intent)
  }

  companion object {
    const val NAME = NativeUpdaterSpec.NAME
  }
}
