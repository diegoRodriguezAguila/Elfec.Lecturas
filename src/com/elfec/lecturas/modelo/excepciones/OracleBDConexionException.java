package com.elfec.lecturas.modelo.excepciones;

/**
 * Se lanza cuando por algún motivo se interrumpió la conexión a la bd
 * 
 * @author drodriguez
 *
 */
public class OracleBDConexionException extends Exception {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = 740114539718064027L;
	private String preMensaje;

	public OracleBDConexionException() {
		this.preMensaje = "";
	}

	public OracleBDConexionException(String preMensaje) {
		this.preMensaje = preMensaje;
	}

	@Override
	public String getMessage() {
		return preMensaje
				+ "No se pudo establecer conexión con el servidor o ésta se interrumpió. ¡Asegurese de estar conectado a la red de la empresa!";
	}
}
