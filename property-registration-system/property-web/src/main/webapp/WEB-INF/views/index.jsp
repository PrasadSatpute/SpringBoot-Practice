<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Property Registration System - Home</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header class="header">
            <h1>🏛️ Government Property Registration System</h1>
            <p class="subtitle">Digital India Initiative</p>
        </header>

        <div class="hero-section">
            <h2>Welcome to Property Registration Portal</h2>
            <p>Register your property online with ease and transparency</p>

            <div class="action-buttons">
                <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">Login</a>
                <a href="${pageContext.request.contextPath}/register" class="btn btn-secondary">Register</a>
            </div>
        </div>

        <div class="features">
            <div class="feature-card">
                <h3>📝 Easy Registration</h3>
                <p>Register properties online without visiting offices</p>
            </div>
            <div class="feature-card">
                <h3>💰 Tax Calculation</h3>
                <p>Automatic calculation of stamp duty and registration fees</p>
            </div>
            <div class="feature-card">
                <h3>📄 PDF Receipts</h3>
                <p>Download official receipts and certificates</p>
            </div>
            <div class="feature-card">
                <h3>🔒 Secure & Safe</h3>
                <p>Government-grade security for your data</p>
            </div>
        </div>

        <footer class="footer">
            <p>&copy; 2024 Government of India - Property Registration Department</p>
            <p>All Rights Reserved</p>
        </footer>
    </div>
</body>
</html>