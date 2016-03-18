# Audiopreview
## Simple audio player

<img src="screen_a.jpg" width="400"/>

### Usage
Add to layout xml.
```xml
<pl.cprojekt.cpaudiopreview.CPAudioPreview
        android:id="@+id/canvas"
        android:layout_width="100dp"
        android:layout_height="100dp"/>
```
Create audioPreview object.
```java
CPAudioPreview audioPreview = (CPAudioPreview) findViewById(R.id.canvas);
audioPreview.setAssetSource("so_bright_so_beautiful.mp3");
audioPreview.init();
```
