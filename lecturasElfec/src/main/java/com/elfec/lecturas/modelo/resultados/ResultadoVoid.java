package com.elfec.lecturas.modelo.resultados;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa el resultado de realizar una acción en un gestionador de la capa
 * de lógica de negocio u otra que lo requiera sin tipo de retorno, solo con
 * lista de errores
 * 
 * @author drodriguez
 *
 */
public class ResultadoVoid implements Serializable {

	protected List<Exception> listaErrores;

	public ResultadoVoid() {
		listaErrores = new ArrayList<Exception>();
	}

	/**
	 * Obtiene la lista de errores del resultado de un servicio web
	 * 
	 * @return Lista de errores del WS
	 */
	public List<Exception> getErrores() {
		return listaErrores;
	}

	/**
	 * Agrega un error a la lista de errores del resultado de un servicio web
	 * 
	 * @param e
	 */
	public void agregarError(Exception e) {
		listaErrores.add(e);
	}

	/**
	 * Agrega multiples errores a la lita de errores
	 * 
	 * @param errores
	 */
	public void agregarErrores(List<Exception> errores) {
		listaErrores.addAll(errores);
	}

	/**
	 * Si es que el resultado del acceso a datos tuvo errores
	 * 
	 * @return true si los hubo
	 */
	public boolean tieneErrores() {
		return listaErrores.size() > 0;
	}
}
