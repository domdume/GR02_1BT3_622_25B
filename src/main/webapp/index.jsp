<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Panel de Control del Hogar</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
        }
        .container {
            width: 80%;
            margin: auto;
            overflow: hidden;
        }
        header {
            background: #333;
            color: #fff;
            padding-top: 30px;
            min-height: 70px;
            border-bottom: #77aaff 3px solid;
        }
        header a {
            color: #fff;
            text-decoration: none;
            text-transform: uppercase;
            font-size: 16px;
        }
        header ul {
            padding: 0;
            margin: 0;
            list-style: none;
            overflow: hidden;
        }
        header li {
            float: left;
            display: inline;
            padding: 0 20px 0 20px;
        }
        header #branding {
            float: left;
        }
        header #branding h1 {
            margin: 0;
        }
        .main {
            padding: 30px;
            background: #fff;
            margin-top: 20px;
        }
        .card-container {
            display: flex;
            justify-content: space-around;
            margin-top: 20px;
        }
        .card {
            background: #fff;
            border: 1px solid #ddd;
            border-radius: 5px;
            padding: 20px;
            width: 45%;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            text-align: center;
        }
        .card h2 {
            color: #333;
        }
        .card a {
            display: inline-block;
            margin-top: 15px;
            padding: 10px 20px;
            background: #333;
            color: #fff;
            text-decoration: none;
            border-radius: 5px;
        }
        .card a:hover {
            background: #77aaff;
        }
    </style>
</head>
<body>
    <header>
        <div class="container">
            <div id="branding">
                <h1><span class="highlight">Panel</span> de Control del Hogar</h1>
            </div>
        </div>
    </header>

    <div class="container main">
        <h2>Bienvenido al sistema de gestión del hogar</h2>
        <p>Seleccione una de las siguientes opciones para comenzar:</p>
        <div class="card-container">
            <div class="card">
                <h2>Miembros del Hogar</h2>
                <p>Administrar los miembros de su hogar.</p>
                <a href="miembros">Gestionar Miembros</a>
            </div>
            <div class="card">
                <h2>Quehaceres</h2>
                <p>Administrar los quehaceres del hogar.</p>
                <a href="quehaceres">Gestionar Quehaceres</a>
            </div>
        </div>
    </div>
</body>
</html>