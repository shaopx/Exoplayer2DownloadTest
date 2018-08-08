package com.spx.exoplayer2downloadtest

import android.app.Notification
import android.util.Log
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.PlatformScheduler
import com.google.android.exoplayer2.ui.DownloadNotificationUtil
import com.google.android.exoplayer2.util.NotificationUtil
import com.google.android.exoplayer2.util.Util


/** A service for downloading media.  */
class DemoDownloadService : DownloadService(FOREGROUND_NOTIFICATION_ID, DownloadService.DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL, CHANNEL_ID, R.string.exo_download_notification_channel_name) {
    val TAG:String = "DemoDownloadService"
    override fun getDownloadManager(): DownloadManager {
        return DownloadMgr.getDownloadManager()!!
    }


    override fun getScheduler(): PlatformScheduler? {
        return if (Util.SDK_INT >= 21) PlatformScheduler(this, JOB_ID) else
        /* contentIntent= */ null
    }

    override fun getForegroundNotification(taskStates: Array<DownloadManager.TaskState>): Notification {
        return DownloadNotificationUtil.buildProgressNotification(
                /* context= */ this,
                R.drawable.exo_controls_play,
                CHANNEL_ID, null, null,
                taskStates)/* contentIntent= *//* message= */
    }

    override fun onTaskStateChanged(taskState: DownloadManager.TaskState?) {
        Log.d(TAG, "onTaskStateChanged ... state:"+taskState!!.state)
        taskState?.run {
            Log.d(TAG, "onTaskStateChanged ... uri:${action.uri}")
            Log.d(TAG, "onTaskStateChanged ... " +
                    "taskId:$taskId, state:$state, downloadPercentage:$downloadPercentage, downloadedBytes:$downloadedBytes")
        }
        if (taskState!!.action.isRemoveAction) {
            return
        }
        var notification: Notification? = null
        if (taskState.state == DownloadManager.TaskState.STATE_COMPLETED) {
            notification = DownloadNotificationUtil.buildDownloadCompletedNotification(
                    /* context= */ this,
                    R.drawable.exo_controls_play,
                    CHANNEL_ID, null,
                    Util.fromUtf8Bytes(taskState.action.data))/* contentIntent= */
            showToast("${taskState.action.uri}下载成功")
        } else if (taskState.state == DownloadManager.TaskState.STATE_FAILED) {
            notification = DownloadNotificationUtil.buildDownloadFailedNotification(
                    /* context= */ this,
                    R.drawable.exo_controls_play,
                    CHANNEL_ID, null,
                    Util.fromUtf8Bytes(taskState.action.data))
        }
        val notificationId = FOREGROUND_NOTIFICATION_ID + 1 + taskState.taskId
        NotificationUtil.setNotification(this, notificationId, notification)
    }

    companion object {

        private val CHANNEL_ID = "download_channel"
        private val JOB_ID = 1
        private val FOREGROUND_NOTIFICATION_ID = 1
    }
}
