package com.lecturas.elfec.modelo.seguridad;

/**
 * Define los permisos que son otorgables a los distintos perfiles de usuario.
 * @author drodriguez
 *
 */
public class Permisos {

	/**
	 * Permiso de forzar una descarga aun cuando no se hayan completado todas las lecturas.
	 */
	public static final String FORZAR_DESCARGA = "FORZAR_DESCARGA";
	/**
	 * Permiso de eliminar todos los datos de la aplicación, incluye datos diarios y mensuales, asi como los usuarios sincronizados.
	 */
	public static final String ELIMINAR_DATOS = "ELIMINAR_DATOS";
	/**
	 * Permiso de configurar los datos para la conexión del servidor, esto incluye la ip, el puerto, el nombre del servicio
	 * , el rol, y el password del rol.
	 */
	public static final String CONFIGURAR_SERVIDOR = "CONFIGURAR_SERVIDOR";
}
