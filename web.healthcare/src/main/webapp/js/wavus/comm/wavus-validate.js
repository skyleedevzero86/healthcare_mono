/**
 * validate 함수정의
 * 
 */

window.wavus = window.wavus || {};

wavus.validate = (function() {
	
	/**
	 * validate 초기화
	 * e는 element를 포함하고있는 <form>태그를 지정(필수)
	 */
	function init(e) {
		$(e).validate({
			onkeyup:false,
			onclick:false,
			onfocusout:false,
			onsubmit: false,
			showErrors:function(errorMap, errorList) {
 				if(this.numberOfInvalids()) {
 					alert(errorList[0].message);
 					$(errorList[0].element).focus();
 				}
 			}
		});
	}
	
	/**
	 * validate rule 추가
	 * e는 element[name=simple] 형태로 지정(필수)
	 */
	function addRules (e, rule) {
		e.rules('add', rule);
	}  
	
	var module = {
		init : init,
		addRules : addRules
	};
	
	return module;
	
})();
