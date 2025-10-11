/**
 * util 함수정의
 *
 * 간단히 자주 사용되는 함수를 여기에 정의합니다.
 *
 * 문자열, 날짜, 숫자형태 등..
 */

window.wavus = window.wavus || {};

wavus.util = (function() {

	var defaultDateFormat = 'yymmdd';

	function commify(n){
		var reg = /(^[+-]?\d+)(\d{3})/;
		n += '';
		while (reg.test(n))
			n = n.replace(reg, '$1' + ',' + '$2');
		return n;
	}

	function removeCommas(str) {
	    return(str.replace(/,/g,''));
	}

	// http://stackoverflow.com/questions/6660977/convert-hyphens-to-camel-case-camelcase
	var camelCase = (function () {
	    var DEFAULT_REGEX = /[-_]+(.)?/g;

	    function toUpper(match, group1) {
	        return group1 ? group1.toUpperCase() : '';
	    }
	    return function (str, delimiters) {
	        return str.replace(delimiters ? new RegExp('[' + delimiters + ']+(.)?', 'g') : DEFAULT_REGEX, toUpper);
	    };
	})();

	function formatDate(format, date) {
		date = date || new Date();
		format = format || defaultDateFormat;

		return $.datepicker.formatDate(format, date);
	}

	function timestamp() {
		var d = new Date();
		return formatDate()
			 + right('0' + d.getHours(), 2)
			 + right('0' + d.getMinutes(), 2)
			 + right('0' + d.getSeconds(), 2);
	}

	function left(str, n) {
		if (n <= 0)
			return "";
		else if (isEmpty(str))
			return "";
		else if (n > String(str).length)
			return str;
		else
			return String(str).substring(0, n);
	}


	function leftComma(str, n) {
		if (n <= 0)
			return "";
		else if (isEmpty(str))
			return "";
		else if (n >= String(str).length)
			return str;
		else
			return String(str).substring(0, n)+"...";
	}

	function right(str, n){
		if (n <= 0)
			return "";
		else if (isEmpty(str))
			return "";
		else if (n > String(str).length)
			return str;
		else {
			var iLen = String(str).length;
			return String(str).substring(iLen, iLen - n);
		}
	}

	function isNumber(n) {
		$.isNumeric(n)
//		if (n) {
//			return !!!_.isNaN(Number(n));
//		}
//
//		return false;
	}

	/**
	 * param.url
	 * param.data
	 * param.async
	 * param.dataType
	 * param.beforeSend
	 *
	 */
	function ajax(param) {
		return $.when(
			$.ajax({
				type : param.type || "POST",
				async : param.async === false ? false : true,
				contentType : param.contentType || "application/x-www-form-urlencoded;charset=utf-8",
				url : param.url,
				dataType : param.dataType || "json",
				cache : param.cache,
				processData : param.processData,
				beforeSend : function(request){
					request.setRequestHeader("mndAJAX", "true");
					if(param.beforeSend){
						param.beforeSend(request);
					}
				},
				data : param.data,
				error : param.error,
				success : param.success
			})
		);
	}

	/**
	 * post 는 data(json object)를 문자열로 변환하여야 한다.
	 */
	function jsonPost(param) {
		param.type = 'POST';
		param.contentType = 'application/json';
		param.data = JSON.stringify(param.data);

		return ajax(param);
	}

	/**
	 * get 은 data를 변환없이 전달한다.
	 */
	function jsonGet(param) {
		param.type = 'GET';
		param.contentType = 'application/json';
		param.data = param.data;

		return ajax(param);
	}


	/**
	 * 변수의 널값 체크
	 */
	function isEmpty(value) {
		if (value == "" || value == null || value == undefined || value =="null" || (value != null && typeof value == "object" && !Object.keys(value).length)) {
			return true
		} else {
			return false
		}
	}

	/**
	 * 공백을 0으로 체우는 메소드
	 */
	function prependZero(value, len) {
		while (value.toString().length < len) {
			value = "0" + value
		}
		return value;
	}

    /**
     * 천단위 콤마
     */
    function comma(x) {
    	x = replaceNull(x);
    	var parts = x.toString().split(".");
        return parts[0].toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") + (parts[1] ? "." + parts[1] : "");
    }

    function uncomma(x) {
        return x.replace(/[^\d]+/g, '');
    }

    function replaceNull(value){
		if (value == "null" || value == null || value == undefined ) {
				return "" ;
		} else {
			return value ;
		}
    }

	/**
	 * url(get 방식) 파라미터를 json 으로 변환 하는 메소드
	 */
	function QueryStringToJSON(url) {
		var urls = url.split('?');
		var str = urls[1];
	    var pairs = str.split('&');
	    var result = {};
	    pairs.forEach(function (pair) {
	        pair = pair.split('=');
            var name = pair[0];
            var value = pair[1];
	        if (name.length)
	            if (result[name] !== undefined) {
	                if (!result[name].push) {
	                    result[name] = [result[name]];
	                }
	                result[name].push(value || '');
	            } else {
	                result[name] = value || '';
	            }
	    });
	    return (result);
	}

	/**
	 * 문자열에서 해당 문자 모두 변환
	 */
	function replaceAll(str, searchStr, replaceStr) {
	    return str.split(searchStr).join(replaceStr);
	}

	function addMonth(sYYYYMM, n) {
		var date, year, month, day;

		if (sYYYYMM.length == 6) date = new Date(sYYYYMM.substring(0, 4), sYYYYMM.substring(4, 6), 1);
		else date = new Date();

		date.setMonth(date.getMonth() + n);

		if (date.getMonth() == "0") {
			year = date.getFullYear() - parseInt(n/12) - 1;
		} else year = date.getFullYear();

		month = (date.getMonth() < 10 ? "0" + date.getMonth() : date.getMonth());
		month = (month == "00" ? "12" : month);

		return year.toString() + month;
	}

	/**
	 * element 영역에 progressBar 추가
	 */
	function showLoading(element, position) {
		element.isLoading({
			text: "불러오는 중..",
			position: position || "overlay",
			disableOthers: []
		});
	}

	/**
	 * element 영역에 progressBar 제거
	 */
	function hideLoading(element) {
		element.isLoading("hide");
	}

	function compareStrNum(a, b) {
		return a - b;
	}

	function init() {
		var deferred = $.Deferred();

		// TODO 초기 설정작업을 여기서 한다.
		deferred.resolve();

		return deferred;
	}

	/**
	 * 입력키 제어 (한글, 숫자 등)
	 */
	function inputKey(gbn, obj) {

		if(gbn == '1'){ //숫자만
			obj.value = obj.value.replace(/[^0-9]/g,'');
		}

		if(gbn == '2'){ //한글만
			obj.value = obj.value.replace(/[^ㄱ-ㅎ|ㅏ-ㅣ|가-힣|\s]/g,'');
		}

		if(gbn == '3'){ //한글입력 불가능
			obj.value = obj.value.replace(/[ㄱ-ㅎ|ㅏ-ㅣ|가-힣|\s]/g,'');
		}

		if(gbn == '4'){ //숫자만(소수점 포함)
			obj.value = obj.value.replace(/[^0-9.]/g,'');
		}
	}

	/**
	 * 이메일 체크
	 */
	function emailCheck(email) {

	    var regex=/([\w-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([\w-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/;
	    return (email != '' && email != 'undefined' && regex.test(email));
	}

	var module = {
		init : init,

		commify : commify,
		removeCommas : removeCommas,

		camelCase : camelCase,
		formatDate : formatDate,

		left : left,
		leftComma : leftComma,
		right : right,

		isNumber: isNumber,

		ajax : ajax,

		jsonPost: jsonPost,
		jsonGet: jsonGet,

		timestamp : timestamp,

		isEmpty : isEmpty,
		prependZero : prependZero,

		QueryStringToJSON : QueryStringToJSON,

        replaceAll: replaceAll,
        replaceNull: replaceNull,

        addMonth: addMonth,

        comma: comma,
		uncomma: uncomma,

		showLoading: showLoading,
		hideLoading: hideLoading,

		compareStrNum: compareStrNum,

		inputKey: inputKey,
		emailCheck: emailCheck
	};

	return module;

})();
