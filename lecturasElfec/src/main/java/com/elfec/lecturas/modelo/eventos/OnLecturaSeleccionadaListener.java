package com.elfec.lecturas.modelo.eventos;

import com.elfec.lecturas.modelo.Lectura;

/**
 * Evento que se ejecuta al seleccionar una lectura
 * 
 * @author drodriguez
 *
 */
public interface OnLecturaSeleccionadaListener {
	/**
	 * Evento que se ejecuta al seleccionar una lectura
	 * 
	 * @param lectura
	 */
	public void onLecturaSeleccionada(Lectura lectura);
}
