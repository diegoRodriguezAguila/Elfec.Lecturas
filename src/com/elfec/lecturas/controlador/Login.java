package com.elfec.lecturas.controlador;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.elfec.lecturas.controlador.accionesycustomizaciones.CustomDialog;
import com.elfec.lecturas.helpers.SincronizadorServidor;
import com.elfec.lecturas.helpers.VariablesDeSesion;
import com.elfec.lecturas.helpers.ui.ClicksBotonesHelper;
import com.elfec.lecturas.helpers.validacionessincronizacion.IEstadoSincronizacion;
import com.elfec.lecturas.modelo.SesionUsuario;
import com.elfec.lecturas.modelo.Usuario;
import com.elfec.lecturas.modelo.validaciones.IValidacionUsuario;
import com.elfec.lecturas.settings.AdministradorSeguridad;
import com.lecturas.elfec.R;

/**
 * Controlador de la actividad de Login, esta es la primera pantalla del sistema
 * 
 * @author drodriguez
 *
 */
public class Login extends Activity {

	private TextView txtUsuario;
	private TextView txtPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		txtUsuario = (TextView) findViewById(R.id.txt_usuario);
		txtUsuario.setText("ecampos");
		txtPassword = (TextView) findViewById(R.id.txt_password);
		txtPassword.setText("123");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Se invoca al apretar el boton de ingresar, se encarga de llamar a las
	 * rutinas de validación de sincronización y de usuario.
	 * 
	 * @param view
	 */
	public void btnIngresarClick(View view) {
		if (ClicksBotonesHelper.sePuedeClickearBoton()) {
			String usuario = txtUsuario.getText().toString();
			String password = txtPassword.getText().toString();
			TelephonyManager telephonyManager = (TelephonyManager) this
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = telephonyManager.getDeviceId();
			VariablesDeSesion.setUsuarioLogeado(usuario);
			VariablesDeSesion.setPasswordUsuario(password);
			VariablesDeSesion.setImeiCelular(imei);
			AdministradorSeguridad.resetearAdministradorDeSeguridad();
			validarYEliminarCuadroTarifario();
		}
	}

	/**
	 * Valida la sincronizacion y al usuario para el ingreso al sistema
	 */
	public void validarUsuarioYSincronizacion() {
		Usuario usuarioEncontrado = Usuario.obtenerUsuario(VariablesDeSesion
				.getUsuarioLogeado());
		if (usuarioEncontrado == null
				|| usuarioEncontrado.FechaSincronizacion == null) {
			ValidarSincronizacion validarSincronizacion = new ValidarSincronizacion();
			validarSincronizacion.execute((Void[]) null);
		} else {
			ValidarUsuario validarUsuario = new ValidarUsuario(null);
			validarUsuario.execute((Void[]) null);
		}
	}

	/**
	 * Valida si existen datos del anterior cuadro tarifario y se eliminan y se
	 * informa al usuario de ello en caso de ser necesario
	 */
	private void validarYEliminarCuadroTarifario() {
		/*
		 * if(GestionadorBDSQLite.datosDiariosFueronCargados() &&
		 * !GestionadorBDSQLite.idCuadroTarifarioEsActual()) { final
		 * CustomDialog dialog = new CustomDialog(this);
		 * dialog.setMessage(R.string.cuadro_tarif_msg);
		 * dialog.setTitle(R.string.titulo_cuadro_tarif);
		 * dialog.setCancelable(false); dialog.setIcon(R.drawable.warning);
		 * dialog.setPositiveButton(R.string.btn_ok, new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * GestionadorBDSQLite.eliminarTodosLosDatos(); dialog.dismiss();
		 * validarUsuarioYSincronizacion(); } }); dialog.show( ); } else {
		 */
		validarUsuarioYSincronizacion();
		// }
	}

	/**
	 * Abre un cuadro de dialogo para mostrar el error de validacion de usuario
	 */
	public void mostrarDialogoErrorValidacionUsuario(
			IValidacionUsuario resultadoValidacion) {
		CustomDialog dialog = new CustomDialog(this);
		dialog.setMessage(resultadoValidacion.obtenerMensaje());
		dialog.setTitle(R.string.titulo_mensajes_error);
		dialog.setCancelable(false);
		dialog.setIcon(R.drawable.error);
		dialog.setPositiveButton(null);
		dialog.show();
		txtPassword.setText("");
	}

