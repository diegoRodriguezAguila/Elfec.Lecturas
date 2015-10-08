package com.elfec.lecturas.modelo.enums;

/**
 * Representa los estados de las asignaciones de rutas
 * 
 * @author drodriguez
 *
 */
public enum EstadoAsignacionRuta {
	/**
	 * Estado de ruta asignada, no definido
	 */
	INDEFINIDO,
	/**
	 * Estado de ruta asignada, pendiente de carga
	 */
	ASIGNADA,
	/**
	 * Estado de ruta importada al móvil
	 */
	IMPORTADA,
	/**
	 * Estado de ruta exportada del móvil al servidor
	 */
	EXPORTADA,
	/**
	 * Estado de ruta exportada del móvil al servidor para relectura
	 */
	EXPORTADA_CON_RELECTURAS,
	/**
	 * Estado de ruta exportada del servidor al billing
	 */
	EXPORTADA_BILLING,
	/**
	 * Estado de ruta en servidor asignada para su relectura
	 */
	RELECTURA_ASIGNADA,
	/**
	 * Estado de ruta de relectura importada al móvil
	 */
	RELECTURA_IMPORTADA,
	/**
	 * Estado de ruta de relectura exportada del móvil al servidor
	 */
	RELECTURA_EXPORTADA;

	/**
	 * Obtiene el estado del usuario equivalente al short provisto
	 * 
	 * @param status
	 * @return
	 */
	public static EstadoAsignacionRuta get(short status) {
		return EstadoAsignacionRuta.values()[status];
	}

	/**
	 * Convierte el estado de asignación de ruta actual a su short equivalente
	 * 
	 * @return Short equivalente al estado
	 */
	public short toShort() {
		return (short) (this.ordinal());
	}
}
