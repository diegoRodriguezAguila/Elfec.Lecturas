package com.lecturas.elfec.controlador.receptoreseventos;

import com.lecturas.elfec.controlador.TomarLectura;
import com.lecturas.elfec.helpers.ManejadorEstadosHW;
import com.lecturas.elfec.helpers.VariablesDeSesion;
import com.lecturas.elfec.modelo.Usuario;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
public class ReceptorEstado3GDatosMoviles extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(ManejadorEstadosHW.datosMoviles3GEstanDesconectados(context))
		{
			try
			{
				ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
				ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
				if(cn.getClassName().equals(TomarLectura.class.getName()))
				{
					if(Usuario.obtenerUsuario(VariablesDeSesion.getUsuarioLogeado()).Requiere3G==1 
							&& !ManejadorEstadosHW.bateriaEstaEnNivelCritico(context.getApplicationContext()))
			  		{
						
							Toast.makeText(context, "Usted no tiene permiso para apagar el 3G!", Toast.LENGTH_SHORT).show();
				  			ManejadorEstadosHW.encenderDatosMoviles3G(context);
					}
			  	}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

}
