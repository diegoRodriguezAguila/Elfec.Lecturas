package com.elfec.lecturas.logica_negocio;

import com.elfec.lecturas.acceso_remoto_datos.ConectorBDOracle;
import com.elfec.lecturas.modelo.eventos.ImportacionDatosListener;
import com.elfec.lecturas.modelo.resultados.ResultadoVoid;
import com.elfec.lecturas.settings.AppPreferences;

import org.json.JSONException;

import java.sql.SQLException;

/**
 * Se encarga de la lógica de negocio de los parametrizables
 * 
 * @author drodriguez
 *
 */
public class ParametrizablesManager {
	/**
	 * Importa las variables parametrizables de las tablas:
	 * ERP_ELFEC.SGC_MOVIL_PARAM ERP_ELFEC.SGC_MOVIL_PARAM_CATEG_NO_CFIJO Y
	 * ERP_ELFEC.SGC_MOVIL_PARAM_COD_ORD_RES y las guarda en archivos JSON para
	 * su uso por la clase estatica VariablesDeEntorno
	 */
	public ResultadoVoid importarParametrizables(ConectorBDOracle conector,
			ImportacionDatosListener importacionDatosListener) {
		ResultadoVoid result = new ResultadoVoid();
		if (!AppPreferences.instance().estaParametrizablesImportados()) {
			try {
				if (importacionDatosListener != null)
					importacionDatosListener.onImportacionIniciada();
				conector.obtenerParametrosGenerales();
				conector.obtenerParamCodOrdenativosResumen();
				conector.obtenerParamCategsNoMuestraCargoFijo();
				AppPreferences.instance().setParametrizablesImportados(
						!result.tieneErrores());
				if (importacionDatosListener != null)
					importacionDatosListener.onImportacionFinalizada(result);
			} catch (SQLException e) {
				e.printStackTrace();
				result.agregarError(e);
			} catch (JSONException e) {
				e.printStackTrace();
				result.agregarError(e);
			} catch (Exception e) {
				e.printStackTrace();
				result.agregarError(e);
			}
		}
		return result;
	}
}
