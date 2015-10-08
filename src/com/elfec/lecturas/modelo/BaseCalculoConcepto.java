package com.elfec.lecturas.modelo;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.elfec.lecturas.settings.VariablesDeEntorno;

/**
 * Almacena la información sobre las bases de calculo de conceptos, de la tabla
 * ERP_ELFEC.GBASES_CALC_CPTOS de la BD Oracle
 * 
 * @author drodriguez
 */
@Table(name = "BasesCalculoConceptos")
public class BaseCalculoConcepto extends Model {

	// atributos nuevos
	@Column(name = "BaseCalculo", index = true, notNull = false, onDelete = ForeignKeyAction.SET_NULL)
	public BaseCalculo BaseCalculo;

	@Column(name = "Concepto", index = true, notNull = false, onDelete = ForeignKeyAction.SET_NULL)
	public Concepto Concepto;

	public BaseCalculoConcepto() {
		super();
	}

	public BaseCalculoConcepto(ResultSet rs) throws SQLException {
		super();

		IdBaseCalculo = rs.getInt("IDBASE_CALCULO");
		IdConcepto = rs.getInt("IDCONCEPTO");
		IdSubConcepto = rs.getInt("IDSUBCONCEPTO");
	}

	@Column(name = "IDBASE_CALCULO", notNull = true, index = true)
	public int IdBaseCalculo;

	@Column(name = "IDCONCEPTO", notNull = true, index = true)
	public int IdConcepto;

	@Column(name = "IDSUBCONCEPTO", notNull = true, index = true)
	public int IdSubConcepto;

	/**
	 * Verifica si un subconcepto pertenece a la base de calculo solicitada
	 * 
	 * @param idBaseCalculo
	 * @param idConcepto
	 * @param idSubConcepto
	 * @return true: si el subconcepto pertenece a la base de calculo, false
	 *         caso contrario
	 */
	public static boolean subConceptoPerteneceABaseCalculo(int idBaseCalculo,
			int idConcepto, int idSubConcepto) {
		return new Select()
				.from(BaseCalculoConcepto.class)
				.where("IDBASE_CALCULO=" + idBaseCalculo + " AND IDCONCEPTO="
						+ idConcepto + " AND IDSUBCONCEPTO=" + idSubConcepto)
				.executeSingle() != null;
	}

	/**
	 * Accede a la base de datos para obtener la base de calculo para imprimir
	 * (idBaseCalculo>=100) correspondiente al subconcepto
	 * 
	 * @param idBaseCalculo
	 * @param idConcepto
	 * @param idSubConcepto
	 * @return baseCalculo
	 */
	public static BaseCalculo obtenerBaseCalculoImpresion(int idConcepto,
			int idSubConcepto) {
		BaseCalculoConcepto baseCalculoConcep = new Select()
				.from(BaseCalculoConcepto.class)
				.where("IDBASE_CALCULO>="
						+ VariablesDeEntorno.idBasesCalculoImpresion
						+ " AND IDCONCEPTO=" + idConcepto
						+ " AND IDSUBCONCEPTO=" + idSubConcepto)
				.executeSingle();
		return (baseCalculoConcep != null ? baseCalculoConcep.BaseCalculo
				: obtenerBaseCalculoImpresion(idConcepto + 2000, idSubConcepto));
	}

	/**
	 * Accede a la base de datos para obtener la base de calculo de concepto
	 * correspondiente al idBaseCalculo proporcionado en caso de ser varios se
	 * retorna solo el primer elemento
	 * 
	 * @param idBaseCalculo
	 * @return baseCalculoConcepto
	 */
	public static BaseCalculoConcepto obtenerBaseCalculoConcepto(
			int idBaseCalculo) {
		return new Select().from(BaseCalculoConcepto.class)
				.where("IDBASE_CALCULO=" + idBaseCalculo).executeSingle();
	}

	/**
	 * ELimina todas las bases de calculo conceptos guardadas
	 */
	public static void eliminarTodasLasBasesDeCalculoCptos() {
		new Delete().from(BaseCalculoConcepto.class).execute();
	}

}
