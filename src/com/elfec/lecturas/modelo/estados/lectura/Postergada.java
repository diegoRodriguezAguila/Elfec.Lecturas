package com.elfec.lecturas.modelo.estados.lectura;

import android.content.Context;
import android.view.View;

import com.elfec.lecturas.R;
import com.elfec.lecturas.controlador.TomarLectura;
import com.elfec.lecturas.helpers.ui.FloatingActionButtonAnimator;
import com.elfec.lecturas.modelo.Lectura;

public class Postergada implements IEstadoLectura {

	Postergada() {

	}

	@Override
	public int getEstadoEntero() {
		return 3;
	}

	@Override
	public String getEstadoCadena() {
		return "Postergada";
	}

	@Override
	public int getColor(Context context) {
		return context.getResources().getColor(R.color.purple_wisteria);
	}

	@Override
	public void mostrarLectura(TomarLectura tomarLectura, Lectura lecturaActual) {
		tomarLectura.lblEstadoLectura.setText(getEstadoCadena());
		tomarLectura.lblLecturaActual.setVisibility(View.GONE);
		tomarLectura.txtLecturaNueva.setVisibility(View.VISIBLE);
		tomarLectura.lblNuevaLectura.setText(tomarLectura.getResources()
				.getString(R.string.nueva_lectura_lbl));
		tomarLectura.lblEstadoLectura
				.setBackgroundColor(getColor(tomarLectura));
		if (tomarLectura.btnConfirmarLectura.getVisibility() == View.GONE
				|| FloatingActionButtonAnimator
						.isAnimating(tomarLectura.btnConfirmarLectura))
			FloatingActionButtonAnimator.show(tomarLectura.btnConfirmarLectura);
		tomarLectura.btnPostergarLectura.setVisibility(View.GONE);
		tomarLectura.btnReintentarLectura.setVisibility(View.VISIBLE);
		tomarLectura.btnAgergarOrdenativo.setVisibility(View.GONE);
	}

	@Override
	public void mostrarMenuLectura(TomarLectura tomarLectura,
			Lectura lecturaActual) {
		if (tomarLectura.menuEstimarLectura != null)
			tomarLectura.menuEstimarLectura.setVisible(true);
		if (tomarLectura.menuImpedirLectura != null)
			tomarLectura.menuImpedirLectura.setVisible(true);
		if (tomarLectura.menuVerPotencia != null)
			tomarLectura.menuVerPotencia.setVisible(false);
		if (tomarLectura.menuReImprimir != null)
			tomarLectura.menuReImprimir.setVisible(false);
		if (tomarLectura.menuModificarLectura != null)
			tomarLectura.menuModificarLectura.setVisible(false);
		if (tomarLectura.menuTomarFoto != null)
			tomarLectura.menuTomarFoto.setVisible(false);
	}

	@Override
	public Postergada crearEstado() {
		return new Postergada();
	}

}
