package com.elfec.lecturas.modelo.excepciones;

import android.nfc.FormatException;

/**
 * Se lanza cuando el formato de las configuraciones de la bd son incorrectos o
 * no se pueden leer
 * 
 * @author drodriguez
 *
 */
public class OracleBDConfiguracionException extends FormatException {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = 1962477658904144427L;
	private String preMensaje;

	public OracleBDConfiguracionException() {
		this.preMensaje = "";
	}

	public OracleBDConfiguracionException(String preMensaje) {
		this.preMensaje = preMensaje;
	}

	@Override
	public String getMessage() {
		return preMensaje
				+ "Los par�metros de la configuraci�n de conexi�n a la base de datos tienen un formato incorrecto! "
				+ "Puede que el archivo haya sido da�ado o eliminado!";
	}
}
