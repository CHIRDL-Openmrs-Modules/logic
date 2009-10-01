<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

Hello, ${authenticatedUser.personName}!
<br />
Logic Service Result is ${result}
<br />
If you can't see a date up there, that means the logic module is not working properly
<br />
The error message is: ${error}

<%@ include file="/WEB-INF/template/footer.jsp"%>
