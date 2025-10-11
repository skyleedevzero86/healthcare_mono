/*******************************************************************************
 * 공통 스크립트
 *******************************************************************************/
/**
 * sendAjax; 
 * - method : POST/GET 
 * - url : 호출 url 
 * - dataType : 전송받을 데이타 형식(text, json, html)
 * - data : data
 * - fileYn : true/false
 * - fun : 콜백함수
 */
 
function sendAjax() {

	if (arguments.length < 6) {
		alert("arguments 값을 확인하시기 바랍니다.\n1. method(POST/GET)\n2. url\n3. form id\n4. parameter\n5. fileYn(true/false)\n6. 콜백함수");
		return;
	}

	var method = arguments[0];
	var url = arguments[1];
	var dataType = arguments[2];
	var data = arguments[3];
	var fileYn = arguments[4];
	var fun = arguments[5];
	
	$.ajax(ajaxOption(method,url,dataType,data,fileYn,fun));
}
function ajaxOption(method,url,dataType,data,fileYn,fun) {
	var contentTp = "application/x-www-form-urlencoded; charset=UTF-8";
	var processDt = true;
	if (fileYn) {
		contentTp = false;
		processDt = false;
	}

	var options = {
		type : method,
		url : url,
		dataType : dataType,
		async: false,
		data : data,
		processData: processDt,
		contentType : contentTp,
		success : function(data) {
			if (fun != "") {
				fun(data);
			}
		},
		beforeSend: function(){
			$('#loading').show();
		},
		complete: function(){
			$('#loading').hide();
		},
		error : function(request, status, error) {
			$("#loading").hide();
			fn_modalAlert('처리 중 오류가 발생하였습니다.');
		}
	}
	return options;
}
/*******************************************************************************
 * STRING FUNCTION
 *******************************************************************************/

//----------------------------------------------------------------------------
// 왼쪽 공백문자만 없앰.
//----------------------------------------------------------------------------
function ltrim (str){
	if (str == null) return "";

	var dest = str;
	var pos = 0;

	while (dest.charAt(pos) == " ") pos++;
	dest = dest.substring(pos, dest.length);

	return dest;
}

//----------------------------------------------------------------------------
// 오른쪽 공백문자만 없앰
//----------------------------------------------------------------------------
function rtrim (str) {
	if (str == null) return "";

	var dest = str;
	var pos = dest.length-1;

	while (dest.charAt(pos) == " ") pos--;

	dest = dest.substring(0, pos+1);

	return dest;
}


//----------------------------------------------------------------------------
// 좌우 공백문자를 없앰.
//----------------------------------------------------------------------------
function trim (str) {
	if (str == null) return "";

	var dest = str;

	// 왼쪽 공백 제거
	var pos = 0;
	while (dest.charAt(pos) == " ") pos++;
	dest = dest.substring(pos, dest.length);

	// 오른쪽 공백 제거
	pos = dest.length-1;
	while (dest.charAt(pos) == " ") pos--;
	dest = dest.substring(0, pos+1);

	return dest;
}


//----------------------------------------------------------------------------
// 공백문자를 모두 없앰.
//----------------------------------------------------------------------------
function trimall (str) {
	if (str == null) return "";

	var dest = str;

	for (; dest.indexOf(" ") != -1 ;) dest = dest.replace(" ","") ;

	return dest;
}

//----------------------------------------------------------------------------
// 문자열(str)에서 특정문자열 X를 Y로 모두 바꿈.
//----------------------------------------------------------------------------
function replaceStr(str,x,y) {
	var d = str;
	var i = 0;
	var l = x.length;

	while(true){
		i = d.indexOf(x);
		if(i<0) break;
		d = d.substring(0,i) + y + d.substring(i+l, str.length);
	}

	return d;
}

