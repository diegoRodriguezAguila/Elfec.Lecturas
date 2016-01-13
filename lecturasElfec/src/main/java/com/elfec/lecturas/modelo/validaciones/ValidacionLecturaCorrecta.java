package com.elfec.lecturas.modelo.validaciones;

import com.elfec.lecturas.modelo.Ordenativo;

/**
 * Es utilizada cuando la validacion de una lectura fue exitosa y no se encontraron errores. 
 * <b>código VL-001</b>
 * @author drodriguez
 */
public class ValidacionLecturaCorrecta implements IValidacionLectura {

	@Override
	public String obtenerCodigo() {
		return "VL-001";
	}

	@Override
	public String obtenerMensaje() {
		return "Lectura correcta!";
	}


	@Override
	public boolean esAdvertencia() {
		return false;
	}
	
	@Override
	public Ordenativo obtenerOrdenativo() {
		return null;
	}

}
