package com.elfec.lecturas.modelo.enums;
/**
 * Define los estados de la exportación de datos
 * @author drodriguez
 *
 */
public enum EstadoExportacion {
	/**
	 * Estado de que NO se realizó la exportación
	 */
	NO_EXPORTADO,
	/**
	 * Estado de que si se exportó
	 */
	EXPORTADO;
	/**
	 * Obtiene el estado de exportación, equivalente al short provisto
	 * @param status
	 * @return
	 */
	public static EstadoExportacion get(short status)
	{
		return EstadoExportacion.values()[status];
	}
	
	/**
	 * Convierte el estado de exportación a su short equivalente
	 * @return Short equivalente al estado
	 */
	public short toShort()
	{
		return (short)this.ordinal();
	}
}
