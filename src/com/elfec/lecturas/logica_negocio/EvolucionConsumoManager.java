package com.elfec.lecturas.logica_negocio;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.elfec.lecturas.helpers.ConectorBDOracle;
import com.elfec.lecturas.logica_negocio.intercambio_datos.DataImporter;
import com.elfec.lecturas.modelo.AsignacionRuta;
import com.elfec.lecturas.modelo.EvolucionConsumo;
import com.elfec.lecturas.modelo.Potencia;
import com.elfec.lecturas.modelo.eventos.ImportacionDatosListener;
import com.elfec.lecturas.modelo.intercambio_datos.ImportSource;
import com.elfec.lecturas.modelo.resultados.ResultadoTipado;

/**
 * Se encarga de las operaciones de negocio de la evolución de consumo
 * 
 * @author drodriguez
 *
 */
public class EvolucionConsumoManager {
	/**
	 * Importa toda la información de evoluciones de consumo de las lecturas de
	 * las rutas asignadas al usuario para la fecha actual.<br>
	 * <b>Nota.-</b> La importación incluye la consulta remota y el guardado
	 * local de los datos
	 * 
	 * @param username
	 * @param password
	 * @param importacionDatosListener
	 *            {@link ImportacionDatosListener}
	 * @return {@link ResultadoTipado} con el resultado de la las lecturas de la
	 *         lista de rutas asignadas al usuario
	 */
	public ResultadoTipado<List<EvolucionConsumo>> importarEvConsumosDeRutasAsignadas(
			ConectorBDOracle conector, List<AsignacionRuta> assignedRoutes,
			final String inClausula,
			ImportacionDatosListener importacionDatosListener) {
		ResultadoTipado<List<EvolucionConsumo>> globalResult = new ResultadoTipado<List<EvolucionConsumo>>(
				new ArrayList<EvolucionConsumo>());
		ResultadoTipado<List<EvolucionConsumo>> result;
		if (importacionDatosListener != null)
			importacionDatosListener.onImportacionIniciada();

		for (AsignacionRuta assignedRoute : assignedRoutes) {
			result = importarEvConsumosDeRuta(conector, assignedRoute,
					inClausula);
			// copiando errores
			globalResult.agregarErrores(result.getErrores());
			if (!globalResult.tieneErrores())
				globalResult.getResultado().addAll(result.getResultado());
			else
				break; // rompe ciclo si hay errores
		}

		if (importacionDatosListener != null)
			importacionDatosListener.onImportacionFinalizada(globalResult);
		return globalResult;
	}

	/**
	 * Importa la informacón de las evoluciones de consumo de lecturas de una
	 * ruta asignada
	 * 
	 * @param username
	 * @param password
	 * @param LecturaRDA
	 * @param assignedRoute
	 * @param inClausula
	 * @return {@link ResultadoTipado} con el resultado de las lecturas de la
	 *         ruta asignada al usuario
	 */
	private ResultadoTipado<List<EvolucionConsumo>> importarEvConsumosDeRuta(
			final ConectorBDOracle conector,
			final AsignacionRuta assignedRoute, final String inClausula) {
		Potencia.eliminarPotenciasDeRutaAsignada(assignedRoute, inClausula);
		return new DataImporter()
				.importData(new ImportSource<EvolucionConsumo>() {
					@Override
					public List<EvolucionConsumo> requestData()
							throws ConnectException, SQLException {
						return conector.obtenerEvolucionConsumosPorRuta(
								assignedRoute.Ruta, inClausula);
					}

					@Override
					public void preSaveData(EvolucionConsumo data) {
					}
				});

	}
}
