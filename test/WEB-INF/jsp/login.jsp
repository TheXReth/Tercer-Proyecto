<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ES">
	<head>
		<title> Iniciar sesión </title>
		<meta charset="utf-8">
		<link rel="stylesheet" type="text/css" href="src/style.css"/>
	</head>
	<body>
		<%=vista.Util.getAttribute("error", request) %>
			<a id="volver" href="main"> Volver </a>
			<div class="cajaCentrada" style="height: 244px;">
				<div class="cajaSuperior">
					<h1> Iniciar sesión </h1>
				</div>
				<form action="entrar" method="post"> 
					<label>Usuario</label>
					<input type="text" name="user" maxlength='15'>
					<br/><br/>
					<label>Contraseña</label>
					<input type="password" name="password" maxlength='20'>
					<br/><br/>
					<input type="submit" value="Entrar">
				</form>
			</div>

	</body>
</html>