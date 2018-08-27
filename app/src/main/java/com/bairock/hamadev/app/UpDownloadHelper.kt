package com.bairock.hamadev.app

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Handler
import com.bairock.hamadev.database.Config
import com.bairock.hamadev.database.SdDbHelper
import com.fasterxml.jackson.databind.ObjectMapper
import java.net.HttpURLConnection
import java.net.URL
import java.io.*


/**
 * 上传下载辅助类
 */
class UpDownloadHelper(private val handler: Handler) {

    fun convertStreamToString(inputStream: InputStream): String {
        val reader = BufferedReader(InputStreamReader(inputStream))
        val sb = StringBuilder()
        var line: String?
        try {
            do {
                line = reader.readLine()
                sb.append(line!! + "/n")
            }while (line != null)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }


    fun uploadUser(){
        UploadTask().execute()
    }

    fun downloadUser(userName: String){
        DownloadTask(userName).execute()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class UploadTask() : AsyncTask<Void, Void, Result<*>>() {
        val uploadLoadUrl = "http://" + Config.serverName + "/user/userUpload"

        override fun doInBackground(vararg params: Void): Result<*> {
            val result = Result<Any>()
            var inputStream: InputStream? = null
            var urlConnection: HttpURLConnection? = null
            try {
                val url = URL(uploadLoadUrl)
                urlConnection = url.openConnection() as HttpURLConnection
                /* optional request header */
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                /* optional request header */
                urlConnection.setRequestProperty("Accept", "application/json")
                // read response
                /* for Get request */
                urlConnection.requestMethod = "POST"
                urlConnection.doOutput = true
                val wr = DataOutputStream(urlConnection.outputStream)

                wr.writeBytes(getUserJson())
                wr.flush()
                wr.close()
                // try to get response
                val statusCode = urlConnection.responseCode
                if (statusCode == 200) {
                    inputStream = BufferedInputStream(urlConnection.inputStream)
                    val response = convertStreamToString(inputStream)
                    val mapper = ObjectMapper()
                    return mapper.readValue(response, Result::class.java)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                result.code = -1
                result.message = e.message
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                urlConnection?.disconnect()
            }
            return result
        }

        override fun onPostExecute(result: Result<*>) {
            if(result.code == 0){
                handler.obtainMessage(MainActivity.UPLOAD_OK).sendToTarget()
            }else{
                handler.obtainMessage(MainActivity.UPLOAD_FAIL, result.message).sendToTarget()
            }
        }

        private fun getUserJson(): String? {
            var json: String? = null
            val user = SdDbHelper.getDbUser()
            if (null != user) {
                val mapper = ObjectMapper()
                try {
                    json = mapper.writeValueAsString(user)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            return json
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class DownloadTask(userName : String) : AsyncTask<Void, Void, Result<*>>() {
        val downLoadUrl = "http://" + Config.serverName + "/user/userDownload/$userName"

        override fun doInBackground(vararg params: Void): Result<*> {
            val result = Result<Any>()
            var inputStream: InputStream? = null
            var urlConnection: HttpURLConnection? = null
            try {
                val url = URL(downLoadUrl)
                urlConnection = url.openConnection() as HttpURLConnection
                /* optional request header */
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                /* optional request header */
                urlConnection.setRequestProperty("Accept", "application/json")
                // read response
                /* for Get request */
                urlConnection.requestMethod = "GET"
                urlConnection.doOutput = true
                urlConnection.connect()
                // try to get response
                val statusCode = urlConnection.responseCode
                if (statusCode == 200) {
                    inputStream = BufferedInputStream(urlConnection.inputStream)
                    val response = convertStreamToString(inputStream)
                    val mapper = ObjectMapper()
                    return mapper.readValue(response, Result::class.java)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                result.code = -1
                result.message = e.message
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                urlConnection?.disconnect()
            }
            return result
        }

        override fun onPostExecute(result: Result<*>) {
            if(result.code == 0){
                handler.obtainMessage(MainActivity.DOWNLOAD_OK).sendToTarget()
            }else{
                handler.obtainMessage(MainActivity.DOWNLOAD_FAIL, result.message).sendToTarget()
            }
        }
    }
}