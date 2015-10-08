package com.elfec.lecturas.modelo.eventos;

import com.elfec.lecturas.modelo.Lectura;

/**
 * Evento que se ejecuta al guardar un recordatorio de una lectura
 * 
 * @author drodriguez
 *
 */
public interface OnRecordatorioGuardadoListener {
	/**
	 * Evento que se ejecuta al guardar un recordatorio de una lectura
	 * 
	 * @param lectura
	 */
	public void onRecordatorioGuardado(Lectura lectura);
}
