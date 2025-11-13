<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Property Registration System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <div class="auth-container">
            <div class="auth-header">
                <h2>🏛️ Login to Property Registration</h2>
                <p>Enter your credentials to access the system</p>
            </div>

            <c:if test="${param.error != null}">
                <div class="alert alert-danger">
                    Invalid username or password!
                </div>
            </c:if>

            <c:if test="${param.logout != null}">
                <div class="alert alert-success">
                    You have been logged out successfully!
                </div>
            </c:if>

            <c:if test="${not empty success}">
                <div class="alert alert-success">${success}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/perform-login" method="post" class="auth-form">
                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" required
                           placeholder="Enter your username" autofocus>
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required
                           placeholder="Enter your password">
                </div>

                <button type="submit" class="btn btn-primary btn-block">Login</button>
            </form>

            <div class="auth-footer">
                <p>Don't have an account? <a href="${pageContext.request.contextPath}/register">Register here</a></p>
                <p><a href="${pageContext.request.contextPath}/">← Back to Home</a></p>
            </div>

            <div class="demo-credentials">
                <h4>Demo Credentials:</h4>
                <ul>
                    <li><strong>Admin:</strong> admin / admin123</li>
                    <li><strong>Officer:</strong> officer / officer123</li>
                    <li><strong>Citizen:</strong> citizen / citizen123</li>
                </ul>
            </div>
        </div>
    </div>
</body>
</html>