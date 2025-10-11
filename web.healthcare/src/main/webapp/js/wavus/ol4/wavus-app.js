
window.wavus = window.wavus || {};

wavus.app = (function() {

	function init(mapDomId) {
		var deferred = $.Deferred();

		//사용자 정보 셋팅
//		wavus.user.init().then(function(){
			//환경 정보 셋팅
//			wavus.config.init().then(function() {
				wavus.map.setMap(mapDomId).then(function(map) {
					wavus.layer.init(map).then(function(drawLayer, highlightLayer, graphicLayer, boundLayer){
						wavus.highlight.init(map, highlightLayer);
						wavus.graphic.init(map, graphicLayer);
						wavus.draw.init(map, drawLayer);
						wavus.bound.init(map, boundLayer);
					});
					wavus.measure.init(map);
//					wavus.print.init(map);
					wavus.select.init(map);
					wavus.swipe.init(map);
					wavus.tooltip.init(map);
					wavus.write.init(map);				
					wavus.controller.init(map);
					wavus.legend.init(map);
					deferred.resolve();
				});
//		});
		return deferred;
	}

	
	var module = {
		init : init
	};
	return module;
	
})();

