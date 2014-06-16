<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="src/webstyle.css">
		<meta charset="UTF-8">
		
		<script src='http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js'></script>
		<script type='text/javascript' src='src/menu_jquery.js'></script>	
		
	</head>
	<body>	
	<div id='contenedorHorizontalTop'>
		<div id="contenedorTop">
			<div id="top">
				<div id="topLogo">
					<a href="main">INFROMATIC</a>
				</div>
			</div>
		</div>

		<div id="contenedorNav">
			<div id="carritoUsuario">
				<%=vista.Util.getAttribute("carritoUsuario", request)%>
			</div>
			<div id="login">
				<%=vista.Util.getLoginBox(request)%>
			</div>
			<div id="search">
				<img src='src/images/buscar.png'> </img><input type="text" name="buscar" id="buscador">
			</div>
			<div id="menu">
				<div id='cssmenu'>
					<ul>
					   <li class='has-sub'><a href='#'><span>Productos</span></a>
					      <ul>
					      	<%=vista.Util.getAttribute("tipos", request)%>
					      </ul>
					   </li>
					</ul>
				</div>
			</div>
		</div>
	</div>
	<div id='contenedorHorizontalProd'>
		<div id="contenedorProd">
			<div id="contenido">
				<%=vista.Util.getAttribute("productos", request)%>
			</div>
		</div>
	</div>
	<div id='contenedorHorizontalBottom'>
		<div id="contenedorBottom">
			<div id="bottom">
				<div id='bottomLeft'> <img src='src/images/villadeaguimes.png'></img>
				<p> Desarrollo de aplicaciones multiplataforma.</br>Proyecto tercer trimestre.</p></div>
				<div id='bottomRight'> <p style="margin-top: 50px; width:350px;">© 2014 David, Joseph y Eduardo.</p> </div>
			</div>
		</div>
	</div>
	</body>
</html>