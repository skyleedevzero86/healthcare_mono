function typeCheck(searchWrd){
    if(searchWrd.includes('temperature')){
       // searchType = 'all';
        condition = '30';
        charttype = 'bar';
        filledcolor = '#F87575';
        fill = true;
        startTime = '00:00'
        title = '체온';
        ymin = 20;
        ymax = 45;
    }else if(searchWrd.includes('spo2')){
       // searchType = 'all';
        condition = '60';
        charttype = 'bar';
        filledcolor = 'blue';
        fill = true;
        startTime = '00:00'
        title = '산소포화도'
        ymin = 0;
        ymax = 100;
    }else if(searchWrd.includes('sleep')){
        //searchType = 'all';
        condition = '60';
        charttype = 'bar';
        startTime = '00:00'
        filledcolor = '#9B6FC8';
        fill = true;
        title = '수면';
        ymin = 0;
        ymax = 24;
    }else if(searchWrd.includes('heartrate')){
       // searchType = 'all';
        condition = '10';
        charttype = 'line';
        filledcolor = '#FFBBBB';
        startTime = '00:00'
        fill = false;
        title = '심박수';
        ymin = 0;
        ymax = 200;
    }else if(searchWrd.includes('step')){
        //searchType = 'avg';
        condition = '60';
        charttype = 'bar';
        startTime = '00:00'
        filledcolor = '#BBEEBA';
        fill = true;
        title= '걸음수';
    }else if(searchWrd.includes('stress')){
       // searchType = 'all';
        condition = '30';
        filledcolor = '#BCBCBC';
        startTime = '00:00'
        charttype = 'line';
        fill = true;
        title = '스트레스';

        ymin = 0;
        ymax = 100;
    }else if(searchWrd.includes('bloodpress_max')){
        //searchType = 'minmax';
        condition = '5';
        startTime = '00:01'
        charttype = 'line';
        filledcolor = '#7A7ABA';
        fill = false;
        title='혈압';

        ymin = 0;
        ymax = 200;
    }else if(searchWrd.includes('sp2_max')){	// 호흡수
        //searchType = 'all';
        condition = '30';
        charttype = 'line';
        filledcolor = 'purple';
        fill = true;
        title='호흡수';
        startTime = '00:00'
        ymin = 0;
        ymax = 100;
    }else {
        charttype = 'line';
        fill = false;
        filledcolor = 'blue';
        startTime = '00:00'
        title='건강점수';
        ymin = 0;
        ymax = 100;
    }
}

function changeSearchWrd(searchType){
    if(searchType == 'D') {
       category = searchWrd;
    }
    else {
        if(searchWrd == 'temperature'){
           category = 'temperature_max' ;
        }else if(searchWrd == 'spo2'){
           category = 'spo2_max';
        }else if(searchWrd == 'sleep'){
           category = 'sleep' ;
        }else if(searchWrd == 'heartrate'){
           category = 'heartrate_max';
        }else if(searchWrd == 'step'){
           category = 'step';
        }else if(searchWrd == 'stress'){
           category = 'stress_max';
        }else if(searchWrd == 'bloodpress'){
           category = 'bloodpress_max';
        }else{	// 호흡수
           category = 'repiratory_max';
        }
    }
    return category;
}