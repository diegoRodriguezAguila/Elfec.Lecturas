package com.lecturas.elfec.modelo.validaciones;

import com.lecturas.elfec.modelo.Ordenativo;

/**
 * Es utilizada cuando el consumo registrado es elevado.
 * <b>código VL-004</b>
 * @author drodriguez
 */
public class AdvConsumoElevado implements IValidacionLectura {

	@Override
	public String obtenerCodigo() {
		return "VL-004";
	}

	@Override
	public String obtenerMensaje() {
		return "Consumo elevado, revise";
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
