package com.lecturas.elfec.controlador;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.lecturas.elfec.R;
import com.lecturas.elfec.controlador.accionesycustomizaciones.CustomDialog;
import com.lecturas.elfec.controlador.dialogos.DialogoSeleccionImpresora;
import com.lecturas.elfec.helpers.ManejadorImpresora;
import com.lecturas.elfec.helpers.excepciones.ImpresoraPredefinidaNoAsignadaExcepcion;
import com.lecturas.elfec.modelo.detallesresumenes.DetalleImpedidas;
import com.lecturas.elfec.modelo.detallesresumenes.DetalleLecturas;
import com.lecturas.elfec.modelo.detallesresumenes.DetalleLecturasEntreLineas;
import com.lecturas.elfec.modelo.detallesresumenes.DetalleOrdenativos;
import com.lecturas.elfec.modelo.detallesresumenes.DetalleResumenGenerico;

public class MenuPrincipal extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_principal);
		try 
		{
			Class.forName("com.lecturas.elfec.helpers.VariablesDeEntorno");
		} 
		catch (Exception e) 
		{
			mostrarDialogoErrorVariables();
		}
		catch (ExceptionInInitializerError e) 
		{
			mostrarDialogoErrorVariables();
		}
	}
	
	/**
	 * Muestra el dialogo de error en caso de que las variables de entorno de las tablas parametrizables
	 * no se encuentren correctas en el dispositivo
	 */
	private void mostrarDialogoErrorVariables()
	{
		final CustomDialog dialog = new CustomDialog(this);
		dialog.setMessage(R.string.no_variables_msg);
		dialog.setTitle(R.string.titulo_no_variables);
		dialog.setIcon(R.drawable.error);
		dialog.setCancelable(false);
		dialog.setPositiveButton(new View.OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				onBackPressed();
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_principal, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
	    finish();//go back to the previous Activity
	    overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);  
	}
	
	public void btnSalirClick(View view)
	{
		Intent intent = new Intent(this, Inicio.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    startActivity(intent);
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out); 
	}
	
	/**
	 * Inicia la actividad de tomar lecturas
	 */
	public void btnTomarLecturasClick(View view)
	{
		Intent intent = new Intent(this, TomarLectura.class);
	    startActivity(intent);
	    overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
	}
	
	/**
	 * Inicia la actividad de resumen de lecturas
	 * @param view
	 */
	public void btnResumenLecturasClick(View view)
	{
		Intent intent = new Intent(this, ResumenLecturas.class);
	    startActivity(intent);
	    overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
	}
	
	/**
	 * Muestra un dialogo para confirmar la impresion de un resumen. Utiliza el titulo, mensaje y resumen pasados en
	 * los parametros
	 * @param idStringMensaje
	 * @param idStringTitulo
	 * @param resumenAImprimir
	 */
	public void mostrarDialogoImprimirResumen(int idStringMensaje, int idStringTitulo, final DetalleResumenGenerico resumenAImprimir)
	{
		final CustomDialog dialog = new CustomDialog(this);
		dialog.setMessage(idStringMensaje);
		dialog.setIcon(getResources().getDrawable(R.drawable.imprimir));
		dialog.setTitle(idStringTitulo);
		dialog.setPositiveButton(R.string.btn_ok, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(!ManejadorImpresora.impresoraPredefinidaFueAsignada())
				{
					mostrarDialogoSeleccionarImpresora(resumenAImprimir);
				}
				else
				{
					iniciarImpresionResumen(resumenAImprimir);
				}
               }
           });
		dialog.setNegativeButton(null);
		dialog.show();
	}
	
	/**
	 * Muestra el dialogo para seleccionar una impresora antes de imprimir
	 */
	public void mostrarDialogoSeleccionarImpresora(final DetalleResumenGenerico resumenAImprimir)
	{
		DialogoSeleccionImpresora dialogo = new DialogoSeleccionImpresora(MenuPrincipal.this);
		dialogo.setCancelable(false);				
		dialogo.show();
		dialogo.esconderBotonSalir();
		dialogo.addOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				iniciarImpresionResumen(resumenAImprimir);
			}
		});
	}
	
	/**
	 * Invoca al metodo para imprimir un resumen
	 * @param resumenAImprimir
	 */
	public void iniciarImpresionResumen(DetalleResumenGenerico resumenAImprimir)
	{
		try {
			ManejadorImpresora.imprimir(resumenAImprimir.obtenerImprimible());
		} catch (ImpresoraPredefinidaNoAsignadaExcepcion e) {
			Toast.makeText(MenuPrincipal.this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * Muestra el dialogo para confirmar la impresion del resumen de detalle de ordenativos
	 * @param view
	 */
	public void btnDetalleOrdenativosClick(View view)
	{
		mostrarDialogoImprimirResumen(R.string.detalle_ordenativos_msg, R.string.titulo_detalle_ordenativos, new DetalleOrdenativos());
	}
	
	/**
	 * Muestra el dialogo para confirmar la impresion del resumen de detalle de lecturas
	 * @param view
	 */
	public void btnDetalleLecturasClick(View view)
	{
		mostrarDialogoImprimirResumen(R.string.detalle_lecturas_msg, R.string.titulo_detalle_lecturas, new DetalleLecturas());
	}
	
	/**
	 * Muestra el dialogo para confirmar la impresion del resumen de detalle de lecturas impedidas
	 * @param view
	 */
	public void btnDetalleImpedidasClick(View view)
	{
		mostrarDialogoImprimirResumen(R.string.detalle_impedidas_msg, R.string.titulo_detalle_impedidas, new DetalleImpedidas());
	}
	
	/**
	 * Muestra el dialogo para confirmar la impresion del resumen de detalle de lecturas entre lineas
	 * @param view
	 */
	public void btnDetalleEntreLineasClick(View view)
	{
		mostrarDialogoImprimirResumen(R.string.detalle_entre_lineas_msg, R.string.titulo_detalle_entre_lineas, new DetalleLecturasEntreLineas());
	}

}
