package com.elfec.lecturas.modelo.validaciones;

/**
 * Es utilizada cuando el usuario del empleado no fue dado de alta, o fue dado de baja, por tanto no puede usar el sistema.
 * <b>código VU-005</b>
 * @author drodriguez
 *
 */
public class ErrorEmpleadoInhabilitado implements IValidacionUsuario{

	@Override
	public String obtenerCodigo() {
		return "VU-004";
	}

	@Override
	public String obtenerMensaje() {
		return "El usuario aún no fue dado de alta, o fue dado de baja, no se le permite ingresar.";
	}

	@Override
	public boolean esError() {
		return true;
	}

}
