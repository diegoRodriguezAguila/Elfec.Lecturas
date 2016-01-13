package com.elfec.lecturas.modelo.eventos;

import com.elfec.lecturas.modelo.MedidorEntreLineas;

/**
 * Evento que se ejecuta al guardar un medidor entre lineas
 * 
 * @author drodriguez
 *
 */
public interface OnMedidorEntreLineasGuardadoListener {
	/**
	 * Evento que se ejecuta al guardar un medidor entre lineas
	 * 
	 * @param medidorEntreLineas
	 */
	public void onMedidorEntreLineasGuardado(
			MedidorEntreLineas medidorEntreLineas);
}
