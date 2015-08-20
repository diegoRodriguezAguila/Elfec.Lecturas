package com.elfec.lecturas.modelo.eventos;

import com.elfec.lecturas.modelo.OrdenativoLectura;

/**
 * Evento que se ejecuta al guardarse una observación
 * 
 * @author drodriguez
 *
 */
public interface OnObservacionGuardadaListener {
	/**
	 * Se ejecuta al guardarse una observación exitosamente
	 * 
	 * @param ordenativoLectura
	 */
	public void onObservacionGuardada(OrdenativoLectura ordenativoLectura);
}
