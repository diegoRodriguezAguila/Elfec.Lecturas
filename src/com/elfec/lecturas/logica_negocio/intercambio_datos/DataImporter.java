package com.elfec.lecturas.logica_negocio.intercambio_datos;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.List;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.elfec.lecturas.modelo.intercambio_datos.ImportSource;
import com.elfec.lecturas.modelo.resultados.ResultadoTipado;
import com.elfec.lecturas.modelo.resultados.ResultadoVoid;

/**
 * Se encarga de importar cualquier tipo de información
 * 
 * @author drodriguez
 *
 */
public class DataImporter {

	/**
	 * Importa cualquier tipo de información que debe ser importada una sola vez
	 * 
	 * @param importSource
	 * @return {@link ResultadoVoid}
	 */
	public <T extends Model> ResultadoVoid importDataWithNoReturn(
			ImportSource<T> importSource) {
		ResultadoVoid result = new ResultadoVoid();
		try {
			List<T> dataList = importSource.requestData();
			ActiveAndroid.beginTransaction();
			for (T data : dataList) {
				importSource.preSaveData(data);
				data.save();
			}
			ActiveAndroid.setTransactionSuccessful();
		} catch (ConnectException e) {
			result.agregarError(e);
		} catch (SQLException e) {
			e.printStackTrace();
			result.agregarError(e);
		} catch (Exception e) {
			e.printStackTrace();
			result.agregarError(e);
		} finally {
			if (ActiveAndroid.inTransaction())
				ActiveAndroid.endTransaction();
		}
		return result;
	}

	/**
	 * Importa cualquier tipo de información
	 * 
	 * @param importSpecs
	 * @return {@link TypedResult}
	 */
	public <T extends Model> ResultadoTipado<List<T>> importData(
			ImportSource<T> importSource) {
		ResultadoTipado<List<T>> result = new ResultadoTipado<List<T>>();
		try {
			List<T> dataList = importSource.requestData();
			ActiveAndroid.beginTransaction();
			for (T data : dataList) {
				importSource.preSaveData(data);
				data.save();
			}
			ActiveAndroid.setTransactionSuccessful();
			result.setResultado(dataList);
		} catch (ConnectException e) {
			result.agregarError(e);
		} catch (SQLException e) {
			e.printStackTrace();
			result.agregarError(e);
		} catch (Exception e) {
			e.printStackTrace();
			result.agregarError(e);
		} finally {
			if (ActiveAndroid.inTransaction())
				ActiveAndroid.endTransaction();
		}
		return result;
	}
}
