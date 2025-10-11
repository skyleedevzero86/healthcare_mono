function getAddr(){
	// 적용예 (api 호출 전에 검색어 체크)
	if (!checkSearchedWord(document.form.keyword)) {
		return ;
	}

	$.ajax({
		 url :"http://www.juso.go.kr/addrlink/addrLinkApiJsonp.do"  //인터넷망		 
		 //url :"/biz/si/main/getJusoApiProxy.do?url=http://www.juso.go.kr/addrlink/addrLinkApiJsonp.do"  // 프록시
		,type:"post"
		,data:$("#form").serialize()
//		,dataType:"json"
		,dataType:"jsonp"
		,crossDomain:true
		,success:function(jsonStr){
			$("#jusoUlList").empty();
			var errCode = jsonStr.results.common.errorCode;
			var errDesc = jsonStr.results.common.errorMessage;
			if(errCode != "0"){
				alert(errCode+"="+errDesc);
			}else{
				if(jsonStr != null){
					makeListJson(jsonStr);
					$("#pageNumer").empty();
					pageMake(jsonStr);
				}
			}
		}
	    ,error: function(xhr,status, error){
	    	$("#popup_alert").PopupWindow("open");
			document.querySelector("#popup_alert_msg").innerText = "에러발생";

			$("#alertChk").off("click").on("click",function(){
				$("#popup_alert").PopupWindow("close");
			});

	    }
	});
}

function fn_selectJuso(rnMgtSn,rn,bdNm,jibunAddr,roadAddr, lnbrMnnm,lnbrSlno, mtYn,buldMnnm,buldSlno, zipNo){
	 //보여주기
    /* $("#applcntAdresEmpValue").val(roadAddr)		;*/

	 $("#applcntAdresSeCdEmpValue").val("003")		   									;
	 $("#applcntLegaldongCdEmpValue").val(rnMgtSn.substring(0,10) )		   			;
	 $("#applcntRnCdEmpValue").val(rnMgtSn)		   									;
	 $("#applcntCtprvnCdEmpValue").val(rnMgtSn.substring(0,2))		   					;
	 $("#applcntSignguCdEmpValue").val(rnMgtSn.substring(2,5))		   					;
	 $("#applcntEmdCdEmpValue").val(rnMgtSn.substring(5,8))		   					;
	 $("#applcntRiCdEmpValue").val(rnMgtSn.substring(8,10))		   					;
	 $("#applcntRnEmpValue").val(rn)		   											;
	 $("#applcntBuldNmEmpValue").val(bdNm)		   											;
	 $("#applcntLnmAdresEmpValue").val(jibunAddr)		   				;
	 $("#applcntRnAdresEmpValue").val(roadAddr)		   				;

	 $("#applcntLnmMnnmEmpValue").val(lnbrMnnm)		   				;
	 $("#applcntLnmSlnoEmpValue").val(lnbrSlno)		   				;
	 $("#applcntMntnAtEmpValue").val(mtYn)		   					;
	 $("#applcntBuldMnnmEmpValue").val(buldMnnm)		   				;
	 $("#applcntBuldSlnoEmpValue").val(buldSlno)		   				;

	 //도로명주소 란에 표시
	 $("#applcntAdresEmpValue").val("");
	 $("#applcntAdresEmpValue").val(roadAddr);

	 //상세주소 입력 받기
	 $("#applcntRnAdresEmpDetailEmpValue").val("");
	 
	//우편번호 입력 받기
	 $("#applcntzipNo").val("");
	 $("#applcntzipNo").val(zipNo);
	 
	 $("#searchJusoList").hide();

}

