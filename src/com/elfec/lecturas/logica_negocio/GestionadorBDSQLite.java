package com.elfec.lecturas.logica_negocio;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Cache;
import com.activeandroid.query.Select;
import com.elfec.lecturas.helpers.ManejadorJSON;
import com.elfec.lecturas.modelo.AsignacionRuta;
import com.elfec.lecturas.modelo.BaseCalculo;
import com.elfec.lecturas.modelo.BaseCalculoConcepto;
import com.elfec.lecturas.modelo.Concepto;
import com.elfec.lecturas.modelo.ConceptoCategoria;
import com.elfec.lecturas.modelo.ConceptoLectura;
import com.elfec.lecturas.modelo.ConceptoTarifa;
import com.elfec.lecturas.modelo.EvolucionConsumo;
import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.MedidorEntreLineas;
import com.elfec.lecturas.modelo.Ordenativo;
import com.elfec.lecturas.modelo.OrdenativoLectura;
import com.elfec.lecturas.modelo.PermisoRestriccion;
import com.elfec.lecturas.modelo.Potencia;
import com.elfec.lecturas.modelo.PreferenciaUI;
import com.elfec.lecturas.modelo.ReclasificacionCategoria;
import com.elfec.lecturas.modelo.TokenServicioWeb;
import com.elfec.lecturas.modelo.Usuario;
import com.elfec.lecturas.settings.AppPreferences;
import com.elfec.lecturas.settings.ConstantesDeEntorno;
import com.elfec.lecturas.settings.VariablesDeSesion;

/**
 * Provee metodos para la eliminacion de tablas mensuales y diarios y de la base
 * de datos completa
 * 
 * @author drodriguez
 *
 */
public class GestionadorBDSQLite {
	private static ArrayList<String> nombresTablasDatosDiarios;
	private static ArrayList<String> nombresTablasDatosMensuales;
	/**
	 * Inicializa las listas de nombres de tablas de los datos diarios y
	 * mensuales
	 */
	static {
		nombresTablasDatosDiarios = new ArrayList<String>();
		nombresTablasDatosDiarios.add(Cache.getTableName(Lectura.class));
		nombresTablasDatosDiarios.add(Cache.getTableName(Ordenativo.class));
		nombresTablasDatosDiarios.add(Cache
				.getTableName(EvolucionConsumo.class));
		// desde aqui no son datos que se cargan de la BD Orcale no hay que
		// verificar si se cargaron
		nombresTablasDatosDiarios.add(Cache.getTableName(Potencia.class));
		nombresTablasDatosDiarios.add(Cache
				.getTableName(MedidorEntreLineas.class));
		nombresTablasDatosDiarios.add(Cache
				.getTableName(OrdenativoLectura.class));
		nombresTablasDatosDiarios
				.add(Cache.getTableName(ConceptoLectura.class));
		nombresTablasDatosDiarios.add(Cache.getTableName(AsignacionRuta.class));
		nombresTablasDatosDiarios.add(Cache.getTableName(Usuario.class));
		nombresTablasDatosDiarios.add(Cache
				.getTableName(PermisoRestriccion.class));
		nombresTablasDatosDiarios.add(Cache.getTableName(PreferenciaUI.class));
		nombresTablasDatosDiarios.add(Cache
				.getTableName(TokenServicioWeb.class));
		nombresTablasDatosMensuales = new ArrayList<String>();
		nombresTablasDatosMensuales.add(Cache.getTableName(BaseCalculo.class));
		nombresTablasDatosMensuales.add(Cache
				.getTableName(BaseCalculoConcepto.class));
		nombresTablasDatosMensuales.add(Cache.getTableName(Concepto.class));
		nombresTablasDatosMensuales.add(Cache
				.getTableName(ConceptoCategoria.class));
		nombresTablasDatosMensuales.add(Cache
				.getTableName(ConceptoTarifa.class));
		nombresTablasDatosMensuales.add(Cache
				.getTableName(ReclasificacionCategoria.class));
	}

	/**
	 * Elimina masivamente todos los datos, tanto diarios como mensuales
	 */
	public static void eliminarTodosLosDatos() {
		ActiveAndroid.getDatabase().close();
		AppPreferences.getApplicationContext().deleteDatabase(
				ActiveAndroid.getDatabase().getPath());
		ActiveAndroid.dispose();
		ActiveAndroid.initialize(AppPreferences.getApplicationContext());
	}

