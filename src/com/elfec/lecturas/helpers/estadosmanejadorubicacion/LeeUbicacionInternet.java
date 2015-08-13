package com.elfec.lecturas.helpers.estadosmanejadorubicacion;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Es el estado que corresponde al entero 1, se lee la ubicación por 3g, en caso de no haber conexión a internet se utiliza el gps
 * @author drodriguez
 *
 */
public class LeeUbicacionInternet implements IEstadoManejadorUbicacion, ConnectionCallbacks, OnConnectionFailedListener {

	private Context context;
	private LocationListener locationListener;
	private LocationClient locationclient;
	private LocationRequest locationrequest;
	private int contVeces;
	
	static
	{
		EstadoManejadorUbicacionFactory.registrarEstado(1,new LeeUbicacionInternet());
	}
	
	private LeeUbicacionInternet()
	{
		contVeces = 0;
	}
	
	private LeeUbicacionInternet(Context context)
	{
		locationclient = new LocationClient(context,this,this);
		locationrequest = LocationRequest.create();
		locationrequest.setInterval(5000);
		locationrequest.setNumUpdates(3);
		locationrequest.setFastestInterval(5000);
		locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		this.context = context;
		contVeces = 0;
	}
	
	@Override
	public LeeUbicacionInternet crearEstado(Context context) {
		return new LeeUbicacionInternet(context);
	}

	@Override
	public boolean leeUbicacion() {
		return true;
	}

	@Override
	public boolean proveedorEstaHabilitado() {
		boolean google_habilitado = false, gps_habilitado = false, red_habilitada = false;
		try{
			LocationManager manejadorUbicacion = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			google_habilitado=GooglePlayServicesUtil.isGooglePlayServicesAvailable(context)==ConnectionResult.SUCCESS;
			gps_habilitado=manejadorUbicacion.isProviderEnabled(LocationManager.GPS_PROVIDER);
			red_habilitada=manejadorUbicacion.isProviderEnabled(LocationManager.GPS_PROVIDER);
			}catch(Exception ex){}
		return google_habilitado && gps_habilitado && red_habilitada;
	}

	@Override
	public void obtenerUbicacion(final android.location.LocationListener classicLocationListener) {
		this.locationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				contVeces++;
				if(contVeces==3)
				{
					classicLocationListener.onLocationChanged(location);
				}
			}
		};
		locationclient.connect();
	}

	@Override
	public void deshabilitarServicio() {
		if(locationListener!=null)
		{
			locationclient.removeLocationUpdates(locationListener);
		}
		locationclient.disconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {

	}

	@Override
	public void onConnected(Bundle bundle) {
		locationclient.requestLocationUpdates(locationrequest, locationListener);
	}

	@Override
	public void onDisconnected() {
		
	}

}
