package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Game;

/**
 * Gestor de estados del juego (Menú Principal, Gameplay, Pausa, etc.)
 */
public class GameStateManager extends Game {
    
    // Referencias a las diferentes pantallas
    private MainMenuScreen mainMenuScreen;
    private GameplayScreen gameplayScreen;
    private OptionsScreen optionsScreen;
    
    @Override
    public void create() {
        // Inicializar pantallas
        mainMenuScreen = new MainMenuScreen(this);
        gameplayScreen = new GameplayScreen(this);
        optionsScreen = new OptionsScreen(this);
        
        // Start with the main menu
        setScreen(mainMenuScreen);
    }
    
    /**
     * Cambiar al menú principal
     */
    public void showMainMenu() {
        setScreen(mainMenuScreen);
    }
    
    /**
     * Cambiar al gameplay
     */
    public void startGame() {
        setScreen(gameplayScreen);
    }
    
    /**
     * Cambiar a la pantalla de opciones
     */
    public void showOptions() {
        setScreen(optionsScreen);
    }
    
    @Override
    public void dispose() {
        if (mainMenuScreen != null) mainMenuScreen.dispose();
        if (gameplayScreen != null) gameplayScreen.dispose();
        if (optionsScreen != null) optionsScreen.dispose();
        super.dispose();
    }
}