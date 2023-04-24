package com.noosle.stories_marmozets.objects.page_transformer

sealed class StoriesPageTransformerType {
    object ZoomOut : StoriesPageTransformerType()
    object Cube : StoriesPageTransformerType()
    object Depth : StoriesPageTransformerType()
}