package com.elfec.lecturas.modelo;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.database.Cursor;
import android.location.Location;

import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.elfec.lecturas.logica_negocio.web_services.ManejadorConexionRemota;
import com.elfec.lecturas.modelo.backuptexto.IModeloBackupableTexto;
import com.elfec.lecturas.modelo.eventos.EventoAlObtenerResultado;
import com.elfec.lecturas.modelo.eventos.EventoAlObtenerUbicacion;
import com.elfec.lecturas.settings.VariablesDeSesion;

/**
 * Guarda información de las lecturas de medidores entre lineas realizadas, esta
 * tabla se descarga a la tabla ERP_ELFEC.SGC_MOVIL_LECT_ENTRE_LINEAS
 * 
 * @author drodriguez
 */
@Table(name = "MedidoresEntreLineas")
public class MedidorEntreLineas extends Model implements
		EventoAlObtenerUbicacion, EventoAlObtenerResultado,
		IModeloBackupableTexto {

	@Column(name = "Ruta")
	public int Ruta;

	@Column(name = "NumeroMedidor", notNull = true)
	public String NumeroMedidor;

	@Column(name = "LecturaNueva", notNull = true)
	public int LecturaNueva;

	@Column(name = "LecturaPotencia")
	public BigDecimal LecturaPotencia;

	@Column(name = "EnergiaReactiva")
	public BigDecimal Reactiva;

	@Column(name = "FechaLectura")
	public Date FechaLectura;

	@Column(name = "HoraLectura")
	public String HoraLectura;

	@Column(name = "GPSLatitud")
	public double GPSLatitud;

	@Column(name = "GPSLongitud")
	public double GPSLongitud;

	/**
	 * Cuando fue enviado por 3g tiene el estado 1, 0 en caso contrario, 2 en
	 * caso de haberse enviado por otro medio al servidor.
	 */
	@Column(name = "Enviado3G")
	public int Enviado3G;
	/**
	 * El usuario que insertó la lectura entre lineas
	 */
	@Column(name = "UsuarioAuditoria")
	public String UsuarioAuditoria;

	public MedidorEntreLineas(String numMedidor, int ruta, int lecturaNueva,
			BigDecimal lecturaPotencia, BigDecimal energiaReactiva, Date fecha) {
		super();
		UsuarioAuditoria = VariablesDeSesion.getUsuarioLogeado();
		NumeroMedidor = numMedidor;
		LecturaNueva = lecturaNueva;
		LecturaPotencia = lecturaPotencia;
		Reactiva = energiaReactiva;
		FechaLectura = fecha;
		DateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
		HoraLectura = df.format(fecha);
		Ruta = ruta;
	}

	public MedidorEntreLineas() {
		super();
	}

	/**
	 * Accede a la base de datos y obtiene todas las lecturas de medidores entre
	 * lineas
	 * 
	 * @return Lista de medidores entre lineas
	 */
	public static List<MedidorEntreLineas> obtenerMedidoresEntreLineas() {
		return new Select().from(MedidorEntreLineas.class).execute();
	}

	/**
	 * Accede a la base de datos y obtiene todas las lecturas de medidores entre
	 * lineas, que tienen el atributo Enviado3G=0
	 * 
	 * @return Lista de medidores entre lineas que no se enviaron por 3g
	 */
	public static List<MedidorEntreLineas> obtenerMedidoresEntreLineasNoEnviados3G() {
		return new Select().from(MedidorEntreLineas.class).where("Enviado3G=0")
				.execute();
	}

	/**
	 * Realiza un count del total de lecturas entre lineas
	 * 
	 * @return numero de lecturas entre lineas realizadas
	 */
	public static int countTotalLecturas() {
		Cursor mCount = Cache.openDatabase().rawQuery(
				"SELECT COUNT(*) FROM MedidoresEntreLineas", null);
		mCount.moveToFirst();
		int count = mCount.getInt(0);
		mCount.close();
		return count;
	}

	/**
	 * Guarda los datos de Latitud y Longitud en la base de datos
	 * 
	 * @param ubicacionActual
	 */
	public void guardarUbicacion(Location ubicacionActual) {
		GPSLatitud = ubicacionActual.getLatitude();
		GPSLongitud = ubicacionActual.getLongitude();
		this.save();
		ManejadorConexionRemota.guardarLecturaEntreLineas(this);
	}

	@Override
	public void ejecutarTarea(Location ubicacionObtenida) {
		guardarUbicacion(ubicacionObtenida);
	}

	@Override
	public void procesarResultado(double resultado) {
		if (resultado == 1.0) {
			Enviado3G = 1;
			this.save();
		}
	}

	@Override
	public void ejecturaSiTimeout() {
		ManejadorConexionRemota.guardarLecturaEntreLineas(this);
	}

	@Override
	public String obtenerLineaTextoBackup() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
		String fechaHora = df.format(this.FechaLectura) + " "
				+ this.HoraLectura;
		StringBuilder str = new StringBuilder("");
		str.append(Ruta).append("|").append(NumeroMedidor).append("|")
				.append(LecturaNueva).append("|")
				.append(Reactiva != null ? Reactiva : "-").append("|")
				.append(LecturaPotencia != null ? LecturaPotencia : "-")
				.append("|").append(fechaHora).append("|")
				.append(UsuarioAuditoria);
		return str.toString();
	}

	@Override
	public String obtenerNombreArchivoBackup() {
		return "lecturas_entre_lineas_backup";
	}

	@Override
	public String obtenerCabeceraBackup() {
		return "Ruta|Anio|Mes|Dia|Numero Medidor|Lectura Activa|Lectura Reactiva|Lectura Potencia|Fecha y Hora Lectura|Usuario";
	}

}
