package servlets;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;

import javax.servlet.http.HttpSession;

import model.ProductosModel;
import model.UsersModel;
import forms.Usuario;

public class Crear extends HttpServlet {
	
	/* =====================================
	 * - Cambiar errores a función "error".
	 * 
	 * ===================================== */

	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");

		ServletContext app = getServletContext();
		HttpSession session = req.getSession();
		
		req.setAttribute("tipos", ProductosModel.getTiposProductos(app,session));
		req.setAttribute("productos", ProductosModel.getProductosInicio(req, app, session));
		
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		if (usuario != null) {
			
			req.setAttribute("carritoUsuario", model.CarritoModel.getCarritoUsuario(session, app));

			app.getRequestDispatcher("/WEB-INF/jsp/main.jsp").forward(req, resp);
		}
		else {
			//Si no está logueado
			String newUser = req.getParameter("newUser");
			String newPassword1 = req.getParameter("newPassword1");
			String newPassword2 = req.getParameter("newPassword2");
			
			if (newUser == null || newPassword1 == null || newPassword2 == null) {
				//Sin pasar por login.jsp
				app.getRequestDispatcher("/WEB-INF/jsp/crear.jsp").forward(req, resp);
				return;
			} else {
				//Pasando por login.jsp
				if (Usuario.samePassword(newPassword1, newPassword2)) {
					//Si el password es igual
					if (newUser.equals("") || newPassword1.equals("")) {
						//Si ha dejado algún campo vacío
						String error = "<div class='mensaje'> Tienes que rellenar todos los campos. </div>";
						req.setAttribute("error", error);
						app.getRequestDispatcher("/WEB-INF/jsp/crear.jsp").forward(req, resp);
						return;
					} else {
						//Si todos los campos están llenos
						if(!UsersModel.existeUsuario(newUser, app)) {
							//Si el usuario no existe
							Usuario user = new Usuario(newUser, newPassword1, false);
							if (UsersModel.nuevoUsuario(user, app)) {
								//Creado correctamente
								String error = "<div class='mensaje'> Usuario creado correctamente. </div>";
								System.out.println("Nuevo usuario creado correctamente. (" + newUser + ")");
								req.setAttribute("error", error);
								
								/*********/
								System.out.println("Login correcto. (" + newUser + ")");
								session.setAttribute("usuario", user);
								req.setAttribute("carritoUsuario", model.CarritoModel.getCarritoUsuario(session, app));
								req.setAttribute("productos", ProductosModel.getProductosInicio(req, app, session));

								app.getRequestDispatcher("/WEB-INF/jsp/main.jsp").forward(req, resp);
								return;
							} else {
								//Error al crear WTF XD
								String error = "<div class='mensaje'> Error desconocido al crear el usuario. </div>";
								req.setAttribute("error", error);
								app.getRequestDispatcher("/WEB-INF/jsp/crear.jsp").forward(req, resp);
								return;
							}
							
						} else {
							//Si el usuario ya existe
							String error = "<div class='mensaje'> El nombre de usuario ya existe. </div>";
							req.setAttribute("error", error);
							app.getRequestDispatcher("/WEB-INF/jsp/crear.jsp").forward(req, resp);
							return;
						}
							
					}
				} else {
					//Volver a la página y decir con error que los password son diferentes.
					String error = "<div class='mensaje'> La contraseña debe ser igual en ambos campos. </div>";
					req.setAttribute("error", error);
					app.getRequestDispatcher("/WEB-INF/jsp/crear.jsp").forward(req, resp);
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
