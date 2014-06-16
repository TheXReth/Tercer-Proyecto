package model;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import forms.Usuario;

public class ProductosModel {
	
	public static String getProductosInicio(HttpServletRequest req, ServletContext app, HttpSession session) {
		String tipo = req.getParameter("tipo");
		
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		boolean admin = false;
		if(usuario != null) if (UsersModel.isAdmin(usuario.getNombre(), app)) admin = true;
			
		String paginaAux = req.getParameter("pagina");
		int pagina; 
		
		if (paginaAux == null) pagina = 1;
		else {
			if(paginaAux.equals("")) pagina = 1;
			else pagina = Integer.parseInt(paginaAux);
		}
		--pagina;

		int primerElemento = (pagina*16);
				
		String sql;
		String sqlCount;
		
		if (tipo == null) {
			sql = "SELECT * FROM pruebas.productos WHERE (oculto = 0) ORDER BY codigo DESC LIMIT " + primerElemento + "," + (primerElemento + 16) + ";";
			sqlCount = "SELECT COUNT(*) AS numElementos FROM pruebas.productos WHERE (oculto = 0) ORDER BY codigo DESC";
		} else {
			if (!tipo.equals("")) {
				sql = "SELECT * FROM pruebas.productos WHERE (tipo LIKE ? AND oculto = 0) ORDER BY precio LIMIT " + primerElemento + "," + (primerElemento + 16) + ";";
				sqlCount = "SELECT COUNT(*) AS numElementos FROM pruebas.productos WHERE (tipo LIKE ? AND oculto = 0) ORDER BY codigo DESC";
			} else {
				sql = "SELECT * FROM pruebas.productos WHERE (oculto = 0) ORDER BY precio LIMIT " + primerElemento + "," + (primerElemento + 16) + ";";
				sqlCount = "SELECT COUNT(*) AS numElementos FROM pruebas.productos WHERE (oculto = 0) ORDER BY codigo DESC";
			}
		}
		
		BaseDatos bd = new BaseDatos(sql, app);
		BaseDatos bdCount = new BaseDatos(sqlCount, app);
		if (tipo != null) {
			if (!tipo.equals("")) {
				bd.setString(1, tipo);
				bdCount.setString(1, tipo);
			}
		}
		
		ResultSet rs = bd.executeQuery();
		ResultSet rsCount = bdCount.executeQuery();
		
		String resultado = parseHTML(rs, rsCount, 1, pagina, tipo, app, admin, session);
		bd.close();
		bdCount.close();
		return resultado;
	}

	public static String getTiposProductos(ServletContext app, HttpSession session) {
		String sql = "SELECT DISTINCT tipo FROM pruebas.productos WHERE (oculto = 0);";
		BaseDatos bd = new BaseDatos(sql, app);
		ResultSet rs = bd.executeQuery();
		String resultado = parseHTML(rs,null, 2, 0, null, app, false, session);
		bd.close();
		return resultado;
	}

	public static String getProductoID(HttpServletRequest req, ServletContext app, HttpSession session) {
		String id = req.getParameter("id");
		if (id != null) {
			if (!id.equals("")) {
				int iden = Integer.parseInt(id);
				String sql = "SELECT * FROM pruebas.productos WHERE (codigo = ?);";
				BaseDatos bd = new BaseDatos(sql, app);
				bd.setInt(1, iden);
				ResultSet rs = bd.executeQuery();
	
				String resultado = parseHTML(rs,null,3,0,null, app, false, session);
				bd.close();
				return resultado;
			}
		}
		return "";
	}
	
	public static void incrementarClickID(int id, int clicks, ServletContext app) {
		String sql = "UPDATE `pruebas`.`productos` SET `clicks`='" + ++clicks + "' WHERE `codigo`='" + id + "';";
		BaseDatos bd = new BaseDatos(sql, app);
		bd.executeUpdate();
		bd.close();
	}
	
