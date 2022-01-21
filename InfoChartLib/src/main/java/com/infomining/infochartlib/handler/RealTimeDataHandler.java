package com.infomining.infochartlib.handler;

import android.util.Log;

import com.infomining.infochartlib.dataProvider.IVitalChartDataProvider;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 실시간 데이터를 관리하는 핸들러.
 * 균일하지 못하거나 무작위로 들어오는 데이터를 Queue에 담아 일정 시간 간격으로 출력하여 위상의 지연되거나 겹치는 현상을 없애고 의도된 대로 균일하게 출력할 수 있도록 구체화한 객체
 *
 * @author Dahun Kim
 */
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
     * 기본 값. 보통은 스펙에서 Max와 Min의 중간 값이 되며, Queue에 데이터가 없을 경우 출력되는 값이다.
     */
    Float defaultValue;

    /**
     * 마지막으로 dequeue된 값.
     */
    Float lastValue;

    /**
     * 출력될 값을 담는 임시 변수.
     */
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

    /**
     * 핸들러 설정값 업데이트.
     * 차트의 스펙이 변경될 경우 호출됨
     */
    public void updateSettings() {
        this.defaultValue = ((mChart.getVitalMaxValue() - mChart.getVitalMinValue()) / 2) + mChart.getVitalMinValue();
    }

    /**
     * Queue Enqueue.
     * @param value     실시간 데이터
     */
    public synchronized void enqueue(float value) {
        run();
        mainQueue.add(value);
    }

    /**
     * Queue Dequeue.
     */
    private void dequeue() {
        lastValue = dequeueValue;
        dequeueValue = mainQueue.poll();
        dequeueValue = (dequeueValue != null) ? dequeueValue : defaultValue;
        //dequeueValue = (dequeueValue != null) ? dequeueValue : lastValue;
        mChart.dequeueRealTimeData(dequeueValue);
    }

    Future future;

    /**
     * 스케쥴러 실행.
     * ex) 1초에 500개의 데이터를 그리는 경우, 0.02초 간격으로 데이터를 내보내는 스케쥴러가 생성됨.
     */
    public void run() {
        if(!isRunning && !executorService.isShutdown()) {
            task = new DataDequeueTask(mChart.getOneSecondDataCount());
            future = executorService.scheduleWithFixedDelay(task, 0, task.mOneDataTime, TimeUnit.MICROSECONDS);

            isRunning = true;
        }
    }

    /**
     * 스케쥴러 정지
     */
    public void stop() {
        if(isRunning && !executorService.isShutdown()) {
            if(future != null) {
                future.cancel(true);
            }

            isRunning = false;
        }
    }

    /**
     * Queue Reset
     */
    public void reset() {
        mainQueue.clear();
    }

    /**
     * 핸들러 자원 해제.
     * (* 핸들러가 포함된 차트의 자원이 해제되거나 더 이상 핸들러를 사용하지 않을 경우 필히 이 메소드를 호출하여야 함. 앱 성능 저하의 원인이 될 수 있음.)
     */
    public void destroy() {
        stop();
        if(!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    /**
     * Queue의 데이터를 dequeue() 하는 Task.
     *
     * @author Dahun Kim
     */
    class DataDequeueTask implements Runnable {
        int mOneDataTime;
        long prevTime;
        long nowTime;
        long totalOverDelayTime;

        DataDequeueTask(int oneSecondDataCount) {
            // TODO: 최적화를 위한 계산식 수정
            this.mOneDataTime = (int) Math.max((1000000 / oneSecondDataCount) * 0.8, 1);
            this.prevTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            dequeue();

            nowTime = System.currentTimeMillis();
            totalOverDelayTime += nowTime - prevTime - 2;
            //Log.e("Log", "delay = " + (nowTime - prevTime) + ", totalOverDelayTime = " + totalOverDelayTime);
            prevTime = nowTime;
        }

    }
}
