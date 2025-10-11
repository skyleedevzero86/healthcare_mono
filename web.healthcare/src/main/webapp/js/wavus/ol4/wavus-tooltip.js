
window.wavus = window.wavus || {};

wavus.tooltip = (function() {
	var _map;
	
	var tooltipElement, tooltip;
	var listener;							//리스너
	
	
	function addTooltip(message){
		if(!wavus.util.isEmpty(message)){
			_message(message);
		}else{
			return console.log('메세지를 입력해주세요');
		}
		_createTooltip();
		listener = _map.on('pointermove', _pointerMoveHandler);
		_map.getViewport().addEventListener('mouseout', _mouseoutHandler);
	}
	
	function removeTooltip(){
		_map.getViewport().removeEventListener('mouseout', _mouseoutHandler);
		ol.Observable.unByKey(listener);
		if(tooltip)_map.removeOverlay(tooltip);
	}
	
	//툴팁생성
	function _createTooltip(){
		if (tooltipElement) {
			tooltipElement.parentNode.removeChild(tooltipElement);
		}
		tooltipElement = document.createElement('div');
		tooltipElement.className = 'tooltip';
		tooltip = new ol.Overlay({
			element : tooltipElement,
			offset : [ 15, 0 ],
			positioning : 'center-left'
		});
		_map.addOverlay(tooltip);
	}
	
	//이벤트 핸들러
	var _pointerMoveHandler = function(evt) {
		if (evt.dragging) {
			return;
		}
		/** @type {string} */
				
		tooltipElement.innerHTML = message;
		tooltip.setPosition(evt.coordinate);
		tooltipElement.classList.remove('hidden');
	}
	
	var _mouseoutHandler = function(){
		tooltipElement.classList.add('hidden');
	}
	
	function _message(message){
		this.message = message;					
	}
	
	function init(map){
		_map = map;
	}
	
	
	var module = {
			addTooltip : addTooltip,
			removeTooltip : removeTooltip,
			
			init : init
	}
	
	return module;
	
})();