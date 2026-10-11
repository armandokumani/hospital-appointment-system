<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Προσθήκη Διαθεσιμότητας</title>
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
            <a href="doctor?action=view" class="px-4 hover:underline">Προφίλ</a>
            <a href="doctor?action=schedule" class="px-4 hover:underline">Πρόγραμμα</a>
            <a href="doctor?action=logout" class="px-4 hover:underline">Αποσύνδεση</a>
        </div>
    </div>
</nav>
<div class="container mx-auto mt-8 p-4">
    <div class="bg-white rounded-lg shadow-lg p-6 max-w-lg mx-auto fade-in">
        <h2 class="text-2xl font-bold text-blue-600 mb-4">Προσθήκη Διαθεσιμότητας</h2>
        <c:if test="${not empty errorMessage}">
            <div class="mb-4 p-4 bg-red-100 text-red-700 rounded-lg">${errorMessage}</div>
        </c:if>
        <c:if test="${not empty successMessage}">
            <div class="mb-4 p-4 bg-green-100 text-green-700 rounded-lg">${successMessage}</div>
        </c:if>
        <form action="doctor?action=addAvailability" method="post" class="space-y-4">
            <div>
                <label for="dateTime" class="block text-sm font-medium text-gray-700">Ημερομηνία και Ώρα</label>
                <input type="datetime-local" id="dateTime" name="dateTime" required
                       class="mt-1 w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500">
            </div>
            <button type="submit" class="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700">Πρόσθεσε</button>
        </form>
        <a href="doctor?action=schedule" class="mt-4 inline-block text-blue-600 hover:underline">Επιστροφή στο Πρόγραμμα</a>
    </div>
</div>
</body>
</html>