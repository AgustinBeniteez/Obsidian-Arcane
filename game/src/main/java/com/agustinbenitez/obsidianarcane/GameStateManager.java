package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector2;

/**
 * Gestor de estados del juego (Menú Principal, Gameplay, Pausa, etc.)
 */
public class GameStateManager extends Game {
    
    // Referencias a las diferentes pantallas
    private MainMenuScreen mainMenuScreen;
    private GameplayScreen gameplayScreen;
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
     * Cambiar al gameplay con una nueva partida
     */
    public void startGame() {
        // Crear nuevo estado de juego con seed aleatoria
        long seed = System.currentTimeMillis();
        currentGameState = new GameState("Nueva Partida", seed, new Vector2(96, 96));
        
        // Recrear GameplayScreen con la nueva seed
        if (gameplayScreen != null) {
            gameplayScreen.dispose();
        }
        gameplayScreen = new GameplayScreen(this, seed);
        setScreen(gameplayScreen);
    }
    
    /**
     * Cargar una partida guardada
     */
    public void loadGame(GameState gameState) {
        this.currentGameState = gameState;
        
        // Recrear GameplayScreen con la seed de la partida guardada
        if (gameplayScreen != null) {
            gameplayScreen.dispose();
        }
        gameplayScreen = new GameplayScreen(this, gameState.getMapSeed());
        
        // Establecer la posición del jugador
        gameplayScreen.setPlayerPosition(gameState.getPlayerPosition());
        
        setScreen(gameplayScreen);
    }
    
    /**
     * Reanudar el juego actual
     */
    public void resumeGame() {
        if (gameplayScreen != null) {
            setScreen(gameplayScreen);
        } else {
            showMainMenu();
        }
    }
    
    /**
     * Mostrar pantalla de guardado
     */
    public void showSaveScreen() {
        if (currentGameState != null && gameplayScreen != null) {
            // Actualizar estado actual con posición del jugador
            Vector2 playerPos = gameplayScreen.getPlayerPosition();
            currentGameState.setPlayerPosition(playerPos);
            
            saveLoadScreen = new SaveLoadScreen(this, true, currentGameState);
            setScreen(saveLoadScreen);
        }
    }
    
    /**
     * Mostrar pantalla de carga
     */
    public void showLoadScreen() {
        saveLoadScreen = new SaveLoadScreen(this, false);
        setScreen(saveLoadScreen);
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
        if (pauseMenuScreen != null) {
            pauseMenuScreen.dispose();
        }
        pauseMenuScreen = new PauseMenuScreen(this);
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
    public GameplayScreen getGameplayScreen() {
        return gameplayScreen;
    }
    
    @Override
    public void dispose() {
        if (mainMenuScreen != null) mainMenuScreen.dispose();
        if (gameplayScreen != null) gameplayScreen.dispose();
        if (optionsScreen != null) optionsScreen.dispose();
        if (saveLoadScreen != null) saveLoadScreen.dispose();
        if (pauseMenuScreen != null) pauseMenuScreen.dispose();
        if (creditsScreen != null) creditsScreen.dispose();
    }
}