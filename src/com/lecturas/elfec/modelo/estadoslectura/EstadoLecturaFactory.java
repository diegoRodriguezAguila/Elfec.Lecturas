package com.lecturas.elfec.modelo.estadoslectura;

import java.util.ArrayList;
import java.util.List;

import android.util.SparseArray;

/**
 * Se encarga de construir un estado a partir de su Int
 * @author drodriguez
 *
 */
public class EstadoLecturaFactory {
	
	private static SparseArray<IEstadoLectura> estadosRegistrados = new SparseArray<IEstadoLectura>();

	/**
	 * Todas las clases de EstadoLectura que se vayan a utilizar deben ser agregadas aqui
	 */
	static
	{
		try {
			Class.forName("com.lecturas.elfec.modelo.estadoslectura.Pendiente");
			Class.forName("com.lecturas.elfec.modelo.estadoslectura.Leida");
			Class.forName("com.lecturas.elfec.modelo.estadoslectura.Impedida");
			Class.forName("com.lecturas.elfec.modelo.estadoslectura.Postergada");
			Class.forName("com.lecturas.elfec.modelo.estadoslectura.ReIntentar");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Asocia un int a una clase de estado que herede de la interfaz IEstadoLectura
	 * @param id
	 * @param estado
	 */
	public static void registrarEstado(int id, IEstadoLectura estado) {
		estadosRegistrados.put(id, estado);
	}
	
	/**
	 * Crea un estado a partir del entero proporcionado, si no existe una clase asociada a ese entero devuelve una excepcion de null
	 * @param id
	 * @return
	 */
	public static IEstadoLectura crearEstado(int id){
		return estadosRegistrados.get(id).crearEstado();
	}
	
	/**
	 * Obtiene una lista de los estados registrados de las lecturas
	 * @return lista de estados
	 */
	public static List<IEstadoLectura> obtenerEstadosRegistrados()
	{
		List<IEstadoLectura> listaEstados = new ArrayList<IEstadoLectura>();
		int tam = estadosRegistrados.size();
		for (int i = 0; i < tam; i++) 
		{
			listaEstados.add(estadosRegistrados.valueAt(i));
		}
		return listaEstados;
	}

}
