package com.elfec.lecturas.modelo.interfaces;

import com.elfec.lecturas.modelo.enums.EstadoExportacion;

/**
 * Interfaz que identifica a las clases que son exportables
 * 
 * @author drodriguez
 *
 */
public interface IExportable {
	/**
	 * Asigna el estado de exportación
	 * 
	 * @param estadoExportacion
	 */
	public void setExportStatus(EstadoExportacion estadoExportacion);

	/**
	 * Obtiene una cadena describiendo el registro que se exporta, se utiliza
	 * para los errores
	 * 
	 * @return
	 */
	public String getRegistryResume();
}
