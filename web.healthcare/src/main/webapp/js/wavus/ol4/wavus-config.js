window.wavus = window.wavus || {};

wavus.config = (function() {
	var _layerListUrl = $(location).attr('protocol')+'//'+$(location).attr('host')+'/spceInfo/layer_info.do';
	/*
	 * 서버 정보
	 */
	var info = {
		gisServer : {
			url : globalProperty.mapLayerUrl +  'geoserver',
			realUrl : globalProperty.mapLayerRealUrl +  'geoserver',
			gwcUrl : globalProperty.mapLayerUrl +  'geoserver/gwc/service/wms',
			gwcRealUrl : globalProperty.mapLayerRealUrl +  'geoserver/gwc/service/wms'
		}
	};
	
	var lhCenters = [{deptCd: "30495", xy: [186338.6653805807, 184175.48498412812]} // 광주전남지역본부	광주광역시 서구 시청로 91
							, {deptCd: "30321", xy: [386003.2023440325, 181667.8422534297]} // 부산울산지역본부	부산광역시 동구 중앙대로 224				
							, {deptCd: "30377", xy: [209697.71266766812, 426582.98488089046]} // 경기지역본부	경기도 성남시 분당구 성남대로 54번길 3(구미동)
							, {deptCd: "30345", xy: [174425.11522076913, 433259.31679960794]} // 인천지역본부	인천광역시 남동구 논현로 46번길 23
							, {deptCd: "30537", xy: [339451.9543925281, 257505.8455532836]} // 대구경북지역본부	대구광역시 달서구 상화로 272
							, {deptCd: "30449", xy: [234679.15974895115, 317351.71327969595]} // 대전충남지역본부	대전광역시 서구 둔산중로 108
							, {deptCd: "30568", xy: [353457.0378265875, 193873.36087970852]} // 경남지역본부	경상남도 창원시 의창구 중앙대로 215
							, {deptCd: "31989", xy: [223730.00213280768, 332680.31022608833]} // 세종특별본부	세종특별자치시 가름로 238-3
							, {deptCd: "30427", xy: [264040.3866883989, 485423.3976089318]} // 강원지역본부	강원도 춘천시 공지로 337
							, {deptCd: "30476", xy: [243451.72712989178, 346185.76017681626]} // 충북지역본부	충청북도 청주시 서원구 청남로 2060
							, {deptCd: "31434", xy: [199338.78699562614, 447686.8906793676]} // 미군기지본부	서울시 용산구 장문로 9
							, {deptCd: "30289", xy: [203557.7275863531, 445797.5859443576]} // 서울지역본부	서울특별시 강남구 선릉로 121길 12
							, {deptCd: "30518", xy: [209413.36827536803, 256397.68107568193]} // 전북지역본부	전라북도 전주시 완산구 홍산로 158
							, {deptCd: "30587", xy: [155666.15329547194, 960.0714811586502]} // 제주지역본부	제주특별자치도 제주시 전농로 100
							, {deptCd: "32330", xy: [235805.41022605365, 322744.4650740268]} // LH토지주택대학교	대전광약시 유성구 전민동 462-2
						];
	
//	var centerXY = [304303.4169128897, 187171.36610007135]; // 기본값: LH 진주 본사
	var centerXY = [197390.0,450315.5]; // 기본값(서울역)
//	var centerXY = [953845.4737463454, 1951879.3834282116];  // 기본값(서울역)
	
	lhCenters.forEach(function(info, bb){
		if( info.deptCd == globalProperty.userDeptCd){
			centerXY = info.xy;
		}
	});
	
	var mapInfo = {
			center : centerXY,
			zoom : 13,
			minZoom : 8,
			maxZoom : 20,
			projection : 'EPSG:5174'
	}
	
	/*
	 * DB좌표계
	 */
	//샘플 데이터 좌표계
	proj4.defs('EPSG:5186', '+proj=tmerc +lat_0=38 +lon_0=127 +k=1'+ '+x_0=200000 +y_0=600000 +ellps=GRS80' + '+units=m +no_defs ');
	//LH공사 사용 GIS 데이터 좌표계																																												
	proj4.defs('EPSG:5174', '+proj=tmerc +lat_0=38 +lon_0=127.0028902777778 +k=1 +x_0=200000 +y_0=500000 +ellps=bessel +units=m +no_defs +towgs84=-115.80,474.99,674.11,1.16,-2.31,-1.63,6.43');
	//네이버 사용 좌표계  +towgs84=-115.80,474.99,674.11,1.16,-2.31,-1.63,6.43
	//현재 네이버지도는 좌표계 값을 바꿔도 움직이지 않는 현상이 발생하고있음
	proj4.defs('EPSG:5179', '+proj=tmerc +lat_0=38 +lon_0=127.5 +k=0.9996 +x_0=1000000 +y_0=2000000 +ellps=GRS80 +units=m +no_defs' );
	//다음 사용 좌표계 (5174와 맞추기 위해 보정계수 도입 (+towgs84=115.80,-474.99,-674.11,-1.16,2.31,1.63,-6.43))
	proj4.defs('EPSG:5181daum', '+proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=500000 +ellps=GRS80 +units=m +no_defs +towgs84=115.80,-474.99,-674.11,-1.16,2.31,1.63,-6.43');
	proj4.defs('EPSG:5181', '+proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=500000 +ellps=GRS80 +units=m +no_defs');
	proj4.defs('EPSG:32652', '+proj=utm +zone=52 +ellps=WGS84 +datum=WGS84 +units=m +no_defs');
	var DBProjection = ol.proj.get('EPSG:5179');
	var EPSG32652 = ol.proj.get('EPSG:32652');
	ol.proj.setProj4 = proj4;
	/*
	 * 벡터레이어 피처색 정의
	 */
	var mapperColor = {
			'대' : 1,
			'장' : 2,
			'전' : 3,
			'답' : 4,
			'구' : 5,
            '도' : 6,
            '답' : 7,
            '학' : 8,
            '잡' : 9
	}
	
	/**
	 * passi-cmprn 피처색 정의
	 */
	var passiCmprnColor = {
		'대' : 1,
		'잡' : 2,
		'도' : 3,
		'학' : 4,
		'구' : 5,
	}
	
	
	/*
	 * 레이어 정의
	 */
	var layers = [];
	
	/**
	 * zoom scale 정보
	 */
	var mapZoomScaleInfo = [    
	 {
		  zoom : 1,
		  resolution : 2445.98,
		  scale : 9244667.36
	  },                  
	  {
		  zoom : 2,
		  resolution : 1222.99,
		  scale : 4622333.68
	  },                  
	  {
		  zoom : 3,
		  resolution : 611.50,
		  scale : 2311166.84
	  },                  
	  {
		  zoom : 4,
		  resolution : 305.75,
		  scale : 1155583.42
	  },                  
	  {
		  zoom : 5,
		  resolution : 152.87,
		  scale : 577791.71
	  },                  
	  {
		  zoom : 6,
		  resolution : 76.44,
		  scale : 288895.85
	  },                  
	  {
		  zoom : 7,
		  resolution : 38.22,
		  scale : 144447.93
	  },                  
	  {
		  zoom : 8,
		  resolution : 19.11,
		  scale : 72223.96
	  },                  
	  {
		  zoom : 9,
		  resolution : 9.55,
		  scale : 36111.98
	  },                  
	  {
		  zoom : 10,
		  resolution : 4.78,
		  scale : 18055.99
	  },                  
	  {
		  zoom : 11,
		  resolution : 2.39,
		  scale : 9028.00
	  },                  
	  {
		  zoom : 12,
		  resolution : 1.19,
		  scale : 4514.00
	  },                  
	  {
		  zoom : 13,
		  resolution : 0.60,
		  scale : 2257.00
	  },                  
	  {
		  zoom : 14,
		  resolution : 0.30,
		  scale : 1128.50
	  }
    ];

	function init() {
		var deferred = $.Deferred();
		
		wavus.util.ajax({
			url : _layerListUrl,
			dataType : "json",
			type : 'POST'
		}).then(function (result){
			layers = result.layerList;
			
			deferred.resolve();
		}).fail(function (xhr, status, thrown){
			console.log(status);
		});
		
		return deferred;
	}
	
	var module = {
		init : init,
		mapZoomScaleInfo : mapZoomScaleInfo,
		mapInfo  : mapInfo,
		DBProjection : DBProjection,
		layers : layers,
        mapperColor : mapperColor,
        EPSG32652 : EPSG32652,    
        passiCmprnColor : passiCmprnColor
	};

	module.info = info;
	return module;
})();
