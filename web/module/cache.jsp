<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:require privilege="View Administration Functions" otherwise="/login.htm" redirect="/admin/index.htm" />

<br/>
<script type="text/javascript">
    function doCacheManagerAction(command) {
        if(command != '') {
            var action = document.getElementById("action");
            action.value = command;
        }
        document.forms['formCacheManagerAction'].submit();
    }
</script>
<form action="cache.form" method="POST" name="formCacheManagerAction">
    <input type="hidden" id="action" name="action" value="flush"/>
    <input type="button" id="flush" value="flush" onclick="doCacheManagerAction('flush')" />
    <input type="button" id="clear" value="clear" onclick="doCacheManagerAction('clear')" />
    <input type="button" id="shutdown" value="shutdown" onclick="doCacheManagerAction('shutdown')" />
    <input type="button" id="refresh" value="refresh" onclick="location.replace('')" />
</form>

Logic caches (${cachesCount}):<br/>
<ul>
<c:forEach var="cacheName" varStatus="stat" items="${cacheNames}">
    <li class="${stat.index % 2 == 1 ? "oddRow" : "evenRow" }">
        ${cacheName}
        <form action="cache.form" >
            <table cellpadding="0" cellspacing="0">
                <tr>
                    <th>Max elements in memory</th>
                    <th>Max elements on disk</th>
                    <th>Default TTL</th>
                </tr>
                <tr>
                    <td><input type="text" name="maxElemInMem" value="" /></td>
                    <td><input type="text" name="maxElemOnDisk" value="" /></td>
                    <td><input type="text" name="DefaultTTL" value="" /></td>
                </tr>
                <tr>
                    <td colspan="3"><input type="submit" value="save" /></td>
                </tr>
            </table>
        </form>
    </li>
</c:forEach>
</ul>

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
    <tr>
        <td>Cache size:</td>
        <td>${cacheSize}</td>
    </tr>
    <tr style="color:#bfbdbd;">
        <td>Cache specific stats:</td>
        <td>${cacheToStr}</td>
    </tr>
</table>
<br/>
<hr/>

<%@ include file="/WEB-INF/template/footer.jsp"%>