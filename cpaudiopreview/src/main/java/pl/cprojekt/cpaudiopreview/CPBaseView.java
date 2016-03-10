package pl.cprojekt.cpaudiopreview;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;


public class CPBaseView extends View {

    protected Context ctx;
    protected Handler progressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            invalidate();
        }
    };
    private Paint paintBgCircle, paintBgBack, paintProg, paintLoader, playPaint;
    private int colorBgCircle, colorBgBack, colorProg, colorLoader, colorControl;
    private float circleStrokeWidth = 14f;
    private Path playPath = new Path();
    private Path stopPath = new Path();
    private Path pausePathA = new Path(), pausePathB = new Path();
    private Point loaderPos = new Point(0, 0);
    //długość loadera w stopniach kątowych
    private int loaderWidth = 100;
    //przesunięcie, obrót loadera
    private int loaderStop = 3;
    //prędkość loadera
    private long loaderInterval = 20;
    private Handler loaderHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            invalidate();
        }
    };
    private boolean loaderRun = false;
    private int progress = 0;
    private int margin = 16;
    private RectF rectFA, rectFC, rectFLoader;
    private int leftB;
    private int topB;
    private int radiusB;
    private VIEW_STATE state = VIEW_STATE.NONE;

    public CPBaseView(Context context) {
        super(context);
        initialize(context);
    }

    public CPBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public CPBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CPBaseView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context);
    }

    //0-100
    protected void setProgress(int val) {
        progress = CPUtil.progressToGrad(val);
    }

    private void initialize(Context ctx) {
        this.ctx = ctx;
        initColors();
        iniShapes();
        calculate();
    }

    private void initColors() {
        colorBgCircle = ContextCompat.getColor(ctx, R.color.colorBgCirce);
        colorBgBack = ContextCompat.getColor(ctx, R.color.colorBgBack);
        colorProg = ContextCompat.getColor(ctx, R.color.colorProg);
        colorLoader = ContextCompat.getColor(ctx, R.color.colorLoader);
        colorControl = ContextCompat.getColor(ctx, R.color.colorControl);
    }

    protected void setPlayerLoaderColor(int color) {
        colorLoader = color;
        paintLoader.setColor(colorLoader);
    }

    protected void setPlayerProgressColor(int color) {
        colorProg = color;
        paintProg.setColor(colorProg);
    }

    protected void setPlayerCtrlColor(int color) {
        colorControl = color;
        playPaint.setColor(colorControl);
    }

    protected void setPlayerBgColor(int color) {
        colorBgBack = color;
        paintBgBack.setColor(colorBgBack);
    }

    protected void setPlayerCtrlBgColor(int color) {
        colorBgCircle = color;
        paintBgCircle.setColor(colorBgCircle);
    }

    private void calculate() {
        //grubość obręczy
        circleStrokeWidth = CPUtil.getPrecent(6f, getWidth());
        paintBgCircle.setStrokeWidth(circleStrokeWidth);
        //margines
        margin = CPUtil.getPrecent(7f, getWidth());
        //grubość kreski progresu i loadera
        circleStrokeWidth = CPUtil.getPrecent(6f, getWidth());
        paintProg.setStrokeWidth(circleStrokeWidth);
        paintLoader.setStrokeWidth(circleStrokeWidth);

        Point playW1 = new Point();
        Point playW2 = new Point();
        Point playW3 = new Point();

        Point stopW1 = new Point();
        Point stopW2 = new Point();
        Point stopW3 = new Point();
        Point stopW4 = new Point();

        Point pauseW1 = new Point();
        Point pauseW2 = new Point();
        Point pauseW3 = new Point();
        Point pauseW4 = new Point();

        Point pauseW5 = new Point();
        Point pauseW6 = new Point();
        Point pauseW7 = new Point();
        Point pauseW8 = new Point();

        //Obliczenie obręczy odtwarzacza tła
        rectFA.left = margin;
        rectFA.top = margin;
        rectFA.right = getWidth() - margin;
        rectFA.bottom = getHeight() - margin;

        //Obliczenie tła całości
        leftB = getWidth() / 2;
        topB = getHeight() / 2;
        radiusB = (getWidth() / 2) - 2;

        //progres
        rectFC.left = margin;
        rectFC.top = margin;
        rectFC.right = getWidth() - margin;
        rectFC.bottom = getHeight() - margin;

        //loader
        rectFLoader.left = margin;
        rectFLoader.top = margin;
        rectFLoader.right = getWidth() - margin;
        rectFLoader.bottom = getHeight() - margin;

        //play
        int radiusPlay = (int) ((getWidth() * 0.50) / 2);
        int left = (getWidth() / 2);
        int top = (getHeight() / 2);
        //bok trójkąta
        int l = (int) ((3 * radiusPlay) / Math.sqrt(3));

        playW1.x = left + radiusPlay;
        playW1.y = top;

        playW2.x = (left - (radiusPlay / 2));
        playW2.y = (top + (l / 2));

        playW3.x = (left - (radiusPlay / 2));
        playW3.y = (top - (l / 2));

        playPath.moveTo(playW1.x, playW1.y);
        playPath.lineTo(playW2.x, playW2.y);
        playPath.lineTo(playW3.x, playW3.y);
        playPath.close();

        //stop
        int radiusStop = (int) ((getWidth() * 0.36) / 2);

        stopW1.x = left - radiusStop;
        stopW1.y = top - radiusStop;
        stopW2.x = left + radiusStop;
        stopW2.y = top - radiusStop;
        stopW3.x = left + radiusStop;
        stopW3.y = top + radiusStop;
        stopW4.x = left - radiusStop;
        stopW4.y = top + radiusStop;
        stopPath.moveTo(stopW1.x, stopW1.y);
        stopPath.lineTo(stopW2.x, stopW2.y);
        stopPath.lineTo(stopW3.x, stopW3.y);
        stopPath.lineTo(stopW4.x, stopW4.y);
        stopPath.close();

        //pause 1
        int a = (int) ((2 * radiusStop) / Math.sqrt(2));
        int a2 = (a / 2);
        int w = a / 3;
        pauseW1.x = left - a2;
        pauseW1.y = top - a2;
        pauseW2.x = left - (w / 2);
        pauseW2.y = top - a2;
        pauseW3.x = left - (w / 2);
        pauseW3.y = top + a2;
        pauseW4.x = left - a2;
        pauseW4.y = top + a2;
        pausePathA.moveTo(pauseW1.x, pauseW1.y);
        pausePathA.lineTo(pauseW2.x, pauseW2.y);
        pausePathA.lineTo(pauseW3.x, pauseW3.y);
        pausePathA.lineTo(pauseW4.x, pauseW4.y);
        pausePathA.close();
        //pause 2
        pauseW5.x = left + (w / 2);
        pauseW5.y = top - a2;
        pauseW6.x = left + a2;
        pauseW6.y = top - a2;
        pauseW7.x = left + a2;
        pauseW7.y = top + a2;
        pauseW8.x = left + (w / 2);
        pauseW8.y = top + a2;
        pausePathB.moveTo(pauseW5.x, pauseW5.y);
        pausePathB.lineTo(pauseW6.x, pauseW6.y);
        pausePathB.lineTo(pauseW7.x, pauseW7.y);
        pausePathB.lineTo(pauseW8.x, pauseW8.y);
        pausePathB.close();

    }

    private void iniShapes() {

        paintBgCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        setPlayerCtrlBgColor(colorBgCircle);
        paintBgCircle.setStyle(Paint.Style.STROKE);
        paintBgCircle.setStrokeWidth(circleStrokeWidth);
        paintBgCircle.setStrokeCap(Paint.Cap.ROUND);

        //tło
        paintBgBack = new Paint(Paint.ANTI_ALIAS_FLAG);
        setPlayerBgColor(colorBgBack);

        //progress
        paintProg = new Paint(Paint.ANTI_ALIAS_FLAG);
        setPlayerProgressColor(colorProg);
        paintProg.setStyle(Paint.Style.STROKE);
        paintProg.setStrokeWidth(circleStrokeWidth);
        paintProg.setStrokeCap(Paint.Cap.ROUND);

        //loader
        paintLoader = new Paint(Paint.ANTI_ALIAS_FLAG);
        setPlayerLoaderColor(colorLoader);
        paintLoader.setStyle(Paint.Style.STROKE);
        paintLoader.setStrokeWidth(circleStrokeWidth);
        paintLoader.setStrokeCap(Paint.Cap.ROUND);

        //kształt A tło obręczy odtwarzacza
        rectFA = new RectF();
        //progress
        rectFC = new RectF();
        //loader
        rectFLoader = new RectF();
        //kszałt play
        playPath.setFillType(Path.FillType.EVEN_ODD);
        //paint kontrolek play i stop
        playPaint = new Paint();
        playPaint.setStrokeWidth(4);
        setPlayerCtrlColor(colorControl);
        playPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        playPaint.setAntiAlias(true);

        //kształt stop
        stopPath.setFillType(Path.FillType.EVEN_ODD);
    }

    protected void loaderStart() {
        loaderRun = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (loaderRun) {
                    CPUtil.incrementDeg(loaderPos, loaderWidth, loaderStop);
                    Message msg = loaderHandler.obtainMessage();
                    loaderHandler.sendMessage(msg);

                    try {
                        Thread.sleep(loaderInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    protected void loaderStop() {
        loaderRun = false;
        invalidate();
    }

    protected void showPlay() {
        state = VIEW_STATE.PLAY;
        invalidate();
    }

    protected void showPause() {
        state = VIEW_STATE.PAUSE;
        invalidate();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("stateInstance", super.onSaveInstanceState());
        bundle.putSerializable("state", state);
        bundle.putBoolean("loaderRun", loaderRun);
        bundle.putInt("progress", progress);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable saveState) {
        if (saveState instanceof Bundle) {
            Bundle bundle = (Bundle) saveState;
            state = (VIEW_STATE) bundle.get("state");
            loaderRun = bundle.getBoolean("loaderRun");
            progress = bundle.getInt("progress");
            if (loaderRun) {
                loaderStart();
            }
            saveState = bundle.getParcelable("stateInstance");
        }
        super.onRestoreInstanceState(saveState);
    }

    protected void showStop() {
        state = VIEW_STATE.STOP;
        invalidate();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //tło odtwarzacza B
        canvas.drawCircle(leftB, topB, radiusB, paintBgBack);
        //obręcz odtwarzacza A
        canvas.drawArc(rectFA, 0, 360, false, paintBgCircle);

        if (loaderRun == false) {
            if (state == VIEW_STATE.PLAY)
                canvas.drawPath(playPath, playPaint);

            if (state == VIEW_STATE.STOP)
                canvas.drawPath(stopPath, playPaint);

            if (state == VIEW_STATE.PAUSE) {
                canvas.drawPath(pausePathA, playPaint);
                canvas.drawPath(pausePathB, playPaint);
            }
        }


        //progress
        if (state == VIEW_STATE.PLAY || state == VIEW_STATE.PAUSE || state == VIEW_STATE.STOP) {
            canvas.drawArc(rectFC, 0, progress, false, paintProg);
        }

        //loader
        if (loaderRun) {
            canvas.drawArc(rectFLoader, loaderPos.x, loaderWidth, false, paintLoader);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        calculate();
    }

    private enum VIEW_STATE {
        NONE, STOP, PLAY, PAUSE
    }
}
