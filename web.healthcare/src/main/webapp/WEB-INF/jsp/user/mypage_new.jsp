<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/_dtd.jsp" %>

<head>
<link rel="stylesheet" href="/css/mzhc/common.css" type="text/css">
<link rel="stylesheet" href="/css/mzhc/Sidebar.css" type="text/css">
<link rel="stylesheet" href="/css/mzhc/header.css" type="text/css">
<link rel="stylesheet" href="/css/mzhc/myPage.css" type="text/css">
<link rel="stylesheet" href="/css/mzhc/footer.css" type="text/css">
<link href="https://fonts.googleapis.com/css2?family==Noto+Sans+KR&Black+Han+Sans&family=Gowun+Dodum&family=Jua&family=Noto+Sans+KR:wght@400;500&display=swap" rel="stylesheet">

<jsp:include page='/WEB-INF/jsp/layout/mainHeader_dev.jsp' />
    <title>
        내정보
    </title>
</head>
<body>
<div class = "App">
	<jsp:include page='/WEB-INF/jsp/layout/mainSidebar.jsp' flush="true"/>
    <main id= "BioInfo" style="">
        <main id="BioMyInfo">
            <div id = "myLoginInfoWrap">
                <div class = "myLoginInfo">
                    <div class = "myLoin">
                        <div class = "imgArea">
                            <c:if test="${not empty userProfile}">
                                <img src="/images/userProfile/${userProfile}" alt = "" style= "width:10vw;height:19vh; border-radius: 50%;">
                            </c:if>
                            <c:if test="${empty userProfile}">
                                 <img src="/images/main/big_profile.png" alt="" style = "width:10vw;height:19vh;">
                            </c:if>
                            <h2> <c:out value="${userId }" /> </h2>
                         </div>
                        <div class = "infoArea">

                            <li>이름 :   <c:out value="${userNm }" /> </li>
                            <li>비밀 번호 : <a class="form_item_file__btn01" rel="modal:open" href="#passwdModal" id="passModalBtn">비밀번호 변경</a></li>
                         </div>
                         <div class = "infoDetailArea">
                            <ul>
                               <li>
                                    <p style = "color:grey;font-size:0.8vw;">혈액형</p>
                                    <h3 style="color:black;font-size:2vw;"> <c:if test="${bloodType ne null and bloodType ne 'null'}"><c:out value="${bloodType }"/></c:if> </h3>
                               </li>
                               <li style="border-left: 1px solid rgb(232,228,228);border-right: 1px solid rgb(232,228,228)">
                                    <p style = "color:grey; font-size:0.8vw;"> 키 </p>
                                    <h3 style="color:black;font-size:2vw;">
                                            <c:if test="${height ne '' and height ne null  and height ne 'null' }">
                                              <c:out value="${height }" />
                                              <span class="ml5" style="font-size:0.8vw;">cm</span>
                                            </c:if>
                                    </h3>
                               </li>
                               <li>
                                    <p style = "color:grey; font-size:0.8vw;"> 몸무게 </p>
                                     <h3 style="color:black;font-size:2vw;">
                                        <c:if test="${weight ne '' and weight ne null and weight ne 'null'}">
                                        <c:out value="${weight }" />
                                         <span class="ml5" style="font-size:0.8vw;">kg</span>
                                        </c:if>
                                     </h3>
                               </li>
                            </ul>
                         </div>
                     </div>
                </div>
            </div>
            <div id = "myDetailInfoWrap">
                <div class = "myDetailInfo">
                    <ul>
                        <li><span class="teal-text">
                            <img src="/images/layout/icons8-email-48.png" style="width:2vw; align-self: flex-start " alt="">
                            E-mail</span>
                            <h3 style="padding-left: 3vw;padding-top: 2vh;"><c:out value="${email }" /></h3></li>
                        <li><span class="teal-text">
                             <img src="/images/layout/icons8-cake-40.png" style="width:2vw; align-self: flex-start " alt="">
                             생일</span>
                            <h3 style="padding-left: 3.5vw;padding-top: 2vh;"><c:out value="${birthEnc }" /> </h3></li>
                        <li><span class="teal-text">
                            <img src="/images/layout/icons8_iphon_48.png" style="width:2vw; align-self: flex-start " alt="">
                            연락처</span>
                            <h3 style="padding-left: 3vw;padding-top: 2vh;"><c:out value="${telNumEnc }" /> </h3></li>
                        <li><span class="teal-text">
                             <img src="/images/layout/icons8-gender-48.png" style="width:2vw; align-self: flex-start " alt="">
                             성별</span>
                           <h3 style="padding-left: 3.5vw;padding-top: 2vh;"> <c:if test="${gender eq 'M'}">남</c:if>
                            <c:if test="${gender eq 'F'}">여</c:if>
                            </h3>
                        </li>
                    </ul>
                </div>
                <div id="btnModify">
                    <button type="button" class="btn_modify" onclick="post('/userInfo/uptUserInfo')">수정</button>
                    <button type="button" class="btn_cancel" id="btn_secession">회원 탈퇴</button>
                </div>
            </div>

        </main>
    </main>
