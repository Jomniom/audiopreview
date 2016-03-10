package pl.cprojekt.cpaudiopreview;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.IOException;

//todo błedy: brak sieci->jest sieć->play
//todo oprogramować obrót
public class CPAudio extends CPBaseView implements View.OnClickListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener {

    private final String TAG = "CPAudioPreview";
    private boolean firstPrepared = true;
    private MediaPlayer mp = null;
    private String audioSrc = null;
    private CPError err = null;
    private CPCompletion completion = null;
    private boolean isStreaming = false;//todo to bundle saveinstance
    private boolean isAutoPlay = false;//todo to bundle saveinstance
    private AUDIO_STATE audioState = AUDIO_STATE.IDLE;//todo to bundle saveinstance
    private int duration = -1;//todo to bundle saveinstance
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
        isStreaming = CPUtil.isStreaming(src);//todo save to bundle
    }

    protected void setCtrlMode(CTRL_MODE mode) {
        ctrlMode = mode;
    }

    protected void setOnError(CPError err) {
        this.err = err;//todo save to bundle
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
            return;
        }
    }

    private void clickModePlayStop() {
        Log.i("X", "ctrl clickModePlayStop()");
        if (audioState == AUDIO_STATE.STARTED) {
            stop();
            return;
        }

        if (audioState == AUDIO_STATE.STOPPED) {
            //todo prepare
            mp.reset();
            create();
            return;
        }
    }

    @Override
    public void onClick(View v) {

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
        Log.i(TAG, "____ONCOMPLETION że zakończono");
        if (audioState != AUDIO_STATE.STARTED) {
            Log.i(TAG, "WYJSCIE " + audioState);
            return;
        }

        audioState = AUDIO_STATE.PLAYBACK_COMPLETED;
        setProgress(progressStartPos);
        stopProgress();
        showPlay();
        fireCompletion(mp);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        if (audioState == AUDIO_STATE.PREPARING) {

        }


        if (what == MediaPlayer.MEDIA_ERROR_IO) {
            reset();
            if (audioSrc != null) {
                //todo sprawdzanie periodyczne sieci, jeśli jest to create()
            }
        }

        fireError("onError what: " + what + ", state: " + audioState);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp1) {
        /**
         * Gotowy do odtwarzania
         */
        loaderStop();

        Log.i("X", "________ONPREPARED gotowy do play");
//        if (mp.isPlaying())
//            return;

        audioState = AUDIO_STATE.PREPARED;

        showPlay();

        duration = mp.getDuration();

        if (ctrlMode == CTRL_MODE.CTRL_PLAY_STOP) {
            //start (poza 1 razem gdy inicjalizacja)
            if (!firstPrepared) {
                play();
            }
            firstPrepared = false;
        }

        if (isAutoPlay) {
            play();
        }

    }

    private void play() {
        Log.i("X", "ctrl PLAY");
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
        Log.i("X", "ctrl PAUSE");
        if (audioState != AUDIO_STATE.STARTED) {
            fireError("Bad audio state for pause. audioState: " + audioState);
            return;
        }
        mp.pause();
        audioState = AUDIO_STATE.PAUSED;
        showPlay();
    }

    private void stop() {
        Log.i("X", "ctrl STOP");
        stopProgress();
        audioState = AUDIO_STATE.STOPPED;
        mp.stop();
        setProgress(progressStartPos);
        showPlay();
    }

    public void destroy() {
        stopProgress();
        if (mp != null) {
            mp.release();
            mp = null;
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //Log.i("x", "Buforowanie percent: " + percent);
        if (percent == 100)
            return;

        loaderStart();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    public enum CTRL_MODE {
        CTRL_PLAY_PAUSE, CTRL_PLAY_STOP
    }

    private enum AUDIO_STATE {
        IDLE, INITIALIZED, PREPARING, PREPARED, STARTED, STOPPED, PAUSED, PLAYBACK_COMPLETED
    }
}
