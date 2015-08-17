package com.elfec.lecturas.controlador;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elfec.lecturas.controlador.accionesycustomizaciones.ActivitySwipeDetector;
import com.elfec.lecturas.controlador.accionesycustomizaciones.CustomDialog;
import com.elfec.lecturas.controlador.accionesycustomizaciones.ISwipeListener;
import com.elfec.lecturas.controlador.adaptadores.NavegacionAdapter;
import com.elfec.lecturas.controlador.adaptadores.NavegacionAdapter.NavegacionListener;
import com.elfec.lecturas.controlador.adaptadores.NavegacionAdapter.Predicado;
import com.elfec.lecturas.controlador.dialogos.DialogoAgregarOrdenativo;
import com.elfec.lecturas.controlador.dialogos.DialogoConfirmacionImpresion;
import com.elfec.lecturas.controlador.dialogos.DialogoFiltrarLecturas;
import com.elfec.lecturas.controlador.dialogos.DialogoMedidorEntreLineas;
import com.elfec.lecturas.controlador.dialogos.DialogoPotencia;
import com.elfec.lecturas.controlador.dialogos.DialogoRecordatorio;
import com.elfec.lecturas.controlador.dialogos.DialogoSeleccionImpresora;
import com.elfec.lecturas.controlador.dialogos.DialogoVerOrdenativos;
import com.elfec.lecturas.controlador.filtroslecturas.CriterioEstado;
import com.elfec.lecturas.controlador.filtroslecturas.CriterioRuta;
import com.elfec.lecturas.controlador.filtroslecturas.FiltroLecturas;
import com.elfec.lecturas.helpers.GestionadorImportesYConceptos;
import com.elfec.lecturas.helpers.ManejadorBackupTexto;
import com.elfec.lecturas.helpers.ManejadorConexionRemota;
import com.elfec.lecturas.helpers.ManejadorDeCamara;
import com.elfec.lecturas.helpers.ManejadorEstadosHW;
import com.elfec.lecturas.helpers.ManejadorImpresora;
import com.elfec.lecturas.helpers.ManejadorSonido;
import com.elfec.lecturas.helpers.ManejadorUbicacion;
import com.elfec.lecturas.helpers.VariablesDeEntorno;
import com.elfec.lecturas.helpers.VariablesDeSesion;
import com.elfec.lecturas.helpers.excepciones.ImpresoraPredefinidaNoAsignadaExcepcion;
import com.elfec.lecturas.helpers.ui.ClicksBotonesHelper;
import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.MedidorEntreLineas;
import com.elfec.lecturas.modelo.Ordenativo;
import com.elfec.lecturas.modelo.OrdenativoLectura;
import com.elfec.lecturas.modelo.Potencia;
import com.elfec.lecturas.modelo.Usuario;
import com.elfec.lecturas.modelo.avisocobranza.AvisoCobranza;
import com.elfec.lecturas.modelo.preferencias_ui.AdminUI;
import com.elfec.lecturas.modelo.preferencias_ui.OpcionesPreferenciasUI;
import com.elfec.lecturas.modelo.seguridad.AdministradorSeguridad;
import com.elfec.lecturas.modelo.seguridad.Restricciones;
import com.elfec.lecturas.modelo.validaciones.IValidacionLectura;
import com.lecturas.elfec.R;

