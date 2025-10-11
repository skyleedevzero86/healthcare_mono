/* !! 개별 chart draw start !!  */
/** 
 * barChart draw 함수
 * @param json {data[],lv[]}
 * @param string (step)
*/
function drawBarChart(data,chartId){
    var ctx = document.getElementById('myChart');
    var config = setBarConfig(data,chartId);
    
    if(myChart == null){
        myChart = new Chart(ctx,config);
        setYTicksLabelById(chartId);
        myChart.update();
    }else{
        myChart.destroy();
        myChart = new Chart(ctx,config);
        setYTicksLabelById(chartId);
        myChart.update();
    }
}

/** 
 * Floating Bar Chart draw 함수
 * @param json {dataMin[], dataMax[], lv[]} 
 * @param string chartId (temperature/spo2/heartrate/stress/bloodpress/repiratory)
*/
function drawFloatingChart(data,chartId){
    var ctx = document.getElementById('myChart');

    if(myChart == null){
        myChart = new Chart(ctx,setFloatingConfig(data,chartId));
    }else{
        myChart.destroy();
        myChart = new Chart(ctx,setFloatingConfig(data,chartId));
    }
}
//샘플 차트 draw
function drawStackedChart(){
    var ctx = document.getElementById('myChart');
    myChart = new Chart(ctx,sleepConfig);
}
/* !! 개별 chart draw end !!  */

/* !! configuration !!  */
/** 
 * bar chart configuration 
 * @param json {data[],lv[]}
*/
function setBarConfig(data,chartId){
    var chartData = {
        labels: data.lv,
        datasets: [{
          data: data.data,
          borderWidth: 1
        }]
      };

    var barConfig = {
        type: 'bar',
        data: chartData,
        options: {
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                x: {
                    ticks: {
                        autoSkip: false,
                        maxRotation: 0,
                        minRotation: 0,
                        callback: function (value, index, ticks) {
                            var xLabel = this.getLabelForValue(value);
                            return setLabelConfig(xLabel.slice(), chartData.labels.length);
                        }
                    },
                    grid: {
                        display: false,
                        drawTicks: false
                    }
                },
                y: {
                    beginAtZero: true,
                    afterDataLimits: (scales) => {
                        scales.max = getMaxScales(scales,chartId,data.data,data.lv);
                    },
                    ticks: {
                        callback: function (value, index, ticks) {
                            var yLabel = this.getLabelForValue(value);
                            if (ticks[1].value >= 1000) {
                                yLabel = value / 1000;
                            }
                            return yLabel;
                        }
                    },
                    grid: {
                        display: true,
                        drawTicks: false
                    }
                }
            }
        }
    };
    
    return barConfig;
}

function setFloatingConfig(data,chartId){
    // var minmax = data.lv != null ? setMinMaxData(data): null;
    var minmax = setMinMaxData(data);

    var chartData = {
        labels: data.lv,
        datasets: [{data: minmax.chartData}]
    }
    // if(data.dataMin != null && data.dataMax != null){
    //     var min = data.dataMin.filter(v=>v!=null).length > 0 ? Math.min(...data.dataMin.filter(v=>v!=null)) : null;
    //     var max = data.dataMax.filter(v=>v!=null).length > 0 ? Math.max(...data.dataMax.filter(v=>v!=null)) : null;
    // }
    var stepSize = setStepSize(chartId);
    var floatingConfig = {
        type: 'bar',
        data: chartData,
        options: {
            maintainAspectRatio :false,
            layout: {
                padding: {
                    top: 20
                }
            },
            plugins:{
                legend: {
                    display: false
                },
                tooltip : {
                    callbacks : {
                        label : function(context){
                            var label =  "max : " + minmax.orgData[context.dataIndex][1] + " min : " + minmax.orgData[context.dataIndex][0];
                            return label;
                        },
                        title : function(context){
                            var index = context[0].dataIndex;
                            return data.lv[index];
                        }
                    }
                }
            },
            scales: {
                x : {
                    ticks : {        //x축 라벨 정각만 'nn시' 형태로 표시. 기울기 수평으로.
                        autoSkip : false,
                        maxRotation : 0,    //최대 기울기
                        minRotation : 0,    //최소 기울기
                        callback : function(value,index,ticks){ //x축 라벨 정각만 'nn시' 형태로 표시.
                            var label  = this.getLabelForValue(value);
                            return setLabelConfig(label.slice(),chartData.labels.length);
                        }
                    },
                    grid : {
                        drawTicks : false,
                        display : false
                    }
                },
                y: {
                    beginAtZero: false,
                    afterDataLimits: (scales) => {
                        if(data.dataMin != null && data.dataMax != null){
                            scales.max = getMaxScales(scales,chartId,data.dataMax,data.lv);
                            scales.min = getMinScales(scales,chartId,data.dataMin);
                        }
                    },
                    grid : {
                        display : true,
                        drawTicks : false
                    },
                    ticks : {
                        suggestedMin: 0,
                        stepSize : stepSize
                    }
                }
            }
        }
    }
    return floatingConfig;
}

