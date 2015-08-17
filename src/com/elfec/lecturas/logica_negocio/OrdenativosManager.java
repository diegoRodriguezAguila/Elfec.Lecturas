package com.elfec.lecturas.logica_negocio;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.List;

import com.elfec.lecturas.helpers.ConectorBDOracle;
import com.elfec.lecturas.logica_negocio.intercambio_datos.DataImporter;
import com.elfec.lecturas.modelo.Ordenativo;
import com.elfec.lecturas.modelo.eventos.ImportacionDatosListener;
import com.elfec.lecturas.modelo.intercambio_datos.ImportSource;
import com.elfec.lecturas.modelo.resultados.ResultadoVoid;
import com.elfec.lecturas.settings.AppPreferences;

/**
 * Provee de una capa de l�gica de negocio para los Ordenativos
 * 
 * @author drodriguez
 *
 */
public class OrdenativosManager {
	/**
	 * Importa los ordenativos del erp si es que no se importaron ya
	 * previamente.<br>
	 * <b>Nota.-</b> La importaci�n incluye la consulta remota y el guardado
	 * local de los datos
	 * 
	 * @param username
	 * @param password
	 * @param dataImportListener
	 *            {@link DataImportListener}
	 * @return {@link ResultadoVoid}
	 */
	public ResultadoVoid importarOrdenativos(final ConectorBDOracle conector,
			ImportacionDatosListener dataImportListener) {
		ResultadoVoid result = new ResultadoVoid();
		if (!AppPreferences.instance().estaOrdenativosImportados()) {
			if (dataImportListener != null)
				dataImportListener.onImportacionIniciada();
			Ordenativo.eliminarTodosLosOrdenativos();
			result = new DataImporter()
					.importData(new ImportSource<Ordenativo>() {
						@Override
						public List<Ordenativo> requestData()
								throws ConnectException, SQLException {
							return conector.obtenerOrdenativos();
						}

						@Override
						public void preSaveData(Ordenativo data) {
						}
					});
			AppPreferences.instance().setOrdenativosImportados(
					!result.tieneErrores());
			if (dataImportListener != null)
				dataImportListener.onImportacionFinalizada(result);
		}
		return result;
	}

}