</div>
<div class="container">

            <section class="layout_content">
                <div class="modal" id="passwdModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
                  <div class="modal-dialog" role="document">
                    <div class="modal-content">
                      <div class="modal-header">
                        <h5 class="modal-title">패스워드 변경</h5>
                      </div>
                      <div class="modal-body">
                        <section class="data_table">
                        <form>
                          <div class="data_table__row">
                            <div class="data_table__th must">현재 비밀번호</div>
                            <div class="data_table__td">
                                <input class="form_item_text" type="password" id="userPwEnc" name="userPwEnc" size="15" maxlength="20" title="현재 비밀번호 이름">
                            </div>
                          </div>
                          <div class="data_table__row">
                            <div class="data_table__th must">변경 비밀번호</div>
                            <div class="data_table__td_column">
                                <div class="form_inline_group">
                                    <input class="form_item_text pwCheck" type="password" id="newUserPwEnc" name="newUserPwEnc" size="15" maxlength="20" title="새로운 비밀번호">
                                </div>
                                <div class="sub_info_txt">※ 숫자와 문자를 포함한 8자 이상이어야 합니다.</div>
                                <div class="sub_info_txt" id="ValidataionPw_fail" style="color: red;" hidden="hidden">사용 불가능한 비밀번호입니다.</div>
                                <div class="sub_info_txt" id="ValidataionPw_success" style="color: green;" hidden="hidden">사용 가능한 비밀번호입니다.</div>
                            </div>
                          </div>
                          <div class="data_table__row">
                            <div class="data_table__th must">비밀번호 확인</div>
                            <div class="data_table__td">
                                <input class="form_item_text pwCheck" type="password" id="newUserPwEncCheck" name="newUserPwEncCheck" size="15" maxlength="20" title="비밀번호 확인">
                            </div>
                            <div class="data_table__td" id="ValidataionPwCheck_fail" style="color: red;" hidden="hidden">
                                비밀번호 일치하지 않습니다.
                            </div>
                            <div class="data_table__td" id="ValidataionPwCheck_success" style="color: green;" hidden="hidden">
                                비밀번호 일치합니다.
                            </div>
                          </div>
                        </form>
                        </section>
                      </div>
                      <div class="button_right_group">
                            <button type="button" id="passwdSave" class="btn btn_modify">저장</button>
                            <a href="#close-modal" rel="modal:close" class="btn btn_cancel">취소</a>
                      </div>
                    </div>
                  </div>
                </div>
            </section>

        <jsp:include page='/WEB-INF/jsp/layout/mainFooter_dev.jsp' />
        </div>
</body>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.0.0/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-modal/0.9.1/jquery.modal.min.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jquery-modal/0.9.1/jquery.modal.min.css" />

