<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="View Administration Functions" otherwise="/login.htm" redirect="/admin/index.htm"/>

<c:if test="${not empty error}">
    <div id="openmrs_error">
        <spring:message code="Hl7inQueue.queueList.errorMessage.header"/>: ${error}
    </div>
</c:if>

<form method="get" action="caches.form" name="back" id="back">
    <p><a href="#" onclick="document.back.submit();"><spring:message code="logic.cache.backToCacheList"/>.</a></p><br/>
</form>

<h3>${cacheName}</h3>

<script type="text/javascript">
    function changeInputAccordingToCheBox(checkBox, associatedInputName) {
        var inputText = document.getElementById(associatedInputName);
        inputText.value = checkBox.checked;
    }

    function doCacheManagerAction(command) {
        var actionInput = document.getElementById("action");

        if (command != '') {
            if(command == "restart") {
                if (!confirm("<spring:message code="logic.cache.restartPopupWarn"/>"))
                    return;
            }
            actionInput.value = command;
            document.cacheAction.submit();
        }
    }
</script>

<form action="cache.form" method="post">
    <input type="hidden" name="cacheName" value="${cacheName}" />
    <table cellpadding="1" cellspacing="1">
        <c:if test="${not empty configTTL}">
        <tr>
            <td class="evenRow"><spring:message code="logic.cache.config.defaultTTL"/></td>
            <td><input type="text" name="defaultTTL" value="${configTTL}"/></td>
        </tr>
        </c:if>
        <c:if test="${not empty configMaxElInMem}">
        <tr>
            <td class="evenRow"><spring:message code="logic.cache.config.maxElementsInMem"/></td>
            <td><input type="text" name="maxElemInMem" value="${configMaxElInMem}"/></td>
        </tr>
        </c:if>
        <c:if test="${not empty configMaxElOnDisk}">
        <tr>
            <td class="evenRow"><spring:message code="logic.cache.config.maxElementsOnDisk"/></td>
            <td><input type="text" name="maxElemOnDisk" value="${configMaxElOnDisk}" <c:if test="${not diskPersistence}">disabled=""</c:if> /></td>
        </tr>
        </c:if>
        <c:if test="${not empty diskPersistence}">
        <tr>
            <td class="evenRow"><label for="_diskPersistence"><spring:message code="logic.cache.config.persistence"/></label></td>
            <td>
                <input type="hidden" id="diskPersistence" name="diskPersistence" value="${diskPersistence}" />
                <input type="checkbox" id="_diskPersistence" onchange="changeInputAccordingToCheBox(this, 'diskPersistence');" <c:if test="${diskPersistence}">checked</c:if> />
            </td>
        </tr>
        </c:if>
        <c:if test="${not empty isDisabled}">
        <tr>
            <td class="evenRow"><label for="_isDisabled"><spring:message code="logic.cache.config.disabled"/></label></td>
            <td>
                <input type="hidden" id="isDisabled" name="isDisabled" value="${isDisabled}" />
                <input type="checkbox" id="_isDisabled" onchange="changeInputAccordingToCheBox(this, 'isDisabled');" <c:if test="${isDisabled}">checked</c:if> />
            </td>
        </tr>
        </c:if>
        <tr class="evenRow">
            <td><spring:message code="logic.cache.config.cacheSize"/></td>
            <td>${cacheSize}</td>
        </tr>
        <tr>
            <td colspan="2">
                <input type="submit" value="<spring:message code="logic.cache.command.save"/>"/>
                <input type="button" value="<spring:message code="logic.cache.refreshPage"/>" onclick="document.cacheAction.submit();"/>
                <input type="button" id="flush" value="<spring:message code="logic.cache.command.flush"/>" onclick="doCacheManagerAction('flush');"/>
                <input type="button" id="clear" value="<spring:message code="logic.cache.command.clear"/>" onclick="doCacheManagerAction('clear');"/>
                <c:if test="${cacheRestart}">
                    <input type="button" value="<spring:message code="logic.cache.command.restart"/>" onclick="doCacheManagerAction('restart');"/>
                </c:if>
            </td>
        </tr>
    </table>
</form>

<c:if test="${isRestartNeeded}">
<div style="font-style:italic; color:#d2691e; padding-top: 15px;">
    <spring:message code="logic.cache.restartWarn"/>
</div>
</c:if>

<form action="cache.form" id="cacheAction" name="cacheAction">
    <input type="hidden" name="cacheName" value="${cacheName}" />
    <input type="hidden" id="action" name="action" value=""/>
</form>

<br/>
<hr/>
TEMPORARY
<br/><b>${cacheName} Statisctics:</b><br/>
<table cellpadding="1" cellspacing="1">
    <tr style="color:#bfbdbd;">
        <td>Cache hits:</td>
        <td>${cacheHits}</td>
    </tr>
    <tr style="color:#bfbdbd;">
        <td>Cache misses:</td>
        <td>${cacheMisses}</td>
    </tr>
    <tr style="color:#bfbdbd;">
        <td>Cache specific stats:</td>
        <td>${cacheToStr}</td>
    </tr>
</table>
<br/>
<hr/>

<%@ include file="/WEB-INF/template/footer.jsp" %>