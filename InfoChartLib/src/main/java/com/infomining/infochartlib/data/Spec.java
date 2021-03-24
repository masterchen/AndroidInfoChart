package com.infomining.infochartlib.data;


import static com.infomining.infochartlib.chart.RealTimeVitalChart.EMPTY_DATA;

public class Spec {
    /**
     * 1초 동안 들어오는 데이터 갯수
     */
    protected int mOneSecondDataCount;

    /**
     * 보여질 X축 범위 (초 단위)
     */
    protected int mVisibleSecondRange;

    /**
     * 새로고침 되는 그래프와 이전 그래프와의 간격
     */
    protected float mRefreshGraphInterval;

    /**
     * 바이탈 최대 값
     */
    protected float mVitalMaxValue;

    /**
     * 바이탈 최소 값
     */
    protected float mVitalMinValue;

    public Spec() {
        this.mOneSecondDataCount = 500;
        this.mVisibleSecondRange = 5;
        this.mRefreshGraphInterval = 0.2f;
        this.mVitalMaxValue = 1.0f;
        this.mVitalMinValue = -0.2f;
    }

    public Spec(int mOneSecondDataCount, int mVisibleSecondRange, float mRefreshGraphInterval, float mVitalMaxValue, float mVitalMinValue) {
        if(mOneSecondDataCount < 1) {
            throw new IllegalArgumentException("1초 동안 들어올 데이터의 갯수는 최소 1 이상이어야 합니다.");
        }
        else if(mVisibleSecondRange < 2) {
            throw new IllegalArgumentException("보여질 범위는 최소 2초 이상이어야 합니다.");
        }
        else if(mRefreshGraphInterval <= 0.0 || mRefreshGraphInterval >= 1.0) {
            throw new IllegalArgumentException("이전 그래프와의 간격은 퍼센트로 나타내어야 하며, 0.0 초과, 1.0 미만이어야 합니다.");
        }
        else if(mVitalMaxValue <= mVitalMinValue) {
            throw new IllegalArgumentException("최대값은 최소값보다 작거나 같을 수 없습니다.");
        }
        else if (mVitalMinValue <= EMPTY_DATA) {
            throw new IllegalArgumentException("최소값은 " + EMPTY_DATA + " 이상이어야 합니다.");
        }

        this.mOneSecondDataCount = mOneSecondDataCount;
        this.mVisibleSecondRange = mVisibleSecondRange;
        this.mRefreshGraphInterval = mRefreshGraphInterval;
        this.mVitalMaxValue = mVitalMaxValue;
        this.mVitalMinValue = mVitalMinValue;
    }

    public void setOneSecondDataCount(int mOneSecondDataCount) {
        if(mOneSecondDataCount < 1) {
            throw new IllegalArgumentException("1초 동안 들어올 데이터의 갯수는 최소 1개 이상이어야 합니다.");
        }
        this.mOneSecondDataCount = mOneSecondDataCount;
    }

    public void setVisibleSecondRange(int mVisibleSecondRange) {
        if(mVisibleSecondRange < 2) {
            throw new IllegalArgumentException("보여질 범위는 최소 2초 이상이어야 합니다.");
        }
        this.mVisibleSecondRange = mVisibleSecondRange;
    }

    public void setRefreshGraphInterval(float mRefreshGraphInterval) {
        if(mRefreshGraphInterval <= 0.0 || mRefreshGraphInterval >= 1.0) {
            throw new IllegalArgumentException("이전 그래프와의 간격은 퍼센트로 나타내어야 하며, 0.0 초과, 1.0 미만이어야 합니다.");
        }
        this.mRefreshGraphInterval = mRefreshGraphInterval;
    }

    public void setVitalMaxValue(float mVitalMaxValue) {
        if(mVitalMaxValue <= mVitalMinValue) {
            throw new IllegalArgumentException("최대값은 최소값보다 작거나 같을 수 없습니다.");
        }
        this.mVitalMaxValue = mVitalMaxValue;
    }

    public void setVitalMinValue(float mVitalMinValue) {
        if(mVitalMaxValue <= mVitalMinValue) {
            throw new IllegalArgumentException("최대값은 최소값보다 작거나 같을 수 없습니다.");
        }
        else if(mVitalMinValue <= EMPTY_DATA) {
            throw new IllegalArgumentException("최소값은 " + EMPTY_DATA + " 이상이어야 합니다.");
        }
        this.mVitalMinValue = mVitalMinValue;
    }

    public int getOneSecondDataCount() {
        return mOneSecondDataCount;
    }

    public int getVisibleSecondRange() {
        return mVisibleSecondRange;
    }

    public float getRefreshGraphInterval() {
        return mRefreshGraphInterval;
    }

    public float getVitalMaxValue() {
        return mVitalMaxValue;
    }

    public float getVitalMinValue() {
        return mVitalMinValue;
    }
}