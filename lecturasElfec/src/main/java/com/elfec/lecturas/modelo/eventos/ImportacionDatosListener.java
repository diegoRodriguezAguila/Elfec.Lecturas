package com.elfec.lecturas.modelo.eventos;

import com.elfec.lecturas.modelo.resultados.ResultadoVoid;

/**
 * Listener para los eventos que ocurren en la importación de datos
 * 
 * @author drodriguez
 *
 */
public interface ImportacionDatosListener {
	/**
	 * La función que se llama cuando se inicializó una importación de datos
	 * 
	 * @param result
	 */
	public void onImportacionIniciada();

	/**
	 * La función que se llama cuando finalizó un evento de importación de datos
	 * 
	 * @param result
	 */
	public void onImportacionFinalizada(ResultadoVoid result);
}