function fn_setApplcntRnAdresEmpDetail() {
	//팝업에 보여주는 데이터 저장
/*var applcntRnAdres = $("#applcntRnAdresEmpValue").val();*/


	var applcntAdresSeCdEmpValue = $("#applcntAdresSeCdEmpValue").val();
	var applcntLegaldongCdEmpValue = $("#applcntLegaldongCdEmpValue").val();
	var applcntRnCdEmpValue= $("#applcntRnCdEmpValue").val();
	var applcntCtprvnCdEmpValue = $("#applcntCtprvnCdEmpValue").val();
	var applcntSignguCdEmpValue = $("#applcntSignguCdEmpValue").val();
	var applcntEmdCdEmpValue = $("#applcntEmdCdEmpValue").val();
	var applcntRiCdEmpValue = $("#applcntRiCdEmpValue").val();
	var applcntRnEmpValue = $("#applcntRnEmpValue").val();
	var applcntBuldNmEmpValue = $("#applcntBuldNmEmpValue").val();
	var applcntLnmAdresEmpValue = $("#applcntLnmAdresEmpValue").val();
	var applcntRnAdresEmpValue = $("#applcntRnAdresEmpValue").val();
	var applcntRnAdresDetail = $("#applcntRnAdresEmpDetailEmpValue").val();
	var applcntLnmMnnmEmpValue = $("#applcntLnmMnnmEmpValue").val();
	var applcntLnmSlnoEmpValue = $("#applcntLnmSlnoEmpValue").val();
	var applcntMntnAtEmpValue = $("#applcntMntnAtEmpValue").val();
	var applcntBuldMnnmEmpValue = $("#applcntBuldMnnmEmpValue").val();
	var applcntBuldSlnoEmpValue = $("#applcntBuldSlnoEmpValue").val();

	var applcntAdresEmpValue = $("#applcntAdresEmpValue").val();
	
	var zipNo = $("#applcntzipNo").val();

	var formId = $("#selFormId").val();

     //$("#"+formId).find("#applcntAdres").val(applcntAdresEmpValue);			//주소
     //$("#"+formId).find("#applcntRnAdresDetail").val(applcntRnAdresDetail);			//상세주소
     
     $("#"+formId).find("#addr").val(applcntAdresEmpValue);			//주소
     $("#"+formId).find("#addrDetail").val(applcntRnAdresDetail);			//상세주소
     $("#"+formId).find("#zip").val(zipNo);				//우편번호

     $("#"+formId).find("#applcntAdresSeCd").val(applcntAdresSeCdEmpValue);
	 $("#"+formId).find("#applcntLegaldongCd").val(applcntLegaldongCdEmpValue);
	 $("#"+formId).find("#applcntRnCd").val(applcntCtprvnCdEmpValue);
	 $("#"+formId).find("#applcntCtprvnCd").val(applcntCtprvnCdEmpValue);
	 $("#"+formId).find("#applcntSignguCd").val(applcntSignguCdEmpValue);
	 $("#"+formId).find("#applcntEmdCd").val(applcntEmdCdEmpValue);
	 $("#"+formId).find("#applcntRiCd").val(applcntRiCdEmpValue);
	 $("#"+formId).find("#applcntRn").val(applcntRnEmpValue);
	 $("#"+formId).find("#applcntBuldNm").val(applcntBuldNmEmpValue);
	 $("#"+formId).find("#applcntLnmAdres").val(applcntLnmAdresEmpValue);

	 $("#"+formId).find("#applcntRnAdres").val(applcntRnAdresEmpValue);

	 $("#"+formId).find("#applcntCstmrInputDetailAdres").val(applcntRnAdresDetail);
	 $("#"+formId).find("#applcntLnmMnnm").val(applcntLnmMnnmEmpValue);
	 $("#"+formId).find("#applcntLnmSlno").val(applcntLnmSlnoEmpValue);
	 $("#"+formId).find("#applcntMntnAt").val(applcntMntnAtEmpValue);
	 $("#"+formId).find("#applcntBuldMnnm").val(applcntBuldMnnmEmpValue);
	 $("#"+formId).find("#applcntBuldSlno").val(applcntBuldSlnoEmpValue);
	 
	 
	 
	
	$("#zip_popup").PopupWindow("close");
	/*$("#"+formId).find("#applcntAdres").empty();*/
}


/*
function makeListJson(jsonStr){
	var htmlStr = "";
	$(jsonStr.results.juso).each(function(index, item){
		htmlStr += "<li onClick=\"javscript:fn_selectJuso('"+item.rnMgtSn+"','"+item.rn+"','"+item.bdNm+"','"+item.jibunAddr+"','"+item.roadAddr+"','"+item.lnbrMnnm+"','"+item.lnbrSlno+"','"+item.mtYn+"','"+item.buldMnnm+"','"+item.buldSlno+"','"+item.zipNo+"');\" >";
		htmlStr += "<div class=\"tx2\">";
		htmlStr += "<em>"+this.zipNo+"</em>";
		htmlStr += "</div>";
		htmlStr += "<div class=\"adrlist\">";
		htmlStr += "<div class=\"tx3\">도로명</div>";
		htmlStr += this.roadAddr;
		htmlStr += "</div>";
		htmlStr += "<div class=\"adrlist\">";
		htmlStr += "<div class=\"tx3\">지번</div>";
		htmlStr += this.jibunAddr;
		htmlStr += "</div>";
		htmlStr += "</li>";
	});
	$("#searchJusoList").show();
	$("#jusoUlList").append(htmlStr);
}*/


function makeListJson(jsonStr){
	var htmlStr = "";
	$(jsonStr.results.juso).each(function(index, item){
		htmlStr += "<div onClick=\"javscript:fn_selectJuso('"+item.rnMgtSn+"','"+item.rn+"','"+item.bdNm+"','"+item.jibunAddr+"','"+item.roadAddr+"','"+item.lnbrMnnm+"','"+item.lnbrSlno+"','"+item.mtYn+"','"+item.buldMnnm+"','"+item.buldSlno+"','"+item.zipNo+"');\" >";	
		htmlStr += "<div style=\"border:1px solid; line-height: 1.5\">";
		htmlStr += "<tr>";
		htmlStr += "<th>"+this.zipNo+"</th>";
		htmlStr += "<th>도로명 주소 </th>";
		htmlStr += "<th>";
		htmlStr += this.roadAddr;
		htmlStr += "</th>";		
		htmlStr += "</div>";
		htmlStr += "</tr>";
		htmlStr += "</div>";
	});
	$("#searchJusoList").show();
	$("#jusoUlList").append(htmlStr);
}

