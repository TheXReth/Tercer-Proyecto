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

public class Entrar extends HttpServlet{

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");

		ServletContext app = getServletContext();
		HttpSession session = req.getSession();
		
		String user = req.getParameter("user");
		String password = req.getParameter("password");
		
		req.setAttribute("tipos", ProductosModel.getTiposProductos(app,session));
		req.setAttribute("productos", ProductosModel.getProductosInicio(req, app, session));
		
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		if (usuario != null) {
			user = usuario.getNombre();
			password = usuario.getPassword();
		}
		
		if (user == null || password == null) {
			app.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
			return;
		} else {
			if (user.equals("") || password.equals("")) {
				//Si ha dejado algún campo vacío
				String error = "<div class='mensaje'> Introduce todos los campos. </div>";
				req.setAttribute("error", error);
				app.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
				return;
			} else {
				//Si todos los campos están llenos
				if(UsersModel.usuarioCorrecto(user, password, app)) {
					//Si el usuario y la contraseña son correctos
					System.out.println("Login correcto. (" + user + ")");
					Usuario usuario1 = new Usuario(user, password, UsersModel.isAdmin(user, app));
					session.setAttribute("usuario", usuario1);

					req.setAttribute("carritoUsuario", model.CarritoModel.getCarritoUsuario(session, app));					
					req.setAttribute("productos", ProductosModel.getProductosInicio(req, app, session));

					app.getRequestDispatcher("/WEB-INF/jsp/main.jsp").forward(req, resp);
					return;						
				} else {
					//Si el usuario y contraseña no son correctos
					String error = "<div class='mensaje'> Usuario y/o contraseña incorrectos. </div>";
					req.setAttribute("error", error);
					app.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
					return;
				}
			}
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
	
}
