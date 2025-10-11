<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
      pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/_dtd.jsp" %>
  <title>건강나이 동기부여</title>
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
              <div class="radius_header">
                <h2 class="radius_header__title">50대 건강나이</h2>
              </div>
              <div class="radius_body">
                <div class="age_group">
                  <!-- 카드반복 -->
                  <section class="age_group_card">
                    <div class="age_card__name">
                      <strong class="_my">(나)<c:out value="${userNm }" /></strong>
                    </div>

                    <div class="age_card__age">
                      <div class="age_card__as">
                        <span class="_age"><c:out value="${ageAvg }" /></span>
                        <span class="_txt">현재나이</span>
                      </div>
                      <div class="age_card__is">
                        <span id = "bodyAge_info" class=" _age">
                          <!-- <span class="_red">00</span> -->
                          <span class="_blue">00</span>
                        </span>
                        <span class="_txt">건강나이</span>
                      </div>
                    </div>

                    <div class="age_card__info_box">
                      <div class="age_card__info">
                         <span>심박수</span>
                            <span id="heartrate">0</span>
                          </div>
                          <div class="age_card__info">
                            <span>혈압</span>
                            <span id="bloodpress">0</span>
                          </div>
                          <div class="age_card__info">
                            <span>체온</span>
                            <span id="temperature">0</span>
                      </div>
                    </div>

                    <div class="age_card__comm _save">
                      <a href="#none" class="btn_save js-pop-open" data-target=".pop02">공유하기</a>
                    </div>
                  </section>
                  <!-- // 카드반복 -->
                  <c:if test="${!empty commulist}">
                    <c:forEach items="${commulist}" var="item">
                      <section class="age_group_card">
                        <div class="age_card__name">
                          <strong class=""><c:out value = "${item.userNm}"/></strong>
                        </div>

                        <div class="age_card__age">
                          <div class="age_card__as">
                            <span class="_age"><c:out value = "${item.age}"/></span>
                            <span class="_txt">현재나이</span>
                          </div>
                          <div class="age_card__is">
                            <span class="_age">
                              <c:if test="${item.bodyAge > item.age}">
                                <span class="_red"><c:out value = "${item.age}"/></span>
                              </c:if>
                              <c:if test="${item.bodyAge < item.age}">
                                 <span class="_blue"><c:out value = "${item.age}"/></span>
                              </c:if>
                            </span>
                            <span class="_txt">건강나이</span>
                          </div>
                        </div>

                        <div class="age_card__info_box">
                          <div class="age_card__info">
                            <span>심박수</span>
                            <span><c:out value = "${item.heartrate}"/></span>
                          </div>
                          <div class="age_card__info">
                            <span>혈압</span>
                            <span><c:out value = "${item.bloodpress}"/></span>
                          </div>
                          <div class="age_card__info">
                            <span>체온</span>
                            <span><c:out value = "${item.temperature}"/></span>
                          </div>
                        </div>
                        <div class="age_card__comm">
                          <div class="age_card__icon_box">
                            <c:if test="${item.smoking eq '1'}">
                              <span class="_icon01 _checked"><span class="blind">담배</span></span>
                            </c:if>
                            <c:if  test="${item.smoking ne '1'}">
                              <span class="_icon01"><span class="blind">담배</span></span>
                            </c:if>

                             <c:if test="${item.drinking eq '1'}">
                              <span class="_icon02 _checked"><span class="blind">술</span></span>
                            </c:if>
                            <c:if  test="${item.drinking ne '1'}">
                              <span class="_icon02"><span class="blind">술</span></span>
                            </c:if>

                             <c:if test="${item.exercise eq '1'}">
                              <span class="_icon03 _checked"><span class="blind">운동</span></span>
                            </c:if>
                            <c:if  test="${item.exercise ne '1'}">
                              <span class="_icon03"><span class="blind">운동</span></span>
                            </c:if>

                          </div>
                          <div class="age_card__comm_txt js-scroll">
                             <c:out value = "${item.content}"/>
                          </div>
                        </div>
                      </section>
                    </c:forEach>
                  </c:if>
                </div>
              </div>
            </section>
          </div>

            <div class="flex_row">
              <section class="flex_item radius_box">
                <div class="radius_header">
                  <h2 class="radius_header__title">60대 건강나이</h2>
                </div>
                <div class="radius_body">
                  <div class="age_group">
                    <!-- 카드반복 -->
                    <section class="age_group_card">
                      <div class="age_card__name">
                        <strong class="">김지영 </strong>
                      </div>

                      <div class="age_card__age">
                        <div class="age_card__as">
                          <span class="_age">60</span>
                          <span class="_txt">현재나이</span>
                        </div>
                        <div class="age_card__is">
                          <span class="_age">
                            <span class="_blue">49</span>
                          </span>
                          <span class="_txt">건강나이</span>
                        </div>
                      </div>

                      <div class="age_card__info_box">
                        <div class="age_card__info">
                          <span>심박수</span>
                          <span>104</span>
                        </div>
                        <div class="age_card__info">
                          <span>혈압</span>
                          <span>98</span>
                        </div>
                        <div class="age_card__info">
                          <span>체온</span>
                          <span>36</span>
                        </div>
                      </div>

                      <div class="age_card__comm">
                        <div class="age_card__icon_box">
                          <span class="_icon01"><span class="blind">담배</span></span>
                          <span class="_icon02"><span class="blind">술</span></span>
                          <span class="_icon03 _checked"><span class="blind">운동</span></span>
                        </div>
                        <div class="age_card__comm_txt js-scroll">
                          담배 안피워요, 술 안마셔요, 운동은 거의 매일 하고 있어요.
                        </div>
                      </div>
                    </section>
                    <!-- // 카드반복 -->

                    <section class="age_group_card">
                      <div class="age_card__name">
                        <strong class="">이승준</strong>
                      </div>

                      <div class="age_card__age">
                        <div class="age_card__as">
                          <span class="_age">64</span>
                          <span class="_txt">현재나이</span>
                        </div>
                        <div class="age_card__is">
                          <span class="_age">
                            <span class="_blue">58</span>
                          </span>
                          <span class="_txt">건강나이</span>
                        </div>
                      </div>

                      <div class="age_card__info_box">
                        <div class="age_card__info">
                          <span>심박수</span>
                          <span>104</span>
                        </div>
                        <div class="age_card__info">
                          <span>혈압</span>
                          <span>98</span>
                        </div>
                        <div class="age_card__info">
                          <span>체온</span>
                          <span>36</span>
                        </div>
                      </div>

                      <div class="age_card__comm">
                        <div class="age_card__icon_box">
                          <span class="_icon01"><span class="blind">담배</span></span>
                          <span class="_icon02"><span class="blind">술</span></span>
                          <span class="_icon03"><span class="blind">운동</span></span>
                        </div>
                        <div class="age_card__comm_txt js-scroll">
                          담배는 피워본적이 없습니다. 술은 하날에 한번 정도만 아주 가끔 맥주를 마십니다. 운동은 따로 하고있지 않아요.
                        </div>
                      </div>
                    </section>

                    <section class="age_group_card">
                      <div class="age_card__name">
                        <strong class="">박현우 </strong>
                      </div>

                      <div class="age_card__age">
                        <div class="age_card__as">
                          <span class="_age">62</span>
                          <span class="_txt">현재나이</span>
                        </div>
                        <div class="age_card__is">
                          <span class="_age">
                            <span class="_red">68</span>
                          </span>
                          <span class="_txt">건강나이</span>
                        </div>
                      </div>

                      <div class="age_card__info_box">
                        <div class="age_card__info">
                          <span>심박수</span>
                          <span>104</span>
                        </div>
                        <div class="age_card__info">
                          <span>혈압</span>
                          <span>98</span>
                        </div>
                        <div class="age_card__info">
                          <span>체온</span>
                          <span>36</span>
                        </div>
                      </div>

                      <div class="age_card__comm">
                        <div class="age_card__icon_box">
                          <span class="_icon01 _checked"><span class="blind">담배</span></span>
                          <span class="_icon02 _checked"><span class="blind">술</span></span>
                          <span class="_icon03"><span class="blind">운동</span></span>
                        </div>
                        <div class="age_card__comm_txt js-scroll">
                          담배는 가끔 피우고 있고요, 술은 거의 매일 마신다고 보면되고 운동은 전혀 하지 않아요.
                        </div>
                      </div>
                    </section>

                    <section class="age_group_card">
                      <div class="age_card__name">
                        <strong class="">정수빈</strong>
                      </div>

                      <div class="age_card__age">
                        <div class="age_card__as">
                          <span class="_age">69</span>
                          <span class="_txt">현재나이</span>
                        </div>
                        <div class="age_card__is">
                          <span class="_age">
                            <span class="_red">74</span>
                          </span>
                          <span class="_txt">건강나이</span>
                        </div>
                      </div>

                      <div class="age_card__info_box">
                        <div class="age_card__info">
                          <span>심박수</span>
                          <span>104</span>
                        </div>
                        <div class="age_card__info">
                          <span>혈압</span>
                          <span>98</span>
                        </div>
                        <div class="age_card__info">
                          <span>체온</span>
                          <span>36</span>
                        </div>
                      </div>

                      <div class="age_card__comm">
                        <div class="age_card__icon_box">
                          <span class="_icon01 _checked"><span class="blind">담배</span></span>
                          <span class="_icon02"><span class="blind">술</span></span>
                          <span class="_icon03"><span class="blind">운동</span></span>
                        </div>
                        <div class="age_card__comm_txt js-scroll">
                          술은 전혀 마지시 않고 있습니다. 하지만 담배를 거의 하루에 두갑 정도 피웁니다. 운동은 가끔 하지만 규칙적이지 않아요.
                        </div>
                      </div>
                    </section>
                  </div>
                </div>
              </section>
            </div>

            <div class="flex_row">
              <section class="flex_item radius_box">
                <div class="radius_header">
                  <h2 class="radius_header__title">70대 건강나이</h2>
                </div>
                <div class="radius_body">
                  <div class="age_group">
                    <!-- 카드반복 -->
                    <section class="age_group_card">
                      <div class="age_card__name">
                        <strong class="">최민지</strong>
                      </div>

                      <div class="age_card__age">
                        <div class="age_card__as">
                          <span class="_age">77</span>
                          <span class="_txt">현재나이</span>
                        </div>
                        <div class="age_card__is">
                          <span class="_age">
                            <span class="_blue">58</span>
                          </span>
                          <span class="_txt">건강나이</span>
                        </div>
                      </div>

                      <div class="age_card__info_box">
                        <div class="age_card__info">
                          <span>심박수</span>
                          <span>104</span>
                        </div>
                        <div class="age_card__info">
                          <span>혈압</span>
                          <span>98</span>
                        </div>
                        <div class="age_card__info">
                          <span>체온</span>
                          <span>36</span>
                        </div>
                      </div>

                      <div class="age_card__comm">
                        <div class="age_card__icon_box">
                          <span class="_icon01"><span class="blind">담배</span></span>
                          <span class="_icon02"><span class="blind">술</span></span>
                          <span class="_icon03"><span class="blind">운동</span></span>
                        </div>
                        <div class="age_card__comm_txt js-scroll">
                          술담배 운동 아무것도 안해요. 스트레스 받지 않게 음악 감상과 책을 많이 읽어요 이게 건강과 관련이 있는지는 잘 모르겠어요. 하지만 그것 때문이라고 믿고 있어서 추천합니다.
                        </div>
                      </div>
                    </section>
                    <!-- // 카드반복 -->

                    <section class="age_group_card">
                      <div class="age_card__name">
                        <strong class="">강태영</strong>
                      </div>

                      <div class="age_card__age">
                        <div class="age_card__as">
                          <span class="_age">73</span>
                          <span class="_txt">현재나이</span>
                        </div>
                        <div class="age_card__is">
                          <span class="_age">
                            <span class="_blue">67</span>
                          </span>
                          <span class="_txt">건강나이</span>
                        </div>
                      </div>

                      <div class="age_card__info_box">
                        <div class="age_card__info">
                          <span>심박수</span>
                          <span>104</span>
                        </div>
                        <div class="age_card__info">
                          <span>혈압</span>
                          <span>98</span>
                        </div>
                        <div class="age_card__info">
                          <span>체온</span>
                          <span>36</span>
                        </div>
                      </div>

                      <div class="age_card__comm">
                        <div class="age_card__icon_box">
                          <span class="_icon01"><span class="blind">담배</span></span>
                          <span class="_icon02"><span class="blind">술</span></span>
                          <span class="_icon03 _checked"><span class="blind">운동</span></span>
                        </div>
                        <div class="age_card__comm_txt js-scroll">
                          건강 상 문제로 담배는 젊었을 때 피우고 끊었습니다. 술도 마시지 않고 있습니다. 건강 때 문에 운동을 매일 하고있어요.
                        </div>
                      </div>
                    </section>

                    <section class="age_group_card">
                      <div class="age_card__name">
                        <strong class="">윤서연</strong>
                      </div>

                      <div class="age_card__age">
                        <div class="age_card__as">
                          <span class="_age">79</span>
                          <span class="_txt">현재나이</span>
                        </div>
                        <div class="age_card__is">
                          <span class="_age">
                            <span class="_blue">70</span>
                          </span>
                          <span class="_txt">건강나이</span>
                        </div>
                      </div>

                      <div class="age_card__info_box">
                        <div class="age_card__info">
                          <span>심박수</span>
                          <span>104</span>
                        </div>
                        <div class="age_card__info">
                          <span>혈압</span>
                          <span>98</span>
                        </div>
                        <div class="age_card__info">
                          <span>체온</span>
                          <span>36</span>
                        </div>
                      </div>

                      <div class="age_card__comm">
                        <div class="age_card__icon_box">
                          <span class="_icon01"><span class="blind">담배</span></span>
                          <span class="_icon02"><span class="blind">술</span></span>
                          <span class="_icon03 _checked"><span class="blind">운동</span></span>
                        </div>
                        <div class="age_card__comm_txt js-scroll">
                          술 담배 안하고, 운동만 열심히 합니다.
                        </div>
                      </div>
                    </section>

                    <section class="age_group_card">
                      <div class="age_card__name">
                        <strong class="">한재호</strong>
                      </div>

                      <div class="age_card__age">
                        <div class="age_card__as">
                          <span class="_age">73</span>
                          <span class="_txt">현재나이</span>
                        </div>
                        <div class="age_card__is">
                          <span class="_age">
                            <span class="_red">80</span>
                          </span>
                          <span class="_txt">건강나이</span>
                        </div>
                      </div>

                      <div class="age_card__info_box">
                        <div class="age_card__info">
                          <span>심박수</span>
                          <span>104</span>
                        </div>
                        <div class="age_card__info">
                          <span>혈압</span>
                          <span>98</span>
                        </div>
                        <div class="age_card__info">
                          <span>체온</span>
                          <span>36</span>
                        </div>
                      </div>

                      <div class="age_card__comm">
                        <div class="age_card__icon_box">
                          <span class="_icon01 _checked"><span class="blind">담배</span></span>
                          <span class="_icon02 _checked"><span class="blind">술</span></span>
                          <span class="_icon03"><span class="blind">운동</span></span>
                        </div>
                        <div class="age_card__comm_txt js-scroll">
                          담배도 안피우고 술도 안마셔요. 몸이 안좋아서 운동도 할 수 없습니다 왜이렇게 결과가 안좋은지 모르겠어요.
                        </div>
                      </div>
                    </section>
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
  
  <section class="popup_dim pop02" style="display: none;">
    <section class="popup_box">
      <div class="pop_header">
        <h1 class="pop_header__title">건강나이 공유</h1>
        <a href="" class="pop_header__close"><span hidden>닫기</span></a>
      </div>
      <div class="popup_body">
        <ul class="age_community">
          <li>
            <div class="form_radio__item">
                <input type="checkbox" name="research" value="smoking" id="ck01" class="">
              <label for="ck01" class="age_community_label">매일 2개비 이상의 담배를 피우고 있습니다. ( +1)</label>
            </div>
          </li>
          <li>
            <div class="form_radio__item">
                <input type="checkbox" name="research" value="drinking" id="ck02" class="" >
              <label for="ck02" class="age_community_label">일주일에 3번 이상 술을 마시고 있습니다. ( +2) </label>
            </div>
          </li>
          <li>
            <div class="form_radio__item">
                <input type="checkbox" name="research" value="exercise" id="ck03" class="">
              <label for="ck03" class="age_community_label">일주일에 2번 숨이 찰 정도의 꾸준한 운동을 하고있습니다. ( -2)</label>
            </div>
          </li>
        </ul>
        <div class="">
            <textarea id="content" name="content"  class="form_item_textarea" cols="30" rows="10" style="height: 100px;" placeholder="나의 건강나이 유지 비결을 적고 공유하세요."></textarea>
        </div>
      </div>
      <div class="button_right_group">
        <a href="#none" class="btn_cancel js-pop-close" data-target=".popup_dim">취소</a>
        <a href="#none" class="btn_save js-pop-close" data-target=".popup_dim" id ="shareSave">공유</a>
      </div>
    </section>
  </section>

