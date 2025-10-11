/**
 *   	Javascript Name  : fn_administzoneManage.js
  		Description      : 행정구역 selectbox 제어 js  지역개발현황
 */

	var objSidoList		 ;
	var objSggList	 ;

	//시도 행정경계 리스트 가져오기
	function selSidList(){
		var url = "/biz/si/ls/selectLegaldongList.do";
		var resultList ;
		$.ajax({
			type : "POST" ,
			url : url ,
			dataType : 'json' ,
			data : {reqType:"SID", gubun:"1"},
			async: false,
			success : function ( data ) {
				resultList = data.resultData.resultList; ;
			},
			error: function (xhr, status, error) {
				commonUtils.customAlert("처리 중 오류가 발생하였습니다.");
			}
		});
		return resultList ;
	}

	function selSggList(){
		  var resultList ;
		  var url = "/biz/si/ls/selectLegaldongList.do";
			$.ajax({
				type : "POST" ,
				url : url ,
				dataType : 'json' ,
				data : {reqType:"SGG"},
				async: false,
				success : function ( data ) {
					resultList = data.resultData.resultList; ;
				},
				error: function (xhr, status, error) {
					commonUtils.customAlert("처리 중 오류가 발생하였습니다.");
				}
			});
			return resultList ;
	}



	/**
	 * onload시 시도 시군구 읍면동 데이터 초기화
	 * 시도, 시군구, 읍면동 정보를 전역변수에 담는다.
	 * **/


	function selAdministzoneDataInit(){
		objSidoList 	= selSidList();
		objSggList 		= selSggList();
	}


	/**
	 * 최초 셀렉트 박스를 그린다.
	 * 전달받은 div 아래에 각각의 셀렉트 박스만 그린다.
	 * **/
	function selboxInit(){
		var result = fn_drawCtprvnSel($("#sCtprvnCd").val());
		if(result == true){
			fn_drawSignguSel($("#sCtprvnCd").val(),$("#sSignguCd").val());
		}
	}
	function selboxInit2(ctprvnCd,signguCd){
		fn_drawCtprvnSel(ctprvnCd);
		fn_drawSignguSel(ctprvnCd,signguCd);
	}


	function fn_drawCtprvnSel(selVal){
		var selectBox = "";
		$("#ctprvnSel").empty();

		if(objSidoList.length > 1){
			selectBox += '<option value="">' + '전체'+ '</option>';
		}
		$.each(objSidoList, function (index, value){
			selectBox += '<option value="'+ value.ctprvnCd+ '">' + value.ctprvnNm + '</option>';
		});

		$("#ctprvnSel").append(selectBox);

		if(selVal != ""){
			$("#ctprvnSel").val(selVal).prop("selected", true);
		}
		return true;
	}



	function fn_drawSignguSel(pValue,selVal){
		var selectBox = "";
		$("#signguSel").empty();
		if(objSggList.length > 1){
			selectBox += '<option value="">' + '전체'+ '</option>';
		}
		if(pValue!=""){
			$.each(objSggList, function (index, value){
				if(value.ctprvnCd == pValue) {
					selectBox += '<option value="'+ value.signguCd+ '">' + value.signguNm + '</option>';
				}
			});
		}
		$("#signguSel").append(selectBox);

		if(selVal != ""){
			$("#signguSel").val(selVal).prop("selected", true);
		}

	}



	$(document).ready(function(){
		selAdministzoneDataInit() ;
		$("#ctprvnSel").change(function() {
			fn_drawSignguSel(this.value);
		});
	});


