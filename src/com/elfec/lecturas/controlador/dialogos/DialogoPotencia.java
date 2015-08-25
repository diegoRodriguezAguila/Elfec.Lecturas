package com.elfec.lecturas.controlador.dialogos;

import java.math.BigDecimal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.Potencia;
import com.elfec.lecturas.modelo.eventos.OnPotenciaGuardadaListener;
import com.lecturas.elfec.R;

/**
 * @author drodriguez
 *
 */
public class DialogoPotencia {

	private AlertDialog mDialog;
	private View rootView;

	private Context mContext;

	private Lectura lecturaActual;
	private TextView lblPotencia;
	private EditText txtReactiva;
	private TextView lblReactiva;
	private EditText txtDemanda;
	private TextView lblDemanda;
	private boolean modoSoloLectura;
	private TextView lblValReactiva;
	private TextView lblValDemanda;
	private OnPotenciaGuardadaListener mListener;

	public DialogoPotencia(Context context, Lectura lectura,
			boolean modoSoloLectura) {
		this(context, lectura, modoSoloLectura, null);
	}

	@SuppressLint("InflateParams")
	public DialogoPotencia(Context context, Lectura lectura,
			boolean modoSoloLectura, OnPotenciaGuardadaListener listener) {
		this.mListener = listener;
		this.mContext = context;
		this.lecturaActual = lectura;
		this.modoSoloLectura = modoSoloLectura;
		rootView = LayoutInflater.from(context).inflate(
				R.layout.dialogo_potencia, null, false);
		AlertDialog.Builder builder = new AlertDialog.Builder(context)
				.setView(rootView)
				.setTitle(
						modoSoloLectura ? R.string.titulo_ver_potencia
								: R.string.titulo_registro_potencia)
				.setIcon(R.drawable.visualizar_potencia_d)
				.setNegativeButton(
						modoSoloLectura ? R.string.salida_btn
								: R.string.btn_cancel, null);

		if (!modoSoloLectura)
			builder.setPositiveButton(R.string.guardar_btn, null);

		mDialog = builder.create();
		mostrarOcultarReactivaYDemanda();
	}

	/**
	 * Muestra el dialogo
	 */
	public void show() {
		mDialog.show();
		if (!modoSoloLectura) {
			mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Potencia potencia = lecturaActual.PotenciaLectura;
							BigDecimal lecturaNueva = null;
							BigDecimal eReactiva = null;
							boolean seGuarda = true;
							try {
								// si lee reactiva
								if (lecturaActual.LeePotencia == 1) {
									lecturaNueva = new BigDecimal(txtDemanda
											.getText().toString());
								}
								// si lee reactiva
								if (lecturaActual.LeeReactiva == 1) {
									eReactiva = new BigDecimal(txtReactiva
											.getText().toString());
								}
							} catch (NumberFormatException e) {
								seGuarda = false;
							}
							if (seGuarda) {
								potencia.leerPotencia(lecturaNueva, eReactiva,
										lecturaActual);
								potencia.save();
								lecturaActual.PotenciaLectura = potencia;
								mDialog.dismiss();
								if (mListener != null)
									mListener.onPotenciaGuardada(lecturaActual,
											potencia);
							} else {
								Toast.makeText(
										mContext,
										R.string.msg_no_se_puede_guardar_potencia,
										Toast.LENGTH_LONG).show();
							}
						}
					});
		}
	}

	private void mostrarOcultarReactivaYDemanda() {
		lblPotencia = (TextView) rootView.findViewById(R.id.lbl_potencia);
		txtReactiva = (EditText) rootView.findViewById(R.id.txt_reactiva);
		lblReactiva = (TextView) rootView.findViewById(R.id.lbl_e_reactiva);
		lblValReactiva = (TextView) rootView
				.findViewById(R.id.lbl_val_reactiva);
		txtDemanda = (EditText) rootView.findViewById(R.id.txt_demanda);
		lblDemanda = (TextView) rootView.findViewById(R.id.lbl_demanda);
		lblValDemanda = (TextView) rootView.findViewById(R.id.lbl_val_demanda);
		lblPotencia.setText(""
				+ lecturaActual.PotenciaLectura.LecturaAnteriorPotencia);
		if (lecturaActual.LeePotencia == 0)// si no lee potencia
		{
			lblDemanda.setVisibility(View.GONE);
			txtDemanda.setVisibility(View.GONE);
			lblValDemanda.setVisibility(View.GONE);
		} else {
			lblDemanda.setVisibility(View.VISIBLE);
			if (modoSoloLectura) {
				lblValDemanda.setVisibility(View.VISIBLE);
				lblValDemanda
						.setText(lecturaActual.PotenciaLectura.LecturaNuevaPotencia
								.toString());
				txtDemanda.setVisibility(View.GONE);
			} else {
				lblValDemanda.setVisibility(View.GONE);
				txtDemanda.setVisibility(View.VISIBLE);
			}
		}
		if (lecturaActual.LeeReactiva == 0)// si no lee reactiva
		{
			txtReactiva.setVisibility(View.GONE);
			lblReactiva.setVisibility(View.GONE);
			lblValReactiva.setVisibility(View.GONE);
		} else {
			lblReactiva.setVisibility(View.VISIBLE);
			if (modoSoloLectura) {
				lblValReactiva.setVisibility(View.VISIBLE);
				lblValReactiva.setText(lecturaActual.PotenciaLectura.Reactiva
						.toString());
				txtReactiva.setVisibility(View.GONE);
			} else {
				lblValReactiva.setVisibility(View.GONE);
				txtReactiva.setVisibility(View.VISIBLE);
			}
		}
	}
}
