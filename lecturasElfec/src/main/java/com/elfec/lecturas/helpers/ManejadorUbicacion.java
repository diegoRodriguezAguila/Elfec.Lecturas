package com.elfec.lecturas.helpers;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.elfec.lecturas.R;
import com.elfec.lecturas.modelo.Usuario;
import com.elfec.lecturas.modelo.estados.ubicacion.EstadoManejadorUbicacionFactory;
import com.elfec.lecturas.modelo.estados.ubicacion.IEstadoManejadorUbicacion;
import com.elfec.lecturas.modelo.eventos.EventoAlObtenerUbicacion;
import com.elfec.lecturas.settings.VariablesDeEntorno;
import com.elfec.lecturas.settings.VariablesDeSesion;
import com.google.android.gms.location.LocationListener;

/**
 * Se encarga de realizar las conexiones con satelite o 3g para obtener la
 * ubicaciÃ³n
 * 
 * @author drodriguez
 *
 */
public class ManejadorUbicacion {
	private static Location ultimaUbicacion;
	private static EventoAlObtenerUbicacion ultimoEvento;

	/**
	 * Obtiene la ubicacion actual dependiendo de la variable parametrizable
	 * tipoGuardadoUbicacion, se usarÃ¡ distinitos medios para obtener la
	 * ubicacion o no se obtendra en el caso de que sea NoLeeUbicacion
	 * 
	 * @param context
	 *            , la actividad desde donde se llama al metodo
	 * @param evento
	 *            , evento que se lanzarÃ¡ una vez encontrada la ubicacion
	 */
	public static void obtenerUbicacionActual(final Context context,
			final EventoAlObtenerUbicacion evento) {
		obtenerUbicacionActual(context, evento,
				VariablesDeEntorno.tipoGuardadoUbicacion);
	}

	/**
	 * Verifica que los servicios de ubicaciÃ³n esten activos segÃºn el parametro
	 * de tipo de guardado de ubicaciÃ³n de la tabla de parametros, en caso de
	 * que no esten activos, muestra el dialogo
	 * 
	 * @param context
	 */
	public static void verificarServiciosEstanActivos(Context context) {
		verificarServiciosEstanActivos(context,
				VariablesDeEntorno.tipoGuardadoUbicacion);
	}

	/**
	 * Verifica que los servicios de ubicaciÃ³n esten activos segÃºn el parametro
	 * de tipo de guardado de ubicaciÃ³n pasado, en caso de que no esten activos,
	 * muestra el dialogo para activar los servicios de ubicaciÃ³n
	 * 
	 * @param context
	 * @param tipoGuardadoUbicacion
	 */
	public static void verificarServiciosEstanActivos(Context context,
			int tipoGuardadoUbicacion) {
		final IEstadoManejadorUbicacion estadoManejadorUbicacion = EstadoManejadorUbicacionFactory
				.crearEstado(tipoGuardadoUbicacion, context);
		if (estadoManejadorUbicacion.leeUbicacion()
				&& !ManejadorEstadosHW.bateriaEstaEnNivelCritico(context)) {

			if (!estadoManejadorUbicacion.proveedorEstaHabilitado()) {
				Toast.makeText(context, R.string.servicios_ubicacion_apagados,
						Toast.LENGTH_LONG).show();
				Intent gpsOptionsIntent = new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				gpsOptionsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(gpsOptionsIntent);
			}
		}
	}

	/**
	 * Obtiene la ubicacion actual dependiendo del parÃ¡metro
	 * tipoGuardadoUbicacion, se usarÃ¡ distinitos medios para obtener la
	 * ubicacion o no se obtendra en el caso de que sea NoLeeUbicacion
	 * 
	 * @param context
	 *            , la actividad desde donde se llama al metodo
	 * @param evento
	 *            , evento que se lanzarÃ¡ una vez encontrada la ubicacion
	 */
	public static void obtenerUbicacionActual(final Context context,
			final EventoAlObtenerUbicacion evento, int tipoGuardadoUbicacion) {
		final IEstadoManejadorUbicacion estadoManejadorUbicacion = EstadoManejadorUbicacionFactory
				.crearEstado(tipoGuardadoUbicacion, context);
		if (estadoManejadorUbicacion.leeUbicacion()
				&& !ManejadorEstadosHW.bateriaEstaEnNivelCritico(context)) {
			if (!estadoManejadorUbicacion.proveedorEstaHabilitado()
					&& Usuario.obtenerUsuario(VariablesDeSesion
							.getUsuarioLogeado()).RequiereGPS == 1) {
				Toast.makeText(context, R.string.servicios_ubicacion_apagados,
						Toast.LENGTH_LONG).show();
				Intent gpsOptionsIntent = new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				context.startActivity(gpsOptionsIntent);
			}
			final Handler timeoutController = new Handler();
			final Runnable timeoutRun = new Runnable() {

				@Override
				public void run() {
					try {
						estadoManejadorUbicacion.deshabilitarServicio();
						evento.ejecturaSiTimeout();
					} catch (Exception e) {
					}
				}
			};
			LocationListener locationListener = new LocationListener() {
				@Override
				public void onLocationChanged(Location location) {
					if (evento != null
							&& !(eventosSonIguales(evento, ultimoEvento) && (ubicacionesSonIguales(
									location, ultimaUbicacion)))) {
						evento.ejecutarTarea(location);
						if (ultimaUbicacion == null)
							ultimaUbicacion = location;
						else {
							ultimaUbicacion.setLatitude(location.getLatitude());
							ultimaUbicacion.setLongitude(location
									.getLongitude());
						}
						ultimoEvento = evento;
						// Toast.makeText(context,
						// "Longitud: "+location.getLongitude()+" Latitud: "+location.getLatitude(),
						// Toast.LENGTH_SHORT).show();
						Log.d("GPS PROVIDER",
								"Longitud: " + location.getLongitude()
										+ " Latitud: " + location.getLatitude());
					}
					timeoutController.removeCallbacks(timeoutRun);
					estadoManejadorUbicacion.deshabilitarServicio();
				}
			};

			estadoManejadorUbicacion.obtenerUbicacion(locationListener);
			timeoutController.postDelayed(timeoutRun,
					VariablesDeEntorno.timeoutGuardadoUbicacion); // pongo en
																	// marcha el
																	// timeout
		}
	}

	/**
	 * Verifica si 2 de los eventos son iguales
	 * 
	 * @param ev1
	 * @param ev2
	 * @return
	 */
	private static boolean eventosSonIguales(EventoAlObtenerUbicacion ev1,
			EventoAlObtenerUbicacion ev2) {
		if (ev1 == null || ev2 == null)
			return false;
		return ev1.equals(ev2);
	}

	/**
	 * Verifica si dos ubicaciones son iguales en longitud y latitud
	 * 
	 * @param ub1
	 * @param ub2
	 * @return
	 */
	private static boolean ubicacionesSonIguales(Location ub1, Location ub2) {
		if (ub1 == null || ub2 == null)
			return false;
		return ub1.getLongitude() == ub2.getLongitude()
				&& ub1.getLatitude() == ub2.getLatitude();
	}

}
