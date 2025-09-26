package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.agustinbenitez.obsidianarcane.menu.MainMenuScreen;
import com.agustinbenitez.obsidianarcane.menu.OptionsScreen;
import com.agustinbenitez.obsidianarcane.menu.PauseMenuScreen;
import com.agustinbenitez.obsidianarcane.SimpleGameScreen;
import com.agustinbenitez.obsidianarcane.menu.CreditsScreen;

/**
 * Gestor de estados del juego (Menú Principal, Gameplay, Pausa, etc.)
 */
public class GameStateManager extends Game {
    
    // Referencias a las diferentes pantallas
    private MainMenuScreen mainMenuScreen;
    private SimpleGameScreen simpleGameScreen;
    private OptionsScreen optionsScreen;
    private SaveLoadScreen saveLoadScreen;
    private PauseMenuScreen pauseMenuScreen;
    private CreditsScreen creditsScreen;
    
    // Estado actual del juego
    private GameState currentGameState;
    
    @Override
    public void create() {
        // Inicializar pantallas
        mainMenuScreen = new MainMenuScreen(this);
        simpleGameScreen = new SimpleGameScreen(this);
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
     * Cambiar al gameplay con una nueva partida
     */
    public void startGame() {
        // Crear nuevo estado de juego con valores por defecto
        currentGameState = new GameState("Nueva Partida", "Mundo 2D", new Vector2(800, 640));
        
        if (simpleGameScreen != null) {
            simpleGameScreen.dispose();
        }
        simpleGameScreen = new SimpleGameScreen(this);
        setScreen(simpleGameScreen);
    }
    
    /**
     * Cargar una partida guardada
     */
    public void loadGame(GameState gameState) {
        this.currentGameState = gameState;
        
        // Recrear SimpleGameScreen
        if (simpleGameScreen != null) {
            simpleGameScreen.dispose();
        }
        simpleGameScreen = new SimpleGameScreen(this);
        
        // Cambiar a la pantalla de juego
        setScreen(simpleGameScreen);
    }
    
    /**
     * Reanudar el juego actual
     */
    public void resumeGame() {
        if (simpleGameScreen != null) {
            setScreen(simpleGameScreen);
        }
    }
    
    /**
     * Mostrar pantalla de guardado
     */
    public void showSaveScreen() {
        if (currentGameState != null && simpleGameScreen != null) {
            // Actualizar estado actual (sin posición del jugador ya que no hay)
            
            saveLoadScreen = new SaveLoadScreen(this, true, currentGameState);
            setScreen(saveLoadScreen);
        }
    }
    
    /**
     * Mostrar pantalla de carga
     */
    public void showLoadScreen() {
        saveLoadScreen = new SaveLoadScreen(this, false, null);
        setScreen(saveLoadScreen);
    }
    

    
    /**
     * Crear nuevo juego directamente en un slot específico sin pedir nombre
     */
    public void startNewGameInSlot(int slot) {
        // Crear nuevo estado de juego con nombre automático
        String saveName = "Game " + slot;
        currentGameState = new GameState(saveName, "Mundo 2D", new Vector2(800, 640)); // Posición central del mapa
        
        // Guardar la nueva partida en el slot especificado
        SaveManager saveManager = SaveManager.getInstance();
        saveManager.saveGame(slot, currentGameState);
        
        // Recrear SimpleGameScreen
        if (simpleGameScreen != null) {
            simpleGameScreen.dispose();
        }
        simpleGameScreen = new SimpleGameScreen(this);
        setScreen(simpleGameScreen);
    }
    
    /**
     * Mostrar pantalla de opciones
     */
    public void showOptions() {
        setScreen(optionsScreen);
    }
    
    /**
     * Mostrar pantalla de créditos
     */
    public void showCredits() {
        if (creditsScreen == null) {
            creditsScreen = new CreditsScreen(this);
        }
        setScreen(creditsScreen);
    }
    
    /**
     * Mostrar menú de pausa
     */
    public void showPauseMenu() {
        if (pauseMenuScreen == null) {
            pauseMenuScreen = new PauseMenuScreen(this);
        }
        setScreen(pauseMenuScreen);
    }
    
    /**
     * Obtener el estado actual del juego
     */
    public GameState getCurrentGameState() {
        return currentGameState;
    }
    
    /**
     * Actualizar el estado actual del juego
     */
    public void updateCurrentGameState(GameState gameState) {
        this.currentGameState = gameState;
    }
    
    /**
     * Obtener la pantalla de gameplay actual
     */
    public SimpleGameScreen getSimpleGameScreen() {
        return simpleGameScreen;
    }
    
    @Override
    public void dispose() {
        if (mainMenuScreen != null) mainMenuScreen.dispose();
        if (simpleGameScreen != null) simpleGameScreen.dispose();
        if (optionsScreen != null) optionsScreen.dispose();
        if (saveLoadScreen != null) saveLoadScreen.dispose();
        if (pauseMenuScreen != null) pauseMenuScreen.dispose();
        if (creditsScreen != null) creditsScreen.dispose();
    }
}