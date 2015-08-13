package com.elfec.lecturas.controlador.dialogos;

import java.util.ArrayList;

import com.elfec.lecturas.controlador.accionesycustomizaciones.CustomDialog;
import com.elfec.lecturas.controlador.adaptadores.OrdenativoLecturaAdapter;
import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.OrdenativoLectura;
import com.lecturas.elfec.R;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class DialogoVerOrdenativos extends CustomDialog{

	private Context context;
	private Lectura lecturaActual;
	private ListView listViewOrdLect;
	private ArrayList<OrdenativoLectura> listaOrdLect;
	private OrdenativoLecturaAdapter ordAdapter;
	private Button btnSalir;
	
	public DialogoVerOrdenativos(Context cont, Lectura lectura) {
		super(cont, R.style.DialogElfecTheme);
		lecturaActual = lectura;
		this.context = cont;
		listaOrdLect = (ArrayList<OrdenativoLectura>) lecturaActual.obtenerOrdenativosLectura();
		ordAdapter = new OrdenativoLecturaAdapter(context, R.layout.list_item_ordenativo,listaOrdLect);
	}
	
	@Override
    protected void onCreate(android.os.Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogo_ver_ordenativos);
	    setTitle(R.string.titulo_ver_ordenativos);
	    listViewOrdLect = (ListView)findViewById(R.id.list_view_ord_lect);
	    btnSalir = (Button)findViewById(R.id.btn_salir_dialogo_medidor_entre_lineas);
	    listViewOrdLect.setFastScrollEnabled(true);
	    listViewOrdLect.setAdapter(ordAdapter);
	    ponerItemClickListenerVO();
	    if(listaOrdLect.size()==0)//no hay ordenativos
	    {
	    	findViewById(R.id.lbl_info_lectura_no_ordenativos).setVisibility(View.VISIBLE);
	    	findViewById(R.id.list_view_ord_lect).setVisibility(View.GONE);
	    }
	    else
	    {
	    	findViewById(R.id.lbl_info_lectura_no_ordenativos).setVisibility(View.GONE);
	    	findViewById(R.id.list_view_ord_lect).setVisibility(View.VISIBLE);
	    }
	    setBotonSalirListener();
    }
	
	private void ponerItemClickListenerVO() {
		listViewOrdLect.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				ordAdapter.setSeleccionado(pos);
			}
		});
	}
	
	private void setBotonSalirListener() {
		btnSalir.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
	}

}
