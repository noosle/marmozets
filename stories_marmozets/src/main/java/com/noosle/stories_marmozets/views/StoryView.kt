package com.noosle.stories_marmozets.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.noosle.stories_marmozets.R
import com.noosle.stories_marmozets.objects.Marmozet
import com.noosle.stories_marmozets.objects.StoryListener
import com.noosle.stories_marmozets.objects.UserStories

@SuppressLint("ResourceType")
class StoryView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs), StoryListener {

    private var numPages: Int = 0
    private var mPager: ViewPager2
    private lateinit var allStories: List<UserStories>
    private lateinit var eventListener: EventListener

    private var progressColor: Int = 0
    private var backgroundColor: Int = 0

    init {
        inflate(context, R.layout.story_view, this)

        mPager = findViewById(R.id.pager)
        val pagerAdapter = ScreenSlidePagerAdapter(context as FragmentActivity)
        mPager.adapter = pagerAdapter
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.Marmozets,
            0, 0
        ).apply {
            try {
                backgroundColor = getColor(R.styleable.Marmozets_story_background_color, Color.BLACK)
                progressColor = getColor(R.styleable.Marmozets_story_progress_color, Color.WHITE)
            } finally {
                recycle()
            }
        }
    }

    fun passAllStories(allStories: List<UserStories>) {
        numPages = allStories.size
        this.allStories = allStories
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

        override fun getItemCount(): Int = numPages

        override fun createFragment(position: Int): Fragment {
            val fragment = StoryFragment()
            val storiesForCurrentUser = allStories[position]
            fragment.setStoriesForCurrentUser(storiesForCurrentUser)
            fragment.setStoryListener(this@StoryView)
            fragment.setMarmozetsColors(backgroundColor, progressColor)
            if (::eventListener.isInitialized) {
                eventListener.onCurrentUserStories(storiesForCurrentUser.marmozets)
                eventListener.onCurrentData(storiesForCurrentUser.user)
            }
            return fragment
        }
    }

    interface EventListener {
        fun onFinish() {}
        fun onDisplayingStory(marmozet: Marmozet) {}
        fun onCurrentUserStories(marmozets: List<Marmozet>) {}
        fun onCurrentData(data: Any?) {}
    }

    fun setEventListener(eventListener: EventListener) {
        this.eventListener = eventListener
    }

    override fun onUpdateStoryPage() {
        if (mPager.currentItem + 1 == numPages) {
            if (::eventListener.isInitialized) eventListener.onFinish()
            return
        }
        mPager.setCurrentItem(mPager.currentItem + 1, true)
    }

    override fun onCurrentStory(marmozet: Marmozet) {
        if (::eventListener.isInitialized)
            eventListener.onDisplayingStory(marmozet)
    }
}