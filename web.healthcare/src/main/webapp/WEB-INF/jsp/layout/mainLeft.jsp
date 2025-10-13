<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/_dtd.jsp" %>
<c:if test="${!empty acToken }">
<section class="lnb_wrap">
		<h1 class="lnb_title">MENU</h1>
		
		<nav class="lnb">
		    <ul class="lnb_list" id="lnbMenu">
		    	<li class="lnb_list__item" id="leftMenu12100">
		 			<a class="lnb_list__link lnb_list__link<c:if test="${fn:split(uri,'/')[0] eq 'dashboard' or fn:split(uri,'/')[0] eq null or fn:split(uri,'/')[0] eq ''}">--on</c:if>" href="javascript:post('/dashboard')">대시보드</a>
			 	</li>
			</ul>
		    <ul class="lnb_list" id="lnbMenu">
		    	<li class="lnb_list__item" id="leftMenu12100">
		 			<a class="lnb_list__link lnb_list__link<c:if test="${fn:split(uri,'/')[0] eq 'health'}">--on</c:if>" href="javascript:userHealthInfo('/health/heartrate')">건강정보</a>
		 			<c:if test="${fn:split(uri,'/')[0] eq 'health'}">
				 	<ul class="lnb_list_2dp">
			 			
			 			<li class="lnb_list_2dp_item" id="leftMenu12103">
				 			<a class="lnb_list_2dp__link<c:if test="${fn:split(uri,'/')[1] eq 'heartrate' }">--on</c:if>" href="javascript:userHealthInfo('/health/heartrate')">
				 				심박수
				 			</a>
						</li>
			 			<li class="lnb_list_2dp_item" id="leftMenu12103">
				 			<a class="lnb_list_2dp__link<c:if test="${fn:split(uri,'/')[1] eq 'spo2' }">--on</c:if>" href="javascript:userHealthInfo('/health/spo2')">
				 				산소포화도
				 			</a>
						</li>
						
						<li class="lnb_list_2dp_item" id="leftMenu12103">
				 			<a class="lnb_list_2dp__link<c:if test="${fn:split(uri,'/')[1] eq 'step' }">--on</c:if>" href="javascript:userHealthInfo('/health/step')">
				 				운동량
				 			</a>
						</li>
						<li class="lnb_list_2dp_item" id="leftMenu12103">
				 			<a class="lnb_list_2dp__link<c:if test="${fn:split(uri,'/')[1] eq 'stress' }">--on</c:if>" href="javascript:userHealthInfo('/health/stress')">
				 				스트레스
				 			</a>
						</li>
						<li class="lnb_list_2dp_item" id="leftMenu12103">
				 			<a class="lnb_list_2dp__link<c:if test="${fn:split(uri,'/')[1] eq 'sleep' }">--on</c:if>" href="javascript:post('/health/sleep')">
				 				수면
				 			</a>
						</li>
						<li class="lnb_list_2dp_item" id="leftMenu12103">
				 			<a class="lnb_list_2dp__link<c:if test="${fn:split(uri,'/')[1] eq 'bloodpress' }">--on</c:if>" href="javascript:userHealthInfo('/health/bloodpress')">
				 				혈압
				 			</a>
						</li>
						<li class="lnb_list_2dp_item" id="leftMenu12103">
				 			<a class="lnb_list_2dp__link<c:if test="${fn:split(uri,'/')[1] eq 'repiratory' }">--on</c:if>" href="javascript:userHealthInfo('/health/repiratory')">
				 				호흡수
				 			</a>
						</li>
						<li class="lnb_list_2dp_item" id="leftMenu12102">
				 			<a class="lnb_list_2dp__link<c:if test="${fn:split(uri,'/')[1] eq 'temperature' }">--on</c:if>" href="javascript:userHealthInfo('/health/temperature')">
				 				체온
				 			</a>
			 			</li>
				 	</ul>
				 	</c:if>
			 	</li>
			</ul>
			<c:if test="${userRoleFk eq '2' or userRoleFk eq '3' or userRoleFk eq '4'}">
		    <ul class="lnb_list" id="lnbMenu">
		    	<li class="lnb_list__item" id="leftMenu12100">
		 			<a class="lnb_list__link lnb_list__link<c:if test="${fn:split(uri,'/')[0] eq 'userInfo' and fn:split(uri,'/')[1] ne 'mypage' and fn:split(uri,'/')[1] ne 'uptUserInfo'}">--on</c:if>" href="javascript:post('/userInfo/list_user')">사용자 목록</a>
					<c:if test="${fn:split(uri,'/')[0] eq 'userInfo' and fn:split(uri,'/')[1] ne 'mypage' and fn:split(uri,'/')[1] ne 'uptUserInfo'}">
						<c:if test="${userRoleFk eq '4'}">
							<ul class="lnb_list_2dp">
								<li class="lnb_list_2dp_item" id="leftMenu12101">
									<a class="lnb_list_2dp__link<c:if test="${fn:split(uri,'/')[1] eq 'list_user'}">--on</c:if>" href="javascript:post('/userInfo/list_user')">
										사용자 목록
									</a>
								</li>
								<li class="lnb_list_2dp_item" id="leftMenu12102">
									<a class="lnb_list_2dp__link<c:if test="${fn:split(uri,'/')[1] eq 'list_par'}">--on</c:if>" href="javascript:post('/userInfo/list_par')">
										보호자 목록
									</a>
								</li>
								<li class="lnb_list_2dp_item" id="leftMenu12103">
									<a class="lnb_list_2dp__link<c:if test="${fn:split(uri,'/')[1] eq 'list_doc'}">--on</c:if>" href="javascript:post('/userInfo/list_doc')">
										담당자 목록
									</a>
								</li>
							
							</ul>
						</c:if>
					</c:if>
			 	</li>
			</ul>
			</c:if>
	</nav>
</section>
</c:if>
<script type="text/javascript">
	function post(url){
		let f = document.createElement('form');
	    f.setAttribute('method', 'post');
	    f.setAttribute('action', url);
	    document.body.appendChild(f);
	    f.submit();
	}
</script>