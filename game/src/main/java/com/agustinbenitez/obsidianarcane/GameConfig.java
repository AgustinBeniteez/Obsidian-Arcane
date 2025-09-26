package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Gestor de configuración del juego Obsidian Arcane
 * Maneja las opciones de resolución, pantalla completa y otras configuraciones
 */
public class GameConfig {
    
    private static GameConfig instance;
    private static final String CONFIG_FILE = "config.json";
    
    // Default configurations
    private int windowWidth = 1920;
    private int windowHeight = 1080;
    private boolean fullscreen = true;
    private boolean vsync = true;
    private int targetFPS = 60;
    
    // Resoluciones predefinidas
    public static final Resolution[] AVAILABLE_RESOLUTIONS = {
        new Resolution(1280, 720, "HD (1280x720)"),
        new Resolution(1366, 768, "WXGA (1366x768)"),
        new Resolution(1600, 900, "HD+ (1600x900)"),
        new Resolution(1920, 1080, "Full HD (1920x1080)"),
        new Resolution(2560, 1440, "QHD (2560x1440)"),
        new Resolution(3840, 2160, "4K (3840x2160)")
    };
    
    // Opciones de FPS predefinidas
    public static final FPSOption[] AVAILABLE_FPS_OPTIONS = {
        new FPSOption(30, "30 FPS"),
        new FPSOption(60, "60 FPS"),
        new FPSOption(120, "120 FPS"),
        new FPSOption(144, "144 FPS"),
        new FPSOption(0, "Unlimited")
    };
    
    private GameConfig() {
        loadConfig();
    }
    
    public static GameConfig getInstance() {
        if (instance == null) {
            instance = new GameConfig();
        }
        return instance;
    }
    
    /**
     * Carga la configuración desde el archivo JSON
     */
    private void loadConfig() {
        try {
            // Use Java File API instead of LibGDX when Gdx.app is not available
            java.io.File configFile = new java.io.File(CONFIG_FILE);
            if (configFile.exists()) {
                Gson gson = new Gson();
                String jsonContent = new String(java.nio.file.Files.readAllBytes(configFile.toPath()));
                GameConfig loadedConfig = gson.fromJson(jsonContent, GameConfig.class);
                
                this.windowWidth = loadedConfig.windowWidth;
                this.windowHeight = loadedConfig.windowHeight;
                this.fullscreen = loadedConfig.fullscreen;
                this.vsync = loadedConfig.vsync;
                this.targetFPS = loadedConfig.targetFPS;
                
                System.out.println("GameConfig: Configuration loaded from " + CONFIG_FILE);
            } else {
                System.out.println("GameConfig: Configuration file not found, using default values");
                saveConfigWithJavaIO(); // Crear archivo con valores por defecto
            }
        } catch (Exception e) {
            System.err.println("GameConfig: Error loading configuration: " + e.getMessage());
        }
    }
    
    /**
     * Guarda la configuración actual en el archivo JSON
     */
    public void saveConfig() {
        if (Gdx.app != null) {
            // Use LibGDX file system when available
            try {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(this);
                
                FileHandle configFile = Gdx.files.local(CONFIG_FILE);
                configFile.writeString(json, false);
                
                Gdx.app.log("GameConfig", "Configuration saved to " + CONFIG_FILE);
            } catch (Exception e) {
                Gdx.app.error("GameConfig", "Error saving configuration: " + e.getMessage());
            }
        } else {
            // Use Java IO when LibGDX is not available
            saveConfigWithJavaIO();
        }
    }
    
    /**
     * Guarda la configuración usando Java IO (para cuando LibGDX no está disponible)
     */
    private void saveConfigWithJavaIO() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(this);
            
            java.io.File configFile = new java.io.File(CONFIG_FILE);
            java.nio.file.Files.write(configFile.toPath(), json.getBytes());
            
