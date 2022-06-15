package com.training.myplayer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.PlaybackPreparer
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.PlayerView
import com.training.player.AudioPlayer
import com.training.player.exception.PlaybackException
import com.training.player.listener.PlaybackListener
import com.training.player.listener.SimplePlaybackListener
import com.xpleemoon.sample.myexoplayer.playercontroller.PlaybackState

class MainActivity : AppCompatActivity() {
    private val audioPlayer by lazy {

        AudioPlayer(applicationContext).also {
            it.startEventLogger()
        }
    }

    /**
     * flv貌似不能正常播
     */
    private val sources = mapOf(
        "1" to "http://vfx.mtime.cn/Video/2019/03/09/mp4/190309153658147087.mp4",
//        "2" to "http://vfx.mtime.cn/Video/2019/02/04/mp4/190204084208765161.mp4",
//        "卡农" to "https://m10.music.126.net/20220531164721/a297762af80ada3c9ef22e8f3357f581/ymusic/055a/535b/5359/206bffe62bd149b82531d34775e2eeb3.mp3",
//        "钢琴-故事的开始" to "http://img.tukuppt.com/preview_music/00/08/60/preview-5b835eb33f4f15003.mp3",
//        "克罗地亚狂想曲" to "https://m10.music.126.net/20220531165101/6d47cccb9bf207874eb80fb4c2f2c16c/ymusic/0e08/035f/040f/9bde0b60bd45d74ca0233ece3b2624e7.mp3",
//        "新闻联播本地raw" to RawResourceDataSource.buildRawResourceUri(R.raw.cctv).toString(),
    )

    private var debugView: TextView? = null
    private var timeBar: DefaultTimeBar? = null
    private var playerView: PlayerView? = null
    private var currentPositionMS: Long = 0

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null && intent.extras != null && intent.extras?.getSerializable("play") != null) {
            val dto = intent.extras?.getSerializable("play") as PlayerBean

            Toast.makeText(this, "onNewIntent" + dto?.position.toString(), Toast.LENGTH_SHORT)
                .show()
            audioPlayer.seekTo(dto.position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        debugView = findViewById(R.id.debug_view)
        timeBar = findViewById(R.id.timeBar)
        playerView = findViewById(R.id.playview)
        audioPlayer.setPlaybackPreparer(PlaybackPreparer {
            playerView?.let {
                audioPlayer.bindPlayView(it)
            }
            val mediaSource = ConcatenatingMediaSource()
            sources.forEach {
                mediaSource.addMediaSource(audioPlayer.mediaSource(it.value))
            }
            audioPlayer.prepare(mediaSource)
        })

        // 启动播放调试
        audioPlayer.startPlaybackDebug {
            debugView?.text = it
        }
        // 启动播放事件打印
        audioPlayer.startEventLogger()

        val playbackListener: PlaybackListener = object : SimplePlaybackListener() {

            override fun onPlaybackProgressChanged(
                currentPositionMS: Long,
                bufferedPositionMS: Long,
                durationMS: Long
            ) {
                Log.i(
                    "onProgressChanged",
                    "currentPositionMS" + currentPositionMS + "bufferedPositionMS" + bufferedPositionMS + "durationMS" + durationMS
                )
                this@MainActivity.currentPositionMS = currentPositionMS
            }

            override fun onPlaybackStateChanged(playbackState: PlaybackState) {

            }

            override fun onPlayerError(error: PlaybackException) {

            }
        }
        audioPlayer.addPlaybackListener(playbackListener)
    }

    fun onPlayControl(view: View) {
        when (view.id) {
            R.id.play -> {
                audioPlayer.play()
            }
            R.id.pause -> {
                audioPlayer.pause()
            }
            R.id.stop -> {
                audioPlayer.stop()
            }
            R.id.repeat_toggle -> {
                audioPlayer.toggleRepeatModes()
            }
            R.id.prev -> {
                audioPlayer.previous()
            }
            R.id.rew -> {
                audioPlayer.rewind()
            }
            R.id.ffwd -> {
                audioPlayer.fastForward()
            }
            R.id.next -> {
                audioPlayer.next()
            }
            R.id.speed_toggle -> {
                audioPlayer.togglePlaybackSpeeds()
            }
            R.id.btn_haha -> {
                audioPlayer.pause()
                MyServerSocket.play(currentPositionMS)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        audioPlayer.play()
    }

    override fun onPause() {
        super.onPause()
        audioPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayer.stopPlaybackDebug()
        audioPlayer.stopEventLogger()
        audioPlayer.stop()
        audioPlayer.release()
        playerView?.let {
            audioPlayer.unbindPlayView(it)
        }
        MyServerSocket.cancel()
    }
}