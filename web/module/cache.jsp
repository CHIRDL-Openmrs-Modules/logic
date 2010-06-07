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

<br/>${cacheName} statisctics:<br/>
<table>
    <tr>
        <td>Status:</td>
        <td>${status}</td>
    </tr>
    <tr>
        <td>Statistics:</td>
        <td>${cacheStat}</td>
    </tr>
    <tr>
        <td>Cache dir:</td>
        <td>${cacheDir}</td>
    </tr>
    <tr>
        <td>Disk store size:</td>
        <td>${serializedSize}</td>
    </tr>
    <tr>
        <td>cacheToStr:</td>
        <td>${cacheToStr}</td>
    </tr>
</table>

<br/>
<form action="cache.form" method="POST">
    <input type="hidden" name="action" value="action"/>
    <input type="submit" value="expireOnDisk" />
</form>



<%@ include file="/WEB-INF/template/footer.jsp"%>