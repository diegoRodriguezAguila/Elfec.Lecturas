package com.lecturas.elfec.controlador.filtroslecturas;

import com.lecturas.elfec.modelo.Lectura;

/**
 * Es el criterio que sirve para filtrar por estado de las lecturas, segun el estado seleccionado
 * @author drodriguez
 *
 */
public class CriterioEstado implements ICriterioFiltroLectura {

	public int estadoSeleccionado;
	
	public CriterioEstado(int estado)
	{
		estadoSeleccionado = estado;
	}
	
	/**
	 * Utiliza el atributo de la base de datos <b>EstadoLectura</b>, para crear la cadena de filtro
	 */
	@Override
	public String obtenerCadenaDeFiltro() 
	{
		return "EstadoLectura="+estadoSeleccionado;
	}

	@Override
	public boolean lecturaCumpleCriterio(Lectura lectura) {
		return lectura.getEstadoLectura().getEstadoEntero()==estadoSeleccionado;
	}
	
	/**
	 * Retorna el estado que fue seleccionado
	 * @return
	 */
	public int obtenerEstadoSeleccionado()
	{
		return estadoSeleccionado;
	}

}
