<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Property - Property Registration System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="nav-container">
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-brand">🏛️ Property Registration</a>
            <div class="nav-menu">
                <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/properties" class="active">Properties</a>
                <a href="${pageContext.request.contextPath}/registrations">Registrations</a>
                <span class="nav-user">👤 ${username}</span>
                <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
            </div>
        </div>
    </nav>

    <div class="container main-content">
        <div class="page-header">
            <h1>Edit Property - ${property.propertyNumber}</h1>
            <a href="${pageContext.request.contextPath}/properties" class="btn btn-secondary">← Back to List</a>
        </div>

        <form action="${pageContext.request.contextPath}/properties/edit/${property.id}" method="post" class="form-container">
            <div class="form-section">
                <h3>Property Information</h3>

                <div class="form-row">
                    <div class="form-group">
                        <label for="propertyType">Property Type *</label>
                        <select id="propertyType" name="propertyType" required>
                            <option value="RESIDENTIAL" ${property.propertyType == 'RESIDENTIAL' ? 'selected' : ''}>Residential</option>
                            <option value="COMMERCIAL" ${property.propertyType == 'COMMERCIAL' ? 'selected' : ''}>Commercial</option>
                            <option value="AGRICULTURAL" ${property.propertyType == 'AGRICULTURAL' ? 'selected' : ''}>Agricultural</option>
                            <option value="INDUSTRIAL" ${property.propertyType == 'INDUSTRIAL' ? 'selected' : ''}>Industrial</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="surveyNumber">Survey Number</label>
                        <input type="text" id="surveyNumber" name="surveyNumber" value="${property.surveyNumber}">
                    </div>
                </div>

                <div class="form-group">
                    <label for="address">Address *</label>
                    <textarea id="address" name="address" rows="3" required>${property.address}</textarea>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="city">City *</label>
                        <input type="text" id="city" name="city" value="${property.city}" required>
                    </div>

                    <div class="form-group">
                        <label for="state">State *</label>
                        <input type="text" id="state" name="state" value="${property.state}" required>
                    </div>

                    <div class="form-group">
                        <label for="pinCode">PIN Code</label>
                        <input type="text" id="pinCode" name="pinCode" value="${property.pinCode}">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="areaSqft">Area (Sq.Ft) *</label>
                        <input type="number" id="areaSqft" name="areaSqft" step="0.01" value="${property.areaSqft}" required>
                    </div>

                    <div class="form-group">
                        <label for="marketValue">Market Value (₹) *</label>
                        <input type="number" id="marketValue" name="marketValue" step="0.01" value="${property.marketValue}" required>
                    </div>
                </div>
            </div>

            <div class="form-section">
                <h3>Owner Information</h3>

                <div class="form-group">
                    <label for="ownerName">Owner Name *</label>
                    <input type="text" id="ownerName" name="ownerName" value="${property.ownerName}" required>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="ownerAadhar">Aadhar Number</label>
                        <input type="text" id="ownerAadhar" name="ownerAadhar" value="${property.ownerAadhar}">
                    </div>

                    <div class="form-group">
                        <label for="ownerPan">PAN Number</label>
                        <input type="text" id="ownerPan" name="ownerPan" value="${property.ownerPan}">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="ownerPhone">Phone Number</label>
                        <input type="tel" id="ownerPhone" name="ownerPhone" value="${property.ownerPhone}">
                    </div>

                    <div class="form-group">
                        <label for="ownerEmail">Email</label>
                        <input type="email" id="ownerEmail" name="ownerEmail" value="${property.ownerEmail}">
                    </div>
                </div>
            </div>

            <div class="form-section">
                <div class="form-group">
                    <label for="description">Description</label>
                    <textarea id="description" name="description" rows="3">${property.description}</textarea>
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">💾 Update Property</button>
                <a href="${pageContext.request.contextPath}/properties" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>

    <footer class="footer">
        <p>&copy; 2024 Government of India - Property Registration Department</p>
    </footer>
</body>
</html>