<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="View Administration Functions" otherwise="/login.htm" redirect="/admin/index.htm"/>

<form action="cache.form">
    <table cellpadding="0" cellspacing="0">
        <tr>
            <th>Max elements in memory</th>
            <th>Max elements on disk</th>
            <th>Default TTL</th>
        </tr>
        <tr>
            <td><input type="text" name="maxElemInMem" value=""/></td>
            <td><input type="text" name="maxElemOnDisk" value=""/></td>
            <td><input type="text" name="DefaultTTL" value=""/></td>
        </tr>
        <tr>
            <td colspan="3"><input type="submit" value="save"/></td>
        </tr>
    </table>
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

<%@ include file="/WEB-INF/template/footer.jsp" %>