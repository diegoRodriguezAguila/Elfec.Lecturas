package com.elfec.lecturas.modelo;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

/**
 * Almacena la información sobre la evolución de consumos, de la tabla
 * MOVILES.LECTURASC de la BD Moviles de Oracle
 * 
 * @author drodriguez
 */
@Table(name = "EvolucionConsumos")
public class EvolucionConsumo extends Model {

	public EvolucionConsumo() {
		super();
	}

	public EvolucionConsumo(ResultSet rs) throws SQLException {
		super();
		Suministro = rs.getLong("LEMSUM");
		Ruta = rs.getInt("LEMRUT");
		Mes = rs.getByte("LEMMES");
		Anio = rs.getShort("LEMANO");
		Dia = rs.getShort("LEMARE");
		CodigoCliente = rs.getLong("LEMCLI");
		Mes01 = rs.getString("LEMMES01");
		ConsumoKWH01 = rs.getInt("LEMCONKWH01");
		Mes02 = rs.getString("LEMMES02");
		ConsumoKWH02 = rs.getInt("LEMCONKWH02");
		Mes03 = rs.getString("LEMMES03");
		ConsumoKWH03 = rs.getInt("LEMCONKWH03");
		Mes04 = rs.getString("LEMMES04");
		ConsumoKWH04 = rs.getInt("LEMCONKWH04");
		Mes05 = rs.getString("LEMMES05");
		ConsumoKWH05 = rs.getInt("LEMCONKWH05");
		Mes06 = rs.getString("LEMMES06");
		ConsumoKWH06 = rs.getInt("LEMCONKWH06");
		Mes07 = rs.getString("LEMMES07");
		ConsumoKWH07 = rs.getInt("LEMCONKWH07");
		Mes08 = rs.getString("LEMMES08");
		ConsumoKWH08 = rs.getInt("LEMCONKWH08");
		Mes09 = rs.getString("LEMMES09");
		ConsumoKWH09 = rs.getInt("LEMCONKWH09");
		Mes10 = rs.getString("LEMMES10");
		ConsumoKWH10 = rs.getInt("LEMCONKWH10");
		Mes11 = rs.getString("LEMMES11");
		ConsumoKWH11 = rs.getInt("LEMCONKWH11");
		Mes12 = rs.getString("LEMMES12");
		ConsumoKWH12 = rs.getInt("LEMCONKWH12");
	}

	@Column(name = "LEMANO", notNull = true, index = true)
	public int Anio;

	@Column(name = "LEMMES", notNull = true, index = true)
	public int Mes;

	@Column(name = "LEMCLI", notNull = true)
	public long CodigoCliente;

	@Column(name = "LEMDIA", notNull = true)
	public int Dia;

	@Column(name = "LEMSUM", notNull = true, index = true)
	public long Suministro;

	@Column(name = "LEMRUT", notNull = true, index = true)
	public int Ruta;

	@Column(name = "LEMMES01")
	public String Mes01;

	@Column(name = "LEMCONKWH01")
	public int ConsumoKWH01;

	@Column(name = "LEMMES02")
	public String Mes02;

	@Column(name = "LEMCONKWH02")
	public int ConsumoKWH02;

	@Column(name = "LEMMES03")
	public String Mes03;

	@Column(name = "LEMCONKWH03")
	public int ConsumoKWH03;

	@Column(name = "LEMMES04")
	public String Mes04;

	@Column(name = "LEMCONKWH04")
	public int ConsumoKWH04;

	@Column(name = "LEMMES05")
	public String Mes05;

	@Column(name = "LEMCONKWH05")
	public int ConsumoKWH05;

	@Column(name = "LEMMES06")
	public String Mes06;

	@Column(name = "LEMCONKWH06")
	public int ConsumoKWH06;

	@Column(name = "LEMMES07")
	public String Mes07;

	@Column(name = "LEMCONKWH07")
	public int ConsumoKWH07;

	@Column(name = "LEMMES08")
	public String Mes08;

	@Column(name = "LEMCONKWH08")
	public int ConsumoKWH08;

	@Column(name = "LEMMES09")
	public String Mes09;

	@Column(name = "LEMCONKWH09")
	public int ConsumoKWH09;

	@Column(name = "LEMMES10")
	public String Mes10;

	@Column(name = "LEMCONKWH10")
	public int ConsumoKWH10;

	@Column(name = "LEMMES011")
	public String Mes11;

	@Column(name = "LEMCONKWH11")
	public int ConsumoKWH11;

	@Column(name = "LEMMES012")
	public String Mes12;

	@Column(name = "LEMCONKWH12")
	public int ConsumoKWH12;

	/**
	 * Accede a la base de datos y obtiene la evolucion de consumo que coincida
	 * con el numero de suministro, mes y anio
	 * 
	 * @param suministro
	 * @param mes
	 * @param anio
	 * @return La evolucion de consumo que coincida con esos parámetros, null si
	 *         no se encuentra
	 */
	public static EvolucionConsumo obtenerEvolucionConsumo(long suministro,
			int mes, int anio) {
		return new Select().from(EvolucionConsumo.class)
				.where("LEMSUM=?", suministro).where("LEMMES=?", mes)
				.where("LEMANO=?", anio).executeSingle();
	}

	/**
	 * Obtiene el maximo de los consumos que tiene.
	 * 
	 * @return
	 */
	public int obtenerMaximoConsumo() {
		return Math.max(ConsumoKWH01, Math.max(ConsumoKWH02, Math.max(
				ConsumoKWH03, Math.max(ConsumoKWH04, Math.max(ConsumoKWH05,
						Math.max(ConsumoKWH06,
								Math.max(ConsumoKWH07, Math.max(ConsumoKWH08,
										Math.max(ConsumoKWH09, Math.max(
												ConsumoKWH10, Math.max(
														ConsumoKWH11,
														ConsumoKWH12)))))))))));
	}

	/**
	 * Elimina toda la información de evolución de consumos de lecturas que
	 * pertenezcan a la asignación de ruta dada
	 * 
	 * @param asignacionRuta
	 * @param nusClausulaIN
	 */
	public static void eliminarEvConsumosDeRutaAsignada(
			AsignacionRuta asignacionRuta, String nusClausulaIN) {
		new Delete()
				.from(EvolucionConsumo.class)
				.where("LEMRUT = ? AND LEMDIA = ? AND LEMMES = ? AND LEMANO = ?",
						asignacionRuta.Ruta, asignacionRuta.Dia,
						asignacionRuta.Mes, asignacionRuta.Anio)
				.where("LEMSUM IN " + nusClausulaIN).execute();
	}

}
