package com.elfec.lecturas.servicios.receivers;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;

import com.elfec.lecturas.R;
import com.elfec.lecturas.controlador.observers.IDataImportationObserver;
import com.elfec.lecturas.modelo.resultados.ResultadoVoid;
import com.elfec.lecturas.servicios.ServicioImportacionDatos;

/**
 * Servicio de presenter que se encarga de escuchar los mensajes del servicio de
 * importación de datos su interacción con la vista
 * 
 * @author drodriguez
 *
 */
public class DataImportationReceiver extends BroadcastReceiver {

	private List<IDataImportationObserver> observers;
	private Context callerContext;

	public DataImportationReceiver(List<IDataImportationObserver> observers,
			Context callerContext) {
		this.observers = observers;
		this.callerContext = callerContext;
	}

	/**
	 * Inicia a recibir mensajes
	 */
	public void startReceiving() {
		HandlerThread handlerThread = new HandlerThread(
				"DataImportationReceiverThread");
		handlerThread.start();
		callerContext.registerReceiver(this, new IntentFilter(
				ServicioImportacionDatos.BROADCAST_ACTION), null, new Handler(
				handlerThread.getLooper()));
	}

	/**
	 * Deja de recibir mensajes
	 */
	public void stopReceiving() {
		callerContext.unregisterReceiver(this);
	}

	@Override
	public void onReceive(Context context, final Intent intent) {
		if (observers != null) {// is not disposed
			int action = intent.getIntExtra("action", -1);
			switch (action) {
			case ServicioImportacionDatos.IMPORTATION_STARTING: {
				importationStarting();
				break;
			}
			case ServicioImportacionDatos.UPDATE_WAITING: {
				updateWaiting(intent.getIntExtra("message", 0));
				break;
			}
			case ServicioImportacionDatos.IMPORTATION_FINISHED: {
				importationFinished((ResultadoVoid) intent
						.getSerializableExtra("result"));
				break;
			}
			default:
				break;
			}
		}

	}

	/**
	 * Notifica al usuario en la UI que empezó la importación de datos
	 */
	private void importationStarting() {
		for (IDataImportationObserver observer : observers) {
			observer.showImportationWaiting();
		}

	}

	/**
	 * Realiza la actualización del mensaje de espera a la interfaz
	 * 
	 * @param msgStrId
	 */
	private void updateWaiting(int msgStrId) {
		for (IDataImportationObserver observer : observers) {
			observer.updateImportationWaiting(msgStrId);
		}
	}

	/**
	 * Notifica al usuario de que el evento de importación finalizó
	 * 
	 * @param result
	 */
	private void importationFinished(ResultadoVoid result) {
		stopReceiving();
		for (IDataImportationObserver observer : observers) {
			observer.hideWaiting();
			observer.showErrors(R.string.title_import_data_error,
					R.drawable.error_import_from_server, result.getErrores());
			if (!result.tieneErrores()) {
				observer.notifySuccessfulImportation();
			}
		}
	}
}
