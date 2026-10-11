<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Προφίλ Ασθενή</title>
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
            <a href="patient?action=history" class="px-4 hover:underline">Ιστορικό Ραντεβού</a>
            <a href="patient?action=available" class="px-4 hover:underline">Διαθέσιμα Ραντεβού</a>
            <a href="patient?action=logout" class="px-4 hover:underline">Αποσύνδεση</a>
        </div>
    </div>
</nav>
<div class="container mx-auto mt-8 p-4">
    <div class="bg-white rounded-lg shadow-lg p-6 max-w-lg mx-auto fade-in">
        <h2 class="text-2xl font-bold text-blue-600 mb-4">Προφίλ Ασθενή</h2>
        <c:if test="${not empty errorMessage}">
            <div class="mb-4 p-4 bg-red-100 text-red-700 rounded-lg">${errorMessage}</div>
        </c:if>
        <c:if test="${not empty successMessage}">
            <div class="mb-4 p-4 bg-green-100 text-green-700 rounded-lg">${successMessage}</div>
        </c:if>
        <div class="space-y-2">
            <p><strong>Όνομα:</strong> ${name} ${surname}</p>
            <p><strong>Όνομα Χρήστη:</strong> ${username}</p>
            <p><strong>Ηλικία:</strong> ${age}</p>
            <p><strong>AMKA:</strong> ${amka}</p>
        </div>
        <a href="patient?action=history" class="mt-4 inline-block bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 transition duration-300">Ιστορικό Ραντεβού</a>
        <a href="patient?action=available" class="mt-4 inline-block bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 transition duration-300">Κλείσιμο Ραντεβού</a>
    </div>
</div>
</body>
</html>