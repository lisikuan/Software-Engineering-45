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
        <p class="eyebrow">TA Recruitment System</p>
        <h1>MO / Admin Portal</h1>
        <p class="hero-text">Sign in to manage job postings, review applicants, and monitor teaching assistant workload.</p>
    </header>

    <main class="content-grid single-column">
        <section class="panel">
            <div class="panel-header">
                <h2>Login</h2>
                <span class="status-chip">Prototype</span>
            </div>
            <form action="/login" method="post" class="form-grid">
                <label>
                    <span>Username</span>
                    <input name="username" placeholder="Enter your username"/>
                </label>

                <label>
                    <span>Password</span>
                    <input type="password" name="password" placeholder="Enter your password"/>
                </label>

                <label>
                    <span>Role</span>
                    <select name="role">
                        <option value="MO">Module Organiser</option>
                        <option value="ADMIN">Admin</option>
                    </select>
                </label>

                <div class="message-box">
                    Demo note: after login, MO users should go to job posting and applicant review, while Admin users should go to workload monitoring.
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
                <a href="job-posting.jsp">Post a Job</a>
                <a href="review-panel.jsp">Review Applicants</a>
                <a href="workload-monitor.jsp">Monitor Workload</a>
            </nav>
        </section>
    </main>
</div>
</body>
</html>
