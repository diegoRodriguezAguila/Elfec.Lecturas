package com.elfec.lecturas.controlador.filtroslecturas;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.activeandroid.query.Select;
import com.elfec.lecturas.modelo.Lectura;

/**
 * Se encarga de aplicar los filtros seleccionados por el usuario a la lista de lecturas
 * @author drodriguez
 */
public class FiltroLecturas 
{
	private static HashMap<String, ICriterioFiltroLectura> criteriosFiltro;
	static
	{
		criteriosFiltro = new HashMap<String, ICriterioFiltroLectura>();
	}
	
	/**
	 * Obtiene la lista de lecturas aplicando los criterios de filtro agregados.
	 * @return Lista de lecturas filtrada
	 */
	public static List<Lectura> obtenerListaDeLecturas()
	{
		StringBuilder condiciones = new StringBuilder("");
		Collection<ICriterioFiltroLectura> criterios = criteriosFiltro.values();
		int tam = criterios.size();
		if(tam==0)//no hay filtros
			return Lectura.obtenerTodasLasLecturas();
		int i = 0;
		for(ICriterioFiltroLectura criterio : criterios)
		{
			condiciones.append(criterio.obtenerCadenaDeFiltro());
			if(i<tam-1)
				condiciones.append(" AND ");
			i++;
		}
		return new Select().from(Lectura.class).where(condiciones.toString()).orderBy("LEMCTAANT").execute();
	}
	
	/**
	 * Agrega o actualiza el tipo de criterio al filtro de lecturas.<br>
	 * Nota.- se debe volver a llamar al metodo obtenerListaDeLecturas() y asignarlos
	 * a la lista actual para que se actualizen los resultados filtrados.
	 * @param criterio
	 */
	public static void agregarCriterioAFiltro(ICriterioFiltroLectura criterio)
	{
		criteriosFiltro.put(criterio.getClass().getName(), criterio);
	}
	
	/**
	 * Obtiene el criterio al filtro, se le debe pasar la clase del criterio, con el methodo .class 
	 * de la clase (ej. CriterioRuta.class ) retornara null en caso de que no se haya agregado dicho criterio 
	 * @param claseCriterio
	 */
	public static ICriterioFiltroLectura obtenerCriterioDeFiltro(Class<?> claseCriterio)
	{
		return criteriosFiltro.get(claseCriterio.getName());
	}
	
	/**
	 * Quita el criterio al filtro, se le debe pasar la clase del criterio, con el methodo .class 
	 * de la clase (ej. CriterioRuta.class )
	 * @param claseCriterio
	 */
	public static void quitarCriterioDeFiltro(Class<?> claseCriterio)
	{
		criteriosFiltro.remove(claseCriterio.getName());
	}
	
	/**
	 * Verifica si una lectura cumple los criterios del filtro, basta que no cumpla con uno para que
	 * sea falso el resultado
	 * @param lectura
	 * @return
	 */
	public static boolean lecturaCoincideCriterios(Lectura lectura)
	{
		Collection<ICriterioFiltroLectura> criterios = criteriosFiltro.values();
		boolean resultado = true;
		for(ICriterioFiltroLectura criterio : criterios)
		{
			if(criterio!=null)
				resultado = resultado && criterio.lecturaCumpleCriterio(lectura);
		}
		return resultado;
	}
	
	/**
	 * Verifica si existe algún criterio agregado al filtro
	 * @return
	 */
	public static boolean existenCriterios()
	{
		return criteriosFiltro.size()>0;
	}
	
	/**
	 * Elimina todos los criterios del filtro de lecturas
	 */
	public static void resetearCriteriosFiltro()
	{
		criteriosFiltro.clear();
	}
}
