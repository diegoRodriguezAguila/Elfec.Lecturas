package com.elfec.lecturas.controlador.dialogos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.elfec.lecturas.helpers.ManejadorUbicacion;
import com.elfec.lecturas.helpers.ui.Animador;
import com.elfec.lecturas.logica_negocio.ManejadorBackupTexto;
import com.elfec.lecturas.logica_negocio.web_services.ManejadorConexionRemota;
import com.elfec.lecturas.modelo.AsignacionRuta;
import com.elfec.lecturas.modelo.MedidorEntreLineas;
import com.elfec.lecturas.modelo.eventos.OnMedidorEntreLineasGuardadoListener;
import com.elfec.lecturas.settings.VariablesDeEntorno;
import com.lecturas.elfec.R;

public class DialogoMedidorEntreLineas {

	/**
	 * La lectura entre lineas del dialogo, null en caso de que no se haya
	 * guardado nada
	 */
	private MedidorEntreLineas medEntreLineas;

	private AlertDialog mDialog;
	private View rootView;

	private OnMedidorEntreLineasGuardadoListener mListener;

	private Context mContext;
	private TextView txtNumMedidor;
	private TextView txtLectura;
	private TextView txtDemanda;
	private TextView txtReactiva;
	private CheckBox chkConPotencia;
	private Spinner selectorRuta;
	private LinearLayout conPotenciaLayout;
	private List<String> listaRutas;

	@SuppressLint("InflateParams")
	public DialogoMedidorEntreLineas(Context context,
			OnMedidorEntreLineasGuardadoListener listener) {
		this.mContext = context;
		this.mListener = listener;
		rootView = LayoutInflater.from(context).inflate(
				R.layout.dialogo_medidor_entre_lineas, null, false);
		mDialog = new AlertDialog.Builder(context).setView(rootView)
				.setTitle(R.string.titulo_medidor_entre_lineas)
				.setIcon(R.drawable.medidor_entre_lineas_d)
				.setNegativeButton(R.string.salida_btn, null)
				.setPositiveButton(R.string.guardar_btn, null).create();
		conPotenciaLayout = (LinearLayout) rootView
				.findViewById(R.id.layout_con_potencia);
		txtNumMedidor = (TextView) rootView
				.findViewById(R.id.txt_num_medidor_entre_lineas);
		txtLectura = (TextView) rootView
				.findViewById(R.id.txt_lectura_entre_lineas);
		txtDemanda = (TextView) rootView
				.findViewById(R.id.txt_demanda_entre_lineas);
		txtReactiva = (TextView) rootView
				.findViewById(R.id.txt_reactiva_entre_lineas);
		chkConPotencia = (CheckBox) rootView
				.findViewById(R.id.chk_con_potencia);
		selectorRuta = (Spinner) rootView.findViewById(R.id.select_ruta);
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

	/**
	 * Muestra el dialogo
	 */
	public void show() {
		mDialog.show();
		mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (guardarMedidorEntreLineas()) {
							mDialog.dismiss();
							Toast.makeText(mContext,
									R.string.medidor_entre_lineas_guardado_msg,
									Toast.LENGTH_LONG).show();
							if (mListener != null)
								mListener
										.onMedidorEntreLineasGuardado(medEntreLineas);
						} else
							Toast.makeText(
									mContext,
									R.string.msg_no_se_puede_guardar_medidor_entre_lineas,
									Toast.LENGTH_LONG).show();
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
			ManejadorUbicacion.obtenerUbicacionActual(mContext, medEntreLineas,
					1);// guarda ubicacion por 3g, o por gps si es que no
						// hubiera conexion
			return true;
		}
		return false;
	}

}
