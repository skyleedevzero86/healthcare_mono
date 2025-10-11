<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/_dtd.jsp" %>
<jsp:include page='/WEB-INF/jsp/layout/head.jsp' flush="true">
<jsp:param name="pageType" value="eb-main" />
	<jsp:param name="bodyAttr" value="onload=''" />
	<jsp:param name="bodyStyle" value="eb-main" />
</jsp:include>
<jsp:include page='/WEB-INF/jsp/layout/mainHeader.jsp' />
<title>
	내정보 수정
</title>
<div class="container">
<div class="container_wrap inner">
	<jsp:include page='/WEB-INF/jsp/layout/mainLeft.jsp' flush="true"/>
	<section class="layout_content">
		<div class="content_wrap">
   	 		<section class="radius_con">	
				<h1 class="radius_con__title">내정보 수정</h1>
				<div class="sub_info_txt"><span class="must"></span> 는 필수 입력값입니다.</div>
				<div id="toggle_div">
					<form action="/userInfo/uptUserInfoAct" method="post" id="uptUserForm">
						<section class="data_table">
				          <div class="data_table__row">
				            <div class="data_table__th must">이름</div>
				            <div class="data_table__td">
				            	<input class="form_item_text trim" type="text" id="userNm" name="userNm" size="50" maxlength="30" title="이름" value="<c:out value="${userNm }"></c:out>"/>
				            </div>
				          </div>
						  <div class="data_table__row">
				            <div class="data_table__th must">생년월일</div>
				            <div class="data_table__td">
								<input class="form_item_date form_date" type="date" id="birthEnc" name="birthEnc" size="50" maxlength="10" title="생년월일" value="<c:out value="${birthEnc }"></c:out>"/>
				            </div>
				          </div>
				          <div class="data_table__row">
				            <div class="data_table__th must">연락처</div>
				            <div class="data_table__td">
				            	<input class="form_item_text form_number" type="tel" id="telNumEnc" name="telNumEnc" class="" size="20" maxlength="13" value="<c:out value="${telNumEnc }"></c:out>">
				            </div>
				          </div>
				          <div class="data_table__row">
				            <div class="data_table__th must">이메일 주소</div>
				            <div class="data_table__td_column">
								<div class="form_inline_group">
									<input class="form_item_text trim" type="email" id="email" class="" name="email" size="50" maxlength="30" value="<c:out value="${email }"></c:out>">
								</div>
								<div class="sub_info_txt" id="ValidataionEmailCheck_fail" style="color: red;" hidden="hidden">※ 유효하지 않은 이메일 형식입니다.</div>
				            </div>
				          </div>
				          <c:if test="${userRoleFk eq '3' or userRoleFk eq '4' }">
					          <div class="data_table__row" id="div_deptNm" <c:if test="${userRoleFk < 3 }">hidden="hidden"</c:if>>
					            <div class="data_table__th must">소속</div>
					            <div class="data_table__td">
					            	<input type="text" class="form_item_text" id="deptNm" name="deptNm" value="<c:out value="${deptNm }"></c:out>">
									<div id="selectAreaDiv" style="float: left; margin-left: 30px;"></div>
					            </div>
					          </div>
				          </c:if>
				          <c:if test="${userRoleFk eq '1' }">
				          <div class="data_table__row">
				            <div class="data_table__th must">성별</div>
				            <div class="data_table__td">
				            	<select id="gender" name="gender" class="form_item_select">
				            		<option value="" <c:if test="${gender eq '' or gender eq null}">selected="selected"</c:if>>선택</option>
				            		<option value="M" <c:if test="${gender eq 'M'}">selected="selected"</c:if>>남</option>
				            		<option value="F" <c:if test="${gender eq 'F'}">selected="selected"</c:if>>여</option>
				            	</select>
				            </div>
				          </div>
				          <div class="data_table__row">
				            <div class="data_table__th must">혈액형</div>
				            <div class="data_table__td">
				            	<select id="bloodType" name="bloodType" class="form_item_select">
				            		<option value="" <c:if test="${bloodType eq '' or bloodType eq null}">selected="selected"</c:if>>선택</option>
				            		<option value="A" <c:if test="${bloodType eq 'A'}">selected="selected"</c:if>>A</option>
				            		<option value="B" <c:if test="${bloodType eq 'B'}">selected="selected"</c:if>>B</option>
				            		<option value="AB" <c:if test="${bloodType eq 'AB'}">selected="selected"</c:if>>AB</option>
				            		<option value="O" <c:if test="${bloodType eq 'O'}">selected="selected"</c:if>>O</option>
				            	</select>
				            </div>
				          </div>
				          <div class="data_table__row">
				            <div class="data_table__th must">키</div>
				            <div class="data_table__td">
				            	<input class="form_item_text form_number" type="text" id="height" name="height" size="3" maxlength="3" value="<c:if test="${height ne '' and height ne null  and height ne 'null' }"><c:out value="${height }"></c:out></c:if>">
				            	<span class="ml5">cm</span>
				            </div>
				          </div>
				          <div class="data_table__row">
				            <div class="data_table__th must">몸무게</div>
				            <div class="data_table__td">
				            	<input class="form_item_text form_number" type="text" id="weight" name="weight" size="3" maxlength="3" value="<c:if test="${weight ne '' and weight ne null  and weight ne 'null' }"><c:out value="${weight }"></c:out></c:if>">
				            	<span class="ml5">kg</span>
				            </div>
				          </div>
					          <div class="data_table__row">
					            <div class="data_table__th">보호자</div>
					            <div class="data_table__td">
					            	<div class="user_add_box">
						            	<ul class="user_add_list" id="parentNm">
						            		<c:forEach items="${gardianNmArr }" var="item">
					            				<li class="user_add_list__item">${item}</li>
						            		</c:forEach>
			                            </ul>
			                            <input class="form_item_text" type="hidden" class="" id="guardian" name="guardian" value='<c:out value='${guardian }'></c:out>'>
			                            <div class="user_add_btn">
			                            	<a type="button" class="form_item_file__btn01" rel="modal:open" id="btn_parModal_open" href="#parModal" data-toggle="modal" data-target="#parModal">보호자 추가/삭제</a>
			                            </div>
		                            </div>
					            </div>
					          </div>
					          <div class="data_table__row">
					            <div class="data_table__th">담당자</div>
						            <div class="data_table__td">
						            	<ul class="user_add_list" id="addDoctorUl">
						            	<c:if test="${doctorNm != null && doctorNm != 'null' && doctorNm != 'null'}">
			                              <li class="user_add_list__item">
			                                <!-- <a href="#none"> -->
			                                  <strong>${doctorNm }</strong>
			                                  <span class="user_add_list__sub"><c:if test="${doctorDept != null && doctorDept != 'null' && doctorDept != 'null'}"><c:out value="${doctorDept }"></c:out></c:if></span>
			                                <!-- </a> -->
			                              </li>
			                            </c:if>
			                            </ul>
	                            <div class="user_add_btn">
	                              <a class="form_item_file__btn01" id="btn_docModal_open" rel="modal:open" href="#docModal"  >담당자 추가/삭제</a>
	                            </div>
									<input class="form_item_text" type="hidden" class="" id="doctorId" name="doctorId" size="50" maxlength="30" hidden="hidden" value="<c:out value="${doctorId }"></c:out>">
									<input class="form_item_text" type="number" class="" id="doctorSeq" name="doctorSeq" size="50" maxlength="30" hidden="hidden" value="<c:out value="${doctorSeq }"></c:out>">
									<input class="form_item_text" type="hidden" class="" id="doctorNm"  size="50" maxlength="30" hidden="hidden" value="<c:out value="${doctorNm }"></c:out>">
									<input class="form_item_text" type="hidden" class="" id="doctorDept" size="50" maxlength="30" hidden="hidden" value="<c:out value="${doctorDept }"></c:out>">
					            </div>
					          </div>
				          </c:if>
	            		  <input class="form_item_text" type="hidden" class="userRole" name="userRoleFk" value="<c:out value='${userRoleFk }'/>">
			              <input type="hidden" class="userRole" name="userRoleFk" >
				        </section>
				        <div class="button_right_group">
							<a href="javascript:submit()" class="btn_modify">저장</a>
							<a href="/userInfo/mypage" class="btn_cancel">취소</a>
						</div>
			        </form>
		        </div>
		        <div class="modal" id="docModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
				  <div class="modal-dialog" role="document">
				    <div class="modal-content">
				      <div class="modal-header">
				        <h5 class="modal-title">담당자 추가/삭제</h5>
				      </div>
				      <div class="modal-body">
				      	<section class="data_table">
				          <div class="data_table__row">
				            <div class="data_table__th must">담당자 이름</div>
				            <div class="data_table__td">
			            		<input class="form_item_text searchMust" name="doctorNm" size="20" maxlength="30" title="담당자 이름">
				            </div>
				          </div>
				          <div class="data_table__row">
	                          <div class="data_table__th">담당자</div>
	                          <div class="data_table__td">
	                            <ul class="user_add_list" id="addDoctor">
	                            </ul>
	                            <div class="user_add_btn">
	                            	<button type="button" id="doctorSearch" class="form_item_file__btn01" >검색</button>
	                              <!-- <button type="button" class="form_item_file__btn01">의사 추가/삭제</button> -->
	                            </div>
                          </div>
                        </div>
				      	</section>
		            	<div class="table_box" id="doctorTb" hidden="hidden">
	                    </div>
				      	<input type="hidden" class="form_item_text searchMust" id="newDoctorId" size="20" maxlength="30" title="담당자 이름">
				      	<input type="hidden" class="form_item_text searchMust" id="newDoctorSeq" size="20" maxlength="30" title="담당자 일련번호">
				      	<input type="hidden" class="form_item_text searchMust" id="newDoctorNm" size="20" maxlength="30" title="담당자명">
				      	<input type="hidden" class="form_item_text searchMust" id="newDoctorDept" size="20" maxlength="30" title="담당자 소속">
				      </div>
				      <div class="button_right_group">
				      		<a href="#close-modal" class="btn btn_modify" id="doctorSave">저장</a>
				        	<button  id="doctor_cancle" class="btn btn_cancel" >취소</button>
			        		<a href="#close-modal" rel="modal:close" id="doctor_cancle_real" class="btn btn_cancel" hidden="hidden"></a>
					  </div>
				    </div>
				  </div>
				</div>
				<div class="modal" id="parModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
				  <div class="modal-dialog" role="document">
				    <div class="modal-content">
				      <div class="modal-header">
				        <h5 class="modal-title">보호자 추가/삭제</h5>
				      </div>
				      <div class="modal-body">
				      <form id="parentForm">
				        <section class="data_table">
				          <div class="data_table__row">
				            <div class="data_table__th must">이름</div>
				            <div class="data_table__td">
				            	<input class="form_item_text searchMust parentKeyEvnt" id="parentNm" name="parentNm" size="20" maxlength="30" title="이름">
				            </div>
				          </div>
				          <div class="data_table__row">
				            <div class="data_table__th must">생년월일</div>
				            <div class="data_table__td">
				            	<input class="form_item_date searchMust parentKeyEvnt" type="date" id="parentBirthEnc" name="parentBirthEnc" size="50" maxlength="10" title="생년월일">
				            </div>
				          </div>
				          <div class="data_table__row">
				            <div class="data_table__th must">연락처</div>
				            <div class="data_table__td">
				            	<input class="form_item_text searchMust parentKeyEvnt" type="tel" id="parentTelNumEnc" name="parentTelNumEnc" size="20" title="전화번호" maxlength="13">
				            	
				            </div>
				          </div>
				          <div class="data_table__row">
                          <div class="data_table__th">보호자</div>
                          <div class="data_table__td">
                          <div class="user_add_box">
                              <ul id="adduser" class="user_add_list">
                              </ul>
                              <div class="user_add_btn">
                              	<button type="button" id="parentSearch" class="form_item_file__btn01" >검색</button>
                              </div>
                            </div>
                          </div>
                        </div>
				          <input type="hidden" id="regGuardian" />
				          <input type="hidden" id="befGuardian" />
				        </section>
				          </form>
				        <div class="table_box" id="parentTb" hidden="hidden">

	                    </div>
				      </div>
				      </div>
				      <div class="button_right_group">
				      		<a href="#close-modal" class="btn btn_modify" id="guardianSave" >저장</a>
				        	<button  id="guardian_cancle" class="btn btn_cancel" >취소</button>
			        		<a href="#close-modal" rel="modal:close" id="guardian_cancle_real" class="btn btn_cancel" hidden="hidden">취소</a>
					  </div>
				   </div>
				</div>
			</section>
		</div>
	</section>
