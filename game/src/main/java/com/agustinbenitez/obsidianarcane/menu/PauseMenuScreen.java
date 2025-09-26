package com.agustinbenitez.obsidianarcane.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Input.Keys;
import com.agustinbenitez.obsidianarcane.GameStateManager;
import com.agustinbenitez.obsidianarcane.LocalizationManager;
import com.agustinbenitez.obsidianarcane.SaveManager;
import com.agustinbenitez.obsidianarcane.SimpleGameScreen;
import com.agustinbenitez.obsidianarcane.GameState;

/**
 * Menú de pausa que aparece cuando el jugador presiona Escape durante el juego
 */
public class PauseMenuScreen implements Screen {
    
    private GameStateManager game;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout glyphLayout;
    private LocalizationManager localization;
    
    // Opciones del menú
    private String[] menuOptions;
    private int selectedOption = 0;
    private final int RESUME = 0;
    private final int SETTINGS = 1;
    private final int MAIN_MENU = 2;
    
    // Configuración visual
    private Color selectedColor = Color.YELLOW;
    private Color normalColor = Color.WHITE;
    
    public PauseMenuScreen(GameStateManager game) {
        this.game = game;
        this.localization = LocalizationManager.getInstance();
        
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.5f); // Hacer la fuente más grande
        glyphLayout = new GlyphLayout();
        
        // Inicializar opciones del menú
        updateMenuOptions();
    }
    
    private void updateMenuOptions() {
        menuOptions = new String[] {
            localization.getText("pause.resume"),
            localization.getText("pause.settings"),
            localization.getText("pause.main_menu")
        };
    }
    
    @Override
    public void show() {
        updateMenuOptions(); // Actualizar por si cambió el idioma
    }
    
    @Override
    public void render(float delta) {
        // Fondo semi-transparente más oscuro para mejor contraste
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.8f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        handleInput();
        
        batch.begin();
        
        // Título del menú de pausa - más grande y más arriba
        font.setColor(Color.WHITE);
        font.getData().setScale(2.0f); // Título aún más grande
        String pauseTitle = localization.getText("pause.title");
        glyphLayout.setText(font, pauseTitle);
        float titleWidth = glyphLayout.width;
        font.draw(batch, pauseTitle, 
                 (Gdx.graphics.getWidth() - titleWidth) / 2, 
                 Gdx.graphics.getHeight() / 2 + 150); // Más arriba
        
        // Restaurar tamaño de fuente para opciones
        font.getData().setScale(1.5f);
        
        // Opciones del menú - más espaciadas
        for (int i = 0; i < menuOptions.length; i++) {
            if (i == selectedOption) {
                font.setColor(selectedColor);
            } else {
                font.setColor(normalColor);
            }
            
            String option = menuOptions[i];
            glyphLayout.setText(font, option);
            float optionWidth = glyphLayout.width;
            font.draw(batch, option, 
                     (Gdx.graphics.getWidth() - optionWidth) / 2, 
                     Gdx.graphics.getHeight() / 2 + 20 - (i * 60)); // Más espaciado (60 en lugar de 40)
        }
        
        // Instrucciones - fuente más pequeña pero visible
        font.getData().setScale(1.0f);
        font.setColor(Color.LIGHT_GRAY);
        String instructions = localization.getText("pause.instructions");
        glyphLayout.setText(font, instructions);
        float instructionsWidth = glyphLayout.width;
        font.draw(batch, instructions, 
                 (Gdx.graphics.getWidth() - instructionsWidth) / 2, 
                 120); // Más arriba desde abajo
        
        batch.end();
    }
    
    private void handleInput() {
        // Navegación vertical
        if (Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.W)) {
            selectedOption = (selectedOption - 1 + menuOptions.length) % menuOptions.length;
        }
        
        if (Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.S)) {
            selectedOption = (selectedOption + 1) % menuOptions.length;
        }
        
        // Selección
        if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            selectOption();
        }
        
        // Reanudar con Escape
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            game.resumeGame();
        }
    }
    
    private void selectOption() {
        switch (selectedOption) {
            case RESUME:
                game.resumeGame();
                break;
            case SETTINGS:
                // TODO: Implementar pantalla de ajustes
                Gdx.app.log("PauseMenu", "Settings not implemented yet");
                break;
            case MAIN_MENU:
                // Guardar automáticamente antes de salir
                if (game.getCurrentGameState() != null) {
                    SaveManager saveManager = SaveManager.getInstance();
                    GameState currentState = game.getCurrentGameState();
                    
                    // Actualizar el estado actual con la información del juego
                    SimpleGameScreen simpleGameScreen = game.getSimpleGameScreen();
                    if (simpleGameScreen != null) {
                        // No hay posición del jugador que actualizar en SimpleGameScreen
                        currentState.updateSaveDate();
                    }
                    
                    // Buscar un slot disponible o usar el último usado
                    int slotToSave = findBestSlotForAutoSave(saveManager);
                    saveManager.saveGame(slotToSave, currentState);
                    
                    Gdx.app.log("PauseMenu", "Game automatically saved in slot " + slotToSave);
                }
                
                game.showMainMenu();
                break;
        }
    }
    
    /**
     * Encuentra el mejor slot para el guardado automático
     */
    private int findBestSlotForAutoSave(SaveManager saveManager) {
        // Primero intentar usar el slot actual si existe
        GameState currentState = game.getCurrentGameState();
        if (currentState != null && currentState.getSaveName() != null) {
            for (int i = 1; i <= 4; i++) {
                if (saveManager.hasSave(i)) {
                    GameState existingSave = saveManager.loadGame(i);
                    if (existingSave != null && 
                        currentState.getSaveName().equals(existingSave.getSaveName())) {
                        return i;
                    }
                }
            }
        }
        
        // Si no encuentra el slot actual, usar el primer slot disponible
        for (int i = 1; i <= 4; i++) {
            if (!saveManager.hasSave(i)) {
                return i;
            }
        }
        
        // Si todos están ocupados, usar el slot 1
        return 1;
    }
    
    @Override
    public void resize(int width, int height) {}
    
    @Override
    public void pause() {}
    
    @Override
    public void resume() {}
    
    @Override
    public void hide() {}
    
    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (font != null) {
            font.dispose();
        }
    }
}