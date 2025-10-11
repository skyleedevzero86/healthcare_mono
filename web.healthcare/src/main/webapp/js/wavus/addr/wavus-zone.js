window.wavus = window.wavus || {};

wavus.zone = (function() {

	var _frame, _destination, _zoneinfopopup;

	function _addToolBox() {
		var selBox = document.createElement("select");
		$(selBox).attr("id", "service_sel");

		var selHTML = ''
		+ '<option value="">선택하세요.</option>'
		+ '<option value="lhzone">사업지구경계도</option>'
		+ '<option value="lhblpn">토지이용계획도</option>'
		+ '<option value="cadastral">지적도</option>'
		+ '<option value="uq111">도시지역</option>'
		+ '<option value="uq112">관리지역</option>'
		+ '<option value="uq113">농림지역</option>'
		+ '<option value="uq114">자연환경보전지역</option>'
		+ '<option value="uq121">경관지구</option>'
		+ '<option value="uq122">미관지구</option>'
		+ '<option value="uq123">고도지구</option>'
		+ '<option value="uq124">방화지구</option>'
		+ '<option value="uq125">방재지구</option>'
		+ '<option value="uq126">보존지구</option>'
		+ '<option value="uq127">시설보호지구</option>'
		+ '<option value="uq128">취락지구</option>'
		+ '<option value="uq129">개발진흥지구</option>'
		+ '<option value="uq130">특정용도제한지구</option>'
		+ '<option value="uq141">국토계획구역</option>'
		+ '<option value="uq162">도시자연공원구역</option>'
		+ '<option value="ud801">개발제한구역</option>'
		+ '<option value="utiscctv">교통CCTV</option>'
		+ '<option value="usfsffb">소방서관할구역</option>'
		+ '<option value="sprd">새주소도로</option>'
		+ '<option value="spbd">새주소건물</option>'
		+ '<option value="adsido">광역시도</option>'
		+ '<option value="adsigg">시군구</option>'
		+ '<option value="ademd">읍면동</option>'
		+ '<option value="adri">리</option>'
		+ '<option value="tdwarea">보행우선구역</option>'
		+ '<option value="damdan">단지경계</option>'
		+ '<option value="damyod">단지용도지역</option>'
		+ '<option value="damyoj">단지시설용지</option>'
		+ '<option value="damyuch">단지유치업종</option>'
		+ '<option value="rirsv">저수지</option>'
		+ '<option value="rifct">수리시설</option>'
		+ '<option value="mgprtfa">아동안전지킴이집</option>'
		+ '<option value="mgprtfb">노인복지시설</option>'
		+ '<option value="mgprtfc">아동복지시설</option>'
		+ '<option value="mgprtfd">기타보호시설</option>'
		+ '<option value="wkmbbsn">대권역</option>'
		+ '<option value="wkmmbsn">중권역</option>'
		+ '<option value="wkmsbsn">표준권역</option>'
		+ '<option value="wkmstrm">하천망</option>'
		+ '<option value="moctlink">교통링크</option>'
		+ '<option value="moctnode">교통노드</option>'
		+ '<option value="trkroad">산책로</option>'
		+ '<option value="frstclimb">등산로</option>'
		+ '<option value="agrixue101">농업진흥지역도</option>'
		+ '<option value="agrixue102">영농여건불리농지도</option>'
		+ '<option value="asitsoildra">배수등급</option>'
		+ '<option value="asitsurston">자갈함량</option>'
		+ '<option value="asitsoildep">유효토심</option>'
		+ '<option value="asitdeepsoil">심토토성</option>'
		+ '<option value="sgisgolf">골프장현황도</option>'
		+ '<option value="weisplafaca">농공단지처리시설</option>'
		+ '<option value="weisplafacs">축산폐수공공처리시설</option>'
		+ '<option value="weisplafacl">매립장침출수처리시설</option>'
		+ '<option value="weisplafacw">하수종말처리시설</option>'
		+ '<option value="weisplaface">기타공동처리시설</option>'
		+ '<option value="weisplafacv">마을하수도</option>'
		+ '<option value="weistaccon">수생태계조사지점</option>'
		+ '<option value="weissitetb">수질자동측정망측정지점</option>'
		+ '<option value="weissitema">수질측정망하천수지점</option>'
		+ '<option value="weissitemb">수질측정망호소수지점</option>'
		+ '<option value="weissitemd">수질측정망농업용수지점</option>'
		+ '<option value="weissiteme">수질측정망공단배수지점</option>'
		+ '<option value="weissitemf">수질측정망도시관류지점</option>'
		+ '<option value="aishcstrip">헬기장</option>'
		+ '<option value="wgispltalk">개발유도연안</option>'
		+ '<option value="wgisplrow">개발조정연안</option>'
		+ '<option value="wgispluse">이용연안</option>'
		+ '<option value="wgisplabs">절대보전연안</option>'
		+ '<option value="wgispljun">준보전연안</option>'
		+ '<option value="wgisreresh">공유수면매립3차수요조사</option>'
		+ '<option value="wgisreplan">공유수면매립기본계획</option>'
		+ '<option value="wgisrecomp">공유수면매립준공</option>'
		+ '<option value="wgisiegug">국가산업단지</option>'
		+ '<option value="wgisienong">농공단지</option>'
		+ '<option value="wgisieilban">일반산업단지</option>'
		+ '<option value="wgisiedosi">첨단산업단지</option>'
		+ '<option value="wgisfmgul">굴양식장</option>'
		+ '<option value="wgisfmkim">김양식장</option>'
		+ '<option value="wgisfmdsm">다시마양식장</option>'
		+ '<option value="wgisfmmyk">미역양식장</option>'
		+ '<option value="wgisfmfish">어류양식장</option>'
		+ '<option value="wgisfmjbok">전복양식장</option>'
		+ '<option value="wgistpsea">무역항해상구역</option>'
		+ '<option value="wgistpland">무역항육상구역</option>'
		+ '<option value="wgistpnewp">무역신항만</option>'
		+ '<option value="wgiscpsea">연안항해상구역</option>'
		+ '<option value="wgiscpland">연안항육상구역</option>'
		+ '<option value="wgisareco">생태계경관보전지역</option>'
		+ '<option value="wgisarfisher">수산자원보호구역</option>'
		+ '<option value="wgisarwet">습지보호구역</option>'
		+ '<option value="wgisnpgug">국립자연공원</option>'
		+ '<option value="wgisnpgun">군립자연공원</option>'
		+ '<option value="wgisnpdo">도립자연공원</option>'
		+ '<option value="tfistidaf">갯벌정보</option>'
		+ '<option value="tfistidafp">갯벌명칭</option>'
		+ '<option value="tfismpa">해양보호구역</option>'
		+ '<option value="edrse002">지진대피소</option>'
		+ '<option value="upisuq151">도시계획(도로)</option>'
		+ '<option value="upisuq152">도시계획(교통시설)</option>'
		+ '<option value="upisuq153">도시계획(공간시설)</option>'
		+ '<option value="upisuq154">도시계획(유통공급시설)</option>'
		+ '<option value="upisuq155">도시계획(공공문화체육시설)</option>'
		+ '<option value="upisuq156">도시계획(방재시설)</option>'
		+ '<option value="upisuq157">도시계획(보건위생시설)</option>'
		+ '<option value="upisuq158">도시계획(환경기초시설)</option>'
		+ '<option value="upisuq159">도시계획(기타기반시설)</option>'
		+ '<option value="upisuq161">지구단위계획</option>'
		+ '<option value="upisuq171">개발행위허가제한지역</option>'
		+ '<option value="upisuq173">기반시설부담구역</option>'
		+ '<option value="upisuq174">개발행위허가필지</option>'
		+ '<option value="upisuq175">토지거래계약에관한허가구역</option>'
		+ '<option value="cdfrsmaxfrq">해안침수(최대범람)</option>'
		+ '<option value="cdfrs200frq">해안침수(200년빈도)</option>'
		+ '<option value="cdfrs150frq">해안침수(150년빈도)</option>'
		+ '<option value="cdfrs100frq">해안침수(100년빈도)</option>'
		+ '<option value="cdfrs050frq">해안침수(50년빈도)</option>'
		+ '<option value="flisfk100">산지(자연휴양림)</option>'
		+ '<option value="flisfk200">산지(채종림)</option>'
		+ '<option value="flisfk300">산지(보안림)</option>'
		+ '<option value="nsnmssitenm">국가지명</option>'
		+ '<option value="toisdepcntah">해안선</option>'
		+ '<option value="uo301">문화재보호도</option>'
		+ '<option value="gimshydro">수문지질단위</option>'
		+ '<option value="gimsstiff">수질다이어그램</option>'
		+ '<option value="gimsec">전기전도도</option>'
		+ '<option value="cgimslinea">지질구조밀도</option>'
		+ '<option value="lgimslinea">지질구조선</option>'
		+ '<option value="gimsdepth">지하수등수심선</option>'
		+ '<option value="gimspoten">지하수등수위선</option>'
		+ '<option value="gimsdirec">지하수유동방향</option>'
		+ '<option value="gimsfault">단층</option>'
		+ '<option value="gimsscs">토양도</option>'
		+ '<option value="fsdifrsts">산림입지도</option>'
		+ '<option value="ub901">시장정비구역</option>'
		+ '<option value="ud610">국민임대주택</option>'
		+ '<option value="uh402">자유무역지역지정및운영</option>'
		+ '<option value="uh501">유통단지</option>'
		+ '<option value="uj401">온천지구</option>'
		+ '<option value="um000">가축사육제한구역</option>'
		+ '<option value="um221">야생동식물보호</option>'
		+ '<option value="um901">습지보호지역</option>'
		+ '<option value="uo101">학교환경위생정화구역</option>'
		+ '<option value="uo601">관광지</option>'
		+ '<option value="mogeffact">여가부시설</option>';

		$(selBox).html(selHTML);
		$(_frame).append(selBox);
	}

	//검색된 장소 클릭 시 공간정보 조회
	function zoneSearch(proj) {
		var aliasId = $("#service_sel option:selected").val();
		if (aliasId == "") return;

		removeZoneLayer();

		try {
			var proj = ol.proj.transform([proj[0], proj[1]], 'EPSG:5174', 'EPSG:4326');
			var point = "point(" + proj[0] + "%20" + proj[1] + ")";

			var serviceUrl = "http://apis.vworld.kr/2ddata/" + aliasId + "/data?"
			+ "apiKey=5CB53588-5036-3ADD-9C6A-52C0412565CC"
			+ "&domain=" + location.href
			+ "&geometry=" + point
			+ "&output=json"
			+ "&srsName=EPSG:4326";

			$.ajax({
				type: "GET",
				url: serviceUrl,
				dataType: "jsonp",
				beforeSend: function(xhr){
		            xhr.setRequestHeader("Content-Type: application/json; charset=utf8");
				},
				success: function(data) {
					//console.log(data);

					if (data.header.resultCode == 200) {
						if (data.featureCollection == undefined) return;

						var features = data.featureCollection.features;
						var bbox = data.featureCollection.bbox;

						$(features).each(function(idx) {
							var items = features[idx];
							var coordinates = items.geometry.coordinates;
							var centerX, centerY;
							$(coordinates).each(function(idx) {
								var rowMax, rowMin;
								var item = coordinates[idx];
								$(item).each(function(i) {
									var i = item[i];
									var proj = ol.proj.transform([i[0], i[1]], 'EPSG:4326', _destination);

									i[0] = proj[0];
									i[1] = proj[1];
								});

								var minPos = item.reduce(function(previous, current) {
									return previous > current ? current:previous;
								});
								var maxPos = item.reduce( function (previous, current) {
									return previous > current ? previous:current;
								});
								centerX = (minPos[0] + maxPos[0])/2;
								centerY = (minPos[1] + maxPos[1])/2;

							});
							_addPolygon(coordinates, items.properties.zonename, centerX, centerY);
							_showLayerInfo(items.properties);
						});
					}
		        },
				error: function(xhr, status, error) {
					console.log(status);
				}
		    });
		} catch(e) {
			fn_modalAlert("공간정보조회를 실패하였습니다.\n\n" + e.message);
		}
	}

	function _addPolygon(features, zonename, centerX, centerY) {
		var feature = new ol.Feature({
			geometry: new ol.geom.Polygon(features)
	    });

	    var style = new ol.style.Style({
	        stroke: new ol.style.Stroke({
	            color: "blue",
	            width: 2
	        }),
	        fill: new ol.style.Fill({
	            color: "rgba(0,0,255,0.1)"
	        })
	    });

	    feature.setStyle(style);
	    feature.set("zone", zonename);

	    var vectorSource = new ol.source.Vector();
	    vectorSource.addFeature(feature);

	    var vectorLayer = new ol.layer.Vector({
	    	source: vectorSource,
	    	zIndex: 11
	    });

	    vectorLayer.set("name", "zone");
	    wavus.map.getMap().addLayer(vectorLayer);
		wavus.map.getView().setCenter([centerX, centerY]);
	}

	function _showLayerInfo(properties) {
		var alias = $("#service_sel option:selected").text();

		var element = document.getElementById("popup_zoneinfo");
		_zoneinfopopup = new ol.Overlay({
			element: element,
			positioning: "bottom-center",
			stopEvent: false
		});
		wavus.map.getMap().addOverlay(_zoneinfopopup);

		var coord = wavus.map.getView().getCenter();
		_zoneinfopopup.setPosition(coord);
		$("#popup_zoneinfo_title").html(alias);
		$("#popup_zoneinfo_content").html(JSON.stringify(properties));
		$(element).popover("show");

		var closer = $("#popup_zoneinfo_close");
		$(closer).on("click", function(e) {
			removeZoneLayer();
		    closer.blur();
		});
	}

	function removeZoneLayer() {
		wavus.map.getMap().getLayers().forEach(function(layer) {
			if (layer.get("name") == "zone") wavus.map.getMap().removeLayer(layer);
		});
		if (_zoneinfopopup != undefined) _zoneinfopopup.setPosition(undefined);
	}

	function init(frame, destination) {
		_frame = $("#" + frame);
		_destination = destination;
		_addToolBox();
	}

	var module = {
		zoneSearch: zoneSearch,
		removeZoneLayer: removeZoneLayer,
		init: init
	};

	return module;

})();