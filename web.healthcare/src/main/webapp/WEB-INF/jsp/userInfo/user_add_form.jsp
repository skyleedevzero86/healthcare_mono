<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
      pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/_dtd.jsp" %>

  <title>환자 정보</title>
</head>
<body>
  <div class="container sub_container">
    <!-- header -->
    <jsp:include page='/WEB-INF/jsp/include/header.jsp' />
    <!-- header -->

    <!-- content 영역 - 서브 페이지 : main_sub_content_wrap -->
    <section class="main_content_wrap main_sub_content_wrap">
      <div class="main_content main_sub_content _inner_wrap">

        <main class="main_wrap sub_wrap"  id="skipnav_target">
          <h2 class="content_title">환자 정보</h2>

          <div class="tab__panels">
            <div class="tab__panel target_tab01 _open" role="tabpanel">
              <div class="tab__panel__item">
                <div class="board_wrap">
                  <section class="board_hd_wrap">
                    <p class="board_all_num">전체
                      <strong>${paginationInfo.totalRecordCount}</strong>
                     건</p>
                    <div class="board_search_wrap">
                      <div class="board_select_wrap">
                        <select name="" id="" class="board_select">
                          <option value="" selected="selected">전체</option>
                          <option value="">제목</option>
                          <option value="">내용</option>
                          <option value="">작성자</option>
                        </select>
                      </div>
                      <div class="board_input_wrap">
                        <input type="text" id="" class="board_search_input" placeholder="검색어를 입력하세요.">
                        <button type="button" class="board_btn btn_search"><span class="blind">검색</span></button>
                      </div>
                    </div>
                  </section>

                  <section class="board_list_wrap">
                    <ul class="board_list">
                      <li class="board_list__box borad_list_hd_wrap">
                        <div class="board_list__title">
                          <div class="board_con_hd flex-auto">번호</div>
                          <div class="board_con_hd flex-auto">소속</div>
                          <div class="board_con_hd flex-auto">이름</div>
                          <div class="board_con_hd flex-auto">성별</div>
                          <div class="board_con_hd flex-auto">나이</div>
                          <div class="board_con_hd flex-auto">생년월일</div>
                        </div>
                      </li>
                      <!-- 게시판 반복 영역 -->
                       <c:if test="${!empty list}">
                           <c:forEach items="${userRolelist}" var="item">
                              <li class="board_list__box">
                                <div class="board_list__item">
                                  <div class="board_con flex-auto"><c:out value = "${item.userSeq}"/></div>
                                  <div class="board_con flex-auto"><c:out value = "${item.deptNm}"/></div>
                                  <div class="board_con flex-auto">
                                    <a href="/userInfo/userBoardInfo/${list.userId}" class="board_list__link"><c:out value = "${item.userNm}"/></a>
                                  </div>
                                  <div class="board_con flex-auto"><c:out value = "${item.gender}"/></div>
                                  <div class="board_con flex-auto"><c:out value = "${item.userAge}"/></div>
                                  <div class="board_con flex-auto"><c:out value = "${item.birthEnc}"/></div>
                                </div>
                              </li>
                          </c:forEach>
                       </c:if>

                      <!-- //게시판 반복 영역 -->
                       <c:if test="${empty list}">
                          <li class="board_list__box">
                            <div class="board_list__item">
                              <div class="board_con board_con_nondata">환자 정보가 없습니다.</div>
                            </div>
                          </li>
                      </c:if>
                    </ul>
                  </section>

                  <section class="board_ft_wrap">
                    <div class="board_ft_left_wrap">
                      <!-- layout 용도 -->
                    </div>
                    <!-- 페이지네이션 -->
                    <div class="pagination_wrap" id="">
                      <ul class="pagination">

                        <sleekydz86:pagination paginationInfo="${paginationInfo}" jsFunction="fn_paging"/>
                      </ul>

                    </div>

                    <div class="board_ft_right_wrap">
                      <button type="button" class="btn_add" onclick="location.href='/addform'">추가</button>
                    </div>
                  </section>
                </div>
              </div>
            </div>

          </div>
        </main>
      </div>
    </section>

    <!-- footer -->
    <jsp:include page="/WEB-INF/jsp/include/footer.jsp" />
    <!-- footer -->
  </div>
<script>
function fn_paging(index) {
    $('#page_pageIndex').val(index);
    $('#paginationForm').submit();
}

</script>
</body>
</html>