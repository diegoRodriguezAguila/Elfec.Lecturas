package com.elfec.lecturas.controlador.dialogos;

import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.elfec.lecturas.controlador.TomarLectura;
import com.elfec.lecturas.controlador.adaptadores.OrdenativoAdapter;
import com.elfec.lecturas.helpers.ui.Animador;
import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.Ordenativo;
import com.elfec.lecturas.modelo.OrdenativoLectura;
import com.lecturas.elfec.R;

public class DialogoAgregarOrdenativo extends AlertDialog {

	public DialogoAgregarOrdenativo(Context context, Lectura lectura) {
		super(context);
		this.context = context;
		this.lecturaActual = lectura;
		hashConvertirCodAPos = new SparseArray<Integer>();
		listaOrdenativos = (ArrayList<Ordenativo>) lecturaActual
				.obtenerOrdenativosNoUsadosLectura();
		adapter = new OrdenativoAdapter(context, R.layout.list_item_ordenativo,
				listaOrdenativos);
	}

	public DialogoAgregarOrdenativo(Context context, Lectura lectura,
			int tituloId, ArrayList<Ordenativo> listaOrdenativos) {
		super(context);
		this.context = context;
		this.lecturaActual = lectura;
		hashConvertirCodAPos = new SparseArray<Integer>();
		this.listaOrdenativos = listaOrdenativos;
		adapter = new OrdenativoAdapter(context, R.layout.list_item_ordenativo,
				listaOrdenativos);
		this.tituloId = tituloId;
	}

	private Context context;
	private Lectura lecturaActual;
	private ListView listViewOrdenativos;
	private boolean textoManual = true;
	private ArrayList<Ordenativo> listaOrdenativos;
	private SparseArray<Integer> hashConvertirCodAPos;
	private EditText txtCodOrd;
	private TextView lblInfoTip;
	private Button btnGuardar;
	private Button btnSalir;
	private RelativeLayout layoutProhibirAgregar;
	private OrdenativoAdapter adapter;
	private int tituloId = -1;
	private ArrayList<View.OnClickListener> guardarListeners;
	public OrdenativoLectura nuevoOrdLect;
	public View.OnClickListener onDialogExit;

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogo_agregar_ordenativo);
		txtCodOrd = (EditText) findViewById(R.id.txt_cod_obs);
		lblInfoTip = (TextView) findViewById(R.id.info_tip);
		listViewOrdenativos = (ListView) findViewById(R.id.list_view_ordenativos);
		btnGuardar = (Button) findViewById(R.id.btn_guardar_obs);
		btnSalir = (Button) findViewById(R.id.btn_salir_dialogo);
		if (!lecturaActual.sePuedeAgregarOrdenativos()) {
			prohibirAgregarOrdenativos();
		}
		setTitle((tituloId == -1 ? R.string.titulo_agregar_ordenativo
				: tituloId));
		ponerTextListener();
		asignarCierreTecladoConOk();
		listViewOrdenativos.setFastScrollEnabled(true);
		listViewOrdenativos.setAdapter(adapter);
		ponerItemClickListenerAO();
		llenarHash();
		setBotonGuardarListener();
		setBotonSalirListener();
	}

	/**
	 * Prohibe agregar ordenativos
	 */
	private void prohibirAgregarOrdenativos() {
		layoutProhibirAgregar = (RelativeLayout) findViewById(R.id.layout_prohibir_agregar);
		layoutProhibirAgregar.setVisibility(View.VISIBLE);
		layoutProhibirAgregar.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				return true;
			}
		});
	}

	@Override
	public void onBackPressed() {
		dismiss();// go back to the previous Activity
		if (onDialogExit != null) {
			onDialogExit.onClick(btnSalir);
		}
	}

	private void setBotonSalirListener() {
		btnSalir.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
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
			dismiss();
			((TomarLectura) context).asignarDatos();

		}
	}

	private void asignarCierreTecladoConOk() {
		txtCodOrd.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					InputMethodManager m = (InputMethodManager) context
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					m.hideSoftInputFromWindow(txtCodOrd.getWindowToken(), 0);
				}
				return false;
			}
		});
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
				view.setBackgroundColor(Color.parseColor("#EBEBEB"));
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
						adapter.setSeleccionado(pos);
						if (textoManual) {
							listViewOrdenativos.smoothScrollToPosition(pos);
							Ordenativo ord = listaOrdenativos.get(pos);
							lblInfoTip.setText(ord.Descripcion);
							if (animarCierre != null) {
								txtCodOrd.removeCallbacks(animarCierre);
							}
							Animador.expand(lblInfoTip);
							esconderVistaLblInfoTip();
						} else {
							lblInfoTip.setVisibility(View.GONE);
						}
					} else {
						adapter.setSeleccionado(-1);
						lblInfoTip.setVisibility(View.GONE);
					}
				} else {
					adapter.setSeleccionado(-1);
					lblInfoTip.setVisibility(View.GONE);
				}
			}

			private Runnable animarCierre;

			private void esconderVistaLblInfoTip() {
				animarCierre = new Runnable() {
					@Override
					public void run() {
						Animador.collapse(lblInfoTip);
					}
				};
				txtCodOrd.postDelayed(animarCierre, 5000);
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