//----------------------------------------------------------------------------
//문자열(str)에서 특수문자 제거.
//----------------------------------------------------------------------------
function regExp(str) {
	// 특수문자 검증 start
	var regExpStr = /[\{\}\[\]\/?.,;:|\)*~`!^\-_+<>@\#$%&\\\=\(\'\"]/gi
	if (regExpStr.test(str)) {
		// 특수문자 제거
		str = str.replace(regExpStr, "")
	}
	return str;
}



// ----------------------------------------------------------------------------
// 문자열(str)에서 왼쪽n문자열를 리턴
//----------------------------------------------------------------------------
function leftStr(str, n)
{
	var i;
	
	if(n >= 0){
		i = str.substring(0,n);
	}else{
		i = leftStr(str,str.length + n);
	}
	
	return i;
}

//----------------------------------------------------------------------------
// 문자열(str)에서 오른쪽n문자열를 리턴
//----------------------------------------------------------------------------
function rightStr(str,n)
{
	var i;
	
	if(n >= 0){ 
		i = str.substring(str.length-n, str.length);
	}else{ 
		i = rightStr(str, str.length+n);
	}
	
	return i;
}

//----------------------------------------------------------------------------
// 문자열(str)의 왼쪽p문자 부터 길이n의 문자열을 리턴
//----------------------------------------------------------------------------
function midStr(str,p,n)
{
	return str.substring(p-1, p+n-1);
}


//----------------------------------------------------------------------------
// 문자열(str)의 BYTE수 리턴
//----------------------------------------------------------------------------
function getStrByte(str)
{
    var len = 0 ;

    for(i=0; i<str.length; i++){
        if (str.charCodeAt(i) > 255) len += 2;
        else len ++;
    }

    return len;
}


//----------------------------------------------------------------------------
// 문자열(str)에서 왼쪽 BYTE수 n만큼의 문자열을 리턴
//----------------------------------------------------------------------------
function getByteLeft(str, n)
{
    var len = str.length ;
    var str1= "", str2= "" ;

    for(i=0; i<len; i++){
		if (str.charCodeAt(i) > 255) str1 += str.charAt(i) + " ";
		else str1 += str.charAt(i) ;
    }

	if(str1.charCodeAt(n-1) > 255) n --;

    for(i=0; i<n; i++)
        if (str1.charAt(i) != " " ) str2 += str1.charAt(i) ;

    return str2 ;
}


//----------------------------------------------------------------------------
// Char -> ASCII 로 변환
//----------------------------------------------------------------------------
function Chr2Asc(c) {
	var Alpha = ' !"#$%& ()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[ ]^_`abcdefghijklmnopqrstuvwxyz{|}~';

	if (c == "'") return 39;
	if (escape(c) == "%5C") return 92;
	if (Alpha.indexOf(c) < 0) return Alpha.indexOf(c);

	return Alpha.indexOf(c) + 32;
}


//----------------------------------------------------------------------------
// ASCII -> Char 로 변환
//----------------------------------------------------------------------------
function Asc2Chr(c)
{  var Alpha = ' !"#$%& ()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[ ]^_`abcdefghijklmnopqrstuvwxyz{|}~';

   if (c == 39) return "'";
   if (c == 92) return unescape("%5C");
   return Alpha.charAt(c - 32);
}


//----------------------------------------------------------------------------
// 문자열str내에서 delemeter로 구분하여 idx번째 있는 것을 return
//----------------------------------------------------------------------------
function getToken(strSrc, idx, delemeter) {
  var str = strSrc;
  var cnt = 0;
  var tok = "";

  if (delemeter == null) var delemeter = ",";


  for (var i=0; i<str.length; i++) {
  	if (str.substring(i,i+1) == delemeter) cnt++;
       else if (cnt == idx-1) tok = tok + str.substring(i,i+1);
       if (cnt >= idx) break;

  }
  return tok;
}

//----------------------------------------------------------------------------
//문자열str내에서 delemeter로 구분하여 배열에 담아 return
//----------------------------------------------------------------------------
function getTokens(strSrc, delemeter) {
  var str = strSrc;
  var cnt = 0;
  var tok = "";

  if (delemeter == null) var delemeter = ",";


  for (var i=0; i<str.length; i++) {
	if (str.substring(i,i+1) == delemeter) cnt++;
  }
  cnt++;

  tok = new Array(cnt);

  for (var i=0, j=0, k=0; i<str.length; i++) {
  	if (str.substring(i,i+1) == delemeter) {
  		tok[k++] = str.substring(j, i);
  		j = i+1;
  	}
  }
  tok[k] = str.substring(j, i);
  return tok;
}

//----------------------------------------------------------------------------
// 자릿수len만큼 문자열str에 숫자 0을 채워서 리턴
//----------------------------------------------------------------------------
function fullZero(str, len)
{
    var l = str.length ;
    var s = "" ;

    if(len == "") len = "0";

    if(len != 0){
        l = Number(len) - Number(l);

        for(i=0; i<l; i++) s += "0";
        s = s + str ;
    }

    return s ;
}
function fullZeroL(str, len)
{
    var l = str.length ;
    var s = "" ;

    if(len == "") len = "0";

    if(len != 0){
        l = Number(len) - Number(l);

        for(i=0; i<l; i++) s = "0" + s;
        s = s + str ;
    }

    return s ;
}

