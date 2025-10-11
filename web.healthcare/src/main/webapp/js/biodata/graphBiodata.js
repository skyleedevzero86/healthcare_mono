function graphBiodata(category){
    searchWrd = category ; //받아온 category 전역변수에 할당: ex. heartrate
    typeCheck(searchWrd);  //해당 category의 그래프 정보 및 타입 변경
    keyword = changeSearchWrd(searchType);//일과 다르게 주, 년도는 max값으로 표시(health_data Table참조) -> 키워드를 키값(ex. heartrate) 으로 주,달(ex. heartrate_max) 키워드를 변경함
    $('.user_state__item').removeClass('_active');
    $('.'+category).addClass('_active');

    //통신시작
    //data는 언제나 (lv[x축의 시간] , data [표시할 데이터])로 구성됨
    $.ajax({
        type:"POST",
        url:"/health/healthgraphBiodata",
        // 옵션이므로 JSON으로 받을게 아니면 안써도 됨
        dataType:"JSON",
        data:{
                "query": searchType, // 일,주,월,년
                //,"type": searchType	//	검색 데이터 (minmaxavg 종류 선택)
                "userId": searchUserId
                ,"date": curTime
                ,"searchWrd": keyword
                ,"startTime" : startTime
                ,"condition": condition
             },
        success : function(data) {
              // 통신이 성공적으로 이루어졌을 때 이 함수를 타게 된다.
              if(data.resultCode == '0000'){
                drawhealthgraph(searchWrd, searchType, data.resultData);
                searchType = 'D'; //모든게 끝나고 다시 초기화해줌
              }
        },
        complete : function(data) {
              // 통신이 실패했어도 완료가 되었을 때 이 함수를 타게 된다.
              // TODO
        },
        error : function(xhr, status, error) {
              console.log(error);
        }
    });
}

/* !! 개별 chart draw start !!  */
/**
 * main graph draw 함수
 * @param json {data[분단위데이],lv[00~23]}
*/
var myChart = null ;

function drawhealthgraph(searchWrd, searchType, result) {
    typeCheck(searchWrd); //해당하는 요소마다 다른 주기,title값을 가지고옴
    var canvas = document.getElementById('myChart');
    // canvas 크기 설정
    var viewportWidth = window.innerWidth;
    var viewportHeight = window.innerHeight;
    var pixelValue = 30;
    var vwValue = (pixelValue / viewportWidth) * 100;

    pixelValue = 5;
    var vhValue = (pixelValue / viewportHeight) * 98;


    canvas.width = vwValue; // 적절한 폭으로 설정
    canvas.height = vhValue; // 적절한 높이로 설정

    // Chart 그리기
    var context = canvas.getContext('2d');

     //선 색상 꾸미기
    var gradientStroke = context.createLinearGradient(500, 0, 100, 0);
    var gradientFill = context.createLinearGradient(500, 0, 100, 0);

    gradientStroke.addColorStop(0,filledcolor);
    gradientStroke.addColorStop(1,filledcolor);
    gradientFill.addColorStop(0,filledcolor);
    gradientFill.addColorStop(1,filledcolor);

    //if(filledcolor == 'blue') {

        //gradientStroke.addColorStop(0,'#80b6f4');
        //gradientFill.addColorStop(1, "rgba(128, 182, 244, 0.6)");
        //gradientStroke.addColorStop(0,'#7FC9DA');
        //gradientStroke.addColorStop(1,'#7FC9DA');
        //gradientFill.addColorStop(0,"rgba(183, 223, 239, 0.6)");
        //gradientFill.addColorStop(1,"rgba(183, 223, 239, 0.6)");
    //}
    //else {
        //gradientStroke.addColorStop(0,'#DFD3FF');//DFD3FF 연보라
        //gradientStroke.addColorStop(1, '#DFD3FF');//DFD3FF 연보라
        //gradientFill.addColorStop(0,"rgba(223, 211, 255,0.6)");//223, 211, 255, 0.6 연보라
        //gradientFill.addColorStop(1,"rgba(223, 211, 255,0.6)");//223, 211, 255, 0.6 연보라

    //}


    if(result != "") {

        if(searchType == "D") {
            var x = result.lv.map(time => time.substr(0,2)); //[00,00,00,...23,23,23]
        }
        else { //'w','m'은 무조건 정수로 전달됨
            var x = result.lv;
        }
        var xLabel =   x.map(function(element,index) { //위의 배열을 재배치할건데
          // 중복 체크
            if(index == x.indexOf(element)) {
                //현재요소가 중복된 숫자들 중 첫번째 숫자라면
                if(searchType == 'D')  return element +'시';
                else if(searchType=='W') return element+ '일' ;
                else if(searchType=='M') return element+ '월' ;
            }
            else {
                return '';
            }
        });

    }

    if(searchWrd == "sleep") {
         var filteredData =  result.data.map(element => element === 0 ? null : Math.floor(element / 60));

    }
    else {
       var filteredData =  result.data.map(element => element === 0 ? null : element);

    }


    var data = {
               labels:xLabel ,
            datasets: [{
                label: title,
                data:  filteredData,
                fill: fill,
                //borderColor: 'pink',//'rgb(255, 159, 64)',
                tension: 0.5,
                borderWidth: 2,
                borderColor:               gradientStroke,
                pointBorderColor:          gradientStroke,
                pointBackgroundColor:      gradientStroke,
                pointHoverBackgroundColor: gradientStroke,
                pointHoverBorderColor:     gradientStroke,
                backgroundColor: gradientFill
            }]
        };
  // Chart 객체 업데이트 또는 생성
    if (myChart !== null) {
        myChart.destroy();
    }

    myChart = new Chart(context, {
            type: charttype, // 차트의 형태
            data:data,
            options: {
                scales: {
                    x: {
                          type: 'linear',
                          position: 'bottom'
                    },
                     yAxes: [{
                        display: true,
                        ticks: {
                            suggestedMin: ymin,    // minimum will be 0, unless there is a lower value.
                             suggestedMax: ymax,
                            // OR //
                            beginAtZero: true   // minimum value will be 0.
                        }
                    }]
                }
            }
    });
}
