package com.stenleone.mysurface.ui.ext

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ConfigurationInfo
import android.content.res.Resources
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder

fun Context.supportES2(): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val configurationInfo: ConfigurationInfo = activityManager.deviceConfigurationInfo
    return configurationInfo.reqGlEsVersion >= 0x20000
}

fun Context.readTextFromRaw(resourceId: Int): String? {
    val stringBuilder = StringBuilder()
    try {
        var bufferedReader: BufferedReader? = null
        try {
            val inputStream: InputStream = this.resources.openRawResource(resourceId)
            bufferedReader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
                stringBuilder.append("\r\n")
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close()
            }
        }
    } catch (ioex: IOException) {
        ioex.printStackTrace()
    } catch (nfex: Resources.NotFoundException) {
        nfex.printStackTrace()
    }
    return stringBuilder.toString()
}