/*
function makeListJson(jsonStr){
	var htmlStr = "";
	htmlStr += "<table>";
	htmlStr += "<tr>";
	htmlStr += "<td>우편번호</td>";
	htmlStr += "<td>도로명 주소</td>";
	htmlStr += "<td>지번주소</td>";
	htmlStr += "</tr>";
	$(jsonStr.results.juso).each(function(index, item){		
		htmlStr += "<tr onClick=\"javscript:fn_selectJuso('"+item.rnMgtSn+"','"+item.rn+"','"+item.bdNm+"','"+item.jibunAddr+"','"+item.roadAddr+"','"+item.lnbrMnnm+"','"+item.lnbrSlno+"','"+item.mtYn+"','"+item.buldMnnm+"','"+item.buldSlno+"','"+item.zipNo+"');\" >";
		htmlStr +=
		//htmlStr += "<li onClick=\"javscript:fn_selectJuso('"+item.rnMgtSn+"','"+item.rn+"','"+item.bdNm+"','"+item.jibunAddr+"','"+item.roadAddr+"','"+item.lnbrMnnm+"','"+item.lnbrSlno+"','"+item.mtYn+"','"+item.buldMnnm+"','"+item.buldSlno+"','"+item.zipNo+"');\" >";
		htmlStr += "<td>"+this.zipNo+"</td>";
		htmlStr += "<td>"+this.roadAddr+"</td>";		
		htmlStr += "<td>"+this.jibunAddr+"</td>";		
		htmlStr += "</tr>";
	});
	htmlStr += "</table>";
	$("#searchJusoList").show();
	$("#jusoUlList").append(htmlStr);
}
*/

//도로명주소 API xml 타입 페이지 처리
function pageMake(jsonStr){
	var total = jsonStr.results.common.totalCount; // 총건수
	var pageNum = document.form.currentPage.value;// 현재페이지
	var paggingStr = "";
	if(total < 1){
		console.log(total);
	}else{
		var PAGEBLOCK=document.form.countPerPage.value;
		var pageSize=document.form.countPerPage.value;
		var totalPages = Math.floor((total-1)/pageSize) + 1;
		var firstPage = Math.floor((pageNum-1)/PAGEBLOCK) * PAGEBLOCK + 1;
		if( firstPage <= 0 ) firstPage = 1;
		var lastPage = parseInt(firstPage-1 + parseInt(PAGEBLOCK));
		if( lastPage > totalPages ) lastPage = totalPages;
		var nextPage = lastPage+1 ;
		var prePage = firstPage-1 ;
		if( firstPage > PAGEBLOCK ){
			paggingStr +=  "<a href='javascript:goPage("+1+");'><img src='/images/bbs/al_icon3.png' alt='처음페이지'></a>  " ;
			paggingStr +=  "<a href='javascript:goPage("+prePage+");'><img src='/images/bbs/al_icon2.png' alt='이전 페이지'></a>  " ;
		}
		for( i=firstPage; i<=lastPage; i++ ){
			if( pageNum == i )
				paggingStr += "<a style='font-weight:bold;color:blue;font-size:15px;' href='javascript:goPage("+i+");'>" + i + "</a>  ";
			else
				paggingStr += "<a href='javascript:goPage("+i+");'>" + i + "</a>  ";
		}
		if( lastPage < totalPages ){
			paggingStr +=  "<a href='javascript:goPage("+nextPage+");'><img src='/images/bbs/ar_icon2.png' alt='다음페이지'></a>";
			paggingStr +=  "<a href='javascript:goPage("+totalPages+");'><img src='/images/bbs/ar_icon3.png' alt='마지막페이지'></a>";
		}
		$("#pageNumer").html(paggingStr);
	}
}

//페이지 이동
function goPage(pageNum){
	document.form.currentPage.value=pageNum;
	getAddr();
}

//특수문자, 특정문자열(sql예약어의 앞뒤공백포함) 제거
function checkSearchedWord(obj){
	if(obj.value.length >0){
		//특수문자 제거
		var expText = /[%=><]/ ;
		if(expText.test(obj.value) == true){
			fn_modalAlert("특수문자를 입력 할수 없습니다.") ;
			obj.value = obj.value.split(expText).join("");
			return false;
		}

		//특정문자열(sql예약어의 앞뒤공백포함) 제거
		var sqlArray = new Array(
			//sql 예약어
			"OR", "SELECT", "INSERT", "DELETE", "UPDATE", "CREATE", "DROP", "EXEC",
             		 "UNION",  "FETCH", "DECLARE", "TRUNCATE"
		);

		var regex;
		for(var i=0; i<sqlArray.length; i++){
			regex = new RegExp( sqlArray[i] ,"gi") ;

			if (regex.test(obj.value) ) {
				fn_modalAlert("\"" + sqlArray[i]+"\"와(과) 같은 특정문자로 검색할 수 없습니다.");
				obj.value =obj.value.replace(regex, "");
				return false;
			}
		}
	}
	return true ;
}

function enterSearch() {
	var evt_code = (window.netscape) ? ev.which : event.keyCode;
	if (evt_code == 13) {
		event.keyCode = 0;
		getAddr();
	}
}