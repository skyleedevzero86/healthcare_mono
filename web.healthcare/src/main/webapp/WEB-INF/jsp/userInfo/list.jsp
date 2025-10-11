<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
      pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/_dtd.jsp" %>

 <jsp:include page='/WEB-INF/jsp/include/header.jsp' />
   <jsp:param name="pageType" value="eb-main" />
	<jsp:param name="bodyAttr" value="onload=''" />
	<jsp:param name="bodyStyle" value="eb-main" />
</jsp:include>
<title>
	<c:if test="${uri eq '/userInfo/list_user'}">사용자 리스트</c:if>
	<c:if test="${uri eq '/userInfo/list_par'}">보호자 리스트</c:if>
	<c:if test="${uri eq '/userInfo/list_doc'}">담당자 리스트</c:if>
</title>
<div class="container">
<div class="container_wrap inner">
	<section class="layout_content">
		<h1 class="content_main_title">
			<c:if test="${uri eq '/userInfo/list_user'}">사용자 리스트</c:if>
			<c:if test="${uri eq '/userInfo/list_par'}">보호자 리스트</c:if>
			<c:if test="${uri eq '/userInfo/list_doc'}">담당자 리스트</c:if>
		</h1>
		<div class="content_wrap">
			<section class="radius_con">
				<form action="${uri }" method="post" id="searchForm">
					<section class="list_search">
						<div class="list_search__form">
							<div class="list_search__row">
								<label for="searchKeyword" class="list_search__label">이름</label>
								<input type="text" title="이름" name="searchKeyword" value='<c:out value="${dto.searchKeyword }"></c:out>' maxlength="50" class="form_item_text w70p" placeholder="이름을 입력해주세요" onkeydown="">
	                			<input type="number" name="pageIndex" id="pageIndex" value="1" hidden="hidden"/>
							</div>
						</div>
					<div class="list_search__btn">
						<button type="submit" id="btn_search" class="btn_list_search">검색</button>
					</div>
				</form>
			</section>
				<div class="table_box">
					<table>
		                <colgroup>
		                	<c:choose>
								<c:when test="${ path eq 'list_doc' }">
									<col style="width: 100px;">
									<col style="width: auto;">
									<col style="width: auto;">
									<col style="width: auto;">
									<col style="width: auto;">
									<col style="width: autox">
								</c:when>
								<c:when test="${ path eq 'list_par' }">
									<col style="width: 100px;">
									<col style="width: auto;">
									<col style="width: auto;">
									<col style="width: auto;">
									<col style="width: auto;">
								</c:when>
								<c:when test="${ path eq 'list_user' }">
									<col style="width: 100px;">
									<col style="width: auto;">
									<col style="width: auto;">
									<col style="width: auto;">
									<col style="width: auto;">
									<col style="width: auto">
									<col style="width: auto">
									<col style="width: auto">
									<col style="width: 150px">
								</c:when>
							</c:choose>
		                </colgroup>
		                <thead>
		                	<th>번호</th>
		                	<c:if test="${ path eq 'list_doc' }">
		                	<th>소속</th>
		                	</c:if>
							<th>이름</th>
							<th>ID</th>
							<c:if test="${ path eq 'list_user' }">
							<th>담당자</th>
							<th>보호자</th>
							</c:if>
							<th>생년월일</th>
							<th>연락처</th>
							<c:if test="${ path eq 'list_user' }">
							<th>위험도</th>
							<th>건강정보</th>
							</c:if>
		                </thead>
		                <tbody id="listTable">
			                <c:forEach items="${list }" var="item" varStatus="status">
			                	<tr>
			                		<td><c:out value="${item.rownum }"/></td>
			                		<c:if test="${ path eq 'list_doc' }">
		                			<td><c:out value="${item.deptNm }"/></td>
			                		</c:if>
			                		<td><c:out value="${item.userNm }" /></td>
			                		<td><c:out value="${item.userId }" /></td>
			                		<c:if test="${ path eq 'list_user' }">
									<td><c:out value="${item.doctorNm }" /></td>
									<td><c:out value="${item.parentNm }" /></td>
									</c:if>
			                		<td><c:out value="${item.birthEnc }" /></td>
			                		<td><c:out value="${item.telNumEnc }" /></td>
			                		<c:if test="${ path eq 'list_user' }">
			                		<td>
		                			<c:choose>
		                				<c:when test="${ item.warning }">
		                					<span class="label_info04">경고</span>
		                				</c:when>
		                				<c:otherwise>
		                					<span class="label_info01">정상</span>
		                				</c:otherwise>
		                			</c:choose>
			                		</td>
			                		<td><a class="btn_table_search" href="javascript:userHealthInfo('/dashboard','<c:out value="${item.userId}" />', '<c:out value="${item.userNm}" />')">건강정보</a></td>
			                		</c:if>
			                	</tr>
		                	</c:forEach>
		                	<c:if test="${result.totalCount eq '0'}">
			                	<tr><td <c:if test="${ path eq 'list_user' }">colspan='9'</c:if>
			                	<c:if test="${ path eq 'list_doc' }">colspan='6'</c:if>
			                	<c:if test="${ path eq 'list_par' }">colspan='5'</c:if>>data가 존재하지 않습니다.</td></tr>
		                	</c:if>
		                </tbody>
					</table>
				</div>
				<form action="${uri }" method="post" id="paginationForm">
					<div class="list_search__form">
						<div class="list_search__row"> 
							<input type="hidden" title="검색어" name="searchKeyword" value="${dto.searchKeyword }" maxlength="50" class="form_item_text w70p" placeholder="검색어를 입력해주세요" onkeydown="">
						</div>
						<input hidden="hidden" id="page_pageIndex" name="pageIndex" value="">
					</div>
				</form>
				<div class="table_bottom_info">
		          	<div class="page_box pagebox_bbs" id="pagingDiv">
						<sleekydz86:pagination paginationInfo="${paginationInfo}" jsFunction="fn_paging"/>
	               	</div>
               	</div>
		</section>
	</div>
</div>
<jsp:include page="/WEB-INF/jsp/include/footer.jsp" />
<script>
	$('#btn_search').click(function(){
		$("#searchForm").attr("action", '${uri}');
		$('#searchForm').submit();
	});

	function fn_paging(index) {
		$('#page_pageIndex').val(index);
		$('#paginationForm').submit();
	}
	
</script>