package com.elfec.lecturas.modelo.estados.lectura;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.view.View;

import com.elfec.lecturas.controlador.TomarLectura;
import com.elfec.lecturas.modelo.Lectura;
import com.lecturas.elfec.R;

public class Impedida implements IEstadoLectura {

	static
	{
		EstadoLecturaFactory.registrarEstado(2,new Impedida());
	}
	
	private Impedida()
	{
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
		return context.getResources().getColor(R.color.red_pomegranate);
	}

	@Override
	public void mostrarLectura(TomarLectura tomarLectura, Lectura lecturaActual) {
		tomarLectura.lblEstadoLectura.setText(getEstadoCadena());
		tomarLectura.lblLecturaActual.setText(""+lecturaActual.LecturaNueva);
		tomarLectura.lblLecturaActual.setVisibility(View.VISIBLE);
		tomarLectura.txtLecturaNueva.setVisibility(View.INVISIBLE);
		tomarLectura.lblNuevaLectura.setText(tomarLectura.getResources().getString(R.string.lectura_lbl));
		tomarLectura.lblEstadoLectura.setBackgroundColor(getColor(tomarLectura));
		tomarLectura.lblFechaLectura.setText(new SimpleDateFormat("dd/MMM/yyyy",Locale.getDefault()).format(lecturaActual.FechaLecturaActual));
		tomarLectura.btnConfirmarLectura.setVisibility(View.INVISIBLE);
		tomarLectura.btnPostergarLectura.setVisibility(View.INVISIBLE);
		tomarLectura.btnReintentarLectura.setVisibility(View.INVISIBLE);
		tomarLectura.btnAgergarOrdenativo.setVisibility(View.VISIBLE);
	}

	@Override
	public Impedida crearEstado() {
		return new Impedida();
	}

	@Override
	public void mostrarMenuLectura(TomarLectura tomarLectura,Lectura lecturaActual) {
		if(tomarLectura.menuEstimarLectura!=null)
			tomarLectura.menuEstimarLectura.setVisible(false);
		if(tomarLectura.menuImpedirLectura!=null)
			tomarLectura.menuImpedirLectura.setVisible(false);
		if(tomarLectura.menuVerPotencia!=null)
			tomarLectura.menuVerPotencia.setVisible(true);
		if(tomarLectura.menuReImprimir!=null)
			tomarLectura.menuReImprimir.setVisible(true);
		if(tomarLectura.menuModificarLectura!=null)
			tomarLectura.menuModificarLectura.setVisible(true);
		if(tomarLectura.menuTomarFoto!=null)
			tomarLectura.menuTomarFoto.setVisible(true);
	}

}
