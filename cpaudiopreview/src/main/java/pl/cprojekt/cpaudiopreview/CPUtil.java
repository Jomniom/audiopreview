package pl.cprojekt.cpaudiopreview;

import android.graphics.Point;
import android.media.MediaPlayer;

import java.io.File;

public class CPUtil {
    /**
     * 0-100 to 0-360
     */
    public static int progressToGrad(int val) {
        return (int) ((360 * val) / 100);
    }

    public static boolean isStreaming(String file) {
        if (file.contains("http://") || file.contains("https://")) {
            return true;
        } else {
            return false;
        }
    }

    public static void incrementDeg(Point pkt, int range, int step) {

        if (range > 360)
            range = 360;

        int x = pkt.x + step;
        if (x > 360)
            x = x - 360;

        pkt.x = x;

        int y = x + range;
        if (y > 360)
            y = y - 360;

        pkt.y = y;
    }

    public static float getDurationInSeconds(MediaPlayer mp) {
        return (mp.getDuration() / 1000.0f);
    }

    public static int getPrecent(float prec, int width) {
        return (int) ((prec * width) / 100);
    }

    public static int getPos(int curr, int total) {
        return ((curr * 100) / total);
    }

    public static boolean localFileExists(String path) {
        File f = new File(path);
        if (f.exists()) {
            return true;
        }
        return false;
    }
}
