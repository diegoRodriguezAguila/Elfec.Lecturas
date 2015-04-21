package com.lecturas.elfec.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
/**
 * Almacena la información sobre la potencia de las lecturas, de la tabla MOVILES.LECTURASP de la BD Moviles de Oracle
 * @author drodriguez
 */
@Table(name = "Potencias")
public class Potencia extends Model {

	@Column(name = "ConsumoFacturado")
	public int ConsumoFacturado;
	public Potencia()
	{
		super();
	}
	public Potencia(ResultSet rs) throws SQLException
	{
		super();
		Suministro = rs.getLong("LEMSUM");
		Ruta = rs.getInt("LEMRUT");
		Reactiva = rs.getBigDecimal("LEMREACTI");
		PromedioPotencia = rs.getInt("LEMPROPOT");
		LecturaNuevaPotencia = rs.getBigDecimal("LEMNVALPO");
		Mes = rs.getByte("LEMMES");
		Anio=rs.getShort("LEMANO");
		Dia=rs.getShort("LEMARE");
		LecturaAnteriorPotencia = rs.getInt("LEMLANPOT");
		HoraLecturaPotencia = rs.getString("LEMHORLEP");
		FactorMultiplicadorPotencia = rs.getBigDecimal("LEMFMUPOT");
		Demanda = rs.getInt("LEMDEMAN");
		CodigoCliente = rs.getLong("LEMCLI");
	}
	
	public Potencia(Lectura lec)
	{
		super();
		Suministro = lec.Suministro;
		Ruta = lec.Ruta;
		Reactiva = null;
		PromedioPotencia = 0;
		LecturaNuevaPotencia = null;
		Mes = lec.Mes;
		Anio= lec.Anio;
		Dia= lec.Dia;
		LecturaAnteriorPotencia = lec.PotenciaFacturada;
		HoraLecturaPotencia = null;
		FactorMultiplicadorPotencia = new BigDecimal(1);
		Demanda = 0;
		CodigoCliente = lec.CodigoCLiente;
	}
	
	@Column(name = "LEMSUM", notNull=true, index=true)
	public long Suministro;
	
	@Column(name = "LEMRUT", notNull=true, index=true)
	public int Ruta;		
	
	@Column(name = "LEMREACTI")
	public BigDecimal Reactiva;	
		
	@Column(name = "LEMPROPOT")
	public int PromedioPotencia;
	
	@Column(name = "LEMNVALPO")
	public BigDecimal LecturaNuevaPotencia;
	
	@Column(name = "LEMMES", notNull=true, index=true)
	public int Mes;
	
	@Column(name = "LEMANO", notNull=true, index=true)
	public int Anio;
	
	@Column(name = "LEMDIA", notNull=true)
	public int Dia;
	
	@Column(name = "LEMCLI", notNull=true)
	public long CodigoCliente;
			
	@Column(name = "LEMLANPOT")
	public int LecturaAnteriorPotencia;
				
	@Column(name = "LEMHORLEP")
	public String HoraLecturaPotencia;
	
	@Column(name = "LEMFMUPOT")
	public BigDecimal FactorMultiplicadorPotencia;
	
	@Column(name = "LEMFECLEP")
	public Date FechaLecturaPotencia;
		
	@Column(name = "LEMDEMAN")
	public int Demanda;
	
	/**
	 * Accede a la base de datos y obtiene la potencia que coincida con el numero de suministro, mes y anio
	 * @param suministro
	 * @param mes
	 * @param anio
	 * @return La potencia que coincida con esos parámetros
	 */
	public static Potencia obtenerPotencia(long suministro, int mes, int anio)
	{
		return new Select()
        .from(Potencia.class).where("LEMSUM=?",suministro).where("LEMMES=?",mes).where("LEMANO=?",anio)
        .executeSingle();
	}
	
	/**
	 * Asigna los valores de la lecturaNueva, asi como la fecha de la lectura y
	 * asigna el consumo, no realiza el guardado en base de datos
	 * @param lecturaPotNueva, el valor del medidor de la nueva lectura
	 * @param reactiva, valor de la energia reactiva, puede ser nulo
	 * @param lectura, la lectura a la que pertenece la potencia;
	 */
	public void leerPotencia(BigDecimal lecturaPotNueva, BigDecimal reactiva, Lectura lectura) {
		LecturaNuevaPotencia = lecturaPotNueva;
		Reactiva = reactiva;
		FechaLecturaPotencia = lectura.FechaLecturaActual;
		HoraLecturaPotencia = lectura.HoraLectura;
		Demanda = (lecturaPotNueva.multiply(FactorMultiplicadorPotencia)).setScale(0,RoundingMode.HALF_EVEN).intValue();
		ConsumoFacturado = Math.max(LecturaAnteriorPotencia, Demanda);
    }
	
	/**
	 * Elimina los datos de , ConsumoFacturado, FechaLecturaPotencia, HoraLecturaPotencia, Demanda.
	 * todos estos cambios no se guardan en la base de datos, es necesario llamar posteriormente a guardar para 
	 * que los cambios sean permanentes
	 * Se utiliza para poder dejar la lectura en estado modificable.
	 */
	public void resetearLecturaPotencia()
	{
		LecturaNuevaPotencia = null;
		ConsumoFacturado = 0;
		Demanda = 0;
		FechaLecturaPotencia = null;
		HoraLecturaPotencia = null;
	}
					
}