</div>
</div>
<jsp:include page='/WEB-INF/jsp/layout/mainFooter.jsp' />
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.0.0/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-modal/0.9.1/jquery.modal.min.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jquery-modal/0.9.1/jquery.modal.min.css" />
<script>
	$('#parentSearch').click(function(){
		var tf = true;
		var id = '';
		$('.parentKeyEvnt').each(function(idx,item){
			if(tf){
				if($(item).val() == null || $(item).val() == ''){
					tf = false;
					id = $(item).attr('id');
					return ;
				}
			}else
				return;
		})
		if(tf){
			$.ajax({
		           type:"Post",
		           url:"/userInfo/parentSearch",
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
			                	 	 str +=	'<table>'
			                	 	 str += 	'<thead>';
			                	 	 str +=			'<tr>';
			                		 str += 			'<th>이름</th>';
			                		 str += 			'<th>생년월일</th>';
		                			 str += 			'<th>연락처</th>';
		            			 	 str += 			'<th>삭제</th>';
	                			 	 str +=			'</tr>';
		                			 str +=		'</thead>';
			                	 data.resultData.forEach(function(item) {
			                		 str += 	'<tbody>';
			                		 str += 		'<tr>';
			                		 str += 			'<td>'+item.userNm+'</td>';
			                		 str += 			'<td>'+item.birthEnc+'</td>';
			                		 str += 			'<td>'+item.telNumEnc+'</td>';
			                		 str += 			'<td>'+'<button type="button" class="form_item_file__btn01" onclick="parentSelect('+item.userSeq+',\''+item.userId+'\',\''+item.userNm+'\',\''+item.telNumEnc+'\')">선택</button>'+'</td>';
			                		 str += 		'</tr>';
		                			 str += 	'</tbody>';
		                			 str += '</table>';
		                		 });
			                 }else if(data.resultCode == '3001'){
			                	 str +=	'<table>'
			                	 str = 		'<thead>';
		                	 	 str +=			'<tr>';
		                		 str += 			'<th>이름</th>';
		                		 str += 			'<th>생년월일</th>';
	                			 str += 			'<th>연락처</th>';
	            			 	 str += 			'<th>선택</th>';
	            			 	 str +=			'</tr>';
	                			 str +=		'</thead>';
		                		 str += 		'<tr>';
		                		 str += 			'<td colspan="4">검색 결과가 없습니다.</td>';
		            			 str +=			'</tr>';
		            			 str += 	'</table>';
			                 }else{
			                	 alert(data.resultCode);
			                 }
			                 $('#parentTb').html(str);
			                 $('#parentTb').prop('hidden',false);
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
		}else{
			alert($('input[id="'+id+'"]').attr('title')+'을 입력해주세요.');
			$('input[id="'+id+'"]').focus();
		}
	});
	$('input[name="doctorNm"]').on("keyup",function(key){
	    if(key.keyCode==13) {
	    	$('#doctorSearch').click();
	    }
	});
	$('.parentKeyEvnt').on("keyup",function(key){
	    if(key.keyCode==13) {
	    	$('#parentSearch').click();
	    }
	});
	
	$('#doctorSearch').click(function(){
		$.ajax({
	           type:"Post",
	           url:"/userInfo/doctorSearch",
	           dataType:"JSON", // 옵션이므로 JSON으로 받을게 아니면 안써도 됨
	           data: {
	        	   "userRoleFk": '1',
	        	   "doctorNm":$('input[name="doctorNm"]').val()
	           },
	           success : function(data) {
	                 // 통신이 성공적으로 이루어졌을 때 이 함수를 타게 된다.
	                 // TODO
	                var str = ''; 
		            var str = '<table>';
		             str += 	'<thead>';
              	 	 str += 	'<tr>';
              		 str += 		'<th>담당자명</th>';
         			 str += 		'<th>소속</th>';
         			 str += 		'<th>선택</th>';
         			 str +=		'</tr>';
             		 str += '</thead>';
	                if(data.resultCode == '0000'){
	               	 data.resultData.list.forEach(function(item) {
	               		 str += '<tbody>';
	               		 str += '<tr>';
	               		 str += 	'<td>'+item.userNm+'</td>';
	               		 str += 	'<td>'
               			 if(item.deptNm != null)
                		 	str +=		item.deptNm;
	               		 str +=		'</td>';
	               		 str += 	'<td>'+'<button class="form_item_file__btn01" onclick="doctorSelect('+item.userSeq+',\''+item.userId+'\',\''+item.userNm.toString()+'\',\''+item.deptNm+'\')">선택</button>'+'</td>';
	              			 str += '</tr>';
	              		 });
	               		 str += '</tbody>';
	                }else if(data.resultCode == '3001'){
	              		 str += 	'<tr>';
	              		 str += 		'<td colspan="3">검색 결과가 없습니다.</td>';
	          			 str +=		'</tr>';
	                }
	                str += '</table>'
	                $('#doctorTb').html(str);
	                $('#doctorTb').prop('hidden',false);
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
		var str = '';
			str += '<li class="user_add_list__item">';
			str += 		'<strong>';
			str +=		$('#newDoctorNm').val();
			str += 		'</strong>';
			str +=		'<span class="user_add_list__sub">'+$('#newDoctorDept').val()+'</span>';
			str += 		'<a href="javascript:doctorRemove()" class="icon_form_remove" ><span class="hidden">삭제</span></a>'
			str += '</li>';
// 		str = str != '' ? head+str +'</section>' : str;
		$('#addDoctor').html(str)
	}
	var EmailCheckTf;
	
	function validation(){
		var role =  $('input[name="userRoleFk"]').val();
		switch(role){
			case '1':	// 사용자
				validation = ['userNm', 'telNumEnc', 'email', 'gender', 'bloodType', 'height', 'weight'];
				break;
			case '2':	// 보호자
				validation = ['userNm', 'telNumEnc', 'email'];
				break;
			default :	// 담당자, 관리자
				validation = ['userNm', 'telNumEnc', 'email', 'deptNm'];
				break;
		}
		var obj = validationCheck(validation);
		var validationTf = obj.tf;
		var tag = obj.tag;
		if(validationTf){
			$('#uptUserForm').submit();	
		}else{
			$('#'+tag).focus();
			alert('필수로 작성해야할 내용을 입력해주세요.');
			return;
		}
	}
	
	function validationCheck(validation){
		var obj = {
				tf: true,
// 				tag: ''
		}
		validation.forEach(function(item, idx){
			if(obj.tf){
				if($('#'+item).attr('type') == 'checkbox'){
					if(!$('#'+item).prop('checked')){
						obj.tag = item;
						obj.tf = false;
					}
				}else{
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
	
	$('#doctorSave').click(function(){
		var tf = confirm("담당자 선택을 완료하시겠습니까?");
		if(tf == true){
			$('#doctorSeq').val($('#newDoctorSeq').val());
			$('#doctorId').val($('#newDoctorId').val());
			var html ='';
			if($('#doctorSeq').val() != null && $('#doctorSeq').val() != '' ){
				html +=	'<li class="user_add_list__item">';

				html +=	'<strong id="doctorNm">';
				html +=	$('#newDoctorNm').val();
				html +=	'</strong>';
				html +=	'<span class="user_add_list__sub" id="doctorDept">';
				html +=	$('#newDoctorDept').val();
				html +=	'</span>';

				html +=	'</li>';
			}
			$('#addDoctorUl').html(html);
			$('#doctorNm').val($('#newDoctorNm').val());
			$('#doctorDept').val($('#newDoctorDept').val());

			$('#doctor_cancle').click();
		}else{
			return;
		}
	});
	
	function parentSelect(seq, userId, userNm, telNumEnc){
		var parentsArr = $('#befGuardian').val() != '' && $('#befGuardian').val() != null && $('#befGuardian').val() != 'null' ? JSON.parse($('#befGuardian').val()) : [];
		var arr = $('input[name="guardian"]').val() != '' && $('input[name="guardian"]').val() != null ? JSON.parse($('input[name="guardian"]').val()): [];
		var oldtf = true;
		var newtf = true;
		var arr = $('#regGuardian').val() != '' && $('#regGuardian').val() != null ? JSON.parse($('#regGuardian').val()): [];

		if(parentsArr.length>0){
			parentsArr.forEach(function(item){
				if(item.guardianSeq == seq)
					oldtf = false;
			});
		}

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
				alert('이미 추가된 목록에 있는 보호자입니다.');
			}
		}else{
			alert('이미 보호자로 지정되어있습니다.');
		}
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
				str += '<li class="user_add_list__item">'
				str += item.userNm;
				str += '</li>';
			});
			$('#parentNm').html(str);
			$('input[name="guardian"]').val(JSON.stringify(parentsArr));
			$('input[name="parentNm"]').val('');
			$('#parentBirthEnc').val('');
			$('#parentTelNumEnc').val('');
			$('#parentTb').html('');
			$('#parentTb').prop('hidden',true);
			$('#guardian_cancle_real').click();
		}else{
			return;
		}
	});
	
	/* 보호자 검색 팝업 close 이벤트 */
	$('#parModal').on('modal:close', function (e) {
		$('#regGuardian,#parentNm,#parentTelNumEnc,#parentBirthEnc').val('');
		$('#parentTb').html('');
	});
	
	/* 담당자 검색 팝업 close 이벤트 */
	$('#docModal').on('modal:close', function (e) {
		$('#doctorNm').val('');
		$('#doctorTb').html('');
	});
	
	function parentList(){
		var parentsArr = $('#befGuardian').val() != ''&& $('#befGuardian').val() != null && $('#befGuardian').val() != 'null'? JSON.parse($('#befGuardian').val()):[];
		var arr = $('#regGuardian').val() != ''&& $('#regGuardian').val() != null ? JSON.parse($('#regGuardian').val()):[];
		
		var str = '';
		if(parentsArr.length>0){
			parentsArr.forEach(function(item, idx){
				str += '<li class="user_add_list__item">';
				str += 		'<span>';
				str +=		item.userNm;
				str += 		'</span>';
				str += 		'<a href="javascript:remove( \'bef\','+idx+')" class="icon_form_remove"><span class="hidden">삭제</span></a>'
				str += '</li>';
			});
		}
		if(arr.length>0){
			arr.forEach(function(item, idx){
				str += '<li class="user_add_list__item">';
				str += 		'<span>';
				str +=		item.userNm;
				str += 		'</span>';
				str += 		'<a href="javascript:remove( \'reg\','+idx+')" class="icon_form_remove"><span class="hidden">삭제</span></a>'
				str += '</li>';
			});
		}
		$('#adduser').html(str);
	}
	
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
	
	function submit(){
		var tf = confirm("저장하시겠습니까?");
		if(tf == true){
			if($('#email').val() != null){
				emailCheck();
				if(EmailCheckTf)
					validation();
				else
					alert('이메일을 올바르게 작성해주세요.');
					$('#email').focus();
			}else{
				alert('이메일을 작성해주세요.');
			}
		}else{
			return;
		}
	}
	$('.form_number').keyup(function(){
		$(this).val($(this).val().replaceAll(/[^0-9]/g,''));
	});

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

	$('#btn_parModal_open').click(function(){
		if($('#guardian').val() != null && $('#guardian').val() != '')
// 		var parentsArr = JSON.parse($('#guardian').val());
		$('#befGuardian').val($('#guardian').val());
		parentList();
	});
	
	$('#guardian_cancle').click(function(){
		$('input[name="parentNm"]').val('');
		$('#parentBirthEnc').val('');
		$('#parentTelNumEnc').val('');
		$('#parentTb').html('');
		$('#parentTb').prop('hidden',true);
		$('#regGuardian').val('');
		$('#guardian_cancle_real').click();
	});
	
	
	$('#doctor_cancle').click(function(){
		$('input[name="doctorNm"]').val('');
		$('#doctorTb').html('');
		$('#doctorTb').prop('hidden',true);
		$('#doctor_cancle_real').click();
	});
	
	$('#btn_docModal_open').click(function(){
		if($('#doctorSeq').val() != '' && $('#doctorSeq').val() != null ){
			$('#newDoctorSeq').val($('#doctorSeq').val());
			$('#newDoctorId').val($('#doctorId').val());
			$('#newDoctorNm').val($('#doctorNm').val());
			$('#newDoctorDept').val($('#doctorDept').val());
			$('#doctor_cancle').click();
			doctorList();
		}
	});
	
	/* 이메일 유효성검사 */
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
		
		/* 공백제거 */
		$('.trim').keyup(function(){
			$(this).val($(this).val().replace(/\s/g,''));
		});
		/* 숫자타입 입력제한 */
	 	$('.form_number').keyup(function(){
	 		$(this).val($(this).val().replaceAll(/[^0-9]/g,''));
	 	});
	</script>
<jsp:include page='/WEB-INF/jsp/layout/mainEnd.jsp' />