package com.elfec.lecturas.modelo.seguridad;

import java.util.HashMap;
import java.util.List;

import com.elfec.lecturas.modelo.PermisoRestriccion;

/**
 * Es el que maneja los permisos de uso de la aplicación con un perfil dado, es un singletone 
 * y solo puede instanciarse a traves del metodo estatico <b>obtenerAdministradorSeguridad</b>
 * @author drodriguez
 *
 */
public class AdministradorSeguridad {

	private static AdministradorSeguridad administradorSeguridad;
	private HashMap<String, PermisoRestriccion> permisosYRestricciones;
	private String perfil;
	
	/**
	 * Obtiene el administrador de seguridad para el perfil indicado, el perfil deberia sacarse de las variables de sesion
	 * @param perfil
	 * @return
	 */
	public static AdministradorSeguridad obtenerAdministradorSeguridad(String perfil)
	{
		if(administradorSeguridad==null || !administradorSeguridad.correspondeAPerfil(perfil))
			administradorSeguridad = new AdministradorSeguridad(perfil);
		return administradorSeguridad;
	}
	
	/**
	 * Asigna a la instancia actual de administradorSeguridad el valor nulo
	 */
	public static void resetearAdministradorDeSeguridad()
	{
		administradorSeguridad = null;
	}

	/**
	 * Constructor privado para evitar que se instancie el singletone fuera de la clase
	 * @param perfil
	 */
	private AdministradorSeguridad(String perfil)
	{
		this.perfil = perfil;
		this.permisosYRestricciones = new HashMap<String, PermisoRestriccion>();
		cargarPermisos();
	}
	
	/**
	 * Carga los permisos segun el perfil obteniendolos de la tabla de PermisosYRestricciones
	 */
	private void cargarPermisos() 
	{
		List<PermisoRestriccion> listaPermisosYRestricciones = PermisoRestriccion.obtenerPermisosPorPerfil(perfil);
		for(PermisoRestriccion permisoRestriccion : listaPermisosYRestricciones)
		{
			permisosYRestricciones.put(permisoRestriccion.Opcion, permisoRestriccion);
		}
	}

	/**
	 * Verifica si el administrador de seguridad corresponde al perfil proporcionado
	 * @param perfil
	 * @return
	 */
	private boolean correspondeAPerfil(String perfil)
	{
		return this.perfil.equals(perfil);
	}
	
	/**
	 * Verifica si con el perfil actual de seguridad se tiene permiso para la opcion determinada
	 * @param opcion
	 * @return
	 */
	public boolean tienePermiso(String opcion)
	{
		return permisosYRestricciones.get(opcion)!=null;
	}
	/**
	 * Obtiene el valor de la restriccion que corresponda a la opcion proporcionada.
	 * @param opcion
	 * @return el valor de la restriccion para el perfil actual, si no existe dicha restriccion se devuelve el maximo entero Integer.MAX_VALUE
	 */
	public int obtenerRestriccion(String opcion)
	{
		PermisoRestriccion permisoRestriccion = permisosYRestricciones.get(opcion);
		return (permisoRestriccion==null?Integer.MAX_VALUE:permisoRestriccion.Restriccion);
	}
	
}
