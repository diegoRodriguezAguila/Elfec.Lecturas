package com.elfec.lecturas.controlador;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.elfec.lecturas.controlador.dialogos.DialogoResultadosBusqueda;
import com.elfec.lecturas.helpers.ui.TecladoHelper;
import com.elfec.lecturas.modelo.AsignacionRuta;
import com.elfec.lecturas.modelo.Lectura;
import com.lecturas.elfec.R;

public class BuscarLectura extends AppCompatActivity {

	private View rootLayout;

	private EditText txtCuenta;
	private EditText txtMedidor;
	private EditText txtNUS;
	private Spinner selectorRuta;
	private static final String TODAS = "Todas";
	private ArrayList<String> listaRutas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buscar_lectura);
		rootLayout = findViewById(R.id.root_layout);
		txtCuenta = (EditText) findViewById(R.id.txt_cuenta);
		txtMedidor = (EditText) findViewById(R.id.txt_medidor);
		txtNUS = (EditText) findViewById(R.id.txt_bus_nus);
		selectorRuta = (Spinner) findViewById(R.id.select_ruta);
		listaRutas = new ArrayList<String>();
		listaRutas.add(TODAS);
		List<AsignacionRuta> rutasUsuario = AsignacionRuta
				.obtenerTodasLasRutas();
		Bundle extras = getIntent().getExtras();
		String rutaSeleccionada = null;
		if (extras != null) {
			rutaSeleccionada = extras.getString("RutaActual");
		}
		String ruta;
		int i = 0, posicionSeleccionada = 0;
		for (AsignacionRuta asignRuta : rutasUsuario) {
			ruta = asignRuta.obtenerRutaFormatoCuenta();
			listaRutas.add(ruta);
			if (rutaSeleccionada != null && rutaSeleccionada.equals(ruta)) {
				posicionSeleccionada = i + 1;
			}
			i++;
		}
		ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this,
				R.layout.spinner_item, R.id.lbl_opcion_item, listaRutas);
		selectorRuta.setAdapter(adapter_state);
		selectorRuta.setSelection(posicionSeleccionada);
		ponerTextListenerCuenta();
	}

	/**
	 * Pone los text listeners a la cuenta para que se pueda poner la mascara al
	 * textbox
	 */
	public void ponerTextListenerCuenta() {
		txtCuenta.addTextChangedListener(new TextWatcher() {
			private int lastChar;

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					boolean huboCambio = false;
					for (int i = 0; i < s.length(); i++) {
						if (s.charAt(i) == '-' && i != 3) {
							s.delete(i, i + 1);
						}
					}
					if (s.length() > 3 && s.charAt(3) != '-') {
						huboCambio = true;
						s.insert(3, "-");
					}
					if (huboCambio) {
						int last = lastChar;
						txtCuenta.setText(s);
						if (s.charAt(last) == '-')
							last += 2;
						txtCuenta.setSelection(last);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				/*
				 * This method is called to notify you that, within s, the count
				 * characters beginning at start are about to be replaced by new
				 * text with length after. It is an error to attempt to make
				 * changes to s from this callback.
				 */
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				lastChar = start;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.buscar_lectura, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		finish();// go back to the previous Activity
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}

	public void btnBuscarClick(View view) {
		List<Lectura> lecturasEncontradas = new ArrayList<Lectura>();
		Lectura lecturaEnc = null;
		if (txtCuenta.getText().length() == 6) {
			String rutaCuenta = selectorRuta.getSelectedItem().toString();
			if (!rutaCuenta.equals(TODAS)) {
				lecturaEnc = Lectura
						.buscarPorCuenta(transformarCuenta(rutaCuenta + "-"
								+ txtCuenta.getText()));
			} else {
				StringBuilder uDC = new StringBuilder(txtCuenta.getText()
						.toString());
				String ultimosDigitosCuenta = uDC.delete(3, 4).toString();
				lecturasEncontradas = Lectura
						.buscarPorUltimosDigitosCuenta(ultimosDigitosCuenta);
			}
		}
		if (lecturaEnc == null && lecturasEncontradas.size() == 0
				&& txtMedidor.getText().length() > 4) {
			lecturaEnc = Lectura.buscarPorNumeroMedidor(txtMedidor.getText()
					.toString());
		}
		if (lecturaEnc == null && lecturasEncontradas.size() == 0
				&& txtNUS.getText().length() > 4) {
			lecturaEnc = Lectura.buscarPorNUS(Long.parseLong(txtNUS.getText()
					.toString()));
		}
		procesarResultadosBusqueda(lecturasEncontradas, lecturaEnc);
	}

	/**
	 * Se encarga de decidir qué hacer con los resultados de la búsqueda
	 * 
	 * @param lecturasEncontradas
	 * @param lecturaEnc
	 */
	private void procesarResultadosBusqueda(List<Lectura> lecturasEncontradas,
			Lectura lecturaEnc) {
		if (lecturaEnc != null) {
			lecturasEncontradas.add(lecturaEnc);
		}
		if (lecturasEncontradas.size() > 0) {
			if (lecturasEncontradas.size() == 1)
				irALecturaEncontrada(lecturasEncontradas.get(0));
			else {
				DialogoResultadosBusqueda dialog = new DialogoResultadosBusqueda(
						this, lecturasEncontradas);
				dialog.show();
			}
		} else {
			mostrarDialogoNoResultados();
		}
	}

	/**
	 * Muestra un dialogo que indica al usuario que no se encontraron resultados
	 * de su búsqueda
	 */
	private void mostrarDialogoNoResultados() {
		new AlertDialog.Builder(this).setMessage(R.string.res_fallido_busqueda)
				.setTitle(R.string.titulo_mensajes_resultado)
				.setCancelable(false).setPositiveButton(R.string.btn_ok, null)
				.show();
	}

	/**
	 * Vuelve a la actividad de Tomar lecturas y se muestra la lectura
	 * encontrada
	 * 
	 * @param lecturaEnc
	 */
	public void irALecturaEncontrada(Lectura lecturaEnc) {
		TecladoHelper.esconderTeclado(rootLayout);
		Intent returnIntent = new Intent();
		returnIntent.putExtra(TomarLectura.ARG_ID_LECTURA, lecturaEnc.getId());
		setResult(RESULT_OK, returnIntent);
		finish();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}

	/**
	 * Transforma una cuenta serie numerica a una formateada con guiones
	 * 
	 * @param cuenta
	 * @return
	 */
	private String transformarCuenta(String cuenta) {
		StringBuilder str = new StringBuilder(cuenta.toString());
		str.delete(2, 3);
		str.delete(5, 6);
		str.delete(8, 9);
		if (str.charAt(0) == '0') {
			str.delete(0, 1);
		}
		return str.toString();
	}

}
