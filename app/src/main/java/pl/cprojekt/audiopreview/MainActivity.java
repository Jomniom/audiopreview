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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String path2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bbb.m4a";
        //String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bbb.m4aXXX";
        String path = "http://playready.directtaps.net/smoothstreaming/ISMAAACHEPR/Taxi3_AACHE.mp4";
        //String path = "http://10.201.224.22/tesy/puchacz_dzwonek.mp3";
        //final String path3 = "http://10.201.224.22/tesy/fonia.m4a";
/*
        CPAudioPreview audioPreview0 = (CPAudioPreview) findViewById(R.id.canvas0);
        audioPreview0.setSource(path);
        audioPreview0.create();

        CPAudioPreview audioPreview1 = (CPAudioPreview) findViewById(R.id.canvas1);
        audioPreview1.setSource(path);
        audioPreview1.create();
*/

        final CPAudioPreview audioPreview = (CPAudioPreview) findViewById(R.id.canvas2);

        //errors
        audioPreview.setOnError(new CPError() {
            @Override
            public void onError(String error) {
                super.onError(error);
                Log.e("X", "kurde błąd: " + error);
                //audioPreview.setSource(path3);
                //audioPreview.init();
            }
        });
        audioPreview.setOnCompletion(new CPCompletion() {
            @Override
            public void onEnd(MediaPlayer mp) {
                super.onEnd(mp);
                Log.i("X", "Zakończono odtwarzanie");
            }
        });
        //background player color
        audioPreview.setPlayerBgColor(Color.parseColor("#AB8674"));
        //kontrolki
        audioPreview.setPlayerCtrlColor(Color.parseColor("#5591BF"));
        //progress
        audioPreview.setPlayerProgressColor(Color.parseColor("#3333CC"));
        //tło progresu
        audioPreview.setPlayerCtrlBgColor(Color.parseColor("#5F7183"));

        //control mode
        //audioPreview.setMode(CPAudio.CTRL_MODE.CTRL_PLAY_PAUSE);
        audioPreview.setMode(CPAudio.CTRL_MODE.CTRL_PLAY_STOP);
        //set audio path
        audioPreview.setSource(path);
        //initialize
        boolean init = audioPreview.init();
        if (!init) {
            Log.w("X", "kurde nie udało się zainicjować odtwarzacza");
        }


    }
}
