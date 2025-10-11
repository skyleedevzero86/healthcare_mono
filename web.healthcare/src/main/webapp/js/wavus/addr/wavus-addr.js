window.wavus = window.wavus || {};

wavus.addr = (function () {
	
	var _frame;
	var page_addr = {"totalCount": 0, "nowPage": 1, "listSize": 10, data: []};
	var page_keyword = {"totalCount": 0, "nowPage": 1, "listSize": 10, data: []};
	var page_dev = {"totalCount": 0, "nowPage": 1, "listSize": 10, data: []};
	var sel_dev_layer = "";
	
	//검색영역 생성
	function _addToolBox() {
		var html = ''
		+ '<div class="tabs_wrap"">'
		+     '<div class="tab" style="width:33.3%">'
		+         '<input type="radio" id="ra_address" name="ra_search_type" value="주소" checked><label for="ra_address" class="main">주소</label>'
		+         _addAddrBox()
		+     '</div>'
		+     '<div class="tab" style="width:33.3%">'
		+         '<input type="radio" id="ra_keyword" name="ra_search_type" value="키워드"><label for="ra_keyword" class="main">키워드</label>'
		+         _addKeywordBox()
		+     '</div>'
		+     '<div class="tab" style="width:33.3%">'
		+         '<input type="radio" id="ra_dev" name="ra_search_type" class="daum" value="사업지구"><label for="ra_dev" class="main">사업지구</label>'
		+          _addDevBox()
		+     '</div>'
		+ '</div>';
		$("#" + _frame).append(html);
	}
	
	function _addAddrBox() {
		var html_box = ''
		+ '<div class="tab_content">'
		+     '<ul class="aside_list2">'
		+         '<li class="ac_active" style="width: 100%;">'
		+             '<input type="text" id="addr_txt" name="addr_txt" value="" style="width: 205px;" placeholder="주소를 입력하세요."/>'
		+             '<input type="button" class="small_btn_blue" id="addr_b" name="addr_b" value="검색" style="float: right;"/>'
		+         '</li>'
		+     '</ul>'
		+	   '<div class="ac_con" style="padding: 0 10px;">'
		+         '<div class="aside_li_con">'
		+             '<p class="total_list" style="margin-top: 5px;">전체&nbsp;<span id="addr_result_cnt" class="blue_color bold">0</span>건</p>'
		+             '<table class="table_type1">'
		+                 '<colgroup><col style="width:40px;"><col style="width:auto;"></colgroup>'
		+                 '<thead><tr><th>번호</th><th>주소</th></tr></thead>'
		+                 '<tbody id="addr_result"></tbody>'
		+            '</table>'
		+            '<div id="pageListTable_addr" class="pageListTable"></div>'
		+         '</div>'
		+     '</div>'
		+ '</div>'
		return html_box;
	}
	
	function _addKeywordBox() {
		var html_box = ''
		+ '<div class="tab_content">'
		+     '<ul class="aside_list2">'
		+         '<li class="ac_active" style="width: 100%;">'
		+             '<input type="text" id="keyword_txt" name="keyword_txt" value="" style="width: 205px;" placeholder="키워드를 입력하세요."/>'
		+             '<input type="button" class="small_btn_blue" id="keyword_b" name="keyword_b" value="검색" style="float: right;"/>'
		+         '</li>'
		+     '</ul>'
		+     '<div class="ac_con" style="padding: 0 10px;">'
		+         '<div class="aside_li_con">'
		+             '<p class="total_list" style="margin-top: 5px;">전체&nbsp;<span id="keyword_result_cnt" class="blue_color bold">0</span>건</p>'
		+             '<table class="table_type1">'
		+                 '<colgroup><col style="width:40px;"><col style="width:auto;"></colgroup>'
		+                 '<thead><tr><th>번호</th><th>주소</th></tr></thead>'
		+                 '<tbody id="keyword_result"></tbody>'
		+             '</table>'
		+             '<div id="pageListTable_keyword" class="pageListTable"></div>'
		+         '</div>'
		+     '</div>'
		+ '</div>'
		return html_box;
	}
	
	function _addDevBox() {
		var deferred = $.Deferred();
		var html_box = '';
		_getDevList().then(function (json) {
			html_box += '<div class="tab_content"><ul class="aside_list2"><li class="ac_active" style="padding: 0; border: none;">';
			html_box += '<select id="sel_dev_type" style="width: 100%; height: 28px; margin: 0;">';
			html_box += '<option value="" style="color: #a10000;">사업지구를 선택하세요.</option>';
			_.each(json.devList, function (data) {
				html_box += '<option value="' + data.eng_name + '">' + data.kor_name + '(' + wavus.util.comma(data.cnt) + '건)</option>';
			});
			html_box += '</select>';
			html_box += '</li></ul><div class="ac_con">';
			html_box += '<div class="aside_li_con">';
			html_box += '<p class="total_list" style="margin-top: 5px;">전체&nbsp;<span id="dev_result_cnt" class="blue_color bold">0</span>건';
			html_box += '<input type="button" class="small_btn_blue" id="dev_b" name="dev_b" value="검색" style="float: right;"/>';
			html_box += '<input type="text" id="dev_txt" name="dev_txt" value="" style="width: 50%; float: right; margin: 0 5px 5px 0;" placeholder="검색어를 입력하세요."/>';
			html_box += '</p>';
			html_box += '<table class="table_type1"><colgroup><col style="width:40px;"><col style="width:auto;"></colgroup><thead><tr><th>번호</th><th>지구명</th></tr></thead><tbody id="dev_result"></tbody></table>';
			html_box += '<div id="pageListTable_dev" class="pageListTable"></div></div></div></div>';
			html_box += '<div class="selectMultiDev"><ul class="selectMultiDevList"></ul></div>'
			html_box += '<div style="padding: 15px 0 10px 0; text-align: center;">'
			html_box += '<input type="button" class="small_btn_blue" value="적용" style="margin-right: 10px;" onClick="wavus.addr._returnCallFunction(); showHidePanel(null);">'
			html_box +=  '<input type="button" class="small_btn_blue" value="닫기" onClick="wavus.addr.vsibleDevLayer(null); showHidePanel(null);">'
			html_box +='</div>';
			deferred.resolve(html_box);
		});
		return deferred;
	}
	
	function _getDevList() {
		var deferred = $.Deferred();
		$.ajax({
			type: "GET",
			url : "/spceInfo/geom/getDevList.do",
			dataType: "json",
			success: function (json) {
				deferred.resolve(json);
			},
	        error: function (xhr, status, error) {
	        	console.log(status);
			}
	    });
		
		return deferred;
	}
	
	//이벤트 생성
	function _addEvent() {
		//주소
		$("#" +_frame + " #addr_b").click(function (e, page) {
			$(".black_bg_layer").show();
			if (page == undefined) page_addr.nowPage = 1;
			if ($("#" +_frame + "_ra_address").prop("checked")) {
				daum("address", $.trim($("#" +_frame + " #addr_txt").val())).then(function (json) {
					$("#" +_frame + " #addr_result").html("");
					_parseJson("address", json);
				});
				$(".black_bg_layer").hide();
			}
		});
		
		//키워드
		$("#" +_frame + " #keyword_b").click(function (e, page) {
			$(".black_bg_layer").show();
			if (page == undefined) page_keyword.nowPage = 1;
			if ($("#" +_frame + "_ra_keyword").prop("checked")) {
				daum("keyword", $.trim($("#" +_frame + " #keyword_txt").val())).then(function (json) {
					$("#" +_frame + " #keyword_result").html("");
					_parseJson("keyword", json);
				});
				$(".black_bg_layer").hide();
			}
		});
		
		//사업지구 select
		$("#" +_frame + " #sel_dev_type").on("change", function () {
			wavus.addr.vsibleDevLayer(null);
			
			var dev_layer = $(this).val();
			if (dev_layer != "") {
				wavus.addr.vsibleDevLayer(dev_layer);
				$("#" +_frame + " #dev_txt").val("");
				$("#" +_frame + " #dev_b").trigger("click");
			} else {
				$("#" +_frame + " #dev_result_cnt").html("0");
				$("#" +_frame + " #dev_txt").val("");
				$("#" +_frame + " #dev_result").html("");
				$("#" +_frame + " #pageListTable_dev").html("");
			}
		});
		
		//사업지구 검색어 버튼 click
		$("#" +_frame + " #dev_b").click(function (e, page) {
			$(".black_bg_layer").show();
			if (page == undefined) page_dev.nowPage = 1;
			if ($("#" +_frame + " #sel_dev_type").val() != "") {
				$.ajax({
					data : "GET",
					url : "/spceInfo/geom/getDevGeom.do",
					dataType : "json",
					data : {
						"layer_name" : $("#" +_frame + " #sel_dev_type").val(),
						"search_txt" : encodeURIComponent($.trim($("#" +_frame + " #dev_txt").val())),
						"nowPage" : page_dev.nowPage,
						"listSize": page_dev.listSize
					},
					success: function (result) {
						_devList(result);
			        },
			        error: function (xhr, status, error) {
						console.log(status);
					}
				});
				$(".black_bg_layer").hide();
			}
		});
		
		//enter key
		$("#" +_frame + " #addr_txt").keypress(function (e) { if (e.keyCode == 13) $("#" +_frame + " #addr_b").trigger("click"); });
		$("#" +_frame + " #keyword_txt").keypress(function (e) { if (e.keyCode == 13) $("#" +_frame + " #keyword_b").trigger("click"); });
		$("#" +_frame + " #dev_txt").keypress(function (e) { if (e.keyCode == 13) $("#" +_frame + " #dev_b").trigger("click"); });
	}
	
	/**
	 * daum api를 이용한 주소 검색
	 * @param1 api
	 * @param2 txt
	 * @return json
	 */
	function daum(api, txt) {
		var deferred = $.Deferred();
		if (txt.length == 0) return deferred;
		
		var url = "https://dapi.kakao.com";
		switch (api) {
		case "address": url += "/v2/local/search/address.json"; break;
		case "keyword": url += "/v2/local/search/keyword.json"; break;
		case "category": url += "/v2/local/search/category.json"; break;
		case "coord2regioncode": url += "/v2/local/geo/coord2regioncode.json"; break;
		case "coord2address": url += "/v2/local/search/keyword.json"; break;
		default: url +=  "/v2/local/search/keyword.json"; break;
		}

		var json;
		$.ajax({
			type: "GET",
			url: url + "?query=" + encodeURI(txt) + "&page=" + (api=="address"?page_addr.nowPage:page_keyword.nowPage) + "&size=" + (api=="address"?page_addr.listSize:page_keyword.listSize),
			dataType: "json",
			beforeSend: function (xhr) {
	            xhr.setRequestHeader("Authorization", "KakaoAK a0b5ba8e2575b4cbaf45c1d665dfdd0c");
	            xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
			},
			success: function (result) {
				json = result;
				deferred.resolve(json);
	        },
	        error: function (xhr, status, error) {
				console.log(status);
			}
	    });
		
		return deferred;
	}
	
	//결과 json 처리
	function _parseJson(type, json) {
		if (json.meta.pageable_count > 0) {
			var meta = json.meta;
			var documents = json.documents;
			var features = new Array();
			
			if (type == "address") {
				page_addr.totalCount = json.meta.pageable_count;
				$("#" +_frame + " #addr_result_cnt").html(page_addr.totalCount);
				arrayPageList("pageListTable_addr", page_addr.totalCount, page_addr.listSize, page_addr.nowPage);
				
				$(documents).each(function (idx) {
					var item = documents[idx];
					_daumAddressList(type, idx, item);
					_addMarker(type, features, idx);
				});
				showMarker(features);
				wavus.map.getView().setZoom(17);
				wavus.map.getView().setCenter([page_addr.data[0].proj[0], page_addr.data[0].proj[1]]);
			} else if (type == "keyword") {
				page_keyword.totalCount = json.meta.pageable_count;
				$("#" +_frame + " #keyword_result_cnt").html(page_keyword.totalCount);
				arrayPageList("pageListTable_keyword", page_keyword.totalCount, page_keyword.listSize, page_keyword.nowPage);
				
				$(documents).each(function (idx) {
					var item = documents[idx];
					_daumKeywordList(type, idx, item);
					_addMarker(type, features, idx);
				});
				wavus.map.getView().setZoom(17);
				wavus.map.getView().setCenter([page_keyword.data[0].proj[0], page_keyword.data[0].proj[1]]);
			}
			showMarker(features);
		} else {
			if (type == "address") {
				$("#" +_frame + " #pageListTable_addr").html("");
				$("#" +_frame + " #addr_result_cnt").html(0);
				$("#" +_frame + " #addr_result").html('<tr><td colspan="2">검색결과가 없습니다.</td></tr>');
			} else if (type == "keyword") {
				$("#" +_frame + " #pageListTable_keyword").html("");
				$("#" +_frame + " #keyword_result_cnt").html(0);
				$("#" +_frame + " #keyword_result").html('<tr><td colspan="2">검색결과가 없습니다.</td></tr>');
			}
		}
	}
	
	//주소검색
	function _daumAddressList(type, idx, item) {
		item.proj = ol.proj.transform([item.x, item.y], 'EPSG:4326', "EPSG:5174");
		page_addr.data[idx] = item;

		var info = '';
		if (item.address != null) {
			if (item.address.b_code != "") info += '<p>법정동: ' + item.address.b_code + '</p>'; 
			if (item.address.h_code != "") info += '<p>행정동: ' + item.address.h_code + '</p>';
		} else {
			if (item.road_address.road_name != "") info += '<p>도로명: ' + item.road_address.road_name + '</p>'; 
			if (item.road_address.building_name != "") info += '<p>건물명: ' + item.road_address.building_name + '</p>';
		}
		
		var html = '<tr><td class="center">' + parseInt((page_addr.nowPage-1) * page_addr.listSize + idx + 1) + '</td>'
		+ '<td><a href="javascript:void(0);" onclick="wavus.addr.addressListClick(' + (idx + 1) + ',event);">' + ((item.road_address_name==undefined||item.road_address_name=="")?item.address_name:item.road_address_name)  + '</a>' + info + '</td></tr>';
		$("#" +_frame + " #addr_result").append(html);
	}
	
	//키워드검색
	function _daumKeywordList(type, idx, item) {
		item.proj = ol.proj.transform([item.x, item.y], 'EPSG:4326', "EPSG:5174");
		page_keyword.data[idx] = item;

		var html = '<tr><td class="center">' + parseInt((page_keyword.nowPage-1) * page_keyword.listSize + idx + 1) + '</td>'
		+ '<td><a href="javascript:void(0);" onclick="wavus.addr.keywordListClick(' + (idx + 1) + ',  event);">' + ((item.road_address_name==undefined||item.road_address_name=="")?item.address_name:item.road_address_name)  + '</a><p>' + item.place_name + '</p></td></tr>';
		$("#" +_frame + " #keyword_result").append(html);
	}
	
	//사업지구검색
	function _devList(data) {
		page_dev.data = data;
		$("#" +_frame + " #dev_result").html("");
		
		if (data != "") {
			page_dev.totalCount = data[0].tot_cnt;

			$(data).each(function (idx) {
				var dev_name = "";
				if (data[idx].dstrcnm != undefined) dev_name = data[idx].dstrcnm;
				else if (data[idx].hsmpnm != undefined) dev_name = data[idx].hsmpnm;
				else if (data[idx].danji_snm != undefined) dev_name = data[idx].danji_snm;
				else if (data[idx].dan_name != undefined) dev_name = data[idx].dan_name;
				else if (data[idx].alias != undefined) dev_name = data[idx].alias;
				
				var html = '<tr><td class="center">' + data[idx].rnum + '</td>'
				+ '<td>'
				+ '<a href="javascript:void(0);" onclick="wavus.addr.devListClick(' + (idx + 1) + ', \'' + dev_name + '\', event);">' + (dev_name==""?"명칭 없음":dev_name)  + '</a>'
				+ '<button class="small_btn_maploc" onclick="wavus.addr.devListMaplocClick(' + (idx + 1) + ', \'' + dev_name + '\', event);">위치보기</button>'
				+ '</td></tr>';
				$("#" +_frame + " #dev_result").append(html);
			});
	
			arrayPageList("pageListTable_dev", page_dev.totalCount, page_dev.listSize, page_dev.nowPage);
		} else {
			page_dev.totalCount = 0;
			$("#" +_frame + " #pageListTable_dev").html("");
			$("#" +_frame + " #dev_result").html('<tr><td colspan="2">검색결과가 없습니다.</td></tr>');
		}
		
		$("#" +_frame + " #dev_result_cnt").html(page_dev.totalCount);
	}
	
	//주소리스트 클릭
	function addressListClick(idx, event) {
		var features = new Array();
		$(page_addr.data).each(function (idx) {
			_addMarker("address", features, idx);
		});
		
		var item = page_addr.data[idx - 1];
		showMarker(features);
        wavus.map.getView().setZoom(18);
		wavus.map.getView().setCenter([item.proj[0], item.proj[1]]);
		
		//법정동 우선
		if (item.address == null) {
			wavus.controller.getLayerFeatures({locValue : [item.proj[0], item.proj[1]], selectLayer : "al_11_d002_20191102"}).then(function (features) {
				wavus.controller.areaHighlight(features[0], 3000);
			});
		} else {
			var _code = (item.address.b_code ==  "" ?  item.address.h_code : item.address.b_code);
			var areaFeature = wavus.controller.selectAreaFeature(_code);
			
			if (areaFeature == null) {
				wavus.controller.getLayerFeatures({locValue : [item.proj[0], item.proj[1]], selectLayer : "al_11_d002_20191102"}).then(function (features) {
					wavus.controller.areaHighlight(features[0], 3000);
				});
			} else {
				wavus.highlight.getHighlightLayer().getSource().clear();
				wavus.controller.areaFit(areaFeature, { duration : 100 });
				if (map != undefined) {				
					wavus.controller.areaHighlight(areaFeature, 3500);
				} else {
					wavus.controller.areaHighlight(areaFeature, 0);
				}
			}
		}
	}
	
	//키워드리스트 클릭
	function keywordListClick(idx, event) {
		var features = new Array();
		$(page_keyword.data).each(function (idx) {
			_addMarker("keyword", features, idx);
		});
		
		showMarker(features);
        wavus.map.getView().setZoom(18);
		wavus.map.getView().setCenter([page_keyword.data[idx - 1].proj[0], page_keyword.data[idx - 1].proj[1]]);
		wavus.controller.getLayerFeatures({locValue : [page_keyword.data[idx - 1].proj[0], page_keyword.data[idx - 1].proj[1]], selectLayer : "al_11_d002_20191102"}).then(function (features) {
			wavus.controller.areaHighlight(features[0], 5000);
		});
	}
	
	//사업지구리스트 클릭
	function devListClick(idx, dev_name, event) {
		var itemData = page_dev.data[idx-1];
		var tmpLi = $('<li class="">' + (dev_name==""?"명칭 없음":dev_name) + '</li>');
		$(tmpLi).on("click", function () {
			$(this).remove();
		});
		$(tmpLi).data("dev-geom", itemData.geom);
		
		if ($("#" + _frame + " .selectMultiDev .selectMultiDevList li").size() == 0) {
			$("#" + _frame + " .selectMultiDev .selectMultiDevList").append(tmpLi);
		} else {
			var isDup = false;
			_.each($("#" + _frame + " .selectMultiDev .selectMultiDevList li"), function (data, index) {
				if ((dev_name=="명칭 없음"?"":dev_name) == ($(data).html()=="명칭 없음"?"":$(data).html()) && $(data).data("dev-geom") == $(tmpLi).data("dev-geom")) isDup = true;
			});
			if (!isDup) $("#" + _frame + " .selectMultiDev .selectMultiDevList").append(tmpLi);
		}
	}
	
	//사업지구리스트 위치보기 클릭
	function devListMaplocClick(idx, dev_name, event) {
		var itemData = page_dev.data[idx-1];
		var pointX = parseFloat(itemData.center.replace("POINT", "").replace("(", "").replace(")", "").split(" ")[0]);
		var pointY = parseFloat(itemData.center.replace("POINT", "").replace("(", "").replace(")", "").split(" ")[1]);
        wavus.map.getView().setZoom(18);
		wavus.map.getView().setCenter([pointX , pointY]);
	}
	
	//마커 등록
	function _addMarker(type, features, idx) {
		var item = null;
		if (type == "address") item = page_addr.data[idx];
		else item = page_keyword.data[idx];
		
		features[idx] = new ol.Feature({
			geometry: new ol.geom.Point([item.proj[0], item.proj[1]]),
			address: item.address_name==undefined?item.address:item.address_name,
            population: 4000,
            rainfall: 500
        });
        var iconStyle = new ol.style.Style({
            image: new ol.style.Icon(({
                anchor: [0.5, 46],
                anchorXUnits: "fraction",
                anchorYUnits: "pixels",
                src: "../images/lieb/map/icon_map.png"
            })),
            text: new ol.style.Text({
            	text: item.address_name,
            	font: 'bold 13px Arial',
            	offsetX: 0,
            	offsetY: -64,
            	fill: new ol.style.Fill({color: 'black'}),
            	stroke: new ol.style.Stroke({color: 'white', width: 7})
            })
        });
        features[idx].setStyle(iconStyle);
	}
	
	//마커보이기
	function showMarker(features) {
		var vectorSource = new ol.source.Vector({
            features: features
        });
        var vectorLayer = new ol.layer.Vector({
            source: vectorSource,
            zIndex: 12
        });
        vectorLayer.set("name", "marker_poi");
        
        //기존 마커 삭제
		wavus.map.getMap().getLayers().forEach(function (layer) {
			if (layer.get("name") == "marker_poi") wavus.map.getMap().removeLayer(layer);
		});
		
        wavus.map.getMap().addLayer(vectorLayer);
        wavus.layer.findLayerById("marker_poi").then(function (layer) {
			layer.setVisible(true);
		});
	}
	
	/**
	 * 페이지 영역
	 * @param1 페이지 영역 element id
	 * @param2 총 결과건수
	 * @param3 페이지 테이블수
	 * @param4 현재 페이지 번호
	 */
	function arrayPageList(objDiv, totalCount, tableLimit, nowPage) {
		pageList = $("#" +_frame + " #" + objDiv);
		$(pageList).html("");

		totalPages = Math.ceil(totalCount/tableLimit);
		blockPage = 0;
		blockSize = 5;
		
		//블럭의 첫번째 페이지
		blockPage = Math.floor((nowPage-1) / blockSize);
		blockPage = blockPage * blockSize + 1;
		
		//처음으로
		if (nowPage == 1) $(pageList).append("<span class='firstBlockPage' title='첫 페이지'></span>");
		else $(pageList).append("<span class='firstBlockPage' title='첫 페이지' onClick=wavus.addr.goTo('"+ objDiv + "',"+ totalCount + ","+ tableLimit + ","+ 1 + ");></span>");
		
		//이전 블럭 페이지
		//if ((blockPage - blockSize) > 0) $(pageList).append("<span class='preBlockPage title='이전 " + blockSize + "페이지' onClick=wavus.addr.goTo('"+ objDiv + "',"+ totalCount + ","+ tableLimit + ","+ (nowPage-blockSize) + ");></span>");
		//else $(pageList).append("<span class='preBlockPage title='이전 " + blockSize + "페이지'></span>");
		
		//이전 페이지
		if (nowPage > 1) $(pageList).append("<span class='prevPage' title='이전 페이지' onClick=wavus.addr.goTo('"+ objDiv + "',"+ totalCount + ","+ tableLimit + ","+ (nowPage-1) + ");></span>");
		else $(pageList).append("<span class='prevPage' title='이전 페이지'></span>");
		
		//페이지리스트
		for (i=1; i<=blockSize; i++, blockPage++) {
			if (blockPage == totalPages) {i = blockSize+1;}
			if (blockPage == nowPage) $(pageList).append("<span class='curPage'>" + blockPage + "</span>");
			else $(pageList).append("<span class='actPage' onClick=wavus.addr.goTo('"+ objDiv + "',"+ totalCount + ","+ tableLimit + ","+ blockPage + ");>" + blockPage + "</span>");
		}    

		//다음 페이지
		if (nowPage < totalPages) $(pageList).append("<span class='nextPage' title='다음 페이지' onClick=wavus.addr.goTo('"+ objDiv + "',"+ totalCount + ","+ tableLimit + ","+ (nowPage+1) + ");></span>");
		else $(pageList).append("<span class='nextPage' title='다음 페이지'></span>");
			
		//다음 블럭 페이지
		//if (totalPages < blockPage) $(pageList).append("<span class='nextBlockPage' title='다음 " + blockSize + "페이지'></span>");
		//else $(pageList).append("<span class='nextBlockPage' title='다음 " + blockSize + "페이지' onClick=wavus.addr.goTo('"+ objDiv + "',"+ totalCount + ","+ tableLimit + ","+ (nowPage+blockSize) + ");></span>");
		
		//끝 페이지
		if (nowPage == totalPages) $(pageList).append("<span class='lastBlockPage' title='마지막 페이지'></span>");
		else $(pageList).append("<span class='lastBlockPage' title='마지막 페이지' onClick=wavus.addr.goTo('"+ objDiv + "',"+ totalCount + ","+ tableLimit + ","+ totalPages + ");></span>");
	}
	
	/**
	 * 페이지 이동
	 * @param1 페이지 영역 element id
	 * @param2 총 결과건수
	 * @param3 페이지 테이블수
	 * @param4 현재 페이지 번호
	 */
	function goTo(objDiv, totalCount, tableLimit, nowPage) {
		$(".black_bg_layer").show();
		if (objDiv == "pageListTable_addr") {
			page_addr.nowPage = nowPage;
			$("#" + _frame + " #addr_b").trigger("click", page_addr.nowPage);
			$(".black_bg_layer").hide();
		} else if (objDiv == "pageListTable_keyword") {
			page_keyword.nowPage = nowPage;
			$("#" + _frame + " #keyword_b").trigger("click", page_keyword.nowPage);
			$(".black_bg_layer").hide();
		} else if (objDiv == "pageListTable_dev") {
			page_dev.nowPage = nowPage;
			$("#" + _frame + " #dev_b").trigger("click", page_dev.nowPage);
			$(".black_bg_layer").hide();
		}
	}
	
	function addAddrDiv(frame, divId, map) {
		_frame = frame; 
		var html = ''
		+ '<div class="tabs_wrap"">'
		+     '<div class="tab" style="width: 50%">'
		+         '<input type="radio" class="ra_tabs" id="' + frame + '_ra_address" name="' + frame + '_ra_search_type" value="주소" checked><label for="' + frame + '_ra_address" class="main">주소</label>'
		+         _addAddrBox()
		+     '</div>'
		+     '<div class="tab" style="width: 50%">'
		+         '<input type="radio" class="ra_tabs" id="' + frame + '_ra_keyword" name="' + frame + '_ra_search_type" value="키워드"><label for="' + frame + '_ra_keyword" class="main">키워드</label>'
		+         _addKeywordBox()
		+     '</div>'
		+ '</div>'
		+ '<div style="padding: 10px 0; text-align: center;"><input type="button" class="small_btn_blue" value="닫기" onclick="showHidePanel(null);"></div>';
		$("#" + _frame + " #" + divId).append(html);
		_addEvent();
	}
	
	function addDevDiv(frame, divId,  map) {
		_frame = frame; 
		_addDevBox().then(function (html) {
			$("#" + _frame + " #" + divId).append(html);
			_addEvent();
		});
	}
	
	function _returnCallFunction() {
		wavus.addr.vsibleDevLayer(null);
		
		var txt = "", list = $("#" + _frame + " .selectMultiDev .selectMultiDevList li");
		var multiDevJsonArray = new Array();
		if ($(list).length > 0) {
			_.each($(list), function (data, index) {
				if (index == 0) txt = $(data).html();
				else if ($(list).length == index + 1) txt += " 외 " + index + "건";
				
				var devJsonInfo = new Object();
				devJsonInfo.feature = $(data).data("dev-geom");
				multiDevJsonArray.push(devJsonInfo);
			});
		}
		
		if (_frame == "landInfo") getSelectedLandInfoDevLayer(txt, multiDevJsonArray);
		else if (_frame == "spaceContainer") getSelectedStatDevLayer(txt, multiDevJsonArray);
	}
	
	function getSelDevLayer() {
		return sel_dev_layer;
	}
	
	function frame() {
		return _frame;
	}
	
	function vsibleDevLayer(layerId) {
		if (layerId == null || layerId == undefined || layerId == "") {
			wavus.map.getMap().getLayers().forEach(function (layer) {
				if (layer.get("name") == "marker_poi") wavus.map.getMap().removeLayer(layer);		//주소검색 마커 삭제
				if (layer.get("name") != undefined && layer.get("name").substring(0, 4) == "dev_") {		//사업지구 전체 숨김
					wavus.layer.findLayerById(layer.get("name")).then(function (layer) {
						layer.setVisible(false);
					});
				}
			});
		} else {
			wavus.layer.findLayerById(layerId).then(function (layer) {	//선택 사업지구 보이기
				layer.setVisible(true);
				sel_dev_layer = layer.get("name");
			});
		}
	}
	
	function init(frame, destination) {
		_frame = frame;
		_addToolBox();
		_addEvent();
	}
	
	var module = {
		addressListClick: addressListClick,
		keywordListClick: keywordListClick,
		devListClick: devListClick,
		_getDevList: _getDevList,
		devListMaplocClick: devListMaplocClick,
		_returnCallFunction: _returnCallFunction,
		daum: daum,
		goTo: goTo,
		addAddrDiv: addAddrDiv,
		addDevDiv: addDevDiv,
		vsibleDevLayer: vsibleDevLayer,
		getSelDevLayer: getSelDevLayer,
		frame : frame,
		init: init
	};

	return module;
	
})();