<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:require privilege="View Administration Functions" otherwise="/login.htm" redirect="/module/logic/manageLogicRules.list" />

<form method="post" action="deleteLogicRule.form">
	<input type="hidden" name="id" value="${ rule.id }"/>
	<input type="submit" value="<spring:message code="logic.rule.edit.delete"/>" style="float: right"/>
</form>

<a href="manageLogicRules.list"><spring:message code="logic.rule.edit.back"/></a>

<h2><spring:message code="logic.rule.edit.title"/></h2>

<form method="post">
	<table>
		<tr valign="top">
			<th><spring:message code="logic.LogicRule.name"/></th>
			<td>
				<spring:bind path="rule.name">
					<input type="text" name="${status.expression}" value="${status.value}"/>
					<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
				</spring:bind>
			</td>
		</tr>
		<tr valign="top">
			<th><spring:message code="logic.LogicRule.description"/></th>
			<td>
				<spring:bind path="rule.description">
					<textarea rows="3" cols="80" name="${status.expression}">${status.value}</textarea>
					<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
				</spring:bind>
			</td>
		</tr>
		<tr valign="top">
			<th><spring:message code="logic.LogicRule.language"/></th>
			<td>
				<spring:bind path="rule.language">
					<select name="${status.expression}">
						<option value=""></option>
						<c:forEach var="language" items="${ruleLanguages}">
							<option value="${language.name}" <c:if test="${language.name == status.value}">selected="selected"</c:if>>
						        ${language.name}
						    </option>
						</c:forEach>
					</select>
					<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
				</spring:bind>
			</td>
		</tr>
		<tr valign="top">
			<th><spring:message code="logic.LogicRule.ruleContent"/></th>
			<td>
				<spring:bind path="rule.ruleContent">
					<textarea rows="20" cols="80" name="${status.expression}">${status.value}</textarea>
					<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
				</spring:bind>
			</td>
		</tr>
		<tr valign="top">
			<th></th>
			<td>
				<input type="submit" value="<spring:message code="general.save"/>"/>
				<input type="button" value="<spring:message code="general.cancel"/>" onClick="window.location = 'manageLogicRules.list';"/>
			</td>
		</tr>
	</table>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>