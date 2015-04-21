package com.lecturas.elfec.helpers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.lecturas.elfec.modelo.Lectura;
import com.lecturas.elfec.modelo.MedidorEntreLineas;
import com.lecturas.elfec.modelo.OrdenativoLectura;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Se encarga de la interacción con la cámara del dispositivo
 * @author drodriguez
 *
 */
public class ManejadorDeCamara {

	private static final int MEDIA_TYPE_IMAGE = 1;
	private static final int MEDIA_TYPE_VIDEO = 2;
	public static final int TOMAR_FOTO_LECTURA_REQUEST = 100;
	public static final int TOMAR_FOTO_ENTRE_LINEAS_REQUEST = 101;
	private static String DIR_LEC = "LECTURAS";
	private static String DIR_ORD = "ORDENATIVOS";
	private static String DIR_ENTRE_LINEAS = "LECTURAS ENTRE LINEAS";
	private static String NOMBRE_ORD = "Ord_";
	private static String NOMBRE_LEC = "Lec_";
	private static String NOMBRE_ENTRE_LINEAS = "EntreLineas";
	
	
	/**
	 * Invoca a la camara, para tomar una foto relacionada a la lectura
	 * @param activity
	 * @param ordLec
	 */
	public static void tomarFotoLectura(Activity activity, Lectura lecturaActual)
	{
		invocarCamara(activity, ""+lecturaActual.Suministro, NOMBRE_LEC+(lecturaActual.NumFotosTomadas+1), 
				DIR_LEC+File.separator+lecturaActual.Suministro, TOMAR_FOTO_LECTURA_REQUEST);
	}
	
	/**
	 * Se llama para sacar una foto al ingresar un ordenativo de impedimento
	 * o cuando se introduce un ordenativo automatico de lectura fuera de rango o volcado de medidor
	 * @param activity
	 * @param ordLec
	 */
	public static void tomarFotoOrdenativo(Activity activity, OrdenativoLectura ordLec)
	{
		invocarCamara(activity, ""+ordLec.Lectura.Suministro, NOMBRE_ORD+ordLec.Ordenativo.Codigo,
				DIR_LEC+File.separator+ordLec.Lectura.Suministro+File.separator+DIR_ORD, TOMAR_FOTO_LECTURA_REQUEST);
	}
	
	/**
	 * Invoca a la camara para tomar una foto relacionada a una lectura entre lineas
	 * o cuando se introduce un ordenativo automatico de lectura fuera de rango o volcado de medidor
	 * @param activity
	 * @param medEntreLineas
	 */
	public static void tomarFotoEntreLineas(Activity activity, MedidorEntreLineas medEntreLineas)
	{
		invocarCamara(activity, medEntreLineas.NumeroMedidor, NOMBRE_ENTRE_LINEAS,DIR_ENTRE_LINEAS+File.separator+medEntreLineas.NumeroMedidor,
				TOMAR_FOTO_ENTRE_LINEAS_REQUEST);
	}
	
	/**
	 * Invoca a la camara para tomar una foto y guardarla con el nombre y en el directorio especificados.
	 * @param activity
	 * @param prefijoNombre , es lo que sale antes de la fecha en el nombre del archivo
	 * @param nombreArch
	 * @param directorio
	 */
	private static void invocarCamara(Activity activity, String prefijoNombre, String nombreArch, String directorio, int request)
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);	
		Uri fileUri = obtenerUriDelArchivoParaEscribir(MEDIA_TYPE_IMAGE, prefijoNombre, nombreArch,directorio);
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
	    activity.startActivityForResult(intent, TOMAR_FOTO_LECTURA_REQUEST);
	}
	
	/**
	 * Obtiene el archivo y su Uri para guardarlo
	 * @param tipo
	 * @param prefijoNombre , es lo que sale antes de la fecha en el nombre del archivo
	 * @param nombreArch
	 * @return
	 */
	private static Uri obtenerUriDelArchivoParaEscribir(int tipo, String prefijoNombre, String nombreArch, String directorio){
	      return Uri.fromFile(obtenerArchivoParaEscribir(tipo, prefijoNombre, nombreArch, directorio));
	}
	
	/**
	 * Crea el archivo para guardar fotos o videos
	 * @param tipo
	 * @param prefijoNombre
	 * @param nombre
	 * @param directorio
	 * @return
	 */
	private static File obtenerArchivoParaEscribir(int tipo ,String prefijoNombre, String nombre,String directorio){
	    File mediaStorageDir = new File(Environment.getExternalStorageDirectory()+File.separator+ConstantesDeEntorno.directorioAplicacion+File.separator+directorio);
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("Elfec Lecturas", "failed to create directory");
	            return null;
	        }
	    }
	    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH-mm", Locale.getDefault()).format(new Date());
	    File mediaFile;
	    if (tipo == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +prefijoNombre+" - "+timeStamp+" "+
	        		nombre + ".jpg");
	    } else if(tipo == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +prefijoNombre+" - "+timeStamp+" "+
	        		nombre + ".mp4");
	    } else {
	        return null;
	    }
	    return mediaFile;
	}
	
	/**
	 * Elimina la carpeta de un suministro junto a todo sus fotos
	 * @param suministro
	 */
	public static void borrarTodasLasFotosDeLectura(long suministro)
	{
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory()+File.separator+ConstantesDeEntorno.directorioAplicacion+File.separator+(""+suministro));
		if (mediaStorageDir.exists()){
			eliminarCarpeta(mediaStorageDir); 
	    }
	}
	
	/**
	 * Eliminar una carpeta y todos sus archivos dentro de forma recursiva
	 * @param path
	 * @return
	 */
	private static boolean eliminarCarpeta(File path) {
	    if( path.exists() ) {
	      File[] files = path.listFiles();
	      if (files == null) {
	          return true;
	      }
	      for(int i=0; i<files.length; i++) {
	         if(files[i].isDirectory()) {
	           eliminarCarpeta(files[i]);
	         }
	         else {
	           files[i].delete();
	         }
	      }
	    }
	    return( path.delete() );
	  }
}
