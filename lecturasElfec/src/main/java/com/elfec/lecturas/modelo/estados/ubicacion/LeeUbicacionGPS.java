package com.elfec.lecturas.modelo.estados.ubicacion;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;

/**
 * Es el estado que corresponde al entero 2, en el se lee la ubicación por GPS
 * 
 * @author drodriguez
 *
 */
public class LeeUbicacionGPS implements IEstadoManejadorUbicacion {

	private LocationManager manejadorUbicacion;
	private LocationListener locationListener;

	LeeUbicacionGPS() {

	}

	private LeeUbicacionGPS(Context context) {
		this.manejadorUbicacion = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	public LeeUbicacionGPS crearEstado(Context context) {
		return new LeeUbicacionGPS(context);
	}

	@Override
	public boolean leeUbicacion() {
		return true;
	}

	@Override
	public boolean proveedorEstaHabilitado() {
		boolean gps_habilitado = false;
		try {
			gps_habilitado = manejadorUbicacion
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}
		return gps_habilitado;
	}

	@Override
	public void obtenerUbicacion(LocationListener locationListener) {
		this.locationListener = locationListener;
		manejadorUbicacion.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				0, 0, locationListener);
	}

	@Override
	public void deshabilitarServicio() {
		if (locationListener != null) {
			manejadorUbicacion.removeUpdates(locationListener);
		}
	}

}
