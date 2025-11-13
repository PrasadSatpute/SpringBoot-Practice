<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Property Registration System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="nav-container">
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-brand">🏛️ Property Registration</a>
            <div class="nav-menu">
                <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/properties">Properties</a>
                <a href="${pageContext.request.contextPath}/registrations">Registrations</a>
                <span class="nav-user">👤 ${username}</span>
                <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
            </div>
        </div>
    </nav>

    <div class="container main-content">
        <div class="dashboard-header">
            <h1>Welcome, ${username}!</h1>
            <p>Role: ${role}</p>
        </div>

        <div class="dashboard-cards">
            <div class="dashboard-card">
                <h3>🏢 Properties</h3>
                <p>Manage property records</p>
                <a href="${pageContext.request.contextPath}/properties" class="btn btn-primary">View Properties</a>
            </div>

            <div class="dashboard-card">
                <h3>📝 Registrations</h3>
                <p>Manage property registrations</p>
                <a href="${pageContext.request.contextPath}/registrations" class="btn btn-primary">View Registrations</a>
            </div>

            <div class="dashboard-card">
                <h3>➕ New Property</h3>
                <p>Add a new property</p>
                <a href="${pageContext.request.contextPath}/properties/add" class="btn btn-secondary">Add Property</a>
            </div>

            <div class="dashboard-card">
                <h3>📄 New Registration</h3>
                <p>Create new registration</p>
                <a href="${pageContext.request.contextPath}/registrations/add" class="btn btn-secondary">New Registration</a>
            </div>
        </div>

        <div class="info-section">
            <h2>Quick Guide</h2>
            <div class="guide-steps">
                <div class="step">
                    <h4>Step 1: Add Property</h4>
                    <p>Register property details in the system</p>
                </div>
                <div class="step">
                    <h4>Step 2: Create Registration</h4>
                    <p>Initiate property transfer registration</p>
                </div>
                <div class="step">
                    <h4>Step 3: Calculate Tax</h4>
                    <p>System calculates stamp duty and fees automatically</p>
                </div>
                <div class="step">
                    <h4>Step 4: Complete Payment</h4>
                    <p>Mark payment as complete and download receipts</p>
                </div>
            </div>
        </div>
    </div>

    <footer class="footer">
        <p>&copy; 2024 Government of India - Property Registration Department</p>
    </footer>
</body>
</html>