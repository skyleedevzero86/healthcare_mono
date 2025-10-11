/**
 * 전자정부프레임워크으 기본 파일 관리 시스템을 ajax로 형태로 구현 할 수 있도록 수정 처리함.
 *
 * **/

	/** 첨부파일 select start **/

	function selectFileInfsForUpdateJson(atchmnflFk) {
	  	var url = "/cmm/fms/selectFileInfsForUpdateJson.do";
	  	var resultData = null ;
		$.ajax({
			type : "POST" ,
			url : url ,
			dataType : 'json' ,
			async: false,
			data : {atchmnflId:atchmnflFk},
			beforeSend : function(xhr, opts) {
				 $('#loading').show();
			},
			success : function ( data ) {
				if(data.resultData != null){
					resultData = data.resultData ;
				}
			},
			complete:function(){
				$('#loading').hide();
			},
			error: function (xhr, status, error) {
				fn_modalAlert("처리 중 오류가 발생하였습니다.");
			}
		});
		return resultData ;
    }

	/** 첨부파일 select end **/

	function fn_egov_file_draw(parentsId, atchmnflFk, maxFileNum,selCode, delChk, flag){
		var fileData = null ;

		$("#"+parentsId).empty();
		fileData = selectFileInfsForUpdateJson(atchmnflFk);

		if(fileData.fileList.length>0){
			var html	 =	"" ;

			html	+=	"<div class=\"file_input_list\">" ;
			html	+=	"<div id=\"egov_file_view_table\" class=\"flie_input_list\" >" ;
			html	+=	"<ul> " ;

			$.each(fileData.fileList, function (index, data){
				html	+=	"<li id=\"egov_file_view_table_tr_"+(parseInt(index)+1)+"\">" ;
				html	+=	"<div class=\"fileicon\"></div>";
				var fileSeNm	=	"" ;
				if(wavus.util.isEmpty(data.fileSeNm) == false){
					fileSeNm = "<font color=\"red\">[ " + data.fileSeNm + " ]</font> "
				}
				html	+=	"<a href=\"javascript:fn_egov_downFileJson('"+data.atchmnflId+"','"+data.fileSn+"')\">"+fileSeNm+data.orignlFileNm+"&nbsp;["+data.fileMg+"&nbsp;byte]</a>" ;
				if(delChk == "Y")
				{
					html	+=	"<button type=\"button\" name=\"fileNm\" class=\"delbtn\" onClick=\"javascript:fn_egov_deleteFileJson('"+data.atchmnflId+"','"+data.fileSn+"','"+parentsId+"','"+maxFileNum+"','"+selCode+"', '"+delChk+"');\">삭제</button> "  ;
				}
				else
				{
					html	+=	""  ;
				}

				html	+=	"</li>" ;

			});


			html	+=	"</ul> " ;
			html	+=	"</div>"  ;
			html	+=	"</div>"  ;

			$("#"+parentsId).append(html);
		}

		var html2  = "<div class=\"file_input\">" ;
		    html2 += "<select id=\"fileSeCd"+ flag +"\" style=\"margin-right: 20px;\">" ;
		    html2 += "</select>" ;
		 	html2 += "<label id=\"fileLabel\" style=\"background:#c7c7c7;\">파일찾기 <input type=\"file\" name=\"file_0\" id=\"egovComFileUploader\" disabled > </label>" ;
		 	html2 += "</div>" ;


		 	html2 += "<div id=\"egovComFileList\" class=\"file_input_list\">" ;
		 	html2 += "<ul id=\"egovComFileListUl\">" ;
		 	html2 += "</ul>" ;
		 	html2 += "</div>" ;

		$("#"+parentsId).append(html2);
		var tmp = "";
		fnCreSelectBoxThis($("#"+parentsId).find("#fileSeCd"+flag+"")[0],"COM",selCode,tmp,"선택");
		fn_multiSelect_init( maxFileNum, parentsId, flag);
	}

	function fn_multiSelect_init (maxFileNum, parentsId, flag){
		debugger;
		var multi_selector = new MultiSelector( $("#"+parentsId).find("#egovComFileList")[0], maxFileNum, $("#"+parentsId).find("#fileSeCd"+flag+"")[0], flag );
		multi_selector.addElement( $("#"+parentsId).find("#egovComFileUploader")[0]  );
	}

	function fn_egov_downFileJson(atchmnflId, fileSn){
		window.open("/cmm/fms/FileDown.do?atchmnflId="+atchmnflId+"&fileSn="+fileSn+"");
	}

	function fn_egov_deleteFileJson(atchmnflId, fileSn, parentsId, maxFileNum, selCode, delChk) {
		/**
		 * ajax로 화면 삭제후 첨부파일 부분은 다시 그리도록 한다.
		 * **/

	  	var url = "/cmm/fms/deleteFileInfsJson.do";
		$.ajax({
			type : "POST" ,
			url : url ,
			dataType : 'json' ,
			async: false,
			data : {fileSn:fileSn, atchmnflId:atchmnflId},
			success : function ( data ) {
				if(data.resultData.resultTrue == "1"){
					fn_modalAlert("삭제 되었습니다");
				}else{
					fn_modalAlert("처리 중 오류가 발생하였습니다.");
				}
				fn_egov_file_draw(parentsId,atchmnflId, maxFileNum, selCode, delChk);
			},
			error: function (xhr, status, error) {
				fn_modalAlert("처리 중 오류가 발생하였습니다.");
				fn_egov_file_draw(parentsId,atchmnflId, maxFileNum, selCode, delChk);
			}
		});
	}

	function file_button2(id)
	{
		$("#"+id).val("Y");
	}
