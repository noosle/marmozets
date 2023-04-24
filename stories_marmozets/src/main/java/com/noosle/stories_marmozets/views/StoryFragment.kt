package com.noosle.stories_marmozets.views

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.ddd.androidutils.DoubleClick
import com.ddd.androidutils.DoubleClickListener
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.noosle.stories_marmozets.objects.StoryListener
import com.noosle.stories_marmozets.objects.UserStories
import com.noosle.marmozets.runnable
import com.noosle.stories_marmozets.R
import com.noosle.stories_marmozets.databinding.FragmentStoryBinding


class StoryFragment : Fragment(R.layout.fragment_story) {

    private lateinit var stories: UserStories

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var storyListener: StoryListener

    private lateinit var runnable: Runnable

    private val binding: FragmentStoryBinding by viewBinding()

    private var currentStoryPosition = 0

    private var progressColor: Int = 0
    private var backgroundColor: Int = 0

    private val exoPlayer by lazy {
        ExoPlayer.Builder(requireContext()).build()
    }

    override fun onResume() {
        super.onResume()
        prepareProgressBars()
        launch()
    }

    override fun onPause() {
        for (i in 0..binding.progressLayout.childCount) {
            val view = binding.progressLayout.getChildAt(i)
            if (view is ProgressBar) {
                view.progress = 0
            }
        }
        stopHandlers()
        super.onPause()
    }

    override fun onStop() {
        stopHandlers()
        super.onStop()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyMarmozetsColors()
        prepareExoPlayer()
        setStoryTouchEvents()
    }

    private fun applyMarmozetsColors() {
        binding.rootLayout.setBackgroundColor(backgroundColor)
    }

    private fun setStoryTouchEvents() {
        val clickListener = DoubleClick(object : DoubleClickListener {

            override fun onSingleClickEvent(view: View?) {
                val pbView = binding.progressLayout.getChildAt(currentStoryPosition) ?: return
                val progressBar = pbView as ProgressBar
                handler.removeCallbacks(runnable)
                progressBar.progress = 0
                when (stories.marmozets[currentStoryPosition].type) {
                    "image" -> {
                        runStoryProgress()
                    }
                    "video" -> {
                        exoPlayer.seekTo(0)
                    }
                }
            }

            override fun onDoubleClickEvent(view: View?) {
                val pbView = binding.progressLayout.getChildAt(currentStoryPosition) ?: return
                val progressBar = pbView as ProgressBar
                handler.removeCallbacks(runnable)
                progressBar.progress = 0
                if (currentStoryPosition != 0) {
                    currentStoryPosition--
                    launch()
                } else {
                    when (stories.marmozets[currentStoryPosition].type) {
                        "image" -> {
                            runStoryProgress()
                        }
                        "video" -> {
                            exoPlayer.seekTo(0)
                        }
                    }
                }
            }
        })

        with(binding) {
            leftTouchLayout.setOnClickListener(clickListener)
            rightTouchLayout.setOnClickListener {
                val view = progressLayout.getChildAt(currentStoryPosition) ?: return@setOnClickListener
                val progressBar = view as ProgressBar
                if (::runnable.isInitialized) handler.removeCallbacks(runnable)
                progressBar.progress = progressBar.max
                currentStoryPosition++
                launch()
            }
        }
    }

    private fun prepareExoPlayer() {
        binding.playerView.player = exoPlayer
    }

    private fun stopExoPlayer() {
        exoPlayer.clearMediaItems()
        exoPlayer.removeListener(exoPlayerListener)
        exoPlayer.stop()
    }

    private fun prepareProgressBars() {
        if (binding.progressLayout.childCount > 0) return
        for (i in stories.marmozets.indices) {
            val pb = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal).apply {
                progressDrawable.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(progressColor, BlendModeCompat.SRC_ATOP)
            }
            val params = LinearLayout.LayoutParams(
                0,
                5, 1f
            )
            params.setMargins(5, 0, 5, 0)
            pb.layoutParams = params
            pb.max = stories.marmozets[i].duration.toInt()
            binding.progressLayout.addView(pb)
        }
    }

    private fun launch() {
        if (currentStoryPosition == stories.marmozets.size) {
            storyListener.onUpdateStoryPage()
        } else {
            showCurrentStory()
        }
    }

    private fun showCurrentStory() {
        binding.progressBar.visibility = View.VISIBLE
        stopExoPlayer()
        val marmozet = stories.marmozets[currentStoryPosition]
        storyListener.onCurrentStory(marmozet)
        when (marmozet.type) {
            "image" -> {
                binding.playerView.visibility = View.INVISIBLE
                Glide.with(this)
                    .load(marmozet.url)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            with(binding) {
                                playerView.visibility = View.INVISIBLE
                                progressBar.visibility = View.VISIBLE
                                image.visibility = View.INVISIBLE
                            }
                            runStoryProgress()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: com.bumptech.glide.request.target.Target<Drawable>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            with(binding) {
                                playerView.visibility = View.INVISIBLE
                                progressBar.visibility = View.INVISIBLE
                                image.visibility = View.VISIBLE
                            }
                            runStoryProgress()
                            return false
                        }
                    })
                    .into(binding.image)
            }
            "video" -> {
                binding.image.visibility = View.INVISIBLE
                exoPlayer.apply {
                    setMediaItem(MediaItem.fromUri(Uri.parse(marmozet.url)))
                    prepare()
                    addListener(exoPlayerListener)
                }
            }
        }
    }

    private val exoPlayerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
                    with(binding) {
                        playerView.visibility = View.VISIBLE
                        progressBar.visibility = View.INVISIBLE
                    }
                    exoPlayer.play()
                    runStoryProgress()
                }
                Player.STATE_BUFFERING -> Unit
                else -> exoPlayer.stop()
            }
        }
    }

    private fun runStoryProgress() {
        var progressStatus = 0
        if (::runnable.isInitialized) handler.removeCallbacks(runnable)
        val view = binding.progressLayout.getChildAt(currentStoryPosition) ?: return
        val progressBar =  view as ProgressBar
        runnable = runnable {
            progressBar.progress = progressStatus
            if (progressStatus < progressBar.max) {
                progressStatus += 20
                handler.postDelayed(this, 20)
            } else {
                currentStoryPosition++
                launch()
            }
        }.also {
            handler.postDelayed(it, 20)
        }
    }

    internal fun setStoriesForCurrentUser(stories: UserStories) {
        this.stories = stories
    }

    internal fun setStoryListener(storyListener: StoryListener) {
        this.storyListener = storyListener
    }

    private fun stopHandlers() {
        currentStoryPosition = 0
        if (::runnable.isInitialized) handler.removeCallbacks(runnable)
        stopExoPlayer()
    }

    internal fun setMarmozetsColors(backgroundColor: Int, progressColor: Int) {
        this.backgroundColor = backgroundColor
        this.progressColor = progressColor
    }
}