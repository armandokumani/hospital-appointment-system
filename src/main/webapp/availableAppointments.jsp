<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Διαθέσιμα Ραντεβού</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        .fade-in { animation: fadeIn 1s ease-in; }
        @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
    </style>
</head>
<body class="bg-gray-100 min-h-screen">
<nav class="bg-blue-600 text-white p-4">
    <div class="container mx-auto flex justify-between items-center">
        <h1 class="text-2xl font-bold">Σύστημα Υγείας</h1>
        <div>
            <a href="patient?action=view" class="px-4 hover:underline">Προφίλ</a>
            <a href="patient?action=logout" class="px-4 hover:underline">Αποσύνδεση</a>
        </div>
    </div>
</nav>
<div class="container mx-auto mt-8 p-4">
    <div class="bg-white rounded-lg shadow-lg p-6 fade-in">
        <h2 class="text-2xl font-bold text-blue-600 mb-4">Διαθέσιμα Ραντεβού</h2>
        <c:if test="${not empty errorMessage}">
            <div class="mb-4 p-4 bg-red-100 text-red-700 rounded-lg">${errorMessage}</div>
        </c:if>
        <c:if test="${not empty successMessage}">
            <div class="mb-4 p-4 bg-green-100 text-green-700 rounded-lg">${successMessage}</div>
        </c:if>
        <form action="patient?action=available" method="get" class="mb-4">
            <input type="hidden" name="action" value="available">
            <select name="specialty" class="p-2 border rounded-lg">
                <option value="">Όλες οι Ειδικότητες</option>
                <option value="Cardiology">Καρδιολογική</option>
                <option value="Pediatrics">Παιδιατρική</option>
                <option value="Pediatrics">Παθολογικό</option>
                <option value="Pediatrics">Οφθαλμιατρική</option>
            </select>
            <button type="submit" class="ml-2 bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 transition duration-300">Φιλτράρισμα</button>
        </form>
        <c:choose>
            <c:when test="${empty availableAppointments}">
                <p class="text-red-600">Δεν βρέθηκαν διαθέσιμα ραντεβού.</p>
            </c:when>
            <c:otherwise>
                <div class="overflow-x-auto">
                    <table class="w-full text-left">
                        <thead>
                        <tr class="bg-blue-100">
                            <th class="p-3">Ημερομηνία & Ώρα</th>
                            <th class="p-3">Γιατρός</th>
                            <th class="p-3">Ειδικότητα</th>
                            <th class="p-3">Ενέργεια</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="appt" items="${availableAppointments}">
                            <c:if test="${empty specialty || appt.doctor.specialty == specialty}">
                                <tr class="hover:bg-gray-50 transition duration-200">
                                    <td class="p-3">${appt.dateTime}</td>
                                    <td class="p-3">Dr. ${appt.doctor.surname}</td>
                                    <td class="p-3">${appt.doctor.specialty}</td>
                                    <td class="p-3">
                                        <a href="patient?action=book&appointmentId=${appt.id}" class="text-blue-600 hover:underline">Κράτηση</a>
                                    </td>
                                </tr>
                            </c:if>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>