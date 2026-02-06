package com.updater

import com.facebook.react.bridge.ReactApplicationContext

class UpdaterModule(reactContext: ReactApplicationContext) :
  NativeUpdaterSpec(reactContext) {

  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }

  companion object {
    const val NAME = NativeUpdaterSpec.NAME
  }
}
