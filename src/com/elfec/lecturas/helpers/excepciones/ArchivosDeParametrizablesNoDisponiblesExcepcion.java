package com.elfec.lecturas.helpers.excepciones;
/**
 * Es lanzada cuando Los archivos que guardan las variables parametrizables 
 * no pudieron ser encontrados o hubo un error en la lectura de ellos
 * @author drodriguez
 *
 */
public class ArchivosDeParametrizablesNoDisponiblesExcepcion extends
		ExceptionInInitializerError {
	private static final long serialVersionUID = 1L;
	private String archivo;
	public ArchivosDeParametrizablesNoDisponiblesExcepcion(String archivoOrigenError) {
		this.archivo = archivoOrigenError;
	}
	@Override
	public String getMessage()
	{
		return "Los archivos que guardan las variables parametrizables no pudieron ser encontrados o hubo un error en la lectura de ellos!" +
				" revise la existencia e integridad del archivo: "+archivo;
	}
}
