package com.elfec.lecturas.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Maneja las sharedpreferences de toda la aplicación
 *
 * @author Diego
 */
public class AppPreferences {

    private final String LOGGED_USERNAME = "loggedUsername";
    // ONCE IMPORT DATOS
    private final String ALL_ONCE_REQUIRED_DATOS_IMPORTADOS = "allOnceReqDataImportados";
    private final String PARAMETRIZABLES_IMPORTADOS = "parametrizablesImportados";
    private final String ORDENATIVOS_IMPORTADOS = "ordenativosImportados";
    private final String BASES_CALCULO_CPTOS_IMPORTADOS = "conceptCalculationBasesImportados";
    private final String BASES_CALCULO_IMPORTADOS = "printCalculationBasesImportados";
    private final String CONCEPTOS_IMPORTADOS = "conceptosImportados";
    private final String CONCEPTOS_CATEG_IMPORTADOS = "conceptosCategImportados";
    private final String CONCEPTOS_TARIFA_IMPORTADOS = "conceptosTarifaImportados";
    private final String RECLASIF_CATEG_IMPORTADOS = "reclasificacionCategoriaImportados";

    private SharedPreferences preferences;

    private AppPreferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static Context context;
    private static AppPreferences appPreferences;

    /**
     * Este método se debe llamar al inicializar la aplicación
     *
     * @param context
     */
    public static void initialize(Context context) {
        AppPreferences.context = context;
    }

    /**
     * Obtiene el contexto de la aplicación
     *
     * @return el contexto de la aplicación
     */
    public static Context getApplicationContext() {
        return AppPreferences.context;
    }

    public static AppPreferences instance() {
        if (appPreferences == null) {
            if (context == null)
                throw new IllegalStateException(
                        "La clase no se inicializó correctamente antes de utilizarse, debe asignarse el contexto antes de utilizar las preferencias");
            appPreferences = new AppPreferences(context);
        }
        return appPreferences;
    }

    /**
     * Obtiene el usuario logeado actual
     *
     * @return null si es que ninguno se ha logeado
     */
    public String getLoggedUsername() {
        return preferences.getString(LOGGED_USERNAME, null);
    }

    /**
     * Asigna el usuario logeado actual, sobreescribe cualquier usuario que haya
     * sido logeado antes
     *
     * @return la instancia actual de PreferencesManager
     */
    public AppPreferences setLoggedUsername(String loggedUsername) {
        preferences.edit().putString(LOGGED_USERNAME, loggedUsername).apply();
        return this;
    }

    /**
     * Indica si toda la información que solo debe ser importada una vez la ha
     * sido
     *
     * @return true si es que ya se importó toda
     */
    public boolean estaInfoReqUnaVezImportados() {
        return preferences
                .getBoolean(ALL_ONCE_REQUIRED_DATOS_IMPORTADOS, false);
    }

    /**
     * Asigna si toda la información que solo debe ser importada una vez la ha
     * sido
     *
     * @return la instancia actual de PreferencesManager
     */
    public AppPreferences setInfoReqUnaVezImportados(boolean estaImportados) {
        preferences.edit()
                .putBoolean(ALL_ONCE_REQUIRED_DATOS_IMPORTADOS, estaImportados)
                .apply();
        return this;
    }

    /**
     * Indica si los SGC_MOVIL_PARAM han sido importados
     *
     * @return true si es que ya se importó
     */
    public boolean estaParametrizablesImportados() {
        return preferences.getBoolean(PARAMETRIZABLES_IMPORTADOS, false);
    }

    /**
     * Asigna si los SGC_MOVIL_PARAM han sido importados
     *
     * @return la instancia actual de PreferencesManager
     */
    public AppPreferences setParametrizablesImportados(boolean estaImportados) {
        preferences.edit()
                .putBoolean(PARAMETRIZABLES_IMPORTADOS, estaImportados)
                .apply();
        return this;
    }

    /**
     * Indica si los TIPOS_CATEG_SUM han sido importados
     *
     * @return true si es que ya se importó
     */
    public boolean estaOrdenativosImportados() {
        return preferences.getBoolean(ORDENATIVOS_IMPORTADOS, false);
    }

    /**
     * Asigna si los TIPOS_CATEG_SUM han sido importados
     *
     * @return la instancia actual de PreferencesManager
     */
    public AppPreferences setOrdenativosImportados(boolean estaImportados) {
        preferences.edit().putBoolean(ORDENATIVOS_IMPORTADOS, estaImportados)
                .apply();
        return this;
    }

    /**
     * Indica si los GBASES_CALC_CPTOS han sido importados
     *
     * @return true si es que ya se importó
     */
    public boolean estaBasesCalcConceptosImportados() {
        return preferences.getBoolean(BASES_CALCULO_CPTOS_IMPORTADOS, false);
    }

