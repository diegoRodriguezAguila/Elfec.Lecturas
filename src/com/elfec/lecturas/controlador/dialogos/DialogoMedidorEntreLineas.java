package com.elfec.lecturas.controlador.dialogos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.elfec.lecturas.helpers.ManejadorUbicacion;
import com.elfec.lecturas.helpers.ui.Animador;
import com.elfec.lecturas.logica_negocio.ManejadorBackupTexto;
import com.elfec.lecturas.logica_negocio.web_services.ManejadorConexionRemota;
import com.elfec.lecturas.modelo.AsignacionRuta;
import com.elfec.lecturas.modelo.MedidorEntreLineas;
import com.elfec.lecturas.settings.VariablesDeEntorno;
import com.lecturas.elfec.R;

public class DialogoMedidorEntreLineas extends AlertDialog {

	/**
	 * La lectura entre lineas del dialogo, null en caso de que no se haya
	 * guardado nada
	 */
	public MedidorEntreLineas medEntreLineas;

	private Activity context;
	private TextView txtNumMedidor;
	private TextView txtLectura;
	private TextView txtDemanda;
	private TextView txtReactiva;
	private CheckBox chkConPotencia;
	private Button btnGuardar;
	private Button btnSalir;
	private Spinner selectorRuta;
	private RelativeLayout conPotenciaLayout;
	private ArrayList<String> listaRutas;

	public DialogoMedidorEntreLineas(Activity context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogo_medidor_entre_lineas);
		setTitle(R.string.titulo_medidor_entre_lineas);
		setIcon(R.drawable.nuevo_medidor);
		conPotenciaLayout = (RelativeLayout) findViewById(R.id.layout_con_potencia);
		txtNumMedidor = (TextView) findViewById(R.id.txt_num_medidor_entre_lineas);
		txtLectura = (TextView) findViewById(R.id.txt_lectura_entre_lineas);
		txtDemanda = (TextView) findViewById(R.id.txt_demanda_entre_lineas);
		txtReactiva = (TextView) findViewById(R.id.txt_reactiva_entre_lineas);
		chkConPotencia = (CheckBox) findViewById(R.id.chk_con_potencia);
		btnGuardar = (Button) findViewById(R.id.btn_guardar_medidor_entre_lineas);
		btnSalir = (Button) findViewById(R.id.btn_salir_dialogo_medidor_entre_lineas);
		selectorRuta = (Spinner) findViewById(R.id.select_ruta);
		conPotenciaLayout.setVisibility(View.GONE);
		listaRutas = new ArrayList<String>();
		List<AsignacionRuta> rutasUsuario = AsignacionRuta
				.obtenerTodasLasRutas();
		for (AsignacionRuta asignRuta : rutasUsuario) {
			listaRutas.add("" + asignRuta.Ruta);
		}
		ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(context,
				R.layout.spinner_item, R.id.lbl_opcion_item, listaRutas);
		selectorRuta.setAdapter(adapter_state);
		btnSalir.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		btnGuardar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (guardarMedidorEntreLineas()) {
					dismiss();
					Toast.makeText(context,
							R.string.medidor_entre_lineas_guardado_msg,
							Toast.LENGTH_LONG).show();
				}
			}
		});

		chkConPotencia
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							Animador.expand(conPotenciaLayout);
						} else {
							Animador.collapse(conPotenciaLayout);
						}
					}
				});

	}

	private boolean guardarMedidorEntreLineas() {
		String numMedidor = txtNumMedidor.getText().toString();
		int ruta = Integer.parseInt(selectorRuta.getSelectedItem().toString());
		int lecturaNueva = (txtLectura.getText().toString().equals("") ? -1
				: Integer.parseInt(txtLectura.getText().toString()));
		BigDecimal lecturaPotencia = txtDemanda.getText().toString().equals("") ? null
				: new BigDecimal(txtDemanda.getText().toString());
		BigDecimal energiaReactiva = txtReactiva.getText().toString()
				.equals("") ? null : new BigDecimal(txtReactiva.getText()
				.toString());
		if (!numMedidor.isEmpty() && lecturaNueva != -1) {
			medEntreLineas = new MedidorEntreLineas(numMedidor, ruta,
					lecturaNueva, lecturaPotencia, energiaReactiva, new Date());
			medEntreLineas.save();
			ManejadorBackupTexto.guardarBackupModelo(medEntreLineas);
			if (VariablesDeEntorno.tipoGuardadoUbicacion == 0)// no se guarda
																// ubicacion
			{
				ManejadorConexionRemota
						.guardarLecturaEntreLineas(medEntreLineas);
			}
			ManejadorUbicacion.obtenerUbicacionActual(context, medEntreLineas,
					1);// guarda ubicacion por 3g, o por gps si es que no
						// hubiera conexion
			return true;
		}
		return false;
	}

}
