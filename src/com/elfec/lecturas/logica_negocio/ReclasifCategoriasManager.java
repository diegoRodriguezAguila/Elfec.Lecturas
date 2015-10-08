package com.elfec.lecturas.logica_negocio;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.List;

import com.elfec.lecturas.acceso_remoto_datos.ConectorBDOracle;
import com.elfec.lecturas.logica_negocio.intercambio_datos.DataImporter;
import com.elfec.lecturas.modelo.ReclasificacionCategoria;
import com.elfec.lecturas.modelo.eventos.ImportacionDatosListener;
import com.elfec.lecturas.modelo.intercambio_datos.ImportSource;
import com.elfec.lecturas.modelo.resultados.ResultadoVoid;
import com.elfec.lecturas.settings.AppPreferences;

/**
 * Capa de lógica de negocio para las reclasificaciones de categorias
 * 
 * @author drodriguez
 *
 */
public class ReclasifCategoriasManager {
	/**
	 * Importa las bases de calculo de ERP_ELFEC.CATEG_RECLASIF si es que no se
	 * importaron ya previamente.<br>
	 * <b>Nota.-</b> La importación incluye la consulta remota y el guardado
	 * local de los datos
	 * 
	 * @param username
	 * @param password
	 * @param dataImportListener
	 *            {@link DataImportListener}
	 * @return {@link ResultadoVoid}
	 */
	public ResultadoVoid importarReclasificacionCategorias(
			final ConectorBDOracle conector,
			ImportacionDatosListener dataImportListener) {
		ResultadoVoid result = new ResultadoVoid();
		if (!AppPreferences.instance().estaReclasifCategoriasImportados()) {
			ReclasificacionCategoria.eliminarTodo();
			result = new DataImporter()
					.importData(new ImportSource<ReclasificacionCategoria>() {
						@Override
						public List<ReclasificacionCategoria> requestData()
								throws ConnectException, SQLException {
							return conector.obtenerReclasificacionCategorias();
						}

						@Override
						public void preSaveData(ReclasificacionCategoria data) {
						}
					});
			AppPreferences.instance().setReclasifCategoriasImportados(
					!result.tieneErrores());
			if (dataImportListener != null)
				dataImportListener.onImportacionFinalizada(result);
		}
		return result;
	}
}
