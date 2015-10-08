package com.elfec.lecturas.modelo;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

/**
 * Almacena la información sobre conceptos, de la tabla ERP_ELFEC.CONCEPTOS de
 * la BD Oracle
 * 
 * @author drodriguez
 */
@Table(name = "Conceptos")
public class Concepto extends Model {

	public Concepto() {
		super();
	}

	public Concepto(ResultSet rs) throws SQLException {
		super();
		IdTipoServicio = rs.getInt("IDTIPO_SRV");
		IdConcepto = rs.getInt("IDCONCEPTO");
		IdSubConcepto = rs.getInt("IDSUBCONCEPTO");
		Descripcion = rs.getString("DESCRIPCION");
		CodigoConcepto = rs.getString("CODCONCEPTO");
		IdGrupo = rs.getInt("IDGRUPO");
		IdMetodo = rs.getInt("IDMETODO");
		AreaImpresion = rs.getInt("IMPRESION_AREA");
		IdStatus = rs.getInt("IDSTATUS");
		TipoPartida = rs.getInt("TIPO_PART");
	}

	@Column(name = "IDCONCEPTO", notNull = true, index = true)
	public int IdConcepto;

	@Column(name = "IDSUBCONCEPTO", notNull = true, index = true)
	public int IdSubConcepto;

	@Column(name = "IDTIPO_SRV", notNull = true)
	public int IdTipoServicio;

	@Column(name = "DESCRIPCION")
	public String Descripcion;

	@Column(name = "CODCONCEPTO")
	public String CodigoConcepto;

	@Column(name = "IDGRUPO")
	public int IdGrupo;

	@Column(name = "IDMETODO")
	public int IdMetodo;

	@Column(name = "IMPRESION_AREA")
	public int AreaImpresion;

	@Column(name = "IDSTATUS")
	public int IdStatus;

	@Column(name = "TIPO_PART")
	public int TipoPartida;

	/**
	 * Accede a la base de datos y obtiene el concepto correspondiente al
	 * IDCONCEPTO y IDSUBCONCEPTO proporcionados
	 * 
	 * @param idConcepto
	 * @param idSubConcepto
	 * @return Concepto que coincida, null si no se encuentra
	 */
	public static Concepto obtenerConcepto(int idConcepto, int idSubConcepto) {
		if (idConcepto >= 12000)
			idConcepto -= 2000;
		return new Select()
				.from(Concepto.class)
				.where("IDCONCEPTO=" + idConcepto + " AND IDSUBCONCEPTO="
						+ idSubConcepto).executeSingle();
	}

	/**
	 * ELimina todos los conceptos guardados
	 */
	public static void eliminarTodosLosConceptos() {
		new Delete().from(Concepto.class).execute();
	}

}
