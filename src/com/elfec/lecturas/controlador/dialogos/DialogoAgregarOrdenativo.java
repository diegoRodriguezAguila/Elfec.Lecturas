package com.elfec.lecturas.controlador.dialogos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.elfec.lecturas.controlador.adaptadores.OrdenativoAdapter;
import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.Ordenativo;
import com.elfec.lecturas.modelo.OrdenativoLectura;
import com.elfec.lecturas.modelo.eventos.OnObservacionGuardadaListener;
import com.lecturas.elfec.R;

public class DialogoAgregarOrdenativo {

	private AlertDialog mDialog;
	private View rootView;

	private OnObservacionGuardadaListener mListener;

	private Context context;
	private Lectura lecturaActual;
	private ListView listViewOrdenativos;
	private boolean textoManual = true;
	private List<Ordenativo> listaOrdenativos;
	private SparseArray<Integer> hashConvertirCodAPos;
	private EditText txtCodOrd;
	private Button btnGuardar;
	private OrdenativoAdapter adapter;
	private int tituloId;
	private ArrayList<View.OnClickListener> guardarListeners;
	public OrdenativoLectura nuevoOrdLect;

	public DialogoAgregarOrdenativo(Context context, Lectura lectura,
			OnObservacionGuardadaListener listener) {
		this(context, lectura, -1, lectura.obtenerOrdenativosNoUsadosLectura(),
				listener);
	}

	public DialogoAgregarOrdenativo(Context context, Lectura lectura,
			int tituloId, List<Ordenativo> listaOrdenativos,
			OnObservacionGuardadaListener listener) {
		this.context = context;
		this.lecturaActual = lectura;
		this.mListener = listener;
		hashConvertirCodAPos = new SparseArray<Integer>();
		this.listaOrdenativos = listaOrdenativos;
		adapter = new OrdenativoAdapter(context, R.layout.list_item_ordenativo,
				listaOrdenativos);
		this.tituloId = tituloId;
		inicializarVistas();
	}

	/**
	 * Asigna un dismiss listener
	 * 
	 * @param listener
	 */
	public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
		mDialog.setOnDismissListener(listener);
	}

	/**
	 * Asigna un dismiss listener
	 * 
	 * @param listener
	 */
	public void setOnCancelListener(DialogInterface.OnCancelListener listener) {
		mDialog.setOnCancelListener(listener);
	}

	/**
	 * Asigna el icono drwabl
	 * 
	 * @param iconDrawbleId
	 */
	public void setIcon(int iconDrawbleId) {
		mDialog.setIcon(R.drawable.impedir_lectura);
	}

	@SuppressLint("InflateParams")
	protected void inicializarVistas() {
		rootView = LayoutInflater.from(context).inflate(
				R.layout.dialogo_agregar_ordenativo, null, false);
		mDialog = new AlertDialog.Builder(context)
				.setView(rootView)
				.setTitle(
						(tituloId == -1 ? R.string.titulo_agregar_ordenativo
								: tituloId))
				.setNegativeButton(R.string.salida_btn, null).create();
		txtCodOrd = (EditText) rootView.findViewById(R.id.txt_cod_obs);
		listViewOrdenativos = (ListView) rootView
				.findViewById(R.id.list_view_ordenativos);
		btnGuardar = (Button) rootView.findViewById(R.id.btn_guardar_obs);
		if (!lecturaActual.sePuedeAgregarOrdenativos()) {
			prohibirAgregarOrdenativos();
		}
		ponerTextListener();
		listViewOrdenativos.setFastScrollEnabled(true);
		listViewOrdenativos.setAdapter(adapter);
		ponerItemClickListenerAO();
		llenarHash();
		setBotonGuardarListener();
	}

	public void show() {
		mDialog.show();
	}

	/**
	 * Prohibe agregar ordenativos
	 */
	private void prohibirAgregarOrdenativos() {
		rootView.findViewById(R.id.layout_prohibir_agregar).setVisibility(
				View.VISIBLE);
		rootView.findViewById(R.id.layout_campos_agregar_ord).setVisibility(
				View.GONE);
		rootView.findViewById(R.id.separator).setVisibility(View.GONE);
		rootView.findViewById(R.id.list_view_ordenativos).setVisibility(
				View.GONE);
	}

	private void setBotonGuardarListener() {
		btnGuardar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				guardarObservacion();
				if (guardarListeners != null) {
					for (View.OnClickListener listener : guardarListeners) {
						listener.onClick(view);
					}
				}
			}
		});
	}

	public void addOnGuardarClickListener(View.OnClickListener listener) {
		if (guardarListeners == null)
			guardarListeners = new ArrayList<View.OnClickListener>();
		guardarListeners.add(listener);
	}

	private void guardarObservacion() {
		int pos = adapter.getSeleccionado();
		if (pos != -1)// si es que se selecciono alguno
		{
			Ordenativo ordSelec = listaOrdenativos.get(pos);
			Date fechaActual = new Date();
			nuevoOrdLect = new OrdenativoLectura(ordSelec, lecturaActual,
					fechaActual);
			nuevoOrdLect.guardarYEnviarPor3G();
			lecturaActual.ObservacionLectura = ordSelec.Codigo;
			lecturaActual.save();
			mDialog.dismiss();
			if (mListener != null)
				mListener.onObservacionGuardada(nuevoOrdLect);

		}
	}

	public void llenarHash() {
		for (int i = 0; i < listaOrdenativos.size(); i++) {
			Ordenativo ord = listaOrdenativos.get(i);
			hashConvertirCodAPos.put(ord.Codigo, i);
		}
	}

	private void ponerItemClickListenerAO() {
		listViewOrdenativos.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				adapter.setSeleccionado(pos);
				int codigoObs = listaOrdenativos.get(pos).Codigo;
				String codString = "" + codigoObs;
				textoManual = false;
				txtCodOrd.setText(codString);
				textoManual = true;
				txtCodOrd.setSelection(codString.length());
			}
		});
	}

	private void ponerTextListener() {
		txtCodOrd.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (!s.toString().isEmpty()) {
					int cod = (Integer.parseInt(s.toString()));
					Integer pos = hashConvertirCodAPos.get(cod);
					if (pos != null) {
						if (textoManual) {
							adapter.setSeleccionado(pos);
							listViewOrdenativos.smoothScrollToPosition(pos);
						}
					} else {
						adapter.setSeleccionado(-1);
					}
				} else {
					adapter.setSeleccionado(-1);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int berfore,
					int count) {
			}
		});
	}

}
