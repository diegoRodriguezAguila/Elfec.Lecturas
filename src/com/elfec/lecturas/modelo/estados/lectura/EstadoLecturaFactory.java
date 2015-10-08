package com.elfec.lecturas.modelo.estados.lectura;

import java.util.ArrayList;
import java.util.List;

import android.util.SparseArray;

/**
 * Se encarga de construir un estado a partir de su Int
 * 
 * @author drodriguez
 *
 */
public class EstadoLecturaFactory {

	private static SparseArray<IEstadoLectura> estadosRegistrados;

	/**
	 * Todas las clases de EstadoLectura que se vayan a utilizar deben ser
	 * agregadas aqui
	 */
	static {
		estadosRegistrados = new SparseArray<IEstadoLectura>();
		registrarEstado(0, new Pendiente());
		registrarEstado(1, new Leida());
		registrarEstado(2, new Impedida());
		registrarEstado(3, new Postergada());
		registrarEstado(4, new ReIntentar());
	}

	/**
	 * Asocia un int a una clase de estado que herede de la interfaz
	 * IEstadoLectura
	 * 
	 * @param id
	 * @param estado
	 */
	public static void registrarEstado(int id, IEstadoLectura estado) {
		estadosRegistrados.put(id, estado);
	}

	/**
	 * Crea un estado a partir del entero proporcionado, si no existe una clase
	 * asociada a ese entero devuelve una excepcion de null
	 * 
	 * @param id
	 * @return
	 */
	public static IEstadoLectura crearEstado(int id) {
		return estadosRegistrados.get(id).crearEstado();
	}

	/**
	 * Obtiene una lista de los estados registrados de las lecturas
	 * 
	 * @return lista de estados
	 */
	public static List<IEstadoLectura> obtenerEstadosRegistrados() {
		List<IEstadoLectura> listaEstados = new ArrayList<IEstadoLectura>();
		int tam = estadosRegistrados.size();
		for (int i = 0; i < tam; i++) {
			listaEstados.add(estadosRegistrados.valueAt(i));
		}
		return listaEstados;
	}

}
