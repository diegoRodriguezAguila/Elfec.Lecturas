package com.elfec.lecturas.controlador;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.elfec.lecturas.acceso_remoto_datos.ConectorBDOracle;
import com.elfec.lecturas.controlador.accionesycustomizaciones.CustomDialog;
import com.elfec.lecturas.controlador.dialogos.DialogoSeleccionImpresora;
import com.elfec.lecturas.controlador.observers.IDataImportationObserver;
import com.elfec.lecturas.helpers.GestionadorBDSQLite;
import com.elfec.lecturas.helpers.ui.ClicksBotonesHelper;
import com.elfec.lecturas.helpers.utils.text.MessageListFormatter;
import com.elfec.lecturas.logica_negocio.AsignacionRutaManager;
import com.elfec.lecturas.modelo.AsignacionRuta;
import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.SesionUsuario;
import com.elfec.lecturas.modelo.seguridad.Permisos;
import com.elfec.lecturas.servicios.ServicioImportacionDatos;
import com.elfec.lecturas.servicios.receivers.DataImportationReceiver;
import com.elfec.lecturas.settings.AdministradorSeguridad;
import com.elfec.lecturas.settings.VariablesDeSesion;
import com.lecturas.elfec.R;

/**
 * Controlador de la actividad de Inicio, esta es la segunda pantalla a la que
 * se accede despues del Login
 * 
 * @author drodriguez
 *
 */
public class Inicio extends Activity implements IDataImportationObserver {

	private TextView lblNomUsuario;
	private TextView lblFecha;
	private TextView lblNumIMEI;
	private TextView lblNumRuta;
	private TextView lblInfoCargaDatos;
	private Button btnMenuPrincipal;
	private Button btnCargarDatos;
	private Button btnDescargarDatos;

