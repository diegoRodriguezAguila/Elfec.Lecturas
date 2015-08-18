package com.elfec.lecturas.controlador.dialogos;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.elfec.lecturas.controlador.BuscarLectura;
import com.elfec.lecturas.controlador.adaptadores.LecturaAdapter;
import com.elfec.lecturas.modelo.Lectura;
import com.lecturas.elfec.R;

public class DialogoResultadosBusqueda extends AlertDialog {

	private BuscarLectura context;
	private Button btnSalir;
	private ListView listViewLecturas;
	private TextView lblNumLecturas;
	private ArrayList<Lectura> listaLecturas;

	public DialogoResultadosBusqueda(BuscarLectura context,
			List<Lectura> listaResultadosLecs) {
		super(context, R.style.DialogElfecTheme);
		this.context = context;
		listaLecturas = (ArrayList<Lectura>) listaResultadosLecs;
	}

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogo_resultados_busqueda);
		setTitle(R.string.titulo_resultados_busqueda);
		setIcon(R.drawable.resultados_busqueda);
		lblNumLecturas = (TextView) findViewById(R.id.lbl_num_lecturas_lista);
		listViewLecturas = (ListView) findViewById(R.id.list_lecturas);
		listViewLecturas.setFastScrollEnabled(true);
		listViewLecturas.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos,
					long id) {
				dismiss();
				Lectura lecturaSelec = listaLecturas.get(pos);
				context.irALecturaEncontrada(lecturaSelec);
			}
		});
		LecturaAdapter adapter = new LecturaAdapter(context,
				R.layout.list_item_lectura, listaLecturas, true); // the adapter
																	// is a
																	// member
																	// field in
																	// the
																	// activity
		listViewLecturas.setAdapter(adapter);
		lblNumLecturas.setText("" + listaLecturas.size());

		btnSalir = (Button) findViewById(R.id.btn_salir_dialogo);
		btnSalir.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
	}

}
