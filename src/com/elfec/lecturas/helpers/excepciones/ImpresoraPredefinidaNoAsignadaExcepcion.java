package com.elfec.lecturas.helpers.excepciones;

/**
 * Es lanzada cuando se intenta realizar una impresión y la impresora predefinida no se asignó
 * @author drodriguez
 *
 */
public class ImpresoraPredefinidaNoAsignadaExcepcion extends Exception {

	private static final long serialVersionUID = 1L;
	@Override
	public String getMessage()
	{
		return "No se seleccionó una impresora!";
	}

}
