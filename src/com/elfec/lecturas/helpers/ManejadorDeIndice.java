package com.elfec.lecturas.helpers;

/**
 * Esta clase se encarga de manejar el indice de la lectura que se esta viendo actualmente
 * @author drodriguez
 *
 */
public class ManejadorDeIndice {
	private static long idLecturaActual=-1;
	
	public static long getIdLecturaActual()
	{
		return idLecturaActual;
	}
	public static void setIdLecturaActual(long nuevoId)
	{
		idLecturaActual=nuevoId;
	}

}
