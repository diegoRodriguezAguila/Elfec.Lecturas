package com.lecturas.elfec.controlador.receptoreseventos;

import com.lecturas.elfec.controlador.TomarLectura;
import com.lecturas.elfec.helpers.ManejadorEstadosHW;
import com.lecturas.elfec.helpers.ManejadorUbicacion;
import com.lecturas.elfec.helpers.VariablesDeSesion;
import com.lecturas.elfec.modelo.Usuario;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class ReceptorEstadoServiciosUbicacion  extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		if(cn.getClassName().equals(TomarLectura.class.getName()))
		{
			if(Usuario.obtenerUsuario(VariablesDeSesion.getUsuarioLogeado()).RequiereGPS==1 
					&& !ManejadorEstadosHW.bateriaEstaEnNivelCritico(context.getApplicationContext()))
			{
				try
				{
					ManejadorUbicacion.verificarServiciosEstanActivos(context.getApplicationContext());
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

}