//----------------------------------------------------------------------------
// 문자열str에 앞의 0을 제거 후 리턴
//----------------------------------------------------------------------------
function delZero(str)
{
    var l = str.length ;

    for(i=0; i<l; i++)
    	if (str.substring(i, i+1) == "0") str = str.substring(1, str.length);
    	else break;

    return str;
}

//----------------------------------------------------------------------------
//문자열 길이 제한
//param : 객체, 제한길이
//----------------------------------------------------------------------------
function checkByteLength(formObj, maxlength)
{
	var len = getByteLength(formObj.value);
	if(len > maxlength)
	{	
		if(event.keyCode != '8')
		{
			alert(maxlength + "Byte (한글 : " + maxlength/2 + "글자, 영문 : " + maxlength + ") 이상 글자는 입력할 수 없습니다.");
		}
		
		formObj.value = formObj.value.substring(0, formObj.value.length - 1);
		formObj.focus();
	}
}

//----------------------------------------------------------------------------
//문자열 길이  리턴
//param : 문자열
//----------------------------------------------------------------------------
function getByteLength(s)
{
	var len = 0;
	if(s == null) return 0;
	for(var i=0; i<s.length; i++){
		var c = escape(s.charAt());
		if(c.length == 1) len++;
		else if(c.indexOf("%u") != -1) len += 2;
		else if(c.indexOf("%") != -1) len += c.length/3;
	}
	return len;
}

//----------------------------------------------------------------------------
//HTML 문자열 이스케이프 처리
//param : 문자열
//----------------------------------------------------------------------------
var entityMap = {
  '&': '&amp;',
  '<': '&lt;',
  '>': '&gt;',
  '"': '&quot;',
  "'": '&#39;',
  '/': '&#x2F;',
  '`': '&#x60;',
  '=': '&#x3D;'
};

function escapeHtml (string) {
  return String(string).replace(/[&<>"'`=\/]/g, function (s) {
    return entityMap[s];
  });
}

/*******************************************************************************
 * is FUNCTION
 *******************************************************************************/

//----------------------------------------------------------------------------
// str의 문자들이 charSet에 존재하는 문자인지 체크
//----------------------------------------------------------------------------
function isExistInCharset(charSet, str) {
	for (i=0; i<str.length; i++)
		if(charSet.indexOf(str.charAt(i)) == -1) return false;

	return true;
}

//----------------------------------------------------------------------------
// str의 문자들이 공백 (' 'SPCASE)문자만 있는지 체크
//----------------------------------------------------------------------------
function isSpaceOnly(str) {
	for(i=0; i<str.length; i++)
		if(str.substring(i,i+1) != " ") return false;

	return true;
}

//----------------------------------------------------------------------------
// str의 문자들이 공백,탭,CR 등의 문자만 있는지 체크
//----------------------------------------------------------------------------
function isWhiteCharOnly(str) {
	for(i=0; i<str.length; i++)
		if(   str.substring(i,i+1) != " "
		   && str.substring(i,i+1) != "\t"
		   && str.substring(i,i+1) != "\r"
		   && str.substring(i,i+1) != "\n"
		   ) return false;

	return true;
}


//----------------------------------------------------------------------------
// str이 null인지 체크
//----------------------------------------------------------------------------
function isNull(str) {
	return ((str == null) || (str.length == 0));
}


//----------------------------------------------------------------------------
// y가 윤년인지 체크
//----------------------------------------------------------------------------
function isLeapYear(y) {
	if(y %   4 != 0) return false;
	if(y % 400 == 0) return true;
	if(y % 100 == 0) return false;
	return true;
}


//----------------------------------------------------------------------------
// str이 시간형식인지 체크
//----------------------------------------------------------------------------
function isTime(str) {
	if (str.length == 8) {
		if (str.substring(2,3) != ":") return false;
		if (str.substring(5,6) != ":") return false;
		if (str.substring(0,2) < '00' || str.substring(0,2) > '23') return false;
		if (str.substring(3,5) < '00' || str.substring(3,5) > '59') return false;
		if (str.substring(6,8) < '00' || str.substring(6,8) > '59') return false;
	}

	if (str.length == 6) {
		if (str.substring(0,2) < '00' || str.substring(0,2) > '23') return false;
		if (str.substring(2,4) < '00' || str.substring(2,4) > '59') return false;
		if (str.substring(4,6) < '00' || str.substring(4,6) > '59') return false;
	}

	if (str.length != 8 && str.length != 6) return false;

	return true;
}


