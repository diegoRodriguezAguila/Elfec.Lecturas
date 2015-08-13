package com.elfec.lecturas.helpers.validacionessincronizacion;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Es utilizada cuando el telefono no esta sincronizado con el servidor
 * <b>código VS-003</b>
 * @author drodriguez
 */
public class DesincronizadoDeServidor implements IEstadoSincronizacion {

	private Date fechaServidor;
	public DesincronizadoDeServidor(Date fechaServidor)
	{
		this.fechaServidor = fechaServidor;
	}
	@Override
	public String obtenerCodigo() {
		return "VS-003";
	}

	@Override
	public String obtenerMensaje() {

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.getDefault());
		String fechaHora = df.format(fechaServidor);
		return "La fecha y hora del telefono están desincronizadas del servidor, corrijala e intente nuevamente" +
				"la fecha y hora del servidor es: "+fechaHora;
	}

	@Override
	public boolean esError() {
		return true;
	}
	@Override
	public Date obtenerFechaSinc() {
		return fechaServidor;
	}


}
