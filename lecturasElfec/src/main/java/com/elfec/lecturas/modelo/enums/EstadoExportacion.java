package com.elfec.lecturas.modelo.enums;
/**
 * Define los estados de la exportaci�n de datos
 * @author drodriguez
 *
 */
public enum EstadoExportacion {
	/**
	 * Estado de que NO se realiz� la exportaci�n
	 */
	NO_EXPORTADO,
	/**
	 * Estado de que si se export�
	 */
	EXPORTADO;
	/**
	 * Obtiene el estado de exportaci�n, equivalente al short provisto
	 * @param status
	 * @return
	 */
	public static EstadoExportacion get(short status)
	{
		return EstadoExportacion.values()[status];
	}
	
	/**
	 * Convierte el estado de exportaci�n a su short equivalente
	 * @return Short equivalente al estado
	 */
	public short toShort()
	{
		return (short)this.ordinal();
	}
}
