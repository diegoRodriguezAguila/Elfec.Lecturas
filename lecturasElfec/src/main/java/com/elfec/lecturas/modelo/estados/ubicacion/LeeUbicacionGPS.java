package com.elfec.lecturas.modelo.estados.ubicacion;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Es el estado que corresponde al entero 2, en el se lee la ubicación por GPS
 * 
 * @author drodriguez
 *
 */
public class LeeUbicacionGPS implements IEstadoManejadorUbicacion, GoogleApiClient
        .ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

	private Context mContext;
	private LocationListener locationListener;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest locationrequest;
	private int contVeces;

	LeeUbicacionGPS() {
		contVeces = 0;
	}

	private LeeUbicacionGPS(Context context) {
        this.mContext = context;
        contVeces = 0;
		mGoogleApiClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API)
				.addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
		locationrequest = LocationRequest.create();
		locationrequest.setInterval(5000);
		locationrequest.setNumUpdates(3);
		locationrequest.setFastestInterval(5000);
		locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mGoogleApiClient.connect();
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
		boolean google_habilitado = false, gps_habilitado = false, red_habilitada = false;
		try {
			LocationManager manejadorUbicacion = (LocationManager) mContext
					.getSystemService(Context.LOCATION_SERVICE);
			google_habilitado = GooglePlayServicesUtil
					.isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS;
			gps_habilitado = manejadorUbicacion
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			red_habilitada = manejadorUbicacion
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}
		return google_habilitado && gps_habilitado && red_habilitada;
	}

	@Override
	public void obtenerUbicacion(
			final android.location.LocationListener classicLocationListener) {
		this.locationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				contVeces++;
				if (contVeces == 3) {
					classicLocationListener.onLocationChanged(location);
				}
			}
		};
		mGoogleApiClient.connect();
	}

	@Override
	public void deshabilitarServicio() {
		if (locationListener != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                    locationListener);
		}
        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
	}


	@Override
	public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                locationrequest,
                locationListener);
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
