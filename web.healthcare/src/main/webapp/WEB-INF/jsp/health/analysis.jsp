<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
      pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/_dtd.jsp" %>
<html lang="en">
<link rel="stylesheet" href="../css/mzhc/healthgraph.css" type="text/css">

  <title>분석</title>
</head>
<body>
  <div class="container sub_container">
    <!-- header -->
    <jsp:include page='/WEB-INF/jsp/include/header.jsp' />
    <!-- header -->

    <!-- content 영역 - 서브 페이지 : main_sub_content_wrap -->
    <section class="main_content_wrap main_sub_content_wrap">
      <div class="main_content main_sub_content _inner_wrap">

        <main class="main_wrap sub_wrap">

          <div class="flex_row">
            <section class="flex_item radius_box">
              <div class="radius_body">

                <div class="flex_row">
                  <h1 class="flex_item"><c:out value="${userNm }" /> (<c:out value="${userId }" />)</h1>

                  <ul class="user_info flex_item__col2 mt10">
                    <li class="user_info__item">
                      <strong class="user_info__title">성별</strong>
                      <div class="user_info__val">
                        <strong>
                             <c:if test="${gender eq 'M'}">남</c:if>
                            <c:if test="${gender eq 'F'}">여</c:if>
                        </strong>
                      </div>
                    </li>
                    <li class="user_info__item">
                      <strong class="user_info__title">혈액형</strong>
                      <div class="user_info__val">
                        <strong>
                            <c:if test="${bloodType ne null and bloodType ne 'null'}"><c:out value="${bloodType }"/></c:if>
                        </strong>
                      </div>
                    </li>
                    <li class="user_info__item">
                      <strong class="user_info__title">키</strong>
                      <div class="user_info__val">
                        <strong>
                            <c:if test="${height ne '' and height ne null  and height ne 'null' }">
                                <c:out value="${height }" />
                            </c:if>
                        </strong>
                        <span>cm</span>
                      </div>
                    </li>
                    <li class="user_info__item">
                      <strong class="user_info__title">몸무게</strong>
                      <div class="user_info__val">
                        <strong>
                          <c:if test="${weight ne '' and weight ne null and weight ne 'null'}">
                             <c:out value="${weight }" />
                          </c:if>
                        </strong>
                        <span>kg</span>
                      </div>
                    </li>
                  </ul>
                </div>
              </div>
            </section>
          </div>

          <div class="flex_row">
            <!-- AI 건강 주치의 -->
            <section class="flex_item radius_box">
              <div class="radius_header">
                <h2 class="radius_header__title">상세 정보</h2>
              </div>
              <div class="radius_body">
                <!-- 날짜 선택 -->
                <section class="chart_search">
                  <div class="form_inline_group">
                    <button type="button" class="form_button searchType" value='M'>월별</button>
                    <button type="button" class="form_button searchType" value='W'> 주별</button>
                    <input type="text" name="" id="datepicker" class="form_item_date ml5">
                  </div>
                </section>

                <!-- 탭 -->
                <ul class="user_state _row mb10">
                  <li class="user_state__item heartrate" onclick=graphBiodata('heartrate')>
                      <div class="user_state__title _state01">
                        <strong>심박수</strong>
                      </div>
                  </li>
                  <li class="user_state__item bloodpress_max" onclick=graphBiodata('bloodpress_max')>
                      <div class="user_state__title _state02">
                        <strong>혈압</strong>
                      </div>
                  </li>
                  <li class="user_state__item temperature" onclick=graphBiodata('temperature')>
                      <div class="user_state__title _state03">
                        <strong>체온</strong>
                      </div>
                  </li>
                  <li class="user_state__item stress" onclick=graphBiodata('stress')>
                      <div class="user_state__title _state04">
                        <strong>스트레스</strong>
                      </div>
                  </li>
                  <li class="user_state__item step" onclick=graphBiodata('step')>
                      <div class="user_state__title _state05">
                        <strong>걷기</strong>
                      </div>
                  </li>
                  <li class="user_state__item sleep" onclick=graphSleepdata()>
                      <div class="user_state__title _state06">
                        <strong>수면</strong>
                      </div>
                  </li>
                </ul>
                <!-- //탭 -->

                     <div id="chat" style="height: 300px">
                         <canvas id="myChart"></canvas>
                    </div>

              </div>
            </section>
          </div>

          <div class="flex_row">
            <!-- 건강 점수 -->
            <section class="flex_item__col1 radius_box _relative">
              <div class="radius_header">
                <h2 class="radius_header__title">나의 건강 점수</h2>
              </div>
              <div class="radius_body">
                 <div class="donut-chart">
                      <canvas id="myDonutChart" ></canvas>
                      <strong class="chart_score">00</strong>
                  </div>
              </div>
            </section>

            <!-- 건강 그래프 -->
            <section class="flex_item__col2 radius_box">
              <div class="radius_header">
                <h2 class="radius_header__title">이번주 건강 그래프</h2>
              </div>
              <div class="radius_body">

                <div class="chat" style="height: 300px;">
                      <canvas id="myhealthChart"></canvas>
                </div>

              </div>
            </section>
          </div>

        </main>
      </div>
    </section>

    <!-- footer -->
        <jsp:include page="/WEB-INF/jsp/include/footer.jsp" />
    <!-- footer -->
  </div>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-circle-progress/1.2.2/circle-progress.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.4.0/Chart.min.js"></script>
