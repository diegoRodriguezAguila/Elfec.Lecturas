package com.lecturas.elfec.controlador;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.lecturas.elfec.R;
import com.lecturas.elfec.controlador.adaptadores.LecturaAdapter;
import com.lecturas.elfec.helpers.ManejadorDeIndice;
import com.lecturas.elfec.modelo.Lectura;
import com.lecturas.elfec.modelo.estadoslectura.EstadoLecturaFactory;
import com.lecturas.elfec.modelo.estadoslectura.IEstadoLectura;


public class ListaLecturas extends Activity {

	private Spinner selectorTipoLectura;
	private ListView listViewLecturas;
	private TextView lblNumLecturas;
	private ArrayList<Lectura> listaLecturas;
	private LecturaAdapter adapter;
	private List<String> listaEstados;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista_lecturas);
		lblNumLecturas =(TextView)findViewById(R.id.lbl_num_lecturas_lista);
		selectorTipoLectura = (Spinner) findViewById(R.id.select_mostrar_tipo_lectura);
		//Datos seleccionables
		listaEstados = new ArrayList<String>();
		listaEstados.add("Todas");
		
		List<IEstadoLectura> estadosLecturas = EstadoLecturaFactory.obtenerEstadosRegistrados();
		for(IEstadoLectura estado : estadosLecturas)
		{
			listaEstados.add(estado.getEstadoCadena()+"s");
		}
		ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this,  R.layout.spinner_item, R.id.lbl_opcion_item, listaEstados);
		selectorTipoLectura.setAdapter(adapter_state);
		
		listViewLecturas = (ListView)findViewById(R.id.list_lecturas);
		listViewLecturas.setFastScrollEnabled(true);
		listViewLecturas.setOnItemClickListener(new LecturaItemClickListener(this));
		listaLecturas = (ArrayList<Lectura>) Lectura.obtenerTodasLasLecturas();
		
		selectorTipoLectura.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
					int position, long id) {
				if(position==0)//Todas
				{
					listaLecturas =(ArrayList<Lectura>) Lectura.obtenerTodasLasLecturas();
				}
				else
				{
					listaLecturas =(ArrayList<Lectura>) Lectura.obtenerLecturasPorEstado(position-1);
				}
				asignarDatos();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		asignarDatos();
	}
	
	public void asignarDatos()
	{
		adapter = new LecturaAdapter(this, R.layout.list_item_lectura,listaLecturas); // the adapter is a member field in the activity
		listViewLecturas.setAdapter(adapter);
		lblNumLecturas.setText(""+listaLecturas.size());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lista_lecturas, menu);
		return true;
	}
	
	
	@Override
	public void onBackPressed() {
	    finish();//go back to the previous Activity
	    overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);  
	}

	//Inner class
	private class LecturaItemClickListener implements OnItemClickListener
	{
		private Context context;
		public LecturaItemClickListener(Context context)
		{
			this.context=context;
		}
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int pos,
				long id) {
			long idLectura = listaLecturas.get(pos).getId();
			Intent intent = new Intent(context, TomarLectura.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			ManejadorDeIndice.setIdLecturaActual(idLectura);
		    startActivity(intent);
			overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out); 
		}
		
	}

}
