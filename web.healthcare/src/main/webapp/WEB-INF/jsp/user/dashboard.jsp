<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
      pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/_dtd.jsp" %>
<html lang="en">
<head>
  <title>환자 상태</title>
</head>
<body>
  <div class="container sub_container">
    <jsp:include page='/WEB-INF/jsp/include/header.jsp' />

    <section class="main_content_wrap main_sub_content_wrap">
      <div class="main_content main_sub_content _inner_wrap">

        <main class="main_wrap sub_wrap">

          <div class="flex_row">
            <section class="flex_item radius_box">
              <div class="radius_header">
                <h2 class="radius_header__title">사용자 정보</h2>
              </div>
              <div class="radius_body">

                <div class="user_profile">
                  <div class="user_profile__img">
                      <c:if test="${userProfile ne null and userProfile ne 'null'}">
                          <img src="/images/userProfile/${userProfile}" alt = "" style= "width:10vw;height:19vh; border-radius: 50%;">
                      </c:if>
                      <c:if test="${userProfile eq null or userProfile eq 'null' }">
                          <img src="/modules/images/common/img_user.png" alt="">
                      </c:if>
                  </div>
                  <div class="user_profile__name">
                    <strong><c:out value="${userNm }" /></strong>
                    <span>(<c:out value="${userId }" />)</span>
                  </div>
                </div>

                <ul class="user_info">
                  <li class="user_info__item">
                    <strong<c:out value="${userNm }" /> class="user_info__title">성별</strong>
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
            </section>
            <section class="flex_item radius_box">
              <div class="radius_header">
                <h2 class="radius_header__title">실시간 상태</h2>
              </div>
              <div class="radius_body">

                <ul class="user_state">
                  <li class="user_state__item">
                    <div class="user_state__title _state01">
                      <strong>심박수</strong>
                      <input type="hidden" value = "${userHealthavg.avgHeartrateMax}" name = "avgHeartrateMax"/>
                      <input type="hidden" value = "${userHealthavg.avgHeartrateMin}" name = "avgHeartrateMin"/>
                      <span>정상 심박수(<c:out value="${userHealthavg.avgHeartrateMin }"/>~<c:out value="${userHealthavg.avgHeartrateMax }"/>)</span>
                    </div>
                    <div class="user_state__num">
                      <strong id= "heartRate_realtime">0</strong>
                    </div>
                  </li>
                  <li class="user_state__item">
                    <div class="user_state__title _state02">
                      <strong>혈압</strong>
                      <input type="hidden" value = "${userHealthavg.avgBloodpressMax}" name = "avgBloodpressMax"/>
                      <input type="hidden" value = "${userHealthavg.avgBloodpressMin}" name = "avgBloodpressMin"/>
                      <span>정상 혈압(<c:out value="${userHealthavg.avgBloodpressMin }"/>~<c:out value="${userHealthavg.avgBloodpressMax }"/>)</span>
                    </div>
                    <div class="user_state__num">
                      <strong id= "bloodAvg_realtime">0</strong>
                    </div>
                  </li>
                  <li class="user_state__item">
                    <div class="user_state__title _state03">
                      <strong>체온</strong>
                      <input type="hidden" value = "${userHealthavg.avgTemperatureMax}" name = "avgTemperatureMax"/>
                      <input type="hidden" value = "${userHealthavg.avgTemperatureMin}" name = "avgTemperatureMin"/>
                      <span>정상 체온(<c:out value="${userHealthavg.avgTemperatureMin }"/>~<c:out value="${userHealthavg.avgTemperatureMax }"/>)</span>
                    </div>
                    <div class="user_state__num">
                      <strong id= "temperature_realtime" >0</strong>
                    </div>
                  </li>
                  <li class="user_state__item">
                    <div class="user_state__title _state04">
                      <strong>스트레스</strong>
                      <input type="hidden" value = "${userHealthavg.avgStressMax}" name = "avgStressMax"/>
                      <input type="hidden" value = "${userHealthavg.avgStressMin}" name = "avgStressMin"/>
                      <span>정상 스트레스(<c:out value="${userHealthavg.avgStressMin }"/>~<c:out value="${userHealthavg.avgStressMax }"/>)</span>
                    </div>
                    <div class="user_state__num">
                      <strong id= "stress_realtime">0</strong>
                    </div>
                  </li>
                </ul>

              </div>
            </section>
            <section class="flex_item radius_box">
              <div class="radius_header">
                <h2 class="radius_header__title">오늘의 목표</h2>
              </div>
              <div class="radius_body">
                <ul class="user_state">
                  <li class="user_state__item">
                    <div class="user_objective__title _objective01">
                      <p class="user_objective__txt">오늘 목표 걸음수는 <strong id="step_target" class="_red">10,000보</strong><br>
                      현재 <strong id ="step_realtime">0보</strong></p>
                    </div>
                  </li>
                  <li class="user_state__item">
                    <div class="user_objective__title _objective02">
                      <p class="user_objective__txt">어제 취침시간은  <strong id ="sleep_realtime" class="_red">8시간</strong>이에요.<br>
                      <strong id="sleep_target" > 개운한 하루 보내세요</strong> </p>
                    </div>
                  </li>
                  <li class="user_state__item">
                    <div class="user_objective__title _objective03">
                      <p class="user_objective__txt">현재 스트레스는 <strong id='stress' class="_red">0</strong>이에요. <br> <strong id ="stress_target" >좋은하루 되세요.</strong></p>
                    </div>
                  </li>
                </ul>
              </div>
            </section>
          </div>

          <div class="flex_row">
            <section class="flex_item radius_box">
              <div class="radius_header">
                <h2 class="radius_header__title">AI 건강 주치의</h2>
              </div>
              <div class="radius_body">
                <p class="chat_txt"><img src="/modules/images/common/img_chat_gpt.png" alt="">가 알려주는 나의 건강상태 <strong class="chat_txt_str">AI 주치의</strong>와 함께하세요.</p>
                <div class="chat_out_txt result_airesponse">
                  <p class="chat_none_out_txt"></p>
                </div>
              </div>
            </section>
          </div>

          <div class="flex_row">
            <section class="flex_item__col2 radius_box">
              <div class="radius_header">
                <h2 class="radius_header__title">보호자/의사 정보</h2>
              </div>
              <div class="radius_body">
                <section class="board_list_wrap">
                  <ul class="board_list">
                    <li class="board_list__box borad_list_hd_wrap">
                      <div class="board_list__title">
                        <div class="board_con_hd flex-auto">구분</div>
                        <div class="board_con_hd flex-auto">소속</div>
                        <div class="board_con_hd flex-auto">이름</div>
                        <div class="board_con_hd flex-auto">이메일</div>
                        <div class="board_con_hd flex-auto">연락처</div>
                      </div>
                    </li>
                    <c:if test="${!empty userRolelist}">
                        <c:forEach items="${userRolelist}" var="item">
                            <li class="board_list__box">
                              <div class="board_list__item">
                                <div class="board_con flex-auto"><c:out value = "${item.userRole}"/></div>
                                <div class="board_con flex-auto"><c:out value = "${item.deptNm}"/></div>
                                <div class="board_con flex-auto"><c:out value = "${item.userNm}"/></div>
                                <div class="board_con flex-auto"><c:out value = "${item.email}"/></div>
                                <c:if test="${item.userRole eq '의사'}">
                                    <div class="board_con flex-auto">010-****-****</div>
                                </c:if>
                                <c:if test="${item.userRole eq '보호자'}">
                                    <div class="board_con flex-auto"><c:out value = "${item.telNumEnc}"/></div>
                                </c:if>
                              </div>
                            </li>
                        </c:forEach>
                    </c:if>
                    <c:if test ="${empty userRolelist}">
                        <li class="board_list__box">
                          <div class="board_list__none">등록된 보호자/의사의 정보가 없습니다.</div>
                         </li>
                    </c:if>
                  </ul>
                </section>
              </div>
            </section>
            <section class="flex_item radius_box">
              <div class="radius_header">
                <h2 class="radius_header__title">건강 소식</h2>
              </div>
              <div class="radius_body">
                <div class="img_box">
                  <a href ="https://www.nhis.or.kr/nhis/together/wbhaea01600m01.do?mode=view&articleNo=10841393" target="_blank">
                  <img src="/modules/images/common/img_sample001.jpg" alt="">
                  </a>
                </div>
              </div>
            </section>
          </div>

        </main>
      </div>
    </section>
        <jsp:include page="/WEB-INF/jsp/include/footer.jsp" />
  </div>


