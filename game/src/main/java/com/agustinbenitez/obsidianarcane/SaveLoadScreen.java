package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Pantalla para guardar y cargar partidas
 */
public class SaveLoadScreen implements Screen {
    private GameStateManager game;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont titleFont;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private LocalizationManager localization;
    private SaveManager saveManager;
    
    private boolean isSaveMode; // true = guardar, false = cargar
    private int selectedSlot = 1;
    private GameState currentGameState; // Para modo guardar
    private SimpleDateFormat dateFormat;
    
    // Colores
    private static final Color BACKGROUND_COLOR = new Color(0.1f, 0.1f, 0.2f, 1f);
    private static final Color SLOT_COLOR = new Color(0.3f, 0.3f, 0.4f, 1f);
    private static final Color SELECTED_SLOT_COLOR = new Color(0.5f, 0.5f, 0.7f, 1f);
    private static final Color EMPTY_SLOT_COLOR = new Color(0.2f, 0.2f, 0.3f, 1f);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color TITLE_COLOR = new Color(0.8f, 0.8f, 1f, 1f);
    
    // Dimensiones
    private static final float SLOT_WIDTH = 600f;
    private static final float SLOT_HEIGHT = 80f;
    private static final float SLOT_SPACING = 20f;
    
    public SaveLoadScreen(GameStateManager game, boolean isSaveMode) {
        this(game, isSaveMode, null);
    }
    
    public SaveLoadScreen(GameStateManager game, boolean isSaveMode, GameState currentGameState) {
        this.game = game;
        this.isSaveMode = isSaveMode;
        this.currentGameState = currentGameState;
        this.localization = LocalizationManager.getInstance();
        this.saveManager = SaveManager.getInstance();
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(TEXT_COLOR);
        
        titleFont = new BitmapFont();
        titleFont.setColor(TITLE_COLOR);
        titleFont.getData().setScale(1.5f);
        
        shapeRenderer = new ShapeRenderer();
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    
    @Override
    public void show() {
        Gdx.app.log("SaveLoadScreen", isSaveMode ? "Mostrando pantalla de guardado" : "Mostrando pantalla de carga");
    }
    
    @Override
    public void render(float delta) {
        handleInput();
        
        // Limpiar pantalla
        Gdx.gl.glClearColor(BACKGROUND_COLOR.r, BACKGROUND_COLOR.g, BACKGROUND_COLOR.b, BACKGROUND_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        
        renderBackground();
        renderUI();
    }
    
    private void handleInput() {
        // Navegación entre slots
        if (Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.W)) {
            selectedSlot = Math.max(1, selectedSlot - 1);
        }
        if (Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.S)) {
            selectedSlot = Math.min(saveManager.getMaxSaveSlots(), selectedSlot + 1);
        }
        
        // Confirmar acción
        if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            if (isSaveMode) {
                performSave();
            } else {
                performLoad();
            }
        }
        
        // Eliminar partida (solo en modo carga)
        if (!isSaveMode && Gdx.input.isKeyJustPressed(Keys.DEL)) {
            performDelete();
        }
        
