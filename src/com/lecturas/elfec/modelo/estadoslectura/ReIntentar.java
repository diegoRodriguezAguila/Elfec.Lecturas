package com.lecturas.elfec.modelo.estadoslectura;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.view.View;

import com.lecturas.elfec.R;
import com.lecturas.elfec.controlador.TomarLectura;
import com.lecturas.elfec.modelo.Lectura;

public class ReIntentar implements IEstadoLectura {

	static
	{
		EstadoLecturaFactory.registrarEstado(4,new ReIntentar());
	}
	private ReIntentar()
	{
		
	}
	@Override
	public int getEstadoEntero() {
		return 4;
	}

	@Override
	public String getEstadoCadena() {
		return "Re-Lectura";
	}

	@Override
	public int getColor(Context context) {
		return context.getResources().getColor(R.color.orange_carrot);
	}

	@Override
	public void mostrarLectura(TomarLectura tomarLectura, Lectura lecturaActual) {
		tomarLectura.lblEstadoLectura.setText(getEstadoCadena());
		tomarLectura.lblLecturaActual.setText("Reintentar dia siguiente.");
		tomarLectura.lblLecturaActual.setVisibility(View.VISIBLE);
		tomarLectura.txtLecturaNueva.setVisibility(View.INVISIBLE);
		tomarLectura.lblNuevaLectura.setText(tomarLectura.getResources().getString(R.string.lectura_lbl));
		tomarLectura.lblEstadoLectura.setBackgroundColor(getColor(tomarLectura));
		tomarLectura.lblFechaLectura.setText(new SimpleDateFormat("dd/MMM/yyyy",Locale.getDefault()).format(new Date()));
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
			tomarLectura.menuVerPotencia.setVisible(false);
		if(tomarLectura.menuReImprimir!=null)
			tomarLectura.menuReImprimir.setVisible(false);
		if(tomarLectura.menuModificarLectura!=null)
			tomarLectura.menuModificarLectura.setVisible(true);
		if(tomarLectura.menuTomarFoto!=null)
			tomarLectura.menuTomarFoto.setVisible(true);
	}
	
	@Override
	public ReIntentar crearEstado() {
		return new ReIntentar();
	}
	
}