public class TomarLectura extends Activity implements ISwipeListener,
		NavegacionListener<Lectura> {

	public static final String ARG_ID_LECTURA = "IdLecturaSeleccionada";
	public static final int LISTA_LECTURAS = 1;
	public static final int BUSCAR_LECTURA = 2;

	private RelativeLayout lecturaLayout;
	private View layoutCargaListaLecturas;

	private TextView lblNombre;
	private TextView lblDireccion;
	private TextView lblNus;
	private TextView lblTarifa;
	private TextView lblCuenta;
	private TextView lblOrden;
	private TextView lblNumMedidor;
	public TextView lblEstadoLectura;
	private TextView lblNumDigitos;
	private TextView lblObservacion;
	private TextView lblObsNum;
	public TextView lblNuevaLectura;
	public EditText txtLecturaNueva;
	public TextView lblLecturaActual;
	public TextView lblFechaLectura;
	public ImageButton btnAgergarOrdenativo;
	public Button btnConfirmarLectura;
	public Button btnPostergarLectura;
	public Button btnReintentarLectura;
	public ImageButton btnRecordatorios;

	public MenuItem menuEstimarLectura;
	public MenuItem menuImpedirLectura;
	public MenuItem menuVerPotencia;
	public MenuItem menuReImprimir;
	public MenuItem menuModificarLectura;
	public MenuItem menuTomarFoto;
	private MenuItem menuRecordatorioLector;
	private MenuItem menuFiltrarLecturas;

	private NavegacionAdapter<Lectura> navegacionAdapter;
	private FiltroLecturas filtroLecturas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tomar_lectura);
		new Thread(new Runnable() {
			@Override
			public void run() {
				navegacionAdapter = new NavegacionAdapter<Lectura>(
						TomarLectura.this);
				filtroLecturas = new FiltroLecturas();
				inicializarCampos();
				asignarLista();
				btnPrimeroClick(null);
				ponerClickListenerAObservacion();
				asignarTouchListenerATxtLecturaNueva();
				crearSwipeListener();
				inicializarVariablesDeEntorno();
			}
		}).start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		verificarServiciosDeHW();
	}

	/**
	 * Inicializa los campos
	 */
	private void inicializarCampos() {
		lecturaLayout = (RelativeLayout) findViewById(R.id.datos_de_lectura);
		layoutCargaListaLecturas = findViewById(R.id.layout_carga_lista_lecturas);

		lblNombre = (TextView) findViewById(R.id.lbl_nombre_cliente);
		lblDireccion = (TextView) findViewById(R.id.lbl_direccion_cliente);
		lblNus = (TextView) findViewById(R.id.lbl_nus_cliente);
		lblTarifa = (TextView) findViewById(R.id.lbl_tarifa_cliente);
		lblCuenta = (TextView) findViewById(R.id.lbl_cuenta_cliente);
		lblOrden = (TextView) findViewById(R.id.lbl_orden_cliente);
		lblNumMedidor = (TextView) findViewById(R.id.lbl_numero_medidor);
		lblEstadoLectura = (TextView) findViewById(R.id.lbl_estado_lectura);
		lblNumDigitos = (TextView) findViewById(R.id.lbl_info_digitos_medidor);
		lblObservacion = (TextView) findViewById(R.id.lbl_obs_medidor);
		lblObsNum = (TextView) findViewById(R.id.lbl_obs);
		txtLecturaNueva = (EditText) findViewById(R.id.txt_nueva_lectura);
		lblNuevaLectura = (TextView) findViewById(R.id.lbl_nueva_lectura);
		lblLecturaActual = (TextView) findViewById(R.id.lbl_lectura);
		lblFechaLectura = (TextView) findViewById(R.id.lbl_fecha_lectura);
		btnConfirmarLectura = (Button) findViewById(R.id.btn_confirmar_lectura);
		btnPostergarLectura = (Button) findViewById(R.id.btn_postergar_lectura);
		btnReintentarLectura = (Button) findViewById(R.id.btn_reintentar_lectura);
		btnAgergarOrdenativo = (ImageButton) findViewById(R.id.btn_agregar_ordenativo);
		btnRecordatorios = (ImageButton) findViewById(R.id.btn_recordatorio);
	}

	/**
	 * Inicializa las variables de entorno
	 */
	private void inicializarVariablesDeEntorno() {
		VariablesDeEntorno.limiteImpresiones = AdministradorSeguridad
				.obtenerAdministradorSeguridad(
						VariablesDeSesion.getPerfilUsuario())
				.obtenerRestriccion(Restricciones.MAX_IMPRESIONES_LECTURA);
		VariablesDeEntorno.limiteModificacionesLectura = AdministradorSeguridad
				.obtenerAdministradorSeguridad(
						VariablesDeSesion.getPerfilUsuario())
				.obtenerRestriccion(Restricciones.MAX_MODIFICAR_LECTURA);
	}

	/**
	 * Verifica que el 3G y los servicios de ubicación esten activados para
	 * realizar lecturas, siempre y cuando, sea obligatorio para el usuario
	 * tenerlos encendidos
	 */
	public void verificarServiciosDeHW() {
		if (!ManejadorEstadosHW.bateriaEstaEnNivelCritico(this)) {
			if (Usuario.obtenerUsuario(VariablesDeSesion.getUsuarioLogeado()).Requiere3G == 1) {
				ManejadorEstadosHW.encenderDatosMoviles3G(this);
			}
			if (Usuario.obtenerUsuario(VariablesDeSesion.getUsuarioLogeado()).RequiereGPS == 1) {
				ManejadorUbicacion.verificarServiciosEstanActivos(this);
			}
		} else {
			if (!ManejadorEstadosHW.datosMoviles3GEstanDesconectados(this)
					|| !ManejadorEstadosHW.servicioGPSEstaDeshabilitado(this))
				Toast.makeText(this, R.string.bateria_nivel_critico,
						Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Asigna la lista de lecturas sobre la cual se trabajará según los filtros
	 * establecidos. Si el resultado de los filtros es una lista vacia mostrará
	 * un mensaje al usuario y se veran todas las lecturas sin filtros.
	 */
	public void asignarLista() {
		boolean criteriosCambiaron = filtroLecturas.criteriosCambiaron();
		if (criteriosCambiaron)
			mostrarCargaLectura();
		List<Lectura> listaLecturas = filtroLecturas.obtenerListaDeLecturas();
		if (listaLecturas.size() == 0) {
			mostrarCargaLectura();
			filtroLecturas.resetearCriteriosFiltro();
			criteriosCambiaron = true;
			mostrarDialogoAdvertenciaFiltros();
		}
		if (criteriosCambiaron)
			navegacionAdapter.setLista(filtroLecturas.obtenerListaDeLecturas());
	}

	/**
	 * Muestra un dialogo de advertencia de filtros
	 */
	private void mostrarDialogoAdvertenciaFiltros() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final CustomDialog dialogo = new CustomDialog(TomarLectura.this);
				dialogo.setTitle(R.string.titulo_mensajes_advertencia);
				dialogo.setIcon(R.drawable.warning);
				dialogo.setMessage(R.string.advertencia_filtros);
				dialogo.setPositiveButton(R.string.btn_ok,
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialogo.dismiss();
							}
						});
				dialogo.setCancelable(false);
				dialogo.show();
			}
		});
	}

	/**
	 * Se utiliza para que no se envien multiples solicitudes de satelite para
	 * una misma lectura si es que ya hay una pendiente
	 */
	private int contadorSatelite = 0;

	/**
	 * Pone el listener que se encarga de que se intente capturar una ubicación
	 * el momento en que se hace focus en el texto de la lectura
	 */
	private void asignarTouchListenerATxtLecturaNueva() {
		txtLecturaNueva.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent ev) {
				if (txtLecturaNueva.hasFocus() && contadorSatelite == 0) {
					Lectura lecturaActual = navegacionAdapter.getActual();
					contadorSatelite++;
					ManejadorUbicacion.obtenerUbicacionActual(
							TomarLectura.this, lecturaActual);
				}
				return false;
			}
		});
	}

	/**
	 * Asigna los datos correspondientes de la lectura actual a la vista
	 */
	public void asignarDatos() {
		final Lectura lecturaActual = navegacionAdapter.getActual();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				lblNombre.setText(lecturaActual.NombreCliente);
				lblDireccion.setText(lecturaActual.DireccionSuministro);
				lblNus.setText("" + lecturaActual.Suministro);
				lblTarifa.setText(lecturaActual.SiglaCategoria);
				lblCuenta.setText(lecturaActual.obtenerCuentaConFormato());
				lblOrden.setText(""
						+ (navegacionAdapter.getPosicionActual() + 1));
				lblNumMedidor.setText("" + lecturaActual.NumeroMedidor);
				lecturaActual.mostrarLecturaEnTomarLectura(TomarLectura.this);
				lecturaActual.mostrarMenuEnTomarLectura(TomarLectura.this);
				lblNumDigitos.setText("El medidor es de "
						+ lecturaActual.NumDigitosMedidor + " dígitos");
				txtLecturaNueva
						.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
								lecturaActual.NumDigitosMedidor) });
				lblObservacion
						.setText((lecturaActual.ObservacionLectura == 0) ? "Ninguna"
								: "" + lecturaActual.ObservacionLectura);
				btnRecordatorios.setEnabled(lecturaActual.tieneRecordatorio());
				txtLecturaNueva.setText("");
			}
		});
	}

	/**
	 * Muestra la lectura y esconde la carga
	 */
	private void mostrarLectura() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (lecturaLayout.getVisibility() == View.GONE) {
					if (menuRecordatorioLector != null)
						menuRecordatorioLector.setVisible(true);
					if (menuFiltrarLecturas != null)
						menuFiltrarLecturas.setVisible(true);
					lecturaLayout.setVisibility(View.VISIBLE);
					layoutCargaListaLecturas.setVisibility(View.GONE);

				}
			}
		});
	}

	/**
	 * Muestra la carga y esconde la lectura
	 */
	private void mostrarCargaLectura() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (layoutCargaListaLecturas.getVisibility() == View.GONE) {
					if (menuRecordatorioLector != null)
						menuRecordatorioLector.setVisible(false);
					if (menuFiltrarLecturas != null)
						menuFiltrarLecturas.setVisible(false);
					layoutCargaListaLecturas.setVisibility(View.VISIBLE);
					lecturaLayout.setVisibility(View.GONE);
				}
			}
		});
	}

	private void ponerClickListenerAObservacion() {
		OnClickListener clickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				lblObservacion.setTextColor(getResources().getColor(
						R.color.blue_peter_river));
				lblObsNum.setTextColor(getResources().getColor(
						R.color.blue_peter_river));
				abrirDialogoVerOrdenativos();
			}
		};
		lblObservacion.setOnClickListener(clickListener);
		lblObsNum.setOnClickListener(clickListener);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tomar_lectura, menu);
		menuEstimarLectura = menu.findItem(R.id.menu_item_estimar_lectura);
		menuImpedirLectura = menu.findItem(R.id.menu_item_impedir_lectura);
		menuVerPotencia = menu.findItem(R.id.menu_item_visualizar_potencia);
		menuReImprimir = menu.findItem(R.id.menu_item_re_imprimir);
		menuModificarLectura = menu.findItem(R.id.menu_item_modificar_lectura);
		menuTomarFoto = menu.findItem(R.id.menu_item_tomar_foto);
		menuRecordatorioLector = menu
				.findItem(R.id.menu_item_recordatorio_lector);
		menuFiltrarLecturas = menu.findItem(R.id.menu_item_filtrar_lecturas);
		if (navegacionAdapter.getLista() != null) {
			menuRecordatorioLector.setVisible(true);
			menuFiltrarLecturas.setVisible(true);
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		finish();// go back to the previous Activity
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}

	public void btnInicioClick(View view) {
		if (ClicksBotonesHelper.sePuedeClickearBoton()) {
			finish();
			overridePendingTransition(R.anim.slide_right_in,
					R.anim.slide_right_out);
		}
	}

	private Lectura lecturaActual;

	public void btnConfirmarLecturaClick(View view) {
		if (ClicksBotonesHelper.sePuedeClickearBoton()) {
			if (!txtLecturaNueva.getText().toString().isEmpty()) {
				lecturaActual = navegacionAdapter.getActual();
				int lectura = Integer.parseInt(txtLecturaNueva.getText()
						.toString());
				boolean procederConGuardado = lecturaActual.LecturaNueva == lectura;
				lecturaActual.LecturaNueva = lectura;
				IValidacionLectura resultadoValidacion = lecturaActual
						.validarLectura();
				if (resultadoValidacion.esAdvertencia()) {
					mostrarAdvertencia(resultadoValidacion, procederConGuardado);
				} else {
					procederConProcesoDeGuardado();
				}
			}
		}
	}

	/**
	 * Invoca a los metodos y validaciones necesarias para guardar la lectura
	 * realizada
	 */
	public void guardarLectura() {
		Date fechaActual = new Date();
		int lecturaNueva = Integer.parseInt(txtLecturaNueva.getText()
				.toString());
		lecturaActual.leerLectura(lecturaNueva, fechaActual);
		if (lecturaActual.TagCalculaPotencia == 1
				&& lecturaActual.LeePotencia == 0) {
			estimarPotencia(lecturaActual);
		}
		if (lecturaActual.TagImpresionAviso == 1) {
			GestionadorImportesYConceptos.agregarConceptos(lecturaActual);
		}
		if (ordLec != null) {
			ordLec.guardarYEnviarPor3G();
			lecturaActual.ObservacionLectura = ordLec.Ordenativo.Codigo;
			// Caso Lectura Adelantada: hubo volteo y consumo es elevado
			if (lecturaActual.ObservacionLectura == 85
					&& lecturaActual.consumoFacturadoAsignadoElevado()) {
				Ordenativo ord = Ordenativo.obtenerOrdenativoPorCodigo(68);
				OrdenativoLectura ordLecAdelantada = new OrdenativoLectura(ord,
						lecturaActual, new Date());
				ordLecAdelantada.guardarYEnviarPor3G();
				lecturaActual.ObservacionLectura = ordLecAdelantada.Ordenativo.Codigo;
			}
			mostrarDialogoFotoOrdenativo(ordLec);
			ordLec = null;
		}
		lecturaActual.save();
		ManejadorBackupTexto.guardarBackupModelo(lecturaActual);
		imprimirLectura(lecturaActual);
		txtLecturaNueva.setText("");
		// no se guarda gps, se guarda directamente la lectura
		if (VariablesDeEntorno.tipoGuardadoUbicacion == 0)
			ManejadorConexionRemota.guardarLectura(lecturaActual);
		asignarDatos();
		agregarOrdenativosLecturaCiclico();
	}

	/**
	 * Muestra el dialogo de agregar ordenativos mientras se ponga aceptar y no
	 * se salgan de el
	 */
	private void agregarOrdenativosLecturaCiclico() {
		DialogoAgregarOrdenativo pd = new DialogoAgregarOrdenativo(this,
				lecturaActual);
		pd.addOnGuardarClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				agregarOrdenativosLecturaCiclico();
			}
		});
		pd.onDialogExit = new OnClickListener() {
			@Override
			public void onClick(View v) {
				actualizarLecturasYFiltro(true);
			}
		};
		pd.show();
	}

	// ------------------------------------------ actualiza Lecturas Y Filtro
	/**
	 * Actualiza las lecturas segun los filtros en caso de existir
	 */
	public void actualizarLecturasYFiltro(boolean irSiguiente) {
		if (irSiguiente)
			btnSiguienteClick(null);
		if (filtroLecturas.existenCriterios()) {
			asignarLista();
		}
	}

	// ------------------------------------------ DIALOGO RESULTADO VALIDACION
	// ----------------------------------------
	private OrdenativoLectura ordLec = null;

	private void mostrarAdvertencia(
			final IValidacionLectura resultadoValidacion,
			final boolean procederConGuardado) {
		ManejadorSonido.reproducirBeep(this);
		final CustomDialog dialog = new CustomDialog(this);
		dialog.setMessage(resultadoValidacion.obtenerMensaje());
		dialog.setTitle(R.string.titulo_mensajes_advertencia);
		dialog.setCancelable(false);
		dialog.setIcon(R.drawable.warning);
		dialog.setPositiveButton(R.string.btn_ok, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (procederConGuardado) {
					Ordenativo ord = resultadoValidacion.obtenerOrdenativo();
					ordLec = new OrdenativoLectura(ord, lecturaActual,
							new Date());
					procederConProcesoDeGuardado();
				}
			}
		});
		dialog.show();
	}

	// ------------------------------------------ DIALOGO POTENCIA
	// ----------------------------------------
	public void procederConProcesoDeGuardado() {
		// si lee potencia o reactiva
		if (lecturaActual.LeePotencia == 1 || lecturaActual.LeeReactiva == 1) {
			DialogoPotencia dialog = new DialogoPotencia(this, lecturaActual,
					false);
			dialog.show();
		} else {
			guardarLectura();
		}
	}

	// --------------------------- BARRA DE HERRAMIENTAS INFERIOR
	// ------------------------------------
	public void btnBuscarClick(View view) {
		if (ClicksBotonesHelper.sePuedeClickearBoton()) {
			Intent intent = new Intent(this, BuscarLectura.class);
			intent.putExtra("RutaActual", lblCuenta.getText().subSequence(0, 6)
					.toString());
			startActivityForResult(intent, BUSCAR_LECTURA);
			overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
		}
	}

	public void btnListaLecturasClick(View view) {
		if (ClicksBotonesHelper.sePuedeClickearBoton()) {
			Intent intent = new Intent(this, ListaLecturas.class);
			startActivityForResult(intent, LISTA_LECTURAS);
			overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
		}
	}

	public void btnAnteriorClick(View view) {
		navegacionAdapter
				.setPosicion(navegacionAdapter.getPosicionActual() - 1);
	}

	private Predicado<Lectura> predicadoLectura = new Predicado<Lectura>() {
		@Override
		public boolean evaluar(Lectura lect) {
			return lect.getEstadoLectura().getEstadoEntero() == 0;
		}
	};

	public void btnSiguienteClick(View view) {
		navegacionAdapter
				.setPosicion(navegacionAdapter.getPosicionActual() + 1);
	}

	public void btnPrimeroClick(View view) {
		int pos = navegacionAdapter.buscar(predicadoLectura);
		navegacionAdapter.setPosicion(pos == -1 ? 0 : pos);
	}

	public void btnUltimoClick(View view) {
		int pos = navegacionAdapter.buscarAlReves(predicadoLectura);
		navegacionAdapter.setPosicion(pos == -1 ? navegacionAdapter.getLista()
				.size() : pos);
	}

	// -------------------------- BOTONES POSTERGAR Y
	// REINTENTAR-------------------------------------
	public void btnPostergarLecturaClick(View view) {
		if (ClicksBotonesHelper.sePuedeClickearBoton()) {
			final CustomDialog dialog = new CustomDialog(this);
			dialog.setMessage(R.string.postergar_mensaje);
			dialog.setTitle(R.string.titulo_mensajes_confirmar);
			dialog.setPositiveButton(R.string.btn_ok,
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Lectura lecturaActual = navegacionAdapter
									.getActual();
							lecturaActual.setEstadoLectura(3);
							lecturaActual.save();
							asignarDatos();
							dialog.dismiss();
							actualizarLecturasYFiltro(true);
						}
					});
			dialog.setNegativeButton(null);
			dialog.show();
		}
	}

	/**
	 * Muestra un dialogo de confirmacón para poner la lectura en estado de
	 * reintentar
	 * 
	 * @param view
	 */
	public void btnReintentarLecturaClick(View view) {
		if (ClicksBotonesHelper.sePuedeClickearBoton()) {
			final CustomDialog dialog = new CustomDialog(this);
			dialog.setMessage(R.string.reintentar_mensaje);
			dialog.setTitle(R.string.titulo_mensajes_confirmar);
			dialog.setPositiveButton(R.string.btn_ok,
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Lectura lecturaActual = navegacionAdapter
									.getActual();
							DateFormat df = new SimpleDateFormat("HH:mm",
									Locale.getDefault());
							Date fechaLec = new Date();
							lecturaActual.FechaLecturaActual = fechaLec;
							lecturaActual.HoraLectura = df.format(fechaLec);
							lecturaActual.setEstadoLectura(4);
							Ordenativo ord = Ordenativo
									.obtenerOrdenativoLecturaReintentar();
							OrdenativoLectura ordLect = new OrdenativoLectura(
									ord, lecturaActual, new Date());
							lecturaActual.ObservacionLectura = ord.Codigo;// le
																			// asigna
																			// el
																			// codigo
																			// de
																			// ordenativo
																			// lectura
																			// estimada
							lecturaActual.UsuarioAuditoria = VariablesDeSesion
									.getUsuarioLogeado();
							lecturaActual.save();
							ordLect.guardarYEnviarPor3G();
							asignarDatos();
							dialog.dismiss();
							actualizarLecturasYFiltro(true);
						}
					});
			dialog.setNegativeButton(null);
			dialog.show();
		}
	}

	public void btnRecordatorioClick(View view) {
		if (ClicksBotonesHelper.sePuedeClickearBoton()) {
			mostrarDialogoRecordatorioLector();
		}
	}

	// -------------------------- DIALOGO AGREGAR
	// ORDENATIVO-------------------------------------

	public void btnAgregarOrdenativoClick(View view) {
		if (ClicksBotonesHelper.sePuedeClickearBoton()) {
			DialogoAgregarOrdenativo pd = new DialogoAgregarOrdenativo(
					TomarLectura.this, navegacionAdapter.getActual());
			pd.show();
		}
	}

	// -------------------------- DIALOGO VER ORDENATIVOS
	// LECTURA-------------------------------------

	private void abrirDialogoVerOrdenativos() {
		DialogoVerOrdenativos dvo = new DialogoVerOrdenativos(
				TomarLectura.this, navegacionAdapter.getActual());
		dvo.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				lblObservacion.setTextColor(getResources().getColor(
						android.R.color.black));
				lblObsNum.setTextColor(getResources().getColor(
						android.R.color.black));
			}
		});
		dvo.show();
	}

	// ----------------------------- OPCIONES DE MENU
	// -----------------------------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (ClicksBotonesHelper.sePuedeClickearBoton()) {
			switch (item.getItemId()) {
			case R.id.menu_item_estimar_lectura:
				mostrarDialogoEstimarLectura();
				return true;
			case R.id.menu_item_impedir_lectura:
				mostrarDialogoImpedirLectura();
				return true;
			case R.id.menu_item_medidor_entre_lineas:
				mostrarDialogoMedidorEntreLineas();
				return true;
			case R.id.menu_item_modificar_lectura:
				mostrarDialogoModificarLectura();
				return true;
			case R.id.menu_item_re_imprimir:
				mostrarDialogoReImprimir();
				return true;
			case R.id.menu_item_filtrar_lecturas:
				mostrarDialogoFiltrarLecturas();
				return true;
			case R.id.menu_item_recordatorio_lector:
				mostrarDialogoRecordatorioLector();
				return true;
			case R.id.menu_item_visualizar_potencia:
				mostrarDialogoVerPotencia();
				return true;
			case R.id.menu_item_tomar_foto:
				mostrarDialogoFotoLectura();
				return true;
			default:
				return true;
			}
		}
		return true;
	}

	// ----------------------------- DIALOGO FILTRAR LECTURAS
	// -----------------------------------------------

	/**
	 * Abre un dialogo para filtrar las lecturas que se estan viendo actualmente
	 */
	private void mostrarDialogoFiltrarLecturas() {
		final DialogoFiltrarLecturas dialogo = new DialogoFiltrarLecturas(this,
				filtroLecturas);
		dialogo.setPositiveButton(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (dialogo.criterioEstado != null)
							filtroLecturas
									.agregarCriterioAFiltro(dialogo.criterioEstado);
						else
							filtroLecturas
									.quitarCriterioDeFiltro(CriterioEstado.class);

						if (dialogo.criterioRuta != null)
							filtroLecturas
									.agregarCriterioAFiltro(dialogo.criterioRuta);
						else
							filtroLecturas
									.quitarCriterioDeFiltro(CriterioRuta.class);
						asignarLista();
					}
				}).start();
				dialogo.dismiss();
			}
		});
		dialogo.setNegativeButton(null);
		dialogo.show();
	}

	// ----------------------------- DIALOGO MEDIDOR ENTRE LINEAS
	// -----------------------------------------------
	private void mostrarDialogoMedidorEntreLineas() {
		final DialogoMedidorEntreLineas dialogo = new DialogoMedidorEntreLineas(
				this);
		dialogo.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (dialogo.medEntreLineas != null)
					mostrarDialogoFotoEntreLineas(dialogo.medEntreLineas);
			}
		});
		dialogo.show();
	}

	// ----------------------------- DIALOGO RECORDATORIOS LECTOR
	// -----------------------------------------------

	/**
	 * Muestra un dialogo para agregar o editar el recordatorio de la lectura
	 * actual
	 */
	private void mostrarDialogoRecordatorioLector() {
		DialogoRecordatorio dialogo = new DialogoRecordatorio(this,
				navegacionAdapter.getActual());
		dialogo.show();
	}

	// ----------------------------- DIALOGO MODIFICAR LECTURA
	// -----------------------------------------------
	/**
	 * Abre el dialogo de confirmación para modificar una lectura
	 */
	private void mostrarDialogoModificarLectura() {
		final Lectura lectura = navegacionAdapter.getActual();
		// verifica el limite de modificaciones
		if (lectura.NumModificaciones < VariablesDeEntorno.limiteModificacionesLectura) {
			final CustomDialog dialog = new CustomDialog(this);
			dialog.setMessage(R.string.modificar_lectura_mensaje);
			dialog.setTitle(R.string.titulo_mensajes_advertencia);
			dialog.setIcon(R.drawable.warning);
			dialog.setPositiveButton(R.string.btn_ok,
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							lectura.resetearLectura();
							asignarDatos();
							dialog.dismiss();
							actualizarLecturasYFiltro(false);
						}
					});
			dialog.setNegativeButton(null);
			dialog.show();
		} else {
			CustomDialog dialog = new CustomDialog(this);
			dialog.setMessage(R.string.limite_modificaciones_mensaje);
			dialog.setTitle(R.string.titulo_limite_modificaciones);
			dialog.setIcon(R.drawable.error);
			dialog.setPositiveButton(null);
			dialog.show();
		}
	}

	// ----------------------------- DIALOGO REIMPRIMIR AVISO DE COBRANZA
	// -----------------------------------------------
	/**
	 * Muestra el dialogo en el que se pregunta al usuario si desea re imprimir
	 * el aviso de cobro
	 * 
	 */
	private void mostrarDialogoReImprimir() {
		final CustomDialog dialog = new CustomDialog(this);
		dialog.setMessage(R.string.reimprimir_mensaje);
		dialog.setIcon(getResources().getDrawable(R.drawable.imprimir));
		dialog.setTitle(R.string.titulo_reimprimir);
		dialog.setPositiveButton(R.string.btn_ok, new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				Lectura lectura = navegacionAdapter.getActual();
				imprimirLectura(lectura);
			}
		});
		dialog.setNegativeButton(null);
		dialog.show();
	}

	// ----------------------------- Estimar Lectura
	// -----------------------------------------------
	/**
	 * Muestra el dialogo de confirmación de estimación de la lectura actual
	 */
	private void mostrarDialogoEstimarLectura() {
		final CustomDialog dialog = new CustomDialog(this);
		dialog.setMessage(R.string.estimar_mensaje);
		dialog.setIcon(getResources().getDrawable(R.drawable.estimar_lectura));
		dialog.setTitle(R.string.titulo_mensajes_confirmar);
		dialog.setPositiveButton(R.string.btn_ok, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				estimarLectura();
				ManejadorBackupTexto.guardarBackupModelo(navegacionAdapter
						.getActual());
				txtLecturaNueva.setText("");
				asignarDatos();
				actualizarLecturasYFiltro(true);
			}
		});
		dialog.setNegativeButton(null);
		dialog.show();
	}

	/**
	 * Invoca a los metodos necesarios para estimar la lectura actual
	 */
	private void estimarLectura() {
		Lectura lecturaActual = navegacionAdapter.getActual();
		Date fechaActual = new Date();
		int lecturaEstimada = lecturaActual.obtenerLecturaActivaEstimada();
		lecturaActual.leerLectura(lecturaEstimada, fechaActual);
		Ordenativo ord = Ordenativo.obtenerOrdenativoLecturaEstimada();
		OrdenativoLectura ordLect = new OrdenativoLectura(ord, lecturaActual,
				fechaActual);
		lecturaActual.ObservacionLectura = ord.Codigo;// le asigna el codigo de
														// ordenativo lectura
														// estimada
		if (lecturaActual.TagCalculaPotencia == 1
				|| lecturaActual.LeePotencia == 1)// si debe calcular potencia
		{
			estimarPotencia(lecturaActual);
		}
		GestionadorImportesYConceptos.agregarConceptos(lecturaActual);
		lecturaActual.save();
		ordLect.guardarYEnviarPor3G();
		imprimirLectura(lecturaActual);
		ManejadorUbicacion.obtenerUbicacionActual(TomarLectura.this,
				lecturaActual);
		if (VariablesDeEntorno.tipoGuardadoUbicacion == 0)// no se guarda gps,
															// se guarda
															// directamente la
															// lectura
			ManejadorConexionRemota.guardarLectura(lecturaActual);
	}

	/**
	 * Estima la potencia de la lectura
	 * 
	 * @param lecturaActual
	 */
	public void estimarPotencia(Lectura lecturaActual) {
		Potencia potencia = lecturaActual.PotenciaLectura;
		if (potencia == null) {
			potencia = new Potencia(lecturaActual);
		}
		potencia.leerPotencia(lecturaActual.obtenerLecturaDemandaEstimada(),
				new BigDecimal(0), lecturaActual);
		potencia.save();
		lecturaActual.PotenciaLectura = potencia;
	}

	// ----------------------------- Impedir Lectura
	// -----------------------------------------------
	private void mostrarDialogoImpedirLectura() {
		final Lectura lecturaActual = navegacionAdapter.getActual();
		final DialogoAgregarOrdenativo pd = new DialogoAgregarOrdenativo(
				TomarLectura.this, lecturaActual,
				R.string.titulo_impedir_lectura,
				(ArrayList<Ordenativo>) Ordenativo
						.obtenerOrdenativosDeImpedimento());
		pd.setIcon(R.drawable.impedir_lectura);
		pd.addOnGuardarClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				estimarLectura();
				lecturaActual.setEstadoLectura(2);// impedida
				lecturaActual.save();
				ManejadorBackupTexto.guardarBackupModelo(lecturaActual);
				asignarDatos();
				if (pd.nuevoOrdLect != null)
					mostrarDialogoFotoOrdenativo(pd.nuevoOrdLect);
				actualizarLecturasYFiltro(true);
			}
		});
		pd.show();
	}

	// ---------------------------------- Visualizar Potencia
	// -------------------------------------
	private void mostrarDialogoVerPotencia() {
		Lectura lecturaActual = navegacionAdapter.getActual();
		if (lecturaActual.LeePotencia == 1 || lecturaActual.LeeReactiva == 1
				|| lecturaActual.TagCalculaPotencia == 1)// si lee potencia o
															// reactiva
		{

			DialogoPotencia dialog = new DialogoPotencia(this, lecturaActual,
					true);
			dialog.show();
		} else {
			CustomDialog dialog = new CustomDialog(this);
			dialog.setMessage(R.string.no_potencia_mensaje);
			dialog.setTitle(R.string.titulo_mensajes_informacion);
			dialog.setPositiveButton(R.string.btn_ok, null);
			dialog.show();
		}
	}

	// ----------------------------------- IMPRIMIR LECTURA
	// --------------------------------------------
	/**
	 * Indica si la impresion anterior fue exitosa, se la utiliza para aumentar
	 * el registro de cantidad de impresiones de la lectura actual
	 */
	private boolean impresionExitosa = false;

	/**
	 * Verifica si la lectura no llego al limite de impresiones y si la bandera
	 * de impresión está activa en caso de que todo este correcto procede a
	 * verificar la asignación de la impresora, caso contrario muestra los
	 * respectivos mensajes de error
	 * 
	 * @param lecturaActual
	 */
	public void imprimirLectura(Lectura lecturaActual) {
		if (lecturaActual.TagImpresionAviso == 1
				&& lecturaActual.ImpresionesAvisoCobro <= VariablesDeEntorno.limiteImpresiones)// si
																								// imprime
																								// aviso
																								// cobro
		{
			final AvisoCobranza avisoCobranza = new AvisoCobranza(lecturaActual);
			impresionExitosa = false;
			AdminUI adminUI = AdminUI.instanciar(VariablesDeSesion
					.getUsuarioLogeado());
			boolean mostrarDialogoConfirmacion = adminUI.obtenerPreferencia(
					OpcionesPreferenciasUI.MOSTRAR_CONFIRMACION_IMPRESION,
					Integer.class) == 1;
			if (mostrarDialogoConfirmacion) {
				mostrarDialogoConfirmacionImpresion(avisoCobranza);
			} else {
				verificarAsignacionImpresora(avisoCobranza);
			}
			if (impresionExitosa) {
				lecturaActual.ImpresionesAvisoCobro++;
				lecturaActual.save();
			}
		} else if (lecturaActual.ImpresionesAvisoCobro > VariablesDeEntorno.limiteImpresiones) {
			mostrarDialogoLimiteDeImpresiones();
		}
	}

	/**
	 * Muestra el dialogo de confirmación de impresion antes de realizarla
	 * 
	 * @param avisoCobranza
	 */
	public void mostrarDialogoConfirmacionImpresion(
			final AvisoCobranza avisoCobranza) {
		final DialogoConfirmacionImpresion dialogo = new DialogoConfirmacionImpresion(
				this);
		dialogo.setPositiveButton(R.string.btn_si, new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogo.dismiss();
				dialogo.guardarPreferenciaMostrarDialogo();
				verificarAsignacionImpresora(avisoCobranza);
			}
		});
		dialogo.setNegativeButton(R.string.btn_no, null);
		dialogo.show();
	}

	/**
	 * Verifica si se asigno la impresora para realizar la impresión y procede a
	 * iniciar la impresion, si no se asignó una impresora, muestra el dialogo
	 * para su selección primeramente.
	 * 
	 * @param avisoCobranza
	 */
	public void verificarAsignacionImpresora(final AvisoCobranza avisoCobranza) {
		if (!ManejadorImpresora.impresoraPredefinidaFueAsignada()) {
			DialogoSeleccionImpresora dialogo = new DialogoSeleccionImpresora(
					this);
			dialogo.setCancelable(false);
			dialogo.show();
			dialogo.esconderBotonSalir();
			dialogo.addOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapter, View v,
						int pos, long arg3) {
					iniciarImpresion(avisoCobranza);
				}
			});
		} else {
			iniciarImpresion(avisoCobranza);
		}
	}

	/**
	 * Manda el aviso de cobro a imprimir
	 * 
	 * @param avisoCobranza
	 */
	public void iniciarImpresion(final AvisoCobranza avisoCobranza) {
		try {
			ManejadorImpresora.imprimir(avisoCobranza.obtenerImprimible());
			impresionExitosa = true;
		} catch (ImpresoraPredefinidaNoAsignadaExcepcion e) {
			Toast.makeText(TomarLectura.this, e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}
	}

	/**
	 * Muestra un dialogo que anuncia al usuario que se llego al limite de
	 * impresiones y que no podrá imprimir la lectura
	 */
	public void mostrarDialogoLimiteDeImpresiones() {
		CustomDialog dialog = new CustomDialog(this);
		dialog.setMessage(R.string.limite_impresion_mensaje);
		dialog.setTitle(R.string.titulo_limite_impresion);
		dialog.setIcon(R.drawable.error);
		dialog.setPositiveButton(null);
		dialog.show();
	}

	// ------------------------EVENTOS DE
	// SWIPE-----------------------------------------------

	private void crearSwipeListener() {
		ActivitySwipeDetector activitySwipeDetector = new ActivitySwipeDetector(
				this);
		lecturaLayout.setOnTouchListener(activitySwipeDetector);
	}

	@Override
	public void onRightToLeftSwipe() {
		btnSiguienteClick(null);
	}

	@Override
	public void onLeftToRightSwipe() {
		btnAnteriorClick(null);
	}

	@Override
	public void onTopToBottomSwipe() {
	}

	@Override
	public void onBottomToTopSwipe() {
	}

	// ------------------------- CAPTURA DE FOTO CAMARA
	// -------------------------------------

	/**
	 * Invoca un dialogo para sacar una foto para una lectura, en caso de
	 * haberse llegado al limite de fotos por lectura, se le mostrara un dialogo
	 * infromando al usuario de ello.
	 */
	private void mostrarDialogoFotoLectura() {
		final Lectura lecturaActual = navegacionAdapter.getActual();
		if (lecturaActual.NumFotosTomadas < VariablesDeEntorno.numMaxFotosPorLectura) {
			final CustomDialog dialog = new CustomDialog(this);
			dialog.setTitle(R.string.tomar_foto_titulo);
			dialog.setIcon(R.drawable.camera);
			dialog.setMessage(R.string.preguntar_tomar_foto_lectura);
			dialog.setPositiveButton("Si", new OnClickListener() {
				@Override
				public void onClick(View v) {
					ManejadorDeCamara.tomarFotoLectura(TomarLectura.this,
							lecturaActual);
					dialog.dismiss();
				}
			});
			dialog.setNegativeButton("No", null);
			dialog.show();
		} else {
			CustomDialog dlg = new CustomDialog(this);
			dlg.setTitle(R.string.limite_fotos_titulo);
			dlg.setIcon(R.drawable.warning);
			dlg.setMessage(R.string.limite_fotos_msg);
			dlg.setPositiveButton(null);
			dlg.show();
		}
	}

	/**
	 * Invoca un dialogo para sacar una foto para un ordenativo, en caso de
	 * haberse llegado al limite de fotos por lectura, se le mostrara un dialogo
	 * infromando al usuario de ello.
	 */
	private void mostrarDialogoFotoOrdenativo(final OrdenativoLectura ordLec) {
		if (navegacionAdapter.getActual().NumFotosTomadas < VariablesDeEntorno.numMaxFotosPorLectura) {
			final CustomDialog dialog = new CustomDialog(this);
			dialog.setTitle(R.string.tomar_foto_titulo);
			dialog.setIcon(R.drawable.camera);
			dialog.setMessage(R.string.preguntar_tomar_foto_ordenativo);
			dialog.setPositiveButton("Si", new OnClickListener() {
				@Override
				public void onClick(View v) {
					ManejadorDeCamara.tomarFotoOrdenativo(TomarLectura.this,
							ordLec);
					dialog.dismiss();
				}
			});
			dialog.setNegativeButton("No", null);
			dialog.show();
		} else {
			CustomDialog dlg = new CustomDialog(this);
			dlg.setTitle(R.string.limite_fotos_titulo);
			dlg.setIcon(R.drawable.warning);
			dlg.setMessage(R.string.limite_fotos_msg);
			dlg.setPositiveButton(null);
			dlg.show();
		}
	}

	/**
	 * Muestra un dialogo para tomar una fotografía de una lectura entre líneas.
	 * 
	 * @param medEntreLineas
	 */
	private void mostrarDialogoFotoEntreLineas(
			final MedidorEntreLineas medEntreLineas) {
		final CustomDialog dialog = new CustomDialog(this);
		dialog.setTitle(R.string.tomar_foto_titulo);
		dialog.setIcon(R.drawable.camera);
		dialog.setMessage(R.string.preguntar_tomar_foto_entre_lineas);
		dialog.setPositiveButton("Si", new OnClickListener() {
			@Override
			public void onClick(View v) {
				ManejadorDeCamara.tomarFotoEntreLineas(TomarLectura.this,
						medEntreLineas);
				dialog.dismiss();
			}
		});
		dialog.setNegativeButton("No", null);
		dialog.show();
	}

	/**
	 * Procesa el resultado una vez invocada la camara, si es que se tomo una
	 * foto
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			final Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == ManejadorDeCamara.TOMAR_FOTO_LECTURA_REQUEST)
				actualizarNumeroDeFotosLectura();
			if (requestCode == LISTA_LECTURAS || requestCode == BUSCAR_LECTURA) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						final long idLectura = data.getLongExtra(
								ARG_ID_LECTURA, -1);
						if (idLectura != -1) {
							int posNueva = obtenerPosLectura(idLectura);
							if (posNueva == -1) {
								filtroLecturas.resetearCriteriosFiltro();
								asignarLista();
								posNueva = obtenerPosLectura(idLectura);
							}
							navegacionAdapter.setPosicion(posNueva == -1 ? 0
									: posNueva);
						}
					}
				}).start();
			}
		} else if (resultCode == RESULT_CANCELED) {
			if (requestCode == ManejadorDeCamara.TOMAR_FOTO_LECTURA_REQUEST
					|| requestCode == ManejadorDeCamara.TOMAR_FOTO_ENTRE_LINEAS_REQUEST) {
				Toast.makeText(
						this,
						(requestCode == ManejadorDeCamara.TOMAR_FOTO_LECTURA_REQUEST || requestCode == ManejadorDeCamara.TOMAR_FOTO_ENTRE_LINEAS_REQUEST) ? R.string.camara_cancelada
								: R.string.bluetooth_no_encendido,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * Obtiene la posición de la lectura
	 * 
	 * @param idLectura
	 * @return pos lectura, -1 si no se encontró
	 */
	private int obtenerPosLectura(final long idLectura) {
		return navegacionAdapter.buscar(new Predicado<Lectura>() {
			@Override
			public boolean evaluar(Lectura lect) {
				return idLectura == lect.getId();
			}
		});
	}

	/**
	 * Actualiza el numero de fotos de la lectura
	 */
	private void actualizarNumeroDeFotosLectura() {
		Lectura lecturaActual = navegacionAdapter.getActual();
		lecturaActual.NumFotosTomadas++;
		lecturaActual.save();
		Toast.makeText(this, R.string.foto_tomada, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onListaChanged(List<Lectura> lecturas) {
		mostrarLectura();
	}

	@Override
	public void onPosicionChanged(int nuevaPos) {
		asignarDatos();
		contadorSatelite = 0;
		ordLec = null;
	}

}
