<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ES">
	<head>
		<title> Crear usuario </title>
		<meta charset="utf-8">
		<link rel="stylesheet" type="text/css" href="src/style.css"/>
	</head>
	<body>
		<%=vista.Util.getAttribute("error", request) %>
			<a id="volver" href="main"> Volver </a>
			<div class="cajaCentrada" style="height: 300px;">
				<div class="cajaSuperior">
					<h1> Crear usuario </h1>
				</div>
				<form action="crear" method="post"> 
					<label>Usuario</label>
					<input type="text" name="newUser" maxlength='15'>
					<br/><br/>
					<label>Contraseña</label>
					<input type="password" name="newPassword1" maxlength='20'>
					<br/><br/>
					<label>Repite la contraseña</label>
					<input type="password" name="newPassword2" maxlength='20'>
					<br/><br/>
					<input type="submit" value="Crear">
				</form>
			</div>

	</body>
</html>