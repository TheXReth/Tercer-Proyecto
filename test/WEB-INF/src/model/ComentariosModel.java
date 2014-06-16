package model;

import java.sql.ResultSet;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;





import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import forms.Comentario;
import forms.Usuario;

public class ComentariosModel {
		
	public static boolean borrar (int id, ServletContext app) {
		
		String sql = "DELETE FROM `pruebas`.`comentarios` WHERE `cod_com`= ? ;";
		
		BaseDatos bd = new BaseDatos(sql, app);
		bd.setInt(1, id);
		
		int numRows = bd.executeUpdate();
		bd.close();

		if (numRows == 1)
			return true;
		else
			return false;
	}
	
	public static boolean setComentario(Comentario c, ServletContext app) {
		
		String sql = "INSERT INTO comentarios (Autor, Texto, ";
		if (c.getValoracion() == -1) {
			sql += "Fecha_post, Id_producto) VALUES (?,?,?,?)";	
		} else {
			sql += "Fecha_post, Id_producto, Valoracion) VALUES (?,?,?,?,?)";
		}
		
		BaseDatos bd = new BaseDatos(sql, app);
		bd.setString(1, c.getAutor());
		bd.setString(2, c.getTexto());		
		bd.setString(3, c.getFecha_post());
		bd.setInt(4, c.getId_producto());
		if(c.getValoracion() != -1)
			bd.setInt(5, c.getValoracion());
		int rs = bd.executeUpdate();
		boolean resultado = (rs != -1);
		bd.close();
		return resultado;
		
	}
	
	public static String getComentariosId(int Id, ServletContext app, HttpSession session){
			String sql = "Select * from comentarios where (Id_producto LIKE " + Id + ") order by Fecha_post desc";
			BaseDatos bd = new BaseDatos(sql, app);
			ResultSet rs = bd.executeQuery();

			String resultado = ComentariosModel.parseHTML(rs, session, app, Id);
			bd.close();
			return resultado;
	}
	
	
	public static String parseHTML(ResultSet rs, HttpSession session, ServletContext app, int Id) {

		Usuario usuario = (Usuario) session.getAttribute("usuario");
		String nombreUsuario = "invitado";
		boolean esAdmin = false;

		if (usuario != null) { 
			nombreUsuario = usuario.getNombre();
			esAdmin = UsersModel.isAdmin(nombreUsuario, app);
		}
		
		StringBuffer sb = new StringBuffer();
		try {
			while (rs.next()) {
				Date d = rs.getDate("Fecha_post");
				Time t = rs.getTime("Fecha_post");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String fecha_post = sdf.format(d);
				sdf = new SimpleDateFormat("HH:mm:ss");
				fecha_post += " " + sdf.format(t);

				String autor = rs.getString("Autor");
				String autorComparacion = autor.toLowerCase();

				sb.append("<div id='comentarioCSS'>");
				sb.append("<div id='fechaComentario'>");
				sb.append(fecha_post + "</div>");
				sb.append("<h1>" + autor + "</h1>");
				
				boolean condicion = ((autorComparacion.equals(nombreUsuario.toLowerCase()) || esAdmin) && (!nombreUsuario.equals("invitado")));
				
				if (condicion) {
					sb.append("<div class='eliminarComentario'><a class='eliminarComentarioLink' href='javascript:void(0);' "
							+ "data-doc_value=" + rs.getString("cod_com") + "x" + Id + ">Eliminar</a></div>");
				}
				int val = rs.getInt("Valoracion");
				if (val == 1)
					sb.append("<div id='valoracion'> Lo recomiendo </div>");
				else
					sb.append("<div id='valoracion'> No lo recomiendo </div>");
				sb.append("<p>" + rs.getString("Texto") + "</p>");
				sb.append("</div>");
			}
		} catch (Exception e) {
			System.out.println("Error en ParseHTML en model.ComentariosModel");
			e.printStackTrace();
		}
		return sb.toString();
	}

}
