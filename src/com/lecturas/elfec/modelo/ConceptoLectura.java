package com.lecturas.elfec.modelo;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

/**
 * Almacena la información sobre conceptos de las lecturas, hay información de la tabla MOVILES.LECTURASCONCEPTOS de la BD Moviles de Oracle.
 * Así como también información de conceptos que se generan al crear el aviso de cobranza de la lectura
 * @author drodriguez
 */
@Table(name = "ConceptosLectura")
public class ConceptoLectura extends Model {
	
	//atributos nuevos
	@Column(name = "AreaImpresion", notNull=true, index=true)
	public int AreaImpresion;
	@Column(name = "OrdenImpresion", notNull=true, index=true)
	public int OrdenImpresion;
	/**
	 * Este atributo se utiliza para la modificación de la lectura, los conceptos que
	 * se importaron de la base de datos Oracle no son eliminables,  y tienen este campo en <b>0</b> 
	 * en cambio los conceptos que se generan al momento de crear el aviso de cobranza si son eliminables
	 * y tienen este campo en <b>1</b>.
	 */
	@Column(name = "EsEliminable", notNull=true, index=true)
	public int EsEliminable;
	
	public ConceptoLectura()
	{
		super();
	}
	
	/**
	 * Los conceptos creados con este constructor tendrán la bandera <b>EsEliminable</b> en 1, dado que se crean 
	 * al momento de generar el aviso de cobranza
	 * @param lectura
	 * @param idBaseCalculo
	 * @param descripcion
	 * @param importe
	 * @param ordenImpresion
	 * @param areaImpresion
	 */
	public ConceptoLectura(Lectura lectura, int idBaseCalculo, String descripcion,
			BigDecimal importe, int ordenImpresion, int areaImpresion) {
		super();
		Lectura = lectura;
		Anio = lectura.Anio;
		Mes = lectura.Mes;
		CodigoCliente = lectura.CodigoCLiente;
		Dia = lectura.Dia;
		Suministro = lectura.Suministro;
		Ruta = lectura.Ruta;
		ConceptoCodigo = idBaseCalculo;
		ContadorSuministro = 1;
		Descripcion = descripcion;
		Importe = importe;
		Orden = 1;
		AreaImpresion = areaImpresion;
		OrdenImpresion = ordenImpresion;
		EsEliminable = 1;
	}
	/**
	 * Los conceptos creados con este constructor tendrán la bandera <b>EsEliminable</b> en 0, dado que se crean 
	 * de un ResultSet.
	 * @param rs
	 * @throws SQLException
	 */
	public ConceptoLectura(ResultSet rs) throws SQLException
	{
		super();
		Suministro = rs.getLong("LEMSUM");
		Ruta = rs.getInt("LEMRUT");
		Mes = rs.getByte("LEMMES");
		Anio=rs.getShort("LEMANO");
		Dia=rs.getShort("LEMARE");
		CodigoCliente = rs.getLong("LEMCLI");
		ConceptoCodigo = rs.getInt("LEMCON");
		ContadorSuministro = rs.getInt("LEMCONCOR");
		Descripcion = rs.getString("LEMCONDSC");
		Importe = rs.getBigDecimal("LEMCONIMP");
		Orden = rs.getInt("LEMCONORD");
		EsEliminable = 0;
	}
	
	@Column(name = "Lectura", index=true, notNull=false, onDelete=ForeignKeyAction.SET_NULL)
    public Lectura Lectura;
	
	@Column(name = "LEMANO", notNull=true, index=true)
	public int Anio;
	
	@Column(name = "LEMMES", notNull=true, index=true)
	public int Mes;
	
	@Column(name = "LEMCLI", notNull=true)
	public long CodigoCliente;
	
	@Column(name = "LEMDIA", notNull=true)
	public int Dia;
	
	@Column(name = "LEMSUM", notNull=true, index=true)
	public long Suministro;
	
	@Column(name = "LEMRUT", notNull=true, index=true)
	public int Ruta;		
	
	@Column(name = "LEMCON")
	public int ConceptoCodigo;
	
	@Column(name = "LEMCONCOR")
	public int ContadorSuministro;
	
	@Column(name = "LEMCONDSC")
	public String Descripcion;
	
	@Column(name = "LEMCONIMP")
	public BigDecimal Importe;
	
	@Column(name = "LEMCONORD")
	public int Orden;
	
	/**
	 * Accede a la base de datos y obtiene la lectura a la que pertenece el concepto,
	 *  que coincida con el numero de suministro, mes y anio
	 * @param suministro
	 * @param mes
	 * @param anio
	 * @return La lectura que coincida con esos parámetros
	 */
	public static Lectura obtenerLectura(long suministro, int mes, int anio)
	{
		return new Select()
        .from(Lectura.class).where("LEMSUM=?",suministro).where("LEMMES=?",mes).where("LEMANO=?",anio)
        .executeSingle();
	}

}
