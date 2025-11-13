<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>New Registration - Property Registration System</title>
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
            <h1>Create New Registration</h1>
            <a href="${pageContext.request.contextPath}/registrations" class="btn btn-secondary">← Back to List</a>
        </div>

        <form action="${pageContext.request.contextPath}/registrations/add" method="post" class="form-container">
            <div class="form-section">
                <h3>Property Selection</h3>

                <div class="form-group">
                    <label for="propertyId">Select Property *</label>
                    <select id="propertyId" name="propertyId" required>
                        <option value="">Select a property</option>
                        <c:forEach var="property" items="${properties}">
                            <option value="${property.id}">
                                ${property.propertyNumber} - ${property.ownerName} - ${property.address}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="transactionValue">Transaction Value (₹) *</label>
                    <input type="number" id="transactionValue" name="transactionValue"
                           step="0.01" required placeholder="Enter transaction value">
                </div>
            </div>

            <div class="form-section">
                <h3>Buyer Information</h3>

                <div class="form-group">
                    <label for="buyerName">Buyer Name *</label>
                    <input type="text" id="buyerName" name="buyerName" required
                           placeholder="Enter buyer full name">
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="buyerAadhar">Aadhar Number</label>
                        <input type="text" id="buyerAadhar" name="buyerAadhar"
                               placeholder="12-digit Aadhar" pattern="[0-9]{12}" maxlength="12">
                    </div>

                    <div class="form-group">
                        <label for="buyerPan">PAN Number</label>
                        <input type="text" id="buyerPan" name="buyerPan"
                               placeholder="PAN" pattern="[A-Z]{5}[0-9]{4}[A-Z]{1}" maxlength="10">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="buyerPhone">Phone Number</label>
                        <input type="tel" id="buyerPhone" name="buyerPhone"
                               placeholder="Phone number" pattern="[0-9]{10}">
                    </div>

                    <div class="form-group">
                        <label for="buyerEmail">Email</label>
                        <input type="email" id="buyerEmail" name="buyerEmail"
                               placeholder="Email">
                    </div>
                </div>

                <div class="form-group">
                    <label for="buyerAddress">Address</label>
                    <textarea id="buyerAddress" name="buyerAddress" rows="2"
                              placeholder="Enter buyer address"></textarea>
                </div>
            </div>

            <div class="form-section">
                <h3>Seller Information</h3>

                <div class="form-group">
                    <label for="sellerName">Seller Name *</label>
                    <input type="text" id="sellerName" name="sellerName" required
                           placeholder="Enter seller full name">
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="sellerAadhar">Aadhar Number</label>
                        <input type="text" id="sellerAadhar" name="sellerAadhar"
                               placeholder="12-digit Aadhar" pattern="[0-9]{12}" maxlength="12">
                    </div>

                    <div class="form-group">
                        <label for="sellerPan">PAN Number</label>
                        <input type="text" id="sellerPan" name="sellerPan"
                               placeholder="PAN" pattern="[A-Z]{5}[0-9]{4}[A-Z]{1}" maxlength="10">
                    </div>
                </div>
            </div>

            <div class="form-section">
                <div class="form-group">
                    <label for="remarks">Remarks</label>
                    <textarea id="remarks" name="remarks" rows="3"
                              placeholder="Enter any additional remarks"></textarea>
                </div>
            </div>

            <div class="alert alert-info">
                <strong>Note:</strong> Tax will be calculated automatically based on the transaction value:
                <ul>
                    <li>Stamp Duty: 5%</li>
                    <li>Registration Fee: 1%</li>
                    <li>Transfer Duty: 2%</li>
                    <li><strong>Total Tax: 8% of transaction value</strong></li>
                </ul>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">💾 Create Registration</button>
                <a href="${pageContext.request.contextPath}/registrations" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>

    <footer class="footer">
        <p>&copy; 2024 Government of India - Property Registration Department</p>
    </footer>
</body>
</html>