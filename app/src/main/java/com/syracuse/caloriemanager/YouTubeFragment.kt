package com.syracuse.caloriemanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragmentX


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class YouTubeFragment : YouTubePlayerSupportFragmentX() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mPlayer: YouTubePlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_you_tube, container, false)
        val youTubePlayerFragment = childFragmentManager.findFragmentById(R.id.youtube_player_fragment) as YouTubePlayerSupportFragmentX
        youTubePlayerFragment.initialize("AIzaSyCzVMeWxsrUpasjmcG1PFLK5zGmv43rESY", object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(
                provider: YouTubePlayer.Provider,
                youTubePlayer: YouTubePlayer,
                wasRestored: Boolean
            ) {
                if (!wasRestored) {
                    youTubePlayer.cueVideo("YE7VzlLtp-4")
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
                }
                mPlayer = youTubePlayer

                //This flag tells the player to switch to landscape when in fullscreen, it will also return to portrait
                //when leaving fullscreen
                mPlayer!!.fullscreenControlFlags = YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION

                //This flag tells the player to automatically enter fullscreen when in landscape. Since we don't have
                //landscape layout for this activity, this is a good way to allow the user rotate the video player.
                mPlayer!!.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE)

                //This flag controls the system UI such as the status and navigation bar, hiding and showing them
                //alongside the player UI
                mPlayer!!.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI)


                if (wasRestored) {
                    mPlayer!!.play()
                } else {
                    mPlayer!!.loadVideo("_-skVpmoaGI")
                }
            }

            override fun onInitializationFailure(
                provider: YouTubePlayer.Provider,
                youTubeInitializationResult: YouTubeInitializationResult
            ) {

                val errorMessage = String.format(
                    "There was an error initializing the YouTubePlayer (%1\$s)",
                    youTubeInitializationResult.toString()
                )
            }
        })
        return view
    }



    companion object {
        const val RECOVERY_DIALOG_REQUEST = 1
    }
}