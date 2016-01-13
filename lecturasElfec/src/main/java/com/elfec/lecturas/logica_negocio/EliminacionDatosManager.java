package com.elfec.lecturas.logica_negocio;

import com.elfec.lecturas.modelo.resultados.ResultadoVoid;
import com.elfec.lecturas.settings.AppPreferences;

/**
 * Se encarga de la lÃ³gica de negocio de eliminaciÃ³n de datos
 * 
 * @author drodriguez
 *
 */
public class EliminacionDatosManager {
	/**
	 * Elimina toda la informaciÃ³n de la aplicaciÃ³n que se debe eliminar del
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
					"OcurriÃ³ un error al eliminar la informaciÃ³n local! "
							+ "Es probable que la informaciÃ³n se haya corrompido, porfavor elimine los datos desde el "
							+ "administrador de aplicaciones de Android! Info. adicional: "
							+ e.getMessage()));
		}

		return result;
	}
}
