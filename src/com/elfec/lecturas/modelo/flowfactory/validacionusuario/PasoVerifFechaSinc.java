package com.elfec.lecturas.modelo.flowfactory.validacionusuario;

import java.util.Calendar;
import java.util.Date;

import com.elfec.lecturas.modelo.validaciones.ErrorRangoFechaUsuario;

/**
 * Define el paso de validación en el que se valida que la fecha de
 * sincronización esté en el rango permitido. <br>
 * <br>
 * <b>Código: PVU-1.2</b>
 * 
 * @author drodriguez
 *
 */
public class PasoVerifFechaSinc extends PasoValidacionUsuario {

	/**
	 * Verifica que la fecha se encuentre en un rango valido para el ingreso del
	 * usuario previamente sincronizado y procede al siguiente paso segun el
	 * resultado.
	 * 
	 * @see PasoVerifDatosCargados
	 * @see PasoVerifPassword
	 */
	@Override
	public PasoValidacionUsuario procesarPaso() {
		// fechas sincronización no está en rango
		if (!fechaSincronizacionEstaEnRango(
				FlujoPasosValidacionUsuario.usuarioEncontrado.FechaSincronizacion,
				FlujoPasosValidacionUsuario.usuarioEncontrado.RangoDias)) {
			resultadoValidacion = new ErrorRangoFechaUsuario(
					FlujoPasosValidacionUsuario.usuarioEncontrado.FechaSincronizacion);
			return null;
		}
		return new PasoVerifPassword();
	}

	/**
	 * Verifica si la fecha de sincronizacion de la sesion del usuario esta
	 * dentro el rango permitido según el campo RangoDias del Usuario.
	 * 
	 * @param fechaSinc
	 *            , fecha de sincronizacion del usuario
	 * @return
	 */
	private static boolean fechaSincronizacionEstaEnRango(Date fechaSinc,
			int rangoDias) {
		if (fechaSinc == null)
			return false;
		Calendar calFechaSinc = Calendar.getInstance();
		calFechaSinc.setTime(fechaSinc);
		Calendar calFechaActual = Calendar.getInstance();
		return (calFechaSinc.get(Calendar.YEAR) == calFechaActual
				.get(Calendar.YEAR)
				&& calFechaSinc.get(Calendar.MONTH) == calFechaActual
						.get(Calendar.MONTH) && (calFechaActual
				.get(Calendar.DAY_OF_MONTH) >= calFechaSinc
				.get(Calendar.DAY_OF_MONTH) && calFechaActual
				.get(Calendar.DAY_OF_MONTH) < (calFechaSinc
				.get(Calendar.DAY_OF_MONTH) + rangoDias)));
	}

}
