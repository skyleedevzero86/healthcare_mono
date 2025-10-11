
window.wavus = window.wavus || {};

wavus.controller = (function() {
	
	var _map;
	var _dragBox = new ol.interaction.DragBox({
		condition: ol.events.condition.always
	});
	var _move_history = [];
	var _move_history_idx = 0;
	var _chk_move = true;
	// 이전, 다음 버튼을 위한 맵 움직임 기록
	function _recordHistory(){
		if(_chk_move == true){
			if(_move_history_idx > 0){
				if(_move_history[_move_history_idx - 1].center == _map.getView().getCenter() && 
						_move_history[_move_history_idx - 1].resolution == _map.getView().getResolution()){
					return;
				}
			}
			if(_move_history.length > _move_history_idx){
				_move_history.splice(_move_history_idx, _move_history.length - _move_history_idx);
			}
			_move_history.splice(_move_history_idx, 0, {
		        center: _map.getView().getCenter(), 
		        resolution: _map.getView().getResolution()
		    });
		    _move_history_idx++;
		    _nextPrevButtonControl();
		}
	}
	
	// 이전, 다음 버튼 disabled 컨트롤
	function _nextPrevButtonControl(){
		if(_move_history_idx <= 1) {
			$("#prev_b").prop("disabled", true);
		}else{
			$("#prev_b").prop("disabled", false);
		}
		if(_move_history_idx == _move_history.length) {
			$("#next_b").prop("disabled", true);
		}else{
			$("#next_b").prop("disabled", false);
		}
	}
	
	//홈으로 이동
	function goHome(){
		
		//한반도전체
//		_map.getView().animate({center : ol.proj.transform([128.25, 35.95],'EPSG:4326','EPSG:5174'), zoom : 7, duration : 200});
//		_map.getView().animate({center : [285501.6054381803, 272894.70741268725], zoom : 8.5, duration : 200});
		_map.getView().animate({center : [187128.76813143413, 304584.1077421839], zoom : 8, duration : 200});
		
		//LH사옥
//		_map.getView().animate({center : [304303.4169128897, 187171.36610007135], zoom : 17, duration : 500});
	}
	
	//지도제어도구를 param으로 넘겨받은 div에 일괄 추가
	function addControlBox(controlDiv){
		_addControlBoxInterface(controlDiv);
		_addControlBoxControl();
	}
	function _addControlBoxInterface(controlDiv){
		//지도제어도구 추가(인터페이스 커스터마이징 시 id,name 변경 금지)
		var div = document.getElementById(controlDiv);
		var controlBoxHTML = "<div style='float:left;'>"+
										"<input type='button' id='mapControl_b' name='mapControl_b' value='지도제어'/>"+
										"<div id='mapControl_controlbox' class='lieb_toolbox' style='display:none;position:fixed;'>"+
											"<input type='button' id='move_b' name='move_b' value='이동'/>"+
											"<input type='button' id='zoom-in_b' name='zoom-in_b' value='확대'/>"+
											"<input type='button' id='zoom-out_b' name='zoom-out_b' value='축소'/>"+
											"<input type='button' id='prev_b' name='prev_b' value='이전' disabled/>"+
											"<input type='button' id='next_b' name='next_b' value='다음' disabled/>"+
										"</div>"+
									"</div>";
		div.innerHTML = controlBoxHTML;
		
		_map.on("moveend", function (e) {
			_recordHistory(); // 이전, 다음을 위한 Center, Zoom Array 설정
		});
	}
	function _addControlBoxControl(){
		//지도제어도구 동작옵션
		$("#mapControl_b").click(function(){
			buttonChange({cursor:""});
			if($("#mapControl_controlbox").css("display") == 'none'){
				$(".lieb_toolbox").hide();
				$("#mapControl_controlbox").show();
			}else{
				$("#mapControl_controlbox").hide();
			}
		});
		$("#mapControl_controlbox input").click(function(){
			buttonChange({cursor:""});
			var controlType = wavus.util.right(this.id, 2) == "_b" ? this.id.substring(0, this.id.length - 2) : this.id;
			_addControlMap(controlType);
		});
	}
	function _addControlMap(controlType){
		// 이전, 다음 버튼 이벤트
		if(controlType == 'prev' || controlType == 'next'){
			_chk_move = false;
			if(controlType == "prev"){
				$("#prev_b").prop("disabled", true);
				if (_move_history_idx > 1) {
					_move_history_idx--;
					_map.getView().animate({center : _move_history[_move_history_idx - 1].center, resolution : _move_history[_move_history_idx - 1].resolution, duration : 200});
				}
			}else if(controlType == "next"){
				$("#next_b").prop("disabled", true);
				if (_move_history_idx < _move_history.length) {
					_move_history_idx++;
					_map.getView().animate({center : _move_history[_move_history_idx - 1].center, resolution : _move_history[_move_history_idx - 1].resolution, duration : 200});
				}
			}
			setTimeout(function() { _chk_move = true; _nextPrevButtonControl(); }, 500);
			// 맵 이동, 줌in, 줌out 컨트롤
		}else if(controlType == 'move' || controlType == 'zoom-in' || controlType == 'zoom-out'){
			buttonChange({cursor:controlType});
			if(controlType != 'move'){
				var click_div_type;
				var box_div_type;
				if(controlType == "zoom-in"){
					click_div_type = 1;
					box_div_type = -4;
				}else if(controlType == "zoom-out"){
					click_div_type = -1;
					box_div_type = 1;
				}
				var boxZoom = function (e){
					var box_size = Math.abs((_dragBox.a.c[0] - _dragBox.a.f[0]) * (_dragBox.a.c[1] - _dragBox.a.f[1]));
					var map_size = _map.getSize().reduce(function(a, b){return a*b});
					var gap_size = map_size / 30;
					var zoom_level = [gap_size*1, gap_size*4, gap_size*9, gap_size*16];
					var box_zoom;
					for(var i=0; i<zoom_level.length; i++){
						if(zoom_level[i] > box_size){
							box_zoom = i + box_div_type;
							break;
						}else{
							box_zoom = 3 + box_div_type;
						}
					}
					// ↑ Box 크기별로 조절위한 로직
					return _map.getView().getZoom() - box_zoom;
				}
				var clickZoom = function (){ return _map.getView().getZoom() + (1 * click_div_type) };
				_map.addInteraction(_dragBox);
				_dragBox.on('boxend', function (e){
					_map.getView().animate({center : ol.extent.getCenter(_dragBox.getGeometry().getExtent()), zoom : boxZoom(e), duration : 200});
				});
				_map.on('singleclick', function(e) {
					_map.getView().animate({center : e.coordinate, zoom : clickZoom(), duration : 200});
				});
			}
		}
	}
	
	//그리기 도구 및 측정도구를 param으로 넘겨받은 div에 일괄 추가
	function addToolBox(toolDiv){
		 _addToolBoxInterface(toolDiv);
		 _addToolBoxControl();
	}
	function _addToolBoxInterface(toolDiv){
		//그리기도구 추가(인터페이스 커스터마이징 시 id,name 변경 금지)
		var div = document.getElementById(toolDiv);
		var toolBoxHTML = "<div style='float:left;'>"+
										"<input type='button' id='measure_b' name='measure_b' value='측정'/>"+
										"<div id='measure_toolbox' class='lieb_toolbox' style='display:none;position:fixed;'>"+
											"<input type='button' id='distance_b' name='distance_b' value='거리'/>"+
											"<input type='button' id='area_b' name='area_b' value='면적'/>"+
											"<input type='button' id='rm_measure_b' name='rm_measure_b' value='지우기'/>"+
										"</div>"+
									"</div>"+
									"<div style='float:left;'>"+
										"<input type='button' id='draw_b' name='draw_b' value='그리기'/>"+
								  		"<div id='draw_toolbox' class='lieb_toolbox' style='display:none;position:fixed;'>"+
											"<input type='button' id='Point' name='Point' value='점'/>"+
											"<input type='button' id='LineString' name='LineString' value='선'/>"+
											"<input type='button' id='Box' name='Box' value='사각'/>"+
											"<input type='button' id='Circle' name='Circle' value='원'/>"+
											"<input type='button' id='Polygon' name='Polygon' value='다각'/>"+
											"<input type='button' id='rm_draw_b' name='rm_draw_b' value='지우기'/>"+
										"</div>"+
									"</div>"+
									"<div style='float:left;'>"+
										"<input type='button' id='textPoint' name='textPoint' value='메모'/>"+
										"<input type='button' id='eraseMemo' name='eraseMemo' value='메모지우기'/>"+
									"</div>"+
									"<div style='float:left;'>"+
										"<input type='button' id='eraseAll_b' name='eraseAll_b' value='모두지우기'/>"+
									"</div>";
		div.innerHTML = toolBoxHTML;
	}
	function _addToolBoxControl(){
		//그리기도구 동작옵션
		$("#draw_b").click(function(){
			buttonChange({cursor:""});
			if($("#draw_toolbox").css("display") == 'none'){
				$(".lieb_toolbox").hide();
				$("#draw_toolbox").show();
//				$("#measure_toolbox").hide();
				wavus.draw.removeDraw();
				wavus.draw.clearDraw();
				wavus.measure.removeResult();
			}else{
				$("#draw_toolbox").hide();
				wavus.draw.removeDraw();
				wavus.draw.clearDraw();
				wavus.measure.removeResult();
			}
		});
		$("#draw_toolbox input").click(function(){
			buttonChange({cursor:""});
			if(this.id == 'rm_draw_b'){
				wavus.draw.clearDraw();
				wavus.measure.removeResult();
			}else{
				wavus.draw.addDraw(this.id);
			}
		});
		$("#textPoint").click(function(){
			if(wavus.user.getErrStatus() != "" ){
				alert("로그인 후 이용하세요");
				return;
			}
			buttonChange({cursor:"copy"});
			wavus.draw.addDraw(this.id);
		});
		$("#eraseMemo").click(function(){
			if(wavus.user.getErrStatus() != "" ){
				alert("로그인 후 이용하세요");
				return;
			}
			wavus.draw.deleteTextLayer();
		});
		
		//측정도구 동작옵션
		$("#measure_b").click(function(){
			buttonChange({cursor:""});
			if($("#measure_toolbox").css("display") == 'none'){
				$(".lieb_toolbox").hide();
				$("#measure_toolbox").show();
//				$("#draw_toolbox").hide();
				wavus.draw.removeDraw();
				wavus.draw.clearDraw();
				wavus.measure.removeResult();
			}else{
				$("#measure_toolbox").hide();
				wavus.draw.removeDraw();
				wavus.draw.clearDraw();
				wavus.measure.removeResult();
			}
		});
		$("#measure_toolbox input").click(function(){
			buttonChange({cursor:""});
			if(this.id == 'distance_b'){
				wavus.draw.addDraw('LineString').then(function(draw){
					wavus.measure.addMeasure(draw);
				});
			}else if(this.id == 'area_b'){
				wavus.draw.addDraw('Polygon').then(function(draw){
					wavus.measure.addMeasure(draw);
				});
			}else if(this.id == 'rm_measure_b'){
				wavus.draw.clearDraw();
				wavus.measure.removeResult();
			}
		});
		//모두지우기 동작옵션
		$("#eraseAll_b").click(function(){
			eraseAll();
		});
	}
	
	function eraseAll(){
//		buttonChange({cursor:""});
		wavus.draw.removeDraw();
//		wavus.layer.getDrawLayer().getSource().clear();
		wavus.measure.removeResult();
//		$.each($.grep(_map.getLayers().getArray(), function(v, i){ return v instanceof ol.layer.Vector && v.get('name') != undefined }), function(key, layer){
//			if($("#mainTOC").find("input[id=" + layer.get('name') + "]").is(":checked") == true){
//				$("#mainTOC").find("input[id=" + layer.get('name') + "]").click();
//			}
//			if($("#LayerList").find("input[id=add_" + layer.get('name') + "]").is(":checked") == true){
//				$("#LayerList").find("input[id=add_" + layer.get('name') + "]").click();
//			}
//		});
	}
	
	function clearLcLayer(){
//		if(radiusDiv != null){
//			radiusDiv.setPosition(undefined);
//		}
//		$('div#searchJuso_context').trigger("click")
		wavus.draw.clearRadius("lc_radiusLayer");
//		wavus.layer.removeLayer(_map,"lc_selectLayer")
//		wavus.layer.removeLayer(_map,"lc_boundary")
//		wavus.layer.removeLayer(_map,"lc_AreaCircle")
//		wavus.layer.removeLayer(_map,"lc_CircumStatusPoint")
//		wavus.layer.removeLayer(_map,"lc_areaLabel")
//		wavus.layer.removeLayer(_map,'lc_hsmpLcClusterLayer');
//		for (var i = 0; i < 7; i++) {
//			wavus.layer.removeLayer(_map,"lc_cluster"+i);
//		}
		_map.getLayers().getArray().forEach(function(obj){
			if(/lc_/.test( obj.get("layerId") )){
				if(obj.get("layerId") != 'lc_radiusLayer'){
					wavus.layer.removeLayer(_map,obj.get("layerId"));
				}
			}
		})
	}
	
	//배경맵 변경 도구를 param으로 넘겨받은 div에 일괄 추가
	function addBgMapBox(bgMapDiv){
		 _addBgMapBoxInterface(bgMapDiv);
		 _addBgMapBoxControl();
	}
	function addBgMapBox_ver2(bgMapDiv){
		 _addBgMapBoxInterface_ver2(bgMapDiv);
		 _addBgMapBoxControl_ver2();
	}
	function _addBgMapBoxInterface(bgMapDiv){//배경맵 선택 버튼 숨김 버전
		//배경맵 변경 도구 추가(인터페이스 커스터마이징 시 id,name 변경 금지)
		var div = document.getElementById(bgMapDiv);
		var bgMapBoxHTML = "<div style='float:left;'>"+
										"<input type='button' id='baseMap_b' name='baseMap_b' value='배경지도'/>"+
										"<div id='mapKind_toolbox' class='lieb_toolbox' style='display:none;position:fixed;'>"+
											"<input type='button' id='naver_b' name='naver_b' value='네이버'/>"+
											"<input type='button' id='naverSatellite_b' name='naverSatellite_b' value='네이버항공'/>"+
											"<input type='button' id='daum_b' name='daum_b' value='다음'/>"+
											"<input type='button' id='daumSatellite_b' name='daumSatellite_b' value='다음항공'/>"+
										"</div>"+								
									  "</div>";
		div.innerHTML = bgMapBoxHTML;
	}
	function _addBgMapBoxInterface_ver2(bgMapDiv){//배경맵 선택 버튼 노출 버전
		//배경맵 변경 도구 추가(인터페이스 커스터마이징 시 id,name 변경 금지)
		var div = document.getElementById(bgMapDiv);
		var bgMapBoxHTML =  "<div id='mapKind_toolbox' class='lieb_toolbox'>"+
											"<ul>"+
											"<li><a href='daum_b' title='다음' class='on'>다음</a></li>"+
											"<li><a href='daumSatellite_b' title='다음항공'>다음항공</a></li>"+
											"<li><a href='naver_b' title='네이버'>네이버</a></li>"+
											"<li><a href='naverSatellite_b' title='네이버항공'>네이버항공</a></li>"+
											"</ul>"+
										"</div>";
		div.innerHTML = bgMapBoxHTML;
	}
	function _addBgMapBoxControl(){
		//배경맵 변경 도구 동작옵션
		$("#baseMap_b").click(function(){
			if($("#mapKind_toolbox").css("display") == 'none'){
				$(".lieb_toolbox").hide();
				$("#mapKind_toolbox").show();
			}else{
				$("#mapKind_toolbox").hide();
			}
		});
		//var baseMapArr = [ 'naver', 'naverSatellite','daum', 'daumSatellite_part1', 'daumSatellite_part2'];
		var baseMapArr = ['daum', 'daumSatellite_part1', 'daumSatellite_part2'];
		$("#mapKind_toolbox input").click(function(){
			var baseMapId = this.id.substring(0, this.id.length - 2); 
			$.each(baseMapArr, function(key, value){
				wavus.layer.hideLayer(value);
			});
			$.each($.grep(baseMapArr, function(v, i){ return v.split("_")[0] == baseMapId; }), function(key, value){
				wavus.layer.showLayer(value);
			});
		});
	}
	function _addBgMapBoxControl_ver2(){
		$("#mapKind_toolbox a").click(function(e){
			e.preventDefault();
			$("#mapKind_toolbox a").removeClass("on");
			$(this).addClass("on");
			var target = $(this).attr("href");
			
			//var baseMapArr = ['daum', 'naver', 'daumSatellite_part1', 'daumSatellite_part2', 'naverSatellite'];
			var baseMapArr = ['daum', 'daumSatellite_part1', 'daumSatellite_part2',];
			var baseMapId = target.substring(0, $(this).attr("href").length - 2); 
			$.each(baseMapArr, function(key, value){
				wavus.layer.hideLayer(value);
			});
			$.each($.grep(baseMapArr, function(v, i){ return v.split("_")[0] == baseMapId; }), function(key, value){
				wavus.layer.showLayer(value);
			});

		});
	}
	
	function bgMapChange(mapId){
		//var baseMapArr = ['daum', 'naver', 'daumSatellite_part1', 'daumSatellite_part2', 'naverSatellite'];
		var baseMapArr = ['daum',  'daumSatellite_part1', 'daumSatellite_part2',];
		$.each(baseMapArr, function(key, value){
			wavus.layer.hideLayer(value);
		});
		$.each($.grep(baseMapArr, function(v, i){ return v.split("_")[0] == mapId; }), function(key, value){
			wavus.layer.showLayer(value);
		});
	}
	
	//TOC 틀 추가
	function addTOCFrame(TOC_Div){
		var div = document.getElementById(TOC_Div);
		var TOC_HTML = "<div id='addLayerLabel'>레이어를 추가해주세요.</div>";
		TOC_HTML = TOC_HTML + "<span id='controlLayerButton' style='visibility:hidden;'></span><span id='addListLayer_plus'></span><span id='addListLayer_minus' style='display:none;'></span>";
		TOC_HTML = TOC_HTML + "<ul id = 'mainTOC'></ul>";
		TOC_HTML = TOC_HTML + "<div class='TOCButtonDiv controlVisibleHidden'></div>";
		div.innerHTML = TOC_HTML;
		$(".TOCButtonDiv").css("display", "none");
		$("#controlLayerButton").click(function(){
			if($(this).hasClass("on") == true){
				$(this).removeClass("on");
				$(".controlVisibleHidden").css("visibility", "hidden");
			}else{
				$(this).addClass("on");
				$(".controlVisibleHidden").css("visibility", "visible");
			}
		});
	}
	
	//TOC 목록 추가
	function addTOCElement(layer_Id, layer_nm){
		var layerId;
		var layerNm;
		
		if(!layer_nm){
			layerId = layer_Id;
			layerNm = layer_Id;
		}else{
			layerId = layer_Id;
			layerNm = layer_nm;
		}
		
		var layer_zindex=0;
		var layer_aindex=0;
		wavus.layer.findLayerById(layerId).then(function(layer){
			layer_zindex = layer.getZIndex();
			layer_aindex = _.findIndex(_map.getLayers().getArray(), layer);
		});
		var check_layer = "<input type='checkbox' id='"+layerId+"' name='"+layerId+"' value='"+layerNm+"'><label for='"+layerId+"' style='height:20px;'></label>";
		var sortUp_button = "<span id='up-"+layerId+"' name='up-"+layerId+"' class='layerUp_b' ></span>";
		var sortDown_button = "<span id='down-"+layerId+"' name='down-"+layerId+"' class='layerDown_b' ></span>";
		var tmp_html = "";
		tmp_html = tmp_html + "<li data-zindex='" + layer_zindex + "' data-aindex='" + layer_aindex + "'>";
		tmp_html = tmp_html + "<table style='display:inline-table;table-layout:fixed;' width='98%' cellpadding='0' cellspacing='0'>";
		tmp_html = tmp_html + "<tr>";
		tmp_html = tmp_html + 	"<td align='left' width='7%' style='vertical-align:middle;' class='controlVisibleHidden'>" + check_layer + "</td>";
		tmp_html = tmp_html + 	"<td align='right' width='15%' style='vertical-align:middle;'><img id ='legend-"+layerId+"' src='' style='cursor:pointer;vertical-align:middle;'></td>";
		tmp_html = tmp_html + 	"<td align='left' width='63%' style='vertical-align:middle;'><label class='TOCLayerNm' for='"+layerId+"' title='" + layerNm + "'>" + layerNm + "</label></td>";
		tmp_html = tmp_html + 	"<td align='right' width='15%' style='line-height:5px;vertical-align:middle;' class='controlVisibleHidden'>" + sortUp_button + "<br />" + sortDown_button + "</td>"
		tmp_html = tmp_html + "</tr>";
		tmp_html = tmp_html + "</table></li>";
		$("#mainTOC").append(tmp_html);
		_addEventTOCElement(layerId);
		if($("#LayerList").length > 0){
			$("#legend-"+layerId).parent().next("td").append("<span class='removeLayerList_b controlVisibleHidden' id='delete-"+layerId+"'/>");
			$("#delete-"+layerId).click(function(){
				if($("#mainTOC").find("input[id=" + layerId + "]").is(":checked") == true){
					$("#mainTOC").find("input[id=" + layerId + "]").click();
				}
				if($("#LayerList").find("input[id=add_" + layerId + "]").is(":checked") == true){
					$("#LayerList").find("input[id=add_" + layerId + "]").click();
				}
				_changeTOCButtonVisible();
			});
			_addLayerElement(layerId, layerNm);
		}
		tocLayerSort();
	}
	
	// 추가된 TOC 목록에 기능설정
	function _addEventTOCElement(layer_Id){
		var layer_ID = '#'+layer_Id;
		$(layer_ID).click(function(){
			if($(this).is(":checked")){
				wavus.layer.findLayerById(layer_Id).then(function(layer){
					layerArray.push(layer_Id);
					layer.setVisible(true);
				});
				$(this).parents("li").eq(0).removeClass("DisabledStyle");
			}else{
				wavus.layer.findLayerById(layer_Id).then(function(layer){
					layerArray.splice(layerArray.indexOf(layer_Id),1);					
					layer.setVisible(false);
				});
				$(this).parents("li").eq(0).addClass("DisabledStyle");
			}
		});
		
		$("#up-"+layer_Id).click(function(){
			_upDownLayerIndex(layer_Id, "up");
		});
		$("#down-"+layer_Id).click(function(){
			_upDownLayerIndex(layer_Id, "down");
		});
	}
	
	// 레이어 Z-index 조정 기능
	function _upDownLayerIndex(layer_Id, arrow){
		var li = $('#' + layer_Id).parents("li").eq(0);
		var zindex = li.data("zindex");
		var aindex = li.data("aindex");
		var li_list = $("#mainTOC > li" ).filter(function(){ return $(this).css("display") != "none"; });
		var index = li_list.index(li);
		var step = 0;
		if(arrow == "up"){
			step = -1;
			if(index < 1){
				alert("맨 위입니다.");
				return;
			}
		}else if(arrow == "down"){
			step = 1;
			if(index > li_list.length - 2){
				alert("맨 아래입니다.");
				return;
			}
		}else{
			return;
		}
		var target_li = li_list.eq(index + step);
		var target_layer_Id = target_li.find("input[type=checkbox]").prop("id");
		var target_zindex = target_li.data("zindex");
		var target_aindex = target_li.data("aindex");
		li.data("zindex", target_zindex);
		li.data("aindex", target_aindex);
		target_li.data("zindex", zindex);
		target_li.data("aindex", aindex);
		wavus.layer.findLayerById(layer_Id).then(function(layer){
			layer.setZIndex(target_zindex);
		});
		wavus.layer.findLayerById(target_layer_Id).then(function(layer){
			layer.setZIndex(zindex);
		});
		var tmp_layer = _map.getLayers().getArray()[aindex];
		_map.getLayers().getArray()[aindex] = _map.getLayers().getArray()[target_aindex];
		_map.getLayers().getArray()[target_aindex] = tmp_layer;
		
		tocLayerSort();
	}
	
	// 레이어 정렬 기능
	function tocLayerSort(){
		var ul = $("#mainTOC");
		var li_array = ul.children('li'); 
		li_array.sort(function(a, b){
			var az = $(a).data("zindex")*1;
			var bz = $(b).data("zindex")*1;
			var aa = $(a).data("aindex")*1;
			var ba = $(b).data("aindex")*1;
			if(az - bz == 0){
				return ba - aa;
			}else{
				return bz - az;
			}
		});
		li_array.detach().appendTo(ul);
		_map.render();
	}
	
	//AddLayer 틀 추가
	function addLayerFrame(tocDiv, add_Div){
		var div = document.getElementById(add_Div);
		var Layer_Add_HTML = "<div style='width:100%;height:9%;padding:6px;'>";
		Layer_Add_HTML = Layer_Add_HTML + "<div style='width:85%;display:inline-block;text-align:left;'><input type='text' id='layerSearch' name='layerSearch' value='' placeholder='검색할 레이어명을 입력하세요.' style='width:195px;' /></div>";
		Layer_Add_HTML = Layer_Add_HTML + "<div style='width:15%;display:inline-block;text-align:center;'><span id='addListClose_b' ></span></div></div>";
		Layer_Add_HTML = Layer_Add_HTML + "<div style='width:100%;height:91%;'><ul id = 'LayerList'>";
		Layer_Add_HTML = Layer_Add_HTML + 	"<li><div id='expand-default-layer' class='toggle-legend-plus'><b>시스템 레이어 목록</b></div><div id='collapse-default-layer' class='toggle-legend-minus' style='display:none;'><b>시스템 레이어 목록</b></div><ul style='display:none;'></ul></li>";
		Layer_Add_HTML = Layer_Add_HTML + 	"<li><div id='expand-custom-layer' class='toggle-legend-plus'><b>사용자 레이어 목록</b></div><div id='collapse-custom-layer' class='toggle-legend-minus' style='display:none;'><b>사용자 레이어 목록</b></div><ul style='display:none;'></ul></li>";
		Layer_Add_HTML = Layer_Add_HTML + "</ul></div>";
		div.innerHTML = Layer_Add_HTML;
		
		$("#addListClose_b").click(function(){
			$("#addListLayer_minus").css("display", "none");
			$("#addListLayer_plus").css("display", "");
			$("#"+add_Div).css("display", "none");
			$("#"+add_Div+" #layerSearch").val("");
			$("#"+add_Div+" #layerSearch").keyup();
		});
		$("#layerTOCFrame span[id*=addListLayer_]").click(function(){
			if($("#"+add_Div).css("display") == "none"){
				$("#addListLayer_minus").css("display", "");
				$("#addListLayer_plus").css("display", "none");
				$("#"+add_Div).css("display", "");
				$("#"+add_Div+" #layerSearch").keyup();
			}else{
				$("#addListLayer_minus").css("display", "none");
				$("#addListLayer_plus").css("display", "");
				$("#"+add_Div).css("display", "none");
				$("#"+add_Div+" #layerSearch").val("");
				$("#"+add_Div+" #layerSearch").keyup();
			}
		});
		
		$("#LayerList div[class*=toggle-legend-]").click(function(){
			if($(this).prop("id") == "expand-default-layer"){
				$(this).css("display", "none");
				$("#collapse-default-layer").css("display", "");
				$(this).nextAll("ul").eq(0).css("display", "");
			}
			if($(this).prop("id") == "collapse-default-layer"){
				$(this).css("display", "none");
				$("#expand-default-layer").css("display", "");
				$(this).nextAll("ul").eq(0).css("display", "none");
			}
			if($(this).prop("id") == "expand-custom-layer"){
				$(this).css("display", "none");
				$("#collapse-custom-layer").css("display", "");
				$(this).nextAll("ul").eq(0).css("display", "");
			}
			if($(this).prop("id") == "collapse-custom-layer"){
				$(this).css("display", "none");
				$("#expand-custom-layer").css("display", "");
				$(this).nextAll("ul").eq(0).css("display", "none");
			}
		});
		$("#"+add_Div+" #layerSearch").keyup(function(){
			var searchKeyword = new RegExp($(this).val(), "i");
			$("#LayerList").find("input[type=checkbox]").each(function(idx){
				var li = $(this).parents("li").eq(0);
				if(searchKeyword.test(li.text())){
					li.css("display", "");
				}else{
					li.css("display", "none");
				}
			});
		});
	}
	
	//AddLayer 목록 추가 및 기능설정
	function _addLayerElement(layerId, layerNm){
		var chkCustomLayer = false;
		wavus.layer.findLayerById(layerId).then(function(layer){
			if(layer.get("custom") == "1"){
				chkCustomLayer = true;
			}
		});
		$("#LayerList ul").eq(chkCustomLayer == true ? 1 : 0).append("<li><input type='checkbox' id='add_"+layerId+"' name='add_"+layerId+"' value='"+layerNm+"'><label class='TOCaddLayerNm' for='add_"+layerId+"' title='"+layerNm+"'>"+layerNm+"</label></li>");
		var add_layerId = '#add_'+layerId;
		var addLi = $("#LayerList "+add_layerId).parents("li").eq(0);
		var layerLi = $("#"+layerId).parents("li").eq(0);
		
		if(chkCustomLayer){
			addLi.append("<span class='cusLayerDel' id='cusLayerDel-"+layerId+"'></span>");
		}
		layerLi.css("display", "none");
		
		$("#LayerList "+add_layerId).click(function(){
			if($(this).is(":checked")){
				$("#"+layerId).click();
				if($("#controlLayerButton").hasClass("on") == true){
					$(".controlVisibleHidden").css("visibility", "visible");
				}else{
					$(".controlVisibleHidden").css("visibility", "hidden");
				}
				layerLi.css("display", "");
				tocLayerSort();
			}else{
				layerLi.css("display", "none");	
				if($('#'+layerId).is(":checked")){
					$('#'+layerId).click();
				}
			}
			_changeTOCButtonVisible();
		});
		
		$("#LayerList #cusLayerDel-"+layerId).click(function(){
			if(!confirm(layerId+" 레이어를 삭제하시겠습니까?")){
				return;
			}
			var url="/file/json/delete.do";  
			$.ajax({      
		        type:"POST",  
		        data: {
		        	fileName:layerId,
		        	},
		        url:url,      
		        dataType:'json',      
		        success:function(args){   
		        	console.log(args);
		        }  
		    });
			wavus.layer.findLayerById(layerId).then(function(layer){
				_map.removeLayer(layer);
			});
			layerLi.remove();
			addLi.remove();
			_changeTOCButtonVisible();
		});
	}
	
	function _changeTOCButtonVisible(){
		var li_list = $("#mainTOC > li" ).filter(function(){ return $(this).css("display") != "none"; });
		if(li_list.length > 0){
			$(".TOCButtonDiv").css("display", "");
			$("#controlLayerButton").css("visibility", "visible");
			$("#addLayerLabel").css("display", "none");
		}else{
			$(".TOCButtonDiv").css("display", "none");
			$("#controlLayerButton").css("visibility", "hidden");
			$("#controlLayerButton").removeClass("on");
			$("#addLayerLabel").css("display", "");
		}
	}
	
	// 레이어 Feature 선택 시 Highlight 
	function _selectLayer(selLayer){
		buttonChange({cursor:""});
		layerOverPointer({ 
			cursor: 'pointer', 
			layerType: 'layer', 
			layerName: selLayer
		});
		_map.addInteraction(_dragBox);
		
		_map.on('singleclick', function(e) {
			getLayerFeatures({locValue : e.coordinate, selectLayer : selLayer});
		});
		_dragBox.on('boxend', function() {
			getLayerFeatures({locValue : _dragBox.getGeometry().getExtent(), selectLayer : selLayer});
		});
	}
	
	// features highlight
	function getLayerFeatures(param){
		var deferred = $.Deferred();
		var layerProjection = wavus.config.mapInfo.projection;
		var locValue = param.locValue;
		var layerName = param.selectLayer;
		var layerVisible;
		var layerType;
		var layerSource;
		var chkLayer = true;
		wavus.layer.findLayerById(layerName).then(function(layer){
			if(layer != undefined){
	        	layerVisible = layer.getVisible();
	        	layerSource = layer.getSource();
	        	layerType = layer.getSource() instanceof ol.source.ImageWMS ? "wms" : layer.getSource() instanceof ol.source.Vector ? "vector" : "";
			}else{
				chkLayer = false;
			}
        });
		if(chkLayer == true){
			console.log(layerType);
//			if(layerVisible == false){
//				return;
//			}
		}else{
			layerType = "wms";
		}
		var selectType = locValue.length == 2 ? "click" : locValue.length == 4 ? "box" : "";
		var features = [];
		if(layerType == "vector"){
			if(selectType == "click"){
				_map.forEachFeatureAtPixel(_map.getPixelFromCoordinate(locValue), function(feature, layer){
					if(layer.get("name") != undefined){
						if(layer.get("name").toLowerCase() == layerName.toLowerCase()){
							features.push(feature);
						}
					}
				});
			}else if(selectType == "box"){
				layerSource.forEachFeatureIntersectingExtent(locValue, function(feature) {
					features.push(feature);
				});
			}
			
		}else if(layerType == "wms"){
			var filter;
			if(selectType == "click"){
				filter = ol.format.filter.intersects("wkb_geometry", new ol.geom.Point(locValue), layerProjection);
			}else if(selectType == "box"){
				filter = ol.format.filter.intersects("wkb_geometry", ol.geom.Polygon.fromExtent(locValue), layerProjection);
			}
			var featureRequest = new ol.format.WFS().writeGetFeature({
				srsName: layerProjection,
				featureNS: "", // namespace
				featurePrefix: "", // workspace
				featureTypes: [layerName], // layer Array
				outputFormat: "application/json",
				filter: filter
			});
			$.ajax({
				url: wavus.config.info.gisServer.baseUrl + "/wfs",
				contentType: "text/xml",
				data: new XMLSerializer().serializeToString(featureRequest),
				method: "POST",
				async: false,
				dataType: "json",
				success: function(response){
					features = new ol.format.GeoJSON().readFeatures(response);
				}
			});
		}
		deferred.resolve(features);
		return deferred;
	}
	
	// 선택된 Features의 정보 호출
	function _infoSet(features){
		if(features.length >= 1000){
			if(features.length >= 10000){
				alert("선택한 Feature가 10,000개 이상이면 정보를 불러올 수 없습니다.");
				return;
			}
			if(confirm("선택한 Feature가 1,000개 이상이므로 정보출력 시 오래걸릴 수 있습니다.\n그래도 정보를 보시겠습니까 ?") == false){
				return;
			}
		}
		features.map(function(feature){
			var tmp_str = "";
			feature.getKeys().map(function(key){
				if(!(feature.get(key) instanceof Object)){
					tmp_str = tmp_str + "["+key+":"+feature.get(key)+"]";
				};
			});
			console.log(tmp_str);
		});
	}
	
	function removeTOCLayer(layerId){
		var url="/file/json/delete.do";  
		$.ajax({      
	        type:"POST",  
	        data: {
	        	fileName:layerId,
	        	},
	        url:url,      
	        dataType:'json',      
	        success:function(args){   
	        	console.log(args);
	        }  
	    });
		$("#LayerList #add_" + layerId).parents("li").eq(0).remove();
		$("#" + layerId).parents("li").eq(0).remove();
		_changeTOCButtonVisible();
	}
	
	function addAreaSelect(AreaDiv, multiChk, map){
		var area_sel = {sido : "sido_sel", sgg : "sgg_sel", emd : "emd_sel", ri : "ri_sel"};
		$("#"+AreaDiv).append("<span id='areaString' code=''>여기를 눌러 지역을 선택하십시오</span>");
		$("#"+AreaDiv).append("<div class='areaSelectBox' style='display:none;'></div>");
		var areaSelectBox = $("#"+AreaDiv+" .areaSelectBox");
		areaSelectBox.append("<div class='areaSelectDiv' id='"+area_sel.sido+"_div' style='width:28%;'><ul class='areaSelectUl' id='"+area_sel.sido+"'></ul></div>");
		areaSelectBox.append("<div class='areaSelectDiv' id='"+area_sel.sgg+"_div' style='width:30%;'><ul class='areaSelectUl' id='"+area_sel.sgg+"'></ul></div>");
		areaSelectBox.append("<div class='areaSelectDiv' id='"+area_sel.emd+"_div' style='width:23%;'><ul class='areaSelectUl' id='"+area_sel.emd+"'></ul></div>");
		areaSelectBox.append("<div class='areaSelectDiv' id='"+area_sel.ri+"_div' style='width:19%;'><ul class='areaSelectUl' id='"+area_sel.ri+"'></ul></div>");
		if(multiChk == true){
			areaSelectBox.append("<br /><div class='areaMultiSelectButton'></div>");
			areaSelectBox.find("div[class=areaMultiSelectButton]").append("<input type='button' class='small_btn_add' value='추가' id='addMultiSelectArea' name='addMultiSelectArea' style='margin-right: 10px;'/>");
			areaSelectBox.find("div[class=areaMultiSelectButton]").append("<input type='button' class='small_btn_del' value='삭제' id='delMultiSelectArea' name='delMultiSelectArea' />");
			areaSelectBox.append("<br /><div class='selectMultiArea'><ul class='selectMultiAreaList'></ul></div>");
		}
		areaSelectBox.append("<br /><div class='areaSelectButton'></div>");
		areaSelectBox.find("div[class=areaSelectButton]").append("<input type='button' class='small_btn_blue' value='적용' id='areaSelectOk' name='areaSelectOk' style='margin-right: 10px;'/>");
		areaSelectBox.find("div[class=areaSelectButton]").append("<input type='button' class='small_btn_blue' value='닫기' id='areaSelectClose' name='areaSelectClose' />");
		
		var sidoSel = $("#"+AreaDiv+" .areaSelectBox #"+area_sel.sido);
		var sggSel = $("#"+AreaDiv+" .areaSelectBox #"+area_sel.sgg);
		var emdSel = $("#"+AreaDiv+" .areaSelectBox #"+area_sel.emd);
		var riSel = $("#"+AreaDiv+" .areaSelectBox #"+area_sel.ri);
		
		var areaJson;
		$.ajax({
			url : '/spceInfo/geom/getAreaListJson.do',
			method: "POST",
			dataType: "json",
			async: false,
			success : function(data){
				areaJson = $.parseJSON(data);
			}
		});
		
		$("#"+AreaDiv+" #areaString").click(function(){
			if(areaSelectBox.css("display") == "none"){
				var selAreaJson = $.grep(areaJson, function(obj){ return obj.zone_cd == $("#"+AreaDiv+" #areaString").attr("code"); })[0];
				_areaSetting(AreaDiv, selAreaJson);
				areaSelectBox.css("display", "");
				if(selAreaJson != undefined){
					_areaSelectScrollTop(AreaDiv);
				}
				$(this).css("background", $(this).css("background").replace("arrow01", "arrow02"));
			}else{
				areaSelectBox.css("display", "none");
				$(this).css("background", $(this).css("background").replace("arrow02", "arrow01"));
			}
		});
		
		var selectEventFunc = function(selectType){
			var selArea = riSel.find("li[class=on]").attr("value") != "" && riSel.find("li[class=on]").attr("value") != undefined ? riSel.find("li[class=on]").attr("value")
							: emdSel.find("li[class=on]").attr("value") != "" && emdSel.find("li[class=on]").attr("value") != undefined ? emdSel.find("li[class=on]").attr("value")
							: sggSel.find("li[class=on]").attr("value") != "" && sggSel.find("li[class=on]").attr("value") != undefined ? sggSel.find("li[class=on]").attr("value")
							: sidoSel.find("li[class=on]").attr("value");
			if(selArea == undefined){
				alert("지역을 선택하십시오.");
				return;
			}
			var selAreaJson = $.grep(areaJson, function(obj){ return obj.zone_cd == selArea; })[0];
			var selAreaString = selAreaJson.sd_nm + " " + selAreaJson.sgg_nm + " " + selAreaJson.emd_nm + " " + selAreaJson.ri_nm;
			var areaFeature = selectAreaFeature(selArea);
			if(selectType == "ok"){
				wavus.highlight.getHighlightLayer().getSource().clear();
				$("#"+AreaDiv+" #areaString").attr("code", selArea);
				$("#"+AreaDiv+" #areaString").text(selAreaString);
				areaFit(areaFeature, { duration : 100 });
				if(map != undefined){				
					areaHighlight(areaFeature, 3500);
				}else{
					areaHighlight(areaFeature, 0);
				}
				areaSelectBox.css("display", "none");
				$("#"+AreaDiv+" #areaString").css("background", $("#"+AreaDiv+" #areaString").css("background").replace("arrow02", "arrow01"));
			}else if(selectType == "add"){
				if($("#"+AreaDiv+" .selectMultiArea .selectMultiAreaList").find("li[data-code=" + selArea + "]").length == 0){
					var tmpLi = $("<li class='' data-code='" + selArea + "' data-feature=''>" + selAreaString + "</li>");
					$("#"+AreaDiv+" .selectMultiArea .selectMultiAreaList").append(tmpLi);
					tmpLi.data("feature", areaFeature);
					areaFit(areaFeature, { duration : 100 });
					areaHighlight(areaFeature, 3500);
				}
			}
		}
		
		if(multiChk == true){
			$("#"+AreaDiv+" #addMultiSelectArea").click(function(){
				selectEventFunc("add");
			});
			$("#"+AreaDiv+" #delMultiSelectArea").click(function(){
				$("#"+AreaDiv+" .selectMultiArea .selectMultiAreaList").find("li[class=on]").remove();
			});
			$("#"+AreaDiv+" #areaSelectOk").click(function(){
				areaSelectBox.css("display", "none");
				wavus.highlight.getHighlightLayer().getSource().clear();
				$("#"+AreaDiv+" #areaString").css("background", $("#"+AreaDiv+" #areaString").css("background").replace("arrow02", "arrow01"));
				var otherAreaCnt = $("#"+AreaDiv+" .selectMultiArea .selectMultiAreaList").find("li").length - 1 > 0 ? " 외 " + ($("#"+AreaDiv+" .selectMultiArea .selectMultiAreaList").find("li").length - 1) + "건" : "";
				$("#"+AreaDiv+" #areaString").text($("#"+AreaDiv+" .selectMultiArea .selectMultiAreaList").find("li").eq(0).text() + otherAreaCnt);
				var multiAreaJsonArray = new Array();
				var areaListString = "";
				$("#"+AreaDiv+" .selectMultiArea .selectMultiAreaList").find("li").each(function(){
					var areaJsonInfo = new Object();
					areaJsonInfo.code = $(this).data("code");
					areaJsonInfo.feature = $(this).data("feature");
					areaJsonInfo.areaString = $(this).text();
					areaListString = areaListString + $(this).text() + "\n";
					multiAreaJsonArray.push(areaJsonInfo);
				});
				$("#"+AreaDiv+" #areaString").data("multi-area", multiAreaJsonArray);
				$("#"+AreaDiv+" #areaString").prop("title", areaListString.substring(0, areaListString.length - 2));
				if (AreaDiv == "analysisAreaDiv") {
					var _sel = $("#"+AreaDiv+" .selectMultiArea .selectMultiAreaList").find("li");
					if (_sel.size() > 0) getSelectedStatArea($("#"+AreaDiv+" .selectMultiArea .selectMultiAreaList").find("li").eq(0).text() + otherAreaCnt);
				}
				if (AreaDiv == "areaDiv") {
					var _sel = $("#"+AreaDiv+" .selectMultiArea .selectMultiAreaList").find("li");
					if (_sel.size() > 0) getSelectedLandInfoArea($("#"+AreaDiv+" .selectMultiArea .selectMultiAreaList").find("li").eq(0).text() + otherAreaCnt);
				}
			});
			$("#"+AreaDiv+" #areaSelectClose").click(function(){
				areaSelectBox.css("display", "none");
				$("#"+AreaDiv+" #areaString").css("background", $("#"+AreaDiv+" #areaString").css("background").replace("arrow02", "arrow01"));
				$("#"+AreaDiv+" .selectMultiArea .selectMultiAreaList").empty();
				$("#"+AreaDiv+" #areaString").removeData("multi-area");
				$("#"+AreaDiv+" #areaString").text("여기를 눌러 지역을 선택하십시오");
				showHidePanel(null);
			});
			$("#"+AreaDiv+" .selectMultiArea .selectMultiAreaList").click(function(e){
				var li = $(e.target);
				$(this).find("li").prop("class", "");
				li.prop("class", "on");
			});
		}else{
			$("#"+AreaDiv+" #areaSelectOk").click(function(){
				selectEventFunc("ok");
			});
			$("#"+AreaDiv+" #areaSelectClose").click(function(){
				areaSelectBox.css("display", "none");
				$("#"+AreaDiv+" #areaString").css("background", $("#"+AreaDiv+" #areaString").css("background").replace("arrow02", "arrow01"));
			});
		}
		
		$.each($.grep(areaJson, function(v, i){ return v.sgg_nm == ""}), function(key, value){
			sidoSel.append("<li class='' value='" + value.zone_cd + "'>" + value.sd_nm + "</li>");
		});
		
		areaSelectBox.find("ul[class=areaSelectUl]").click(function(e){
			var li = $(e.target);
			if(!li.is("li")){
				return;
			}
			$(this).find("li").prop("class", "");
			li.prop("class", "on");
			var sd_txt = sidoSel.find("li[class=on]").text();
			if($(this).prop("id") == area_sel.sido){
				sggSel.html("<li value=''>전체</li>");
				emdSel.html("<li value=''>전체</li>");
				riSel.html("<li value=''>전체</li>");
				$.each($.grep(areaJson, function(v, i){ return sd_txt == v.sd_nm && v.sgg_nm != "" && v.emd_nm == ""}), function(key, value){
					sggSel.append("<li class='' value='" + value.zone_cd + "'>" + value.sgg_nm + "</li>");
				});
			}
			var sgg_txt = sggSel.find("li[class=on]").text();
			if($(this).prop("id") == area_sel.sgg){
				emdSel.html("<li value=''>전체</li>");
				riSel.html("<li value=''>전체</li>");				
				$.each($.grep(areaJson, function(v, i){ return sd_txt == v.sd_nm && sgg_txt == v.sgg_nm && v.emd_nm != "" && v.ri_nm == ""}), function(key, value){
					emdSel.append("<li class='' value='" + value.zone_cd + "'>" + value.emd_nm + "</li>");
				});
			}
			var emd_txt = emdSel.find("li[class=on]").text();
			if($(this).prop("id") == area_sel.emd){
				riSel.html("<li value=''>전체</li>");				
				$.each($.grep(areaJson, function(v, i){ return sd_txt == v.sd_nm && sgg_txt == v.sgg_nm && emd_txt == v.emd_nm && v.ri_nm != ""}), function(key, value){
					riSel.append("<li class='' value='" + value.zone_cd + "'>" + value.ri_nm + "</li>");
				});
			}
		});
		if(map != undefined){
			map.on("moveend", function (e) {
				changeAreaSelect(AreaDiv, _map.getView().getCenter());
			});
		}
	}
	
	function selectAreaFeature(area_cd){
		var feature;
		$.ajax({
			url : '/spceInfo/geom/getAreaGeom.do',
			method: "POST",
			data: {"area_cd" : area_cd},
			dataType: "json",
			async: false,
			success : function(data){
				if (data.areaGeom == null) {
					feature = null;
				} else {
					feature = new ol.format.WKT().readFeature(data.areaGeom, {
						dataProjection: wavus.config.mapInfo.projection,
						featureProjection: _map.getView().getProjection()
					});
				}
			}
		});
		return feature;
	}
	
	function areaFit(feature, option){
		var durTime = option.duration || 100;
		_map.getView().fit(feature.getGeometry().getExtent(), { duration : durTime });
		setTimeout(function(){ 
			_map.getView().animate({center : feature.getGeometry().getInteriorPoints().getCoordinates()[0], duration : 50}); 
		}, durTime);
	}
	
	function shiftAreaFit(shiftWidthPixel, extent){
		if( extent == null || extent == undefined){
			extent = wavus.map.getMap().getView().calculateExtent(wavus.map.getMap().getSize());
		}
		
		var leftPixel = parseInt(shiftWidthPixel);
        
        var xLength = leftPixel * (extent[2] - extent[0]) / _map.getSize()[0];  
        extent[0] = extent[0] - xLength;

        _map.getView().fit(extent);
	}
	
	function areaHighlight(feature, setTime){
		var delayTime = setTime || 0;
		wavus.highlight.getHighlightLayer().getSource().addFeature(feature);
		if(delayTime > 0){
			setTimeout(function(){
				wavus.highlight.getHighlightLayer().getSource().removeFeature(feature);
			}, delayTime);
		}
	}
		
	function changeAreaSelect(AreaDiv, centerPoint){
		if(centerPoint == undefined){
			return;
		}
		if(isNaN(centerPoint[0])){
			return;
		}
		$.ajax({
			url : '/spceInfo/geom/getAreaSelect.do',
			method: "POST",
			data: {"centerX" : centerPoint[0], "centerY" : centerPoint[1]},
			dataType: "json",
			success : function(data){
				var selAreaJson = $.parseJSON(data);
				if(selAreaJson == null){
					return;
				}
				_areaSetting(AreaDiv, selAreaJson);
			}
		});
	}
	
	function _areaSetting(AreaDiv, selAreaJson){
		$("#"+AreaDiv+" .areaSelectUl").find("li").prop("class", "");
		var sdList = $("#"+AreaDiv+" .areaSelectDiv").eq(0);
		var sggList = $("#"+AreaDiv+" .areaSelectDiv").eq(1);
		var emdList = $("#"+AreaDiv+" .areaSelectDiv").eq(2);
		var riList = $("#"+AreaDiv+" .areaSelectDiv").eq(3);
		if(selAreaJson == undefined){
			sggList.find("ul").html("");
			emdList.find("ul").html("");
			riList.find("ul").html("");
			return;
		}
		sdList.find("li").filter(function(){ return $(this).text() == selAreaJson.sd_nm; }).prop("class", "on").click();
		sggList.find("li").filter(function(){ return ($(this).text() == "전체" ? "" : $(this).text()) == selAreaJson.sgg_nm; }).prop("class", "on").click();
		emdList.find("li").filter(function(){ return ($(this).text() == "전체" ? "" : $(this).text()) == selAreaJson.emd_nm; }).prop("class", "on").click();
		riList.find("li").filter(function(){ return ($(this).text() == "전체" ? "" : $(this).text()) == selAreaJson.ri_nm; }).prop("class", "on");
		
		var selAreaString = selAreaJson.sd_nm + " " + selAreaJson.sgg_nm + " " + selAreaJson.emd_nm + " " + selAreaJson.ri_nm;
		$("#"+AreaDiv+" #areaString").attr("code", selAreaJson.zone_cd);
		$("#"+AreaDiv+" #areaString").text(selAreaString);
	}
	
	function _areaSelectScrollTop(AreaDiv){
		var sdList = $("#"+AreaDiv+" .areaSelectDiv").eq(0);
		var sggList = $("#"+AreaDiv+" .areaSelectDiv").eq(1);
		var emdList = $("#"+AreaDiv+" .areaSelectDiv").eq(2);
		var riList = $("#"+AreaDiv+" .areaSelectDiv").eq(3);
		
		sdList.scrollTop(sdList.find("li[class=on]").offset().top - sdList.offset().top + sdList.scrollTop());
		sggList.scrollTop(sggList.find("li[class=on]").offset().top - sggList.offset().top + sggList.scrollTop());
		emdList.scrollTop(emdList.find("li[class=on]").offset().top - emdList.offset().top + emdList.scrollTop());
		riList.scrollTop(riList.find("li[class=on]").offset().top - riList.offset().top + riList.scrollTop());
	}
	
	var layerArray = new Array();

	function setLayerArray(param) {
		layerArray.push(param);
		return layerArray;
	}
	
	function getLayerArray() {
		return layerArray;
	}
	
	function vectorNameChange(layerId, layerNm){
		$("#"+layerId).val(layerNm);
		$("label[for="+layerId+"].TOCLayerNm").prop("title", layerNm);
		$("label[for="+layerId+"].TOCLayerNm").text(layerNm);
		$("#add_"+layerId).val(layerNm);
		$("label[for=add_"+layerId+"]").prop("title", layerNm);
		$("label[for=add_"+layerId+"]").text(layerNm);
	}
	
	// cursor 변경 및 기존 기능 초기화
	function buttonChange(param){
		var cursorStyle = param.cursor || "default";
		_map.removeEventListener('click');
//		_map.removeEventListener('singleclick');
		_map.removeEventListener('pointermove');
		_map.removeInteraction(wavus.draw.getTranslateLayer());
		_map.removeInteraction(_dragBox);
		_dragBox.removeEventListener('boxstart');
		_dragBox.removeEventListener('boxend');
		wavus.draw.removeDraw();
		_map.on('pointermove', function() {
			_map.getTarget().style.cursor = cursorStyle;
		});
	}
	
	// 화면상 특정 layer에 마우스over 되었을 경우 cursor 설정 기능
	function layerOverPointer(param){
		_map.on("pointermove", function(e){
			var cursor = param.cursor || "default";
			var layerType = param.layerType || "";
			var layerName = param.layerName || "";
			if (e.dragging) return;
			try{
				var hit = _map.forEachLayerAtPixel(e.pixel, function(layer) {
					if(layerType.toLowerCase() == "layer"){
						if(layer.get("name").toLowerCase() == layerName.toLowerCase()){
							return true;
						}
					}else if(layerType.toLowerCase() == "draw"){
						if(layerName.toLowerCase() == "text"){
							return _map.forEachFeatureAtPixel(e.pixel, function(feature){
								if(feature.get('feature_type') == "text"){
									return true;
								}
							});
						}else{
							if(layer == wavus.layer.getDrawLayer()){
								return _map.forEachFeatureAtPixel(e.pixel, function(feature){
									if(feature.get('feature_type') == layerName){
										return true;
									}
								});
							}
						}
					}
				});
			}catch(e){
				return;
			}
			_map.getTarget().style.cursor = hit ? cursor : 'default';
		});
	}
	
	// 인터렉션의 사용여부를 제어하는 기능
	function controlInteraction(interact_obj, active){
	    var interactions = _map.getInteractions().getArray();
    	$.each($.grep(interactions, function(interaction, i){ return interaction instanceof interact_obj; }), function(idx, interaction){
    		interaction.setActive(active);
    	});		
	}
	
	function controlMapController(control_obj, active){
		if(active){
	    	_map.addControl(control_obj);
	    }else{
	    	var controls = _map.getControls().getArray();
	    	$.each($.grep(controls, function(control, i){ return control instanceof control_obj; }), function(idx, control){
	    		_map.removeControl(control);
	    	});
	    }
	}
	
	function singleClickInteraction(interact_obj, feature){
		try{
			interact_obj.getFeatures().clear();
		}
		catch(e){
			console.log(e);
		}
		
		if(feature != null)
			interact_obj.getFeatures().push(feature);

		interact_obj.dispatchEvent({type:'click'})
	}
	
	function getDragBox(){
		return _dragBox;
	}
	
	function init(map){
		_map = map;
		_map.on("moveend", function (e) {
			_recordHistory(); // 이전, 다음을 위한 Center, Zoom Array 설정
		});
	}

		//클러스터 click시 스타일 변경
		var clickInteraction = new ol.interaction.Select({
			layers : function(layer){
				return layer.get('selectable') == true;
			},
			condition: function(evt){
				return evt.type == 'click';
			},
			style : function(feature){
				var size  = feature.get('features').length;
				style = [new ol.style.Style({
					image: new ol.style.Icon ( (
							{
								anchor :[ 0.5 , 18 ] ,
								anchorXUnits : 'fraction' ,
								anchorYUnits : 'pixels' ,
								src : '/images/rdm/map/pin_yellow3.png',
//									opacity : 0.8,
								scale : 1.0
							} ) ),
							text: new ol.style.Text({
								font: 'Bold 16px Arial ',
								text: size.toString(),
								fill: new ol.style.Fill({
									color: '#333'
								})
							})
				})];
				
				return style;
			}
		});
		
		clickInteraction.on("click", function(){
			console.log(args);
		});
		
		//클러스터 클릭 이벤트    //extInfo ==> 저장소 외 정보 
		var OpenPopupOverlay = function(param){
			var map = wavus.map.getMap();
			
			wavus.layer.removeLayer(map,"selectLayer")
			
			var feature = map.forEachFeatureAtPixel(param, function (feature){
					return feature;
			});
			
			if(feature && typeof feature.get('features') != 'undefined' && feature.get('features').length>0){
				locationSearch.obj.olpopup2.setPosition(null);
				
				if(feature.get('features')[0].get("hoAdmNo") ==  undefined) return;
					var cfeatures = feature.get('features');
					
					if(cfeatures.length >= 1){
						$(".popup-cluster-header").css("display","none");
						$(".popup-cluster-content").css("display","none");
						
						var selectBox2 = "";
						
						var fInfo = locationSearch.func.getSearchList(cfeatures);
						locationSearch.data.filterSearchList = fInfo;
						for(var ii=0; ii < locationSearch.data.filterSearchList.length; ii++){
							locationSearch.data.filterSearchList[ii].no = ii + 1;
							selectBox2 += ls_cmm.lcCreateDrawGridHtml(locationSearch.data.filterSearchList[ii]);
						}
						
						
						 $("#hoTotalCount").text(cfeatures.length);
						 $("#addrList2").html(selectBox2); 
						
						if(map.getView().getZoom() > 14){
							ls_cmm.displayFeatureInfo( fInfo[0]);
						}else{
							commonUtils.customAlert("지도를 더 확대하시면 선택한 임대주택의 단지/동 정보를 조회할 수 있습니다.");
						}
						
						//전체보기 버튼 활성화
						$("#btnLcViewAll").show();
					}
				
			}
		}; 

	var module = {
		goHome : goHome,
		addControlBox : addControlBox,
		eraseAll : eraseAll,
		addToolBox : addToolBox,
		addBgMapBox : addBgMapBox,
		addBgMapBox_ver2 : addBgMapBox_ver2,
		bgMapChange : bgMapChange,
		addTOCFrame : addTOCFrame,
		addTOCElement : addTOCElement,
		addLayerFrame : addLayerFrame,
		addAreaSelect : addAreaSelect,
		selectAreaFeature : selectAreaFeature,
		areaFit : areaFit,
		shiftAreaFit: shiftAreaFit,
		areaHighlight : areaHighlight,
		changeAreaSelect : changeAreaSelect,
		buttonChange : buttonChange,
		layerOverPointer : layerOverPointer,
		getDragBox : getDragBox,
		controlInteraction : controlInteraction,
		singleClickInteraction: singleClickInteraction,
		controlMapController : controlMapController,
		tocLayerSort : tocLayerSort,
		getLayerFeatures : getLayerFeatures,
		vectorNameChange : vectorNameChange,
		removeTOCLayer : removeTOCLayer,
		clearLcLayer : clearLcLayer,
		clickInteraction : clickInteraction,
		OpenPopupOverlay : OpenPopupOverlay,
		init : init,
		
		getLayerArray : getLayerArray,
		setLayerArray : setLayerArray,
		
		goPrevNext : _addControlMap
	};
	
	return module;
	
})();