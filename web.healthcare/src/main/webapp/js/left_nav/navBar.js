function navSwitchShowmore(id){
    if($("#"+id+" .secNavBar_ul").is(":visible")){
        $("#navBar_ul li").removeClass("on");
        $("#"+id).removeClass("on");
    }else{
    	$("#navBar_ul li").removeClass("on");
        $("#"+id).addClass("on");
    }
}

/**2021-07-28 원준식*/
function mainNavSelect(id){
    if($("#"+id+" .subIdxUl").is(":visible")){
        $("#"+id+" .subIdxUl").stop().slideUp(100);
    }else{
        $(".mainIdxSelect .subIdxUl").slideUp(100);
        $("#"+id+" .subIdxUl").stop().slideDown(100);
    }
}
