package com.elfec.lecturas.controlador;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.elfec.lecturas.R;
import com.elfec.lecturas.modelo.AsignacionRuta;
import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.MedidorEntreLineas;

public class ResumenLecturas extends AppCompatActivity {

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

	private List<AsignacionRuta> listaRutas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resumen_lecturas);
		new Thread(new Runnable() {
			@Override
			public void run() {
				asignarCampos();
				obtenerSpinnerRutas();
				ponerItemSelectedListenerRutas();
				obtenerDatos(-1);
			}
		}).start();
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}

	/**
	 * Obtiene los datos de la ruta
	 * 
	 * @param ruta
	 *            -1 para todas las rutas
	 */
	public void obtenerDatos(int ruta) {
		int numLecturas = Lectura.countLecturasPorEstadoYRuta(ruta);
		int lecturasRealizadas = Lectura
				.countLecturasPorEstadoYRuta(ruta, 1, 2);
		int lecturasPendientes = Lectura.countLecturasPorEstadoYRuta(ruta, 0);
		int lecturasNormales = Lectura.countLecturasPorEstadoYRuta(ruta, 1);
		int lecturasEntreLineas = MedidorEntreLineas.countTotalLecturas();
		int lecturasImpedidas = Lectura.countLecturasPorEstadoYRuta(ruta, 2);
		int lecturasPostergadas = Lectura.countLecturasPorEstadoYRuta(ruta, 3);
		int lecturasReintentar = Lectura.countLecturasPorEstadoYRuta(ruta, 4);
		int lecturasTotales = Lectura.countLecturasPorEstadoYRuta(ruta, 1, 2,
				3, 4);
		lecturasRealizadas += lecturasEntreLineas;
		lecturasTotales += lecturasEntreLineas;
		asignarDatos(numLecturas, lecturasRealizadas, lecturasPendientes,
				lecturasNormales, lecturasEntreLineas, lecturasImpedidas,
				lecturasPostergadas, lecturasReintentar, lecturasTotales);
	}

	private void asignarDatos(final int numLecturas,
			final int lecturasRealizadas, final int lecturasPendientes,
			final int lecturasNormales, final int lecturasEntreLineas,
			final int lecturasImpedidas, final int lecturasPostergadas,
			final int lecturasReintentar, final int lecturasTotales) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				txtNumLecturas.setText("" + numLecturas);
				txtLecturasRealizadas.setText("" + lecturasRealizadas);
				txtLecturasPendientes.setText("" + lecturasPendientes);
				txtLecturasNormales.setText("" + lecturasNormales);
				txtLecturasEntreLineas.setText("" + lecturasEntreLineas);
				txtLecturasImpedidas.setText("" + lecturasImpedidas);
				txtLecturasPostergadas.setText("" + lecturasPostergadas);
				txtLecturasReintentar.setText("" + lecturasReintentar);
				txtLecturasTotales.setText("" + lecturasTotales);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.resumen_lecturas, menu);
		return true;
	}

	public void btnInicioClick(View view) {
		Intent intent = new Intent(this, MenuPrincipal.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}

	@Override
	public void onBackPressed() {
		finish();// go back to the previous Activity
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}

	/**
	 * Asigna los campos a las variables
	 */
	private void asignarCampos() {
		txtNumLecturas = (TextView) findViewById(R.id.txt_numero_lecturas);
		txtLecturasRealizadas = (TextView) findViewById(R.id.txt_lecturas_realizadas);
		txtLecturasPendientes = (TextView) findViewById(R.id.txt_lecturas_pendientes);
		txtLecturasNormales = (TextView) findViewById(R.id.txt_lecturas_normales);
		txtLecturasEntreLineas = (TextView) findViewById(R.id.txt_lecturas_entre_lineas);
		txtLecturasImpedidas = (TextView) findViewById(R.id.txt_lecturas_impedidas);
		txtLecturasPostergadas = (TextView) findViewById(R.id.txt_lecturas_postergadas);
		txtLecturasReintentar = (TextView) findViewById(R.id.txt_lecturas_reintentar);
		txtLecturasTotales = (TextView) findViewById(R.id.txt_lecturas_totales);
		selectorRuta = (Spinner) findViewById(R.id.select_ruta);
	}

	private void obtenerSpinnerRutas() {
		listaRutas = AsignacionRuta.obtenerTodasLasRutas();
		List<String> listaRutasStr = new ArrayList<String>();
		listaRutasStr.add("Todas");
		for (AsignacionRuta asignRuta : listaRutas) {
			listaRutasStr.add("" + asignRuta.Ruta);
		}
		setRutas(listaRutasStr);
	}

	/**
	 * Asigna las rutas del spinner
	 * 
	 * @param listaRutas
	 */
	private void setRutas(final List<String> listaRutas) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				selectorRuta.setAdapter(new ArrayAdapter<String>(
						ResumenLecturas.this, R.layout.spinner_item,
						R.id.lbl_opcion_item, listaRutas));
			}
		});
	}

	/**
	 * Asigna el item selected listener
	 */
	private void ponerItemSelectedListenerRutas() {
		selectorRuta.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, final int position, long id) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						obtenerDatos(position == 0 ? -1 : (listaRutas
								.get(position - 1).Ruta));
					}
				}).start();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

}
