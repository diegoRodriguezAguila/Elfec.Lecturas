package com.elfec.lecturas.logica_negocio;

import com.elfec.lecturas.acceso_remoto_datos.ConectorBDOracle;
import com.elfec.lecturas.logica_negocio.intercambio_datos.DataImporter;
import com.elfec.lecturas.modelo.AsignacionRuta;
import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.Potencia;
import com.elfec.lecturas.modelo.eventos.ImportacionDatosListener;
import com.elfec.lecturas.modelo.excepciones.PotenciaSinLecturaException;
import com.elfec.lecturas.modelo.intercambio_datos.ImportSource;
import com.elfec.lecturas.modelo.resultados.ResultadoTipado;
import com.elfec.lecturas.modelo.resultados.ResultadoVoid;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Se encarga de la lógica de negocio de las potencias LECTURSP
 * 
 * @author drodriguez
 *
 */
public class PotenciasManager {
	/**
	 * Importa toda la información de potencias de las lecturas de las rutas
	 * asignadas al usuario para la fecha actual.<br>
	 * <b>Nota.-</b> La importación incluye la consulta remota y el guardado
	 * local de los datos
	 *
	 * @param conector
	 * @param importacionDatosListener
	 *            {@link ImportacionDatosListener}
	 * @return {@link ResultadoTipado} con el resultado de la las lecturas de la
	 *         lista de rutas asignadas al usuario
	 */
	public ResultadoTipado<List<Potencia>> importarPotenciasDeRutasAsignadas(
			ConectorBDOracle conector, List<AsignacionRuta> assignedRoutes,
			final String inClausula,
			ImportacionDatosListener importacionDatosListener) {
		ResultadoTipado<List<Potencia>> globalResult = new ResultadoTipado<List<Potencia>>(
				new ArrayList<Potencia>());
		ResultadoTipado<List<Potencia>> result;
		if (importacionDatosListener != null)
			importacionDatosListener.onImportacionIniciada();

		for (AsignacionRuta assignedRoute : assignedRoutes) {
			result = importarPotenciasDeRuta(conector, assignedRoute,
					inClausula);
			// copiando errores
			globalResult.agregarErrores(result.getErrores());
			if (!globalResult.tieneErrores())
				globalResult.getResultado().addAll(result.getResultado());
			else
				break; // rompe ciclo si hay errores
		}
		ResultadoVoid asignacionRes = asignarPotenciasALecturas(globalResult
				.getResultado());
		globalResult.agregarErrores(asignacionRes.getErrores());
		if (importacionDatosListener != null)
			importacionDatosListener.onImportacionFinalizada(globalResult);
		return globalResult;
	}

	/**
	 * Importa la informacón de las potencias de lecturas de una ruta asignada
	 *
	 * @param conector
	 * @param assignedRoute
	 * @param inClausula
	 * @return {@link ResultadoTipado} con el resultado de las lecturas de la
	 *         ruta asignada al usuario
	 */
	private ResultadoTipado<List<Potencia>> importarPotenciasDeRuta(
			final ConectorBDOracle conector,
			final AsignacionRuta assignedRoute, final String inClausula) {
		ResultadoTipado<List<Potencia>> resultado = new ResultadoTipado<List<Potencia>>();
		try {
			Potencia.eliminarPotenciasDeRutaAsignada(assignedRoute, inClausula);
			resultado = new DataImporter()
					.importData(new ImportSource<Potencia>() {
						@Override
						public List<Potencia> requestData()
								throws ConnectException, SQLException {
							return conector.obtenerPotenciasPorRuta(
									assignedRoute.Ruta, inClausula);
						}

						@Override
						public void preSaveData(Potencia data) {
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
			resultado.agregarError(e);
		}
		return resultado;
	}

	/**
	 * Asigna las potencias a las lecturas
	 * 
	 * @param potencias
	 * @return {@link ResultadoVoid}
	 */
	private ResultadoVoid asignarPotenciasALecturas(List<Potencia> potencias) {
		ResultadoVoid resultado = new ResultadoVoid();
		Lectura lect;
		for (Potencia potencia : potencias) {
			lect = Lectura.buscarPorNUS(potencia.Suministro);
			if (lect != null) {
                lect.PotenciaLectura = potencia;
                lect.save();
            }
			else {
				resultado.agregarError(new PotenciaSinLecturaException(
						potencia.Suministro));
				break;
			}
		}
		return resultado;
	}
}
