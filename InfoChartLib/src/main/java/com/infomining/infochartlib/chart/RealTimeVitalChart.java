package com.infomining.infochartlib.chart;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.infomining.infochartlib.data.Spec;
import com.infomining.infochartlib.dataProvider.IVitalChartDataProvider;
import com.infomining.infochartlib.dataProvider.SurfaceViewHolder;
import com.infomining.infochartlib.handler.RealTimeDataHandler;
import com.infomining.infochartlib.renderer.RealTimeVitalRenderer;
import com.infomining.infochartlib.thread.DrawingThread;
import com.infomining.infochartlib.util.Transformer;
import com.infomining.infochartlib.util.ViewPortHandler;

public class RealTimeVitalChart extends SurfaceView implements IVitalChartDataProvider, SurfaceHolder.Callback {

    public static float EMPTY_DATA = -9999;

    /**
     * 실시간 차트 스펙
     */
    protected Spec mSpec = new Spec();

    /**
     * 실시간 데이터
     */
    protected float[] mRealTimeData;

    /**
     * 렌더링 스레드
     */
    protected DrawingThread mDrawingThread;

    /**
     * 실시간 데이터 렌더링 객체
     */
    protected RealTimeVitalRenderer mRealTimeVitalRenderer;

    /**
     * View Port Handler
     */
    protected ViewPortHandler mViewPortHandler = new ViewPortHandler();

    /**
     * 서로 다른 좌표계에서 x, y value 를 변한
     */
    protected Transformer mTransformer;

    /**
     * 차트 선 색상
     */
    protected int mLineColor;

    /**
     * 차트 선 두께
     */
    protected float mLineWidth;

    /**
     * 차트 선 모양
     */
    protected LineMode mLineMode;

    /**
     * 현재 값 인디케이터 활성 여부
     */
    protected boolean isEnabledValueCircleIndicator;

    /**
     * 현재 값 인디케이터 크기
     */
    protected float mValueCircleIndicatorRadius;

    /**
     * 현재 값 인디케이터 색상
     */
    protected int mValueCircleIndicatorColor;

    /**
     * 실시간 데이터 핸들러
     */
    protected RealTimeDataHandler mDataHandler;

    public RealTimeVitalChart(Context context) {
        super(context);
        init();
    }

    public RealTimeVitalChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RealTimeVitalChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        mDrawingThread = new DrawingThread(new SurfaceViewHolder(surfaceHolder), mRealTimeVitalRenderer);
        mDrawingThread.setRunning(true);
        mDrawingThread.start();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        boolean retry = true;

        mDrawingThread.setRunning(false);
        reset();

        while (retry) {
            try {
                mDrawingThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void init() {
        getHolder().addCallback(this);

        mLineColor = Color.RED;
        mLineWidth = 5f;
        mLineMode = LineMode.LINEAR;

        isEnabledValueCircleIndicator = true;
        mValueCircleIndicatorColor = Color.RED;
        mValueCircleIndicatorRadius = 10f;

        setWillNotDraw(false);
        mTransformer = new Transformer(mViewPortHandler);
        mRealTimeVitalRenderer = new RealTimeVitalRenderer(this);
        mDataHandler = new RealTimeDataHandler(this);

        setRealTimeSpec(mSpec);
    }

    public void setRealTimeSpec(Spec spec) {
        this.mSpec = spec;
        mRealTimeVitalRenderer.updateSettings();
        mDataHandler.updateSettings();
        mRealTimeData = new float[mSpec.getOneSecondDataCount() * mSpec.getVisibleSecondRange()];
        resetRealTimeData();
    }

    private void addRealTimeData(float value) {
        mRealTimeVitalRenderer.readyForUpdateData();

        mRealTimeData[mRealTimeVitalRenderer.getDrawPointer()] = value;
        mRealTimeData[mRealTimeVitalRenderer.getRemovePointer()] = EMPTY_DATA;

        //invalidate();
    }

    private void settingTransformer() {
        mTransformer.initOffsetMatrix();
        mTransformer.initValueMatrix(0, mSpec.getVitalMinValue(),
                mSpec.getOneSecondDataCount() * mSpec.getVisibleSecondRange(),
                mSpec.getVitalMaxValue() - mSpec.getVitalMinValue());
    }

    private void resetRealTimeData() {
        for (int i = 0; i < mRealTimeData.length ; i++) {
            mRealTimeData[i] = EMPTY_DATA;
        }
    }

    public void reset() {
        resetRealTimeData();
        mDataHandler.stop();
        mDataHandler.reset();
        mDataHandler.updateSettings();
        mRealTimeVitalRenderer.updateSettings();
        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if(w > 0 && h > 0 && w < 10000 && h < 10000) {
            mViewPortHandler.setChartDimens(h, w);
        }

        settingTransformer();

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void dequeueRealTimeData(float value) {
        addRealTimeData(value);
    }

    @Override
    public int getOneSecondDataCount() {
        return mSpec.getOneSecondDataCount();
    }

    @Override
    public int getVisibleSecondRange() {
        return mSpec.getVisibleSecondRange();
    }

    @Override
    public int getTotalRangeCount() {
        return mSpec.getOneSecondDataCount() * mSpec.getVisibleSecondRange();
    }

    @Override
    public float getRefreshGraphInterval() {
        return mSpec.getRefreshGraphInterval();
    }

    @Override
    public float[] getRealTimeDataList() {
        return mRealTimeData;
    }

    @Override
    public float getLineWidth() {
        return mLineWidth;
    }

    @Override
    public int getLineColor() {
        return mLineColor;
    }

    @Override
    public LineMode getLineMode() {
        return mLineMode;
    }

    @Override
    public boolean getEnabledValueCircleIndicator() {
        return isEnabledValueCircleIndicator;
    }

    @Override
    public float getValueCircleIndicatorRadius() {
        return mValueCircleIndicatorRadius;
    }

    @Override
    public int getValueCircleIndicatorColor() {
        return mValueCircleIndicatorColor;
    }

    @Override
    public float getVitalMaxValue() {
        return mSpec.getVitalMaxValue();
    }

    @Override
    public float getVitalMinValue() {
        return mSpec.getVitalMinValue();
    }

    @Override
    public Transformer getTransformer() {
        return mTransformer;
    }

    public RealTimeDataHandler getDataHandler() {
        return mDataHandler;
    }

    public void setLineColor(int mLineColor) {
        this.mLineColor = mLineColor;
    }

    public void setLineWidth(float mLineWidth) {
        this.mLineWidth = mLineWidth;
    }

    public void setEnabledValueCircleIndicator(boolean enabledValueCircleIndicator) {
        isEnabledValueCircleIndicator = enabledValueCircleIndicator;
    }

    public void setValueCircleIndicatorRadius(float mValueCircleIndicatorRadius) {
        this.mValueCircleIndicatorRadius = mValueCircleIndicatorRadius;
    }

    public void setValueCircleIndicatorColor(int mValueCircleIndicatorColor) {
        this.mValueCircleIndicatorColor = mValueCircleIndicatorColor;
    }

    public enum LineMode {
        LINEAR,
        CUBIC
    }
}
