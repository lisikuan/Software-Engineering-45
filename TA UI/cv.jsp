<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Upload CV</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/ta_style.css">
</head>
<body>
<div class="page-shell">
    <div class="top-nav">
        <a href="dashboard.jsp">Dashboard</a>
        <a href="profile.jsp">Profile</a>
        <a href="jobs_browse.jsp">Jobs</a>
    </div>

    <div class="panel">
        <div class="panel-header">
            <h2>CV UPLOAD</h2>
        </div>

        <% if (request.getAttribute("successMsg") != null) { %>
            <div class="message-box">${successMsg}</div>
        <% } %>

        <form method="post" enctype="multipart/form-data" action="taCvUploadServlet" class="form-grid">
            <label class="full-width">
                TA ID
                <input value="${ta.userId}" readonly>
            </label>
            <label class="full-width">
                Select PDF File
                <input type="file" name="cvFile" accept=".pdf">
            </label>
            <p class="muted-note full-width">Only PDF format is allowed</p>
            <div class="button-row full-width">
                <button type="submit" class="primary-button">UPLOAD</button>
                <a href="dashboard.jsp" class="secondary-button">BACK</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>