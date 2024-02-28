package app.accrescent.client.util

import android.os.Build

fun isSdkVersionCompatible(minSdkVersion: Int): Boolean {
    return Build.VERSION.SDK_INT >= minSdkVersion
}