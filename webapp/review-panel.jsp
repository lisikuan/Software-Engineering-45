<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Application Management</title>
    <link rel="stylesheet" href="styles.css"/>
</head>
<body>
<div class="app-layout">
    <aside class="sidebar">
        <div class="brand-block">
            <div class="brand-mark">TA</div>
            <div>
                <p class="brand-title">System</p>
                <p class="brand-caption">MO Portal</p>
            </div>
        </div>
        <nav class="side-nav">
            <a href="login.jsp"><span class="nav-index">01</span> Login</a>
            <a href="review-panel.jsp" class="current"><span class="nav-index">02</span> Applications</a>
            <a href="job-posting.jsp"><span class="nav-index">03</span> Modules</a>
            <a href="workload-monitor.jsp"><span class="nav-index">04</span> Workload</a>
        </nav>
        <div class="sidebar-footer">TA Recruitment System</div>
    </aside>

    <div class="main-shell">
        <header class="topbar">
            <div class="topbar-icon">N</div>
            <div class="user-pill">
                <div>
                    <p class="user-name">Dr. Sarah Jenkins</p>
                    <p class="user-role">Module Organiser</p>
                </div>
                <div class="avatar">S</div>
            </div>
        </header>

        <main class="page-content">
            <div class="page-heading">
                <div>
                    <h1>Application Management</h1>
                    <p>Review and manage all teaching assistant applications across modules.</p>
                </div>
                <button type="button" class="primary-button">Export Selected</button>
            </div>

            <section class="kpi-grid">
                <div class="kpi-card"><span>Total Applications</span><strong>45</strong></div>
                <div class="kpi-card"><span>Pending Review</span><strong>12</strong></div>
                <div class="kpi-card"><span>Accepted</span><strong>8</strong></div>
            </section>

            <section class="panel wide-panel">
                <div class="panel-body">
                    <div class="toolbar">
                        <form action="/reviewFilter" method="get" class="filter-row">
                            <div class="search-field">
                                <input name="applicantKeyword" placeholder="Search by name, ID, or email"/>
                            </div>
                            <select name="jobKeyword">
                                <option value="">All Modules</option>
                                <option value="job-001">Database Systems TA</option>
                                <option value="job-002">Programming Lab Support</option>
                            </select>
                            <button type="submit" class="secondary-button">More Filters</button>
                        </form>
                        <span class="meta-text">Showing <strong>45</strong> total applications</span>
                    </div>

                    <table class="data-table">
                        <tr>
                            <th></th>
                            <th>Applicant ID</th>
                            <th>Applicant Info</th>
                            <th>Target Module</th>
                            <th>Match</th>
                            <th>Status</th>
                            <th>Applied On</th>
                            <th>Actions</th>
                        </tr>
                        <tr>
                            <td><input type="checkbox"/></td>
                            <td>APP-2026-001</td>
                            <td><span class="entity-title">Tom</span><span class="entity-subtitle">TA-014 - Java, SQL</span></td>
                            <td>CS101</td>
                            <td>87%</td>
                            <td><span class="status-chip pending">Submitted</span></td>
                            <td>Oct 20, 2026</td>
                            <td class="action-cell"><button type="button" class="link-action">Review</button><span>...</span></td>
                        </tr>
                        <tr>
                            <td><input type="checkbox"/></td>
                            <td>APP-2026-002</td>
                            <td><span class="entity-title">Alice</span><span class="entity-subtitle">TA-023 - Python, mentoring</span></td>
                            <td>CS204</td>
                            <td>79%</td>
                            <td><span class="status-chip interviewed">Shortlisted</span></td>
                            <td>Oct 19, 2026</td>
                            <td class="action-cell"><button type="button" class="link-action">Review</button><span>...</span></td>
                        </tr>
                    </table>
                </div>
            </section>

            <section class="panel" style="margin-top: 24px;">
                <div class="panel-header">
                    <h2>Application Decision</h2>
                    <span class="status-chip">Review Action</span>
                </div>
                <div class="panel-body">
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
                </div>
            </section>
        </main>
    </div>
</div>
</body>
</html>
