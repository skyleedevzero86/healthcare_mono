<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/_dtd.jsp" %>
<jsp:include page='/WEB-INF/jsp/before/layout/head.jsp' flush="true">
<jsp:param name="pageType" value="eb-main" />
	<jsp:param name="bodyAttr" value="onload=''" />
	<jsp:param name="bodyStyle" value="eb-main" />
</jsp:include>
<jsp:include page='/WEB-INF/jsp/before/layout/mainHeader.jsp' />
<title>회원가입</title>
<div class="container">
<div class="container_wrap inner">
	<section class="layout_content">
		<div class="content_wrap">
   	 		<section class="radius_con">	
				<h1 class="radius_con__title">회원가입</h1>
				<div class="sub_info_txt"><span class="must"></span> 는 필수 입력값입니다.</div>

				<div id="toggle_div">
					<form action="/user/succesSignup" enctype="multipart/form-data" method="post" id="signupForm">
						<section class="data_table">
						  <div class="data_table__row">
				            <div class="data_table__th must">사용자 구분</div>
				            <div class="data_table__td">
				            	<div class="form_check_group">
				            	<!--  readonly="true" -->
					            	<input type="radio" class="userRole" name="userRoleFk" id="userRoleFk1" class="form_radio_group" value="1" checked="checked"><label for="userRoleFk1" class="form_item_label">사용자</label>
					            	<input type="radio" class="userRole" name="userRoleFk" id="userRoleFk2" class="form_radio_group" value="2"><label for="userRoleFk2" class="form_item_label">보호자</label>
				            	</div>
				            </div>
				          </div>
				          <div class="data_table__row">
				            <div class="data_table__th must">아이디</div>
				            <div class="data_table__td_column">
				            	<div class="form_inline_group">
				            		<input class="form_item_text trim" size="50" maxlength="30" title="아이디" pattern="^([a-z0-9_]){6,50}$" id="userId" name="userId" onkeyup="this.value=this.value.replace(/[^a-zA-Z0-9]/g,'')"/>
									<button type="button" class="form_item_file__btn01" id="popup_button_search">중복검사</button>
				            	</div>
				            	<div class="sub_info_txt">※ 아이디는 영어 또는 숫자로 조합된 6자 이상부터 설정 가능합니다.</div>
								<div class="sub_info_txt" id="duplicate_fail" style="color: red;" hidden="hidden">※ 사용 불가능한 아이디입니다.</div>
								<div class="sub_info_txt" id="duplicate_success" style="color: green;" hidden="hidden">※ 사용 가능한 아이디입니다.</div>
								<div class="sub_info_txt" id="duplicate_change">※ 아이디 중복 검사를 진행해주세요.</div>
				            </div>
				          </div>
				          <div class="data_table__row">
				            <div class="data_table__th must">비밀번호</div>
				            <div class="data_table__td_column">
				            	<div class="form_inline_group">
									<input class="form_item_text pwCheck trim" type="password" id="userPwEnc" name="userPwEnc" title="비밀번호" value="" size="20" maxlength="20" />
								</div>
								<div class="sub_info_txt">※ 숫자와 문자 또는 특수문자를 포함한 8자 이상이어야 합니다.</div>
								<div class="sub_info_txt" id="ValidataionPw_fail" style="color: red;" hidden="hidden">※ 사용 불가능한 비밀번호입니다.</div>
								<div class="sub_info_txt" id="ValidataionPw_success" style="color: green;" hidden="hidden">※ 사용 가능한 비밀번호입니다.</div>
				            </div>
				          </div>
				          <div class="data_table__row">
				            <div class="data_table__th must">비밀번호 확인</div>
				            <div class="data_table__td_column">
				            	<div class="form_inline_group">
									<input class="form_item_text pwCheck trim" type="password" id="userPwEncCheck" name="userPwEncCheck" title="비밀번호 확인" value="" size="20" maxlength="20" />
								</div>
								<div class="sub_info_txt" id="ValidataionPwCheck_fail" style="color: red;" hidden="hidden">※ 비밀번호가 일치하지않습니다.</div>
								<div class="sub_info_txt" id="ValidataionPwCheck_success" style="color: green;" hidden="hidden">※ 비밀번호가 일치합니다.</div>
				            </div>
				          </div>
				          <div class="data_table__row">
				            <div class="data_table__th must">이름</div>
				            <div class="data_table__td">
				            	<input class="form_item_text trim" type="text" id="userNm" name="userNm" size="50" maxlength="30" title="이름" />
				            </div>
				          </div>
				          <div class="data_table__row">
				            <div class="data_table__th must">생년월일</div>
				            <div class="data_table__td">
				            	<input class="form_item_date" type="date" id="birthEnc" name="birthEnc" size="50" maxlength="10" title="생년월일" />
				            </div>
				          </div>
				          <div class="data_table__row">
				            <div class="data_table__th must">연락처</div>
				            <div class="data_table__td_column">
				            	<div class="form_inline_group">
				            		<input class="form_item_text form_number" type="tel" id="telNumEnc" name="telNumEnc" class="" size="20" maxlength="11">
				            	</div>
				            	<div class="sub_info_txt">※ -제외한 숫자만 입력해주세요.</div>
				            </div>
				          </div>
				          <div class="data_table__row">
				            <div class="data_table__th must">이메일 주소</div>
				            <div class="data_table__td_column">
								<div class="form_inline_group">
									<input class="form_item_text trim" type="email" id="email" class="" name="email" size="50" maxlength="30">
								</div>
								<div class="sub_info_txt" id="ValidataionEmailCheck_fail" style="color: red;" hidden="hidden">※ 유효하지 않은 이메일 형식입니다.</div>
				            </div>
				          </div>
				          <c:if test="${userRoleFk eq '4' and userRoleFk eq '3' }">
					          <div class="data_table__row" id="div_deptNm" hidden="hidden">
					            <div class="data_table__th must">소속</div>
					            <div class="data_table__td">
					            	<input type="text" class="form_item_text" id="deptNm" name="deptNm">
									<div id="selectAreaDiv" style="float: left; margin-left: 30px;"></div>
					            </div>
					          </div>
				          </c:if>
				          <div class="data_table__row userDiv">
				            <div class="data_table__th must">성별</div>
				            <div class="data_table__td">
				            	<select id="gender" name="gender" class="form_item_select">
				            		<option value="" selected="selected">선택</option>
				            		<option value="M">남</option>
				            		<option value="F">여</option>
				            	</select>
				            </div>
				          </div>
				          <div class="data_table__row userDiv">
				            <div class="data_table__th must">혈액형</div>
				            <div class="data_table__td">
				            	<select id="bloodType" name="bloodType" class="form_item_select">
				            		<option value="" selected="selected">선택</option>
				            		<option value="A">A</option>
				            		<option value="B">B</option>
				            		<option value="AB">AB</option>
				            		<option value="O">O</option>
				            	</select>
				            </div>
				          </div>
				          <div class="data_table__row userDiv">
				            <div class="data_table__th must">키</div>
				            <div class="data_table__td">
				            	<input class="form_item_text form_number" type="text" class="" id="height" name="height" size="3" maxlength="3">
				            	<span class="ml5">cm</span>
				            </div>
				          </div>
				          <div class="data_table__row userDiv">
				            <div class="data_table__th must">몸무게</div>
				            <div class="data_table__td">
				            	<input class="form_item_text form_number" type="text" class="" id="weight" name="weight" size="3" maxlength="3">
				            	<span class="ml5">kg</span>
				            </div>
				          </div>
				          <div class="data_table__row userDiv">
                            <div class="data_table__th">이미지 업로드</div>
                            <div class="data_table__td">
                                <input type="file" class="" id="profileImg" name="profileImg"></input>
                            </div>
                          </div>

				          <div class="data_table__row">
				            <div class="data_table__th must">개인정보 동의</div>
				            <div class="data_table__td">
				            	<div class="form_inline_group">
					            	<input type="checkbox" class="form_item_check agreeCheck" id="agreeCheck" name="agreeCheck" value="1">
					            	<label for="agreeCheck" class="form_item_label">동의합니다.</label>
					          		<!-- <a type="button" class="form_item_file__btn01" rel="modal:open" href="#agreeModal">약관 확인</a> -->
					          		<a type="button" class="form_item_file__btn01" rel="modal:open" href="#agreeModal" id="agreeModalBtn">약관 확인</a>
				            	</div>
				            </div>
				          </div>
	            		  <input class="form_item_text" type="hidden" class="userRole" name="userRoleFk">
			              <input type="hidden" class="userRole" name="userRoleFk" >

				        </section>
				        <div class="button_right_group">
							<button type="button" id="btn_signup" class="btn_modify">
								회원가입
							</button>
							<a href="/user/signin" class="btn_cancel">취소</a>
						</div>
			        </form>
		        </div>
			</section>
			</div>

			<div class="modal" id="agreeModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
				<div class="modal-dialog" role="document">
					<div class="modal-header">
				    	<h5 class="modal-title">개인정보 제공 동의 약관 확인</h5>
				    </div>
				    <br>
					본인은 귀사에 이력서를 제출함에 따라 [개인정보 보호법] 제15조 및 제17조에 따라
					아래의 내용으로 개인정보를 수집, 이용 및 제공하는데 동의합니다.
					<br>
					<br>
					□ 개인정보의 수집 및 이용에 관한 사항<br>
					- 수집하는 개인정보 항목 (이력서 양식 내용 일체) : 성명, 생년월일, 연락처, 
					주소, 이메일, 가족관계, 학력사항, 경력사항, 자격사항 등과 그 外 이력서 기재 내용 
					일체
					<br>
					 - 개인정보의 이용 목적 : 수집된 개인정보를 사업장 신규 채용 서류 심사 및 인사서
					류로 활용하며, 목적 외의 용도로는 사용하지 않습니다. 
					<br>
					<br>
					□ 개인정보의 보관 및 이용 기간<br>
					- 귀하의 개인정보를 다음과 같이 보관하며, 수집, 이용 및 제공목적이 달성된 경우 
					[개인정보 보호법] 제21조에 따라 처리합니다.   
					<br>
					<br>
					□ 본인은 개인정보 수집 및 이용에 대하여 [□ 동의 □ 부동의]합니다.
					<br>
					<br>
			    <div class="button_right_group">

		        	<a href="#close-modal" rel="modal:close" class="btn btn_cancel" >닫기</a>
				</div>
			    </div>
			</div>
		</section>
	</div>