//----------------------------------------------------------------------------
// str이 날짜형식인지 체크
//----------------------------------------------------------------------------
function isDate(str)
{
	if (str.length != 10 && str.length != 8) return false;
	if (str.length == 10) str = str.substring(0,4) + str.substring(5,7) + str.substring(8,10);
	y = str.substring(0,4);
	m = str.substring(4,6);
	d = str.substring(6,8);

	if(y < "1900" || y > "2100") return false;
	if(m < "01"   || m > "12"  ) return false;

	if(m=="01" || m=="03" || m=="05" || m=="07" || m=="08" || m=="10" || m=="12"){
		if(d<"01" || d>"31" ) return false;
	}

	if(m=="04" || m=="06" || m=="09" || m=="11"){
		if(d<"01" || d>"30") return false;
	}

	if(m=="02"){
		if(d < "01") return false;

		if(isLeapYear(y)){
			if(d > "29") false;
		}
		else{
			if(d > "28") false;
		}
	}

	return true;
}


//----------------------------------------------------------------------------
// str이 a와 b사이의 문자열인지 체크
//----------------------------------------------------------------------------
function isBeetweenAnB(str, a, b) {
	return (str >= a && str <= b);
}


//----------------------------------------------------------------------------
// n이 a와 b사이의 숫자인지 체크
//----------------------------------------------------------------------------
function isBeetweenNumAnB(n, a, b) {
	return (parseInt(a) <= parseInt(n) && parseInt(n) <= parseInt(b));
}


//----------------------------------------------------------------------------
// n이 홀수인지 체크
//----------------------------------------------------------------------------
function isOdd (n) {
	return ((n % 2) == 1);
}


//----------------------------------------------------------------------------
// n이 짝수인지 체크
//----------------------------------------------------------------------------
function isEven (n) {
	return ((n % 2) == 0);
}


//----------------------------------------------------------------------------
// n이 정수인지 체크
//----------------------------------------------------------------------------
function isInteger(n) {
	if(n.indexOf(".",0) >= 0) return false;
	return !isNaN(n);
}

//----------------------------------------------------------------------------
// n이 Float인지 체크
//----------------------------------------------------------------------------
function isFloat(n) {
	return !isNaN(n);
}

//----------------------------------------------------------------------------
// str이 A~Z, a~z, 숫자인지 체크
//----------------------------------------------------------------------------
function isAlphaNumeric(str) {
	s = str.toUpperCase();

	if(!isNaN(s)) return true;

	if(s.length == 0) return false;

	for (i=0; i<s.length; i++) {
		if( (s.charAt(i) >= 'A' && s.charAt(i) <= 'Z') || (s.charAt(i) >= '9' && s.charAt(i) <= '9') );
		else return false;
	}

	return true;
}

//----------------------------------------------------------------------------
// str의 길이가 b~e사이인지 체크
//----------------------------------------------------------------------------
function isLenFromTo(str, b, e) {
	l = str.length;
	return (b <= l && e >= l);
}

//----------------------------------------------------------------------------
// str이 주민번호인지 체크
//----------------------------------------------------------------------------
function isSSN(str) {
	if(str.substring(6,7) == '-') str = str.substring(0,6) + str.substring(7,13);

	var chk = 0;
	var sex = str.substring(6,7);

	if (sex != 1 && sex !=2) return false;

	for (i=0; i<=11; i++) chk = chk + ((i%8+2) * parseInt(str.substring(i,i+1)));

	chk = 11 - (chk %11);
	chk = chk % 10;

	return (chk == str.substring(12,13));
}

//----------------------------------------------------------------------------
//문자열의 길이가 제한길이를 벗어났는지 체크
//----------------------------------------------------------------------------
function isLength(str, maxlength)
{
	var retVal = false;
	var len = getByteLength(str);
	if(len <= maxlength)
	{
		retVal = true;
	}
	
	return retVal;
}

//----------------------------------------------------------------------------
// str이 정상적인URL인지 체크
//param : url문자열
//return : boolean
//----------------------------------------------------------------------------
function isUrl(strUrl) 
{
	var retVal = false;
	if(strUrl=="")
	{
		alert("URL을 입력해주세요");
	}
	
	var expUrl = /^(http\:\/\/)?((\w+)[.])+(asia|biz|cc|cn|com|de|eu|in|info|jobs|jp|kr|mobi|mx|name|net|nz|org|travel|tv|tw|uk|us)(\/(\w*))*$/i;
	if(!expUrl.test(strUrl))
	{
		alert("올바른 형식의 URL을 입력하세요");
	}
	else retVal = true;

	return retVal;
}


