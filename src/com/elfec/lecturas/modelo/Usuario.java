package com.elfec.lecturas.modelo;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import android.content.Context;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.elfec.lecturas.modelo.flowfactory.validacionusuario.FlujoPasosValidacionUsuario;
import com.elfec.lecturas.modelo.validaciones.IValidacionUsuario;

/**
 * Almacena la información sobre los usuarios del sistema, una vez que un usuario inicia sesión en la red interna de elfec
 * se copia su información de la tabla MOVILES.USUARIOS_APP de la BD Moviles de Oracle
 * @author drodriguez
 */
@Table(name = "Usuarios")
public class Usuario extends Model{
	
	@Column(name = "Usuario", index = true,notNull=true)
	public String Usuario;
	
	@Column(name = "Password",notNull=true) 
	public String Password;
	
	@Column(name = "FechaSincronizacion") 
	public Date FechaSincronizacion;
	
	@Column(name = "Estado",notNull=true) 
	public int Estado;
	
	@Column(name = "Perfil",notNull=true) 
	public String Perfil;
	
	@Column(name = "RangoDias",notNull=true) 
	public int RangoDias;
	/**
	 * Indica si se le obligará al usuario a tener el GPS encendido para realizar lecturas
	 * si está en 1, se le obligará.
	 */
	@Column(name = "RequiereGPS",notNull=true) 
	public int RequiereGPS;
	/**
	 * Si está en 1 se le obligará al usuario a tener el 3G de datos moviles encendido para realizar lecturas.
	 */
	@Column(name = "Requiere3G",notNull=true) 
	public int Requiere3G;
	
	public Usuario()
	{
		super();
		RangoDias = 1;
		RequiereGPS = 1;
		Requiere3G = 1;
	}
	
	public Usuario(ResultSet rs) throws SQLException
	{
		super();
		Usuario = rs.getString("USUARIO");
		Estado = rs.getInt("ESTADO");
		Perfil = rs.getString("PERFIL");
		RangoDias = rs.getInt("SESION_DIAS");
		RequiereGPS = rs.getInt("GPS");
		Requiere3G = rs.getInt("DATOS_MOVILES_3G");
	}
	
	/**
	 * Obtiene todos los usuarios registrados en el movil
	 * @return Retorna una lista con todos los usuarios de la base de datos del móvil**/
	public static List<Usuario> obtenerTodosLosUsuarios() {
	    return new Select()
	        .from(Usuario.class)
	        .execute();
	}
	
	/**
	 * Accede a la base de datos y obtiene el usuario que corresponde al nombre de usuario provisto
	 * @return El usuario con el nombre de usuario correspondiente
	 * **/
	public static Usuario obtenerUsuario(String usuario) {
	    return new Select()
	        .from(Usuario.class).where("Usuario='"+usuario+"'")
	        .executeSingle();
	}
	
	/**
	 * Valida el usuario para iniciar sesión
	 * @param usuario El nombre de usuario que se quiere validar
	 * @param password El password (sin encriptar) que se quiere validar
	 * @param imei El IMEI del telefono
	 * @param fechaSinc 
	 * @return Una clase que implemente IValidacionUsuario, estas se componen de codigo y mensaje e indican el resultado del proceso de validación**/
	public static IValidacionUsuario validar(String usuario, String password, String imei, Date fechaSinc, Context context)
	{
		return FlujoPasosValidacionUsuario.validarUsuario(usuario, password, imei, fechaSinc, context);
	}
	
	/**
	 * Obtiene la cadena hash aplicando SHA-256 al password
	 * @param password
	 * @return
	 */
	public static String hash(String password) {
	    MessageDigest sha256;
		try {
			sha256 = MessageDigest.getInstance("SHA-256");
		    byte[] passBytes = password.getBytes();
		    byte[] passHash = sha256.digest(passBytes);
		    return new String(passHash, "UTF-8");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}     
		return null;
	}

}
