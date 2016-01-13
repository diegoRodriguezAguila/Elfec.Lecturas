package com.elfec.lecturas.modelo.validaciones;

import com.elfec.lecturas.modelo.Ordenativo;

/**
 * Es utilizada cuando el consumo registrado es bajo.
 * <b>código VL-003</b>
 * @author drodriguez
 */
public class AdvConsumoBajo implements IValidacionLectura {

	@Override
	public String obtenerCodigo() {
		return "VL-003";
	}

	@Override
	public String obtenerMensaje() {
		return "Consumo bajo, revise";
	}


	@Override
	public boolean esAdvertencia() {
		return true;
	}

	@Override
	public Ordenativo obtenerOrdenativo() {
		return Ordenativo.obtenerOrdenativoPorCodigo(14);//Ord de consumo fuera de rango
	}

}
