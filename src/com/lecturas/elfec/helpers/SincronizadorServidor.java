package com.lecturas.elfec.helpers;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.lecturas.elfec.helpers.validacionessincronizacion.DesincronizadoDeServidor;
import com.lecturas.elfec.helpers.validacionessincronizacion.ErrorConexionServidor;
import com.lecturas.elfec.helpers.validacionessincronizacion.IEstadoSincronizacion;
import com.lecturas.elfec.helpers.validacionessincronizacion.SincronizacionCorrecta;

import android.content.Context;

/**
 * Se encarga del proceso de sincronización con el servidor
 * @author drodriguez
 *
 */
public class SincronizadorServidor {

	/**
	 * Intenta obtener la fecha del servidor y la compara con la fecha actual del movil y en función a eso 
	 * se devuelven los distintos tipos de resultados de sincronizacion, las clases que implementan la interfaz
	 * IEstadoSincronizacion
	 * @param context
	 * @return
	 */
	public static IEstadoSincronizacion verificarSincronizacionFechaYHoraConServidor(Context context)
	{
		ConectorBDOracle conexion = new ConectorBDOracle(context, false);
		try {
			Date fechaServidor = conexion.obtenerFechaDelServidor();
			Calendar calendarServidor = Calendar.getInstance();
			calendarServidor.setTime(fechaServidor);
			Calendar calendarTelefono = Calendar.getInstance();
			if(calendarServidor.get(Calendar.YEAR)==calendarTelefono.get(Calendar.YEAR)
			   && calendarServidor.get(Calendar.MONTH)==calendarTelefono.get(Calendar.MONTH)
			   && calendarServidor.get(Calendar.DAY_OF_MONTH)==calendarTelefono.get(Calendar.DAY_OF_MONTH)
			   && calendarServidor.get(Calendar.HOUR_OF_DAY)==calendarTelefono.get(Calendar.HOUR_OF_DAY)
			   && calendarTelefono.get(Calendar.MINUTE)<=(calendarServidor.get(Calendar.MINUTE)+5)
			   && calendarTelefono.get(Calendar.MINUTE)>=(calendarServidor.get(Calendar.MINUTE)-5))
			{
				return new SincronizacionCorrecta(fechaServidor);
			}
			else return new DesincronizadoDeServidor(fechaServidor);
		} catch (SQLException e) {
			return new ErrorConexionServidor();
		} catch (ParseException e) {
			return new ErrorConexionServidor();
		}
		catch (Exception e)
		{
			return new ErrorConexionServidor();
		}
	}
}
