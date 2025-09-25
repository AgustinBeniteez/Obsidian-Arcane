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
import com.agustinbenitez.obsidianarcane.menu.CreditsScreen;
import com.agustinbenitez.obsidianarcane.game.GameplayScreen;

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
    private VillageNameInputScreen villageNameInputScreen;
    
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
        // Crear nuevo estado de juego (sin seed ya que el mapa es fijo)
        currentGameState = new GameState("Nueva Partida", "Mi Aldea", new Vector2(800, 640)); // Posición central del mapa
        
        // Recrear GameplayScreen
        if (gameplayScreen != null) {
            gameplayScreen.dispose();
        }
        gameplayScreen = new GameplayScreen(this);
        setScreen(gameplayScreen);
    }
    
    /**
     * Cargar una partida guardada
     */
    public void loadGame(GameState gameState) {
        this.currentGameState = gameState;
        
        // Recrear GameplayScreen (sin seed ya que el mapa es fijo)
        if (gameplayScreen != null) {
            gameplayScreen.dispose();
        }
        gameplayScreen = new GameplayScreen(this);
        
        // Establecer la posición de la cámara
        gameplayScreen.setCameraPosition(gameState.getPlayerPosition());
        
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
            // Actualizar estado actual con posición de la cámara
            Vector2 cameraPos = gameplayScreen.getCameraPosition();
            currentGameState.setPlayerPosition(cameraPos);
            
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
     * Mostrar pantalla de entrada de nombre de aldea
     */
    public void showVillageNameInput() {
        showVillageNameInput(1); // Slot por defecto
    }
    
    /**
     * Mostrar pantalla de entrada de nombre de aldea para un slot específico
     */
    public void showVillageNameInput(int slot) {
        villageNameInputScreen = new VillageNameInputScreen(this, slot);
        setScreen(villageNameInputScreen);
    }
    
    /**
     * Crear nuevo juego con el nombre de aldea especificado
     */
    public void startGameWithVillageName(String villageName) {
        startGameWithVillageName(villageName, 1); // Slot por defecto
    }
    
    /**
     * Crear nuevo juego con el nombre de aldea especificado en un slot específico
     */
    public void startGameWithVillageName(String villageName, int slot) {
        // Crear nuevo estado de juego con el nombre de aldea
        currentGameState = new GameState("Partida " + slot, villageName, new Vector2(800, 640)); // Posición central del mapa
        
        // Guardar la nueva partida en el slot especificado
        SaveManager saveManager = SaveManager.getInstance();
        saveManager.saveGame(slot, currentGameState);
        
        // Recrear GameplayScreen
        if (gameplayScreen != null) {
            gameplayScreen.dispose();
        }
        gameplayScreen = new GameplayScreen(this);
        setScreen(gameplayScreen);
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