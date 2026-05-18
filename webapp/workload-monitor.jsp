<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Workload Monitor</title>
    <link rel="stylesheet" href="styles.css"/>
</head>
<body>
<div class="app-layout">
    <aside class="sidebar">
        <div class="brand-block">
            <div class="brand-mark">TA</div>
            <div>
                <p class="brand-title">System</p>
                <p class="brand-caption">Workload Overview</p>
            </div>
        </div>
        <nav class="side-nav">
            <a href="login.jsp"><span class="nav-index">01</span> Login</a>
            <a href="review-panel.jsp"><span class="nav-index">02</span> Applications</a>
            <a href="job-posting.jsp"><span class="nav-index">03</span> Modules</a>
            <a href="workload-monitor.jsp" class="current"><span class="nav-index">04</span> Workload</a>
        </nav>
        <div class="sidebar-footer">TA Recruitment System</div>
    </aside>

    <div class="main-shell">
        <header class="topbar">
            <div class="topbar-icon">N</div>
            <div class="user-pill">
                <div>
                    <p class="user-name">Module Office</p>
                    <p class="user-role">Workload Supervisor</p>
                </div>
                <div class="avatar">M</div>
            </div>
        </header>

        <main class="page-content">
            <div class="page-heading">
                <div>
                    <h1>Workload Monitor</h1>
                    <p>Monitor assigned hours and rebalance workload before approval.</p>
                </div>
                <button type="submit" form="workloadActionForm" class="primary-button">Submit Action</button>
            </div>

            <section class="kpi-grid">
                <div class="kpi-card"><span>Active TAs</span><strong>38</strong></div>
                <div class="kpi-card"><span>Warning Cases</span><strong>7</strong></div>
                <div class="kpi-card"><span>Overloaded</span><strong>2</strong></div>
            </section>

            <section class="content-grid">
                <div class="panel">
                    <div class="panel-header">
                        <h2>Assigned Workload</h2>
                        <span class="status-chip">Overview</span>
                    </div>
                    <div class="panel-body">
                        <form action="/workload" method="get" class="filter-row">
                            <div class="search-field">
                                <input name="studentKeyword" placeholder="Search TA name"/>
                            </div>
                            <select name="riskLevel">
                                <option value="">All Levels</option>
                                <option value="normal">Normal</option>
                                <option value="warning">Warning</option>
                                <option value="overloaded">Overloaded</option>
                            </select>
                            <button type="submit" class="secondary-button">Search</button>
                        </form>

                        <table class="data-table" style="margin-top: 18px;">
                            <tr>
                                <th>TA</th>
                                <th>Assigned Work</th>
                                <th>Total Hours</th>
                                <th>Limit</th>
                                <th>Risk</th>
                                <th>Action</th>
                            </tr>
                            <tr>
                                <td><span class="entity-title">Tom</span><span class="entity-subtitle">TA-014</span></td>
                                <td>Database Systems TA, Invigilation</td>
                                <td>10</td>
                                <td>12</td>
                                <td><span class="risk-badge normal">Normal</span></td>
                                <td><button type="button" class="link-action">Inspect</button></td>
                            </tr>
                            <tr>
                                <td><span class="entity-title">Alice</span><span class="entity-subtitle">TA-023</span></td>
                                <td>Programming Lab Support, Marking</td>
                                <td>12</td>
                                <td>12</td>
                                <td><span class="risk-badge warning">Warning</span></td>
                                <td><button type="button" class="link-action">Inspect</button></td>
                            </tr>
                            <tr>
                                <td><span class="entity-title">Ben</span><span class="entity-subtitle">TA-031</span></td>
                                <td>Networks TA, Lab Support, Invigilation</td>
                                <td>15</td>
                                <td>12</td>
                                <td><span class="risk-badge danger">Overloaded</span></td>
                                <td><button type="button" class="link-action">Inspect</button></td>
                            </tr>
                        </table>
                    </div>
                </div>

                <div class="panel">
                    <div class="panel-header">
                        <h2>Workload Actions</h2>
                        <span class="status-chip pending">Adjustment</span>
                    </div>
                    <div class="panel-body">
                        <form id="workloadActionForm" action="/adjustWorkload" method="post" class="form-grid">
                            <label class="full-width">
                                <span>TA ID</span>
                                <input name="studentId" placeholder="TA-003"/>
                            </label>

                            <label class="full-width">
                                <span>Suggested Action</span>
                                <select name="actionType">
                                    <option value="reassign">Reassign Task</option>
                                    <option value="limit">Reduce Hours</option>
                                    <option value="approve">Approve Exception</option>
                                </select>
                            </label>

                            <label class="full-width">
                                <span>Note</span>
                                <textarea name="note" rows="6" placeholder="Explain why the workload needs adjustment."></textarea>
                            </label>

                            <div class="button-row full-width">
                                <button type="submit" class="primary-button">Submit Action</button>
                            </div>
                        </form>
                    </div>
                </div>
            </section>
        </main>
    </div>
</div>
</body>
</html>
