<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Προσθήκη Χρήστη</title>
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
            <a href="admin?action=logout" class="px-4 hover:underline">Αποσύνδεση</a>
        </div>
    </div>
</nav>
<div class="container mx-auto mt-8 p-4">
    <div class="bg-white rounded-lg shadow-lg p-6 fade-in">
        <h2 class="text-2xl font-bold text-blue-600 mb-4">Προσθήκη Νέου Χρήστη</h2>
        <c:if test="${not empty errorMessage}">
            <div class="mb-4 p-4 bg-red-100 text-red-700 rounded-lg">${errorMessage}</div>
        </c:if>
        <c:if test="${not empty successMessage}">
            <div class="mb-4 p-4 bg-green-100 text-green-700 rounded-lg">${successMessage}</div>
        </c:if>
        <form action="admin?action=addUser" method="post" class="space-y-4">
            <div>
                <label for="username" class="block text-sm font-medium text-gray-700">Όνομα Χρήστη</label>
                <input type="text" id="username" name="username" required class="mt-1 w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
            </div>
            <div>
                <label for="password" class="block text-sm font-medium text-gray-700">Κωδικός</label>
                <input type="password" id="password" name="password" required class="mt-1 w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
            </div>
            <div>
                <label for="name" class="block text-sm font-medium text-gray-700">Όνομα</label>
                <input type="text" id="name" name="name" required class="mt-1 w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
            </div>
            <div>
                <label for="surname" class="block text-sm font-medium text-gray-700">Επώνυμο</label>
                <input type="text" id="surname" name="surname" required class="mt-1 w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
            </div>
            <div>
                <label for="age" class="block text-sm font-medium text-gray-700">Ηλικία</label>
                <input type="number" id="age" name="age" required class="mt-1 w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
            </div>
            <div>
                <label for="type" class="block text-sm font-medium text-gray-700">Τύπος Χρήστη</label>
                <select id="type" name="type" required class="mt-1 w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                    <option value="PATIENT">Ασθενής</option>
                    <option value="ADMIN">Διαχειριστής</option>
                </select>
            </div>
            <div>
                <label for="amka" class="block text-sm font-medium text-gray-700">AMKA (μόνο για Ασθενείς)</label>
                <input type="number" id="amka" name="amka" class="mt-1 w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
            </div>
            <button type="submit" class="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 transition duration-300">Προσθήκη Χρήστη</button>
        </form>
        <a href="admin?action=view" class="mt-4 inline-block bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 transition duration-300">Επιστροφή</a>
    </div>
</div>
</body>
</html>