	/**
	 * Elimina los datos de las tablas correspondientes a los modelos de:
	 * Lectura, Ordenativo, EvolucionConsumo , Potencia, MedidorEntreLineas,
	 * OrdenativoLectura, ConceptoLectura, AsignacionRuta, Usuario y
	 * PermisoRestriccion. Tambien borra los archivos de las variables de
	 * parametros
	 */
	public static void eliminarDatosDiarios() {
		final String comandoSQL = "DELETE FROM ";
		for (String nombreTabla : nombresTablasDatosDiarios) {
			ActiveAndroid.execSQL(comandoSQL + nombreTabla);
		}
		ManejadorJSON
				.eliminarArchivoJSON(ConstantesDeEntorno.archivoParametrosGrales);
		ManejadorJSON
				.eliminarArchivoJSON(ConstantesDeEntorno.archivoCategsNoMostrarCargoFijo);
		ManejadorJSON
				.eliminarArchivoJSON(ConstantesDeEntorno.archivoCodsResumenOrdenativos);
	}

	/**
	 * Elimina los datos de las tablas correspondientes a los modelos de:
	 * Lectura, Ordenativo, EvolucionConsumo , Potencia, MedidorEntreLineas,
	 * OrdenativoLectura, ConceptoLectura y AsignacionRuta. Tambien borra los
	 * archivos de las variables de parametros
	 */
	public static void eliminarDatosDiariosExceptoUsuario() {
		final String comandoSQL = "DELETE FROM ";
		int datosDiarios = 8;
		int i = 0;
		for (String nombreTabla : nombresTablasDatosDiarios) {
			if (i < datosDiarios)
				ActiveAndroid.execSQL(comandoSQL + nombreTabla);
			i++;
		}
		ManejadorJSON
				.eliminarArchivoJSON(ConstantesDeEntorno.archivoParametrosGrales);
		ManejadorJSON
				.eliminarArchivoJSON(ConstantesDeEntorno.archivoCategsNoMostrarCargoFijo);
		ManejadorJSON
				.eliminarArchivoJSON(ConstantesDeEntorno.archivoCodsResumenOrdenativos);
	}

	/**
	 * Elimina los datos de las tablas correspondientes a los modelos de:
	 * BaseCalculo, BaseCalculoConcepto, Concepto, ConceptoCategoria, y
	 * ReclasificacionCategoria
	 */
	public static void eliminarDatosMensuales() {
		final String comandoSQL = "DELETE FROM ";
		for (String nombreTabla : nombresTablasDatosMensuales) {
			ActiveAndroid.execSQL(comandoSQL + nombreTabla);
		}
	}

	/**
	 * Comprueba si los datos diarios de la base de datos fueron cargados. Los
	 * datos diarios son las tablas correspondientes a los modelos de:
	 * BaseCalculo, BaseCalculoConcepto, Concepto, ConceptoCategoria y
	 * ReclasificacionCategoria
	 * 
	 * @return true o false
	 */
	public static boolean datosMensualesFueronCargados() {
		boolean res = false;
		for (String nombreTabla : nombresTablasDatosMensuales) {
			res = existeTabla(nombreTabla) && tieneDatos(nombreTabla);
			if (res == false)
				break;
		}
		return res;
	}

	/**
	 * Comprueba el cuadro tarifario de la fecha actual con el cuadro tarifario
	 * cargado en los datos de la tabla ConceptoCategoria.
	 * 
	 * @return si son iguales devuelve true
	 */
	public static boolean idCuadroTarifarioEsActual() {
		if (existeTabla(Cache.getTableName(ConceptoCategoria.class))) {
			List<ConceptoCategoria> cptosCateg = new Select().from(
					ConceptoCategoria.class).execute();
			if (cptosCateg.size() > 0) {
				return cptosCateg.get(0).IdCuadroTarifario == VariablesDeSesion
						.idCuadroTarifario();
			}
		}
		return false;
	}

	/**
	 * Verifica la existencia de una tabla
	 * 
	 * @param nombreTabla
	 * @return
	 */
	private static boolean existeTabla(String nombreTabla) {
		if (nombreTabla == null || ActiveAndroid.getDatabase() == null) {
			return false;
		}
		Cursor cursor = ActiveAndroid
				.getDatabase()
				.rawQuery(
						"SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?",
						new String[] { "table", nombreTabla });
		if (!cursor.moveToFirst()) {
			return false;
		}
		int count = cursor.getInt(0);
		cursor.close();
		return count > 0;
	}

	/**
	 * Verifica que la tabla tenga al menos un registro
	 * 
	 * @param nombreTabla
	 * @return
	 */
	private static boolean tieneDatos(String nombreTabla) {
		if (nombreTabla == null || ActiveAndroid.getDatabase() == null) {
			return false;
		}
		Cursor cursor = ActiveAndroid.getDatabase().rawQuery(
				"SELECT COUNT(*) FROM " + nombreTabla, null);
		if (!cursor.moveToFirst()) {
			return false;
		}
		int count = cursor.getInt(0);
		cursor.close();
		return count > 0;
	}

}
