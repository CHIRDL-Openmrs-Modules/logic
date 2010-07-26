<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="View Administration Functions" otherwise="/login.htm" redirect="/admin/index.htm"/>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js" />

<script type="text/javascript">
	var $j = jQuery.noConflict();
</script>

<c:if test="${not empty error}">
    <div id="openmrs_error">
        <spring:message code="Hl7inQueue.queueList.errorMessage.header"/>: ${error}
    </div>
</c:if>
<c:if test="${not empty configSuccessfullySaved}">
    <div id="openmrs_msg">
        <spring:message code="logic.cache.config.savedMessage"/>
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

    function validate() {
        var defTtlValue = document.getElementById("defaultTTL").value;
        var maxElemInMemValue = document.getElementById("maxElemInMem").value;
        var maxElemOnDiskValue = document.getElementById("maxElemOnDisk").value;
        var posIntRegExp = /^[1-9]\d*$/;
        var retVal = true;

        if(!posIntRegExp.test(defTtlValue)) {
            $j("#defaultTtlError").show();
            retVal = false;
        } else $j("#defaultTtlError").hide();
        if(!posIntRegExp.test(maxElemInMemValue)) {
            $j("#maxElemInMemError").show();
            retVal = false;
        } else $j("#maxElemInMemError").hide();
        if(!posIntRegExp.test(maxElemOnDiskValue)) {
            $j("#maxElemOnDiskError").show();
            retVal = false;
        } else $j("#maxElemOnDiskError").hide();

        return retVal;
    }
</script>

<form action="cache.form" method="post" onsubmit="return validate();" >
    <input type="hidden" name="cacheName" value="${cacheName}" />
    <table cellpadding="1" cellspacing="1">
        <c:if test="${not empty configTTL}">
        <tr>
            <td class="evenRow"><spring:message code="logic.cache.config.defaultTTL"/></td>
            <td>
                <input type="text" id="defaultTTL" name="defaultTTL" value="${configTTL}"/>
                <span class="error" id="defaultTtlError" style="display: none;"><spring:message code="logic.cache.validation.intPositive"/></span>
            </td>
        </tr>
        </c:if>
        <c:if test="${not empty configMaxElInMem}">
        <tr>
            <td class="evenRow"><spring:message code="logic.cache.config.maxElementsInMem"/></td>
            <td>
                <input type="text" id="maxElemInMem" name="maxElemInMem" value="${configMaxElInMem}"/>
                <span class="error" id="maxElemInMemError" style="display: none;"><spring:message code="logic.cache.validation.intPositive"/></span>
            </td>
        </tr>
        </c:if>
        <c:if test="${not empty configMaxElOnDisk}">
        <tr>
            <td class="evenRow"><spring:message code="logic.cache.config.maxElementsOnDisk"/></td>
            <td>
                <input type="text" id="maxElemOnDisk" name="maxElemOnDisk" value="${configMaxElOnDisk}" <c:if test="${not diskPersistence}">disabled=""</c:if> />
                <span class="error" id="maxElemOnDiskError" style="display: none;"><spring:message code="logic.cache.validation.intPositive"/></span>
            </td>
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
                <input type="submit" value="<spring:message code="logic.cache.command.applyAndSave"/>"/>
                <input type="button" value="<spring:message code="logic.cache.refreshPage"/>" onclick="document.cacheAction.submit();"/>
                <c:if test="${isCacheFlush}">
                    <input type="button" id="flush" value="<spring:message code="logic.cache.command.flush"/>" onclick="doCacheManagerAction('flush');"/>
                </c:if>
                <input type="button" id="clear" value="<spring:message code="logic.cache.command.clear"/>" onclick="doCacheManagerAction('clear');"/>
                <c:if test="${isCacheRestart}">
                    <input type="button" value="<spring:message code="logic.cache.command.restart"/>" onclick="doCacheManagerAction('restart');"/>
                </c:if>
            </td>
        </tr>
    </table>
</form>

<c:if test="${cacheSizeWarn}">
<div style="font-style:italic; color:#d2691e; padding-top: 15px;">
    Cache size warning! Cache size is (Max elements in memory + Max elements on disk).
    Recommended size at least is ${atLeastCacheSize}. 
</div>
</c:if>

<c:if test="${isRestartNeeded and isCacheRestart}">
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