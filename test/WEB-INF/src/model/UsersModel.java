package model;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletContext;

import forms.Usuario;

public class UsersModel {
	
	public static boolean usuarioCorrecto(String nombre, String password, ServletContext app) {
		String sql = "select * from usuarios where (nombre = ? AND password = ?)";
		BaseDatos bd = new BaseDatos(sql, app);
		bd.setString(1, nombre);
		bd.setString(2, password);
		
		ResultSet rs = bd.executeQuery();
				
		try {
			if (rs.next()) {
				bd.close();
				return true;
			} else {
				bd.close();
				return false;
			}
		} catch (Exception e) {
			System.out.println("Error en usersModel.existeUsuario.");
			e.printStackTrace();
			bd.close();
			return true;
		}
	}
	
	public static boolean existeUsuario(String nombre, ServletContext app) {
		String sql = "select * from usuarios where nombre = ?";
		BaseDatos bd = new BaseDatos(sql, app);
		bd.setString(1, nombre);
		
		ResultSet rs = bd.executeQuery();

		try {
			if (rs.next()) {
				bd.close();
				return true;
			} else {
				bd.close();
				return false;
			}
		} catch (Exception e) {
			System.out.println("Error en usersModel.existeUsuario.");
			e.printStackTrace();
			bd.close();
			return true;
		}
	}
	
	public static boolean nuevoUsuario(Usuario user, ServletContext app) {
		String sql = "INSERT INTO usuarios (nombre, password) VALUES (?, ?);";
				
		BaseDatos bd = new BaseDatos(sql, app);
		bd.setString(1, user.getNombre());
		bd.setString(2, user.getPassword());
		
		int rs = bd.executeUpdate();

		boolean resultado = (rs != -1);
		bd.close();
		return resultado;
		
	}

	public static boolean isAdmin(String user, ServletContext app) {
		String sql = "SELECT god FROM usuarios WHERE nombre = ?";
		
		BaseDatos bd = new BaseDatos(sql, app);
		bd.setString(1, user);
		
		ResultSet rs = bd.executeQuery();
		boolean respuesta = false;
		
		try {
			if (rs.next()) {
				respuesta = (rs.getInt("god") == 1);
				bd.close();
				return respuesta;
			} else {
				bd.close();
				return respuesta;
			}
		} catch (Exception e) {
			System.out.println("Error en usersModel.idAdmin.");
			e.printStackTrace();
			bd.close();
			return false;
		}
	}

	public static String getUsuarios(ServletContext app) {
		String sql = "SELECT nombre,god FROM pruebas.usuarios;";
		
		BaseDatos bd = new BaseDatos(sql, app);		
		ResultSet rs = bd.executeQuery();
		
		String resultado = parseListaUsuarios(rs);
		bd.close();
		return resultado;
		
	}

	private static String parseListaUsuarios(ResultSet rs) {
				
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("<div id='listaUsuarios'>");
			sb.append("<div class='usuario' style='background-color:rgba(0,0,0,0);'><div class='nombre'"
					+ " style='background-color: rgba(247, 222, 213,.5);'> Nombre </div> <div class='admin'"
					+ "style='background-color: rgba(247, 222, 213,.5);'> Tipo </div> </div>");
			while (rs.next()) {
				sb.append("<div class='usuario'>");
				
				sb.append("<div class='nombre'>" + rs.getString("nombre") + "</div>");
				
				int god = rs.getInt("god");
				if (god == 1) {
					sb.append("<div class='admin'>Administrador</div>");
					sb.append("<a class='botonPrivilegios' href='consultar?usuarios&admin=0&nombre="+rs.getString("nombre")+"'>Quitar privilegios</a>");
				}
				else {
					sb.append("<div class='admin'>Usuario normal</div>");
					sb.append("<a class='botonPrivilegios' href='consultar?usuarios&admin=1&nombre="+rs.getString("nombre")+"'>Dar privilegios</a>");
				}
				sb.append("<a class='botonEliminar' href='consultar?usuarios&eliminar=" + rs.getString("nombre") + "'>Eliminar</a>");

				sb.append("</div>");
			}
			sb.append("</div>");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static void setPrivilegios(String admin, String nombre, ServletContext app) {
		if (soloQuedaUnAdmin(app) && admin.equals("0")) System.out.println("Intentan borrar un admin cuando sólo queda uno. Que malotes D:");
		else {
			if ((admin.equals("1") && isAdmin(nombre, app)) || (admin.equals("0") && !isAdmin(nombre, app))) {
				System.out.println("Alguien intenta poner a 1 un admin o a 0 un usuario normal.");
			} else {
				String sql = "UPDATE `pruebas`.`usuarios` SET god = ? WHERE nombre = ?";
				
				BaseDatos bd = new BaseDatos(sql, app);
				bd.setString(1, admin);
				bd.setString(2, nombre);
				
				bd.executeUpdate();
				bd.close();
			}
		}
	}

	private static boolean soloQuedaUnAdmin(ServletContext app) {
		String sql = "SELECT COUNT(*) AS numeroAdmins FROM pruebas.usuarios WHERE (god = 1);";
		
		BaseDatos bd = new BaseDatos(sql, app);		
		ResultSet rs = bd.executeQuery();
		
		int i = 1;
			try {
				if (rs.next())
				i = rs.getInt("numeroAdmins");
			} catch (SQLException e) {
				System.out.println("Error en UsersModel.soloQuedaUnAdmin");
				e.printStackTrace();
			}		
		bd.close();
		return (i == 1);
		
	}

	public static void eliminarUsuario(String nombre, ServletContext app) {
		if (!isAdmin(nombre, app)) {
			String sql = "DELETE FROM `pruebas`.`usuarios` WHERE nombre = ?";
			
			BaseDatos bd = new BaseDatos(sql, app);
			bd.setString(1, nombre);
			
			bd.executeUpdate();
			bd.close();

		} else System.out.println("¡Intentan borrar un admin! D:");
	}
}
