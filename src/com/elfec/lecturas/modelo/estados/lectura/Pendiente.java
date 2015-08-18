package com.elfec.lecturas.modelo.estados.lectura;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.view.View;

import com.elfec.lecturas.controlador.TomarLectura;
import com.elfec.lecturas.modelo.Lectura;
import com.lecturas.elfec.R;

public class Pendiente implements IEstadoLectura {

	Pendiente() {
	}

	@Override
	public int getEstadoEntero() {
		return 0;
	}

	@Override
	public String getEstadoCadena() {
		return "Pendiente";
	}

	@Override
	public int getColor(Context context) {
		return context.getResources().getColor(R.color.red_alizarin);
	}

	@Override
	public void mostrarLectura(TomarLectura tomarLectura, Lectura lecturaActual) {
		tomarLectura.lblEstadoLectura.setText(getEstadoCadena());
		tomarLectura.lblLecturaActual.setVisibility(View.INVISIBLE);
		tomarLectura.txtLecturaNueva.setVisibility(View.VISIBLE);
		tomarLectura.lblNuevaLectura.setText(tomarLectura.getResources()
				.getString(R.string.nueva_lectura_lbl));
		tomarLectura.lblEstadoLectura
				.setBackgroundColor(getColor(tomarLectura));
		tomarLectura.lblFechaLectura.setText(new SimpleDateFormat(
				"dd/MMM/yyyy", Locale.getDefault()).format(new Date()));
		tomarLectura.btnConfirmarLectura.setVisibility(View.VISIBLE);
		tomarLectura.btnPostergarLectura.setVisibility(View.VISIBLE);
		tomarLectura.btnReintentarLectura.setVisibility(View.VISIBLE);
		tomarLectura.btnAgergarOrdenativo.setVisibility(View.INVISIBLE);
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
	public Pendiente crearEstado() {
		return new Pendiente();
	}

}
