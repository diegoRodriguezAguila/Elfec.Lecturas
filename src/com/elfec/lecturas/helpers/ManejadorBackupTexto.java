package com.elfec.lecturas.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.elfec.lecturas.modelo.backuptexto.IModeloBackupableTexto;

import android.os.Environment;
import android.util.Log;

/**
 * Es el encargado de realizar un backup en archivo de texto del modelo deseado
 * @author drodriguez
 *
 */
public class ManejadorBackupTexto 
{
	private static final String FIN_LINEA = "\r\n";
	
	/**
	 * Guarda una lectura que implemente la interfaz <b>ILecturaBackupTexto</b> en el archivo correspondiente
	 * @param modeloBackupable
	 * @return
	 */
	public static boolean guardarBackupModelo(IModeloBackupableTexto modeloBackupable)
	{
		boolean tieneCabecera = true;
		String nombreArch = modeloBackupable.obtenerNombreArchivoBackup();
		if(!archivoExiste(nombreArch))
		{
			tieneCabecera = guardarLineaTexto(modeloBackupable.obtenerCabeceraBackup()+FIN_LINEA, nombreArch);
		}
		return tieneCabecera && guardarLineaTexto(modeloBackupable.obtenerLineaTextoBackup()+FIN_LINEA, nombreArch);
	}
	
	/**
	 * Se encarga de escribir una linea en el archivo pasado como parametro
	 * @param lineaTexto
	 * @param nombreArch
	 * @return
	 */
	public static boolean guardarLineaTexto(String lineaTexto, String nombreArch)
	{
		try {
			File mediaStorageDir = new File(Environment.getExternalStorageDirectory()+File.separator+ConstantesDeEntorno.directorioAplicacion);
		    if (! mediaStorageDir.exists()){
		        if (! mediaStorageDir.mkdirs()){
		            Log.d("Elfec Lecturas", "failed to create directory");
		            return false;
		        }
		    }
		    File mediaFile = new File(mediaStorageDir.getPath() + File.separator +nombreArch+".txt");
			FileOutputStream fOut = new FileOutputStream(mediaFile,true);
			OutputStreamWriter osw = new OutputStreamWriter(fOut); 
			  osw.write(lineaTexto);
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
	 * Verifica si un archivo existe o no
	 * @param nombreArch
	 * @return
	 */
	public static boolean archivoExiste(String nombreArch)
	{
		File fileDir = new File(Environment.getExternalStorageDirectory()+File.separator+ConstantesDeEntorno.directorioAplicacion+ File.separator +nombreArch+".txt");
		return fileDir.exists();
	}
}
