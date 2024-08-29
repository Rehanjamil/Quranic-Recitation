package com.quranicvoices.app

import android.graphics.Color
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.quranicvoices.app.databinding.ActivityMainBinding
import com.quranicvoices.app.models.PlayerModel
import com.quranicvoices.app.utils.Constants
import com.quranicvoices.app.viewmodels.PlayerViewModel
import com.quranicvoices.rabiulqaloob.utils.PreferenceHelper
import com.quranicvoices.rabiulqaloob.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler()
    private var playerModel: PlayerModel = PlayerModel()
    private var isFirstTime = false
    private var count = 0

    @Inject
    lateinit var preferenceHelper: PreferenceHelper
    private val playerViewModel by viewModels<PlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor =
            Color.parseColor("#F5F4EF")

//        (supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment)
//            .let {
//                navController = it.findNavController()
//                it.findNavController().let {
//                    it.addOnDestinationChangedListener { controller, destination, arguments ->
//                        when (destination.id) {
////                            in listOf(
////                                R.id.homeFragment,
////                                R.id.juzzListingFragment,
////                                R.id.surahListingFragment,
////                                R.id.bookmarkListingFragment,
////                                R.id.splashFragment,
////                                R.id.prayerTimesFragment,
////                                R.id.prayerSettingsFragment,
////                            ) -> {
////                                setUpPlayerVisibility()
////                                window.statusBarColor =
////                                    ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
////                            }
////
////                            else -> {
////                                window.statusBarColor = Color.BLACK
////                            }
//                        }
//                    }
//                }
//            }

//        binding.seekbarProgress.setOnSeekBarChangeListener(object :
//            SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
//                if (p2) {
//                    // Update the MediaPlayer playback position when the SeekBar is dragged
//                    mediaPlayer?.let {
//                        if (it.isPlaying.not()) {
//                            startMedia()
//                        }
//                        mediaPlayer?.seekTo(p1)
//                    }
//                }
//            }
//
//            override fun onStartTrackingTouch(p0: SeekBar?) {
//            }
//
//            override fun onStopTrackingTouch(p0: SeekBar?) {
//            }
//
//        })
//        binding.buttonPlayPause1.setOnClickListener {
//            mediaPlayer?.isPlaying?.let {
//                if (it) {
//                    pauseMedia()
//                } else {
//                    startMedia()
//                }
//            }
//        }


        binding.btnPlay.setOnClickListener {
            startMedia()
        }
        binding.btnPause.setOnClickListener {
            pauseMedia()
        }
        showLoaderDialog()
        setObserver()
    }

    private fun setObserver() {
        isFirstTime = true
        playerViewModel.playerModel.observe(this) {
            if (playerModel.currentSurah == it.currentSurah) {
                it.surahProgress = playerModel.surahProgress
            }
            playerModel = it
            checkPlayerConditions()
            binding.btnPlay.isEnabled = true
        }

    }

    private fun checkPlayerConditions() {
        if (playerModel.currentSurah != "0") {
            setUpMediaPlayer()
        } else {
            mediaPlayer?.isPlaying?.takeIf { it }?.let {
                handler.removeCallbacksAndMessages(null)
                mediaPlayer?.release()
            }
            hideLoader()
        }
    }

    private fun setUpMediaPlayer() {
        initMediaPlayer()
        setPlayerAttributes(
            playerModel.currentSurah.toInt()
        )
    }

    private fun startMedia() {
        binding.btnPlay.isVisible = false
        binding.btnPause.isVisible = true
        mediaPlayer?.start()
    }

    private fun pauseMedia() {
        binding.btnPlay.isVisible = true
        binding.btnPause.isVisible = false
        mediaPlayer?.pause()
    }

    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
    }

    private fun setPlayerAttributes(fileName: Int) {
//        binding.clp.show()

        val audioUrl =
            "https://www.nooresunnat.com/Audio/Complete%20Quran/Shuraim-Sudais-Urdu/${
                Constants.surahMap[String.format(
                    "%03d",
                    fileName
                ).toInt()]
            }"
        Log.e("setPlayerAttributes: ", audioUrl)
        mediaPlayer?.apply {
            setDataSource(audioUrl)
            setOnCompletionListener {
                proceedToNextSurah()
            }
            setOnPreparedListener {
                // Start playing audio when MediaPlayer is prepared
//                binding.seekbarProgress.isInvisible = false
//                binding.seekbarProgress.max = mediaPlayer!!.duration
//                binding.clp.hide()
                hideLoader()
                cacheProgress()
                if (isFirstTime) {
//                    binding.buttonPlayPause1.isSelected = false
//                    binding.seekbarProgress.progress = playerModel.surahProgress
                    seekTo(playerModel.surahProgress)
                    binding.btnPlay.isVisible = true
                    isFirstTime = false
                } else {
                    startMedia()
                }
            }

            setOnErrorListener { mediaPlayer, what, extra ->
                checkError(what, extra)
                true
            }

            // Update SeekBar position every 100 milliseconds
            handler.postDelayed(object : Runnable {
                override fun run() {
                    cacheProgress()
                    handler.postDelayed(this, 100)
                }
            }, 100)
            prepareAsync() // Prepare the MediaPlayer asynchronously
        }

    }

    private fun proceedToNextSurah() {
        if (playerModel.currentSurah.toInt() < 114) {
            preferenceHelper.setString(
                Constants.SURAH_NUMBER,
                playerModel.currentSurah.toInt().plus(1).toString()
            )
            playerViewModel.setPlayerModel(
                PlayerModel(
                    playerModel.currentSurah.toInt().plus(1).toString(),
                    isPlayerShown = true
                )
            )
        } else {
            preferenceHelper.setString(
                Constants.SURAH_NUMBER,
                "1"
            )
            playerViewModel.setPlayerModel(
                PlayerModel(
                    "1",
                    isPlayerShown = true
                )
            )
        }
    }

    private fun checkError(what: Int, extra: Int) {
//        binding.clp.hide()
//        binding.seekbarProgress.isInvisible = false
        pauseMedia()
        mediaPlayer?.release()
        setUpMediaPlayer()
        when (extra) {
            MediaPlayer.MEDIA_ERROR_IO -> {
                // Handle network-related I/O error
                // This could indicate network issues, such as timeouts or connectivity problems
                // You can display an error message to the user or retry the operation
                showToast(
                    "No network connection"
                )
            }

            MediaPlayer.MEDIA_ERROR_TIMED_OUT -> {
                // Handle timeout error
                // This indicates a timeout occurred while trying to connect to the media server
                // You can display an error message to the user or retry the operation
                showToast(
                    "timed out"
                )
            }

            else -> {
                // Handle other MediaPlayer errors here
                showToast(
                    "System error"
                )
            }
        }


    }

    private fun cacheProgress() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                playerModel.surahProgress = it.currentPosition
                preferenceHelper.setInt(
                    Constants.PROGRESS,
                    it.currentPosition
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release() // Release the MediaPlayer when the activity is destroyed
        handler.removeCallbacksAndMessages(null)
    }

    fun showLoaderDialog() {
        binding.videoLoadingView.isVisible = true
        binding.btnPlay.isVisible = false
    }

    fun hideLoader() {
        binding.videoLoadingView.isVisible = false
        binding.btnPlay.isVisible = true
    }


}

