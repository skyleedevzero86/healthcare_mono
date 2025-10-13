<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/_dtd.jsp" %>

<jsp:include page='/WEB-INF/jsp/layout/head.jsp' flush="true">
<jsp:param name="pageType" value="eb-main" />
	<jsp:param name="bodyAttr" value="onload=''" />
	<jsp:param name="bodyStyle" value="eb-main" />
</jsp:include>
<jsp:include page='/WEB-INF/jsp/layout/mainHeader.jsp' />
<title>
	대시보드
</title>
<div class="container">
<div class="container_wrap inner">
			<jsp:include page='/WEB-INF/jsp/layout/mainLeft.jsp' flush="true"/>
			<section class="layout_content">
				<h1 class="content_main_title"><c:if test="${userNm ne null && userNm ne ''}"><c:out value="${userNm}님"></c:out></c:if> 대시보드</h1>
				<input type="date" id="date" name="date" value="" hidden="hidden">
				<input type="hidden" id="userId" name="userId" value="">
				<input type="hidden" id="searchType" name="searchType" value="D" />
				<div class="content_wrap">
					<h1 class="content_group__title"><a href="javascript:userHealthInfo('/health/heartrate','<c:out value="${searchUserId}" />','<c:out value="${userNm}" />')" class="title_chart01">심박수</a></h1>
					<section class="radius_con chartSection">
				  		<canvas class="myChart" id="heartrate"></canvas>
				  	</section>
				</div>
				<div class="content_wrap">
					<h1 class="content_group__title"><a href="javascript:userHealthInfo('/health/spo2','<c:out value="${searchUserId}" />','<c:out value="${userNm}" />')" class="title_chart02">산소포화도</a></h1>
					<section class="radius_con chartSection">
				  		<canvas class="myChart" id="spo2"></canvas>
				  	</section>
				</div>
				<div class="content_wrap">
					<h1 class="content_group__title"><a href="javascript:userHealthInfo('/health/step','<c:out value="${searchUserId}" />','<c:out value="${userNm}" />')" class="title_chart03">운동량</a></h1>
					<section class="radius_con chartSection">
				  		<canvas class="myChart" id="step"></canvas>
				  	</section>
				</div>
				<div class="content_wrap">
					<h1 class="content_group__title"><a href="javascript:userHealthInfo('/health/stress','<c:out value="${searchUserId}" />','<c:out value="${userNm}" />')" class="title_chart04">스트레스</a></h1>
					<section class="radius_con chartSection">
				  		<canvas class="myChart" id="stress"></canvas>
				  	</section>
				</div>
				<div class="content_wrap">
					<h1 class="content_group__title"><a href="javascript:userHealthInfo('/health/sleep','<c:out value="${searchUserId}" />','<c:out value="${userNm}" />')" class="title_chart05">수면</a></h1>
					<section class="radius_con chartSection">
				  		<canvas class="myChart" id="sleep"></canvas>
				  	</section>
				</div>
				<div class="content_wrap">
					<h1 class="content_group__title"><a href="javascript:userHealthInfo('/health/bloodpress','<c:out value="${searchUserId}" />','<c:out value="${userNm}" />')" class="title_chart06">혈압</a></h1>
					<section class="radius_con chartSection">
				  		<canvas class="myChart" id="bloodpress"></canvas>
				  	</section>
				</div>
				<div class="content_wrap">
					<h1 class="content_group__title"><a href="javascript:userHealthInfo('/health/repiratory','<c:out value="${searchUserId}" />','<c:out value="${userNm}" />')" class="title_chart07">호흡수</a></h1>
					<section class="radius_con chartSection">
				  		<canvas class="myChart" id="repiratory"></canvas>
				  	</section>
				</div>
				<div class="content_wrap">
					<h1 class="content_group__title"><a href="javascript:userHealthInfo('/health/temperature','<c:out value="${searchUserId}" />','<c:out value="${userNm}" />')" class="title_chart08">체온</a></h1>
					<section class="radius_con chartSection">
				  		<canvas class="myChart" id="temperature"></canvas>
				  	</section>
				</div>
			</section>
		</div>
	</div>
<jsp:include page='/WEB-INF/jsp/layout/mainFooter.jsp' />

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script src="/js/chart/healthChart.js"></script>
<style>
	.chartSection {
		height : 500px;
	}
</style>
<script>
	var chart = new Object();
 	function drawChart(){
 		 	var chartList = [];
			$.ajax({
		        type:"POST",
		        url:"/health/healthInfoChart",
		        dataType:"JSON", 
		        data:{ "userId": '<c:out value="${searchUserId}" />' },
		        success : function(data) {
		            var res = data;
		            if(res.resultCode == '0000'){
						beforeDrawChartList(data.resultData);
		            }
		        },
		        complete : function(data) {
		        },
		        error : function(xhr, status, error) {
		              console.log(error);
		        }
		  });
	}

	function beforeDrawChartList(data){
		$('.myChart').each(function(idx, item){
			var id = $(item).attr('id');
			var condition = "",chartType = "";
			var chartData = {};

			switch(id){
				case "temperature": case "bloodpress": case "repiratory" : {
					condition = "half";
					chartType = "floating";
					break;
				}
				case "spo2" : {
					condition = "hour";
					chartType = "floating";
					break;
				}
				case "heartrate" : {
					condition = "min";
					chartType = "floating";
					break;
				}
				case "stress" :{
					condition = "half";
					chartType = "bar";
					break;
				}
				case "step" : {
					condition = "hour";
					chartType = "bar";
					break;
				}
				case "sleep" : {
					condition = "hour";
					chartType = "sleep";
					break;
				}
			}
			
			//datasetting by chart Type
			if (chartType == "floating") {
				chartData["dataMin"] = data[condition][id+'Min'];
				chartData["dataMax"] = data[condition][id+'Max'];
				chartData["lv"] = data[condition].lv;
				drawFloatingChartList(chartData,id);
			}else if (chartType == "bar"){
				if(id == "stress"){
					chartData["data"] = data[condition][id+"Max"];
				}else{
					chartData["data"] = data[condition][id];
				}
				chartData["lv"] = data[condition].lv;
				drawBarChartList(chartData,id);
			}else if (chartType == "sleep"){
				drawStackedChartList(id);
			}
		});
	}
	
	$(document).ready(function(){
		drawChart();
		$('#date').val(dateFormat(new Date()));
	});
	
	$('#date').change(function(){
		drawChart();
	})
	
	function chartSet(type){
		$('#searchType').val(type);
		drawChart();
	}

	function dateFormat(date) {
        let month = date.getMonth() + 1;
        let day = date.getDate();

        month = month >= 10 ? month : '0' + month;
        day = day >= 10 ? day : '0' + day;

        return date.getFullYear() + '-' + month + '-' + day;
	}
	
</script>
<jsp:include page='/WEB-INF/jsp/layout/mainEnd.jsp' />
