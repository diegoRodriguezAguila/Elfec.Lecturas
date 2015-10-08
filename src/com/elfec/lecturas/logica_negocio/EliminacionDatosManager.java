package com.elfec.lecturas.logica_negocio;

import com.elfec.lecturas.modelo.resultados.ResultadoVoid;
import com.elfec.lecturas.settings.AppPreferences;

/**
 * Se encarga de la l�gica de negocio de eliminaci�n de datos
 * 
 * @author drodriguez
 *
 */
public class EliminacionDatosManager {
	/**
	 * Elimina toda la informaci�n de la aplicaci�n que se debe eliminar del
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
					"Ocurri� un error al eliminar la informaci�n local! "
							+ "Es probable que la informaci�n se haya corrompido, porfavor elimine los datos desde el "
							+ "administrador de aplicaciones de Android! Info. adicional: "
							+ e.getMessage()));
		}

		return result;
	}
}
