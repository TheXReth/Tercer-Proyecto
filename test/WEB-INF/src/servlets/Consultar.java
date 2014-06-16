package servlets;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.ProductosModel;
import model.UsersModel;
import forms.Usuario;

public class Consultar extends HttpServlet {

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
				int tipo = 0;
				if (req.getParameter("usuarios") != null) tipo = 1;
				else if (req.getParameter("productos") != null) tipo = 2;
				else if (req.getAttribute("VolverProductos") != null) tipo = 2;
				else if (req.getParameter("estadisticas") != null) tipo = 3;
				
				String consulta = "";
				switch (tipo) {
					case 1:
						String admin = req.getParameter("admin");
						String nombre = req.getParameter("nombre");
						if (admin != null && nombre != null) UsersModel.setPrivilegios(admin, nombre, app);
						else {
							String eliminar = req.getParameter("eliminar");
							if (eliminar != null) if (!eliminar.equals("")) UsersModel.eliminarUsuario(eliminar, app);
						}
						consulta = UsersModel.getUsuarios(app);
						break;
					case 2:
						String eliminar = req.getParameter("eliminar");
						if (eliminar != null) if (!eliminar.equals("")) {
							ProductosModel.eliminarProducto(eliminar, app);
							String tipoProductos = req.getParameter("tipo"); 
							String pagina = req.getParameter("pagina");
							if (tipoProductos != null && pagina != null) {
								req.setAttribute("productos", ProductosModel.getProductosInicio(req, app, session));
								app.getRequestDispatcher("/WEB-INF/jsp/main.jsp").forward(req, resp);
								return;
							}
						}
						consulta = ProductosModel.getConsultaProductos(app);
						break;
					case 3:
						consulta = vista.Util.getEstadisticas(app);
						break;
					default:
						app.getRequestDispatcher("/zonaAdmin").forward(req, resp);
						return;
				}
				req.setAttribute("consulta", consulta);
				
				app.getRequestDispatcher("/WEB-INF/jsp/consultar.jsp").forward(req, resp);
				return;
			}
		}
		app.getRequestDispatcher("/WEB-INF/jsp/main.jsp").forward(req, resp);
	}
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
			doGet(req, resp);
	}
	
}
