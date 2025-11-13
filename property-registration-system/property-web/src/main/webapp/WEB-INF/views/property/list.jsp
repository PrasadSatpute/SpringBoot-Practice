<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Properties List - Property Registration System</title>
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
            <h1>Property Management</h1>
            <a href="${pageContext.request.contextPath}/properties/add" class="btn btn-primary">➕ Add New Property</a>
        </div>

        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <div class="search-bar">
            <form action="${pageContext.request.contextPath}/properties/search" method="get">
                <input type="text" name="keyword" placeholder="Search by property number, owner name, or address..."
                       value="${keyword}" class="search-input">
                <button type="submit" class="btn btn-secondary">🔍 Search</button>
            </form>
        </div>

        <div class="table-container">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Property No.</th>
                        <th>Type</th>
                        <th>Owner Name</th>
                        <th>Address</th>
                        <th>City</th>
                        <th>Area (Sq.Ft)</th>
                        <th>Market Value</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="property" items="${properties}">
                        <tr>
                            <td><strong>${property.propertyNumber}</strong></td>
                            <td><span class="badge badge-${property.propertyType}">${property.propertyType}</span></td>
                            <td>${property.ownerName}</td>
                            <td>${property.address}</td>
                            <td>${property.city}</td>
                            <td><fmt:formatNumber value="${property.areaSqft}" pattern="#,##0.00"/></td>
                            <td>₹<fmt:formatNumber value="${property.marketValue}" pattern="#,##0.00"/></td>
                            <td class="action-buttons">
                                <a href="${pageContext.request.contextPath}/properties/view/${property.id}"
                                   class="btn btn-sm btn-info">View</a>
                                <a href="${pageContext.request.contextPath}/properties/edit/${property.id}"
                                   class="btn btn-sm btn-warning">Edit</a>
                                <a href="${pageContext.request.contextPath}/properties/delete/${property.id}"
                                   class="btn btn-sm btn-danger"
                                   onclick="return confirm('Are you sure you want to delete this property?')">Delete</a>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty properties}">
                        <tr>
                            <td colspan="8" class="text-center">No properties found</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>

    <footer class="footer">
        <p>&copy; 2024 Government of India - Property Registration Department</p>
    </footer>
</body>
</html>