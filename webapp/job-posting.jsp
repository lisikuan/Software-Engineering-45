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
        <section class="panel">
            <div class="panel-header">
                <h1>Publish a TA Job</h1>
                <span class="status-chip">MO</span>
            </div>
            <form action="/addJob" method="post" class="form-grid">
                <label>
                    <span>Job Title</span>
                    <input name="title" placeholder="e.g. Database Systems TA"/>
                </label>

                <label>
                    <span>Module</span>
                    <input name="module" placeholder="e.g. EBU6304"/>
                </label>

                <label>
                    <span>Category</span>
                    <select name="category">
                        <option value="module">Module Support</option>
                        <option value="invigilation">Invigilation</option>
                        <option value="lab">Lab Support</option>
                    </select>
                </label>

                <label>
                    <span>Estimated Weekly Hours</span>
                    <input name="hours" type="number" min="1" max="20" placeholder="6"/>
                </label>

                <label>
                    <span>Application Deadline</span>
                    <input name="deadline" type="date"/>
                </label>

                <label class="full-width">
                    <span>Description</span>
                    <textarea name="description" rows="5" placeholder="Describe the work, schedule, and expectations."></textarea>
                </label>

                <label class="full-width">
                    <span>Required Skills</span>
                    <input name="skills" placeholder="Java, communication, marking experience"/>
                </label>

                <div class="button-row full-width">
                    <button type="submit" class="primary-button">Publish Job</button>
                    <button type="reset" class="secondary-button">Reset</button>
                </div>
            </form>
        </section>

        <section class="panel">
            <div class="panel-header">
                <h2>Published Jobs</h2>
            </div>
            <table class="data-table">
                <tr>
                    <th>Title</th>
                    <th>Module</th>
                    <th>Hours</th>
                    <th>Status</th>
                </tr>
                <tr>
                    <td>Database Systems TA</td>
                    <td>EBU6304</td>
                    <td>6</td>
                    <td>Open</td>
                </tr>
                <tr>
                    <td>Programming Lab Support</td>
                    <td>EBU6203</td>
                    <td>4</td>
                    <td>Draft</td>
                </tr>
            </table>
        </section>
    </main>
</div>
</body>
</html>
