<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="View Administration Functions" otherwise="/login.htm" redirect="/admin/index.htm"/>

<script type="text/javascript">
    function editCache(cacheName) {
        var cacheNameInput = document.getElementById("editCacheName");
        cacheNameInput.value = cacheName;
        document.forms['formEditCache'].submit();
    }
</script>

<form action="cache.form" name="formEditCache">
    <input type="hidden" id="editCacheName" name="cacheName" value=""/>
</form>

Logic caches (${cachesCount}):<br/>
<ul>
    <c:forEach var="cacheName" varStatus="stat" items="${cacheNames}">
        <li class="${stat.index % 2 == 1 ? "oddRow" : "evenRow" }">
            ${cacheName}
            <input type="button" id="edit" value="edit" onclick="editCache('${cacheName}')"/>
        </li>
    </c:forEach>
</ul>

<%@ include file="/WEB-INF/template/footer.jsp" %>