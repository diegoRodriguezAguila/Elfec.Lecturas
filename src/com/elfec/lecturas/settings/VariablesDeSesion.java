package com.elfec.lecturas.settings;

import java.util.Calendar;
import java.util.Locale;

import com.elfec.lecturas.modelo.SesionUsuario;

/**
 * Guarda variables correspondientes a la sesion iniciada, que se utilizan a lo largo de toda la aplicación
 * por eso es una clase estática
 * @author drodriguez
 *
 */
public class VariablesDeSesion 
{
	/**
	 * Es el cuadro tarifario que corresponde a la fecha actual
	 */
	public static int idCuadroTarifario()
	{
		Calendar calendar = Calendar.getInstance();
		return ((calendar.get(Calendar.YEAR)-2000)*100)+(calendar.get(Calendar.MONTH)+1);
	}
	/**
	 * El usuario que esta logeado actualmente al sistema
	 */
	private static String usuarioLogeado;
	/**
	 * El password del usuario que esta logeado actualmente en el sistema
	 */
	private static String passwordUsuario;
	/**
	 * El imei del celular en el que se ejecuta la aplicación
	 */
	private static String imeiCelular;
	/**
	 * El perfil del usuario que inició sesión
	 */
	private static String perfilUsuario;
	
	
	public static String getUsuarioLogeado() 
	{
		if(usuarioLogeado==null)
		{
			SesionUsuario ultimaSesion = SesionUsuario.recuperarSesionUsuario();
			if(ultimaSesion!=null)
				usuarioLogeado = ultimaSesion.Usuario;
		}
		return usuarioLogeado;
	}

	public static void setUsuarioLogeado(String usuarioLogeado) 
	{
		VariablesDeSesion.usuarioLogeado = usuarioLogeado.toUpperCase(Locale.getDefault());
	}

	public static String getPasswordUsuario() 
	{
		if(passwordUsuario==null)
		{
			SesionUsuario ultimaSesion = SesionUsuario.recuperarSesionUsuario();
			if(ultimaSesion!=null)
				passwordUsuario = ultimaSesion.getPassword();
		}
		return passwordUsuario;
	}

	public static void setPasswordUsuario(String passwordUsuario) 
	{
		VariablesDeSesion.passwordUsuario = passwordUsuario;
	}

	public static String getImeiCelular() 
	{
		if(imeiCelular==null)
		{
			SesionUsuario ultimaSesion = SesionUsuario.recuperarSesionUsuario();
			if(ultimaSesion!=null)
				imeiCelular = ultimaSesion.Imei;
		}
		return imeiCelular;
	}

	public static void setImeiCelular(String imeiCelular) 
	{
		VariablesDeSesion.imeiCelular = imeiCelular;
	}

	public static String getPerfilUsuario() 
	{
		if(perfilUsuario==null)
		{
			SesionUsuario ultimaSesion = SesionUsuario.recuperarSesionUsuario();
			if(ultimaSesion!=null)
				perfilUsuario = ultimaSesion.Perfil;
		}
		return perfilUsuario;
	}

	public static void setPerfilUsuario(String perfilUsuario) 
	{
		VariablesDeSesion.perfilUsuario = perfilUsuario;
	}
}
