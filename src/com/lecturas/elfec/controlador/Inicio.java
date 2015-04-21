package com.lecturas.elfec.controlador;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lecturas.elfec.R;
import com.lecturas.elfec.controlador.accionesycustomizaciones.CustomDialog;
import com.lecturas.elfec.controlador.dialogos.DialogoSeleccionImpresora;
import com.lecturas.elfec.helpers.ConectorBDOracle;
import com.lecturas.elfec.helpers.GestionadorBDSQLite;
import com.lecturas.elfec.helpers.VariablesDeSesion;
import com.lecturas.elfec.modelo.AsignacionRuta;
import com.lecturas.elfec.modelo.Lectura;
import com.lecturas.elfec.modelo.SesionUsuario;
import com.lecturas.elfec.modelo.seguridad.AdministradorSeguridad;
import com.lecturas.elfec.modelo.seguridad.Permisos;

/**
 * Controlador de la actividad de Inicio, esta es la segunda pantalla a la que se accede despues del Login
 * @author drodriguez
 *
 */
public class Inicio extends Activity {

	private TextView lblNomUsuario;
	private TextView lblFecha;
	private TextView lblNumIMEI;
	private TextView lblNumRuta;
	private TextView lblInfoCargaDatos;
	private Button btnMenuPrincipal;
	private Button btnCargarDatos;
	private Button btnDescargarDatos;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_inicio);
		asignarTextos();
		lblNumRuta = (TextView) findViewById(R.id.lbl_numero_ruta);
		btnMenuPrincipal=(Button)findViewById(R.id.btn_menu_principal);
		btnCargarDatos=(Button)findViewById(R.id.btn_cargar_datos);
		btnDescargarDatos=(Button)findViewById(R.id.btn_descargar_datos);
		lblInfoCargaDatos=(TextView)findViewById(R.id.lbl_info_carga_datos);
		asignarEstadoBotonCargar();
		asignarEstadoBotonDescargar();
	}


	/**
	 * Asigna los datos a los labels de nombre de usuario, fecha e IMEI
	 */
	public void asignarTextos() {
		lblNomUsuario = (TextView) findViewById(R.id.lbl_nom_usuario);
		String usuario = VariablesDeSesion.getUsuarioLogeado();
		if(usuario!=null && !usuario.isEmpty())
			lblNomUsuario.setText(usuario);
		lblFecha = (TextView) findViewById(R.id.lbl_fecha);
		Date hoy = new Date();
		String fechaHoy=new SimpleDateFormat("dd/MMM/yyyy",Locale.getDefault()).format(hoy);
		lblFecha.setText(fechaHoy);
		lblNumIMEI = (TextView) findViewById(R.id.lbl_num_imei);
		String imei = VariablesDeSesion.getImeiCelular();
		if(imei!=null && !imei.isEmpty())
			lblNumIMEI.setText(imei);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.inicio, menu);
		(menu.findItem(R.id.menu_item_configurar))
		.setVisible(AdministradorSeguridad
				.obtenerAdministradorSeguridad(VariablesDeSesion.getPerfilUsuario())
				.tienePermiso(Permisos.CONFIGURAR_SERVIDOR));
		(menu.findItem(R.id.menu_item_forzar_descarga))
		.setVisible(AdministradorSeguridad
				.obtenerAdministradorSeguridad(VariablesDeSesion.getPerfilUsuario())
				.tienePermiso(Permisos.FORZAR_DESCARGA));
		(menu.findItem(R.id.menu_item_eliminar_datos))
		.setVisible(AdministradorSeguridad
				.obtenerAdministradorSeguridad(VariablesDeSesion.getPerfilUsuario())
				.tienePermiso(Permisos.ELIMINAR_DATOS));
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int idItem = item.getItemId();
		switch(idItem)
		{
			case (R.id.menu_item_selec_impresora):		
			{
				btnMenuSelecImpresoraClick(item);
				return true;
			}
			case(R.id.menu_item_configurar):		
			{
				btnMenuConfigurarClick(item);
				return true;
			}
			case(R.id.menu_item_forzar_descarga):		
			{
				forzarDescarga();
				return true;
			}
			case(R.id.menu_item_eliminar_datos):		
			{
				eliminarDatos();
				return true;
			}
			default:
			{
				return true;
			}
		}
	}
	
	/**
	 * Elimina todos los datos diarios y mensuales, es invocado cuando se presiona la opcion del menu de eliminar datos
	 * y se confirma. Esta opción deberia estar disponible solo para administradores.
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
				EliminacionDeDatos eliminacionDeDatos = new EliminacionDeDatos(Inicio.this);
				eliminacionDeDatos.execute((Void[])null);
			}
		});
		dialog.setNegativeButton(null);
		dialog.show();
	}


	/**
	 * Fuerza la descarga de datos, aun cuando no se hayan terminado de realizar todas las lecturas. 
	 * Esta opción deberia estar disponible solo para administradores.
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
				DescargaDeDatos descargaDeDatos = new DescargaDeDatos(Inicio.this);
				descargaDeDatos.execute((Void[])null);
			}
		});
		dialog.setNegativeButton(null);
		dialog.show();
	}

	/**
	 * Se utiliza para que cada vez que se vaya a esta actividad, se actualize el estado de los botones.
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		asignarEstadoBotonDescargar();
		asignarTextos();
	}
	
	@Override
	public void onBackPressed() {
		SesionUsuario.cerrarSesionUsuario(VariablesDeSesion.getUsuarioLogeado());
	    finish();//go back to the previous Activity
	    overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);  
	}
	
	/**
	 * Vuelve a la actividad de Login
	 * @param view
	 */
	public void btnSalirClick(View view)
	{
		onBackPressed();
	}
	
	/**
	 * Va a la actividad del MenuPrinipal
	 * @param view
	 */
	public void btnMenuPrincipalClick(View view)
	{
		Intent intent = new Intent(this, MenuPrincipal.class);
	    startActivity(intent);
	    overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
	}
	
	/**
	 * Inicia la actividad de Configurar el servidor. 
	 * Esta opción deberia estar disponible solo para administradores.
	 * @param menuitem
	 */
	public void btnMenuConfigurarClick(MenuItem menuitem)
	{
		Intent intent = new Intent(this, Configurar.class);
	    startActivity(intent);
	    overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
	}
	
	/**
	 * Abre el cuadro de dialogo para selección de impresora
	 * @param menuitem
	 */
	public void btnMenuSelecImpresoraClick(MenuItem menuitem)
	{
		DialogoSeleccionImpresora dialogo = new DialogoSeleccionImpresora(this);
		dialogo.show();
	}
	
	/**
	 * Llena el label de asignacion de rutas segun las rutas que se hayan asignado al usuario
	 */
	public void asignarLabelDeRutas()
	{
		List<AsignacionRuta> listaRutas = AsignacionRuta.obtenerRutasDeUsuario(VariablesDeSesion.getUsuarioLogeado());
		if(listaRutas.size()==0)
		{
			lblNumRuta.setText(R.string.ruta_info_lbl);
		}
		else
		{
			StringBuilder strBuild = new StringBuilder("");
			int tam = listaRutas.size();
			for (int i = 0; i < tam; i++) 
			{
				strBuild.append(listaRutas.get(i).Ruta);
				if(i<tam-1)
					strBuild.append(", ");
			}
			lblNumRuta.setText(strBuild.toString());
		}
	}
	
	/**
	 * Habilita o deshabilita el boton de Cargar datos, verificando si es que ya fueron cargados todos los datos
	 */
	public void asignarEstadoBotonCargar()
	{
		if(!(GestionadorBDSQLite.datosDiariosFueronCargados()))
		{
			btnMenuPrincipal.setEnabled(false);
			btnCargarDatos.setEnabled(true);
			lblInfoCargaDatos.setText(getResources().getString(R.string.info_datos_no_cargados_lbl));
		}
		else
		{
			btnMenuPrincipal.setEnabled(true);
			btnCargarDatos.setEnabled(false);
			lblInfoCargaDatos.setText(getResources().getString(R.string.info_datos_cargados_lbl));
		}
		asignarLabelDeRutas();
	}
	
	/**
	 * Habilita o deshabilita el boton de Descargar datos, verificando que se hayan realizado todas las lecturas
	 */
	private boolean btnDescargarHabilitado;
	public void asignarEstadoBotonDescargar()
	{
		btnDescargarHabilitado = Lectura.seRealizaronTodasLasLecturas();
		btnDescargarDatos.setBackgroundResource(btnDescargarHabilitado?
		R.drawable.elfectheme_btn_default_holo_light:
		R.drawable.elfectheme_btn_default_disabled_holo_light);
	}
	
	/**
	 * Inicia la tarea asincrona de carga de datos
	 * @param view
	 */
	public void btnCargarDatosClick(View view)
	{

		CargaDeDatos cargaDeDatos = new CargaDeDatos(this);
		cargaDeDatos.execute((Void[])null);
	}
	
	/**
	 * Inicia la tarea asincrona de descarga de datos, en caso de estar deshabilitado el boton
	 * lanza un mensaje de no completitud de lecturas.
	 * @param view
	 */
	public void btnDescargarDatosClick(View view)
	{
		if(btnDescargarHabilitado)
		{
			DescargaDeDatos descargaDeDatos = new DescargaDeDatos(this);
			descargaDeDatos.execute((Void[])null);
		}
		else
		{
			Toast.makeText(this, R.string.descarga_inhabilitada, Toast.LENGTH_SHORT).show();
		}
		asignarLabelDeRutas();
	}
	/**
	 * Es la tarea asincrona encargada de descargar los datos del telefono a la base de datos ERP_ELFEC,
	 * tamién elimina los datos una vez descargados, elimina datos diarios y verifica si el dia siguiente
	 * es cambio de mes para borrar los datos mensuales.
	 * @author drodriguez
	 *
	 */
	private class DescargaDeDatos extends AsyncTask<Void, Void, Boolean>
	{
		private CustomDialog progressDialog;
		private Context context;
		
		public DescargaDeDatos(Context context)
		{
			this.context=context;
		}
		
		@Override 
		protected void onPreExecute() 
		{ 
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
			if(resp)
			{
				GestionadorBDSQLite.eliminarTodosLosDatos();
			}
			return resp;
		}
		
		@Override 
		protected void 
		onPostExecute(Boolean result) 
		{ 
			if (progressDialog!=null) 
			{ 
				progressDialog.dismiss(); 
				if(result)
				{
					asignarEstadoBotonCargar();
					Toast.makeText(context, R.string.datos_descargados_exito, Toast.LENGTH_LONG).show();
					onBackPressed();
				}
				else
				{
					Toast.makeText(context, R.string.error_descarga_datos, Toast.LENGTH_LONG).show();
				}
			} 
		} 
		
	}
	/**
	 * Es la tarea asincrona encargada de cargar los datos de las bases de datos de oracle ERP_ELFEC y MOVILES.
	 * necesarios para el funcionamiento del sistema. Verifica si existen datos mensuales antes de descargarlos
	 * nuevamente.
	 * @author drodriguez
	 *
	 */
	private class CargaDeDatos extends AsyncTask<Void, Void, Boolean>
	{
		private CustomDialog progressDialog;
		private Context context;
		public CargaDeDatos(Context context)
		{
			this.context=context;
		}
			@Override 
			protected void onPreExecute() 
			{ 
				progressDialog = new CustomDialog(context);
				progressDialog.showProgressbar(true);
				progressDialog.setCancelable(false);
				progressDialog.setIcon(R.drawable.cargar_datos);
				progressDialog.setMessage(R.string.cargando_datos_msg);
				progressDialog.setTitle(R.string.titulo_cargando_datos);
				progressDialog.show();
			} 
			@Override 
			protected Boolean doInBackground(Void...voids) 
			{ 
				ConectorBDOracle conexion = new ConectorBDOracle(context, true);
				return conexion.importarDatosMensualesDeOracle()
						&& conexion.importarDatosDiariosDeOracle();
			} 
			@Override 
			protected void 
			onPostExecute(Boolean result) 
			{ 
				if (progressDialog!=null) 
				{ 
					asignarEstadoBotonCargar();
					progressDialog.dismiss(); 
					if(result)
					{
						Toast.makeText(context, R.string.datos_cargados_exito, Toast.LENGTH_LONG).show();
					}
					else
					{
						Toast.makeText(context, R.string.error_carga_datos, Toast.LENGTH_LONG).show();
					}
				} 
			} 
	}
	
	/**
	 * Es la tarea asincrona encargada de eliminar los datos de la base de datos SQLite del telefono y actualizar
	 * el estado de las rutas en la asignacion de rutas de oracle en MOVILES.USUARIO_ASIGNACION
	 * @author drodriguez
	 *
	 */
	private class EliminacionDeDatos extends AsyncTask<Void, Void, Boolean>
	{
		private CustomDialog progressDialog;
		private Context context;
		public EliminacionDeDatos(Context context)
		{
			this.context=context;
		}
			@Override 
			protected void onPreExecute() 
			{ 
				progressDialog = new CustomDialog(context);
				progressDialog.showProgressbar(true);
				progressDialog.setCancelable(false);
				progressDialog.setIcon(R.drawable.borrar_datos);
				progressDialog.setMessage(R.string.eliminando_datos_msg);
				progressDialog.setTitle(R.string.titulo_eliminar_datos);
				progressDialog.show();
			} 
			@Override 
			protected Boolean doInBackground(Void...voids) 
			{ 
				try
				{
					ConectorBDOracle conexion = new ConectorBDOracle(context, true);
					List<AsignacionRuta> listaRutas = AsignacionRuta.obtenerRutasDeUsuario(VariablesDeSesion.getUsuarioLogeado());
					for(AsignacionRuta asignRuta : listaRutas)
					{
						asignRuta.Estado--;//si es 2(ruta cargada a movil) se vuelve 1(ruta pendiente) y si es 7 (relectura cargada a movil) se vuelve 6(relectura asignada)
					}
					conexion.actualizarEstadoRutas(listaRutas,false);
					GestionadorBDSQLite.eliminarTodosLosDatos();
					return true;
				}
				catch(Exception e)
				{
					Log.e("Eliminación de datos", e.getMessage());
					return false;
				}
			} 
			@Override 
			protected void 
			onPostExecute(Boolean result) 
			{ 
				if (progressDialog!=null) 
				{ 
					asignarEstadoBotonCargar();
					progressDialog.dismiss(); 
					if(result)
					{
						Toast.makeText(Inicio.this, R.string.datos_eliminados_exito, Toast.LENGTH_SHORT).show();
						onBackPressed();
					}
					else
					{
						Toast.makeText(Inicio.this, R.string.datos_eliminados_error, Toast.LENGTH_SHORT).show();
					}
				} 
			} 
	}
}
