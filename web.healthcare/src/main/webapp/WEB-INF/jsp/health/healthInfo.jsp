<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ include file="/WEB-INF/jsp/include/_dtd.jsp" %>

<jsp:include page="/WEB-INF/jsp/layout/head.jsp" flush="true">
  <jsp:param name="pageType" value="eb-main" />
  <jsp:param name="bodyAttr" value="onload=''" />
  <jsp:param name="bodyStyle" value="eb-main" />
</jsp:include>
<title>
  <c:choose> <c:when test="${fn:split(uri,'/')[1] eq 'temperature'
  }">체온</c:when> <c:when test="${fn:split(uri,'/')[1] eq 'spo2'
  }">산소포화도</c:when> <c:when test="${fn:split(uri,'/')[1] eq 'sleep'
  }">수면</c:when> <c:when test="${fn:split(uri,'/')[1] eq 'heartrate'
  }">심박수</c:when> <c:when test="${fn:split(uri,'/')[1] eq 'step'
  }">운동량</c:when> <c:when test="${fn:split(uri,'/')[1] eq 'stress'
  }">스트레스</c:when> <c:when test="${fn:split(uri,'/')[1] eq 'bloodpress'
  }">혈압</c:when> <c:otherwise>호흡수</c:otherwise> </c:choose>
</title>
<jsp:include page="/WEB-INF/jsp/layout/mainHeader.jsp" />
<div class="container">
  <div class="container_wrap inner">
    <jsp:include page="/WEB-INF/jsp/layout/mainLeft.jsp" flush="true" />
    <section class="layout_content">
      <h1 class="content_main_title">
        <c:if test="${userNm ne 'undefined' && userNm ne null && userNm ne ''}"
          ><c:out value="${userNm}" />님 </c:if
        >건강정보
      </h1>
      <div class="content_wrap">
        <section class="radius_con">
          <div class="chart_header">
            <c:choose>
              <c:when test="${fn:split(uri,'/')[1] eq 'heartrate' }"
                ><h1 class="chart_header__chart01">심박수</h1></c:when
              >
              <c:when test="${fn:split(uri,'/')[1] eq 'spo2' }"
                ><h1 class="chart_header__chart02">산소포화도</h1></c:when
              >
              <c:when test="${fn:split(uri,'/')[1] eq 'step' }"
                ><h1 class="chart_header__chart03">운동량</h1></c:when
              >
              <c:when test="${fn:split(uri,'/')[1] eq 'stress' }"
                ><h1 class="chart_header__chart04">스트레스</h1></c:when
              >
              <c:when test="${fn:split(uri,'/')[1] eq 'sleep' }"
                ><h1 class="chart_header__chart05">수면</h1></c:when
              >
              <c:when test="${fn:split(uri,'/')[1] eq 'bloodpress' }"
                ><h1 class="chart_header__chart06">혈압</h1></c:when
              >
              <c:when test="${fn:split(uri,'/')[1] eq 'temperature' }"
                ><h1 class="chart_header__chart08">체온</h1></c:when
              >
              <c:otherwise
                ><h1 class="chart_header__chart07">호흡수</h1></c:otherwise
              >
            </c:choose>

            <div class="chart_header__tab">
              <a
                href="javascript:chartSet('D');"
                id="chartSetD"
                class="chart_header__tab_link"
                >1일</a
              >
              <a
                href="javascript:chartSet('W');"
                id="chartSetW"
                class="chart_header__tab_link"
                >1주일</a
              >
              <a
                href="javascript:chartSet('M');"
                id="chartSetM"
                class="chart_header__tab_link"
                >1개월</a
              >
              <a
                href="javascript:chartSet('Y');"
                id="chartSetY"
                class="chart_header__tab_link"
                >1년</a
              >
            </div>
          </div>

          <div class="flex-box _end">
            <div class="move_date">
              <input
                type="hidden"
                id="searchType"
                name="searchType"
                value="D"
              />
              <input type="hidden" id="searchWrd" name="" value="<c:out
                value="${fn:split(uri,'/')[1] }"
              ></c:out
              >"/>
              <a href="javascript:dateChange('prev')" class="move_date__prev"
                ><span hidden></span
              ></a>
              <input
                type="date"
                id="date"
                name="date"
                value=""
                hidden="hidden"
              />
              <span id="dateTxt" class="move_date__txt"></span>
              <a href="javascript:dateChange('next')" class="move_date__next"
                ><span hidden></span
              ></a>
            </div>
          </div>

          <div class="chartSection">
            <canvas id="myChart"></canvas>
          </div>
        </section>
      </div>
      <!-- // 콘텐츠 내용 -->
    </section>
  </div>
