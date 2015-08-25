package com.elfec.lecturas.logica_negocio;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.List;

import com.elfec.lecturas.acceso_remoto_datos.ConectorBDOracle;
import com.elfec.lecturas.logica_negocio.intercambio_datos.DataExporter;
import com.elfec.lecturas.modelo.MedidorEntreLineas;
import com.elfec.lecturas.modelo.eventos.ExportacionDatosListener;
import com.elfec.lecturas.modelo.intercambio_datos.ExportSpecs;
import com.elfec.lecturas.modelo.resultados.ResultadoVoid;

/**
 * Capa de lógica de negocio para medidores entre lineas
 * 
 * @author drodriguez
 *
 */
public class MedidoresEntreLineasManager {
	/**
	 * Exporta todas las lecturas entre lineas tomadas
	 * 
	 * @return resultado del acceso remoto a datos
	 */
	public ResultadoVoid exportarLecturasTomadas(
			final ConectorBDOracle conector, ExportacionDatosListener exportListener) {
		return new DataExporter().exportData(
				new ExportSpecs<MedidorEntreLineas>() {

					@Override
					public int exportData(MedidorEntreLineas lecturaELTomada)
							throws ConnectException, SQLException {
						return conector
								.exportarLecturaEntreLineas(lecturaELTomada);
					}

					@Override
					public List<MedidorEntreLineas> requestDataToExport() {
						return MedidorEntreLineas
								.obtenerMedidoresEntreLineasNoEnviados3G();
					}
				}, exportListener);
	}
}
