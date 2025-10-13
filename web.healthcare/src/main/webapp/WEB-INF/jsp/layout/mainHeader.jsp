<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/_dtd.jsp" %>
</head>
<body>
  <div class="layout_wrap">
  	<c:if test="${!empty acToken }">
		<div class="header_layout">
			<div class="header_wrap inner">
				<a href="javascript:post('/dashboard')" class="main_logo">
		          <img src="/images/layout/logo.png" class="main_logo_img" alt="헬스케어">
		          <span>MZ Healthcare</span>
		        </a>
				<div class="login_info">
				<c:if test="${empty acToken }">
					<a href="<c:url value='/user/signin'/>" class="login_info__link">Login</a>
				</c:if>
				<c:if test="${!empty acToken }">
					<span class="login_info__txt">
						<strong class="login_info__user"><c:out value="${sessUserNm}" /></strong> 님, 안녕하세요.
					</span>
					<span class="login_info_bar"></span>
					<a href="<c:url value='/userInfo/mypage'/>" class="mypage_link">마이페이지</a>
					<a href="<c:url value='/user/logout'/>" class="login_info__link">Logout</a>
				</c:if>
				</div>
			</div>
		</div>
</c:if>