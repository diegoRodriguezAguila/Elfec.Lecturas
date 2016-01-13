package com.elfec.lecturas.logica_negocio;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.List;

import com.elfec.lecturas.acceso_remoto_datos.ConectorBDOracle;
import com.elfec.lecturas.logica_negocio.intercambio_datos.DataImporter;
import com.elfec.lecturas.modelo.BaseCalculo;
import com.elfec.lecturas.modelo.BaseCalculoConcepto;
import com.elfec.lecturas.modelo.Concepto;
import com.elfec.lecturas.modelo.eventos.ImportacionDatosListener;
import com.elfec.lecturas.modelo.intercambio_datos.ImportSource;
import com.elfec.lecturas.modelo.resultados.ResultadoVoid;
import com.elfec.lecturas.settings.AppPreferences;

/**
 * Se encarga de la lÃ³gica de negocio de bases de calculo
 * 
 * @author drodriguez
 *
 */
public class BasesCalculoManager {
	/**
	 * Importa las bases de calculo de ERP_ELFEC.GBASES_CALC_IMP si es que no se
	 * importaron ya previamente.<br>
	 * <b>Nota.-</b> La importaciÃ³n incluye la consulta remota y el guardado
	 * local de los datos
	 * 
	 * @param username
	 * @param password
	 * @param dataImportListener
	 *            {@link DataImportListener}
	 * @return {@link ResultadoVoid}
	 */
	public ResultadoVoid importarBasesCalculo(final ConectorBDOracle conector,
			ImportacionDatosListener dataImportListener) {
		ResultadoVoid result = new ResultadoVoid();
		if (!AppPreferences.instance().estaBasesCalculoImportados()) {
			if (dataImportListener != null)
				dataImportListener.onImportacionIniciada();
			BaseCalculo.eliminarTodasLasBasesDeCalculo();
			result = new DataImporter()
					.importData(new ImportSource<BaseCalculo>() {
						@Override
						public List<BaseCalculo> requestData()
								throws ConnectException, SQLException {
							return conector.obtenerBasesCalculo();
						}

						@Override
						public void preSaveData(BaseCalculo data) {
						}
					});
			AppPreferences.instance().setBasesCalculoImportados(
					!result.tieneErrores());
			if (dataImportListener != null)
				dataImportListener.onImportacionFinalizada(result);
		}
		return result;
	}

	/**
	 * Importa las bases de calculo de ERP_ELFEC.GBASES_CALC_CPTOS si es que no
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
	public ResultadoVoid importarBasesCalculoConceptos(
			final ConectorBDOracle conector,
			ImportacionDatosListener dataImportListener) {
		ResultadoVoid result = new ResultadoVoid();
		if (!AppPreferences.instance().estaBasesCalcConceptosImportados()) {
			if (dataImportListener != null)
				dataImportListener.onImportacionIniciada();
			BaseCalculoConcepto.eliminarTodasLasBasesDeCalculoCptos();
			result = new DataImporter()
					.importData(new ImportSource<BaseCalculoConcepto>() {
						@Override
						public List<BaseCalculoConcepto> requestData()
								throws ConnectException, SQLException {
							return conector.obtenerBasesCalculoConceptos();
						}

						@Override
						public void preSaveData(BaseCalculoConcepto basCalcCon) {
							BaseCalculo baseCalc = BaseCalculo
									.obtenerBaseDeCalculo(basCalcCon.IdBaseCalculo);
							Concepto concepto = Concepto.obtenerConcepto(
									basCalcCon.IdConcepto,
									basCalcCon.IdSubConcepto);
							basCalcCon.BaseCalculo = baseCalc;
							basCalcCon.Concepto = concepto;
						}
					});
			AppPreferences.instance().setBasesCalcConceptosImportados(
					!result.tieneErrores());
			if (dataImportListener != null)
				dataImportListener.onImportacionFinalizada(result);
		}
		return result;
	}

}
