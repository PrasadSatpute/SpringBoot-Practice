<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Citizen Dashboard</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: Arial, sans-serif;
            background: #f5f5f5;
        }
        .navbar {
            background: #667eea;
            color: white;
            padding: 15px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .navbar h1 {
            font-size: 24px;
        }
        .navbar nav a {
            color: white;
            text-decoration: none;
            margin-left: 20px;
            padding: 8px 15px;
            border-radius: 5px;
            transition: background 0.3s;
        }
        .navbar nav a:hover {
            background: rgba(255,255,255,0.2);
        }
        .container {
            max-width: 1200px;
            margin: 30px auto;
            padding: 0 20px;
        }
        .welcome-card {
            background: white;
            padding: 30px;
            border-radius: 10px;
            margin-bottom: 30px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        .welcome-card h2 {
            color: #333;
            margin-bottom: 10px;
        }
        .actions {
            display: flex;
            gap: 15px;
            margin-top: 20px;
        }
        .btn {
            padding: 12px 24px;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;
            transition: all 0.3s;
            border: none;
            cursor: pointer;
            display: inline-block;
        }
        .btn-primary {
            background: #667eea;
            color: white;
        }
        .btn-primary:hover {
            background: #5568d3;
        }
        .btn-secondary {
            background: #6c757d;
            color: white;
        }
        .btn-secondary:hover {
            background: #5a6268;
        }
        .applications-section {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        .applications-section h3 {
            color: #333;
            margin-bottom: 20px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background: #f8f9fa;
            font-weight: bold;
            color: #333;
        }
        .status {
            padding: 5px 10px;
            border-radius: 5px;
            font-size: 12px;
            font-weight: bold;
        }
        .status-submitted {
            background: #cce5ff;
            color: #004085;
        }
        .status-approved {
            background: #d4edda;
            color: #155724;
        }
        .status-rejected {
            background: #f8d7da;
            color: #721c24;
        }
        .status-pending {
            background: #fff3cd;
            color: #856404;
        }
        .no-data {
            text-align: center;
            padding: 40px;
            color: #999;
        }
    </style>
</head>
<body>
    <div class="navbar">
        <h1>Property Registration System</h1>
        <nav>
            <a href="/citizen/dashboard">Dashboard</a>
            <a href="/citizen/profile">Profile</a>
            <a href="/logout">Logout</a>
        </nav>
    </div>

    <div class="container">
        <div class="welcome-card">
            <h2>Welcome, ${user.fullName}!</h2>
            <p>Manage your property registration applications from this dashboard.</p>

            <div class="actions">
                <a href="/citizen/application/new" class="btn btn-primary">New Application</a>
                <a href="/citizen/profile" class="btn btn-secondary">View Profile</a>
            </div>
        </div>

        <div class="applications-section">
            <h3>My Applications</h3>

            <c:choose>
                <c:when test="${not empty applications}">
                    <table>
                        <thead>
                            <tr>
                                <th>Application No.</th>
                                <th>Property Type</th>
                                <th>Location</th>
                                <th>Status</th>
                                <th>Submitted Date</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${applications}" var="app">
                                <tr>
                                    <td>${app.applicationNumber}</td>
                                    <td>${app.propertyType}</td>
                                    <td>${app.city}, ${app.zone}, ${app.ward}</td>
                                    <td>
                                        <span class="status status-${app.status == 'APPROVED' ? 'approved' : (app.status == 'REJECTED' ? 'rejected' : 'pending')}">
                                            ${app.status}
                                        </span>
                                    </td>
                                    <td>${app.createdAt}</td>
                                    <td>
                                        <a href="/citizen/application/view/${app.id}" class="btn btn-primary" style="padding: 6px 12px; font-size: 12px;">View</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <div class="no-data">
                        <p>No applications found. Click "New Application" to create your first application.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</body>
</html>