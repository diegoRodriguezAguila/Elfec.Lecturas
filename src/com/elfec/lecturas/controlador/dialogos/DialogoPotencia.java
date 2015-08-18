package com.elfec.lecturas.controlador.dialogos;

import java.math.BigDecimal;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.elfec.lecturas.controlador.TomarLectura;
import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.Potencia;
import com.lecturas.elfec.R;

/**
 * @author drodriguez
 *
 */
public class DialogoPotencia extends AlertDialog {

	private Context context;
	private Lectura lecturaActual;
	private TextView lblPotencia;
	private EditText txtReactiva;
	private TextView lblReactiva;
	private EditText txtDemanda;
	private TextView lblDemanda;
	private Button btnAceptar;
	private Button btnCancelar;
	private boolean modoSoloLectura;
	private TextView lblValReactiva;
	private TextView lblValDemanda;
	public boolean seGuardoLectura = false;

	public DialogoPotencia(Context context, Lectura lectura,
			boolean modoSoloLectura) {
		super(context);
		this.context = context;
		this.lecturaActual = lectura;
		this.modoSoloLectura = modoSoloLectura;
	}

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogo_potencia);
		setTitle(R.string.titulo_registro_potencia);
		setIcon(R.drawable.visualizar_potencia);
		mostrarOcultarReactivaYDemanda();
		btnAceptar = (Button) findViewById(R.id.btn_aceptar);
		btnCancelar = (Button) findViewById(R.id.btn_cancelar);
		if (modoSoloLectura) {
			btnAceptar.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
			btnCancelar.setVisibility(View.INVISIBLE);
			RelativeLayout.LayoutParams lp = (LayoutParams) btnAceptar
					.getLayoutParams();
			lp.leftMargin = 65;
			btnAceptar.setLayoutParams(lp);
			setTitle(R.string.titulo_ver_potencia);
		} else {
			btnAceptar.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Potencia potencia = lecturaActual.PotenciaLectura;
					BigDecimal lecturaNueva = null;
					BigDecimal eReactiva = null;
					boolean seGuarda = true;
					try {
						if (lecturaActual.LeePotencia == 1)// si lee reactiva
						{
							lecturaNueva = new BigDecimal(txtDemanda.getText()
									.toString());
						}
						if (lecturaActual.LeeReactiva == 1)// si lee reactiva
						{
							eReactiva = new BigDecimal(txtReactiva.getText()
									.toString());
						}
					} catch (NumberFormatException e) {
						seGuarda = false;
					}
					if (seGuarda) {
						potencia.leerPotencia(lecturaNueva, eReactiva,
								lecturaActual);
						potencia.save();
						lecturaActual.PotenciaLectura = potencia;
						((TomarLectura) context).guardarLectura();
						seGuardoLectura = true;
						dismiss();
					}
				}

			});
		}
		btnCancelar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	public void mostrarOcultarReactivaYDemanda() {
		lblPotencia = (TextView) findViewById(R.id.lbl_potencia);
		txtReactiva = (EditText) findViewById(R.id.txt_reactiva);
		lblReactiva = (TextView) findViewById(R.id.lbl_e_reactiva);
		lblValReactiva = (TextView) findViewById(R.id.lbl_val_reactiva);
		txtDemanda = (EditText) findViewById(R.id.txt_demanda);
		lblDemanda = (TextView) findViewById(R.id.lbl_demanda);
		lblValDemanda = (TextView) findViewById(R.id.lbl_val_demanda);
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
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		if (lecturaActual.LeeReactiva == 0)// si no lee reactiva
		{
			txtReactiva.setVisibility(View.GONE);
			lblReactiva.setVisibility(View.GONE);
			lblValReactiva.setVisibility(View.GONE);
			lp.addRule(RelativeLayout.BELOW, R.id.lbl_pot);
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
			lp.addRule(RelativeLayout.BELOW, R.id.lbl_e_reactiva);
		}
		lp.topMargin = 10;
		lblDemanda.setLayoutParams(lp);
	}
}
