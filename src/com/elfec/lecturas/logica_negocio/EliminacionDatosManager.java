package com.elfec.lecturas.logica_negocio;

import com.elfec.lecturas.modelo.resultados.ResultadoVoid;
import com.elfec.lecturas.settings.AppPreferences;

/**
 * Se encarga de la lógica de negocio de eliminación de datos
 * 
 * @author drodriguez
 *
 */
public class EliminacionDatosManager {
	/**
	 * Elimina toda la información de la aplicación que se debe eliminar del
	 * dispositivo
	 * 
	 * @return
	 */
	public ResultadoVoid eliminarTodosLosDatos() {
		ResultadoVoid result = new ResultadoVoid();
		try {
			GestionadorBDSQLite.eliminarTodosLosDatos();
			AppPreferences.instance().eliminarPreferenciasDeInfoReqUnaVez();
		} catch (Exception e) {
			e.printStackTrace();
			result.agregarError(new RuntimeException(
					"Ocurrió un error al eliminar la información local! "
							+ "Es probable que la información se haya corrompido, porfavor elimine los datos desde el "
							+ "administrador de aplicaciones de Android! Info. adicional: "
							+ e.getMessage()));
		}

		return result;
	}
}
