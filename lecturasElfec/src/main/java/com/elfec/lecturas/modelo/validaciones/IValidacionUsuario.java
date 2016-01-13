package com.elfec.lecturas.modelo.validaciones;

/**
 * La interfaz que deben implementar los resultados de validaciones. 
 * El código asignado para las validaciones de usuario es <b>VU</b>.
 * @author drodriguez
 *
 */
public interface IValidacionUsuario {
	/**
	 * Obtiene el codigo del resultado de la validación del usuario
	 * @return
	 */
	public String obtenerCodigo();
	/**
	 * Obtiene el mensaje del resultado de la validación del usuario
	 * @return
	 */
	public String obtenerMensaje();
	/**
	 * Indica si el resultado de la validación de usuario es un error, es decir que la validación no fue correcta
	 * @return
	 */
	public boolean esError();
}
