package com.elfec.lecturas.modelo.excepciones;

/**
 * Se lanza cuando por alg�n motivo se interrumpi� la conexi�n a la bd
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
				+ "No se pudo establecer conexi�n con el servidor o �sta se interrumpi�. �Asegurese de estar conectado a la red de la empresa!";
	}
}
