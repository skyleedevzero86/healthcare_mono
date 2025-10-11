
window.wavus = window.wavus || {};

wavus.map = (function() {
	var map;
	var map2;
	/**
	 * 뷰 체인지 이벤트 용 deferred
	 * 
	 * 받는 쪽에서는
	 * wavus.map.viewChangeDeferred.then(null, null, function(view){
	 * 	view.getZoom(); view.xxx(); view.xxx
	 * 사용하면됨
	 * });
	 */
	var viewChangeDeferred = $.Deferred();
	
	/**
	 * 다중맵 배열
	 */
	var subMaps = [];
	
	
	
	var view = new ol.View({ // map 뷰 설정
		projection : wavus.config.mapInfo.projection,
		center : wavus.config.mapInfo.center,
		minZoom : wavus.config.mapInfo.minZoom,
		maxZoom : wavus.config.mapInfo.maxZoom
	});
	
	var view2 = new ol.View({ // map 뷰 설정
		projection : wavus.config.mapInfo.projection,
		center : wavus.config.mapInfo.center,
		minZoom : wavus.config.mapInfo.minZoom,
		maxZoom : wavus.config.mapInfo.maxZoom
	});
	
//	view.on('change:resolution', function(){
//		console.log(view.getResolution());
//		console.log(view.getZoom());
//	})
	
	function setMap(mapDiv) {
		var div = document.getElementById(mapDiv);
		var deferred = $.Deferred();

		map = new ol.Map({ // 맵생성
			target : div,
			view : view,
			logo : false
		});
		
		view.setZoom(wavus.config.mapInfo.zoom);
		view.setCenter(wavus.config.mapInfo.center);

		map.addControl(new ol.control.ZoomSlider());
		map.addControl(new ol.control.ScaleLine());
        
        //주소검색 control 생성
        //map.addControl(wavus.geocode.addGeocodeFrame());
        
		// 뷰 체인지 공지용
		view.on('change', function() {
			viewChangeDeferred.notify(getView());
		});

		deferred.resolve(map);
		return deferred;
	}

	function setMap_print(mapDiv)
	{
		var div = document.getElementById(mapDiv);
		var deferred = $.Deferred();

		map2 = new ol.Map({ // 맵생성
			target : div,
			view : view2
		});

		view2.setZoom(wavus.config.mapInfo.zoom);
		view2.setCenter(wavus.config.mapInfo.center);

		map2.addControl(new ol.control.ZoomSlider());
		map2.addControl(new ol.control.ScaleLine());

		// 뷰 체인지 공지용
		view2.on('change', function()
		{
			viewChangeDeferred.notify(getView_print());
		});

		deferred.resolve(map2);
		return deferred;
	}

	// 분할지도 화면 맵 생성
	function makeSubMap(mapDiv)
	{
		var deferred = $.Deferred();

		var div = document.getElementById(mapDiv);

		// 맵생성
		var subMap = new ol.Map({
			controls : ol.control.defaults({
				attribution : false,
				rotate : false,
				zoom : false,
				scaleline : true
			}),
			target : div,
			view : map.getView()
		});

		// 주석처리
		 subMaps.push(subMap);

		// subMaps[0] 번에 주둔지정보비교 subMap등록
		subMaps[0] = subMap;

		deferred.resolve(subMap);
		return deferred;
	}

	function addLayer(layer)
	{
		map.addLayer(layer);
	}

	function removeLayer(layer)
	{
		if(layer != undefined){
			if(layer.get('name') != undefined){
				wavus.controller.removeTOCLayer(layer.get('name'));
			}
		}
		map.removeLayer(layer);
	}
	
	function addLayer_print(layer)
	{
		map2.addLayer(layer);
	}

	function getMap()
	{
		return map;
	}
	
	function getMap_print()
	{
		return map2;
	}

	// 정보비교 SubMap 호출
	function getSubMap()
	{
		return subMaps[0];
	}

	function getSynMap()
	{
		var deferred = $.Deferred();
		deferred.resolve(map);
		return deferred;
	}

	function getView()
	{
		return map.getView();
	}

	function getView_print()
	{
		return map2.getView();
	}

	function getSynView()
	{
		var deferred = $.Deferred();
		deferred.resolve(map.getView());
		return deferred;
	}
	
	function getBounds() {
		return getView().calculateExtent(map.getSize());
	}

	/**
	 * 맵 이동 메소드
	 * 
	 * 
	 * param.extent 지오메터리 값 param.geom 디비에서 나오는 값
	 * 
	 * param.options 옵션
	 * 
	 */
	function moveMap(param)
	{
		var _geometry = null;

		if(param.geom)
		{
			var wktFormat = new ol.format.WKT();

			var feature = wktFormat.readFeature(param.geom, {
			dataProjection : wavus.config.DBProjection,
			featureProjection : wavus.config.mapInfo.projection
			});
			_geometry = feature.getGeometry();
		}

		if(param.extent)
		{
			_geometry = param.extent;
		}

		if(_geometry == null)
		{
			_geometry = getView().calculateExtent(map.getSize());
		}

		// var extent = param.extent ||
		// getView().calculateExtent(map.getSize());
		var options = {
			duration : 300
		}

		getView().fit(_geometry);
	}

	/**
	 * 맵의 특정 영역 이상으로 못나가게 설정 하는 메소드 param.extent : 영역 extent
	 * 
	 * param.geom : 디비 검색된 geom 값
	 * 
	 * 넘어올 좌표계에 따라서 좌표 변환이 추가 필요함
	 * 
	 */
	function setLimitExtent(param)
	{

		var _geometry = null;

		if(param.geom)
		{
			var wktFormat = new ol.format.WKT();

			var feature = wktFormat.readFeature(param.geom, {
			dataProjection : wavus.config.DBProjection,
			featureProjection : wavus.config.mapInfo.projection
			});
			_geometry = feature.getGeometry().getExtent();
		}

		if(param.extent)
		{
			_geometry = param.extent;
		}

		var limitView = new ol.View({ // map 뷰 설정
		projection : wavus.config.mapInfo.projection,
		extent : _geometry,
		// minZoom : 13,
		// minZoom : 15,
		// maxZoom : 19
		minZoom : 1,
		maxZoom : 14
		});

		limitView.on('change', function()
		{
			viewChangeDeferred.notify(getView());
		});

		map.setView(limitView);

		for( var attr in subMaps)
		{
			subMaps[attr].setView(limitView);
		}

		moveMap({
			extent : _geometry
		});

		setScaleSelectBox();

		viewChangeDeferred.notify(getView());

	}

	/**
	 * 맵의 특정 영역 이상으로 못나가게 설정 하는 메소드 param.extent : 영역 extent
	 * 
	 * param.geom : 디비 검색된 geom 값
	 * 
	 * 넘어올 좌표계에 따라서 좌표 변환이 추가 필요함
	 * 
	 */
	function unLimitExtent()
	{

		map.setView(view);

		for( var attr in subMaps)
		{
			subMaps[attr].setView(view);
		}

		setScaleSelectBox();

		viewChangeDeferred.notify(getView());

	}

	/**
	 * 스케일 셀렉트 박스 id 저장 변수
	 */
	var _scaleSelectBoxDomId = null;

	/**
	 * 스케일 셀렉트 박스 셋팅
	 */
	function setScaleSelectBox(param)
	{
		if(_scaleSelectBoxDomId == null)
		{
			if(param)
			{
				_scaleSelectBoxDomId = param.id;
			}
		}

		var minResolution = getView().getMinResolution();
		var maxResolution = getView().getMaxResolution();
		var tempView = getView();

		// var resolutions = [1.40625, 0.703125, 0.3515625, 0.17578125,
		// 0.087890625, 0.0439453125, 0.02197265625, 0.010986328125,
		// 0.0054931640625, 0.00274658203125, 0.001373291015625,
		// 0.0006866455078125, 0.00034332275390625, 0.000171661376953125,
		// 0.0000858306884765625, 0.00004291534423828125,
		// 0.000021457672119140625, 0.000010728836059570312,
		// 0.000005364418029785156, 0.000002682209014892578,
		// 0.000001341104507446289, 6.705522537231445e-7, 3.3527612686157227e-7,
		// 1.6763806343078613e-7, 8.381903171539307e-8, 4.190951585769653e-8,
		// 2.0954757928848267e-8, 1.0477378964424133e-8, 5.238689482212067e-9,
		// 2.6193447411060333e-9, 1.3096723705530167e-9, 6.548361852765083e-10,
		// 3.2741809263825417e-10, 1.6370904631912708e-10,
		// 8.185452315956354e-11, 4.092726157978177e-11, 2.0463630789890885e-11,
		// 1.0231815394945443e-11, 5.115907697472721e-12,
		// 2.5579538487363607e-12, 1.2789769243681803e-12,
		// 6.394884621840902e-13, 3.197442310920451e-13];
		var resolutions = [ 2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1, 0.5, 0.25 ];
		var selectBox = $(_scaleSelectBoxDomId);
		selectBox.empty();

		var minZoom = resolutions.indexOf(minResolution);
		var maxZoom = resolutions.indexOf(maxResolution);

		var scaleInfoList = wavus.config.mapZoomScaleInfo
				.slice(maxZoom - 1, minZoom);
		_
				.each(scaleInfoList, function(info)
				{
					selectBox
							.append('<option value="' + info.zoom + '"> 1 : ' + info.scale + '</option>');
				});

		setTimeout(function()
		{
			selectBox.val(getView().getZoom()).attr('selected', 'selected');
		}, 100);

		selectBox.change(function()
		{
			getView().animate({
			zoom : $(this).val(),
			duration : 300
			})
		});
	}

	function addOverlay(layer) {
		map.addOverlay(layer);
	}
	
	function addEventListener(event, func) {
		map.addEventListener(event, func);
	}
	
	function removeEventListener(event, func) {
		map.removeEventListener(event, func);
	}
	
	var module = {
		setMap : setMap,
		addLayer : addLayer,
		removeLayer: removeLayer,
		getMap : getMap,
		getView : getView,
		getBounds: getBounds,
		makeSubMap : makeSubMap,
		getSynMap : getSynMap,
		getSynView : getSynView,
		getSubMap : getSubMap,
		moveMap : moveMap,
		setLimitExtent : setLimitExtent,
		unLimitExtent : unLimitExtent,
		viewChangeDeferred : viewChangeDeferred,
		setScaleSelectBox : setScaleSelectBox,
		setMap_print : setMap_print,
		getMap_print : getMap_print,
		getView_print : getView_print,
		addLayer_print : addLayer_print,
		addOverlay: addOverlay,
		addEventListener: addEventListener,
		removeEventListener: removeEventListener
	};

	return module;
})();
