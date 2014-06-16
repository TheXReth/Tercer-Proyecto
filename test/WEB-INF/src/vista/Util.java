package vista;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import model.ProductosModel;
import forms.Usuario;

public class Util {
	
	public static String getAttribute(String p, HttpServletRequest req) {
		String s = (String) req.getAttribute(p);
		if (s != null)
			return s;
		else
			return "";
	}
	
	public static String getLoginBox(HttpServletRequest req) {
		
		HttpSession session = req.getSession();
		
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		String loginBox = "";
		if (usuario != null) {
			if (!usuario.isAdmin()) loginBox = "<div class='loginText'><p>Hola, " + usuario.getNombre() + ".</p></div>";
			else loginBox = "<div class='loginText'><a href='zonaAdmin'>Zona admin</a></div>";
			loginBox += "<div class='loginText'><a href='main?cerrarSesion=1'>Cerrar sesión</a></div>";
		} else {
			loginBox = "<div class='loginText'><a href='entrar'>"
					+ "Iniciar sesión</a></div><div class='loginText'>"
					+ "<a href='crear'>Crear cuenta</a></div>";
		}
		return loginBox;
	}

	public static String getEstadisticas(ServletContext app) {
		final int CODIGO = 0;
		final int NOMBRE = 1;
		final int CLICKS = 2;
		final int ALTURA = 498;

		
		String[][] producto = ProductosModel.getEstadisticas(app);
		
		StringBuffer sb = new StringBuffer();
		
		double max = Integer.parseInt(producto[0][CLICKS]);
		
		sb.append("<div id='grafica'>");
		
		for (int i = 0; i < 20; ++i) {
			double valor = Integer.parseInt(producto[i][CLICKS]);
			double porcentaje = valor/max;
			double tamaño = ALTURA*porcentaje;
			
			double espacioTop = ALTURA - tamaño;
			
			sb.append("<div class='barra' style='height: " + tamaño + "px; margin-top: " + espacioTop  + "px'>"
					+ "<div class='valor' style='line-height:" + tamaño + "px'>" + (int)valor + "</div>" 
					+ "<div class='nombre'>" + producto[i][NOMBRE] + "</div>" 
					+ "</div>");
		}
		
		sb.append("</div>");
		sb.append("<div id='listaGrafica'>");

		for (int i = 0; i < 20; ++i) {
			sb.append((i + 1) + ". <a href='producto?id=" + producto[i][CODIGO] + "'>" + producto[i][NOMBRE] + "</a>"
					+ " - Clicks: " + producto[i][CLICKS] + "<br/>");
		}
		
		sb.append("</div>");
		
		return sb.toString();
	}
	
}
