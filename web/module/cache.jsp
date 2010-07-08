<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="View Administration Functions" otherwise="/login.htm" redirect="/admin/index.htm"/>

<form method="get" action="caches.form" name="back" id="back">
<p><a href="#" onclick="document.back.submit();">Back to the cache list page.</a></p><br/>
</form>

<h3>${cacheName}</h3>

<form action="cache.form">
    <input type="hidden" name="cacheName" value="${cacheName}" />
    <table cellpadding="1" cellspacing="1">
        <c:if test="${not empty configMaxElInMem}">
        <tr>
            <td class="evenRow">Max elements in memory</td>
            <td><input type="text" name="maxElemInMem" value="${configMaxElInMem}"/></td>
        </tr>
        </c:if>
        <c:if test="${not empty configMaxElOnDisk}">
        <tr>
            <td class="evenRow">Max elements on disk</td>
            <td><input type="text" name="maxElemOnDisk" value="${configMaxElOnDisk}"/></td>
        </tr>
        </c:if>
        <c:if test="${not empty configTTL}">
        <tr>
            <td class="evenRow">Default TTL</td>
            <td><input type="text" name="defaultTTL" value="${configTTL}"/></td>
        </tr>
        </c:if>
        <c:if test="${not empty diskPersistence}">
        <tr>
            <td class="evenRow">Disk persistence</td>
            <td><input type="checkbox" name="diskPersistence" <c:if test="${diskPersistence}">checked</c:if> value="true" /></td>
        </tr>
        </c:if>
        <tr class="evenRow">
            <td>Cache size:</td>
            <td>${cacheSize}</td>
        </tr>
        <tr>
            <td colspan="2">
                <input type="submit" value="save"/>
                <input type="button" value="refresh" onclick="document.refresh.submit();"/>
            </td>
        </tr>
    </table>
</form>

<form action="cache.form" id="refresh" name="refresh">
    <input type="hidden" name="cacheName" value="${cacheName}" />
</form>

<br/>
<hr/>
TEMPORARY
<br/><b>${cacheName} Statisctics:</b><br/>
<table cellpadding="1" cellspacing="1">
    <tr>
        <td>Cache hits:</td>
        <td>${cacheHits}</td>
    </tr>
    <tr>
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