<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Tokens" otherwise="/login.htm" redirect="/module/logic/status.form" />
${jsonOutput}