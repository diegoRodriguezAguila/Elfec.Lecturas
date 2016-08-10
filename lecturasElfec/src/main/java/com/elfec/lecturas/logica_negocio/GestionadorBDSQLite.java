package com.elfec.lecturas.logica_negocio;

import android.database.Cursor;

import com.activeandroid.ActiveAndroid;
import com.elfec.lecturas.settings.AppPreferences;

/**
 * Provee metodos para la eliminacion de tablas mensuales y diarios y de la base
 * de datos completa
 * 
 * @author drodriguez
 *
 */
public class GestionadorBDSQLite {

	/**
	 * Elimina masivamente todos los datos, tanto diarios como mensuales
	 */
	public static void eliminarTodosLosDatos() {
		ActiveAndroid.getDatabase().close();
		AppPreferences.getApplicationContext().deleteDatabase(
				ActiveAndroid.getDatabase().getPath());
		ActiveAndroid.dispose();
		ActiveAndroid.initialize(AppPreferences.getApplicationContext());
	}

	/**
	 * Verifica la existencia de una tabla
	 * 
	 * @param nombreTabla nombre tabla
	 * @return bool
	 */
	private static boolean existeTabla(String nombreTabla) {
		if (nombreTabla == null || ActiveAndroid.getDatabase() == null) {
			return false;
		}
		Cursor cursor = ActiveAndroid
				.getDatabase()
				.rawQuery(
						"SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?",
						new String[] { "table", nombreTabla });
		if (!cursor.moveToFirst()) {
			return false;
		}
		int count = cursor.getInt(0);
		cursor.close();
		return count > 0;
	}

}
