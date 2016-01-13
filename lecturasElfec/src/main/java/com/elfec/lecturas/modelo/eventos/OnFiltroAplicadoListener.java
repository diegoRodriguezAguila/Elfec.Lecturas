package com.elfec.lecturas.modelo.eventos;

import com.elfec.lecturas.controlador.filtroslecturas.FiltroLecturas;

/**
 * Evento que ocurre al seleccionar un filtro
 * 
 * @author drodriguez
 *
 */
public interface OnFiltroAplicadoListener {

	/**
	 * Evento que ocurre al seleccionar un filtro
	 * 
	 * @param filtroLecturas
	 */
	public void onFiltroAplicado(FiltroLecturas filtroLecturas);
}
