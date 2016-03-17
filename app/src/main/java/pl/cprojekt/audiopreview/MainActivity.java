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

        audioPreview1.setPlayerBgColor(Color.parseColor("#AB8674"));
        audioPreview1.setPlayerCtrlColor(Color.parseColor("#5591BF"));
        audioPreview1.setPlayerProgressColor(Color.parseColor("#3333CC"));
        audioPreview1.setPlayerCtrlBgColor(Color.parseColor("#5F7183"));

        audioPreview1.init();




        //final String path2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bbb.m4a";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bbb.m4a";
        //String path = "http://playready.directtaps.net/smoothstreaming/ISMAAACHEPR/Taxi3_AACHE.mp4";
        //String path = "http://10.201.224.22/tesy/puchacz_dzwonek.mp3";
        //final String path3 = "http://10.201.224.22/tesy/fonia.m4a";
        //path = "http://soundbible.com/grab.php?id=1632&type=wav";
/*
        CPAudioPreview audioPreview0 = (CPAudioPreview) findViewById(R.id.canvas0);
        audioPreview0.setSource(path);
        audioPreview0.create();

        CPAudioPreview audioPreview1 = (CPAudioPreview) findViewById(R.id.canvas1);
        audioPreview1.setSource(path);
        audioPreview1.create();
*/

        audioPreview2 = (CPAudioPreview) findViewById(R.id.canvas2);

        //errors
        audioPreview2.setOnError(new CPError() {
            @Override
            public void onError(String error) {
                super.onError(error);
                Log.e("X", "kurde błąd: " + error);
                //audioPreview2.setSource(path3);
                //audioPreview2.init();
            }
        });
        audioPreview2.setOnCompletion(new CPCompletion() {
            @Override
            public void onEnd(MediaPlayer mp) {
                super.onEnd(mp);
                Log.i("X", "Zakończono odtwarzanie");
            }
        });
        //background player color
//        audioPreview2.setPlayerBgColor(Color.parseColor("#AB8674"));
//        //kontrolki
//        audioPreview2.setPlayerCtrlColor(Color.parseColor("#5591BF"));
//        //progress
//        audioPreview2.setPlayerProgressColor(Color.parseColor("#3333CC"));
//        //tło progresu
//        audioPreview2.setPlayerCtrlBgColor(Color.parseColor("#5F7183"));

        //control mode
        audioPreview2.setMode(CPAudio.CTRL_MODE.CTRL_PLAY_PAUSE);
        //audioPreview2.setMode(CPAudio.CTRL_MODE.CTRL_PLAY_STOP);
        //set audio path
        ///audioPreview2.setSource(path);
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
        if (audioPreview2 != null)
            audioPreview2.onPause();
    }
}
