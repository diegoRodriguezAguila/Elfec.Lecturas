package com.elfec.lecturas.modelo.validaciones.sincronizacion;

import java.util.Date;

/**
 * Es utilizada cuando la conexion con el servidor fall� o no se pudo obtener su fecha
 * <b>c�digo VS-001</b>
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
		return "Ocurri� un problema al intentar conectar con el servidor, revise su nombre de usuario, contrase�a y que est� conectado a la red de la empresa.";
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
