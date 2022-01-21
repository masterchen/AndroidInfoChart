package com.infomining.infochartlib.dataProvider;


import android.graphics.drawable.Drawable;

import com.infomining.infochartlib.chart.RealTimeVitalChart;
import com.infomining.infochartlib.util.Transformer;

/**
 * 실시간 차트의 설정값 및 정보 전달자
 * @author Dahun Kim
 */
public interface IVitalChartDataProvider {

    int getOneSecondDataCount();

    int getVisibleSecondRange();

    int getTotalRangeCount();

    float getRefreshGraphInterval();

    float[] getRealTimeDataList();

    int getLineColor();

    float getLineWidth();

    RealTimeVitalChart.LineMode getLineMode();

    boolean getEnabledValueCircleIndicator();

    float getValueCircleIndicatorRadius();

    int getValueCircleIndicatorColor();

    float getVitalMaxValue();

    float getVitalMinValue();

    Transformer getTransformer();

    void dequeueRealTimeData(float value);

    Drawable getChartBackgroundDrawable();

    Integer getChartBackgroundColor();

}
