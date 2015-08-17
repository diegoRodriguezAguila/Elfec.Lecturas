package com.elfec.lecturas.modelo.excepciones;

import com.elfec.lecturas.modelo.AsignacionRuta;

/**
 * Excepción que se lanza cuando no se encontraron lecturas para la asignación
 * de ruta
 * 
 * @author drodriguez
 *
 */
public class RutaAsignadaSinLecturasException extends Exception {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 6837574751992918201L;
	private AsignacionRuta assignedRoute;

	public RutaAsignadaSinLecturasException(AsignacionRuta assignedRoute) {
		this.assignedRoute = assignedRoute;
	}

	@Override
	public String getMessage() {
		return "No existen lecturas de la ruta <b>"
				+ assignedRoute.Ruta
				+ "</b>  en la fecha del rol: <b>"
				+ assignedRoute.getFechaCronograma().toString("dd/MM/yyyy")
				+ "</b>. Si tiene problemas al realizar la carga solicite la desasignación de dicha ruta al administrador.";
	}
}