        // Volver al menú
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Keys.M)) {
            game.showMainMenu();
        }
    }
    
    private void performSave() {
        if (currentGameState == null) {
            Gdx.app.error("SaveLoadScreen", "No hay estado de juego para guardar");
            return;
        }
        
        // Crear nombre automático si no tiene
        if (currentGameState.getSaveName() == null || currentGameState.getSaveName().isEmpty()) {
            currentGameState.setSaveName("Partida " + selectedSlot);
        }
        
        boolean success = saveManager.saveGame(selectedSlot, currentGameState);
        if (success) {
            Gdx.app.log("SaveLoadScreen", "Partida guardada exitosamente en slot " + selectedSlot);
            // Volver al juego después de guardar
            game.resumeGame();
        } else {
            Gdx.app.error("SaveLoadScreen", "Error al guardar la partida");
        }
    }
    
    private void performLoad() {
        if (!saveManager.hasSave(selectedSlot)) {
            // Si no hay partida guardada, crear nueva partida directamente
            Gdx.app.log("SaveLoadScreen", "Creando nueva partida en slot " + selectedSlot);
            
            // Crear nueva partida directamente sin pedir nombre
            game.startNewGameInSlot(selectedSlot);
            return;
        }
        
        GameState loadedState = saveManager.loadGame(selectedSlot);
        if (loadedState != null) {
            Gdx.app.log("SaveLoadScreen", "Cargando partida: " + loadedState.getSaveName());
            game.loadGame(loadedState);
        } else {
            Gdx.app.error("SaveLoadScreen", "Error al cargar la partida del slot " + selectedSlot);
        }
    }
    
    private void performDelete() {
        if (!saveManager.hasSave(selectedSlot)) {
            return;
        }
        
        boolean success = saveManager.deleteSave(selectedSlot);
        if (success) {
            Gdx.app.log("SaveLoadScreen", "Partida eliminada del slot " + selectedSlot);
        }
    }
    
    private void renderBackground() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        
        // Fondo principal
        shapeRenderer.setColor(BACKGROUND_COLOR);
        shapeRenderer.rect(0, 0, screenWidth, screenHeight);
        
        shapeRenderer.end();
    }
    
    private void renderUI() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        
        // Título
        String title = isSaveMode ? localization.getText("save.title") : localization.getText("load.title");
        float titleWidth = titleFont.getRegion().getRegionWidth() * title.length() * 0.6f;
        titleFont.draw(batch, title, (screenWidth - titleWidth) / 2, screenHeight - 50);
        
        // Instrucciones
        String instructions = isSaveMode ? 
            localization.getText("save.instructions") :
            localization.getText("load.instructions");
        font.draw(batch, instructions, 50, screenHeight - 100);
        
        batch.end();
        
        // Renderizar slots
        renderSlots();
        
        batch.begin();
        batch.end();
    }
    
    private void renderSlots() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float startY = screenHeight - 200;
        
        GameState[] saves = saveManager.getAllSaveInfo();
        
        for (int i = 0; i < saveManager.getMaxSaveSlots(); i++) {
            int slot = i + 1;
            float slotY = startY - (i * (SLOT_HEIGHT + SLOT_SPACING));
            float slotX = (screenWidth - SLOT_WIDTH) / 2;
            
            // Color del slot
            Color slotColor;
            if (slot == selectedSlot) {
                slotColor = SELECTED_SLOT_COLOR;
            } else if (saves[i] != null) {
                slotColor = SLOT_COLOR;
            } else {
                slotColor = EMPTY_SLOT_COLOR;
            }
            
            shapeRenderer.setColor(slotColor);
            shapeRenderer.rect(slotX, slotY, SLOT_WIDTH, SLOT_HEIGHT);
            
            // Borde del slot seleccionado
            if (slot == selectedSlot) {
                shapeRenderer.end();
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(Color.WHITE);
                shapeRenderer.rect(slotX - 2, slotY - 2, SLOT_WIDTH + 4, SLOT_HEIGHT + 4);
                shapeRenderer.end();
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            }
        }
        
        shapeRenderer.end();
        
        // Texto de los slots
        batch.begin();
        
        for (int i = 0; i < saveManager.getMaxSaveSlots(); i++) {
            int slot = i + 1;
            float slotY = startY - (i * (SLOT_HEIGHT + SLOT_SPACING));
            float slotX = (screenWidth - SLOT_WIDTH) / 2;
            
            GameState save = saves[i];
            
            if (save != null) {
                // Información de la partida guardada
                String saveName = save.getSaveName();
                String saveDate = dateFormat.format(save.getSaveDate());
                String playTime = String.format("%.1f %s", save.getPlayTime() / 60f, localization.getText("save.minutes"));
                String level = localization.getText("save.level") + " " + save.getPlayerLevel();
                
                font.draw(batch, localization.getText("save.slot") + " " + slot + ": " + saveName, slotX + 10, slotY + SLOT_HEIGHT - 15);
                font.draw(batch, saveDate + " | " + playTime + " | " + level, slotX + 10, slotY + SLOT_HEIGHT - 35);
                font.draw(batch, localization.getText("save.village") + ": " + save.getVillageName(), slotX + 10, slotY + SLOT_HEIGHT - 55);
            } else {
                // Slot vacío
                font.draw(batch, localization.getText("save.slot") + " " + slot + ": " + localization.getText("save.empty"), slotX + 10, slotY + SLOT_HEIGHT - 30);
            }
        }
        
        batch.end();
    }
    
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }
    
    @Override
    public void pause() {}
    
    @Override
    public void resume() {}
    
    @Override
    public void hide() {}
    
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        titleFont.dispose();
        shapeRenderer.dispose();
    }
}