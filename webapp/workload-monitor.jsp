<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>Workload Monitor</title>
    <link rel="stylesheet" href="styles.css"/>
</head>
<body>
<div class="page-shell">
    <header class="top-nav">
        <a href="login.jsp">Login</a>
        <a href="job-posting.jsp">Job Posting</a>
        <a href="review-panel.jsp">Review Panel</a>
        <a href="workload-monitor.jsp" class="current">Workload Monitor</a>
    </header>

    <main class="content-grid">
        <section class="panel">
            <div class="panel-header">
                <h1>TA Workload Monitoring</h1>
                <span class="status-chip">Admin</span>
            </div>

            <form action="/workload" method="get" class="filter-row">
                <input name="studentKeyword" placeholder="Search TA name"/>
                <select name="riskLevel">
                    <option value="">All Levels</option>
                    <option value="normal">Normal</option>
                    <option value="warning">Warning</option>
                    <option value="overloaded">Overloaded</option>
                </select>
                <button type="submit" class="secondary-button">Search</button>
            </form>

            <table class="data-table">
                <tr>
                    <th>TA</th>
                    <th>Assigned Work</th>
                    <th>Total Hours</th>
                    <th>Limit</th>
                    <th>Risk</th>
                </tr>
                <tr>
                    <td>Tom</td>
                    <td>Database Systems TA, Invigilation</td>
                    <td>10</td>
                    <td>12</td>
                    <td><span class="risk-badge normal">Normal</span></td>
                </tr>
                <tr>
                    <td>Alice</td>
                    <td>Programming Lab Support, Marking</td>
                    <td>12</td>
                    <td>12</td>
                    <td><span class="risk-badge warning">Warning</span></td>
                </tr>
                <tr>
                    <td>Ben</td>
                    <td>Networks TA, Lab Support, Invigilation</td>
                    <td>15</td>
                    <td>12</td>
                    <td><span class="risk-badge danger">Overloaded</span></td>
                </tr>
            </table>
        </section>

        <section class="panel">
            <div class="panel-header">
                <h2>Admin Actions</h2>
            </div>
            <form action="/adjustWorkload" method="post" class="form-grid">
                <label>
                    <span>TA ID</span>
                    <input name="studentId" placeholder="TA-003"/>
                </label>

                <label>
                    <span>Suggested Action</span>
                    <select name="actionType">
                        <option value="reassign">Reassign Task</option>
                        <option value="limit">Reduce Hours</option>
                        <option value="approve">Approve Exception</option>
                    </select>
                </label>

                <label class="full-width">
                    <span>Note</span>
                    <textarea name="note" rows="4" placeholder="Explain why the workload needs adjustment."></textarea>
                </label>

                <div class="button-row full-width">
                    <button type="submit" class="primary-button">Submit Action</button>
                </div>
            </form>
        </section>
    </main>
</div>
</body>
</html>
