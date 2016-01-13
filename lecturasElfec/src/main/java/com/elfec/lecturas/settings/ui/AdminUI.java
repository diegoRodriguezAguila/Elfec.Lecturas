package com.elfec.lecturas.settings.ui;

import java.util.HashMap;
import java.util.List;

import com.elfec.lecturas.modelo.PreferenciaUI;

/**
 * Es el que maneja las preferencias de UI de los usuarios en la aplicación con un usuario dado, es un singletone 
 * y solo puede instanciarse a traves del metodo estatico <b>instanciar</b>
 * @author drodriguez
 *
 */
public class AdminUI {

	private static AdminUI administradorUI;
	private HashMap<String, PreferenciaUI> preferenciasUI;
	private String usuario;
	
	/**
	 * Constructor privado para evitar que se instancie el singletone fuera de la clase, para obtener el objeto
	 * utilize el metodo <b>instanciar</b>
	 * @param usuario
	 */
	private AdminUI(String usuario)
	{
		this.usuario = usuario;
		this.preferenciasUI = new HashMap<String, PreferenciaUI>();
		cargarValoresPorDefecto(usuario);
		cargarPreferenciasUI();
	}
	
	/**
	 * Obtiene el administrador de preferencias de UI para el usuario indicado, el usuario deberia sacarse de las variables de sesion
	 * @param usuario
	 * @return
	 */
	public static AdminUI instanciar(String usuario)
	{
		if(administradorUI==null || !administradorUI.correspondeAUsuario(usuario))
			administradorUI = new AdminUI(usuario);
		return administradorUI;
	}
	
	/**
	 * Obtiene el valor de la preferencia de UI que corresponda a la opcion proporcionada. Si el usuario no definió
	 * la preferencia de usuario para esa llave se retornará la preferencia por defecto. (vease el metodo cargarValoresPorDefecto) para ver
	 * que valores se asignan a cada llave por defecto.
	 * @param opcion se deberia usar las constantes en la clase OpcionesPreferenciasUI
	 * @param type el tipo a llamar, las llamadas tendrian que ser con la clase de un tipo por ejemplo: Integer.class<br>
	 * Los tipos que se soportan son Integer, Double y String, otros tipos retornaran null
	 * @return el valor de la preferencia para el usuario actual, si no existe dicha preferencia se retorna null. Si existe
	 * pero el usuario no la definió utiliza el valor por defecto.
	 */
	public <T> T obtenerPreferencia(String opcion, Class<T> type)
	{
		PreferenciaUI preferenciaUI = preferenciasUI.get(opcion);
		return (preferenciaUI!=null?preferenciaUI.obtenerValor(type):null);
	}
	
	/**
	 * Guarda una preferencia de UI del usuario, en caso de no existir un valor para la <b>opcion</b> crea una nueva.<br>
	 * Si la opcion tenía un valor por defecto este se sobre escribirá y guardará en las preferencias del usuario.
	 * @param opcion
	 * @param valor
	 */
	public void guardarPreferencia(String opcion, String valor)
	{
		PreferenciaUI preferenciaUI = preferenciasUI.get(opcion);
		if(preferenciaUI==null)
		{
			preferenciaUI =  new PreferenciaUI(usuario, opcion, valor);
			preferenciasUI.put(opcion, preferenciaUI);
		}
		else
		{
			preferenciaUI.Valor = valor;
		}
		preferenciaUI.save();
	}
	
	/**
	 * Carga preferencias predefinidas de UI.
	 * @param usuario
	 */
	private void cargarValoresPorDefecto(String usuario)
	{
		preferenciasUI.put(OpcionesPreferenciasUI.MOSTRAR_CONFIRMACION_IMPRESION, 
				new PreferenciaUI(usuario, OpcionesPreferenciasUI.MOSTRAR_CONFIRMACION_IMPRESION, "1"));
	}
	
	/**
	 * Carga los las preferencias para el usuario seleccionado, obteniendolos de la tabla PreferenciasUI
	 */
	private void cargarPreferenciasUI() 
	{
		List<PreferenciaUI> listaPreferenciaUI = PreferenciaUI.obtenerPreferenciasUIPorUsuario(usuario);
		for(PreferenciaUI preferenciaUI : listaPreferenciaUI)
		{
			preferenciasUI.put(preferenciaUI.Opcion, preferenciaUI);
		}
	}
	
	/**
	 * Verifica si el administrador de UI corresponde al usuario proporcionado
	 * @param usuario
	 * @return
	 */
	private boolean correspondeAUsuario(String usuario)
	{
		return this.usuario.equals(usuario);
	}
}
