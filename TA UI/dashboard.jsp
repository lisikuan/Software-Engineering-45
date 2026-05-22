<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>TA Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/ta_style.css">
</head>
<body>
<div class="page-shell">
    <div class="top-nav">
        <a href="profile.jsp">Profile</a>
        <a href="jobs_browse.jsp">Jobs</a>
        <a href="cv_upload.jsp">Upload CV</a>
        <a href="apply_status.jsp">Applications</a>
        <a href="#">Logout</a>
    </div>

    <div class="hero-card">
        <h1 class="stacked-title">TA DASHBOARD</h1>
        <div class="mono-divider"></div>
        <p>User ID: ${ta.userId}</p>
        <p>Name: ${ta.fullName == null ? 'UNCOMPLETED' : ta.fullName}</p>
    </div>

    <div class="content-grid single-column">
        <div class="panel wide-panel">
            <div class="panel-header">
                <h2>APPLICATION STATISTICS</h2>
            </div>
            <div class="form-grid">
                <div>PENDING: ${applyStats.pending}</div>
                <div>ACCEPTED: ${applyStats.accepted}</div>
                <div>REJECTED: ${applyStats.rejected}</div>
            </div>
        </div>
    </div>
</div>
</body>
</html>