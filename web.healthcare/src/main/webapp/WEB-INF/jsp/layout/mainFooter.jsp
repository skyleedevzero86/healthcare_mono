<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/_dtd.jsp" %>
  </div>
  <script>
	function userHealthInfo(url, userId, userNm){
		let f = document.createElement('form');
	    f.setAttribute('method', 'post');
	    f.setAttribute('action', url);
	    
	    var input1 = document.createElement('input');
		input1.setAttribute("type", "hidden");
		input1.setAttribute("name", "searchUserId");
		input1.setAttribute("value", userId);
		f.appendChild(input1);
		
		var input2 = document.createElement('input');
		input2.setAttribute("type", "hidden");
		input2.setAttribute("name", "userNm");
		input2.setAttribute("value", userNm);
		f.appendChild(input2);
		
	    document.body.appendChild(f);
	    f.submit();
	}
  </script>