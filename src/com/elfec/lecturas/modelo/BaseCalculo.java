package com.elfec.lecturas.modelo;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

/**
 * Almacena la información sobre las bases de calculo, de la tabla
 * ERP_ELFEC.GBASES_CALC de la BD ERP_ELFEC de Oracle
 * 
 * @author drodriguez
 */
@Table(name = "BasesCalculo")
public class BaseCalculo extends Model {

	// atributos nuevos
	@Column(name = "ORDEN_IMPRESION", index = true)
	public int OrdenImpresion;

	public BaseCalculo() {
		super();
	}

	public BaseCalculo(ResultSet rs) throws SQLException {
		super();

		IdBaseCalculo = rs.getInt("IDBASE_CALCULO");
		Descripcion = rs.getString("DESCRIPCION");
		OrdenImpresion = rs.getInt("ORDEN_IMPRESION");
	}

	@Column(name = "IDBASE_CALCULO", notNull = true, index = true)
	public int IdBaseCalculo;

	@Column(name = "DESCRIPCION")
	public String Descripcion;

	/**
	 * Accede a la base de datos y obtiene la base de calculo correspodiente al
	 * ID proporcionado
	 * 
	 * @param idBaseCalculo
	 * @return BaseCalculo que coincida, null si no se encuentra
	 */
	public static BaseCalculo obtenerBaseDeCalculo(int idBaseCalculo) {
		return new Select().from(BaseCalculo.class)
				.where("IDBASE_CALCULO=" + idBaseCalculo).executeSingle();
	}

	/**
	 * ELimina todas las bases de calculo guardadas
	 */
	public static void eliminarTodasLasBasesDeCalculo() {
		new Delete().from(BaseCalculo.class).execute();
	}

}