</div>
<jsp:include page="/WEB-INF/jsp/layout/mainFooter.jsp" />
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script src="/js/chart/healthChart.js"></script>
<style>
  .chartSection {
    height: 500px;
  }
</style>
<script>
  var tag = "";
  var type = "bar";
  var myChart;
  var condition = "";
  var searchType = "";

  $(document).ready(function () {
    var now = new Date();
    $("#date").val(now.toISOString().substring(0, 10));
    $("#dateTxt").text(now.toISOString().substring(0, 10).replaceAll("-", "."));
    chartSet("D");
  });
  function dateChange(calculation) {
    var searchQuery = $("#searchType").val();
    var query = calculation === "prev" ? -1 : 1;
    var now = new Date($("#date").val());
    var date = "";
    switch (searchQuery) {
      case "D":
        date = new Date(now.setDate(now.getDate() + query));
        break;
      case "W":
        date = new Date(now.setDate(now.getDate() + query * 7));
        break;
      case "M":
        date = new Date(now.setDate(now.getDate() + query * 30));
        break;
      case "Y":
        date = new Date(now.setFullYear(now.getFullYear() + query));
        break;
      default:
        date = new Date(now.setDate(now.getDate() + query));
        break;
    }

    $("#dateTxt").text(
      date.toISOString().substring(0, 10).replaceAll("-", ".")
    );
    $("#date").val(date.toISOString().substring(0, 10));
    drawChartAjax();
  }

  function typeCheck() {
    var id = $("#searchWrd").val();
    if (id == "temperature") {
      searchType = "all";
      condition = "30";
    } else if (id == "spo2") {
      searchType = "all";
      condition = "60";
    } else if (id == "sleep") {
      searchType = "all";
      condition = "60";
    } else if (id == "heartrate") {
      searchType = "all";
      condition = "5";
    } else if (id == "step") {
      searchType = "avg";
      condition = "60";
    } else if (id == "stress") {
      searchType = "all";
      condition = "30";
    } else if (id == "bloodpress") {
      searchType = "minmax";
      condition = "30";
    } else {
      // 호흡수
      searchType = "all";
      condition = "30";
    }
  }

  function drawChartAjax() {
    typeCheck();
    $.ajax({
      type: "POST",
      url: "/health/healthInfoChart",
      dataType: "JSON",
      data: {
        query: $("#searchType").val(),
        type: searchType,
        userId: '<c:out value="${searchUserId}" />',
        date: $("#date").val(),
        searchWrd: $("#searchWrd").val(),
        condition: condition,
      },
      success: function (data) {
        if (data.resultCode == "0000") {
          var chartId = $("#searchWrd").val();
          if (chartId == "step") {
            drawBarChart(daata.resultData, chartId);
          } else if (chartId == "stress") {
            var chartData = {};
            chartData["data"] = data.resultData.dataMax;
            chartData["lv"] = data.resultData.lv;
            drawBarChart(chartData, chartId);
          } else if (chartId == "sleep") {
            drawStackedChart();
          } else {
            drawFloatingChart(data.resultData, chartId);
          }
        }
        if ($("#searchType").val() != "D")
          $("#dateTxt").text(data.resultData.startDt.replaceAll("-", "."));
        else $("#dateTxt").text($("#date").val().replaceAll("-", "."));
      },
      complete: function (data) {},
      error: function (xhr, status, error) {
        console.log(error);
      },
    });
  }

  function chartSet(type) {
    $("#searchType").val(type);
    $(".chart_header__tab_link--on").each(function (idx, item) {
      $(item).attr("class", "chart_header__tab_link");
    });
    switch (type) {
      case "Y":
        $("#chartSetY").attr("class", "chart_header__tab_link--on");
        break;
      case "M":
        $("#chartSetM").attr("class", "chart_header__tab_link--on");
        break;
      case "W":
        $("#chartSetW").attr("class", "chart_header__tab_link--on");
        break;
      case "D":
      default:
        $("#chartSetD").attr("class", "chart_header__tab_link--on");
        break;
    }
    drawChartAjax();
  }
</script>
<jsp:include page="/WEB-INF/jsp/layout/mainEnd.jsp" />
