package com.elfec.lecturas.controlador;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.elfec.lecturas.controlador.adaptadores.LecturaAdapter;
import com.elfec.lecturas.helpers.ManejadorDeIndice;
import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.estadoslectura.EstadoLecturaFactory;
import com.elfec.lecturas.modelo.estadoslectura.IEstadoLectura;
import com.lecturas.elfec.R;
import com.quentindommerc.superlistview.SuperListview;

public class ListaLecturas extends Activity {

	private Spinner selectorTipoLectura;
	private SuperListview listViewLecturas;
	private TextView lblNumLecturas;
	private LecturaAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista_lecturas);
		new Thread(new Runnable() {

			@SuppressLint("NewApi")
			@Override
			public void run() {
				lblNumLecturas = (TextView) findViewById(R.id.lbl_num_lecturas_lista);
				selectorTipoLectura = (Spinner) findViewById(R.id.select_mostrar_tipo_lectura);
				// Datos seleccionables
				cargarEstadosLecturas();
				listViewLecturas = (SuperListview) findViewById(R.id.list_lecturas);
				listViewLecturas.getList().setFastScrollEnabled(true);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
					listViewLecturas.getList().setFastScrollAlwaysVisible(true);
				listViewLecturas
						.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> arg0,
									View view, int pos, long id) {
								ManejadorDeIndice.setIdLecturaActual(adapter
										.getItemId(pos));
								finish();
								overridePendingTransition(
										R.anim.slide_right_in,
										R.anim.slide_right_out);
							}
						});
				selectorTipoLectura
						.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> adapter,
									View selectedItemView, final int position,
									long id) {
								new Thread(new Runnable() {
									@Override
									public void run() {
										cargarLecturasPorEstado(position == 0 ? -1
												: (position - 1));
									}
								}).start();

							}

							@Override
							public void onNothingSelected(AdapterView<?> adapter) {
							}
						});
				cargarLecturasPorEstado(-1);
			}
		}).start();
	}

	private void cargarEstadosLecturas() {
		List<String> listaEstados = new ArrayList<String>();
		listaEstados.add("Todas");
		List<IEstadoLectura> estadosLecturas = EstadoLecturaFactory
				.obtenerEstadosRegistrados();
		for (IEstadoLectura estado : estadosLecturas) {
			listaEstados.add(estado.getEstadoCadena() + "s");
		}
		asignarEstadosLecturas(listaEstados);
	}

	private void asignarEstadosLecturas(final List<String> listaEstados) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				selectorTipoLectura.setAdapter(new ArrayAdapter<String>(
						ListaLecturas.this, R.layout.spinner_item,
						R.id.lbl_opcion_item, listaEstados));
			}
		});
	}

	public void cargarLecturasPorEstado(int estado) {
		asignarListaLecturas(estado == -1 ? Lectura.obtenerTodasLasLecturas()
				: Lectura.obtenerLecturasPorEstado(estado));
	}

	public void asignarListaLecturas(final List<Lectura> lecturas) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				adapter = new LecturaAdapter(ListaLecturas.this,
						R.layout.list_item_lectura, lecturas);
				listViewLecturas.setAdapter(adapter);
				lblNumLecturas.setText("" + adapter.getCount());
			}
		});
	}

	@Override
	public void onBackPressed() {
		finish();// go back to the previous Activity
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}

}
