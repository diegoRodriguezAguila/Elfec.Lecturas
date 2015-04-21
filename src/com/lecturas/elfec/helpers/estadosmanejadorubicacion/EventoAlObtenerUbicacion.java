package com.lecturas.elfec.helpers.estadosmanejadorubicacion;

import android.location.Location;

/**
 * Este evento se utiliza para que el manejador de ubicacion ejecute una tarea una vez encontrada la ubicacion
 * @author drodriguez
 *
 */
public interface EventoAlObtenerUbicacion {
	/**
	 * El metodo que se ejecuta al haber obtenido la ubicacion
	 */
	public void ejecutarTarea(Location ubicacionObtenida);
	
	/**
	 * El metodo que se ejecuta en caso de no haber podido obtener la ubicación, es decir que se cumplio el timeout
	 */
	public void ejecturaSiTimeout();
}
