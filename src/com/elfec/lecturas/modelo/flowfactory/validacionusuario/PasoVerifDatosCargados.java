package com.elfec.lecturas.modelo.flowfactory.validacionusuario;

import com.elfec.lecturas.helpers.GestionadorBDSQLite;
import com.elfec.lecturas.modelo.Usuario;
import com.elfec.lecturas.modelo.validaciones.ErrorRangoFechaUsuario;

/**
 * Define el paso de validación en el que se verifican si los datos diarios
 * fueron cargados.
 * <br><br><b>Código: PVU-1.2.1</b>
 * @author drodriguez
 */
public class PasoVerifDatosCargados extends PasoValidacionUsuario {

	/**
	 * Verifica si se cargaron los datos diarios y asigna a la variable <b>resultadoValidacion</b> el resultado
	 * correspondiente
	 * <br> <br> retorna null porque es un paso final
	 * @see ErrorRangoFechaUsuario
	 * @see Usuario
	 */
	@Override
	public PasoValidacionUsuario procesarPaso() {
		if(GestionadorBDSQLite.datosDiariosFueronCargados())
		{
			resultadoValidacion = new ErrorRangoFechaUsuario(FlujoPasosValidacionUsuario.usuarioEncontrado.FechaSincronizacion);
		}
		else
		{
			GestionadorBDSQLite.eliminarDatosDiarios();
			resultadoValidacion = Usuario.validar(FlujoPasosValidacionUsuario.usuario, FlujoPasosValidacionUsuario.password, 
					FlujoPasosValidacionUsuario.imei, FlujoPasosValidacionUsuario.fechaSinc, FlujoPasosValidacionUsuario.context);
		}
		return null;
	}

}
