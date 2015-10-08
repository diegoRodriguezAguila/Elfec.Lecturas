package com.elfec.lecturas.helpers.ui;

import android.os.SystemClock;

/**
 * Helper para el control de m�ltiples clicks seguidos a un mismo bot�n
 * 
 * @author drodriguez
 *
 */
public class ClicksBotonesHelper {
	/**
	 * Tiempo m�nimo entre clicks a un boton
	 */
	private static final int TIME_BETWEEN_CLICKS = 500;
	/**
	 * �ltimo click realizado
	 */
	private volatile static long lastClickTime = 0;

	/**
	 * Verifica si pas� el m�nimo tiempo requerido entre clicks
	 * {@link #TIME_BETWEEN_CLICKS} para realizar un click
	 * 
	 * @return true si es que se permite hacer un click
	 */
	public static synchronized boolean sePuedeClickearBoton() {
		if (SystemClock.elapsedRealtime() - lastClickTime > TIME_BETWEEN_CLICKS) {
			lastClickTime = SystemClock.elapsedRealtime();
			return true;
		}
		return false;
	}
}
