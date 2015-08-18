package com.elfec.lecturas.modelo.validaciones.sincronizacion;

import java.util.Date;

/**
 * La interfaz que deben implementar los resultados de validaciones de sincronizacion con el servidor. 
 * El c�digo asignados para las validaciones de sincronizaion es <b>VS</b>.
 * @author drodriguez
 *
 */
public interface IEstadoSincronizacion {

	/**
	 * Obtiene el codigo del resultado de la sincronizaci�n
	 * @return
	 */
	public String obtenerCodigo();
	/**
	 * Obtiene el mensaje del resultado de la sincronizaci�n
	 * @return
	 */
	public String obtenerMensaje();
	/**
	 * Indica si el resultado de la sincronizaci�n fue erroneo, es decir que no la sincronizaci�n no fue correcta
	 * @return
	 */
	public boolean esError();
	/**
	 * Obtiene la fecha de sincronizaci�n obtenida, en caso de no haberla podido obtener devuelve null
	 * @return
	 */
	public Date obtenerFechaSinc();
}
