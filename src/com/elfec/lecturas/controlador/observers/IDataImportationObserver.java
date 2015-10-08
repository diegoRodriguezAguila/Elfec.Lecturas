package com.elfec.lecturas.controlador.observers;

import java.util.List;

/**
 * Representa una vista o cualquier observador que quiera recibir
 * actualizaciones durante un evento de importación de datos
 * 
 * @author drodriguez
 *
 */
public interface IDataImportationObserver {
	/**
	 * Muestra un mensaje de espera al usuario
	 */
	public void showImportationWaiting();

	/**
	 * Acualiza el mensaje de espera del usuario
	 * 
	 * @param msgStrId
	 *            id del recurso string del mensaje
	 */
	public void updateImportationWaiting(int msgStrId);

	/**
	 * Esconde el mensaje de espera
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
	 * Indica al usuario que la importación fué exitosa
	 */
	public void notifySuccessfulImportation();
}
