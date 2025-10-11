
window.wavus = window.wavus || {};

wavus.write = (function() {
	
	var _map;
	
	var textOverLays = [];
	
	function writeText(text, coordinate){
		var textElement = document.createElement('div');
		textElement.innerHTML = text;
		textElement.className = 'tooltip tooltip-static print';
		var textOverLay = new ol.Overlay({
			position : coordinate,
			element : textElement,
			offset : [ 0, -22 ],
			positioning : 'center-center',
			stopEvent : false
		});
		_map.addOverlay(textOverLay);
		textOverLays.push(textOverLay);
	}
	
	function removeText(){
		_.each(textOverLays, function(overLay){
			_map.removeOverlay(overLay);
		})
	}
	
	function init(map){
		_map = map;
	}
	
	
	var module = {
			writeText : writeText,
			removeText : removeText,
			
			init : init
		};
	
	return module;
	
})();