package servlets;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.ComentariosModel;
import model.ProductosModel;
import forms.Comentario;
import forms.Usuario;

public class Posteo extends HttpServlet{

	private static final long serialVersionUID = 1L;
	String error = "";
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");

		ServletContext app = getServletContext();
		HttpSession session = req.getSession();
		
		req.setAttribute("productos", ProductosModel.getProductosInicio(req, app, session));
		req.setAttribute("tipos", ProductosModel.getTiposProductos(app,session));
		
		String comentario = req.getParameter("comentario");

		req.setAttribute("carritoUsuario", model.CarritoModel.getCarritoUsuario(session, app));
		
		if (comentario == null){
			error = "Erroraso en doPost";
			req.setAttribute("error", "Error general");
			
			app.getRequestDispatcher("/WEB-INF/jsp/main.jsp").forward(req, resp);
			return;
		} else {
			if (comentario.equals("")) {
				error = "Comentario vacio chichón";
				
				int idProCom = 1;
				String idPC = req.getParameter("idprocom");
				if (idPC != null) idProCom = Integer.parseInt(idPC);
				
				String direccion = "/producto?id=" + idProCom;
				app.getRequestDispatcher(direccion).forward(req, resp);
				return;
			} else {		
				int valoracion = Integer.parseInt(req.getParameter("valoracion"));

				Usuario usuario = (Usuario)session.getAttribute("usuario");
				String user = "Invitado";
				if (usuario != null) user = usuario.getNombre();
				
				int idProCom = 1;
				String idPC = req.getParameter("idprocom");
				if (idPC != null) idProCom = Integer.parseInt(idPC);

				Comentario c = new Comentario(user, comentario, valoracion, idProCom);
				if(ComentariosModel.setComentario(c, app)){

					String direccion = "/producto?id=" + idProCom;
					app.getRequestDispatcher(direccion).forward(req, resp);
					return;
				}
				else{
					error = "No se pudo ponel el comentario";
					req.setAttribute("error", error);
					
				}
			}	
		}
		app.getRequestDispatcher("/WEB-INF/jsp/main.jsp").forward(req, resp);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	

}
