package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

/**
 * Clase principal que inicia el juego Obsidian Arcane
 * Roguelike Dungeon Crawler 2D
 */
public class Main {
    public static void main(String[] args) {
        // Game window configuration
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        
        // Basic window configuration
        config.setTitle("Obsidian Arcane");
        config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());  // Fullscreen by default
        config.setResizable(true);
        config.setMaximized(false);
        
        // Alternative configuration for windowed mode (commented)
        // config.setWindowedMode(1920, 1080);  // Full HD resolution
        
        // Performance configuration
        config.setForegroundFPS(60);  // 60 FPS
        config.setIdleFPS(30);        // 30 FPS when window is not active
        
        // Create and launch the application
        new Lwjgl3Application(new GameStateManager(), config);
    }
}