	private CustomDialog progressDialog;

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
		final CustomDialog dialog = new CustomDialog(this);
		dialog.setTitle(R.string.titulo_mensajes_advertencia);
		dialog.setMessage(R.string.eliminar_datos_msg);
		dialog.setIcon(R.drawable.warning);
		dialog.setPositiveButton(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				EliminacionDeDatos eliminacionDeDatos = new EliminacionDeDatos(
						Inicio.this);
				eliminacionDeDatos.execute((Void[]) null);
			}
		});
		dialog.setNegativeButton(null);
		dialog.show();
	}

	/**
	 * Fuerza la descarga de datos, aun cuando no se hayan terminado de realizar
	 * todas las lecturas. Esta opción deberia estar disponible solo para
	 * administradores.
	 */
	private void forzarDescarga() {
		final CustomDialog dialog = new CustomDialog(this);
		dialog.setTitle(R.string.titulo_mensajes_advertencia);
		dialog.setMessage(R.string.forzar_descarga_msg);
		dialog.setIcon(R.drawable.warning);
		dialog.setPositiveButton(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				DescargaDeDatos descargaDeDatos = new DescargaDeDatos(
						Inicio.this);
				descargaDeDatos.execute((Void[]) null);
			}
		});
		dialog.setNegativeButton(null);
		dialog.show();
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
		boolean datosDiariosCargados = GestionadorBDSQLite
				.datosDiariosFueronCargados();
		asignarEstadoBotonCargar(
				datosDiariosCargados,
				!datosDiariosCargados,
				(getResources()
						.getString(datosDiariosCargados ? R.string.info_datos_cargados_lbl
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

	/**
	 * Habilita o deshabilita el boton de Descargar datos, verificando que se
	 * hayan realizado todas las lecturas
	 */
	private boolean btnDescargarHabilitado;

	public void obtenerEstadoBotonDescargar() {
		btnDescargarHabilitado = Lectura.seRealizaronTodasLasLecturas();
		asignarEstadoBotonDescargar();
	}

	public void asignarEstadoBotonDescargar() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				btnDescargarDatos.setEnabled(true);
				btnDescargarDatos
						.setBackgroundResource(btnDescargarHabilitado ? R.drawable.elfectheme_btn_default_holo_light
								: R.drawable.elfectheme_btn_default_disabled_holo_light);
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
	public void iniciarImportacionDatos() {
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
				DescargaDeDatos descargaDeDatos = new DescargaDeDatos(this);
				descargaDeDatos.execute((Void[]) null);
			} else {
				mostrarMensajeUsuario(R.string.descarga_inhabilitada);
			}
			asignarLabelDeRutas();
		}
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

	/**
	 * Es la tarea asincrona encargada de descargar los datos del telefono a la
	 * base de datos ERP_ELFEC, tamién elimina los datos una vez descargados,
	 * elimina datos diarios y verifica si el dia siguiente es cambio de mes
	 * para borrar los datos mensuales.
	 * 
	 * @author drodriguez
	 *
	 */
	private class DescargaDeDatos extends AsyncTask<Void, Void, Boolean> {
		private CustomDialog progressDialog;
		private Context context;

		public DescargaDeDatos(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = new CustomDialog(context);
			progressDialog.showProgressbar(true);
			progressDialog.setCancelable(false);
			progressDialog.setIcon(R.drawable.descargar_datos);
			progressDialog.setMessage(R.string.descargando_datos_msg);
			progressDialog.setTitle(R.string.titulo_descargando_datos);
			progressDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			ConectorBDOracle conexion = new ConectorBDOracle(context, true);
			Boolean resp = conexion.exportarInformacionAlServidor();
			if (resp) {
				GestionadorBDSQLite.eliminarTodosLosDatos();
			}
			return resp;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				if (result) {
					obtenerEstadoBotonCargar();
					mostrarMensajeUsuario(R.string.datos_descargados_exito);
					onBackPressed();
				} else {
					mostrarMensajeUsuario(R.string.error_descarga_datos);
				}
			}
		}

	}

	/**
	 * Es la tarea asincrona encargada de eliminar los datos de la base de datos
	 * SQLite del telefono y actualizar el estado de las rutas en la asignacion
	 * de rutas de oracle en MOVILES.USUARIO_ASIGNACION
	 * 
	 * @author drodriguez
	 *
	 */
	private class EliminacionDeDatos extends AsyncTask<Void, Void, Boolean> {
		private CustomDialog progressDialog;
		private Context context;

		public EliminacionDeDatos(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = new CustomDialog(context);
			progressDialog.showProgressbar(true);
			progressDialog.setCancelable(false);
			progressDialog.setIcon(R.drawable.borrar_datos);
			progressDialog.setMessage(R.string.eliminando_datos_msg);
			progressDialog.setTitle(R.string.titulo_eliminar_datos);
			progressDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... voids) {
			try {
				ConectorBDOracle conexion = new ConectorBDOracle(context, true);
				List<AsignacionRuta> listaRutas = AsignacionRuta
						.obtenerRutasDeUsuario(VariablesDeSesion
								.getUsuarioLogeado());
				new AsignacionRutaManager().restaurarAsignacionDeRutas(
						conexion, listaRutas);
				GestionadorBDSQLite.eliminarTodosLosDatos();
				return true;
			} catch (Exception e) {
				Log.e("Eliminación de datos", e.getMessage());
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (progressDialog != null) {
				obtenerEstadoBotonCargar();
				progressDialog.dismiss();
				if (result) {
					mostrarMensajeUsuario(R.string.datos_eliminados_exito);
					onBackPressed();
				} else {
					mostrarMensajeUsuario(R.string.datos_eliminados_error);
				}
			}
		}
	}

	// #region Observer methods

	@Override
	public void showImportationWaiting() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressDialog = new CustomDialog(Inicio.this);
				progressDialog.showProgressbar(true);
				progressDialog.setCancelable(false);
				progressDialog.setIcon(R.drawable.cargar_datos);
				progressDialog
						.setMessage(R.string.msg_inicializando_importacion);
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
	}
	// #endregion
}
