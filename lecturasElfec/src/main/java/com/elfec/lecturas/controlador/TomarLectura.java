package com.elfec.lecturas.controlador;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elfec.lecturas.R;
import com.elfec.lecturas.controlador.accionesycustomizaciones.ActivitySwipeDetector;
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
import com.elfec.lecturas.controlador.filtroslecturas.FiltroLecturas;
import com.elfec.lecturas.helpers.ManejadorDeCamara;
import com.elfec.lecturas.helpers.ManejadorEstadosHW;
import com.elfec.lecturas.helpers.ManejadorImpresora;
import com.elfec.lecturas.helpers.ManejadorSonido;
import com.elfec.lecturas.helpers.ManejadorUbicacion;
import com.elfec.lecturas.helpers.ui.ClicksBotonesHelper;
import com.elfec.lecturas.logica_negocio.GestionadorImportesYConceptos;
import com.elfec.lecturas.logica_negocio.ManejadorBackupTexto;
import com.elfec.lecturas.logica_negocio.web_services.ManejadorConexionRemota;
import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.MedidorEntreLineas;
import com.elfec.lecturas.modelo.Ordenativo;
import com.elfec.lecturas.modelo.OrdenativoLectura;
import com.elfec.lecturas.modelo.Potencia;
import com.elfec.lecturas.modelo.Usuario;
import com.elfec.lecturas.modelo.avisocobranza.AvisoCobranza;
import com.elfec.lecturas.modelo.excepciones.ImpresoraPredefinidaNoAsignadaException;
import com.elfec.lecturas.modelo.seguridad.Restricciones;
import com.elfec.lecturas.modelo.validaciones.IValidacionLectura;
import com.elfec.lecturas.settings.AdministradorSeguridad;
import com.elfec.lecturas.settings.VariablesDeEntorno;
import com.elfec.lecturas.settings.VariablesDeSesion;
import com.elfec.lecturas.settings.ui.AdminUI;
import com.elfec.lecturas.settings.ui.OpcionesPreferenciasUI;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TomarLectura extends AppCompatActivity implements ISwipeListener,
        NavegacionListener<Lectura> {

    public static final String ARG_ID_LECTURA = "IdLecturaSeleccionada";
    public static final int LISTA_LECTURAS = 1;
    public static final int BUSCAR_LECTURA = 2;

    private LinearLayout lecturaLayout;
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
    public ImageButton btnAgergarOrdenativo;
    public FloatingActionButton btnConfirmarLectura;
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
        new Thread(() -> {
            navegacionAdapter = new NavegacionAdapter<>(
                    TomarLectura.this);
            filtroLecturas = new FiltroLecturas();
            inicializarCampos();
            asignarLista();
            btnPrimeroClick(null);
            ponerClickListeners();
            ponerClickListenerAObservacion();
            asignarTouchListenerATxtLecturaNueva();
            crearSwipeListener();
            inicializarVariablesDeEntorno();
        }).start();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
        lecturaLayout = (LinearLayout) findViewById(R.id.datos_de_lectura);
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
        btnConfirmarLectura = (FloatingActionButton) findViewById(R.id.btn_confirmar_lectura);
        btnPostergarLectura = (Button) findViewById(R.id.btn_postergar_lectura);
        btnReintentarLectura = (Button) findViewById(R.id.btn_reintentar_lectura);
        btnAgergarOrdenativo = (ImageButton) findViewById(R.id.btn_agregar_ordenativo);
        btnRecordatorios = (ImageButton) findViewById(R.id.btn_recordatorio);
    }

    /**
     * Pone click listeners al boton reintentar y postergar
     */
    protected void ponerClickListeners() {
        btnPostergarLectura.setOnClickListener(this::btnPostergarLecturaClick);
        btnReintentarLectura.setOnClickListener(this::btnReintentarLecturaClick);
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
        runOnUiThread(() -> new AlertDialog.Builder(TomarLectura.this)
                .setTitle(R.string.titulo_mensajes_advertencia)
                .setIcon(R.drawable.warning)
                .setMessage(R.string.advertencia_filtros)
                .setPositiveButton(R.string.btn_ok, null).show());
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
        txtLecturaNueva.setOnTouchListener((v, ev) -> {
            if (txtLecturaNueva.hasFocus() && contadorSatelite == 0) {
                Lectura lecturaActual1 = navegacionAdapter.getActual();
                contadorSatelite++;
                ManejadorUbicacion.obtenerUbicacionActual(
                        TomarLectura.this, lecturaActual1);
            }
            return false;
        });
    }

    /**
     * Asigna los datos correspondientes de la lectura actual a la vista
     */
    public void asignarDatos() {
        final Lectura lecturaActual = navegacionAdapter.getActual();
        runOnUiThread(() -> {
            lblNombre.setText(lecturaActual.NombreCliente);
            lblDireccion.setText(lecturaActual.DireccionSuministro);
            lblNus.setText(String.valueOf(lecturaActual.Suministro));
            lblTarifa.setText(lecturaActual.SiglaCategoria);
            lblCuenta.setText(lecturaActual.obtenerCuentaConFormato());
            lblOrden.setText(String.valueOf(navegacionAdapter.getPosicionActual() + 1));
            lblNumMedidor.setText(String.valueOf(lecturaActual.NumeroMedidor));
            lecturaActual.mostrarLecturaEnTomarLectura(TomarLectura.this);
            lecturaActual.mostrarMenuEnTomarLectura(TomarLectura.this);
            lblNumDigitos.setText(String.format(
                    getString(R.string.info_digitos_lbl),
                    lecturaActual.NumDigitosMedidor));
            txtLecturaNueva
                    .setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                            lecturaActual.NumDigitosMedidor)});
            lblObservacion
                    .setText((lecturaActual.ObservacionLectura == 0) ? "Ninguna"
                            : "" + lecturaActual.ObservacionLectura);
            btnRecordatorios.setEnabled(lecturaActual.tieneRecordatorio());
            txtLecturaNueva.setText("");
        });
    }

    /**
     * Muestra la lectura y esconde la carga
     */
    private void mostrarLectura() {
        runOnUiThread(() -> {
            if (lecturaLayout.getVisibility() == View.GONE) {
                if (menuRecordatorioLector != null)
                    menuRecordatorioLector.setVisible(true);
                if (menuFiltrarLecturas != null)
                    menuFiltrarLecturas.setVisible(true);
                lecturaLayout.setVisibility(View.VISIBLE);
                layoutCargaListaLecturas.setVisibility(View.GONE);

            }
        });
    }

    /**
     * Muestra la carga y esconde la lectura
     */
    private void mostrarCargaLectura() {
        runOnUiThread(() -> {
            if (layoutCargaListaLecturas.getVisibility() == View.GONE) {
                if (menuRecordatorioLector != null)
                    menuRecordatorioLector.setVisible(false);
                if (menuFiltrarLecturas != null)
                    menuFiltrarLecturas.setVisible(false);
                layoutCargaListaLecturas.setVisibility(View.VISIBLE);
                lecturaLayout.setVisibility(View.GONE);
            }
        });
    }

    private void ponerClickListenerAObservacion() {
        OnClickListener clickListener = v -> {
            lblObservacion.setTextColor(ContextCompat.getColor(
                    TomarLectura.this, R.color.elfectheme_color));
            lblObsNum.setTextColor(ContextCompat.getColor(
                    TomarLectura.this, R.color.elfectheme_color));
            abrirDialogoVerOrdenativos();
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

    private Lectura lecturaActual;

    public void btnConfirmarLecturaClick(View view) {
        if (ClicksBotonesHelper.sePuedeClickearBoton()) {
            return;
        }
        if (txtLecturaNueva.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.msg_debe_llenar_campos_lectura,
                    Toast.LENGTH_SHORT).show();
            return;
        }
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
                lecturaActual, ordenativoLectura -> {
            asignarDatos();
            agregarOrdenativosLecturaCiclico();
        });
        pd.setOnCancelListener(dialog -> actualizarLecturasYFiltro(true));
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
        new AlertDialog.Builder(this)
                .setMessage(resultadoValidacion.obtenerMensaje())
                .setTitle(R.string.titulo_mensajes_advertencia)
                .setIcon(R.drawable.warning)
                .setPositiveButton(R.string.btn_ok,
                        (dialog, which) -> {
                            if (!procederConGuardado) {
                                return;
                            }
                            Ordenativo ord = resultadoValidacion
                                    .obtenerOrdenativo();
                            ordLec = new OrdenativoLectura(ord,
                                    lecturaActual, new Date());
                            procederConProcesoDeGuardado();
                        }).show();
    }

    // ------------------------------------------ DIALOGO POTENCIA
    // ----------------------------------------
    public void procederConProcesoDeGuardado() {
        // si lee potencia o reactiva
        if (lecturaActual.LeePotencia == 1 || lecturaActual.LeeReactiva == 1) {
            new DialogoPotencia(this, lecturaActual, false,
                    (lectura, potencia) -> guardarLectura()).show();
        } else {
            guardarLectura();
        }
    }

    // --------------------------- BARRA DE HERRAMIENTAS INFERIOR
    // ------------------------------------
    public void btnBuscarClick(View view) {
        if (!ClicksBotonesHelper.sePuedeClickearBoton()) {
            return;
        }
        Intent intent = new Intent(this, BuscarLectura.class);
        intent.putExtra("RutaActual", lblCuenta.getText().subSequence(0, 6)
                .toString());
        startActivityForResult(intent, BUSCAR_LECTURA);
        overridePendingTransition(R.anim.slide_left_in,
                R.anim.slide_left_out);
    }

    public void btnListaLecturasClick(View view) {
        if (ClicksBotonesHelper.sePuedeClickearBoton()) {
            return;
        }
        Intent intent = new Intent(this, ListaLecturas.class);
        startActivityForResult(intent, LISTA_LECTURAS);
        overridePendingTransition(R.anim.slide_left_in,
                R.anim.slide_left_out);
    }

    public void btnAnteriorClick(View view) {
        navegacionAdapter
                .setPosicion(navegacionAdapter.getPosicionActual() - 1);
    }

    private Predicado<Lectura> predicadoLectura = lect -> lect.getEstadoLectura().getEstadoEntero() == 0;

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
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage(R.string.postergar_mensaje)
                .setTitle(R.string.titulo_mensajes_confirmar)
                .setPositiveButton(R.string.btn_ok,
                        (dialog, which) -> {
                            Lectura lecturaActual1 = navegacionAdapter
                                    .getActual();
                            lecturaActual1.setEstadoLectura(3);
                            lecturaActual1.save();
                            asignarDatos();
                            dialog.dismiss();
                            actualizarLecturasYFiltro(true);
                        }).setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    /**
     * Muestra un dialogo de confirmacón para poner la lectura en estado de
     * reintentar
     *
     * @param view view
     */
    public void btnReintentarLecturaClick(View view) {
        if (ClicksBotonesHelper.sePuedeClickearBoton()) {
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage(R.string.reintentar_mensaje)
                .setTitle(R.string.titulo_mensajes_confirmar)
                .setPositiveButton(R.string.btn_ok,
                        (dialog, which) -> {
                            Lectura lecturaActual1 = navegacionAdapter
                                    .getActual();
                            DateFormat df = new SimpleDateFormat(
                                    "HH:mm", Locale.getDefault());
                            Date fechaLec = new Date();
                            lecturaActual1.FechaLecturaActual = fechaLec;
                            lecturaActual1.HoraLectura = df
                                    .format(fechaLec);
                            lecturaActual1.setEstadoLectura(4);
                            Ordenativo ord = Ordenativo
                                    .obtenerOrdenativoLecturaReintentar();
                            OrdenativoLectura ordLect = new OrdenativoLectura(
                                    ord, lecturaActual1, new Date());
                            // le asigna el codigo de ordenativo lectura
                            // estimada
                            lecturaActual1.ObservacionLectura = ord.Codigo;
                            lecturaActual1.UsuarioAuditoria = VariablesDeSesion
                                    .getUsuarioLogeado();
                            lecturaActual1.save();
                            ordLect.guardarYEnviarPor3G();
                            asignarDatos();
                            dialog.dismiss();
                            actualizarLecturasYFiltro(true);
                        })
                .setNegativeButton(R.string.btn_cancel, null).show();
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
            return;
        }
        new DialogoAgregarOrdenativo(TomarLectura.this,
                navegacionAdapter.getActual(),
                ordenativoLectura -> asignarDatos()).show();
    }

    // -------------------------- DIALOGO VER ORDENATIVOS
    // LECTURA-------------------------------------

    private void abrirDialogoVerOrdenativos() {
        DialogoVerOrdenativos dvo = new DialogoVerOrdenativos(
                TomarLectura.this, navegacionAdapter.getActual());
        dvo.setOnDismissListener(dialog -> {
            lblObservacion.setTextColor(ContextCompat.getColor(
                    TomarLectura.this, android.R.color.black));
            lblObsNum.setTextColor(ContextCompat.getColor(
                    TomarLectura.this, android.R.color.black));
        });
        dvo.show();
    }

    // ----------------------------- OPCIONES DE MENU
    // -----------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (ClicksBotonesHelper.sePuedeClickearBoton()) {
            return true;
        }
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

    // ----------------------------- DIALOGO FILTRAR LECTURAS
    // -----------------------------------------------

    /**
     * Abre un dialogo para filtrar las lecturas que se estan viendo actualmente
     */
    private void mostrarDialogoFiltrarLecturas() {
        new DialogoFiltrarLecturas(this, filtroLecturas,
                filtroLecturas1 -> asignarLista()).show();
    }

    // ----------------------------- DIALOGO MEDIDOR ENTRE LINEAS
    // -----------------------------------------------
    private void mostrarDialogoMedidorEntreLineas() {
        new DialogoMedidorEntreLineas(this,
                this::mostrarDialogoFotoEntreLineas).show();
    }

    // ----------------------------- DIALOGO RECORDATORIOS LECTOR
    // -----------------------------------------------

    /**
     * Muestra un dialogo para agregar o editar el recordatorio de la lectura
     * actual
     */
    private void mostrarDialogoRecordatorioLector() {
        new DialogoRecordatorio(this, navegacionAdapter.getActual(),
                lectura -> btnRecordatorios.setEnabled(lectura.tieneRecordatorio())).show();
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
            new AlertDialog.Builder(this)
                    .setMessage(R.string.modificar_lectura_mensaje)
                    .setTitle(R.string.titulo_modificar_lectura)
                    .setIcon(R.drawable.modificar_lectura_d)
                    .setPositiveButton(R.string.btn_ok,
                            (dialog, which) -> {
                                lectura.resetearLectura();
                                asignarDatos();
                                actualizarLecturasYFiltro(false);
                            }).setNegativeButton(R.string.btn_cancel, null)
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.limite_modificaciones_mensaje)
                    .setTitle(R.string.titulo_limite_modificaciones)
                    .setIcon(R.drawable.error_modificar_lectura)
                    .setPositiveButton(R.string.btn_ok, null).show();
        }
    }

    // ----------------------------- DIALOGO REIMPRIMIR AVISO DE COBRANZA
    // -----------------------------------------------

    /**
     * Muestra el dialogo en el que se pregunta al usuario si desea re imprimir
     * el aviso de cobro
     */
    private void mostrarDialogoReImprimir() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.reimprimir_mensaje)
                .setIcon(R.drawable.imprimir_d)
                .setTitle(R.string.titulo_reimprimir)
                .setPositiveButton(R.string.btn_ok,
                        (dialog, which) -> {
                            Lectura lectura = navegacionAdapter.getActual();
                            imprimirLectura(lectura);
                        }).setNegativeButton(R.string.btn_cancel, null).show();
    }

    // ----------------------------- Estimar Lectura
    // -----------------------------------------------

    /**
     * Muestra el dialogo de confirmación de estimación de la lectura actual
     */
    private void mostrarDialogoEstimarLectura() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.estimar_mensaje)
                .setIcon(R.drawable.estimar_lectura_d)
                .setTitle(R.string.titulo_estimar_lectura)
                .setPositiveButton(R.string.btn_ok,
                        (dialog, which) -> {
                            estimarLectura();
                            ManejadorBackupTexto
                                    .guardarBackupModelo(navegacionAdapter
                                            .getActual());
                            txtLecturaNueva.setText("");
                            asignarDatos();
                            actualizarLecturasYFiltro(true);
                        }).setNegativeButton(R.string.btn_cancel, null).show();
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
        // le asigna el codigo de ordenativo lectura estimada
        lecturaActual.ObservacionLectura = ord.Codigo;
        // si debe calcular potencia
        if (lecturaActual.TagCalculaPotencia == 1
                || lecturaActual.LeePotencia == 1) {
            estimarPotencia(lecturaActual);
        }
        GestionadorImportesYConceptos.agregarConceptos(lecturaActual);
        lecturaActual.save();
        ordLect.guardarYEnviarPor3G();
        imprimirLectura(lecturaActual);
        ManejadorUbicacion.obtenerUbicacionActual(TomarLectura.this,
                lecturaActual);
        // no se guarda gps, se guarda directamente la lectura
        if (VariablesDeEntorno.tipoGuardadoUbicacion == 0)
            ManejadorConexionRemota.guardarLectura(lecturaActual);
    }

    /**
     * Estima la potencia de la lectura
     *
     * @param lecturaActual lect actual
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
                Ordenativo.obtenerOrdenativosDeImpedimento(),
                ordenativoLectura -> {
                    estimarLectura();
                    lecturaActual.setEstadoLectura(2);// impedida
                    lecturaActual.save();
                    ManejadorBackupTexto.guardarBackupModelo(lecturaActual);
                    asignarDatos();
                    if (ordenativoLectura != null)
                        mostrarDialogoFotoOrdenativo(ordenativoLectura);
                    actualizarLecturasYFiltro(true);
                    asignarDatos();
                });
        pd.setIcon(R.drawable.impedir_lectura_d);
        pd.show();
    }

    // ---------------------------------- Visualizar Potencia
    // -------------------------------------
    private void mostrarDialogoVerPotencia() {
        Lectura lecturaActual = navegacionAdapter.getActual();
        // si lee potencia o reactiva
        if (lecturaActual.LeePotencia == 1 || lecturaActual.LeeReactiva == 1
                || lecturaActual.TagCalculaPotencia == 1) {
            new DialogoPotencia(this, lecturaActual, true).show();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.no_potencia_mensaje)
                    .setTitle(R.string.titulo_mensajes_informacion)
                    .setPositiveButton(R.string.btn_ok, null).show();
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
     * de impresión está activa en caso de que este correcto procede a
     * verificar la asignación de la impresora, caso contrario muestra los
     * respectivos mensajes de error
     *
     * @param lecturaActual lect actual
     */
    public void imprimirLectura(Lectura lecturaActual) {
        // si imprime aviso cobro
        if (lecturaActual.TagImpresionAviso == 1
                && lecturaActual.ImpresionesAvisoCobro <= VariablesDeEntorno.limiteImpresiones) {
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
     * @param avisoCobranza aviso cobranza
     */
    public void mostrarDialogoConfirmacionImpresion(
            final AvisoCobranza avisoCobranza) {
        new DialogoConfirmacionImpresion(this,
                () -> verificarAsignacionImpresora(avisoCobranza)).show();
    }

    /**
     * Verifica si se asigno la impresora para realizar la impresión y procede a
     * iniciar la impresion, si no se asignó una impresora, muestra el dialogo
     * para su selección primeramente.
     *
     * @param avisoCobranza aviso cobranza
     */
    public void verificarAsignacionImpresora(final AvisoCobranza avisoCobranza) {
        if (!ManejadorImpresora.impresoraPredefinidaFueAsignada()) {
            new DialogoSeleccionImpresora(this).addOnItemClickListener(
                    (adapter, v, pos, arg3) -> iniciarImpresion(avisoCobranza)).show();
        } else {
            iniciarImpresion(avisoCobranza);
        }
    }

    /**
     * Manda el aviso de cobro a imprimir
     *
     * @param avisoCobranza aviso cobranza
     */
    public void iniciarImpresion(final AvisoCobranza avisoCobranza) {
        try {
            ManejadorImpresora.imprimir(avisoCobranza.obtenerImprimible());
            impresionExitosa = true;
        } catch (ImpresoraPredefinidaNoAsignadaException e) {
            Toast.makeText(TomarLectura.this, e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * Muestra un dialogo que anuncia al usuario que se llego al limite de
     * impresiones y que no podrá imprimir la lectura
     */
    public void mostrarDialogoLimiteDeImpresiones() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.limite_impresion_mensaje)
                .setTitle(R.string.titulo_limite_impresion)
                .setIcon(R.drawable.error_imprimir)
                .setPositiveButton(R.string.btn_ok, null).show();
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
            new AlertDialog.Builder(this)
                    .setTitle(R.string.tomar_foto_titulo)
                    .setIcon(R.drawable.camera_d)
                    .setMessage(R.string.preguntar_tomar_foto_lectura)
                    .setPositiveButton(R.string.btn_si,
                            (dialog, which) -> ManejadorDeCamara.tomarFotoLectura(
                                    TomarLectura.this, lecturaActual))
                    .setNegativeButton(R.string.btn_no, null).show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.limite_fotos_titulo)
                    .setIcon(R.drawable.warning)
                    .setMessage(R.string.limite_fotos_msg)
                    .setPositiveButton(R.string.btn_ok, null).show();
        }
    }

    /**
     * Invoca un dialogo para sacar una foto para un ordenativo, en caso de
     * haberse llegado al limite de fotos por lectura, se le mostrara un dialogo
     * infromando al usuario de ello.
     */
    private void mostrarDialogoFotoOrdenativo(final OrdenativoLectura ordLec) {
        if (navegacionAdapter.getActual().NumFotosTomadas < VariablesDeEntorno.numMaxFotosPorLectura) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.tomar_foto_titulo)
                    .setIcon(R.drawable.camera_d)
                    .setMessage(R.string.preguntar_tomar_foto_ordenativo)
                    .setPositiveButton(R.string.btn_si,
                            (dialog, which) -> ManejadorDeCamara.tomarFotoOrdenativo(
                                    TomarLectura.this, ordLec))
                    .setNegativeButton(R.string.btn_no, null).show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.limite_fotos_titulo)
                    .setIcon(R.drawable.warning)
                    .setMessage(R.string.limite_fotos_msg)
                    .setPositiveButton(R.string.btn_ok, null).show();
        }
    }

    /**
     * Muestra un dialogo para tomar una fotografía de una lectura entre líneas.
     *
     * @param medEntreLineas medidor entre lineas
     */
    private void mostrarDialogoFotoEntreLineas(
            final MedidorEntreLineas medEntreLineas) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.tomar_foto_titulo)
                .setIcon(R.drawable.camera_d)
                .setMessage(R.string.preguntar_tomar_foto_entre_lineas)
                .setPositiveButton(R.string.btn_si,
                        (dialog, which) -> ManejadorDeCamara.tomarFotoEntreLineas(
                                TomarLectura.this, medEntreLineas))
                .setNegativeButton(R.string.btn_no, null).show();
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
                new Thread(() -> {
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
                }).start();
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == ManejadorDeCamara.TOMAR_FOTO_LECTURA_REQUEST
                    || requestCode == ManejadorDeCamara.TOMAR_FOTO_ENTRE_LINEAS_REQUEST) {
                Toast.makeText(
                        this,
                        R.string.camara_cancelada,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Obtiene la posición de la lectura
     *
     * @param idLectura id lectura
     * @return pos lectura, -1 si no se encontró
     */
    private int obtenerPosLectura(final long idLectura) {
        return navegacionAdapter.buscar(lect -> idLectura == lect.getId());
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
