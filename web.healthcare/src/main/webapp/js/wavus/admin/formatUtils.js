var formatUtils = {
	//파라미터에 3자리마다 콤마를 붙여서 반환한다. 
	objToComma : function(obj){
		if(obj && obj != null){
			return ax5.util.number(obj,{money:true});
		}else{
			return obj;
		}
	}
	//파라미터를 YYYY-MM-DD 형태로 반환한다.
	,addMinusChar : function(obj){
		if(obj && obj != null && obj.length === 8){
			return ax5.util.date(obj,{'return' : "yyyy-MM-dd"});
		}else if(obj && obj != null && obj.length === 6){
			return ax5.util.date(obj,{'return' : "yyyy-MM"});
		}else{
			return obj;
		}
	}
	//파라미터를 사업자번호 형태로 반환한다. xxx-xx-xxxxx
	,addMinusCompNumber : function(obj){
		if(obj && obj != null){
			var replaceStr=obj.replace(/[^0-9]/g,'')
			if(replaceStr != null && replaceStr !== ''){
				if(replaceStr.length === 10){
					return replaceStr.substring(0,3)+'-'+replaceStr.substring(3,5)+'-'+replaceStr.substring(5,10);
				}else 
					return obj;
			}else{
				return obj;
			}
		}else{
			return obj;
		}
	}
	//파라미터를 전화번호 형태로 반환한다.
	,addPhoneChar : function(obj){
		if(obj && obj != null){
			obj = obj.replace(/\D/g, "");
			var regExpPattern = /^([0-9]{3})\-?([0-9]{1,4})?\-?([0-9]{1,4})?\-?([0-9]{1,4})?\-?([0-9]{1,4})?/;
            if (obj.substr(0, 2) == "02") {
            	regExpPattern = /^([0-9]{2})\-?([0-9]{1,3})?\-?([0-9]{1,4})?\-?([0-9]{1,4})?\-?([0-9]{1,4})?/;
                if(obj.length > 9){
                    regExpPattern = /^([0-9]{2})\-?([0-9]{1,4})?\-?([0-9]{1,4})?\-?([0-9]{1,4})?\-?([0-9]{1,4})?/;
                }
            }else{
            	if(obj.length == 10){
            		regExpPattern = /^([0-9]{3})\-?([0-9]{1,3})?\-?([0-9]{1,4})?\-?([0-9]{1,4})?\-?([0-9]{1,4})?/;
            	}
            }			
			var returnValue = obj.replace(regExpPattern, function (a, b) {
				var nval = [arguments[1]];
				if (arguments[2]) nval.push(arguments[2]);
				if (arguments[3]) nval.push(arguments[3]);
				if (arguments[4]) nval.push(arguments[4]);
				if (arguments[5]) nval.push(arguments[5]);
				return nval.join("-");
			});
			return returnValue;
		}else{
			return obj;
		}
	}
	,addIPaddress : function(obj){
		if(obj && obj != null){
		var val = obj.replace(/\D/g, "");
		var regExpPattern = /^([1-9]?[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])(\.([1-9]?[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])){3}$/;
		var returnValue = val.replace(regExpPattern, function (a, b) {
            var nval = [arguments[1]];
            if (arguments[2]) nval.push(arguments[2]);
            if (arguments[3]) nval.push(arguments[3]);
            if (arguments[4]) nval.push(arguments[4]);
            return nval.join(".");
        });
		return returnValue;
		}else{
			return obj;
		}
	},addZipCode : function(obj){
		if(obj && obj != null){
			var replaceStr=obj.replace(/[^0-9]/g,'')
			if(replaceStr != null && replaceStr !== ''){
				if(replaceStr.length === 6){
					return replaceStr.substring(0,3)+'-'+replaceStr.substring(3,6);
				}else 				
					return obj;
			}else{
				return obj;
			}
		}else{
			return obj;
		}
	}
}
