package servlets;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.ProductosModel;

public class Productos extends HttpServlet{

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");

		ServletContext app = getServletContext();
		HttpSession session = req.getSession();
		
		req.setAttribute("tipos", ProductosModel.getTiposProductos(app,session));
		String productos = ProductosModel.getProductoID(req, app,session);

		req.setAttribute("carritoUsuario", model.CarritoModel.getCarritoUsuario(session, app));

		if (productos.equals("")) app.getRequestDispatcher("/main").forward(req, resp);	
		else {
			req.setAttribute("productos", productos);
			app.getRequestDispatcher("/WEB-INF/jsp/producto.jsp").forward(req, resp);
		}

	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
	
}
