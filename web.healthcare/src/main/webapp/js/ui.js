
	// 상단메뉴 고정
$(document).on("scroll",function(){
			if($(document).scrollTop()>50){ 
				$("header").removeClass("large").addClass("small");
				}
			else{
				$("header").removeClass("small").addClass("large");
				}
			});

	// 모바일 퀵메뉴 아코디언 효과
jQuery(document).ready(function () {
	jQuery("#jquery-accordion-menu").jqueryAccordionMenu();
	
});

$(function(){	
	//
	$("#demo-list li").click(function(){
		$("#demo-list li.active").removeClass("active")
		$(this).addClass("active");
	})	
})	



// j쿼리 js 파일 인크루드 

function include(file)   
{   
  var script  = document.createElement('script');   
  script.src  = file;   
  script.type = 'text/javascript';   
  script.defer = true;   
  
  document.getElementsByTagName('head').item(0).appendChild(script);     
}  


	// 패밀리사이트
$(document).ready(function () {
	$('.familysite button').on('mouseenter focus', function() {
		$('.familysite dd').show();
		var famTop = $('.familysite dd').outerHeight() - 1;
		$('.familysite dd').css({'top' : -1*famTop});
	});
	$('.familysite button').on('click', function() {
		$('.familysite dd').toggle();
		var famTop = $('.familysite dd').outerHeight() - 1;
		$('.familysite dd').css({'top' : -1*famTop});
	});
	$('.familysite').on('mouseleave', function() {
		$('.familysite dd').hide();
	});
	$('#policy a, #logo_btm a').on('focus', function() {
		$('.familysite dd').hide();
	});

});
