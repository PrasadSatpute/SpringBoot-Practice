<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Registration - Property Registration System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="nav-container">
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-brand">🏛️ Property Registration</a>
            <div class="nav-menu">
                <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/properties">Properties</a>
                <a href="${pageContext.request.contextPath}/registrations" class="active">Registrations</a>
                <span class="nav-user">👤 ${username}</span>
                <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
            </div>
        </div>
    </nav>

    <div class="container main-content">
        <div class="page-header">
            <h1>Edit Registration - ${registration.registrationNumber}</h1>
            <a href="${pageContext.request.contextPath}/registrations" class="btn btn-secondary">← Back to List</a>
        </div>

        <form action="${pageContext.request.contextPath}/registrations/edit/${registration.id}" method="post" class="form-container">
            <div class="form-section">
                <h3>Transaction Information</h3>

                <div class="form-group">
                    <label for="transactionValue">Transaction Value (₹) *</label>
                    <input type="number" id="transactionValue" name="transactionValue"
                           step="0.01" value="${registration.transactionValue}" required>
                </div>
            </div>

            <div class="form-section">
                <h3>Buyer Information</h3>

                <div class="form-group">
                    <label for="buyerName">Buyer Name *</label>
                    <input type="text" id="buyerName" name="buyerName"
                           value="${registration.buyerName}" required>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="buyerAadhar">Aadhar Number</label>
                        <input type="text" id="buyerAadhar" name="buyerAadhar"
                               value="${registration.buyerAadhar}">
                    </div>

                    <div class="form-group">
                        <label for="buyerPan">PAN Number</label>
                        <input type="text" id="buyerPan" name="buyerPan"
                               value="${registration.buyerPan}">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="buyerPhone">Phone Number</label>
                        <input type="tel" id="buyerPhone" name="buyerPhone"
                               value="${registration.buyerPhone}">
                    </div>

                    <div class="form-group">
                        <label for="buyerEmail">Email</label>
                        <input type="email" id="buyerEmail" name="buyerEmail"
                               value="${registration.buyerEmail}">
                    </div>
                </div>

                <div class="form-group">
                    <label for="buyerAddress">Address</label>
                    <textarea id="buyerAddress" name="buyerAddress" rows="2">${registration.buyerAddress}</textarea>
                </div>
            </div>

            <div class="form-section">
                <h3>Seller Information</h3>

                <div class="form-group">
                    <label for="sellerName">Seller Name *</label>
                    <input type="text" id="sellerName" name="sellerName"
                           value="${registration.sellerName}" required>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="sellerAadhar">Aadhar Number</label>
                        <input type="text" id="sellerAadhar" name="sellerAadhar"
                               value="${registration.sellerAadhar}">
                    </div>

                    <div class="form-group">
                        <label for="sellerPan">PAN Number</label>
                        <input type="text" id="sellerPan" name="sellerPan"
                               value="${registration.sellerPan}">
                    </div>
                </div>
            </div>

            <div class="form-section">
                <div class="form-group">
                    <label for="remarks">Remarks</label>
                    <textarea id="remarks" name="remarks" rows="3">${registration.remarks}</textarea>
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">💾 Update Registration</button>
                <a href="${pageContext.request.contextPath}/registrations" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>

    <footer class="footer">
        <p>&copy; 2024 Government of India - Property Registration Department</p>
    </footer>
</body>
</html>