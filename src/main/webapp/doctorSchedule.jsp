<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Πρόγραμμα Ραντεβού</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        .fade-in { animation: fadeIn 1s ease-in; }
        @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
        .status-available { background-color: #d1fae5; color: #065f46; }
        .status-booked { background-color: #fefcbf; color: #b45309; }
        .status-completed { background-color: #e0e7ff; color: #4338ca; }
    </style>
</head>
<body class="bg-gray-100 min-h-screen">
<nav class="bg-blue-600 text-white p-4">
    <div class="container mx-auto flex justify-between items-center">
        <h1 class="text-2xl font-bold">Σύστημα Υγείας</h1>
        <div>
            <a href="doctor?action=view" class="px-4 hover:underline">Προφίλ</a>
            <a href="doctor?action=logout" class="px-4 hover:underline">Αποσύνδεση</a>
        </div>
    </div>
</nav>
<div class="container mx-auto mt-8 p-4">
    <div class="bg-white rounded-lg shadow-lg p-6 fade-in">
        <h2 class="text-2xl font-bold text-blue-600 mb-4">Πρόγραμμα Ραντεβού</h2>
        <c:if test="${not empty errorMessage}">
            <div id="errorModal" class="mt-4 p-4 bg-red-100 text-red-700 rounded-lg flex justify-between items-center">
                <span>${errorMessage}</span>
                <button onclick="document.getElementById('errorModal').style.display='none'" class="text-red-700 font-bold">×</button>
            </div>
        </c:if>
        <c:if test="${not empty successMessage}">
            <div id="successModal" class="mt-4 p-4 bg-green-100 text-green-700 rounded-lg flex justify-between items-center">
                <span>${successMessage}</span>
                <button onclick="document.getElementById('successModal').style.display='none'" class="text-green-700 font-bold">×</button>
            </div>
        </c:if>
        <c:choose>
            <c:when test="${empty appointments}">
                <p class="text-red-600">Δεν υπάρχουν ραντεβού.</p>
            </c:when>
            <c:otherwise>
                <div class="overflow-x-auto">
                    <table class="w-full text-left">
                        <thead>
                        <tr class="bg-blue-100">
                            <th class="p-3">Ημερομηνία & Ώρα</th>
                            <th class="p-3">Ασθενής</th>
                            <th class="p-3">Κατάσταση</th>
                            <th class="p-3">Ενέργεια</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="appt" items="${appointments}">
                            <tr class="hover:bg-gray-50 transition duration-200">
                                <td class="p-3">${appt.dateTime}</td>
                                <td class="p-3">${appt.patient.surname} ${appt.patient.name}</td>
                                <td class="p-3">
                                            <span class="px-2 py-1 rounded-full text-sm status-${appt.status.toLowerCase()}">
                                                    ${appt.status}
                                            </span>
                                </td>
                                <c:if test="${appt.status == 'BOOKED'}">
                                    <td class="p-3">
                                        <a href="doctor?action=cancel&appointmentId=${appt.id}" class="text-red-600 hover:underline">Ακύρωση</a>
                                    </td>
                                </c:if>
                                <c:if test="${appt.status != 'BOOKED'}">
                                    <td class="p-3">-</td>
                                </c:if>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
        <a href="doctor?action=addAvailability" class="mt-4 inline-block bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 transition duration-300">Προσθήκη Διαθεσιμότητας</a>
    </div>
</div>
<script>
    setTimeout(() => {
        const modal = document.getElementById('errorModal') || document.getElementById('successModal');
        if (modal) modal.style.display = 'none';
    }, 5000);
</script>
</body>
</html>