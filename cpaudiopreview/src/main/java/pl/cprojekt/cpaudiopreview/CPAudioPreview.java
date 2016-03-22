package pl.cprojekt.cpaudiopreview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class CPAudioPreview extends CPAudio {
    public CPAudioPreview(Context context) {
        super(context);
    }

    public CPAudioPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CPAudioPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Set audio source.
     *
     * @param path local file system path or from url
     */
    public void setSource(String path) {
        super.setSource(path);
    }

    public void setAssetSource(String fileName) {
        super.setAssetSource(fileName);
    }

    /**
     * Set error handler
     *
     * @param err error class
     */
    public void setOnError(CPError err) {
        super.setOnError(err);
    }

    /**
     * Evant end of play
     *
     * @param completion
     */
    public void setOnCompletion(CPCompletion completion) {
        super.setOnCompletion(completion);
    }

    /**
     * Initialize audio
     *
     * @return initialization result
     */
    public boolean init() {
        return super.create();
    }

    /**
     * Set mode:
     * PLAY -> PAUSE (STOP)
     * PLAY -> STOP
     *
     * @param mode
     */
    public void setMode(CTRL_MODE mode) {
        super.setCtrlMode(mode);
    }

    /**
     * Set circle background color
     *
     * @param color
     */
    public void setPlayerCtrlBgColor(int color) {
        super.setPlayerCtrlBgColor(color);
    }

    /**
     * Set player background color
     *
     * @param color
     */
    public void setPlayerBgColor(int color) {
        super.setPlayerBgColor(color);
    }

    /**
     * Set player controls color
     *
     * @param color
     */
    public void setPlayerCtrlColor(int color) {
        super.setPlayerCtrlColor(color);
    }

    /**
     * Set player progress color
     *
     * @param color
     */
    public void setPlayerProgressColor(int color) {
        super.setPlayerProgressColor(color);
    }

    public void setVolume(float leftVolume, float rightVolume) {
        super.setVolume(leftVolume, rightVolume);
    }

    /**
     * Call onPause
     */
    public void onPause() {
        super.onPause();
    }

    /**
     * show player
     */
    public void show() {
        setVisibility(View.VISIBLE);
    }

    /**
     * hide player
     */
    public void hide() {
        super.setVisibility(View.GONE);
    }

    public void invalide() {
        super.invalidate();
    }
}
