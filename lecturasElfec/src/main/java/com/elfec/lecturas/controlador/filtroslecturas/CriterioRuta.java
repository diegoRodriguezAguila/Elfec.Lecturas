package com.elfec.lecturas.controlador.filtroslecturas;

import com.elfec.lecturas.modelo.AsignacionRuta;
import com.elfec.lecturas.modelo.Lectura;

/**
 * Es el criterio que sirve para filtrar por rutas segÃºn una seleccionada
 *
 * @author drodriguez
 */
public class CriterioRuta implements ICriterioFiltroLectura {

    /**
     * La ruta seleccionada para el filtro.
     */
    private AsignacionRuta rutaSeleccionada;

    public CriterioRuta(AsignacionRuta ruta) {
        rutaSeleccionada = ruta;
    }

    /**
     * Utiliza el atributo de la base de datos <b>LEMRUT</b>, para crear la
     * cadena de filtro
     */
    @Override
    public String obtenerCadenaDeFiltro() {
        return "(LEMRUT=" + rutaSeleccionada.Ruta +
                " AND LEMCTAANT BETWEEN "
                + rutaSeleccionada.obtenerCuentaInicio() + " AND "
                + rutaSeleccionada.obtenerCuentaFin()+")";
    }

    @Override
    public boolean lecturaCumpleCriterio(Lectura lectura) {
        return lectura.Ruta == rutaSeleccionada.Ruta &&
                Long.parseLong(lectura.Cuenta) >= rutaSeleccionada.obtenerCuentaInicio()
                && Long.parseLong(lectura.Cuenta) <= rutaSeleccionada.obtenerCuentaFin();
    }

    /**
     * Retorna la ruta seleccionada del criterio
     *
     * @return
     */
    public AsignacionRuta obtenerRutaSeleccionada() {
        return rutaSeleccionada;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof CriterioRuta
                && ((CriterioRuta) o).rutaSeleccionada == this.rutaSeleccionada;
    }
}
