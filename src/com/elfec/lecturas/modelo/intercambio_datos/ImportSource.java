package com.elfec.lecturas.modelo.intercambio_datos;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.List;

import com.activeandroid.Model;

/**
 * Interfaz que utiliza DataImporter para determinar la fuente de los datos que
 * se quieren importar
 * 
 * @author drodriguez
 *
 * @param <T>
 */
public interface ImportSource<T extends Model> {
	/**
	 * Se ejecuta antes de guardar localmente cada fila de información
	 */
	public void preSaveData(T data);

	/**
	 * Obtiene la información
	 * 
	 * @return Lista de tipo T
	 * @throws ConnectException
	 * @throws SQLException
	 */
	public List<T> requestData() throws ConnectException, SQLException;
}