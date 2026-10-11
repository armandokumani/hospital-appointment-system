<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Προφίλ Διαχειριστή</title>
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
            <a href="admin?action=addUser" class="px-4 hover:underline">Προσθήκη Ασθενή</a>
            <a href="admin?action=addDoctor" class="px-4 hover:underline">Προσθήκη Ιατρού</a>
            <a href="admin?action=deleteDoctor" class="px-4 hover:underline">Διαγραφή Ιατρού</a>
            <a href="admin?action=deletePatients" class="px-4 hover:underline">Διαγραφή Ασθενή</a>
            <a href="admin?action=viewUsers" class="px-4 hover:underline">Προβολή Χρηστών</a>
            <a href="admin?action=logout" class="px-4 hover:underline">Αποσύνδεση</a>
        </div>
    </div>
</nav>
<div class="container mx-auto mt-8 p-4">
    <div class="bg-white rounded-lg shadow-lg p-6 max-w-lg mx-auto fade-in">
        <h2 class="text-2xl font-bold text-blue-600 mb-4">Προφίλ Διαχειριστή</h2>
        <c:if test="${not empty errorMessage}">
            <div class="mb-4 p-4 bg-red-100 text-red-700 rounded-lg">${errorMessage}</div>
        </c:if>
        <div class="space-y-4">
            <p><strong>Όνομα Χρήστη:</strong> ${sessionScope.username}</p>
            <p><strong>Τύπος:</strong> Διαχειριστής</p>
        </div>
        <div class="mt-6 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            <a href="admin?action=addUser" class="inline-block bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 text-center">Προσθήκη Ασθενή</a>
            <a href="admin?action=addDoctor" class="inline-block bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 text-center">Προσθήκη Ιατρού</a>
            <a href="admin?action=deleteDoctor" class="inline-block bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 text-center">Διαγραφή Ιατρού</a>
            <a href="admin?action=deletePatients" class="inline-block bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 text-center">Διαγραφή Ασθενή</a>
            <a href="admin?action=viewUsers" class="inline-block bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 text-center">Προβολή Χρηστών</a>
            <a href="admin?action=logout" class="inline-block bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 text-center">Αποσύνδεση</a>
        </div>
    </div>
</div>
</body>
</html>