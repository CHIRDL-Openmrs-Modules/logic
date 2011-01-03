<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:require privilege="View Administration Functions" otherwise="/login.htm" redirect="/module/logic/manageLogicRules.list" />

<h2><spring:message code="logic.rule.manage.title"/></h2>

<a href="editLogicRule.form"><spring:message code="logic.rule.manage.add"/></a>

<br/><br/>

<div class="boxHeader">
	<spring:message code="logic.rule.manage.existing"/>
</div>
<table class="box">
	<thead>
		<tr>
			<th><spring:message code="logic.LogicRule.name"/></th>
			<th><spring:message code="logic.LogicRule.language"/></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="rule" items="${ rules }">
			<tr>
				<td><a href="editLogicRule.form?id=${ rule.id }">${ rule.name }</a></td>
				<td>${ rule.language }</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<%@ include file="/WEB-INF/template/footer.jsp"%>