<!--datePicker-->
<script src="./jquery-3.1.1.min.js"></script> <!-- 값 제어를 위해 jquery -->
<link href="/datepicker/air-datepicker/dist/css/datepicker.min.css" rel="stylesheet" type="text/css" media="all">
<!-- Air datepicker css -->
<script src="/datepicker/air-datepicker/dist/js/datepicker.js"></script> <!-- Air datepicker js -->
<script src="/datepicker/air-datepicker/dist/js/i18n/datepicker.ko.js"></script> <!-- 달력 한글 추가를 위해 커스텀 -->

<!--custom js-->
<script src="/js/biodata/realtimeBiodata.js" type="text/javascript"></script>
<script src="/js/biodata/graphBiodata.js" type="text/javascript"></script>
<script src="/js/biodata/graphSleepdata.js" type="text/javascript"></script>
<script src="/js/biodata/healthdetailgraph.js" type="text/javascript"></script>
<script src="/js/chart/healthgrade.js" type="text/javascript"></script>
<script src="/js/biodata/healthscore.js" type="text/javascript"></script>
<script>
    var searchUserId = '${UserinfoId}' ;

    var searchType = "D"; //default로 일일 데이터
    var searchWrd = "heartrate"; //default로 heartrate값
    var curTime = "";
    var searchUserAge = '${ageAvg}';

    $(document).ready(function(){
        var result = "";
        //오늘날짜
        curDate = new Date();
        curDay = curDate.getDate();
        curMon = (curDate.getMonth() + 1) ;

        if(curDate.getDate() < 10) {
           curDay = "0" + curDate.getDate(); //2023-12-05
        }

        if((curDate.getMonth() + 1) < 10) {
            curMon = "0" + curMon;
        }

        curTime = curDate.getFullYear() + "-" + curMon + "-" + curDay; //2023-12-10

        $('#datepicker').val(curTime);

        if(${!empty acToken }){
           graphBiodata(searchWrd);
           drawScoreAjax();
        }
        else {
        }

        $('#header_user_analysis').addClass('_active');
    });

    $(".searchType").on('click',function(e){

        searchType = e.target.value; //D,W,M 일인지 주인지 달인지 들어옴


        if(searchWrd=='sleep') {
            graphSleepdata();
        }
        else {
          graphBiodata(searchWrd); //호출
        }
    });

    $("#datepicker").datepicker({
       language: 'ko',
       autoClose: true,
       onSelect: function (formattedDate, date, inst) {
         // 사용자가 날짜를 선택할 때마다 호출되는 이벤트 핸들러
         // 선택한 날짜를 내부 변수에 저장
        curTime = formattedDate;
            if(searchWrd=='sleep') {
                graphSleepdata();
            }
            else {
                searchType = "D";
                graphBiodata(searchWrd);
            }
       }
   });

</script>
</body>
</html>