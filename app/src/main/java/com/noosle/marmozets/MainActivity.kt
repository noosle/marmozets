package com.noosle.marmozets

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.noosle.stories_marmozets.objects.Marmozet
import com.noosle.stories_marmozets.objects.page_transformer.StoriesPageTransformerType
import com.noosle.stories_marmozets.views.StoryView

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val storyView = findViewById<StoryView>(R.id.story_view)
        storyView.passAllStories(mockUsers)
        storyView.setPageTransformer(StoriesPageTransformerType.Cube)
        storyView.setEventListener(object : StoryView.EventListener {

            override fun onDisplayingStory(marmozet: Marmozet) {
                super.onDisplayingStory(marmozet)
            }

            override fun onCurrentData(data: Any?) {
                super.onCurrentData(data)
            }

            override fun onCurrentUserStories(marmozets: List<Marmozet>) {
                super.onCurrentUserStories(marmozets)
            }

            override fun onCloseButtonPressed() {
                finish()
            }

            override fun onFinish() {
                finish()
            }
        })
    }
}