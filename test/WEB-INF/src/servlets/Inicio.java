package servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.ProductosModel;

public class Inicio extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		
		try {
			req.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ServletContext app = getServletContext();
		HttpSession session = req.getSession();
		
		try {
			req.setAttribute("tipos", ProductosModel.getTiposProductos(app,session));
			req.setAttribute("productos", ProductosModel.getProductosInicio(req, app, session));

			req.setAttribute("carritoUsuario", model.CarritoModel.getCarritoUsuario(session, app));
			
			app.getRequestDispatcher("/WEB-INF/jsp/main.jsp").forward(req, resp);
		} catch (ServletException e) {
			System.out.println("Error en Crear.java.doGet (Servlet)");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error en Crear.java.doGet (IO)");
			e.printStackTrace();
		}
	}
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
			doGet(req, resp);
	}
}
