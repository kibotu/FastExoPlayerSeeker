package net.kibotu.fastexoplayerseeker.demo

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.exozet.android.core.extensions.*
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SeekParameters
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.exo_playback_control_view.*
import net.kibotu.fastexoplayerseeker.SeekPositionEmitter
import net.kibotu.fastexoplayerseeker.seekWhenReady
import net.kibotu.logger.LogcatLogger
import net.kibotu.logger.Logger
import net.kibotu.logger.Logger.loge
import net.kibotu.logger.Logger.logv
import kotlin.math.absoluteValue
import kotlin.math.roundToLong

class MainActivity : AppCompatActivity() {

    @LayoutRes
    private val layout = R.layout.activity_main

    private var subscriptions = CompositeDisposable()

    private lateinit var simpleExoPlayer: SimpleExoPlayer

    private var autoPlay = true

    private val playerControlVisibilityListener = PlayerControlView.VisibilityListener {
        when (it) {
            View.VISIBLE -> {
                exo_playback_control_layout.waitForLayout {
                    val marginBottomPx = exo_playback_control_layout.measuredHeight
                    swipe_detector.setMargins(bottom = marginBottomPx)
                }
            }
            else -> swipe_detector.setMargins(bottom = 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)

        Logger.addLogger(LogcatLogger())

//        initPlayer("walkaround.mp4".parseAssetFile())
        // ffmpeg -i walkaround -c:v libx264 -profile:v baseline -level 3.0 -x264opts keyint=10:min-keyint=10 -g 10 -movflags +faststart+rtphint -maxrate:v 3000k -bufsize:v 3500k walkaround_with_additional_iframes.mp4
        initPlayer("walkaround_with_additional_iframes.mp4".parseAssetFile())
        initSwipeControls()
    }

    override fun onDestroy() {
        player_view.setControllerVisibilityListener(null)
        super.onDestroy()

        subscriptions.dispose()
    }

    private fun initPlayer(videoPath: Uri) {

        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(applicationContext, DefaultTrackSelector(AdaptiveTrackSelection.Factory()))

        // snap to keyframes
        // also see https://github.com/albertdaurell/AndroidPlayer/blob/master/ExoPlayer/library/core/src/main/java/com/google/android/exoplayer2/SeekParameters.java#L39-L48
        simpleExoPlayer.seekParameters = SeekParameters.CLOSEST_SYNC

        with(player_view) {
            setControllerVisibilityListener(playerControlVisibilityListener)
            show()
            requestFocus()
            player = simpleExoPlayer
            player_view.showController()
        }

        with(simpleExoPlayer) {
            prepare(ExtractorMediaSource.Factory(DefaultDataSourceFactory(applicationContext, "ua")).createMediaSource(videoPath))
            if (autoPlay) playWhenReady = true
        }
    }

    private fun initSwipeControls() {

        var startScrollingSeekPosition = 0L

        swipe_detector.onClick {
            if (player_view.isControllerVisible)
                player_view.hideController()
            else
                player_view.showController()
        }

        // toggle video playback based on scrolling state
        swipe_detector?.onIsScrollingChanged {
            logv("onIsScrollingChanged isScrolling=$it")
            if (it) {
                startScrollingSeekPosition = simpleExoPlayer.currentPosition
            }
            if (autoPlay) simpleExoPlayer.playWhenReady = !it
        }

        val emitter = SeekPositionEmitter()

        simpleExoPlayer.seekWhenReady(emitter)
            .subscribe({
                logv("seekTo=${it.first} isSeeking=${it.second}")
            }, { loge("${it.message}") })
            .addTo(subscriptions)

        swipe_detector?.onScroll { percentX, percentY ->

            val duration = simpleExoPlayer.duration

            val maxPercent = 0.75f
            val scaledPercent = percentX * maxPercent
            val percentOfDuration = scaledPercent * -1 * duration + startScrollingSeekPosition
            // shift in position domain and ensure circularity
            val newSeekPosition = ((percentOfDuration + duration) % duration).roundToLong().absoluteValue

            emitter.seekFast(newSeekPosition)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }
}