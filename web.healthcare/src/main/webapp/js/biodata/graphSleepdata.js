function graphSleepdata() {
    searchWrd = 'sleep';
    //searchType = "S"; //sleep
        $('.user_state__item').removeClass('_active');
        $('.sleep').addClass('_active');
    //통신시작
    //data는 언제나 (lv[x축의 시간] , data [표시할 데이터])로 구성됨
    $.ajax({
        type:"POST",
        url:"/health/healthinfoDailySleep",
        // 옵션이므로 JSON으로 받을게 아니면 안써도 됨
        dataType:"JSON",
        data:{
                "query": searchType, // 일,주,월,년
                //,"type": searchType	//	검색 데이터 (minmaxavg 종류 선택)
                "userId": searchUserId
                ,"date": curTime
             },
        success : function(data) {
              // 통신이 성공적으로 이루어졌을 때 이 함수를 타게 된다.
              if(data.resultCode == '0000'){
                if(searchType=='D') {

                   drawsleepgraph(searchWrd, searchType, data.resultData);
                }
                else {
                   drawhealthgraph(searchWrd, searchType, data.resultData) ;
                }
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


function drawsleepgraph(searchWrd, searchType, result) {
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
    gradientStroke.addColorStop(0, '#DFD3FF');//DFD3FF 연보라
    gradientStroke.addColorStop(1, '#80b6f4');

    var gradientFill = context.createLinearGradient(500, 0, 100, 0);
    gradientFill.addColorStop(0, "rgba(223, 211, 255,0.6)");//223, 211, 255, 0.6 연보라
    gradientFill.addColorStop(1, "rgba(128, 182, 244, 0.6)");

    //{"data":"[[1,51,61,10],[0,28,89,61],[2,1,90,89],[1,77,167,90],[2,1,168,167],[1,123,291,168],[0,25,316,291],[1,103,419,316]]"}
    if(result != null) {
        const rawdata = result.data;

        const dataArray = JSON.parse(rawdata);

        // lv 배열 생성
        var x = [];
        var sleepdata = dataArray.map(function(element,index, array) { //위의 배열을 재배치할건데
            x.push(`${Math.floor(element[3] / 60)}시${element[3] % 60}분`); //시작
            if(array.length-1 === index)
            x.push(`${Math.floor(element[2] / 60)}시${element[2] % 60}분`); //종료
            //return [element[0], element[0]]; //그래프 표현을 위해 동일한 값을 두번 넣어줌
            return element[0];
        });

         sleepdata.push(sleepdata[dataArray.length-1]);

    }
    else{
         //수면 데이터 없는경우 강제설정
         var x = ['00시','01시','02시','03시','04시','05시','06시','07시','08시','09시','10시','11시','12시'];
         var sleepdata  = "";
    }

    var xLabel =   x ;
    var filteredData = sleepdata;

    var data = {
               labels:xLabel ,
            datasets: [{
                label: title,
                data:  filteredData,
                fill: fill,
                steppedLine:true,
                //borderColor: 'pink',//'rgb(255, 159, 64)',
                tension: 0.1,
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
             type: 'line',
            data:data,
            options: {
                elements: {
                    line: {
                        tension: 0// 곡선의 완만함을 조절 (0에서 1 사이의 값)
                    }
                },
                scales: {
                    x: {
                          type: 'linear',
                          position: 'bottom'
                    },
                    yAxes: [
                        {
                          ticks: {
                              //min: -0.99,
                              callback: function(value, index, values) {
                                  // 숫자에 따라 라벨을 매핑
                                  switch (value) {
                                      case 0:
                                          return '깊은잠';
                                      case 1:
                                          return '얕은잠';
                                      case 2:
                                          return '뒤척임';
                                      case 3:
                                          return '램수면';
                                      default:
                                          return '';
                                  }
                              }
                          }
                      }
                    ]
                }
            }
    });

    // 동적으로 차트 타입 변경
    //myChart.config.type = 'step';
    //myChart.update(); // 변경사항 적용
}