package com.lecturas.elfec.modelo.seguridad;

/**
 * Define las restricciones que son aplicables a los distintos perfiles de usuario.
 * @author drodriguez
 *
 */
public class Restricciones {
	/**
	 * Restricción de la cantidad maxima de veces que se puede modificar una lectura.
	 */
	public static final String MAX_MODIFICAR_LECTURA = "MAX_MODIFICAR_LECTURA";
	/**
	 * Restricción de la cantidad maxima de impresiones por lectura.
	 */
	public static final String MAX_IMPRESIONES_LECTURA = "MAX_IMPRESIONES_LECTURA";
}
