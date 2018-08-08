package com.spx.exoplayer2downloadtest

import android.net.Uri
import android.util.Log
import com.google.android.exoplayer2.offline.*
import com.google.android.exoplayer2.source.dash.offline.DashDownloadAction
import com.google.android.exoplayer2.source.hls.offline.HlsDownloadAction
import com.google.android.exoplayer2.source.smoothstreaming.offline.SsDownloadAction
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.*
import com.spx.exoplayer2downloadtest.DemoApplication.Companion.application
import java.io.File
import java.util.HashMap

object DownloadMgr{
    private const val TAG:String = "DownloadMgr"
    private var userAgent: String = "demo"
    private const val DOWNLOAD_ACTION_FILE = "actions"
    private const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"
    private const val MAX_SIMULTANEOUS_DOWNLOADS = 2
    private val DOWNLOAD_DESERIALIZERS = arrayOf(DashDownloadAction.DESERIALIZER, HlsDownloadAction.DESERIALIZER,
            SsDownloadAction.DESERIALIZER, ProgressiveDownloadAction.DESERIALIZER)


    private var downloadDirectory: File? = null
    private var downloadCache: Cache? = null
    private var downloadManager: DownloadManager? = null

    /** Returns a [HttpDataSource.Factory].  */
    fun buildHttpDataSourceFactory(
            listener: TransferListener<in DataSource>?): HttpDataSource.Factory {
        return DefaultHttpDataSourceFactory(userAgent, listener)
    }


    fun getDownloadManager(): DownloadManager? {
        initDownloadManager()
        return downloadManager
    }


    @Synchronized
    private fun initDownloadManager() {
        if (downloadManager ==
                /* eventListener= */ null) {
            val downloaderConstructorHelper = DownloaderConstructorHelper(
                    getDownloadCache(), buildHttpDataSourceFactory(/* listener= */null))
            downloadManager = DownloadManager(
                    downloaderConstructorHelper,
                    MAX_SIMULTANEOUS_DOWNLOADS,
                    DownloadManager.DEFAULT_MIN_RETRY_COUNT,
                    File(getDownloadDirectory(), DOWNLOAD_ACTION_FILE),
                    *DOWNLOAD_DESERIALIZERS)

        }
    }

    @Synchronized
    private fun getDownloadCache(): Cache {
        Log.d(TAG, "getDownloadCache ... ")
        if (downloadCache == null) {
            val downloadContentDirectory = File(getDownloadDirectory(), DOWNLOAD_CONTENT_DIRECTORY)
            downloadCache = SimpleCache(downloadContentDirectory, NoOpCacheEvictor())
        }
        return downloadCache!!
    }

    private fun getDownloadDirectory(): File {
        Log.d(TAG, "getDownloadDirectory ... ")
        if (downloadDirectory == null) {
            downloadDirectory = application.getExternalFilesDir(null)
            if (downloadDirectory == null) {
                downloadDirectory = application.filesDir
            }
        }
        return downloadDirectory!!
    }

    private fun buildReadOnlyCacheDataSource(
            upstreamFactory: DefaultDataSourceFactory, cache: Cache): CacheDataSourceFactory {
        return CacheDataSourceFactory(
                cache,
                upstreamFactory,
                FileDataSourceFactory(),
                /* eventListener= */ null,
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null)/* cacheWriteDataSinkFactory= */
    }

    /** Returns a [DataSource.Factory].  */
    fun buildDataSourceFactory(listener: TransferListener<in DataSource>?): DataSource.Factory {
        val upstreamFactory = DefaultDataSourceFactory(application, listener, buildHttpDataSourceFactory(listener))
        return buildReadOnlyCacheDataSource(upstreamFactory, getDownloadCache())
    }

//    fun <K> getOfflineStreamKeys(uri: Uri): List<K> {
//        if (!trackedDownloadStates.containsKey(uri)) {
//            return emptyList()
//        }
//        val action = trackedDownloadStates[uri]
//        return if (action is SegmentDownloadAction<*>) {
//            action.keys
//        } else emptyList()
//    }
}