package com.lecturas.elfec.modelo.flowfactory.validacionusuario;

import com.lecturas.elfec.modelo.validaciones.ErrorEmpleadoInhabilitado;

/**
 * Define el paso de validación en el que se verifican si el usuario obtenido de la base
 * de datos oracle se encuentra activo.
 * fueron cargados.
 * <br><br><b>Código: PVU-1.1.1</b>
 * @author drodriguez
 */
public class PasoVerifEstadoUsuario extends PasoValidacionUsuario {

	/**
	 * Verifica si el usuario tiene Estado=1 es decir que se encuentra activo, en caso de no encontrarse
	 * retorna null y finaliza el flujo de pasos asignando el error a <b>resultadoValidacion</b>. Caso contrario retorna el paso para verificar el imei <b>PasoVerifExistenciaIMEI</b>
	 * @see PasoVerifExistenciaIMEI
	 * @see ErrorEmpleadoInhabilitado
	 */
	@Override
	public PasoValidacionUsuario procesarPaso() {
		if(FlujoPasosValidacionUsuario.usuarioEncontrado.Estado==0)
		{
			resultadoValidacion = new ErrorEmpleadoInhabilitado();
		}
		else
		{
			return new PasoVerifExistenciaIMEI();
		}
		return null;
	}

}
