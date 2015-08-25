package com.elfec.lecturas.controlador;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.elfec.lecturas.acceso_remoto_datos.LectorConfigBD;
import com.lecturas.elfec.R;

public class Configurar extends AppCompatActivity {

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
		txtIp = (EditText) findViewById(R.id.txt_ip);
		txtPuerto = (EditText) findViewById(R.id.txt_puerto);
		txtServicio = (EditText) findViewById(R.id.txt_servicio);
		txtRol = (EditText) findViewById(R.id.txt_rol);
		txtPasswordRol = (EditText) findViewById(R.id.txt_password_rol);
		try {
			config = LectorConfigBD.obtenerConfiguracion(this);
			txtIp.setText(config.getString("ip"));
			txtPuerto.setText(config.getString("puerto"));
			txtServicio.setText(config.getString("servicio"));
			txtRol.setText(config.getString("rol"));
		} catch (JSONException e) {
			new AlertDialog.Builder(this).setMessage(R.string.config_invalida)
					.setTitle(R.string.titulo_config_invalida)
					.setPositiveButton(R.string.btn_ok, null).show();
		}
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.configurar, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		finish();// go back to the previous Activity
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}

	public void btnSalirClick(View view) {
		finish();// go back to the previous Activity
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}

	public void btnGuardarClick(View view) {
		try {
			config.put("ip", txtIp.getText());
			config.put("puerto", txtPuerto.getText());
			config.put("servicio", txtServicio.getText());
			config.put("rol", txtRol.getText());
			if (!txtPasswordRol.getText().toString().equals("")) {
				String password = txtPasswordRol.getText().toString();
				config.put("password", password);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		boolean exito = LectorConfigBD.escribirConfiguracion(config);
		int tituloResultado;
		int mensajeResultado;
		if (exito) {
			mensajeResultado = R.string.config_guardado_exito;
			tituloResultado = R.string.titulo_mensajes_exito;
		} else {
			mensajeResultado = R.string.config_no_guardada;
			tituloResultado = R.string.titulo_mensajes_error;
		}
		new AlertDialog.Builder(this).setMessage(mensajeResultado)
				.setTitle(tituloResultado).setCancelable(false)
				.setPositiveButton(R.string.btn_ok, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();// go back to the previous Activity
						overridePendingTransition(R.anim.slide_right_in,
								R.anim.slide_right_out);
					}
				}).show();
	}

}
