<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>View Registration - Property Registration System</title>
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
            <h1>Registration Details</h1>
            <div>
                <c:if test="${registration.paymentStatus == 'PAID'}">
                    <a href="${pageContext.request.contextPath}/api/registrations/${registration.id}/pdf/registration"
                       class="btn btn-primary" target="_blank">📄 Registration Receipt</a>
                    <a href="${pageContext.request.contextPath}/api/registrations/${registration.id}/pdf/tax"
                       class="btn btn-success" target="_blank">📄 Tax Receipt</a>
                </c:if>
                <a href="${pageContext.request.contextPath}/registrations" class="btn btn-secondary">← Back to List</a>
            </div>
        </div>

        <div class="details-container">
            <div class="details-section">
                <h3>Registration Information</h3>
                <table class="details-table">
                    <tr>
                        <th>Registration Number:</th>
                        <td><strong>${registration.registrationNumber}</strong></td>
                    </tr>
                    <tr>
                        <th>Registration Date:</th>
                        <td><fmt:formatDate value="${registration.registrationDate}" pattern="dd-MM-yyyy"/></td>
                    </tr>
                    <tr>
                        <th>Registration Status:</th>
                        <td><span class="badge badge-status-${registration.registrationStatus}">${registration.registrationStatus}</span></td>
                    </tr>
                    <tr>
                        <th>Payment Status:</th>
                        <td><span class="badge badge-payment-${registration.paymentStatus}">${registration.paymentStatus}</span></td>
                    </tr>
                    <tr>
                        <th>Transaction Value:</th>
                        <td class="highlight">₹<fmt:formatNumber value="${registration.transactionValue}" pattern="#,##0.00"/></td>
                    </tr>
                </table>
            </div>

            <div class="details-section">
                <h3>Property Information</h3>
                <table class="details-table">
                    <tr>
                        <th>Property Number:</th>
                        <td><strong>${registration.property.propertyNumber}</strong></td>
                    </tr>
                    <tr>
                        <th>Property Type:</th>
                        <td>${registration.property.propertyType}</td>
                    </tr>
                    <tr>
                        <th>Address:</th>
                        <td>${registration.property.address}</td>
                    </tr>
                    <tr>
                        <th>City:</th>
                        <td>${registration.property.city}</td>
                    </tr>
                </table>
            </div>

            <div class="details-section">
                <h3>Buyer Information</h3>
                <table class="details-table">
                    <tr>
                        <th>Buyer Name:</th>
                        <td><strong>${registration.buyerName}</strong></td>
                    </tr>
                    <tr>
                        <th>Aadhar Number:</th>
                        <td>${registration.buyerAadhar}</td>
                    </tr>
                    <tr>
                        <th>PAN Number:</th>
                        <td>${registration.buyerPan}</td>
                    </tr>
                    <tr>
                        <th>Phone:</th>
                        <td>${registration.buyerPhone}</td>
                    </tr>
                    <tr>
                        <th>Email:</th>
                        <td>${registration.buyerEmail}</td>
                    </tr>
                    <tr>
                        <th>Address:</th>
                        <td>${registration.buyerAddress}</td>
                    </tr>
                </table>
            </div>

            <div class="details-section">
                <h3>Seller Information</h3>
                <table class="details-table">
                    <tr>
                        <th>Seller Name:</th>
                        <td><strong>${registration.sellerName}</strong></td>
                    </tr>
                    <tr>
                        <th>Aadhar Number:</th>
                        <td>${registration.sellerAadhar}</td>
                    </tr>
                    <tr>
                        <th>PAN Number:</th>
                        <td>${registration.sellerPan}</td>
                    </tr>
                </table>
            </div>

            <div class="details-section">
                <h3>Tax Calculation</h3>
                <table class="details-table">
                    <tr>
                        <th>Stamp Duty (5%):</th>
                        <td>₹<fmt:formatNumber value="${registration.stampDuty}" pattern="#,##0.00"/></td>
                    </tr>
                    <tr>
                        <th>Registration Fee (1%):</th>
                        <td>₹<fmt:formatNumber value="${registration.registrationFee}" pattern="#,##0.00"/></td>
                    </tr>
                    <tr>
                        <th>Transfer Duty (2%):</th>
                        <td>₹<fmt:formatNumber value="${registration.transferDuty}" pattern="#,##0.00"/></td>
                    </tr>
                    <tr class="highlight-row">
                        <th>Total Tax:</th>
                        <td class="highlight"><strong>₹<fmt:formatNumber value="${registration.totalTax}" pattern="#,##0.00"/></strong></td>
                    </tr>
                </table>
            </div>

            <c:if test="${registration.paymentStatus == 'PAID'}">
                <div class="details-section">
                    <h3>Payment Information</h3>
                    <table class="details-table">
                        <tr>
                            <th>Payment Reference:</th>
                            <td>${registration.paymentReference}</td>
                        </tr>
                        <tr>
                            <th>Payment Date:</th>
                            <td><fmt:formatDate value="${registration.paymentDate}" pattern="dd-MM-yyyy HH:mm:ss"/></td>
                        </tr>
                    </table>
                </div>
            </c:if>

            <c:if test="${not empty registration.remarks}">
                <div class="details-section">
                    <h3>Remarks</h3>
                    <p>${registration.remarks}</p>
                </div>
            </c:if>
        </div>
    </div>

    <footer class="footer">
        <p>&copy; 2024 Government of India - Property Registration Department</p>
    </footer>
</body>
</html>