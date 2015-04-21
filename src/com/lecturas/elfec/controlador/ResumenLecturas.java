package com.lecturas.elfec.controlador;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.lecturas.elfec.R;
import com.lecturas.elfec.modelo.AsignacionRuta;
import com.lecturas.elfec.modelo.Lectura;
import com.lecturas.elfec.modelo.MedidorEntreLineas;

public class ResumenLecturas extends Activity {

	private Spinner selectorRuta;
	private TextView txtNumLecturas;
	private TextView txtLecturasRealizadas;
	private TextView txtLecturasPendientes;
	private TextView txtLecturasNormales;
	private TextView txtLecturasEntreLineas;
	private TextView txtLecturasImpedidas;
	private TextView txtLecturasPostergadas;
	private TextView txtLecturasReintentar;
	private TextView txtLecturasTotales;
	
	private ArrayList<Lectura> listaLecturas;
	private ArrayList<String> listaRutas;
	private ArrayList<MedidorEntreLineas> listaLecturasEntreLineas;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resumen_lecturas);
		txtNumLecturas = (TextView) findViewById(R.id.txt_numero_lecturas);
		txtLecturasRealizadas = (TextView) findViewById(R.id.txt_lecturas_realizadas);
		txtLecturasPendientes = (TextView) findViewById(R.id.txt_lecturas_pendientes);
		txtLecturasNormales = (TextView) findViewById(R.id.txt_lecturas_normales);
		txtLecturasEntreLineas = (TextView) findViewById(R.id.txt_lecturas_entre_lineas);
		txtLecturasImpedidas = (TextView) findViewById(R.id.txt_lecturas_impedidas);
		txtLecturasPostergadas = (TextView) findViewById(R.id.txt_lecturas_postergadas);
		txtLecturasReintentar = (TextView) findViewById(R.id.txt_lecturas_reintentar);
		txtLecturasTotales = (TextView) findViewById(R.id.txt_lecturas_totales);
		listaLecturas = (ArrayList<Lectura>) Lectura.obtenerTodasLasLecturas();
		listaLecturasEntreLineas = (ArrayList<MedidorEntreLineas>) MedidorEntreLineas.obtenerMedidoresEntreLineas();
		listaRutas = new ArrayList<String>();
		listaRutas.add("Todas");
		List<AsignacionRuta> rutasUsuario = AsignacionRuta.obtenerTodasLasRutas();
		for(AsignacionRuta asignRuta : rutasUsuario)
		{
			listaRutas.add(""+asignRuta.Ruta);
		}
		asignarDatos();
		selectorRuta = (Spinner) findViewById(R.id.select_ruta);
		ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this,  R.layout.spinner_item,R.id.lbl_opcion_item, listaRutas);
		selectorRuta.setAdapter(adapter_state);
		selectorRuta.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
					int position, long id) {
				String ruta = listaRutas.get(position);
				if(ruta.equals("Todas"))
				{
					listaLecturas = (ArrayList<Lectura>) Lectura.obtenerTodasLasLecturas();
				}
				else
				{
					listaLecturas = (ArrayList<Lectura>)Lectura.obtenerLecturasDeRuta(Integer.parseInt((ruta)));
				}
				asignarDatos();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	public void asignarDatos()
	{
		txtNumLecturas.setText(""+listaLecturas.size());
		int lecturasRealizadas = 0;
		int lecturasPendientes = 0;
		int lecturasNormales = 0;
		int lecturasEntreLineas = 0;
		int lecturasImpedidas = 0;
		int lecturasPostergadas = 0;
		int lecturasReintentar = 0;
		int lecturasTotales = 0;

		for(Lectura lectura : listaLecturas)
		{
			switch(lectura.getEstadoLectura().getEstadoEntero())
			{
				case (0): //pendiente
				{
					lecturasPendientes++;
					break;
				}
				case (1): //leida
				{
					lecturasRealizadas++;
					lecturasNormales++;
					lecturasTotales++;
					break;
				}
				case (2): //impedida
				{
					lecturasRealizadas++;
					lecturasImpedidas++;
					lecturasTotales++;
					break;
				}
				case (3): //postergada
				{
					lecturasPostergadas++;
					lecturasTotales++;
					break;
				}
				case (4): //reintentar
				{
					lecturasReintentar++;
					lecturasTotales++;
					break;
				}
			}
		}
		
		lecturasEntreLineas = listaLecturasEntreLineas.size();
		lecturasRealizadas+=lecturasEntreLineas;
		lecturasTotales+=lecturasEntreLineas;
		txtLecturasRealizadas.setText(""+lecturasRealizadas);
		txtLecturasPendientes.setText(""+lecturasPendientes);
		txtLecturasNormales.setText(""+lecturasNormales);
		txtLecturasEntreLineas.setText(""+lecturasEntreLineas);
		txtLecturasImpedidas.setText(""+lecturasImpedidas);
		txtLecturasPostergadas.setText(""+lecturasPostergadas);
		txtLecturasReintentar.setText(""+lecturasReintentar);
		txtLecturasTotales.setText(""+lecturasTotales);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.resumen_lecturas, menu);
		return true;
	}
	
	public void btnInicioClick(View view)
	{ 
		Intent intent = new Intent(this, MenuPrincipal.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    startActivity(intent);
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out); 
	}
	
	@Override
	public void onBackPressed() {
	    finish();//go back to the previous Activity
	    overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);  
	}

}
