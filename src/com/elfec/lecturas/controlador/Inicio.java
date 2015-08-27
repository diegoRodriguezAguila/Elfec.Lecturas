package com.elfec.lecturas.controlador;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alertdialogpro.ProgressDialogPro;
import com.elfec.lecturas.R;
import com.elfec.lecturas.acceso_remoto_datos.ConectorBDOracle;
import com.elfec.lecturas.controlador.dialogos.DialogoSeleccionImpresora;
import com.elfec.lecturas.controlador.observers.IDataExportationObserver;
import com.elfec.lecturas.controlador.observers.IDataImportationObserver;
import com.elfec.lecturas.helpers.ui.ClicksBotonesHelper;
import com.elfec.lecturas.helpers.utils.text.MessageListFormatter;
import com.elfec.lecturas.logica_negocio.AsignacionRutaManager;
import com.elfec.lecturas.logica_negocio.EliminacionDatosManager;
import com.elfec.lecturas.modelo.AsignacionRuta;
import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.SesionUsuario;
import com.elfec.lecturas.modelo.resultados.ResultadoTipado;
import com.elfec.lecturas.modelo.resultados.ResultadoVoid;
import com.elfec.lecturas.modelo.seguridad.Permisos;
import com.elfec.lecturas.servicios.ServicioExportacionDatos;
import com.elfec.lecturas.servicios.ServicioImportacionDatos;
import com.elfec.lecturas.servicios.receivers.DataExportationReceiver;
import com.elfec.lecturas.servicios.receivers.DataImportationReceiver;
import com.elfec.lecturas.settings.AdministradorSeguridad;
import com.elfec.lecturas.settings.VariablesDeSesion;

/**
 * Controlador de la actividad de Inicio, esta es la segunda pantalla a la que
 * se accede despues del Login
 * 
 * @author drodriguez
 *
 */
