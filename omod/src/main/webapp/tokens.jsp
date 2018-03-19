<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Tokens" otherwise="/login.htm" redirect="/module/logic/tokens.form" />
<c:forEach var="keyword" items="${listOutput}">${keyword}
</c:forEach>