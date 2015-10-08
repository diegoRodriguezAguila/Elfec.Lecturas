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
 * ERP_ELFEC.CPTOS_CATEGORIAS de la BD Oracle
 * 
 * @author drodriguez
 */
@Table(name = "ConceptosCategorias")
public class ConceptoCategoria extends Model {

	// atributos nuevos
	@Column(name = "Concepto", index = true, notNull = false, onDelete = ForeignKeyAction.SET_NULL)
	public Concepto Concepto;

	public ConceptoCategoria() {
		super();
	}

	public ConceptoCategoria(ResultSet rs) throws SQLException {
		super();
		IdTipoServicio = rs.getInt("IDTIPO_SRV");
		IdCategoria = rs.getString("IDCATEGORIA");
		IdCuadroTarifario = rs.getInt("IDCUADRO_TARIF");
		IdConcepto = rs.getInt("IDCONCEPTO");
		IdSubConcepto = rs.getInt("IDSUBCONCEPTO");
		LimiteValor = rs.getInt("LIM_VALOR");
		FrecuenciaLimite = rs.getBigDecimal("LIM_FRECUENCIA");
		EsMinimo = rs.getInt("LIM_ESMINIMO");
		PermiteRecalculo = rs.getInt("LIM_RECALCULO");
		TipoCalculo = rs.getString("TIPO_CALCULO");
		IdGrupo = rs.getInt("IDGRUPO");
		TipoTarifa = rs.getString("TIPO_TARIFA");
		Tasa = rs.getBigDecimal("TASA");
		Importe = rs.getBigDecimal("IMPORTE");
		ImporteReferencial = rs.getBigDecimal("IMPORTE_REF");
		IdBaseCalculo = rs.getInt("IDBASE_CALCULO");
		SobreReferencia = rs.getInt("SOBRE_REFERENCIA");
		IdMoneda = rs.getInt("IDMONEDA");
		Aplicabilidad = rs.getString("APLICABILIDAD");
		OrdenCalculo = rs.getInt("ORDEN_CALCULO");
		OrdenImpresion = rs.getInt("ORDEN_IMPRESION");
		IncluirAuto = rs.getInt("INCLUIR_AUTO");
		Script = rs.getString("SCRIPT");
		ScriptReferencial = rs.getString("SCRIPT_REF");
		IdTipoCategoriaReferencial = rs.getInt("IDTIPO_CATEG_REF");
		IdConceptoReferencial = rs.getInt("IDCONCEPTO_REF");
		IdSubConceptoReferencial = rs.getInt("IDSUBCONCEPTO_REF");
		ScriptActualizacion = rs.getString("SCRIPT_PARA_ACT");
		IdBaseCalculoCon = rs.getInt("IDBASE_CALCULO_CON");
	}

	@Column(name = "IDTIPO_SRV", notNull = true)
	public int IdTipoServicio;

	@Column(name = "IDCATEGORIA", notNull = true, index = true)
	public String IdCategoria;

	@Column(name = "IDCUADRO_TARIF", notNull = true)
	public int IdCuadroTarifario;

	@Column(name = "IDCONCEPTO", notNull = true, index = true)
	public int IdConcepto;

	@Column(name = "IDSUBCONCEPTO", notNull = true, index = true)
	public int IdSubConcepto;

	@Column(name = "LIM_VALOR")
	public int LimiteValor;

	@Column(name = "LIM_FRECUENCIA")
	public BigDecimal FrecuenciaLimite;

	@Column(name = "LIM_ESMINIMO")
	public int EsMinimo;

	@Column(name = "LIM_RECALCULO")
	public int PermiteRecalculo;

	@Column(name = "TIPO_CALCULO")
	public String TipoCalculo;

	@Column(name = "IDGRUPO")
	public int IdGrupo;

	@Column(name = "TIPO_TARIFA")
	public String TipoTarifa;

	@Column(name = "TASA")
	public BigDecimal Tasa;

	@Column(name = "IMPORTE")
	public BigDecimal Importe;

	@Column(name = "IMPORTE_REF")
	public BigDecimal ImporteReferencial;

	@Column(name = "IDBASE_CALCULO", index = true)
	public int IdBaseCalculo;

	@Column(name = "SOBRE_REFERENCIA")
	public int SobreReferencia;

	@Column(name = "IDMONEDA")
	public int IdMoneda;

	@Column(name = "APLICABILIDAD")
	public String Aplicabilidad;

	@Column(name = "ORDEN_CALCULO")
	public int OrdenCalculo;

	@Column(name = "ORDEN_IMPRESION")
	public int OrdenImpresion;

	@Column(name = "INCLUIR_AUTO")
	public int IncluirAuto;

	@Column(name = "SCRIPT")
	public String Script;

	@Column(name = "SCRIPT_REF")
	public String ScriptReferencial;

	@Column(name = "IDTIPO_CATEG_REF")
	public int IdTipoCategoriaReferencial;

	@Column(name = "IDCONCEPTO_REF")
	public int IdConceptoReferencial;

	@Column(name = "IDSUBCONCEPTO_REF")
	public int IdSubConceptoReferencial;

	@Column(name = "SCRIPT_PARA_ACT")
	public String ScriptActualizacion;

	@Column(name = "IDBASE_CALCULO_CON")
	public int IdBaseCalculoCon;

	/**
	 * Accede a la base de datos y obtiene todos los conceptos por categoria
	 * 
	 * @param categoria
	 * @return Lista de ConceptoCategorias en orden de ORDEN_CALCULO,
	 *         IDCONCEPTO, LIM_VALOR, IDSUBCONCEPTO
	 */
	public static List<ConceptoCategoria> obtenerConceptoCategoriasPorCategoria(
			String categoria) {
		return new Select().from(ConceptoCategoria.class)
				.where("IDCATEGORIA=?", categoria)
				.orderBy("ORDEN_CALCULO, IDCONCEPTO, LIM_VALOR, IDSUBCONCEPTO")
				.execute();
	}

	/**
	 * Accede a la base de datos y obtiene todos los conceptos por categoria y
	 * aplicabilidad
	 * 
	 * @param categoria
	 * @return Lista de ConceptoCategorias en orden de ORDEN_CALCULO,
	 *         IDCONCEPTO, LIM_VALOR, IDSUBCONCEPTO
	 */
	public static List<ConceptoCategoria> obtenerConceptoCategoriasPorCategoriaYAplicabilidad(
			String categoria, String aplicabilidad) {
		return new Select()
				.from(ConceptoCategoria.class)
				.where("IDCATEGORIA='" + categoria + "' AND APLICABILIDAD='"
						+ aplicabilidad + "'")
				.orderBy("ORDEN_CALCULO, IDCONCEPTO, LIM_VALOR, IDSUBCONCEPTO")
				.execute();
	}

	/**
	 * ELimina todos los conceptos categorias guardados
	 */
	public static void eliminarTodosLosConceptosCatgeorias() {
		new Delete().from(ConceptoCategoria.class).execute();
	}

}
