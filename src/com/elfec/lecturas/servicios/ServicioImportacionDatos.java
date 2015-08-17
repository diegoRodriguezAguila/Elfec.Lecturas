package com.elfec.lecturas.servicios;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.elfec.lecturas.helpers.ConectorBDOracle;
import com.elfec.lecturas.helpers.VariablesDeSesion;
import com.elfec.lecturas.logica_negocio.AsignacionRutaManager;
import com.elfec.lecturas.logica_negocio.BasesCalculoManager;
import com.elfec.lecturas.logica_negocio.ConceptosManager;
import com.elfec.lecturas.logica_negocio.LecturasManager;
import com.elfec.lecturas.logica_negocio.OrdenativosManager;
import com.elfec.lecturas.logica_negocio.ParametrizablesManager;
import com.elfec.lecturas.logica_negocio.ReclasifCategoriasManager;
import com.elfec.lecturas.modelo.AsignacionRuta;
import com.elfec.lecturas.modelo.eventos.ImportacionDatosListener;
import com.elfec.lecturas.modelo.resultados.ResultadoTipado;
import com.elfec.lecturas.modelo.resultados.ResultadoVoid;
import com.elfec.lecturas.settings.AppPreferences;
import com.lecturas.elfec.R;

/**
 * Servicio android que corre en segundo plano para realizar la importaci�n de
 * datos del servidor, notifica eventos de UI a
 * {@link DataImportationReceiverPresenter}
 * 
 * @author drodriguez
 *
 */
public class ServicioImportacionDatos extends Service {

	/**
	 * Acci�n del broadcast
	 */
	public static final String BROADCAST_ACTION = "com.elfec.lecturas.importEvent";
	/**
	 * Mensaje de inicio de importaci�n
	 */
	public static final int IMPORTATION_STARTING = 1;
	/**
	 * Mensaje de actualizaci�n de un paso de importaci�n
	 */
	public static final int UPDATE_WAITING = 2;
	/**
	 * Mensajde de fin de la importaci�n
	 */
	public static final int IMPORTATION_FINISHED = 3;

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
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				sendImportationAction(IMPORTATION_STARTING);
				ConectorBDOracle conector = new ConectorBDOracle(
						ServicioImportacionDatos.this, true);
				ImportacionDatosListener importacionDatosListener = new ImportacionDatosListener() {
					@Override
					public void onImportacionIniciada() {
						sendImportationAction(UPDATE_WAITING, strMsgId);
					}

					@Override
					public void onImportacionFinalizada(ResultadoVoid result) {
					}
				};

				ResultadoVoid result = importarDatosRequeridosUnaVez(conector,
						importacionDatosListener);

				List<AsignacionRuta> rutasAsignadas = null;
				AsignacionRutaManager asignacionRutaManager = new AsignacionRutaManager();
				if (!result.tieneErrores()) {
					strMsgId = R.string.msg_importando_asignacion_rutas;
					result = asignacionRutaManager
							.importarRutasAsignadasAUsuario(conector,
									VariablesDeSesion.getUsuarioLogeado(),
									importacionDatosListener);
					rutasAsignadas = ((ResultadoTipado<List<AsignacionRuta>>) result)
							.getResultado();
				}
				if (!result.tieneErrores()) {
					strMsgId = R.string.msg_importando_lecturas;
					result = new LecturasManager()
							.importarLecturasDeRutasAsignadas(conector,
									rutasAsignadas, importacionDatosListener);
				}
				if (!result.tieneErrores()) {
					strMsgId = R.string.msg_importing_reading_meters;
					result = new ReadingMeterManager().importReadingMeters(
							username, password,
							((TypedResult<List<ReadingGeneralInfo>>) result)
									.getResultado(), importacionDatosListener);
				}
				if (!result.tieneErrores()) {// FINALIZACION
					sendImportationAction(UPDATE_WAITING,
							R.string.msg_finishing_import);
					result = routeAssignmentManager
							.setRoutesSuccessfullyImported(username, password,
									assignedRoutes);
				}
				sendImportationFinished(result);
				ServicioImportacionDatos.this.stopSelf();
			}
		}).start();
		return Service.START_NOT_STICKY;
	}

	/**
	 * Envia un mensaje al broadcaster
	 * 
	 * @param action
	 *            acci�n
	 */
	private void sendImportationAction(int action) {
		sendImportationAction(action, -1);
	}

	/**
	 * Envia un mensaje al broadcaster
	 * 
	 * @param action
	 *            acci�n
	 * @param strMsgId
	 *            stringId del mensaje
	 */
	private void sendImportationAction(int action, int strMsgId) {
		intent.putExtra("action", action);
		if (strMsgId != -1)
			intent.putExtra("message", strMsgId);
		sendBroadcast(intent);
	}

	/**
	 * Envia un mensaje al broadcaster de que se finaliz� de realizar la
	 * importaci�n o esta se interrumpi�
	 * 
	 * @param result
	 */
	private void sendImportationFinished(ResultadoVoid result) {
		intent.putExtra("action", IMPORTATION_FINISHED);
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

	/**
	 * Importado los datos que solo son necesarios una vez por sesi�n
	 * 
	 * @param conector
	 * @param importacionDatosListener
	 * @return resultado
	 */
	private ResultadoVoid importarDatosRequeridosUnaVez(
			ConectorBDOracle conector,
			ImportacionDatosListener importacionDatosListener) {
		ResultadoVoid result = new ResultadoVoid();
		if (!AppPreferences.instance().estaAllOnceReqDataImportados()) {
			strMsgId = R.string.msg_importando_parametrizables;
			result = new ParametrizablesManager().importarParametrizables(
					conector, importacionDatosListener);
			if (!result.tieneErrores()) {
				strMsgId = R.string.msg_importando_ordenativos;
				result = new OrdenativosManager().importarOrdenativos(conector,
						importacionDatosListener);
			}
			ConceptosManager cptManager = new ConceptosManager();
			if (!result.tieneErrores()) {
				strMsgId = R.string.msg_importando_conceptos;
				result = cptManager.importarConceptos(conector,
						importacionDatosListener);
			}
			if (!result.tieneErrores()) {
				strMsgId = R.string.msg_importando_conceptos_categ;
				result = cptManager.importarConceptos(conector,
						importacionDatosListener);
			}
			if (!result.tieneErrores()) {
				strMsgId = R.string.msg_importando_conceptos_tarifa;
				result = cptManager.importarConceptos(conector,
						importacionDatosListener);
			}
			BasesCalculoManager basesCalcManager = new BasesCalculoManager();
			if (!result.tieneErrores()) {
				strMsgId = R.string.msg_importando_bases_calculo;
				result = basesCalcManager.importarBasesCalculo(conector,
						importacionDatosListener);
			}
			if (!result.tieneErrores()) {
				strMsgId = R.string.msg_importando_bases_calculo_cptos;
				result = basesCalcManager.importarBasesCalculoConceptos(
						conector, importacionDatosListener);
			}
			if (!result.tieneErrores()) {
				strMsgId = R.string.msg_importando_reclasif_categs;
				result = new ReclasifCategoriasManager()
						.importarReclasificacionCategorias(conector,
								importacionDatosListener);
			}
			AppPreferences.instance().setAllOnceReqDataImportados(
					result.tieneErrores());
		}
		return result;
	}

}