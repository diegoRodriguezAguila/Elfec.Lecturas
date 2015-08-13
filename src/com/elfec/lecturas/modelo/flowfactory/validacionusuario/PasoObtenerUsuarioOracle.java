package com.elfec.lecturas.modelo.flowfactory.validacionusuario;

import com.elfec.lecturas.helpers.ConectorBDOracle;
import com.elfec.lecturas.modelo.validaciones.ErrorUsuarioPasswordIncorrectos;

/**
 * Define el paso de validaci�n de usuario en el que se intenta obtener al usuario de la base de datos Oracle.
 * <br><br><b>C�digo: PVU-1.1</b>
 * @author drodriguez
 *
 */
public class PasoObtenerUsuarioOracle extends PasoValidacionUsuario {

	/**
	 * Intenta obtener al usuario de la base de datos oracle y lo llena en la variable 
	 * <b>FlujoPasosValidacionUsuario.usuarioEncontrado</b>. Si existe un error de conexi�n retorna null y el resultado
	 * respectivo en <b>resultadoValidacion</b>. O bien llama al siguiente paso <b>PasoVerifEstadoUsuario</b>.
	 * @see PasoVerifEstadoUsuario
	 * @see ErrorUsuarioPasswordIncorrectos
	 */
	@Override
	public PasoValidacionUsuario procesarPaso() {
		try 
		{
			FlujoPasosValidacionUsuario.conexion = new ConectorBDOracle(FlujoPasosValidacionUsuario.context, false);
			FlujoPasosValidacionUsuario.usuarioEncontrado = FlujoPasosValidacionUsuario.conexion.obtenerUsuario(FlujoPasosValidacionUsuario.usuario);
			if(FlujoPasosValidacionUsuario.usuarioEncontrado==null)
			{
				resultadoValidacion = new ErrorUsuarioPasswordIncorrectos();
			}
			else
			{
				return new PasoVerifEstadoUsuario();
			}
		} 
		catch(Exception e)
		{
			resultadoValidacion = new ErrorUsuarioPasswordIncorrectos();
		}
		return null;
	}

}
