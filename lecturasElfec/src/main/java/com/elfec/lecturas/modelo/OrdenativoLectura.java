package com.elfec.lecturas.modelo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.elfec.lecturas.logica_negocio.ManejadorBackupTexto;
import com.elfec.lecturas.logica_negocio.web_services.ManejadorConexionRemota;
import com.elfec.lecturas.modelo.backuptexto.IModeloBackupableTexto;
import com.elfec.lecturas.modelo.enums.EstadoExportacion;
import com.elfec.lecturas.modelo.eventos.EventoAlObtenerResultado;
import com.elfec.lecturas.modelo.interfaces.IExportable;
import com.elfec.lecturas.settings.VariablesDeEntorno;
import com.elfec.lecturas.settings.VariablesDeSesion;

/**
 * Almacena la información sobre los ordenativos que se asignaron a las
 * lecturas, esta tabla se descarga en la tabla ERP_ELFEC.SGC_MOVIL_ORDENATIVOS
 * de la base de datos ORACLE
 * 
 * @author drodriguez
 */
@Table(name = "OrdenativosLectura")
public class OrdenativoLectura extends Model implements
		EventoAlObtenerResultado, IModeloBackupableTexto, IExportable {

	private static final String INSERT_QUERY = "INSERT INTO ERP_ELFEC.SGC_MOVIL_ORDENATIVOS VALUES (%d, %d, %d, %d, %d, %d, "
			+ "TO_DATE('%s', 'dd/mm/yyyy hh24:mi:ss'), UPPER('%s'), USER , SYSDATE)";

	public OrdenativoLectura() {
		super();
	}

	public OrdenativoLectura(Ordenativo ord, Lectura lec, Date fecha) {
		super();
		Ordenativo = ord;
		Lectura = lec;
		Fecha = fecha;
		DateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
		Hora = df.format(fecha);
	}

	@Column(name = "Lectura", index = true, notNull = false, onDelete = ForeignKeyAction.SET_NULL)
	public Lectura Lectura;

	@Column(name = "Ordenativo", index = true, notNull = false, onDelete = ForeignKeyAction.SET_NULL)
	public Ordenativo Ordenativo;

	@Column(name = "Fecha", notNull = true)
	public Date Fecha;

	@Column(name = "Hora", notNull = true)
	public String Hora;

	/**
	 * Cuando fue enviado por 3g tiene el estado 1, 0 en caso contrario , 2 en
	 * caso de que se lo haya enviado por cualquier otro medio
	 */
	@Column(name = "Enviado3G")
	public int Enviado3G;
	/**
	 * El usuario que asignó el ordenativo a la lectura
	 */
	@Column(name = "UsuarioAuditoria")
	public String UsuarioAuditoria;

	/**
	 * Accede a la base de datos y obtiene todos los ordenativos de lecturas
	 * 
	 * @return Todos los ordenativos asignados a lecturas
	 */
	public static List<OrdenativoLectura> obtenerTodasLecturasConOrdenativos() {

		return new Select().from(OrdenativoLectura.class).execute();
	}

	/**
	 * Accede a la base de datos y obtiene todos los ordenativos de lecturas ,
	 * que tienen el atributo Enviado3G=0
	 * 
	 * @return Todos los ordenativos asignados a lecturas que no se enviaron por
	 *         3g
	 */
	public static List<OrdenativoLectura> obtenerLecturasConOrdenativosNoEnviados3G() {

		return new Select().from(OrdenativoLectura.class).where("Enviado3G=0")
				.execute();
	}

	/**
	 * Accede a la base de datos y obtiene las lecturas con ordenativos,
	 * ordenados por orden de cuenta de la lectura
	 * 
	 * @return las lecturas que tienen los ordenativos definidos en las
	 *         variables de entorno, parametrizable
	 */
	public static List<OrdenativoLectura> obtenerLecturasConOrdenativosParametros() {
		StringBuilder ordenativos = new StringBuilder("");
		int i = 0;
		int tam = VariablesDeEntorno.codigosOrdenativosResumen.size();
		for (Integer codOrd : VariablesDeEntorno.codigosOrdenativosResumen) {
			ordenativos.append("O.IDNOVEDAD=");
			ordenativos.append(codOrd);
			if (i < tam - 1)
				ordenativos.append(" OR ");
			i++;
		}
		return new Select()
				.from(OrdenativoLectura.class)
				.where("Ordenativo IN (SELECT ID FROM Ordenativos O WHERE "
						+ ordenativos + ")").execute();
	}

	@Override
	public void procesarResultado(double resultado) {
		if (resultado == 1.0) {
			Enviado3G = 1;
			this.save();
		}
	}

	/**
	 * Guarda el ordenativo en la base de datos sqlite e intenta enviarlo por
	 * 3G, tambien lo guarda en el archivo de texto de backup
	 * 
	 * @return retorna el Id que se le asignó
	 */
	public Long guardarYEnviarPor3G() {
		UsuarioAuditoria = VariablesDeSesion.getUsuarioLogeado();
		Long id = this.save();
		ManejadorBackupTexto.guardarBackupModelo(this);
		ManejadorConexionRemota.guardarOrdenativo(this);
		return id;
	}

	@Override
	public String obtenerLineaTextoBackup() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
		String fechaHora = df.format(this.Fecha) + " " + this.Hora;
		StringBuilder str = new StringBuilder("");
		str.append(Lectura.Ruta).append("|").append(Lectura.Anio).append("|")
				.append(Lectura.Mes).append("|").append(Lectura.Dia)
				.append("|").append(Lectura.Suministro).append("|")
				.append(Lectura.Cuenta).append("|")
				.append(Lectura.NumeroMedidor).append("|")
				.append(Ordenativo.Codigo).append("|").append(fechaHora)
				.append("|").append(UsuarioAuditoria);
		return str.toString();
	}

	@Override
	public String obtenerNombreArchivoBackup() {
		return "ordenativos_backup";
	}

	@Override
	public String obtenerCabeceraBackup() {
		return "Ruta|Anio|Mes|Dia|Suministro|Cuenta|Numero Medidor|Codigo|Fecha y Hora Agregado|Usuario";
	}

	@Override
	public void setExportStatus(EstadoExportacion estadoExportacion) {
		Enviado3G = estadoExportacion == EstadoExportacion.EXPORTADO ? 2 : 0;
	}

	@Override
	public String getRegistryResume() {
		return "Ordenativo: <b>" + Ordenativo.Codigo
				+ "</b> de la lectura con suministro: <b>" + Lectura.Suministro
				+ "</b>";
	}

	/**
	 * Obtiene la insert query SQL remota de la lectura
	 * 
	 * @return {@link OrdenativoLectura#INSERT_QUERY}
	 */
	public String toRemoteInsertQuery() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
		String fechaHora = df.format(Fecha) + " " + Hora;
		return String.format(Locale.getDefault(), INSERT_QUERY, Lectura.Ruta,
				Lectura.Anio, Lectura.Mes, Lectura.Dia, Lectura.Suministro,
				Ordenativo.Codigo, fechaHora, UsuarioAuditoria);
	}
}
