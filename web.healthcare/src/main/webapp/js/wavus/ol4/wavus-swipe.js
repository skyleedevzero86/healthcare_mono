window.wavus = window.wavus || {};

wavus.swipe = (function() {
	var _map;
	
	var swipe;
	var subLayer;
	var status;
	
	//스와이프 켜기
	function onSwipe(subLayer){
		status = 'on';
		this.subLayer = subLayer;
		_makeSwipeElement();
		this.subLayer.on('precompose', _preHandler);
		this.subLayer.on('postcompose', _postHandler);
		_map.render();
	}
	
	//스와이프 끄기
	function offSwipe(){
		status = 'off';
		$(swipe).remove();
		if(subLayer){
			subLayer.un('precompose', _preHandler);
			subLayer.un('postcompose', _postHandler)
		}
		this.subLayer = null;
		_map.render();
	}
	
	//swipe 객체 생성
	function _makeSwipeElement(){
		swipe = document.createElement('input');
		swipe.setAttribute('type', 'range');
		swipe.setAttribute('style', 'width: 100%');
		
		$(_map.getTarget()).after($(swipe));
		
		$(swipe).on('change',function(){
			_map.render(); 
		})
	}
	
	// 전처리 핸들러
	var _preHandler = function(evt){
		var ctx = evt.context;
		var width;
		if(status == 'on'){
			width = ctx.canvas.width * (swipe.value / 100);
		}else if(status == 'off'){
			width = 0;
		}
		
		ctx.save();
        ctx.beginPath();
        ctx.rect(width, 0, ctx.canvas.width - width, ctx.canvas.height);
        ctx.clip();
	}
	
	//post 핸들러
	var _postHandler = function(evt){
		var ctx = evt.context;
        ctx.restore();
	}
	
	function init(map){
		_map = map;
	}
	
	
	var module ={
			onSwipe : onSwipe,
			offSwipe : offSwipe,
			
			init : init
	}
	
	return module;
})();