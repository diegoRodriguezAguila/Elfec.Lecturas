package com.elfec.lecturas.modelo.flowfactory.validacionusuario;

import com.elfec.lecturas.modelo.validaciones.IValidacionUsuario;

/**
 * Define un paso de la validación de usuario
 * @author drodriguez
 *
 */
public abstract class PasoValidacionUsuario {

	/**
	 * Todos los pasos deben llenar este campo antes de retornar el paso procesado
	 */
	protected IValidacionUsuario resultadoValidacion;
	
	/**
	 * Obtiene el resultado de ese paso de la validación, null en caso de
	 * que no se obtenga resultado del paso.
	 * @return Se retorna el valor de la variable <b>resultadoValidacion</b>
	 */
	public final IValidacionUsuario obtenerResultadoValidacion()
	{
		return resultadoValidacion;
	}
	
	
	/**
	 * Todos los pasos de validacion de usuario deben implementar un metodo para procesar el paso
	 * y deben retornar el siguiente paso o null en el caso de que se finalize el flujo, tambien es importante
	 * que se asigne un valor a resultadoValidacion en el caso de que sea null el resultado es decir cuando 
	 * termine el flujo de pasos
	 * @return el siguiente paso del flujo
	 */
	public abstract PasoValidacionUsuario procesarPaso();
}
