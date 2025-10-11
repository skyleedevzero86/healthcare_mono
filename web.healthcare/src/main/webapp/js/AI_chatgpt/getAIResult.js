function getAIResult(data, gender, age, userName) {
    $.ajax({
        type:"POST",
        url:"/aidoctor/chat_ai",
        // 옵션이므로 JSON으로 받을게 아니면 안써도 됨
        dataType:"JSON",
        data : {
            "heartrate" : data.heartrate,
            "temperature" : data.temperature ,
            "stress" : data.stress,
            "bloodpressMin" : data.bloodpressMin,
            "bloodpressMax" : data.bloodpressMax,
            "gender" : gender ,
            "age" : age,
            "userNm" : userName,
            "userId" : searchUserId
        },
        success : function(res) {
              // 통신이 성공적으로 이루어졌을 때 이 함수를 타게 된다.
              $('.result_airesponse').html(res.resultData.aiResponse);
              $('.result_airesponse').css('text-align', 'left');
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
