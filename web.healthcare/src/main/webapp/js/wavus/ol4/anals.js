///******************************************************
//  Javascript Name  : anals.js
//  Description      : 임대주택정보 공간분석
//  Modification Information
// 
//  수정일        수정자            수정내용
//  -------     --------    ---------------------------
//  2018-05-29  한영빈          최초 생성
// 
//  author   : 한영빈
//  since    : 2018-05-29
// ******************************************************/
//
//window.wavus = window.wavus || {};
//
//wavus.anals = (function()
//{
///**************************************************
// * 전역변수 영역
//**************************************************/
//	var map;
//	var params = {};
//	var realHeatmapLayer = new ol.layer.Heatmap(); //실거래가 히트맵 레이어
//	var heatmapLayer = new ol.layer.Heatmap(); //임대주택정보 히트맵 레이어
//	var drawLayer;
//	var clusterLayer = null;
//	var ltceAnalsAllLayer =  new ol.layer.Vector();
//	var ltceAnalsLayer =  new ol.layer.Vector();
//	var ltceStroke = 'rgba(255,255,255,0.1)';
//	var ltceFill = 'rgba(0,84,255,0.1)';
//	var radius = 8;
//	var blur = 55;
//	var radiusDiv = null; //반경계산
//	var selectRthousInfoList = new Array();
//	var selectRealDlpcList = new Array();
//	var sidoLayerUrl = '';
//	var sggLayerUrl = '';
//	var emdLayerUrl = '';
///**************************************************/	
//	//공간분석 팝업창 열기
//	function fnSpceAnalsPopup(){
//		
//		fnInit();
//		getLayerUrlInfo();
//		$("#analsSelect").val('01');
//		$("#stDdoAr").val('');
//		$("#edDdoAr").val('');
//		$("#realDlpc").val('00');
//		selectRthousInfoList = new Array();
//		selectRealDlpcList = new Array();
//		fnAnalsSelectChange();
//		fnIndictGbnChange();
//		fnRealDlpcChange();
//		fnHeatMapOpacity();
//		fnHeatMapRdmOpacity();
//		
//		getSidoList();
//		getSplTpCdList();
//		//내관심지역 선택 레이어 삭제
//		map.getLayers().forEach(function(layer){
//			var layerName =  layer.get('name') + '';
//			if(layerName == 'glanAr'){
//				map.removeLayer(layer);
//			}
//		});
//	}
//	//시군구, 읍면동 레이어 url 가져오기 
//	  function getLayerUrlInfo(){
//		  var url = "/anals/selectLayerUrlInfo.do";
//		   $.ajax({
//				type : "POST" ,
//				url : url ,
//				dataType : 'json' ,
//				success : function ( data ) {
//					var result = data.resultData.result;
//					sidoLayerUrl = result.sidoLayerUrl;
//					sggLayerUrl = result.sggLayerUrl;
//					emdLayerUrl = result.emdLayerUrl;
//				},
//				error: function (xhr, status, error) {
//					commonUtils.customAlert('처리 중 오류가 발생하였습니다.');
//				}
//			});
//	 }
//	
//	//읍면동 리스트 가져오기 
//	  function getEmdList(sigungu){
//		  var url = "/anals/selectEmdList.do";
//		  params["sggCd"] = sigungu;
//		  $("#emdList").empty();
//			$.ajax({
//				type : "POST" ,
//				url : url ,
//				data : params,
//				dataType : 'json' ,
//				success : function ( data ) {
//					var list = data.resultData.resultList;
//					$("#emdList").append('<option value="all">전체</option>');
//					$.each(list, function (index, value){
//						$("#emdList").append('<option value="'+ value.emdCd+ '">' + value.emdNm + '</option>');
//					});
//					params["sggCd"] = "";
//				},
//				error: function (xhr, status, error) {
//					commonUtils.customAlert('처리 중 오류가 발생하였습니다.');
//				}
//			});
//	 } 
//	//시군구 리스트 가져오기 
//	  function getSigunguList(sido){
//		  var url = "/anals/selectSigunguList.do";
//		  params["sidoCd"] = sido;
//		  $("#sggList").empty();
//			$.ajax({
//				type : "POST" ,
//				url : url ,
//				data : params,
//				dataType : 'json' ,
//				success : function ( data ) {
//					var list = data.resultData.resultList;
//					$("#sggList").append('<option value="all">전체</option>');
//					$.each(list, function (index, value){
//						$("#sggList").append('<option value="'+ value.sggCd+ '">' + value.sggNm + '</option>');
//					});
//					
//					if($("#sggList").val() == "all"){
//						$("#emdList").empty();
//						$("#emdList").append('<option value="all">전체</option>');
//					}
//					params["sidoCd"] = "";
//				},
//				error: function (xhr, status, error) {
//					commonUtils.customAlert('처리 중 오류가 발생하였습니다.');
//				}
//			});
//	 }
//	  
//	 //시도 리스트 가져오기 
//	  function getSidoList(){
//		  var url = "/anals/selectSidoList.do";
//			$.ajax({
//				type : "POST" ,
//				url : url ,
//				dataType : 'json' ,
//				success : function ( data ) {
//					var list = data.resultData.resultList;
//					var selectBox = "";
//					$("#lawdDiv").html("");
//					selectBox += '<select id="sidoList" onchange="wavus.anals.getSigunguList(this.value)" style="cursor:pointer">';
//					selectBox += '<option value="all">전국</option>';
//					$.each(list, function (index, value){
//						selectBox += '<option value="'+ value.sidoCd+ '">' + value.sidoNm + '</option>';
//		        	});
//					selectBox += '</select>&nbsp;';
//					selectBox += '<select id="sggList" onchange="wavus.anals.getEmdList(this.value)" style="cursor:pointer">';
//					selectBox += '<option value="all">전체</option>';
//					selectBox += '</select>&nbsp;';
//					selectBox += '</select>';
//					selectBox += '<select id="emdList" style="cursor:pointer">';
//					selectBox += '<option value="all">전체</option>';
//					selectBox += '</select>';
//
//					$("#lawdDiv").html(selectBox);
//				},
//				error: function (xhr, status, error) {
//					commonUtils.customAlert('처리 중 오류가 발생하였습니다.');
//				}
//			});
//	 }
//	//공급유형 리스트 가져오기 
//	  function getSplTpCdList(){
//		  var url = "/anals/selectSplTpCdList.do";
//		  params["lsTpCd"] = $("input[name=searchSpceLsTpCd]:checked").val();
//		  if(params["lsTpCd"] != "00"){
//			  $("#splTpCdTr").show(); 
//		  }else{
//			  $("#splTpCdTr").hide();
//			  return;
//		  }
//		  rdm_loading(true);
//		  if(params["lsTpCd"] != '02'){
//			  $("#splTpCd").attr('title','공급유형');
//		  }else{
//			  $("#splTpCd").attr('title','취득유형');
//		  }
//			$.ajax({
//				type : "POST" ,
//				url : url ,
//				data : params,
//				dataType : 'json' ,
//				async: true,
//				success : function ( data ) {
//					var list = data.resultData.resultList;
//					var selectBox = "";
//					$("#splTpCd").html("");
//					selectBox += '<option value="all">전체</option>';
//					$.each(list, function (index, value){
//						selectBox += '<option value="'+ value.splTpCd+ '">' + value.splTpNm + '</option>';
//		        	});
//					$("#splTpCd").html(selectBox);
//					if(params["lsTpCd"] != "03"){
//						 $("#lohRsdScpoCd").hide();
//						 rdm_loading(false);
//					}else{
//						$("#lohRsdScpoCd").show();
//						getLfstsMvnSclpstCodeList();
//					}
//					    
//				},
//				error: function (xhr, status, error) {
//					rdm_loading(false);
//					commonUtils.customAlert('처리 중 오류가 발생하였습니다.');
//				}
//			});
//			
//	 }
//	//전세임대 임주자신분별 코드 리스트 가져오기 
//	  function getLfstsMvnSclpstCodeList(){
//		  var url = "/anals/selectLfstsMvnSclpstCodeList.do";
//		  
//			$.ajax({
//				type : "POST" ,
//				url : url ,
//				data : params,
//				dataType : 'json' ,
//				//async: true,
//				success : function ( data ) {
//					var list = data.resultData.resultList;
//					var selectBox = "";
//					$("#lohRsdScpoCd").html("");
//					selectBox += '<option value="all">전체</option>';
//					   
//					$.each(list, function (index, value){
//						selectBox += '<option value="'+ value.lohRsdScpoCd+ '">' + value.lohRsdScpoNm + '</option>';
//		        	});
//					$("#lohRsdScpoCd").html(selectBox);
//					rdm_loading(false);
//				},
//				error: function (xhr, status, error) {
//					rdm_loading(false);
//					commonUtils.customAlert('처리 중 오류가 발생하였습니다.');
//				}
//			});
//	 }
//	//실거래가 년도 리스트 가져오기 
//	  function getRtmsYearList(){
//		  rdm_loading(true);
//		  var url = "/anals/selectRtmsYearList.do";
//		  params["realDlpc"] = $("#realDlpc").val();
//			$.ajax({
//				type : "POST" ,
//				url : url ,
//				data : params,
//				dataType : 'json' ,
//				async: true,
//				success : function ( data ) {
//					var list = data.resultData.resultList;
//					var selectBox = "";
//					$("#searchRtmsYears").html("");
//					$.each(list, function (index, value){
//						selectBox += '<option value="'+ value.accYear+ '">' + value.accYear + '</option>';
//		        	});
//					$("#searchRtmsYears").html(selectBox);
//					rdm_loading(false);
//				},
//				error: function (xhr, status, error) {
//					rdm_loading(false);
//					commonUtils.customAlert('처리 중 오류가 발생하였습니다.');
//				}
//			});
//	 }
//	  //내관심지역 리스트 가져오기 
//	  function getGlanAreaList(){
//		  rdm_loading(true);
//		  var url = "/anals/selectGlanAreaList.do";
//		  
//			$.ajax({
//				type : "POST" ,
//				url : url ,
//				data : params,
//				dataType : 'json' ,
//				async: true,
//				success : function ( data ) {
//					var msg = data.resultData.msg;
//					var list = data.resultData.resultList;
//					var selectBox = "";
//					if(msg == ""){	
//						if(list.length > 0){
//							$("#glanDiv").html("");
//							selectBox += '<select id="analsGlanList" onchange="wavus.anals.getGlanAreaInfo(this.value)" style="cursor:pointer">';
//							selectBox += '<option value="">선택하십시오.</option>';
//							$.each(list, function (index, value){
//								selectBox += '<option value="'+ value.glanId+ '">' + value.glanNm + '</option>';
//				        	});
//							selectBox += '</select>';
//							$("#glanDiv").html(selectBox);
//							rdm_loading(false);
//						}else{
//							$("#glanDiv").html("");
//							selectBox += '<select id="analsGlanList" onchange="wavus.anals.getGlanAreaInfo(this.value)" style="cursor:pointer">';
//							selectBox += '<option value="N">등록된 관심지역이 없습니다.</option>';
//							selectBox += '</select>';
//							$("#glanDiv").html(selectBox);
//							rdm_loading(false);
//							return;
//						}
//					}else{
//						rdm_loading(false);
//						commonUtils.customAlert(msg);
//						$("#indictGbnTr").hide(); 
//						return;
//					}
//				},
//				error: function (xhr, status, error) {
//					rdm_loading(false);
//					$("#indictGbnTr").hide(); 
//					commonUtils.customAlert('처리 중 오류가 발생하였습니다.');
//				}
//			});
//	 }
//	//공간분석 기법 선택시 실거래가 비교 컨트롤
//	function fnAnalsSelectChange(){
//		 if($("#analsSelect").val() != "01"){
//			  $("#realDlpcLi").hide();
//			  $("#rdmOpacityTr").hide();
//			  if($("#analsSelect").val() == "02"){
//				  $("#ltceIntrvlTr").show();
//				  $("#ltceColorTr").show();
//			  }else{
//				  $("#ltceIntrvlTr").hide();
//				  $("#ltceColorTr").hide();
//			  }
//		 }else{
//			  $("#ltceIntrvlTr").hide();
//		      $("#realDlpcLi").show();
//		      $("#ltceColorTr").hide();
//		      $("#rdmOpacityTr").show();
//		 }
//		 fnRealDlpcCtl();
//	 }
//	
//	 //표시기법 컨트롤
//	 function fnIndictGbnChange(){
//		 if($("input[name=indictGbn]:checked").val() == "01"){
//			 $("#indictGbnTr").show();
//			 $("#lawdDiv").show();
//			 $("#indictNm").text("행정구역");
//			 $("#glanDiv").hide();
//			 $("#ltceWidth").val(150);
//			 $("#ltceHeight").val(150);
//			 $("#btn-add").show();
//		}else if($("input[name=indictGbn]:checked").val() == "02"){
//			 getGlanAreaList();
//			 $("#indictGbnTr").show();
//			 $("#lawdDiv").hide();
//			 $("#indictNm").text("내관심지역");
//			 $("#glanDiv").show();
//			 $("#ltceWidth").val(50);
//			 $("#ltceHeight").val(50);
//			 $("#btn-add").hide();
//		 }else if($("input[name=indictGbn]:checked").val() == "03"){
//			 $("#indictGbnTr").hide();
//			 $("#lawdDiv").hide(); 
//			 $("#glanDiv").hide();
//			 $("#ltceWidth").val(50);
//			 $("#ltceHeight").val(50);
//			 $("#btn-add").hide();
//		 }else if($("input[name=indictGbn]:checked").val() == "04"){
//			 $("#indictGbnTr").hide();
//			 $("#lawdDiv").hide(); 
//			 $("#glanDiv").hide();
//			 $("#ltceWidth").val(50);
//			 $("#ltceHeight").val(50);
//			 $("#btn-add").hide();
//		}
//	 }
//	 
//	//실거래가 컨트롤(매매, 전월세 선택시)
//	 function fnRealDlpcChange(){
//		if($("#analsSelect").val() == "01"){ 
//			if($("#realDlpc").val() == "00"){
//				 $("#realDlpcChkSpan").hide();
//				 $("#rtmsOpacityTr").hide();
//				 $("#rtmsColorTr").hide();
//				 $("#rtmsYearsTr").hide();
//			}else { 
//				 getRtmsYearList();
//				 $("#realDlpcChkSpan").show();
//				 $("#rtmsOpacityTr").show();
//				 $("#rtmsColorTr").show();
//				 $("#rtmsYearsTr").show();
//			}
//		}else{
//			 $("#realDlpcChkSpan").hide();
//			 $("#rtmsOpacityTr").hide();
//			 $("#rtmsColorTr").hide();
//			 $("#rtmsYearsTr").hide();
//		}
//	 }
//	 
//	//실거래가 컨트롤(공간분석기법 클릭 시)
//	 function fnRealDlpcCtl(){
//		if($("#analsSelect").val() == "01"){ 
//			if($("#realDlpc").val() == "00"){
//				 $("#realDlpcChkSpan").hide();
//				 $("#rtmsOpacityTr").hide();
//				 $("#rtmsColorTr").hide();
//				 $("#rtmsYearsTr").hide();
//			}else { 
//				 $("#realDlpcChkSpan").show();
//				 $("#rtmsOpacityTr").show();
//				 $("#rtmsColorTr").show();
//				 $("#rtmsYearsTr").show();
//			}
//		}else{
//			 $("#realDlpcChkSpan").hide();
//			 $("#rtmsOpacityTr").hide();
//			 $("#rtmsColorTr").hide();
//			 $("#rtmsYearsTr").hide();
//		}
//	 }
//	 //히트맵 클릭
//	 function fnHeatMapClick(){
//		 $("#analsSelect").val('01');
//		 fnAnalsSelectChange();
//	 }
//	 //격자분석 클릭
//	 function fnLtceAnalsClick(){
//		 $("#analsSelect").val('02');
//		 fnAnalsSelectChange();	
//	 }
//	 //클러스터 클릭
//	 function fnClusterClick(){
//		 $("#analsSelect").val('03');
//		 fnAnalsSelectChange();	
//	 }
//	
//	 //내관심지역 정보 가져오기 
//	  function getGlanAreaInfo(glanId){
//		  if(glanId == ""){
//			  return;
//		  }
//		  rdm_loading(true);
//		  map.getLayers().forEach(function(layer){
//				var layerName =  layer.get('name') + '';
//				if(layerName == 'userGlanLayer'){
//					map.removeLayer(layer);
//				}
//			});
//		  
//		  var url = "/anals/selectGlanAreaInfo.do";
//		  params["glanId"] = glanId;
//			$.ajax({
//				type : "POST" ,
//				url : url ,
//				data : params,
//				dataType : 'json' ,
//				async: true,
//				success : function ( data ) {
//					var result = data.resultData.result;
//					var coord = data.resultData.resultCoord;
//					
//					var sggCd = result.sggCd;
//					var bjdCd = result.bjdCd;
//					
//					if(sggCd == 'all' && bjdCd == 'all'){
//						$("#ltceWidth").val(50);
//						$("#ltceHeight").val(50);
//					}else{
//						$("#ltceWidth").val(150);
//						$("#ltceHeight").val(150);
//					}
//					
//					fnGlanAreaDraw(result, coord);
//				},
//				error: function (xhr, status, error) {
//					rdm_loading(false);
//					commonUtils.customAlert('처리 중 오류가 발생하였습니다.');
//				}
//			});
//	 }
//	//내관심지역 영역그리기
//	function fnGlanAreaDraw(result, coord){
//		  var vectorSource = new ol.source.Vector();
//		  var vector = new ol.layer.Vector({
//			  	name : 'userGlanLayer',
//				layerId: 'boundary',
//				source: vectorSource,
//				zIndex : 90,
//				style: new ol.style.Style({
//					stroke: new ol.style.Stroke({
//						color: 'rgba(255, 0, 0, 1.0)',
//						lineDash: [5,5,.1,5],
//						width: 3
//					})
//				})
//		  });
//		map = wavus.map.getMap();	
//		var coordinates = coord;
//		var coordinates2 = result.geom.replace("MULTIPOLYGON", "").replace(/\(/gi, "").replace(/\)/gi, "");
//
//		var geojsonObject = {
//				'type' : 'FeatureCollection',
//				'crs' : {
//					'type' : 'name',
//					'properties' : {
//						'name' : 'EPSG:5147'
//					}
//				},
//				'features' : [{
//					'type' : 'Feature',
//					'geometry' : {
//						'type' : 'MultiPolygon',
//						'coordinates' : [coordinates]
//					}
//				}]
//			};
//			
//			var features = (new ol.format.GeoJSON()).readFeatures(geojsonObject);
//			vectorSource.addFeatures(features);
//			
//		    if(vectorLayer != null){
//		    	removeLayer();
//		    }
//
//		    map.addLayer(vector);
//		    vectorLayer = vector;
//		    var extent = vectorSource.getExtent();
//		    var center = ol.extent.getCenter(extent);
//		    map.getView().fit(extent);
//		params["xmin"] = result.xmin;
//		params["xmax"] = result.xmax; 
//		params["ymin"] = result.ymin;
//		params["ymax"] = result.ymax; 
//		params["geom"] = coordinates2;
//		rdm_loading(false);
//		
//	}
//	
//	//임대주택 분석기법 실행  
//    function fnAnalsExec(){
//
//      //반경내일 경우 우선 실행
//      if($("input[name=indictGbn]:checked").val() == "03"){
//   		  	fnRdusCircleDraw();
//   		  	return;
//   	  }
//      if($("input[name=indictGbn]:checked").val() == "02"){
//	      if($("#analsGlanList").val() == undefined){
//			  commonUtils.customAlert('로그인을 하십시오.');
//			  return;
//		  }else if($("#analsGlanList").val() == "N"){
//			  commonUtils.customAlert('내관심지역을 등록 후 사용하십시오.');
//			  return;
//		  }
//      }
//      
//    	var innerOption={
//				title : ''
//				,message : '공간분석을 수행 하시겠습니까? (단, 공간분석 수행시 선택한 지역에 따라 시간이 많이 소요될 수 있습니다.)'
//				,callBackY : function(){fnGoAnalsExec();}
//				,callBackN : function(){}
//				,buttonLabel : {
//					ok : ''
//					,cancel : ''
//				}
//				,transition:'fade'
//				,closable:false
//			}
//    	//확인창 수행
//    	commonUtils.confirm(innerOption);
//    }
//	
//	//임대주택 분석기법 실행  
//    function fnGoAnalsExec(){
//    	 var url = "";
//			wavus.map.getMap().getLayers().forEach(function(layer){
//				var layerName =  layer.get('name') + '';
//
//				if(layerName == 'searchAdd_boundary'){
//					wavus.map.getMap().removeLayer(layer);
//				}
//			});//주소검색 아이콘 초기화
//    	 map = wavus.map.getMap();
//    	 var lawdCd = "";
//    	  
//    	 if($("input[name=indictGbn]:checked").val() == "03"){
//    		  fnRdusCircleDraw();
//    		  return;
//    	  }
//    	 
//         if($("input[name=indictGbn]:checked").val() == "02"){
//   	      if($("#analsGlanList").val() == undefined){
//   			  commonUtils.customAlert('로그인을 하십시오.');
//   			  return;
//   		  }else if($("#analsGlanList").val() == "N"){
//   			  commonUtils.customAlert('내관심지역을 등록 후 사용하십시오.');
//   			  return;
//   		  }
//         }
//    	 
//    	 if($("input[name=indictGbn]:checked").val() == "01" || $("input[name=indictGbn]:checked").val() == "04"){
//	    	  if($("input[name=indictGbn]:checked").val() == "01" && $("#sidoList").val() == "all"){
//	    		   commonUtils.customAlert('전국으로 선택 시 공간분석이 어렵습니다.');
//	    		   return;
//	    	   }else if($("input[name=indictGbn]:checked").val() == "01" && $("#sggList").val() == "all"){
//	    		   commonUtils.customAlert('시도로 선택 시 공간분석이 어렵습니다.');
//	    		   return;
//	    	   }else{
//	    		   if($("input[name=indictGbn]:checked").val() == "04"){
//	    			    if(map.getView().getZoom() < 16){
//	    					commonUtils.customAlert('선택 한 범위가 큽니다. 지도를 확대하여 주십시오.'); 
//	    					return;
//	    				}
//	    			    params["geom"] = getScrinCoord();
//	    			    params["xmin"] = map.getView().calculateExtent(map.getSize())[0];
//	    				params["ymin"] = map.getView().calculateExtent(map.getSize())[1]; 
//	    				params["xmax"] = map.getView().calculateExtent(map.getSize())[2];
//	    				params["ymax"] = map.getView().calculateExtent(map.getSize())[3]; 
//	    		   }
//	    		   
//	    		   if($("#analsSelect").val() == '02'){
//	    			   if($.trim($("#ltceWidth").val()) == '' || $.trim($("#ltceHeight").val()) == ''){
//	    				   commonUtils.customAlert('격자분석의 간격을 입력하십시오.');
//	    	    		   return;
//	    			   }else if($("#ltceWidth").val() < 30 || $("#ltceWidth").val() > 200){
//	    				   commonUtils.customAlert('너비는 30에서 200사이로 선택하십시오.');
//	    	    		   return;
//	    			   }else if($("#ltceHeight").val() < 30 || $("#ltceHeight").val() > 200){
//	    				   commonUtils.customAlert('높이는 30에서 200사이로 선택하십시오.');
//	    	    		   return;
//	    			   }
//	    		   }
//		    		   
//
//	    		   fnInit();
//	    		   if($("#emdList").val() == "all"){
//	    			   params["lawdCd"] = $("#sggList").val();
//	    			   params["sggCd"] = $("#sggList").val();
//	    			   lawdCd = params["sggCd"];
//	    			   
//	    			}else{
//	    			   params["lawdCd"] = $("#emdList").val().substring(0,8);
//	    			   params["emdCd"] = $("#emdList").val().substring(0,8);
//	    			   lawdCd = params["emdCd"];
//	    			}
//	    		   
//	    		   
//	    		   params["indictGbn"] = $("input[name=indictGbn]:checked").val();
//	    		   params["lsTpCd"] = $("input[name=searchSpceLsTpCd]:checked").val();
//	    		   params["splTpCd"] = params["lsTpCd"] == "00"?"all":$("#splTpCd").val();
//	    		   params["stDdoAr"] = $("#stDdoAr").val(); 
//	    		   params["edDdoAr"] = $("#edDdoAr").val();
//	    		   params["realDlpc"] = $("#realDlpc").val();
//	    		   params["ltceWidth"] = $("#ltceWidth").val();
//	    		   params["ltceHeight"] = $("#ltceHeight").val();
//	    		   params["accYear"] = $("#searchRtmsYears").val();
//	    		   params["lohRsdScpoCd"] = $("#lohRsdScpoCd").val();
//	    		   params["glanId"] = $("#analsGlanList").val();
//	    		   params["empNo"] = $("#empNo").val(); 
//	    		}
//    	  }else{
//    		  if($("#analsGlanList").val() == ""){
//    			  commonUtils.customAlert('내관심지역을 선택하여 주십시오.');
//    			  return;
//    		  }
//    		  
//    		  if($("#analsSelect").val() == '02'){
//	   			   if($.trim($("#ltceWidth").val()) == '' || $.trim($("#ltceHeight").val()) == ''){
//	   				   commonUtils.customAlert('격자분석의 간격을 입력하십시오.');
//	   	    		   return;
//	   			   }else if($("#ltceWidth").val() < 30 || $("#ltceWidth").val() > 200){
//	   				   commonUtils.customAlert('너비는 30에서 200사이로 선택하십시오.');
//	   	    		   return;
//	   			   }else if($("#ltceHeight").val() < 30 || $("#ltceHeight").val() > 200){
//	   				   commonUtils.customAlert('높이는 30에서 200사이로 선택하십시오.');
//	   	    		   return;
//	   			   }
//   		       }
//
//    		  for(var i=0;i<3;i++){
//   			      removeGlanLayer();
//   		      }
//    		  params["indictGbn"] = $("input[name=indictGbn]:checked").val();
//   		      params["lsTpCd"] = $("input[name=searchSpceLsTpCd]:checked").val();
//   		      params["splTpCd"] =  params["lsTpCd"] == "00"?"all":$("#splTpCd").val();
//   		      params["stDdoAr"] = $("#stDdoAr").val(); 
//   		      params["edDdoAr"] = $("#edDdoAr").val();
//   		      params["realDlpc"] = $("#realDlpc").val();
//   		      params["ltceWidth"] = $("#ltceWidth").val();
//		      params["ltceHeight"] = $("#ltceHeight").val();
//		      params["accYear"] = $("#searchRtmsYears").val();
//		      params["lohRsdScpoCd"] = $("#lohRsdScpoCd").val();
//		      params["glanId"] = $("#analsGlanList").val();
//		      params["empNo"] = $("#empNo").val(); 
//    	  }
//    	  
//    	  
//    	  if($("#analsSelect").val() == "01" || $("#analsSelect").val() == "03"){
//    		  url = "/anals/selectRthousInfoList.do";
//    	  }else if($("#analsSelect").val() == "02"){
//    		  url = "/anals/selectLtceAnalsList.do";
//    	  }
//    	  
//    	  if($("#analsSelect").val() == "02"){
//    	    	var innerOption={
//    					title : ''
//    					,message : '선택하신 지역에 따라 1~2분의 시간이 많이 소요될 수 있습니다. 격자분석을 수행 하시겠습니까?'
//    					,callBackY : function(){
//    						rdm_loading(true);
//    						$.ajax({
//    			    			type : "POST" ,
//    			    			data : params,
//    			    			url : url ,
//    			    			dataType : 'json' ,
//    			    			async: true,
//    			    			success : function ( data ) {
//    			 	    		  
//    			    				var list = data.resultData.resultList;
//    			    				if(list.length < 1){
//    			    					rdm_loading(false);
//    			    					commonUtils.customAlert('분석결과가 없습니다.');
//    			    					return;
//    			    				}
//
//    			    				var listAll = data.resultData.resultAllList;
//    			    				fnLtceAnalsAllExec('userLawdAllLtceAnalsLayer',listAll);
//    			    				fnLtceAnalsExec('userLawdLtceAnalsLayer',list);
//    			    			
//    			    				var listExtent = data.resultData.resultExtent;
//    			    				selectRthousInfoList = new Array(); //임대주택정보 리스트 초기화
//    			    				selectRealDlpcList = new Array(); //실거래가 리스트 초기화
//    			    				selectRthousInfoList = list;
//    			    				
//    			    				if($("#analsSelect").val() == "01" && $("#realDlpc").val() != "00"){ //실거래가 비교를 선택한 경우
//    			    					var realList = data.resultData.resultRealList;
//    			    					if(realList == null || realList == undefined || realList.length == 0){
//    			    						params["lawdCd"] = '';
//    			    	    				params["sggCd"] = '';
//    			    	    				params["emdCd"] = '';
//    			    	    				params["lsTpCd"] = '';
//    			    	    				rdm_loading(false);
//    			    	    				commonUtils.customAlert('실거래가 정보가 없습니다.');
//    			    	    				return;
//    			    					}
//    			    					fnRealDlpcHeatMapAnals('userRealDplcHeatMapLayer', realList); //실거래가 히트맵 실행
//    			    					selectRealDlpcList = realList;
//    			    					$("#realDlpcChk").prop("checked",true);
//    			    				}
//    			    				if($("input[name=indictGbn]:checked").val() == "01"){
//    			    					 coordMove(lawdCd, listExtent, $("#analsSelect").val()); //선택한 행정구역으로 이동
//    			    				}
//    			    				params["lawdCd"] = '';
//    			    				params["sggCd"] = '';
//    			    				params["emdCd"] = '';
//    			    				params["lsTpCd"] = '';
//    			    				rdm_loading(false);
//    			    			},
//    			    			error: function (xhr, status, error) {
//    			    				rdm_loading(false);
//    			    				params["lawdCd"] = '';
//    			    				params["sggCd"] = '';
//    			    				params["emdCd"] = '';
//    			    				params["lsTpCd"] = '';
//    			    				commonUtils.customAlert('처리 중 오류가 발생하였습니다.');
//    			    			}
//    			    		});	
//    					}
//    					,callBackN : function(){}
//    					,buttonLabel : {
//    						ok : ''
//    						,cancel : ''
//    					}
//    					,transition:'fade'
//    					,closable:false
//    				}
//    	    	//확인창 수행
//    	    	commonUtils.confirm(innerOption);
//    	  }else{
//    		  rdm_loading(true);
//    	   $.ajax({
//    			type : "POST" ,
//    			data : params,
//    			url : url ,
//    			dataType : 'json' ,
//    			async: true,
//    			success : function ( data ) {
// 	    		 
//    				var list = data.resultData.resultList;
//    				if(list.length < 1){
//    					rdm_loading(false);
//    					commonUtils.customAlert('분석결과가 없습니다.');
//    					return;
//    				}
//    				
//    				if($("#analsSelect").val() == "01"){ //히트맵
//    					fnHeatMapAnalsExec('userHeatMapLayer',list);
//    				}else if($("#analsSelect").val() == "02"){//격자분석
//    					var listAll = data.resultData.resultAllList;
//    					fnLtceAnalsAllExec('userLawdAllLtceAnalsLayer',listAll);
//    					fnLtceAnalsExec('userLawdLtceAnalsLayer',list);
//    				}else if($("#analsSelect").val() == "03"){//클러스터
//    					//fnClusterAnalsExec('userClusterLayer', list);
//    					fnAllClusterAnalsExec('userClusterLayer', list);
//    				}
//    				selectRthousInfoList = new Array(); //임대주택정보 리스트 초기화
//    				selectRealDlpcList = new Array(); //실거래가 리스트 초기화
//    				selectRthousInfoList = list;
//    				
//    				if($("#analsSelect").val() == "01" && $("#realDlpc").val() != "00"){ //실거래가 비교를 선택한 경우
//    					var realList = data.resultData.resultRealList;
//    					if(realList == null || realList == undefined || realList.length == 0){
//    						params["lawdCd"] = '';
//    	    				params["sggCd"] = '';
//    	    				params["emdCd"] = '';
//    	    				params["lsTpCd"] = '';
//    	    				rdm_loading(false);
//    	    				commonUtils.customAlert('실거래가 정보가 없습니다.');
//    	    				return;
//    					}
//    					fnRealDlpcHeatMapAnals('userRealDplcHeatMapLayer', realList); //실거래가 히트맵 실행
//    					selectRealDlpcList = realList;
//    					$("#realDlpcChk").prop("checked",true);
//    				}
//    				if($("input[name=indictGbn]:checked").val() == "01"){
//    					 coordMove(lawdCd, list, $("#analsSelect").val()); //선택한 행정구역으로 이동
//    				}
//    				params["lawdCd"] = '';
//    				params["sggCd"] = '';
//    				params["emdCd"] = '';
//    				params["lsTpCd"] = '';
//    				rdm_loading(false);
//    			},
//    			error: function (xhr, status, error) {
//    				rdm_loading(false);
//    				params["lawdCd"] = '';
//    				params["sggCd"] = '';
//    				params["emdCd"] = '';
//    				params["lsTpCd"] = '';
//    				commonUtils.customAlert('처리 중 오류가 발생하였습니다.');
//    			}
//    		});	
//    	  }
//	}
//    //임대주택 분석기법 실행(관심지역 및 반경내)  
//    function fnDrawAnalsExec(){
//    	   params["indictGbn"] = $("input[name=indictGbn]:checked").val();
//		   params["lsTpCd"] = $("input[name=searchSpceLsTpCd]:checked").val();
//		   params["splTpCd"] = params["lsTpCd"] == "00"?"all":$("#splTpCd").val();
//		   params["stDdoAr"] = $("#stDdoAr").val(); 
//		   params["edDdoAr"] = $("#edDdoAr").val();
//		   params["realDlpc"] = $("#realDlpc").val();
//		   params["ltceWidth"] = $("#ltceWidth").val();
//		   params["ltceHeight"] = $("#ltceHeight").val();
//		   params["accYear"] = $("#searchRtmsYears").val();
//		   params["lohRsdScpoCd"] = $("#lohRsdScpoCd").val();
//		   params["empNo"] = $("#empNo").val(); 
//		   
//		   if($("#analsSelect").val() == "01" || $("#analsSelect").val() == "03"){
//	    	    url = "/anals/selectRthousInfoList.do";
//	       }else if($("#analsSelect").val() == "02"){
//	    		url = "/anals/selectLtceAnalsList.do";
//	       }
//		   rdm_loading(true);
//    	  $.ajax({
//  			type : "POST" ,
//  			data : params,
//  			url : url ,
//  			dataType : 'json' ,
//  			async: true,
//  			success : function ( data ) {
//  				
//  				var list = data.resultData.resultList;
//  				if(list.length < 1){
//  					rdm_loading(false);
//  					wavus.draw.clearDraw();
//  					radiusDiv.setPosition(undefined);
//  					commonUtils.customAlert('분석결과가 없습니다.');
//  					return;
//  				}
//  				
//  				if($("#analsSelect").val() == "01"){ //히트맵
//  					fnHeatMapAnalsExec('userHeatMapLayer',list);
//  				}else if($("#analsSelect").val() == "02"){//격자분석
//  					var listAll = data.resultData.resultAllList;
//  					fnLtceAnalsAllExec('userLawdAllLtceAnalsLayer',listAll);
//  					fnLtceAnalsExec('userLawdLtceAnalsLayer',list);
//  				}else if($("#analsSelect").val() == "03"){//클러스터
//  					//fnClusterAnalsExec('userClusterLayer', list);
//  					fnAllClusterAnalsExec('userClusterLayer', list);
//  				}
//  				selectRthousInfoList = new Array(); //임대주택정보 리스트 초기화
//  				selectRealDlpcList = new Array(); //실거래가 리스트 초기화
//  				selectRthousInfoList = list;
//  				
//  				if($("#analsSelect").val() == "01" && $("#realDlpc").val() != "00"){ //실거래가 비교를 선택한 경우
//  					var realList = data.resultData.resultRealList;
//  					fnRealDlpcHeatMapAnals('userRealDplcHeatMapLayer', realList); //실거래가 히트맵 실행
//  					selectRealDlpcList = realList;
//  					$("#realDlpcChk").prop("checked",true);
//  				}
//  				
//  				params["lsTpCd"] = '';
//  				rdm_loading(false);
//  			},
//  			error: function (xhr, status, error) {
//  				rdm_loading(false);
//  				params["lsTpCd"] = '';
//  				wavus.draw.clearDraw();
//  				commonUtils.customAlert('처리 중 오류가 발생하였습니다.');
//  			}
//  		});	
//    	
//    }
//   //임대주택 정보 엑셀다운로드  
//    function fnExcelDownLoad(){
//    	
//    	var innerOption={
//				title : ''
//				,message : '공간분석을 수행한 속성정보를 엑셀 다운로드 하시겠습니까?\n(단, 선택한 지역에 따라 다운로드 소요시간이 길어 질 수 있습니다.)'
//				,callBackY : function(){fnGoExcelDownLoad();}
//				,callBackN : function(){}
//				,buttonLabel : {
//					ok : ''
//					,cancel : ''
//				}
//				,transition:'fade'
//				,closable:false
//			}
//    	//확인창 수행
//    	commonUtils.confirm(innerOption);
//    }
//    //임대주택 정보 엑셀다운로드  확인 시 수행
//    function fnGoExcelDownLoad(){
//    	if(selectRthousInfoList != null && selectRthousInfoList.length > 0){
//    		$excelDown("/anals/excelDownLoad.do",selectRthousInfoList, selectRealDlpcList); //엑셀 다운로드 수행
//    		fnExcelDownLoadLog();
//    	}else{
//    		commonUtils.customAlert('분석수행한 결과값이 없어서 다운로드를 할 수 없습니다.');
//    		return;
//    	}
//    }
//    
//     //엑셀다운 실행
//     $excelDown = function(url, dataList, realDataList){
//    	
//    	var $excelForm = $('<form></form>');
//    	
//    	$excelForm.append($("<input type='hidden' name='lsTpCd' id='lsTpCd' value='"+$("input[name=searchSpceLsTpCd]:checked").val()+"' />"));
//    	if($("input[name=searchSpceLsTpCd]:checked").val() == '03'){
//    		for(var i=0;i<dataList.length;i++){
//	    		 var ddoAr = dataList[i].ddoAr + "㎡";
//	    		 var data = dataList[i].juso + "!" + ddoAr + "!"+ dataList[i].lsTpNm + "!" + dataList[i].splTpNm + "!" + dataList[i].lohRsdScpoNm;
//	    		 var paramTag = $("<input type='hidden' name='dataList["+i +"].data" + "' id='dataList["+i+"].data"+"' value='"+data+"' />");
//	    		 $excelForm.append(paramTag);
//	    	 }
//    	}else{
//    		for(var i=0;i<dataList.length;i++){
//	    		 var ddoAr = dataList[i].ddoAr + "㎡";
//	    		 var data = dataList[i].juso + "!" + ddoAr + "!"+ dataList[i].lsTpNm + "!" + dataList[i].splTpNm;
//	    		 var paramTag = $("<input type='hidden' name='dataList["+i +"].data" + "' id='dataList["+i+"].data"+"' value='"+data+"' />");
//	    		 $excelForm.append(paramTag);
//	    	 }
//    	}
//        if(realDataList.length > 0){
//		        for(var i=0;i<realDataList.length;i++){
//		    		 var ddoAr = realDataList[i].bldgArea + "㎡";
//		    		 var data = realDataList[i].juso + "!" + ddoAr + "!"+ realDataList[i].dealGbnNm + "!" + realDataList[i].rt011Nm;
//		    		 realParamTag = $("<input type='hidden' name='realDataList["+i +"].data" + "' id='realDataList["+i+"].data"+"' value='"+data+"' />");
//		    		 $excelForm.append(realParamTag);
//		    	}
//        }
//    	$excelForm.attr('action',url);
//    	$("body").append($excelForm);
//    	$excelForm.attr('method','post');
//    	$excelForm.submit();
//    	$excelForm.remove();
//    	
//    }
//    
//	//임대주택 히트맵 분석기법 실행  
//	function fnHeatMapAnalsExec(layerNm, list){
//		
//			map = wavus.map.getMap();
//			var features = new Array(list.length);
//			var _radius = radius || 10;
//			var _blur = blur || 50;
//			
//		    for(var i=0;i<list.length;i++){
//		    	
//		    	var coordinates = [list[i].xAxis, list[i].yAxis];
//		    	features[i] = new ol.Feature(new ol.geom.Point(coordinates)); 
//		    }
//		    
//		    heatmapLayer = new ol.layer.Heatmap({
//				source : new ol.source.Vector({
//					 features : features
//				}),
//				blur : _blur,
//				radius : _radius,
//				zIndex : 99,
//				opacity: parseFloat($("#rdmOpacity").val(),10),
//				weight : function(feature){
//					var magnitude = parseFloat(feature.get('magnitude'));
//					return magnitude -5;
//				}
//			});
//		    map.addLayer(heatmapLayer);
//		    heatmapLayer.set('name', layerNm);
//	}
//	
//	//임대주택 격자분석 실행(전체)
//	function fnLtceAnalsAllExec(layerNm,list)
//	{
//		geojsonObject = {
//				"type" : "FeatureCollection" ,
//				"features" : []
//			};
//		$.each(list, function (index, value){
//			var mCoorXY = value.geom.replace("POLYGON", "").replace(/\(/gi, "").replace(/\)/gi, "");
//			var coorXY = "[[" +  mCoorXY.replace(/,/g, "],[").replace(/ /g, ",") + "]]";
//			var polygon_coor = JSON.parse(coorXY);
//			var poly = {
//					"type" : "Feature" ,
//					"geometry" : {
//						'type' : 'Polygon', 
//						'coordinates' : [polygon_coor]
//					}
//			};
//			geojsonObject["features"].push(poly);
//			
//		});
//		
//        var ltceAllStyle =  new ol.style.Style({
//			stroke : new ol.style.Stroke({
//				 color: '#'+$('#bordrFillColorHide').val(),
//				 width: 1
//			}),
//			fill: new ol.style.Fill({
//				color: 'rgba(255,255,255,0.1)' //흰색
//			})
//		});
//		var vectorSource = new ol.source.Vector();
//		ltceAnalsAllLayer = new ol.layer.Vector({
//			source : vectorSource,
//			zIndex : 100,
//			style : ltceAllStyle
//		});
//		ltceAnalsAllLayer.getSource().addFeatures ((new ol.format.GeoJSON()).readFeatures(geojsonObject));
//		ltceAnalsAllLayer.set('name', layerNm);
//		map.addLayer(ltceAnalsAllLayer);
//	}
//	
//	//임대주택 격자분석 실행(해당영역)
//	function fnLtceAnalsExec(layerNm,list)
//	{
//		geojsonObject = {
//				"type" : "FeatureCollection" ,
//				"features" : []
//			};
//		$.each(list, function (index, value){
//			
//			var mCoorXY = value.geom.replace("POLYGON", "").replace(/\(/gi, "").replace(/\)/gi, "");
//			var coorXY = "[[" +  mCoorXY.replace(/,/g, "],[").replace(/ /g, ",") + "]]";
//			var polygon_coor = JSON.parse(coorXY);
//			var poly = {
//					"type" : "Feature" ,
//					"geometry" : {
//						'type' : 'Polygon', 
//						'coordinates' : [polygon_coor]
//					}
//			};
//			geojsonObject["features"].push(poly);
//			
//		});
//		fnLtceColor();
//		
//		var ltceStyle =  new ol.style.Style({
//			   stroke : new ol.style.Stroke({
//					 color: ltceStroke,
//					 width: 0.1
//				}),
//				fill: new ol.style.Fill({
//					color: ltceFill 
//				})
//			});
//		
//		var vectorSource = new ol.source.Vector();
//		ltceAnalsLayer = new ol.layer.Vector({
//			source : vectorSource,
//			zIndex : 99,
//			style : ltceStyle
//		});
//		ltceAnalsLayer.getSource().addFeatures ((new ol.format.GeoJSON()).readFeatures(geojsonObject));
//		ltceAnalsLayer.set('name', layerNm);
//		map.addLayer(ltceAnalsLayer);
//	}	
//	//임대주택 정보 클러스터 분석
//	function fnAllClusterAnalsExec(layerNm, list)
//	{
//		var rsList01 = [];
//		var rsList02 = [];
//		var rsList03 = [];
//		var rsList04 = [];
//		for(var i=0;i<list.length;i++){
//	    	var lsTpCd =  list[i].lsTpCd;
//	    	if(lsTpCd == "01"){
//	    		rsList01.push(list[i]);
//	    	}else if(lsTpCd == "02"){
//	    		rsList02.push(list[i]);
//	    	}else if(lsTpCd == "03"){
//	    		rsList03.push(list[i]);
//	    	}else if(lsTpCd == "04"){
//	    		rsList04.push(list[i]);
//	    	}
//	    }
//		fnClusterAnalsExec(layerNm+"_01",rsList01);
//		fnClusterAnalsExec(layerNm+"_02",rsList02);
//		fnClusterAnalsExec(layerNm+"_03",rsList03);
//		fnClusterAnalsExec(layerNm+"_04",rsList04);
//	}
//	//임대주택 정보 클러스터 분석
//	function fnClusterAnalsExec(layerNm, list)
//	{
//		map = wavus.map.getMap();
//		wavus.layer.removeLayer(map,'hsmpLcClusterLayer');
//		//스타일 설정
//		var clusterColor = "";
//		if($("input[name=searchSpceLsTpCd]:checked").val() == '01'){
//			clusterColor = "#1080BE";
//		}else if($("input[name=searchSpceLsTpCd]:checked").val() == '02'){
//			clusterColor = "#8D378F";
//		}else if($("input[name=searchSpceLsTpCd]:checked").val() == '03'){
//			clusterColor = "#FD5151";
//		}else if($("input[name=searchSpceLsTpCd]:checked").val() == '04'){
//			clusterColor = "#57B637";
//		}
//		
//		var cfeature = new Array(list.length);
//		for(var i=0;i<list.length;i++){
//	    	var coordinates = [list[i].xAxis, list[i].yAxis];
//	    	var juso =  list[i].juso;
//	    	var ddoAr =  list[i].ddoAr;
//	    	var lsTpCd =  list[i].lsTpCd;
//	    	var lsTpNm = list[i].lsTpNm;
//	    	cfeature[i] = new ol.Feature({
//	    		geometry: new ol.geom.Point(coordinates),
//	    		name: juso,
//	    		ddoAr : ddoAr+"㎡",
//	    		lsTpCd : lsTpCd,
//	    		lsTpNm : lsTpNm
//	    	});
//	    }
//		var source = new ol.source.Vector({
//			features : cfeature
//		});
//		
//		var clusterSource = new ol.source.Cluster({
//			  distance : 40,
//			  source : source
//		});
//		
//		var styleCache = {};
//		clusterLayer = new ol.layer.Vector({
//			source : clusterSource,
//			zIndex : 99,
//			opacity : 0.9,
//			style : function(feature){
//				var size  = feature.get('features').length;
//				if($("input[name=searchSpceLsTpCd]:checked").val() == '00'){
//					switch(getClusterLsTpCd(feature.get("features")[0])){
//						case "01":	
//							clusterColor = "#1080BE";	
//							break;
//						case "02":	
//							clusterColor = "#8D378F";	
//							break;
//						case "03":	
//							clusterColor = "#FD5151";	
//							break;
//						case "04":	
//							clusterColor = "#57B637";  
//							break;
//				    }
//				}
//				var style = styleCache[size];
//				if(!style){
//					style = [new ol.style.Style({
//						image: new ol.style.Circle({
//							radius:getClusterRadius(parseInt(size.toString())),
//							stroke: new ol.style.Stroke({
//								color: clusterColor,
//								width: 1
//							}),
//							fill: new ol.style.Fill({
//								color: clusterColor
//							})
//						}),
//						text: new ol.style.Text({
//							text: wavus.util.comma(size.toString() + ''),
//							font: 'bold 13px Arial',
//							fill: new ol.style.Fill({
//								color: '#fff'
//							})
//						}),
//					})];
//					styleCache[size] = style;
//				}
//				return style;
//			}
//		});
//		
//		map.addLayer(clusterLayer);
//		clusterLayer.set('name', layerNm);
//		
//		//클러스터 클릭시 작동 (해당 클러스터의 속성정보 표현)
//		var popup = document.getElementById('popup_cluster');
//		var popup_content = document.getElementById('popup_cluster_content');
//		var cluster_count = document.getElementById('cluster_count');
//		
//		
//		$(popup).show();
//		var olpopup = new ol.Overlay({
//			element: popup,
//		    autoPan: false
//		});
//		map.addOverlay(olpopup);
//		//클러스터 속성팝업창 닫기 클릭 이벤트
//		$(".pop-cluster-close").click(function(){
//			olpopup.setPosition(undefined);
//			clusterClickInteraction.getFeatures().clear();
//			return false;
//		});
//		
//		var OpenPopup = function(evt){
//			var feature = map.forEachFeatureAtPixel(evt.pixel,
//				function (feature, layer){
//					if(feature){
//						var coord = map.getCoordinateFromPixel(evt.pixel);
//						if(typeof feature.get('features') == 'undefined'){
//							return false;
//							//$(popup_content).html('<li>조회결과가 없습니다.</li>');
//						}else{
//							$(".popup-cluster-header").css("display","block");
//							$(".popup-cluster-content").css("display","block");
//							var cfeatures = feature.get('features');
//							var fontColor = "";
//							$(cluster_count).html(wavus.util.comma(cfeatures.length + ''));
//							$(popup_content).html('');
//							for(var i=0;i<cfeatures.length;i++){
//								switch(cfeatures[i].get('lsTpCd')){
//								case "01":	
//									fontColor = "#1080BE";	
//									break;
//								case "02":	
//									fontColor = "#8D378F";	
//									break;
//								case "03":	
//									fontColor = "#FD5151";	
//									break;
//								case "04":	
//									fontColor = "#57B637";  
//									break;
//						    }
//								$(popup_content).append('<li><font color=' + fontColor +'><strong>[' + cfeatures[i].get('lsTpNm') +']</strong></font>' + cfeatures[i].get('name')  + ' (' + cfeatures[i].get('ddoAr') + ')'+ '</li>');
//							}
//						}
//						olpopup.setPosition(coord);
//					}else{
//						olpopup.setPosition(undefined); //클러스터 속성팝업창 닫기
//					}
//			 });
//		}; 
//		
//		//마우스 클릭시
//		map.on('singleclick', OpenPopup);
//	   
//		//클러스터에 hover시 스타일변경
//	    clusterLayer.set('clusterSelectable',true);
//		var clusterInteraction = new ol.interaction.Select({
//			layers : function(layer){
//				$(map.getTarget()).css("cursor", ( layer.get('clusterSelectable') == true ? "pointer" : ""));
//				return layer.get('clusterSelectable');
//			},
//			condition: function(evt){
//				return evt.type == 'pointermove';
//			},
//			style : function(feature){
//				var size  = feature.get('features').length;
//				if($("input[name=searchSpceLsTpCd]:checked").val() == '00'){
//					switch(getClusterLsTpCd(feature.get("features")[0])){
//						case "01":	
//							clusterColor = "#1080BE";	
//							break;
//						case "02":	
//							clusterColor = "#8D378F";	
//							break;
//						case "03":	
//							clusterColor = "#FD5151";	
//							break;
//						case "04":	
//							clusterColor = "#57B637";  
//							break;
//				    }
//				}
//					style = [new ol.style.Style({
//						image: new ol.style.Circle({
//							radius:getClusterRadius(parseInt(size.toString())) + 3,
//							stroke: new ol.style.Stroke({
//								color: clusterColor,
//								width: 2
//							}),
//							fill: new ol.style.Fill({
//								color: '#fff'
//							})
//						}),
//						text: new ol.style.Text({
//							text: wavus.util.comma(size.toString() + ''),
//							font: 'bold 15px Arial',
//							fill: new ol.style.Fill({
//								color: clusterColor
//							})
//						}),
//						
//					})];
//				return style;
//			}
//		});
//		 //클러스터에 클릭시 스타일변경
//	    clusterLayer.set('clusterClicktable',true);
//		var clusterClickInteraction = new ol.interaction.Select({
//			layers : function(layer){
//				return layer.get('clusterClicktable');
//			},
//			condition: function(evt){
//				return evt.type == 'click';
//			},
//			style : function(feature){
//				var size  = feature.get('features').length;
//				if($("input[name=searchSpceLsTpCd]:checked").val() == '00'){
//					switch(getClusterLsTpCd(feature.get("features")[0])){
//						case "01":	
//							clusterColor = "#1080BE";	
//							break;
//						case "02":	
//							clusterColor = "#8D378F";	
//							break;
//						case "03":	
//							clusterColor = "#FD5151";	
//							break;
//						case "04":	
//							clusterColor = "#57B637";  
//							break;
//				    }
//				}
//					style = [new ol.style.Style({
//						image: new ol.style.Circle({
//							radius:getClusterRadius(parseInt(size.toString())) + 3,
//							stroke: new ol.style.Stroke({
//								color: clusterColor,
//								width: 2
//							}),
//							fill: new ol.style.Fill({
//								color: '#fff'
//							})
//						}),
//						text: new ol.style.Text({
//							text: wavus.util.comma(size.toString() + ''),
//							font: 'bold 15px Arial',
//							fill: new ol.style.Fill({
//								color: clusterColor
//							})
//						}),
//					})];
//				return style;
//			}
//		});
//		
//		clusterClickInteraction.on("click", function(){
//		});
//		
//		clusterInteraction.set("name", "clusterInteraction");
//		clusterClickInteraction.set("name", "clusterClickInteraction");
//		
//		map.getInteractions().extend([clusterInteraction]);
//		map.getInteractions().extend([clusterClickInteraction]);
//		
//		fnClusterLegend();
//		
//	}	
//	
//	//히트맵 분석(실거래가)
//	function fnRealDlpcHeatMapAnals(layerNm, list)
//	{
//		map = wavus.map.getMap();
//		var features = new Array(list.length);
//		var _radius = radius || 10;
//		var _blur = blur || 50;
//		
//	    for(var i=0;i<list.length;i++){
//	    	
//	    	var coordinates = [list[i].xAxis, list[i].yAxis];
//	    	features[i] = new ol.Feature(new ol.geom.Point(coordinates)); 
//	    }
//	    realHeatmapLayer = new ol.layer.Heatmap({
//			source : new ol.source.Vector({
//				 features : features
//			 }),
//			blur : _blur,
//			opacity: parseFloat($("#rtmsOpacity").val(),10),
//			radius : _radius,
//			zIndex : 99,
//			weight : function(feature){
//				var magnitude = parseFloat(feature.get('magnitude'));
//				return magnitude - 5;
//			}
//		});
//	    fnHeatMapColor($("#fillColorHide").val());
//	    map.addLayer(realHeatmapLayer);
//	    realHeatmapLayer.set('name', layerNm);
//	    
//	}
//	//히트맵 투명도 조정  
//	function fnHeatMapRdmOpacity(){
//		var opacityTitle = parseFloat($("#rdmOpacity").val(),10)*100;
//		heatmapLayer.setOpacity(parseFloat($("#rdmOpacity").val(),10)); 
//		$("#rdmOpacity").attr("title","임대주택 투명도(" + opacityTitle + "%)");
//	}
//	
//	//히트맵 투명도 조정(실거래가)  
//	function fnHeatMapOpacity(){
//		var opacityTitle = parseFloat($("#rtmsOpacity").val(),10)*100;
//		realHeatmapLayer.setOpacity(parseFloat($("#rtmsOpacity").val(),10)); 
//		$("#rtmsOpacity").attr("title","실거래가 투명도(" +opacityTitle + "%)");
//		
//	}
//	//히트맵 색상 조정(실거래가)  
//	function fnHeatMapColor(selectColor){
//		switch(selectColor){
//			case '353535':
//				realHeatmapLayer.setGradient(['#eaeaea','#bdbdbd','#8c8c8c','#5d5d5d','#353535']); 
//				break;
//			case '990085':
//				realHeatmapLayer.setGradient(['#ffd9fa','#ffb2f5','#f361dc','#d941c5','#990085']); 
//				break;
//			case '3f0099':
//				realHeatmapLayer.setGradient(['#e8d9ff','#d1b2ff','#a566ff','#8041d9','#3f0099']);
//				break;
//			case '003399':
//				realHeatmapLayer.setGradient(['#d9e5ff','#b2ccff','#6799ff','#4374d9','#003399']);
//				break;
//			case '008299':
//				realHeatmapLayer.setGradient(['#d4f4fa','#b2ebf4','#5cd1e5','#3db7cc','#008299']);
//				break;
//			case '6b9900':
//				realHeatmapLayer.setGradient(['#eaf7ba','#cef279','#bce55c','#9fc93c','#6b9900']);
//				break;
//			case '6b9900':
//				realHeatmapLayer.setGradient(['#d4f4fa','#b2ebf4','#5cd1e5','#3db7cc','#008299']);
//				break;
//			case '997000':
//				realHeatmapLayer.setGradient(['#faecc5','#ffe08c','#f2cb61','#cca63d','#997000']);
//				break;
//			case '993800':
//				realHeatmapLayer.setGradient(['#fae0d4','#ffc19e','#f29661','#cc723d','#993800']);
//				break;
//		}
//	}
//	//격자분석 시 배경 및 테두리 색상 조정
//	function fnLtceColor(){
//		var selectColor = $("input[name=fillColorGbnChk]:checked").val() == "02" ? $('#bordrFillColorHide').val():$('#bgFillColorHide').val();
//	    
//		if(selectColor == "ff0000"){
//			$("input[name=fillColorGbnChk]:checked").val() == "02" ? ltceStroke = 'rgba(255,0,0,0.1)' : ltceFill = 'rgba(255,0,0,0.1)';
//		}else if(selectColor == "ff5e00"){
//			$("input[name=fillColorGbnChk]:checked").val() == "02" ? ltceStroke = 'rgba(255,94,0,0.1)' : ltceFill = 'rgba(255,94,0,0.1)';
//		}else if(selectColor == "ffbb00"){
//			$("input[name=fillColorGbnChk]:checked").val() == "02" ? ltceStroke = 'rgba(255,187,0,0.1)' : ltceFill = 'rgba(255,187,0,0.1)';
//		}else if(selectColor == "ffe400"){
//			$("input[name=fillColorGbnChk]:checked").val() == "02" ? ltceStroke = 'rgba(255,228,0,0.1)' : ltceFill = 'rgba(255,228,0,0.1)';
//		}else if(selectColor == "abf200"){
//			$("input[name=fillColorGbnChk]:checked").val() == "02" ? ltceStroke = 'rgba(171,242,0,0.1)' : ltceFill = 'rgba(171,242,0,0.1)';
//		}else if(selectColor == "1ddb16"){
//			$("input[name=fillColorGbnChk]:checked").val() == "02" ? ltceStroke = 'rgba(29,219,22,0.1)' : ltceFill = 'rgba(29,219,22,0.1)';
//		}else if(selectColor == "00d8ff"){
//			$("input[name=fillColorGbnChk]:checked").val() == "02" ? ltceStroke = 'rgba(0,216,255,0.1)' : ltceFill = 'rgba(0,216,255,0.1)';
//		}else if(selectColor == "0054ff"){
//			$("input[name=fillColorGbnChk]:checked").val() == "02" ? ltceStroke = 'rgba(0,84,255,0.1)' : ltceFill = 'rgba(0,84,255,0.1)';
//		}else if(selectColor == "0100ff"){
//			$("input[name=fillColorGbnChk]:checked").val() == "02" ? ltceStroke = 'rgba(1,0,255,0.1)' : ltceFill = 'rgba(1,0,255,0.1)';
//		}else if(selectColor == "5f00ff"){
//			$("input[name=fillColorGbnChk]:checked").val() == "02" ? ltceStroke = 'rgba(95,0,255,0.1)' : ltceFill = 'rgba(95,0,255,0.1)';
//		}else if(selectColor == "ff00dd"){
//			$("input[name=fillColorGbnChk]:checked").val() == "02"? ltceStroke = 'rgba(255,0,221,0.1)' : ltceFill = 'rgba(255,0,221,0.1)';
//		}else if(selectColor == "ff007f"){
//			$("input[name=fillColorGbnChk]:checked").val() == "02" ? ltceStroke = 'rgba(255,0,127,0.1)' : ltceFill = 'rgba(255,0,127,0.1)';
//		}else if(selectColor == "000000"){
//			$("input[name=fillColorGbnChk]:checked").val() == "02" ? ltceStroke = 'rgba(0,0,0,0.1)' : ltceFill = 'rgba(0,0,0,0.1)';
//		}else if(selectColor == "ffffff"){
//			$("input[name=fillColorGbnChk]:checked").val() == "02" ? ltceStroke = 'rgba(255,255,255,0.1)' : ltceFill = 'rgba(255,255,255,0.1)';
//		}
//		
//	}
//	//격자분석 색상 체크
//	function fnFillColorGbnChk(){
//		 if($("input[name=fillColorGbnChk]:checked").val() == "02"){
//			 $('#ltceFillColor').css('background-color','#'+ $('#bordrFillColorHide').val());
//		 }else{
//			 $('#ltceFillColor').css('background-color','#'+ $('#bgFillColorHide').val());
//		 }
//	}
//	
//	//격자분석 색상 클릭
//	function fnFillColorClick(){
//		
//		var ltceAllStyle;
//		var ltceStyle;
//		
//		fnLtceColor();
//		
//		ltceAllStyle =  new ol.style.Style({
//			stroke : new ol.style.Stroke({
//				 color: '#'+$('#bordrFillColorHide').val(),
//				 width: 1
//			}),
//			fill: new ol.style.Fill({
//				color: 'rgba(255,255,255,0.1)' //흰색
//			})
//		 });
//	    ltceAnalsAllLayer.setStyle(ltceAllStyle);
//	    ltceStyle =  new ol.style.Style({
//		   stroke : new ol.style.Stroke({
//				 color: ltceStroke,
//				 width: 0.1
//			}),
//			fill: new ol.style.Fill({
//				color: ltceFill
//			})
//	    });
//	    ltceAnalsLayer.setStyle(ltceStyle);
//	}
//	
//	
//	//반경내 그리기
//	function fnRdusCircleDraw()
//	{
//		 wavus.draw.clearDraw();
//		 wavus.draw.removeDraw();
//		 map = wavus.map.getMap();
//		 if(map.getView().getZoom() < 15){
//			commonUtils.customAlert('선택 한 범위가 큽니다. 지도를 확대하여 주십시오.'); 
//			return;
//		 }
//		 if($("#analsSelect").val() == '02'){
//			   if($.trim($("#ltceWidth").val()) == '' || $.trim($("#ltceHeight").val()) == ''){
//				   commonUtils.customAlert('격자분석의 간격을 입력하십시오.');
//	    		   return;
//			   }else if($("#ltceWidth").val() < 30 || $("#ltceWidth").val() > 200){
//				   commonUtils.customAlert('너비는 30에서 200사이로 선택하십시오.');
//	    		   return;
//			   }else if($("#ltceHeight").val() < 30 || $("#ltceHeight").val() > 200){
//				   commonUtils.customAlert('높이는 30에서 200사이로 선택하십시오.');
//	    		   return;
//			   }
//		 }
//		 fnInit();
//		 var innerOption={
//					title : '알림'
//					,message : '공간분석을 수행하고자하는 반경을 지정하세요.'
//			   } 
//		 commonUtils.alert(innerOption);
//		
//		 wavus.draw.addDraw('Circle').then(function(draw){
//			draw.on('drawstart',function(evt){
//				var startSketch = null;
//			});
//			draw.on('drawend',function(evt){
//				wavus.draw.removeDraw();
//				drawLayer = draw;
//				var endSketch = evt.feature;
//				var center = endSketch.getGeometry().getCenter();
//				var radius = endSketch.getGeometry().getRadius();
//				var radiusStr = radius >1000?(radius/1000).toFixed(1) + ' ㎞':Math.round(radius) + ' m';
//				var radiusCal = document.getElementById('radiusCal');
//				$(radiusCal).show();
//				radiusDiv = new ol.Overlay({
//					positioning: 'center-center',
//					element: radiusCal
//				});
//				map.addOverlay(radiusDiv);
//				$("#radiusCal-content").css('left','0px');
//				$("#radiusCal-content").css('top','0px');
//				$("#radiusCal-content").html('<div class="tooltip">반경 : ' + radiusStr + '</div>');
//				$("#radiusCal-content").dblclick(function(){
//					removeRadiusDiv();
//				});
//				$("#radiusCal-content").css('cursor','pointer');
//				$("#radiusCal-content").attr('title','드래그로 이동이 가능하며 더블클릭하면 닫힙니다.');
//				$("#radiusCal-content").draggable();
//				radiusDiv.setPosition(center);
//				
//				var circle = new ol.geom.Polygon.fromCircle(new ol.geom.Circle (center, radius));
//				var circleFeature = new ol.Feature (
//					{
//						geometry : circle
//					} );
//				var featureArray = new Array();
//				featureArray.push(circleFeature);
//				var beforeJson = (new ol.format.GeoJSON()).writeFeaturesObject(featureArray);
//				var coordinates = beforeJson.features[0].geometry.coordinates[0];
//				getMinMaxCoords(coordinates);
//				params["geom"] = getCoords(coordinates);
//				fnDrawAnalsExec();
//			});
//		});
//	}
//	
//	//행정구역 경계 영역그리기
//	function fnLawdAreaDraw(result, lawdCd){
//		map = wavus.map.getMap();
//		var gubun = result.gubun;
//		var lawd = '';
//		var url = '';
//		
//		if(lawdCd.length < 5){
//			lawd = 'sid_cd =' + lawdCd.substring(0,2);
//			url = sidoLayerUrl + lawd;
//		}
//		
//		if(lawdCd.length == 5){
//			lawd = 'adm_sect_c =' + lawdCd.substring(0,5);
//			url = sggLayerUrl + lawd;
//		}
//		
//		if(lawdCd.length > 5){
//			lawd = 'emd_cd =' + lawdCd.substring(0,8);
//			url = emdLayerUrl + lawd;
//		}
//		
//		var vectorSource = new ol.source.Vector({
//			format: new ol.format.GeoJSON(),
//			url : url
//		});
//		
//		var lawdLayer = new ol.layer.Vector({
//				zIndex : 99,
//				source: vectorSource,
//				style: new ol.style.Style({
//					stroke: new ol.style.Stroke({
//						lineDash: [5,5,0.1,5],
//						color: '#ff0000',
//						width: 3
//					}),
//					fill: new ol.style.Fill({
//						color: "rgba(234,234,234,0.1)"
//					})
//				})
//		});
//		map.addLayer(lawdLayer);
//		lawdLayer.set('name', 'userLawdLayer');
//	
//	}
//   //레이어 지우기(전체레이어 삭제)
//   function removeLayer()
//  {
//		map = wavus.map.getMap();
//		wavus.draw.clearDraw();
//        wavus.controller.clearLcLayer();
//	    wavus.controller.clickInteraction.getFeatures().clear();
//	    wavus.layer.removeInteraction(map, "lc_overInter");
//		wavus.layer.removeInteraction(map, "lc_clickInter");
//		realHeatmapLayer = new ol.layer.Heatmap();
//		selectRthousInfoList = new Array(); //임대주택정보 리스트 초기화
//		selectRealDlpcList = new Array(); //실거래가 리스트 초기화
//		clusterLayer = null;
//		removeRadiusDiv();
//		map.getLayers().forEach(function(layer){
//			var layerName =  layer.get('name') + '';
//			if(layerName.substring(0,4) == 'user'){
//				map.removeLayer(layer);
//			}
//		});
//		$("#realDlpcChk").prop("checked",false);
//   }
//   
//  //레이어 지우기(내관심지역 레이어 제외)
//   function removeGlanLayer()
//  {
//		map = wavus.map.getMap();
//		wavus.draw.clearDraw();
//		wavus.controller.clearLcLayer();
//	    wavus.controller.clickInteraction.getFeatures().clear();
//	    wavus.layer.removeInteraction(map, "lc_overInter");
//		wavus.layer.removeInteraction(map, "lc_clickInter");
//		realHeatmapLayer = new ol.layer.Heatmap();
//		selectRthousInfoList = new Array(); //임대주택정보 리스트 초기화
//		selectRealDlpcList = new Array(); //실거래가 리스트 초기화
//		clusterLayer = null;
//		removeRadiusDiv();
//		map.getLayers().forEach(function(layer){
//			var layerName =  layer.get('name') + '';
//			if(layerName.substring(0,4) == 'user'){
//				if(layerName != "userGlanLayer"){
//					map.removeLayer(layer);
//				}
//			}
//		});
//		$("#realDlpcChk").prop("checked",false);
//   }
//   
//   //반경표시 삭제
//   function removeRadiusDiv()
//  {
//	   if(radiusDiv != null){
//			radiusDiv.setPosition(undefined);
//		}
//   }
//   
//   //좌표가져오기(폴리곤 좌표) 
//   function getPolygonCoords(coordinates){
//	   var coordArray = coordinates.split(',');
// 	   var polygonCoords = "[";	
// 	   for(var i=0; i<coordArray.length;i++){
//			var xyCoord = coordArray[i].split(' ');
//			if(i == coordArray.length-1){
//				polygonCoords += "["+xyCoord[0]+"," +xyCoord[1]+"]";
//			}else{
//				polygonCoords += "["+xyCoord[0]+"," +xyCoord[1]+"],";
//			}
//		}
// 	   polygonCoords += "]";
// 	   return eval(polygonCoords);
// 	}
//   
//  //좌표가져오기 
//  function getCoords(coordinates){
//	    var startCoords  = "";
//		var coords = "";
//		
//		for(var i=0;i<coordinates.length;i++){
//			for(var j=0;j<coordinates[i].length;j++){
//				if(j == 0){
//					coords += coordinates[i][j] + " ";
//				}else{
//					coords += coordinates[i][j];
//				}
//			}
//			coords += ",";
//		}
//		
//		coords = coords.substring(0,coords.length-1);
//		return coords;
//	}
//  
//  //좌표가져오기(min, max)
//  function getMinMaxCoords(coordinates){
//	    var xmin = coordinates[0][0];
//		var ymin = coordinates[0][1];
//		var xmax = coordinates[0][0];
//		var ymax = coordinates[0][1];
//		
//		for(var i=0;i<coordinates.length;i++){
//			
//			for(var j=0;j<coordinates[i].length;j++){
//				if(j == 0){
//					if(xmin > coordinates[i][j]){
//						xmin = coordinates[i][j];
//					}
//					if(xmax < coordinates[i][j]){
//						xmax = coordinates[i][j];
//					}
//				}else{
//					if(ymin > coordinates[i][j]){
//						ymin = coordinates[i][j];
//					}
//					if(ymax < coordinates[i][j]){
//						ymax = coordinates[i][j];
//					}
//				}
//			}
//		}
//		params["xmin"] = xmin;
//		params["ymin"] = ymin;
//		params["xmax"] = xmax;
//		params["ymax"] = ymax;
//	}
//  //현재화면 좌표 가져오기
//  function getScrinCoord(){
//	    map = wavus.map.getMap();
//		var extentMap = map.getView().calculateExtent(map.getSize());
//		var topRight = ol.extent.getTopRight (extentMap);
//		var bottomRight = ol.extent.getBottomRight(extentMap);
//		var bottomLeft = ol.extent.getBottomLeft(extentMap);
//		var topLeft = ol.extent.getTopLeft(extentMap);
//		var coord = "[" + topRight + "],[" + bottomRight + "],[" + bottomLeft + "],[" + topLeft + "],[" + topRight + "]";
//		var axis = [bottomLeft[0], topRight[0], bottomLeft[1], topRight[1]];
//		coord = coord.replace(/,/gi, " ").replace(/\] \[/gi, ",");
//		coord = coord.replace(/\[/gi, "").replace(/\]/gi, "");
//		return coord;
//	}
//  //실거래가 히트맵 체크박스 컨트롤	
//  function fnLayerChkYn(){
//		  map = wavus.map.getMap();
//		  if($("#realDlpcChk").is(":checked")){
//			    if(realHeatmapLayer.getZIndex() == 10){
//			    	realHeatmapLayer.setZIndex(999);
//			    }else{
//			    	realHeatmapLayer.setZIndex(10);
//			    }
//			    map.addLayer(realHeatmapLayer);
//			}else{
//				map.getLayers().forEach(function(layer){
//					  var layerName =  layer.get('name') + '';
//					  if(layerName == "userRealDplcHeatMapLayer"){
//						  map.removeLayer(layer);
//					  }
//				});
//			}
//	}
//  
//  //실거래가 히트맵 초기화
//  function heatMapLayerDel(){
//	  map = wavus.map.getMap();
//	  realHeatmapLayer = new ol.layer.Heatmap();
//	  wavus.draw.clearDraw();
//	  
//	  map.getLayers().forEach(function(layer){
//		  var layerName =  layer.get('name') + '';
//			if(layerName == "userRealLayer"){
//				map.removeLayer(layer);
//			}
//		});
//	}
//  
//  //클러스터 크기 설정
//  function getClusterRadius(size){
//	 var result = 0;
//	 
//	 if(size < 10){
//		 result = 10;
//	 }else if(size > 300){
//		 result =  25 + (size/30);
//	 }else{
//		 result =  10 + (size/10);
//	 } 
//	   return result;
//	}
//  
//    //extent 구성하여 좌표 이동하기
//	function coordMove(lawdCd, list, analsSelect)
//	{
//		map = wavus.map.getMap();
//		var url = "/anals/selectExtentInfo.do";
//		
//		if(lawdCd.length == 2){
//			params["sidoCd"] = lawdCd;
//			params["sggCd"] = "";
//			params["emdCd"] = "";
//		}else if(lawdCd.length == 5){
//			params["sggCd"] = lawdCd;
//			params["sidoCd"] = "";
//			params["emdCd"] = "";
//		}else{
//			params["emdCd"] = lawdCd;
//			params["sggCd"] = "";
//			params["sidoCd"] = "";
//		}
//		$.ajax({
//			type : "POST" ,
//			data : params,
//			url : url ,
//			dataType : 'json' ,
//			success : function ( data ) {
//				fnLawdAreaDraw(data.resultData.result, lawdCd);
//				var extent = [data.resultData.result.xmin, data.resultData.result.ymin, data.resultData.result.xmax, data.resultData.result.ymax];
//				if(list != null && analsSelect != '02'){
//					extent = getCoord(list);
//				}else{
//    				$.each(list, function (index, value){
//    					var temp = [];
//    					temp = [value.minX, value.minY, value.maxX, value.maxY];
//    					extent = temp;
//    				});
//				}
//				var shiftWidth = $("#asideTab").hasClass("on") ?  $("#asideTab").width() : 0;
//		        wavus.controller.shiftAreaFit(shiftWidth, extent);
//				params["sidoCd"] = '';
//				params["sggCd"] = '';
//				params["emdCd"] = '';
//			},
//			error: function (xhr, status, error) {
//				commonUtils.customAlert('처리 중 오류가 발생하였습니다.');
//			}
//		});
//	}
//	
//	   //extent 구성하여 좌표 이동하기
//	function coordMove2(lawdCd)
//	{
//		map = wavus.map.getMap();
//		var url = "/anals/selectExtentInfo.do";
//	
//		if(lawdCd.length == 2){
//			params["sidoCd"] = lawdCd;
//			params["sggCd"] = "";
//			params["emdCd"] = "";
//		}else if(lawdCd.length == 5){
//			params["sggCd"] = lawdCd;
//			params["sidoCd"] = "";
//			params["emdCd"] = "";
//		}else{
//			params["emdCd"] = lawdCd;
//			params["sggCd"] = "";
//			params["sidoCd"] = "";
//		}
//		$.ajax({
//			type : "POST" ,
//			data : params,
//			url : url ,
//			dataType : 'json' ,
//			success : function ( data ) {
//				fnLawdAreaDraw(data.resultData.result, lawdCd);
//				
//				var extent = [data.resultData.result.xmin, data.resultData.result.ymin, data.resultData.result.xmax, data.resultData.result.ymax];
//				var shiftWidth = $("#asideTab").hasClass("on") ?  $("#asideTab").width() : 0;
//		        wavus.controller.shiftAreaFit(shiftWidth, extent);
//				params["sidoCd"] = '';
//				params["sggCd"] = '';
//				params["emdCd"] = '';
//			},
//			error: function (xhr, status, error) {
//				commonUtils.customAlert('처리 중 오류가 발생하였습니다.');
//			}
//		});
//	}
//	
//	//클러스터 범례추가
//	 function fnClusterLegend() {
//	    	var thematicTitle = "클러스터 범례";
//	    	
//	    	$('.anals_legend_title').empty();
//	    	
//	    	var tempLegend = "<div class='area_anals_legend'><div class='anals_legend_title'>&nbsp;<span class='anals_legend_title_text'>"+thematicTitle+"</span></div><div class='anals_legend_content'><ul>";
//	    	var tempLegendDetail = "";
//	    	
//	    	tempLegendDetail += "<li><span class='anals_legend_color' style='background-color :#1080BE;'></span><span class='anals_legend_text'>건설임대</span></li>";
//	    	tempLegendDetail += "<li><span class='anals_legend_color' style='background-color :#8D378F;'></span><span class='anals_legend_text'>매입임대</span></li>";
//	    	tempLegendDetail += "<li><span class='anals_legend_color' style='background-color :#FD5151;'></span><span class='anals_legend_text'>전세임대</span></li>";
//	    	tempLegendDetail += "<li><span class='anals_legend_color' style='background-color :#57B637;'></span><span class='anals_legend_text'>건설(매입)임대</span></li>";
//	    	
//	    	
//	    	tempLegend = tempLegend + tempLegendDetail + "</ul></div></div>";
//	    	$('.ol-viewport').append(tempLegend);
//	    } 
//	 
//	//모든 레이어 삭제
//	function fnInit(){
//		map = wavus.map.getMap();
//		wavus.draw.removeDraw();
//		wavus.layer.removeInteraction(map, "clusterInteraction");
//		for(var i=0;i<5;i++){
//			 removeLayer();
//		}
//		$('#analsGlanList').val("");
//		$('.balloon').hide();
//		$('.thematic_title').hide();
//		$('.area_thematic_legend').hide();
//		$('.area_anals_legend').hide();
//	}
//	
//	function getCoord(list){
//		var coord = [];
//		var xMax = 0; yMax = 0; xMin = 0; yMin = 0;
//		
//		for(var i = 0; i < list.length; i ++){
//			if(i == 0){
//				xMax = list[i].xAxis;
//				xMin = list[i].xAxis;
//				yMax = list[i].yAxis;
//				yMin = list[i].yAxis;
//			}else{
//				if(list[i].xAxis > xMax){
//					xMax = list[i].xAxis;
//				}else if(list[i].xAxis < xMin){
//					xMin = list[i].xAxis;
//				}
//				
//				if(list[i].yAxis > yMax){
//					yMax = list[i].yAxis;
//				}else if(list[i].yAxis < yMin){
//					yMin = list[i].yAxis;
//				}
//			}
//		}
//
//		coord = [xMin, yMin, xMax, yMax];		
//		return coord;
//	};
//	
//    //엑살 다운로드시 log
//    function fnExcelDownLoadLog(){
//    	var fctNm = '공간분석 엑셀 다운로드';
//    	var fctUrl = '/biz/si/ls/locationSearch-main.do';
//    	
//    	var params = {};
//    	params["fctNm"] = fctNm;
//    	params["fctUrl"] = fctUrl;
//    	
//    	var url = "/biz/si/main/fileDownloadInsertLog.do";
//		$.ajax({
//			type : "POST" ,
//			url : url ,
//			data : params,
//			dataType : 'json' ,
//			error: function (xhr, status, error) {
//				commonUtils.customAlert("처리 중 오류가 발생하였습니다.");
//			}
//		});
//    };
//	
//	function glanAdd(){
//		if($("#sidoList").val() == 'all' && $("#sggList").val() == 'all'){
//			commonUtils.customAlert('행정구역을 선택하여 주십시요.');
//			return;
//		}else if($("#sidoList").val() != 'all' && $("#sggList").val() == 'all'){
//			commonUtils.customAlert('시도단위는 추가하실수 없습니다.');
//			return;
//		}else{
//			var innerOption={
//					title : ''
//					,message : '해당 지역을 내관심지역에 추가하시겠습니까?'
//					,callBackY : function(){
//						var url = "/glan/addGlanArea.do";
//						params["sggCd"] = $("#sggList").val();
//						params["emdCd"] = $("#emdList").val();
//						params["empNo"] = $("#empNo").val();
//						$.ajax({
//							type : "POST" ,
//							data : params,
//							url : url ,
//							dataType : 'json' ,
//							success : function ( data ) {
//								commonUtils.customAlert('내관심지역에 추가하였습니다.');
//								wavus.glan.getGlanList();
//							},
//							error: function (xhr, status, error) {
//								commonUtils.customAlert('처리 중 오류가 발생하였습니다.');
//							}
//						});
//					}
//					,callBackN : function(){}
//					,buttonLabel : {
//						ok : ''
//						,cancel : ''
//					}
//					,transition:'fade'
//					,closable:false
//				}
//	    	//확인창 수행
//	    	commonUtils.confirm(innerOption);
//		}
//	}
//	
//	var getClusterLsTpCd = function(param){
//		var lsTpCd = '';
//
//		if( param instanceof ol.Feature ){
//			lsTpCd =  param.get("lsTpCd"); 
//		}
//		else if( typeof(param) == "string" ){
//			lsTpCd = param;
//		}
//		else{
//			lsTpCd = param.lsTpCd;
//		}
//		return lsTpCd;
//	}
//	 
//	var module = {
//			fnSpceAnalsPopup : fnSpceAnalsPopup,
//			getLayerUrlInfo : getLayerUrlInfo,
//			fnAnalsExec : fnAnalsExec,
//			fnGoAnalsExec : fnGoAnalsExec,
//			fnExcelDownLoad : fnExcelDownLoad,
//			getSplTpCdList : getSplTpCdList,
//			getGlanAreaInfo : getGlanAreaInfo,
//			fnIndictGbnChange : fnIndictGbnChange,
//			fnAnalsSelectChange : fnAnalsSelectChange,
//			fnRealDlpcChange : fnRealDlpcChange,
//			fnHeatMapClick : fnHeatMapClick,
//			fnLtceAnalsClick : fnLtceAnalsClick,
//			fnClusterClick : fnClusterClick,
//			fnHeatMapOpacity : fnHeatMapOpacity,
//			fnHeatMapRdmOpacity : fnHeatMapRdmOpacity,
//			fnHeatMapColor : fnHeatMapColor,
//			fnFillColorGbnChk : fnFillColorGbnChk,
//			fnFillColorClick : fnFillColorClick,
//			removeLayer : removeLayer,
//			removeRadiusDiv : removeRadiusDiv,
//			fnInit : fnInit,
//			fnLayerChkYn : fnLayerChkYn,
//			getSidoList : getSidoList,
//			getSigunguList : getSigunguList,
//			getEmdList : getEmdList,
//			coordMove : coordMove,
//			coordMove2 : coordMove2,
//			glanAdd : glanAdd
//	};
//
//	return module;
//
//})();
