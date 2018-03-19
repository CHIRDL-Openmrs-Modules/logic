<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage LOGIC" otherwise="/login.htm" redirect="/module/logic/status.form" />
${jsonOutput}