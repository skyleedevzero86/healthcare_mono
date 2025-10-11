//jquery 확장 옵션 정의
(function(){
	//달력 요일명 지정
    ax5.info.weekNames = [
                          {label: "일"},
                          {label: "월"},
                          {label: "화"},
                          {label: "수"},
                          {label: "목"},
                          {label: "금"},
                          {label: "토"}
                      ];
    ax5.info.months = ["1월","2월","3월","4월","5월","6월","7월","8월","9월","10월","11월","12월"];
    
	//폼 입력값 검증
	$.fn.validForm = function(options) {
		try{
			var validMsg='';
			var validResult=false;
			this.find("[data-validate-type]").each(function(i,v){
				var validArr=$(v).attr('data-validate-type').replace(/ /gi, "").split(',');
				if($(v).is('input')){
					if($(v).is('[type="text"]') || $(v).is('[type="hidden"]') || $(v).is('[type="password"]')){
						$(validArr).each(function(j,validType){
							if(validType.toLowerCase() === 'notnull'){
								if($(v).val() == null || $(v).val() == ''){
									validResult=true;
									validMsg = $(v).attr('title') + ' 은/는 필수 입력항목입니다.'; 
								}
							}
						});
					}else if($(v).is('[type="radio"]')){
						if(!$(':radio[name="'+$(v).attr("name")+'"]:checked').val() || $(':radio[name="'+$(v).attr("name")+'"]:checked').val() === ""){
							validResult=true;
							validMsg = $(v).attr('title') + ' 을/를 선택해주세요'; 
						}
					}else if($(v).is('[type="checkbox"]')){
						if(!$(':checkbox[name="'+$(v).attr("name")+'"]:checked').val() || $(':checkbox[name="'+$(v).attr("name")+'"]:checked').val() === ""){
							validResult=true;
							validMsg = $(v).attr('title') + ' 을/를 체크해주세요'; 
						}
					}
				}
				if($(v).is('select')){
					if(!$(v).find(':selected').attr('data-ignore-validate')){
						if(!$(v).val() || $(v).val() === "" || $(v).val() === "선택"){
							validResult=true;
							validMsg = $(v).attr('title') + ' 을/를 선택해주세요'; 
						}
					}
				}
				if($(v).is('textarea')){
					var textarea = $.trim($(v).val());
					if(textarea.length == 0){
						validResult=true;
						validMsg = $(v).attr('title') + ' 은/는 필수 입력항목입니다.';						
					}
				}
				if(validResult){
					if(validMsg){
						if(options && options.tabClass){
							$("."+options.tabClass+" > li").removeClass("active");
							$("."+options.tabShowId).hide();
							$(v).parents("."+options.tabShowId).show();
							if($(v).parents("."+options.tabShowId).attr('id')){
								var idx=Number($(v).parents("."+options.tabShowId).attr('id').replace(/[^0-9]/g,''));
								if(!isNaN(idx)){
									if($("#"+options.liId+(idx+1)).length > 0){
										$("#"+options.liId+(idx+1)).addClass("active");
									}
								}
							}
						}
						commonUtils.alert({
							theme : 'warning',
							title:'입력값을 확인해 주세요',
							msg:validMsg,
							elem:$(v)
						});
						return false;
					}
				}
			});
			return !validResult;
		}catch(e){
			alert('validate 스크립트 오류가 발생하였습니다.');
		}
	}
	
    $.fn.handlebars = {
    	compile : function(template, data){
    		if (template instanceof jQuery) {
				template = $(template).html();
			}
			var compiled = Handlebars.compile(template);
    		return $('<output>').append(compiled(data));
    	},
    	render : function(target, data){
    		if (data instanceof jQuery) {
    			target.html(data.html());
    			$('[data-ax5formatter]').ax5formatter();
			}
    	}
    }
    
    //폼 입력값 초기화
    $.fn.clearForm = function() {
        return this.each(function() {
            var type = this.type, tag = this.tagName.toLowerCase();
            if (tag === 'form'){
                return $(':input',this).clearForm();
            }
            if (type === 'text' || type === 'password' || tag === 'textarea'){
                this.value = '';
            }else if (type === 'checkbox' || type === 'radio'){
                this.checked = false;
            }else if (tag === 'select'){
            	$(this).find("option:eq(0)").prop('selected',true);
            }
        });
    };
    
    //입력 패턴(마스크) 처리
	$.fn.inputpattern = function() {
		this.each(function() {
			switch ($(this).attr("data-input-pattern")){
				case "email" : 
					$(this).inputmask({
						  mask: "*{1,20}[.*{1,20}][.*{1,20}][.*{1,20}]@*{1,20}[.*{2,6}][.*{1,2}]",
						  greedy: false,
						  onBeforePaste: function (pastedValue, opts) {
						    pastedValue = pastedValue.toLowerCase();
						    return pastedValue.replace("mailto:", "");
						  },
						  definitions: {
						    '*': {
						      validator: "[0-9A-Za-z!#$%&'*+/=?^_`{|}~\-]",
						      cardinality: 1,
						      casing: "lower"
						    }
						  }
						});					
					break;
			}
		});
	};
	
	//달력 팝업
	$.fn.commonCalender = function(){
		this.each(function() {
			switch ($(this).attr("data-ax5picker")){
				case "calendar" :
				case "calendar2": 
					$(this).ax5picker({
				        direction: "auto",
			            btns: {
			                ok: {label: "선택", theme: "btn-info"}
			            },
				        content: {
				            width: 270,
				            margin: 10,
				            type: 'date',
				            config: {
				                control: {
				                    left: '<i class="fa fa-chevron-left"></i>',
				                    yearTmpl: '%s',
				                    monthTmpl: '%s',
				                    right: '<i class="fa fa-chevron-right"></i>'
				                },
				                lang: {
				                    yearTmpl: "%s년",
				                    dayTmpl: "%s"
				                },
				                marker: (function () {
				                    var marker = {};
				                    marker[ax5.util.date(new Date(), {'return': 'yyyy-MM-dd', 'add': {d: 0}})] = true;
				                    return marker;
				                })()
				            },
				            formatter: {
				                pattern: 'date'
				            }
				        }
				    });				
					break;
				case "calendar-month" :
					$(this).ax5picker({
				        direction: "auto",
				        content: {
				            width: 270,
				            margin: 10,
				            type: 'date',
				            config: {
				                control: {
				                    left: '<i class="fa fa-chevron-left"></i>',
				                    yearTmpl: '%s',
				                    monthTmpl: '%s',
				                    right: '<i class="fa fa-chevron-right"></i>'
				                },
				                lang: {
				                    yearTmpl: "%s년",
				                    dayTmpl: "%s"
				                }
				                ,mode: "month", selectMode: "month"
				            },
				            formatter: {
				                pattern: 'date(month)'
				            }
				        }
				    });						
					break;
				case "calendar-year" :
					$(this).ax5picker({
						direction: "auto",
						content: {
							width: 270,
							margin: 10,
							type: 'date',
							config: {
								lang: {
									yearTmpl: "%s년"
								},
								mode: "year", selectMode: "year"
							},
							formatter: {
								pattern: 'date(year)'
							}
						}
					});						
					break;
			}
		});		
	}

})();