package com.saurabh.media3_exo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.ui.PlayerControlView
import androidx.media3.ui.PlayerView

class MainActivity : AppCompatActivity() {

    //http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4
    private val bunnyUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

    private val playerView : PlayerView by lazy {
        findViewById(R.id.playerview)
    }

    //in this project we are using default media source
    private val mediaSourceFactory: MediaSource.Factory by lazy {
        DefaultMediaSourceFactory(this)
    }

    //For HLS
    val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
    val mediaItem = MediaItem.fromUri(bunnyUrl)
    private val hlsMediaSource  = HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)

    //For DASH
    private val dashMediaSource  = DashMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)

    //For SmoothStreaming
    private val ssMediaSource  = SsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)

    private val player : ExoPlayer by lazy {
        ExoPlayer.Builder(this).setMediaSourceFactory(mediaSourceFactory).build()
    }

    @OptIn(UnstableApi::class) override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mediaItem = MediaItem.fromUri(bunnyUrl)
        player.setMediaItem(mediaItem)

        //DRM mediaItem
        /**
         * val mediaItem =
         *           MediaItem.Builder()
         *             .setUri(videoUri)
         *             .setDrmConfiguration(
         *               MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
         *                 .setLicenseUri(licenseUri)
         *                 .setMultiSession(true)
         *                 .setLicenseRequestHeaders(httpRequestHeaders)
         *                 .build()
         *             )
         *             .build()
         */

        //add Media source
        /**
         * Different media sources
         * DashMediaSource for DASH.
         * SsMediaSource for SmoothStreaming.
         * HlsMediaSource for HLS.
         * ProgressiveMediaSource for regular media files.
         * RtspMediaSource for RTSP.
         */

//        player.setMediaSource(hlsMediaSource)

        playerView.player = player
        player.prepare()
        player.playWhenReady = true

        player.addListener(object : Player.Listener{
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                Log.e("playerState", playbackState.toString())
            }

            override fun onDeviceVolumeChanged(volume: Int, muted: Boolean) {
                super.onDeviceVolumeChanged(volume, muted)
                Log.e("DeviceVolume", "$volume - isMuted =$muted")
            }

            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)

                Log.e("PlayerEvents", events.toString())

            }

            override fun onVolumeChanged(volume: Float) {
                super.onVolumeChanged(volume)
                Log.e("Volume2", volume.toString())
            }
        })

        //Add subtitle
        /**
         * val subtitle =
         *   SubtitleConfiguration.Builder(subtitleUri)
         *     .setMimeType(mimeType) // The correct MIME type (required).
         *     .setLanguage(language) // The subtitle language (optional).
         *     .setSelectionFlags(selectionFlags) // Selection flags for the track (optional).
         *     .build()
         * val mediaItem =
         *   MediaItem.Builder().setUri(videoUri).setSubtitleConfigurations(listOf(subtitle)).build()
         */


    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }

    override fun onResume() {
        super.onResume()
        player.play()
    }

    override fun onStop() {
        player.release()
        super.onStop()
    }
}