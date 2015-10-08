package com.elfec.lecturas.helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.elfec.lecturas.settings.VariablesDeEntorno;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.provider.Settings;

/**
 * Se encarga de verifica y cambiar los estados de los dispositivos de HW como los datos moviles, el GPS y la bateria
 * @author drodriguez
 *
 */
public class ManejadorEstadosHW {

	/**
	 * Verifica el estado del servicio de ubicacion de GPS, verifica que la opcion este activada o no,
	 * no verifica conectividad ni ningun otro tipo de estado.
	 * @param context
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean servicioGPSEstaDeshabilitado(Context context)
	{
		return !Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED).contains("gps");
	}
	/**
	 * Verifica el estado de los datos 3g y si estan desactivados retorna true, cualquier otro
	 * estado es false, haya conexiona  internet o no, solo verifica que la opcion este o no activada.
	 * @param context
	 * @return
	 */
	public static boolean datosMoviles3GEstanDesconectados(Context context)
	{
		final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
		final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return mobile.getState()==android.net.NetworkInfo.State.DISCONNECTED;
	}
	
	/**
	 * Enciende los datos moviles 3G
	 * @param context
	 * @return
	 */
	public static boolean encenderDatosMoviles3G(Context context)
	{
		ConnectivityManager dataManager;
		dataManager  = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		Method dataMtd;
		try {
			dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
			dataMtd.setAccessible(true);
			dataMtd.invoke(dataManager, true); 
			return true;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Obtiene el porcentaje de bateria restante del dispositivo.
	 * @param context
	 * @return
	 */
	public static float obtenerNivelBateria(Context context) {
	    Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	    int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
	    int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
	    if(level == -1 || scale == -1) {
	        return 50.0f;
	    }
	    return ((float)level / (float)scale) * 100.0f;
	}
	
	/**
	 * Verifica si la bateria esta por debajo del nivel critico establecido en las VariablesDeEntorno
	 * @param context
	 * @see VariablesDeEntorno
	 * @return
	 */
	public static boolean bateriaEstaEnNivelCritico(Context context)
	{
		return obtenerNivelBateria(context)<=VariablesDeEntorno.nivelBateriaCritico;
	}
}
