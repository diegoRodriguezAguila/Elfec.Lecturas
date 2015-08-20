package com.elfec.lecturas.modelo.eventos;

import com.elfec.lecturas.modelo.OrdenativoLectura;

/**
 * Evento que se ejecuta al guardarse una observaci�n
 * 
 * @author drodriguez
 *
 */
public interface OnObservacionGuardadaListener {
	/**
	 * Se ejecuta al guardarse una observaci�n exitosamente
	 * 
	 * @param ordenativoLectura
	 */
	public void onObservacionGuardada(OrdenativoLectura ordenativoLectura);
}
