<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<html>
<head>
    <title>Available Jobs</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/ta_style.css">
</head>
<body>
<div class="page-shell">
    <div class="top-nav">
        <a href="dashboard.jsp">Dashboard</a>
        <a href="profile.jsp">Profile</a>
        <a href="apply_status.jsp">Applications</a>
    </div>

    <div class="panel wide-panel">
        <div class="panel-header">
            <h2>AVAILABLE POSITIONS</h2>
            <button onclick="location.reload()" class="secondary-button">REFRESH</button>
        </div>

        <table class="data-table">
            <tr>
                <th>JOB ID</th>
                <th>MODULE</th>
                <th>SKILLS</th>
                <th>HOURS</th>
                <th>QUOTA</th>
                <th>ACTION</th>
            </tr>
            <%
                List<Job> list = (List<Job>) request.getAttribute("jobList");
                if(list!=null&&!list.isEmpty()) for(Job j : list) {
            %>
            <tr>
                <td>${j.jobId}</td>
                <td>${j.moduleName}</td>
                <td>${j.requiredSkills}</td>
                <td>${j.weeklyHours}</td>
                <td>${j.quota}</td>
                <td class="action-cell">
                    <form method="post" action="taJobApplyServlet">
                        <input type="hidden" name="jobId" value="${j.jobId}">
                        <button class="primary-button">APPLY</button>
                    </form>
                </td>
            </tr>
            <% } %>
        </table>
    </div>
</div>
</body>
</html>