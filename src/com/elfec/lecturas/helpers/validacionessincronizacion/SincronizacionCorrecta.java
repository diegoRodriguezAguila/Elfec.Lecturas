package com.elfec.lecturas.helpers.validacionessincronizacion;

import java.util.Date;

/**
 * Es utilizada cuando la sincronizacion con el servidor es correcta
 * <b>código VS-002</b>
 * @author drodriguez
 */
public class SincronizacionCorrecta implements
		IEstadoSincronizacion {

	private Date fechaServidor;
	public SincronizacionCorrecta(Date fechaServidor)
	{
		this.fechaServidor = fechaServidor;
	}
	
	@Override
	public String obtenerCodigo() {
		return "VS-002";
	}

	@Override
	public String obtenerMensaje() {
		return "Sincronizacion correcta!";
	}

	@Override
	public boolean esError() {
		return false;
	}

	@Override
	public Date obtenerFechaSinc() {
		return fechaServidor;
	}

}
