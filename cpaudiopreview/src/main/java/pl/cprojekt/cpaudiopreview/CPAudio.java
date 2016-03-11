package pl.cprojekt.cpaudiopreview;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.IOException;


//todo oprogramować obrót

public class CPAudio extends CPBaseView implements View.OnClickListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener {

    private final String TAG = "CPAudio";
    private boolean firstPrepared = true;
    private MediaPlayer mp = null;
    private String audioSrc = null;
    private CPError err = null;
    private CPCompletion completion = null;
    private boolean isStreaming = false;
    private boolean isAutoPlay = false;
    private AUDIO_STATE audioState = AUDIO_STATE.IDLE;
    private int duration = -1;
    private boolean progress = false;
    private int progressStartPos = 0;
    private CTRL_MODE ctrlMode = CTRL_MODE.CTRL_PLAY_STOP;

    public CPAudio(Context context) {
        super(context);
        initialize();
    }

    public CPAudio(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public CPAudio(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public CPAudio(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    private void initialize() {
        setOnClickListener(this);
        mp = new MediaPlayer();
        mp.setOnCompletionListener(this);
        mp.setOnPreparedListener(this);
        mp.setOnErrorListener(this);
        mp.setOnBufferingUpdateListener(this);
    }

    protected void setSource(String src) {
        audioSrc = src;
        isStreaming = CPUtil.isStreaming(src);
    }

    protected void setCtrlMode(CTRL_MODE mode) {
        ctrlMode = mode;
    }

    protected void setOnError(CPError err) {
        this.err = err;
    }

    protected void setOnCompletion(CPCompletion completion) {
        this.completion = completion;
    }

    private void reset() {
        if (mp == null)
            return;

        if (mp.isPlaying())
            mp.stop();

        mp.reset();
        audioState = AUDIO_STATE.IDLE;
    }

    protected boolean create() {
        Log.i(TAG, "____create()");
        if (audioSrc == null) {
            fireError("Set audio source. audioUri is null");
            return false;
        }

        reset();

        setProgress(progressStartPos);

        try {
            mp.setDataSource(audioSrc);
            audioState = AUDIO_STATE.INITIALIZED;
        } catch (IOException e) {
            e.printStackTrace();
            fireError(e.toString());
            return false;
        }
        audioState = AUDIO_STATE.INITIALIZED;
        //walidacja
        audioState = AUDIO_STATE.PREPARING;
        if (isStreaming) {
            mp.prepareAsync();
            loaderStart();
        } else {
            boolean exist = CPUtil.localFileExists(audioSrc);
            if (!exist) {
                fireError("Local file does not exist");
                return false;
            }

            try {
                mp.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                fireError(e.toString());
                return false;
            }
        }

        return true;
    }

    protected void setVolume(float leftVolume, float rightVolume) {
        mp.setVolume(leftVolume, rightVolume);
    }

    private void fireError(String error) {
        if (err == null)
            return;
        Log.e(TAG, error);
        err.onError(error);
    }

    private void fireCompletion(MediaPlayer mp) {
        if (completion == null)
            return;
        completion.onEnd(mp);
    }

    private void clickModePlayPause() {
        if (audioState == AUDIO_STATE.STARTED) {
            pause();
        }
//        if (audioState == AUDIO_STATE.STOPPED) {
//            play();
//        }
    }

    private void clickModePlayStop() {
        if (audioState == AUDIO_STATE.STARTED) {
            stop();
            return;
        }

        if (audioState == AUDIO_STATE.STOPPED) {
            mp.reset();
            create();
        }
    }

    @Override
    public void onClick(View v) {
        //state after error IO
        if (audioState == AUDIO_STATE.IDLE) {
            if (audioSrc != null)
                create();
            return;
        }

        if (audioState == AUDIO_STATE.PREPARED || audioState == AUDIO_STATE.PAUSED || audioState == AUDIO_STATE.PLAYBACK_COMPLETED) {
            play();
            return;
        }

        if (ctrlMode == CTRL_MODE.CTRL_PLAY_PAUSE)
            clickModePlayPause();

        if (ctrlMode == CTRL_MODE.CTRL_PLAY_STOP)
            clickModePlayStop();

    }

    private void startProgress() {
        progress = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progress) {
                    int currPos = mp.getCurrentPosition();
                    int v = CPUtil.getPos(currPos, duration);
                    setProgress(v);//0-100
                    Message msg = progressHandler.obtainMessage();
                    progressHandler.sendMessage(msg);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void stopProgress() {
        progress = false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (audioState != AUDIO_STATE.STARTED) {
            Log.i(TAG, "WYJSCIE " + audioState);
            return;
        }

        audioState = AUDIO_STATE.PLAYBACK_COMPLETED;
        setProgress(progressStartPos);
        stopProgress();
        showPlay();
        mp.seekTo(0);
        fireCompletion(mp);

//        if(ctrlMode == CTRL_MODE.CTRL_PLAY_PAUSE)
//            stop();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        if (extra == MediaPlayer.MEDIA_ERROR_IO) {
            Log.e(TAG, "Error MEDIA_ERROR_IO");
            reset();//idle
            loaderStop();
            showPlay();
            firstPrepared = false;

        }

        fireError("onError what: " + what + ", extra: " + extra + ", state: " + audioState);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp1) {
        /**
         * Gotowy do odtwarzania
         */
        loaderStop();
        audioState = AUDIO_STATE.PREPARED;
        showPlay();
        duration = mp.getDuration();

        if (!firstPrepared) {
            play();
        }
        firstPrepared = false;

        if (isAutoPlay) {
            play();
        }

    }

    private void play() {

        if (!(audioState == AUDIO_STATE.PREPARED || audioState == AUDIO_STATE.PAUSED || audioState == AUDIO_STATE.PLAYBACK_COMPLETED)) {
            fireError("Bad audio state for play. audioState: " + audioState);
            return;
        }

        mp.start();
        audioState = AUDIO_STATE.STARTED;

        if (ctrlMode == CTRL_MODE.CTRL_PLAY_PAUSE)
            showPause();

        if (ctrlMode == CTRL_MODE.CTRL_PLAY_STOP)
            showStop();

        startProgress();
    }

    private void pause() {
        if (audioState != AUDIO_STATE.STARTED) {
            fireError("Bad audio state for pause. audioState: " + audioState);
            return;
        }
        mp.pause();
        audioState = AUDIO_STATE.PAUSED;
        showPlay();
    }

    private void stop() {
        stopProgress();
        audioState = AUDIO_STATE.STOPPED;
        mp.stop();
        setProgress(progressStartPos);
        showPlay();
    }

    protected void destroy() {
        stopProgress();
        if (mp != null) {
            mp.release();
            mp = null;
        }
        audioState = AUDIO_STATE.IDLE;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (percent == 100)
            return;
        loaderStart();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    protected void onPause() {
        if (ctrlMode == CTRL_MODE.CTRL_PLAY_PAUSE) {
            if (audioState == AUDIO_STATE.STARTED) {
                pause();
            }
            return;
        }

        if (ctrlMode == CTRL_MODE.CTRL_PLAY_STOP) {
            if (audioState == AUDIO_STATE.STARTED || audioState == AUDIO_STATE.PLAYBACK_COMPLETED) {
                stop();
            }
        }
    }

    public enum CTRL_MODE {
        CTRL_PLAY_PAUSE, CTRL_PLAY_STOP
    }

    private enum AUDIO_STATE {
        IDLE, INITIALIZED, PREPARING, PREPARED, STARTED, STOPPED, PAUSED, PLAYBACK_COMPLETED
    }
}
