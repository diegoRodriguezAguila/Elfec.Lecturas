package com.elfec.lecturas.acceso_remoto_datos;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Es un objeto estÃ¡tico que sirve para poder obtener y escribir el archivo de configuraciÃ³n que estÃ¡ en JSON bajo la carpeta
 * de assets, el nombre del archivo es <b>config_bd.json</b>.
 * @author drodriguez
 *
 */
public class LectorConfigBD {

	/** Define el nombre del archivo de configuraciÃ³n. **/
	private static final String archivoConfig="config_bd.json";
	private static Context contexto;
	
	/**
	 * Se encarga de devolver un objeto json que representa el archivo de configuraciÃ³n leido.
	 * @param contextoNuevo, es el contexto la actividad de la que se llama, necesaria para obtener el asset.
	 * @return JSONObject que respresenta la configuraciÃ³n actual.
	 * @throws JSONException
	 */
	public static JSONObject obtenerConfiguracion(Context contextoNuevo) throws JSONException
	{
		contexto = contextoNuevo;
		String res=leerArchivoConfiguracion();
		return new JSONObject(res);
	}
	
	/**
	 * Se encarga de escribir la informaciÃ³n de configuraciÃ³n a partir de un JSONObject
	 * @param nuevaConfig, Recibe un objeto JSON que sea valido para la configuracion
	 * @return si se guardÃ³ exitosamente la configuraciÃ³n.
	 */
	public static boolean escribirConfiguracion(JSONObject nuevaConfig)
	{
		try {
			FileOutputStream fOut = contexto.openFileOutput(archivoConfig, Context.MODE_PRIVATE);
			OutputStreamWriter osw = new OutputStreamWriter(fOut); 
			  osw.write(nuevaConfig.toString());
			  osw.flush();
			  osw.close();
			  return true;
		} catch (FileNotFoundException e) {
			Log.println(1, "FileNotFoundException", e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.println(1, "IOException", e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Lee el archivo de configuracion del usuario, busca en la carpeta de la aplicaciÃ³n, caso contrario usa el por defecto de assets
	 * @return retorna la cadena JSON o caso contrario un mensaje de error de que no existe el archivo.
	 */
	private static String leerArchivoConfiguracion()
	{
		try {
			  FileInputStream fIn = contexto.openFileInput(archivoConfig);
			  InputStreamReader isr = new InputStreamReader(fIn);
			  char[] inputBuffer = new char[250];
			  isr.read(inputBuffer);
			  String readString = new String(inputBuffer);
			  isr.close();
			  return readString;
			 } 
			catch (FileNotFoundException e) 
			{
				return leerArchivoConfiguracionAssets();
			} 
			catch (IOException e) 
			{
				Log.println(1, "IOException", e.getMessage());
				e.printStackTrace();
			}
			 return "Error al leer";
	}
	
	/**
	 * Lee el archivo de configuracion de assets, es decir el que viene en el paquete por defecto
	 * @return retorna la cadena JSON o caso contrario un mensaje de error de que no existe el archivo.
	 */
	private static String leerArchivoConfiguracionAssets()
	{
		try 
		{
			InputStream is = contexto.getAssets().open(archivoConfig);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			String bufferString = new String(buffer);
			return bufferString;
		} 
		catch (FileNotFoundException e) 
		{
			Log.println(1, "FileNotFoundException", e.getMessage());
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			Log.println(1, "IOException", e.getMessage());
			e.printStackTrace();
		}
		return "No existe archivo de configuracion";
	}

}
