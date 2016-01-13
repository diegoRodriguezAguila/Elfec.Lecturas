package com.elfec.lecturas.modelo.avisocobranza;

/**
 * Es un mes de consumo del usuario con su debido consumo y asteriscos, solo se usa para imprimir
 * @author drodriguez
 *
 */
public class MesConsumo {

	public int maxConsumo;
	public String fechaMes;
	public String asteriscosConsumo;
	public int consumo;
	private static final int MAX_ASTERISCOS = 20;
	
	public MesConsumo()
	{
		
	}
	public MesConsumo(String fechaMes, int consumo, int max)
	{
		this.maxConsumo = max;
		this.fechaMes = fechaMes;
		this.consumo = consumo;
		asignarAsteriscos(max);
	}
	/**
	 * Asigna la cantidad de asteriscos proporcional al maximo consumo proporcionado y al maximo de asteriscos
	 * de la variable MAX_ASTERISCOS
	 * @param max
	 */
	private void asignarAsteriscos(int max)
	{
		double numAsteriscos = (MAX_ASTERISCOS/(float)max)*consumo;
		StringBuilder str = new StringBuilder("");
		for (int i = 0; i < (int)numAsteriscos; i++) 
		{
			str.append("*");
		}
		asteriscosConsumo = str.toString();
	}
	/**
	 * Asigna la cantidad de asteriscos nueva proporcional al maximo consumo y al maximo de asteriscos proporcionados
	 * @param max
	 */
	public String obtenerAsteriscosProporcionNueva(int maxAsteriscos)
	{
		double numAsteriscos = (maxAsteriscos/(float)maxConsumo)*consumo;
		StringBuilder str = new StringBuilder("");
		for (int i = 0; i < (int)numAsteriscos; i++) 
		{
			str.append("*");
		}
		return str.toString();
	}
}