//----------------------------------------------------------------------------
// 입력값이 정상적인E-MAIL형식인지 체크
// param : 문자열
// return : boolean
//----------------------------------------------------------------------------
function isEmail(strEmail) 
{
	var retVal = false;
	if(strEmail=="")
	{
		alert("이메일을 입력해주세요");
		
	}else{
	
		var isEmailStr = /^((\w|[\-\.])+)@((\w|[\-\.])+)\.([A-Za-z]+)$/;
		if(!isEmailStr.test(strEmail))
		{
			alert("올바른 형식의 이메일을 입력해주세요");
		}
		else retVal = true;
	}
	return retVal;
}

//----------------------------------------------------------------------------
//입력값이 정상적인 IP형식인지 체크
//param : 문자열
//return : boolean
//----------------------------------------------------------------------------
function isIP(strIP)
{
	var retVal = false;
	
	if(strIP=="")
	{
		alert("IP를 입력해주세요");
	}else{
	
	    var expIP = /^(1|2)?\d?\d([.](1|2)?\d?\d){3}$/;
	    
	    if(!expIP.test(strIP))
		{
			alert("올바른 형식의 IP를 입력해주세요");
		}
	    else retVal = true;
	}
    
    return retVal;
}

/*******************************************************************************
 * Numeric FUNCTION
 *******************************************************************************/

//----------------------------------------------------------------------------
// str을 천단위마다 ','로 구분하여 리턴
//----------------------------------------------------------------------------
function makeComma(str) {
	var txtNumber = '' + str;

	//txtNumber = replaceStr(txtNumber,",", "");

	if (isNaN(txtNumber) || txtNumber == "") return "0";

	// 앞에 0를 제거
	var check_zero = true;
	var buf = "";
	for (var i=0; i<txtNumber.length; i++) {
		if (check_zero && txtNumber.substring(i,i+1) == "0") continue;
		buf = buf + txtNumber.substring(i,i+1);
		check_zero = false;
	}

	if (buf == "") buf = "0";

	txtNumber = buf;

	var rxSplit = new RegExp('([0-9])([0-9][0-9][0-9][,.])');
	var arrNumber = txtNumber.split('.');
	arrNumber[0] += '.';

	do {
		arrNumber[0] = arrNumber[0].replace(rxSplit, '$1,$2');
		if(arrNumber[0] == null){
			break;
		}
	} while (rxSplit.test(arrNumber[0]));

	if (arrNumber.length > 1) {
		return arrNumber.join('');
	}
	else {
		return arrNumber[0].split('.')[0];
	}
}


//----------------------------------------------------------------------------
// str의 ','를 제거하여 리턴
//----------------------------------------------------------------------------
function delComma(str){
	var strNew = "";

	for(i=0; i<str.length; i++) {
		if (str.charAt(i) == ',') continue;
		strNew = strNew + str.charAt(i);
	}

	return strNew;
}


//----------------------------------------------------------------------------
// input text에서 onblur이벤트발생시 콤마형식으로 표시
//----------------------------------------------------------------------------
function CommaNum_onblur() {
	var strData, strPnt, strNew = "";

	strData = event.srcElement.value;
	for(i=0; i<strData.length; i++) {
		if (strData.charAt(i) == ',') continue;
		strNew = strNew + strData.charAt(i);
	}
	strData = strNew;
	strNew = "";
	strPnt = "";

	/*
	if(strData.indexOf(".") != -1){
		strPnt  = strData.substring(strData.indexOf("."), strData.length);
		strData = strData.substring(0, strData.indexOf("."));
		if(strPnt.length == 1) strPnt = strPnt + "00";
		if(strPnt.length == 2) strPnt = strPnt + "0";
	}
	*/

	if(isNaN(strData)){
		window.status = "[Input Error] Is Not Number!";
		event.srcElement.select();
		event.srcElement.focus();
		return;
	}

	strNew = makeComma(strData);
	event.srcElement.value = strNew;

	window.status = "";
}

//----------------------------------------------------------------------------
// input text에서 onblur이벤트발생시 콤마를 제거하여 표시
//----------------------------------------------------------------------------
function CommaNum_onfocus() {
	var strData, strNew = "";

	strData = event.srcElement.value;
	strNew  = delComma(strData);

	event.srcElement.value = strNew;
	event.srcElement.select();
}


//---------------------------------------------------------------------------
// 소숫점이하에서 반올림함수
// num 반올림할 숫자, len 반올림할 자리수
//---------------------------------------------------------------------------
function round(num,len){
	var tempNumber;
	var roundResult;

	if (len == '' || len == null) len = 0;

	tempNumber  = Math.pow(10,len);
	roundResult = Math.round(num*tempNumber);

	if (tempNumber != 0) roundResult = roundResult / tempNumber;
	return roundResult;
}

