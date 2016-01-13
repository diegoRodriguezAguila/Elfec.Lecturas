package com.elfec.lecturas.logica_negocio;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.List;

import com.elfec.lecturas.acceso_remoto_datos.ConectorBDOracle;
import com.elfec.lecturas.logica_negocio.intercambio_datos.DataImporter;
import com.elfec.lecturas.modelo.Concepto;
import com.elfec.lecturas.modelo.ConceptoCategoria;
import com.elfec.lecturas.modelo.ConceptoTarifa;
import com.elfec.lecturas.modelo.eventos.ImportacionDatosListener;
import com.elfec.lecturas.modelo.intercambio_datos.ImportSource;
import com.elfec.lecturas.modelo.resultados.ResultadoVoid;
import com.elfec.lecturas.settings.AppPreferences;

/**
 * Manager para operaciones de logica de negocio relacionadas con conceptos
 * 
 * @author drodriguez
 *
 */
public class ConceptosManager {
	/**
	 * Importa los conceptos del ERP_ELFEC.CONCEPTOS si es que no se importaron
	 * ya previamente.<br>
	 * <b>Nota.-</b> La importaciÃ³n incluye la consulta remota y el guardado
	 * local de los datos
	 * 
	 * @param username
	 * @param password
	 * @param dataImportListener
	 *            {@link DataImportListener}
	 * @return {@link ResultadoVoid}
	 */
	public ResultadoVoid importarConceptos(final ConectorBDOracle conector,
			ImportacionDatosListener dataImportListener) {
		ResultadoVoid result = new ResultadoVoid();
		if (!AppPreferences.instance().estaConceptosImportados()) {
			if (dataImportListener != null)
				dataImportListener.onImportacionIniciada();
			Concepto.eliminarTodosLosConceptos();
			result = new DataImporter()
					.importData(new ImportSource<Concepto>() {
						@Override
						public List<Concepto> requestData()
								throws ConnectException, SQLException {
							return conector.obtenerConceptos();
						}

						@Override
						public void preSaveData(Concepto data) {
						}
					});
			AppPreferences.instance().setConceptosImportados(
					!result.tieneErrores());
			if (dataImportListener != null)
				dataImportListener.onImportacionFinalizada(result);
		}
		return result;
	}

	/**
	 * Importa los conceptos categorias de ERP_ELFEC.CPTOS_CATEGORIAS si es que
	 * no se importaron ya previamente.<br>
	 * <b>Nota.-</b> La importaciÃ³n incluye la consulta remota y el guardado
	 * local de los datos
	 * 
	 * @param username
	 * @param password
	 * @param dataImportListener
	 *            {@link DataImportListener}
	 * @return {@link ResultadoVoid}
	 */
	public ResultadoVoid importarConceptosCategorias(
			final ConectorBDOracle conector,
			ImportacionDatosListener dataImportListener) {
		ResultadoVoid result = new ResultadoVoid();
		if (!AppPreferences.instance().estaConceptosCategoriasImportados()) {
			if (dataImportListener != null)
				dataImportListener.onImportacionIniciada();
			ConceptoCategoria.eliminarTodosLosConceptosCatgeorias();
			result = new DataImporter()
					.importData(new ImportSource<ConceptoCategoria>() {
						@Override
						public List<ConceptoCategoria> requestData()
								throws ConnectException, SQLException {
							return conector.obtenerConceptosCategorias();
						}

						@Override
						public void preSaveData(ConceptoCategoria cat) {
							cat.Concepto = Concepto.obtenerConcepto(
									cat.IdConcepto, cat.IdSubConcepto);
						}
					});
			AppPreferences.instance().setConceptosCategoriasImportados(
					!result.tieneErrores());
			if (dataImportListener != null)
				dataImportListener.onImportacionFinalizada(result);
		}
		return result;
	}

	/**
	 * Importa los conceptos tarifas de ERP_ELFEC.CONCEPTOS_TARIFAS si es que no
	 * se importaron ya previamente.<br>
	 * <b>Nota.-</b> La importaciÃ³n incluye la consulta remota y el guardado
	 * local de los datos
	 * 
	 * @param username
	 * @param password
	 * @param dataImportListener
	 *            {@link DataImportListener}
	 * @return {@link ResultadoVoid}
	 */
	public ResultadoVoid importarConceptosTarifas(
			final ConectorBDOracle conector,
			ImportacionDatosListener dataImportListener) {
		ResultadoVoid result = new ResultadoVoid();
		if (!AppPreferences.instance().estaConceptosTarifasImportados()) {
			if (dataImportListener != null)
				dataImportListener.onImportacionIniciada();
			ConceptoTarifa.eliminarTodosLosConceptosTarifas();
			result = new DataImporter()
					.importData(new ImportSource<ConceptoTarifa>() {
						@Override
						public List<ConceptoTarifa> requestData()
								throws ConnectException, SQLException {
							return conector.obtenerConceptosTarifas();
						}

						@Override
						public void preSaveData(ConceptoTarifa tarif) {
							tarif.Concepto = Concepto.obtenerConcepto(
									tarif.IdConcepto, tarif.IdSubConcepto);
						}
					});
			AppPreferences.instance().setConceptosTarifasImportados(
					!result.tieneErrores());
			if (dataImportListener != null)
				dataImportListener.onImportacionFinalizada(result);
		}
		return result;
	}
}