	/**
	 * Abre un cuadro de dialogo para mostrar el error de sincronizacion
	 * 
	 * @param estadoSinc
	 */
	public void mostrarDialogoErrorSincronizacion(
			final IEstadoSincronizacion estadoSinc) {
		final CustomDialog dialog = new CustomDialog(this);
		dialog.setTitle(R.string.titulo_mensajes_error);
		dialog.setMessage(estadoSinc.obtenerMensaje());
		dialog.setIcon(R.drawable.error);
		dialog.setCancelable(false);
		dialog.setPositiveButton(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				txtPassword.setText("");
				dialog.dismiss();
				if (estadoSinc.obtenerCodigo().equals("VS-003")) {
					startActivity(new Intent(
							android.provider.Settings.ACTION_DATE_SETTINGS));
				}
			}
		});
		dialog.show();
	}

	/**
	 * Resetea los cuadros de texto del usuario y password y lleva a la
	 * actividad de Inicio debe llamarse solo cuando un usuario se ha
	 * autenticado correctamente
	 */
	private void procederConIngresoAlSistema() {
		txtUsuario.setText("");
		txtPassword.setText("");
		SesionUsuario.iniciarSesionUsuario(
				VariablesDeSesion.getUsuarioLogeado(),
				VariablesDeSesion.getPasswordUsuario(),
				VariablesDeSesion.getImeiCelular(),
				VariablesDeSesion.getPerfilUsuario());
		Intent intent = new Intent(this, Inicio.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
	}

	/**
	 * Dialogo de progreso que se muestra mientras se autentica al usuario
	 * conectando con la base de datos oracle no se muestra cuando ya se tiene
	 * al usuario en el teléfono
	 */
	private CustomDialog progressDialog;

	/**
	 * Tarea asincrona para la validacion de la sincronizacion del dispositivo
	 * con el servidor
	 * 
	 * @author drodriguez
	 *
	 */
	private class ValidarSincronizacion extends
			AsyncTask<Void, IEstadoSincronizacion, IEstadoSincronizacion> {

		@Override
		protected void onPreExecute() {
			if (progressDialog == null) {
				progressDialog = new CustomDialog(Login.this);
				progressDialog.showProgressbar(true);
				progressDialog.setCancelable(false);
				progressDialog.setIcon(R.drawable.login_usuario);
				progressDialog.setMessage(R.string.login_usuario_msg);
				progressDialog.setTitle(R.string.titulo_login_usuario);
				progressDialog.show();
			}
		}

		@Override
		protected IEstadoSincronizacion doInBackground(Void... arg0) {
			return SincronizadorServidor
					.verificarSincronizacionFechaYHoraConServidor(Login.this);
		}

		@Override
		protected void onPostExecute(IEstadoSincronizacion estadoSinc) {
			if (estadoSinc.esError()) {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				mostrarDialogoErrorSincronizacion(estadoSinc);
			} else {
				Date fechaSinc = estadoSinc.obtenerFechaSinc();
				ValidarUsuario validarUsuario = new ValidarUsuario(fechaSinc);
				validarUsuario.execute((Void[]) null);
			}
		}

	}

	/**
	 * Tarea asincrona para la validacion de un usuario
	 * 
	 * @author drodriguez
	 *
	 */
	private class ValidarUsuario extends
			AsyncTask<Void, IValidacionUsuario, IValidacionUsuario> {
		private Date fechaSinc;

		public ValidarUsuario(Date fechaSinc) {
			this.fechaSinc = fechaSinc;
		}

		@Override
		protected IValidacionUsuario doInBackground(Void... params) {
			return Usuario.validar(VariablesDeSesion.getUsuarioLogeado(),
					VariablesDeSesion.getPasswordUsuario(),
					VariablesDeSesion.getImeiCelular(), fechaSinc, Login.this);
		}

		@Override
		protected void onPostExecute(IValidacionUsuario resultadoValidacion) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (resultadoValidacion.esError()) {
				mostrarDialogoErrorValidacionUsuario(resultadoValidacion);
			} else {
				procederConIngresoAlSistema();
			}
		}

	}

	@Override
	public void onBackPressed() {
		this.finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_item_configurar) {
			Intent intent = new Intent(this, Configurar.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
		}
		return true;
	}

}
