package com.spx.exoplayer2downloadtest

import android.app.Application
import android.util.Log
import com.google.android.exoplayer2.offline.DownloadAction
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper
import com.google.android.exoplayer2.offline.ProgressiveDownloadAction
import com.google.android.exoplayer2.source.dash.offline.DashDownloadAction
import com.google.android.exoplayer2.source.hls.offline.HlsDownloadAction
import com.google.android.exoplayer2.source.smoothstreaming.offline.SsDownloadAction
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.util.Util
import java.io.File


/**
 * Placeholder application to facilitate overriding Application methods for debugging and testing.
 */
class DemoApplication : Application() {
    companion object {
        lateinit var application: DemoApplication
    }


    override fun onCreate() {
        super.onCreate()
        application = this
    }

}
