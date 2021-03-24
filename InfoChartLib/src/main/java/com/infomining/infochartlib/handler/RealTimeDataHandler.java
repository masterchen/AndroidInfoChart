package com.infomining.infochartlib.handler;

import android.util.Log;

import com.infomining.infochartlib.dataProvider.IVitalChartDataProvider;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RealTimeDataHandler {

    /**
     * 메인 큐 Queue
     */
    Queue<Float> mainQueue = new LinkedList<>();

    /**
     * 차트 데이터 프로바이더
     */
    IVitalChartDataProvider mChart;

    /**
     * 변수
     */
    Float defaultValue;
    Float dequeueValue;

    /**
     * 스케쥴러
     */
    final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    /**
     * Task
     */
    DataDequeueTask task;

    /**
     * Task Flag
     */
    boolean isRunning = false;

    public RealTimeDataHandler(IVitalChartDataProvider provider) {
        this.mChart = provider;
        updateSettings();
    }

    public void updateSettings() {
        this.defaultValue = ((mChart.getVitalMaxValue() - mChart.getVitalMinValue()) / 2) + mChart.getVitalMinValue();
    }

    public synchronized void enqueue(float value) {
        run();
        mainQueue.add(value);
    }

    private void dequeue() {
        dequeueValue = mainQueue.poll();
        dequeueValue = (dequeueValue != null) ? dequeueValue : defaultValue;
        mChart.dequeueRealTimeData(dequeueValue);
    }

    Future future;

    public void run() {
        if(!isRunning && !executorService.isShutdown()) {
            task = new DataDequeueTask(mChart.getOneSecondDataCount());
            future = executorService.scheduleWithFixedDelay(task, 0, task.mOneDataTime, TimeUnit.MICROSECONDS);

            isRunning = true;
        }
    }

    public void stop() {
        if(isRunning && !executorService.isShutdown()) {
            if(future != null) {
                future.cancel(true);
            }

            isRunning = false;
        }
    }

    public void reset() {
        mainQueue.clear();
    }

    public void destroy() {
        stop();
        executorService.shutdown();
    }

    class DataDequeueTask implements Runnable {
        int mOneDataTime;
        long prevTime;
        long nowTime;

        DataDequeueTask(int oneSecondDataCount) {
            this.mOneDataTime = Math.max(1000000 / oneSecondDataCount, 1) / 2;
            this.prevTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            dequeue();

            nowTime = System.currentTimeMillis();
            Log.e("Log", "delay = " + (nowTime - prevTime));
            prevTime = nowTime;
        }

    }
}
