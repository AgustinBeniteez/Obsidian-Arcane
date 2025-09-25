package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import java.util.Locale;

/**
 * Gestor de localización para el juego Obsidian Arcane
 * Maneja la carga de archivos de idioma y proporciona acceso a textos localizados
 */
public class LocalizationManager {
    
    private static LocalizationManager instance;
    private I18NBundle bundle;
    private Locale currentLocale;
    
    // Idiomas soportados
    public enum Language {
        SPANISH("es", "ES"),
        ENGLISH("en", "US");
        
        private final String language;
        private final String country;
        
        Language(String language, String country) {
            this.language = language;
            this.country = country;
        }
        
        public Locale getLocale() {
            return new Locale(language, country);
        }
    }
    
    private LocalizationManager() {
        // Inicializar con español por defecto
        setLanguage(Language.SPANISH);
    }
    
    /**
     * Obtiene la instancia singleton del LocalizationManager
     */
    public static LocalizationManager getInstance() {
        if (instance == null) {
            instance = new LocalizationManager();
        }
        return instance;
    }
    
    /**
     * Cambia el idioma del juego
     * @param language El idioma a establecer
     */
    public void setLanguage(Language language) {
        currentLocale = language.getLocale();
        loadBundle();
    }
    
    /**
     * Carga el bundle de localización para el idioma actual
     */
    private void loadBundle() {
        try {
            FileHandle bundleFile = Gdx.files.internal("i18n/messages");
            bundle = I18NBundle.createBundle(bundleFile, currentLocale);
            Gdx.app.log("LocalizationManager", "Idioma cargado: " + currentLocale.getDisplayName());
        } catch (Exception e) {
            Gdx.app.error("LocalizationManager", "Error al cargar el idioma: " + currentLocale.getDisplayName(), e);
            // Fallback a inglés si hay error
            if (currentLocale != Language.ENGLISH.getLocale()) {
                currentLocale = Language.ENGLISH.getLocale();
                loadBundle();
            }
        }
    }
    
    /**
     * Obtiene un texto localizado por su clave
     * @param key La clave del texto
     * @return El texto localizado o la clave si no se encuentra
     */
    public String getText(String key) {
        if (bundle == null) {
            return key;
        }
        
        try {
            return bundle.get(key);
        } catch (Exception e) {
            Gdx.app.error("LocalizationManager", "Clave no encontrada: " + key);
            return key;
        }
    }
    
    /**
     * Obtiene un texto localizado con parámetros
     * @param key La clave del texto
     * @param args Los argumentos para formatear el texto
     * @return El texto localizado formateado
     */
    public String getText(String key, Object... args) {
        String text = getText(key);
        try {
            return String.format(text, args);
        } catch (Exception e) {
            Gdx.app.error("LocalizationManager", "Error al formatear texto: " + key);
            return text;
        }
    }
    
    /**
     * Obtiene el idioma actual
     */
    public Language getCurrentLanguage() {
        if (currentLocale.equals(Language.SPANISH.getLocale())) {
            return Language.SPANISH;
        } else {
            return Language.ENGLISH;
        }
    }
    
    /**
     * Verifica si hay un texto disponible para la clave dada
     */
    public boolean hasKey(String key) {
        if (bundle == null) {
            return false;
        }
        
        try {
            bundle.get(key);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}