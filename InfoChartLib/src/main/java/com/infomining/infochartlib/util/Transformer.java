package com.infomining.infochartlib.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * 좌표 변환계
 *
 * @author Dahun Kim
 */
public class Transformer {

    protected Matrix mOffsetMatrix = new Matrix();

    protected Matrix mValueMatrix = new Matrix();

    protected ViewPortHandler mViewPortHandler;

    public Transformer(ViewPortHandler viewPortHandler) {
        this.mViewPortHandler = viewPortHandler;
    }

    public void valuesToPixel(float[] pts) {
        mValueMatrix.mapPoints(pts);
        mOffsetMatrix.mapPoints(pts);
    }

    public void initValueMatrix(float xMin, float yMin, float deltaX, float deltaY) {
        float scaleX = mViewPortHandler.mChartWidth / deltaX;
        float scaleY = mViewPortHandler.mChartHeight / deltaY;

        mValueMatrix.postTranslate(-xMin, -yMin);
        mValueMatrix.postScale(scaleX, -scaleY);
    }

    public Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resized = Bitmap.createBitmap(bitmap, 0, 0,width, height, matrix, false);
        return resized;
    }

    public void initOffsetMatrix() {
        mOffsetMatrix.postTranslate(mViewPortHandler.getOffsetLeft(), mViewPortHandler.getChartHeight() - mViewPortHandler.getOffsetBottom());
    }

}
