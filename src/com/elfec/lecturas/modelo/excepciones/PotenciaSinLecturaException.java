package com.elfec.lecturas.modelo.excepciones;


public class PotenciaSinLecturaException extends Exception {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -6319695251233698862L;
	private long nus;

	public PotenciaSinLecturaException(long nus) {
		this.nus = nus;
	}

	@Override
	public String getMessage() {
		return "La potencia del suministro: <b>"
				+ nus
				+ "</b> no pertenece a ninguna lectura descargada."
				+ "Puede que haya un problema en la información del servidor, contacte con TI";
	}
}
