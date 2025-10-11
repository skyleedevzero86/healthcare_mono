window.wavus = window.wavus || {};

wavus.layer = (function(){

	var _map;
	var _map2;
	var vworldImageUrl = 'http://xdworld.vworld.kr:8080/2d/Base/201612/{z}/{x}/{y}.png';
	var drawLayer;
	var highlightLayer;
	var boundLayer;
	var radiusLayer;

	// 단순 일회성 그래픽 레이어
	var graphicLayer;
	var z_index = 5001;

	// 베이스 레이어 셋팅
	function setBaseLayer(){
		var deferred = $.Deferred();
		setBaseLayerMode("dual");
		_setVectorLayer(deferred);
		return deferred;
	}

	function setBaseLayerMode(param){
		if(param == "solo"){
			_setPortalLayer({
				portalName : "google"
			}).then(function(portalLayer){
				portalLayer.set('name', 'google');
				_map2.addLayer(portalLayer);
			});
		} else if(param == "dual"){
			_setPortalLayer({
				portalName : "daum"
			}).then(function(portalLayer){
				portalLayer.set('name', 'daum');
				portalLayer.setVisible(true);
				_map.addLayer(portalLayer);
			});
			_setPortalLayer({
				portalName : "daumSatellite_part1"
			}).then(function(portalLayer){
				portalLayer.set('name', 'daumSatellite_part1');
				portalLayer.setVisible(false);
				_map.addLayer(portalLayer);
			});
			_setPortalLayer({
				portalName : "daumSatellite_part2"
			}).then(function(portalLayer){
				portalLayer.set('name', 'daumSatellite_part2');
				portalLayer.setVisible(false);
				_map.addLayer(portalLayer);
			});
		} else if(param == "quad"){
			_setPortalLayer({
				portalName : "daum"
			}).then(function(portalLayer){
				portalLayer.set('name', 'daum');
				portalLayer.setVisible(true);
				_map.addLayer(portalLayer);
			});
			_setPortalLayer({
				portalName : "daumSatellite_part1"
			}).then(function(portalLayer){
				portalLayer.set('name', 'daumSatellite_part1');
				portalLayer.setVisible(false);
				_map.addLayer(portalLayer);
			});
			_setPortalLayer({
				portalName : "daumSatellite_part2"
			}).then(function(portalLayer){
				portalLayer.set('name', 'daumSatellite_part2');
				portalLayer.setVisible(false);
				_map.addLayer(portalLayer);
			});
			_setPortalLayer({
				portalName : "naver"
			}).then(function(portalLayer){
				portalLayer.set('name', 'naver');
				portalLayer.setVisible(false);
				_map.addLayer(portalLayer);
			});
			_setPortalLayer({
				portalName : "naverSatellite"
			}).then(function(portalLayer){
				portalLayer.set('name', 'naverSatellite');
				portalLayer.setVisible(false);
				_map.addLayer(portalLayer);
			});
			_setPortalLayer({
				portalName : "rdm_dron"
			}).then(function(portalLayer){
				portalLayer.set('name', 'rdm_dron');
				portalLayer.setVisible(false);
				_map.addLayer(portalLayer);
			});
		} else if(param == "none"){
			console.log(param);
		}
	}

	function setTileLayer(param){
		var tileLayer = new ol.layer.Tile({
		source : param.source,
		map : param.map || _map,
		visible : param.visible
		})
		tileLayer.set('name', param.name);
		tileLayer.setZIndex(param.zIndex);
	}

	// 기본 벡터레이어 셋팅
	// 벡터에 layerType 추가 : 지역개발사업통합모니터링 시스템 지도서비스 사용시 사용자의 설정값에 따라 레이어 표출을 하는데에 있어서 레이어onoff 기능필요하여 구분하기 위한 값 구분위해 사용
	function _setVectorLayer(deferred){
		var vectorSource = new ol.source.Vector();
		var vectorStyle = wavus.symbol.findSymbol({
		type : 'polygon',
		style : 'draw'
		});

		drawLayer = new ol.layer.Vector({
			source : vectorSource,
			style : vectorStyle,
			name: 'drawLayer',
			layerType : 'vector',
			zIndex : 6000,
			baseLayer: true
		});
		_map.addLayer(drawLayer);

		vectorSource = new ol.source.Vector();
		vectorStyle = wavus.symbol.findSymbol({
		type : 'polygon',
		style : 'radius'
		});

		radiusLayer = new ol.layer.Vector({
		layerId : 'lc_radiusLayer',
		name: 'lc_radiusLayer',
		layerType : 'vector',
		source : vectorSource,
		style : vectorStyle,
		zIndex : 6001,
		category: 'lcBase'
		});

		_map.addLayer(radiusLayer);

		vectorSource = new ol.source.Vector();
		vectorStyle = wavus.symbol.findSymbol({
			type : 'polygon',
			style : 'highlight'
		});

		highlightLayer = new ol.layer.Vector({
			source : vectorSource,
			style : vectorStyle,
			name: 'highlightLayer',
			layerType : 'vector',
			zIndex : 6001,
			category: 'lcBase'
		});
		_map.addLayer(highlightLayer);

		vectorSource = new ol.source.Vector();
		vectorStyle = wavus.symbol.findSymbol({
		type : 'polygon',
		style : 'bound'
		});

		boundLayer = new ol.layer.Vector({
		source : vectorSource,
		style : vectorStyle,
		name: 'boundlayer',
		layerType : 'vector',
		zIndex : 5001,
		category: 'lcBase'
		});
		_map.addLayer(boundLayer);

		vectorSource = new ol.source.Vector();

		graphicLayer = new ol.layer.Vector({
		source : vectorSource,
		name: 'graphicLayer',
		zIndex : 5003,
		layerType : 'vector',
		baseLayer: true
		});
		_map.addLayer(graphicLayer);

		deferred.resolve(drawLayer, highlightLayer, graphicLayer, boundLayer);
		_manageZIndex();
	}

	// 벡터레이어 추가하기
	function makeVectorLayer(name, map, index){
		var _index;
		if(wavus.util.isEmpty(index))	{
			_index = z_index;
			_manageZIndex();
		} else{
			_index = index;
		}

		findLayerById(name).then(function(layer){
			if(layer != undefined) wavus.map.removeLayer(layer);
		});

		var vectorSource = new ol.source.Vector();
		var vectorLayer = new ol.layer.Vector({
		source : vectorSource,
		zIndex : _index
		});
		vectorLayer.set("custom", "1"); // 추가로 생성된 Vector Layer 구분용
		vectorLayer.set('name', name);
		map ? map.addLayer(vectorLayer) : _map.addLayer(vectorLayer)

		var deferred = $.Deferred();
		deferred.resolve(vectorLayer);
		return deferred;
	}

	function makeHeatLayer(name, map, index, radius, blur){
		var _index;
		var _radius = radius || 10;
		var _blur = blur || 50;
		if(wavus.util.isEmpty(index))	{
			_index = z_index;
			_manageZIndex();
		} else{
			_index = index;
		}
		findLayerById(name).then(function(layer){
			if(layer != undefined) wavus.map.removeLayer(layer);
		});

		var vectorSource = new ol.source.Vector();
		var heatLayer = new ol.layer.Heatmap({
		source : vectorSource,
		zIndex : _index,
		radius : _radius,
		blur : _blur
		});
		heatLayer.set("custom", "1"); // 추가로 생성된 Vector Layer 구분용
		heatLayer.set('name', name);
		map ? map.addLayer(heatLayer) : _map.addLayer(heatLayer)

		var deferred = $.Deferred();
		deferred.resolve(heatLayer);
		return deferred;
	}

	// 웹맵서비스 레이어 목록 셋팅
	function setWmsLayer(){
		_.each(wavus.config.layers, function(layer, key){
			if(layer.rqustType == 'img'){
				_map.addLayer(_setImgLayer(layer))
			}
			_map.addLayer(_setTileLayer(layer))
		})
	}

	// 레이어 요청(wms-Tile)
	// geoserver 용
	function _setTileLayer(param){
		var resolution;
		if(param.zoom){
			var zoomInfo = _.find(wavus.config.mapZoomScaleInfo, function(info){
				return info.zoom == param.zoom;
			});
			resolution = zoomInfo.resolution;
		};
		function customLoader(tile, src) {
			var client = new XMLHttpRequest();
			client.open('GET', "/biz/si/main/getGeoServerLayers.do?url=" + src);
			client.responseType = "arraybuffer";
			client.onload = function() {
				var arrayBufferView = new Uint8Array(this.response);
	            var blob = new Blob([this.response], { type: 'image/png' });
	            var urlCreator = window.URL || window.webkitURL;
	            var imageUrl = urlCreator.createObjectURL(blob);
	            tile.getImage().src = imageUrl;
			};
			client.send();
	      }
		var wmsSource = new ol.source.TileWMS({
//			url : wavus.config.info.gisServer.url + "/wms", // TEST서버
			url : wavus.config.info.gisServer.realUrl + "/wms", // 운영서버
//			url : wavus.config.info.gisServer.gwcUrl, // 타일캐싱 데이터
//			url : wavus.config.info.gisServer.gwcRealUrl, // 타일캐싱운영 데이터
			tileLoadFunction: customLoader,
			params : {
				'LAYERS' : param.layerId,
				'VERSION' : '1.1.1',
				'SRS' : wavus.config.mapInfo.projection,
				'STYLES' : param.style || param.layerId
			},
			serverType : 'geoserver',
			crossOrigin : 'anonymous' /* 크로스 도메인 허용 */
		});

		var wmsLayer = new ol.layer.Tile({
			source : wmsSource,
			zIndex : param.zIndex,
			visible : param.visible,
			maxResolution : resolution || param.maxResolution
		});

		wmsLayer.set('name', param.layerId);
		wmsLayer.set('korNm', param.korNm);
		wmsLayer.set('group', 'wms');
		wmsLayer.set('baseLayer', param.basemap);
		wmsLayer.set('zoom', param.zoom);
		wmsLayer.set('accsAthry', param.accsAthry);
		wmsLayer.set('opacity', '0.6');
		_map.addLayer(wmsLayer);
		return wmsLayer;
	}

	function _setPortalLayer(param){
		var deferred = $.Deferred();
		var resolutions;
		var extent;
		var origin;
		var projection;
		if(param.portalName == "naver"){
			resolutions = [ 2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1, 0.5, 0.25 ];
			origin = [ 90105, 1192910 ];
			extent = [90105, 1192910, 2187257, 2761678];
			projection = ol.proj.get('EPSG:5179');
			projection.setExtent(extent);

			var source = new ol.source.XYZ({
														projection : projection,
														tileSize : 256,
														minZoom : 0,
														maxZoom : resolutions.length - 1,
														crossOrigin : 'anonymous', /* 크로스 도메인 허용 */
														tileGrid : new ol.tilegrid.TileGrid({
																									extent : extent,
																									origin : origin,
																									resolutions : resolutions
														}),
														tileUrlFunction : function(tileCoord, pixelRatio, projection){
															if(tileCoord == null)
																return undefined;
															var s = Math.floor(Math.random() * 3) + 1; // 1 ~ 4
															var z = tileCoord[0] + 1;
															var x = tileCoord[1];
															var y = tileCoord[2];

															return 'http://onetile' + s + '.map.naver.net/get/195/0/0/' + z + '/' + x + '/' + y + '/bl_vc_bg/ol_vc_an';
														}
			});
		}else if(param.portalName == "naverSatellite"){
			resolutions = [ 2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1, 0.5, 0.25 ];
			origin = [ 90105, 1192910 ];
			extent = [90105, 1192910, 2187257, 2761678];
			projection = 'EPSG:5179';
			var source = new ol.source.XYZ({
			crossOrigin : 'anonymous', /* 크로스 도메인 허용 */
			projection : projection,
			tileSize : 256,
			minZoom : 0,
			maxZoom : resolutions.length - 1,
			tileGrid : new ol.tilegrid.TileGrid({
			extent : extent,
			origin : origin,
			resolutions : resolutions
			}),
			tileUrlFunction : function(tileCoord, pixelRatio, projection){
				if(tileCoord == null)
					return undefined;
				var s = Math.floor(Math.random() * 3) + 1; // 1 ~ 4
				var z = tileCoord[0] + 1;
				var x = tileCoord[1];
				var y = tileCoord[2];

				return 'http://onetile' + s + '.map.naver.net/get/174/0/1/' + z + '/' + x + '/' + y + '/bl_st_bg/ol_st_rd/ol_st_an';
			}
			});
		} else if(param.portalName == 'vworld'){
			resolutions = [ 2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1, 0.5, 0.25 ];
			var source = new ol.source.XYZ({
			url : vworldImageUrl,
			maxZoom : resolutions.length - 1
			});
		} else if(param.portalName == 'daum'){
			resolutions = [ 2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1, 0.5, 0.25 ];
			origin = [ -30007, -59987 ];
//			extent = [ -30007, -59987, 494288, 988576 ];
			extent = [ -430000, -60000, 894288, 988576 ];
			projection = 'EPSG:5181';
			var source = new ol.source.XYZ({
			projection : projection,
			tileSize : 256,
			minZoom : 0,
			maxZoom : resolutions.length - 1,
			crossOrigin : 'anonymous', /* 크로스 도메인 허용 */
			tileGrid : new ol.tilegrid.TileGrid({
			extent : extent,
			origin : origin,
			resolutions : resolutions
			}),
			tileUrlFunction : function(tileCoord, pixelRatio, projection){
				if(tileCoord == null)
					return undefined;
				var s = Math.floor(Math.random() * 4); // 1 ~ 4
				var z = 14 - (tileCoord[0]);
				var x = tileCoord[1];
				var y = tileCoord[2];
				
				
//				return '/biz/si/main/getMapImg.do?url=map' + s + '.daumcdn.net/map_2d/1810uis/L' + z + '/' + y + '/' + x + '.png' // 기존
				return '/biz/si/main/getMapProxyImg.do?url=map' + s + '.daumcdn.net/map_2d/1810uis/L' + z + '/' + y + '/' + x + '.png' // 프록시 설정
			}
			});
		} else if(param.portalName == 'daumSatellite_part1'){
			resolutions = [ 2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1, 0.5, 0.25 ];
			origin = [ -30007, -59987 ];
			//extent = [ -30007, -59987, 494288, 988576 ];
			extent = [ -230000, -60000, 694288, 988576 ];
			projection = 'EPSG:5181';
			var source = new ol.source.XYZ({
			projection : projection,
			tileSize : 256,
			minZoom : 0,
			maxZoom : resolutions.length - 1,
			crossOrigin : 'anonymous', /* 크로스 도메인 허용 */
			tileGrid : new ol.tilegrid.TileGrid({
			extent : extent,
			origin : origin,
			resolutions : resolutions
			}),
			tileUrlFunction : function(tileCoord, pixelRatio, projection){
				if(tileCoord == null)
					return undefined;
				var s = Math.floor(Math.random() * 4); // 1 ~ 4
				var z = 14 - (tileCoord[0]);
				var x = tileCoord[1];
				var y = tileCoord[2];
//				return '/biz/si/main/getMapImg.do?url=map' + s + '.daumcdn.net/map_skyview/L' + z + '/' + y + '/' + x + '.jpg'; // 기존
				return '/biz/si/main/getMapProxyImg.do?url=map' + s + '.daumcdn.net/map_skyview/L' + z + '/' + y + '/' + x + '.jpg'; // 프록시설정
			}
			});
		} else if(param.portalName == 'daumSatellite_part2'){
			resolutions = [ 2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1, 0.5, 0.25 ];
			origin = [ -30004, -59985 ];
			//extent = [ -30004, -59985, 494288, 988576 ];
			extent = [ -230000, -60000, 694288, 988576 ];
			projection = 'EPSG:5181';
			var source = new ol.source.XYZ({
			projection : projection,
			tileSize : 256,
			minZoom : 0,
			maxZoom : resolutions.length - 1,
			crossOrigin : 'anonymous', /* 크로스 도메인 허용 */
			tileGrid : new ol.tilegrid.TileGrid({
			extent : extent,
			origin : origin,
			resolutions : resolutions
			}),
			tileUrlFunction : function(tileCoord, pixelRatio, projection){
				if(tileCoord == null)
					return undefined;
				var s = Math.floor(Math.random() * 4); // 1 ~ 4
				var z = 14 - (tileCoord[0]);
				var x = tileCoord[1];
				var y = tileCoord[2];
//				return 'http://map' + s + '.daumcdn.net/map_hybrid/1790mnb/L' + z + '/' + y + '/' + x + '.png'; // 외부망
//				return '/biz/si/main/getMapImg.do?url=map' + s + '.daumcdn.net/map_hybrid/1810uis/L' + z + '/' + y + '/' + x + '.png'; // 기존
				return '/biz/si/main/getMapProxyImg.do?url=map' + s + '.daumcdn.net/map_hybrid/1810uis/L' + z + '/' + y + '/' + x + '.png'; // 프록시 설정
			}
			});
		} else if(param.portalName == 'google'){
//			resolutions = [ 2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1, 0.5, 0.25 ];
//			var source = new ol.source.OSM();

			resolutions = [ 2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1, 0.5, 0.25 ];
			var source = new ol.source.OSM({
				url: 'http://mt{0-3}.google.com/vt/lyrs=m&x={x}&y={y}&z={z}',
	            attributions: [
	                new ol.Attribution({ html: '© Google' }),
	                new ol.Attribution({ html: '<a href="https://developers.google.com/maps/terms">Terms of Use.</a>' })
	            ]
			});
		} else if(param.portalName == 'osm'){
			resolutions = [ 2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1, 0.5, 0.25 ];
			var source = new ol.source.OSM();
		} else if(param.portalName == 'rdm_dron'){

			var layer = _setTileLayer({layerId: 'rdm_dron', style: 'rdm_dron', basemap: true, visible: false, maxResolution: 128, zIndex: 50})
			deferred.resolve(layer);
			return deferred;
		}

		var portalLayer = new ol.layer.Tile({
		source : source,
		zIndex : 10,
		visible : true,
		baseLayer: true
		});
		deferred.resolve(portalLayer);
		return deferred;
	}

	// shapefile 요청
	function getShapefile(layerName, storeName){
		var url = wavus.config.info.gisServer.realServerUrl + '/ows';
//		var url = wavus.config.info.gisServer.url + '/ows';
		url += '?service=WFS&version=1.0.0&request=GetFeature&outputformat=SHAPE-ZIP&typeName=' + storeName + ':' + layerName;
		window.location.href = url;
	}

	// 레이어 이름으로 레이어 찾기
	function findLayerById(layerId, subMap){
		var deferred = $.Deferred();
		var layer;

		if(subMap){
			layer = _
					.find(wavus.map.getSubMap().getLayers().getArray(), function(layer){
						return layer.get('name') == layerId || null;
					})
		} else{
			layer = _.find(_map.getLayers().getArray(), function(layer){
				return layer.get('name') == layerId || null;
			})
		}
		deferred.resolve(layer);
		return deferred;
	}

	function getLayerById(layerId){
		var layer = null;

		for(var ii=0; ii < _map.getLayers().getArray().length; ii++){
			if(_map.getLayers().getArray()[ii].get("name") == layerId) layer = _map.getLayers().getArray()[ii];

		}

		for(var ii=0; ii < _map.getLayers().getArray().length; ii++){
			if(_map.getLayers().getArray()[ii].get("name") == layerId) layer = _map.getLayers().getArray()[ii];

		}

		return layer;
	}

	function findLayerPrintById(layerId, subMap){
		var deferred = $.Deferred();
		var layer;

		if(subMap){
			layer = _.find(wavus.map.getSubMap().getLayers().getArray(), function(layer){
						return layer.get('name') == layerId || null;
					})
		} else{
			layer = _.find(_map2.getLayers().getArray(), function(layer){
				return layer.get('name') == layerId || null;
			})
		}
		deferred.resolve(layer);
		return deferred;
	}

	// 레이어 온오프
	function onOffLayer(param){
		wavus.map.getSynView().then(function(view){
					param.layer.setVisible(param.visible);
					// 해상도 기준 레이어 제어
					if(view.getResolution() > param.layer.getMaxResolution() && param.visible){

						if(confirm('현재 해상도에서는 레이어를 확인할 수 없습니다. 확대 하시겠습니까?')){
							view.setResolution(param.layer.getMaxResolution() - 0.00000000001);
						}
					}

					/* 줌 기준 레이어 제어 */
					/*
					 * if(view.getZoom() < param.layer.get('zoom') &&
					 * param.visible){ if(confirm('현재 해상도에서는 레이어를 확인할 수 없습니다. 확대
					 * 하시겠습니까?')){ view.setZoom(param.layer.get('zoom')); } }
					 */
				})
	}

	function getImgLayerExtent(param){
		var projection = param.projection || wavus.config.mapInfo.projection;
		var layer_name = param.layer || "";
		var extent;
		$.ajax({
				url : wavus.config.info.gisServer.realServerUrl + "/wms?request=GetCapabilities&service=WMS&version=1.1.1",
//				url : wavus.config.info.gisServer.url + "/wms?request=GetCapabilities&service=WMS&version=1.1.1",
				async: false
			}
		).then( function(response){
	    	var layers = $(response).find("WMT_MS_Capabilities").find("Capability").find("Layer").find("Layer");
	    	var layer = _.find(layers, function(layer){
	    		return $(layer).find("Name").eq(0).text() == layer_name;
	    	});
	    	var boundingBox = $(layer).find("BoundingBox");
    		var srs = boundingBox.attr("SRS");
    		var tmp_extent = [boundingBox.attr("minx")*1, boundingBox.attr("miny")*1, boundingBox.attr("maxx")*1, boundingBox.attr("maxy")*1];
    		extent = ol.proj.transformExtent(tmp_extent, ol.proj.get(srs), ol.proj.get(projection));
	    })
		return extent;
	}

	function getDrawLayer(){
		return drawLayer;
	}

	function getHighlightLayer(){
		return highlightLayer;
	}

	function getGraphicLayer(){
		return graphicLayer;
	}
	function getRadiusLayer(){
		return radiusLayer;
	}

	// 벡터레이어 z인덱스 관리
	function _manageZIndex(){
		this.z_index = this.z_index + 1;
	}

	/**
	 * 레이어 초기화 param - map : map 객체
	 */
	function init(map){
		var deferred = $.Deferred();
		_map = map;
		setBaseLayer()
				.then(function(drawLayer, highlightLayer, graphicLayer, boundLayer)
				{
					deferred
							.resolve(drawLayer, highlightLayer, graphicLayer, boundLayer);
				});

		return deferred;
	}

	function printInit(map2){
		var deferred = $.Deferred();
		_map2 = map2;
		setBaseLayerMode("solo");
		return deferred;
	}

	function showLayer(layerName){
		findLayerById(layerName).then(function(layer){
			layer.setVisible(true);
		});
	}

	function hideLayer(layerName){
		findLayerById(layerName).then(function(layer){
			layer.setVisible(false);
		});
	}

	function getLayerList(mapObj, _key, _value){
		var list = [];
		if(typeof mapObj == 'undefined') return list;
		mapObj.getLayers().getArray().forEach(function(obj){
			if(typeof _key == 'undefined' || (typeof _key != 'undefined' && _key == '')){
				list.push(obj);
			}else if(typeof _key != 'undefined' && _key != '' && typeof _value != 'undefined' && _value != ''){
				for( var key in obj.getProperties() ) {
					if( key == _key && obj.get(key) == _value ){
						list.push(obj);
					}
				}
 			}
		});
		return list;
	}

	 function removeLayer(mapObj, _name){
		var list = this.getLayerList(mapObj, 'name', _name);
		list.forEach(function(obj, index){
			mapObj.removeLayer(obj);
		});
	}


	 function removeLayerAll(mapObj, excludeCategoryArr, excludeLayerNameArr){
		 if( excludeCategoryArr instanceof String ){
			 excludeCategoryArr = [excludeCategoryArr];
		 }else if( excludeCategoryArr instanceof Array ){
			 console.log("");
		 }else {
			 excludeCategoryArr = [];
		 }

		 if( excludeLayerNameArr instanceof String ){
			 excludeLayerNameArr = [excludeLayerNameArr];
		 }else if( excludeLayerNameArr instanceof Array ){
			 console.log("");
		 }else {
			 excludeLayerNameArr = [];
		 }

		 var isRemove, layerArr = mapObj.getLayers().getArray();
		 for(var kk=layerArr.length - 1; kk >= 0; kk-- ){
			 isRemove = true;

			 if(layerArr[kk].get("baseLayer") == true){
				 isRemove = false;
			 }
			 else{
				 for(var ii = 0; ii < excludeCategoryArr.length; ii++){
					 if( layerArr[kk].get("category") == excludeCategoryArr[ii]){
						 isRemove = false;
						 break;
					 }
				}

				 for(var ii=0; ii < excludeLayerNameArr.length; ii++){
					 if( layerArr[kk].get("name") == excludeLayerNameArr[ii]){
						 isRemove = false;
						 break;
					 }
				 }
			 }

			 if(isRemove) {
				 mapObj.removeLayer( layerArr[kk] );
			 }
		 }
	 }

	 function removeInteraction(mapObj, interactionName){
		 mapObj.getInteractions().getArray().forEach(function(it){
				if(it.get("name") == interactionName){
					try{
						it.getFeatures().clear();
					}
					catch(e){
						console.error(e);
					}
					mapObj.removeInteraction(it);
				}
			});
	 }

	 function removeOverlay(mapObj, overlayName){
		 mapObj.getOverlays().getArray().forEach(function(ovl){
				if(ovl.get("name") == overlayName){
					mapObj.getOverlays().remove(ovl);
				}
			});
	 }

	 function addAreaLabelLayer(mapObj, _feature, areaDis){
			var areaLabel_extent = _feature.getGeometry().getExtent();
			var X = areaLabel_extent[0]; // minX
			var Y = areaLabel_extent[1] + (areaLabel_extent[3]-areaLabel_extent[1])/2; // centerY
			var areaLabel_Feature = new ol.Feature(new ol.geom.Point([X,Y]));
			var areaLabel_style = new ol.style.Style({
				text: new ol.style.Text({
					font: '15px Arial',
					fill: new ol.style.Fill({
						color: '#ff7300'
					}),
					stroke: new ol.style.Stroke({
						color: '#fff' ,
						width: 2
					}),
					textAlign: 'center',
					textBaseline: 'middle',
					text: areaDis
				})
			});
			var areaLabel_Point= new ol.layer.Vector({
				source: new ol.source.Vector({
					features: [areaLabel_Feature]
				}),
	    		zIndex : 90,
	    		visible : true,
				style: areaLabel_style,
				layerId: 'lc_areaLabel',
				name:'lc_areaLabel'
			});
			mapObj.addLayer(areaLabel_Point);
		}

	 //선택한 포인트
	 function selectLayer(mapObj, center){
		 var select_Feature = new ol.Feature(new ol.geom.Point(center));

		 var select_Vector= new ol.layer.Vector({
			 source: new ol.source.Vector({
				 features: [select_Feature]
			 }),
			 zIndex : 80,
			 visible : true,
			 style: function(feature){
					style = [new ol.style.Style({
						image: new ol.style.Icon ( (
								{
									anchor :[ 0.5 , 15 ] ,
									anchorXUnits : 'fraction' ,
									anchorYUnits : 'pixels' ,
									src : '/images/rdm/map/pin_yellow3.png',
//										opacity : 0.8,
									scale : 0.7
								} ) )

					})];

					return style;
				},
			 layerId: 'lc_selectLayer',
			 name:'lc_selectLayer'
		 });
		 mapObj.addLayer(select_Vector);
	 }


		//레이어 팝업창 열기
		function fnLayerPopup(){
//			for(var i=0;i<3;i++){
//				 removeLayer();
//			}

			if($("#layerPop").css("display") != "none"){
				$("#layerPop").hide();
				map = wavus.map.getMap();
//				GlanModify.init();
//				GlanModify.setActive(false);
			}else{
//				getGlanList();
				$("#layerPop").show();
			}
		}
		var layerList = {
				  label : 'layerList'
			    , basic  : [
							  {layerId:'basicLayer0',visible:'true',layerKorName:'시도경계',cnt:'1',style:'style_caa_lpaasido_kl',rule:'',zIndex:1005},
							  {layerId:'basicLayer1',visible:'true',layerKorName:'시군구경계',cnt:'1',style:'style_caa_lpaasgg_kl',maxResolution : '80',rule:'',zIndex:1004},
							  {layerId:'basicLayer2',visible:'true',layerKorName:'읍면동경계',cnt:'1',style:'style_caa_lpaaemd_kl',maxResolution :'30',rule:'',zIndex:1003},
							  {layerId:'basicLayer3',visible:'true',layerKorName:'연속지적도',cnt:'1',style:'style_cla_lppacbnd_kl',maxResolution:'25',rule:'',zIndex:1002},
							  {layerId:'basicLayer4',visible:'true',layerKorName:'토지이용계획도',cnt:'1',style:'style_cda_leplanmap_bi',maxResolution:'25',rule:'',zIndex:1001},
							  {layerId:'basicLayer5',visible:'true',layerKorName:'고속국도_접도구역',cnt:'1',style:'style_tba_ltcui201_kl',maxResolution:'25',rule:'',zIndex:1000},
						  	]
				, bizJigu : [
								{layerId:'bizJiguLayer0',visible:'true',layerKorName:'혁신도시',cnt:'1',style:'style_cda_invcty_bi',maxResolution :'25',rule:'',zIndex:1103}
								,{layerId:'bizJiguLayer1',visible:'true',layerKorName:'행정중심복합도시',cnt:'1',style:'style_cda_atcccty_bi',maxResolution :'25',rule:'',zIndex:1102}
								,{layerId:'bizJiguLayer2',visible:'true',layerKorName:'연안정보_국가산업단지',cnt:'1',style:'style_z_momaf_wgis_ie_gugga',maxResolution :'25',rule:'',zIndex:1101}
								,{layerId:'bizJiguLayer3',visible:'true',layerKorName:'산업단지',cnt:'1',style:'style_use_ie',maxResolution :'25',rule:'',zIndex:1100}
								]
				, useJiyeok  : [
								{layerId:'jiyeokLayer0',visible:'true',layerKorName:'국토계획_도시지역',cnt:'1',style:'style_tba_ltcuq111_kl',maxResolution :'25',rule:'',zIndex:1204}
								,{layerId:'jiyeokLayer1',visible:'true',layerKorName:'국토계획_관리지역',cnt:'1',style:'style_tba_ltcuq112_kl',maxResolution :'25',rule:'',zIndex:1203}
								,{layerId:'jiyeokLayer2',visible:'true',layerKorName:'국토계획_농림지역',cnt:'1',style:'style_tba_ltcuq113_kl',maxResolution :'25',rule:'',zIndex:1202}
								,{layerId:'jiyeokLayer3',visible:'true',layerKorName:'국토계획_자연환경보전지역',cnt:'1',style:'style_tba_ltcuq114_kl',maxResolution :'25',rule:'',zIndex:1201}
				                ,{layerId:'jiyeokLayer4',visible:'true',layerKorName:'국토계획_기타용도지역',cnt:'1',style:'style_tba_ltcuq115_kl',maxResolution:'80',rule:'',zIndex:1200}
								]

				, useJigu : [
				             	{layerId:'jiguLayer0',visible:'true',layerKorName:'보금자리사업지구',cnt:'1',style:'style_cda_homebsdstc_bi',maxResolution :'25',rule:'',zIndex:1302}
				             	,{layerId:'jiguLayer1',visible:'true',layerKorName:'택지개발지구',cnt:'1',style:'style_cda_hvbsdstc_bi',maxResolution :'25',rule:'',zIndex:1301}
				             	,{layerId:'jiguLayer2',visible:'true',layerKorName:'행복주택지구',cnt:'1',style:'style_cda_happyhouse_bi',maxResolution :'25',rule:'',zIndex:1300}
				             	]

				, useGuyeok : [
									{layerId:'guyeokLayer0',visible:'true',layerKorName:'도시개발구역',cnt:'1',style:'style_cda_ujdstrc_bi',maxResolution :'25',rule:'',zIndex:1400}
				               		]

				, nature : [
								{layerId:'natureLayer0',visible:'true',layerKorName:'국토환경성평가지도',cnt:'1',style:'style_tra_tteemap_eg',maxResolution :'25',rule:'',zIndex:1501}
								,{layerId:'natureLayer1',visible:'true',layerKorName:'생태자연도',cnt:'1',style:'style_tra_eclynd_eg',maxResolution :'25',rule:'',zIndex:1500}
								]
				, etc : [
							{layerId:'etcLayer0',visible:'true',layerKorName:'문화재_문화재보호',cnt:'1',style:'style_tba_ltcuo301_kl',maxResolution :'25',rule:'',zIndex:1601}
							,{layerId:'etcLayer1',visible:'true',layerKorName:'등록공장현황',cnt:'1',style:'style_ik_ie_fact_main',maxResolution :'25',rule:'',zIndex:1600}
							]
			};

	var module = {
		setBaseLayer : setBaseLayer,
		findLayerById : findLayerById,
		getLayerById: getLayerById,
		setWmsLayer : setWmsLayer,
		onOffLayer : onOffLayer,
		getImgLayerExtent : getImgLayerExtent,
		getDrawLayer : getDrawLayer,
		getHighlightLayer : getHighlightLayer,
		getGraphicLayer : getGraphicLayer,
		makeVectorLayer : makeVectorLayer,
		makeHeatLayer : makeHeatLayer,
		getShapefile : getShapefile,
		_setTileLayer : _setTileLayer,
		init : init,
		printInit : printInit,
		findLayerPrintById : findLayerPrintById,
		showLayer : showLayer,
		hideLayer : hideLayer,
		getLayerList : getLayerList,
		fnLayerPopup : fnLayerPopup,
		addAreaLabelLayer : addAreaLabelLayer,
		getRadiusLayer : getRadiusLayer,
		selectLayer : selectLayer,
		removeLayer: removeLayer,
		removeLayerAll: removeLayerAll,
		removeInteraction: removeInteraction,
		removeOverlay: removeOverlay,
		layerList : layerList
	};

	return module;

})();