<script>
	var pwSameTf = null;	// 패스워드 일치 여부 확인
	var pwCheckTf = null;
	$(document).ready(function(){
		$('#ValidataionPwCheck_fail').hide()
		$('#ValidataionPwCheck_success').hide();
	});
	$('.pwCheck').keyup(function(){
		if(!/^(?=.*[A-Za-z@$!%*#?&])(?=.*\d)[A-Za-z\d@$!%*#?&]{8,}$/.test($('#newUserPwEnc').val())){
			$('#ValidataionPw_fail').prop('hidden',false);
			$('#ValidataionPw_success').prop('hidden',true);
			pwCheckTf = false;
		}else{
			$('#ValidataionPw_fail').prop('hidden',true);
			$('#ValidataionPw_success').prop('hidden',false);
			pwCheckTf = true;
		}
		if($('#newUserPwEncCheck').val().length > 0){
			if($('#newUserPwEncCheck').val() == $('#newUserPwEnc').val()){
				$('#ValidataionPwCheck_fail').hide();
				$('#ValidataionPwCheck_success').show();
				pwSameTf = true;
			}else{
				$('#ValidataionPwCheck_fail').show();
				$('#ValidataionPwCheck_success').hide();
				pwSameTf = false;
			}
		}else{
			$('#ValidataionPwCheck_fail').hide();
			$('#ValidataionPwCheck_success').hide();
		}
	});
	
	$('#passwdSave').click(function(){
		if(pwSameTf){
			if(!pwCheckTf)
				alert('비밀번호는 숫자와 문자 또는 특수문자를 포함한 8자 이상이어야 합니다.');
			else{
				if($('#newUserPwEnc').val() == $('#userPwEnc').val()){
					alert('현재 비밀번호와 동일한 비밀번호를 입력하였습니다.');
				}else{
					var tf = confirm("저장하시겠습니까?");
					if(tf == true){
						changePasswd()
					}else{
						return;
					}
				}
			}
		}else{
			alert('비밀번호를 확인해주세요.');
		}
		
	});
	
	function changePasswd(){
		$.ajax({
	           type:"Post",
	           url:"/userInfo/updatePasswd",
	           dataType:"JSON",
	           data: {
	        	   "userPwEnc": $('input[name="userPwEnc"]').val(),
	        	   "newUserPwEnc": $('input[name="newUserPwEnc"]').val()
	           },
	           success : function(data) {
               	   if(data.resultCode == '0000'){
               			alert('비밀번호가 변경되었습니다.\n다시 로그인 해주세요.')
               			$('.modal').popModal('hide');
               	   		window.location.href = '/';
               	   }else if(data.resultCode = '3002'){
               			alert('비밀번호를 확인해주세요.');
               	   }
	           },
	           complete : function(data) {
	           },
	           error : function(xhr, status, error) {
	                 alert("에러발생");
	           }
	     });
	}

	$('.modal').on('modal:close', function (e) {
		$(this).find('form')[0].reset();
		$("#ValidataionPw_fail,#ValidataionPw_success,#ValidataionPwCheck_fail,#ValidataionPwCheck_success").hide();
	});
	
	$('#btn_secession').click(function(){
		var tf = confirm("회원탈퇴 하시겠습니까?");
		if(tf == true){
			$.ajax({
		           type:"Post",
		           url:"/userInfo/secession",
		           dataType:"JSON",
		           data: {},
		           success : function(data) {
	            	   if(data.resultCode == '0000'){
	            			alert('회원탈퇴 되었습니다.')
	            	   		window.location.href = '/';
	            	   }else{
	            		   alert('회원탈퇴 실패하였습니다.')
	            	   }
		           },
		           complete : function(data) {
		           },
		           error : function(xhr, status, error) {
		        	   
		           }
		     });
		}else{
			return;
		}
	})
</script>
<jsp:include page='/WEB-INF/jsp/layout/mainEnd.jsp' />