/** 
 * Floating Chart (최대/최소값 그래프) 데이터 셋팅 
 * 1. min array와 max array를 index 기준으로 [min,max] 형태의 array로 변환한다. 
 * 2. min array의 value 중 최소값, max Array 의 value 중 최대값을 구한다.
 * @param 
 *  type : json 
 *  구조 : {dataMin[], dataMax[], lv[]} 
 *  설명 : 차트로 뿌려줄 데이터
 * @returns 
 *  타입 : json
 *  구조 : result {arr : [[min,max],[min,max]...], min : min, max : max}
*/
function setMinMaxData(data){
    var chartData = [];
    var orgData = [];

    if(data.lv != null && data.dataMin != null){
        data.lv.map((t, index) => {
            nowMin = data.dataMin[index];
            nowMax = data.dataMax[index];

            orgData.push([nowMin, nowMax]);
    
            if(nowMin != null && nowMax != null && (nowMin == nowMax)){     //  min == max일경우 차트 보여지지 않기에 min-1하여 보여지도록함 
                nowMin--;
            }

            chartData.push([nowMin, nowMax]);
        });
    }
    return {chartData,orgData};
}
/** 
 * getMaxScales :  y축 범위 max 지정 (y축 제일 위 눈금값)
 * [max scales 처리유형]
 * ---- 모든 데이터가 null 일때만 지정 ----
 * 1. 심박수 heartrate : 100
 * 2. 걸음수 step
 *      - 분 : 3000(일일차트) / 일 : 30,000(일주일,1개월 차트) / 월 300,000 (1년 차트)
 * 3. 스트레스 stress : 100
 * 4. 혈압 bloodpress : 120
 * 5. 호흡 repiratory : 20
 * ----          항상 고정             ----
 * 6. 산소포화도 spo2 : 100
 * 7. 체온 temperature : 45
*/
function getMaxScales(scales,chartId,data,lv){

    var maxMap = {"heartrate": 100, "step": [3000,30000,300000], "stress": 100
                    , "bloodpress": 120, "repiratory": 20, "spo2":100, "temperature":45 };

    var nNullData = data.filter(a => a != null);
    
    if(nNullData.length > 0 && chartId != "spo2" && chartId != "temperature"){ // null이 아니면 최대값 유동적. (체온,산소포화도 제외)
        return scales.max * 1.05;
    }

    if(chartId == "step"){
        if(lv.length == 7 || lv.length == 30){
            return maxMap[chartId][1];
        }else if(lv.length == 12){
            return maxMap[chartId][2];
        }else{
            return maxMap[chartId][0];
        }
    }
    return maxMap[chartId];
}
/** 
 * getMinScales : 모든 데이터가 null 일때 y축 범위 min 지정 (y축 눈금 시작값)
 * [min scales 처리유형]
 * ---- 모든 데이터가 null 일때만 지정 ----
 * 1. 심박수 : 60
 * 2. 혈압 : 80
 * 3. 호흡 : 10
 * ----          항상 고정             ----
 * 4. 산소포화도 : 80
 * 3. 체온 : 30
*/
function getMinScales(scales,chartId,data){
    var minMap = {"heartrate": 60, "bloodpress": 80, "repiratory": 10, "spo2":80, "temperature":30 };
    var nNullData = data.filter(a => a != null);

    if(nNullData.length > 0 && chartId != "spo2" && chartId != "temperature"){ // null이 아니면 최소값 유동적. (체온,산소포화도 제외)
        return scales.min * 0.9;
    }
    return minMap[chartId];
}