//---------------------------------------------------------------------------
// 소숫점이하에서 버림함수
// num 버림할 숫자,len 버림할 자리수
//---------------------------------------------------------------------------
function trunc(num,len){
	var tempNumber;
	var roundResult;
	var orgLen = num.length;

	if (len == '' || len == null) len = 0;

	tempNumber  = Math.pow(10,len);
	roundResult = parseInt(num*tempNumber);

	if (tempNumber != 0){
		roundResult = roundResult / tempNumber;
	}
	return roundResult;
}

//---------------------------------------------------------------------------
// 소숫점이하에서 올림함수
// num 올림할 숫자, len 올림할 자리수
//---------------------------------------------------------------------------
function ceil(num,len){
	var tempNumber;
	var roundResult;

	if (len == '' || len == null) len = 0;

	tempNumber  = Math.pow(10,len);
	roundResult = Math.ceil(num*tempNumber);

	if (tempNumber != 0){
		roundResult = roundResult / tempNumber;
	}
	return roundResult;
}
//---------------------------------------------------------------------------
// km 또는 m 단위 추가(면적) 
// 숫자 입력
//---------------------------------------------------------------------------
function mOrKm(num){
	if (num > 10000) {
		num = (Math.round(num / 1000000 * 100) / 100) + ' '
				+ 'km<sup>2</sup>';
	} else {
		num = (Math.round(num * 100) / 100) + ' ' + 'm<sup>2</sup>';
	}
	return num;
}


/*******************************************************************************
 * DATE FUNCTION
 *******************************************************************************/
//----------------------------------------------------------------------------
//콤보박스에 대한 년도 셋팅
//param : obj : 콤보객체 ex)document.frm.year --> 콤보객체
//      min : 현재년도 - min
//      max : 현재년도 + max
//----------------------------------------------------------------------------
function setYearCombo(obj, min, max)
{
	var thisDate = new Date();
	var currentYear = thisDate.getFullYear(); //현재년도
	
	if(min == "") min = 0;
	if(max == "") max = 0;
	
	 //option 태그를 생성해서 select에 넣기
	for (var i = currentYear + max; i >= currentYear - min ; i--)
	{
		var opt1 = document.createElement("option"); //option 태그 생성	   	
		opt1.value = i;
		
		if(i == currentYear)
		{
			opt1.selected = "selected";
		}                               
    
		opt1.appendChild(document.createTextNode(i)); //option 태그에 텍스트 추가
    
    
		obj.appendChild(opt1); // select 태그에 추가
	}
}

//---------------------------------------------------------------------------
//현재 일자를  리턴
//param : 형식 문자열(null, "-")
//---------------------------------------------------------------------------
function getToday(str)
{
	var date = new Date();
	var month = date.getMonth()+1;
	if(month.toString().length < 2){
		month = "0"+month;
	}
	var day = date.getDate();
	if(day.toString().length < 2){
		day = "0"+day;
	}
	
	var today = "";
	
	if(str == "") today = date.getFullYear()+ month + day;
	else today = date.getFullYear()+ str + month + str + day;
	
	return today;
}

//---------------------------------------------------------------------------
// 일자 yyyymmdd에 a일을 더한 일자를 리턴
//---------------------------------------------------------------------------
function addDays(yyyymmdd, n)
{
	if (yyyymmdd.length != 10 && yyyymmdd.length != 8) return "";
	if (yyyymmdd.length == 10) yyyymmdd = yyyymmdd.substring(0,4) + yyyymmdd.substring(5,7) + yyyymmdd.substring(8,10);

	now = new Date(yyyymmdd.substring(0,4),(eval(yyyymmdd.substring(4,6))-1),yyyymmdd.substring(6,8));
	ret = new Date(now.getTime() + (1000 * 60 * 60 * 24 * n));

	retY = ret.getYear();
	retM = parseInt(ret.getMonth()) + 1;
	retD = parseInt(ret.getDate());

	if (retM < 10) retM = "0" + retM;
	if (retD < 10) retD = "0" + retD;

	return "" + retY + retM + retD;
}

//---------------------------------------------------------------------------
// 일자의 구분자
//---------------------------------------------------------------------------
var __DATE_SEPARATOR__ = ".";


//---------------------------------------------------------------------------
// 사간의 구분자
//---------------------------------------------------------------------------
var __TIME_SEPARATOR__ = ":";


