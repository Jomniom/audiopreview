package pl.cprojekt.audiopreview;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import pl.cprojekt.cpaudiopreview.CPAudio;
import pl.cprojekt.cpaudiopreview.CPAudioPreview;
import pl.cprojekt.cpaudiopreview.CPCompletion;
import pl.cprojekt.cpaudiopreview.CPError;

public class MainActivity extends AppCompatActivity {
    CPAudioPreview audioPreview0, audioPreview1, audioPreview2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioPreview0 = (CPAudioPreview) findViewById(R.id.canvas0);
        audioPreview0.setAssetSource("so_bright_so_beautiful.mp3");
        audioPreview0.init();


        audioPreview1 = (CPAudioPreview) findViewById(R.id.canvas1);
        audioPreview1.setAssetSource("red_or_blue.mp3");
        audioPreview1.setMode(CPAudio.CTRL_MODE.CTRL_PLAY_PAUSE);

        audioPreview1.setPlayerBgColor(Color.parseColor("#353E38"));
        audioPreview1.setPlayerCtrlColor(Color.parseColor("#F05526"));
        audioPreview1.setPlayerProgressColor(Color.parseColor("#F05526"));
        audioPreview1.setPlayerCtrlBgColor(Color.parseColor("#6D8C7A"));

        audioPreview1.init();


        //final String path2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bbb.m4a";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bbb.m4a";

        audioPreview2 = (CPAudioPreview) findViewById(R.id.canvas2);

        //errors listener
        audioPreview2.setOnError(new CPError() {
            @Override
            public void onError(String error) {
                super.onError(error);
                Log.e("X", "kurde błąd: " + error);
                //audioPreview2.setSource(path3);
                //audioPreview2.init();
            }
        });
        //completion play listener
        audioPreview2.setOnCompletion(new CPCompletion() {
            @Override
            public void onEnd(MediaPlayer mp) {
                super.onEnd(mp);
                Log.i("X", "Zakończono odtwarzanie");
            }
        });
        //background player color
        audioPreview2.setPlayerBgColor(Color.parseColor("#9E9086"));
        //kontrolki
        audioPreview2.setPlayerCtrlColor(Color.parseColor("#ffffff"));
        //progress
        audioPreview2.setPlayerProgressColor(Color.parseColor("#23223A"));
        //tło progresu
        audioPreview2.setPlayerCtrlBgColor(Color.parseColor("#7F6755"));

        //control mode
        audioPreview2.setMode(CPAudio.CTRL_MODE.CTRL_PLAY_PAUSE);
        //audioPreview2.setMode(CPAudio.CTRL_MODE.CTRL_PLAY_STOP);
        //set audio path
        audioPreview2.setAssetSource("italian_stallion.mp3");
        //initialize
        boolean init = audioPreview2.init();
        if (!init) {
            Log.w("X", "kurde nie udało się zainicjować odtwarzacza");
        }


    }

    @Override
    protected void onPause() {
        super.onPause();

        if (audioPreview0 != null)
            audioPreview0.onPause();
        if (audioPreview1 != null)
            audioPreview1.onPause();
        if (audioPreview2 != null)
            audioPreview2.onPause();

    }
}
