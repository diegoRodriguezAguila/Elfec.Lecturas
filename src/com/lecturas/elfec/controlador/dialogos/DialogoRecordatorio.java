package com.lecturas.elfec.controlador.dialogos;


import com.lecturas.elfec.R;
import com.lecturas.elfec.controlador.TomarLectura;
import com.lecturas.elfec.controlador.accionesycustomizaciones.CustomDialog;
import com.lecturas.elfec.modelo.Lectura;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DialogoRecordatorio  extends CustomDialog{

	private Lectura lecturaActual;
	private Button btnSalir;
	private Button btnGuardar;
	private TextView lblRecordatorioActual;
	private EditText txtRecordatorioNuevo;
	private Activity context;
	
	public DialogoRecordatorio(Activity cont, Lectura lectura) {
		super(cont, R.style.DialogElfecTheme);
		lecturaActual = lectura;
		context = cont;
	}
	
	@Override
    protected void onCreate(android.os.Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogo_recordatorio_lector);
	    setTitle(R.string.titulo_recordatorio);
	    setIcon(R.drawable.recordatorio_lector);
	    btnSalir = (Button)findViewById(R.id.btn_salir_dialogo_medidor_entre_lineas);
	    btnSalir.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
	    lblRecordatorioActual = (TextView)findViewById(R.id.lbl_recordatorio_actual_msg);
	    if(lecturaActual.Recordatorio!=null)
	    	lblRecordatorioActual.setText(lecturaActual.Recordatorio);
	    if(!lecturaActual.tieneRecordatorio())
	    {
	    	lblRecordatorioActual.setText(R.string.recordatorio_actual_lbl);
	    }
	    btnGuardar = (Button)findViewById(R.id.btn_guardar_medidor_entre_lineas);
	    btnGuardar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				guardarRecordatorio();
				dismiss();
				Toast.makeText(context, R.string.recordatorio_guardado_msg, Toast.LENGTH_LONG).show();
			}
		});    
	    txtRecordatorioNuevo = (EditText)findViewById(R.id.txt_recordatorio_nuevo);
    }
	
	private void guardarRecordatorio()
	{
		String recNuevo = txtRecordatorioNuevo.getText().toString();
		lecturaActual.Recordatorio = recNuevo;
		lecturaActual.save();
		((TomarLectura)context).btnRecordatorios.setEnabled(lecturaActual.tieneRecordatorio());
	}
	
}
