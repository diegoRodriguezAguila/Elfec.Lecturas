package com.elfec.lecturas.controlador.accionesycustomizaciones;

/**
 * Define los eventos de Swipe en una actividad, sirve para poner un Swipe Listener
 * @author drodriguez
 *
 */
public interface ISwipeListener {
	
	public void onRightToLeftSwipe();

	public void onLeftToRightSwipe();

	public void onTopToBottomSwipe();

	public void onBottomToTopSwipe();
}
