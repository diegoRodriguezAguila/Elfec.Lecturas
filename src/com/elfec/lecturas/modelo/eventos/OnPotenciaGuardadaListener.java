package com.elfec.lecturas.modelo.eventos;

import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.Potencia;

/**
 * Evento que se ejecuta al guardarse una potencia
 * 
 * @author drodriguez
 *
 */
public interface OnPotenciaGuardadaListener {
	/**
	 * Evento que se ejecuta al guardarse una potencia
	 * 
	 * @param lectura
	 * @param potencia
	 */
	public void onPotenciaGuardada(Lectura lectura, Potencia potencia);
}