public class Inicio extends AppCompatActivity implements
		IDataImportationObserver, IDataExportationObserver {

	/**
	 * Habilita o deshabilita el boton de Descargar datos, verificando que se
	 * hayan realizado todas las lecturas
	 */
	private boolean btnDescargarHabilitado;

	private TextView lblNomUsuario;
	private TextView lblFecha;
	private TextView lblNumIMEI;
	private TextView lblNumRuta;
	private TextView lblInfoCargaDatos;
	private Button btnMenuPrincipal;
	private Button btnCargarDatos;
	private Button btnDescargarDatos;

	private ProgressDialogPro progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inicio);
		lblNumRuta = (TextView) findViewById(R.id.lbl_numero_ruta);
		btnMenuPrincipal = (Button) findViewById(R.id.btn_menu_principal);
		btnCargarDatos = (Button) findViewById(R.id.btn_cargar_datos);
		btnDescargarDatos = (Button) findViewById(R.id.btn_descargar_datos);
		lblInfoCargaDatos = (TextView) findViewById(R.id.lbl_info_carga_datos);
		lblNomUsuario = (TextView) findViewById(R.id.lbl_nom_usuario);
		lblFecha = (TextView) findViewById(R.id.lbl_fecha);
		lblNumIMEI = (TextView) findViewById(R.id.lbl_num_imei);
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}

	@Override
	public void onResume() {
		super.onResume();
		new Thread(new Runnable() {
			public void run() {
				obtenerTextos();
				obtenerEstadoBotonDescargar();
				obtenerEstadoBotonCargar();
				asignarLabelDeRutas();
			}
		}).start();
	}

	/**
	 * Asigna los datos a los labels de nombre de usuario, fecha e IMEI
	 */
	public void obtenerTextos() {
		String usuario = VariablesDeSesion.getUsuarioLogeado();
		Date hoy = new Date();
		String fechaHoy = new SimpleDateFormat("dd/MMM/yyyy",
				Locale.getDefault()).format(hoy);
		String imei = VariablesDeSesion.getImeiCelular();
		asignarTextos(usuario, fechaHoy, imei);
	}

	/**
	 * Asigna los campos de los textos
	 * 
	 * @param usuario
	 * @param fecha
	 * @param imei
	 */
	public void asignarTextos(final String usuario, final String fecha,
			final String imei) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (usuario != null && !usuario.isEmpty())
					lblNomUsuario.setText(usuario);
				lblFecha.setText(fecha);
				if (imei != null && !imei.isEmpty())
					lblNumIMEI.setText(imei);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.inicio, menu);
		(menu.findItem(R.id.menu_item_configurar))
				.setVisible(AdministradorSeguridad
						.obtenerAdministradorSeguridad(
								VariablesDeSesion.getPerfilUsuario())
						.tienePermiso(Permisos.CONFIGURAR_SERVIDOR));
		(menu.findItem(R.id.menu_item_forzar_descarga))
				.setVisible(AdministradorSeguridad
						.obtenerAdministradorSeguridad(
								VariablesDeSesion.getPerfilUsuario())
						.tienePermiso(Permisos.FORZAR_DESCARGA));
		(menu.findItem(R.id.menu_item_eliminar_datos))
				.setVisible(AdministradorSeguridad
						.obtenerAdministradorSeguridad(
								VariablesDeSesion.getPerfilUsuario())
						.tienePermiso(Permisos.ELIMINAR_DATOS));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int idItem = item.getItemId();
		switch (idItem) {
		case (R.id.menu_item_selec_impresora): {
			btnMenuSelecImpresoraClick(item);
			return true;
		}
		case (R.id.menu_item_configurar): {
			btnMenuConfigurarClick(item);
			return true;
		}
		case (R.id.menu_item_forzar_descarga): {
			forzarDescarga();
			return true;
		}
		case (R.id.menu_item_eliminar_datos): {
			eliminarDatos();
			return true;
		}
		default: {
			return true;
		}
		}
	}

	/**
	 * Elimina todos los datos diarios y mensuales, es invocado cuando se
	 * presiona la opcion del menu de eliminar datos y se confirma. Esta opción
	 * deberia estar disponible solo para administradores.
	 */
	private void eliminarDatos() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.titulo_eliminar_todos_los_datos)
				.setMessage(R.string.msg_wipe_all_data_confirm)
				.setIcon(R.drawable.eliminar_todos_los_datos_d)
				.setPositiveButton(R.string.btn_ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								EliminacionDeDatos eliminacionDeDatos = new EliminacionDeDatos(
										Inicio.this);
								eliminacionDeDatos.execute((Void[]) null);
							}
						}).setNegativeButton(R.string.btn_cancel, null).show();
	}

	/**
	 * Fuerza la descarga de datos, aun cuando no se hayan terminado de realizar
	 * todas las lecturas. Esta opción deberia estar disponible solo para
	 * administradores.
	 */
	private void forzarDescarga() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.titulo_forzar_descarga)
				.setMessage(R.string.forzar_descarga_msg)
				.setIcon(R.drawable.warning_export_to_server)
				.setPositiveButton(R.string.btn_ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								iniciarExportacionDatos();
							}
						}).setNegativeButton(R.string.btn_cancel, null).show();
	}

	@Override
	public void onBackPressed() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				SesionUsuario.cerrarSesionUsuario(VariablesDeSesion
						.getUsuarioLogeado());
			}
		}).start();
		finish();// go back to the previous Activity
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}

	/**
	 * Va a la actividad del MenuPrinipal
	 * 
	 * @param view
	 */
	public void btnMenuPrincipalClick(View view) {
		if (ClicksBotonesHelper.sePuedeClickearBoton()) {
			Intent intent = new Intent(this, MenuPrincipal.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
		}
	}

	/**
	 * Inicia la actividad de Configurar el servidor. Esta opción deberia estar
	 * disponible solo para administradores.
	 * 
	 * @param menuitem
	 */
	public void btnMenuConfigurarClick(MenuItem menuitem) {
		if (ClicksBotonesHelper.sePuedeClickearBoton()) {
			Intent intent = new Intent(this, Configurar.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
		}
	}

	/**
	 * Abre el cuadro de dialogo para selección de impresora
	 * 
	 * @param menuitem
	 */
	public void btnMenuSelecImpresoraClick(MenuItem menuitem) {
		if (ClicksBotonesHelper.sePuedeClickearBoton()) {
			DialogoSeleccionImpresora dialogo = new DialogoSeleccionImpresora(
					this);
			dialogo.show();
		}
	}

	/**
	 * Llena el label de asignacion de rutas segun las rutas que se hayan
	 * asignado al usuario
	 */
	public void asignarLabelDeRutas() {
		List<AsignacionRuta> listaRutas = AsignacionRuta
				.obtenerRutasDeUsuario(VariablesDeSesion.getUsuarioLogeado());
		if (listaRutas.size() == 0) {
			setLabelDeRutas(getText(R.string.ruta_info_lbl));
		} else {
			StringBuilder strBuild = new StringBuilder("");
			int tam = listaRutas.size();
			for (int i = 0; i < tam; i++) {
				strBuild.append(listaRutas.get(i).Ruta);
				if (i < tam - 1)
					strBuild.append(", ");
			}
			setLabelDeRutas(strBuild.toString());
		}
	}

	public void setLabelDeRutas(final CharSequence txt) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				lblNumRuta.setText(txt);
			}
		});
	}

	/**
	 * Habilita o deshabilita el boton de Cargar datos, verificando si es que ya
	 * fueron cargados todos los datos
	 */
	public void obtenerEstadoBotonCargar() {
		boolean todasRutasCargadas = AsignacionRuta
				.seCargaronTodasLasRutasAsignadas();
		asignarEstadoBotonCargar(
				todasRutasCargadas,
				!todasRutasCargadas,
				(getResources()
						.getString(todasRutasCargadas ? R.string.info_datos_cargados_lbl
								: R.string.info_datos_no_cargados_lbl)));
	}

	public void asignarEstadoBotonCargar(final boolean menuPrincipalEnabled,
			final boolean cargarDatosEnabled,
			final CharSequence lblInfoCargaDatosStr) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				btnMenuPrincipal.setEnabled(menuPrincipalEnabled);
				btnCargarDatos.setEnabled(cargarDatosEnabled);
				lblInfoCargaDatos.setText(lblInfoCargaDatosStr);
			}
		});
	}

	public void obtenerEstadoBotonDescargar() {
		btnDescargarHabilitado = Lectura.seRealizaronTodasLasLecturas();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				btnDescargarDatos.setEnabled(true);
			}
		});
	}

	/**
	 * Inicia la tarea asincrona de carga de datos
	 * 
	 * @param view
	 */
	public void btnCargarDatosClick(View view) {
		if (ClicksBotonesHelper.sePuedeClickearBoton()) {
			new AlertDialog.Builder(this)
					.setTitle(R.string.titulo_cargando_datos)
					.setIcon(R.drawable.import_from_server_d)
					.setMessage(R.string.msg_confirm_import_data)
					.setNegativeButton(R.string.btn_cancel, null)
					.setPositiveButton(R.string.btn_ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									iniciarImportacionDatos();
								}
							}).show();
		}
	}

	/**
	 * Inicia el servicio de importación de datos
	 */
	private void iniciarImportacionDatos() {
		startService(new Intent(this, ServicioImportacionDatos.class));
		new DataImportationReceiver(
				Arrays.asList((IDataImportationObserver) this), this)
				.startReceiving();
	}

	/**
	 * Inicia la tarea asincrona de descarga de datos, en caso de estar
	 * deshabilitado el boton lanza un mensaje de no completitud de lecturas.
	 * 
	 * @param view
	 */
	public void btnDescargarDatosClick(View view) {
		if (ClicksBotonesHelper.sePuedeClickearBoton()) {
			if (btnDescargarHabilitado) {
				new AlertDialog.Builder(this)
						.setTitle(R.string.titulo_exportar_datos)
						.setIcon(R.drawable.export_to_server_d)
						.setMessage(R.string.msg_confirmar_exportacion)
						.setNegativeButton(R.string.btn_cancel, null)
						.setPositiveButton(R.string.btn_ok,
								new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										iniciarExportacionDatos();
									}
								}).show();
			} else
				mostrarMensajeUsuario(R.string.descarga_inhabilitada);
		}
	}

	/**
	 * Inicia el servicio de exportación de datos
	 */
	private void iniciarExportacionDatos() {
		startService(new Intent(this, ServicioExportacionDatos.class));
		new DataExportationReceiver(
				Arrays.asList((IDataExportationObserver) this), this)
				.startReceiving();
	}

	/**
	 * Muestra un mensaje en un Toast
	 * 
	 * @param msgStrId
	 *            id de la cadena del mensaje
	 */
	public void mostrarMensajeUsuario(final int msgStrId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(Inicio.this, msgStrId, Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	// #region Observer methods

	@Override
	public void showImportationWaiting() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressDialog = new ProgressDialogPro(Inicio.this,
						R.style.AppStyle_Dialog_FlavoredMaterialLight);
				progressDialog.setCancelable(false);
				progressDialog.setIcon(R.drawable.import_from_server_d);
				progressDialog
						.setMessage(getText(R.string.msg_inicializando_importacion));
				progressDialog.setTitle(R.string.titulo_cargando_datos);
				progressDialog.show();
			}
		});
	}

	@Override
	public void updateImportationWaiting(final int msgStrId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (progressDialog != null)
					progressDialog.setMessage(getResources()
							.getString(msgStrId));
			}
		});
	}

	@Override
	public void hideWaiting() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (progressDialog != null) {
					obtenerEstadoBotonCargar();
					progressDialog.dismiss();
				}
			}
		});
	}

	@Override
	public void showErrors(final int titleStrId, final int iconDrawableId,
			final List<Exception> errors) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (errors.size() > 0) {
					new AlertDialog.Builder(Inicio.this)
							.setTitle(titleStrId)
							.setIcon(iconDrawableId)
							.setMessage(
									MessageListFormatter
											.fotmatHTMLFromErrors(errors))
							.setPositiveButton(R.string.btn_ok, null).show();
				}
			}
		});
	}

	@Override
	public void notifySuccessfulImportation() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mostrarMensajeUsuario(R.string.datos_cargados_exito);
			}
		});
		obtenerEstadoBotonCargar();
		asignarLabelDeRutas();
	}

	@Override
	public void showExportationWaiting() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressDialog = new ProgressDialogPro(Inicio.this,
						R.style.AppStyle_Dialog_FlavoredMaterialLight);
				progressDialog.setMessage(getResources().getText(
						R.string.msg_inicializando_exportacion));
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(true);
				progressDialog.setIcon(R.drawable.export_to_server_d);
				progressDialog.setTitle(R.string.titulo_exportar_datos);
				progressDialog
						.setProgressStyle(ProgressDialogPro.STYLE_HORIZONTAL);
				progressDialog.setCanceledOnTouchOutside(false);
				progressDialog.show();
			}
		});
	}

	@Override
	public void updateExportationWaiting(final int strId, final int totalData) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (progressDialog != null) {
					progressDialog.setIndeterminate(false);
					progressDialog.setMax(totalData);
					progressDialog.setMessage(getResources().getString(strId));
				}
			}
		});
	}

	@Override
	public void updateExportationWaiting(final int strId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (progressDialog != null) {
					progressDialog.setMax(0);
					progressDialog.setIndeterminate(true);
					progressDialog.setMessage(getResources().getString(strId));
				}
			}
		});
	}

	@Override
	public void updateExportationProgress(final int dataCount, int totalData) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (progressDialog != null)
					progressDialog.setProgress(dataCount);
			}
		});
	}

	@Override
	public void notifySuccessfulExportation() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mostrarMensajeUsuario(R.string.msg_exportacion_exitosa);
			}
		});
		onBackPressed();
	}

	// #endregion

	// #region Eliminacion Datos
	/**
	 * Es la tarea asincrona encargada de eliminar los datos de la base de datos
	 * SQLite del telefono y actualizar el estado de las rutas en la asignacion
	 * de rutas de oracle en MOVILES.USUARIO_ASIGNACION
	 * 
	 * @author drodriguez
	 *
	 */
	private class EliminacionDeDatos extends
			AsyncTask<Void, Void, ResultadoVoid> {
		private Context context;

		public EliminacionDeDatos(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialogPro(Inicio.this,
					R.style.AppStyle_Dialog_FlavoredMaterialLight);
			progressDialog.setCancelable(false);
			progressDialog.setIcon(R.drawable.eliminar_todos_los_datos_d);
			progressDialog
					.setMessage(getText(R.string.msg_restoring_route_assignments));
			progressDialog.setTitle(R.string.titulo_eliminar_todos_los_datos);
			progressDialog.show();
		}

		@Override
		protected ResultadoVoid doInBackground(Void... voids) {
			List<AsignacionRuta> listaRutas = AsignacionRuta
					.obtenerRutasImportadas();
			ResultadoVoid result = null;
			ResultadoTipado<ConectorBDOracle> conectResult = ConectorBDOracle
					.crear(context, true);
			result = conectResult;
			if (!result.tieneErrores()) {
				result = new AsignacionRutaManager()
						.restaurarAsignacionDeRutas(
								conectResult.getResultado(), listaRutas);
			}
			if (!result.tieneErrores()) {
				updateImportationWaiting(R.string.msg_wiping_all_data);
				new EliminacionDatosManager().eliminarTodosLosDatos();
			}
			return result;
		}

		@Override
		protected void onPostExecute(ResultadoVoid result) {
			hideWaiting();
			showErrors(R.string.title_wipe_all_data_errors,
					R.drawable.eliminar_todos_los_datos_d, result.getErrores());
			if (!result.tieneErrores()) {
				mostrarMensajeUsuario(R.string.msg_all_data_wiped_successfully);
				onBackPressed();
			}
		}
	}
	// #endregion

}
