package com.bairock.hamadev.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import com.bairock.hamadev.database.Config
import java.io.File

class DownloadReceiver : BroadcastReceiver() {

    companion object {
        var APP_NAME : String? = null
    }
    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            if(null == APP_NAME){
                return
            }
            val myDwonloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val refernece = Config.getDownloadId(context)
            if (refernece == myDwonloadID) {
                //val dManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                //val install = Intent(Intent.ACTION_VIEW)

//                val downloadFileUri = dManager.getUriForDownloadedFile(myDwonloadID)
                val file = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS + File.separator +APP_NAME)
                if (file != null) {
                    install(context)
                }
            }
        }
    }

    private fun install(context: Context){
        val downloadManager = context
                .getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query()
        query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL)
        val c = downloadManager.query(query)
        // 获取文件名并开始安装
        if (c.moveToFirst()) {
            var fileName = c.getString(c
                    .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
            fileName = fileName.replace("file://", "")
            val file = File(fileName)
            val intent1 = Intent(Intent.ACTION_VIEW)
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent1.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive")
            context.startActivity(intent1)
        }
    }
}
