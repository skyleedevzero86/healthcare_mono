window.wavus = window.wavus || {};

wavus.shp = (function() {
	var gDivId = "";
	
	/**
	 * SHP 업로드 버튼 클릭 이벤트
	 */
	function shpUpload(divId) {
		gDivId = divId;
		
		var input = "<input type=\"file\" id=\"uploadShpFile\">";
		$("#" + divId).html(input);
		
		var inputObj = $("#uploadShpFile");
		inputObj.trigger("click");
		
		var deferred = $.Deferred();
		inputObj.change(function() {
			fileChange().then(function(json) {
				deferred.resolve(json);
			});
		});
		
		return deferred;
	}
	
	/**
	 * 파일 선택 체인지 이벤트
	 */
	function fileChange() {
		var deferred = $.Deferred();
		var obj      = $("#" + gDivId + " #uploadShpFile");
		
		var formData = new FormData();
		if(obj[0] != null) formData.append("uploadShpFile", obj[0].files[0]);
		
		$(".black_bg_layer").show();
		
		
		$.ajax({
			type        : "post",
			url         : "/spceInfo/geom/shpUpload.do",
			processData : false,
			contentType : false,
			data        : formData,
			success     : function(json){
				
				deferred.resolve(json);
				
				$(".black_bg_layer").hide();
			},
			error: function (xhr, status, error) {
				$(".black_bg_layer").hide();
			}
		});
		
		return deferred;
	}
	
	var module = {
			shpUpload  : shpUpload,
			fileChange : fileChange
	};
	
	return module;
})();