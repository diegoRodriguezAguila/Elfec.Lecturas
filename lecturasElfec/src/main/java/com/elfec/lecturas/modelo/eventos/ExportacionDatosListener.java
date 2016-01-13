package com.elfec.lecturas.modelo.eventos;

/**
 * Clase que informa de los eventos que ocurren durante la exportación de datos
 * @author drodriguez
 *
 */
public interface ExportacionDatosListener {

	/**
	 * Se ejecuta el momento en que se obtuvieron todos los datos para exportar
	 * y se esta por empezar la exportación
	 * @param totalElementos total de datos a exportar
	 */
	public void onExportacionInicializada(int totalElementos);
	
	/**
	 * Se ejecuta cada que se exporta una fila
	 * @param countExportacion datos exportados hasta el momento
	 * @param totalElementos total de datos a exportar
	 */
	public void onExportando(int countExportacion, int totalElementos);
	
	/**
	 * Se ejecuta el momento en que se finaliza la exportación, ya sea exitosa o fallidamente
	 * y se esta por empezar la exportación
	 */
	public void onExportacionFinalizada();
}
