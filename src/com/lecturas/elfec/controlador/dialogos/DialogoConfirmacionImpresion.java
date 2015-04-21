package com.lecturas.elfec.controlador.dialogos;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.lecturas.elfec.R;
import com.lecturas.elfec.controlador.accionesycustomizaciones.CustomDialog;
import com.lecturas.elfec.helpers.VariablesDeSesion;
import com.lecturas.elfec.modelo.preferencias_ui.AdminUI;
import com.lecturas.elfec.modelo.preferencias_ui.OpcionesPreferenciasUI;

public class DialogoConfirmacionImpresion extends CustomDialog {

	private TextView txtAclaracionDecision;
	private CheckBox chkDecision;
	public DialogoConfirmacionImpresion(Context context) 
	{
		super(context, R.style.DialogElfecTheme);
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogo_confirmar_impresion);
		setTitle(R.string.titulo_confirmar_impresion);
		setIcon(R.drawable.impresora);
		txtAclaracionDecision = (TextView) findViewById(R.id.txt_aclaracion_no_mostrar);
		chkDecision = (CheckBox) findViewById(R.id.chk_no_mostrar_mensaje);
		chkDecision.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton btn, boolean checked) {
				txtAclaracionDecision.setVisibility((checked? View.VISIBLE: View.GONE));
			}
		});
    }
	
	/**
	 * Guarda la preferencia de mostrar dialogo de confirmacion de impresion del usuario.
	 */
	public void guardarPreferenciaMostrarDialogo()
	{
		String preferenciaMostrarDialogo = "1";
		if(chkDecision.isChecked())
			preferenciaMostrarDialogo = "0";
		AdminUI adminUI = AdminUI.instanciar(VariablesDeSesion.getUsuarioLogeado());
		adminUI.guardarPreferencia(OpcionesPreferenciasUI.MOSTRAR_CONFIRMACION_IMPRESION, preferenciaMostrarDialogo);
	}
}
