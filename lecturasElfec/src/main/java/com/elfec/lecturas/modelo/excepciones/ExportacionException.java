package com.elfec.lecturas.modelo.excepciones;

/**
 * Excepción que se lanza cuando ocurrió un error al realizar la exportación de
 * información
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
		return "Ocurrió un error en el servidor al realizar la exportación! Información del error: "
				+ errorInfo;
	}

}
