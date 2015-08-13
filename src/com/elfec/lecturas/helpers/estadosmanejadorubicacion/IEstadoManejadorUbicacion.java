package com.elfec.lecturas.helpers.estadosmanejadorubicacion;

import android.content.Context;
import android.location.LocationListener;


/**
 * Define el patron State de diseño, abstrae el estado del manejador de ubicacion
 * @author drodriguez
 *
 */
public interface IEstadoManejadorUbicacion 
{
	/**
	 * Define la creacion del estado, el constructor es privado, se utiliza para el EstadoManejadorUbicacionFactory
	 * @return El estado
	 */
	public IEstadoManejadorUbicacion crearEstado(Context context);
	/**
	 * Indica si se debe leer la ubicacion
	 * @return true, si se debe leer la ubicacion, false en caso contrario
	 */
	public boolean leeUbicacion();
	
	/**
	 * Indica si el proveedor de Ubicacion esta habilitado
	 * @return true, si el proveedor de ubicacion esta habilitado
	 */
	public boolean proveedorEstaHabilitado();
	
	/**
	 * Habilita el servicio para obtener la ubicacion, y utiliza el listener proporcionado para crear el servicio
	 */
	public void obtenerUbicacion(LocationListener locationListener);
	
	/**
	 * Deshabilita el servicio de obtencion de ubicacion
	 */
	public void deshabilitarServicio();
	
}