/** setLabelConfig : x축 라벨 설정. 유형별 특정 기준에 해당하는 라벨만 남기고 공백처리.
 * 
 * [라벨 처리 유형 - 모바일] 
 * 1. 1일 데이터 (string array) 
 *  : ['00시00분','00시15분','00시30분','01시00분','01시15분','01시30분',,,,]
 * 2. 1주 데이터 (object array)
 *  : [['12/30',''],['12/31',''],['01/01','2023'],['01/02',''],['01/03',''],['01/04',''],['01/05','']]
 * 3. 1개월 데이터 (object array)
 *  : [['12/30',''],['12/31',''],['01/01','2023'],['01/02',''],['01/03',''],['01/04',''],['01/05',''],,,,,]
 * 4. 1년 데이터 (object array)
 *  : [['4',''],['5',''],['6',''],['7',''],['8',''],['9',''],['10',''],['11',''],['12',''],['1','2023'],['2',''],['3','']]
 */
function setLabelConfig(label,length){
    if( typeof(label) == 'string' ){    //1일 데이터
        label = label.replace(/(^0)/, "");
        if(label.includes("00분")){
            label = label.substring(0,label.indexOf("시",0));
            label = (label%3 == 0) ? label : '';
        } else {
            label = '';
        }
        return label;
    }else if( typeof(label) == 'object' ){  //1주, 1개월, 1년 데이터
        var month = '', date = '';
        if(label[0].split('/').length >= 2){
            month = label[0].split('/')[0].replace(/(^0)/, '')+'/';
            date = label[0].split('/')[1].replace(/(^0)/, '');
        }else{
            date = label[0].replace(/(^0)/, '');
        }

        // 1개월 데이터일경우 매월 1일 또는 5의 배수일만 보여줌
        if(length > 7 && length != 12){
            label[0] = (date == 1 || date%5 == 0) ? month+date : '';
        }   
        return [label[0],label[1]];
    }
}
/* y축 단위지정(텍스트 표시) */
function setYTicksLabelById(chartId){
    switch(chartId){
        case "step" : {
            if(myChart.scales.y.ticks[1].value >= 1000){
                myChart.options.plugins["title"] = {};
                myChart.options.plugins.title["display"] = true;
                myChart.options.plugins.title["text"] = "걸음수(1000단위)";
                myChart.options.plugins.title["align"] = "start";
            }
            break;
        }
    }
}

function setStepSize(chartId){
    switch(chartId){
        case "spo2" : {
            return 1;
        }
        case "temperature" : {
            return 0.5;
        }
        case "repiratory" : {
            return 0.5;
        }
        default : 
            return 15;
    }
}

/** 
 * Stacked Bar Chart draw 함수 Samle
*/
//색상 리스트
const colorMap = {blue1 : "#3296D79F" , blue2 : "#A5D8FA9F", blue3 : "#CDECFA9F"}; 
//데이터 샘플
const chartData = [{data: [[7,10]],backgroundColor: colorMap.blue1},
                    {data: [[1]],backgroundColor: colorMap.blue2},
                    {data: [[2.5]],backgroundColor: colorMap.blue1},
                    {data: [[2.5]],backgroundColor: colorMap.blue3},
                    {data: [[1]],backgroundColor: colorMap.blue2}] ;    
// 라벨(x축) 샘플
// const labels = ["20시","21시","22시","23시","0시","1시","2시","3시","4시","5시","6시","7시","8시","9시","10시","11시","12시","13시","14시","15시","16시","17시","18시","19시"];
const sleepConfig = {type: 'bar',
                data: {
                    labels : [1],
                    datasets: chartData
                },
                options: {
                    maintainAspectRatio :false,
                    barPercentage: 1.0,
                    categoryPercentage:1.0,
                    events: [],
                    plugins:{
                        legend: {
                            display: false
                        }
                    },
                    indexAxis: 'y',
                    scales: {
                        x: {
                            // display: false,
                            stacked: true,
                            grid:{
                                display: false,
                                drawTicks : false,
                            },
                            ticks : {
                                stepSize : 1,
                                callback : function(value,index,ticks){ //x축 라벨 정각만 'nn시' 형태로 표시.
                                    var label = this.getLabelForValue(value);
                                    label = label % 3 == 0 ? label : "";
                                    return label;
                                }
                            },
                            afterDataLimits: (scales) => {
                                scales.max = 24;
                            }
                        },
                        y: {
                            display: false,
                            stacked: true,
                            beginAtZero: false
                        }
                    }
                }
            };