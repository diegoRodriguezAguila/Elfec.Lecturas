package com.elfec.lecturas.controlador;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.elfec.lecturas.controlador.accionesycustomizaciones.CustomDialog;
import com.elfec.lecturas.helpers.LectorConfigBD;
import com.lecturas.elfec.R;

public class Configurar extends Activity {

	private EditText txtIp;
	private EditText txtPuerto;
	private EditText txtServicio;
	private EditText txtRol;
	private EditText txtPasswordRol;
	private JSONObject config;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configurar);
		txtIp = (EditText)findViewById(R.id.txt_ip);
		txtPuerto = (EditText)findViewById(R.id.txt_puerto);
		txtServicio = (EditText)findViewById(R.id.txt_servicio);
		txtRol = (EditText)findViewById(R.id.txt_rol);
		txtPasswordRol = (EditText)findViewById(R.id.txt_password_rol);
		try {
			config =LectorConfigBD.obtenerConfiguracion(this);
			txtIp.setText(config.getString("ip"));
			txtPuerto.setText(config.getString("puerto"));
			txtServicio.setText(config.getString("servicio"));
			txtRol.setText(config.getString("rol"));
		} catch (JSONException e) {
			CustomDialog dialog = new CustomDialog(this);
			dialog.setMessage(R.string.config_invalida);
			dialog.setIcon(R.drawable.error);
			dialog.setTitle(R.string.titulo_mensajes_error);
			dialog.setPositiveButton(null);
			dialog.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.configurar, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
	    finish();//go back to the previous Activity
	    overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);  
	}
	
	public void btnSalirClick(View view)
	{
		finish();//go back to the previous Activity
	    overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);  
	}
	
	public void btnGuardarClick(View view)
	{
		try 
		{
			config.put("ip", txtIp.getText());
			config.put("puerto", txtPuerto.getText());
			config.put("servicio", txtServicio.getText());
			config.put("rol", txtRol.getText());
			if(!txtPasswordRol.getText().toString().equals(""))
			{
				String password = txtPasswordRol.getText().toString();
				config.put("password", password);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		boolean exito = LectorConfigBD.escribirConfiguracion(config);
		int tituloResultado;
		int mensajeResultado;
		if(exito)
		{
			mensajeResultado = R.string.config_guardado_exito;
			tituloResultado = R.string.titulo_mensajes_exito;
		}
		else
		{
			mensajeResultado = R.string.config_no_guardada;
			tituloResultado = R.string.titulo_mensajes_error;
		}
		final CustomDialog dialog = new CustomDialog(this);
		dialog.setMessage(mensajeResultado);
		dialog.setTitle(tituloResultado);
		dialog.setCancelable(false);
		dialog.setPositiveButton(R.string.btn_ok, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				finish();//go back to the previous Activity
			    overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);  
			}
           });
		dialog.show();
	}
	
}
