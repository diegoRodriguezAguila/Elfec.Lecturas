package com.elfec.lecturas.controlador.observers;

import java.util.List;

/**
 * Representa una vista o cualquier observador que quiera recibir
 * actualizaciones durante un evento de exportación de datos
 * 
 * @author drodriguez
 *
 */
public interface IDataExportationObserver {
	/**
	 * Muestra un mensaje de espera al usuario
	 */
	public void showExportationWaiting();

	/**
	 * Acualiza el mensaje de espera del usuario a un mensaje de descarga con
	 * porcentaje total de datos
	 * 
	 * @param strId
	 * @param totalData
	 */
	public void updateExportationWaiting(int strId, int totalData);

	/**
	 * Acualiza el mensaje de espera del usuario a un mensaje de descarga
	 * 
	 * @param strId
	 */
	public void updateExportationWaiting(int strId);

	/**
	 * Acualiza el mensaje de la barra de progreso
	 * 
	 * @param dataCount
	 * @param totalData
	 */
	public void updateExportationProgress(int dataCount, int totalData);

	/**
	 * Borra el mensaje de espera
	 */
	public void hideWaiting();

	/**
	 * Muestra un mensaje de error al usuario en caso de haber
	 * 
	 * @param titleStrId
	 *            id del recurso string del titulo del mensaje de error
	 * @param errors
	 */
	public void showErrors(int titleStrId, int iconDrawableId,
			List<Exception> errors);

	/**
	 * Indica al usuario que la exportación fué exitosa
	 */
	public void notifySuccessfulExportation();
}
