package com.elfec.lecturas.modelo.intercambio_datos;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.List;

import com.activeandroid.Model;
import com.elfec.lecturas.modelo.interfaces.IExportable;

/**
 * Interfaz que utiliza DataExporter para determinar la fuente de los datos que
 * se quieren exportar y el método de exportación
 * 
 * @author drodriguez
 *
 * @param <T>
 */
public interface ExportSpecs<T extends Model & IExportable> {
	/**
	 * Obtiene la información que se exportará
	 * 
	 * @return Lista
	 */
	public List<T> requestDataToExport();

	/**
	 * Método que se llama para exportar la información
	 * 
	 * @return el numero de filas exportadas exitosamente, debería ser 1
	 */
	public int exportData(T data) throws ConnectException, SQLException;
}
