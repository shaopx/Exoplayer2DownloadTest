package com.spx.exoplayer2downloadtest

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.offline.FilteringManifestParser
import com.google.android.exoplayer2.offline.ProgressiveDownloadAction
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.dash.manifest.DashManifest
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser
import com.google.android.exoplayer2.source.dash.manifest.RepresentationKey
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylist
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParser
import com.google.android.exoplayer2.source.hls.playlist.RenditionKey
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser
import com.google.android.exoplayer2.source.smoothstreaming.manifest.StreamKey
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.RandomTrackSelection
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val TAG: String = "MainActivity"
    private var mediaDataSourceFactory: DataSource.Factory? = null
    private val BANDWIDTH_METER = DefaultBandwidthMeter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        var initUrl = "https://f.us.sinaimg.cn/002AQIssgx07mE4yi1BS01040200B75I0k010.mp4?label=mp4_ld&template=640x360.28&Expires=1533722261&ssig=KdQSWNklOz&KID=unistore,video"
        var initUrl = "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd"
        input.setText(initUrl)
        input.setSelection(initUrl.length)

        download_btn.setOnClickListener { view -> startDownload() }
        play_btn.setOnClickListener { view -> startPlay() }

        mediaDataSourceFactory = buildDataSourceFactory(true)

        checkPermission()
    }

    fun startDownload() {
        var uri = Uri.parse(input.text.toString())
        var downloadAction = ProgressiveDownloadAction(uri, false, null, null)

        DownloadService.startWithAction(this@MainActivity,
                DemoDownloadService::class.java, downloadAction, true)
    }

    fun startPlay() {
        var uri = Uri.parse(input.text.toString())
        var trackSelectorParameters = DefaultTrackSelector.ParametersBuilder().build()
        var trackSelectionFactory = RandomTrackSelection.Factory()
        var trackSelector = DefaultTrackSelector(trackSelectionFactory)
        trackSelector.setParameters(trackSelectorParameters)
        var player = ExoPlayerFactory.newSimpleInstance(DefaultRenderersFactory(this), trackSelector, DefaultLoadControl())
//        player.addListener(PlayerEventListener())
        player.setPlayWhenReady(true)
//        player.addAnalyticsListener(EventLogger(trackSelector))
        player_view.setPlayer(player)

        var mediaSources = buildMediaSource(uri)
        player.prepare(mediaSources)
    }


    private fun buildMediaSource(uri: Uri): MediaSource {
        return buildMediaSource(uri, null)
    }


    private fun buildMediaSource(uri: Uri, @Nullable overrideExtension: String?): MediaSource {
        @C.ContentType val type = Util.inferContentType(uri, overrideExtension)
        Log.d(TAG, "type:"+type)
        when (type) {
            C.TYPE_DASH -> return DashMediaSource.Factory(
                    DefaultDashChunkSource.Factory(mediaDataSourceFactory),
                    buildDataSourceFactory(false))
                    .setManifestParser(
                            FilteringManifestParser<DashManifest, RepresentationKey>(
                                    DashManifestParser(), getOfflineStreamKeys(uri) as List<RepresentationKey>))
                    .createMediaSource(uri)
            C.TYPE_SS -> return SsMediaSource.Factory(
                    DefaultSsChunkSource.Factory(mediaDataSourceFactory),
                    buildDataSourceFactory(false))
                    .setManifestParser(
                            FilteringManifestParser<SsManifest, StreamKey>(
                                    SsManifestParser(), getOfflineStreamKeys(uri) as List<StreamKey>))
                    .createMediaSource(uri)
            C.TYPE_HLS -> return HlsMediaSource.Factory(mediaDataSourceFactory)
                    .setPlaylistParser(
                            FilteringManifestParser<HlsPlaylist, RenditionKey>(
                                    HlsPlaylistParser(), getOfflineStreamKeys(uri) as List<RenditionKey>))
                    .createMediaSource(uri)
            C.TYPE_OTHER -> return ExtractorMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri)
            else -> {
                throw IllegalStateException("Unsupported type: $type")
            }
        }
    }

    private fun buildDataSourceFactory(useBandwidthMeter: Boolean): DataSource.Factory {
        return DownloadMgr.buildDataSourceFactory(if (useBandwidthMeter) BANDWIDTH_METER else null)
    }

    private fun getOfflineStreamKeys(uri: Uri): List<*> {
        // 实在太麻烦了, 就返回空吧
        return emptyList<String>()
    }
}
