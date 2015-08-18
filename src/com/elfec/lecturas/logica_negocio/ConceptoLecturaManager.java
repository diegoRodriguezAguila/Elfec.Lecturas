package com.elfec.lecturas.logica_negocio;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.elfec.lecturas.acceso_remoto_datos.ConectorBDOracle;
import com.elfec.lecturas.logica_negocio.intercambio_datos.DataImporter;
import com.elfec.lecturas.modelo.AsignacionRuta;
import com.elfec.lecturas.modelo.BaseCalculoConcepto;
import com.elfec.lecturas.modelo.ConceptoLectura;
import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.Potencia;
import com.elfec.lecturas.modelo.eventos.ImportacionDatosListener;
import com.elfec.lecturas.modelo.intercambio_datos.ImportSource;
import com.elfec.lecturas.modelo.resultados.ResultadoTipado;

/**
 * Maneja la logica de negocio de LECTURASCONCEPTOS
 * 
 * @author drodriguez
 *
 */
public class ConceptoLecturaManager {
	/**
	 * Importa toda la información de LECTURASCONCEPTOS de las rutas asignadas
	 * al usuario para la fecha actual.<br>
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
	public ResultadoTipado<List<ConceptoLectura>> importarConceptosLecturasDeRutasAsignadas(
			ConectorBDOracle conector, List<AsignacionRuta> assignedRoutes,
			final String inClausula,
			ImportacionDatosListener importacionDatosListener) {
		ResultadoTipado<List<ConceptoLectura>> globalResult = new ResultadoTipado<List<ConceptoLectura>>(
				new ArrayList<ConceptoLectura>());
		ResultadoTipado<List<ConceptoLectura>> result;
		if (importacionDatosListener != null)
			importacionDatosListener.onImportacionIniciada();

		for (AsignacionRuta assignedRoute : assignedRoutes) {
			result = importarConceptosLecturasDeRuta(conector, assignedRoute,
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
	 * Importa la informacón de LECTURASCONCEPTOS de una ruta asignada
	 * 
	 * @param username
	 * @param password
	 * @param LecturaRDA
	 * @param assignedRoute
	 * @param inClausula
	 * @return {@link ResultadoTipado} con el resultado de las lecturas de la
	 *         ruta asignada al usuario
	 */
	private ResultadoTipado<List<ConceptoLectura>> importarConceptosLecturasDeRuta(
			final ConectorBDOracle conector,
			final AsignacionRuta assignedRoute, final String inClausula) {
		Potencia.eliminarPotenciasDeRutaAsignada(assignedRoute, inClausula);
		return new DataImporter()
				.importData(new ImportSource<ConceptoLectura>() {
					@Override
					public List<ConceptoLectura> requestData()
							throws ConnectException, SQLException {
						return conector.obtenerConceptosPorRuta(
								assignedRoute.Ruta, inClausula);
					}

					@Override
					public void preSaveData(ConceptoLectura conc) {
						Lectura lectura = ConceptoLectura.obtenerLectura(
								conc.Suministro, conc.Mes, conc.Anio);
						BaseCalculoConcepto baseCalculoConcepto = BaseCalculoConcepto
								.obtenerBaseCalculoConcepto(conc.ConceptoCodigo);
						conc.Lectura = lectura;
						conc.OrdenImpresion = baseCalculoConcepto == null ? null
								: baseCalculoConcepto.BaseCalculo.OrdenImpresion;
						conc.AreaImpresion = baseCalculoConcepto == null ? null
								: baseCalculoConcepto.Concepto.AreaImpresion;
					}
				});

	}
}
