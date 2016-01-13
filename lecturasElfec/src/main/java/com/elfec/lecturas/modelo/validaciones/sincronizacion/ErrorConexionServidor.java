package com.elfec.lecturas.modelo.validaciones.sincronizacion;

import java.util.Date;

/**
 * Es utilizada cuando la conexion con el servidor falló o no se pudo obtener su fecha
 * <b>código VS-001</b>
 * @author drodriguez
 *
 */
public class ErrorConexionServidor implements IEstadoSincronizacion {

	@Override
	public String obtenerCodigo() {
		return "VS-001";
	}

	@Override
	public String obtenerMensaje() {
		return "Ocurrió un problema al intentar conectar con el servidor, revise su nombre de usuario, contraseña y que esté conectado a la red de la empresa.";
	}

	@Override
	public boolean esError() {
		return true;
	}

	@Override
	public Date obtenerFechaSinc() {
		return null;
	}

}
