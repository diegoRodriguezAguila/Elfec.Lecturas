package com.elfec.lecturas.controlador;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.elfec.lecturas.controlador.adaptadores.LecturaAdapter;
import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.estados.lectura.EstadoLecturaFactory;
import com.elfec.lecturas.modelo.estados.lectura.IEstadoLectura;
import com.lecturas.elfec.R;
import com.quentindommerc.superlistview.SuperListview;

public class ListaLecturas extends AppCompatActivity {

	private Spinner selectorTipoLectura;
	private SuperListview listViewLecturas;
	private TextView lblNumLecturas;
	private LecturaAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista_lecturas);
		new Thread(new Runnable() {
			@Override
			public void run() {
				lblNumLecturas = (TextView) findViewById(R.id.lbl_num_lecturas_lista);
				selectorTipoLectura = (Spinner) findViewById(R.id.select_mostrar_tipo_lectura);
				// Datos seleccionables
				cargarEstadosLecturas();
				listViewLecturas = (SuperListview) findViewById(R.id.list_lecturas);
				listViewLecturas
						.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> arg0,
									View view, int pos, long id) {
								Intent returnIntent = new Intent();
								returnIntent.putExtra(
										TomarLectura.ARG_ID_LECTURA,
										adapter.getItemId(pos));
								setResult(RESULT_OK, returnIntent);
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
								listViewLecturas.showProgress();
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

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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

	@SuppressLint("NewApi")
	public void asignarListaLecturas(final List<Lectura> lecturas) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				adapter = new LecturaAdapter(ListaLecturas.this,
						R.layout.list_item_lectura, lecturas);
				listViewLecturas.setAdapter(adapter);
				lblNumLecturas.setText("" + adapter.getCount());
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
					listViewLecturas.getList().setFastScrollAlwaysVisible(true);
				listViewLecturas.getList().setFastScrollEnabled(true);
			}
		});
	}

	@Override
	public void onBackPressed() {
		Intent returnIntent = new Intent();
		setResult(RESULT_CANCELED, returnIntent);
		finish();// go back to the previous Activity
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}

}
