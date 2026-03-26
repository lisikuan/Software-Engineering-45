<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>Job Posting</title>
    <link rel="stylesheet" href="styles.css"/>
</head>
<body>
<div class="page-shell">
    <header class="top-nav">
        <a href="login.jsp">Login</a>
        <a href="job-posting.jsp" class="current">Job Posting</a>
        <a href="review-panel.jsp">Review Panel</a>
        <a href="workload-monitor.jsp">Workload Monitor</a>
    </header>

    <main class="content-grid">
        <section class="panel wide-panel">
            <div class="panel-header">
                <h1 class="stacked-title">SYSTEM / MO PORTAL</h1>
                <span class="status-chip">MO</span>
            </div>
            <p class="muted-note">Strict terminal mode. Fill all required parameters below.</p>
            <div class="mono-divider"></div>
            <form action="/addJob" method="post" class="form-grid">
                <label>
                    <span>MO ID</span>
                    <input name="moId" placeholder="MO-84920"/>
                </label>

                <label>
                    <span>Quota</span>
                    <input name="quota" type="number" min="1" placeholder="1"/>
                </label>

                <label>
                    <span>Module Name</span>
                    <input name="module" placeholder="e.g. Frontend Development Core"/>
                </label>

                <label class="full-width">
                    <span>Required Skills</span>
                    <textarea name="skills" rows="3" placeholder="React, TypeScript, CSS..."></textarea>
                </label>

                <label>
                    <span>Weekly Hours</span>
                    <input name="hours" type="number" min="1" max="20" placeholder="1-20"/>
                </label>

                <label>
                    <span>Application Deadline</span>
                    <input name="deadline" type="date"/>
                </label>

                <label class="full-width">
                    <span>Description (Optional)</span>
                    <textarea name="description" rows="5" placeholder="Enter detailed job description here..."></textarea>
                </label>

                <div class="button-row full-width">
                    <button type="reset" class="secondary-button">Reset</button>
                    <button type="submit" class="primary-button">Post Job</button>
                </div>
            </form>
        </section>

        <section class="panel">
            <div class="panel-header">
                <h2>Published Jobs</h2>
                <button type="button" class="secondary-button">Refresh List</button>
            </div>
            <table class="data-table">
                <tr>
                    <th>Job ID</th>
                    <th>Module</th>
                    <th>Required Skills</th>
                    <th>Weekly Hours</th>
                    <th>Status</th>
                </tr>
                <tr>
                    <td>JOB-2026-001</td>
                    <td>Frontend Core</td>
                    <td><span class="job-chip">React</span><span class="job-chip">TypeScript</span></td>
                    <td>6</td>
                    <td>Open</td>
                </tr>
                <tr>
                    <td>JOB-2026-002</td>
                    <td>Backend API</td>
                    <td><span class="job-chip">Node.js</span><span class="job-chip">SQL</span></td>
                    <td>4</td>
                    <td>Draft</td>
                </tr>
            </table>
        </section>
    </main>
</div>
</body>
</html>
