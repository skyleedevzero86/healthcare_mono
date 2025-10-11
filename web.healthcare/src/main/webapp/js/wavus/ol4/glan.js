window.wavus = window.wavus || {};

wavus.glan = (function()
{
	var map;
	var select;
	var modify;
	var params = {};
	var wgs84Sphere = new ol.Sphere(6378137);
	var glanLayerList;
	var vectorLayer;
	var editPolygonSt = false;
	
	//바로가기 팝업창 열기
	function fnGlanPopup(){				
		/*for(var i=0;i<3;i++){
			 removeLayer();
		}*/
		
		/*if($("#glanPop").css("display") != "none"){
			fnGlanPopupClose();
		}else{
			//getGlanList();
			//editPolygonSt = false;
			$("#glanPop").show();
		}*/
		$("#glanPop").show();

		//map.removeInteraction(select);
		//map.removeInteraction(modify);
	}
	
	//외부 기능 활성화시 바로가기 팝업창 및 기능 닫기
	function fnGlanPopupClose(){				
		for(var i=0;i<3;i++){
			 removeLayer();
		}	
		map.removeInteraction(select);
		map.removeInteraction(modify);
		closeLayer();
		editPolygonSt = false;
		$("#glanCreatPop").hide();
		$("#glanEditPop").hide();
	}
	
	//바로가기 신규생성팝업창 열기
	function fnGlanCreatPopup(area){
		if($("#glanCreatPop").css("display") != "none"){
			$("#glanCreatPop").hide();
		}else{
			var content = "";
			$("#glanCreatContent").html("");
			content += '<ul>';
			content +='	<table width="100%" style="margin-bottom: 5px;"><tbody><a height="1px"></a></tbody></table>';
			content +='	<table width="100%" style="margin-bottom: 5px;"><tbody><tr><th width="42px">명  칭 : </th><td><input type="text" id="glanNm_1" name="glanNm_1"></td></tr></tbody></table>';
			content +='	<table width="100%" style="margin-bottom: 5px;"><tbody><tr><th width="42px">면  적 : </th><td><input type="text" id="glanArea_1" name="glanArea_1" value="'+area+'" readonly></td></tr></tbody></table>';
			content +='	<table width="100%" style="margin-bottom: 5px;"><tbody><tr><th width="42px">비  고 : </th><td><input type="text" id="glanRmk_1" name="glanRmk_1"></td></tr></tbody></table>';
			content += '</ul>';	
			$("#glanCreatContent").html(content);
			$("#glanCreatPop").show();
		}
	}
	
	//바로가기 수정팝업창 열기
	function fnGlanEditPopup(){
			var url = "/glan/getGlanInfo.do";
			$.ajax({
				type : "POST" ,
				url : url ,
				data : params,
				dataType : 'json' ,
				success : function ( data ) {
					var glanInfo = data.resultData.getInfo;
					var content = "";
					$("#glanContent").html("");
					content += '<ul>';
					content +='	<table width="100%" style="margin-bottom: 5px;"><tbody><a height="1px"></a></tbody></table>';
					content +='	<table width="100%" style="margin-bottom: 5px;"><tbody><tr><th width="42px">명  칭 : </th><td><input type="text" id="glanNm_2" name="glanNm_2" value="'+glanInfo.glanNm+'"></td></tr></tbody></table>';
					content +='	<table width="100%" style="margin-bottom: 5px;"><tbody><tr><th width="42px">면  적 : </th><td><input type="text" id="glanArea_2" name="glanArea_2" value="'+glanInfo.glanAr+'" readonly></td></tr></tbody></table>';
					content +='	<table width="100%" style="margin-bottom: 5px;"><tbody><tr><th width="42px">비  고 : </th><td><input type="text" id="glanRmk_2" name="glanRmk_2" value="'+glanInfo.rm+'"></td></tr></tbody></table>';
					content += '</ul>';	
					if(glanInfo.sggCd == 'all' && glanInfo.bjdCd == 'all'){
						content += '<button class="btn-editPt" onclick="wavus.glan.editPolygonPoint()">점편집</button>';
						content += '<button class="btn-edit" onclick="wavus.glan.fnGlanSave(2)">수정</button>';
					}
					$("#glanContent").html(content);
					$("#glanEditPop").show();
				},
				error: function (xhr, status, error) {
					commonUtils.customAlert("처리 중 오류가 발생하였습니다.");
				}
			});
	}
	
	//바로가기 지역 저장
	function fnGlanSave(gub){
		var url;
		if(gub == 1){
			url = "/glan/insertGlanList.do";
			  params["empNo"] = $("#empNo").val();
			  params["glanNm"] = $("#glanNm_1").val();
			  params["glanAr"] = $("#glanArea_1").val();
			  params["glanRmk"] = $("#glanRmk_1").val();
			  
			  if(params["glanNm"] == ""){
				  commonUtils.customAlert("관심지역 명을 입력해주십시요.");
				  return;
			  }

				$.ajax({
					type : "POST" ,
					url : url ,
					data : params,
					dataType : 'json' ,
					success : function ( data ) {
						$("#glanCreatPop").hide();
						$("#glanEditPop").hide();
						removeLayer();
						getGlanList();
						commonUtils.customAlert("바로가기에 추가되었습니다.");
					},
					error: function (xhr, status, error) {
						commonUtils.customAlert("처리 중 오류가 발생하였습니다.");
					}
				});
				polygonDraw = false;  
				closeLayer();
		}else{
			url = "/glan/editGlanList.do";
			map = wavus.map.getMap();
			map.getLayers().forEach(function(layer){
				var layerName =  layer.get('name') + '';
				if(layerName == 'glanAr'){
					var feature = layer.getSource().getFeatures();
					var coordinates = feature[0].getGeometry().getCoordinates();
					var polygon = new ol.geom.Polygon([coordinates[0][0]]);
					var area = formatArea(polygon);
					$("#glanArea").val(area);
					var geomatry = 'MULTIPOLYGON(((' + getCoords(coordinates[0][0]) + ')))';
					params["geom"] = geomatry;
					params["empNo"] = $("#empNo").val();
					params["glanNm"] = $("#glanNm_2").val();
					params["glanAr"] = $("#glanArea_2").val();
					params["glanRmk"] = $("#glanRmk_2").val();
					
					  if(params["glanNm"] == ""){
						  commonUtils.customAlert("관심지역 명을 입력해주십시요.");
						  return;
					  }

						$.ajax({
							type : "POST" ,
							url : url ,
							data : params,
							dataType : 'json' ,
							success : function ( data ) {
								//$("#glanCreatPop").hide();
								//$("#glanEditPop").hide();
								getGlanList();
								commonUtils.customAlert("수정되었습니다.");
							},
							error: function (xhr, status, error) {
								commonUtils.customAlert("처리 중 오류가 발생하였습니다.");
							}
						});
				}
			});
			//closeLayer();
			editPolygonSt = false;
			map.removeInteraction(select);
			map.removeInteraction(modify);
		}
	}
	
	 //바로가기 리스트 가져오기 
	  function getGlanList(){
		  var url = "/glan/selectGlanList.do";
		  params["empNo"] = $("#empNo").val();
			$.ajax({
				type : "POST" ,
				url : url ,
				data : params,
				dataType : 'json' ,
				success : function ( data ) {
					var list = data.resultData.resultList;
					var glanList = "";
					$("#glanList").html("");
					glanList += '<ul>';
					$.each(list, function (index, value){
						glanList += '<li><input type="checkbox" id="glanList_' +index+ '" name="glanList"  glan_id="' + value.glanId + '" glan_dt="' + value.inputDt + '" value="' + value.glanNm + '";"><a onclick="wavus.glan.drawGlanArea('+index+')";> ' + value.glanNm + '</a></input></li>';
		        	});
					glanList += '</ul>&nbsp;';					
					$("#glanList").html(glanList);
				},
				error: function (xhr, status, error) {
					commonUtils.customAlert("처리 중 오류가 발생하였습니다.");
				}
			});
	 }
	  
	  //바로가기 지역 그리기
	  function drawGlanArea(value){
		  var url = "/glan/getGlanArea.do";
		  params["empNo"] = $("#empNo").val();
		  params["glanNm"] = $('input[id=glanList_'+value+']').val();
		  params["glanId"] = $('input[id=glanList_'+value+']').attr("glan_id");
		  params["glanDt"] = $('input[id=glanList_'+value+']').attr("glan_dt");
		  
		  var vectorSource = new ol.source.Vector();
		  var vector = new ol.layer.Vector({
			  	name : 'glanAr',
				layerId: 'boundary',
				source: vectorSource,
				zIndex : 90,
				style: new ol.style.Style({
					stroke: new ol.style.Stroke({
						color: 'rgba(255, 0, 0, 1.0)',
						lineDash: [5,5,.1,5],
						width: 3
					})
				})
		  });
		  
		  $.ajax({
				type : "POST" ,
				url : url ,
				data : params,
				dataType : 'json' ,
				success : function ( data ) {
					var coordinates = data.resultData.resultList
					map = wavus.map.getMap();

					var geojsonObject = {
						'type' : 'FeatureCollection',
						'crs' : {
							'type' : 'name',
							'properties' : {
								'name' : 'EPSG:5147'
							}
						},
						'features' : [{
							'type' : 'Feature',
							'geometry' : {
								'type' : 'MultiPolygon',
								'coordinates' : [coordinates]
							}
						}]
					};
					
					var features = (new ol.format.GeoJSON()).readFeatures(geojsonObject);
					vectorSource.addFeatures(features);
					
				    if(vectorLayer != null){
				    	removeLayer();
				    }

				    map.addLayer(vector);
				    vectorLayer = vector;
				    var extent = vectorSource.getExtent();
				    var center = ol.extent.getCenter(extent);
				    map.getView().fit(extent);
				    
				    fnGlanEditPopup();
				},
				error: function (xhr, status, error) {
					commonUtils.customAlert("처리 중 오류가 발생하였습니다.");
				}
		  });
	  }
	  
	  function editPolygonPoint(){
		  wavus.draw.clearDraw();
		  wavus.draw.removeDraw();
		  if(!editPolygonSt){
			  map = wavus.map.getMap();
			  select = new ol.interaction.Select();
			  map.addInteraction(select);
			
			  modify = new ol.interaction.Modify({
				  features : select.getFeatures()
			  });
			  map.addInteraction(modify);
			
			  var selectedFeatures = select.getFeatures();
			  select.on('change : active', function(){
				  selectedFeatures.forEach(selectedFeatures.remove, selectedFeatures);
			  });
			
			  select.setActive(true);
			  modify.setActive(true);
			  
			  editPolygonSt = true;
			  
		  }
	  }
	  
	 //바로가기 리스트 삭제
	  function deleteGlanList(){
		  var deleteList = "";
		  var glanlist_value = document.getElementsByName("glanList");
		  var j=0;
		  for(var i = 0; i < glanlist_value.length; i++){
			  if(glanlist_value[i].checked){
				  if(j == 0){
					  deleteList += $('input[id=glanList_'+i+']').attr("glan_id");
					  deleteList += $("#empNo").val();
				  }else{
					  deleteList += '/';
					  deleteList += $('input[id=glanList_'+i+']').attr("glan_id");
					  deleteList += $("#empNo").val();
				  }
				  j++;
			  }
		  }
  
		  if(deleteList == ""){
			  commonUtils.customAlert("삭제하실 목록을 체크해주십시요.");
			  return;
		  }
		  
		  var url = "/glan/deleteGlanList.do";
		  params["deleteList"] = deleteList;
			$.ajax({
				type : "POST" ,
				url : url ,
				data : params,
				dataType : 'json' ,
				success : function ( data ) {
					getGlanList();
					removeLayer();
					$("#glanCreatPop").hide();
					$("#glanEditPop").hide();
					commonUtils.customAlert("삭제되었습니다.");
				},
				error: function (xhr, status, error) {
					commonUtils.customAlert("처리 중 오류가 발생하였습니다.");
				}
			});
	  }


	  //바로가기 지역 지정하기
	  function fnPolygonDraw()
	  {
		  wavus.draw.clearDraw();
		  wavus.draw.removeDraw();
		  
		  	 if($("#empNo").val() == ''){
		  		 commonUtils.customAlert("로그인이 필요한 기능입니다. 로그인을 해주십시요.");
		  		 return;
		  	 }
		  	 map = wavus.map.getMap();
		  	 if(map.getView().getZoom() < 15){
		  		 commonUtils.customAlert("선택 한 범위가 큽니다. 지도를 확대하여 주십시오.");
		  		 return;
		  	 }
		  	 $("#glanEditPop").hide();
			 commonUtils.customAlert("바로가기에 추가할 지역을 지정하세요.");
			 wavus.draw.addDraw('Polygon').then(function(draw){
				 draw.on('drawstart',function(evt){
					var startSketch = null;
				});
				draw.on("drawend", function(evt){
					wavus.draw.removeDraw();
					drawLayer = draw;
					var endSketch = evt.feature;
					var featureArray = new Array();
					featureArray.push(endSketch);
					var beforeJson = (new ol.format.GeoJSON()).writeFeaturesObject(featureArray);
					var coordinates = beforeJson.features[0].geometry.coordinates[0];
					var polygon = new ol.geom.Polygon([coordinates]);
					var area = formatArea(polygon);
					
					var geomatry = 'MULTIPOLYGON(((' + getCoords(coordinates) + ')))';
					params["geom"] = geomatry;
					
					fnGlanCreatPopup(area);
				});
			});
		}
		
		  //좌표가져오기 
		  function getCoords(coordinates){
			    var startCoords  = "";
				var coords = "";
				
				for(var i=0;i<coordinates.length;i++){
					for(var j=0;j<coordinates[i].length;j++){
						if(j == 0){
							coords += coordinates[i][j] + " ";
						}else{
							coords += coordinates[i][j];
						}
					}
					coords += ",";
				}
				
				coords = coords.substring(0,coords.length-1);
				return coords;
			}
		
		   //레이어 지우기(전체레이어 삭제)
		   function removeLayer()
		  {
				map = wavus.map.getMap();
				wavus.draw.clearDraw();
				map.getLayers().forEach(function(layer){
					var layerName =  layer.get('name') + '';

					if(layerName == 'glanAr'){
						map.removeLayer(layer);
					}
				});
		   }
		   
		   //면적 측정
		   function formatArea(polygon) {
				var area;
				var geom = /** @type {ol.geom.Polygon} */
				(polygon.clone().transform(wavus.config.mapInfo.projection, 'EPSG:4326'));
				var coordinates = geom.getLinearRing(0).getCoordinates();
				area = Math.abs(wgs84Sphere.geodesicArea(coordinates));
				
				var output;
				if (area > 10000) {
					output = (Math.round(area / 1000000 * 100) / 100) + ' ' + '㎢';
				} else {
					output = (Math.round(area * 100) / 100) + ' ' + '㎡';
				}
				return output;
			};
			
			//편집창 종료 및 위치 리셋
			function closeLayer(){
				$("#glanEditPop").css({
					"top" : "40%",
					"left" : "42%",
					"position" : "absolute"
				});
				$("#glanEditPop").hide();
				
				$("#glanCreatPop").css({
					"top" : "40%",
					"left" : "42%",
					"position" : "absolute"
				});
				$("#glanCreatPop").hide();
				
				editPolygonSt = false;
				map.removeInteraction(select);
				map.removeInteraction(modify);
				for(var i=0;i<3;i++){
			 		removeLayer();
				}
			};
			
	var module = {
			fnGlanPopup : fnGlanPopup,
			getGlanList : getGlanList,
			deleteGlanList : deleteGlanList,
			fnPolygonDraw : fnPolygonDraw,
			removeLayer : removeLayer,
			fnGlanSave : fnGlanSave,
			drawGlanArea : drawGlanArea,
			editPolygonPoint : editPolygonPoint,
			closeLayer : closeLayer,
			fnGlanPopupClose : fnGlanPopupClose
	};

	return module;
	
})();