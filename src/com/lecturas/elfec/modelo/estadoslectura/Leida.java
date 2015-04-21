package com.lecturas.elfec.modelo.estadoslectura;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.view.View;

import com.lecturas.elfec.R;
import com.lecturas.elfec.controlador.TomarLectura;
import com.lecturas.elfec.modelo.Lectura;

public class Leida implements IEstadoLectura {

	static
	{
		EstadoLecturaFactory.registrarEstado(1,new Leida());
	}
	private Leida()
	{
		
	}
	@Override
	public int getEstadoEntero() {
		return 1;
	}

	@Override
	public String getEstadoCadena() {
		return "Le�da";
	}

	@Override
	public int getColor(Context context) {
		return context.getResources().getColor(R.color.green_nephritis);
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
	
	@Override
	public Leida crearEstado() {
		return new Leida();
	}

}
