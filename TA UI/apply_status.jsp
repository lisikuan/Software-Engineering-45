<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<html>
<head>
    <title>Application Status</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/ta_style.css">
</head>
<body>
<div class="page-shell">
    <div class="top-nav">
        <a href="dashboard.jsp">Dashboard</a>
        <a href="jobs_browse.jsp">Browse Jobs</a>
    </div>

    <div class="panel wide-panel">
        <div class="panel-header">
            <h2>MY APPLICATIONS</h2>
            <button onclick="location.reload()" class="secondary-button">REFRESH</button>
        </div>

        <table class="data-table">
            <tr>
                <th>APP ID</th>
                <th>JOB ID</th>
                <th>MATCH %</th>
                <th>STATUS</th>
            </tr>
            <%
                List<Application> apps = (List<Application>) request.getAttribute("applyList");
                if(apps!=null&&!apps.isEmpty()) for(Application a : apps) {
            %>
            <tr>
                <td>${a.appId}</td>
                <td>${a.jobId}</td>
                <td>${a.matchPercent}%</td>
                <td><span class="status-chip">${a.status}</span></td>
            </tr>
            <% } %>
        </table>
    </div>
</div>
</body>
</html>