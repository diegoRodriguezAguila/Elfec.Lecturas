package com.elfec.lecturas.modelo.excepciones;

/**
 * Excepci�n que se lanza cuando ocurri� un error al realizar la exportaci�n de
 * informaci�n
 * 
 * @author drodriguez
 *
 */
public class ExportacionException extends Exception {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -6608414009181842278L;

	private String errorInfo;

	public ExportacionException(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	@Override
	public String getMessage() {
		return "Ocurri� un error en el servidor al realizar la exportaci�n! Informaci�n del error: "
				+ errorInfo;
	}

}
