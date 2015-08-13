package com.elfec.lecturas.controlador.filtroslecturas;

import com.elfec.lecturas.modelo.Lectura;

/**
 * Define la interfaz de criterios para filtrar lecturas.<br>
 * Todos los criterios para filtrar rutas deben implementar esta interfaz.
 * @author drodriguez
 *
 */
public interface ICriterioFiltroLectura {

	/**
	 * Metodo que obtiene la cadena del filtro, la cadena del filtro debe tener el formato de una
	 * consulta <b>SQL</b> de una condición <b>WHERE</b>, caso contrario fallará
	 * @return
	 */
	public String obtenerCadenaDeFiltro();
	
	/**
	 * Verifica si una lectura cumple el criterio especifico
	 * @param lectura
	 * @return
	 */
	public boolean lecturaCumpleCriterio(Lectura lectura);
}
