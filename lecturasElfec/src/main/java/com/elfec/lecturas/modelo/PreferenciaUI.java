package com.elfec.lecturas.modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import android.annotation.SuppressLint;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;


/**
 * Almacena la información sobre las personalizaciones y preferencias de UI definidas por los usuarios,
 * se copia su esta información el momento en que el usuario se logea por primera vez en el dispositivo
 * de la tabla MOVILES.USUARIO_PREFERENCIAS_UI de la BD Moviles de Oracle
 * @author drodriguez
 */
@Table(name = "PreferenciasUI")
public class PreferenciaUI extends Model{

	@Column(name = "Usuario", index = true, notNull=true)
	public String Usuario;
	
	@Column(name = "Opcion", notNull=true)
	public String Opcion;
	
	@Column(name = "Valor")
	public String Valor;
	
	public PreferenciaUI()
	{
		super();
	}
	
	public PreferenciaUI(String usuario, String opcion, String valor)
	{
		super();
		Usuario = usuario;
		Opcion = opcion;
		Valor = valor;
	}
	
	public PreferenciaUI(ResultSet rs) throws SQLException
	{
		super();
		Usuario = rs.getString("USUARIO");
		Opcion = rs.getString("OPCION");
		Valor = rs.getString("VALOR");
	}
	
	/**
	 * Obtiene el valor de la preferencia con el tipo pasado en los parametros
	 * @param type el tipo a retornar, las llamadas tendrian que ser con la clase de un tipo por ejemplo: Integer.class<br>
	 * Los tipos que se soportan son Integer, Double y String, otros tipos retornaran null
	 * @return
	 */
	@SuppressLint("UseValueOf")
	@SuppressWarnings("unchecked")
	public <T> T obtenerValor(Class<T> type)
	{
		if (type.getName().equals(Integer.class.getName()))
		{
			return (T) new Integer(Integer.parseInt(Valor));
		}
		if (type.getName().equals(Double.class.getName()))
		{
			return (T) new Double(Double.parseDouble(Valor));
		}
		if (type.getName().equals(String.class.getName()))
		{
			return (T) Valor;
		}
		return null;
	}
	
	
	/**
	 * Accede a la base de datos y obtiene todas las preferencias de UI del usuario <b>usuario</b> proporcionado
	 * @param usuario
	 * @return Lista de Preferencias UI 
	 */
	public static List<PreferenciaUI> obtenerPreferenciasUIPorUsuario(String usuario)
	{
		return new Select()
        .from(PreferenciaUI.class).where("Usuario=?",usuario)
        .execute();
	}
}
