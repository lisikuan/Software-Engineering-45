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
        <section class="panel">
            <div class="panel-header">
                <h1>Review Applicants</h1>
                <span class="status-chip">MO</span>
            </div>

            <form action="/reviewFilter" method="get" class="filter-row">
                <input name="jobKeyword" placeholder="Search by job or applicant"/>
                <select name="status">
                    <option value="">All Status</option>
                    <option value="submitted">Submitted</option>
                    <option value="shortlisted">Shortlisted</option>
                    <option value="rejected">Rejected</option>
                </select>
                <button type="submit" class="secondary-button">Filter</button>
            </form>

            <table class="data-table">
                <tr>
                    <th>Name</th>
                    <th>Applied Job</th>
                    <th>Skills</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
                <tr>
                    <td>Tom</td>
                    <td>Database Systems TA</td>
                    <td>Java, SQL</td>
                    <td>Submitted</td>
                    <td class="action-cell">
                        <button class="primary-button">Accept</button>
                        <button class="danger-button">Reject</button>
                    </td>
                </tr>
                <tr>
                    <td>Alice</td>
                    <td>Programming Lab Support</td>
                    <td>Python, mentoring</td>
                    <td>Shortlisted</td>
                    <td class="action-cell">
                        <button class="secondary-button">View CV</button>
                        <button class="primary-button">Confirm</button>
                    </td>
                </tr>
            </table>
        </section>

        <section class="panel">
            <div class="panel-header">
                <h2>Review Notes</h2>
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
                    <button type="submit" class="primary-button">Save Review</button>
                </div>
            </form>
        </section>
    </main>
</div>
</body>
</html>
