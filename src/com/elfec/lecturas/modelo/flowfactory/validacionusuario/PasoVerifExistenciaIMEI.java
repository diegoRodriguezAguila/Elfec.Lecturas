package com.elfec.lecturas.modelo.flowfactory.validacionusuario;

import java.sql.SQLException;

import com.elfec.lecturas.modelo.validaciones.ErrorDeAsignacionDeMovil;
import com.elfec.lecturas.modelo.validaciones.ErrorUsuarioPasswordIncorrectos;
import com.elfec.lecturas.modelo.validaciones.ValidacionUsuarioCorrecta;

/**
 * Define el paso de validación en el que se verifican la existencia del IMEI del telefono
 * como valido en la base de datos Oracle
 * fueron cargados.
 * <br><br><b>Código: PVU-1.1.1.1</b>
 * @author drodriguez
 */
public class PasoVerifExistenciaIMEI extends PasoValidacionUsuario {

	/**
	 * Utiliza la conexion a la base de datos oracle creada en el paso <b>PasoObtenerUsuarioOracle</b> y
	 * verifica si el IMEI del telefono está habilitado para utilizar la aplicación.<br>
	 * En caso de que si asigna  la validacion correcta a <b>resultadoValidacion</b> y guarda al usuario en la BD local.<br>
	 * Caso contrario asigna el error de asignacion de móvil.
	 * @see ValidacionUsuarioCorrecta
	 * @see ErrorDeAsignacionDeMovil
	 */
	@Override
	public PasoValidacionUsuario procesarPaso() {
		try {
			if(FlujoPasosValidacionUsuario.conexion.validarExistenciaIMEI(FlujoPasosValidacionUsuario.imei))
			{
				FlujoPasosValidacionUsuario.guardarUsuario();
				resultadoValidacion = new ValidacionUsuarioCorrecta();//validacion correcta
			}
			else
			{
				resultadoValidacion = new ErrorDeAsignacionDeMovil();
			}
		} catch (SQLException e) {
			resultadoValidacion = new ErrorUsuarioPasswordIncorrectos();
		}
		return null;
	}

}
