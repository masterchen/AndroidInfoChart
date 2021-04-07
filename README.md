![symbol](https://user-images.githubusercontent.com/57319751/113797582-82c38780-978c-11eb-8c1a-443597935f4a.png)

![Generic badge](https://img.shields.io/badge/version-v1.0.0-blue.svg)
![Generic badge](https://img.shields.io/badge/API-+19-orange.svg)

# InfoCharts-Android

**InfoCharts**는 우리 회사 앱에 사용될 `자체 제작 차트 라이브러리` 입니다.

iOS 버전의 라이브러리를 사용하고자 한다면, [여기](https://github.com/infodevelop/iOSInfoChart)를 눌러주십시오.

# ⚙️ Setting

### AAR

1. SDK 파일을 [다운로드](https://www.dropbox.com/s/ms924qb14dsf5kc/InfoChartSDK_v1.0.0.aar?dl=0)합니다.

2. **'InfoChartSDK.aar'** 파일을 아래 경로로 이동시킵니다.

    ```kotlin
    app/libs
    ```

3. app 모듈 단위의 Gradle 파일을 아래와 같이 수정합니다.

    ```kotlin
    dependencies {
    	...
    		
    	implementation name: 'infoChartSDK_vx.y.z', ext: 'aar'
    }
    ```

# 📈 Charts

## 1. RealTimeVitalChart


![RealTimeVitalChart Video](https://user-images.githubusercontent.com/57319751/113797689-b6061680-978c-11eb-98a9-de5ced40b574.gif)

ECG 등의 `실시간 생체 신호`를 표시해주는 차트입니다. 실제 의료기기에서 표시되는 동일한 애니메이션이 지원됩니다.

## How To Use?

1. XML에 차트를 그리세요.

    ```xml
    <com.infomining.infochartlib.chart.RealTimeVitalChart
            android:id="@+id/chart_vital"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>
    ```

2. 코드에서 차트 View를 바인딩한 후, **실시간 출력에 대한 스펙**을 설정합니다.

    **Java**

    ```java

    RealTimeVitalChart chart;

    chart = findViewById(R.id.chart_vital);
    chart.setRealTimeSpec(new Spec(500, 5, 0.2f, 1.0f, -0.2f));
    ```

    **Kotlin**

    ```kotlin
    lateinit var chart: RealTimeVitalChart

    chart = findViewById(R.id.chart_vital)
    chart.setRealTimeSpec(Spec(500, 5, 0.2f, 1.0f, -0.2f))
    ```

    **※ Spec Class**

    ```kotlin
    Spec(int mOneSecondDataCount, int mVisibleSecondRange, float mRefreshGraphInterval, 
    			float mVitalMaxValue, float mVitalMinValue)

    /*
    	1. mOneSecondDataCount   : 1초 동안 들어오는 데이터 갯수
    	2. mVisibleSecondRage    : 보여질 시간 (초 단위)
    	3. mRefreshGraphInterval : 새로고침 되는 그래프와 이전 그래프와의 간격
    	4. mVitalMaxValue        : 바이탈 최대 값
    	5. mVitalMinValue        : 바이탈 최소 값
    */
    ```

3. 차트에 표시하고자 하는 **실시간 데이터**를 `DataHandler`에 넣어줍니다. 데이터를 넣으면, 차트 애니메이션이 시작되면서 **실시간 차트가 표시**됩니다.

    **Java**

    ```java
    chart.getDataHandler().enqueue(2f);
    ```

    **Kotlin**

    ```kotlin
    chart.dataHandler.enqueue(2f)
    ```

4. 차트가 그려진 `Activity` **혹은** `Fragment`**의 생명주기가 끝나거나**, 더 이상 **차트를 표시할 일이 없다**면 **차트의 자원을 해제**해줍니다.

    **Java**

    ```java
    @Override
    public void onDestroy() {
    	super.onDestory();
    	chart.getDataHandler().destroy();
    }
    ```

    **Kotlin**

    ```kotlin
    override fun onDestory() {
    	super.onDestory()
    	chart.dataHandler.destroy()
    }
    ```

## Details

### 1) 차트 설정 관련

- `setRealTimeSpec(Spec)` : 실시간 차트의 스펙을 설정합니다. 1초 당 출력 데이터 갯수, 보여질 시간, 최대 최소 값 등의 정보를 담고 있습니다.
- `reset()` : 차트를 리셋합니다. 그려진 데이터와 아직 출력되지 않은 데이터를 초기화 하고 애니메이션을 중단합니다.
- `setLineColor(int)` : 그래프의 색을 변경합니다.
- `setLineWidth(float)` : 그래프의 두께를 변경합니다.
- `setEnabledValueCircleIndicator(boolean)` : 가장 마지막으로 그려진 값을 표시하는 인디케이터 활성 여부를 설정합니다.
- `setValueCircleIndicatorRadius(float)` : 가장 마지막으로 그려진 값을 표시하는 인디케이터의 크기를 설정합니다.
- `setValueCircleIndicatorColor(int)` : 가장 마지막으로 그려진 값을 표시하는 인디케이터의 색상을 설정합니다.

### 2) 데이터 관련

- `getDataHandler()` : 실시간 데이터를 관리하는 핸들러 객체를 가져옵니다. `DataHandler`는 균일하지 못하거나 무작위로 들어오는 데이터로 인한 위상 지연 현상 등을 해결하기 위해 데이터를 관리하는 객체입니다.
- `getDataHandler().enqueue(float)` : 핸들러에 출력할 데이터를 추가합니다. 차트가 정지되어 있다면 데이터를 출력하는 동작도 함께 실행합니다.
- `getDataHandler().run()` : 실시간 데이터를 차트에 출력합니다. 핸들러에 출력할 데이터가 남아있다면 해당 데이터들을 순차적으로 차트에 출력하고, 없다면 기본값이 출력됩니다.
- `getDataHandler().stop()` : 차트에 실시간 데이터 출력을 정지합니다.
- `getDataHandler().reset()` : 핸들러에 있는 데이터를 초기화 합니다. 실시간 데이터 출력에 영향을 미치지 않습니다.
- `getDataHandler().destory()` : 핸들러에 할당된 자원을 해제합니다. __(* 이를 하지 않을 경우 스케쥴러 및 렌더링 스레드로 인하여 앱 성능 저하의 원인이 될 수 있습니다.)__

# 📄 Documentaion
- SDK에 대한 상세한 정보는 [JavaDoc](https://infodevelop.github.io/AndroidInfoChart/)을 통해 확인하실 수 있습니다.
