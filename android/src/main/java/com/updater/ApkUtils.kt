package com.updater

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException

object ApkUtils {
   @Throws(IOException::class)
    fun installApk(context: Context, apkFile: File) {
        val installAPKIntent = getApkInstallIntent(context, apkFile)
        context.startActivity(installAPKIntent)
    }

    @Throws(IOException::class)
    fun getApkInstallIntent(context: Context, apkFile: File): Intent {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val uri: Uri = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.update.provider",
                apkFile
            ).also {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } else {
            getApkUri(apkFile)
        }

        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        return intent
    }

    @Throws(IOException::class)
    private fun getApkUri(apkFile: File): Uri {
        val command = listOf("chmod", "777", apkFile.toString())
        val builder = ProcessBuilder(command)
        builder.start()

        return Uri.fromFile(apkFile)
    }
}
