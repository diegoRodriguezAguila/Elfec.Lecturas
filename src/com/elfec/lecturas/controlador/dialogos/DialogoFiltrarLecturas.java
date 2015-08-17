package com.elfec.lecturas.controlador.dialogos;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.elfec.lecturas.controlador.accionesycustomizaciones.CustomDialog;
import com.elfec.lecturas.controlador.filtroslecturas.CriterioEstado;
import com.elfec.lecturas.controlador.filtroslecturas.CriterioRuta;
import com.elfec.lecturas.controlador.filtroslecturas.FiltroLecturas;
import com.elfec.lecturas.modelo.AsignacionRuta;
import com.elfec.lecturas.modelo.estadoslectura.EstadoLecturaFactory;
import com.elfec.lecturas.modelo.estadoslectura.IEstadoLectura;
import com.lecturas.elfec.R;

public class DialogoFiltrarLecturas extends CustomDialog {
	private Spinner selectorTipoLectura;
	private Spinner selectorRuta;
	private Context context;
	private List<String> listaEstados;
	private List<String> listaRutas;
	private List<IEstadoLectura> estadosLecturas;
	private List<AsignacionRuta> rutasUsuario;
	public CriterioEstado criterioEstado;
	public CriterioRuta criterioRuta;
	private FiltroLecturas filtroLecturas;

	public DialogoFiltrarLecturas(Context context, FiltroLecturas filtroLecturas) {
		super(context, R.style.DialogElfecTheme);
		this.context = context;
		this.filtroLecturas = filtroLecturas;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogo_filtrar_lecturas);
		setTitle(R.string.titulo_filtrar_lecturas);
		setIcon(R.drawable.filtrar_lecturas);
		listaRutas = new ArrayList<String>();
		listaRutas.add("Todas");
		rutasUsuario = AsignacionRuta.obtenerTodasLasRutas();
		for (AsignacionRuta asignRuta : rutasUsuario) {
			listaRutas.add("" + asignRuta.Ruta);
		}
		selectorRuta = (Spinner) findViewById(R.id.select_ruta);
		ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(context,
				R.layout.spinner_item, R.id.lbl_opcion_item, listaRutas);
		selectorRuta.setAdapter(adapter_state);
		asignarRutaSeleccionada();

		listaEstados = new ArrayList<String>();
		listaEstados.add("Todos");

		estadosLecturas = EstadoLecturaFactory.obtenerEstadosRegistrados();
		for (IEstadoLectura estado : estadosLecturas) {
			listaEstados.add(estado.getEstadoCadena() + "s");
		}
		selectorTipoLectura = (Spinner) findViewById(R.id.select_mostrar_tipo_lectura);
		ArrayAdapter<String> adapter_state2 = new ArrayAdapter<String>(context,
				R.layout.spinner_item, R.id.lbl_opcion_item, listaEstados);
		selectorTipoLectura.setAdapter(adapter_state2);
		asignarTipoLecturaSeleccionado();

		asignarSelectListeners();
	}

	/**
	 * Asigna el estado de lecturas que fue seleccionada previamente segun el
	 * criterio del filtro <b>FiltroLecturas</b> si el criterio es null, pone
	 * por defecto a todas;
	 */
	private void asignarTipoLecturaSeleccionado() {
		CriterioEstado criterioEstado = (CriterioEstado) filtroLecturas
				.obtenerCriterioDeFiltro(CriterioEstado.class);
		int posSeleccionada = 0;
		if (criterioEstado != null) {
			int estadoSeleccionado = criterioEstado.obtenerEstadoSeleccionado();
			int tam = estadosLecturas.size();
			for (int i = 0; i < tam; i++) {
				if (estadosLecturas.get(i).getEstadoEntero() == estadoSeleccionado) {
					posSeleccionada = i + 1;
					break;
				}
			}
		}
		selectorTipoLectura.setSelection(posSeleccionada);
	}

	/**
	 * Asigna la ruta que fue seleccionada previamente segun el criterio del
	 * filtro <b>FiltroLecturas</b> si el criterio es null, pone por defecto a
	 * todas;
	 */
	private void asignarRutaSeleccionada() {
		CriterioRuta criterioRuta = (CriterioRuta) filtroLecturas
				.obtenerCriterioDeFiltro(CriterioRuta.class);
		int posSeleccionada = 0;
		if (criterioRuta != null) {
			int rutaSeleccionada = criterioRuta.obtenerRutaSeleccionada();
			int tam = rutasUsuario.size();
			for (int i = 0; i < tam; i++) {
				if (rutasUsuario.get(i).Ruta == rutaSeleccionada) {
					posSeleccionada = i + 1;
					break;
				}
			}
		}
		selectorRuta.setSelection(posSeleccionada);
	}

	/**
	 * Asigna los select listeners a los combobox de filtros
	 */
	private void asignarSelectListeners() {
		selectorRuta.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				if (position == 0)// todas las rutas
				{
					criterioRuta = null;
				} else {
					String ruta = listaRutas.get(position);
					criterioRuta = new CriterioRuta(Integer.parseInt(ruta));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		selectorTipoLectura
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parentView,
							View selectedItemView, int position, long id) {
						if (position == 0)// todos los tipos de lectura
						{
							criterioEstado = null;
						} else {
							IEstadoLectura estado = estadosLecturas
									.get(position - 1);
							criterioEstado = new CriterioEstado(estado
									.getEstadoEntero());
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
	}

}