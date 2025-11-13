<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registrations List - Property Registration System</title>
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
            <h1>Property Registrations</h1>
            <a href="${pageContext.request.contextPath}/registrations/add" class="btn btn-primary">➕ New Registration</a>
        </div>

        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <div class="table-container">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Reg. No.</th>
                        <th>Property No.</th>
                        <th>Buyer Name</th>
                        <th>Seller Name</th>
                        <th>Transaction Value</th>
                        <th>Total Tax</th>
                        <th>Status</th>
                        <th>Payment</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="registration" items="${registrations}">
                        <tr>
                            <td><strong>${registration.registrationNumber}</strong></td>
                            <td>${registration.property.propertyNumber}</td>
                            <td>${registration.buyerName}</td>
                            <td>${registration.sellerName}</td>
                            <td>₹<fmt:formatNumber value="${registration.transactionValue}" pattern="#,##0.00"/></td>
                            <td>₹<fmt:formatNumber value="${registration.totalTax}" pattern="#,##0.00"/></td>
                            <td>
                                <span class="badge badge-status-${registration.registrationStatus}">
                                    ${registration.registrationStatus}
                                </span>
                            </td>
                            <td>
                                <span class="badge badge-payment-${registration.paymentStatus}">
                                    ${registration.paymentStatus}
                                </span>
                            </td>
                            <td class="action-buttons">
                                <a href="${pageContext.request.contextPath}/registrations/view/${registration.id}"
                                   class="btn btn-sm btn-info">View</a>

                                <c:if test="${registration.registrationStatus == 'PENDING'}">
                                    <a href="${pageContext.request.contextPath}/registrations/approve/${registration.id}"
                                       class="btn btn-sm btn-success">Approve</a>
                                    <a href="${pageContext.request.contextPath}/registrations/reject/${registration.id}?remarks=Rejected"
                                       class="btn btn-sm btn-danger"
                                       onclick="return confirm('Are you sure you want to reject this registration?')">Reject</a>
                                </c:if>

                                <c:if test="${registration.registrationStatus == 'APPROVED' && registration.paymentStatus != 'PAID'}">
                                    <a href="#" onclick="markPayment(${registration.id})"
                                       class="btn btn-sm btn-success">Mark Paid</a>
                                </c:if>

                                <c:if test="${registration.paymentStatus == 'PAID'}">
                                    <a href="${pageContext.request.contextPath}/api/registrations/${registration.id}/pdf/tax"
                                       class="btn btn-sm btn-primary" target="_blank">📄 Receipt</a>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty registrations}">
                        <tr>
                            <td colspan="9" class="text-center">No registrations found</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>

    <footer class="footer">
        <p>&copy; 2024 Government of India - Property Registration Department</p>
    </footer>

    <script>
        function markPayment(id) {
            const reference = prompt('Enter payment reference number:');
            if (reference) {
                window.location.href = '${pageContext.request.contextPath}/registrations/payment/' + id + '?paymentReference=' + encodeURIComponent(reference);
            }
        }
    </script>
</body>
</html>