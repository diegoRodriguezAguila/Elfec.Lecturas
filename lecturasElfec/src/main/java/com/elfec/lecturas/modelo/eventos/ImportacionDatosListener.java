package com.elfec.lecturas.modelo.eventos;

import com.elfec.lecturas.modelo.resultados.ResultadoVoid;

/**
 * Listener para los eventos que ocurren en la importaci�n de datos
 * 
 * @author drodriguez
 *
 */
public interface ImportacionDatosListener {
	/**
	 * La funci�n que se llama cuando se inicializ� una importaci�n de datos
	 * 
	 * @param result
	 */
	public void onImportacionIniciada();

	/**
	 * La funci�n que se llama cuando finaliz� un evento de importaci�n de datos
	 * 
	 * @param result
	 */
	public void onImportacionFinalizada(ResultadoVoid result);
}
