package com.elfec.lecturas.controlador;

import java.util.Date;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alertdialogpro.ProgressDialogPro;
import com.elfec.lecturas.helpers.ui.ClicksBotonesHelper;
import com.elfec.lecturas.logica_negocio.SincronizadorServidor;
import com.elfec.lecturas.modelo.SesionUsuario;
import com.elfec.lecturas.modelo.Usuario;
import com.elfec.lecturas.modelo.validaciones.IValidacionUsuario;
import com.elfec.lecturas.modelo.validaciones.sincronizacion.IEstadoSincronizacion;
import com.elfec.lecturas.settings.AdministradorSeguridad;
import com.elfec.lecturas.settings.VariablesDeSesion;
import com.lecturas.elfec.R;

/**
 * Controlador de la actividad de Login, esta es la primera pantalla del sistema
 * 
 * @author drodriguez
 *
 */
public class Login extends AppCompatActivity {

	private TextView txtUsuario;
	private TextView txtPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		getSupportActionBar().setTitle(R.string.titulo_login);
		txtUsuario = (TextView) findViewById(R.id.txt_usuario);
		txtUsuario.setText("ecampos");
		txtPassword = (TextView) findViewById(R.id.txt_password);
		txtPassword.setText("123");
		setVersionTitle();
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Asigna la versión al titulo
	 */
	private void setVersionTitle() {
		try {
			PackageInfo pinfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			((TextView) findViewById(R.id.title_app_full_name)).setText(String
					.format(getString(R.string.app_full_name),
							pinfo.versionName));
		} catch (NameNotFoundException e) {
			((TextView) findViewById(R.id.title_app_full_name)).setText(String
					.format(getString(R.string.app_full_name), "desconocida"));
		}
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
		new AlertDialog.Builder(this)
				.setMessage(resultadoValidacion.obtenerMensaje())
				.setTitle(R.string.titulo_mensajes_error)
				.setPositiveButton(R.string.btn_ok, null).show();
		txtPassword.setText("");
	}

	/**
	 * Abre un cuadro de dialogo para mostrar el error de sincronizacion
	 * 
	 * @param estadoSinc
	 */
	public void mostrarDialogoErrorSincronizacion(
			final IEstadoSincronizacion estadoSinc) {
		new AlertDialog.Builder(this).setTitle(R.string.titulo_mensajes_error)
				.setMessage(estadoSinc.obtenerMensaje())
				.setPositiveButton(R.string.btn_ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						txtPassword.setText("");
						if (estadoSinc.obtenerCodigo().equals("VS-003")) {
							startActivity(new Intent(
									android.provider.Settings.ACTION_DATE_SETTINGS));
						}
					}
				}).show();
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
	private ProgressDialogPro progressDialog;

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
				progressDialog = new ProgressDialogPro(Login.this,
						R.style.AppStyle_Dialog_FlavoredMaterialLight);
				progressDialog.setCancelable(false);
				progressDialog.setIcon(R.drawable.login_user);
				progressDialog.setMessage(getResources().getText(
						R.string.login_usuario_msg));
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
