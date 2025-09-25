package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

/**
 * Clase principal que inicia el juego Obsidian Arcane
 * Roguelike Dungeon Crawler 2D
 */
public class Main {
    public static void main(String[] args) {
        // Configuración de la ventana del juego
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        
        // Configuración básica de la ventana
        config.setTitle("Obsidian Arcane - Roguelike Dungeon Crawler");
        config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());  // Pantalla completa por defecto
        config.setResizable(true);
        config.setMaximized(false);
        
        // Configuración alternativa para modo ventana (comentada)
        // config.setWindowedMode(1920, 1080);  // Resolución Full HD
        
        // Configuración de rendimiento
        config.setForegroundFPS(60);  // 60 FPS
        config.setIdleFPS(30);        // 30 FPS cuando la ventana no está activa
        
        // Crear y lanzar la aplicación
        new Lwjgl3Application(new ObsidianArcaneGame(), config);
    }
}