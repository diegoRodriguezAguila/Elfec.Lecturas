package com.elfec.lecturas.helpers.ui;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

/**
 * Helper para asignar background a vistas
 * 
 * @author drodriguez
 *
 */
public class ViewBackgroundCompat {
	/**
	 * Asigna el background
	 * 
	 * @param view
	 * @param background
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void setBackground(View view, Drawable background) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			view.setBackground(background);
		} else {
			view.setBackgroundDrawable(background);
		}
	}
}
