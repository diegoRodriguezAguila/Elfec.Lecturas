package com.elfec.lecturas.modelo.validaciones;

import com.elfec.lecturas.modelo.Ordenativo;

/**
 * Es utilizada cuando la lectura introducida es menor a la anterior
 * <b>código VL-002</b>
 * @author drodriguez
 */
public class AdvIndiceMenorActual implements IValidacionLectura{

	@Override
	public String obtenerCodigo() {
		return "VL-002";
	}

	@Override
	public String obtenerMensaje() {
		return "Indice actual menor al anterior";
	}


	@Override
	public boolean esAdvertencia() {
		return true;
	}
	
	@Override
	public Ordenativo obtenerOrdenativo() {
		return Ordenativo.obtenerOrdenativoPorCodigo(85);//Ord de volteo de medidor
	}

}
