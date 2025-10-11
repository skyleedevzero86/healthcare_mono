window.wavus = window.wavus || {};

wavus.print = (function() {
	
	var _map;
	

	//지도출력(map 객체 출력)
	function otptImage(map) {
		map = wavus.map.getMap();
		if (!map) {
			map = _map;
		}
		var canvas = $(map.getViewport()).children('canvas')[0];
		_drawText(map, canvas);
		canvas.toBlob(function(blob) {
			saveAs(blob, 'map.png');
		});
		map.renderSync();
	}
	
	//지도 이미지 출력
	function printMap(map){
		if (!map) {
			map = _map;
		}
		var canvas = $(map.getViewport()).children('canvas')[0];
		_drawText(map, canvas);
		
		var img = document.createElement('img');
		img.src = canvas.toDataURL();
		$(img).printElement();
		map.renderSync();
	}
	
	//지도 위의 텍스트 그리기
	function _drawText(map, canvas){
		
		_.each(map.getOverlays().getArray(), function(overLay){
			var element = overLay.getElement();
			
			if($(element).hasClass('print')){
				var text = element.innerHTML;
				
				//엘레멘트 크기 고려
				var x = element.offsetParent.offsetLeft + element.offsetParent.offsetWidth/2;
				var y = element.offsetParent.offsetTop + element.offsetParent.offsetHeight;
				var ctx = canvas.getContext("2d");
				
				//오픈레이어즈 폰트
				ctx.font = 'bold 15pt Helvetica Neue';
				ctx.fillStyle = '#514e3d';
				ctx.textAlign= 'center';
				ctx.fillText(text.replace('<sup>2</sup>', '²'), x, y);
				ctx.strokeStyle = '#ffffff';
				ctx.strokeText(text.replace('<sup>2</sup>', '²'), x, y)
			} 
		})
	}
	
	function init(map){
		_map = map;
	}

	var module = {
		printMap : printMap,
		otptImage : otptImage,
		
		init : init
	}

	return module;

})();