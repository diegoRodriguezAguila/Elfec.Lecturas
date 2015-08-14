package com.elfec.lecturas.controlador.filtroslecturas;

import com.elfec.lecturas.modelo.Lectura;

/**
 * Es el criterio que sirve para filtrar por rutas según una seleccionada
 * 
 * @author drodriguez
 *
 */
public class CriterioRuta implements ICriterioFiltroLectura {

	/**
	 * La ruta seleccionada para el filtro.
	 */
	private int rutaSeleccionada;

	public CriterioRuta(int ruta) {
		rutaSeleccionada = ruta;
	}

	/**
	 * Utiliza el atributo de la base de datos <b>LEMRUT</b>, para crear la
	 * cadena de filtro
	 */
	@Override
	public String obtenerCadenaDeFiltro() {
		return "LEMRUT=" + rutaSeleccionada;
	}

	@Override
	public boolean lecturaCumpleCriterio(Lectura lectura) {
		return lectura.Ruta == rutaSeleccionada;
	}

	/**
	 * Retorna la ruta seleccionada del criterio
	 * 
	 * @return
	 */
	public int obtenerRutaSeleccionada() {
		return rutaSeleccionada;
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof CriterioRuta) {
			return ((CriterioRuta) o).rutaSeleccionada == this.rutaSeleccionada;
		}
		return false;
	}
}
