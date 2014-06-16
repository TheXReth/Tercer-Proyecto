package forms;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Comentario {
	
	private String autor;
	private String texto;
	private int valoracion;
	private String fecha_post;
	private int id_producto;
	
	public Comentario(String autor, String texto, int valoracion, int id_producto){
		this.autor = autor;
		this.texto = texto.replace("<", "&lt");
		this.valoracion = valoracion;
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		fecha_post = sdf.format(d);
		this.id_producto = id_producto;
	}
	
	public String getAutor() {
		return autor;
	}
	public void setAutor(String autor) {
		this.autor = autor;
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}
	public int getValoracion() {
		return valoracion;
	}
	public void setValoracion(int valoracion) {
		this.valoracion = valoracion;
	}
	public String getFecha_post() {
		return fecha_post;
	}
	public int getId_producto() {
		return id_producto;
	}
	public void setId_producto(int id_producto) {
		this.id_producto = id_producto;
	}
	
	
}