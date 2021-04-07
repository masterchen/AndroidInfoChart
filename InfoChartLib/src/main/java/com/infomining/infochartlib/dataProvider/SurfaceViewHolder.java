package com.infomining.infochartlib.dataProvider;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * SurfaceView Holder Class
 * @author Dahun Kim
 */
public class SurfaceViewHolder implements ISurfaceHolder {

    private final SurfaceHolder surfaceHolder;

    public SurfaceViewHolder(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
    }

    @Override
    public void unlockCanvasAndPost(Canvas canvas) {
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public Canvas lockCanvas() {
        return surfaceHolder.lockCanvas();
    }
}
