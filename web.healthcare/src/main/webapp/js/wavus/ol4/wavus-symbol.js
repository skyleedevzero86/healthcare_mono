
window.wavus = window.wavus || {};

wavus.symbol = (function() {
	function findSymbol(param) {
		return _setSymbolAsPolygon(param.style);
	}
	
	function _setSymbolAsPolygon(style) {
		var vectorStyle = new ol.style.Style();
		switch(style) {
			case 'none' :
				vectorStyle.setFill(new ol.style.Fill({
					color : 'rgba(255, 255, 255, 0.2)'
				})),
				vectorStyle.setStroke(new ol.style.Stroke({
					color : '#1666b7',
					width : 2
				})),
				vectorStyle.setImage(new ol.style.Circle({
					radius : 7,
					fill : new ol.style.Fill({
						color : '#1666b7'
					})
				}))
				break;
			case 'draw' :
				vectorStyle.setFill(new ol.style.Fill({
					color : 'rgba(255, 255, 255, 0.2)'
				})),
				vectorStyle.setStroke(new ol.style.Stroke({
					color : '#1666b7',
					//lineDash : [ 10, 10 ],
					width : 2
				})),
				vectorStyle.setImage(new ol.style.Circle({
					radius : 5,
					stroke : new ol.style.Stroke({ 
						color : 'rgba(0, 0, 0, 0.7)'
					}),
					fill : new ol.style.Fill({
						color : 'rgba(255, 255, 255, 0.2)'
					})
				}))
				break;
			case 'radius' :
				vectorStyle.setFill(new ol.style.Fill({
					color : 'rgba(230, 67, 51, 0.05)'
				})),
				vectorStyle.setStroke(new ol.style.Stroke({
					color : 'rgba(230, 67, 51, 1.0)',
					//lineDash : [ 10, 10 ],
					width : 2
				})),
				vectorStyle.setImage(new ol.style.Circle({
					radius : 5,
					stroke : new ol.style.Stroke({ 
						color : 'rgba(230, 67, 51, 1.0)'
					}),
					fill : new ol.style.Fill({
						color : 'rgba(230, 67, 51, 0.05)'
					})
				}))
				break;
			case 'highlight' :
				/*vectorStyle.setFill(new ol.style.Fill({
					color : 'rgba(255, 255, 255, 0.2)'
				})),*/
				vectorStyle.setStroke(new ol.style.Stroke({
					color : '#f5d80a',
					width : 5
				})),
				vectorStyle.setImage(new ol.style.Circle({
					radius : 7,
					fill : new ol.style.Fill({
						color : '#f5d80a'
					})
				}))
				break;
			case 'bound' :
				/*vectorStyle.setFill(new ol.style.Fill({
					color : 'rgba(255, 255, 255, 0.2)'
				})),*/
				vectorStyle.setStroke(new ol.style.Stroke({
					color : '#00cef5',
					width : 3
				})),
				vectorStyle.setImage(new ol.style.Circle({
					radius : 7,
					fill : new ol.style.Fill({
						color : '#00cef5'
					})
				}))
				break;
			case 'addBuilding' :
				vectorStyle.setFill(new ol.style.Fill({
					color : 'rgba(255, 255, 255, 0.2)'
				})),
				vectorStyle.setStroke(new ol.style.Stroke({
					color : '#f5d80a',
					lineDash : [ 10, 10 ],
					width : 2
				})),
				vectorStyle.setImage(new ol.style.Circle({
					radius : 7,
					fill : new ol.style.Fill({
						color : '#f5d80a'
					})
				}))
				break;
			case 'sofa' :
				vectorStyle.setFill(new ol.style.Fill({
					color : 'rgba(26, 57, 105, 0.2)'
				})),
				vectorStyle.setStroke(new ol.style.Stroke({
					color : 'rgba(26, 57, 105, 1)',
					width : 2
				}))
				break;
			case 'bsnss_mngme' :
				bsnssStyle = function(feature){
					vectorStyle.setFill(new ol.style.Fill({
						color : 'rgba(249, 170, 53, 0.5)'
					})),
					vectorStyle.setStroke(new ol.style.Stroke({
						color : 'rgba(245, 83, 21, 1)',
						width : 3
					})),
					vectorStyle.setText(new ol.style.Text({
						font : '11px Gulim ',
						stroke : new ol.style.Stroke({
							color : '#FFFFFF', 
							width : 5
						}),
						text : wavus.map.getMap().getView().getZoom() > 15 ?  feature.get('label') : ''
					})),
					vectorStyle.setImage(new ol.style.Circle({
						radius : 7,
						fill : new ol.style.Fill({
							color : 'rgba(144, 0, 221, 1)'
						})
					}))
					return vectorStyle;
				}
				break;
				default : 
					break;
		}
		return vectorStyle;
	}

    function setFeatureStyle(feature){
        var vectorStyle = new ol.style.Style();
        var colorNo;

        if (!wavus.util.isEmpty(feature.get('colorNo'))) {
            colorNo = feature.get('colorNo');
        } else {
            colorNo = 99;
        }

        // console.log('colorNo : ' + colorNo + ', _colorList[colorNo] : ' + _colorList[colorNo]);

        var rgb = _hexToRgb(_colorList[colorNo]);

        vectorStyle.setFill(new ol.style.Fill({
            color: 'rgba(' +rgb.r + ', ' + rgb.g +', ' + rgb.b + ', ' + '0.6)'
        }))

        vectorStyle.setStroke(new ol.style.Stroke({
			   color : _colorList[colorNo],
			   width : '3'
		}))
        return vectorStyle;
    }
    
    /**
     * 기본 스타일을 리턴한다.
     */
    function getDefaultStyle(){
    	var vectorStyle = new ol.style.Style();
    	var rgb = _hexToRgb(_colorList[99]);
        vectorStyle.setFill(new ol.style.Fill({
            color: 'rgba(' +rgb.r + ', ' + rgb.g +', ' + rgb.b + ', ' + '0.6)'
        }))

        vectorStyle.setStroke(new ol.style.Stroke({
			   color : _colorList[99],
			   width : '3'
		}))
        return vectorStyle;
    }
    
    /**
     * 피처에 컬러 인덱스에 따른 해당 스타일을 리턴
     */
    function getFeatureStyle(colorIdx){
    	var vectorStyle = new ol.style.Style();
    	var rgb = _hexToRgb(_colorList[colorIdx]);
        vectorStyle.setFill(new ol.style.Fill({
            color: 'rgba(' +rgb.r + ', ' + rgb.g +', ' + rgb.b + ', ' + '0.6)'
        }))

        vectorStyle.setStroke(new ol.style.Stroke({
			   color : _colorList[colorIdx],
			   width : '3'
		}))
        return vectorStyle;    	
    	
    }
    
    function _hexToRgb(hex) {
        var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
        return result ? {
            r: parseInt(result[1], 16),
            g: parseInt(result[2], 16),
            b: parseInt(result[3], 16)
        } : null;
    }


	var _colorList = {
			1 : '#65cd12',
			2 : '#23238E',
			3 : '#4D4DFF',
			4 : '#FF6EC7',
			5 : '#00009C',
			6 : '#EBC79E',
			7 : '#CFB53B',
			8 : '#FF7F00',
			9 : '#FF2400',
			10 : '#DB70DB',
			11 : '#8FBC8F',
			12 : '#BC8F8F',
			13 : '#EAADEA',
			14 : '#D9D9F3',
			15 : '#5959AB',
			16 : '#6F4242',
			17 : '#8C1717',
			18 : '#238E68',
			19 : '#6B4226',
			20 : '#8E6B23',
			21 : '#E6E8FA',
			22 : '#3299CC',
			23 : '#007FFF',
			24 : '#FF1CAE',
			25 : '#00FF7F',
			26 : '#236B8E',
			27 : '#38B0DE',
			28 : '#DB9370',
			29 : '#D8DFD8',
			30 : '#ADEAEA',
			31 : '#5C4033',
			32 : '#CDCDCD',
			33 : '#4F2F4F',
			34 : '#CC3229',
			35 : '#D8D8BF',
			36 : '#99CC32',
			37 : '#99CC32',
			38 : '#99CC32',
			39 : '#99CC32',
			40 : '#99CC32',
			41 : '#99CC32',
			42 : '#99CC32',
			43 : '#99CC32',
			44 : '#99CC32',
			45 : '#99CC32',
			46 : '#99CC32',
			47 : '#99CC32',
			48 : '#99CC32',
			49 : '#99CC32',
			50 : '#99CC32',
			51 : '#99CC32',
			52 : '#99CC32',
			53 : '#99CC32',
			54 : '#99CC32',
			55 : '#99CC32',
			56 : '#99CC32',
			57 : '#99CC32',
			58 : '#99CC32',
			59 : '#99CC32',
			60 : '#99CC32',
			61 : '#99CC32',
			62 : '#99CC32',
			63 : '#99CC32',
			64 : '#99CC32',
			65 : '#99CC32',
			66 : '#99CC32',
			67 : '#99CC32',
			68 : '#99CC32',
			69 : '#99CC32',
			70 : '#99CC32',
			71 : '#99CC32',
			72 : '#99CC32',
			73 : '#99CC32',
			74 : '#99CC32',
			75 : '#99CC32',
			76 : '#99CC32',
			77 : '#99CC32',
			78 : '#99CC32',
			79 : '#99CC32',
			80 : '#99CC32',
			81 : '#99CC32',
			82 : '#99CC32',
			83 : '#99CC32',
			84 : '#99CC32',
			85 : '#99CC32',
			86 : '#99CC32',
			87 : '#99CC32',
			88 : '#99CC32',
			89 : '#99CC32',
			90 : '#99CC32',
			91 : '#99CC32',
			92 : '#99CC32',
			93 : '#99CC32',
			94 : '#99CC32',
			95 : '#99CC32',
			96 : '#99CC32',
			97 : '#99CC32',
			98 : '#99CC32',
			99 : '#49f86f',
			100 : '#99CC32',
			101 : '#99CC32',
			102 : '#99CC32',
			103 : '#99CC32',
			104 : '#99CC32',
			105 : '#99CC32',
			106 : '#99CC32'
	}

	var module = {
		findSymbol : findSymbol,
		setFeatureStyle : setFeatureStyle,
		
		getDefaultStyle : getDefaultStyle,
		getFeatureStyle : getFeatureStyle,

		_colorList : _colorList
	};
	
	return module;
	
})();

