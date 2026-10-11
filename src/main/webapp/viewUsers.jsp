<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Προβολή Όλων των Χρηστών</title>
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
            <a href="admin?action=view" class="px-4 hover:underline">Προφίλ</a>
            <a href="admin?action=addUser" class="px-4 hover:underline">Προσθήκη Χρήστη</a>
            <a href="admin?action=addDoctor" class="px-4 hover:underline">Προσθήκη Ιατρού</a>
            <a href="admin?action=deleteDoctor" class="px-4 hover:underline">Διαγραφή Ιατρού</a>
            <a href="admin?action=deletePatients" class="px-4 hover:underline">Διαγραφή Ασθενή</a>
            <a href="admin?action=viewUsers" class="px-4 hover:underline">Προβολή Χρηστών</a>
            <a href="admin?action=logout" class="px-4 hover:underline">Αποσύνδεση</a>
        </div>
    </div>
</nav>
<div class="container mx-auto mt-8 p-4">
    <div class="bg-white rounded-lg shadow-lg p-6 fade-in">
        <h2 class="text-2xl font-bold text-blue-600 mb-4">Όλοι οι Χρήστες του Συστήματος</h2>
        <c:if test="${not empty errorMessage}">
            <div class="mb-4 p-4 bg-red-100 text-red-700 rounded-lg">${errorMessage}</div>
        </c:if>
        <table class="w-full table-auto">
            <thead>
            <tr class="bg-blue-200">
                <th class="px-4 py-2">Όνομα</th>
                <th class="px-4 py-2">Επώνυμο</th>
                <th class="px-4 py-2">Όνομα Χρήστη</th>
                <th class="px-4 py-2">Ηλικία</th>
                <th class="px-4 py-2">Τύπος</th>
                <th class="px-4 py-2">Λεπτομέρειες</th>
                <th class="px-4 py-2">Ενέργειες</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="user" items="${users}">
                <tr class="border-b">
                    <td class="px-4 py-2">${user.name}</td>
                    <td class="px-4 py-2">${user.surname}</td>
                    <td class="px-4 py-2">${user.username}</td>
                    <td class="px-4 py-2">${user.age}</td>
                    <td class="px-4 py-2">
                        <c:choose>
                            <c:when test="${user.type == 'ADMIN'}">Διαχειριστής</c:when>
                            <c:when test="${user.type == 'DOCTOR'}">Ιατρός</c:when>
                            <c:when test="${user.type == 'PATIENT'}">Ασθενής</c:when>
                        </c:choose>
                    </td>
                    <td class="px-4 py-2">
                        <c:choose>
                            <c:when test="${user.type == 'DOCTOR'}">${user.details}</c:when>
                            <c:when test="${user.type == 'PATIENT'}">${user.details}</c:when>
                            <c:otherwise>-</c:otherwise>
                        </c:choose>
                    </td>
                    <td class="px-4 py-2">
                        <c:if test="${user.type == 'DOCTOR'}">
                            <a href="admin?action=deleteDoctor&userId=${user.id}" class="text-red-500 hover:underline" onclick="return confirm('Είστε σίγουροι ότι θέλετε να διαγράψετε αυτόν τον ιατρό;');">Διαγραφή</a>
                        </c:if>
                        <c:if test="${user.type == 'PATIENT'}">
                            <a href="admin?action=deletePatients&userId=${user.id}" class="text-red-500 hover:underline" onclick="return confirm('Είστε σίγουροι ότι θέλετε να διαγράψετε αυτόν τον ασθενή;');">Διαγραφή</a>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <a href="admin?action=view" class="mt-4 inline-block text-blue-600 hover:underline">Επιστροφή στο Προφίλ</a>
    </div>
</div>
</body>
</html>