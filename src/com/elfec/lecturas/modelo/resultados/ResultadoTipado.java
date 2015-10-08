package com.elfec.lecturas.modelo.resultados;

import java.util.ArrayList;

/**
 * Es una clase que contiene el resultado de un acceso a datos adicionalmente de
 * una lista de errores en caso de haber ocurrido alguno
 * 
 * @author Diego
 *
 * @param <TResult>
 */
public class ResultadoTipado<TResult> extends ResultadoVoid {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = 8475461808008087175L;
	protected transient TResult resultado;

	public ResultadoTipado() {
		listaErrores = new ArrayList<Exception>();
	}

	public ResultadoTipado(TResult result) {
		this.resultado = result;
		listaErrores = new ArrayList<Exception>();
	}

	/**
	 * Asigna el resultado a un servicio web
	 * 
	 * @param result
	 * @return
	 */
	public ResultadoTipado<TResult> setResultado(TResult result) {
		this.resultado = result;
		return this;
	}

	/**
	 * Obtiene el resultado de un acceso a datos
	 * 
	 * @return Resultado del acceso a datos
	 */
	public TResult getResultado() {
		return this.resultado;
	}
}
