package servlets;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import model.ProductosModel;
import forms.Usuario;

@MultipartConfig(fileSizeThreshold=1024*1024*4, 
maxFileSize=1024*1024*4, maxRequestSize=1024*1024*10)
public class Modificar extends HttpServlet {

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
				String id = req.getParameter("id");
				if (id != null) {
					String ocultar = req.getParameter("Ocultar");
					String tipo = req.getParameter("tipo");
					String pagina = req.getParameter("pagina");
					if (ocultar != null && tipo != null && pagina != null) {
						ProductosModel.ocultarProducto(id, app);
						String direccion = "/main?tipo=" + tipo + "&pagina=" + pagina;
						app.getRequestDispatcher(direccion).forward(req, resp);
						return;
					} else {
						if (tipo != null && pagina != null) {
							req.setAttribute("modificar", ProductosModel.getModificar(id, app, tipo, pagina));
							app.getRequestDispatcher("/WEB-INF/jsp/modificar.jsp").forward(req, resp);
							return;
						} else {
							req.setAttribute("modificar", ProductosModel.getModificar(id, app, "-1", "-1"));
							app.getRequestDispatcher("/WEB-INF/jsp/modificar.jsp").forward(req, resp);
							return;
						}
					} 
				} else {
					String tipo = req.getParameter("tipo");
					String pagina = req.getParameter("pagina");
					if (tipo != null && pagina != null) {
						String direccion = "/main?tipo=" + tipo + "&pagina=" + pagina;
						app.getRequestDispatcher(direccion).forward(req, resp);
						return;
					} else {
						String direccion = "consultar?productos";
						app.getRequestDispatcher(direccion).forward(req, resp);
						return;
					}
				}	
			}
		} else {
			app.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
			return;
		}
	}
	
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		
		boolean volverMain = true;
		ServletContext app = getServletContext();
		HttpSession session = req.getSession();
		
		String anadir = req.getParameter("anadir");		
		String tipo = req.getParameter("tipo");		
		String pagina = req.getParameter("pagina");
		
		final String UPLOAD_DIRECTORY = "/home/thexreth/workspace/test/src/images/";
		String nombreImagen = "";
		int id = 0;
		
		if (anadir != null) {
			volverMain = false;
			ProductosModel.anadirProducto(req, app);
			nombreImagen = "image" + ProductosModel.getUltimaID(app);
			id = ProductosModel.getUltimaID(app);
		} else {
			String update = req.getParameter("update");
			if (update != null) {
				if (tipo == null || pagina == null) volverMain = false;
				if (!update.equals("")) id = Integer.parseInt(update); 
				ProductosModel.modificarProducto(id, req, app);
				nombreImagen = "image" + id;
			}
		}
		
		//////////////////////////////////////////////////////////////////
			Part filePart = req.getPart("file");
		    String filename = getFilename(filePart);		
		    
		    if (filename != null) if (filename.length() > 4) {
			    int size = filename.length();
				String formato = filename.substring(size - 4, size).toLowerCase();
				if (formato.equals(".jpg") || formato.equals(".png") || formato.equals(".jpeg"))
			    filePart.write(UPLOAD_DIRECTORY + File.separator + nombreImagen + formato);
			    ProductosModel.actualizarImagen(app, id, nombreImagen + formato);
		    }
		//////////////////////////////////////////////////////////////////
		    
		req.setAttribute("tipos", ProductosModel.getTiposProductos(app,session));
		req.setAttribute("carritoUsuario", model.CarritoModel.getCarritoUsuario(session, app));
		
		if (volverMain) {
			req.setAttribute("productos", ProductosModel.getProductosInicio(req, app, session));
			
			String direccion = "/main?tipo=" + tipo + "&pagina=" + pagina;
			app.getRequestDispatcher(direccion).forward(req, resp);
			return;
		} else {
			String direccion = "/consultar";
			req.setAttribute("VolverProductos", "wooolap");
			app.getRequestDispatcher(direccion).forward(req, resp);
			return;
		}
	}
	
	private static String getFilename(Part part) {
	    for (String cd : part.getHeader("content-disposition").split(";")) {
	        if (cd.trim().startsWith("filename")) {
	            String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
	            return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE fix.
	        }
	    }
	    return null;
	}

}