    /**
     * Asigna si los GBASES_CALC_CPTOS han sido importados
     *
     * @return la instancia actual de PreferencesManager
     */
    public AppPreferences setBasesCalcConceptosImportados(boolean estaImportados) {
        preferences.edit()
                .putBoolean(BASES_CALCULO_CPTOS_IMPORTADOS, estaImportados)
                .apply();
        return this;
    }

    /**
     * Indica si los GBASES_CALC_IMP han sido importados
     *
     * @return true si es que ya se importó
     */
    public boolean estaBasesCalculoImportados() {
        return preferences.getBoolean(BASES_CALCULO_IMPORTADOS, false);
    }

    /**
     * Asigna si los GBASES_CALC_IMP han sido importados
     *
     * @return la instancia actual de PreferencesManager
     */
    public AppPreferences setBasesCalculoImportados(boolean estaImportados) {
        preferences.edit().putBoolean(BASES_CALCULO_IMPORTADOS, estaImportados)
                .apply();
        return this;
    }

    /**
     * Indica si los CONCEPTOS han sido importados
     *
     * @return true si es que ya se importó
     */
    public boolean estaConceptosImportados() {
        return preferences.getBoolean(CONCEPTOS_IMPORTADOS, false);
    }

    /**
     * Asigna si los CONCEPTOS han sido importados
     *
     * @return la instancia actual de PreferencesManager
     */
    public AppPreferences setConceptosImportados(boolean estaImportados) {
        preferences.edit().putBoolean(CONCEPTOS_IMPORTADOS, estaImportados)
                .apply();
        return this;
    }

    /**
     * Indica si los CPTOS_CATEGORIAS han sido importados
     *
     * @return true si es que ya se importó
     */
    public boolean estaConceptosCategoriasImportados() {
        return preferences.getBoolean(CONCEPTOS_CATEG_IMPORTADOS, false);
    }

    /**
     * Asigna si los CPTOS_CATEGORIAS han sido importados
     *
     * @return la instancia actual de PreferencesManager
     */
    public AppPreferences setConceptosCategoriasImportados(
            boolean estaImportados) {
        preferences.edit()
                .putBoolean(CONCEPTOS_CATEG_IMPORTADOS, estaImportados)
                .apply();
        return this;
    }

    /**
     * Indica si los CONCEPTOS_TARIFAS han sido importados
     *
     * @return true si es que ya se importó
     */
    public boolean estaConceptosTarifasImportados() {
        return preferences.getBoolean(CONCEPTOS_TARIFA_IMPORTADOS, false);
    }

    /**
     * Asigna si los CONCEPTOS_TARIFAS han sido importados
     *
     * @return la instancia actual de PreferencesManager
     */
    public AppPreferences setConceptosTarifasImportados(boolean estaImportados) {
        preferences.edit()
                .putBoolean(CONCEPTOS_TARIFA_IMPORTADOS, estaImportados)
                .apply();
        return this;
    }

    /**
     * Borra las preferencias guardadas de la información que se debe importar
     * solo una vez
     */
    public void eliminarPreferenciasDeInfoReqUnaVez() {
        preferences.edit()
                .remove(LOGGED_USERNAME)
                .remove(ALL_ONCE_REQUIRED_DATOS_IMPORTADOS)
                .remove(PARAMETRIZABLES_IMPORTADOS)
                .remove(ORDENATIVOS_IMPORTADOS).remove(CONCEPTOS_IMPORTADOS)
                .remove(CONCEPTOS_CATEG_IMPORTADOS)
                .remove(CONCEPTOS_TARIFA_IMPORTADOS)
                .remove(BASES_CALCULO_IMPORTADOS)
                .remove(BASES_CALCULO_CPTOS_IMPORTADOS)
                .remove(RECLASIF_CATEG_IMPORTADOS).apply();
    }

    /**
     * Indica si las RECLASIFICACION CATEGORIAS han sido importadas
     *
     * @return true si es que ya se importó
     */
    public boolean estaReclasifCategoriasImportados() {
        return preferences.getBoolean(RECLASIF_CATEG_IMPORTADOS, false);
    }

    /**
     * Asigna si las RECLASIFICACION CATEGORIAS han sido importadas
     *
     * @return la instancia actual de PreferencesManager
     */
    public AppPreferences setReclasifCategoriasImportados(boolean estaImportados) {
        preferences.edit()
                .putBoolean(RECLASIF_CATEG_IMPORTADOS, estaImportados).apply();
        return this;
    }

    public static void dispose() {
        context = null;
        appPreferences = null;
    }
}
