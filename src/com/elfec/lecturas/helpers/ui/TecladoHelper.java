package com.elfec.lecturas.helpers.ui;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Helper para acciones del teclado
 * 
 * @author drodriguez
 *
 */
public class TecladoHelper {

	/**
	 * Esconde el teclado
	 */
	public static void esconderTeclado(View rootView) {
		if (rootView != null) {
			// Check if no view has focus:
			View view = rootView.findFocus();
			if (view != null) {
				InputMethodManager inputManager = (InputMethodManager) rootView
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(view.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}
}
