function realtimeBiodata(searchUserId){
    var chartList = [];

    $.ajax({
        type:"POST",
        url:"/health/healthrealtimeData",
        dataType:"JSON",
        data:{ "userId": searchUserId,
               "sleep" : sleepData},
        success : function(data) {
            if(data.resultCode == '0000'){
                setBiodata(data);
            }
        },
        complete : function(data) {
        },
        error : function(xhr, status, error) {
              console.log(error);
        }
    });
}

function setBiodata(data){
    var avgHeartrateMax = $('input[name="avgHeartrateMax"]').val();
    var avgBloodpressMax = $('input[name="avgBloodpressMax"]').val();
    var avgTemperatureMax = $('input[name="avgTemperatureMax"]').val();
    var avgStressMax = $('input[name="avgStressMax"]').val();

    var avgHeartrateMin = $('input[name="avgHeartrateMin"]').val();
    var avgBloodpressMin = $('input[name="avgBloodpressMin"]').val();
    var avgTemperatureMin = $('input[name="avgTemperatureMin"]').val();
    var avgStressMin = $('input[name="avgStressMin"]').val();


 //$('#heartrateMaxSpan').css('height', (data.resultData.heartrate * 0.5)+'%');
     if((data.resultData.heartrate > avgHeartrateMax) || (data.resultData.heartrate < avgHeartrateMin)) {
        $('#heartRate_realtime').text(data.resultData.heartrate).addClass('_state_warning');
     }
     else {
        $('#heartRate_realtime').text(data.resultData.heartrate).addClass('_state');
     }

     var bloodpressAvg = (data.resultData.bloodpressMin + data.resultData.bloodpressMax )/2;
     if((data.resultData.bloodpressMax > avgBloodpressMax) || (data.resultData.bloodpressMax < avgBloodpressMin)) {
         $('#bloodAvg_realtime').text(bloodpressAvg.toFixed(0)).addClass('_state_warning');
     }
     else {
        $('#bloodAvg_realtime').text(bloodpressAvg.toFixed(0)).addClass('_state');
     }

     if((data.resultData.temperature >  avgTemperatureMax) || (data.resultData.temperature < avgTemperatureMin)) {
         $('#temperature_realtime').text(data.resultData.temperature).addClass('_state_warning');
     }
     else {
         $('#temperature_realtime').text(data.resultData.temperature).addClass('_state');
     }

     if(data.resultData.stress < avgStressMin)  {
        $('#stress_realtime').text(data.resultData.stress).addClass('_state_warning');
        $('#stress_target').text(data.resultData.stresscomment);
        $('#stress').text(data.resultData.stress);
     }
     else {
        $('#stress_realtime').text(data.resultData.stress).addClass('_state');
        $('#stress').text(data.resultData.stress).addClass('_state');
        $('#stress_target').text(data.resultData.stresscomment);
     }

     if(data.resultData.steptarget != '50000') {
        $('#step_target').text(data.resultData.steptarget + "보")
     }
     else {
        $('#step_target').text("완료했습니다.")
     }
      $('#step_realtime').html(" <strong class=\"_blue\">"+data.resultData.step + "보!</strong> " + data.resultData.stepcomment)

     if(data.resultData.sleeptarget != '600' && data.resultData.sleeptarget !=  '1000') {
        var sleeptarget =  Math.floor(data.resultData.sleeptarget / 60); //몇시간 예정인지
          if(!Number.isNaN(sleeptarget)){
             var str = "오늘 <strong class=\"_blue\">" + sleeptarget+ "시간 </strong> ";
          }
          else{
            var str = "오늘 <strong class=\"_blue\">0시간 </strong> ";
          }
       $('#sleep_target').html(str + data.resultData.sleepcomment);
     }
     else {
        $('#sleep_target').text(data.resultData.sleepcomment);
     }
     var sleep = Math.floor(data.resultData.sleep / 60)
     if(!Number.isNaN(sleep)){
       $('#sleep_realtime').text(sleep+'시간');
     }
     else{
          $('#sleep_realtime').text('0시간');
     }
     //var sleep =  Math.floor(data.resultData.sleep / 60); //몇시간 잤는지

}