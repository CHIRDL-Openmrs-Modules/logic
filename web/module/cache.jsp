<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
there are ${cachesCount} caches<br/>
<ul>
<c:forEach var="cacheName" varStatus="stat" items="${cacheNames}">
    <li class="${stat.index % 2 == 1 ? "oddRow" : "evenRow" }">
        ${cacheName}
    </li>
</c:forEach>
</ul>

<br/><b>${cacheName} statisctics:</b><br/>
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
    <tr>
        <td>Cache specific stats:</td>
        <td>${cacheToStr}</td>
    </tr>
</table>

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
    <input type="button" id="clear" value="clear" disabled="true" onclick="doCacheManagerAction('clear')" />
    <input type="button" id="shutdown" value="shutdown" onclick="doCacheManagerAction('shutdown')" />
</form>



<%@ include file="/WEB-INF/template/footer.jsp"%>