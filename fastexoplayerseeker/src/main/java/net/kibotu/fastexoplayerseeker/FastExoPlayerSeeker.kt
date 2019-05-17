package net.kibotu.fastexoplayerseeker

import com.google.android.exoplayer2.Player
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject


fun Player.seekingStateChangedObservable(playerListener: PlayerListener = PlayerListener()) = BehaviorSubject.create<Boolean> { emitter ->

    if (emitter.isDisposed)
        return@create

    addListener(playerListener)

    playerListener.onSeekingChanged = { isSeeking ->
        emitter.onNext(isSeeking)
    }

}.doOnDispose {
    removeListener(playerListener)
}

fun seekPositionChangedObservable(seekEmitter: SeekPositionEmitter) = BehaviorSubject.create<Long> { emitter ->

    if (emitter.isDisposed)
        return@create

    seekEmitter.seekFast = {
        emitter.onNext(it)
    }
}

fun Player.seekWhenReady(emitter: SeekPositionEmitter) = seekPositionChangedObservable(emitter)
    .withLatestFrom(seekingStateChangedObservable())
    .observeOn(AndroidSchedulers.mainThread())
    .doOnNext {
        if (!it.second)
            seekTo(it.first)
    }
