package com.elfec.lecturas.modelo.validaciones;

import com.elfec.lecturas.modelo.Ordenativo;

/**
 * La interfaz de la que deben heredar los resultados de validaciones. 
 * El código asignado para las validaciones de lectura es <b>VL</b>.
 * @author drodriguez
 *
 */
public interface IValidacionLectura {
	/**
	 * Obtiene el codigo del resultado de la validacion de la lectura
	 * @return
	 */
	public String obtenerCodigo();
	/**
	 * Obtiene el mensaje del resultado de la validacion de la lectura
	 * @return
	 */
	public String obtenerMensaje();
	/**
	 * Inidica si es una advertencia o no, en caso de ser advertencia quiere decir que la validacion no fue exitosa
	 * @return
	 */
	public boolean esAdvertencia();
	/**
	 * Obtiene el ordenativo asociado al tipo de resultado de validacion de lectura obtenido. En caso de no tener uno
	 * devuelve null
	 * @return
	 */
	public Ordenativo obtenerOrdenativo();
}
