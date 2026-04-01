<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>TA Profile</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/ta_style.css">
</head>
<body>
<div class="page-shell">
    <div class="top-nav">
        <a href="dashboard.jsp">Dashboard</a>
        <a href="jobs_browse.jsp">Jobs</a>
        <a href="apply_status.jsp">Applications</a>
    </div>

    <div class="panel">
        <div class="panel-header">
            <h2>TA PROFILE</h2>
        </div>

        <% if (request.getAttribute("successMsg") != null) { %>
            <div class="message-box">${successMsg}</div>
        <% } %>

        <form action="${pageContext.request.contextPath}/taProfileServlet" method="post" class="form-grid">
            <label class="full-width">
                User ID (Read Only)
                <input type="text" name="userId" value="${ta.userId}" readonly>
            </label>
            <label>
                Full Name
                <input type="text" name="fullName" value="${ta.fullName}">
            </label>
            <label>
                Major
                <input type="text" name="major" value="${ta.major}">
            </label>
            <label>
                Grade
                <input type="text" name="grade" value="${ta.grade}">
            </label>
            <label class="full-width">
                Skills (Comma Separated)
                <input type="text" name="skills" value="${ta.skills}">
            </label>
            <div class="button-row full-width">
                <button type="submit" class="primary-button">SUBMIT</button>
                <a href="dashboard.jsp" class="secondary-button">BACK</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>