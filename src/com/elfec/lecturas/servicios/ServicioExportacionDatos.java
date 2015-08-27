package com.elfec.lecturas.servicios;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.elfec.lecturas.R;
import com.elfec.lecturas.acceso_remoto_datos.ConectorBDOracle;
import com.elfec.lecturas.logica_negocio.AsignacionRutaManager;
import com.elfec.lecturas.logica_negocio.EliminacionDatosManager;
import com.elfec.lecturas.logica_negocio.LecturasManager;
import com.elfec.lecturas.logica_negocio.MedidoresEntreLineasManager;
import com.elfec.lecturas.logica_negocio.OrdenativosManager;
import com.elfec.lecturas.modelo.AsignacionRuta;
import com.elfec.lecturas.modelo.eventos.ExportacionDatosListener;
import com.elfec.lecturas.modelo.resultados.ResultadoTipado;
import com.elfec.lecturas.modelo.resultados.ResultadoVoid;

/**
 * Servicio android que corre en segundo plano para realizar la exportación de
 * datos al servidor, notifica eventos de UI a
 * {@link DataImportationReceiverPresenter}
 * 
 * @author drodriguez
 *
 */
public class ServicioExportacionDatos extends Service {
	/**
	 * Acción del broadcast
	 */
	public static final String BROADCAST_ACTION = "com.elfec.lecturas.exportEvent";
	/**
	 * Mensaje de inicio de exportación
	 */
	public static final int EXPORTATION_STARTING = 1;
	/**
	 * Mensaje de actualización de un paso de exportación
	 */
	public static final int UPDATE_WAITING = 2;
	/**
	 * Mensaje de actualización del progreso de exportación
	 */
	public static final int UPDATE_PROGRESS = 3;
	/**
	 * Mensajde de fin de la exportación
	 */
	public static final int EXPORTATION_FINISHED = 4;

	private int strMsgId;

	private Intent intent;

	@Override
	public void onCreate() {
		super.onCreate();
		intent = new Intent(BROADCAST_ACTION);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				sendExportationMessage(EXPORTATION_STARTING);
				ExportacionDatosListener exportListener = new ExportacionDatosListener() {
					@Override
					public void onExportacionInicializada(int totalElements) {
						sendExportationMessage(UPDATE_WAITING, strMsgId,
								totalElements);
					}

					@Override
					public void onExportando(int exportCount, int totalElements) {
						sendExportationProgress(exportCount, totalElements);
					}

					@Override
					public void onExportacionFinalizada() {
					}
				};
				ResultadoVoid result = null;
				ResultadoTipado<ConectorBDOracle> conectResult = ConectorBDOracle
						.crear(ServicioExportacionDatos.this, true);
				result = conectResult;
				ConectorBDOracle conector = conectResult.getResultado();
				if (!result.tieneErrores()) {
					strMsgId = R.string.msg_exportando_lecturas;
					result = new LecturasManager().exportarLecturasTomadas(
							conector, exportListener);
				}
				if (!result.tieneErrores()) {
					strMsgId = R.string.msg_exportando_lecturas_entre_lineas;
					result = new MedidoresEntreLineasManager()
							.exportarLecturasTomadas(conector, exportListener);
				}
				if (!result.tieneErrores()) {
					strMsgId = R.string.msg_exportando_ordenativos;
					result = new OrdenativosManager()
							.exportarOrdenativosLecturas(conector,
									exportListener);
				}
				if (!result.tieneErrores()) {// FINALIZACION
					sendExportationMessage(UPDATE_WAITING,
							R.string.msg_finalizando_exportacion);
					result = new AsignacionRutaManager()
							.setRutasExportadasExitosamente(conector,
									AsignacionRuta.obtenerRutasImportadas());
				}
				if (!result.tieneErrores()) {// wipe data
					sendExportationMessage(UPDATE_WAITING,
							R.string.msg_wiping_all_data);
					result = new EliminacionDatosManager()
							.eliminarTodosLosDatos();
				}
				sendExportationFinished(result);
				stopSelf();
			}
		}).start();
		return Service.START_NOT_STICKY;
	}

	/**
	 * Envia un mensaje al broadcaster
	 * 
	 * @param action
	 *            acción
	 */
	private void sendExportationMessage(int action) {
		sendExportationMessage(action, -1);
	}

	/**
	 * Envia un mensaje al broadcaster
	 * 
	 * @param action
	 *            acción
	 */
	private void sendExportationMessage(int action, int strMsgId) {
		sendExportationMessage(action, strMsgId, -1);
	}

	/**
	 * Envia un mensaje al broadcaster
	 * 
	 * @param action
	 *            acción
	 * @param strMsgId
	 *            stringId del mensaje
	 * @param totalData
	 *            numero total de datos a importar -1 si solo es un mensaje
	 *            informativo
	 */
	private void sendExportationMessage(int action, int strMsgId, int totalData) {
		intent.putExtra("action", action);
		if (strMsgId != -1)
			intent.putExtra("message", strMsgId);
		if (strMsgId != -1)
			intent.putExtra("total_data", totalData);
		sendBroadcast(intent);
	}

	/**
	 * Enviaun mensaje al broadcaster de actualizar el progreso de exportación
	 * 
	 * @param count
	 * @param total
	 */
	private void sendExportationProgress(int count, int total) {
		intent.putExtra("action", UPDATE_PROGRESS);
		intent.putExtra("data_count", count);
		intent.putExtra("total_data", total);
	}

	/**
	 * Envia un mensaje al broadcaster de que se finalizó de realizar la
	 * exportación o esta se interrumpió
	 * 
	 * @param result
	 */
	private void sendExportationFinished(ResultadoVoid result) {
		intent.putExtra("action", EXPORTATION_FINISHED);
		intent.putExtra("result", result);
		sendBroadcast(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		intent = null;
		super.onDestroy();
	}
}
