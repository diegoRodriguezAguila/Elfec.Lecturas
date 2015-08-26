package com.elfec.lecturas.servicios;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.elfec.lecturas.acceso_remoto_datos.ConectorBDOracle;
import com.elfec.lecturas.helpers.utils.text.AttributePicker;
import com.elfec.lecturas.helpers.utils.text.ObjectListToSQL;
import com.elfec.lecturas.logica_negocio.AsignacionRutaManager;
import com.elfec.lecturas.logica_negocio.BasesCalculoManager;
import com.elfec.lecturas.logica_negocio.ConceptoLecturaManager;
import com.elfec.lecturas.logica_negocio.ConceptosManager;
import com.elfec.lecturas.logica_negocio.EvolucionConsumoManager;
import com.elfec.lecturas.logica_negocio.LecturasManager;
import com.elfec.lecturas.logica_negocio.OrdenativosManager;
import com.elfec.lecturas.logica_negocio.ParametrizablesManager;
import com.elfec.lecturas.logica_negocio.PotenciasManager;
import com.elfec.lecturas.logica_negocio.ReclasifCategoriasManager;
import com.elfec.lecturas.modelo.AsignacionRuta;
import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.eventos.ImportacionDatosListener;
import com.elfec.lecturas.modelo.resultados.ResultadoTipado;
import com.elfec.lecturas.modelo.resultados.ResultadoVoid;
import com.elfec.lecturas.servicios.receivers.DataImportationReceiver;
import com.elfec.lecturas.settings.AppPreferences;
import com.elfec.lecturas.settings.VariablesDeSesion;
import com.lecturas.elfec.R;

/**
 * Servicio android que corre en segundo plano para realizar la importación de
 * datos del servidor, notifica eventos de UI a {@link DataImportationReceiver}
 * 
 * @author drodriguez
 *
 */
public class ServicioImportacionDatos extends Service {

	/**
	 * Acción del broadcast
	 */
	public static final String BROADCAST_ACTION = "com.elfec.lecturas.importEvent";
	/**
	 * Mensaje de inicio de importación
	 */
	public static final int IMPORTATION_STARTING = 1;
	/**
	 * Mensaje de actualización de un paso de importación
	 */
	public static final int UPDATE_WAITING = 2;
	/**
	 * Mensajde de fin de la importación
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
			@Override
			public void run() {
				sendImportationAction(IMPORTATION_STARTING);
				ImportacionDatosListener importacionDatosListener = new ImportacionDatosListener() {
					@Override
					public void onImportacionIniciada() {
						sendImportationAction(UPDATE_WAITING, strMsgId);
					}

					@Override
					public void onImportacionFinalizada(ResultadoVoid result) {
					}
				};
				ResultadoVoid result = null;
				ResultadoTipado<ConectorBDOracle> conectResult = ConectorBDOracle
						.crear(ServicioImportacionDatos.this, true);
				result = conectResult;
				ConectorBDOracle conector = conectResult.getResultado();
				result = importarDatosRequeridosUnaVez(conector,
						importacionDatosListener, result);
				result = importarDatosRutas(conector, importacionDatosListener,
						result);
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
	 *            acción
	 */
	private void sendImportationAction(int action) {
		sendImportationAction(action, -1);
	}

	/**
	 * Envia un mensaje al broadcaster
	 * 
	 * @param action
	 *            acción
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
	 * Envia un mensaje al broadcaster de que se finalizó de realizar la
	 * importación o esta se interrumpió
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
	 * Importado los datos que solo son necesarios una vez por sesión
	 * 
	 * @param conector
	 * @param importacionDatosListener
	 * @param result
	 * @return resultado
	 */
	private ResultadoVoid importarDatosRequeridosUnaVez(
			ConectorBDOracle conector,
			ImportacionDatosListener importacionDatosListener,
			ResultadoVoid result) {
		if (!AppPreferences.instance().estaInfoReqUnaVezImportados()) {
			if (!result.tieneErrores()) {
				strMsgId = R.string.msg_importando_parametrizables;
				result = new ParametrizablesManager().importarParametrizables(
						conector, importacionDatosListener);
			}
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
			AppPreferences.instance().setInfoReqUnaVezImportados(
					result.tieneErrores());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private ResultadoVoid importarDatosRutas(ConectorBDOracle conector,
			ImportacionDatosListener importacionDatosListener,
			ResultadoVoid result) {
		List<AsignacionRuta> rutasAsignadas = null;
		String lecturasClausulaIN = "";
		AsignacionRutaManager asignacionRutaManager = new AsignacionRutaManager();
		if (!result.tieneErrores()) {
			strMsgId = R.string.msg_importando_asignacion_rutas;
			result = asignacionRutaManager.importarRutasAsignadasAUsuario(
					conector, VariablesDeSesion.getUsuarioLogeado(),
					importacionDatosListener);
			rutasAsignadas = ((ResultadoTipado<List<AsignacionRuta>>) result)
					.getResultado();
		}
		if (!result.tieneErrores()) {
			strMsgId = R.string.msg_importando_lecturas;
			result = new LecturasManager().importarLecturasDeRutasAsignadas(
					conector, rutasAsignadas, importacionDatosListener);
			lecturasClausulaIN = convertirAClausulaIn(((ResultadoTipado<List<Lectura>>) result)
					.getResultado());
		}
		if (!result.tieneErrores()) {
			strMsgId = R.string.msg_importando_potencias;
			result = new PotenciasManager().importarPotenciasDeRutasAsignadas(
					conector, rutasAsignadas, lecturasClausulaIN,
					importacionDatosListener);
		}
		if (!result.tieneErrores()) {
			strMsgId = R.string.msg_importando_ev_consumos;
			result = new EvolucionConsumoManager()
					.importarEvConsumosDeRutasAsignadas(conector,
							rutasAsignadas, lecturasClausulaIN,
							importacionDatosListener);
		}
		if (!result.tieneErrores()) {
			strMsgId = R.string.msg_importando_cptos_lecturas;
			result = new ConceptoLecturaManager()
					.importarConceptosLecturasDeRutasAsignadas(conector,
							rutasAsignadas, lecturasClausulaIN,
							importacionDatosListener);
		}
		if (!result.tieneErrores()) {// FINALIZACION
			sendImportationAction(UPDATE_WAITING,
					R.string.msg_finalizando_importacion);
			result = asignacionRutaManager.setRutasImportadasExitosamente(
					conector, rutasAsignadas);
		}
		return result;
	}

	/**
	 * Obtiene la clausula In de la información general de lecturas
	 * 
	 * @param lecturas
	 * @return clausula IN SQL de suministros de lecturas
	 */
	private String convertirAClausulaIn(List<Lectura> lecturas) {
		return ObjectListToSQL.convertToSQL(lecturas, "LEMSUM",
				new AttributePicker<String, Lectura>() {
					@Override
					public String pickAttribute(Lectura readingGeneralInfo) {
						return "" + readingGeneralInfo.Suministro;
					}
				});
	}

}
