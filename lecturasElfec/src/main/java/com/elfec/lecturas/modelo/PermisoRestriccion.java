package com.elfec.lecturas.modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

/**
 * Almacena la información sobre los permisos y restricciones de los usuarios del sistema, una vez que un usuario inicia sesión en la red interna de elfec
 * se copia su información de la tabla MOVILES.PERFIL_APP_OPCIONES de la BD Moviles de Oracle
 * @author drodriguez
 */
@Table(name = "PermisosYRestricciones")
public class PermisoRestriccion extends Model{


	
	@Column(name = "Perfil", notNull=true)
	public String Perfil;
	
	@Column(name = "Opcion", notNull=true)
	public String Opcion;
	
	@Column(name = "Restriccion")
	public int Restriccion;
	
	public PermisoRestriccion()
	{
		super();
	}
	
	public PermisoRestriccion(ResultSet rs) throws SQLException
	{
		super();
		Opcion = rs.getString("OPCION");
		Restriccion = rs.getInt("RESTRICCION");
		Perfil = rs.getString("PERFIL");
	}
	
	/**
	 * Accede a la base de datos y obtiene todos los permisos que tengan el <b>perfil</b> proporcionado
	 * @param perfil
	 * @return Lista de PermisosYRestricciones 
	 */
	public static List<PermisoRestriccion> obtenerPermisosPorPerfil(String perfil)
	{
		return new Select()
        .from(PermisoRestriccion.class).where("Perfil=?",perfil)
        .execute();
	}
}
