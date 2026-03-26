<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>Applicant Review Panel</title>
    <link rel="stylesheet" href="styles.css"/>
</head>
<body>
<div class="page-shell">
    <header class="top-nav">
        <a href="login.jsp">Login</a>
        <a href="job-posting.jsp">Job Posting</a>
        <a href="review-panel.jsp" class="current">Review Panel</a>
        <a href="workload-monitor.jsp">Workload Monitor</a>
    </header>

    <main class="content-grid">
        <section class="panel wide-panel">
            <div class="panel-header">
                <h1 class="stacked-title">SWING PAGE DESIGN</h1>
                <button type="button" class="secondary-button">Refresh Jobs</button>
            </div>

            <form action="/reviewFilter" method="get" class="filter-row">
                <select name="jobKeyword">
                    <option value="">Select Job</option>
                    <option value="job-001">Database Systems TA</option>
                    <option value="job-002">Programming Lab Support</option>
                </select>
                <div></div>
                <button type="submit" class="secondary-button">Load Applicants</button>
            </form>

            <table class="data-table">
                <tr>
                    <th>App ID</th>
                    <th>TA ID</th>
                    <th>TA Name</th>
                    <th>TA Skills</th>
                    <th>Match %</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
                <tr>
                    <td>APP-001</td>
                    <td>TA-014</td>
                    <td>Tom</td>
                    <td>Java, SQL</td>
                    <td>87%</td>
                    <td>Submitted</td>
                    <td class="action-cell">
                        <button class="secondary-button">View Details</button>
                    </td>
                </tr>
                <tr>
                    <td>APP-002</td>
                    <td>TA-023</td>
                    <td>Alice</td>
                    <td>Python, mentoring</td>
                    <td>79%</td>
                    <td>Shortlisted</td>
                    <td class="action-cell">
                        <button class="secondary-button">View Details</button>
                    </td>
                </tr>
            </table>
        </section>

        <section class="panel">
            <div class="panel-header">
                <h2>Application Decision</h2>
            </div>
            <form action="/reviewApplication" method="post" class="form-grid">
                <label>
                    <span>Application ID</span>
                    <input name="applicationId" placeholder="APP-001"/>
                </label>

                <label>
                    <span>Decision</span>
                    <select name="decision">
                        <option value="accept">Accept</option>
                        <option value="reject">Reject</option>
                        <option value="hold">Hold</option>
                    </select>
                </label>

                <label class="full-width">
                    <span>Comment</span>
                    <textarea name="comment" rows="4" placeholder="Add feedback for the applicant or internal notes."></textarea>
                </label>

                <div class="button-row full-width">
                    <button type="button" class="secondary-button">View CV</button>
                    <button type="submit" class="primary-button">Save Review</button>
                    <button type="button" class="danger-button">Reject</button>
                </div>
            </form>
        </section>
    </main>
</div>
</body>
</html>
