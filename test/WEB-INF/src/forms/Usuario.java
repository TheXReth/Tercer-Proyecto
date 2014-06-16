package forms;

public class Usuario {
	private String nombre;
	private String password;
	private boolean admin;
	
	public Usuario(String nombre, String password, boolean admin) {
		this.nombre = nombre;
		this.password = password;
		this.admin = admin;
	}
	
	public static boolean samePassword(String password1, String password2) {
		return password1.equals(password2);
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public String getPassword() {
		return password;
	}
	
	public boolean isAdmin() {
		return admin;
	}
	
}