//---------------------------------------------------------------------------
// 일자의 구분자지정
//---------------------------------------------------------------------------
function setDateSeparator(s){
	__DATE_SEPARATOR__ = s;
}

//---------------------------------------------------------------------------
// 일자의 구분자 리턴
//---------------------------------------------------------------------------
function getDateSeparator(){
	return __DATE_SEPARATOR__ ;
}


//---------------------------------------------------------------------------
// yyyymmdd형식의 일자를 구분자가 포함된 형식의 일자로 리턴
//---------------------------------------------------------------------------
function getFormatDate(yyyymmdd){
	if (yyyymmdd.length != 10 && yyyymmdd.length != 8) return yyyymmdd;
	if (yyyymmdd.length == 10) yyyymmdd = yyyymmdd.substring(0,4) + yyyymmdd.substring(5,7) + yyyymmdd.substring(8,10);

	ret = yyyymmdd.substring(0,4)
	    + __DATE_SEPARATOR__
	    + yyyymmdd.substring(4,6)
	    + __DATE_SEPARATOR__
	    + yyyymmdd.substring(6,8);

	return ret;
}


//---------------------------------------------------------------------------
// 구분자가 포함된 형식의 일자를 yyyymmdd형식의 일자로 리턴
//---------------------------------------------------------------------------
function getYYYYMMDD(yyyymmdd){
	if (yyyymmdd.length != 10 && yyyymmdd.length != 8) return "";
	if (yyyymmdd.length == 10) yyyymmdd = yyyymmdd.substring(0,4) + yyyymmdd.substring(5,7) + yyyymmdd.substring(8,10);

	return yyyymmdd;
}


//---------------------------------------------------------------------------
// hhnnss형식의 시간을 구분자가 포함된 형식의 시간으로 리턴
//---------------------------------------------------------------------------
function getFormatTime(hhnnss){
	var ret = hhnnss;
	if (hhnnss.length == 5) hhnnss = hhnnss.substring(0,4) + hhnnss.substring(5,7);
	if (hhnnss.length == 8) hhnnss = hhnnss.substring(0,2) + hhnnss.substring(3,5) + hhnnss.substring(6,8);

	if (hhnnss.length == 6){
		ret = hhnnss.substring(0,2)
		    + __TIME_SEPARATOR__
		    + hhnnss.substring(2,4)
		    + __TIME_SEPARATOR__
		    + hhnnss.substring(4,6);
		return ret;
	}
	if (hhnnss.length == 4){
		ret = hhnnss.substring(0,2)
		    + __TIME_SEPARATOR__
		    + hhnnss.substring(2,4);
		return ret;
	}
	return ret;
}


//---------------------------------------------------------------------------
//자바스크립트 date 형식의 시간을 yyyy-MM-dd HH:mm:ss 형식의 시간으로 리턴
//---------------------------------------------------------------------------
function dateFormat(date) {
	 let month = date.getMonth() + 1;
	 let day = date.getDate();
	 let hour = date.getHours();
	 let minute = date.getMinutes();
	 let second = date.getSeconds();
	
	 month = month >= 10 ? month : '0' + month;
	 day = day >= 10 ? day : '0' + day;
	 hour = hour >= 10 ? hour : '0' + hour;
	 minute = minute >= 10 ? minute : '0' + minute;
	 second = second >= 10 ? second : '0' + second;
	
	 return date.getFullYear() + '-' + month + '-' + day + ' ' + hour + ':' + minute + ':' + second;
 
}

/*******************************************************************************
 * 쿠키 FUNCTION
 *******************************************************************************/

//---------------------------------------------------------------------------
// 쿠키설정
//---------------------------------------------------------------------------
function setCookie (name, value, expires) {
	document.cookie = name + "=" + escape(value) + "; expires=" + expires.toGMTString() +  "; path=/";
}


//---------------------------------------------------------------------------
// 쿠키값 리턴
//---------------------------------------------------------------------------
function getCookie(name) {
	var search;

	search = name + "=";
	offset = document.cookie.indexOf(search);

	if (offset == -1) return "";

	offset += search.length ;
	end = document.cookie.indexOf(";", offset) ;
	if (end == -1)	end = document.cookie.length;

	return unescape(document.cookie.substring(offset, end));
}


//---------------------------------------------------------------------------
// 쿠키삭제
//---------------------------------------------------------------------------
function deleteCookie(name) {
	var expdate = new Date();
	expdate.setTime(expdate.getTime() - (86400 * 1000 * 1));
	setCookie(name, "", expdate);
}