</div>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.0.0/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-modal/0.9.1/jquery.modal.min.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jquery-modal/0.9.1/jquery.modal.min.css" />
<script type="text/javascript">
	var duplicateTf = false;
	var pwCheckTf = false;
	var pwSameTf = false; 
	var validation = [];
	var EmailCheckTf = false;
	var readTerms = false;
	
	$('input[name="userRoleFk"]').change(function(){
		if($('input[name="userRoleFk"]:checked').val() == 1){
			$('.userDiv').each(function(index,item){
				$(item).show();
			});
		}else{
			$('.userDiv').each(function(index,item){
				$(item).hide();
			});
		}
	});
	

	
	$('#parentSearch').click(function(){
		$.ajax({
	           type:"Post",
	           url:"/user/parentSearch",
	           data:{
	        	   "parentNm": $('input[name="parentNm"]').val(),
	        	   "parentBirthEnc": $('input[name="parentBirthEnc"]').val(),
	        	   "parentTelNumEnc": $('input[name="parentTelNumEnc"]').val()
	           },
				// 옵션이므로 JSON으로 받을게 아니면 안써도 됨
	           dataType:"JSON", 
	           success : function(data) {
	                 // 통신이 성공적으로 이루어졌을 때 이 함수를 타게 된다.
	                 // TODO
	                 if(data.resultCode == '3001'){
	                	 alert('검색결과가 없습니다.');
	                 }else if(data.resultCode == '0000'){
		                 var str = '';
		                 if(data.resultCode == '0000'){
		                	 	 str = 	'<tr>';
		                		 str += 	'<th>이름</th>';
	                			 str += 	'<th>연락처</th>';
                			 	 str += 	'<th>선택</th>';
	                			 str +=	'</tr>';
		                	 data.resultData.forEach(function(item) {
		                		 str += '<tr>';
		                		 str += 	'<td>'+item.userNm+'</td>';
		                		 str += 	'<td>'+item.telNumEnc+'</td>';
		                		 str += 	'<td>'+'<button class="form_item_file__btn01" onclick="parentSelect('+item.userSeq+',\''+item.userId+'\',\''+item.userNm+'\',\''+item.telNumEnc+'\')">선택</button>'+'</td>';
	                			 str += '</tr>';
	                		 });
		                 }else if(data.resultCode == '3001'){
		                	 str = 	'<tr>';
	                		 str += 		'<th>이름</th>';
	            			 str += 		'<th>연락처</th>';
	            			 str += 	'</tr>';
	                		 str += 	'<tr>';
	                		 str += 		'<td colspan="3">검색 결과가 없습니다.</td>';
	            			 str +=		'</tr>';
		                 }
		                 $('#parentTb').html(str);
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
	});
	
	
	$('#doctorSearch').click(function(){
		$.ajax({
	           type:"Post",
	           url:"/user/doctorSearch",
	           data:{
	        	   "doctorNm": $('input[name="doctorNm"]').val()
	           },
				// 옵션이므로 JSON으로 받을게 아니면 안써도 됨
	           dataType:"JSON", 
	           success : function(data) {
	                 // 통신이 성공적으로 이루어졌을 때 이 함수를 타게 된다.
	                 // TODO
	                 var str = '';
	                 if(data.resultCode == '0000'){
	                	 	 str = 	'<tr>';
	                		 str += 	'<th>담당자명</th>';
                			 str += 	'<th>소속</th>';
                			 str += 		'<th>선택</th>';
                			 str +=	'</tr>';
	                	 data.resultData.list.forEach(function(item) {
	                		 str += '<tr>';
	                		 str += 	'<td>'+item.userNm+'</td>';
	                		 str += 	'<td>'
	                		 if(item.deptNm != null)
	                		 	str +=		item.deptNm;
	                		 str +=		'</td>';
	                		 str += 	'<td>'+'<button class="form_item_file__btn01" onclick="doctorSelect('+item.userSeq+',\''+item.userId+'\',\''+item.userNm.toString()+'\',\''+item.deptNm+'\')">선택</button>'+'</td>';
                			 str += '</tr>';
                		 });
	                 }else if(data.resultCode == '3001'){
	                	 str = 	'<tr>';
                		 str += 		'<th>담당자명</th>';
            			 str += 		'<th>소속</th>';
            			 str += 		'<th>선택</th>';
            			 str += 	'</tr>';
                		 str += 	'<tr>';
                		 str += 		'<td colspan="3">검색 결과가 없습니다.</td>';
            			 str +=		'</tr>';
	                 }
	                 $('#doctorTb').html(str);
	           },
	           complete : function(data) {
	                 // 통신이 실패했어도 완료가 되었을 때 이 함수를 타게 된다.
	                 // TODO
	           },
	           error : function(xhr, status, error) {
	                 alert("에러발생");
	           }
	     });
	})
	
	
	
	// min, max, 공백 체크
	$('#popup_button_search').click(function(){
		duplicateTf = false;
		if($('#userId').val().trim().length>5){
			$.ajax({
		           type:"POST",
		           url:"/user/duplicateId",
		        	// 옵션이므로 JSON으로 받을게 아니면 안써도 됨
		           dataType:"JSON", 
		           data: {
		        	   "userId": $('input[name="userId"]').val()
		        	   },
		           success : function(data) {
		                 // 통신이 성공적으로 이루어졌을 때 이 함수를 타게 된다.
		                 if(data.resultCode == '0000'){
		                	 $('#duplicate_success').prop('hidden',false);
		                	 $('#duplicate_fail').prop('hidden',true);
		                	 $('#duplicate_change').prop('hidden',true);
		                	 duplicateTf = true;
		                 }else{
		                	 $('#duplicate_success').prop('hidden',true);
		                	 $('#duplicate_fail').prop('hidden',false);
		                	 $('#duplicate_change').prop('hidden',true);
		                	 duplicateTf = false;
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
		}else{
			if($('#userId').val().includes(' ')){
				alert('공백을 사용할 수 없습니다.');
			}else if($('#userId').val().trim().length<6){
				alert('아이디를 6글자 이상 입력해주세요.');
			}
		}

	});

	$('#btn_signup').click(function(){
		var incorrect = false;
		
		var role =  $('input[name="userRoleFk"]:checked').val();
		switch(role){
			case '1':	// 사용자
				validation = ['userId', 'userPwEnc', 'userPwEncCheck', 'userNm', 'birthEnc', 'telNumEnc', 'email', 'gender', 'bloodType', 'height', 'weight' , 'profileImg'];
				break;
			case '2':	// 보호자
				validation = ['userId', 'userPwEnc', 'userPwEncCheck', 'userNm', 'birthEnc', 'telNumEnc', 'email'];
				break;
			default :	// 담당자, 관리자
				validation = ['userId', 'userPwEnc', 'userPwEncCheck', 'userNm', 'birthEnc', 'telNumEnc', 'email'];
				incorrect = true;
				break;
		}
		
		if(!incorrect){	// 올바르지 않은 회원 가입 X
			var trimTf = true;
			validation.forEach(function(item, idx){
				if($('#'+item).val().includes(' ')){
					$('#'+tag).focus();
					trimTf = false;
				}
				if(!trimTf){
					alert('공백을 사용할 수 없습니다.');
				}
				
			})
			var obj = validationCheck(validation);
			var validationTf = obj.tf;
			var tag = obj.tag;
			if(validationTf){
				if(!duplicateTf){
					alert('아이디 중복체크를 진행해주세요.');
			    	return;
				}
				if(!pwCheckTf){
					alert('비밀번호가 적합하지않습니다.');
					return;
				}
				if(!pwSameTf){
					alert('비밀번호가 일치하지않습니다.');
					return;
				}
				if(!EmailCheckTf){
					alert('이메일을 올바르게 작성해주세요.');
					return;
				}
				if(!readTerms){
					alert('약관을 동의해주세요.');
					return;
				}
				else{
					$('#signupForm').submit();
				}	
			}else{
				$('#'+tag).focus();
				alert('필수로 작성해야할 내용을 입력해주세요.');
				return;
			}
		}else{	// 올바르지 않은 회원 가입 O
			alert('올바르지 않은 접근입니다.');
		}
	});
	
	function validationCheck(validation){
		var obj = {
				tf: true,
				tag: ''
		}
		validation.forEach(function(item, idx){
			if(obj.tf){
				if($('#'+item).attr('type') == 'checkbox'){
					if(!$('#'+item).prop('checked')){
						obj.tag = item;
						obj.tf = false;
					}
				}
				else if($('#'+item).attr('type')=='file') {
				}
				else{
					var text = $('#'+item).val().trim();
					if(text.length < 1){
						obj.tag = item;
						obj.tf = false;
					}
				}
			}else{
				return false;
			}
		});
		return obj;
	}
	
	// 비밀번호 적합성 체크
	$('#userPwEnc').keyup(function(){
		if(!/^(?=.*[A-Za-z@$!%*#?&])(?=.*\d)[A-Za-z\d@$!%*#?&]{8,}$/.test($('#userPwEnc').val())){
			$('#ValidataionPw_fail').prop('hidden',false);
			$('#ValidataionPw_success').prop('hidden',true);
			$('#userPwEnc').val();
			pwCheckTf = false;
		}else{
			$('#ValidataionPw_fail').prop('hidden',true);
			$('#ValidataionPw_success').prop('hidden',false);
			pwCheckTf = true;
		}
	});
	
	$('.pwCheck').keyup(function(){
		if($('#userPwEncCheck').val() == $('#userPwEnc').val()){
			$('#ValidataionPwCheck_fail').prop('hidden', true);
			$('#ValidataionPwCheck_success').prop('hidden', false);
			pwSameTf = true;
		}else{
			$('#ValidataionPwCheck_fail').prop('hidden', false);
			$('#ValidataionPwCheck_success').prop('hidden', true);
			pwSameTf = false;
		}
	});
	
	$('#userId').keyup(function(){
		duplicateTf = false;
		$('#duplicate_success').prop('hidden',true);
   	 	$('#duplicate_fail').prop('hidden',true);
		$('#duplicate_change').prop('hidden',false);
	});
	
	function doctorSelect(seq, userId, doctorNm, deptNm){
		$('#newDoctorSeq').val(seq);
		$('#newDoctorId').val(userId);
		$('#newDoctorNm').val(doctorNm);
		$('#newDoctorDept').val(deptNm);
		doctorList();
	}
	function doctorRemove(){
		$('#newDoctorSeq').val('');
		$('#newDoctorId').val('');
		$('#newDoctorNm').val('');
		$('#newDoctorDept').val('');
		$('#addDoctor').html('');
	}
	function doctorList(){
		var head = '등록된 담당자'
		head += '<section class="data_table">' 
		var str = '';
			str += '<div class="data_table__row">';
			str += 		'<div class="data_table__td">';
			str +=		$('#newDoctorNm').val();
			str += 		'<button class="form_item_file__btn01" type="button" onclick="doctorRemove()">삭제</button>'
			str += 		'</div>';
			str += '</div>';
		str = str != '' ? head+str +'</section>' : str;
		$('#addDoctor').html(str)
	}
	
	function parentList(){
		var parentsArr = $('#befGuardian').val() != ''&& $('#befGuardian').val() != null && $('#befGuardian').val() != 'null'? JSON.parse($('#befGuardian').val()):[];
		var arr = $('#regGuardian').val() != ''&& $('#regGuardian').val() != null ? JSON.parse($('#regGuardian').val()):[];
		var head = '등록된 보호자'
		head += '<section class="data_table">' 
		var str = '';
		
		if(parentsArr.length>0){
			parentsArr.forEach(function(item, idx){
				str += '<div class="data_table__row">';
				str += 		'<div class="data_table__td">';
				str +=		item.userNm+'\t'+item.telNumEnc
				str += 		'<button type="button" class="form_item_file__btn01" onclick="remove( \'bef\','+idx+')">삭제</button>'
				str += 		'</div>';
				str += '</div>';
			});
		}
		if(arr.length>0){
			arr.forEach(function(item, idx){
				str += '<div class="data_table__row">';
				str += 		'<div class="data_table__td">';
				str +=		item.userNm+'\t'+item.telNumEnc
				str += 		'<button type="button" class="form_item_file__btn01" onclick="remove( \'reg\','+idx+')">삭제</button>'
				str += 		'</div>';
				str += '</div>';
			});
		}
		str = str != '' ? head+str +'</section>' : str;
		$('#adduser').html(str);
	}
	$('#guardianSave').click(function(){
		var tf = confirm("담당자 선택을 완료하시겠습니까?");
		if(tf == true){
			var parentsArr = $('#befGuardian').val() != ''&& $('#befGuardian').val() != null && $('#befGuardian').val() != 'null'? JSON.parse($('#befGuardian').val()):[];
			var arr = $('#regGuardian').val() != '' && $('#regGuardian').val() != null && $('#regGuardian').val() != 'null'? JSON.parse($('#regGuardian').val()): null;
			if(arr != null && arr !=''){
				arr.forEach(function(item){
					var obj = new Object({
						"guardianSeq" : item.guardianSeq,
						"guardianId" : item.guardianId,
						"userNm": item.userNm,
						"telNumEnc": item.telNumEnc
					});
					parentsArr.push(obj)
				});
			}
			var str = '';
			parentsArr.forEach(function(item){
				if(str != '' && str != null)
					str += ',';
				str += item.userNm+' '+item.telNumEnc;
			});
			$('#parentNm').text(str);
			$('input[name="guardian"]').val(JSON.stringify(parentsArr));
			
			$('#guardian_cancle').click();
		}else{
			return;
		}
	});
	
	$('#doctorSave').click(function(){
		var tf = confirm("담당자 선택을 완료하시겠습니까?");
		if(tf == true){
			$('#doctorSeq').val($('#newDoctorSeq').val());
			$('#doctorId').val($('#newDoctorId').val());
			$('#doctorNm').html($('#newDoctorNm').val());
			$('#doctorDept').html($('#newDoctorDept').val());
			$('#doctor_cancle').click();
		}else{
			return;
		}
	});
	$('#doctor_cancle').click(function(){

		$('#searchDoctorNm').val('')
		$('#doctorTb').html('');
		$('#doctor_cancle_real').click();
	})
	
	function parentSelect(seq, userId, userNm, telNumEnc){
		var arr = $('input[name="guardian"]').val() != '' && $('input[name="guardian"]').val() != null ? JSON.parse($('input[name="guardian"]').val()): [];
		var oldtf = true;
		var newtf = true;
		var arr = $('#regGuardian').val() != '' && $('#regGuardian').val() != null ? JSON.parse($('#regGuardian').val()): [];


		arr.forEach(function(item){
			if(item.guardianSeq == seq)
				newtf = false;
		});
		if(oldtf){
			if(newtf){
				var obj = new Object({
					"guardianSeq":seq,
					"guardianId": userId,
					"userNm": userNm,
					"telNumEnc": telNumEnc
				});
				arr.push(obj);
				$('#regGuardian').val(JSON.stringify(arr));

				arr.forEach(function(item){
					if(item.guardianSeq == seq)
						tf = false;
				});

				parentList();
			}else{
				alert('이미 추가된 목록에 있는 사용자입니다.');
			}
		}else{
			alert('이미 보호자로 지정되어있습니다.');
		}
	}
	
	$('#btn_docModal_open').click(function(){
		if($('#doctorSeq').val() != '' && $('#doctorSeq').val() != null ){
			$('#newDoctorSeq').val($('#doctorSeq').val());
			$('#newDoctorId').val($('#doctorId').val());
			$('#newDoctorNm').val($('#doctorNm').text());
			$('#doctor_cancle').click();
			doctorList();
		}
	});
	
	$('#btn_parModal_open').click(function(){
		parentList();
	});
	
	$('#guardian_cancle').click(function(){
		$('input[name="parentNm"]').val('');
		$('#parentBirthEnc').val('');
		$('#parentTelNumEnc').val('');
		$('#parentTb').html('')
		$('#guardian_cancle_real').click();
	});
	
	function remove(tag, idx){
		var parentsArr = tag == 'bef' ? JSON.parse($('#befGuardian').val()): JSON.parse($('#regGuardian').val());
		parentsArr = parentsArr.filter(function(item, index) {
		    return idx != index;
		});
		var result = parentsArr.length>0 ? JSON.stringify(parentsArr): '';
		
		if(tag == 'bef')
			$('#befGuardian').val(result);
		else
			$('#regGuardian').val(result);
		parentList();
	}
	
	$('.form_number').keyup(function(){
		$(this).val($(this).val().replaceAll(/[^0-9]/g,''));
	});
	
	/* 공백제거 */
	$('.trim').keyup(function(){
		$(this).val($(this).val().replace(/\s/g,''));
	});

	/* 숫자타입 입력제한 */
	$('.form_number').keyup(function(){
		$(this).val($(this).val().replaceAll(/[^0-9]/g,''));
	});

	/* 약관 확인버튼 click event */
	$('#agreeModalBtn').click(function(){
		readTerms = true;
	});
	/* 개인정보 동의 체크 시 약관 확인여부 체크 */
	$('#agreeCheck').on("change",function(){
		if($(this).is(':checked') && readTerms == false){
			alert('약관을 확인해주세요.');
			$(this).prop('checked',false);
			return false;
		}
	});
	
	var regexEmail = /^([0-9a-zA-Z_\.-]+)@([0-9a-zA-Z_-]+)(\.[0-9a-zA-Z_-]+){1,2}$/;
	$('#email').keyup(function(){
		emailCheck();
	});
	
	function emailCheck(){
		if(regexEmail.test($('#email').val())){
			$('#ValidataionEmailCheck_fail').prop('hidden',true);
			EmailCheckTf = true;
		}else{
			$('#ValidataionEmailCheck_fail').prop('hidden',false);
			$('#userPwEnc').val();
			EmailCheckTf = false;
		}
	}
	
	$("#birthEnc").on("input", function() {
        const inputValue = $(this).val();

        if (isFutureDate(inputValue)) {
            alert("미래 날짜는 입력할 수 없습니다.");
            $(this).val(""); // 입력 값을 초기화
        }else if (!isValidDate(inputValue)) {
            alert("올바른 형식(YYYY-MM-DD)으로 입력하세요.");
            $(this).val(""); // 입력 값을 초기화
        }
    });

    function isValidDate(dateString) {
        const pattern = /^\d{4}-\d{2}-\d{2}$/;
        if (!pattern.test(dateString)) return false;

        const parts = dateString.split("-");
        const year = parseInt(parts[0], 10);
        const month = parseInt(parts[1], 10);
        const day = parseInt(parts[2], 10);

        if (year < 1900 || year > new Date().getFullYear()) return false;
        if (month < 1 || month > 12) return false;
        if (day < 1 || day > new Date(year, month, 0).getDate()) return false;

        return true;
    }

    function isFutureDate(dateString) {
        const parts = dateString.split("-");
        const year = parseInt(parts[0], 10);
        const month = parseInt(parts[1], 10);
        const day = parseInt(parts[2], 10);

        const currentDate = new Date();
        const inputDate = new Date(year, month - 1, day); // 월은 0부터 시작하므로 -1

        return inputDate > currentDate;
    }
</script>
<jsp:include page='/WEB-INF/jsp/before/layout/mainEnd.jsp' />