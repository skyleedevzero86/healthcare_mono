function drawUserList(){

    //통신시작
    $.ajax({
        type:"POST",
        url:"/userInfo/drguardianList",
        // 옵션이므로 JSON으로 받을게 아니면 안써도 됨
        dataType:"JSON",
        data:{
                //,"type": searchType	//	검색 데이터 (minmaxavg 종류 선택)
                "userId": '<c:out value="${searchUserId}" />'
             },
        success : function(data) {
              // 통신이 성공적으로 이루어졌을 때 이 함수를 타게 된다.
             var str = '';
                 data.forEach(function(item) {
                     str += 		'<li class="board_list__box"> ;
                     str +=             '<div class="board_list__item">';
                     str += 			    '<div class="board_con flex-auto">'+item.userRole+'</div>';
                     str += 			    '<div class="board_con flex-auto">'+item.deptNm+'</div>';
                     str += 			    '<div class="board_con flex-auto">'+item.userNm+'</div>';
                     str += 			    '<div class="board_con flex-auto">'+item.email+'</div>';
                     str +=                 '<c:if test="${item.userRole eq ''의사''}">';
                     str += 			        '<div class="board_con flex-auto">010-****-****</div>';
                     str +=                 '</c:if>'
                     str +=                 '<c:if test="${item.userRole eq ''보호자''}">';
                     str += 			       '<div class="board_con flex-auto">'+item.tel_num_enc+'</div>';
                     str +=                 '</c:if>'
                     str += 		    '</div>';
                     str += 		 '</li>';
                 });
                  $('.board_list').append(str);
                  $('.board_list').prop('hidden',false);

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