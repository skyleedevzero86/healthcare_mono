window.wavus = window.wavus || {};

wavus.geocode = (function() {
	
	function addButton(tocDiv){
		wavus.file.setUploadObjectId ( "#filepeke" );
    	$("#"+tocDiv).append("&nbsp;<input type='button' id='geocode_btn' name='geocode_btn' value='지오코딩'/>");
    	$("#geocode_btn").click(function(){
    		$("#modal_popup1").modal();
		});
	}
	
	function _getGeocodeLocation(goecode_layerName){
		var agent = navigator.userAgent.toLowerCase ( );
		if ( ( navigator.appName == 'Netscape' && navigator.userAgent.search ( 'Trident' ) != - 1 ) || ( agent.indexOf ( "msie" ) != - 1 ) ) {
			// ie 일때 input[type=file] init.
			$ ( "#file_up" ).replaceWith ( $ ( "#file_up" ).clone ( true ) );
			$ ( "#parentCreateGroupFullName" ).val ( "" );
		}
		else {
			// other browser 일때 input[type=file] init.
			$ ( "#file_up" ).val ( "" );
			$ ( "#parentCreateGroupFullName" ).val ( "" );
		}
		
		var address_v = $ ( "#address" ).val ( ).split ( "," );
		address_v.splice(0,1); // 주소파일 헤더가 존재할 경우;
		var count = address_v.length;
		var coor = "";
		var pro = "";
		var featureArray = new Array();
		_.each ( address_v , function ( model ) {
			$.ajax ({
						type : "GET" ,
						url : "https://dapi.kakao.com/v2/local/search/address.json?query=" + encodeURI ( model ) + "&page=1&size=10" ,
						dataType : "json" ,
						beforeSend : function ( xhr ) {
							xhr.setRequestHeader ( "Authorization" , "KakaoAK a0b5ba8e2575b4cbaf45c1d665dfdd0c" );
							xhr.setRequestHeader ( "Content-type" , "application/x-www-form-urlencoded" );
						} ,
						success : function ( result ) {
							count--;
							pro = ol.proj.transform ([result.documents[0].x , result.documents[0].y	] , 'EPSG:4326' , 'EPSG:5174' );
							 
								if ( 0 < count ) {
									coor += "[" + pro + "]" + ",";
								}
								else {
									coor += "[" + pro + "]";
								}
							 
								var iconFeature = new ol.Feature (
									{
										geometry : new ol.geom.Point ( pro )
									}
								);
								
								var iconStyle = new ol.style.Style (
										{
											image : new ol.style.Icon ( (
												{
													anchor :
														[
															0.5 , 46
														] ,
													anchorXUnits : 'fraction' ,
													anchorYUnits : 'pixels' ,
													src : 'https://openlayers.org/en/v3.19.1/examples/data/icon.png'
												} ) )
										} );
								iconFeature.setStyle ( iconStyle );
								featureArray.push(iconFeature);
							
							if ( count == 0 ) {
								var coor_v = JSON.parse ( '[[' + coor + ']]' );
								var geojsonObject =
									{
										"type" : "FeatureCollection" ,
										"features" :
											[
													{
														"type" : "Feature" ,
														"geometry" :
															{
																'type' : 'Polygon' ,
																'coordinates' : coor_v
															}
													}
											]
									};
								
								var vectorSource = new ol.source.Vector (
									{
										features : ( new ol.format.GeoJSON ( ) ).readFeatures ( geojsonObject )
									} );
								
								wavus.map.getView ( ).fit ( vectorSource.getExtent ( ) );
								
								var geoCodeSource = new ol.source.Vector ();
								geoCodeSource.addFeatures(featureArray);
								var geoCodeVectorLayer = new ol.layer.Vector ();
								geoCodeVectorLayer.setSource(geoCodeSource);
								
								geoCodeVectorLayer.setZIndex ( 10 );
								geoCodeVectorLayer.set('name',goecode_layerName);
								wavus.map.getMap ( ).addLayer ( geoCodeVectorLayer );
								wavus.layer.showLayer(goecode_layerName);
								
								var beforeJson = (new ol.format.GeoJSON()).writeFeaturesObject(featureArray);
								var convertJson = JSON.stringify(beforeJson);
					 			var url="/file/json/upload.do";  
								$.ajax({      
							        type:"POST",  
							        data: {
							        	filename:goecode_layerName,
							        	json:convertJson
							        	},
							        url:url,      
							        dataType:'json',      
							        success:function(args){   
							        	console.log('success');
							        }
							    });
							}
						} 
					} );
		});
	}
	
	function GeocodeValidate(goecode_layerName){
		if ( wavus.file.getFileCount ( '#filepeke' ) > 0 ) {
			if ( wavus.file.getFileCheck ( '#filepeke' ) ) {
				wavus.file.uploadFile ( '#filepeke' );
			}
			else {
				alert ( '첨부할 수 없는 파일입니다.' );
				return false;
			}
		}
		_fileCheck (goecode_layerName);
	}
	
	function _fileCheck (goecode_layerName) {
		var fileValue = ($(".jFiler-item")[0].textContent);
		fileValue = fileValue.substr(0, fileValue.indexOf("size: "));

		var fileName = fileValue;
		if ( fileValue.length > 1 ) {
			var param =
				{
					"fileName" : fileName
				};
			
			$.ajax (
				{
					url : '/file/getContent.do' ,
					type : 'POST' ,
					data : param ,
					dataType : 'json' ,
					success : function ( result ) {
						$ ( "#address" ).val ( result );
						_getGeocodeLocation(goecode_layerName);
					}
				} );
		}
	}
	
	function loadFileGeoJson(){
		//----------
		var getUserID = '';
		if(wavus.user.getUserName()!= "" ) {
			getUserID = wavus.user.getUserName();
		}
		//-----------
		    var url="/file/json/read.do";  
			$.ajax({      
		        type:"POST",  
		        url:url,
		        data:{userID : getUserID},
		        dataType:'json',      
		        success:function(args){  
			 		var testVectorMap = new ol.layer.Vector({
						source : new ol.source.Vector({
							features: (new ol.format.GeoJSON()).readFeatures(args.fileCn)
						}),
						zIndex : 11
					});
			 		wavus.layer.findLayerById('testVectorMap').then(function(layer){
						wavus.map.getMap().removeLayer(layer);
					});
		 			testVectorMap.set('name', 'testVectorMap');
					wavus.map.getMap().addLayer(testVectorMap);
					wavus.layer.showLayer('testVectorMap');
					var modifySource = testVectorMap.getSource().getFeaturesCollection();
					var modify = new ol.interaction.Modify({features:new ol.Collection(testVectorMap.getSource().getFeatures())})
					wavus.map.getMap().addInteraction(modify);
		        }  
		    });
	}
	
	var module = {
//		toggle: toggle,
		addButton: addButton,
		loadFileGeoJson: loadFileGeoJson,
		GeocodeValidate : GeocodeValidate
	};

	return module;
	
})();