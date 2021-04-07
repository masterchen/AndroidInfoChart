package com.infomining.infochartlib.dataProvider;

import android.graphics.Canvas;

/**
 * SurfaceView Holder의 인터페이스 정의
 * @author Dahun Kim
 */
public interface ISurfaceHolder {

    void unlockCanvasAndPost(Canvas canvas);
    Canvas lockCanvas();

}