<script src="/js/biodata/realtimeBiodata.js" type="text/javascript"></script>
<script src="/js/AI_chatgpt/getAIResult.js" type="text/javascript"></script>
<script>

    var searchUserId = '${UserinfoId}';
    var searchUserGender = '${gender}';
    var searchUserAge = '${ageAvg}';
    var searchUserNm = '${userNm }';
    var sleepData = 0;

    $(document).ready(function(){

        $('#header_user_detail').addClass('_active');

        if(${!empty acToken}){
           getHealthdata(searchUserId);
           setInterval(() => realtimeBiodata(searchUserId), 60000);

        }
        else {
            location.href = 'redirect:/user/signin';
        }
    });

function getHealthdata(searchUserId){
    var chartList = [];
    $.ajax({
        type:"POST",
        url:"/health/healthdailyData",
        dataType:"JSON",
        data:{ "userId": searchUserId},
        success : function(data) {
            if(data.resultCode == '0000'){

                getAIResult(data.resultData,searchUserGender,searchUserAge, searchUserNm);
                sleepData =  data.resultData.sleep;
                setBiodata(data);
            }
            else {
                $('.chat_none_out_txt').text("건강 상태 정보가 없어서 진단하지 못했습니다.");
            }

        },
        complete : function(data) {
        },
        error : function(xhr, status, error) {
              console.log(error);
        }
    });
}

</script>
</body>
</html>