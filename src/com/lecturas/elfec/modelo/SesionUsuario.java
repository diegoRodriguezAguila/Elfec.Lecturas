package com.lecturas.elfec.modelo;

import java.security.Key;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

/**
 * Almacena la información sobre la sesion de usuario actual, <b>no debe instanciarse directamente</b>, en su lugar, deben utilizarse
 * los metodos de iniciar, cerrar y recuperar sesion
 * @author drodriguez
 */
@Table(name = "SesionesDeUsuario")
public class SesionUsuario extends Model {

	@Column(name = "Usuario", index = true, notNull=true, unique=true)
	public String Usuario;
	
	@Column(name = "Password",notNull=true) 
	private byte[] Password;
	
	@Column(name = "Imei",notNull=true) 
	public String Imei;
	
	@Column(name = "Perfil",notNull=true) 
	public String Perfil;
	
	@Column(name = "Fecha", notNull=true) 
	public Date Fecha;
	/**
	 * Crea una sesion para el usuario actual, crea un registro nuevo de seson y cierra cualquier sesion previa del mismo usuario
	 * @param usuario
	 * @param password
	 * @param imei
	 * @param perfil
	 */
	public static void iniciarSesionUsuario(String usuario, String password, String imei, String perfil)
	{
		cerrarSesionUsuario(usuario);//cierra cualquier sesion previa iniciada por el usuario
		SesionUsuario nuevaSesion = new SesionUsuario();
		nuevaSesion.Usuario = usuario;
		nuevaSesion.Imei = imei;
		nuevaSesion.Perfil = perfil;
		nuevaSesion.Fecha = new Date();
		nuevaSesion.setPassword(password);
		nuevaSesion.save();
	}
	/**
	 * Cierra la sesion para el usuario indicado, elimina el registro de la sesion
	 * @param usuario
	 */
	public static void cerrarSesionUsuario(String usuario)
	{
		SesionUsuario ultimaSesion = new Select()
        .from(SesionUsuario.class).where("Usuario=?",usuario)
        .executeSingle();
		if(ultimaSesion!=null)
			ultimaSesion.delete();
	}
	
	/**
	 * Recupera la ultima sesion abierta
	 * @return
	 */
	public static SesionUsuario recuperarSesionUsuario()
	{
		List<SesionUsuario> sesiones =  new Select()
        .from(SesionUsuario.class)
        .execute();
		int tam = sesiones.size();
		if(tam==0)
			return null;
		return sesiones.get(tam-1);
	}
	
	/**
	 * Obtiene el password desencriptado
	 * @return
	 */
	public String getPassword() 
	{
		return desencriptar(Password);
	}
	
	/**
	 * Asigna la variable de password encriptando el parametro provisto
	 * @param password
	 */
	public void setPassword(String password) 
	{
		Password = encriptar(password);
	}
	
	/**
	 * Parte de la clave con la que se encripta, tiene que ser de 16 caracteres de longitud
	 */
	private static final String CLAVE = "ElfecLectur@s!12";
	/**
	 * Encripta el parametro utilizando la <b>Fecha</b> como parte de la clave
	 * @param valor
	 * @return
	 */
	public byte[] encriptar(String valor) 
	{
        byte[] encrypted = null;
        try {
        	DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.getDefault());
            byte[] raw = (df.format(Fecha)+CLAVE).getBytes("UTF-8");
            Key skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = new byte[cipher.getBlockSize()];

            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec,ivParams);
            encrypted  = cipher.doFinal(valor.getBytes("UTF-8"));
            return encrypted;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

	/**
	 * Desencripta el parametro usando la <b>Fecha</b> como parte de la clave
	 * @param encriptado
	 * @return
	 */
    public String desencriptar(byte[] encriptado) 
    {
         byte[] original = null;
         Cipher cipher = null;
        try {
        	DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.getDefault());
            byte[] raw = (df.format(Fecha)+CLAVE).getBytes("UTF-8");
            Key key = new SecretKeySpec(raw, "AES");
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] ivByte = new byte[cipher.getBlockSize()];
            IvParameterSpec ivParamsSpec = new IvParameterSpec(ivByte);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParamsSpec);
            original= cipher.doFinal(encriptado);
            return new String (original, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }  
	
}
