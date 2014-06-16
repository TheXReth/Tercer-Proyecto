package model;

import javax.servlet.ServletContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

import javax.servlet.http.HttpSession;

import forms.Carrito;
import forms.Usuario;

public class CarritoModel {
	
	public static void añadirACarrito(int id, HttpSession session){
		Carrito c = (Carrito)session.getAttribute("carrito");
		
		if (c == null) {
			c = new Carrito(id);
			session.setAttribute("carrito", c);
		} else c.setProducto(id);
	}
	
	public static void restarDeCarrito(int id, HttpSession session){
		Carrito c = (Carrito)session.getAttribute("carrito");
		
		if (c == null) {
			c = new Carrito(id);
			c.removeProducto(id);
			session.setAttribute("carrito", c);
		} else c.setMenosProducto(id);
	}
		
	public static void eliminarDeCarrito(int id, HttpSession session){
		Carrito c = (Carrito)session.getAttribute("carrito");
		if (c != null) c.removeProducto(id);
	}
	
	public static String parseCarritoSession(Carrito c, ServletContext app){
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("<img src='src/images/carrito1.png'></img><div id='Carrito'>");
			if (c.getNumElem() > 0) {
				String listaIds = c.getIdList();

				String sql = "SELECT * FROM pruebas.productos WHERE (codigo IN ( " + listaIds + "))";
				BaseDatos bd = new BaseDatos(sql, app);
				ResultSet rsProductos = bd.executeQuery();

				double total = 0;

				while (rsProductos.next()) {
					String codigo = rsProductos.getString("codigo");

					int[] aux = c.getElementoPorPos(c.getPosicion(rsProductos.getInt("codigo")));
					total += rsProductos.getDouble("precio")*aux[1];

					sb.append("<div id='ProduCarrito'>");
					
					sb.append("<div id='cantProdu'><a class='decrementar' href='javascript:void(0);' "
							+ "data-doc_value="+ codigo + "> - </a>" + aux[1] 
							+ "<a class='carritoDescripcion' href='javascript:void(0);' "
							+ "data-doc_value="+ codigo + "> + </a>"
							+ "</div>");

					/********************/
					String nombre = rsProductos.getString("nombre");
					if (nombre.length() > 30) nombre = nombre.substring(0, 29) + "...";
					sb.append("<div id='idProdu'><img class='imgCarrito' src='src/images/"
							+ rsProductos.getString("imagen") + "'></img>"
							+ "<a href='producto?id=" + aux[0] + "'>" + nombre + "</a>"
							+ "<div class='precioCarrito'> " + rsProductos.getString("precio") + " </div></div>");
					/********************/
					
					sb.append("<a class='eliminar' href='javascript:void(0);' "
							+ "data-doc_value="+ codigo + ">Eliminar</a>");
					sb.append("</div>");
				}
				if (total > 0) {
					DecimalFormat df = new DecimalFormat("#.##");
					sb.append("<div id='totalCarrito'>Total: " + df.format(total) + "€</div>");
					
					/////////////////////////////////
					sb.append("<a class='eliminarCarrito' href='javascript:void(0);'>Vaciar carrito</a>");
					/////////////////////////////////
					
					sb.append("<a id='guardarCarrito' href='panelCarritos?guardar'>Guardar carrito</a>");
				}

				bd.close();

			} else sb.append("<p> No tienes ningún elemento. </p>");
			sb.append("<a id='verCarritos' href='carrito?verCarritos'>Ver carritos</a>");

			sb.append("</div>");
		} catch (Exception e) {
			System.out.println("Error en CarritoModel.parseCarritoSession");
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static boolean guardarCarrito(Carrito c, HttpSession session, ServletContext app) {
		Usuario user = (Usuario)session.getAttribute("usuario");
		if (user != null) {
			if (c.getNumElem() > 0) {
				String sql = "INSERT INTO carritos(productos, cant_prod, usuario) VALUES (?, ?, ?)";
				BaseDatos bd = new BaseDatos(sql, app);
				bd.setString(1, c.getIdList());
				bd.setString(2, c.getCantList());
				bd.setString(3, user.getNombre());
				int numRows = bd.executeUpdate();
				
				bd.close();
				if (numRows == 1)
					return true;
				else
					return false;
			}
		}
		System.out.println("Intentan guardar un carrito sin tener iniciada la sesión.");
		return false;
	}
	
	public static boolean borrarCarrito(int idcarrito, ServletContext app) {
		
		String sql = "DELETE FROM carritos WHERE (idcarritos = ?)";
		
		BaseDatos bd = new BaseDatos(sql, app);
		bd.setInt(1, idcarrito);
		
		int numRows = bd.executeUpdate();
		
		bd.close();
		if (numRows == 1)
			return true;
		else
			return false;
	}
	
	public static void borrarDeCarrito(int idProducto, HttpSession session) {
		Carrito c = (Carrito)session.getAttribute("carrito");
		
		if (c == null) System.out.println("Intentan borrar un producto sin tener carrito en session.");
		else c.removeProducto(idProducto);
	}
	
	public static String parseCarritosUsuario(ResultSet rs, ServletContext app){
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("<div id='listaCarritos'>");
			while (rs.next()) {

				String listaIds = rs.getString("productos");
				String[] lstIds = listaIds.split(", ");
				String listaCds = rs.getString("cant_prod");
				String[] lstCds = listaCds.split(", ");

				String sql = "SELECT * FROM pruebas.productos WHERE (codigo IN ( " + listaIds + "))";
				BaseDatos bd = new BaseDatos(sql, app);
				ResultSet rsProductos = bd.executeQuery();
				
				sb.append("<div class='carrito'>");
				sb.append("<a class='cargarlo' href='panelCarritos?cargar&id=" + rs.getInt("idcarritos") + "'> Cargar carro </a>");

				double total = 0;
				sb.append("<div class='datos'>");

				while (rsProductos.next()) {
					String cod = rsProductos.getString("codigo");
					int pos = getPosicionId(lstIds, cod);
					
					total += rsProductos.getDouble("precio")*Integer.parseInt(lstCds[pos]);
					sb.append("<div class='cantidad'>");
					sb.append(lstCds[pos]);
					sb.append("</div>");
					
					/********************/
					//Append datos productos -> ¿Llamar a ProductosModel Parse?
					sb.append("<div class='identificador'>");
					String nombre = rsProductos.getString("nombre");
					if (nombre.length() > 40) nombre = nombre.substring(0, 39) + "...";
					sb.append(nombre);
					sb.append("</div>");
					/********************/
				}
				sb.append("</div>");
				bd.close();
				
				DecimalFormat df = new DecimalFormat("#.##");

				sb.append("<div class='total'>Total: " + df.format(total) + "€</div>");
				sb.append("<a class='EliminarDeLista' href='panelCarritos?borrar&id=" + rs.getInt("idcarritos") + "'>Borrar carrito</a>");

				sb.append("</div>");
			}
			sb.append("</div>");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	private static int getPosicionId(String[] lstIds, String cod) {
		int i = 0;
		while (i < lstIds.length) {
			if (lstIds[i].equals(cod)) return i;
			++i;
		}
		return -1;
	}

	public static String getCarritosUsuario(String usuario, ServletContext app){
		String sql = "SELECT * FROM carritos WHERE (usuario LIKE '" + usuario + "')";
		BaseDatos bd = new BaseDatos(sql, app);
		ResultSet rs = bd.executeQuery();
		String resultado = parseCarritosUsuario(rs, app);
		bd.close();
		return resultado;
	}
	
	public static Carrito getCarritoId(int id, ServletContext app){
		String sql = "SELECT * FROM carritos WHERE (idcarritos = " + id + ")";
		BaseDatos bd = new BaseDatos(sql, app);
		ResultSet rs = bd.executeQuery();
		Carrito c = parseResultSet(rs);
		bd.close();
		return c;
	}
	
	public static Carrito parseResultSet(ResultSet rs){
		try {
			if (rs.next()) {
				
				String listaIds = rs.getString("productos");
				String[] lstIds = listaIds.split(", ");
				
				String listaCds = rs.getString("cant_prod");
				String[] lstCds = listaCds.split(", ");
				
				Carrito carroAux = new Carrito(Integer.parseInt(lstIds[0]));
				carroAux.setCantidadProducto(Integer.parseInt(lstIds[0]), Integer.parseInt(lstCds[0]));
				
				for (int i = 1; i < lstIds.length; ++i) {
					carroAux.setProducto(Integer.parseInt(lstIds[i]));
					carroAux.setCantidadProducto(Integer.parseInt(lstIds[i]), Integer.parseInt(lstCds[i]));
				}
				
				carroAux.setId(rs.getInt("idcarritos"));
				return carroAux;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error en CarritoModel.parseResultSet");
		}
		return null;
	}
	
	public static String getCarritoUsuario(HttpSession session, ServletContext app) {
		String carritoUser = "";
		
		if (session.getAttribute("carrito") != null) {
			Carrito car = (Carrito)session.getAttribute("carrito");
			carritoUser = parseCarritoSession(car, app);
		} else {
			Carrito c = new Carrito(1);
			c.removeProducto(1);
			carritoUser = parseCarritoSession(c, app);
		}
		return carritoUser;
	}

	public static void borrarCarritoSession(HttpSession session) {
		Carrito car = (Carrito)session.getAttribute("carrito");
		if (car != null) session.removeAttribute("carrito");
	}
	
	public static void cargarCarrito(HttpSession session, int id, ServletContext app) {
		session.removeAttribute("carrito");
		
		Carrito carrito = getCarritoId(id, app);
		
		session.setAttribute("carrito", carrito);
		
	}
	
}
