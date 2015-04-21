package com.lecturas.elfec.modelo.validaciones;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Es utilizada cuando un usuario se intenta logear y la fecha del movil
 * no corresponde a la fecha resultado de la sincronizacion con el servidor o la fecha de sincronizaci�n
 * de la fecha del usuario no esta dentro el rango permitido
 * <b>c�digo VU-005</b>
 * @author drodriguez
 *
 */
public class ErrorRangoFechaUsuario implements IValidacionUsuario {

	private Date fechaServidor;
	
	public ErrorRangoFechaUsuario(Date fechaServidor)
	{
		this.fechaServidor = fechaServidor;
	}
	
	@Override
	public String obtenerCodigo() {
		return "VU-005";
	}

	@Override
	public String obtenerMensaje() {
		
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
		String fecha = df.format(fechaServidor);
		return "La fecha del dispositivo no corresponde con la que se sincroniz� con el servidor y tampoco est� en el rango de dias permitidos, la fecha sincronizada es: "+fecha
				+". Este error puede ocurrir cuando el dispositivo se qued� sin bateria y la fecha no coincide. Tambi�n puede ser que "+
		"no se hayan descargado los datos del d�a anterior, para ello contacte con un administrador";
	}

	@Override
	public boolean esError() {
		return true;
	}

}
