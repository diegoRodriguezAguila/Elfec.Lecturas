package com.elfec.lecturas.modelo.detallesresumenes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.elfec.lecturas.modelo.MedidorEntreLineas;

/**
 * Es el resumen diario de Detalle de Lecturas Impedidas, listo para ser impreso
 * 
 * @author drodriguez
 *
 */
public class DetalleLecturasEntreLineas extends DetalleResumenGenerico {

	public DetalleLecturasEntreLineas() {
		titulo = "DETALLE LECTURAS ENTRE LINEAS - GC-0702-15";
		Date fechaActual = new Date();
		DateFormat df = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
		DateFormat horaFormato = new SimpleDateFormat("HH:mm:ss",
				Locale.getDefault());
		fechaImpresion = df.format(fechaActual);
		horaImpresion = horaFormato.format(fechaActual);
	}

	@Override
	public List<ResumenLectura> obtenerResumenesLecturas() {
		List<MedidorEntreLineas> lecturas = MedidorEntreLineas
				.obtenerMedidoresEntreLineas();
		List<ResumenLectura> resLecs = new ArrayList<ResumenLectura>();
		for (MedidorEntreLineas lec : lecturas) {
			ResumenLectura resLec = new ResumenLectura(lec);
			resLecs.add(resLec);
		}
		return resLecs;
	}

}
