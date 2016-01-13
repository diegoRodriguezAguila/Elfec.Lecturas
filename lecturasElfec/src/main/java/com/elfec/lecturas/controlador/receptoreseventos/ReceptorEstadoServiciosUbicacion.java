package com.elfec.lecturas.controlador.receptoreseventos;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.elfec.lecturas.controlador.TomarLectura;
import com.elfec.lecturas.helpers.ManejadorEstadosHW;
import com.elfec.lecturas.helpers.ManejadorUbicacion;
import com.elfec.lecturas.modelo.Usuario;
import com.elfec.lecturas.settings.VariablesDeSesion;

public class ReceptorEstadoServiciosUbicacion extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		@SuppressWarnings("deprecation")
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		if (cn.getClassName().equals(TomarLectura.class.getName())) {
			if (Usuario.obtenerUsuario(VariablesDeSesion.getUsuarioLogeado()).RequiereGPS == 1
					&& !ManejadorEstadosHW.bateriaEstaEnNivelCritico(context
							.getApplicationContext())) {
				try {
					ManejadorUbicacion.verificarServiciosEstanActivos(context
							.getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
