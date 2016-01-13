package com.elfec.lecturas.controlador.filtroslecturas;

import com.activeandroid.query.Select;
import com.elfec.lecturas.modelo.Lectura;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Se encarga de aplicar los filtros seleccionados por el usuario a la lista de
 * lecturas
 * 
 * @author drodriguez
 */
public class FiltroLecturas {
	private HashMap<String, ICriterioFiltroLectura> criteriosFiltro;
	private volatile boolean criteriosCambiaron;
	private List<Lectura> cacheListaLectura;

	public FiltroLecturas() {
		criteriosFiltro = new HashMap<String, ICriterioFiltroLectura>();
		criteriosCambiaron = true;
	}

	/**
	 * Obtiene la lista de lecturas aplicando los criterios de filtro agregados.
	 * 
	 * @return Lista de lecturas filtrada
	 */
	public List<Lectura> obtenerListaDeLecturas() {
		if (!criteriosCambiaron) {
			return cacheListaLectura;
		}
		criteriosCambiaron = false;
		StringBuilder condiciones = new StringBuilder("");
		Collection<ICriterioFiltroLectura> criterios = criteriosFiltro.values();
		int tam = criterios.size();
		if (tam == 0)// no hay filtros
			cacheListaLectura = Lectura.obtenerTodasLasLecturas();
		else {
			int i = 0;
			for (ICriterioFiltroLectura criterio : criterios) {
				condiciones.append(criterio.obtenerCadenaDeFiltro());
				if (i < tam - 1)
					condiciones.append(" AND ");
				i++;
			}
			cacheListaLectura = new Select().from(Lectura.class)
					.where(condiciones.toString()).orderBy("LEMCTAANT")
					.execute();
		}
		return cacheListaLectura;
	}

	public synchronized boolean criteriosCambiaron() {
		return criteriosCambiaron;
	}

	/**
	 * Agrega o actualiza el tipo de criterio al filtro de lecturas.<br>
	 * Nota.- se debe volver a llamar al metodo obtenerListaDeLecturas() y
	 * asignarlos a la lista actual para que se actualizen los resultados
	 * filtrados.
	 * 
	 * @param criterio
	 */
	public void agregarCriterioAFiltro(ICriterioFiltroLectura criterio) {
		criteriosCambiaron = !criterio.equals(criteriosFiltro.get(criterio
				.getClass().getName())) || criteriosCambiaron;
		criteriosFiltro.put(criterio.getClass().getName(), criterio);
	}

	/**
	 * Obtiene el criterio al filtro, se le debe pasar la clase del criterio,
	 * con el methodo .class de la clase (ej. CriterioRuta.class ) retornara
	 * null en caso de que no se haya agregado dicho criterio
	 * 
	 * @param claseCriterio
	 */
	public ICriterioFiltroLectura obtenerCriterioDeFiltro(Class<?> claseCriterio) {
		return criteriosFiltro.get(claseCriterio.getName());
	}

	/**
	 * Quita el criterio al filtro, se le debe pasar la clase del criterio, con
	 * el methodo .class de la clase (ej. CriterioRuta.class )
	 * 
	 * @param claseCriterio
	 */
	public void quitarCriterioDeFiltro(Class<?> claseCriterio) {
		criteriosCambiaron = criteriosFiltro.containsKey(claseCriterio
				.getName()) || criteriosCambiaron;
		criteriosFiltro.remove(claseCriterio.getName());
	}

	/**
	 * Verifica si una lectura cumple los criterios del filtro, basta que no
	 * cumpla con uno para que sea falso el resultado
	 * 
	 * @param lectura
	 * @return
	 */
	public boolean lecturaCoincideCriterios(Lectura lectura) {
		Collection<ICriterioFiltroLectura> criterios = criteriosFiltro.values();
		boolean resultado = true;
		for (ICriterioFiltroLectura criterio : criterios) {
			if (criterio != null)
				resultado = resultado
						&& criterio.lecturaCumpleCriterio(lectura);
		}
		return resultado;
	}

	/**
	 * Verifica si existe algÃºn criterio agregado al filtro
	 * 
	 * @return
	 */
	public boolean existenCriterios() {
		return criteriosFiltro.size() > 0;
	}

	/**
	 * Elimina todos los criterios del filtro de lecturas
	 */
	public void resetearCriteriosFiltro() {
		criteriosCambiaron = existenCriterios();
		criteriosFiltro.clear();
	}
}
