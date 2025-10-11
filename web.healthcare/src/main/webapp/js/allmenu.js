$(document).ready(function(){
	//top menu
	$(function(){
		var top_mn = $('#top_mn'),
			dep1 = top_mn.find('.dep1 span a'),
			sub = top_mn.find('.dep1'),
			dep2 = sub.find('li a');

		function onOffSub(elem) {
			sub.css('overflow','hidden');
			if (elem) elem.css('overflow', 'visible');
		}
		function onOffDep1(elem, add) {
			add? elem.addClass('active') : elem.removeClass();
		}

		dep2.on({
			'focus': function () {
				var $this = $(this),
					dep1 = $this.parents('.dep1');

				onOffSub(dep1);
				if (/^(dep2)/.test($this.parent().attr('class'))) {
					onOffDep1(dep1.find('span'), true);
				}
			},
			'blur': function () {
				var $this = $(this);

				onOffSub();
				if (/^(dep2)/.test($this.parent().attr('class'))) {
					onOffDep1($this.parents('.dep1').find('span'));
				}
			}
		});
		dep1.on({
			'mouseover': function () {
				var $this = $(this),
					dep1 = $this.parents('.dep1');
				onOffSub(dep1);
			},
			'mouseout': function () {
				var $this = $(this);
				onOffSub();
			}
		});
		sub.find('ul').hover(
			function () {
				var $this = $(this);
				onOffSub($this.parent());
				onOffDep1($this.prev(), true);
			},
			function () {
				var $this = $(this);
				onOffSub();
				onOffDep1($this.prev());
			}
		);

	});
	//gnb
	$(function(){
		var dep1 = $("#gnb ul.dep1 > li");
		var dep2 = $("#gnb ul.dep2 li a");

		//mouseover
		dep1.bind("mouseover",function(){
			func_dep1(dep1.index(this));
		});

		func_dep1 = function(depth1){
			for(i=0;i<dep1.length;i++){
				if(depth1==i){
					$(dep1[depth1]).addClass("on");
				}else if(depth1!=i){
					$(dep1[i]).removeClass("on");
				}
			}
		}

		//keyboard focus
		$("> a", dep1).bind("mouseover focus",function(){
			var idx = $("> a", dep1).index(this);
			for(i=0;i<$("> a", dep1).length;i++){
				if(idx==i){
					$($("> a", dep1)[idx]).parent().addClass("on");
//					$("#gnb").stop(true).animate({height:"400px"},500);
					$("#gnb").stop(true).animate({height:"420px"},500);
				}else if(idx!=i){
					$($("> a", dep1)[i]).parent().removeClass("on");
				}
			}

		}).bind("mouseleave blur",function(){
			for(i=0;i<$("> a", dep1).length;i++){
				$($("> a", dep1)[i]).parent().removeClass("on");
			}
			$("#gnb").stop(true).animate({height:"100px"},500);
		});
		dep2.bind("mouseover focus",function(){
			var idx = dep2.index(this);
			for(i=0;i<dep2.length;i++){
				if(idx==i){
					$(dep2[idx]).parent().addClass("on");
				}else if(idx!=i){
					$(dep2[i]).parent().removeClass("on");
				}
			}
			$(this).parents('ul').parent().addClass("on");
			$("#gnb").stop(true).animate({height:"400px"},500);
			$("#gnb").stop(true).animate({height:"420px"},500);
		}).bind("mouseleave blur",function(){
			for(i=0;i<dep2.length;i++){
				$(dep2[i]).parent().removeClass("on");
			}
			for(i=0;i<$("> a", dep1).length;i++){
				$($("> a", dep1)[i]).parent().removeClass("on");
			}
			$(this).parents('ul').parent().removeClass("on");
			$("#gnb").stop(true).animate({height:"100px"},500);
		});

		//gnb
		$("#gnb").bind("mouseleave",function(){
			if(depth1!=null){
				$(this).stop().animate({height:"100px"},500);
				func_dep1(depth1);
			}else{
				$(this).stop().animate({height:"100px"},500,function(){
					$(this).css("height","100px")
				});
				for(i=0;i<dep1.length;i++){
					$(dep1[i]).parent().removeClass("on");
				}
			}
		}).bind("mouseover",function(){
			$(this).stop().animate({height:"400px"},300);
			$(this).stop().animate({height:"420px"},300);
		});

		//depth1 setting
		func_dep1(depth1);
	});






});
