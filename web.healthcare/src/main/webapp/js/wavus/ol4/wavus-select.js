window.wavus = window.wavus || {};

wavus.select = (function() {
	var _map;
	
	var _select;
	
	var selDeferred;
	
	//벡터레이어 선택하기
	
	/**
	 * 벡터레이어 선택하기
	 * return $.Deferred()
	 * 
	 * 벡터 선택시 -notify 이벤트 발생
	 * 이벤트 발생 후 선택된 것은 해지 됨
	 * 하이라이트 레이어등을 이용하여 별도로 그려야함
	 * 
	 *  사용법
	 *  
	 *  {1} 클릭 타입 종류 : 'click' or 'altClick'
	 *  {2} 클릭시 선택된것 초기화 유무(별도로 화면에 그릴시 true, 최상위로 그냥 올릴시 false)
	 *  
	 *  var xxx = wavus.select.addSelect('click', false);
	 *  xxx.then(null, null, function(feature){
	 *  	console.log('선택된 벡터');
	 *  	console.log(feature);
	 *  });
	 */
	function addSelect(type, clearFlag) {
		removeSelect();
		selDeferred = $.Deferred();
		
		var _clearFlag = false;
		
		if(clearFlag){
			_clearFlag = true;
		}
		
		switch (type) {
			case 'altClick':
				_select = new ol.interaction.Select({
					condition : function(mapBrowserEvent) {
						return ol.events.condition.click(mapBrowserEvent)
								&& ol.events.condition.altKeyOnly(mapBrowserEvent);
					}
				});
				break;
			case 'click':
				_select = new ol.interaction.Select({
					condition : ol.events.condition.click
				});
				break;
				default : 
					break;
		}
		_map.addInteraction(_select);
		_select.on('select', function(evt){
			
			if(_clearFlag){
				_select.getFeatures().clear();
			}
			selDeferred.notify(evt.selected[0]);
		})
		
		return selDeferred;
	}
	
	function removeSelect(){
		if(!wavus.util.isEmpty(_select)){
			_map.removeInteraction(_select);
			selDeferred = null;
		}
	}
	
	function init(map){
		_map = map;
	}
	

	var module = {
		addSelect : addSelect,
		removeSelect : removeSelect,
		
		init : init
	};

	return module;
})();
