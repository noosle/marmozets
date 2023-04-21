package com.noosle.stories_marmozets.objects

data class UserStories(val user: Any? = null, val marmozets: List<Marmozet>)

data class Marmozet(
    val url: String,
    val actionLink: String,
    val duration: Long = 2000,
    val type: String = "image"
)