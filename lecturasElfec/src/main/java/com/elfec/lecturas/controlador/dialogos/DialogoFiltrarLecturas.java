package com.elfec.lecturas.controlador.dialogos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.elfec.lecturas.R;
import com.elfec.lecturas.controlador.filtroslecturas.CriterioEstado;
import com.elfec.lecturas.controlador.filtroslecturas.CriterioRuta;
import com.elfec.lecturas.controlador.filtroslecturas.FiltroLecturas;
import com.elfec.lecturas.modelo.AsignacionRuta;
import com.elfec.lecturas.modelo.estados.lectura.EstadoLecturaFactory;
import com.elfec.lecturas.modelo.estados.lectura.IEstadoLectura;
import com.elfec.lecturas.modelo.eventos.OnFiltroAplicadoListener;

import java.util.ArrayList;
import java.util.List;

public class DialogoFiltrarLecturas {
    private AlertDialog mDialog;
    private View rootView;
    private Spinner selectorTipoLectura;
    private Spinner selectorRuta;
    private List<String> listaEstados;
    private List<String> listaRutas;
    private List<IEstadoLectura> estadosLecturas;
    private List<AsignacionRuta> rutasUsuario;
    public CriterioEstado criterioEstado;
    public CriterioRuta criterioRuta;
    private FiltroLecturas mFiltroLecturas;
    private OnFiltroAplicadoListener mListener;

    @SuppressLint("InflateParams")
    public DialogoFiltrarLecturas(Context context,
                                  FiltroLecturas filtroLecturas, OnFiltroAplicadoListener listener) {
        this.mFiltroLecturas = filtroLecturas;
        this.mListener = listener;
        rootView = LayoutInflater.from(context).inflate(
                R.layout.dialogo_filtrar_lecturas, null, false);
        mDialog = new AlertDialog.Builder(context).setView(rootView)
                .setTitle(R.string.titulo_filtrar_lecturas)
                .setIcon(R.drawable.filtrar_lecturas_d)
                .setNegativeButton(R.string.btn_cancel, null)
                .setPositiveButton(R.string.btn_ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (criterioEstado != null)
                                    mFiltroLecturas
                                            .agregarCriterioAFiltro(criterioEstado);
                                else
                                    mFiltroLecturas
                                            .quitarCriterioDeFiltro(CriterioEstado.class);

                                if (criterioRuta != null)
                                    mFiltroLecturas
                                            .agregarCriterioAFiltro(criterioRuta);
                                else
                                    mFiltroLecturas
                                            .quitarCriterioDeFiltro(CriterioRuta.class);
                                if (mListener != null)
                                    mListener.onFiltroAplicado(mFiltroLecturas);
                            }
                        }).start();
                    }
                }).create();
        listaRutas = new ArrayList<String>();
        listaRutas.add("Todas");
        rutasUsuario = AsignacionRuta.obtenerTodasLasRutas();
        for (AsignacionRuta asignRuta : rutasUsuario) {
            listaRutas.add("" + asignRuta.Ruta);
        }
        selectorRuta = (Spinner) rootView.findViewById(R.id.select_ruta);
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
        selectorTipoLectura = (Spinner) rootView
                .findViewById(R.id.select_mostrar_tipo_lectura);
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
        CriterioEstado criterioEstado = (CriterioEstado) mFiltroLecturas
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
     * Muestra el dialogo
     */
    public void show() {
        mDialog.show();
    }

    /**
     * Asigna la ruta que fue seleccionada previamente segun el criterio del
     * filtro <b>FiltroLecturas</b> si el criterio es null, pone por defecto a
     * todas;
     */
    private void asignarRutaSeleccionada() {
        CriterioRuta criterioRuta = (CriterioRuta) mFiltroLecturas
                .obtenerCriterioDeFiltro(CriterioRuta.class);
        int posSeleccionada = 0;
        if (criterioRuta != null) {
            AsignacionRuta rutaSeleccionada = criterioRuta.obtenerRutaSeleccionada();
            int tam = rutasUsuario.size();
            for (int i = 0; i < tam; i++) {
                if (rutasUsuario.get(i).equals(rutaSeleccionada)) {
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
                criterioRuta = null;
                if (position > 0)// no es todas las rutas
                    criterioRuta = new CriterioRuta(rutasUsuario.get(position - 1));
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