	private static String getPaginas(int elementos, int paginaActual, String tipo) {
		++paginaActual;
		elementos += 15;
		int paginas = elementos/16;
		
		StringBuffer sb = new StringBuffer();
		sb.append("<div id='navPaginas'>");
		
		if(paginaActual != 1) {
			sb.append("<div class='page'><a class='page' href='main?pagina=1");
			if (tipo != null) sb.append("&tipo=" + tipo);
			sb.append("'><<</a></div>");
		} else sb.append("<div class='page'></div>");
		
		for (int i = -2; i < 3; ++i) {
			if (paginaActual + i >= 1 && paginaActual + i <= paginas) {
				sb.append("<div class='page'><a class='page' href='main?pagina=" + (paginaActual + i));
				if (tipo != null) sb.append("&tipo=" + tipo);
				sb.append("'>" + (paginaActual + i) + "</a></div>");
			} else sb.append("<div class='page'></div>");
		}

		if (paginaActual != paginas) {
			sb.append("<div class='page'><a class='page' href='main?pagina=" + paginas);
			if (tipo != null) sb.append("&tipo=" + tipo);
			sb.append("'>>></a></div>");
		} else sb.append("<div class='page'></div>");
		
		sb.append("</div>");
		return sb.toString();
	}
	
	private static String parseHTML(ResultSet rs, ResultSet rsCount, int tipo, int paginaActual, 
			String tipoDeProducto, ServletContext app, boolean admin, HttpSession session) {
		if (rs != null) {
			StringBuffer resultado = new StringBuffer();
			switch (tipo) {
				case 1:
					int cont = 0;
					try {
						while (rs.next() && cont < 16) {
							resultado.append("<div class='elemento'>");
							
							resultado.append("<div class='descripcion'><a href='producto?id=" + rs.getString("codigo") + "'>");
							if(rs.getString("minidescripcion") != null) {
								if (!rs.getString("minidescripcion").equals("null")) {
									String minidescripcion = rs.getString("minidescripcion");
									if (minidescripcion.length() > 200) minidescripcion = minidescripcion.substring(0, 199) + "...";
									resultado.append(minidescripcion);
								}
								else resultado.append("David, termina el buscador.</br></br></br></br>Y paga la coca.");
							} else resultado.append("David, termina el buscador.</br></br></br></br>Y paga la coca.");
							
							if (admin) {
								resultado.append("<a class='botonModificar' href='modificar?id=" 
								+ rs.getString("codigo") + "&zonaAdmin'>Modificar</a>");
								resultado.append("<a class='botonEliminar' href='consultar?productos&eliminar=" 
								+ rs.getString("codigo") + "'>Eliminar</a>");
							}

							resultado.append("<a class='carritoDescripcion' href='javascript:void(0);' "
									+ "data-doc_value="+rs.getString("codigo") + "> Añadir al carrito </a>");
							
							String nombre = rs.getString("nombre");
							if (nombre.length() > 30) nombre = nombre.substring(0, 29) + "...";
							resultado.append("</a></div><div class='etImg'><img class='imagenProducto' src='src/images/"
							+ rs.getString("imagen") + "'></img></div><p class='etTipo'>" + rs.getString("tipo") 
							+ "</p><p class='etNombre'>" + nombre + "</p><div class='etPrecio'>");
							resultado.append(rs.getString("precio") + "€</div>");
							resultado.append("</div>");
							++cont;
						}
						
						while (cont%4 != 0) {
							resultado.append("<div class='elementoNulo'></div>");
							++cont;
						}
						
						if (rsCount != null) {
							if (rsCount.next()) resultado.append(getPaginas(rsCount.getInt("numElementos"), paginaActual, tipoDeProducto));
						}
							
						return resultado.toString();
					} catch (SQLException e) {
						System.out.println("Error en ProductosModel.parseHTML1");
						e.printStackTrace();
					}
					break;
				case 2:
					try {
						while (rs.next()) {
							String tipoProducto = rs.getString("tipo");
							if (tipoProducto != null) {
								resultado.append("<li><a href='main?tipo=" + tipoProducto + "'>");
								resultado.append("<span>" + tipoProducto + "</span></a></li>");
							}
						}
						return resultado.toString();
					} catch (SQLException e) {
						System.out.println("Error en ProductosModel.parseHTML2");
						e.printStackTrace();
					}
					break;
				case 3:
					try {
						if (rs.next()) {
							String tipoProducto = rs.getString("tipo");
							resultado.append("<div id='boton'><a href='main'> Productos </a>");
							if (tipoProducto != null) 
								resultado.append("<p>&gt&gt</p><a href='main?tipo=" + tipoProducto + "'><span>" + tipoProducto + "</span></a>");
								resultado.append("</div><div id='title'>" +  rs.getString("nombre")+ "</div>"
									+ "<div id='imagen'><img class='imagenProducto' src='src/images/"
									+ rs.getString("imagen") + "'></img><div id='precio'> " + rs.getString("precio") +" €</div></div>"
									+ "<div id='descripcion'><p>" +  rs.getString("descripcion")+ "</p></div>"
									+ "<div id='panel'><a id='anadirACarrito' href='carrito?id=" + rs.getInt("codigo") + "'>Añadir al carrito </a></div>"
									+ "<div id='comentarios'>" + formComentarios(rs.getInt("codigo"))
									+ "<div id='CajaComentarios'>"
									+ ComentariosModel.getComentariosId(rs.getInt("codigo"), app, session) + "</div></div>");
								incrementarClickID(rs.getInt("codigo"), rs.getInt("clicks"), app);
							}

						return resultado.toString();
					} catch (SQLException e) {
						System.out.println("Error en ProductosModel.parseHTML3");
						e.printStackTrace();
					}
				default:
					return "";
			}
		}
		return null;
	}

