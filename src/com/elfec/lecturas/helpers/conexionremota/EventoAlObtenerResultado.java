package com.elfec.lecturas.helpers.conexionremota;


/**
 * Define el evento que se ejecuta una vez finalizada la llamada asincrona a un servicio web
 * @author drodriguez
 *
 */
public interface EventoAlObtenerResultado {

	/**
	 * El metodo que se ejecuta al haber obtenido el resultado de la conexion asincrona, del servicio web 3G invocado
	 */
	public void procesarResultado(double resultado);
}
