package com.elfec.lecturas.logica_negocio;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.List;

import com.elfec.lecturas.helpers.ConectorBDOracle;
import com.elfec.lecturas.logica_negocio.intercambio_datos.DataImporter;
import com.elfec.lecturas.modelo.AsignacionRuta;
import com.elfec.lecturas.modelo.eventos.ImportacionDatosListener;
import com.elfec.lecturas.modelo.excepciones.NoHayRutasAsignadasException;
import com.elfec.lecturas.modelo.intercambio_datos.ImportSource;
import com.elfec.lecturas.modelo.resultados.ResultadoTipado;

/**
 * Se encarga de la lógica de negocio de las asignaciones de rutas
 * 
 * @author drodriguez
 *
 */
public class AsignacionRutaManager {
	/**
	 * Importa las rutas asignadas al usuario para la fecha actual.<br>
	 * <b>Nota.-</b> La importación incluye la consulta remota y el guardado
	 * local de los datos
	 * 
	 * @param username
	 * @param password
	 * @param dataImportListener
	 *            {@link DataImportListener}
	 * @return {@link TypedResult} con el resultado de la lista de rutas
	 *         asignadas al usuario
	 */
	public ResultadoTipado<List<AsignacionRuta>> importarRutasAsignadasAUsuario(
			final ConectorBDOracle conector, final String usuario,
			ImportacionDatosListener dataImportListener) {
		if (dataImportListener != null)
			dataImportListener.onImportacionIniciada();
		AsignacionRuta.eliminarTodasLasRutasNoImportadasDelUsuario(usuario);
		ResultadoTipado<List<AsignacionRuta>> result = new DataImporter()
				.importData(new ImportSource<AsignacionRuta>() {
					@Override
					public List<AsignacionRuta> requestData()
							throws ConnectException, SQLException {
						return conector.obtenerRutasAsignadas(usuario);
					}

					@Override
					public void preSaveData(AsignacionRuta data) {
					}
				});
		if (result.getResultado().size() == 0)// no tiene rutas asignadas
			result.agregarError(new NoHayRutasAsignadasException(usuario));

		if (dataImportListener != null)
			dataImportListener.onImportacionFinalizada(result);
		return result;
	}
}
