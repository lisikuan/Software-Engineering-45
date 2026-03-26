<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>MO/Admin Login</title>
    <link rel="stylesheet" href="styles.css"/>
</head>
<body>
<div class="page-shell">
    <header class="hero-card">
        <div class="system-title">BUPT International School</div>
        <div class="system-subtitle">TA Recruitment System</div>
    </header>

    <main class="content-grid single-column">
        <section class="panel">
            <div class="panel-header">
                <h2>Login</h2>
                <span class="status-chip">Prototype UI</span>
            </div>
            <form action="/login" method="post" class="form-grid">
                <label class="full-width">
                    <span>Select Role</span>
                    <div class="button-row">
                        <label><input type="radio" name="role" value="TA"/> TA</label>
                        <label><input type="radio" name="role" value="MO" checked="checked"/> Module Organiser (MO)</label>
                        <label><input type="radio" name="role" value="ADMIN"/> Admin</label>
                    </div>
                </label>

                <label>
                    <span>User ID</span>
                    <input name="username" placeholder="Enter User ID"/>
                </label>

                <label>
                    <span>Password</span>
                    <input type="password" name="password" placeholder="Enter Password"/>
                </label>

                <div class="message-box">
                    TA first-time login: please complete your profile.
                </div>

                <div class="button-row">
                    <button type="submit" class="primary-button">Login</button>
                    <button type="reset" class="secondary-button">Clear</button>
                </div>
            </form>
        </section>

        <section class="panel">
            <div class="panel-header">
                <h2>Quick Navigation</h2>
            </div>
            <nav class="nav-links">
                <a href="job-posting.jsp">MO Job Posting</a>
                <a href="review-panel.jsp">MO Review Panel</a>
                <a href="workload-monitor.jsp">Admin Workload Monitor</a>
            </nav>
        </section>
    </main>
</div>
</body>
</html>
