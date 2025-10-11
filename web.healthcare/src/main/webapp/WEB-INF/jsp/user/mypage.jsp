<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/_dtd.jsp" %>
<jsp:include page='/WEB-INF/jsp/layout/head.jsp' flush="true">
<jsp:param name="pageType" value="eb-main" />
	<jsp:param name="bodyAttr" value="onload=''" />
	<jsp:param name="bodyStyle" value="eb-main" />
</jsp:include>

<jsp:include page='/WEB-INF/jsp/layout/mainHeader.jsp' />
<title>
	내정보
</title>
<div class="container">
<div class="container_wrap inner">
	<jsp:include page='/WEB-INF/jsp/layout/mainLeft.jsp' flush="true"/>
	<section class="layout_content">
		<div class="content_wrap">
	  	 		<section class="radius_con">	
				<h1 class="radius_con__title">내정보</h1>
				<section class="data_table">
		          <div class="data_table__row">
		            <div class="data_table__th">아이디</div>
		            <div class="data_table__td">
		            	<c:out value="${userId }" />
		            </div>
		          </div>
		          <div class="data_table__row">
		            <div class="data_table__th ">비밀번호</div>
		            <div class="data_table__td">
		            	<a class="form_item_file__btn01" rel="modal:open" href="#passwdModal" id="passModalBtn">비밀번호 변경</a>
		            </div>
		          </div>
		          <div class="data_table__row">
		            <div class="data_table__th ">이름</div>
		            <div class="data_table__td">
		            	<c:out value="${userNm }" />
		            </div>
		          </div>
		          <div class="data_table__row">
		            <div class="data_table__th ">생년월일</div>
		            <div class="data_table__td">
		            	<c:out value="${birthEnc }" />
		            </div>
		          </div>
		          <div class="data_table__row">
		            <div class="data_table__th ">연락처</div>
		            <div class="data_table__td">
		            	<c:out value="${telNumEnc }" />
		            </div>
		          </div>
		          <div class="data_table__row">
		            <div class="data_table__th ">이메일 주소</div>
		            <div class="data_table__td">
		            	<c:out value="${email }" />
		            </div>
		          </div>

		          <c:if test="${userRoleFk eq '3' or userRoleFk eq '4' }">
			          <div class="data_table__row" id="div_deptNm" hidden="hidden">
			            <div class="data_table__th ">소속</div>
			            <div class="data_table__td">
			            	<c:out value="${deptNm }" />

			            </div>
			          </div>
		          </c:if>
		          <c:if test="${userRoleFk eq '1' }">
		          <div class="data_table__row">
		            <div class="data_table__th ">성별</div>
		            <div class="data_table__td">
		            	<c:if test="${gender eq 'M'}">남</c:if>
		            	<c:if test="${gender eq 'F'}">여</c:if>
		            </div>
		          </div>
		          <div class="data_table__row">
		            <div class="data_table__th ">혈액형</div>
		            <div class="data_table__td">
		            	<c:if test="${bloodType ne null and bloodType ne 'null'}"><c:out value="${bloodType }"/></c:if>

		            </div>
		          </div>

		          	  <div class="data_table__row" id="div_deptNm">
			            <div class="data_table__th ">키</div>
			            <div class="data_table__td">
			            	<c:if test="${height ne '' and height ne null  and height ne 'null' }">
			            		<c:out value="${height }" />
			            		<span class="ml5">cm</span>
			            	</c:if>

			            </div>
			          </div>
			          <div class="data_table__row" id="div_deptNm">
			            <div class="data_table__th ">몸무게</div>
			            <div class="data_table__td">
				            <c:if test="${weight ne '' and weight ne null and weight ne 'null'}">
				            	<c:out value="${weight }" />
			            		<span class="ml5">kg</span>
				            </c:if>
			            </div>
			          </div>
			          
			          <div class="data_table__row" id="div_deptNm">
			            <div class="data_table__th ">보호자</div>
			            <div class="data_table__td">
			            	<ul class="user_add_list">
			            		<c:forEach items="${gardianNmArr }" var="item">
			            			<li class="user_add_list__item">${item}</li>
			            		</c:forEach>
                            </ul>
			            </div>
			          </div>
			          <div class="data_table__row" id="div_deptNm">
			            <div class="data_table__th ">담당자</div>
			            <div class="data_table__td">
			            	<ul class="user_add_list">
			            	<c:if test="${doctorNm != null && doctorNm != 'null' && doctorNm != 'null'}">
                              <li class="user_add_list__item">

                                  <strong id="doctorNm">${doctorNm }</strong>
                                  <span class="user_add_list__sub"><c:if test="${doctorDept != null && doctorDept != 'null' && doctorDept != 'null'}"><c:out value="${doctorDept }"></c:out></c:if></span>

                              </li>
                            </c:if>
                            </ul>
							<input class="form_item_text" type="hidden" class="" id="doctorId" name="doctorId" size="50" maxlength="30">
							<input class="form_item_text" type="hidden" class="" id="doctorSeq" name="doctorSeq" size="50" maxlength="30">

			            </div>
			          </div>
			          </c:if>

		        </section>
		        <div class="button_right_group">
		        	<button type="button" class="btn_cancel" id="btn_secession">
						회원 탈퇴
					</button>
					<button type="button" class="btn_modify" onclick="post('/userInfo/uptUserInfo')">
						수정
					</button>
				</div>
			</section>
		</div>
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
  </div>
</div>
<jsp:include page='/WEB-INF/jsp/layout/mainFooter.jsp' />
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
// 	$('#newUserPwEnc').keyup(function(){
		
// 	});
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
	           dataType:"JSON", // 옵션이므로 JSON으로 받을게 아니면 안써도 됨
	           data: {
	        	   "userPwEnc": $('input[name="userPwEnc"]').val(),
	        	   "newUserPwEnc": $('input[name="newUserPwEnc"]').val()
	           },
	           success : function(data) {
               // 통신이 성공적으로 이루어졌을 때 이 함수를 타게 된다.
               // TODO
               	   if(data.resultCode == '0000'){
               			alert('비밀번호가 변경되었습니다.\n다시 로그인 해주세요.')
               			$('.modal').popModal('hide');
//                			$('.modal').modal('hide');
               	   		window.location.href = '/';
               	   }else if(data.resultCode = '3002'){
               			alert('비밀번호를 확인해주세요.');
               	   }
	           },
	           complete : function(data) {
	                 // 통신이 실패했어도 완료가 되었을 때 이 함수를 타게 된다.
	                 // TODO
	                 
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
		           dataType:"JSON", // 옵션이므로 JSON으로 받을게 아니면 안써도 됨
		           data: {},
		           success : function(data) {
	            // 통신이 성공적으로 이루어졌을 때 이 함수를 타게 된다.
	            // TODO
	            	   if(data.resultCode == '0000'){
	            			alert('회원탈퇴 되었습니다.')
	            	   		window.location.href = '/';
	            	   }else{
	            		   alert('회원탈퇴 실패하였습니다.')
	            	   }
		           },
		           complete : function(data) {
		                 // 통신이 실패했어도 완료가 되었을 때 이 함수를 타게 된다.
		                 // TODO
		                 
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