// // JavaScript Document

$(function(){
    $(document).on('click', '.map-area .chart-box>ul>li', function(){
    	var idx = $(this).index();
        if($(this).hasClass('active')){
            $(this).removeClass('active').siblings().removeClass('disabled');
            $('.map-area .wrap>div.chart-left>.map-box>.canvus>ul>li').removeClass('on').removeClass('disabled');
        }else{
            $(this).addClass('active').removeClass('disabled').siblings().addClass('disabled').removeClass('active');
            $('.map-area .wrap>div.chart-left>.map-box>.canvus>ul>li').eq(idx).addClass('on').siblings().addClass('disabled').removeClass('on');
        }
    });
    
    $(document).on('click', '.chart-right-bottom>ol>li', function(){
            $(this).addClass('on').siblings().removeClass('on');
            var bsnsSeCd = "00"+this.value;
            var rddYearCnt = null;
            bsnsSeCd == '000' ? rddYearCnt = fn_getBsnsListCnt() : rddYearCnt = fn_getBsnsListCnt(bsnsSeCd);
        	fn_bsnsYearListTable(rddYearCnt);
    });

    $('section.banner p.control a,section.partner p.control a').on('click',function(e){
        e.preventDefault();
    });

    /* 2022-12-05 하단 배너 주석처리*/
   /* setBannerInfinite('.partner-banner',true);*/
    // 메인페이지에서 배너 있는 경우 
    if($(".banner-bottom").length){
    	setBannerInfinite('.banner-bottom',false);
    }
    
	function setBannerInfinite(selector,status) {
        var numBanner = $(selector).find('ul.banner li').length;
        var bannerNow = 0;
        var bannerPrev = 0;
        var bannerNext = 0;
        var widthBox = $(selector).find('> div.box').innerWidth();
        var widthBar = 0;
        var offsetLeft = 0;
        var minNumSide = 0;
        var timerId = null;
        var isTimerOn = status;
        var timerSpeed = 3000;
        
        $(selector).find('ul.banner li').each(function(i) {
            widthBar += $(this).outerWidth(true);
            $(this).attr({'data-num': (i + 1)});
        });
        $(selector).find('ul.banner').css({'width': widthBar + 'px'});
        if (isTimerOn === true) {
            $(selector).find('a.play').addClass('on');
        } else {
            $(selector).find('a.play').removeClass('on');
        }
        checkMinNumSide();
        showBanner(1);
        
        // 이벤트
        $(selector).find('ul.indicator li a').on('click', function() {
            var index = $(selector).find('ul.indicator li').index($(this).parent());
            showBanner(index + 1);
        });
        $(selector).find('a.prev').on('click', function() {
            showBanner(bannerPrev);
        });
        $(selector).find('a.next').on('click', function() {
            showBanner(bannerNext);
        });
        $(selector).find('a.play').on('click', function() {
            if (isTimerOn === true) {
                clearTimeout(timerId);
                $(this).removeClass('on');
                isTimerOn = false;
            } else {
                timerId = setTimeout(function() {showBanner(bannerNext);}, timerSpeed);
                $(this).addClass('on');
                isTimerOn = true;
            }
        });
        $(window).on('resize', function() {
            widthBox = $(selector).find('> div.box').innerWidth();
            checkMinNumSide(bannerNow);
            showBanner(bannerNow);
        });
        
        function showBanner(n) {
            clearTimeout(timerId);
            checkVisibility(n);
            var $bannerNow = null;
            $(selector).find('ul.banner li').each(function() {
                if (Number($(this).attr('data-num')) === n) {
                    $bannerNow = $(this);
                    return false;
                }
            });
            offsetLeft = -$bannerNow.position().left + (widthBox / 2) - $bannerNow.outerWidth() / 2;
            if (bannerNow === 0) {
                $(selector).find('ul.banner').css({'transition': 'none', 'left': offsetLeft + 'px'});
            } else {
                $(selector).find('ul.banner').css({'transition': 'left 0.5s', 'left': offsetLeft + 'px'});
            }
            $(selector).find('ul.banner li').removeClass('on');
            $bannerNow.addClass('on');
            $(selector).find('ul.indicator li').removeClass('on');
            $(selector).find('ul.indicator li:eq(' + (n - 1) + ')').addClass('on');
            bannerNow = n;
            bannerPrev = (n - 1) < 1 ? numBanner : n - 1;
            bannerNext = (n + 1) > numBanner ? 1 : n + 1;
            if (isTimerOn === true) {
                timerId = setTimeout(function() {showBanner(bannerNext);}, timerSpeed);
            }
        }
        
        function checkMinNumSide() {
            var widthBox = $(selector).find('> div.box').innerWidth();
            var sumWidth = 0;
            $(selector).find('ul.banner li').each(function(i) {
                sumWidth += $(this).outerWidth(true);
                if (sumWidth > widthBox) {
                    minNumSide = Math.floor((i + 1) / 2);
                    return false;
                }
            });
        }
        
        // 앞뒤로 minNumSide 갯수 만큼의 banner가 존재하도록 재배치
        function checkVisibility(n) {
            var $bannerNow = null;
            $(selector).find('ul.banner li').each(function() {
                if (Number($(this).attr('data-num')) === n) {
                	console.log(JSON.stringify($(this)));
                    $bannerNow = $(this);
                    return false;
                }
            });
            for (var i = $bannerNow.prevAll().length; i < minNumSide; i++) {
                $bannerNow.parent().prepend($(selector).find('ul.banner li:last').clone());
                $(selector).find('ul.banner li:last').remove();
                offsetLeft -= $(selector).find('ul.banner li:eq(0)').outerWidth(true);
                $(selector).find('ul.banner').css({'transition': 'none', 'left': offsetLeft + 'px'});
            }
            for (var i = $bannerNow.nextAll().length; i < minNumSide; i++) {
                $bannerNow.parent().append($(selector).find('ul.banner li:eq(0)').clone());
                $(selector).find('ul.banner li:eq(0)').remove();
                offsetLeft += $(selector).find('ul.banner li:last').outerWidth(true);
                $(selector).find('ul.banner').css({'transition': 'none', 'left': offsetLeft + 'px'});
            }
        } 
    }
});//jQb_area