            System.out.println("GameConfig: Configuration saved to " + CONFIG_FILE);
        } catch (Exception e) {
            System.err.println("GameConfig: Error saving configuration: " + e.getMessage());
        }
    }
    
    /**
     * Aplica la configuración actual al juego
     */
    public void applyConfig() {
        if (Gdx.graphics == null) {
            Gdx.app.log("GameConfig", "Graphics not available, configuration will be applied on initialization");
            return;
        }
        
        try {
            Graphics graphics = Gdx.graphics;
            
            if (fullscreen) {
                // Cambiar a pantalla completa
                Graphics.DisplayMode displayMode = graphics.getDisplayMode();
                graphics.setFullscreenMode(displayMode);
                Gdx.app.log("GameConfig", "Changed to fullscreen: " + displayMode.width + "x" + displayMode.height);
            } else {
                // Cambiar a modo ventana
                graphics.setWindowedMode(windowWidth, windowHeight);
                Gdx.app.log("GameConfig", "Changed to windowed mode: " + windowWidth + "x" + windowHeight);
            }
            
            // Aplicar VSync
            graphics.setVSync(vsync);
            
            // Aplicar configuración de FPS
            if (targetFPS > 0) {
                graphics.setForegroundFPS(targetFPS);
                Gdx.app.log("GameConfig", "FPS limited to: " + targetFPS);
            } else {
                // FPS ilimitado - usar un valor muy alto
                graphics.setForegroundFPS(0);
                Gdx.app.log("GameConfig", "Unlimited FPS activated");
            }
            
        } catch (Exception e) {
            Gdx.app.error("GameConfig", "Error applying configuration: " + e.getMessage());
        }
    }
    
    /**
     * Alterna entre pantalla completa y modo ventana
     */
    public void toggleFullscreen() {
        fullscreen = !fullscreen;
        applyConfig();
        saveConfig();
    }
    
    /**
     * Establece una nueva resolución
     */
    public void setResolution(int width, int height) {
        this.windowWidth = width;
        this.windowHeight = height;
        if (!fullscreen) {
            applyConfig();
        }
        saveConfig();
    }
    
    /**
     * Establece una resolución desde el array predefinido
     */
    public void setResolution(Resolution resolution) {
        setResolution(resolution.width, resolution.height);
    }
    
    /**
     * Obtiene la resolución actual como string
     */
    public String getCurrentResolutionString() {
        return windowWidth + "x" + windowHeight;
    }
    
    /**
     * Encuentra la resolución actual en el array predefinido
     */
    public Resolution getCurrentResolution() {
        for (Resolution res : AVAILABLE_RESOLUTIONS) {
            if (res.width == windowWidth && res.height == windowHeight) {
                return res;
            }
        }
        // Si no se encuentra, crear una personalizada
        return new Resolution(windowWidth, windowHeight, getCurrentResolutionString());
    }
    
    // Getters y Setters
    public int getWindowWidth() { return windowWidth; }
    public int getWindowHeight() { return windowHeight; }
    public boolean isFullscreen() { return fullscreen; }
    public boolean isVsync() { return vsync; }
    public int getTargetFPS() { return targetFPS; }
    
    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
        applyConfig();
        saveConfig();
    }
    
    public void setVsync(boolean vsync) {
        this.vsync = vsync;
        applyConfig();
        saveConfig();
    }
    
    public void setTargetFPS(int targetFPS) {
        this.targetFPS = targetFPS;
        applyConfig();
        saveConfig();
    }
    
    /**
     * Get current FPS option
     */
    public FPSOption getCurrentFPSOption() {
        for (FPSOption option : AVAILABLE_FPS_OPTIONS) {
            if (option.fps == targetFPS) {
                return option;
            }
        }
        // Default to 60 FPS if not found
        return AVAILABLE_FPS_OPTIONS[1];
    }
    
    /**
     * Set FPS option
     */
    public void setFPSOption(FPSOption fpsOption) {
        setTargetFPS(fpsOption.fps);
    }
    
    /**
     * Clase interna para representar resoluciones
     */
    public static class Resolution {
        public final int width;
        public final int height;
        public final String displayName;
        
        public Resolution(int width, int height, String displayName) {
            this.width = width;
            this.height = height;
            this.displayName = displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Resolution that = (Resolution) obj;
            return width == that.width && height == that.height;
        }
    }
    
    /**
     * Clase interna para representar opciones de FPS
     */
    public static class FPSOption {
        public final int fps;
        public final String displayName;
        
        public FPSOption(int fps, String displayName) {
            this.fps = fps;
            this.displayName = displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            FPSOption that = (FPSOption) obj;
            return fps == that.fps;
        }
    }
}