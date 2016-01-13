package com.elfec.lecturas.modelo.flowfactory.validacionusuario;

import com.elfec.lecturas.modelo.Usuario;

/**
 * Define el paso de validación en el que se busca al usuario en la tabla local Usuarios.
 * Es el primer paso del flujo.<br><br>
 * <b>Código: PVU-1</b>
 * @author drodriguez
 *
 */
public class PasoBusquedaUsuario extends PasoValidacionUsuario {

	/**
	 * En caso de encontrar al usuario en la tabla local retorna el siguiente paso: <b>PasoVerificarFechaSinc</b>,
	 * caso contrario retorna al paso: 
	 * @see PasoVerifFechaSinc
	 */
	@Override
	public PasoValidacionUsuario procesarPaso() 
	{
		FlujoPasosValidacionUsuario.usuarioEncontrado = Usuario.obtenerUsuario(FlujoPasosValidacionUsuario.usuario);
		if(FlujoPasosValidacionUsuario.usuarioEncontrado==null)//si el usuario ya se encuentra en la base de datos local
		{
			return new PasoObtenerUsuarioOracle();
		}
		return new PasoVerifFechaSinc();
	}

}
