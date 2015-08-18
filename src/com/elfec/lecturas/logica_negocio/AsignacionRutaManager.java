package com.elfec.lecturas.logica_negocio;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.List;

import com.elfec.lecturas.acceso_remoto_datos.ConectorBDOracle;
import com.elfec.lecturas.logica_negocio.intercambio_datos.DataImporter;
import com.elfec.lecturas.modelo.AsignacionRuta;
import com.elfec.lecturas.modelo.enums.EstadoAsignacionRuta;
import com.elfec.lecturas.modelo.eventos.ImportacionDatosListener;
import com.elfec.lecturas.modelo.excepciones.NoHayRutasAsignadasException;
import com.elfec.lecturas.modelo.intercambio_datos.ImportSource;
import com.elfec.lecturas.modelo.resultados.ResultadoTipado;
import com.elfec.lecturas.modelo.resultados.ResultadoVoid;

/**
 * Se encarga de la lógica de negocio de las asignaciones de rutas
 * 
 * @author drodriguez
 *
 */
public class AsignacionRutaManager {
	/**
	 * Importa las rutas asignadas al usuario para la fecha actual.<br>
	 * <b>Nota.-</b> La importación incluye la consulta remota y el guardado
	 * local de los datos
	 * 
	 * @param username
	 * @param password
	 * @param dataImportListener
	 *            {@link DataImportListener}
	 * @return {@link TypedResult} con el resultado de la lista de rutas
	 *         asignadas al usuario
	 */
	public ResultadoTipado<List<AsignacionRuta>> importarRutasAsignadasAUsuario(
			final ConectorBDOracle conector, final String usuario,
			ImportacionDatosListener dataImportListener) {
		if (dataImportListener != null)
			dataImportListener.onImportacionIniciada();
		AsignacionRuta.eliminarTodasLasRutasNoImportadasDelUsuario(usuario);
		ResultadoTipado<List<AsignacionRuta>> result = new DataImporter()
				.importData(new ImportSource<AsignacionRuta>() {
					@Override
					public List<AsignacionRuta> requestData()
							throws ConnectException, SQLException {
						return conector.obtenerRutasAsignadas(usuario);
					}

					@Override
					public void preSaveData(AsignacionRuta data) {
					}
				});
		if (result.getResultado().size() == 0)// no tiene rutas asignadas
			result.agregarError(new NoHayRutasAsignadasException(usuario));

		if (dataImportListener != null)
			dataImportListener.onImportacionFinalizada(result);
		return result;
	}

	/**
	 * Asigna remota y localmente el estado de CARGADAS a la lista de rutas. Si
	 * ocurre un error al realizar el update remoto aquellas rutas que se hayan
	 * logrado guardar estarán marcadas como exportadas localmente
	 * 
	 * @param rutasAsignadas
	 * @return {@link ResultadoVoid} lista de errores del proceso
	 */
	public ResultadoVoid setRutasImportadasExitosamente(
			ConectorBDOracle conector, List<AsignacionRuta> rutasAsignadas) {
		ResultadoVoid result = new ResultadoVoid();
		try {
			for (AsignacionRuta ruta : rutasAsignadas) {
				if (ruta.estaAsignada()) {
					ruta.setEstado(ruta.getEstado() == EstadoAsignacionRuta.ASIGNADA ? EstadoAsignacionRuta.IMPORTADA
							: EstadoAsignacionRuta.RELECTURA_IMPORTADA);
					conector.actualizarEstadoRuta(ruta, false);
					ruta.save();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			result.agregarError(e);
		} catch (Exception e) {
			e.printStackTrace();
			result.agregarError(e);
		}
		return result;
	}

	/**
	 * Restaura el estado que tenían las rutas antes de ser importadas al
	 * telefono local y remotamente
	 * 
	 * @param rutas
	 * @return {@link ResultadoVoid} lista de errores del proceso
	 */
	public ResultadoVoid restaurarAsignacionDeRutas(ConectorBDOracle conector,
			List<AsignacionRuta> rutas) {
		ResultadoVoid result = new ResultadoVoid();
		try {
			for (AsignacionRuta ruta : rutas) {
				if (ruta.estaImportada()) {
					ruta.setEstado(ruta.getEstado() == EstadoAsignacionRuta.IMPORTADA ? EstadoAsignacionRuta.ASIGNADA
							: EstadoAsignacionRuta.RELECTURA_ASIGNADA);
					conector.actualizarEstadoRuta(ruta, false);
					ruta.save();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			result.agregarError(e);
		} catch (Exception e) {
			e.printStackTrace();
			result.agregarError(e);
		}
		return result;
	}
}
