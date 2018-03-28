package galaxy.qiitaviewer

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.support.v4.content.FileProvider
import android.widget.Toast
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Created by galaxy on 2018/03/16.
 */
class Updater(context: Context, url: String) {
    private var context: Context = context
    private lateinit var currentVersionNme: String
    private var currentVersionCode: Int = 0
    private lateinit var updateEventListener: UpdateEventListener
    private lateinit var updateCheckUrl: String
    private var tempApkFile: File? = null
    private var updateApkFileUrl: String = url
    private var latestVersionName: String? = null
    private var latestVersionCode: Int = 0
    private var updateDescription: String? = null
    private var updateMessage: String? = null

    fun Updater(context: Context, url: String) {
        this.context = context
//        updateCheckUrl = context.getString(R.string.update_url)
        getCurrentVersion()
    }

    fun setListener(updateEventListener: UpdateEventListener) {
        this.updateEventListener = updateEventListener
    }

    fun getCurrentVersion() {
        val packageInfo = context?.packageManager?.getPackageInfo(context?.packageName, PackageManager.GET_META_DATA) as PackageInfo
        currentVersionCode = packageInfo.versionCode
        currentVersionNme = packageInfo.versionName
    }

    fun checkUpdate() {
        UpdateCheckTask().execute()
    }

    inner class UpdateCheckTask : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String): String? {
            getCurrentVersion()
            getLatestVersion()
            if (currentVersionCode < latestVersionCode)
                return updateMessage
            else {
                if (updateMessage == null)
                    updateMessage = "App is up to date"
            }
            return updateMessage
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            updateEventListener.onCheckFinished(result)
            Toast.makeText(context, updateMessage, Toast.LENGTH_LONG).show();
        }
    }

    fun getCurrentVersionName(): String? {
        return currentVersionNme
    }

    fun getCurrentVersionCode(): Int {
        return currentVersionCode
    }

    fun getLatestVersion() {
        try {
            val map = parseXml("")
            latestVersionCode = Integer.parseInt(map["versionCode"])
            latestVersionName = map["versionName"]
            updateDescription = map["description"]
            updateApkFileUrl = map["url"]!!
        } catch (e: Exception) {
            updateMessage = "Failed to check update"
        }
    }

    private fun parseXml(url: String?): HashMap<String, String> {
        val map: HashMap<String, String> = HashMap()

        val inputStream = URL(url).openConnection().getInputStream().bufferedReader().readText()
        val root = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream).documentElement

        if (root.tagName == "update") {
            val nodeList = root.childNodes as NodeList
            var i = 0
            do {
                val node = nodeList.item(i)
                if (node.nodeType == Element.ELEMENT_NODE) {
                    val element = node as Element
                    map.put(element.tagName, element.textContent.trim())
                }
                i++
            } while (i < nodeList.length)
        }
        return map
    }

    fun getLatestVersionName(): String? {
        return latestVersionName
    }

    fun getLatestVersionCode(): Int {
        return latestVersionCode
    }

    fun getUpdateDescription(): String? {
        return updateDescription
    }

    fun downloadApkFile() {
        DownloadApkTask().execute()
    }

    inner class DownloadApkTask : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void): Boolean? {
            tempApkFile = File.createTempFile("apk", ".apk", context.externalCacheDir)
            return uriToFile(updateApkFileUrl, tempApkFile)
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            if (result) {
                updateEventListener.onDownloadApkComplete()
            } else {
                updateEventListener.onDownloadApkFailed()
            }
        }
    }

    fun uriToFile(url: String?, file: File?): Boolean {
        return urlToOutputStream(url, FileOutputStream(file))
    }

    private fun urlToOutputStream(url: String?, outputStream: FileOutputStream):Boolean {
        try {
            val urlConnection = URL(url).openConnection()
            urlConnection.getRequestProperty("GET")
            urlConnection.doOutput = true
            urlConnection.connect()

            val inputStream = urlConnection.getInputStream()
            val buffer = ByteArray(1024)
            var bufferLength = 0

            while ({ bufferLength = inputStream.read(buffer); bufferLength }() > 0) {
                outputStream.write(buffer, 0, bufferLength)
            }
            outputStream.close()
            inputStream.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun installApk() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //Android 7.0以降の端末ではこう書かないと落ちる
            context.startActivity(Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
                data = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".provider", tempApkFile!!)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            })
        } else {
            //逆に6.0以下ではこのままじゃないと動かない
            context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.fromFile(tempApkFile), "appliation/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }

    fun deleteTempApkFile() {
        for (file in context.externalCacheDir.listFiles()) {
            if (file.name.endsWith(".apk"))
                file.delete()
        }
    }

    interface UpdateEventListener {
        fun onCheckFinished(result: String?)

        fun onDownloadApkComplete()

        fun onDownloadApkFailed()
    }
}