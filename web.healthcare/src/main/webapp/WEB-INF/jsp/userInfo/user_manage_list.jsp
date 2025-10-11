<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
      pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/_dtd.jsp" %>
<html lang="en">
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
                          <div class="board_con_hd flex-auto">분석</div>
                        </div>
                      </li>
                      <!-- 게시판 반복 영역 -->
                       <c:if test="${!empty list}">
                           <c:forEach items="${list}" var="item">
                              <li class="board_list__box">
                                <div class="board_list__item">
                                  <div class="board_con flex-auto"><c:out value = "${item.userSeq}"/></div>
                                  <div class="board_con flex-auto"><c:out value = "${item.deptNm}"/></div>
                                  <div class="board_con flex-auto">
                                    <a href="/userInfo/userBoardInfo?userinfoId=${item.userId}" class="board_list__link"><c:out value = "${item.userNm}"/></a>
                                  </div>
                                  <div class="board_con flex-auto">
                                   <c:if test="${item.gender eq 'M'}">남</c:if>
                                   <c:if test="${item.gender eq 'F'}">여</c:if>
                                  </div>
                                  <div class="board_con flex-auto"><c:out value = "${item.userAge}"/></div>
                                  <div class="board_con flex-auto"><c:out value = "${item.birthEnc}"/></div>
                                  <div class="board_con flex-auto">
                                   <button type="button" class=" form_button" onclick="window.location.href = '/userInfo/analysis?userinfoId=${item.userId}'">분석</button>
                                  </div>
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
                         <li class="page_item"><a href="#" class="page_link">1</a></li>
                      </ul>
                    </div>
                    <!-- 페이지네이션 -->

                    <div class="board_ft_right_wrap">
                      <button type="button" class="btn_add js-pop-open" data-target=".pop01">추가</button>
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

  <section class="popup_dim pop01" style="display: none;">
    <section class="popup_box" style="width:800px">
      <div class="pop_header">
        <h1 class="pop_header__title">환자 선택</h1>
        <a href="" class="pop_header__close"><span hidden>닫기</span></a>
      </div>
      <div class="popup_body">

        <section class="list_search mb20">
          <div class="list_search__form">
            <div class="board_search_wrap">
              <div class="board_select_wrap">
                <select name="requestUser" id="" class="board_select">
                  <option value="allsearch" selected="selected">전체</option>
                  <option value="deptsearch">소속</option>
                  <option value="namesearch">이름</option>
                </select>
              </div>
              <div class="board_input_wrap">
                <input type="text" id="searchKeyword" class="board_search_input" name="searchKeyword" placeholder="검색어를 입력하세요.">
                <button type="button" class="board_btn btn_search" id="searchUser"><span class="blind">검색</span></button>
              </div>
            </div>
          </div>
        </section>

        <div class="table_box _padding_none">
          <table>
            <colgroup>
              <col style="width:auto;">
              <col style="width:auto;">
              <col style="width:auto;">
              <col style="width:auto;">
              <col style="width:auto;">
              <col style="width:auto;">
              <col style="width:auto;">
            </colgroup>
            <thead>
              <tr>
                <th>번호</th>
                <th>소속</th>
                <th>이름</th>
                <th>성별</th>
                <th>나이</th>
                <th>생년월일</th>
                <th>선택</th>
              </tr>
            </thead>
            <tbody id="listTable">

            </tbody>
          </table>
        </div>

      </div>
      <div class="button_right_group">
        <a href="#none" class="btn_cancel js-pop-close" data-target=".popup_dim">취소</a>
        <a href="#none" id="requestInfo" class="btn_save" onclick="requestUser()">등록</a>
      </div>
    </section>
  </section>

<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.0.0/jquery.min.js"></script>
<link rel="stylesheet" href="../../../css/vender/jquery.mCustomScrollbar.css">

<script src="../../../modules/js/vendor/jquery.mCustomScrollbar.js"></script>
<script>
//    function fn_paging(index) {
//        $('#page_pageIndex').val(index);
//        $('#paginationForm').submit();
//    }

    function searchUserList() {
        alert("환자가 1명입니다.");
        //$('#searchuserlist').submit();
    }

    $(document).ready(function(){
       // $('#header_user_list').addClass('_active');
    });

    function requestUser(){
        alert("등록요청이 완료되었습니다.");
        $('.popup_dim').hide();
        location.href = 'redirect:/userInfo/manage_userList';
    }

    $('#searchUser').click(function(){
        var keyword= $("#searchKeyword").val();
        var category = $('select[name="requestUser"]').val();

        $.ajax({
          type:"Post",
          url:"/userInfo/searchUser",
          dataType:"JSON", // 옵션이므로 JSON으로 받을게 아니면 안써도 됨
          data: {
              "keyword":keyword,
              "category" : category,
              "searchtype"  : "allUser"
          },
          success : function(data) {
          // 통신이 성공적으로 이루어졌을 때 이 함수를 타게 된다.
          // TODO
              var str = '';
              data.forEach(function(item, index){
                str += '<tr>';
                str += '<td>'+ (index + 1) + '</td>';
                str += '<td>'+item.deptNm+'</td>';
                str += '<td>'+item.userNm+'</td>';
                if(item.gender == 'M') {
                  str += '<td>남</td>';
                }
                else if(item.gender == 'F') {
                  str += '<td>여</td>';
                }
                str += '<td>'+item.age+'</td>';
                str += '<td>'+item.birthEnc+'</td>';
                str += '<td>';
                str += '  <input type="checkbox" name="requestSeq" value="'+item.userSeq+'"/>';
                str += '</td>';
                str += 'tr>';
              });
              $('#listTable').empty();
              $('#listTable').append(str);
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
</script>
</body>
</html>