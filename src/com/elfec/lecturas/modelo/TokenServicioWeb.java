package com.elfec.lecturas.modelo;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.elfec.lecturas.helpers.VariablesDeSesion;

/**
 * Guarda los tokens para el uso del servicio web mediante ip publica
 * los tokens deben sacarse de la bd oracle al momento de la descarga
 * @author drodriguez
 *
 */
@Table(name = "TokensServicioWeb")
public class TokenServicioWeb extends Model{
	
	@Column(name = "Usuario")
	public String Usuario;
	
	@Column(name = "Fecha")
	public Date Fecha;
	
	@Column(name = "Token")
	public String Token;
	
	public TokenServicioWeb()
	{
		super();
	}
	
	public TokenServicioWeb(ResultSet rs) throws SQLException, UnsupportedEncodingException
	{
		super();
		Usuario = rs.getString("USUARIO").toLowerCase(Locale.getDefault());
		Calendar cal = Calendar.getInstance();
		cal.setTime(rs.getDate("FECHA"));
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
		Fecha = cal.getTime();
		byte[] token = rs.getBytes("TOKEN");
		Token = new String(token, "UTF-8");
	}
	/**
	 * Retorna el token para el usuario logeado y segun la fecha actuales, -1 en el caso
	 * de que no exista un token para el usuario logeado en la fecha actual
	 * @return
	 */
	public static String obtenerTokenActual()
	{
		Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
		TokenServicioWeb tokenActual = ((TokenServicioWeb) new Select()
        .from(TokenServicioWeb.class).where("Usuario = ?",VariablesDeSesion.getUsuarioLogeado())
        .where("Fecha = ?",cal.getTime().getTime())
        .executeSingle());
        return ( tokenActual!=null ? tokenActual.Token : "-1");
	}
	
	public static List<TokenServicioWeb> obtenerTodosLosTokens()
	{
		return new Select()
        .from(TokenServicioWeb.class).where("Usuario = ?", VariablesDeSesion.getUsuarioLogeado()).execute();
	}
}
