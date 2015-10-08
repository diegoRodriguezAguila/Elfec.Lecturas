package com.elfec.lecturas.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import org.json.JSONException;
import org.json.JSONObject;

import com.elfec.lecturas.settings.ConstantesDeEntorno;

import android.os.Environment;
import android.util.Log;

/**
 * Se encarga de operaciones con JSON, como los archivos de parametros y su conversion
 * @author drodriguez
 *
 */
public class ManejadorJSON {

	/**
	 * Convierte un resultset en un objeto JSON
	 * @param rs
	 * @return
	 * @throws SQLException
	 * @throws JSONException
	 */
	public static JSONObject convertirResultSetAJSON(ResultSet rs) throws SQLException, JSONException
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		int numCols = rsmd.getColumnCount();
		JSONObject obj = new JSONObject();
		String nombreColumna;
		int tipo;
		for (int i = 1; i <= numCols; i++) 
		{
			nombreColumna = rsmd.getColumnName(i);
			tipo = rsmd.getColumnType(i);
			if(tipo==Types.BIGINT || tipo==Types.INTEGER || tipo==Types.SMALLINT || tipo==Types.TINYINT)
			{
				obj.put(nombreColumna, rs.getLong(nombreColumna));
			}
			else if(tipo==Types.DECIMAL || tipo==Types.NUMERIC || tipo==Types.DOUBLE || tipo==Types.FLOAT)
			{
				obj.put(nombreColumna, rs.getDouble(nombreColumna));
			}
			else if(tipo==Types.BOOLEAN)
			{
				obj.put(nombreColumna, rs.getBoolean(nombreColumna));
			}
			else
			{
				obj.put(nombreColumna, rs.getString(nombreColumna));
			}
		}
		return obj;
	}
	
	/**
	 * Guarda una cadena en formato JSON en el archivo con el nombre proporcionado (SIN LA EXTENSION .JS, el nombre plano) 
	 * en el directorio principal de almacenamiento interno de la aplicación definido en las constantes de entorno como "directorioAplicacion"
	 * @param jsonStr
	 * @param nombreArch
	 * @see ConstantesDeEntorno.directorioAplicacion
	 * @return
	 */
	public static boolean guardarJSONEnArchivo(String jsonStr, String nombreArch)
	{
		try {
			File mediaStorageDir = new File(Environment.getExternalStorageDirectory()+File.separator+ConstantesDeEntorno.directorioAplicacion);
		    if (! mediaStorageDir.exists()){
		        if (! mediaStorageDir.mkdirs()){
		            Log.d("Elfec Lecturas", "failed to create directory");
		            return false;
		        }
		    }
		    File mediaFile = new File(mediaStorageDir.getPath() + File.separator +nombreArch+".js");
			FileOutputStream fOut = new FileOutputStream(mediaFile);
			OutputStreamWriter osw = new OutputStreamWriter(fOut); 
			  osw.write(jsonStr);
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
	
	public static String LeerArchivoJSON(String nombreArch)
	{
		try {
			File mediaStorageDir = new File(Environment.getExternalStorageDirectory()+File.separator+ConstantesDeEntorno.directorioAplicacion);
		    if (! mediaStorageDir.exists()){
		        if (! mediaStorageDir.mkdirs()){
		            Log.d("Elfec Lecturas", "failed to create directory");
		            return null;
		        }
		    }
		    File mediaFile = new File(mediaStorageDir.getPath() + File.separator +nombreArch+".js");
			FileInputStream fIn = new FileInputStream(mediaFile);
			InputStreamReader isr = new InputStreamReader(fIn); 
			char[] buffer = new char[5000];
			isr.read(buffer);
			String cad = new String(buffer);
			isr.close();
			return cad;
		} catch (FileNotFoundException e) {
			Log.println(1, "FileNotFoundException", e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.println(1, "IOException", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean eliminarArchivoJSON(String nombreArch)
	{
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory()+File.separator+ConstantesDeEntorno.directorioAplicacion);
		if (! mediaStorageDir.exists())
		{
		    return true;
		}
		File mediaFile = new File(mediaStorageDir.getPath() + File.separator +nombreArch+".js");
		return mediaFile.delete();
	}
	
}
