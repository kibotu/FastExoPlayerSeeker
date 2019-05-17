package net.kibotu.fastexoplayerseeker

import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray

/**
 * https://medium.com/@takusemba/understands-callbacks-of-exoplayer-c05ac3c322c2
 */
class PlayerListener : EventListener {

    var onSeekingChanged: ((isSeeking: Boolean) -> Unit)? = null

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
        log("onPlaybackParametersChanged $playbackParameters")
    }

    override fun onSeekProcessed() {
        log("onSeekProcessed")
        onSeekingChanged?.invoke(false)
    }

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
        log("onTracksChanged trackGroups=$trackGroups trackSelections=$trackSelections")
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        log("onPlayerError error=$error")
    }

    override fun onLoadingChanged(isLoading: Boolean) {
        log("onLoadingChanged isLoading=$isLoading")
    }

    override fun onPositionDiscontinuity(@DiscontinuityReason reason: Int) {
        val reasonString = when (reason) {
            DISCONTINUITY_REASON_PERIOD_TRANSITION -> ::DISCONTINUITY_REASON_PERIOD_TRANSITION.name
            DISCONTINUITY_REASON_SEEK -> ::DISCONTINUITY_REASON_SEEK.name
            DISCONTINUITY_REASON_SEEK_ADJUSTMENT -> ::DISCONTINUITY_REASON_SEEK_ADJUSTMENT.name
            DISCONTINUITY_REASON_AD_INSERTION -> ::DISCONTINUITY_REASON_AD_INSERTION.name
            DISCONTINUITY_REASON_INTERNAL -> ::DISCONTINUITY_REASON_INTERNAL.name
            else -> reason.toString()
        }
        log("onPositionDiscontinuity reason=$reasonString")

        if (reason == DISCONTINUITY_REASON_SEEK || reason == DISCONTINUITY_REASON_SEEK_ADJUSTMENT) {
            onSeekingChanged?.invoke(true)
        }
    }

    override fun onRepeatModeChanged(@RepeatMode repeatMode: Int) {
        val repeatModeString = when (repeatMode) {
            REPEAT_MODE_OFF -> ::REPEAT_MODE_OFF.name
            REPEAT_MODE_ONE -> ::REPEAT_MODE_ONE.name
            REPEAT_MODE_ALL -> ::REPEAT_MODE_ALL.name
            else -> repeatMode.toString()
        }
        log("onRepeatModeChanged repeatMode=$repeatModeString")
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        log("onShuffleModeEnabledChanged shuffleModeEnabled=$shuffleModeEnabled")
    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, @TimelineChangeReason reason: Int) {
        val reasonString = when (reason) {
            TIMELINE_CHANGE_REASON_PREPARED -> ::TIMELINE_CHANGE_REASON_PREPARED.name
            TIMELINE_CHANGE_REASON_RESET -> ::TIMELINE_CHANGE_REASON_RESET.name
            TIMELINE_CHANGE_REASON_DYNAMIC -> ::TIMELINE_CHANGE_REASON_DYNAMIC.name
            else -> reason.toString()
        }
        log("onTimelineChanged timeline=$timeline manifest=$manifest reason=$reasonString")
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        val playbackStateString = when (playbackState) {
            STATE_IDLE -> ::STATE_IDLE.name
            STATE_BUFFERING -> ::STATE_BUFFERING.name
            STATE_READY -> ::STATE_READY.name
            STATE_ENDED -> ::STATE_ENDED.name
            else -> playbackState.toString()
        }
        log("onPlayerStateChanged playWhenReady=$playWhenReady playbackState=$playbackStateString")

        if (playbackState == STATE_READY) {
            onSeekingChanged?.invoke(false)
        }
    }
}