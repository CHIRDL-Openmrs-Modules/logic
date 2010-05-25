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

<br/>
${cacheName} statisctics:
<br/>
Status: ${status}
<br/>
Objects cached: ${cacheCount}
<br/>
<br/>



<%@ include file="/WEB-INF/template/footer.jsp"%>