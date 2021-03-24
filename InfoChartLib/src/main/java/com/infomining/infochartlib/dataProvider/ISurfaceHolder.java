package com.infomining.infochartlib.dataProvider;

import android.graphics.Canvas;

public interface ISurfaceHolder {

    void unlockCanvasAndPost(Canvas canvas);
    Canvas lockCanvas();

}
