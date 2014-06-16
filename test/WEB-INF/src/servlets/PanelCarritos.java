package servlets;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.CarritoModel;
import model.ProductosModel;
import forms.Carrito;
import forms.Usuario;

public class PanelCarritos extends HttpServlet{

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");

		ServletContext app = getServletContext();
		HttpSession session = req.getSession();
		
		Usuario usuario = (Usuario) session.getAttribute("usuario");

		if (usuario == null) {
			app.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
			return;
		} else {
			String id = req.getParameter("id");
			if (id != null) {
				if (!id.equals("")) {
					if (req.getParameter("borrar") != null) CarritoModel.borrarCarrito(Integer.parseInt(id), app);
					else if (req.getParameter("cargar") != null) CarritoModel.cargarCarrito(session, Integer.parseInt(id), app);
				}
			} else {
				if (req.getParameter("guardar") != null) {
					Carrito c = (Carrito)session.getAttribute("carrito");
					model.CarritoModel.guardarCarrito(c, session, app);
				}
			}
		}

		req.setAttribute("listaCarritos", model.CarritoModel.getCarritosUsuario(usuario.getNombre(), app));
		req.setAttribute("carritoUsuario", model.CarritoModel.getCarritoUsuario(session, app));
		req.setAttribute("tipos", ProductosModel.getTiposProductos(app,session));

		app.getRequestDispatcher("/WEB-INF/jsp/carritos.jsp").forward(req, resp);
		return;
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
	
}
