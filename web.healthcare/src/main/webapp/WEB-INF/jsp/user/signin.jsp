<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/_dtd.jsp" %>
<jsp:include page='/WEB-INF/jsp/before/layout/head.jsp' flush="true">
<jsp:param name="pageType" value="eb-main" />
	<jsp:param name="bodyAttr" value="onload=''" />
	<jsp:param name="bodyStyle" value="eb-main" />

</jsp:include>
<title>로그인</title>
<jsp:include page='/WEB-INF/jsp/before/layout/mainHeader.jsp' />

<div class="container">
     <div class="login_layout">
     	<form id="command" name="loginForm" action="/user/loginCheck" method="post">
        <section class="login_box">
          <div class="login_box__header">
            <img src="../images/layout/login_logo.png" alt="">
            <strong class="login_box__logo_txt">나무 Healthcare</strong>
            <span class="login_box__logo_sub">인공지능 기반 건강관리</span>
          </div>
          <div class="login_box__body">
            <section class="login_box__form">
           		<div class="login_box__form_item _id">
              		<input type="text" id="userId" name="userId" class="login_form_text" placeholder="아이디">
                </div>
                <div class="login_box__form_item _pw">
                	<input type="password" id="userPwEnc" name="userPwEnc" class="login_form_password" placeholder="비밀번호">
                </div>

                 <button type="button" class="login_box__form_btn" id="btn_login" onclick="duplicatelogin()">로그인</button>

              </section>
              <section class="login_box__foot _end">

              	  <a href="/user/signup" class="login_box__foot_link" >회원가입</a>

              </section>
            </div>
        </section>
        </form>
     </div>
   </div>

<script>
	$('#userPwEnc').keypress(function(e){
		if(e.keyCode && e.keyCode == 13){
			$('#btn_login').click();
		}
	});

	// 중복 로그인 방지
	function duplicatelogin() {
		if(!!getCookie('acToken')){	//로그인 되어있을때
			location.href = '/';
		}else{
			$.ajax({
		           type:"Post",
		           url:"/user/loginCheck",
		           dataType:"JSON", // 옵션이므로 JSON으로 받을게 아니면 안써도 됨
		           data: {
// 		        	   "userRoleFk": $('select[name="userRoleFk"]').val(),
		        	   "userId": $('input[id="userId"]').val(),
		        	   "userPwEnc": $('input[id="userPwEnc"]').val()
		           },
		           success : function(data) {
		               if(data.resultCode == '0000'){
                            if(data.userRoleFk == '1' ) { //사용자
		                    	location.href = '/userInfo/userBoardInfo?userinfoId=' + data.userId;
		                    }
		                    else {
		                        location.href = '/userInfo/manage_userList';
		                    }
		               }else{
			        	   alert('아이디 또는 비밀번호를 잘못 입력했습니다.\n입력하신 내용을 다시 확인해주세요.')
			           }
		           },
		           complete : function(data) {
		                 // 통신이 실패했어도 완료가 되었을 때 이 함수를 타게 된다.

		           },
		           error : function(xhr, status, error) {
		                console.log(error)
		           }
		     });
// 			$('#command').submit();
		}
	}

</script>
<jsp:include page='/WEB-INF/jsp/before/layout/mainEnd.jsp' />