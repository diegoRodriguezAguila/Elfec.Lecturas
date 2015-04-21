package com.lecturas.elfec.helpers.estadosmanejadorubicacion;

import android.content.Context;
import android.location.LocationListener;

/**
 * Es el estado que corresponde al entero 0, en el no se lee la ubicación y por tanto el evento que se ejecutaria
 * al recibir la ubicacion no se ejecuta
 * @author drodriguez
 *
 */
public class NoLeeUbicacion implements IEstadoManejadorUbicacion {

	static
	{
		EstadoManejadorUbicacionFactory.registrarEstado(0,new NoLeeUbicacion());
	}
	
	
	@Override
	public NoLeeUbicacion crearEstado( Context context) {
		return new NoLeeUbicacion();
	}

	@Override
	public boolean leeUbicacion() {
		return false;
	}

	@Override
	public boolean proveedorEstaHabilitado() {
		return true;
	}

	@Override
	public void obtenerUbicacion(LocationListener locationListener) {
	}

	@Override
	public void deshabilitarServicio() {
		
	}

}
