package com.infomining.infochartlib.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.infomining.infochartlib.dataProvider.IVitalChartDataProvider;
import com.infomining.infochartlib.util.Transformer;

import static com.infomining.infochartlib.chart.RealTimeVitalChart.EMPTY_DATA;


public class RealTimeVitalRenderer {

    /**
     * 차트 데이터 프로바이더
     */
    protected IVitalChartDataProvider mChart;

    /**
     * 차트 Draw Pointer (index)
     */
    protected int mDrawPointer = 0;

    /**
     * 차트 Remove Pointer (index)
     */
    protected int mRemovePointer = 0;

    /**
     * Transformer
     */
    protected Transformer mTransformer;

    /**
     * Draw Paint
     */
    protected Paint mRenderPaint;

    /**
     * Alpha 그라데이션 비율
     * (지워지는 영역에서 그라데이션이 차지하는 비율)
     */
    final protected float GRADIENT_RATIO = 0.1f;

    /**
     * 전체 중 지워지는 부분의 갯수
     */
    protected int mRemoveRangeCount = 0;


    public RealTimeVitalRenderer(IVitalChartDataProvider chart) {
        this.mChart = chart;
        mTransformer = this.mChart.getTransformer();
        mRenderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        updateSettings();

        //mRenderPaint.setStyle(Paint.Style.STROKE);
    }

    public void updateSettings() {
        mDrawPointer = 0;
        mRemovePointer = mChart.getTotalRangeCount() - (int)(mChart.getTotalRangeCount() * (1 - mChart.getRefreshGraphInterval()));
        //mRemoveRangeCount = (int) (mChart.getTotalRangeCount() * mChart.getRefreshGraphInterval());
    }

    public void readyForUpdateData() {
        mDrawPointer++;
        mRemovePointer++;

        if(mDrawPointer >= mChart.getTotalRangeCount()) {
            mDrawPointer = 0;
        }
        if(mRemovePointer >= mChart.getTotalRangeCount()) {
            mRemovePointer = 0;
        }
    }

    public void drawVitalValue(Canvas canvas) {
        switch (mChart.getLineMode()) {
            case LINEAR:
                drawLinear(canvas);
                break;

            case CUBIC:
                drawCubic(canvas);
                break;
        }
    }

    /**
     * 라인 버퍼
     */
    protected float[] mLineBuffer = new float[4];
    float firstY, secondY;
    int x, alphaCount = 0;

    private void drawLinear(Canvas canvas) {
        alphaCount = 0;
        mRenderPaint.setColor(mChart.getLineColor());
        mRenderPaint.setStrokeWidth(mChart.getLineWidth());

        for (x = 1; x < mChart.getTotalRangeCount(); x++) {
            firstY = mChart.getRealTimeDataList()[x == 0 ? 0 : x - 1];
            secondY = mChart.getRealTimeDataList()[x];
            mRemoveRangeCount = (mDrawPointer < mRemovePointer) ? mRemovePointer - mDrawPointer : mRemovePointer;

            if(firstY == EMPTY_DATA || secondY == EMPTY_DATA)
                continue;

            mLineBuffer[0] = x == 0 ? 0 : x - 1;
            mLineBuffer[1] = firstY;
            mLineBuffer[2] = x;
            mLineBuffer[3] = secondY;

            mTransformer.valuesToPixel(mLineBuffer);

            if(x >= mRemovePointer && alphaCount < mRemoveRangeCount) {
                mRenderPaint.setAlpha(calculateAlphaRatio(mRemoveRangeCount , alphaCount));
                alphaCount++;
            }

            canvas.drawLines(mLineBuffer, mRenderPaint);
        }

        if(mChart.getEnabledValueCircleIndicator()) {
            mRenderPaint.setColor(mChart.getValueCircleIndicatorColor());

            mLineBuffer[0] = mDrawPointer;
            mLineBuffer[1] = mChart.getRealTimeDataList()[mDrawPointer];
            mTransformer.valuesToPixel(mLineBuffer);

            canvas.drawCircle(mLineBuffer[0], mLineBuffer[1], mChart.getValueCircleIndicatorRadius(), mRenderPaint);
        }



    }

    private void drawCubic(Canvas canvas) {

    }

    private int calculateAlphaRatio(int totalCount, int count) {
        float ratio = (float)count / totalCount;
        return (int) (255 * ratio);
    }

    public int getDrawPointer() {
        return mDrawPointer;
    }

    public int getRemovePointer() {
        return mRemovePointer;
    }
}
