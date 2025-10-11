//
//var fileUtils = {
//	//파일업로드
//	fileGridUpload : function(options){
//		if(!options){alert('정의된 속성이 없습니다.\ntarget속성을 정의해주세요');return false;}
//		if(!options.target){alert('target 속성을 정의해주세요');return false;}
//
//		var titleMsg="파일첨부";
//		if(options.titleMsg)titleMsg=options.titleMsg;
//
//		var ax5uploaderId=Math.random().toString(36).slice(2);
//		var maximunFileSize="10MB"; //개별 파일사이즈 제한
//		var maximunFileSizeStr="10MB"; //개별 파일사이즈 제한
//		var maximunAllFileSize="20MB"; //전체 파일사이즈 제한
//		var maximunAllFileSizeStr="20MB"; //전체 파일사이즈 제한
//		var maximumNumberOfFiles="10"; //최대 파일첨부 개수
//		var deleteBtn=true; //삭제버튼 표시여부
//		var insertBtn=true; //파일추가버튼 표시여부
//		if(options.maximunFileSize){
//			maximunFileSize=options.maximunFileSize;
//			maximunFileSizeStr=options.maximunFileSize;
//		}
//		if(options.maximunAllFileSize){
//			maximunAllFileSize=options.maximunAllFileSize;
//			maximunAllFileSizeStr=options.maximunAllFileSize;
//		}
//		if(options.maximumNumberOfFiles)maximumNumberOfFiles=options.maximumNumberOfFiles;
//		if(options.deleteBtn === false)deleteBtn=options.deleteBtn;
//		if(options.insertBtn === false)insertBtn=options.insertBtn;
//
//		var maxSize=maximunFileSize.replace(/[^0-9.]/g,'');
//		var maxSizeStr=maximunFileSize.replace(/[0-9.]/g,'');
//		var maxAllSize=maximunAllFileSize.replace(/[^0-9.]/g,'');
//		var maxAllSizeStr=maximunAllFileSize.replace(/[0-9.]/g,'');
//		switch(maxSizeStr){
//			case "M" :
//				maximunFileSize = Number(maxSize) * 1024 * 1024;
//				maximunAllFileSize = Number(maxAllSize) * 1024 * 1024;
//				break;
//			case "MB" :
//				maximunFileSize = Number(maxSize) * 1024 * 1024;
//				maximunAllFileSize = Number(maxAllSize) * 1024 * 1024;
//				break;
//			case "G" :
//				maximunFileSize = Number(maxSize) * 1024 * 1024 * 1024;
//				maximunAllFileSize = Number(maxAllSize) * 1024 * 1024 * 1024;
//				break;
//			case "GB" :
//				maximunFileSize = Number(maxSize) * 1024 * 1024 * 1024
//				maximunAllFileSize = Number(maxAllSize) * 1024 * 1024 * 1024;
//				break;
//		}
//
//        var toast = new ax5.ui.toast({
//            containerPosition: "top-right",
//            icon : '<i class="axi axi-upload2"></i>',
//            width : 400
//        });
//		var ax5uploaderElem=[];
//		ax5uploaderElem.push('<div class="ta-l clearFix mt-15">');
//		ax5uploaderElem.push('	<div class="fl-l">');
//		ax5uploaderElem.push('		<span style="font-size: large;font-weight: bold;">'+titleMsg+'</span>');
//		ax5uploaderElem.push('		<div data-ax5uploader="'+ax5uploaderId+'">');
//		ax5uploaderElem.push('			<input type="hidden" name="maximunFileSize" value="'+maximunFileSize+'"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="maximunFileSizeStr" value="'+maximunFileSizeStr+'"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="maximunAllFileSize" value="'+maximunAllFileSize+'"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="maximunAllFileSizeStr" value="'+maximunAllFileSizeStr+'"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="maximumNumberOfFiles" value="'+maximumNumberOfFiles+'"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="currentAllFilesSize" value="0"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="accept" value="*/*"/>');
//		ax5uploaderElem.push('		</div>');
//		ax5uploaderElem.push('	</div>');
//		ax5uploaderElem.push('	<div class="fl-r">');
//		if(insertBtn === true){
//			ax5uploaderElem.push('		<button type="button" data-ax5uploader-button="selector" class="btn-dkGray">파일추가</button>');
//		}
//		if(deleteBtn === true){
//			ax5uploaderElem.push('		<button type="button" data-upload-btn="removeFile" class="btn-gray">선택한파일삭제</button>');
//		}
//		ax5uploaderElem.push('	</div>');
//		ax5uploaderElem.push('</div>');
//		$(options.target.selector).append(ax5uploaderElem.join(""));
//
//		$(options.target.selector).find('[data-ax5uploader-button="selector"]').click(function(e){
//			if(ax5uploader["__uploading"]){
//    			commonUtils.alert({
//    				theme : "warning"
//    				,title:"파일이 업로드중입니다."
//            		,msg:"업로드가 끝나고 추가 할 수 있습니다."
//            	});
//				e.stopImmediatePropagation();
//				return false;
//			}
//		});
//
//		var $uploadTarget = $(options.target.selector).find('[data-ax5uploader="'+ax5uploaderId+'"]');
//
//		var gridObj = {};
//		if(options.gridObj){
//			gridObj = options.gridObj;
//			gridObj.showRowSelector = deleteBtn;
//			if(!gridObj.config.body.onClick){
//				gridObj.config.body.onClick = function(){
//					if(this.column.key == "download" && this.item.atchmnflId){
//						var downloadParamObj={};
//						downloadParamObj={
//								atchmnflId : this.item.atchmnflId
//								,fileSn : this.item.fileSn
//						}
//						if(this.item.crudType){
//							if(this.item.crudType === "C")
//								downloadParamObj['tempFileChk'] = true
//						}
//						fileUtils.fileDownload(downloadParamObj);
//					}
//				}
//			}
//		}else{
//			if(!options.gridTarget){alert('gridTarget 속성을 정의해주세요');return false;}
//			gridObj = commonUtils.grid({
//	            target: options.gridTarget,
//	            sortable : false,
//	            header : {align: 'center',columnHeight : 41},
//                showLineNumber: false,
//                showRowSelector: deleteBtn,
//                multipleSelect: false,
//                lineNumberColumnWidth: 40,
//                rowSelectorColumnWidth: 27,
//	            columns: [
//	                {key: "crudtype", label: "상태",align: "center", width:50},
//	                {key: "orignlFileNm", label: "파일명", width: "*"},
//	                {key: "fileSize", label: "파일크기", align: "right", formatter: function () {
//	                    return ax5.util.number(this.value, {byte: true});
//	                }},
//	                {key: "fileExtsn", label: "확장자", align: "center", width: 60},
//	                {key: "lastUpdtDt", label: "등록일시", align: "center", width: 140, formatter: function () {
//	                	if(this.item.crudType){
//	                		if(this.item.crudType === "C")
//	                			return '임시등록됨';
//	                	}
//	                	return this.value;
//	                    //return ax5.util.date(this.value, {"return": "yyyy/MM/dd hh:mm:ss"});
//	                }},
//	                {key: "download", label: "다운로드", width: 60, align: "center", formatter: function () {
//	                    return '<i class="fa fa-download" aria-hidden="true"></i>'
//	                }}
//	            ],
//	            body: {
//	                onClick: function () {
//	                    if(this.column.key == "download" && this.item.atchmnflId){
//	                    	var downloadParamObj={};
//	                    	downloadParamObj={
//	                    		atchmnflId : this.item.atchmnflId
//	                    		,fileSn : this.item.fileSn
//	                    	}
//	                    	if(this.item.crudType){
//	                    		if(this.item.crudType === "C")
//	                    			downloadParamObj['tempFileChk'] = true
//	                    	}
//	                    	fileUtils.fileDownload(downloadParamObj);
//	                    }
//	                }
//	            }
//	        });
//		}
//
//		var uploadOptions={};
//		var uploadOptions=$.extend({
//            debug: false,
//    		maximunFileSize:maximunFileSize,
//    		maximunAllFileSize:maximunAllFileSize,
//            target: $uploadTarget,
//            form: {
//                action: "/cmm/fms/insertTempFileInfs.do",
//                fileName: "file"
//            },
//            progressBox: true,
//            progressBoxDirection: "left",
//            multiple: false,
//            //manualUpload: true,
//            validateSelectedFiles: function () {
//            	if(this.selectedFiles.length){
//            		if(this.selectedFiles[0].size > maximunFileSize){
//            			alert(maximunFileSizeStr+' 보다 큰 파일은 업로드를 할 수가 없습니다.');
//            			return false;
//            		}
//            	}
//            	if(this.self.config.accept){
//            		$uploadTarget.find('input[name="accept"]').val(this.self.config.accept);
//            	}
//                // 지정된 파일제한개수 이상 업로드 되지 않도록 제한.
//                var gridLength=gridObj.list.filter(function(item){
//                	return item.crudType !== "D";
//                }).length;
//                if((gridLength + this.uploadedFiles.length + this.selectedFiles.length) > Number(maximumNumberOfFiles)){
//    				commonUtils.alert({
//    					msg : '첨부파일 개수는 최대 '+maximumNumberOfFiles+'개 까지 가능합니다.'
//    					,theme : 'warning'
//    				});
//                	return false;
//                }
//                var fileSum=0;
//                $.each(gridObj.list,function(i,v){
//                	fileSum+=Number(v.fileSize);
//                })
//                $uploadTarget.find('input[name="currentAllFilesSize"]').val(fileSum.toFixed(0));
//                return true;
//            },
//            onprogress: function () {
//             	var progress = Math.floor(this.loaded / this.total * 100);
//            	ax5uploader.$progressBox.find(".progress").css('width',progress+'%');
//            	ax5uploader.$progressBox.find(".progress-bar-striped").css('width','0');
//            	ax5uploader.$progressBox.find("[data-percent-txt]").html(progress+'% 진행중...');
//
//            	if(progress === 100){
//            		ax5uploader.$progressBox.find("[data-percent-txt]").html(progress+'% 완료! 서버에서 파일을 처리하는 중입니다. 잠시만 기다려주세요');
//                    toast.confirm('서버에 파일을 올리는 중입니다.<br/>잠시만 기다려주세요.');
//            	}
//            },
//            onuploaded: function () {
//            },
//            onuploaderror: function () {
//            	switch(this.self.status){
//            		case 401 :
//            			commonUtils.alert({
//            				theme : "error"
//    	            		,msg:"로그인이 필요한 서비스입니다."
//    	            	});
//            			break;
//            		case 403 :
//            			commonUtils.alert({
//            				theme : "error"
//    	            		,msg:"권한이 없습니다."
//    	            	});
//            			break;
//            		case 400 :
//            			commonUtils.alert({
//            				theme : "error"
//    	            		,msg:this.self.responseText
//    	            	});
//            			break;
//            		default :
//            			commonUtils.alert({
//            				theme : "error"
//    	            		,msg:"파일업로드 처리중 에러가 발생하였습니다."
//    	            	});
//            	}
//            },
//            onuploadComplete: function () {
//            	if(toast.queue.length > 0) toast.close();
//            	switch(this.self.xhr.status){
//	        		case 400 :
//						commonUtils.alert({
//							theme : "error"
//							,msg:this.self.xhr.responseText
//						});
//						ax5uploader.removeFileAll();
//						break;
//            		case 401 :
//            			commonUtils.alert({
//            				theme : "error"
//    	            		,msg:"로그인이 필요한 서비스입니다."
//    	            	});
//	        			ax5uploader.removeFileAll();
//	        			break;
//	        		case 400 :
//            			commonUtils.alert({
//            				theme : "error"
//    	            		,msg:"권한이 없습니다."
//    	            	});
//	        			ax5uploader.removeFileAll();
//	        			break;
//            	}
//            	ACTIONS["UPDATE_uploaded"](this.self.uploadedFiles);
//            	if(this.self.xhr.status.toString().charAt(0) === "2"){
//            		if(toast){
//            			toast.push('임시 파일업로드가 완료 되었습니다.');
//            		}else{
//                        var toast2 = new ax5.ui.toast({
//                            containerPosition: "top-right",
//                            icon : '<i class="axi axi-upload2"></i>',
//                            width : 400
//                        });
//                        toast2.push('임시 파일업로드가 완료 되었습니다.');
//            		}
//            		if(options.onuploadCompleteCallback){
//            			options.onuploadCompleteCallback(this);
//            		}
//            	}
//            }
//        },options);
//		var ax5uploader=new ax5.ui.uploader(uploadOptions);
//		ax5uploader['getGrid'] = gridObj;
//        /// ACTIONS
//        var ACTIONS = {
//            "INIT": function () {
//                // 컨트롤 버튼 이벤트 제어
//                uploadView.initView();
//            },
//            "GET_grid": function (data) {
//                return gridObj.getList(data);
//            },
//            "GET_uploaded": function () {
//            	var data={};
//            	if(options.atchmnflId){
//            		data['atchmnflId']=options.atchmnflId;
//            	}
//                // 업로드 파일 가져오기
//            	commonUtils.ajax({
//                    url: "/cmm/fms/selectFileInfsList.do",
//                    data : $.param(data),
//                    success: function (response) {
//                        uploadView.setData(response);
//                    }
//            	})
//            },
//            "DELETE_files": function (data) {
//            },
//            "UPDATE_uploaded": function (data) {
//            	$.each(data,function(i,v){
//            		$.each(v.result,function(i2,v2){
//            			v2['crudType']="C";
//                		gridObj.addRow(v2,"last",{focus:"END"});
//            		});
//            	})
//            	ax5uploader.removeFileAll();
//            }
//        };
//        /// uploadView
//        var uploadView = {
//            initView: function () {
//            	$(options.target.selector).find("[data-upload-btn]").on('click',function(){
//            		switch($(this).attr("data-upload-btn")){
//            			case "removeFile" :
//            				if(gridObj.getList("selected").length === 0)
//            					return commonUtils.alert({title : '확인',msg : '삭제하려는 항목을 체크해주세요!',theme : 'warning'});
//            				//신규추가된 항목은 삭제처리
//            				$.each(gridObj.list,function(i,v){
//            					if(v['__selected__'] === true)
//            						if(v.crudType==='C')gridObj.deleteRow(v['__index']);
//            				})
//            				//기존에 존재하던 항목은 삭제 구분자만 변경
//            				$(gridObj.getList("selected")).each(function(i,v){
//            						if(!v.crudType)v.crudType='U';
//            						if(v.crudType){
//            							if(v.crudType === 'U' || v.crudType === 'D'){
//            								v.crudType="D";
//            								gridObj.updateRow(v,v.__index);
//            								gridObj.select(v.__index,{selected: false});
//            							}
//            						}
//            				});
//                        break;
//            		}
//            	})
//            },
//            setData: function (response) {
//            	//ax5uploader.setUploadedFiles(data);
//            	gridObj.setData(response.resultData.resultList);
//        		if(response.resultData.resultList.length === 0){ //목록이 존재하지 않을 경우 '데이터없음'을 표시한다.
//        			commonUtils.gridListEmptyMsg({
//        				target : options.gridTarget //그리드 타겟
//        				,msg : options.gridEmptyMsg?options.gridEmptyMsg:response.resultMessage //'데이터없음' 메세지
//        						,toast : false //toast를 표시여부
//        			});
//        		};
//            }
//        };
//        ACTIONS["INIT"]();
//        ACTIONS["GET_uploaded"]();
//
//        ax5uploader.$progressUpload.css('display','none');
//        ax5uploader.$progressAbort.text("중지");
//        ax5uploader.$progressAbort.click(function(){
//        	if(toast.queue.length > 0){
//        		toast.close();
//        	}
//            var toast2 = new ax5.ui.toast({
//                containerPosition: "top-right",
//                icon : '<i class="axi axi-upload2"></i>',
//                width : 400
//            });
//            toast2.push('파일업로드를 중지 하였습니다.');
//
//        })
//		return ax5uploader;
//	},
//	//이미지 파일업로드
//	fileUpload : function(options){
//		if(!options){alert('정의된 속성이 없습니다.\ntarget속성을 정의해주세요');return false;}
//		if(!options.target){alert('target 속성을 정의해주세요');return false;}
//
//		var titleMsg="파일첨부";
//		if(options.titleMsg)titleMsg=options.titleMsg;
//
//		var ax5uploaderId=Math.random().toString(36).slice(2);
//		var maximunFileSize="10MB"; //개별 파일사이즈 제한
//		var maximunFileSizeStr="10MB"; //개별 파일사이즈 제한
//		var maximunAllFileSize="20MB"; //전체 파일사이즈 제한
//		var maximunAllFileSizeStr="20MB"; //전체 파일사이즈 제한
//		var maximumNumberOfFiles="10"; //최대 파일첨부 개수
//		var deleteBtn=true; //삭제버튼 표시여부
//		var insertBtn=true; //파일추가버튼 표시여부
//		var imgPreview=true; //이미지 미리보기 표시여부
//		var imgOnly=false; //이미지 확장자만 허용가능여부
//		var allFileDownBtn=true; // 전체다운로드 버튼 허용가능 여부
//		var accept="*/*"; //허용확장자
//
//		if(options.maximunFileSize){
//			maximunFileSize=options.maximunFileSize;
//			maximunFileSizeStr=options.maximunFileSize;
//		}
//		if(options.maximunAllFileSize){
//			maximunAllFileSize=options.maximunAllFileSize;
//			maximunAllFileSizeStr=options.maximunAllFileSize;
//		}
//		if(options.maximumNumberOfFiles)maximumNumberOfFiles=options.maximumNumberOfFiles;
//		if(options.deleteBtn === false)deleteBtn=options.deleteBtn;
//		if(options.insertBtn === false)insertBtn=options.insertBtn;
//		if(options.allFileDownBtn === false)allFileDownBtn=options.allFileDownBtn;
//
//		if(options.imgPreview === false)imgPreview=options.imgPreview;
//		if(options.imgOnly)accept=".bmp, .rle, .dib, .jpg, .gif, .png, .tif, .tiff, .raw, jpeg, .BMP, .RLE, .DIB, .JPG, .GIF, .PNG, .TIF, .TIFF, .RAW";
//		if(options.accept)accept=options.accept;
//
//		var maxSize=maximunFileSize.replace(/[^0-9.]/g,'');
//		var maxSizeStr=maximunFileSize.replace(/[0-9.]/g,'');
//		var maxAllSize=maximunAllFileSize.replace(/[^0-9.]/g,'');
//		var maxAllSizeStr=maximunAllFileSize.replace(/[0-9.]/g,'');
//		switch(maxSizeStr){
//			case "K" :
//				maximunFileSize = Number(maxSize) * 1024;
//				maximunAllFileSize = Number(maxAllSize) * 1024;
//				break;
//			case "KB" :
//				maximunFileSize = Number(maxSize) * 1024;
//				maximunAllFileSize = Number(maxAllSize) * 1024;
//				break;
//			case "M" :
//				maximunFileSize = Number(maxSize) * 1024 * 1024;
//				maximunAllFileSize = Number(maxAllSize) * 1024 * 1024;
//				break;
//			case "MB" :
//				maximunFileSize = Number(maxSize) * 1024 * 1024;
//				maximunAllFileSize = Number(maxAllSize) * 1024 * 1024;
//				break;
//			case "G" :
//				maximunFileSize = Number(maxSize) * 1024 * 1024 * 1024;
//				maximunAllFileSize = Number(maxAllSize) * 1024 * 1024 * 1024;
//				break;
//			case "GB" :
//				maximunFileSize = Number(maxSize) * 1024 * 1024 * 1024
//				maximunAllFileSize = Number(maxAllSize) * 1024 * 1024 * 1024;
//				break;
//		}
//
//        var toast = new ax5.ui.toast({
//            containerPosition: "top-right",
//            icon : '<i class="axi axi-upload2"></i>',
//            width : 400
//        });
//
//        //이미지 확장자
//        var imgFileExtsn=['bmp','rle','dib','jpg','gif','png','tif','tiff','raw','BMP','RLE','DIB','JPG','GIF','PNG','TIF','TIFF','RAW']
//
//		var ax5uploaderElem=[];
//		ax5uploaderElem.push('<div class="ta-l clearFix mt-15 file-upload-div">');
//		ax5uploaderElem.push('	<div class="fl-l">');
//		ax5uploaderElem.push('		<h3>'+titleMsg+'</h3>');
//		ax5uploaderElem.push('		<div data-ax5uploader="'+ax5uploaderId+'">');
//		ax5uploaderElem.push('			<input type="hidden" name="maximunFileSize" value="'+maximunFileSize+'"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="maximunFileSizeStr" value="'+maximunFileSizeStr+'"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="maximunAllFileSize" value="'+maximunAllFileSize+'"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="maximunAllFileSizeStr" value="'+maximunAllFileSizeStr+'"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="maximumNumberOfFiles" value="'+maximumNumberOfFiles+'"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="currentAllFilesSize" value="0"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="accept" value="'+accept+'"/>');
//		ax5uploaderElem.push('		</div>');
//		ax5uploaderElem.push('	</div>');
//		ax5uploaderElem.push('	<div class="fl-r" style="margin-top:10px;">');
//		if(insertBtn === true){
//			ax5uploaderElem.push('		<button type="button" data-ax5uploader-button="selector" class="btn-dkGray mr-5">파일추가</button>');
//		}else{
//			ax5uploaderElem.push('		<button type="button" style="display:none;" data-ax5uploader-button="selector" class="btn-dkGray mr-5">파일추가</button>');
//		}
//		if(deleteBtn === true){
//			ax5uploaderElem.push('		<button type="button" data-upload-btn="removeFile" class="btn-gray mr-5">선택한 파일삭제</button>');
//		}
//		if(options.extraBtn){
//			ax5uploaderElem.push(options.extraBtn);
//		}
//		if(allFileDownBtn === true){
//			ax5uploaderElem.push('		<button type="button" data-upload-btn="allFileDownload" class="btn-gray mr-5">전체 다운로드</button>');
//		}
//		ax5uploaderElem.push('	</div>');
//		ax5uploaderElem.push('</div>');
//		$(options.target.selector).append(ax5uploaderElem.join(""));
//
//		$(options.target.selector).find('[data-ax5uploader-button="selector"]').click(function(e){
//			if(ax5uploader["__uploading"]){
//    			commonUtils.alert({
//    				theme : "warning"
//    				,title:"파일이 업로드중입니다."
//            		,msg:"업로드가 끝나고 추가 할 수 있습니다."
//            	});
//				e.stopImmediatePropagation();
//				return false;
//			}
//		});
//
//		var emptyTrColspan=5;
//		if(deleteBtn === true)emptyTrColspan+=2;
//		if(imgPreview === true)emptyTrColspan+=1;
//		var emptyTr='<tr id="emptyTr"><td colspan="'+emptyTrColspan+'" style="text-align: center;font-size: 15px;font-weight: bold;">'+(options.gridEmptyMsg?options.gridEmptyMsg:'첨부파일이 존재하지 않습니다.')+'</td></tr>';
//		var tableTmplStr=[];
//		tableTmplStr.push('<table class="table-list file-upload-table">');
//		tableTmplStr.push('	<colgroup>');
//		if(deleteBtn === true){
//			tableTmplStr.push('		<col style="width: 50px;">');
//			tableTmplStr.push('		<col style="width: 70px;">');
//		}
//		tableTmplStr.push('		<col style="width: 350px;">');
//		if(imgPreview === true)
//			tableTmplStr.push('		<col>');
//		tableTmplStr.push('		<col style="width: 100px;">');
//		tableTmplStr.push('		<col style="width: 75px;">');
//		tableTmplStr.push('		<col style="width: 150px;">');
//		tableTmplStr.push('		<col style="width: 70px;">');
//		tableTmplStr.push('	</colgroup>');
//		tableTmplStr.push('	<thead>');
//		tableTmplStr.push('		<tr>');
//		if(deleteBtn === true){
//			tableTmplStr.push('			<th>선택</th>');
//			tableTmplStr.push('			<th>상태</th>');
//		}
//		tableTmplStr.push('			<th>파일명</th>');
//		if(imgPreview === true)
//			tableTmplStr.push('			<th>미리보기</th>');
//		tableTmplStr.push('			<th>파일크기</th>');
//		tableTmplStr.push('			<th>확장자</th>');
//		tableTmplStr.push('			<th>등록일시</th>');
//		tableTmplStr.push('			<th>다운로드</th>');
//		tableTmplStr.push('		</tr>');
//		tableTmplStr.push('	</thead>');
//		tableTmplStr.push('	<tbody id="imgTableBody">');
//		tableTmplStr.push('	</tbody>');
//		tableTmplStr.push('</table>');
//		options.gridTarget.append(tableTmplStr.join(""));
//
//		var $uploadTable = options.gridTarget.find('table');
//
//		var $uploadTarget = $(options.target.selector).find('[data-ax5uploader="'+ax5uploaderId+'"]');
//
//		var uploadOptions={};
//		var uploadOptions=$.extend({
//            debug: false,
//    		maximunFileSize:maximunFileSize,
//    		maximunAllFileSize:maximunAllFileSize,
//            target: $uploadTarget,
//            form: {
//                action: "/cmm/fms/insertTempFileInfs.do",
//                fileName: "file"
//            },
//            accept:accept,
//            progressBox: true,
//            progressBoxDirection: "left",
//            multiple: false,
//            //manualUpload: true,
//            validateSelectedFiles: function () {
//				if(this.selectedFiles.length){
//					var size=0;
//					if(this.selectedFiles[0].size)
//						size = this.selectedFiles[0].size;
//					else if(this.selectedFiles[0][0].size)
//						size = this.selectedFiles[0][0].size;
//					if(size > maximunFileSize){
//						alert(maximunFileSizeStr+' 보다 큰 파일은 업로드를 할 수가 없습니다.');
//						return false;
//					}
//				}
//
//            	var fileSum=0;
//            	var fileCnt=0;
//				$uploadTable.find('tbody tr').each(function(i,v){
//					if($(v).attr("id") !== 'emptyTr' && $(v).find('input[name="crudType"]').val() !== "D"){
//						fileCnt++;
//						fileSum+=Number($(v).find('input[name="fileSize"]').val());
//					}
//				});
//                // 지정된 파일제한개수 이상 업로드 되지 않도록 제한.
//                if((fileCnt + this.uploadedFiles.length + this.selectedFiles.length) > Number(maximumNumberOfFiles)){
//    				commonUtils.alert({
//    					msg : '첨부파일 개수는 최대 '+maximumNumberOfFiles+'개 까지 가능합니다.'
//    					,theme : 'warning'
//    				});
//                	return false;
//                }
//                $uploadTarget.find('input[name="currentAllFilesSize"]').val(fileSum.toFixed(0));
//                return true;
//            },
//            onprogress: function () {
//             	var progress = Math.floor(this.loaded / this.total * 100);
//            	ax5uploader.$progressBox.find(".progress").css('width',progress+'%');
//            	ax5uploader.$progressBox.find(".progress-bar-striped").css('width','0');
//            	ax5uploader.$progressBox.find("[data-percent-txt]").html(progress+'% 진행중...');
//            	if(progress === 100){
//            		ax5uploader.$progressBox.find("[data-percent-txt]").html(progress+'% 완료! 서버에서 파일을 처리하는 중입니다. 잠시만 기다려주세요');
//                    toast.confirm('서버에 파일을 올리는 중입니다.<br/>잠시만 기다려주세요.');
//            	}
//            },
//            onuploaded: function () {
//            },
//            onuploaderror: function () {
//            	switch(this.self.status){
//            		case 401 :
//            			commonUtils.alert({
//            				theme : "error"
//    	            		,msg:"로그인이 필요한 서비스입니다."
//    	            	});
//            			break;
//            		case 403 :
//            			commonUtils.alert({
//            				theme : "error"
//    	            		,msg:"권한이 없습니다."
//    	            	});
//            			break;
//            		case 400 :
//            			commonUtils.alert({
//            				theme : "error"
//    	            		,msg:this.self.responseText
//    	            	});
//            			break;
//            		default :
//            			commonUtils.alert({
//            				theme : "error"
//    	            		,msg:"파일업로드 처리중 에러가 발생하였습니다."
//    	            	});
//            	}
//            },
//            onuploadComplete: function () {
//            	if(toast.queue.length > 0) toast.close();
//            	switch(this.self.xhr.status){
//	        		case 400 :
//						commonUtils.alert({
//							theme : "error"
//							,msg:this.self.xhr.responseText
//						});
//						ax5uploader.removeFileAll();
//						break;
//            		case 401 :
//            			commonUtils.alert({
//            				theme : "error"
//    	            		,msg:"로그인이 필요한 서비스입니다."
//    	            	});
//	        			ax5uploader.removeFileAll();
//	        			break;
//	        		case 400 :
//            			commonUtils.alert({
//            				theme : "error"
//    	            		,msg:"권한이 없습니다."
//    	            	});
//	        			ax5uploader.removeFileAll();
//	        			break;
//            	}
//            	ACTIONS["UPDATE_uploaded"](this.self.uploadedFiles);
//            	if(this.self.xhr.status.toString().charAt(0) === "2"){
//            		if(toast){
//            			toast.push('임시 파일업로드가 완료 되었습니다.');
//            		}else{
//                        var toast2 = new ax5.ui.toast({
//                            containerPosition: "top-right",
//                            icon : '<i class="axi axi-upload2"></i>',
//                            width : 400
//                        });
//                        toast2.push('임시 파일업로드가 완료 되었습니다.');
//            		}
//            		if(options.onuploadCompleteCallback){
//            			options.onuploadCompleteCallback(this);
//            		}
//            	}
//            }
//        },options);
//
//		var ax5uploader=new ax5.ui.uploader(uploadOptions);
//		ax5uploader['getGrid'] = {
//			list : function(){
//				var objarr=[];
//				$uploadTable.find('tbody tr').each(function(i,v){
//					if($(v).attr("id") !== 'emptyTr'){
//						objarr.push({
//							crudType : $(v).find('input[name="crudType"]').val()
//							,atchmnflId : $(v).find('input[name="atchmnflIdTmp"]').val()
//							,fileSize : $(v).find('input[name="fileSize"]').val()
//							,fileSn : $(v).find('input[name="fileSn"]').val()
//							,tempFileChk : $(v).find('input[name="tempFileChk"]').val()
//							,streFileNm : $(v).find('input[name="streFileNm"]').val()
//							,streFlpth : $(v).find('input[name="streFlpth"]').val()
//						});
//					}
//				});
//				return objarr;
//			}
//		};
//        /// ACTIONS
//        var ACTIONS = {
//            "INIT": function () {
//                // 컨트롤 버튼 이벤트 제어
//                uploadView.initView();
//            },
//            "GET_grid": function (data) {
//                return gridObj.getList(data);
//            },
//            "GET_uploaded": function () {
//				var data={};
//				if(options.atchmnflId && options.atchmnflId != null && options.atchmnflId !== ''){
//					data['atchmnflId']=options.atchmnflId;
//					// 업로드 파일 가져오기
//					commonUtils.ajax({
//						url: "/cmm/fms/selectFileInfsList.do",
//						data : $.param(data),
//						maskTarget : options.gridTarget,
//						success: function (response) {
//							uploadView.setData(response);
//						}
//					});
//				}else{
//					uploadView.setData({resultData:{resultList:[]}});
//				}
//            },
//            "DELETE_files": function (data) {
//            },
//            "UPDATE_uploaded": function (data) {
//            	$uploadTable.find("#emptyTr").remove();
//            	$.each(data,function(i,v){
//            		$.each(v.result,function(i2,v2){
//            			var trTmplStr=[];
//            			var randomVal=Math.random().toString(36).slice(2);
//            			trTmplStr.push('<tr id="'+randomVal+'">');
//            			trTmplStr.push('	<input type="hidden" name="crudType" value="C">');
//            			trTmplStr.push('	<input type="hidden" name="atchmnflIdTmp" value="'+v2.atchmnflId+'">');
//            			trTmplStr.push('	<input type="hidden" name="fileSize" value="'+v2.fileSize+'">');
//            			trTmplStr.push('	<input type="hidden" name="fileSn" value="'+v2.fileSn+'">');
//            			trTmplStr.push('	<input type="hidden" name="tempFileChk" value="true">');
//            			trTmplStr.push('	<input type="hidden" name="streFlpth" value="'+v2.streFlpth+'">');
//            			trTmplStr.push('	<input type="hidden" name="streFileNm" value="'+v2.streFileNm+'">');
//            			if(deleteBtn === true){
//            				trTmplStr.push('	<td style="padding:0;"><input type="checkbox" name="imgCheckbox" value="'+randomVal+'"></td>');
//            				trTmplStr.push('	<td><span data-crudTxt>신규</span></td>');
//            			}
//            			trTmplStr.push('	<td class="ta-l" style="max-width: 350px; overflow: hidden;">'+v2.orignlFileNm+'</td>');
//
//            			if(imgPreview === true){
//            				if(imgFileExtsn.indexOf(v2.fileExtsn) !== -1){
//            					trTmplStr.push('	<td style="padding: 5px;"><img width="100%" height="100" style="cursor:pointer" src="/cmm/fms/FileDown.do?atchmnflId='+v2.atchmnflId+'&fileSn='+v2.fileSn+'&tempFileChk=true"/></td>');
//            				}else{
//            					trTmplStr.push('	<td>없음</td>');
//            				}
//            			}
//            			trTmplStr.push('	<td class="ta-l">'+ax5.util.number(v2.fileSize, {byte: true})+'</td>');
//            			trTmplStr.push('	<td class="ta-l">'+v2.fileExtsn+'</td>');
//            			trTmplStr.push('	<td class="ta-l">임시등록됨</td>');
//            			trTmplStr.push('	<td><span data-img-download data-ax5grid-cellholder="" data-ax5grid-text-align="center" style="height:25px;line-height: 19px; font-size: large; cursor:pointer;"><i class="fa fa-download" aria-hidden="true"></i></span></td>');
//            			trTmplStr.push('</tr>');
//            			$newTr=$(trTmplStr.join(""));
//
//            			$newTr.find('img').click(function(){
//            				var _this=$(this).clone();
//            				$(_this).css({width : '800'});
//            				$(_this).css({height : '600'});
//            				var modal=commonUtils.modal({title:'이미지미리보기',width:810,height:610});
//            				modal.open({},function(){
//            					this.$["body-frame"].html(_this);
//            				});
//            			});
//
//            			$newTr.find('[data-img-download]').click(function(){
//            				$parentTr=$(this).parents('tr');
//	                    	var downloadParamObj={};
//	                    	downloadParamObj={
//	                    		atchmnflId : $parentTr.find('input[name="atchmnflIdTmp"]').val()
//	                    		,fileSn : $parentTr.find('input[name="fileSn"]').val()
//	                    	}
//                    		if($parentTr.find('input[name="crudType"]').val() === "C")
//                    			downloadParamObj['tempFileChk'] = true
//	                    	fileUtils.fileDownload(downloadParamObj);
//            			});
//            			$uploadTable.find('tbody').append($newTr);
//            		});
//            	})
//            	ax5uploader.removeFileAll();
//            }
//        };
//        /// uploadView
//        var uploadView = {
//            initView: function () {
//            	$(options.target.selector).find("[data-upload-btn]").on('click',function(){
//            		switch($(this).attr("data-upload-btn")){
//            			case "removeFile" :
//            				var $checkedNode=$uploadTable.find('input[name="imgCheckbox"]:checked');
//            				if($checkedNode.length === 0)
//            					return commonUtils.alert({title : '확인',msg : '삭제하려는 항목을 체크해주세요!',theme : 'warning'});
//            				$checkedNode.each(function(i,v){
//            					var $currTr=$uploadTable.find("#"+v.value);
//            					if($currTr.find("input[name='crudType']").val() === "C"){
//            						//신규추가된 항목은 삭제처리
//            						$currTr.remove();
//            					}else{
//            						//기존에 존재하던 항목은 삭제 플래그만 변경
//            						$currTr.find("input[name='crudType']").val("D");
//            						$currTr.find("[data-crudTxt]").text('삭제');
//            						$currTr.find("[data-crudTxt]").parents('td').css('background','#f1f2f6');
//            						$currTr.find("[data-crudTxt]").parents('td').next().css('text-decoration','line-through');
//            						$currTr.css('display','none');
//            					}
//            				});
//            				if($uploadTable.find('tbody tr').length === 0){
//            					$uploadTable.find('tbody').empty().append(emptyTr);
//            				}
//            				$uploadTable.find('input[type=checkbox]').prop("checked",false);
//            				break;
//            			case "allFileDownload" :
//            				var filterData=[];
//            				$.each(ax5uploader.getGrid.list(), function(index,item){
//            					if(item.crudType !== 'D'){
//            						filterData.push(item);
//            					}
//            				});
//
//            				if(filterData.length === 0){
//                				commonUtils.alert({
//                					msg : '다운로드 대상 첨부파일이 존재하지 않습니다.'
//                					,theme : 'warning'
//                				});
//                            	return false;
//            				}
//            				//전체파일 다운로드
//            				fileUtils.allFileDownload({
//            					data : [{name:'fileDatas',value:JSON.stringify(filterData)}]
//            				});
//            				break;
//            		}
//            	})
//            },
//            setData: function (response) {
//            	var resultList=response.resultData.resultList;
//        		if(resultList.length === 0){ //목록이 존재하지 않을 경우 '데이터없음'을 표시한다.
//        			$uploadTable.find('tbody').empty().append(emptyTr);
//        		}else{
//        			$.each(resultList,function(i,v){
//            			var trTmplStr=[];
//            			var randomVal=Math.random().toString(36).slice(2);
//            			trTmplStr.push('<tr id="'+randomVal+'">');
//            			trTmplStr.push('	<input type="hidden" name="crudType" value="">');
//            			trTmplStr.push('	<input type="hidden" name="atchmnflIdTmp" value="'+v.atchmnflId+'">');
//            			trTmplStr.push('	<input type="hidden" name="fileSize" value="'+v.fileSize+'">');
//            			trTmplStr.push('	<input type="hidden" name="fileSn" value="'+v.fileSn+'">');
//            			trTmplStr.push('	<input type="hidden" name="tempFileChk" value="false">');
//            			trTmplStr.push('	<input type="hidden" name="streFlpth" value="'+v.streFlpth+'">');
//            			trTmplStr.push('	<input type="hidden" name="streFileNm" value="'+v.streFileNm+'">');
//            			if(deleteBtn === true){
//            				trTmplStr.push('	<td  style="padding:0;"><input type="checkbox" name="imgCheckbox" value="'+randomVal+'"></td>');
//            				trTmplStr.push('	<td><span data-crudTxt></span></td>');
//            			}
//            			trTmplStr.push('	<td style="max-width: 350px; overflow: hidden; text-align : left;">'+v.orignlFileNm+'</td>');
//
//            			if(imgPreview === true){
//            				if(imgFileExtsn.indexOf(v.fileExtsn) !== -1){
//            					trTmplStr.push('	<td style="padding: 5px;"><img width="100%" height="100"  style="cursor:pointer" src="/cmm/fms/FileDown.do?atchmnflId='+v.atchmnflId+'&fileSn='+v.fileSn+'"/></td>');
//            				}else{
//            					trTmplStr.push('	<td>없음</td>');
//            				}
//            			}
//            			trTmplStr.push('	<td>'+ax5.util.number(v.fileSize, {byte: true})+'</td>');
//            			trTmplStr.push('	<td>'+v.fileExtsn+'</td>');
//            			trTmplStr.push('	<td>'+v.lastUpdtDt+'</td>');
//            			trTmplStr.push('	<td><span data-img-download data-ax5grid-cellholder="" data-ax5grid-text-align="center" style="height:25px;line-height: 19px; font-size: large; cursor:pointer;"><i class="fa fa-download" aria-hidden="true"></i></span></td>');
//            			trTmplStr.push('</tr>');
//            			$newTr=$(trTmplStr.join(""));
//
//            			$newTr.find('img').click(function(){
//            				var _this=$(this).clone();
//            				$(_this).css({width : '800'});
//            				$(_this).css({height : '600'});
//            				var modal=commonUtils.modal({title:'이미지미리보기',width:810,height:610});
//            				modal.open({},function(){
//            					this.$["body-frame"].html(_this);
//            				})
//            			});
//
//            			$newTr.find('[data-img-download]').click(function(){
//            				$parentTr=$(this).parents('tr');
//	                    	var downloadParamObj={};
//	                    	downloadParamObj={
//	                    		atchmnflId : $parentTr.find('input[name="atchmnflIdTmp"]').val()
//	                    		,fileSn : $parentTr.find('input[name="fileSn"]').val()
//	                    	}
//                    		if($parentTr.find('input[name="crudType"]').val() === "C")
//                    			downloadParamObj['tempFileChk'] = true
//	                    	fileUtils.fileDownload(downloadParamObj);
//            			});
//            			$uploadTable.find('tbody').append($newTr);
//        			});
//        		}
//            }
//        };
//        ACTIONS["INIT"]();
//        ACTIONS["GET_uploaded"]();
//
//        if(ax5uploader.$progressUpload){
//        	ax5uploader.$progressUpload.css('display','none');
//        	ax5uploader.$progressAbort.text("중지");
//        	ax5uploader.$progressAbort.click(function(){
//        		if(toast.queue.length > 0){
//        			toast.close();
//        		}
//        		var toast2 = new ax5.ui.toast({
//        			containerPosition: "top-right",
//        			icon : '<i class="axi axi-upload2"></i>',
//        			width : 400
//        		});
//        		toast2.push('파일업로드를 중지 하였습니다.');
//        	})
//        }
//        ax5uploader.config.accept=accept;
//		return ax5uploader;
//	},
//	//이미지 업로드
//	imgUpload : function(options){
//		if(!options){alert('정의된 속성이 없습니다.\ntarget속성을 정의해주세요');return false;}
//		if(!options.target){alert('target 속성을 정의해주세요');return false;}
//
//		var titleMsg="파일첨부";
//		if(options.titleMsg)titleMsg=options.titleMsg;
//
//		var ax5uploaderId=Math.random().toString(36).slice(2);
//		var maximunFileSize="10MB"; //개별 파일사이즈 제한
//		var maximunFileSizeStr="10MB"; //개별 파일사이즈 제한
//		var maximunAllFileSize="20MB"; //전체 파일사이즈 제한
//		var maximunAllFileSizeStr="20MB"; //전체 파일사이즈 제한
//		var maximumNumberOfFiles="10"; //최대 파일첨부 개수
//		var deleteBtn=true; //삭제버튼 표시여부
//		var insertBtn=true; //파일추가버튼 표시여부
//		var imgPreview=true; //이미지 미리보기 표시여부
//		var imgOnly=false; //이미지 확장자만 허용가능여부
//		var allFileDownBtn=true; // 전체다운로드 버튼 허용가능 여부
//		var accept="*/*"; //허용확장자
//
//		if(options.maximunFileSize){
//			maximunFileSize=options.maximunFileSize;
//			maximunFileSizeStr=options.maximunFileSize;
//		}
//		if(options.maximunAllFileSize){
//			maximunAllFileSize=options.maximunAllFileSize;
//			maximunAllFileSizeStr=options.maximunAllFileSize;
//		}
//		if(options.maximumNumberOfFiles)maximumNumberOfFiles=options.maximumNumberOfFiles;
//		if(options.deleteBtn === false)deleteBtn=options.deleteBtn;
//		if(options.insertBtn === false)insertBtn=options.insertBtn;
//		if(options.allFileDownBtn === false)allFileDownBtn=options.allFileDownBtn;
//
//		if(options.imgPreview === false)imgPreview=options.imgPreview;
//		if(options.imgOnly)accept=".bmp, .rle, .dib, .jpg, .gif, .png, .tif, .tiff, .raw, .BMP, .RLE, .DIB, .JPG, .GIF, .PNG, .TIF, .TIFF, .RAW";
//		if(options.accept)accept=options.accept;
//
//		var maxSize=maximunFileSize.replace(/[^0-9.]/g,'');
//		var maxSizeStr=maximunFileSize.replace(/[0-9.]/g,'');
//		var maxAllSize=maximunAllFileSize.replace(/[^0-9.]/g,'');
//		var maxAllSizeStr=maximunAllFileSize.replace(/[0-9.]/g,'');
//		switch(maxSizeStr){
//		case "K" :
//			maximunFileSize = Number(maxSize) * 1024;
//			maximunAllFileSize = Number(maxAllSize) * 1024;
//			break;
//		case "KB" :
//			maximunFileSize = Number(maxSize) * 1024;
//			maximunAllFileSize = Number(maxAllSize) * 1024;
//			break;
//		case "M" :
//			maximunFileSize = Number(maxSize) * 1024 * 1024;
//			maximunAllFileSize = Number(maxAllSize) * 1024 * 1024;
//			break;
//		case "MB" :
//			maximunFileSize = Number(maxSize) * 1024 * 1024;
//			maximunAllFileSize = Number(maxAllSize) * 1024 * 1024;
//			break;
//		case "G" :
//			maximunFileSize = Number(maxSize) * 1024 * 1024 * 1024;
//			maximunAllFileSize = Number(maxAllSize) * 1024 * 1024 * 1024;
//			break;
//		case "GB" :
//			maximunFileSize = Number(maxSize) * 1024 * 1024 * 1024
//			maximunAllFileSize = Number(maxAllSize) * 1024 * 1024 * 1024;
//			break;
//		}
//
//		var toast = new ax5.ui.toast({
//			containerPosition: "top-right",
//			icon : '<i class="axi axi-upload2"></i>',
//			width : 400
//		});
//
//		//이미지 확장자
//		var imgFileExtsn=['bmp','rle','dib','jpg','gif','png','tif','tiff','raw','BMP','RLE','DIB','JPG','GIF','PNG','TIF','TIFF','RAW']
//
//		var ax5uploaderElem=[];
//		ax5uploaderElem.push('<div class="table-wrap" style="display: inline-block; margin-top:0;">');
//		ax5uploaderElem.push('	<div class="fl-l">');
//		ax5uploaderElem.push('		<h3>'+titleMsg+'</h3>');
//		ax5uploaderElem.push('		<div data-ax5uploader="'+ax5uploaderId+'">');
//		ax5uploaderElem.push('			<input type="hidden" name="maximunFileSize" value="'+maximunFileSize+'"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="maximunFileSizeStr" value="'+maximunFileSizeStr+'"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="maximunAllFileSize" value="'+maximunAllFileSize+'"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="maximunAllFileSizeStr" value="'+maximunAllFileSizeStr+'"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="maximumNumberOfFiles" value="'+maximumNumberOfFiles+'"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="currentAllFilesSize" value="0"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="accept" value="'+accept+'"/>');
//		ax5uploaderElem.push('		</div>');
//		ax5uploaderElem.push('	</div>');
//		ax5uploaderElem.push('	<div class="fl-r" style="margin-top:10px;">');
//		if(insertBtn === true){
//			ax5uploaderElem.push('		<button type="button" data-ax5uploader-button="selector" style="position: inherit;" class="btn-dkGray mr-5">파일추가</button>');
//		}else{
//			ax5uploaderElem.push('		<button type="button" style="display:none;" data-ax5uploader-button="selector" class="btn-dkGray mr-5">파일추가</button>');
//		}
//		if(deleteBtn === true){
//			ax5uploaderElem.push('		<button type="button" data-upload-btn="removeFile" class="btn-gray mr-5">선택한 파일삭제</button>');
//		}
//		if(options.extraBtn){
//			ax5uploaderElem.push(options.extraBtn);
//		}
//		if(allFileDownBtn === true){
//			ax5uploaderElem.push('		<button type="button" data-upload-btn="allFileDownload" class="btn-gray mr-5">전체 다운로드</button>');
//		}
//		ax5uploaderElem.push('	</div>');
//		ax5uploaderElem.push('</div>');
//		$(options.target.selector).append(ax5uploaderElem.join(""));
//
//		$(options.target.selector).find('[data-ax5uploader-button="selector"]').click(function(e){
//			if(ax5uploader["__uploading"]){
//				commonUtils.alert({
//					theme : "warning"
//						,title:"파일이 업로드중입니다."
//							,msg:"업로드가 끝나고 추가 할 수 있습니다."
//				});
//				e.stopImmediatePropagation();
//				return false;
//			}
//		});
//
//		var emptyTr='<li id="emptyTr" style="width:100%;"><div style="text-align: center;font-size: 15px;font-weight: bold;">'+(options.gridEmptyMsg?options.gridEmptyMsg:'첨부파일이 존재하지 않습니다.')+'</div></li>';
//
//		var tableTmplStr=[];
//		tableTmplStr.push('<div id="thumb-grid" class="thumb-list" style="border-top: 2px solid #767676; padding: 10px 10px 0;">');
//		tableTmplStr.push('	<ul id="imgTableBody">');
//		tableTmplStr.push('	</ul>');
//		tableTmplStr.push('</div>');
//		options.gridTarget.append(tableTmplStr.join(""));
//
//		var $uploadTable = options.gridTarget.find('ul');
//
//		var $uploadTarget = $(options.target.selector).find('[data-ax5uploader="'+ax5uploaderId+'"]');
//
//		var uploadOptions={};
//		var uploadOptions=$.extend({
//			debug: false,
//			maximunFileSize:maximunFileSize,
//			maximunAllFileSize:maximunAllFileSize,
//			target: $uploadTarget,
//			form: {
//				action: "/cmm/fms/insertTempFileInfs.do",
//				fileName: "file"
//			},
//			accept:accept,
//			progressBox: true,
//			progressBoxDirection: "left",
//			multiple: false,
//			//manualUpload: true,
//			validateSelectedFiles: function () {
//				if(this.selectedFiles.length){
//					var size=0;
//					if(this.selectedFiles[0].size)
//						size = this.selectedFiles[0].size;
//					else if(this.selectedFiles[0][0].size)
//						size = this.selectedFiles[0][0].size;
//					if(size > maximunFileSize){
//						alert(maximunFileSizeStr+' 보다 큰 파일은 업로드를 할 수가 없습니다.');
//						return false;
//					}
//				}
//
//				var fileSum=0;
//				var fileCnt=0;
//				$uploadTable.find('li').each(function(i,v){
//					if($(v).attr("id") !== 'emptyTr' && $(v).find('input[name="crudType"]').val() !== "D"){
//						fileCnt++;
//						fileSum+=Number($(v).find('input[name="fileSize"]').val());
//					}
//				});
//				// 지정된 파일제한개수 이상 업로드 되지 않도록 제한.
//				if((fileCnt + this.uploadedFiles.length + this.selectedFiles.length) > Number(maximumNumberOfFiles)){
//					commonUtils.alert({
//						msg : '첨부파일 개수는 최대 '+maximumNumberOfFiles+'개 까지 가능합니다.'
//						,theme : 'warning'
//					});
//					return false;
//				}
//				$uploadTarget.find('input[name="currentAllFilesSize"]').val(fileSum.toFixed(0));
//				return true;
//			},
//			onprogress: function () {
//				var progress = Math.floor(this.loaded / this.total * 100);
//				ax5uploader.$progressBox.find(".progress").css('width',progress+'%');
//				if(progress === 100){
//					toast.confirm('서버에 파일을 올리는 중입니다.<br/>잠시만 기다려주세요.');
//				}
//			},
//			onuploaded: function () {
//			},
//			onuploaderror: function () {
//				switch(this.self.status){
//				case 401 :
//					commonUtils.alert({
//						theme : "error"
//						,msg:"로그인이 필요한 서비스입니다."
//					});
//					break;
//				case 403 :
//					commonUtils.alert({
//						theme : "error"
//							,msg:"권한이 없습니다."
//					});
//					break;
//				case 400 :
//					commonUtils.alert({
//						theme : "error"
//							,msg:this.self.responseText
//					});
//				default :
//					commonUtils.alert({
//						theme : "error"
//							,msg:"파일업로드 처리중 에러가 발생하였습니다."
//					});
//				}
//			},
//			onuploadComplete: function () {
//				if(toast.queue.length > 0) toast.close();
//				switch(this.self.xhr.status){
//				case 400 :
//					commonUtils.alert({
//						theme : "error"
//						,msg:this.self.xhr.responseText
//					});
//					ax5uploader.removeFileAll();
//					break;
//				case 401 :
//					commonUtils.alert({
//						theme : "error"
//							,msg:"로그인이 필요한 서비스입니다."
//					});
//					ax5uploader.removeFileAll();
//					break;
//				case 400 :
//					commonUtils.alert({
//						theme : "error"
//							,msg:"권한이 없습니다."
//					});
//					ax5uploader.removeFileAll();
//					break;
//				}
//				ACTIONS["UPDATE_uploaded"](this.self.uploadedFiles);
//				if(this.self.xhr.status.toString().charAt(0) === "2"){
//					if(toast){
//						toast.push('임시 파일업로드가 완료 되었습니다.');
//					}else{
//						var toast2 = new ax5.ui.toast({
//							containerPosition: "top-right",
//							icon : '<i class="axi axi-upload2"></i>',
//							width : 400
//						});
//						toast2.push('임시 파일업로드가 완료 되었습니다.');
//					}
//					if(options.onuploadCompleteCallback){
//						options.onuploadCompleteCallback(this);
//					}
//				}
//			}
//		},options);
//
//		var ax5uploader=new ax5.ui.uploader(uploadOptions);
//		ax5uploader['getGrid'] = {
//				list : function(){
//					var objarr=[];
//					$uploadTable.find('li').each(function(i,v){
//						if($(v).attr("id") !== 'emptyTr'){
//							objarr.push({
//								crudType : $(v).find('input[name="crudType"]').val()
//								,atchmnflId : $(v).find('input[name="atchmnflIdTmp"]').val()
//								,fileSize : $(v).find('input[name="fileSize"]').val()
//								,fileSn : $(v).find('input[name="fileSn"]').val()
//								,tempFileChk : $(v).find('input[name="tempFileChk"]').val()
//								,streFileNm : $(v).find('input[name="streFileNm"]').val()
//								,streFlpth : $(v).find('input[name="streFlpth"]').val()
//							});
//						}
//					});
//					return objarr;
//				}
//		};
//		/// ACTIONS
//		var ACTIONS = {
//				"INIT": function () {
//					// 컨트롤 버튼 이벤트 제어
//					uploadView.initView();
//				},
//				"GET_grid": function (data) {
//					return gridObj.getList(data);
//				},
//				"GET_uploaded": function () {
//					var data={};
//					if(options.atchmnflId && options.atchmnflId != null && options.atchmnflId !== ''){
//						data['atchmnflId']=options.atchmnflId;
//						// 업로드 파일 가져오기
//						commonUtils.ajax({
//							url: "/cmm/fms/selectFileInfsList.do",
//							data : $.param(data),
//							maskTarget : options.gridTarget,
//							success: function (response) {
//								uploadView.setData(response);
//							}
//						});
//					}else{
//						uploadView.setData({resultData:{resultList:[]}});
//					}
//				},
//				"DELETE_files": function (data) {
//				},
//				"UPDATE_uploaded": function (data) {
//					$uploadTable.find("#emptyTr").remove();
//					$.each(data,function(i,v){
//						$.each(v.result,function(i2,v2){
//							var trTmplStr=[];
//							var randomVal=Math.random().toString(36).slice(2);
//							trTmplStr.push('<li id="'+randomVal+'">');
//							trTmplStr.push('	<input type="hidden" name="crudType" value="C">');
//							trTmplStr.push('	<input type="hidden" name="atchmnflIdTmp" value="'+v2.atchmnflId+'">');
//							trTmplStr.push('	<input type="hidden" name="fileSize" value="'+v2.fileSize+'">');
//							trTmplStr.push('	<input type="hidden" name="fileSn" value="'+v2.fileSn+'">');
//							trTmplStr.push('	<input type="hidden" name="tempFileChk" value="true">');
//							trTmplStr.push('	<input type="hidden" name="streFlpth" value="'+v2.streFlpth+'">');
//							trTmplStr.push('	<input type="hidden" name="streFileNm" value="'+v2.streFileNm+'">');
//							if(deleteBtn === true){
//								trTmplStr.push('	<input class="custom-checkbox" type="checkbox" name="imgCheckbox" value="'+randomVal+'"/>');
//							}
//							trTmplStr.push('<div class="thumb" style="margin:auto;">');
//							if(imgFileExtsn.indexOf(v2.fileExtsn) !== -1){
//								trTmplStr.push('	<img width="100%" height="100%" style="cursor:pointer" src="/cmm/fms/FileDown.do?atchmnflId='+v2.atchmnflId+'&fileSn='+v2.fileSn+'&tempFileChk=true"/>');
//							}
//							trTmplStr.push('</div>');
//							trTmplStr.push('</li>');
//							$newTr=$(trTmplStr.join(""));
//
//							$newTr.find('img').click(function(){
//								var _this=$(this).clone();
//								$(_this).css({width : '800'});
//								$(_this).css({height : '600'});
//								var modal=commonUtils.modal({title:'이미지미리보기',width:810,height:610});
//								modal.open({},function(){
//									this.$["body-frame"].html(_this);
//								});
//							});
//
//							$newTr.find('[data-img-download]').click(function(){
//								$parentTr=$(this).parents('li');
//								var downloadParamObj={};
//								downloadParamObj={
//										atchmnflId : $parentTr.find('input[name="atchmnflIdTmp"]').val()
//										,fileSn : $parentTr.find('input[name="fileSn"]').val()
//								}
//								if($parentTr.find('input[name="crudType"]').val() === "C")
//									downloadParamObj['tempFileChk'] = true
//									fileUtils.fileDownload(downloadParamObj);
//							});
//							$uploadTable.append($newTr);
//						});
//					})
//					ax5uploader.removeFileAll();
//				}
//		};
//		/// uploadView
//		var uploadView = {
//				initView: function () {
//					$(options.target.selector).find("[data-upload-btn]").on('click',function(){
//						switch($(this).attr("data-upload-btn")){
//						case "removeFile" :
//							var $checkedNode=$uploadTable.find('input[name="imgCheckbox"]:checked');
//							if($checkedNode.length === 0)
//								return commonUtils.alert({title : '확인',msg : '삭제하려는 항목을 체크해주세요!',theme : 'warning'});
//							$checkedNode.each(function(i,v){
//								var $currTr=$uploadTable.find("#"+v.value);
//								if($currTr.find("input[name='crudType']").val() === "C"){
//									//신규추가된 항목은 삭제처리
//									$currTr.remove();
//								}else{
//									//기존에 존재하던 항목은 삭제 플래그만 변경
//									$currTr.find("input[name='crudType']").val("D");
//									$currTr.find("[data-crudTxt]").text('삭제');
//									$currTr.find("[data-crudTxt]").parents('td').css('background','#f1f2f6');
//									$currTr.find("[data-crudTxt]").parents('td').next().css('text-decoration','line-through');
//									$currTr.css('display','none');
//								}
//							});
//							if($uploadTable.find('li').length === 0){
//								$uploadTable.empty().append(emptyTr);
//							}
//							$uploadTable.find('input[type=checkbox]').prop("checked",false);
//							break;
//						case "allFileDownload" :
//							var filterData=[];
//							$.each(ax5uploader.getGrid.list(), function(index,item){
//								if(item.crudType !== 'D'){
//									filterData.push(item);
//								}
//							});
//
//							if(filterData.length === 0){
//								commonUtils.alert({
//									msg : '다운로드 대상 첨부파일이 존재하지 않습니다.'
//										,theme : 'warning'
//								});
//								return false;
//							}
//							//전체파일 다운로드
//							fileUtils.allFileDownload({
//								data : [{name:'fileDatas',value:JSON.stringify(filterData)}]
//							});
//							break;
//						}
//					})
//				},
//				setData: function (response) {
//					var resultList=response.resultData.resultList;
//					if(resultList.length === 0){ //목록이 존재하지 않을 경우 '데이터없음'을 표시한다.
//						$uploadTable.empty().append(emptyTr);
//					}else{
//						$.each(resultList,function(i,v){
//							var trTmplStr=[];
//							var randomVal=Math.random().toString(36).slice(2);
//							trTmplStr.push('<li id="'+randomVal+'">');
//							trTmplStr.push('	<input type="hidden" name="crudType" value="">');
//							trTmplStr.push('	<input type="hidden" name="atchmnflIdTmp" value="'+v.atchmnflId+'">');
//							trTmplStr.push('	<input type="hidden" name="fileSize" value="'+v.fileSize+'">');
//							trTmplStr.push('	<input type="hidden" name="fileSn" value="'+v.fileSn+'">');
//							trTmplStr.push('	<input type="hidden" name="tempFileChk" value="false">');
//							trTmplStr.push('	<input type="hidden" name="streFlpth" value="'+v.streFlpth+'">');
//							trTmplStr.push('	<input type="hidden" name="streFileNm" value="'+v.streFileNm+'">');
//							if(deleteBtn === true){
//								trTmplStr.push('	<input class="custom-checkbox" type="checkbox" name="imgCheckbox" value="'+randomVal+'"/>');
//							}
//							trTmplStr.push('<div class="thumb" style="margin:auto;">');
//							if(imgFileExtsn.indexOf(v.fileExtsn) !== -1){
//								trTmplStr.push('	<img width="100%" height="100%" style="cursor:pointer" src="/cmm/fms/FileDown.do?atchmnflId='+v.atchmnflId+'&fileSn='+v.fileSn+'"/>');
//							}
//							trTmplStr.push('</div>');
//							trTmplStr.push('</li>');
//							$newTr=$(trTmplStr.join(""));
//
//							$newTr.find('img').click(function(){
//								var _this=$(this).clone();
//								$(_this).css({width : '800'});
//								$(_this).css({height : '600'});
//								var modal=commonUtils.modal({title:'이미지미리보기',width:810,height:610});
//								modal.open({},function(){
//									this.$["body-frame"].html(_this);
//								})
//							});
//
//							$newTr.find('[data-img-download]').click(function(){
//								$parentTr=$(this).parents('li');
//								var downloadParamObj={};
//								downloadParamObj={
//										atchmnflId : $parentTr.find('input[name="atchmnflIdTmp"]').val()
//										,fileSn : $parentTr.find('input[name="fileSn"]').val()
//								}
//								if($parentTr.find('input[name="crudType"]').val() === "C")
//									downloadParamObj['tempFileChk'] = true
//									fileUtils.fileDownload(downloadParamObj);
//							});
//							$uploadTable.append($newTr);
//						});
//					}
//				}
//		};
//		ACTIONS["INIT"]();
//		ACTIONS["GET_uploaded"]();
//
//		if(ax5uploader.$progressUpload){
//			ax5uploader.$progressUpload.css('display','none');
//			ax5uploader.$progressAbort.text("중지");
//			ax5uploader.$progressAbort.click(function(){
//				if(toast.queue.length > 0){
//					toast.close();
//				}
//				var toast2 = new ax5.ui.toast({
//					containerPosition: "top-right",
//					icon : '<i class="axi axi-upload2"></i>',
//					width : 400
//				});
//				toast2.push('파일업로드를 중지 하였습니다.');
//			})
//		}
//		ax5uploader.config.accept=accept;
//		return ax5uploader;
//	},
//	//성과품 파일업로드
//	outComeUpload : function(options){
//		var fileMask = new ax5.ui.mask();
//		if(!options){alert('정의된 속성이 없습니다.\ntarget속성을 정의해주세요');return false;}
//		if(!options.target){alert('target 속성을 정의해주세요');return false;}
//
//		var titleMsg="파일첨부";
//		if(options.titleMsg)titleMsg=options.titleMsg;
//
//		var ax5uploaderId=Math.random().toString(36).slice(2);
//		var maximunFileSize="1GB"; //개별 파일사이즈 제한
//		var maximunFileSizeStr="1GB"; //개별 파일사이즈 제한
//		var maximunAllFileSize="1GB"; //전체 파일사이즈 제한
//		var maximunAllFileSizeStr="1GB"; //전체 파일사이즈 제한
//		var maximumNumberOfFiles="1"; //최대 파일첨부 개수
//		var deleteBtn=true; //삭제버튼 표시여부
//		var insertBtn=true; //파일추가버튼 표시여부
//		var imgPreview=false; //이미지 미리보기 표시여부
//		var imgOnly=false; //이미지 확장자만 허용가능여부
//		var allFileDownBtn=true; // 전체다운로드 버튼 허용가능 여부
//		var accept=".zip"; //허용확장자
//
//		var idxTemplate = options.idxTemplate; //인덱스 템플릿
//		var dataTemplate = options.dataTemplate; //데이타 템플릿
//		var outcomeZipTemplate = options.outcomeZipTemplate //성과품 압축파일 템플릿
//		var outcomeListTemplate = options.outcomeListTemplate //성과품 목록 템플릿
//
//		if(options.maximunFileSize){
//			maximunFileSize=options.maximunFileSize;
//			maximunFileSizeStr=options.maximunFileSize;
//		}
//		if(options.maximunAllFileSize){
//			maximunAllFileSize=options.maximunAllFileSize;
//			maximunAllFileSizeStr=options.maximunAllFileSize;
//		}
//		if(options.maximumNumberOfFiles)maximumNumberOfFiles=options.maximumNumberOfFiles;
//		if(options.deleteBtn === false)deleteBtn=options.deleteBtn;
//		if(options.insertBtn === false)insertBtn=options.insertBtn;
//		if(options.allFileDownBtn === false)allFileDownBtn=options.allFileDownBtn;
//		options.imgPreview=false;
//		if(options.accept)accept=options.accept;
//
//		var maxSize=maximunFileSize.replace(/[^0-9.]/g,'');
//		var maxSizeStr=maximunFileSize.replace(/[0-9.]/g,'');
//		var maxAllSize=maximunAllFileSize.replace(/[^0-9.]/g,'');
//		var maxAllSizeStr=maximunAllFileSize.replace(/[0-9.]/g,'');
//		switch(maxSizeStr){
//			case "K" :
//				maximunFileSize = Number(maxSize) * 1024;
//				maximunAllFileSize = Number(maxAllSize) * 1024;
//				break;
//			case "KB" :
//				maximunFileSize = Number(maxSize) * 1024;
//				maximunAllFileSize = Number(maxAllSize) * 1024;
//				break;
//			case "M" :
//				maximunFileSize = Number(maxSize) * 1024 * 1024;
//				maximunAllFileSize = Number(maxAllSize) * 1024 * 1024;
//				break;
//			case "MB" :
//				maximunFileSize = Number(maxSize) * 1024 * 1024;
//				maximunAllFileSize = Number(maxAllSize) * 1024 * 1024;
//				break;
//			case "G" :
//				maximunFileSize = Number(maxSize) * 1024 * 1024 * 1024;
//				maximunAllFileSize = Number(maxAllSize) * 1024 * 1024 * 1024;
//				break;
//			case "GB" :
//				maximunFileSize = Number(maxSize) * 1024 * 1024 * 1024
//				maximunAllFileSize = Number(maxAllSize) * 1024 * 1024 * 1024;
//				break;
//		}
//
//        var toast = new ax5.ui.toast({
//            containerPosition: "top-right",
//            icon : '<i class="axi axi-upload2"></i>',
//            width : 400
//        });
//
//        //이미지 확장자
//        var imgFileExtsn=['bmp','rle','dib','jpg','gif','png','tif','tiff','raw','BMP','RLE','DIB','JPG','GIF','PNG','TIF','TIFF','RAW']
//
//        var $form;
//        if(options.formId){
//        	$form = $("#"+options.formId);
//        }
//
//		var ax5uploaderElem=[];
//		ax5uploaderElem.push('<div class="ta-l clearFix mt-15 file-upload-div">');
//		ax5uploaderElem.push('	<div class="fl-l">');
//		ax5uploaderElem.push('		<h3 data-title-area>'+titleMsg+'</h3>');
//		ax5uploaderElem.push('		<div data-ax5uploader="'+ax5uploaderId+'">');
//		ax5uploaderElem.push('			<input type="hidden" name="maximunFileSize" value="'+maximunFileSize+'"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="maximunFileSizeStr" value="'+maximunFileSizeStr+'"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="maximunAllFileSize" value="'+maximunAllFileSize+'"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="maximunAllFileSizeStr" value="'+maximunAllFileSizeStr+'"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="maximumNumberOfFiles" value="'+maximumNumberOfFiles+'"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="currentAllFilesSize" value="0"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="accept" value="'+accept+'"/>');
//		ax5uploaderElem.push('			<input type="hidden" name="indexParams" value="'+accept+'"/>');
//		ax5uploaderElem.push('		</div>');
//		ax5uploaderElem.push('	</div>');
//		ax5uploaderElem.push('	<div class="fl-r" style="margin-top:10px;">');
//		if(insertBtn === true){
//			ax5uploaderElem.push('		<button type="button" data-addfile class="btn-dkGray mr-5">파일추가</button>');
//			ax5uploaderElem.push('		<button type="button" style="display:none;" data-ax5uploader-button="selector" class="btn-dkGray mr-5">파일추가</button>');
//		}else{
//			ax5uploaderElem.push('		<button type="button" style="display:none;" data-ax5uploader-button="selector" class="btn-dkGray mr-5">파일추가</button>');
//		}
//		if(deleteBtn === true){
//			ax5uploaderElem.push('		<button type="button" data-upload-btn="removeFile" class="btn-gray mr-5" style="display:none;">선택한 파일삭제</button>');
//		}
//		if(options.extraBtn){
//			ax5uploaderElem.push(options.extraBtn);
//		}
//		if(allFileDownBtn === true){
//			ax5uploaderElem.push('		<button type="button" data-upload-btn="allFileDownload" class="btn-gray mr-5" style="display:none;">전체 다운로드</button>');
//		}
//		ax5uploaderElem.push('	</div>');
//		ax5uploaderElem.push('</div>');
//		$(options.target.selector).append(ax5uploaderElem.join(""));
//		if(options.formId){
//			$(options.target.selector).find("input[name='indexParams']").val(commonUtils.formToJSON(options.formId));
//		}
//
//
//		$(options.target.selector).find('[data-addfile]').click(function(e){
//			if(ax5uploader["__uploading"]){
//				commonUtils.alert({
//					theme : "warning"
//						,title:"파일이 업로드중입니다."
//							,msg:"업로드가 끝나고 추가 할 수 있습니다."
//				});
//				e.stopImmediatePropagation();
//				return false;
//			}
//			if(options.formId){
//				if($("#"+options.formId).validForm()){
//					$(options.target.selector).find('[data-ax5uploader-button="selector"]').click();
//				}
//			}
//		});
//
///*		$(options.target.selector).find('[data-ax5uploader-button="selector"]').click(function(e){
//		});*/
//
//		var emptyTrColspan=5;
//		if(deleteBtn === true)emptyTrColspan+=2;
//		if(imgPreview === true)emptyTrColspan+=1;
//		var emptyTr='<tr id="emptyTr"><td colspan="'+emptyTrColspan+'" style="text-align: center;font-size: 15px;font-weight: bold;">'+(options.gridEmptyMsg?options.gridEmptyMsg:'첨부파일이 존재하지 않습니다.')+'</td></tr>';
//		var tableTmplStr=[];
//		tableTmplStr.push('<table class="table-list file-upload-table">');
//		tableTmplStr.push('	<colgroup>');
//		if(deleteBtn === true){
//			tableTmplStr.push('		<col style="width: 50px;">');
//			tableTmplStr.push('		<col style="width: 70px;">');
//		}
//		tableTmplStr.push('		<col style="width: 350px;">');
//		if(imgPreview === true)
//			tableTmplStr.push('		<col>');
//		tableTmplStr.push('		<col style="width: 100px;">');
//		tableTmplStr.push('		<col style="width: 75px;">');
//		tableTmplStr.push('		<col style="width: 150px;">');
//		tableTmplStr.push('		<col style="width: 70px;">');
//		tableTmplStr.push('	</colgroup>');
//		tableTmplStr.push('	<thead>');
//		tableTmplStr.push('		<tr>');
//		if(deleteBtn === true){
//			tableTmplStr.push('			<th>선택</th>');
//			tableTmplStr.push('			<th>상태</th>');
//		}
//		tableTmplStr.push('			<th>파일명</th>');
//		if(imgPreview === true)
//			tableTmplStr.push('			<th>미리보기</th>');
//		tableTmplStr.push('			<th>파일크기</th>');
//		tableTmplStr.push('			<th>확장자</th>');
//		tableTmplStr.push('			<th>등록일시</th>');
//		tableTmplStr.push('			<th>다운로드</th>');
//		tableTmplStr.push('		</tr>');
//		tableTmplStr.push('	</thead>');
//		tableTmplStr.push('	<tbody id="imgTableBody">');
//		tableTmplStr.push('	</tbody>');
//		tableTmplStr.push('</table>');
//		options.gridTarget.append(tableTmplStr.join(""));
//
//		var $uploadTable = options.gridTarget.find('table');
//
//		var $uploadTarget = $(options.target.selector).find('[data-ax5uploader="'+ax5uploaderId+'"]');
//
//		var uploadOptions={};
//		var uploadOptions=$.extend({
//            debug: false,
//    		maximunFileSize:maximunFileSize,
//    		maximunAllFileSize:maximunAllFileSize,
//            target: $uploadTarget,
//            form: {
//                action: "/cmm/fms/insertTempFileOutComeInfs.do",
//                fileName: "file"
//            },
//            accept:accept,
//            progressBox: true,
//            progressBoxDirection: "left",
//            multiple: false,
//            //manualUpload: true,
//            validateSelectedFiles: function () {
//				if(this.selectedFiles.length){
//					var size=0;
//					if(this.selectedFiles[0].size)
//						size = this.selectedFiles[0].size;
//					else if(this.selectedFiles[0][0].size)
//						size = this.selectedFiles[0][0].size;
//					if(size > maximunFileSize){
//						//alert(maximunFileSizeStr+' 보다 큰 파일은 업로드를 할 수가 없습니다.');
//						commonUtils.alert({
//							msg: maximunFileSizeStr+' 보다 큰 파일은 업로드를 할 수가 없습니다.'
//						})
//						return false;
//					}
///*					var fileName='';
//					if(this.selectedFiles[0].name)
//						fileName = this.selectedFiles[0].name;
//					else if(this.selectedFiles[0][0].name)
//						fileName = this.selectedFiles[0][0].name;*/
//
///*					if(fileName !== options.fileName){
//						commonUtils.alert({
//							msg: '성과품 파일명을 <b>'+options.fileName+'</b> 으로 올려주세요'
//						})
//						return false;
//					}*/
//				}
//
//            	var fileSum=0;
//            	var fileCnt=0;
//				$uploadTable.find('tbody tr').each(function(i,v){
//					if($(v).attr("id") !== 'emptyTr' && $(v).find('input[name="crudType"]').val() !== "D"){
//						fileCnt++;
//						fileSum+=Number($(v).find('input[name="fileSize"]').val());
//					}
//				});
//                // 지정된 파일제한개수 이상 업로드 되지 않도록 제한.
//                if((fileCnt + this.uploadedFiles.length + this.selectedFiles.length) > Number(maximumNumberOfFiles)){
//    				commonUtils.alert({
//    					msg : '첨부파일 개수는 최대 '+maximumNumberOfFiles+'개 까지 가능합니다.'
//    					,theme : 'warning'
//    				});
//                	return false;
//                }
//                $uploadTarget.find('input[name="currentAllFilesSize"]').val(fileSum.toFixed(0));
//                return true;
//            },
//            onprogress: function () {
//             	var progress = Math.floor(this.loaded / this.total * 100);
//            	ax5uploader.$progressBox.find(".progress").css('width',progress+'%');
//            	ax5uploader.$progressBox.find(".progress-bar-striped").css('width','0');
//            	ax5uploader.$progressBox.find("[data-percent-txt]").html(progress+'% 진행중...');
//            	if(progress === 100){
//                    //toast.confirm('서버에 파일을 올리는 중입니다.<br/>잠시만 기다려주세요.');
//            		ax5uploader.$progressBox.find("[data-percent-txt]").html(progress+'% 완료! 서버에서 파일을 처리하는 중입니다. 잠시만 기다려주세요');
//        			fileMask.open({
//        	        	target: $("#contentWrap"),
//        	            content: '<h1><i class="fa fa-spinner fa-spin"></i> 서버에서 파일을 처리 중 입니다. 잠시만 기다려주세요.</h1>'
//        	        });
//            	}
//            },
//            onuploaded: function () {
//            },
//            onuploaderror: function () {
//
//            	switch(this.self.status){
//            		case 401 :
//            			commonUtils.alert({
//            				theme : "error"
//    	            		,msg:"로그인이 필요한 서비스입니다."
//    	            	});
//            			break;
//            		case 403 :
//            			commonUtils.alert({
//            				theme : "error"
//    	            		,msg:"권한이 없습니다."
//    	            	});
//            			break;
//            		case 400 :
//            			commonUtils.alert({
//            				theme : "error"
//    	            		,msg:this.self.responseText
//    	            	});
//            			break;
//            		default :
//            			commonUtils.alert({
//            				theme : "error"
//    	            		,msg:"파일업로드 처리중 에러가 발생하였습니다."
//    	            	});
//            	}
//            },
//            onuploadComplete: function () {
//            	fileMask.close();
//            	if(toast.queue.length > 0) toast.close();
//            	switch(this.self.xhr.status){
//	        		case 400 :
//						commonUtils.alert({
//							theme : "error"
//							,msg:this.self.xhr.responseText
//						});
//						ax5uploader.removeFileAll();
//						break;
//            		case 401 :
//            			commonUtils.alert({
//            				theme : "error"
//    	            		,msg:"로그인이 필요한 서비스입니다."
//    	            	});
//	        			ax5uploader.removeFileAll();
//	        			break;
//	        		case 400 :
//            			commonUtils.alert({
//            				theme : "error"
//    	            		,msg:"권한이 없습니다."
//    	            	});
//	        			ax5uploader.removeFileAll();
//	        			break;
//            	}
//            	ACTIONS["UPDATE_uploaded"](this.self.uploadedFiles);
//            	if(this.self.xhr.status.toString().charAt(0) === "2"){
//            		if(toast){
//            			toast.push('임시 파일업로드가 완료 되었습니다.');
//            		}else{
//                        var toast2 = new ax5.ui.toast({
//                            containerPosition: "top-right",
//                            icon : '<i class="axi axi-upload2"></i>',
//                            width : 400
//                        });
//                        toast2.push('임시 파일업로드가 완료 되었습니다.');
//            		}
//            		if(options.onuploadCompleteCallback){
//            			options.onuploadCompleteCallback(this);
//            		}
//            	}
//            }
//        },options);
//
//		var ax5uploader=new ax5.ui.uploader(uploadOptions);
//		ax5uploader['getGrid'] = {
//			list : function(){
//				var objarr=[];
//				$uploadTable.find('tbody tr').each(function(i,v){
//					if($(v).attr("id") !== 'emptyTr'){
//						objarr.push({
//							crudType : $(v).find('input[name="crudType"]').val()
//							,searchCondition : $(v).find('input[name="fileSearchCondition"]').val()
//							,searchKeyword : $(v).find('input[name="fileSearchKeyword"]').val()
//							,fileCn : $(v).find('input[name="tempFileCn"]').val()
//							,atchmnflId : $(v).find('input[name="atchmnflIdTmp"]').val()
//							,fileSize : $(v).find('input[name="fileSize"]').val()
//							,fileSn : $(v).find('input[name="fileSn"]').val()
//							,tempFileChk : $(v).find('input[name="tempFileChk"]').val()
//							,streFileNm : $(v).find('input[name="streFileNm"]').val()
//							,streFlpth : $(v).find('input[name="streFlpth"]').val()
//						});
//					}
//				});
//				return objarr;
//			}
//		};
//		ax5uploader['clearAll'] = function(){
//			$uploadTable.find('tbody').empty().append(emptyTr);
//		};
//        /// ACTIONS
//        var ACTIONS = {
//            "INIT": function () {
//                // 컨트롤 버튼 이벤트 제어
//                uploadView.initView();
//            },
//            "GET_grid": function (data) {
//                return gridObj.getList(data);
//            },
//            "GET_uploaded": function () {
//				var data={};
//				if(options.atchmnflId && options.atchmnflId != null && options.atchmnflId !== ''){
//					data['atchmnflId']=options.atchmnflId;
//					// 업로드 파일 가져오기
//					commonUtils.ajax({
//						url: "/cmm/fms/selectFileInfsList.do",
//						data : $.param(data),
//						maskTarget : options.gridTarget,
//						success: function (response) {
//							uploadView.setData(response);
//						}
//					});
//				}else{
//					uploadView.setData({resultData:{resultList:[]}});
//				}
//            },
//            "DELETE_files": function (data) {
//            },
//            "UPDATE_uploaded": function (data) {
//            	$uploadTable.find("#emptyTr").remove();
//            	$.each(data,function(i,v){
//            		if(options.uploadedCallback){
//            			options.uploadedCallback(v.result);
//            		}
//            		$.each(v.result,function(i2,v2){
//            			var trTmplStr=[];
//            			var randomVal=Math.random().toString(36).slice(2);
//            			trTmplStr.push('<tr id="'+randomVal+'">');
//            			trTmplStr.push('	<input type="hidden" name="crudType" value="C">');
//            			trTmplStr.push('	<input type="hidden" name="fileSearchCondition" value="'+v2.searchCondition+'">');
//            			trTmplStr.push('	<input type="hidden" name="fileSearchKeyword" value="'+v2.searchKeyword+'">');
//            			trTmplStr.push('	<input type="hidden" name="tempFileCn" value="'+v2.fileCn+'">');
//            			trTmplStr.push('	<input type="hidden" name="atchmnflIdTmp" value="'+v2.atchmnflId+'">');
//            			trTmplStr.push('	<input type="hidden" name="fileSize" value="'+v2.fileSize+'">');
//            			trTmplStr.push('	<input type="hidden" name="fileSn" value="'+v2.fileSn+'">');
//            			trTmplStr.push('	<input type="hidden" name="tempFileChk" value="true">');
//            			trTmplStr.push('	<input type="hidden" name="streFlpth" value="'+v2.streFlpth+'">');
//            			trTmplStr.push('	<input type="hidden" name="streFileNm" value="'+v2.streFileNm+'">');
//            			if(deleteBtn === true){
//            				trTmplStr.push('	<td style="padding:0;"><input type="checkbox" name="imgCheckbox" value="'+randomVal+'"></td>');
//            				trTmplStr.push('	<td><span data-crudTxt>신규</span></td>');
//            			}
//            			trTmplStr.push('	<td class="ta-l" style="max-width: 350px; overflow: hidden;">'+v2.orignlFileNm+'</td>');
//
//            			if(imgPreview === true){
//            				if(imgFileExtsn.indexOf(v2.fileExtsn) !== -1){
//            					trTmplStr.push('	<td style="padding: 5px;"><img width="100%" height="100" style="cursor:pointer" src="/cmm/fms/FileDown.do?atchmnflId='+v2.atchmnflId+'&fileSn='+v2.fileSn+'&tempFileChk=true"/></td>');
//            				}else{
//            					trTmplStr.push('	<td>없음</td>');
//            				}
//            			}
//            			trTmplStr.push('	<td class="ta-l">'+ax5.util.number(v2.fileSize, {byte: true})+'</td>');
//            			trTmplStr.push('	<td class="ta-l">'+v2.fileExtsn+'</td>');
//            			trTmplStr.push('	<td class="ta-l">임시등록됨</td>');
//            			trTmplStr.push('	<td><span data-img-download data-ax5grid-cellholder="" data-ax5grid-text-align="center" style="height:25px;line-height: 19px; font-size: large; cursor:pointer;"><i class="fa fa-download" aria-hidden="true"></i></span></td>');
//            			trTmplStr.push('</tr>');
//            			$newTr=$(trTmplStr.join(""));
//
//            			$newTr.find('img').click(function(){
//            				var _this=$(this).clone();
//            				$(_this).css({width : '800'});
//            				$(_this).css({height : '600'});
//            				var modal=commonUtils.modal({title:'이미지미리보기',width:810,height:610});
//            				modal.open({},function(){
//            					this.$["body-frame"].html(_this);
//            				});
//            			});
//
//            			$newTr.find('[data-img-download]').click(function(){
//            				$parentTr=$(this).parents('tr');
//	                    	var downloadParamObj={};
//	                    	downloadParamObj={
//	                    		atchmnflId : $parentTr.find('input[name="atchmnflIdTmp"]').val()
//	                    		,fileSn : $parentTr.find('input[name="fileSn"]').val()
//	                    	}
//                    		if($parentTr.find('input[name="crudType"]').val() === "C")
//                    			downloadParamObj['tempFileChk'] = true
//	                    	fileUtils.fileDownload(downloadParamObj);
//            			});
//            			$uploadTable.find('tbody').append($newTr);
//            		});
//            	})
//            	ax5uploader.removeFileAll();
//            }
//        };
//        /// uploadView
//        var uploadView = {
//            initView: function () {
//            	$(options.target.selector).find("[data-upload-btn]").on('click',function(){
//            		switch($(this).attr("data-upload-btn")){
//            			case "removeFile" :
//            				var $checkedNode=$uploadTable.find('input[name="imgCheckbox"]:checked');
//            				if($checkedNode.length === 0)
//            					return commonUtils.alert({title : '확인',msg : '삭제하려는 항목을 체크해주세요!',theme : 'warning'});
//            				$checkedNode.each(function(i,v){
//            					var $currTr=$uploadTable.find("#"+v.value);
//            					if($currTr.find("input[name='crudType']").val() === "C"){
//            						//신규추가된 항목은 삭제처리
//            						$currTr.remove();
//            					}else{
//            						//기존에 존재하던 항목은 삭제 플래그만 변경
//            						$currTr.find("input[name='crudType']").val("D");
//            						$currTr.find("[data-crudTxt]").text('삭제');
//            						$currTr.find("[data-crudTxt]").parents('td').css('background','#f1f2f6');
//            						$currTr.find("[data-crudTxt]").parents('td').next().css('text-decoration','line-through');
//            						$currTr.css('display','none');
//            					}
//            				});
//            				if($uploadTable.find('tbody tr').length === 0){
//            					$uploadTable.find('tbody').empty().append(emptyTr);
//            				}
//            				$uploadTable.find('input[type=checkbox]').prop("checked",false);
//            				break;
//            			case "allFileDownload" :
//            				var filterData=[];
//            				$.each(ax5uploader.getGrid.list(), function(index,item){
//            					if(item.crudType !== 'D'){
//            						filterData.push(item);
//            					}
//            				});
//
//            				if(filterData.length === 0){
//                				commonUtils.alert({
//                					msg : '다운로드 대상 첨부파일이 존재하지 않습니다.'
//                					,theme : 'warning'
//                				});
//                            	return false;
//            				}
//            				//전체파일 다운로드
//            				fileUtils.allFileDownload({
//            					data : [{name:'fileDatas',value:JSON.stringify(filterData)}]
//            				});
//            				break;
//            		}
//            	})
//            },
//            setData: function (response) {
//            	var resultList=response.resultData.resultList;
//        		if(resultList.length === 0){ //목록이 존재하지 않을 경우 '데이터없음'을 표시한다.
//        			$uploadTable.find('tbody').empty().append(emptyTr);
//        		}else{
//        			$.each(resultList,function(i,v){
//            			var trTmplStr=[];
//            			var randomVal=Math.random().toString(36).slice(2);
//            			trTmplStr.push('<tr id="'+randomVal+'">');
//            			trTmplStr.push('	<input type="hidden" name="crudType" value="">');
//            			trTmplStr.push('	<input type="hidden" name="atchmnflIdTmp" value="'+v.atchmnflId+'">');
//            			trTmplStr.push('	<input type="hidden" name="fileSize" value="'+v.fileSize+'">');
//            			trTmplStr.push('	<input type="hidden" name="fileSn" value="'+v.fileSn+'">');
//            			trTmplStr.push('	<input type="hidden" name="tempFileChk" value="false">');
//            			trTmplStr.push('	<input type="hidden" name="streFlpth" value="'+v.streFlpth+'">');
//            			trTmplStr.push('	<input type="hidden" name="streFileNm" value="'+v.streFileNm+'">');
//            			if(deleteBtn === true){
//            				trTmplStr.push('	<td  style="padding:0;"><input type="checkbox" name="imgCheckbox" value="'+randomVal+'"></td>');
//            				trTmplStr.push('	<td><span data-crudTxt></span></td>');
//            			}
//            			trTmplStr.push('	<td style="max-width: 350px; overflow: hidden; text-align : left;">'+v.orignlFileNm+'</td>');
//
//            			if(imgPreview === true){
//            				if(imgFileExtsn.indexOf(v.fileExtsn) !== -1){
//            					trTmplStr.push('	<td style="padding: 5px;"><img width="100%" height="100"  style="cursor:pointer" src="/cmm/fms/FileDown.do?atchmnflId='+v.atchmnflId+'&fileSn='+v.fileSn+'"/></td>');
//            				}else{
//            					trTmplStr.push('	<td>없음</td>');
//            				}
//            			}
//            			trTmplStr.push('	<td>'+ax5.util.number(v.fileSize, {byte: true})+'</td>');
//            			trTmplStr.push('	<td>'+v.fileExtsn+'</td>');
//            			trTmplStr.push('	<td>'+v.lastUpdtDt+'</td>');
//            			trTmplStr.push('	<td><span data-img-download data-ax5grid-cellholder="" data-ax5grid-text-align="center" style="height:25px;line-height: 19px; font-size: large; cursor:pointer;"><i class="fa fa-download" aria-hidden="true"></i></span></td>');
//            			trTmplStr.push('</tr>');
//            			$newTr=$(trTmplStr.join(""));
//
//            			$newTr.find('img').click(function(){
//            				var _this=$(this).clone();
//            				$(_this).css({width : '800'});
//            				$(_this).css({height : '600'});
//            				var modal=commonUtils.modal({title:'이미지미리보기',width:810,height:610});
//            				modal.open({},function(){
//            					this.$["body-frame"].html(_this);
//            				})
//            			});
//
//            			$newTr.find('[data-img-download]').click(function(){
//            				$parentTr=$(this).parents('tr');
//	                    	var downloadParamObj={};
//	                    	downloadParamObj={
//	                    		atchmnflId : $parentTr.find('input[name="atchmnflIdTmp"]').val()
//	                    		,fileSn : $parentTr.find('input[name="fileSn"]').val()
//	                    	}
//                    		if($parentTr.find('input[name="crudType"]').val() === "C")
//                    			downloadParamObj['tempFileChk'] = true
//	                    	fileUtils.fileDownload(downloadParamObj);
//            			});
//            			$uploadTable.find('tbody').append($newTr);
//        			});
//        		}
//            }
//        };
//        ACTIONS["INIT"]();
//        ACTIONS["GET_uploaded"]();
//
//        if(ax5uploader.$progressUpload){
//        	ax5uploader.$progressUpload.css('display','none');
//        	ax5uploader.$progressAbort.text("중지");
//        	ax5uploader.$progressAbort.click(function(){
//        		if(toast.queue.length > 0){
//        			toast.close();
//        		}
//        		var toast2 = new ax5.ui.toast({
//        			containerPosition: "top-right",
//        			icon : '<i class="axi axi-upload2"></i>',
//        			width : 400
//        		});
//        		toast2.push('파일업로드를 중지 하였습니다.');
//        	})
//        }
//        ax5uploader.config.accept=accept;
//		return ax5uploader;
//	},
//	//개별 첨부파일 다운로드
//	fileDownload : function(options){
//		commonUtils.ajax({
//			url : '/cmm/fms/FileDownloadChk.do'
//			,data: $.param(options)
//			,success : function(response){
//				location.href='/cmm/fms/FileDown.do?'+$.param(options);
//			}
//		});
//	},
//	//전체 첨부파일 다운로드
//	allFileDownload : function(options){
//		commonUtils.ajax({
//			url : '/cmm/fms/allFileDownloadChk.do'
//			,data : options.data
//			,success : function(response){
//				var mask=commonUtils.pageSubmit({
//					url : '/cmm/fms/allFileDown.do'
//					,data : options.data
//				});
//				mask.close();
//			}
//		});
//	},
//	//파일ID에 해당하는 전체 첨부파일 다운로드
//	atchmnflIdDownload : function(atchmnflId){
//		commonUtils.ajax({
//			url : '/cmm/fms/selectFileInfsList.do'
//				,data : [{name:'atchmnflId',value:atchmnflId}]
//				,success : function(response){
//    				//전체파일 다운로드
//    				fileUtils.allFileDownload({
//    					data : [{name:'fileDatas',value:JSON.stringify(response.resultData.resultList)}]
//    				});
//				}
//		});
//	}
//}