</body>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.0.0/jquery.min.js"></script>
<link rel="stylesheet" href="../../../css/vender/jquery.mCustomScrollbar.css">

<script src="../../../modules/js/vendor/jquery.mCustomScrollbar.js"></script>
<script>

    var searchUserId = '${UserinfoId}';
    var searchUserGender = '${gender}';
    var searchUserAge = '${ageAvg}';
    var searchUserNm = '${sessUserNm}';
    var heartrateval =0;
    var temperatureval =0;
    var bloodpressval = 0;
    let bodyAge = parseInt('${bodyAge}');

    $(document).ready(function(){


        if(${!empty acToken}){
          commuBiodata(searchUserId);
        }
        else {
            //로그인 안한 유저가 들어온경우
            location.href = 'redirect:/user/signin';
        }
    });

function commuBiodata(searchUserId){
    var chartList = [];

    $.ajax({
        type:"POST",
        url:"/health/healthrealtimeData",
        dataType:"JSON",
        data:{ "userId": searchUserId},
        success : function(data) {
            if(data.resultCode == '0000'){
                $('#heartrate').text(data.resultData.heartrate);
                $('#temperature').text(data.resultData.temperature);

                var bloodpressAvg = (data.resultData.bloodpressMin + data.resultData.bloodpressMax )/2;

                $('#bloodpress').text(bloodpressAvg);

                if(data.resultData.stress < 30) {
                    bodyAge += 1; //stress불건강
                }
                if(data.resultData.bloodpressMax > 140 ) {
                    bodyAge += 1;
                }

                if(searchUserAge > bodyAge) {
                     $('#bodyAge_info').html('<span class="_blue">'+bodyAge+'</span>');

                }
                else if(searchUserAge < bodyAge) {
                     $('#bodyAge_info').html('<span class="_red">'+bodyAge+'</span>');
                }

                heartrateval = data.resultData.heartrate;
                temperatureval = data.resultData.temperature;
                bloodpressval = bloodpressAvg;
            }

        },
        complete : function(data) {
        },
        error : function(xhr, status, error) {
              console.log(error);
        }
    });
}

	$('#shareSave').click(function(){
	    const query = 'input[name="research"]:checked';
	    const selectedEls = document.querySelectorAll(query);
	    let smokeVal = '0';
	    let drinkVal = '0';
	    let exerciseVal = '0';

	    selectedEls.forEach((el) => {
            if(el.value == 'smoking') { smokeVal = 1; bodyAge += 1;}
            if(el.value == 'drinking') { drinkVal = 1; bodyAge += 2;}
            if(el.value == 'exercise') {exerciseVal = 1; bodyAge -= 2;}

        });

	    $.ajax({
              type:"Post",
              url:"/community/inscommunity",
              dataType:"JSON", // 옵션이므로 JSON으로 받을게 아니면 안써도 됨
              data: {
                  "heartrate": heartrateval,
                  "temperature" : temperatureval,
                  "bloodpress" : bloodpressval,
                  "userId" : '${userId}',
                  "age" : searchUserAge,
                  "content": $('textarea[name="content"]').val(),
                  "smoking" : smokeVal,
                  "drinking" :drinkVal,
                  "exercise" : exerciseVal,
                  "userNm" : searchUserNm,
                  "bodyAge" :bodyAge
              },
              success : function(data) {
              // 통신이 성공적으로 이루어졌을 때 이 함수를 타게 된다.
              // TODO
                  if(data.resultCode == '0000'){
                      alert("공유했습니다.");
                      location.href = 'redirect:/userInfo/userBoardInfo?userinfoId=' + searchUserId;
                  }
              },
               complete : function(data) {
                     // 통신이 실패했어도 완료가 되었을 때 이 함수를 타게 된다.
                     // TODO
                    $('textarea[name="content"]').val("");
                    $('input[type="checkbox"]').prop('checked', false);

               },
               error : function(xhr, status, error) {
                     alert("에러발생");
               }
         });
	});



</script>
</html>