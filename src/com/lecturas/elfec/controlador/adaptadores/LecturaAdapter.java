package com.lecturas.elfec.controlador.adaptadores;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lecturas.elfec.R;
import com.lecturas.elfec.modelo.Lectura;
/**
 * Sirve para crear la lista de lecturas, es el adaptador que se encarga de ajustar la vista
 * @author drodriguez
 *
 */
public class LecturaAdapter extends ArrayAdapter<Lectura>{

	private boolean esDialogo;
    private ArrayList<Lectura> lLecturas;
    private static LayoutInflater inflater = null;
    
	public LecturaAdapter(Activity activity, int textViewResourceId,
			ArrayList<Lectura> lecturas) {
		super(activity, textViewResourceId, lecturas);
		esDialogo = false;
		try {
            this.lLecturas = lecturas;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {

        }
	}
	
	public LecturaAdapter(Activity activity, int textViewResourceId,
			ArrayList<Lectura> lecturas, boolean esDialogo) {
		super(activity, textViewResourceId, lecturas);
		this.esDialogo = esDialogo;
		try {
            this.lLecturas = lecturas;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {

        }
	}
	
	
	@Override
	public int getCount() {
        return lLecturas.size();
    }
	
	@Override
	 public Lectura getItem(int position) {
        return lLecturas.get(position);
    }

	 @Override
    public long getItemId(int position) {
        return position;
    }
	 
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        vi = inflater.inflate(R.layout.list_item_lectura, null);
        Lectura lectura = lLecturas.get(position);
        TextView lblCuenta = ((TextView) vi.findViewById(R.id.lbl_num_cuenta_item));
        lblCuenta.setText(lectura.obtenerCuentaConFormato());
        if(esDialogo)
        {
        	lblCuenta.setTextSize(18);
        }
        ((TextView) vi.findViewById(R.id.lbl_nus_cliente_item)).setText(""+lectura.Suministro);
        ((TextView) vi.findViewById(R.id.lbl_num_medidor_item)).setText(lectura.NumeroMedidor);
        ((TextView) vi.findViewById(R.id.lbl_estado_lectura)).setText(lectura.obtenerEstadoLectura());
        ((TextView) vi.findViewById(R.id.lbl_estado_lectura)).setBackgroundColor(lectura.getEstadoLectura().getColor(vi.getContext()));
        ((TextView) vi.findViewById(R.id.lbl_ruta_item)).setText(""+lectura.Ruta);
        return vi;
    }

}
