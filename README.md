# Marmozets. Android component for user stories


## App
![](marmozets_gif.gif)

## Setup
- Add it in your root build.gradle at the end of repositories:
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

- Add the dependency
```
dependencies {
	   implementation 'com.github.noosle:marmozets:0.1.0'
	}
```

- Add StoryView into your layout
```
<com.noosle.stories_marmozets.views.StoryView
        android:id="@+id/story_view"
        android:layout_width="match_parent"
        app:story_background_color="@color/black"
        app:story_progress_color="@color/white"
        android:layout_height="match_parent"/>
    <!-- story_background_color attribute is not important. Default value is black -->
    <!-- story_progress_color attribute is not important. Default value is white -->
```

## Navigation
* Single tap on left side of screen - run story from start
* Single tap on right side of screen - show next story/next user stories
* Double tap on left side of screen - show previous story
* Press and hold on the middle of screen - pause story

## Example of usage
```
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val storyView = findViewById<StoryView>(R.id.story_view)
        storyView.passAllStories(mockUsers)
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
```

## Page transformer
Use ```storyView.setPageTransformer(storiesPageTransformerType)``` to change default animation between pages 

Available animations:

1) ```StoriesPageTransformerType.Cube```
![](marmozets_cube_gif.gif)
2) ```StoriesPageTransformerType.ZoomOut```
![](marmozets_zoomout_gif.gif)
3) ```StoriesPageTransformerType.Depth```
![](marmozets_depth_gif.gif)
   
## Customize your stories
See type of story object [here](https://github.com/noosle/marmozets/blob/master/app/src/main/java/com/noosle/marmozets/Mock.kt)
