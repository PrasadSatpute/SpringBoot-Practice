<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>View Property - Property Registration System</title>
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
            <h1>Property Details</h1>
            <div>
                <a href="${pageContext.request.contextPath}/properties/edit/${property.id}" class="btn btn-warning">✏️ Edit</a>
                <a href="${pageContext.request.contextPath}/properties" class="btn btn-secondary">← Back to List</a>
            </div>
        </div>

        <div class="details-container">
            <div class="details-section">
                <h3>Property Information</h3>
                <table class="details-table">
                    <tr>
                        <th>Property Number:</th>
                        <td><strong>${property.propertyNumber}</strong></td>
                    </tr>
                    <tr>
                        <th>Property Type:</th>
                        <td><span class="badge badge-${property.propertyType}">${property.propertyType}</span></td>
                    </tr>
                    <tr>
                        <th>Survey Number:</th>
                        <td>${property.surveyNumber}</td>
                    </tr>
                    <tr>
                        <th>Address:</th>
                        <td>${property.address}</td>
                    </tr>
                    <tr>
                        <th>City:</th>
                        <td>${property.city}</td>
                    </tr>
                    <tr>
                        <th>State:</th>
                        <td>${property.state}</td>
                    </tr>
                    <tr>
                        <th>PIN Code:</th>
                        <td>${property.pinCode}</td>
                    </tr>
                    <tr>
                        <th>Area (Sq.Ft):</th>
                        <td><fmt:formatNumber value="${property.areaSqft}" pattern="#,##0.00"/> sq.ft</td>
                    </tr>
                    <tr>
                        <th>Market Value:</th>
                        <td class="highlight">₹<fmt:formatNumber value="${property.marketValue}" pattern="#,##0.00"/></td>
                    </tr>
                </table>
            </div>

            <div class="details-section">
                <h3>Owner Information</h3>
                <table class="details-table">
                    <tr>
                        <th>Owner Name:</th>
                        <td><strong>${property.ownerName}</strong></td>
                    </tr>
                    <tr>
                        <th>Aadhar Number:</th>
                        <td>${property.ownerAadhar}</td>
                    </tr>
                    <tr>
                        <th>PAN Number:</th>
                        <td>${property.ownerPan}</td>
                    </tr>
                    <tr>
                        <th>Phone Number:</th>
                        <td>${property.ownerPhone}</td>
                    </tr>
                    <tr>
                        <th>Email:</th>
                        <td>${property.ownerEmail}</td>
                    </tr>
                </table>
            </div>

            <c:if test="${not empty property.description}">
                <div class="details-section">
                    <h3>Description</h3>
                    <p>${property.description}</p>
                </div>
            </c:if>

            <div class="details-section">
                <h3>System Information</h3>
                <table class="details-table">
                    <tr>
                        <th>Created At:</th>
                        <td><fmt:formatDate value="${property.createdAt}" pattern="dd-MM-yyyy HH:mm:ss"/></td>
                    </tr>
                    <tr>
                        <th>Last Updated:</th>
                        <td><fmt:formatDate value="${property.updatedAt}" pattern="dd-MM-yyyy HH:mm:ss"/></td>
                    </tr>
                </table>
            </div>
        </div>
    </div>

    <footer class="footer">
        <p>&copy; 2024 Government of India - Property Registration Department</p>
    </footer>
</body>
</html>