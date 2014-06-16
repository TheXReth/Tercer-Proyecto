package servlets;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.ComentariosModel;

public class EliminarComentario extends HttpServlet{


	private static final long serialVersionUID = 1L;


	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");

		ServletContext app = getServletContext();
		HttpSession session = req.getSession();

		String idComentarioAux = req.getParameter("doc_reply");

		String id[] = idComentarioAux.split("x");

		int idCom = Integer.parseInt(id[0]);
		int idPro = Integer.parseInt(id[1]);

		ComentariosModel.borrar(idCom, app);

		String comentarios = model.ComentariosModel.getComentariosId(idPro, app, session);

		resp.getWriter().write(comentarios);

	}


	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req,resp);
	}

}