/*******************************************************************************
 * WINDOW FUNCTION
 *******************************************************************************/

//---------------------------------------------------------------------------
// dialogWindow 중앙으로 옮기기
//---------------------------------------------------------------------------
function dialogCenter(w) {
	var x = (screen.width  - parseInt(w.dialogWidth))  / 2;
	var y = (screen.height - parseInt(w.dialogHeight)) / 2;
	w.dialogLeft = x;
	w.dialogTop = y;
}

//---------------------------------------------------------------------------
// Sub-Window 중앙으로 옮기기
//---------------------------------------------------------------------------
function windowCenter(w) {
	if (opener == null) {
		dialog_center(w);
	}
	else {
		var x = (screen.width  - document.body.offsetWidth)  / 2;
		var y = (screen.height - document.body.offsetHeight) / 2;
		w.moveTo (x, y);
	}
}
function windowCenter2(x_size,y_size) {
    var x_pos = ( screen.width-x_size)  / 2;
    var y_pos = ( screen.height-y_size)  / 2;
    var props = "";
    props += (",Top="+y_pos+"px");
    props += (",Left="+x_pos+"px;");
    return props;
}


/*******************************************************************************
 * 기타 FUNCTION
 *******************************************************************************/

//---------------------------------------------------------------------------
// input-text에 대해 Return값이 들어올 경우 파라미터로 넘겨준 함수를 호출한다.
// 검색기능 구현시 해당 검색항목 input에 대해 onkeydown=keydown(검색함수명)와 같이 사용
//---------------------------------------------------------------------------
function onEnterKey(reffunc) {
  if (window.event.keyCode == 13) {
      window.event.cancelBubble = true;
      if (reffunc != null) reffunc(event.arguments);
  }
}


//---------------------------------------------------------------------------
// 자동 필드이동
//---------------------------------------------------------------------------
function goNextObj(arg, len, next) {
	var myObj = arg;
	var nextObj = next;
	if((event.keyCode == 13) || (myObj.value.length == len)) nextObj.focus();
	return;
}


//----------------------------------------------------------------------------
// 전화번호의 앞자리 국번을 인자로 받아서 리스트 박스에 선택시켜줌.
//----------------------------------------------------------------------------
function setTelNo1 (selectObj, telNo1) {
	if (selectObj == null) return;
	for(i=0 ; i <  selectObj.length ; i++) {
		if(selectObj[i].text == telNo1) {
			selectObj[i].selected = true;
			break;
		}
	}
}

//----------------------------------------------------------------------------
// 전화번호입력체크
//----------------------------------------------------------------------------
function isValidTelNo(ttl, no1, no2, no3, no4){
	if(ttl == null) ttl = "전화번호";

	if(no1.value.length > 0){
		if(isNaN(no2.value) || no2.value.length < 3) {
			alert(ttl + "의 국번이 잘못 입력되었습니다.");
			no2.focus();
			no2.select();
			return false;
		}
		if(isNaN(no3.value) || no3.value.length < 4) {
			alert(ttl + "의 입력이 잘못 되었습니다.");
			no3.focus();
			no3.select();
			return false;
		}
	}
	else {
		if(no2.value.length > 0 || no3.value.length > 0 || (no4==null?0:no4.value.length) > 0) {
			alert(ttl + "의 지역번호 또는 식별번호가 잘못 입력되었습니다.");
			no1.focus();
			return false;
		}
	}

	return true;
}


/**
 * 전화번호 하이픈(-) 자동입력
 * @param str
 * @returns
 */
function autoHypenPhone(str){
    str = str.replace(/[^0-9]/g, '');
    var tmp = '';
    if( str.length < 4){
        return str;
    }else if(str.length < 7){
        tmp += str.substr(0, 3);
        tmp += '-';
        tmp += str.substr(3);
        return tmp;
    }else if(str.length < 11){
    	if(str.indexOf('02') == 0){
            tmp += str.substr(0, 2);
            tmp += '-';
            if(str.length == 10){
                tmp += str.substr(2, 4);
                tmp += '-';
                tmp += str.substr(6);
            }else{
                tmp += str.substr(2, 3);
                tmp += '-';
                tmp += str.substr(5);               	
            }
    	}else{
            tmp += str.substr(0, 3);
            tmp += '-';
            tmp += str.substr(3, 3);
            tmp += '-';
            tmp += str.substr(6);
    	}
        return tmp;
    }else{              
        tmp += str.substr(0, 3);
        tmp += '-';
        tmp += str.substr(3, 4);
        tmp += '-';
        tmp += str.substr(7);
        return tmp;
    }
    return str;
}