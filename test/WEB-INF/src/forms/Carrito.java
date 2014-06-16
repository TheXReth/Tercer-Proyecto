package forms;

import java.util.ArrayList;

public class Carrito {
	
	ArrayList<int[]> productos = new ArrayList<int[]>();
	
	private int idcarrito;
	
	public Carrito(int id){
		int[] aux = new int[2];
		aux[0] = id;
		aux[1] = 1;
		productos.add(aux);
		idcarrito = -1;
	}
	
	public void setId(int idcarrito) {
		this.idcarrito = idcarrito;
	}
	
	public int getId() {
		return idcarrito;
	}
		
	public void setProducto(int id){
		int[] aux = new int[2];
		if (repetido(id)){
			int pos = getPosicion(id);
			if (pos != -1) {
				aux = getElementoPorPos(pos);
				aux[1] = aux[1] + 1;
				productos.set(pos, aux);
			}
		} else {
			aux[0] = id;
			aux[1] = 1;
			productos.add(aux);
		}
		
	}
	
	public ArrayList<int[]> getElementosCarrito(){
		return productos;
	}
	
	public void removeProducto(int id){
		int pos = getPosicion(id);
		if (pos != -1) productos.remove(pos);
		else System.out.println("Tenemos a un listillo intentando borrar algo que no existe.");
	}
	
	public void removeCarrito(){
		productos.clear();
	}
	
	public void setMenosProducto(int id){
		int pos = getPosicion(id);
		if (pos != -1) {
			int[] aux = getElementoPorPos(pos);
			--aux[1];
			if (aux[1] <= 0) removeProducto(id);
			else productos.set(pos, aux);
		}
	}
	
	public void setCantidadProducto(int id, int cant){
		int pos = this.getPosicion(id);
		
		if (pos != -1) {
			int[] aux = getElementoPorPos(pos);
			aux[1] = cant;
			if (aux[1] < 0)	aux[1] *= -1;
			else {
				if (aux[1] == 0) removeProducto(id);
				else productos.set(pos, aux);
			}
		}
	}
	
	public int getPosicion(int id){
		int i = 0;

		int [] aux = new int[2];
		while (i < getNumElem()) {
			aux = productos.get(i);
			if (aux[0] == id)
				return i;
			i++;
		}
		return -1;
	}
	
	public int getNumElem(){
		return productos.size();
	}

	public int[] getElementoPorPos(int i){
		if (i >= 0 && i < getNumElem())
			return productos.get(i);
		else {
			System.out.println("Carrito.getIdElemento: Accediendo a posicion fuera de rango.");
			return null;
		}
	}
	
	public int getIdPos(int i){
		return getElementoPorPos(i)[0];
	}
	
	public boolean repetido(int id){
		int i = 0;
		boolean found = false;
		int[] aux = new int[2];
		while(!found && i < getNumElem()) {
			aux = productos.get(i);
			if (aux[0] == id) found = true;
			++i;
		}
		return found;
	}
	
	public int[] getIdProductos(){
		int[] listaIDs = new int[getNumElem()];
		for (int i = 0; i < getNumElem(); i++){
			listaIDs[i] = productos.get(i)[0];
		}
		return listaIDs;
	}
	
	public String getIdList(){
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < getNumElem(); i++){
			if (i != 0) sb.append(", ");
			sb.append(productos.get(i)[0]);
		}
		return sb.toString();
	}
	
	public String getCantList(){
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < getNumElem(); i++){
			if (i != 0) sb.append(", ");
			sb.append(productos.get(i)[1]);
		}
		return sb.toString();
	}

}


