package com.elfec.lecturas.modelo.estados.ubicacion;

import android.content.Context;
import android.util.SparseArray;

/**
 * Se encarga de construir un estado a partir de su Int
 * 
 * @author drodriguez
 *
 */
public class EstadoManejadorUbicacionFactory {

	private static SparseArray<IEstadoManejadorUbicacion> estadosRegistrados = new SparseArray<IEstadoManejadorUbicacion>();
	/**
	 * Todas las clases de EstadoManejadorUbicacion que se vayan a utilizar
	 * deben ser agregadas aqui
	 */
	static {
		registrarEstado(0, new NoLeeUbicacion());
		registrarEstado(1, new LeeUbicacionGPS());
	}

	/**
	 * 
	 * @param id
	 *            , el entero que representa este estado, se deberia usar el
	 *            parametro obtenido de VariablesDeEntorno.tipoGuardadoUbicacion
	 * @param estado
	 *            , el estado que corresponde a ese tipo de ubicacion
	 */
	public static void registrarEstado(int id, IEstadoManejadorUbicacion estado) {
		estadosRegistrados.put(id, estado);
	}

	/**
	 * 
	 * @param id
	 *            , el entero que representa este estado, se deberia usar el
	 *            parametro obtenido de VariablesDeEntorno.tipoGuardadoUbicacion
	 * @return el estado correspondiente a ese entero
	 */
	public static IEstadoManejadorUbicacion crearEstado(int id, Context context) {
		return estadosRegistrados.get(id).crearEstado(context);
	}
}
