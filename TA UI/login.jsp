<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>TA Recruitment System - Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/ta_style.css">
</head>
<body>
<div class="page-shell">
    <div class="hero-card">
        <h1 class="system-title">BUPT International School</h1>
        <p class="system-subtitle">TA Recruitment System | Login</p>
        <div class="mono-divider"></div>

        <% if (request.getAttribute("errorMsg") != null) { %>
            <div class="message-box">${errorMsg}</div>
        <% } %>

        <form action="${pageContext.request.contextPath}/taLoginServlet" method="post" class="form-grid">
            <label class="full-width">
                User ID
                <input type="text" name="userId" required>
            </label>
            <label class="full-width">
                Password
                <input type="password" name="password" required>
            </label>
            <div class="button-row full-width">
                <button type="submit" class="primary-button">LOGIN</button>
                <button type="reset" class="secondary-button">RESET</button>
            </div>
            <p class="muted-note full-width">First-time login: complete profile after login</p>
        </form>
    </div>
</div>
</body>
</html>