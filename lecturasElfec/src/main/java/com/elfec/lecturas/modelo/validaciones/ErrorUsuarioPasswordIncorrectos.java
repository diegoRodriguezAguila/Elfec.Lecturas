package com.elfec.lecturas.modelo.validaciones;

/**
 * Es utilizada cuando el usuario o el password no corresponden a los de la base de datos. 
 * <b>código VU-002</b>
 * @author drodriguez
 *
 */
public class ErrorUsuarioPasswordIncorrectos implements IValidacionUsuario {

	@Override
	public String obtenerCodigo() {
		return "VU-002";
	}

	@Override
	public String obtenerMensaje() {
		return "Usuario o password incorrectos, por favor revise los datos.";
	}

	@Override
	public boolean esError() {
		return true;
	}

}
