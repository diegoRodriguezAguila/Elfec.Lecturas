package com.elfec.lecturas.modelo.excepciones;

import org.joda.time.DateTime;

/**
 * Excepción que se lanza cuando no existe ninguna ruta asignada para el día
 * indicado
 * 
 * @author drodriguez
 *
 */
public class NoHayRutasAsignadasException extends Exception {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -5652686360710549296L;
	private String username;

	public NoHayRutasAsignadasException(String username) {
		this.username = username;
	}

	@Override
	public String getMessage() {
		return "El usuario <b>"
				+ username
				+ "</b> no tiene ninguna ruta asignada para la fecha de hoy <b>"
				+ DateTime.now().toString("dd/MM/yyyy")
				+ "</b> o ya cargó las rutas que se le asignaron.";
	}
}
