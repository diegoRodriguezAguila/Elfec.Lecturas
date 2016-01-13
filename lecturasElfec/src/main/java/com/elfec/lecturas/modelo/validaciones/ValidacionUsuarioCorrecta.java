package com.elfec.lecturas.modelo.validaciones;
/**
 * Es utilizada cuando la validacion de un usuario fue exitosa y no se encontraron errores. 
 * <b>código VU-001</b>
 * @author drodriguez
 *
 */
public class ValidacionUsuarioCorrecta implements IValidacionUsuario{

	@Override
	public String obtenerCodigo() {
		return "VU-001";
	}

	@Override
	public String obtenerMensaje() {
		return "Usuario validado con exito!";
	}

	@Override
	public boolean esError() {
		return false;
	}

}
