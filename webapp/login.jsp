<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>MO/Admin Login</title>
    <link rel="stylesheet" href="styles.css"/>
</head>
<body>
<main class="login-shell">
    <section class="panel login-card">
        <div class="login-intro">
            <div class="brand-block">
                <div class="brand-mark">TA</div>
                <div>
                    <p class="brand-title">System</p>
                    <p class="brand-caption">Teaching Assistant Recruitment</p>
                </div>
            </div>
            <h1>Welcome back</h1>
            <p>Review applications, publish module positions, and keep TA workload balanced from one clean workspace.</p>
        </div>

        <div class="panel-body">
            <div class="page-heading">
                <div>
                    <h1>MO / Admin Login</h1>
                    <p>Use your role account to continue.</p>
                </div>
                <span class="status-chip">Prototype</span>
            </div>

            <form action="/login" method="post" class="form-grid">
                <label class="full-width">
                    <span>Select Role</span>
                    <div class="role-options">
                        <label><input type="radio" name="role" value="TA"/> TA</label>
                        <label><input type="radio" name="role" value="MO" checked="checked"/> MO</label>
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
                    First-time TA users should complete their profile before applying for positions.
                </div>

                <div class="button-row full-width">
                    <button type="submit" class="primary-button">Login</button>
                    <button type="reset" class="secondary-button">Clear</button>
                </div>
            </form>

            <div class="toolbar" style="margin-top: 24px; margin-bottom: 0;">
                <a class="secondary-button" href="job-posting.jsp">Job Posting</a>
                <a class="secondary-button" href="review-panel.jsp">Review Panel</a>
                <a class="secondary-button" href="workload-monitor.jsp">Workload Monitor</a>
            </div>
        </div>
    </section>
</main>
</body>
</html>
