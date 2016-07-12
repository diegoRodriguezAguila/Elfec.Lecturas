package com.elfec.lecturas.acceso_remoto_datos;

import android.content.Context;
import android.util.Log;

import com.elfec.lecturas.BuildConfig;

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
 * Es un objeto estático que sirve para poder obtener y escribir el
 * archivo de configuración que está en JSON bajo la carpeta
 * de assets, el nombre del archivo es <b>config_bd.json</b>. con prefijo
 * del tipo de bd
 *
 * @author drodriguez
 */
public class LectorConfigBD {

    /**
     * Define el nombre del archivo de configuración.
     **/
    private static final String archivoConfig;
    private static Context contexto;

    static {
        String prefix = "sidprod_";
        if (BuildConfig.IS_PRODUCTION)
            prefix = "eprod_";
        archivoConfig = prefix + "config_bd.json";
    }

    /**
     * Se encarga de devolver un objeto json que representa el archivo de configuración leido.
     *
     * @param contextoNuevo es el contexto la actividad de la que se llama, necesaria para obtener el asset.
     * @return JSONObject que respresenta la configuración actual.
     * @throws JSONException
     */
    public static JSONObject obtenerConfiguracion(Context contextoNuevo) throws JSONException {
        contexto = contextoNuevo;
        String res = leerArchivoConfiguracion();
        return new JSONObject(res);
    }

    /**
     * Se encarga de escribir la información de configuración a partir de un JSONObject
     *
     * @param nuevaConfig Recibe un objeto JSON que sea valido para la configuracion
     * @return si se guardó exitosamente la configuración.
     */
    public static boolean escribirConfiguracion(JSONObject nuevaConfig) {
        try {
            FileOutputStream fOut = contexto.openFileOutput(archivoConfig, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(nuevaConfig.toString());
            osw.flush();
            osw.close();
            return true;
        } catch (FileNotFoundException e) {
            Log.println(Log.ERROR, "FileNotFoundException", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.println(Log.ERROR, "IOException", e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lee el archivo de configuracion del usuario, busca en la carpeta de la aplicación, caso contrario usa el por defecto de assets
     *
     * @return retorna la cadena JSON o caso contrario un mensaje de error de que no existe el archivo.
     */
    private static String leerArchivoConfiguracion() {
        try {
            FileInputStream fIn = contexto.openFileInput(archivoConfig);
            InputStreamReader isr = new InputStreamReader(fIn);
            char[] inputBuffer = new char[250];
            isr.read(inputBuffer);
            String readString = new String(inputBuffer);
            isr.close();
            return readString;
        } catch (FileNotFoundException e) {
            return leerArchivoConfiguracionAssets();
        } catch (IOException e) {
            Log.println(Log.ERROR, "IOException", e.getMessage());
            e.printStackTrace();
        }
        return "Error al leer";
    }

    /**
     * Lee el archivo de configuracion de assets, es decir el que viene en el paquete por defecto
     *
     * @return retorna la cadena JSON o caso contrario un mensaje de error de que no existe el archivo.
     */
    private static String leerArchivoConfiguracionAssets() {
        try {
            InputStream is = contexto.getAssets().open(archivoConfig);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer);
        } catch (FileNotFoundException e) {
            Log.println(Log.ERROR, "FileNotFoundException", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.println(Log.ERROR, "IOException", e.getMessage());
            e.printStackTrace();
        }
        return "No existe archivo de configuracion";
    }

}
