package com.elfec.lecturas.logica_negocio;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.elfec.lecturas.acceso_remoto_datos.ConectorBDOracle;
import com.elfec.lecturas.logica_negocio.intercambio_datos.DataImporter;
import com.elfec.lecturas.modelo.AsignacionRuta;
import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.eventos.ImportacionDatosListener;
import com.elfec.lecturas.modelo.excepciones.RutaAsignadaSinLecturasException;
import com.elfec.lecturas.modelo.intercambio_datos.ImportSource;
import com.elfec.lecturas.modelo.resultados.ResultadoTipado;

/**
 * Se encarga de la lógica de negocio de lecturas
 * 
 * @author drodriguez
 *
 */
public class LecturasManager {
	/**
	 * Importa toda la información general de las lecturas de las rutas
	 * asignadas al usuario para la fecha actual.<br>
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
	public ResultadoTipado<List<Lectura>> importarLecturasDeRutasAsignadas(
			ConectorBDOracle conector, List<AsignacionRuta> assignedRoutes,
			ImportacionDatosListener importacionDatosListener) {
		ResultadoTipado<List<Lectura>> globalResult = new ResultadoTipado<List<Lectura>>(
				new ArrayList<Lectura>());
		ResultadoTipado<List<Lectura>> result;
		if (importacionDatosListener != null)
			importacionDatosListener.onImportacionIniciada();

		for (AsignacionRuta assignedRoute : assignedRoutes) {
			result = importarLecturasDeRuta(conector, assignedRoute);
			// no tiene lecturas en la ruta
			if (result.getResultado() != null
					&& result.getResultado().size() == 0)
				result.agregarError(new RutaAsignadaSinLecturasException(
						assignedRoute));
			// copiando errores
			globalResult.agregarErrores(result.getErrores());
			if (!globalResult.tieneErrores())
				globalResult.getResultado().addAll(result.getResultado());
			else
				// rompe ciclo si hay errores
				break;
		}

		if (importacionDatosListener != null)
			importacionDatosListener.onImportacionFinalizada(globalResult);
		return globalResult;
	}

	/**
	 * Importa la informacón general de lecturas de una ruta asignada
	 * 
	 * @param username
	 * @param password
	 * @param LecturaRDA
	 * @param assignedRoute
	 * @return {@link ResultadoTipado} con el resultado de las lecturas de la
	 *         ruta asignada al usuario
	 */
	private ResultadoTipado<List<Lectura>> importarLecturasDeRuta(
			final ConectorBDOracle conector, final AsignacionRuta assignedRoute) {
		Lectura.eliminarLecturasDeRutaAsignada(assignedRoute);
		return new DataImporter().importData(new ImportSource<Lectura>() {
			@Override
			public List<Lectura> requestData() throws ConnectException,
					SQLException {
				return conector.obtenerLecturasPorRuta(assignedRoute);
			}

			@Override
			public void preSaveData(Lectura data) {
			}
		});

	}
}
