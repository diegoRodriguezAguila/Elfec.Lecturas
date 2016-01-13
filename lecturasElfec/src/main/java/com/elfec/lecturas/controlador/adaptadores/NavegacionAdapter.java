package com.elfec.lecturas.controlador.adaptadores;

import java.util.List;

/**
 * Adapter para navegaciÃ³n sobre un conjunto
 * 
 * @author drodriguez
 *
 */
public class NavegacionAdapter<T> {

	private NavegacionListener<T> mNavegacionListener;
	private List<T> mLista;
	private int mPosActual;

	public NavegacionAdapter(NavegacionListener<T> navegacionListener) {
		mNavegacionListener = navegacionListener;
		mPosActual = -1;
	}

	/**
	 * Asigna el conjunto de objetos
	 * 
	 * @param lista
	 *            de objetos
	 */
	public void setLista(List<T> lista) {
		T objActual = null;
		if (mLista != null) {
			objActual = mLista.get(mPosActual);
		}
		mLista = lista;
		if (mNavegacionListener != null)
			mNavegacionListener.onListaChanged(lista);
		if (mLista != null) {
			int pos = mLista.indexOf(objActual);
			setPosicion(pos == -1 ? 0 : pos);
		}
	}

	/**
	 * Obtiene la lista de objetos asignada
	 * 
	 * @return {@link List}<{@link T}>
	 */
	public List<T> getLista() {
		return mLista;
	}

	/**
	 * Selecciona la posiciÃ³n actual, si la posiciÃ³n seleccionada es mayor al
	 * tamaÃ±o de la lista se va al ultimo elemento, y si es menor a cero al
	 * primero
	 * 
	 * @param pos
	 */
	public void setPosicion(int pos) {
		if (pos < 0)
			pos = 0;
		if (pos >= mLista.size())
			pos = mLista.size() - 1;
		mPosActual = pos;
		if (mNavegacionListener != null)
			mNavegacionListener.onPosicionChanged(mPosActual);
	}

	/**
	 * Devuelve la posiciÃ³n del objeto solicitado, -1 si es que no se encuentra
	 * 
	 * @param obj
	 * @return
	 */
	public int getPosicion(T obj) {
		if (mLista != null) {
			mLista.indexOf(obj);
		}
		return -1;
	}

	/**
	 * Obtiene la posiciÃ³n actual
	 * 
	 * @return posiciÃ³n actual
	 */
	public int getPosicionActual() {
		return mPosActual;
	}

	/**
	 * Obtiene el objeto selecionado actual
	 * 
	 * @return {@link T}
	 */
	public T getActual() {
		return mLista.get(mPosActual);
	}

	/**
	 * Devuelve la posiciÃ³n del primer objeto que cumple las condiciones
	 * 
	 * @param condicion
	 * @return posiciÃ³n del objeto, -1 si no se encontrÃ³ el objeto
	 */
	public int buscar(Predicado<T> condicion) {
		int tam = mLista.size();
		for (int i = 0; i < tam; i++) {
			if (condicion.evaluar(mLista.get(i)))
				return i;
		}
		return -1;
	}

	/**
	 * Devuelve la posiciÃ³n del ultimo objeto que cumple las condiciones
	 * 
	 * @param condicion
	 * @return posiciÃ³n del objeto, -1 si no se encontrÃ³ el objeto
	 */
	public int buscarAlReves(Predicado<T> condicion) {
		for (int i = mLista.size() - 1; i >= 0; i--) {
			if (condicion.evaluar(mLista.get(i)))
				return i;
		}
		return -1;
	}

	/**
	 * Eventos de cambios en la lista de objetos
	 * 
	 * @author drodriguez
	 *
	 */
	public static interface NavegacionListener<T> {

		/**
		 * Se llama cuando el conjunto de objetos ha cambiado
		 */
		public void onListaChanged(List<T> lista);

		/**
		 * Se llama cuando la posicion del objeto actual cambia
		 * 
		 * @param nuevaPos
		 */
		public void onPosicionChanged(int nuevaPos);
	}

	/**
	 * Define una interfaz de predicado para bÃºsquedas de navegaciÃ³n, etc
	 * 
	 * @author drodriguez
	 *
	 * @param <T>
	 */
	public static interface Predicado<T> {
		/**
		 * Evalua si el objeto coincide con el predicado
		 * 
		 * @param obj
		 * @return true si coincide
		 */
		public boolean evaluar(T obj);
	}
}
