/**
 * 파일 관련 내용을 처리할 js
 */

window.wavus = window.wavus || {};

/**
 * 필수 import 파일들
 * <link href="<c:url value="/plugin/jQuery.filer/css/jquery.filer.css"/>" rel="stylesheet" type="text/css" />
 * <script type="text/javascript" src="<c:url value="/plugin/jQuery.filer/js/jquery.filer.js"/>"></script>
 * <script type="text/javascript" src="<c:url value="/js/wavus/wavus-util.js"/>"></script>
 *
 *
 *
 */
wavus.file = (function() {

	var optionUrl = {
		upload : '/file/upload.do',

		fileList : '/file/filelist.do',

		download : '/file/download.do',

		fileDeleteList : '/file/deleteFileList.do',
		xxx: 'xxx'
	}

	var _uploadObject = null;

	/**
	 * <input id="filepeke" type="file" name="filepeke[]" multiple />
	 * <input id="atchmnflId" name="atchmnflId" type="hidden">
	 *
	 *
	 * 파일 업로드 객체 생성
	 * domID : "#xxxId" or ".xxxClass"
	 */
	function setUploadObjectId(domId){

		$(domId).filer({
			showThumbs: true,
			addMore: false,
			allowDuplicates: false,
		});

	}

	/**
	 * 파일 갯수 확인
	 * domID : "#xxxId" or ".xxxClass"
	 */
	function getFileCount(domId){
		var filerKit = $(domId).prop('jFiler');
		return filerKit.files_list.length;
	}

	/**
	 * 업데이트 할 파일이 있는지 체크
	 * domID : "#xxxId" or ".xxxClass"
	 */
	function checkUpdateFileCount(domId){
		var filerKit = $(domId).prop('jFiler');

		if(filerKit.files_list.length > 0 || filerKit.revmoeList.length > 0){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * !!!! 업로드시 파일이 없는 경우 에러
	 * !!!! 업로드 전 파일 카운트 체크 하여 필수 값 아닌경우 업로드 수행하지 말것!!!
	 * 파일 업로드
	 * domID : "#xxxId" or ".xxxClass"
	 *
	 *  return deferred
	 */
	function uploadFile(domId, fileControlFrameDomId){
		var fileControlId = domId.replace('#','');

		var deferred = $.Deferred();

		var filerKit = $(domId).prop('jFiler');

		var fileUpDeferred = filerKit.setUploadUrl(optionUrl.upload);

		var uploadCount = 0;

		fileUpDeferred.then(null, null, function(result){
			uploadCount++;
			if(filerKit.files_list.length > uploadCount){
				_uploadFile(filerKit, uploadCount, result);
			}else{
				deferred.resolve(result.atchmnflId);
			}

		});

		_uploadFile(filerKit, uploadCount, {});

		$(domId).remove();
		$(fileControlFrameDomId).html("<input id='"+fileControlId+"' type='file' name='"+fileControlId+"' multiple/>");
		setUploadObjectId(domId);

		return deferred;
	}

	function _uploadFile(filerKit, count, data){
		filerKit.upload(count, data);
	}

	/**
	 * 파일 뷰 설정
	 * domID : "#xxxId" or ".xxxClass"
	 * atchmnflId : atchmnflId (파일 아이디)
	 *  return deferred
	 */
	function setViewObjectId(domId, atchmnflId){

		_getFileList(atchmnflId).then(function(result){
			var files = [];
			for(attr in result.filelist){
				var file = {
					name : result.filelist[attr].orignlFileNm,
					size : result.filelist[attr].fileMg,
					type : result.filelist[attr].fileCn,
					url : optionUrl.download+"?atchmnflId="+result.filelist[attr].atchmnflId+"&fileSn="+result.filelist[attr].fileSn
				};
				files.push(file);
			}

			$(domId).filer({
				showThumbs: true,
				addMore: false,
				allowDuplicates: false,
				files : files
			});
			//입력폼 히든 처리
			$(".jFiler-input").hide();
			$(".jFiler-item-assets").hide();

			$(".jFiler-item").click(function(evt){
//				alert($(this).data('jfilerIndex'));
				var filerKit = $(domId).prop('jFiler');
//				console.log(filerKit);
//				console.log($(this).data('jfilerIndex'));
				_download(filerKit.files[$(this).data('jfilerIndex')].url);
			})
		});


	}


	function _getFileList(atchmnflId){
		//로그인한 사용자만 파일 다운로드가 가능하도록 할 경우
//		return wavus.util.ajax({
//			url : optionUrl.fileList,
//			data : {
//				atchmnflId : atchmnflId
//			},
//			dataType : "json",
//			type : 'POST'
//		});
		//모든 사용자가 파일 다운로드가 가능하도록 할 경우
		return $.ajax({
			url : optionUrl.fileList,
			data : {
				atchmnflId : atchmnflId
			},
			dataType : "json",
			type : 'POST',
			success : function(result){
				console.log(result);
			}
		});
	}

	/**
	 * 파일 편집 설정
	 * domID : "#xxxId" or ".xxxClass"
	 * atchmnflId : atchmnflId (파일 아이디)
	 *  return deferred
	 */
	function setEditObjectId(domId, atchmnflId){
		_getFileList(atchmnflId).then(function(result){
			var files = [];
			for(attr in result.filelist){
				var file = {
					name : result.filelist[attr].orignlFileNm,
					size : result.filelist[attr].fileMg,
					type : result.filelist[attr].fileCn,
					url : optionUrl.download+"?atchmnflId="+result.filelist[attr].atchmnflId+"&fileSn="+result.filelist[attr].fileSn
				};
				files.push(file);
			}

			$(domId).filer({
				showThumbs: true,
				addMore: true,
				allowDuplicates: false,
				files : files
			});

			var filerKit = $(domId).prop('jFiler');
			//기존 파일 ajax 안올라가게 설정
			filerKit.setEditDefaultFile();

		});
	}

	/**
	 * !!!! 업로드시 파일이 없는 경우 에러
	 * !!!! 업로드 전 파일 카운트 체크 하여 필수 값 아닌경우 업로드 수행하지 말것!!!
	 * 파일 업로드
	 * domID : "#xxxId" or ".xxxClass"
	 *
	 *  return deferred
	 */
	function editUploadFile(domId, atchmnflId){
		var deferred = $.Deferred();

		var filerKit = $(domId).prop('jFiler');

		//삭제된 파일들 삭제처리
		_delete(filerKit.revmoeList).then(function(){

			if(filerKit.files_list.length == 0){
				deferred.resolve(atchmnflId);
			}else{
				var fileUpDeferred = filerKit.setUploadUrl(optionUrl.upload);

				var uploadCount = 0;

				fileUpDeferred.then(null, null, function(result){
					uploadCount++;
					if(filerKit.files_list.length > uploadCount){

						if(wavus.util.isEmpty(atchmnflId)){
							_uploadFile(filerKit, uploadCount, {atchmnflId : result.atchmnflId });
						}else{
							_uploadFile(filerKit, uploadCount, {atchmnflId : atchmnflId });
						}


	//					_uploadFile(filerKit, uploadCount, {atchmnflId : atchmnflId });
					}else{

						if(wavus.util.isEmpty(result.atchmnflId)){
							deferred.resolve(atchmnflId);
						}else{
							deferred.resolve(result.atchmnflId);
						}
					}
				});

				_uploadFile(filerKit, uploadCount, {atchmnflId : atchmnflId });
			}
		});

		return deferred;
	}

	/**
	 * 파일 삭제 메소드
	 */
	function _delete(revmoeList){
		var deferred = $.Deferred();
		if(revmoeList.length > 0){

			var data = {
				atchmnflId : null,
				fileSnList : [],
				fileSnString : null
			};


			for(attr in revmoeList){
				var paramObj = wavus.util.QueryStringToJSON(revmoeList[attr]);
				data.atchmnflId = paramObj.atchmnflId;
				data.fileSnList.push(paramObj.fileSn);
			}

			data.fileSnString = data.fileSnList.join(",");

//			console.log(data);
			wavus.util.ajax({
				url : optionUrl.fileDeleteList,
				data : data,
				dataType : "json",
				type : 'POST'
			}).then(function(result){
//				console.log('0000000000000000000000');
//				console.log(result);
				deferred.resolve();
			});


		}else{
			deferred.resolve();
		}

		return deferred;
	}

	/**
	 * 업로드 할 파일의 확장자 확인
	 * true 시 업로드 가능
	 * false 시 업로드 불가능
	 */
	function getFileCheck(domId){
		var filerKit = $(domId).prop('jFiler');

		var fileExtList = ["application/haansofthwp", "text/plain", "application/x-zip-compressed", "image/jpeg", "image/png", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/pdf", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/vnd.ms-excel"];

		var uploadFlag = true;

		for (var index = 0 ; index < filerKit.files.length ; index++){
			if(_.contains(fileExtList, filerKit.files[index].type)){
				console.log("uploadFlag");
			}else{
				uploadFlag = false;
			}
		}

		return uploadFlag;
	}


	/**
	 * 다운로드 url을 알때 사용 하는 다운로드 메소드
	 */
	function _download(url){
		document.location.href = url;
	}



	function init() {
		var deferred = $.Deferred();

//		wavus.util.ajax({
//			url : _sessionUrl,
//			dataType : "json",
//			type : 'POST'
//		}).then(function (result){
//			userInfo = result;
//			deferred.resolve();
//		}).fail(function (xhr, status, thrown){
//		});

		return deferred;
	}

	var module = {
		init : init,
		setUploadObjectId : setUploadObjectId,
		uploadFile : uploadFile,
		getFileCount : getFileCount,
		checkUpdateFileCount : checkUpdateFileCount,

		setViewObjectId : setViewObjectId,

		setEditObjectId : setEditObjectId,
		editUploadFile : editUploadFile,

		getFileCheck : getFileCheck
	};

	return module;
})();