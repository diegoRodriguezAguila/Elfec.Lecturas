package com.elfec.lecturas.controlador;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.elfec.lecturas.R;
import com.elfec.lecturas.controlador.accionesycustomizaciones.SquareButton;
import com.elfec.lecturas.controlador.dialogos.DialogoSeleccionImpresora;
import com.elfec.lecturas.helpers.ManejadorImpresora;
import com.elfec.lecturas.helpers.ui.ClicksBotonesHelper;
import com.elfec.lecturas.modelo.detallesresumenes.DetalleImpedidas;
import com.elfec.lecturas.modelo.detallesresumenes.DetalleLecturas;
import com.elfec.lecturas.modelo.detallesresumenes.DetalleLecturasEntreLineas;
import com.elfec.lecturas.modelo.detallesresumenes.DetalleOrdenativos;
import com.elfec.lecturas.modelo.detallesresumenes.DetalleResumenGenerico;
import com.elfec.lecturas.modelo.excepciones.ImpresoraPredefinidaNoAsignadaException;
import com.elfec.lecturas.settings.VariablesDeEntorno;

public class MenuPrincipal extends AppCompatActivity {

	private SquareButton btnTomarLecturas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_principal);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					btnTomarLecturas = (SquareButton) findViewById(R.id.btn_tomar_lecturas);
					VariablesDeEntorno.inicializar();
					mostrarBotonTomarLecturas();
				} catch (Exception e) {
					mostrarDialogoErrorVariables();
				}
			}
		}).start();
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}

	/**
	 * Hace visible el boton de tomar lecturas
	 */
	private void mostrarBotonTomarLecturas() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				btnTomarLecturas.setVisibility(View.VISIBLE);
			}
		});
	}

	/**
	 * Muestra el dialogo de error en caso de que las variables de entorno de
	 * las tablas parametrizables no se encuentren correctas en el dispositivo
	 */
	private void mostrarDialogoErrorVariables() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new AlertDialog.Builder(MenuPrincipal.this)
						.setMessage(R.string.no_variables_msg)
						.setTitle(R.string.titulo_no_variables)
						.setCancelable(false)
						.setPositiveButton(R.string.btn_ok,
								new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										onBackPressed();
									}
								}).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_principal, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		finish();// go back to the previous Activity
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}

	/**
	 * Inicia la actividad de tomar lecturas
	 */
	public void btnTomarLecturasClick(View view) {
		if (ClicksBotonesHelper.sePuedeClickearBoton()) {
			Intent intent = new Intent(this, TomarLectura.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
		}
	}

	/**
	 * Inicia la actividad de resumen de lecturas
	 * 
	 * @param view
	 */
	public void btnResumenLecturasClick(View view) {
		if (ClicksBotonesHelper.sePuedeClickearBoton()) {
			Intent intent = new Intent(this, ResumenLecturas.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
		}
	}

	/**
	 * Muestra un dialogo para confirmar la impresion de un resumen. Utiliza el
	 * titulo, mensaje y resumen pasados en los parametros
	 * 
	 * @param idStringMensaje
	 * @param idStringTitulo
	 * @param resumenAImprimir
	 */
	public void mostrarDialogoImprimirResumen(int idStringMensaje,
			int idStringTitulo, final DetalleResumenGenerico resumenAImprimir) {
		new AlertDialog.Builder(this).setMessage(idStringMensaje)
				.setIcon(R.drawable.imprimir_d).setTitle(idStringTitulo)
				.setPositiveButton(R.string.btn_ok, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (!ManejadorImpresora
								.impresoraPredefinidaFueAsignada()) {
							mostrarDialogoSeleccionarImpresora(resumenAImprimir);
						} else {
							iniciarImpresionResumen(resumenAImprimir);
						}
					}
				}).setNegativeButton(R.string.btn_cancel, null).show();
	}

	/**
	 * Muestra el dialogo para seleccionar una impresora antes de imprimir
	 */
	public void mostrarDialogoSeleccionarImpresora(
			final DetalleResumenGenerico resumenAImprimir) {
		new DialogoSeleccionImpresora(MenuPrincipal.this)
				.addOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						iniciarImpresionResumen(resumenAImprimir);
					}
				}).show();
	}

	/**
	 * Invoca al metodo para imprimir un resumen
	 * 
	 * @param resumenAImprimir
	 */
	public void iniciarImpresionResumen(DetalleResumenGenerico resumenAImprimir) {
		try {
			ManejadorImpresora.imprimir(resumenAImprimir.obtenerImprimible());
		} catch (ImpresoraPredefinidaNoAsignadaException e) {
			Toast.makeText(MenuPrincipal.this, e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Muestra el dialogo para confirmar la impresion del resumen de detalle de
	 * ordenativos
	 * 
	 * @param view
	 */
	public void btnDetalleOrdenativosClick(View view) {
		if (ClicksBotonesHelper.sePuedeClickearBoton()) {
			mostrarDialogoImprimirResumen(R.string.detalle_ordenativos_msg,
					R.string.titulo_detalle_ordenativos,
					new DetalleOrdenativos());
		}
	}

	/**
	 * Muestra el dialogo para confirmar la impresion del resumen de detalle de
	 * lecturas
	 * 
	 * @param view
	 */
	public void btnDetalleLecturasClick(View view) {
		if (ClicksBotonesHelper.sePuedeClickearBoton()) {
			mostrarDialogoImprimirResumen(R.string.detalle_lecturas_msg,
					R.string.titulo_detalle_lecturas, new DetalleLecturas());
		}
	}

	/**
	 * Muestra el dialogo para confirmar la impresion del resumen de detalle de
	 * lecturas impedidas
	 * 
	 * @param view
	 */
	public void btnDetalleImpedidasClick(View view) {
		if (ClicksBotonesHelper.sePuedeClickearBoton()) {
			mostrarDialogoImprimirResumen(R.string.detalle_impedidas_msg,
					R.string.titulo_detalle_impedidas, new DetalleImpedidas());
		}
	}

	/**
	 * Muestra el dialogo para confirmar la impresion del resumen de detalle de
	 * lecturas entre lineas
	 * 
	 * @param view
	 */
	public void btnDetalleEntreLineasClick(View view) {
		if (ClicksBotonesHelper.sePuedeClickearBoton()) {
			mostrarDialogoImprimirResumen(R.string.detalle_entre_lineas_msg,
					R.string.titulo_detalle_entre_lineas,
					new DetalleLecturasEntreLineas());
		}
	}

}
