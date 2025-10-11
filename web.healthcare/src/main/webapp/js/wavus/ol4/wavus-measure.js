
window.wavus = window.wavus || {};

wavus.measure = (function() {
	
	var _map;
	
	
	var sketch;
	var listener;
	var measureTooltipElement, measureTooltip;
	var measureTooltips = [];
	var wgs84Sphere = new ol.Sphere(6378137);
	
	function addMeasure(draw){
		if(!draw || typeof draw != 'object'){
			return console.log('wavus.draw.addDraw() 의 draw객체를 상속받아야 합니다.');
		}
		_createMeasureTooltip();
		draw.on('drawstart', _setMesureTootip);
		draw.on('drawend', _resultMesureTootip);
	}
	
	function removeResult(coord){
		if(coord != undefined){
			var selMeasureTooltip = _.find(measureTooltips, function(measureTooltip){ return _.isEqual(measureTooltip.getPosition(), coord); });
			_map.removeOverlay(selMeasureTooltip);
		}else{
			_.each(measureTooltips, function(measureTooltip){
				_map.removeOverlay(measureTooltip);
			});
		}
	}
	
	function _createMeasureTooltip() {
		if (measureTooltipElement) {
			measureTooltipElement.parentNode.removeChild(measureTooltipElement);
		}
		measureTooltipElement = document.createElement('div');
		measureTooltipElement.className = 'tooltip tooltip-measure';
		measureTooltip = new ol.Overlay({
			element : measureTooltipElement,
			offset : [ 0, -15 ],
			positioning : 'bottom-center',
			stopEvent : false
		});
		_map.addOverlay(measureTooltip);
		measureTooltips.push(measureTooltip);
	}
	
	var _setMesureTootip = function(evt) {
		sketch = evt.feature;

		/** @type {ol.Coordinate|undefined} */
		var tooltipCoord = evt.coordinate;

		listener = sketch.getGeometry().on('change', function(evt) {
			var geom = evt.target;
			var output;
			if (geom instanceof ol.geom.Polygon) {
				output = _formatArea(geom);
				tooltipCoord = geom.getInteriorPoint().getCoordinates();
			} else if (geom instanceof ol.geom.LineString) {
				output = _formatLength(geom);
				tooltipCoord = geom.getLastCoordinate();
			}
			measureTooltipElement.innerHTML = output;
			measureTooltip.setPosition(tooltipCoord);
		});
	}
	
	/**
	 * Format area output.
	 * @param {ol.geom.Polygon} polygon The polygon.
	 * @return {string} Formatted area.
	 */
	var _formatArea = function(polygon) {
		var area;
		
		var geom = /** @type {ol.geom.Polygon} */
		(polygon.clone().transform(wavus.config.mapInfo.projection, 'EPSG:4326'));
		var coordinates = geom.getLinearRing(0).getCoordinates();
		area = Math.abs(wgs84Sphere.geodesicArea(coordinates));
		
		var output;
		if (area > 10000) {
			output = (Math.round(area / 1000000 * 100) / 100) + ' '
					+ 'km<sup>2</sup>';
		} else {
			output = (Math.round(area * 100) / 100) + ' ' + 'm<sup>2</sup>';
		}
		return output;
	};
	/**
	 * Format length output.
	 * @param {ol.geom.LineString} line The line.
	 * @return {string} The formatted length.
	 */
	var _formatLength = function(line) {
		var length;
		var coordinates = line.getCoordinates();
		length = 0;
		for (var i = 0, ii = coordinates.length - 1; i < ii; ++i) {
			var c1 = ol.proj.transform(coordinates[i], wavus.config.mapInfo.projection, 'EPSG:4326');
			var c2 = ol.proj.transform(coordinates[i + 1], wavus.config.mapInfo.projection, 'EPSG:4326');
			length += wgs84Sphere.haversineDistance(c1, c2);
		}
		var output;
		if (length > 100) {
			output = (Math.round(length / 1000 * 100) / 100) + ' ' + 'km';
		} else {
			output = (Math.round(length * 100) / 100) + ' ' + 'm';
		}
		return output;
	};
	
	var _resultMesureTootip = function(){
		measureTooltipElement.className = 'tooltip tooltip-static print';
	    measureTooltip.setOffset([0, -7]);
	    sketch = null;
	    measureTooltipElement = null;
	    _createMeasureTooltip();
	    ol.Observable.unByKey(listener);
	}
	
	
	function init(map){
		_map = map;
	} 
	
	
	var module = {
			addMeasure : addMeasure,
			removeResult : removeResult,
			
			init : init 
		};
	
	return module;
})();

