package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import com.mchange.v2.c3p0.*;

public class BaseDatos {

	private static final String CONEXION_BD = "jdbc:mysql://127.0.0.1/pruebas";
	private static final String USER_BD = "root";
	private static final String PASS_BD = "asdasd";

	Connection con;
	private String sql;
	private PreparedStatement pstmt;
	private ServletContext app;
	
	public BaseDatos(String sql, ServletContext app) {
		this.sql = sql;
		this.app = app;
		conectar();
	}
	
	public void setString(int pos, String s) {
		try {
			pstmt.setString(pos, s);
		} catch (SQLException e) {
			System.out.println("Error en BaseDatos.setString");
			e.printStackTrace();
		} 
	}
	
	public void setInt(int pos, int i) {
		try {
			pstmt.setInt(pos, i);
		} catch (SQLException e) {
			System.out.println("Error en BaseDatos.setInt");
			e.printStackTrace();
		} 
	}
	
	public void setDouble(int pos, Double i) {
		try {
			pstmt.setDouble(pos, i);
		} catch (SQLException e) {
			System.out.println("Error en BaseDatos.setDouble");
			e.printStackTrace();
		} 
	}
	
	private void conectar() {
		DataSource pooled = (DataSource)app.getAttribute("pooled");

		try {
			if (pooled == null) {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				DataSource unpooled = DataSources.unpooledDataSource(CONEXION_BD, USER_BD, PASS_BD);
				pooled = DataSources.pooledDataSource(unpooled);
				app.setAttribute("pooled", pooled);
			}
			con = pooled.getConnection();
			pstmt = con.prepareStatement(sql);
		} catch (Exception e) {
			System.out.println("Error en BaseDatos.conectar");
			e.printStackTrace();
		}

	}
	
	public void close() {
		try {
			pstmt.close();
			con.close();
		} catch (Exception e) {
			System.out.println("Error en BaseDatos.close");
			e.printStackTrace();
		}
	}

	
	public ResultSet executeQuery() {
			try {
				ResultSet rs = pstmt.executeQuery();
				return rs;
			} catch (Exception e) {
				System.out.println("Error en la SQL de executeQuery");
				e.printStackTrace();
				return null;
			}
	}
	
	public int executeUpdate() {
			try {
				int i = pstmt.executeUpdate();
				return i;
			} catch (Exception e) {
				System.out.println("Error en la SQL de executeUpdate");
				e.printStackTrace();
				return -1;
			}
	}	
	
}












