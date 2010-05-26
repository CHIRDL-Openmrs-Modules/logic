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
Cache stat: ${cacheStat}
<br/>
Cache dir: ${cacheDir}
<br/>
Serialized size: ${serializedSize}
<br/>
<form action="cache.form" method="POST">
    <input type="hidden" name="flush" value="flush"/>
    <input type="submit" value="flush" />
</form>



<%@ include file="/WEB-INF/template/footer.jsp"%>