	private static String formComentarios(int id) {
		String resultado = "<form id='peneForm' action='posteo.do?idprocom="+ id +"' method='post'>"
					+ "<textarea placeholder='Máximo 350 caracteres.' name='comentario' maxlength='350'></textarea>"
					+ "<div id='cajaDerechaPost'><div id='postRecom'><input type='radio' name='valoracion' value='1' checked='checked'/>Lo recomiendo"
					+ "<br/><input type='radio' name='valoracion' value='0' />No lo recomiendo</div>"
					+ "<input type='submit' value='Enviar'></div>"
					+ "</form>";
		return resultado;
	}

	public static String getConsultaProductos(ServletContext app) {
		String sql = "SELECT codigo,nombre,precio,oculto,disponible FROM pruebas.productos;";
		
		BaseDatos bd = new BaseDatos(sql, app);	
		ResultSet rs = bd.executeQuery();
		
		String resultado = parseListaProductos(rs);
		bd.close();
		return resultado;
	}

	private static String parseListaProductos(ResultSet rs) {
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("<div id='listaProductos'>");
			sb.append("<a href='modificar?id=0'>Añadir producto</a>");
			
			
			sb.append("<div class='producto' style='background-color: rgba(0,0,0,0);'>");
			sb.append("<div class='codigo' style='background-color: rgba(247, 222, 213,.5);'>ID</div>");
			sb.append("<div class='nombre' style='background-color: rgba(247, 222, 213,.5);'>Nombre</div>");
			sb.append("<div class='precio' style='background-color: rgba(247, 222, 213,.5);'>Precio</div>");
			sb.append("<div class='oculto' style='background-color: rgba(247, 222, 213,.5);'>Visible</div>");
			sb.append("<div class='Disponible' style='background-color: rgba(247, 222, 213,.5);'>Disponible</div>");
			sb.append("</div>");
			
			while (rs.next()) {
				sb.append("<div class='producto'>");
				sb.append("<div class='codigo'>" + rs.getString("codigo") + "</div>");
				String nombre = rs.getString("nombre");
				if (nombre.length() > 40) nombre = nombre.substring(0, 35) + "...";
				sb.append("<div class='nombre'>" + nombre + "</div>");
				sb.append("<div class='precio'>" + rs.getString("precio") + "</div>");
				sb.append("<div class='oculto' style='background-color:rgba(0,0,0,0);'><img src='src/images/");
				if (rs.getString("oculto").equals("1")) sb.append("oculto.png'>");
				else sb.append("nooculto.png'>");
				sb.append("</img></div>");
				sb.append("<div class='disponible' style='background-color:rgba(0,0,0,0);'><img src='src/images/");
				if (rs.getString("disponible").equals("1")) sb.append("stock.png'>");
				else sb.append("nostock.png'>");
				sb.append("</img></div>");
				sb.append("<a class='botonModificar' href='modificar?id=" + rs.getString("codigo") + "&zonaAdmin'>Modificar</a>");
				sb.append("<a class='botonEliminar' href='consultar?productos&eliminar=" + rs.getString("codigo") + "'>Eliminar</a>");
				sb.append("</div>");
			}
			sb.append("</div>");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static void eliminarProducto(String codigo, ServletContext app) {
		String sql = "DELETE FROM `pruebas`.`productos` WHERE codigo = ?";
		
		BaseDatos bd = new BaseDatos(sql, app);
		bd.setString(1, codigo);
		
		bd.executeUpdate();
		bd.close();
	}

	public static void ocultarProducto(String id, ServletContext app) {
		String sql = "UPDATE `pruebas`.`productos` SET oculto = 1 WHERE codigo = ?;";
		
		BaseDatos bd = new BaseDatos(sql, app);
		bd.setString(1, id);
		
		bd.executeUpdate();
		bd.close();
	}

	public static String getModificar(String id, ServletContext app, String tipo, String pagina) {
		int codigo = 0;
		if (!id.equals("")) codigo = Integer.parseInt(id);
		StringBuffer sb = new StringBuffer();
		
		String sqlTipos = "SELECT DISTINCT tipo FROM pruebas.productos;";
		BaseDatos bdTipos = new BaseDatos(sqlTipos, app);
		ResultSet rsTipos = bdTipos.executeQuery();
		
		if (codigo == 0) {
			sb.append("<form id='modificar' action='modificar' method='post' enctype='multipart/form-data'>"
					+ "<input name='anadir' type='hidden'/><div id='modNombre'><label>Nombre</label><input placeholder='Máximo 100 caracteres'"
					+ "type='text' name='nombre' maxlength='100'/></div><div id='modPrecio'><label>Precio</label>"
					+ "<input placeholder='Máximo 7 caracteres' type='text' name='precio' maxlength='8'>€"
					+ "</div><div id='modOculto'><label>Oculto</label><input type='checkbox' value='1' name='oculto' checked>"
					+ "</div><div id='modDescripcion'><label>Descripción</label><br/><textarea "
					+ "placeholder='Máximo 1000 caracteres' class='textareaDescripciones' maxlength='1000' "
					+ "name='descripcion'></textarea></div><div id='modMinidescripcion'><label>Minidescripción</label><br/>"
					+ "<textarea placeholder='Máximo 150 caracteres' class='textareaDescripciones' maxlength='150'"
					+ " name='minidescripcion'></textarea></div>"
					+ "<div id='subirImagen'> Imagen&nbsp<input type='file' name='file' accept='image/x-png, image/jpeg, image/jpg'/></div>"
					+ "<div id='modUnidades'><label>Unidades</label>"
					+ "<input placeholder='Máximo 4 caracteres' type='text' name='unidades' maxlength='4'></div><div id='modTipo'>"
					+ "<label>Tipo</label><input type='radio' name='tipo' value='lista' checked>"
					+ "<select name='lista'>");
				boolean primera = true;
			try {
				while (rsTipos.next()) {
					String tipoProd = rsTipos.getString("tipo");
					if (tipoProd != null) sb.append("<option value='" + tipoProd + "'");
					if (primera) {
						primera = false;
						sb.append(" selected='selected'");
					}
					sb.append(">" + tipoProd + "</option>");
				}
			} catch (SQLException e) {
				System.out.println("Error en ProductosModel.getModificar");
				e.printStackTrace();
			}
			sb.append("</select>"
					+ "<input type='radio' name='tipo' value='tipoNuevo'><input "
					+ "placeholder='Máximo 45 caracteres' type='text' placeholder='Tipo nuevo' name='tipoNuevo'"
					+ " maxlength='45'></div>"
					+ "<input id='modAceptar' type='submit' value='Aceptar'><input "
					+ "id='modReset'type='reset' value='Borrar todo'></form>");
		} else {
			
			String sqlProducto = "SELECT * FROM pruebas.productos WHERE (codigo = ?);";
			BaseDatos bd = new BaseDatos(sqlProducto, app);
			bd.setInt(1, codigo);
			ResultSet rsProducto = bd.executeQuery();
			
			try {
				if (rsProducto.next()) {
					//enctype='multipart/form-data'
					sb.append("<form id='modificar' action='modificar' method='post' enctype='multipart/form-data'>"
							+ "<input name='update' type='hidden' value='" + codigo + "'/><div id='modNombre'>"
							+ "<label>Nombre</label><input placeholder='Máximo 100 caracteres'"
							+ "type='text' name='nombre' maxlength='100' value='" + rsProducto.getString("nombre") 
							+ "'/></div><div id='modPrecio'><label>Precio</label>"
							+ "<input placeholder='Máximo 7 caracteres' type='text' name='precio' maxlength='8' "
							+ "value='" + rsProducto.getDouble("precio") + "'>€"
							+ "</div><div id='modOculto'><label>Oculto</label><input type='checkbox' value='1' name='oculto' ");
					if (rsProducto.getInt("oculto") == 1) sb.append("checked");
					sb.append("></div><div id='modDescripcion'><label>Descripción</label><br/><textarea "
							+ "placeholder='Máximo 1000 caracteres' class='textareaDescripciones' maxlength='1000' "
							+ "name='descripcion'>" + rsProducto.getString("descripcion") + "</textarea></div>"
							+ "<div id='modMinidescripcion'><label>Minidescripción</label><br/>"
							+ "<textarea placeholder='Máximo 150 caracteres' class='textareaDescripciones' maxlength='150'"
							+ " name='minidescripcion'>" + rsProducto.getString("minidescripcion") + "</textarea>"
							+ "</div>"
							+ "<div id='subirImagen'> Imagen&nbsp<input type='file' name='file' accept='image/x-png, image/jpeg, image/jpg'/></div>"
							+ "<div id='modUnidades'><label>Unidades</label>"
							+ "<input placeholder='Máximo 4 caracteres' type='text' name='unidades' value='"
							+ rsProducto.getInt("unidades") + "'maxlength='4'></div><div id='modTipo'>"
							+ "<label>Tipo</label><input type='radio' name='tipo' value='lista' checked>"
							+ "<select name='lista'>");
					try {
						while (rsTipos.next()) {
							String tipoProd = rsTipos.getString("tipo");
							if (tipoProd != null) {
								sb.append("<option value='" + tipoProd + "'");
										if (tipoProd.equals(rsProducto.getString("tipo"))) sb.append(" selected='selected'");
										sb.append(">" + tipoProd + "</option>");
							}
						}
					} catch (SQLException e) {
						System.out.println("Error en ProductosModel.getModificar (tipo)");
						e.printStackTrace();
					}
					sb.append("</select>"
					+ "<input type='radio' name='tipo' value='tipoNuevo'><input "
							+ "type='text' placeholder='Tipo nuevo' name='tipoNuevo'"
							+ " maxlength='45'></div>"
							+ "<input id='modAceptar' type='submit' value='Aceptar'><input "
							+ "id='modReset'type='reset' value='Descartar cambios'>"
							+ "</form>");
					bd.close();
				}
			} catch (SQLException e) {
				System.out.println("Error en ProductosModel.getModificar (producto)");
				e.printStackTrace();
			}
		}
		bdTipos.close();
		return sb.toString();
	}

	public static void anadirProducto(HttpServletRequest req, ServletContext app) {
		String sql = "INSERT INTO `pruebas`.`productos` "
				+ "(`nombre`, `precio`, `oculto`, `descripcion`, `unidades`, `tipo`, `minidescripcion`)"
				+ " VALUES ( ? , ? , ? , ? , ? , ? , ? )";
		
		BaseDatos bd = new BaseDatos(sql, app);
		
		String nombre = req.getParameter("nombre");
		if (nombre != null) {
			nombre = nombre.replace("<", "&lt");
			bd.setString(1, nombre);
		}
		else bd.setString(1, "");
		
		String precio = req.getParameter("precio");
		if (precio != null) {
			if (!precio.equals("")) {
				precio = precio.replace(",", ".");
				double price = 0.0;
				if (isNumeric(precio)) price = Double.parseDouble(precio);
				bd.setDouble(2, price);
			} else bd.setDouble(2, 0.0);
		} else bd.setDouble(2, 0.0);
		
		String oculto = req.getParameter("oculto");
		if (oculto != null) {
			if (!oculto.equals("")) bd.setInt(3, 1);
			else bd.setInt(3, 0);
		} else bd.setInt(3, 0);
		
		String descripcion = req.getParameter("descripcion");
		if (descripcion != null) {
			descripcion = descripcion.replace("<", "&lt");
			bd.setString(4, descripcion);
		} else bd.setString(4, "");
		
		String unidades = req.getParameter("unidades");
		if (unidades != null) {
			if (!unidades.equals("")) {
				int units = 0;
				if (isNumeric(precio)) units = Integer.parseInt(unidades);
				bd.setInt(5, units);
			} else bd.setInt(5, 100);
		} else bd.setInt(5, 100);		
		
		String tipo = req.getParameter("tipo");
		if (tipo != null) {
			if (tipo.equals("lista")) {
				String lista = req.getParameter("lista");
				if (lista != null) bd.setString(6, lista);
				else bd.setString(6, "Desconocido");
			} else {
				String tipoNuevo = req.getParameter("tipoNuevo");
				if (tipoNuevo != null) {
					tipoNuevo = tipoNuevo.replace("<", "&lt");
					bd.setString(6, tipoNuevo);
				}
				else bd.setString(6, "Desconocido");
			}
		} else bd.setString(6, "Desconocido");

		String minidescripcion = req.getParameter("minidescripcion");
		if (minidescripcion != null) {
			minidescripcion = minidescripcion.replace("<", "&lt");
			bd.setString(7, minidescripcion);
		}
		else bd.setString(7, "");

		bd.executeUpdate();
		bd.close();
	}

	public static void modificarProducto(int id, HttpServletRequest req,	ServletContext app) {
		String sql = "UPDATE `pruebas`.`productos` SET "
				+ "`nombre`= ? , `precio`= ? , `oculto`= ? , `descripcion`= ? , `unidades`= ? "
				+ ", `tipo`= ? , `minidescripcion`= ? WHERE `codigo`= ? ";
		
		BaseDatos bd = new BaseDatos(sql, app);
		
		String nombre = req.getParameter("nombre");
		if (nombre != null) {
			nombre = nombre.replace("<", "&lt");
			bd.setString(1, nombre);
		}
		else bd.setString(1, "");
		
		String precio = req.getParameter("precio");
		if (precio != null) {
			if (!precio.equals("")) {
				precio = precio.replace(",", ".");
				Double price = 0.0;
				if (isNumeric(precio)) price = Double.parseDouble(precio);
				bd.setDouble(2, price);
			} else bd.setDouble(2, 0.0);
		} else bd.setDouble(2, 0.0);
		
		String oculto = req.getParameter("oculto");
		if (oculto != null) {
			if (!oculto.equals("")) bd.setInt(3, 1);
			else bd.setInt(3, 0);
		} else bd.setInt(3, 0);
		
		String descripcion = req.getParameter("descripcion");
		if (descripcion != null) {
			descripcion = descripcion.replace("<", "&lt");
			bd.setString(4, descripcion);
		}
		else bd.setString(4, "");
		
		String unidades = req.getParameter("unidades");
		if (unidades != null) {
			if (!unidades.equals("")) {
				int units = 0;
				if (isNumeric(unidades)) units = Integer.parseInt(unidades);
				bd.setInt(5, units);
			} else bd.setInt(5, 100);
		} else bd.setInt(5, 100);		
		
		String tipo = req.getParameter("tipo");
		if (tipo != null) {
			if (tipo.equals("lista")) {
				String lista = req.getParameter("lista");
				if (lista != null) bd.setString(6, lista);
				else bd.setString(6, "Desconocido");
			} else {
				String tipoNuevo = req.getParameter("tipoNuevo");
				if (tipoNuevo != null) {
					tipoNuevo = tipoNuevo.replace("<", "&lt");
					bd.setString(6, tipoNuevo);
				}
				else bd.setString(6, "Desconocido");
			}
		} else bd.setString(6, "Desconocido");

		String minidescripcion = req.getParameter("minidescripcion");
		if (minidescripcion != null) {
			minidescripcion = minidescripcion.replace("<", "&lt");
			bd.setString(7, minidescripcion);
		}
		else bd.setString(7, "");
		
		bd.setInt(8, id);

		bd.executeUpdate();
		bd.close();	
	}
	
	private static boolean isNumeric(String str) {
	    if (str == null) {
	        return false;
	    }
	    int sz = str.length();
	    for (int i = 0; i < sz; i++) {
	        if (Character.isDigit(str.charAt(i)) == false) {
	        	if (str.charAt(i) != '.') return false;
	        }
	    }
	    return true;
	}

	public static String[][] getEstadisticas(ServletContext app) {
		final int CODIGO = 0;
		final int NOMBRE = 1;
		final int CLICKS = 2;
		
		String sql = "SELECT codigo, nombre, clicks FROM pruebas.productos ORDER BY clicks DESC LIMIT 0, 20;";
		
		BaseDatos bd = new BaseDatos(sql, app);	
		ResultSet rs = bd.executeQuery();
		
		String[][] producto = new String[20][3];

		int i = 0;
		try {
			while (rs.next()) {
				producto[i][CODIGO] = rs.getString("codigo");
				producto[i][NOMBRE] = rs.getString("nombre");
				producto[i][CLICKS] = rs.getString("clicks");
				++i;
			}
		} catch (SQLException e) {
			System.out.println("Error en vista.Util.getEstadisticas");
			e.printStackTrace();
		}
		
		while (i < 20) {
			producto[i][CODIGO] = "0";
			producto[i][NOMBRE] = "No hay producto";
			producto[i][CLICKS] = "0";
			++i;
		}
		
		bd.close();
		return producto;
	}

	public static int getUltimaID(ServletContext app) {
		int id = 0;
		String sql = "SELECT MAX(codigo) as idMaxima FROM pruebas.productos;";
		
		BaseDatos bd = new BaseDatos(sql, app);
		ResultSet rs = bd.executeQuery();
		
		try {
			if(rs.next()) {
				id = rs.getInt("idMaxima");
			}
		} catch (SQLException e) {
			System.out.println("Error en ProductosModel.getUltimaID");
			e.printStackTrace();
		}
		
		bd.close();
		return id;
	}

	public static void actualizarImagen(ServletContext app, int id,	String nombre) {
		String sql = "UPDATE `pruebas`.`productos` SET `imagen`= ? WHERE `codigo`= ? ";
		
		BaseDatos bd = new BaseDatos(sql, app);
		
		bd.setString(1, nombre);
		bd.setInt(2, id);

		bd.executeUpdate();
		bd.close();	
	}	
	
public static String buscarProductos(HttpServletRequest req, ServletContext app, HttpSession session, String buscar) {
		
		int pagina;
		
		if (req.getParameter("pagina") == null) pagina = 1;
		else pagina = Integer.parseInt(req.getParameter("pagina"));
		--pagina;

		int primerElemento = (pagina*16);
		
		String sql;
		String sqlCount;

		sql = "SELECT * FROM pruebas.productos WHERE (nombre LIKE ? OR tipo LIKE ?) ORDER BY codigo DESC LIMIT " + primerElemento + "," + (primerElemento + 16) + ";";
		sqlCount = "SELECT COUNT(*) AS numElementos FROM pruebas.productos ORDER BY codigo DESC";
		
		BaseDatos bd = new BaseDatos(sql, app);
		BaseDatos bdCount = new BaseDatos(sqlCount, app);
		
		bd.setString(1, "%" + buscar + "%");
		bd.setString(2, "%" + buscar + "%");
		
		ResultSet rs = bd.executeQuery();
		ResultSet rsCount = bdCount.executeQuery();
		
		String resultado = parseHTML(rs, rsCount, 1, pagina, "", app, false, session);
		bd.close();
		bdCount.close();
		return resultado;
	}
	
}
