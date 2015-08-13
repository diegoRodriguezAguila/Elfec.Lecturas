package com.elfec.lecturas.modelo.flowfactory.validacionusuario;

import java.util.Date;
import java.util.Locale;

import android.content.Context;

import com.elfec.lecturas.helpers.ConectorBDOracle;
import com.elfec.lecturas.helpers.VariablesDeSesion;
import com.elfec.lecturas.modelo.Usuario;
import com.elfec.lecturas.modelo.validaciones.IValidacionUsuario;

/**
 * Clase estática que se encarga de iniciar el flujo de pasos para validar al usuario
 * @author drodriguez
 *
 */
public class FlujoPasosValidacionUsuario 
{
	/**
	 * El nombre de usuario a validar.
	 */
	public static String usuario;
	/**
	 * El password del usuario a validar.
	 */
	public static String password;
	/**
	 * El imei del telefono.
	 */
	public static String imei;
	/**
	 * La fecha de sincronización del telefono con el servidor.
	 */
	public static Date fechaSinc;
	/**
	 * La actividad desde la cual se invoca.
	 */
	public static Context context;
	/**
	 * Usuario encontrado, este campo se llena con el usuario encontrado en la base de datos local (SQLite) 
	 * o la remota (Oracle).
	 */
	public static Usuario usuarioEncontrado;
	/**
	 * En caso de necesitarse es la conexión a la base de datos Oracle.
	 */
	public static ConectorBDOracle conexion;
	
	/**
	 * Inicia el flujo de pasos para validar el usuario
	 * @param usuario
	 * @param password
	 * @param imei
	 * @param fechaSinc
	 * @param context
	 * @return
	 */
	public static IValidacionUsuario validarUsuario(String usuario, String password, String imei, Date fechaSinc, Context context)
	{
		FlujoPasosValidacionUsuario.usuario = usuario.toUpperCase(Locale.getDefault());
		FlujoPasosValidacionUsuario.password = password;
		FlujoPasosValidacionUsuario.imei = imei;
		FlujoPasosValidacionUsuario.fechaSinc = fechaSinc;
		FlujoPasosValidacionUsuario.context = context;
		
		PasoValidacionUsuario pasoActual = new PasoBusquedaUsuario();
		PasoValidacionUsuario pasoSiguiente = null;
		while(pasoActual!=null)
		{
			pasoSiguiente = pasoActual.procesarPaso();
			if(pasoSiguiente==null)//el flujo termina
			{
				return pasoActual.obtenerResultadoValidacion();
			}
			pasoActual = pasoSiguiente;
		}
		return null;
	}
	
	/**
	 * Una vez finalizada la validación del usuario y en caso de que esta sea correcta se debe guardar los datos de usuario
	 * recopilados durante la validación, este metodo se encarga de dicho proceso
	 */
	public static void guardarUsuario()
	{
		usuarioEncontrado.FechaSincronizacion = fechaSinc;
		usuarioEncontrado.Password = Usuario.hash(password);
		usuarioEncontrado.save();
		conexion.importarPermisosPerfilUsuario(usuarioEncontrado.Perfil);
		conexion.importarPreferenciasUIUsuario(usuarioEncontrado.Usuario);
		conexion.importarTokensUsuario(usuarioEncontrado);
		VariablesDeSesion.setPerfilUsuario(usuarioEncontrado.Perfil);
	}
}
