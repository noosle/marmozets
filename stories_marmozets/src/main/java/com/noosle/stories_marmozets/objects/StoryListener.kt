package com.noosle.stories_marmozets.objects

interface StoryListener {
    fun onUpdateStoryPage()
    fun onCurrentStory(marmozet: Marmozet)
}