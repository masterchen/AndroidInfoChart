package com.infomining.infochartlib.chart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
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

/**
 * ECG 및 생체신호 실시간 차트를 그려주는 클래스.
 *
 *   For Example :
 *  <pre>
 *      chart.setRealTimeSpec(spec);
 *      chart.getDataHandler().run();
 *      chart.getDataHandler().enqueue(2f);
 *      chart.getDataHandler().stop();
 *      chart.getDataHandler().destroy();
 *  </pre>
 *
 * @author Dahun Kim
 */
public class RealTimeVitalChart extends SurfaceView implements IVitalChartDataProvider, SurfaceHolder.Callback {

    /**
     * 빈 데이터 값. Null의 의미를 가짐.
     */
    public static float EMPTY_DATA = -999999;

    /**
     * 실시간 차트 스펙
     *
     * @see #setRealTimeSpec(Spec)
     */
    protected Spec mSpec = new Spec();

    /**
     * 실시간 데이터, 렌더링 시 출력되는 데이터를 담고 있음.
     * ( 배열의 index를 x 값으로 사용, 배열의 값을 y 값으로 사용 )
     *
     * @see #addRealTimeData(float)
     * @see #getRealTimeDataList()
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
     *
     * @see #settingTransformer()
     * @see #getTransformer()
     */
    protected Transformer mTransformer;

    /**
     * 차트 선 색상
     *
     * @see #getLineColor()
     * @see #setLineColor(int)
     */
    protected int mLineColor;

    /**
     * 차트 선 두께
     *
     * @see #getLineWidth()
     * @see #setLineWidth(float)
     */
    protected float mLineWidth;

    /**
     * 차트 선 모양
     *
     * @see #getLineMode()
     */
    protected LineMode mLineMode;

    /**
     * 차트 백그라운드 (drawable)
     *
     * @see #getChartBackgroundDrawable()
     * @see #setChartBackground(Drawable)
     */
    protected Bitmap mChartBackgroundDrawable;

    /**
     * 차트 백그라운드 (color)
     *
     * @see #getChartBackgroundColor()
     * @see #setChartBackground(int)
     */
    protected Integer mChartBackgroundColor;

    /**
     * 현재 값 인디케이터 활성 여부
     * 
     * @see #getEnabledValueCircleIndicator() 
     * @see #setEnabledValueCircleIndicator(boolean) 
     */
    protected boolean isEnabledValueCircleIndicator;

    /**
     * 현재 값 인디케이터 크기
     * 
     * @see #getValueCircleIndicatorRadius() 
     * @see #setValueCircleIndicatorRadius(float) 
     */
    protected float mValueCircleIndicatorRadius;

    /**
     * 현재 값 인디케이터 색상
     * 
     * @see #getValueCircleIndicatorColor() 
     * @see #setValueCircleIndicatorColor(int)
     */
    protected int mValueCircleIndicatorColor;

    /**
     * 실시간 데이터 핸들러
     *
     * @see #getDataHandler()
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

    /**
     * 차트 선의 색상 및 두께 등 렌더링 정보 초기화와 Transformer, 렌더링 객체, 데이터 핸들러를 할당.
     */
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

    /**
     * 실시간 차트 스펙 설정
     * @param spec      설정될 차트 스펙 객체
     */
    public void setRealTimeSpec(Spec spec) {
        this.mSpec = spec;
        mRealTimeVitalRenderer.updateSettings();
        mDataHandler.updateSettings();
        mRealTimeData = new float[mSpec.getOneSecondDataCount() * mSpec.getVisibleSecondRange()];
        resetRealTimeData();
        //settingTransformer();
    }

    /**
     * 실시간 데이터를 렌더링에 사용되는 배열에 추가. Queue에서 dequeue된 데이터를 렌더링하고자 할 때 사용함.
     * @param value     추가될 실시간 데이터
     */
    private void addRealTimeData(float value) {
        mRealTimeVitalRenderer.readyForUpdateData();

        mRealTimeData[mRealTimeVitalRenderer.getDrawPointer()] = value;
        mRealTimeData[mRealTimeVitalRenderer.getRemovePointer()] = EMPTY_DATA;

        //invalidate();
    }

    /**
     * 좌표변환계 설정
     */
    private void settingTransformer() {
        mTransformer.initOffsetMatrix();
        mTransformer.initValueMatrix(0, mSpec.getVitalMinValue(),
                mSpec.getOneSecondDataCount() * mSpec.getVisibleSecondRange(),
                mSpec.getVitalMaxValue() - mSpec.getVitalMinValue());
    }

    /**
     * 그려진 실시간 데이터 삭제
     */
    private void resetRealTimeData() {
        for (int i = 0; i < mRealTimeData.length ; i++) {
            mRealTimeData[i] = EMPTY_DATA;
        }
    }

    /**
     * 차트 리셋. 그려진 데이터와 아직 출력되지 않고 Queue에 남은 데이터도 초기화되며, 데이터 핸들러 스케쥴러가 정지됨.
     */
    public void reset() {
        resetRealTimeData();
        mDataHandler.stop();
        mDataHandler.reset();
        mDataHandler.updateSettings();
        mRealTimeVitalRenderer.updateSettings();
        postInvalidate();
    }

    /**
     * 차트 자원 할당 해제
     */
    public void destory() {
        mDataHandler.destroy();
        mDrawingThread.setRunning(false);
        mDrawingThread.interrupt();
        mRealTimeVitalRenderer = null;
        mDrawingThread = null;
        mViewPortHandler = null;
        mTransformer = null;
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
    public Bitmap getChartBackgroundDrawable() {
        return mChartBackgroundDrawable;
    }

    @Override
    public Integer getChartBackgroundColor() {
        return mChartBackgroundColor;
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

    @Override
    public int getChartTop() {
        return getTop();
    }

    @Override
    public int getChartBottom() {
        return getBottom();
    }

    @Override
    public int getChartLeft() {
        return getLeft();
    }

    @Override
    public int getChartRight() {
        return getRight();
    }

    @Override
    public int getChartWidth() {
        return getWidth();
    }

    @Override
    public int getChartHeight() {
        return getHeight();
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

    public void setChartBackground(Drawable background) {
        Bitmap bitmap = ((BitmapDrawable)background).getBitmap();
        this.mChartBackgroundDrawable = bitmap;
        this.mChartBackgroundColor = null;
    }

    public void setChartBackground(int color) {
        this.mChartBackgroundColor = color;
        this.mChartBackgroundDrawable = null;
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
