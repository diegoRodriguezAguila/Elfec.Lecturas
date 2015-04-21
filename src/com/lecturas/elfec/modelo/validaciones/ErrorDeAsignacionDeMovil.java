package com.lecturas.elfec.modelo.validaciones;

/**
 * Es utilizada cuando el IMEI del dispositivo no se encuentra en la base de datos, por lo tanto no esta autorizado
 * <b>c�digo VU-003</b>
 * @author drodriguez
 *
 */
public class ErrorDeAsignacionDeMovil implements IValidacionUsuario {

	@Override
	public String obtenerCodigo() {
		return "VU-003";
	}

	@Override
	public String obtenerMensaje() {
		return "Este m�vil no fue autorizado para utilizar la aplicaci�n o fue dado de baja.";
	}

	@Override
	public boolean esError() {
		return true;
	}

}
