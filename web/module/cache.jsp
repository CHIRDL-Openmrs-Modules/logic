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
<table>
    <%--<tr>--%>
        <%--<td>Status:</td>--%>
        <%--<td>${status}</td>--%>
    <%--</tr>--%>
    <%--<tr>--%>
        <%--<td>Statistics:</td>--%>
        <%--<td>${cacheStat}</td>--%>
    <%--</tr>--%>
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
<form action="cache.form" method="POST">
    <input type="hidden" name="action" value="action"/>
    <input type="submit" value="flush" />
</form>



<%@ include file="/WEB-INF/template/footer.jsp"%>