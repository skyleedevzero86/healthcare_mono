
window.wavus = window.wavus || {};

wavus.legend = (function() {
	
	var _map;
	
	//레전드 만들기
	function setLayerLegend(param){
		var layerId = param.layerId;
		if(wavus.util.isEmpty(layerId)){
			return console.log('Not Found layerId');
		};
		var imgDom = param.imgDom;
		if(wavus.util.isEmpty(imgDom)){
			return console.log('Not Found imgDom');
		};
		var workSpace = param.workSpace;
		if(wavus.util.isEmpty(workSpace)){
			return console.log('Not Found workSpace');
		};
		
		var width = param.width || 20;
		var height = param.height || 20;
		var format = param.format || 'image/png';
		var style = param.style;
		var viewRule = param.viewRule || 'draw';
		var backgroundColor = '0x' + param.backgroundColor || '0x' + 'fff';
		
		var url = wavus.config.info.gisServer.baseUrl + '/wms?REQUEST=GetLegendGraphic&VERSION=1.0.0';
		url += '&LAYER=' + workSpace + ':' + layerId;
		url += '&width=' + width;
		url += '&height=' + height;
		url += '&format=' + format;
		url += '&style=' + style;
//		url += '&rule=' + viewRule;
		url += '&LEGEND_OPTIONS=bgColor:' + backgroundColor;
		
		imgDom.attr('src', url);
	}
	
	function setCustomLayer(tocDiv){
		_layerController('wms'); // wms 레이어 컨트롤 추가
    	_layerController('vector'); // vector 레이어 컨트롤 추가
    	_layerDefaultEvent();
    	_wmsStyleChange(); // WMS 스타일 변경 이벤트 추가
		_vectorStyleChange(); // Vector 스타일 변경 이벤트 추가
    	_layerConfigLoad(); // 사용자 레이어 설정 호출
    	_userLayerLoad(); // 사용자 임의 레이어 호출
    	
    	$("#"+tocDiv+" #mainTOC").next(".TOCButtonDiv").append("<input type='button' id='config_save' name='config_save' value='저 장' style='margin:5px;width:80px;'/>");
    	$("#"+tocDiv+" #mainTOC").next(".TOCButtonDiv").append("<input type='button' id='config_clear' name='config_clear' value='초기화' style='margin:5px;width:80px;'/>");
    	$("#config_save").click(function(){
			_configSaveClear("save");
		});
		$("#config_clear").click(function(){
			_configSaveClear("clear");
		});
	}
	
	function _layerDefaultEvent(){
		/* 레이어 추가, Feature 추가 시 컨트롤 창 생성 ↓↓ */
		_map.getLayers().on('add', function(evt){
			_TOCLayerChange(evt.element);
		});
		
		$.each(_map.getLayers().getArray(), function(key, layer){
			_TOCLayerChange(layer);
		});
		/* 레이어 추가, Feature 추가 시 컨트롤 창 생성 ↑↑ */
	}
	
	function _layerController(layerType){ // Vector Layer를 TOC에 추가, Style 변경 기능 추가
		if(layerType == 'wms'){
			$("#mainTOC").find("input[type=checkbox]").each(function(){
				_TOCModAdd($(this).prop("id"));
			});
		}else if(layerType == 'vector'){
			$.each($.grep(_map.getLayers().getArray(), function(v, i){ return v instanceof ol.layer.Vector && v.get('name') != undefined }), function(key, layer){
				wavus.layer.hideLayer(layer.get("name"));
				wavus.controller.addTOCElement(layer.get("name"));
				_styleLegend(layer);
			});
		}
	}
	
	function _userLayerLoad(){
		var url="/file/json/multiRead.do";  
		$.ajax({      
	        type:"POST",
	        url:url,
	        dataType:'json',
	        async:false,
	        success:function(args){
	        	$.each(args, function(key, val){
	        		if(key != "resultStatusCode" && key != "resultMessage" && key != "resultFlag"){
	        			var features = (new ol.format.GeoJSON()).readFeatures(val);
	        			if(key == "heatmap"){
	        				wavus.layer.makeHeatLayer('heatmap', wavus.map.getMap(), 11, 10, 50).then(function(layer){
	        					layer.getSource().addFeatures(features);
	        				});
	        			}else{
		    	        	wavus.layer.makeVectorLayer(key, _map, 11).then(function(layer){
		    	        		layer.getSource().addFeatures(features);
		    	        	});
	        			}
	        		}
	        	});
	        }  
		});
	}
	
	function _TOCModAdd(layerId){ // WMS Style 변경 기능 추가
		$('img[id=legend-'+layerId+']').after(
			'<select id="wms_style_' + layerId + '" name="wms_style_' + layerId + '" class="wms_style_select" size="6" style="display:none;">'+
			'<option value="">선택(Default)</option>'+
			'<option value="test_poly_blue">test_poly_blue</option>'+
			'<option value="test_poly_green">test_poly_green</option>'+
			'<option value="test_poly_yellow">test_poly_yellow</option>'+
			'<option value="test_poly_red">test_poly_red</option>'+
			'<option value="test_poly_black">test_poly_black</option>'+
			'<option value="polygon">polygon</option>'+
			'</select>'
		); // 스타일 목록 호출로 변경 ?
		$('#legend-' + layerId).click(function(){
			$('#wms_style_' + layerId).css("display", "");
			$('#wms_style_' + layerId).css("margin-top", (20-$("#mainTOC").scrollTop())+"px");
			$('#wms_style_' + layerId).focus();
		});
		$('#wms_style_' + layerId).blur(function(){
			$('#wms_style_' + layerId).css("display", "none");
		});
		wavus.layer.hideLayer(layerId);
	}
	
	function _styleLegend(layer){ // Vector Layer Legend 생성
		var layerName = layer.get('name');
		var parent_td = $('#legend-' + layerName).parents("td").eq(0);
		$('#legend-' + layerName).remove();
		parent_td.html("<div id='legend-" + layerName + "' class='v-legend_back'></div>");
		var legendElement = $("div[id=legend-"+layerName+"]");
		var textFeatureChk = false;
		var featureSvgObj;
		var layerStyle;
		var chkHeatmap = false;
		if(layer instanceof ol.layer.Heatmap){
			chkHeatmap = true;
			layerStyle = new ol.style.Style();
		}else{
			if(layer.getStyle() instanceof ol.style.Style){
				layerStyle = layer.getStyle();
			}else{
				if(layer.getStyle()().length != undefined){
					layerStyle = layer.getStyle()()[0];	
				}else{
					layerStyle = layer.getStyle()();
				}
			}
		}
		var geomType = "";
		var before_geomType = "";
		$.each(layer.getSource().getFeatures(), function(i, feature){
			if(feature.getGeometry() instanceof ol.geom.Point || feature.getGeometry() instanceof ol.geom.MultiPoint){
		    	geomType="point";
		    	if(feature.getStyle() != null){
			    	if(feature.getStyle().getImage() instanceof ol.style.Icon){
			    		textFeatureChk = true;
			    		featureSvgObj = $((new DOMParser()).parseFromString(feature.getStyle().getImage().getSrc().replace("data:image/svg+xml, ", ""), "image/svg+xml"));
			    	}
		    	}
		    }else if (feature.getGeometry() instanceof ol.geom.LineString || feature.getGeometry() instanceof ol.geom.MultiLineString){
		    	geomType="line";
		    }else if (feature.getGeometry() instanceof ol.geom.Polygon || feature.getGeometry() instanceof ol.geom.MultiPolygon){
		    	geomType="polygon";
		    }
			if(before_geomType != "" && before_geomType != geomType){
				geomType="multi";
				return;
			}
			before_geomType = geomType;
		});
		if(geomType == ""){
			parent_td.html("<span id='legend-" + layerName + "'></span>");
			return;
		}
		legendElement.data("geomtype", geomType);
		var layerOpacity = layer.getOpacity();
		var strokeColor = layerStyle.getStroke() ? layerStyle.getStroke().getColor() : "";
		var strokeDash = layerStyle.getStroke() ? layerStyle.getStroke().getLineDash() != null ? layerStyle.getStroke().getLineDash() : "0" : "0";
		var fillColor = layerStyle.getFill() ? layerStyle.getFill().getColor() : "";
		var textSize = "10";
		var textFontType = "Gulim";
		var textColor = "";
		var textAlign = "center";
		var textBaseline = "middle";
		
		parent_td.append("<input type='hidden' id='layer-" + layerName + "' name='layer-" + layerName + "' value='" + layerOpacity + "' data-color-type='single' />");
		if(geomType == "point"){
			if(layerStyle.getImage() instanceof ol.style.Icon){
				legendElement.html("<img id='icon-" + layerName + "' src='" + layerStyle.getImage().getSrc().replace(window.location.origin, "") + "' style='width:100%; height:100%;' />");
			}else{
				if(textFeatureChk == true){
					strokeColor = featureSvgObj.find("polygon").attr("stroke");
					fillColor = featureSvgObj.find("polygon").attr("fill");
					textColor = featureSvgObj.find("text").attr("fill");
					textSize = featureSvgObj.find("text").attr("font-size");
					textFontType = featureSvgObj.find("text").attr("font-family");
					legendElement.html("<div id='stroke-" + layerName + "' class='v-legend_ps'><div class='v-legend_pw'><div id='fill-" + layerName + "' class='v-legend_pf'><div id='text-" + layerName + "' class='v-legend_tf1'></div><div class='v-legend_tf2'></div></div></div></div>");
					$("#stroke-"+layerName).css("background-color", strokeColor);
					$("#fill-"+layerName).css("background-color", fillColor);
					$("#text-"+layerName).css("background-color", textColor);
					$("#text-"+layerName).next(".v-legend_tf2").eq(0).css("background-color", textColor);
					$("#text-"+layerName).data("size", textSize);
					$("#text-"+layerName).data("font-type", textFontType);
				}else{
					if(chkHeatmap == true){
						legendElement.html("<div id='heat-" + layerName + "' class='v-legend_circle_s'></div>");
						$("#heat-"+layerName).css("background", "radial-gradient(rgba(255,0,0,1) 0%, rgba(255,153,0,0.7) 30%, rgba(255,255,0,0.6) 50%, rgba(0,255,0,0.5) 70%, rgba(0,0,255,0.4) 90%, rgba(0,0,80,0.3) 95%, rgba(80,0,80,0.2) 100%);");
						$("#heat-"+layerName).data("radius", layer.getRadius());
						$("#heat-"+layerName).data("blur", layer.getBlur());
					}else{
						strokeColor = layerStyle.getImage().getStroke() ? layerStyle.getImage().getStroke().getColor() : "";
						fillColor = layerStyle.getImage().getFill() ? layerStyle.getImage().getFill().getColor() : "";
						legendElement.html("<div id='stroke-" + layerName + "' class='v-legend_circle_s'><div class='v-legend_circle_w'><div id='fill-" + layerName + "' class='v-legend_circle_f'></div></div></div>");
						$("#stroke-"+layerName).css("background-color", strokeColor);
						$("#fill-"+layerName).css("background-color", fillColor);
					}
				}
			}
		}else if(geomType == "line"){
			legendElement.html("<div id='stroke-" + layerName + "' class='v-legend_line'></div>");
			 $("#stroke-"+layerName).css("background-color", strokeColor);
			 $("#stroke-"+layerName).data("dash", strokeDash);
		}else if(geomType == "polygon"){
			legendElement.html("<div id='stroke-" + layerName + "' class='v-legend_ps'><div class='v-legend_pw'><div id='fill-" + layerName + "' class='v-legend_pf'></div></div></div>");
			$("#stroke-"+layerName).css("background-color", strokeColor);
			$("#stroke-"+layerName).data("dash", strokeDash);
			$("#fill-"+layerName).css("background-color", fillColor);
		}else if(geomType == "multi"){
			strokeColor = strokeColor == "" ? (layerStyle.getImage().getStroke() ? layerStyle.getImage().getStroke().getColor() : "") : strokeColor;
			fillColor = fillColor == "" ? (layerStyle.getImage().getFill() ? layerStyle.getImage().getFill().getColor() : "") : fillColor;
			legendElement.html("<div id='stroke-" + layerName + "' class='v-legend_ps'><div class='v-legend_pw'><div id='fill-" + layerName + "' class='v-legend_pf'></div></div></div>");
			$("#stroke-"+layerName).css("background-color", strokeColor);
			$("#fill-"+layerName).css("background-color", fillColor);
		}
		if(textFeatureChk != true){
			parent_td.append("<input type='hidden' id='text-" + layerName + "' name='text-" + layerName + "' />");
			$("#text-"+layerName).data("field", "");
			$("#text-"+layerName).data("size", textSize);
			$("#text-"+layerName).data("font-type", textFontType);
			$("#text-"+layerName).data("align", textAlign);
			$("#text-"+layerName).data("baseline", textBaseline);
			$("#text-"+layerName).css("color", textColor);
		}
	}
	
	function _layerConfigLoad(){
		$.ajax({
			url : '/file/getConfig.do',
			success :  function(data){
				if(data != "" && data != "{}"){
					var configJson = JSON.parse(data);
					$.each(configJson.textLayer, function(idx, textLayerObj){
						var tmpTextLayerCnt = textLayerObj.featureLayerId.split("_")[1]*1;
						if(wavus.draw.getTextLayerCnt() <= tmpTextLayerCnt + 1){
							wavus.draw.setTextLayerCnt(wavus.draw.getTextLayerCnt() <= tmpTextLayerCnt ? tmpTextLayerCnt + 1 : wavus.draw.getTextLayerCnt());
							var textVector;
							wavus.layer.findLayerById(textLayerObj.featureLayerId).then(function(layer){
								textVector =  layer;
							});
							if(!(textVector instanceof ol.layer.Vector)){
								wavus.layer.makeVectorLayer(textLayerObj.featureLayerId, _map, 7000).then(function(layer){
									textVector =  layer;
								});
							}
							var textFeature = new ol.Feature({ geometry : new ol.geom.Point(textLayerObj.featureCoordinate), feature_type : 'text'});
							textFeature.setStyle(wavus.draw.createTextImgSVG({textString : unescape(textLayerObj.featureText), type : textLayerObj.memoType}));
							textVector.getSource().addFeature(textFeature);
						}
					});
					
					$.each(configJson.layerConfig, function(idx, layerConfigObj){
						if($("input[id=add_"+layerConfigObj.layerId+"]").length > 0){
							if($("input[id=add_"+layerConfigObj.layerId+"]").is(":checked") == false){
								$("input[id=add_"+layerConfigObj.layerId+"]").click();
							}
							if(!$("#mainTOC").find("input[id="+layerConfigObj.layerId+"]").is(":checked") == layerConfigObj.onOff){
								$("#mainTOC").find("input[id="+layerConfigObj.layerId+"]").click();
							}
							$("#mainTOC").find("input[id="+layerConfigObj.layerId+"]").parents("li").eq(0).data("zindex", layerConfigObj.zIndex);
							wavus.layer.findLayerById(layerConfigObj.layerId).then(function(layer){
								layer.setZIndex(layerConfigObj.zIndex);
							});
							$("#mainTOC").find("input[id="+layerConfigObj.layerId+"]").parents("li").eq(0).data("aindex", layerConfigObj.aIndex);
							var layer_aindex;
							wavus.layer.findLayerById(layerConfigObj.layerId).then(function(layer){
								layer_aindex = _.findIndex(_map.getLayers().getArray(), layer);
								var tmp_layer = _map.getLayers().getArray()[layer_aindex];
								_map.getLayers().getArray()[layer_aindex] = _map.getLayers().getArray()[layerConfigObj.aIndex];
								_map.getLayers().getArray()[layerConfigObj.aIndex] = tmp_layer;
							});
							wavus.controller.vectorNameChange(layerConfigObj.layerId, unescape(layerConfigObj.layerNm));
						}
						if($("#wms_style_"+layerConfigObj.layerId).length > 0){
							$("#wms_style_"+layerConfigObj.layerId).val(layerConfigObj.wmsStyle);
							$("#wms_style_"+layerConfigObj.layerId).change();
						}
						if($("div[id=legend-"+layerConfigObj.layerId+"]").length > 0){
							$("#color_popup").remove();
							if(layerConfigObj.vectorStyle.color_ramp != undefined){
								$("#stroke-"+layerConfigObj.layerId).css("display", "none");
								$("#ramp-"+layerConfigObj.layerId).remove();
								$("#expand-"+layerConfigObj.layerId).remove();
								$("#collapse-"+layerConfigObj.layerId).remove();
								$("#color-ramp-legend-"+layerConfigObj.layerId).remove();
								var tmpClassName = $("div[id=legend-"+layerConfigObj.layerId+"]").data("geomtype") == "point" ? "v-legend_circle_s" : "v-legend_ps";
								$("div[id=legend-"+layerConfigObj.layerId+"]").append("<div id='ramp-"+layerConfigObj.layerId+"' class='"+tmpClassName+" "+layerConfigObj.vectorStyle.color_ramp.colorType+"'></div>");
								$("#layer-"+layerConfigObj.layerId).data("color-type", "multi");
								$.each(layerConfigObj.vectorStyle.color_ramp, function(optName, optValue){
									if(optName == "numField"){
										$("#ramp-"+layerConfigObj.layerId).data(optName, unescape(optValue));
									}else{
										$("#ramp-"+layerConfigObj.layerId).data(optName, optValue);
									}
								});
								$("div[id=legend-"+layerConfigObj.layerId+"]").before("<div id='expand-"+layerConfigObj.layerId+"' class='toggle-legend-plus'></div>" +
																										"<div id='collapse-"+layerConfigObj.layerId+"' class='toggle-legend-minus' style='display:none;'></div>");
								$("div[id=legend-"+layerConfigObj.layerId+"]").parents("li").append("<div id='color-ramp-legend-"+layerConfigObj.layerId+"' class='color-ramp-legend' style='display:none;'>" +
																																													"Field Name : "+$("#color-ramp").data("num-field")+"</div>");
								$.each($("#ramp-"+layerConfigObj.layerId).data("color-arr"), function(idx, rgbColor){
									$("#color-ramp-legend-"+layerConfigObj.layerId).append("<div><div class='multi-legend "+tmpClassName+"' style='background-color:"+rgbColor+";display:inline-block;'></div>" +
											"<div class='multi-legend-val' style='display:inline-block;'>&nbsp; "+($("#ramp-"+layerConfigObj.layerId).data("range-arr")[idx]*1+1)+" - "+$("#ramp-"+layerConfigObj.layerId).data("range-arr")[idx+1]+"</div></div>");
								});
								$("#mainTOC div[class*=toggle-legend-]").click(function(){
									if($(this).prop("id") == "expand-"+layerConfigObj.layerId){
										$(this).css("display", "none");
										$("#collapse-"+layerConfigObj.layerId).css("display", "");
										$("#color-ramp-legend-"+layerConfigObj.layerId).css("display", "");
									}
									if($(this).prop("id") == "collapse-"+layerConfigObj.layerId){
										$(this).css("display", "none");
										$("#expand-"+layerConfigObj.layerId).css("display", "");
										$("#color-ramp-legend-"+layerConfigObj.layerId).css("display", "none");
									}
								});
							}
							$("div[id='legend-"+layerConfigObj.layerId+"']").click();
							$.each(layerConfigObj.vectorStyle, function(styleName, styleValue){
								if(styleName == "color_ramp"){
									return true;
								}
								var realStyleName = styleName.replace(/_/g, "-");
								var realStyleValue = styleValue;
								if(realStyleName == "icon-img-src"){
									realStyleValue = unescape(realStyleValue);
								}
								if(realStyleName.split("-")[1].toLowerCase() != "color"){
									$("#"+realStyleName).val(realStyleValue);
								}else{
									$("#"+realStyleName).css("background-color", realStyleValue);
								}
							});
							$("#change_color").click();
						}
					});
					wavus.controller.tocLayerSort();
				}
			}
		});
	}
	
	function layerStyleCustom(layerId, params){
		/*
		params.layer_opacity : 0 ~ 1
		params.stroke_color : ex) rgba(255,255,255,0.5)
		params.stroke_dash : ex) 0(실선), 4(점선), 10(파선)
		params.fill_color : ex) rgba(255,255,255,0.5)
		params.color_ramp .colorArr : ["rgb(126,0,129)", "rgb(14,0,240)", "rgb(0,188,68)", "rgb(147,255,0)", "rgb(255,211,0)", "rgb(255,109,0)", "rgb(243,0,12)", "rgb(129,0,127)"]
								  .colorCnt : 8
								  .colorType : "PBGYORP_ramp"
								  .numField : "pilji_gun"
								  .rangeArr : [0, "6", "11", "16", "21", "26", "31", "36", "41"]
		params.text_field : ex) "pilji_gun"
		params.text_size : ex) "11"
		params.font_type : ex) "Gulim"
		params.text_align : ex) "center"
		params.text_baseline : ex) "middle"
		params.text_color : ex) "rgb(0,0,0)"
		params.icon_img_src : ex) "/sample/dasodksa.jpg"  
		*/
		if($("div[id=legend-"+layerId+"]").length > 0){
			$("#color_popup").remove();
			if(params.color_ramp != undefined){
				$("#stroke-"+layerId).css("display", "none");
				$("#ramp-"+layerId).remove();
				$("#expand-"+layerId).remove();
				$("#collapse-"+layerId).remove();
				$("#color-ramp-legend-"+layerId).remove();
				var tmpClassName = $("div[id=legend-"+layerId+"]").data("geomtype") == "point" ? "v-legend_circle_s" : "v-legend_ps";
				$("div[id=legend-"+layerId+"]").append("<div id='ramp-"+layerId+"' class='"+tmpClassName+" "+ params.color_ramp.colorType +"'></div>");
				$("#layer-"+layerId).data("color-type", "multi");
				$.each(params.color_ramp, function(optName, optValue){
					$("#ramp-"+layerId).data(optName, optValue);
				});
				$("div[id=legend-"+layerId+"]").before("<div id='expand-"+layerId+"' class='toggle-legend-plus'></div>" +
																						"<div id='collapse-"+layerId+"' class='toggle-legend-minus' style='display:none;'></div>");
				$("div[id=legend-"+layerId+"]").parents("li").append("<div id='color-ramp-legend-"+layerId+"' class='color-ramp-legend' style='display:none;'>" +
																																									"Field Name : "+$("#color-ramp").data("num-field")+"</div>");
				$.each($("#ramp-"+layerId).data("color-arr"), function(idx, rgbColor){
					$("#color-ramp-legend-"+layerId).append("<div><div class='multi-legend "+tmpClassName+"' style='background-color:"+rgbColor+";display:inline-block;'></div>" +
							"<div class='multi-legend-val' style='display:inline-block;'>&nbsp; "+($("#ramp-"+layerId).data("range-arr")[idx]*1+1)+" - "+$("#ramp-"+layerId).data("range-arr")[idx+1]+"</div></div>");
				});
				$("#mainTOC div[class*=toggle-legend-]").click(function(){
					if($(this).prop("id") == "expand-"+layerId){
						$(this).css("display", "none");
						$("#collapse-"+layerId).css("display", "");
						$("#color-ramp-legend-"+layerId).css("display", "");
					}
					if($(this).prop("id") == "collapse-"+layerId){
						$(this).css("display", "none");
						$("#expand-"+layerId).css("display", "");
						$("#color-ramp-legend-"+layerId).css("display", "none");
					}
				});
			}
			$("div[id='legend-"+layerId+"']").click();
			
			$.each(params, function(styleName, styleValue){
				if(styleName == "color_ramp"){
					return true;
				}
				var realStyleName = styleName.replace(/_/g, "-");
				var realStyleValue = styleValue;
				if(realStyleName == "icon-img-src"){
					realStyleValue = unescape(realStyleValue);
				}
				if(realStyleName.split("-")[1].toLowerCase() != "color"){
					$("#"+realStyleName).val(realStyleValue);
				}else{
					$("#"+realStyleName).css("background-color", realStyleValue);
				}
			});
			$("#change_color").click();
		}
	}
	
	function _wmsStyleChange(){
		$("select[id*='wms_style_']").change(function(){
			var layerName = $(this).prop("id").replace("wms_style_", "");
			var selStyle = $(this).val();
			var tmpWorkSpace = "";
			var tmpStr = "";
			wavus.layer.findLayerById(layerName).then(function(layer){
				layer.getSource().updateParams({'STYLES':selStyle});
				tmpStr = layer.getSource().getUrl();
				tmpWorkSpace = tmpStr.substring(tmpStr.indexOf("geoserver"), tmpStr.length).split("/")[1];
			});
			setLayerLegend({
				layerId : layerName,
				style : selStyle,
				viewRule : 'draw',
				backgroundColor : 'eaeaea',
				imgDom : $('#legend-' + layerName),
				workSpace : tmpWorkSpace
			});
			$(this).css("display", "none");
		});
	}
	
	function _vectorStyleChange(){
		$("div[id*='legend-']").click(function(e){
			$("#addListClose_b").click();
			if($("#color_popup").length > 0){
				$("#color_popup").remove();
			}else{
				var layerName = $(this).prop("id").replace("legend-", "");
				var layerOpacity = $("#layer-"+layerName).val();
				var colorType = $("#layer-"+layerName).data("color-type");
				var strokeColor = $("#stroke-"+layerName).css("background-color");
				var strokeDash = $("#stroke-"+layerName).data("dash");
				var fillColor = "";
				if($("#fill-"+layerName).length > 0){
					fillColor = $("#fill-"+layerName).css("background-color");
				}
				var rampBackground = "";
				var rampNumField = "";
				var rampColorCnt = "";
				var rampColorType = "";
				var rampRangeArr = "";
				var rampColorArr = "";
				if($("#ramp-"+layerName).length > 0){
					rampBackground = $("#ramp-"+layerName).css("background");
					rampNumField = $("#ramp-"+layerName).data("num-field");
					rampColorCnt = $("#ramp-"+layerName).data("color-cnt");
					rampColorType = $("#ramp-"+layerName).data("color-type");
					rampRangeArr = $("#ramp-"+layerName).data("range-arr");
					rampColorArr = $("#ramp-"+layerName).data("color-arr");
				}
				var textLabelField = $("#text-"+layerName).data("field");
				var textSize = $("#text-"+layerName).data("size");
				var textFontType = $("#text-"+layerName).data("font-type");
				var textAlign = $("#text-"+layerName).data("align");
				var textBaseline = $("#text-"+layerName).data("baseline");
				var textColor = $("#text-"+layerName).css("color");
				
				var heatRadius = $("#heat-"+layerName).data("radius");
				var heatBlur = $("#heat-"+layerName).data("blur");
							
				$("#mainTOC").next().after("<div id='color_popup'></div>");
				var tmp_str = "";
				tmp_str = tmp_str + "<div class='buttonsDiv'>";
				tmp_str = tmp_str + "<input type='text' id='changeLayerNm' name='changeLayerNm' value='" + $("input[id="+layerName + "]").val() + "' />&nbsp;";
				tmp_str = tmp_str + "<input type='button' value='수정' id='change_color' name='change_color' />&nbsp;<input type='button' value='취소' id='close_color' name='close_color' />";
				tmp_str = tmp_str + "</div>";
				tmp_str = tmp_str + "<div class='styleTbDiv'>";
				tmp_str = tmp_str + "<table class='color_popup_tb'>";
				tmp_str = tmp_str + "<tr><th colspan='4'>스타일</th></tr>";
				tmp_str = tmp_str + "<tr><td>투명도</td><td colspan='3'>" +
																"<input type='range' step='0.1' min='0' max='1' value='" + layerOpacity + "' class='range_a' id='layer-opacity' name='layer-opacity' />" +
																"&nbsp;<span id='layer-opacity_str'>" + layerOpacity + "</span></td></tr>";
				if($("div[id=text-"+layerName+"]").length > 0){
					textColor = $("#text-"+layerName).css("background-color");
					tmp_str = tmp_str + "<tr><td>채움색상</td><td><div id='fill-color' name='fill-color' class='rgba_change' style='background-color:" + fillColor + ";'></div></td>";
					tmp_str = tmp_str + 	  "<td>선색상</td><td><div id='stroke-color' name='stroke-color' class='rgba_change' style='background-color:" + strokeColor + ";'></div></td></tr>";
					tmp_str = tmp_str + "<tr><th colspan='4'>텍스트</th></tr>";
					tmp_str = tmp_str + "<tr><td>폰트</td><td colspan='3'><div id='text-color' name='text-color' class='rgba_change' style='background-color:" + textColor + ";'></div> " +
																							"<input type='number' step='1' value='" + textSize + "' id='text-size' name='text-size' />px " +
																							"<select id='font-type-sel' name='font-type-sel'></select>" +
																							"<input type='hidden' value='" + textFontType + "' id='font-type' name='font-type' /></td></tr>";
				}else{
					if($(this).children("img").length > 0){
						tmp_str = tmp_str + "<tr><td>이미지경로</td><td colspan='3'><input type='text' value='" + $(this).children("img").prop("src") + "' id='icon-img-src' name='icon-img-src' /></td></tr>";
					}else{
						if($("#fill-"+layerName).length > 0){
							tmp_str = tmp_str + "<tr><td>색상타입</td><td><select id='color-type-sel' name='color-type-sel'></select>" +
																																"<input type='hidden' value='" + colorType + "' id='color-type' name='color-type' /></td>";
							tmp_str = tmp_str + 	 "<td>채움색상</td><td><div id='fill-color' name='fill-color' class='rgba_change' style='background-color:" + fillColor + ";'></div></td>";
							tmp_str = tmp_str + 	 "<td>다중색상</td><td><div id='color-ramp' class='color_ramp' style='background:" + rampBackground + ";' data-num-field='" + rampNumField + "' ";
							tmp_str = tmp_str +																			"data-color-cnt='" + rampColorCnt + "' data-color-type='" + rampColorType + "'></div></td></tr>";
						}
						if($("#stroke-"+layerName).length > 0){
							tmp_str = tmp_str + "<tr><td>선색상</td><td><div id='stroke-color' name='stroke-color' class='rgba_change' style='background-color:" + strokeColor + ";'></div></td>";
							if(strokeDash){
								tmp_str = tmp_str +   "<td>선종류</td><td><select id='stroke-dash-sel' name='stroke-dash-sel'></select>" +
																																"<input type='hidden' value='" + strokeDash + "' id='stroke-dash' name='stroke-dash' /></td></tr>";
							}else{
								tmp_str = tmp_str +   "<td colspan='2'></td></tr>";
							}
						}
						if($("#heat-"+layerName).length > 0){
							tmp_str = tmp_str + "<tr><td>크기</td><td><input type='number' step='1' value='" + heatRadius + "' id='heat-radius' name='heat-radius' /></td>" +
															  "<td>흐리기</td><td><input type='number' step='1' value='" + heatBlur + "' id='heat-blur' name='heat-blur' /></td></tr>";
						}
					}
					tmp_str = tmp_str + "</table></div>";
					tmp_str = tmp_str + "<div class='textTbDiv'>";
					tmp_str = tmp_str + "<table class='color_popup_tb'>";
					tmp_str = tmp_str + "<tr><th colspan='4'>텍스트</th></tr>";
					tmp_str = tmp_str + "<tr><td>필드</td><td colspan='3'><select id='text-field-sel' name='text-field-sel'></select>"+
																						"<input type='hidden' value='" + textLabelField + "' id='text-field' name='text-field' /></td></tr>";
					tmp_str = tmp_str + "<tr><td>폰트</td><td colspan='3'><div id='text-color' name='text-color' class='rgba_change' style='background-color:" + textColor + ";'></div> " +
																							"<input type='number' step='1' value='" + textSize + "' id='text-size' name='text-size' />px " +
																							"<select id='font-type-sel' name='font-type-sel'></select>" +
																							"<input type='hidden' value='" + textFontType + "' id='font-type' name='font-type' /></td></tr>";
					tmp_str = tmp_str + "<tr><td rowspan='2'>정렬</td>" +
													  "<td>수평</td><td colspan='2'><select id='text-align-sel' name='text-align-sel'></select>"+
					   																   									"<input type='hidden' value='" + textAlign + "' id='text-align' name='text-align'/></td></tr>";
					tmp_str = tmp_str + "<tr><td>수직</td><td colspan='2'><select id='text-baseline-sel' name='text-baseline-sel'></select>"+
					   																   									"<input type='hidden' value='" + textBaseline + "' id='text-baseline' name='text-baseline'/></td></tr>";
				}
				tmp_str = tmp_str + "</table></div>";
				$("#color_popup").html(tmp_str);
				if($("#fill-"+layerName).length > 0){
					_colorTypeSelect();
					$("#color-ramp").data("color-arr", rampColorArr);
					$("#color-ramp").data("range-arr", rampRangeArr);
				}
				
				textFieldSelect(layerName, "text-field-sel");
				_textOptionSetting();
				_afterSetting();
				_opacityChange();
				_colorRamp(layerName);
				_rgabChange();
				_changeColor(layerName);
			}
		});
	}
	
	function _colorTypeSelect(){
		$("#color-type-sel").append("<option value='single'>단일</option>");
		$("#color-type-sel").append("<option value='multi'>다중</option>");
		$("#color-type-sel").val($("#color-type").val());
		$("#color-type-sel").change(function(){
			$("#color-type").val($(this).val());
			if($(this).val() == "single"){
				$("#fill-color").parent().css("display", "");
				$("#fill-color").parent().prev("td").css("display", "");
				$("#color-ramp").parent().css("display", "none");
				$("#color-ramp").parent().prev("td").css("display", "none");
			}else if($(this).val() == "multi"){
				$("#fill-color").parent().css("display", "none");
				$("#fill-color").parent().prev("td").css("display", "none");
				$("#color-ramp").parent().css("display", "");
				$("#color-ramp").parent().prev("td").css("display", "");
			}
		});
		$("#color-type-sel").change();
	}
	
	function textFieldSelect(layerName, target, optNumber){
		var optNum = optNumber ? true : false;
		$("#"+target).append("<option value=''>선택</option>");
		
		if($("div[id=legend-"+layerName+"]").data("geomtype") != "multi"){
			wavus.layer.findLayerById(layerName).then(function(layer){
				var features = layer.getSource().getFeatures();
				var firstFeature = layer.getSource().getFeatures()[0];
				$.each($.grep(firstFeature.getKeys(), function(val, idx){ return !(firstFeature.get(val) instanceof Object); }), function(idx, val){
					if(optNum == true){
						if($.grep(features, function(feature, idx){ return wavus.util.isNumber(wavus.util.removeCommas(feature.get(val)+"")); }).length > 0){
							$("#"+target).append("<option value='"+ val +"'>" + val + "</option>");
						}
					}else{
						$("#"+target).append("<option value='"+ val +"'>" + val + "</option>");
					}
				});
			});
		}
	}
	
	function _textOptionSetting(){
		$("#text-align-sel").append("<option value='center'>중앙</option>");
		$("#text-align-sel").append("<option value='left'>우측</option>");
		$("#text-align-sel").append("<option value='right'>좌측</option>");
		
		$("#text-baseline-sel").append("<option value='middle'>중앙</option>");
		$("#text-baseline-sel").append("<option value='bottom'>상단</option>");
		$("#text-baseline-sel").append("<option value='top'>하단</option>");
		
		$("#font-type-sel").append("<option value=''>선택</option>");
		$("#font-type-sel").append("<option value='malgun gothic' style='font-family:\"Malgun Gothic\";'>맑은 고딕</option>");
		$("#font-type-sel").append("<option value='dotum' style='font-family:\"Dotum\";'>돋움</option>");
		$("#font-type-sel").append("<option value='dotumche' style='font-family:\"DotumChe\";'>돋움체</option>");
		$("#font-type-sel").append("<option value='gulim' style='font-family:\"Gulim\";'>굴림</option>");
		$("#font-type-sel").append("<option value='gulimche' style='font-family:\"GulimChe\";'>굴림체</option>");
		$("#font-type-sel").append("<option value='batang' style='font-family:\"Batang\";'>바탕</option>");
		$("#font-type-sel").append("<option value='batangche' style='font-family:\"BatangChe\";'>바탕체</option>");
		$("#font-type-sel").append("<option value='gungsuh' style='font-family:\"Gungsuh\";'>궁서</option>");
		$("#font-type-sel").append("<option value='gungsuhche' style='font-family:\"GungsuhChe\";'>궁서체</option>");
		$("#font-type-sel").append("<option value='new gulim' style='font-family:\"New Gulim\";'>새굴림</option>");
		$("#font-type-sel").append("<option value='arial' style='font-family:\"Arial\";'>Arial</option>");
		$("#font-type-sel").append("<option value='arial black' style='font-family:\"Arial Black\";'>Arial Black</option>");
		$("#font-type-sel").append("<option value='arial narrow' style='font-family:\"Arial Narrow\";'>Arial Narrow</option>");
		$("#font-type-sel").append("<option value='comic sans ms' style='font-family:\"Comic Sans MS\";'>Comic Sans MS</option>");
		
		$("#stroke-dash-sel").append("<option value='0'>실선</option>");
		$("#stroke-dash-sel").append("<option value='4'>점선</option>");
		$("#stroke-dash-sel").append("<option value='10'>파선</option>");
	}
	
	function _afterSetting(){
		$("#stroke-dash-sel").val($("#stroke-dash").val());
		$("#stroke-dash-sel").change(function(){
			$("#stroke-dash").val($(this).val());
		});
		
		$("#text-field-sel").val($("#text-field").val());
		$("#text-field-sel").change(function(){
			$("#text-field").val($(this).val());
		});
		
		$("#font-type-sel").val($("#font-type").val().toLowerCase());
		$("#font-type-sel").change(function(){
			$("#font-type").val($(this).val());
		});
		
		$("#text-align-sel").val($("#text-align").val());
		$("#text-align-sel").change(function(){
			$("#text-align").val($(this).val());
		});
		
		$("#text-baseline-sel").val($("#text-baseline").val());
		$("#text-baseline-sel").change(function(){
			$("#text-baseline").val($(this).val());
		});
	}
	
	function _opacityChange(){
		$("#layer-opacity").on('input', function(){
			$("#" + $(this).prop("id") + "_str").html($(this).val());
		});
	}
	
	function _rgabChange(opt){
		var optType = opt == "noOpacity" ? "rgb" : "rgba";
		$("."+optType+"_change").click(function(e){
			if($("#"+optType+"_popup").length > 0){
				$("#"+optType+"_popup").remove();
			}else{
				var Colorobj = $(this);
				Colorobj.after("<div id='"+optType+"_popup'></div>");
				var opacityYN = false;
				if(optType == "rgba"){
					opacityYN = true;
				}else{
					$("#"+optType+"_popup").css("margin-left", -1*$(this).css("width").replace("px", ""));
				}
				$("#"+optType+"_popup").html("<input type='text' id='rgba_palette' data-opacity='" + _getAlpha(Colorobj.css("background-color")) + "' />");
				$("#rgba_palette").minicolors({
					defaultValue : _rgbString2hex(Colorobj.css("background-color")),
					opacity : opacityYN,
					control : "wheel",
					format : "rgb",
					inline : true,
					change : function(rgba, opacity){
						Colorobj.css("background-color", rgba);
					}
				});
			}
		});
	}
	
	function _getAlpha(rgba) {
		rgba = rgba.match(/^rgba?[\s+]?\([\s+]?(\d+)[\s+]?,[\s+]?(\d+)[\s+]?,[\s+]?(\d+)[\s+]?,[\s+]?(\d+(\.\d{1,2})?|\.\d{1,2})[\s+]?/i);
		return (rgba && rgba.length === 6) ? rgba[4] : '1';
	}
	
	function _rgbString2hex(rgb){
		rgb = rgb.match(/^rgba?[\s+]?\([\s+]?(\d+)[\s+]?,[\s+]?(\d+)[\s+]?,[\s+]?(\d+)[\s+]?/i);
		return (rgb && rgb.length === 4) ? '#' +
		('0' + parseInt(rgb[1],10).toString(16)).slice(-2) +
		('0' + parseInt(rgb[2],10).toString(16)).slice(-2) +
		('0' + parseInt(rgb[3],10).toString(16)).slice(-2) : '';
	}
	
	function _colorRamp(layerName){
		$("#color-ramp").click(function(){
			if($("#colorRamp_popup").length > 0){
				$("#colorRamp_popup").remove();
			}else{
				$(this).after("<div id='colorRamp_popup'></div>");
				var featureNumField = $("#color-ramp").data("num-field");
				var colorRampType = $("#color-ramp").data("color-type");
				var colorDivCnt = $("#color-ramp").data("color-cnt");
				var colorRangeArr = $("#color-ramp").data("range-arr");
				var tdColorArr = $("#color-ramp").data("color-arr");
				
				var tmp_str = "";
				tmp_str = tmp_str + "<table>";
				tmp_str = tmp_str + "<tr><td>필드</td><td><select id='feature-num-field-sel' name='feature-num-field-sel'></select>";
				tmp_str = tmp_str + 								"<input type='hidden' value='"+featureNumField+"' id='feature-num-field' name='feature-num-field' /></td>";
				tmp_str = tmp_str + "<td rowspan='3'><input type='button' id='createColorRamp_b' name='createColorRamp_b' value='생성' style='height:40px;' /><br />";
				tmp_str = tmp_str + 						"<input type='button' id='saveColorRamp_b' name='saveColorRamp_b' value='적용' style='height:40px;display:none;' /></td>"
				tmp_str = tmp_str + "</tr>";
				tmp_str = tmp_str + "<tr><td>색상</td><td>";
				tmp_str = tmp_str + 								"<canvas id='color-ramp-type-canv'></canvas>";
				tmp_str = tmp_str + 								"<div id='color-ramp-type' name='color-ramp-type' style='display:none;'>"+colorRampType+"</div></td></tr>";
				tmp_str = tmp_str + "<tr><td>개수</td><td><select id='color-div-cnt-sel' name='color-div-cnt-sel'>";
				tmp_str = tmp_str + 									"<option value=''>선택</option>";
				for(var i=2; i<=10; i++){
					tmp_str = tmp_str + 									"<option value='" + i + "'>" + i + "</option>";
				}
				tmp_str = tmp_str + 								"</select>";
				tmp_str = tmp_str + 								"<input type='hidden' value='"+colorDivCnt+"' id='color-div-cnt' name='color-div-cnt' /></td></tr>";
				tmp_str = tmp_str + "<tr><td colspan='3'>";
				tmp_str = tmp_str + 	"<div id='color-ramp-result' style='background-color:white;'></div>";
				tmp_str = tmp_str + "</td></tr></table>";
				$("#colorRamp_popup").html(tmp_str);
				
				_colorRampEvent(layerName);
				textFieldSelect(layerName, "feature-num-field-sel", true);
				if(featureNumField != "" && colorRampType != "" && colorDivCnt != "" && colorRangeArr != "" && tdColorArr != ""){
					_colorRampSetting(colorRangeArr, tdColorArr);
				}
			}
		});
	}
	
	function _colorRampEvent(layerName){
		$("#feature-num-field-sel").change(function(){
			$("#feature-num-field").val($(this).val());
			$("#color-ramp-result").html("");
			$("#saveColorRamp_b").css("display", "none");
		});
		
		$("#color-div-cnt-sel").change(function(){
			$("#color-div-cnt").val($(this).val());
			$("#color-ramp-result").html("");
			$("#saveColorRamp_b").css("display", "none");
		});
		
		$("#color-ramp-type-canv").click(function(){
			if($("#colorRampType_popup").length > 0){
				$("#colorRampType_popup").remove();
			}else{
				$(this).after("<div id='colorRampType_popup'></div>");
				$("#colorRampType_popup").append("<div id='GYOR_ramp' class='colorRamps'></div>");
				$("#colorRampType_popup").append("<div id='rainbow_ramp' class='colorRamps'></div>");
				$("#colorRampType_popup").append("<div id='PBGYORP_ramp' class='colorRamps'></div>");
				$("#colorRampType_popup").append("<div id='YOR_ramp' class='colorRamps'></div>");
				$("#colorRampType_popup").append("<div id='grayBlack_ramp' class='colorRamps'></div>");
				$("#colorRampType_popup").append("<div id='convex_ramp' class='colorRamps'></div>");
				
				$(".colorRamps").click(function(){
					var canv = document.getElementById("color-ramp-type-canv");
					var ctx = canv.getContext("2d");
					var selData = '<svg xmlns="http://www.w3.org/2000/svg" width="'+canv.width+'" height="'+canv.height+'">'+
											'<foreignObject width="100%" height="100%">'+
											'<div xmlns="http://www.w3.org/1999/xhtml" style="width:100%;height:100%;background:'+$(this).css("background")+';"></div></foreignObject></svg>';
					var DOMURL = window.URL || window.webkitURL || window;
					var tmpImage = new Image();
					var url = 'data:image/svg+xml;base64,' + btoa(selData);
					
					tmpImage.onload = function(){
						ctx.drawImage(tmpImage, 0, 0);
						DOMURL.revokeObjectURL(url);
					}
					tmpImage.src = url;
					$("#color-ramp-type").css("background", $(this).css("background"));
					$("#color-ramp-type").html($(this).prop("id"));
					$("#color-ramp-result").html("");
					$("#saveColorRamp_b").css("display", "none");
					$("#colorRampType_popup").remove();
				});
			}
		});
		
		$("#createColorRamp_b").click(function(){
			if($("#feature-num-field").val() == ""){
				alert("필드를 선택하십시오");
				return;
			}
			if($("#color-ramp-type").html() == ""){
				alert("색상을 선택하십시오");
				return;
			}
			if($("#color-div-cnt").val() == ""){
				alert("개수를 선택하십시오");
				return;
			}
			
			var fieldVal_arr = new Array();
			wavus.layer.findLayerById(layerName).then(function(layer){
				var features = layer.getSource().getFeatures();
				$.each(features, function(idx, feature){
					fieldVal_arr.push(wavus.util.removeCommas(feature.get($("#feature-num-field").val())+"") * 1);
				});
			});
			var maxFieldVal = Math.max.apply(Math, fieldVal_arr);
			var minFieldVal = Math.min.apply(Math, fieldVal_arr);
			var gapVal = (maxFieldVal - minFieldVal) / ($("#color-div-cnt").val()*1).toFixed(2);
			var tmpVal = minFieldVal;
			var newMaxVal = tmpVal + (gapVal * $("#color-div-cnt").val());
			newMaxVal = newMaxVal < maxFieldVal ? maxFieldVal : newMaxVal;
			if((maxFieldVal - minFieldVal) < ($("#color-div-cnt").val()*1)){
				alert("필드 값의 범위가 선택하신 개수보다 작습니다. 확인 하십시오");
				return;
			}
			$("#saveColorRamp_b").css("display", "");
			var colorRampCanvas = document.getElementById("color-ramp-type-canv");
			var colorRampContext = colorRampCanvas.getContext('2d');
			$("#color-ramp-result").html("");
			$("#color-ramp-result").append("<table id='tb-color-ramp-sel' style='height:70%;width:100%;' border='0' cellpadding='0' cellspacing='0'>" +
					"<tr></tr>" +
					"<tr style='height:9px;'><td colspan='"+($("#color-div-cnt").val()*1+1)+"' style='padding-bottom:11px;'></td></tr>" +
					"</table><table id='tb-color-ramp' style='height:31%;'  border='0' cellpadding='0' cellspacing='0'><tr></tr></table>");
			var gapColor = (colorRampCanvas.width-1) / ($("#color-div-cnt").val()*1-1);
			var stColor = 0;
			var tmpGapVal = minFieldVal;
			for(var i=1; i<=$("#color-div-cnt").val()*1; i++){
				$("#tb-color-ramp-sel tr").eq(0).append("<td></td>");
				var colorData = colorRampContext.getImageData(stColor, 0, colorRampCanvas.width, 1).data;
				tmpVal = tmpVal + gapVal;
				var rndTmpVal = Math.round(tmpVal);
				if(i != $("#color-div-cnt").val()*1){
					$("#tb-color-ramp-sel tr").eq(1).find("td").append(	"<input type='range' id='range-color-ramp"+i+"' name='range-color-ramp"+i+"' class='range-ramp' min='"+minFieldVal+"' max='"+newMaxVal+"' step='1' />"+
																						"<output for='range-color-ramp"+i+"' id='range-color-ramp"+i+"-val' class='range-ramp-val'></output>");
					$("#range-color-ramp"+i).val(rndTmpVal);
					$("#range-color-ramp"+i+"-val").val(rndTmpVal);
					$("#range-color-ramp"+i+"-val").css("left", (rndTmpVal - minFieldVal)/(newMaxVal - minFieldVal)*($("#range-color-ramp"+i).css("width").replace("px", "") - 14) + 1);
				}
				$("#tb-color-ramp tr").eq(0).append("<td id='td-color-ramp' class='rgb_change' " +
						"style='width:"+ ($("#tb-color-ramp").css("width").replace("px", "")/(newMaxVal - minFieldVal)) * (rndTmpVal - tmpGapVal) +"px;background-color:rgb("+colorData[0]+","+colorData[1]+","+colorData[2]+");'></td>");
				tmpGapVal = rndTmpVal;
				stColor = stColor + gapColor;
			}	
			$("#tb-color-ramp-sel tr").eq(0).append("<td></td>");
			$("#tb-color-ramp-sel tr").eq(0).find("td").eq(0).append("최소:"+minFieldVal);
			$("#tb-color-ramp-sel tr").eq(0).find("td").eq($("#tb-color-ramp-sel tr").eq(0).find("td").length-1).append("최대:"+newMaxVal);
			$("#tb-color-ramp-sel tr").eq(0).find("td").eq($("#tb-color-ramp-sel tr").eq(0).find("td").length-1).css("text-align", "right");
			
			$(".range-ramp").on("input change", function(){
				var rampIdx = $(".range-ramp").index($(this));
				if($(".range-ramp").length > 1){
					if(rampIdx == $(".range-ramp").length-1){
						if($(this).val() >= newMaxVal){
							$(this).val(newMaxVal-1);
							$("#"+$(this).prop("id")+"-val").val(newMaxVal-1);
							$("#"+$(this).prop("id")+"-val").css("left", (newMaxVal-1 - minFieldVal)/(newMaxVal - minFieldVal)*($(this).css("width").replace("px", "") - 14) + 1);
							return false;
						}
					}
					if(rampIdx < $(".range-ramp").length-1){
						if($(this).val() >= $("#range-color-ramp" + (rampIdx+1+1)).val()*1){
							$(this).val($("#range-color-ramp" + (rampIdx+1+1)).val()*1-1);
							$("#"+$(this).prop("id")+"-val").val($("#range-color-ramp" + (rampIdx+1+1)).val()*1-1);
							$("#"+$(this).prop("id")+"-val").css("left", ($("#range-color-ramp" + (rampIdx+1+1)).val()*1-1 - minFieldVal)/(newMaxVal - minFieldVal)*($(this).css("width").replace("px", "") - 14) + 1);
							return false;
						}
					}
					if(rampIdx > 0){
						if($(this).val() <= $("#range-color-ramp" + (rampIdx+1-1)).val()*1){
							$(this).val($("#range-color-ramp" + (rampIdx+1-1)).val()*1+1);
							$("#"+$(this).prop("id")+"-val").val($("#range-color-ramp" + (rampIdx+1-1)).val()*1+1);
							$("#"+$(this).prop("id")+"-val").css("left", ($("#range-color-ramp" + (rampIdx+1-1)).val()*1+1 - minFieldVal)/(newMaxVal - minFieldVal)*($(this).css("width").replace("px", "") - 14) + 1);
							return false;
						}
					}
				}
				$("#"+$(this).prop("id")+"-val").val($(this).val());
				$("#"+$(this).prop("id")+"-val").css("left", ($(this).val() - minFieldVal)/(newMaxVal - minFieldVal)*($(this).css("width").replace("px", "") - 14) + 1);
				var tdSumWidth = 0;
				tdSumWidth = $("td[id=td-color-ramp]").eq(rampIdx).css("width").replace("px", "")*1 + $("td[id=td-color-ramp]").eq(rampIdx+1).css("width").replace("px", "")*1;
				var tmpWidth = 0;
				for(var i=rampIdx-1; i>=0; i--){
					tmpWidth = tmpWidth + $("td[id=td-color-ramp]").eq(i).css("width").replace("px", "")*1;
				}
				$("td[id=td-color-ramp]").eq(rampIdx).css("width", (Math.round(($(this).val() - minFieldVal)/(newMaxVal - minFieldVal)*$("#tb-color-ramp").css("width").replace("px", "")) - tmpWidth) + "px");
				$("td[id=td-color-ramp]").eq(rampIdx+1).css("width", (tdSumWidth - (Math.round(($(this).val() - minFieldVal)/(newMaxVal - minFieldVal)*$("#tb-color-ramp").css("width").replace("px", "")) - tmpWidth)) + "px");
			});
			_rgabChange('noOpacity');
		});
		
		$("#saveColorRamp_b").click(function(){
			$("#color-ramp").data("num-field", $("#feature-num-field").val());
			$("#color-ramp").data("color-cnt", $("#color-div-cnt").val());
			var rangeArr = $(".range-ramp-val").map(function(){ return $(this).val(); }).get();
			rangeArr.unshift($("#range-color-ramp1").prop("min"));
			rangeArr.push($("#range-color-ramp1").prop("max"));
			$("#color-ramp").data("range-arr", rangeArr);
			$("#color-ramp").data("color-arr", $("td[id=td-color-ramp]").map(function(){ return $(this).css("background-color").replace(/ /g,""); }).get());
			$("#color-ramp").css("background", $("#color-ramp-type").css("background"));
			$("#color-ramp").data("color-type", $("#color-ramp-type").html());
			
			$("#colorRamp_popup").remove();
		});
	}
	
	function _colorRampSetting(colorRangeArr, tdColorArr){
		$("#feature-num-field-sel").val($("#feature-num-field").val());
		$("#color-ramp-type-canv").click();
		$("#"+$("#color-ramp-type").html()).click();
		$("#color-div-cnt-sel").val($("#color-div-cnt").val());
		$("#createColorRamp_b").click();
		$(".range-ramp").each(function(idx){
			$(this).val(colorRangeArr[idx+1]);
			$(this).change();
		});
		$("td[id=td-color-ramp]").each(function(idx){
			$(this).css("background-color", tdColorArr[idx]);
		});
	}
	
	function _changeColor(layerName){
		$("#close_color").click(function(){
			$("#color_popup").remove();
		});
		$("#change_color").click(function(){
			var geomType = $("div[id=legend-"+layerName+"]").data("geomtype");
			var layerOpacity = $("#layer-opacity").val();
			var fillColor = $("#fill-color").css("background-color");
			var strokeColor = $("#stroke-color").css("background-color");
			var strokeDash = $("#stroke-dash").val();
			var textLabelField = $("#text-field").val();
			var textSize = $("#text-size").val();
			var textFontType = $("#font-type").val();
			var textFont = "normal" +" "+ textSize +"px '"+ textFontType +"'";
			var textColor = $("#text-color").css("background-color");
			var textAlign = $("#text-align").val();
			var textBaseline = $("#text-baseline").val();
			var fillColorType = $("#color-type").val();
			var colorDivCnt = $("#color-ramp").data("color-cnt");
			var colorArr = $("#color-ramp").data("color-arr");
			var rangeField = $("#color-ramp").data("num-field");
			var rangeArr = $("#color-ramp").data("range-arr");
			var colorRampType = $("#color-ramp").data("color-type");
			var imgSrc = $("#icon-img-src").val();
			var heatRadius = $("#heat-radius").val(); 
			var heatBlur = $("#heat-blur").val();
			
			var chk_img_url = true;
			
			wavus.layer.findLayerById(layerName).then(function(layer){
				var style;
				var chkHeatmap = false;
				if(layer instanceof ol.layer.Heatmap){
					chkHeatmap = true;
					style = new ol.style.Style();
				}else{
					if(layer.getStyle() instanceof ol.style.Style){
						style = layer.getStyle().clone();
					}else{
						if(layer.getStyle()().length != undefined){
							style = layer.getStyle()()[0].clone();
						}else{
							style = layer.getStyle()().clone();
						}
					}
				}
				
				layer.setOpacity(layerOpacity);
				if(chkHeatmap == true){
					layer.setRadius(heatRadius*1);
					layer.setBlur(heatBlur*1);
				}else{
					var dynamicStyle = function(tmpStyle){					
						if($("div[id=text-"+layerName+"]").length > 0){
							$.each(layer.getSource().getFeatures(), function(idx, feature){
								var svgObj = $((new DOMParser()).parseFromString(feature.getStyle().getImage().getSrc().replace("data:image/svg+xml, ", ""), "image/svg+xml"));
								var textFeatureStyle = wavus.draw.createTextImgSVG({
									polygonStroke : strokeColor,
									polygonFill : fillColor,
									type : svgObj.find("svg").attr("id"),
									textFill : textColor,
									textFontFamily : textFontType,
									textFontSize : textSize,
									svg : svgObj
								});
								feature.setStyle(textFeatureStyle);
							});
						}else{
							var tmpStyleFunc = function (feature, resolution){
								if(feature != undefined){
									tmpStyle.getText().setText(textLabelField != "" ? feature.get(textLabelField)+"" : "");
									if(fillColorType == 'multi'){
										var featureVal = wavus.util.removeCommas(feature.get(rangeField)+"")*1;
										$.each(colorArr, function(idx, rgbColor){
											if((idx == 0 ? rangeArr[idx] - 1 : rangeArr[idx]) < featureVal && rangeArr[idx+1] >= featureVal){
												if(tmpStyle.getImage()){
													var tmpImageStyle;
													tmpStyle.getImage().getFill().setColor(rgbColor);
													if(tmpStyle.getImage() instanceof ol.style.Circle){
														tmpImageStyle = new ol.style.Circle({
															radius: tmpStyle.getImage().getRadius(),
															stroke: tmpStyle.getImage().getStroke(),
															fill: tmpStyle.getImage().getFill()
														});
													}else if(tmpStyle.getImage() instanceof ol.style.RegularShape){
														tmpImageStyle = new ol.style.RegularShape({
															radius: tmpStyle.getImage().getRadius(),
															stroke: tmpStyle.getImage().getStroke(),
															fill: tmpStyle.getImage().getFill()
														});
													}
													tmpStyle.setImage(tmpImageStyle);
												}
												tmpStyle.setFill(new ol.style.Fill({ color : rgbColor }));
												tmpStyle.setZIndex(featureVal);
											}
										});
									}
								}
								tmpStyle.getText().setFill(new ol.style.Fill({ color: textColor }));
								tmpStyle.getText().setFont(textFont);
								tmpStyle.getText().setTextAlign(textAlign);
								tmpStyle.getText().setTextBaseline(textBaseline);							
								return [tmpStyle];
							}					
							layer.setStyle(tmpStyleFunc);
						}
					}
					
					var imageStyle;
					if(style.getImage()){
						if(style.getImage() instanceof ol.style.Icon){
							var chk_img = true;
							$.ajax({
								type: "HEAD", url: imgSrc, async: false, cache: true, timeout: 1,
								complete: function(response){
									if(response.status == 200){
										if(response.getResponseHeader('Content-Type').indexOf("image") == -1) chk_img = false;
									}else{
										chk_img = false;
									}
								} 
							});
							if(!chk_img){
								alert("Image URL이 잘못되었습니다.");
								chk_img_url = false;
								return;
							}
							imageStyle = new ol.style.Icon({
									src: imgSrc
							});
						}else if(style.getImage() instanceof ol.style.Circle || style.getImage() instanceof ol.style.RegularShape){
							if($("#stroke-"+layerName).length > 0){
								style.getImage().getStroke().setColor(strokeColor);
							}
							if($("#fill-"+layerName).length > 0){
								style.getImage().getFill().setColor(fillColor);
							}
							if(style.getImage() instanceof ol.style.Circle){
								imageStyle = new ol.style.Circle({
									radius: style.getImage().getRadius(),
									stroke: style.getImage().getStroke(),
									fill: style.getImage().getFill()
								});
							}else if(style.getImage() instanceof ol.style.RegularShape){
								imageStyle = new ol.style.RegularShape({
									radius: style.getImage().getRadius(),
									stroke: style.getImage().getStroke(),
									fill: style.getImage().getFill()
								});
							}
						}
					}
					if(!style.getStroke()){ 
						style.setStroke(new ol.style.Stroke());
					}
					if($("#stroke-"+layerName).length > 0){
						style.getStroke().setColor(strokeColor);
						style.getStroke().setLineDash([strokeDash*1, strokeDash*1]);
					}
					if($("#fill-"+layerName).length > 0){
						style.setFill(new ol.style.Fill({ color : fillColor }));
					}
					if(!style.getText()){
						style.setText(new ol.style.Text());
					}
					var tmpStyle = new ol.style.Style({
						image: imageStyle,
						fill: style.getFill(),
						stroke: style.getStroke(),
						text: style.getText()
					});
					dynamicStyle(tmpStyle);
				}
			});
			
			if(!chk_img_url){
				return;
			}
			if($("div[id=text-"+layerName+"]").length > 0){
				$("#stroke-"+layerName).css("background-color", strokeColor);
				$("#fill-"+layerName).css("background-color", fillColor);
				$("#text-"+layerName).css("background-color", textColor);
				$("#text-"+layerName).next(".v-legend_tf2").eq(0).css("background-color", textColor);
				$("#text-"+layerName).data("size", textSize);
				$("#text-"+layerName).data("font-type", textFontType);
			}else{
				if($("div[id=legend-"+layerName+"]").children("img").length > 0){
					$("#icon-"+layerName).prop("src", imgSrc);
				}else{
					if($("#stroke-"+layerName).length > 0){
						$("#stroke-"+layerName).css("background-color", strokeColor);
						$("#stroke-"+layerName).data("dash", strokeDash);
					}
					if($("#fill-"+layerName).length > 0){
						$("#fill-"+layerName).css("background-color", fillColor);
					}
					if($("#heat-"+layerName).length > 0){
						$("#heat-"+layerName).data("radius", heatRadius);
						$("#heat-"+layerName).data("blur", heatBlur);
					}
					if(fillColorType == "multi"){
						$("#stroke-"+layerName).css("display", "none");
						$("#ramp-"+layerName).remove();
						$("#expand-"+layerName).remove();
						$("#collapse-"+layerName).remove();
						$("#color-ramp-legend-"+layerName).remove();
						var tmpClassName = geomType == "point" ? "v-legend_circle_s" : "v-legend_ps";
						$("div[id=legend-"+layerName+"]").append("<div id='ramp-"+layerName+"' class='"+tmpClassName+" " + colorRampType + "'></div>");
						$("#layer-"+layerName).data("color-type", "multi");
						$("#ramp-"+layerName).data("num-field", rangeField);
						$("#ramp-"+layerName).data("color-type", colorRampType);
						$("#ramp-"+layerName).data("color-cnt", colorDivCnt);
						$("#ramp-"+layerName).data("range-arr", rangeArr);
						$("#ramp-"+layerName).data("color-arr", colorArr);
						$("div[id=legend-"+layerName+"]").before("<div id='expand-"+layerName+"' class='toggle-legend-plus'></div><div id='collapse-"+layerName+"' class='toggle-legend-minus' style='display:none;'></div>");
						$("div[id=legend-"+layerName+"]").parents("li").append("<div id='color-ramp-legend-"+layerName+"' class='color-ramp-legend' style='display:none;'>Field Name : "+rangeField+"</div>");
						$.each(colorArr, function(idx, rgbColor){
							$("#color-ramp-legend-"+layerName).append("<div><div class='multi-legend "+tmpClassName+"' style='background-color:"+rgbColor+";display:inline-block;'></div>" +
									"<div class='multi-legend-val' style='display:inline-block;'>&nbsp; "+((idx == 0 ? rangeArr[idx] : rangeArr[idx]*1+1))+" - "+rangeArr[idx+1]+"</div></div>");
						});
						$("#mainTOC div[class*=toggle-legend-]").click(function(){
							if($(this).prop("id") == "expand-"+layerName){
								$(this).css("display", "none");
								$("#collapse-"+layerName).css("display", "");
								$("#color-ramp-legend-"+layerName).css("display", "");
							}
							if($(this).prop("id") == "collapse-"+layerName){
								$(this).css("display", "none");
								$("#expand-"+layerName).css("display", "");
								$("#color-ramp-legend-"+layerName).css("display", "none");
							}
						});
					}else{
						$("#layer-"+layerName).data("color-type", "single");
						$("#stroke-"+layerName).css("display", "");
						$("#ramp-"+layerName).remove();
						$("#expand-"+layerName).remove();
						$("#collapse-"+layerName).remove();
					}
				}
				$("#text-"+layerName).data("field", textLabelField);
				$("#text-"+layerName).data("size", textSize);
				$("#text-"+layerName).data("font-type", textFontType);
				$("#text-"+layerName).data("align",textAlign);
				$("#text-"+layerName).data("baseline",textBaseline);
				$("#text-"+layerName).css("color", textColor);
			}
			$("#layer-"+layerName).val(layerOpacity);
			wavus.controller.vectorNameChange(layerName, $("#changeLayerNm").val());
			$("#color_popup").remove();
		});
	}
		
	function _TOCLayerChange(layer){
		if(layer instanceof ol.layer.Vector && layer.get("name") != undefined){
			if($("#"+layer.get("name")).length == 0){
				wavus.controller.addTOCElement(layer.get("name"));
				layer.setVisible(false);
			}
			layer.getSource().once('addfeature', function(evt){
				_styleLegend(layer);
				$("div[id*='legend-']").off();
				_vectorStyleChange();
				layer.getSource().on('addfeature', function(e){
					if(layer.getSource().getFeatures()[0].getGeometry().getType() != e.feature.getGeometry().getType()){
						_styleLegend(layer);
						$("div[id*='legend-']").off();
						_vectorStyleChange();
					}
				});
			});
		}
	}
	
	function _configSaveClear(actionType){
		if(wavus.user.getErrStatus() != "" ){
			alert("로그인 후 이용하세요");
			return;
		}
		var sucFunc;
		var customConfigJson = new Object();
		if(actionType == "save"){
			if(!confirm("설정을 저장하시겠습니까?")){
				return;
			}
			$.each($.grep(_map.getLayers().getArray(), function(v, i){ return v instanceof ol.layer.Vector && v.get('name') != undefined && $("#add_"+v.get('name')).is(":checked") == false }), function(key, layer){
				wavus.map.removeLayer(layer);
			});
			var customJsonArray = new Array();
			var textLayerJsonArray = new Array();
			for(var i=1; i<=wavus.draw.getTextLayerCnt(); i++){
				wavus.layer.findLayerById('text_'+i).then(function(layer){
					if(layer != undefined){
						if($("#add_text_"+i).is(":checked") == true){
							$.each(layer.getSource().getFeatures(), function(i, feature){
								var textLayerJson = new Object();
								textLayerJson.featureLayerId = layer.get('name');
								textLayerJson.featureCoordinate = feature.getGeometry().getCoordinates();
								var textLineArr = new Array();
								var svgObj = $((new DOMParser()).parseFromString(feature.getStyle().getImage().getSrc().replace("data:image/svg+xml, ", ""), "image/svg+xml"));
								svgObj.find("text tspan").each(function(){ 
									textLineArr.push(decodeURIComponent($(this).text())); 
								});
								var textString = textLineArr.join('\n');
								textLayerJson.featureText = escape(textString);
								textLayerJson.memoType = svgObj.find("svg").attr("id");
								textLayerJsonArray.push(textLayerJson);
							});
						}
					}
				});
			}
			customConfigJson.textLayer = textLayerJsonArray;
			
			$("#mainTOC").find("input[type=checkbox]").each(function(){
				var layerId = $(this).prop("id");
				var layerNm = $(this).val();
				var layer_zindex = "";
				var layer_aindex = "";
				wavus.layer.findLayerById(layerId).then(function(layer){
					layer_zindex = layer.getZIndex();
					layer_aindex = _.findIndex(_map.getLayers().getArray(), layer);
				});
				if($(this).parents("li").eq(0).css("display") != "none"){
					var customJson = new Object();
					customJson.layerId = layerId;
					customJson.layerNm = escape(layerNm);
					customJson.onOff = $(this).is(":checked");
					customJson.zIndex = layer_zindex;
					customJson.aIndex = layer_aindex;
					customJson.wmsStyle = $("select[id=wms_style_"+layerId+"]").length > 0 ? $("select[id=wms_style_"+layerId+"]").val() : "";
					var styleObj = new Object();
					if($("div[id=legend-"+layerId+"]").length > 0){
						styleObj.layer_opacity = $("#layer-"+layerId).val();
						styleObj.heat_radius = $("#heat-"+layerId).length > 0 ? $("#heat-"+layerId).data("radius") : "";
						styleObj.heat_blur = $("#heat-"+layerId).length > 0 ? $("#heat-"+layerId).data("blur") : "";
						styleObj.stroke_color = $("#stroke-"+layerId).length > 0 ? $("#stroke-"+layerId).css("background-color").replace(/ /g, "") : "";
						styleObj.stroke_dash = $("#stroke-"+layerId).length > 0 ? $("#stroke-"+layerId).data("dash") : "";
						styleObj.fill_color = $("#fill-"+layerId).length > 0 ? $("#fill-"+layerId).css("background-color").replace(/ /g, "") : "";
						var color_ramp_tmp = $("#ramp-"+layerId).data();
						color_ramp_tmp.numField = escape(color_ramp_tmp.numField); 
						styleObj.color_ramp = color_ramp_tmp;
						styleObj.text_field = $("#text-"+layerId).data("field");
						styleObj.text_size = $("#text-"+layerId).data("size");
						styleObj.font_type = $("#text-"+layerId).data("font-type");
						styleObj.text_align = $("#text-"+layerId).data("align");
						styleObj.text_baseline = $("#text-"+layerId).data("baseline");
						styleObj.text_color = $("#text-"+layerId).css("color").replace(/ /g, '');
						styleObj.icon_img_src = $("#icon-"+layerId).length > 0 ? escape($("#icon-"+layerId).prop("src")) : "";
					}
					customJson.vectorStyle = styleObj;
					customJsonArray.push(customJson);
				}
			});
			customConfigJson.layerConfig = customJsonArray;
			
			
			$.each($.grep(_map.getLayers().getArray(), function(v, i){ return v instanceof ol.layer.Vector && v.get('name') != undefined && v.getSource().getFeatures()[0].get("feature_type") != 'text'}), function(key, layer){
					var url="/file/json/delete.do";  
					$.ajax({      
					url : url,
					type : "POST",  
					data: {
						fileName:layer.get('name'),
					},
					dataType:'json'
				});
			});
			
			$.each($.grep(_map.getLayers().getArray(), function(v, i){ return v instanceof ol.layer.Vector && v.get('name') != undefined 
																									&& v.getSource().getFeatures()[0].get("feature_type") != 'text' && $("#add_"+v.get('name')).is(":checked") == true; 	}), function(key, layer){				
				var VectorGeoJson = (new ol.format.GeoJSON()).writeFeaturesObject(layer.getSource().getFeatures());
				var convertJson = JSON.stringify(VectorGeoJson);
	 			
				var url="/file/json/upload.do";  
				$.ajax({      
					url : url,
					type : "POST",  
			        data : {
			        	filename:layer.get('name'),
			        	json:convertJson
		        	},
			        dataType:'json'
			    });
			});
			
			sucFunc = function(){
				alert("설정 저장이 완료되었습니다.");
			};
		}else if(actionType == "clear"){
			if(!confirm("설정을 초기화하시겠습니까?")){
				return;
			}
			$.each($.grep(_map.getLayers().getArray(), function(v, i){ return v instanceof ol.layer.Vector 
																									&& v.get('name') != undefined 
																									&& v.getSource().getFeatures()[0].get("feature_type") != 'text'}), function(key, layer){
				var url="/file/json/delete.do";  
				$.ajax({      
					url : url,
					type : "POST",  
			        data: {
			        	fileName:layer.get('name'),
		        	},
			        dataType:'json'
			    });
			});
			sucFunc = function(){
				alert("설정이 초기화되었습니다.");
				window.location.reload();
			};
		}
		var configParam = {"configJson" : JSON.stringify(customConfigJson)};
		$.ajax({
			url : '/file/configSave.do',
			data : configParam,
			success :  sucFunc
		});
	}
	
	function init(map){
		_map = map;
	}
	
	var module = {
		setLayerLegend : setLayerLegend,
		setCustomLayer : setCustomLayer,
		layerStyleCustom : layerStyleCustom,
		textFieldSelect : textFieldSelect,
		init : init
	};
	
	return module;
	
})();