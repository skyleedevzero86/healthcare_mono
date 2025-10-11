$(document).ready(function(){

	$(".js-scroll").mCustomScrollbar();
	
	// 탭 - a
	$(".js-a-tab a").on("click", function(){
		var thisTarget = $(this).attr("data-target");
		$(this).addClass("_active");
		$(this).siblings().removeClass("_active");
		$(thisTarget).addClass("_open");
		$(thisTarget).siblings().removeClass("_open");
	});

	// LNB open
	$(".lnb a[class^=menu-item]").on("click", function(){
		$(this).parent().toggleClass("on");
		$(this).next().toggleClass("_active");
		$(this).parent().siblings().removeClass("on");
		$(this).parent().siblings().find("ul").removeClass("_active");
	});

	// 팝업
	$(".js-pop-open").on("click", function(){
		var thisPopup = $(this).attr("data-target");
		$(thisPopup).show();
	});
	$(".js-pop-close, .dimmed").on("click", function(){
		var thisPopup = $(this).attr("data-target");
		$(thisPopup).hide();
	});
});