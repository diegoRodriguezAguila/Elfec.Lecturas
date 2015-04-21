package com.lecturas.elfec.modelo.validaciones;

/**
 * Es utilizada cuando el IMEI del dispositivo no se encuentra en la base de datos, por lo tanto no esta autorizado
 * <b>código VU-003</b>
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
		return "Este móvil no fue autorizado para utilizar la aplicación o fue dado de baja.";
	}

	@Override
	public boolean esError() {
		return true;
	}

}
