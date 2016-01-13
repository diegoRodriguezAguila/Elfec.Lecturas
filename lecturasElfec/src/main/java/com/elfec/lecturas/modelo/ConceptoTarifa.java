package com.elfec.lecturas.modelo;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

/**
 * Almacena la información sobre las categorias de conceptos, de la tabla
 * ERP_ELFEC.CONCEPTOS_TARIFAS de la BD Oracle
 * 
 * @author drodriguez
 */
@Table(name = "ConceptosTarifas")
public class ConceptoTarifa extends Model {

	// atributos nuevos
	@Column(name = "Concepto", index = true, notNull = false, onDelete = ForeignKeyAction.SET_NULL)
	public Concepto Concepto;

	public ConceptoTarifa() {
		super();
	}

	public ConceptoTarifa(ResultSet rs) throws SQLException {
		super();

		IdTipoServicio = rs.getInt("IDTIPO_SRV");
		IdCuadroTarifario = rs.getInt("IDCUADRO_TARIF");
		IdConcepto = rs.getInt("IDCONCEPTO");
		IdSubConcepto = rs.getInt("IDSUBCONCEPTO");
		TipoTarifa = rs.getString("TIPO_TARIFA");
		Tasa = rs.getBigDecimal("TASA");
		Importe = rs.getBigDecimal("IMPORTE");
		IdMoneda = rs.getInt("IDMONEDA");
		Aplicabilidad = rs.getString("APLICABILIDAD");
		AfectaContrato = rs.getInt("AFECTA_CONTRATO");
		SobreReferencia = rs.getInt("SOBRE_REFERENCIA");
		IdGrupo = rs.getInt("IDGRUPO");
		IdBaseCalculo = rs.getInt("IDBASE_CALCULO");
		MontoMinimo = rs.getString("MONTO_MINIMO");
		BaseAfectar = rs.getInt("BASE_AFECTAR");
		OrdenCalculo = rs.getInt("ORDEN_CALCULO");
		OrdenImpresion = rs.getInt("ORDEN_IMPRESION");
		IncluirAuto = rs.getInt("INCLUIR_AUTO");
		Script = rs.getString("SCRIPT");
		ScriptRef = rs.getString("SCRIPT_REF");
	}

	@Column(name = "IDTIPO_SRV", notNull = true)
	public int IdTipoServicio;

	@Column(name = "IDCUADRO_TARIF", notNull = true, index = true)
	public int IdCuadroTarifario;

	@Column(name = "IDCONCEPTO", notNull = true, index = true)
	public int IdConcepto;

	@Column(name = "IDSUBCONCEPTO", notNull = true, index = true)
	public int IdSubConcepto;

	@Column(name = "TIPO_TARIFA")
	public String TipoTarifa;

	@Column(name = "TASA")
	public BigDecimal Tasa;

	@Column(name = "IMPORTE")
	public BigDecimal Importe;

	@Column(name = "IDMONEDA")
	public int IdMoneda;

	@Column(name = "APLICABILIDAD")
	public String Aplicabilidad;

	@Column(name = "AFECTA_CONTRATO")
	public int AfectaContrato;

	@Column(name = "SOBRE_REFERENCIA")
	public int SobreReferencia;

	@Column(name = "IDGRUPO")
	public int IdGrupo;

	@Column(name = "IDBASE_CALCULO")
	public int IdBaseCalculo;

	@Column(name = "MONTO_MINIMO")
	public String MontoMinimo;

	@Column(name = "BASE_AFECTAR")
	public int BaseAfectar;

	@Column(name = "ORDEN_CALCULO")
	public int OrdenCalculo;

	@Column(name = "ORDEN_IMPRESION")
	public int OrdenImpresion;

	@Column(name = "INCLUIR_AUTO")
	public int IncluirAuto;

	@Column(name = "SCRIPT")
	public String Script;

	@Column(name = "SCRIPT_REF")
	public String ScriptRef;

	/**
	 * Accede a la base de datos y obtiene todos los concepto tarifas por
	 * aplicabilidad
	 * 
	 * @param aplicabilidad
	 * @return Lista de ConceptoTarifas en orden de ORDEN_CALCULO
	 */
	public static List<ConceptoTarifa> obtenerConceptoTarifasPorAplicabilidad(
			String aplicabilidad) {
		return new Select().from(ConceptoTarifa.class)
				.where("APLICABILIDAD='" + aplicabilidad + "'")
				.orderBy("ORDEN_CALCULO").execute();
	}

	/**
	 * ELimina todos los conceptos tarifas guardados
	 */
	public static void eliminarTodosLosConceptosTarifas() {
		new Delete().from(ConceptoTarifa.class).execute();
	}

}
