package servlets;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import forms.Usuario;
import model.ProductosModel;

public class ZonaAdmin extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");

		ServletContext app = getServletContext();
		HttpSession session = req.getSession();
		
		req.setAttribute("tipos", ProductosModel.getTiposProductos(app,session));
		req.setAttribute("carritoUsuario", model.CarritoModel.getCarritoUsuario(session, app));
		req.setAttribute("productos", ProductosModel.getProductosInicio(req, app, session));

		Usuario usuario = (Usuario) session.getAttribute("usuario");
		if(usuario != null) {
			if (usuario.isAdmin()) {
				app.getRequestDispatcher("/WEB-INF/jsp/zonaAdmin.jsp").forward(req, resp);
				return;
			}
		} else {
			app.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
			return;
		}
		app.getRequestDispatcher("/WEB-INF/jsp/main.jsp").forward(req, resp);
	}
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
			doGet(req, resp);
	}
	
}
