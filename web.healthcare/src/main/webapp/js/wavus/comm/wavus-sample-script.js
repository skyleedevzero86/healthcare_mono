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

function selectFeature(){
	var featureClick = wavus.select.addSelect('click', false);
	featureClick.then(null, null, function(feature){
		console.log('선택된 벡터');
		console.log(feature);
	});
}






