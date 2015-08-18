package com.elfec.lecturas.modelo.flowfactory.validacionusuario;

import com.elfec.lecturas.modelo.Usuario;
import com.elfec.lecturas.modelo.validaciones.ErrorUsuarioPasswordIncorrectos;
import com.elfec.lecturas.modelo.validaciones.ValidacionUsuarioCorrecta;
import com.elfec.lecturas.settings.VariablesDeSesion;

/**
 * Define el paso de validación en el que se verifica que el hash del password ingresado
 *  coincide con el hash del password guardado en la bd local
 * <br><br><b>Código: PVU-1.2.2</b>
 * @author drodriguez
 */
public class PasoVerifPassword extends PasoValidacionUsuario {

	/**
	 * Verifica si coinciden los passwords, y asigna el resultado a <b>resultadoValidacion</b>.
	 * <br> <br> retorna null porque es un paso final.
	 * @see ErrorUsuarioPasswordIncorrectos
	 * @see ValidacionUsuarioCorrecta
	 */
	@Override
	public PasoValidacionUsuario procesarPaso() {
		if(!FlujoPasosValidacionUsuario.usuarioEncontrado.Password.equals
				(Usuario.hash(FlujoPasosValidacionUsuario.password)))  //contraseña no coincide
		{
			resultadoValidacion = new ErrorUsuarioPasswordIncorrectos();
		}
		else
		{
			VariablesDeSesion.setPerfilUsuario(FlujoPasosValidacionUsuario.usuarioEncontrado.Perfil);
			resultadoValidacion = new ValidacionUsuarioCorrecta();//contraseña coincide, exito de validacion
		}
		return null;
	}

}
