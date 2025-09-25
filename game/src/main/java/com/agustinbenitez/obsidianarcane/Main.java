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
        
        // Initialize game configuration
        GameConfig gameConfig = GameConfig.getInstance();
        
        // Basic window configuration
        config.setTitle("Obsidian Arcane");
        config.setResizable(true);
        config.setMaximized(false);
        
        // Apply saved configuration
        if (gameConfig.isFullscreen()) {
            config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        } else {
            config.setWindowedMode(gameConfig.getWindowWidth(), gameConfig.getWindowHeight());
        }
        
        // Performance configuration
        config.setForegroundFPS(gameConfig.getTargetFPS());  // Use configured FPS
        config.setIdleFPS(30);        // 30 FPS when window is not active
        
        // VSync configuration
        config.useVsync(gameConfig.isVsync());
        
        // Create and launch the application
        new Lwjgl3Application(new GameStateManager(), config);
    }
}