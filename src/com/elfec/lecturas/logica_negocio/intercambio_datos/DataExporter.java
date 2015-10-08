package com.elfec.lecturas.logica_negocio.intercambio_datos;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.List;

import com.activeandroid.Model;
import com.elfec.lecturas.modelo.enums.EstadoExportacion;
import com.elfec.lecturas.modelo.eventos.ExportacionDatosListener;
import com.elfec.lecturas.modelo.excepciones.ExportacionException;
import com.elfec.lecturas.modelo.intercambio_datos.ExportSpecs;
import com.elfec.lecturas.modelo.interfaces.IExportable;
import com.elfec.lecturas.modelo.resultados.ResultadoTipado;

/**
 * Se encarga de exportar cualquier tipo de información
 * 
 * @author drodriguez
 *
 */
public class DataExporter {

	/**
	 * Importa cualquier tipo de información que debe ser importada una sola vez
	 * 
	 * @param exportSpecs
	 * @param exportListener
	 * @return Resultado del acceso remoto a datos
	 */
	public <T extends Model & IExportable> ResultadoTipado<Boolean> exportData(
			ExportSpecs<T> exportSpecs, ExportacionDatosListener exportListener) {
		ResultadoTipado<Boolean> result = new ResultadoTipado<Boolean>(true);
		if (exportListener == null)
			exportListener = new ExportacionDatosListener() {// DUMMY Listener
				@Override
				public void onExportando(int exportCount, int totalElements) {
				}

				@Override
				public void onExportacionInicializada(int totalElements) {
				}

				@Override
				public void onExportacionFinalizada() {
				}
			};// DUMMY Listener

		try {
			List<T> dataList = exportSpecs.requestDataToExport();
			int size = dataList.size();
			exportListener.onExportacionInicializada(size);
			int rowRes, count = 0;
			for (T data : dataList) {
				rowRes = exportSpecs.exportData(data);
				if (rowRes == 1) // se insertó existosamente
				{
					data.setExportStatus(EstadoExportacion.EXPORTADO);
					data.save();
					count++;
					exportListener.onExportando(count, size);
				} else
					throw new ExportacionException(
							"No se pudo insertar el registro: "
									+ data.getRegistryResume());

			}
			result.setResultado(true);
		} catch (ExportacionException e) {
			result.agregarError(e);
		} catch (ConnectException e) {
			result.agregarError(e);
		} catch (SQLException e) {
			e.printStackTrace();
			result.agregarError(new ExportacionException(e.getMessage()));
		} catch (Exception e) {
			e.printStackTrace();
			result.agregarError(new ExportacionException(e.getMessage()));
		}
		exportListener.onExportacionFinalizada();
		return result;
	}
}
