package servlets;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import forms.Usuario;
import model.CarritoModel;
import model.ProductosModel;

public class Carritos extends HttpServlet{

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		
		ServletContext app = getServletContext();
		HttpSession session = req.getSession();
		
		String idProductoAux = req.getParameter("id");
		String borrarAux = req.getParameter("borrar");
		String idReturn = req.getParameter("idReturn");
		if (idReturn == null) idReturn = idProductoAux;
		
		String producto = "";
		
		boolean borrar = false;
		if (borrarAux != null) borrar = Boolean.parseBoolean(borrarAux);

		if (req.getParameter("verCarritos") != null) {
			Usuario usuario = (Usuario) session.getAttribute("usuario");

			if (usuario == null) {
				app.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
				return;
			} else {
				req.setAttribute("listaCarritos", model.CarritoModel.getCarritosUsuario(usuario.getNombre(), app));
				req.setAttribute("carritoUsuario", model.CarritoModel.getCarritoUsuario(session, app));
				req.setAttribute("tipos", ProductosModel.getTiposProductos(app,session));

				app.getRequestDispatcher("/WEB-INF/jsp/carritos.jsp").forward(req, resp);
				return;
			}
		}
		
		String eliminar = req.getParameter("eliminar");
		if (eliminar != null) CarritoModel.borrarCarritoSession(session);
		else {
			if (idProductoAux == null) {
				req.setAttribute("carritoUsuario", model.CarritoModel.getCarritoUsuario(session, app));
				req.setAttribute("tipos", ProductosModel.getTiposProductos(app,session));

				app.getRequestDispatcher("/WEB-INF/jsp/main.jsp").forward(req, resp);
				return;
			}
			else {
				int idProducto = 0;
				if (!idProductoAux.equals("")) idProducto = Integer.parseInt(idProductoAux);
				producto = ("?id=" + idReturn);

				if(borrar) CarritoModel.borrarDeCarrito(idProducto, session);
				else {
					if (idProducto < 0) {
						System.out.println("Un listillo poniendo una idCarrito < 1");
						req.setAttribute("carritoUsuario", model.CarritoModel.getCarritoUsuario(session, app));
						req.setAttribute("tipos", ProductosModel.getTiposProductos(app,session));

						app.getRequestDispatcher("/WEB-INF/jsp/main.jsp").forward(req, resp);
						return;
					} else {
						if (req.getParameter("decrementar") != null) {
							CarritoModel.restarDeCarrito(idProducto, session);
						} else CarritoModel.añadirACarrito(idProducto, session);
					}
				}
			}
		}
		/***********************/
		session.setAttribute("carritoUsuario", model.CarritoModel.getCarritoUsuario(session, app));
		/***********************/

		String direccion = "/producto" + producto;
		app.getRequestDispatcher(direccion).forward(req, resp);

	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
	
	

}

