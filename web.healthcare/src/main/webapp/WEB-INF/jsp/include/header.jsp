<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/_dtd.jsp" %>
<!-- skip nav -->
    <div class="skip_nav">
      <a href="#skipnav_target">본문 영역 바로가기</a>
    </div>
    <!-- skip nav -->

    <!-- header_wrap -->
    <header class="header_wrap">
      <div class="header">
        <div class="main_logo_wrap">
          <h1><a href="/userInfo/userBoardInfo?userinfoId=${UserinfoId}" class="main_logo"><img src="../modules/images/common/main_logo.png" alt="1"></a></h1>
        </div>

        <nav class="gnb_wrap">
          <ul class="gnb">
            <!-- gnb_link _active 클래스 넣으면 색 바뀜 -->
              <c:if test="${sessuserRoleFk ne '1'}">

               <li class="gnb_item">
                 <a href="<c:url value='/userInfo/manage_userList'/>" id="header_user_list" class="gnb_link">환자</a>
               </li>
              </c:if>
              <c:if test="${sessuserRoleFk eq '1'}">
                <li class="gnb_item">
                <a href="/userInfo/userBoardInfo?userinfoId=${UserinfoId}" id="header_user_detail" class="gnb_link ">상태</a>
                </li>
                <li class="gnb_item">
                  <a href="/userInfo/analysis?userinfoId=${UserinfoId}" id="header_user_analysis" class="gnb_link">분석</a>
                </li>
                <li class="gnb_item">
                  <a href="/userInfo/community?userinfoId=${UserinfoId}" class="gnb_link">커뮤니티</a>
                </li>
              </c:if>
          </ul>
        </nav>

        <div class="header_info">
          <div class="header_info__txt">
            <strong><c:out value="${sessUserNm}" /></strong>
            <span>님, 안녕하세요</span>
          <a href="javascript:void(0);" class="header_info__link">마이페이지</a>
          <a href="/user/logout" class="header_info__link">로그아웃</a>
        </div>
      </div>
    </header>
    <!-- header_wrap -->