package com.updater

import android.app.IntentService
import android.content.Context
import android.content.Intent

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class DownloadService : IntentService("DownloadService") {
    private val BUFFER_SIZE = 10 * 1024
    private lateinit var mContext: Context
    private var isFirstConnection = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mContext = applicationContext
        isFirstConnection = true
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onHandleIntent(intent: Intent?) {
        val urlStr = intent?.getStringExtra("url")

        var inStream: InputStream? = null
        var outStream: FileOutputStream? = null

        try {
            var urlStrNotNull = urlStr ?: ""
            while (!downloadFile(urlStrNotNull, !isFirstConnection)) {
                isFirstConnection = false
            }
            if (urlStr != null) {
                val apkFileName = urlStr.substring(urlStr.lastIndexOf("/") + 1)
                val apkFile = File(this.getCacheDir(), apkFileName);

                ApkUtils.installApk(this, apkFile);
            }
          
        } catch (e: Exception) {
        } finally {
            inStream?.close()
            outStream?.close()
        }
    }

    private fun downloadFile(urlStr: String, resumeDownload: Boolean): Boolean {
        var inStream: InputStream? = null
        var outStream: FileOutputStream? = null

        try {
            val url = URL(urlStr)
            val urlConnection = url.openConnection() as HttpURLConnection

            urlConnection.requestMethod = "GET"
            urlConnection.doOutput = false
            urlConnection.connectTimeout = 10 * 1000
            urlConnection.readTimeout = 10 * 1000
            urlConnection.setRequestProperty("Connection", "Keep-Alive")
            urlConnection.setRequestProperty("Charset", "UTF-8")
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate")

            var bytesum: Long = 0
            var byteread: Int

            val dir = this.getCacheDir()
            val apkName = urlStr.substring(urlStr.lastIndexOf("/") + 1)
            val apkFile = File(dir, apkName)

            if (apkFile.exists() && resumeDownload) {
                val downloadedBytesLength = apkFile.length()
                bytesum = downloadedBytesLength

                urlConnection.setRequestProperty("Range", "bytes=$downloadedBytesLength-")

                outStream = FileOutputStream(apkFile, true)
            } else {
                outStream = FileOutputStream(apkFile)
            }

            urlConnection.connect()

            val bytetotal = urlConnection.contentLength + bytesum
            val status = urlConnection.responseCode


            if (status != 416) {
                inStream = urlConnection.inputStream
                val buffer = ByteArray(BUFFER_SIZE)


                while (inStream.read(buffer).also { byteread = it } != -1) {
                    bytesum += byteread
                    outStream.write(buffer, 0, byteread)

                }
            }
            return true
        } catch (e: Exception) {
            if (e.message?.toLowerCase()?.contains("connection reset") == true) {
                return false
            }
            throw e
        } finally {
            outStream?.close()
            inStream?.close()
        }
    }
}
