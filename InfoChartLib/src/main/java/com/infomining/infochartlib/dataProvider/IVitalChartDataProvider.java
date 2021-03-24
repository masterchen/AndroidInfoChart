package com.infomining.infochartlib.dataProvider;


import com.infomining.infochartlib.chart.RealTimeVitalChart;
import com.infomining.infochartlib.util.Transformer;

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

}
