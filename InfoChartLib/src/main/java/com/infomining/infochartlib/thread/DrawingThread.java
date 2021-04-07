package com.infomining.infochartlib.thread;

import android.graphics.Canvas;
import android.graphics.Color;

import com.infomining.infochartlib.dataProvider.ISurfaceHolder;
import com.infomining.infochartlib.renderer.RealTimeVitalRenderer;

/**
 * SurfaceView의 렌더링 스레드
 *
 * @author Dahun Kim
 */
public class DrawingThread extends Thread {

    private final ISurfaceHolder surfaceHolder;
    private final RealTimeVitalRenderer renderer;

    private boolean isRunning = false;
    private long previousTime;
    private final int fps = 70;

    public DrawingThread(ISurfaceHolder holder, RealTimeVitalRenderer renderer) {
        this.surfaceHolder = holder;
        this.renderer = renderer;
        previousTime = System.currentTimeMillis();
    }

    public void setRunning(boolean run) {
        isRunning = run;
    }

    @Override
    public void run() {
        Canvas canvas;
        long nowTime, elapsedTime, sleepTime;

        while (isRunning) {
            nowTime = System.currentTimeMillis();
            elapsedTime = nowTime - previousTime;
            sleepTime = (long) (1000f / fps - elapsedTime);

            canvas = null;

            try {
                canvas = surfaceHolder.lockCanvas();

                if (canvas == null) {
                    //Thread.sleep(1);
                    continue;
                }
                else if (sleepTime > 0) {
                    //Thread.sleep(sleepTime);
                }

                synchronized (surfaceHolder) {
                    // draw
                    canvas.drawColor(Color.WHITE);
                    renderer.drawVitalValue(canvas);
                }

            } catch (Exception e) {

            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                    previousTime = System.currentTimeMillis();
                }
            }
        }
    }
}
