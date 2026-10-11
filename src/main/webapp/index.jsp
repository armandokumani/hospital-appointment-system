<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Σύστημα Υγείας - Είσοδος</title>
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
            <a href="login?action=register" class="px-4 hover:underline">Εγγραφή</a>
        </div>
    </div>
</nav>
<div class="container mx-auto mt-8 p-4">
    <div class="bg-white rounded-lg shadow-lg p-6 max-w-md mx-auto fade-in">
        <h2 class="text-2xl font-bold text-blue-600 mb-4">Είσοδος</h2>
        <c:if test="${not empty errorMessage}">
            <div class="mb-4 p-4 bg-red-100 text-red-700 rounded-lg">${errorMessage}</div>
        </c:if>
        <form action="login" method="post" class="space-y-4">
            <input type="hidden" name="action" value="login">
            <div>
                <label for="username" class="block text-sm font-medium text-gray-700">Όνομα Χρήστη</label>
                <input type="text" id="username" name="username" required
                       class="mt-1 w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500">
            </div>
            <div>
                <label for="password" class="block text-sm font-medium text-gray-700">Κωδικός</label>
                <input type="password" id="password" name="password" required
                       class="mt-1 w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500">
            </div>
            <button type="submit" class="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700">Είσοδος</button>
        </form>
        <p class="mt-4 text-center">
            Δεν έχετε λογαριασμό; <a href="login?action=register" class="text-blue-600 hover:underline">Εγγραφή</a>
        </p>
    </div>
</div>
</body>
</html>