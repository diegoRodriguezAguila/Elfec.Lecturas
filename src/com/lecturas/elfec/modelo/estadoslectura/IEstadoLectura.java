package com.lecturas.elfec.modelo.estadoslectura;

import android.content.Context;

import com.lecturas.elfec.controlador.TomarLectura;
import com.lecturas.elfec.modelo.Lectura;

/**
 * Define el patron State de diseño abstrae el estado de la lectura
 * @author drodriguez
 *
 */
public interface IEstadoLectura 
{
	/**
	 * Es la interfaz que define la creacion del estado, el constructor es privado
	 * @return El estado
	 */
	public IEstadoLectura crearEstado();
	/**
	 * @return El estado de la lectura en entero, como su valor de la base de datos
	 */
	public int getEstadoEntero();
	/**
	 * @return El estado en cadena, es decir si el Estado=0, devolverá "Pendiente"
	 */
	public String getEstadoCadena();
	/**
	 * @param context, necesario para poder obtener los recursos, normalmente se pasa this como parametro,
	 * desde la vista que se este llamando
	 * @return retorna el int, que representa el color correspondiente al estado
	 */
	public int getColor(Context context);
	/**
	 * Asigna la visibilidad y color de los elementos de la vista <b>TomarLectura</b> para mostrarlos
	 * @param tomarLectura, la vista de toma de lecturas donde se muestran los datos
	 * @param lecturaActual, la lectura de la que se sacan los datos
	 */
	public void mostrarLectura(TomarLectura tomarLectura, Lectura lecturaActual);
	
	/**
	 * Asigna la visibilidad de los elementos del menu de la vista <b>TomarLectura</b> para mostrarlos
	 * @param tomarLectura, la vista de toma de lecturas donde se muestran los datos
	 * @param lecturaActual, la lectura de la que se sacan los datos
	 */
	public void mostrarMenuLectura(TomarLectura tomarLectura, Lectura lecturaActual);

}
