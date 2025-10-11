
window.wavus = window.wavus || {};

wavus.draw = (function() {
	
	var _map;
	var _drawLayer;
	var _textLayerCnt = 0;
	var _translateLayer;
	
	var draw;
	var drawType;
	var helpTooltipElement, helpTooltip;
	var sketch;
	var mapListener, mousListener;
	var msg_selectMap = '지도를 클릭해주십시오.';
	var msg_continuePolygon = '더블클릭하면 다각형을 완성할 수 있습니다.';
	var msg_continueLine = '더블클릭하면 선을 완성할 수 있습니다.';
	var msg_continueBox = '클릭하면 사각형을 완성할 수 있습니다.';
	var msg_continueCircle = '클릭하면 원을 완성할 수 있습니다.';
	var msg_continueText = "텍스트 입력 후 지도를 클릭해주십시오.<br /><br /><textarea id='text_layer_contents' name='text_layer_contents' style='position:fixed;width:220px;height:42px;resize:none;' ></textarea>";
	
	// 드로우 인터렉션 추가
	function addDraw(value, style, vectorSource, memoType) {
		var deferred = $.Deferred();
		var geometryFunction;
		drawType = value;
		if(value == 'Box'){
			value = 'Circle';
			geometryFunction = ol.interaction.Draw.createBox();
		}
		if(value == 'textPoint'){
			wavus.controller.buttonChange({cursor:"copy"});
			_map.on("singleclick", function(e){
				if($("#text_layer_contents").val().replace(/ /g, "") == ""){
					alert("텍스트를 입력하세요");
					return;
				}
				var textLayerCnt = _textLayerCnt;
				var textLayerId = 'text_' + textLayerCnt; 
				var textVector;
				wavus.layer.findLayerById(textLayerId).then(function(layer){
					textVector =  layer;
				});
				if(!(textVector instanceof ol.layer.Vector)){
					wavus.layer.makeVectorLayer(textLayerId, wavus.map.getMap(), 7000).then(function(layer){
						textVector =  layer;
					});
				}
				var textFeature = new ol.Feature({ geometry : new ol.geom.Point(e.coordinate), feature_type : 'text'});
				if(textVector.getSource().getFeatures().length > 0){
					var featureSvgObj = $((new DOMParser()).parseFromString(textVector.getSource().getFeatures()[0].getStyle().getImage().getSrc().replace("data:image/svg+xml, ", ""), "image/svg+xml"));
					var strokeColor = featureSvgObj.find("polygon").attr("stroke");
					var fillColor = featureSvgObj.find("polygon").attr("fill");
					var memoTypeId = featureSvgObj.find("svg").attr("id");
					var textColor = featureSvgObj.find("text").attr("fill");
					var textSize = featureSvgObj.find("text").attr("font-size");
					var textFontType = featureSvgObj.find("text").attr("font-family");
					
					textFeature.setStyle(createTextImgSVG({ 
						polygonStroke : strokeColor,
						polygonFill : fillColor,
						type : memoTypeId,
						textFill : textColor,
						textFontFamily : textFontType,
						textFontSize : textSize,
						textString : $("#text_layer_contents").val()
					}));
				}else{
					textFeature.setStyle(createTextImgSVG({textString : $("#text_layer_contents").val(), type : memoType}));
				}
				textVector.getSource().addFeature(textFeature);
				if($("#add_" + textLayerId).is(":checked") != true){
					$("#add_" + textLayerId).click();
				}
				$("#text_layer_contents").focus();
			});
		}else{
			draw = new ol.interaction.Draw({
				source : vectorSource || _drawLayer.getSource(),
				type : value,
				style : style || wavus.symbol.findSymbol({style : 'draw'}),
				geometryFunction : geometryFunction
			});
			_map.addInteraction(draw);
			draw.on('drawstart', _checkTootip);
			draw.on('drawend', _endTootip);
		}
		_createHelpTooltip();
		mousListener = _map.on('pointermove', _pointerMoveHandler);
		_map.getViewport().addEventListener('mouseout', _mouseoutHandler);
		
		deferred.resolve(draw);
		return deferred;
	}
	
	function createTextImgSVG(params){
		/*
		params.polygonStroke : 배경선색
		params.polygonFill : 배경선색 
		params.textFill : 글자색
		params.textFontFamily : 폰트종류
		params.textFontSize : 폰트크기
		params.textString : 입력한 내용
		params.svg : svg
		
		params.type : 'title' ---> 사각형
							'bubble' ---> 말풍선
		*/
		var polygonStroke = params.polygonStroke || "#000000";
		var polygonFill = params.polygonFill || "rgba(255,255,255,0.5)";
		var polygonType = params.type || 'title';
		var textFill = params.textFill || "#000000";
		var textFontFamily = params.textFontFamily || "malgun gothic";
		var textFontSize = params.textFontSize || "15";
		var textString = params.textString || "";
		var svgObj = params.svg || null;
		var textWidth = 0;
		var textHeight = 0;
		var imgHeight = 0;
		var tspanX = "5";
		var tspanY = (textFontSize*1) + 2;
		var marginX = "5";
		var marginY = "5";
		tspanX = (tspanX*1) + (marginX*1);
		var textLineArr = new Array();
		if(svgObj != null){
			svgObj.find("text tspan").each(function(){ 
				textLineArr.push(decodeURIComponent($(this).text())); 
			});
		}else{
			textLineArr = textString.split('\n');
		}
		for(var i=0; i<textLineArr.length; i++){
			var tmpWidth = _getTextWidth(textLineArr[i], 'normal ' + textFontSize + 'px "' + textFontFamily + '"');
			textWidth = textWidth < tmpWidth ? tmpWidth : textWidth;
			if(textLineArr[i] == "") textLineArr[i] = " ";
		}
		textString = textLineArr.join('</tspan><tspan x="' + tspanX + '" dy="' + tspanY + '">');
		textWidth = Math.round(textWidth + (tspanX*1) + (marginX*2));
		textHeight = Math.round((textLineArr.length * tspanY) + (tspanY/2) + (marginY*2));
		
		if(polygonType.toLowerCase() == 'title' || polygonType.toLowerCase() == 'circletitle'){
			imgHeight = textHeight;
		}else if(polygonType.toLowerCase() == 'bubble' || polygonType.toLowerCase() == 'circlebubble'){
			imgHeight = textHeight + Math.round(textWidth/4);
		}
		var polygonCoord = new Array();
		polygonCoord = _getTextPolygonArray(polygonType, textWidth, textHeight, imgHeight);
		var polygonCoordStr = polygonCoord.join(" ");
		var imgSVG = '<svg version="1.1" id="' + polygonType.toLowerCase() + '" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" '+
							'width="' + (textWidth+2) + 'px" height="' + imgHeight + 'px" viewBox="0 0 ' + (textWidth+2) + ' ' + imgHeight + '">'+
								'<polygon points="' + polygonCoordStr + '" stroke="' + polygonStroke + '" fill="' + polygonFill + '" stroke-width="1"/>'+
								'<text x="0" y="' + marginY + '" fill="' + textFill + '" font-family="' + textFontFamily + '" font-size="' + textFontSize + '" font-weight="normal"><tspan x="' + tspanX + '" dy="' + tspanY + '">' + textString + '</tspan></text>'+
							'</svg>';
		var imgElement = new Image();
		imgElement.src = 'data:image/svg+xml, ' + imgSVG;
		var text_feature_style = new ol.style.Style({
			image : new ol.style.Icon({
				img : imgElement,
				imgSize : [textWidth+2, imgHeight]
			})
		});
		
		return text_feature_style;
	}
	
	function _getTextPolygonArray(pType, pWidth, pHeight, imgHeight){
		var polygonArray = new Array();
		var borderRadius = 5;
		var partitionTot = 1*(2*(borderRadius-1)/borderRadius).toFixed(5);
		var patition = 1*(partitionTot/borderRadius).toFixed(5);
		var heightSum = 0;
		var tmpRadius = borderRadius;
		if(pType.toLowerCase() == 'title'){
			polygonArray = [
	    		[0, 0], 
	    		[0, pHeight], 
	    		[pWidth, pHeight],
	    		[pWidth, 0]
			];
		}else if(pType.toLowerCase() == 'circletitle'){
			polygonArray.push([tmpRadius, 0]);
			for(var i=patition; i<=partitionTot; i+=patition){
				heightSum = heightSum + i;
				polygonArray.push([--tmpRadius, heightSum]);
			}
			polygonArray.push([tmpRadius, pHeight-heightSum]);
			for(var i=partitionTot; i>patition; i-=patition){
				heightSum = heightSum - i
				polygonArray.push([++tmpRadius, pHeight-heightSum]);
			}
			polygonArray.push([pWidth-tmpRadius, pHeight]);
			for(var i=patition; i<=partitionTot; i+=patition){
				heightSum = heightSum + i
				polygonArray.push([pWidth-(--tmpRadius), pHeight-heightSum]);
			}
			polygonArray.push([pWidth-tmpRadius, heightSum]);
			for(var i=partitionTot; i>patition; i-=patition){
				heightSum = heightSum - i
				polygonArray.push([pWidth-(++tmpRadius), heightSum]);
			}
		}else if(pType.toLowerCase() == 'bubble'){
			polygonArray = [
        		[0, 0], 
        		[0, pHeight], 
        		[Math.round(pWidth/2)-Math.round(pWidth/10), pHeight], 
        		[Math.round(pWidth/2), imgHeight],
        		[Math.round(pWidth/2)+Math.round(pWidth/10), pHeight],
        		[pWidth, pHeight],
        		[pWidth, 0]
    		];
		}else if(pType.toLowerCase() == 'circlebubble'){			
			polygonArray.push([tmpRadius, 0]);
			for(var i=patition; i<=partitionTot; i+=patition){
				heightSum = heightSum + i;
				polygonArray.push([--tmpRadius, heightSum]);
			}
			polygonArray.push([tmpRadius, pHeight-heightSum]);
			for(var i=partitionTot; i>patition; i-=patition){
				heightSum = heightSum - i
				polygonArray.push([++tmpRadius, pHeight-heightSum]);
			}
			
			polygonArray.push([Math.round(pWidth/2)-Math.round(pWidth/10), pHeight]); 
			polygonArray.push([Math.round(pWidth/2), imgHeight]);
			polygonArray.push([Math.round(pWidth/2)+Math.round(pWidth/10), pHeight]);
			
			polygonArray.push([pWidth-tmpRadius, pHeight]);
			for(var i=patition; i<=partitionTot; i+=patition){
				heightSum = heightSum + i
				polygonArray.push([pWidth-(--tmpRadius), pHeight-heightSum]);
			}
			polygonArray.push([pWidth-tmpRadius, heightSum]);
			for(var i=partitionTot; i>patition; i-=patition){
				heightSum = heightSum - i
				polygonArray.push([pWidth-(++tmpRadius), heightSum]);
			}
		}
		return polygonArray;
	}
	
	function _getTextWidth(text, fontStyle){
		var canvas = document.createElement("canvas");
		var context = canvas.getContext("2d");
		context.font = fontStyle;
		return context.measureText(text).width;
	}
		
	function deleteDrawLayer(drawType){
		wavus.controller.buttonChange({cursor:""});
		wavus.controller.layerOverPointer({ 
			cursor: 'alias', 
			layerType: 'draw',
			layerName: drawType
		});
		_map.on("singleclick", function(e){
			_map.forEachFeatureAtPixel(e.pixel, function(feature, layer){
				try{
					if(drawType == "text"){
						if(feature.get("feature_type") == drawType){
							layer.getSource().removeFeature(feature);
							if(layer.get('name').split("_")[1]*1 <= _textLayerCnt && layer.getSource().getFeatures().length == 0){
								if(layer.get('name').split("_")[1]*1 == _textLayerCnt){
									_textLayerCnt = _textLayerCnt + 1;
								}
								wavus.map.removeLayer(layer);
							}
						}
					}else{
						if(_drawLayer == layer && feature.get("feature_type") == drawType){
							if(drawType == 'measure'){
								var coord = feature.getGeometry() instanceof ol.geom.Polygon ? feature.getGeometry().getInteriorPoint().getCoordinates() : feature.getGeometry().getLastCoordinate(); 
								wavus.measure.removeResult(coord);
							}
							layer.getSource().removeFeature(feature);
						}else{
							console.log(layer.get("name"));
							return ;
						}
					}
				}catch(e){
					console.log(e);
				}
			});
		});
	}
	
	function moveTextLayer(){
		if(_translateLayer != undefined){
			_map.removeInteraction(_translateLayer);
		}
		var layerArray = new Array();
		$.each($.grep(_map.getLayers().getArray(), function(v, i){ return v instanceof ol.layer.Vector && v.get('name') != undefined && v.getSource().getFeatures()[0].get("feature_type") == 'text'}), function(key, layer){
			layerArray.push(layer);
		});
		_translateLayer = new ol.interaction.Translate({ layers : layerArray });
		_map.addInteraction(_translateLayer);
	}
	
	var _mouseoutHandler = function(){
		helpTooltipElement.classList.add('hidden');
	}
	
	// 드로우 지우기
	function removeDraw(){
		_map.removeInteraction(draw);
		_map.getViewport().removeEventListener('mouseout', _mouseoutHandler);
		ol.Observable.unByKey(mousListener);
		draw = null;
		if(helpTooltip)_map.removeOverlay(helpTooltip);
	}
	
	// 드로우 클리어
	function clearDraw(){
		$.each($.grep(_drawLayer.getSource().getFeatures(), function(feature, idx){ return feature.get('feature_type') != 'text' }), function(idx, feature){
			_drawLayer.getSource().removeFeature(feature);
		});
	}
	
	// 반경검색 드로우 클리어
	function clearRadius(layerId){
		var _layer; 
		wavus.layer.findLayerById(layerId).then(function(layer){
			_layer = layer;
		});
		$.each($.grep(_layer.getSource().getFeatures(), function(feature, idx){ return feature.get('feature_type') != 'text' }), function(idx, feature){
			_layer.getSource().removeFeature(feature);
		});
	}
	
	function getDraw(){
		return draw;
	}
	
	function _createHelpTooltip() {
		if (helpTooltipElement) {
			helpTooltipElement.parentNode.removeChild(helpTooltipElement);
		}
		helpTooltipElement = document.createElement('div');
		helpTooltipElement.className = 'tooltip hidden';
		helpTooltip = new ol.Overlay({
			element : helpTooltipElement,
			offset : [ 15, 0 ],
			positioning : 'center-left'
		});
		_map.addOverlay(helpTooltip);
	}
	
	var _pointerMoveHandler = function(evt) {
		if (evt.dragging) {
			return;
		}
		/** @type {string} */
		var tooltipMsg = msg_selectMap;
		
		if (sketch) {
			if(drawType == 'Polygon'){
				tooltipMsg = msg_continuePolygon;
			}else if(drawType == 'LineString'){
				tooltipMsg = msg_continueLine;
			}else if(drawType == 'Box'){
				tooltipMsg = msg_continueBox;
			}else if(drawType == 'Circle'){
				tooltipMsg = msg_continueCircle;
			}
		}
		if(drawType == 'textPoint'){
			tooltipMsg = msg_continueText; 
		}
		if($("#text_layer_contents").length == 0){
			helpTooltipElement.innerHTML = tooltipMsg;
		}
		helpTooltip.setPosition(evt.coordinate);
		helpTooltipElement.classList.remove('hidden');
		if(drawType == 'textPoint'){
			helpTooltip.getElement().style.height = "90px";
			$("#text_layer_contents").focus();
			$("#text_layer_contents").on('keyup keydown keypress', function(e){
				var repStr = $(this).val().replace(/[^가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9\!\?\,\.\(\)\~\:\-\_\n ]/gi, "");
				$(this).val(repStr);
			});
		}	
	}
	
	var _checkTootip = function(evt){
		sketch = evt.feature;
	}
	
	var _endTootip = function(){
	    sketch = null;
// _createHelpTooltip();
	}
	function setTextLayerCnt(cnt){
		_textLayerCnt = cnt;
	}
	function getTextLayerCnt(){
		return _textLayerCnt; 
	}
	function setNextTextLayerCnt(){
		var chk = true;
		if(_textLayerCnt == 0){
			_textLayerCnt = 1;
		}
		while(chk){
			var layerChk = false;
			wavus.layer.findLayerById('text_'+_textLayerCnt).then(function(layer){
				if(layer != undefined){
					layerChk =  true;
				}
			});
			if(layerChk == true){
				_textLayerCnt = _textLayerCnt + 1;
			}else{
				chk = false;
			}
		}
	}
	
	function getTranslateLayer(){
		return _translateLayer;
	}
	
	var init = function(map, drawLayer){
		_map = map;
		_drawLayer = drawLayer;
	}
	
	var module = {
			addDraw : addDraw,
			removeDraw : removeDraw,
			clearDraw : clearDraw,
			getDraw : getDraw,
			createTextImgSVG : createTextImgSVG,
			deleteDrawLayer : deleteDrawLayer,
			setTextLayerCnt : setTextLayerCnt,
			getTextLayerCnt : getTextLayerCnt,
			setNextTextLayerCnt : setNextTextLayerCnt,
			moveTextLayer : moveTextLayer,
			getTranslateLayer : getTranslateLayer,
			clearRadius : clearRadius,
			init : init
		};
	
	return module;
})();