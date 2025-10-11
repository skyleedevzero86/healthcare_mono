/**
 * 사용자 정보 및 사용자 세션 관련 정보들을 나두는 곳
 */

window.wavus = window.wavus || {};

wavus.user = (function() {
	var _sessionUrl = $(location).attr('protocol')+'//'+$(location).attr('host')+'/sssn/session.do';
	
	var errStatus = "";
	var userInfo;
	
	function getUserName(){
		return userInfo.name;
	}
	
	function getUserId(){
		return userInfo.id;
	}
	
	function getUserDeptId(){
		return userInfo.deptId;
	}
	
	function getUserDeptNm(){
		return userInfo.deptNm;
	}
	
	function getUserRoleList(){
		return userInfo.roleList;
	}
	
	function getUserForno(){
		return userInfo.forno;
	}

	function getUserTpcd(){
		return userInfo.userTpcd;
	}
	
	function getErrStatus(){
		return errStatus;
	}
	
	
	/**
	 * 사용자가 해당 롤을 가지고 있는지 확인 하는 메소드
	 * roleId : 롤명
	 * 
	 * return : 해당 롤이 있으면 true 없으면 false
	 */
	function isUserRoleCheck(roleId){
		var roleName = roleId.toUpperCase();
		
		return _.contains(userInfo.roleList, roleName);
	}
	
	/**
	 * 사용자가 관리자인지 체크
	 */
//	function isAdmin(){
//		var adminFornoList = ["2000000001", "6000005108", "2000000220", "2000010211", "2000000220"];
		
//		return _.contains(adminFornoList, getUserForno());
//		return isUserRoleCheck('ROLE_ADMIN');
//	}
	
	
	function init() {		
		var deferred = $.Deferred();
		
		wavus.util.ajax({
			url : _sessionUrl,
			dataType : "json",
			type : 'POST'
		}).then(function (result){
			userInfo = result;
			
			deferred.resolve();
		}).fail(function (xhr, status, thrown){
			errStatus = status;
			
			deferred.resolve();
		});
		
		return deferred;
	}
	
	var module = {
		init : init,

		getUserInfo : function(){return userInfo},
		
		getUserName : getUserName,
		getUserId : getUserId,
		getUserDeptId : getUserDeptId,
		getUserDeptNm : getUserDeptNm,
		getUserRoleList : getUserRoleList,
		isUserRoleCheck : isUserRoleCheck,
		getUserForno : getUserForno,
		getUserTpcd : getUserTpcd,
		getErrStatus : getErrStatus,
		isAdmin : isAdmin
	};
	
	return module;
})();