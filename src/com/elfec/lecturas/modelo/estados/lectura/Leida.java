package com.elfec.lecturas.modelo.estados.lectura;

import android.content.Context;
import android.view.View;

import com.elfec.lecturas.R;
import com.elfec.lecturas.controlador.TomarLectura;
import com.elfec.lecturas.helpers.ui.FloatingActionButtonAnimator;
import com.elfec.lecturas.modelo.Lectura;

public class Leida implements IEstadoLectura {

	Leida() {

	}

	@Override
	public int getEstadoEntero() {
		return 1;
	}

	@Override
	public String getEstadoCadena() {
		return "Leída";
	}

	@Override
	public int getColor(Context context) {
		return context.getResources().getColor(R.color.green_nephritis);
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

	@Override
	public Leida crearEstado() {
		return new Leida();
	}

}
