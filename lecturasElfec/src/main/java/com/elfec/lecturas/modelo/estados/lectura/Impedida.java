package com.elfec.lecturas.modelo.estados.lectura;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.elfec.lecturas.R;
import com.elfec.lecturas.controlador.TomarLectura;
import com.elfec.lecturas.helpers.ui.FloatingActionButtonAnimator;
import com.elfec.lecturas.modelo.Lectura;

public class Impedida implements IEstadoLectura {

	Impedida() {
	}

	@Override
	public int getEstadoEntero() {
		return 2;
	}

	@Override
	public String getEstadoCadena() {
		return "Impedida";
	}

	@Override
	public int getColor(Context context) {
		return ContextCompat.getColor(context, R.color.red_pomegranate);
	}

	@Override
	public void mostrarLectura(TomarLectura tomarLectura, Lectura lecturaActual) {
		tomarLectura.lblEstadoLectura.setText(getEstadoCadena());
		tomarLectura.lblLecturaActual.setText("" + lecturaActual.LecturaNueva);
		tomarLectura.lblLecturaActual.setVisibility(View.VISIBLE);
		tomarLectura.txtLecturaNueva.setVisibility(View.GONE);
		tomarLectura.lblNuevaLectura.setText(tomarLectura.getResources()
				.getString(R.string.lectura_lbl));
		tomarLectura.lblEstadoLectura
				.setBackgroundColor(getColor(tomarLectura));
		if (tomarLectura.btnConfirmarLectura.getVisibility() == View.VISIBLE
				|| FloatingActionButtonAnimator
						.isAnimating(tomarLectura.btnConfirmarLectura))
			FloatingActionButtonAnimator.hide(tomarLectura.btnConfirmarLectura);
		tomarLectura.btnPostergarLectura.setVisibility(View.GONE);
		tomarLectura.btnReintentarLectura.setVisibility(View.GONE);
		tomarLectura.btnAgergarOrdenativo.setVisibility(View.VISIBLE);
	}

	@Override
	public Impedida crearEstado() {
		return new Impedida();
	}

	@Override
	public void mostrarMenuLectura(TomarLectura tomarLectura,
			Lectura lecturaActual) {
		if (tomarLectura.menuEstimarLectura != null)
			tomarLectura.menuEstimarLectura.setVisible(false);
		if (tomarLectura.menuImpedirLectura != null)
			tomarLectura.menuImpedirLectura.setVisible(false);
		if (tomarLectura.menuVerPotencia != null)
			tomarLectura.menuVerPotencia.setVisible(true);
		if (tomarLectura.menuReImprimir != null)
			tomarLectura.menuReImprimir.setVisible(true);
		if (tomarLectura.menuModificarLectura != null)
			tomarLectura.menuModificarLectura.setVisible(true);
		if (tomarLectura.menuTomarFoto != null)
			tomarLectura.menuTomarFoto.setVisible(true);
	}

}
