
function drawdonutgraph(result) {
 var data = {
        datasets: [{
            data: [result.userExerciseScore, result.userSleepScore,result.userStressScore], // 두 가지 데이터 세트
            backgroundColor: ["#BC9FE8", "#D4E9F2", "#91C2EC"], // 각 데이터의 색상
        }],
        labels: ['활동점수', '수면점수','스트레스 점수']
    };

    // 도넛 차트 생성
    var ctx = document.getElementById('myDonutChart').getContext('2d');
    var myDonutChart = new Chart(ctx, {
        type: 'doughnut',
        data: data,
        options: {
            cutoutPercentage: 0, // cutoutPercentage를 0으로 설정
            animation: {
                animateScale: true,
                animateRotate: true,
                onComplete: function () {
                //addTextToChart(result.healthScore + "점")
                }
            },
            legend: {
                display: true,
                position: 'right'
            },
            maintainAspectRatio: false, // false로 설정하여 aspect ratio를 유지하지 않도록 합니다.
            responsive: true, // false로 설정하여 반응형으로 크기를 조정하지 않도록 합니다.
            width: 50, // 너비 설정
            height: 50 ,// 높이 설정
//             onResize : function(event, elements) {
//                    if (elements.length > 0) {
//                        var result = elements[0]._chart.tooltip._data.datasets[0].data[elements[0]._index];
//                        addTextToChart(result.healthScore + "점");
//                    } else {
//                        addTextToChart(""); // hover가 아닐 때 텍스트를 지우도록 수정
//                    }
//                }
        }
    });
}

function addTextToChart(text) {
    var canvas = document.getElementById('myDonutChart');
    var ctx = canvas.getContext('2d');
    var centerX = ((canvas.width/5)*3 );
    var centerY = canvas.height /2 ;

    var fontSize = 2;
    ctx.font = fontSize + "vw 'Jua'";
    ctx.fillStyle = "grey";

    ctx.textBaseline = 'middle';
    ctx.textAlign = 'center';

    ctx.fillText(text, centerX, centerY);
}

 $(".circle-graph").each(function(index, node) {
      let perNum = $(this).attr('circleProgress');
      let fillColors = ["#ffb935", "#e1dc98", "pink","blue"]
      let fillColor = fillColors[index];

      $(this).circleProgress({
        size: 200,
        value: perNum/ 100,
        startAngle: 300,
        thickness: 20,
        fill: {
          color: fillColor
        },
        animation: {
          duration: 1200,
        },
        lineCap: "round",
        reverse: false
      });
    });