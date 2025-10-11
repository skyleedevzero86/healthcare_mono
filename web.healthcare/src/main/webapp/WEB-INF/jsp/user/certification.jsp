<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page='/WEB-INF/jsp/layout/head.jsp' flush="true">
	<jsp:param name="pageType" value="eb-main" />
	<jsp:param name="bodyAttr" value="onload=''" />
	<jsp:param name="bodyStyle" value="eb-main" />
</jsp:include>
<html>
<head>
<meta charset="UTF-8">
</head>
<body>
	<div class="layout_wrap">
	    <div class="container">
	      <div class="login_layout">
	      	<form id="command" name="certificationForm" action="/mypage" method="post">
		        <section class="login_box">
		          <div class="login_box__header">헬스케어</div>
		          <div class="login_box__body">
		            <section class="login_box__form">
		              <h1 class="login_box__title">본인확인</h1>
		              <input type="hidden" id="userId" name="userId" class="form_item_text" placeholder="아이디" value="">
		              <input type="password" id="userPwEnc" name="userPwEnc" class="form_item_text" placeholder="비밀번호" value="">
		              <button type="button" class="login_box__form_btn" id="btn_login" onclick="duplicatelogin()" >확인</button>
		              	<div class="login_ch">
							<div class="slink">
								<a href="/user/signup" class="join_btn">회원가입</a>
								<!-- <a href="javascript:searchIdPW();" class="idfind_btn">아이디 찾기</a>
								<a href="javascript:searchIdPW();" class="pwfind_btn">비밀번호찾기</a> -->
							</div>
						</div>
		            </section>
		          </div>
		        </section>
	        </form>
	      </div>
	    </div>
	</div>
</body>
</html>