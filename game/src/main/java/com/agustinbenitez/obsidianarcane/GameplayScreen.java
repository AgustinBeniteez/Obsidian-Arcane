package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Input.Keys;

/**
 * Main gameplay screen for Obsidian Arcane
 * Integra Player y GameMap para crear el gameplay del roguelike
 */
public class GameplayScreen implements Screen {
    
    private GameStateManager game;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private LocalizationManager localization;
    private ShapeRenderer shapeRenderer;
    
    // Objetos del juego
    private Player player;
    private GameMap gameMap;
    
    // Game variables
    private float deltaTime;
    
    // Camera settings
    private boolean followPlayer = true;
    
    public GameplayScreen(GameStateManager game) {
        this(game, System.currentTimeMillis());
    }
    
    public GameplayScreen(GameStateManager game, long mapSeed) {
        this.game = game;
        
        // Initialize localization
        this.localization = LocalizationManager.getInstance();
        
        // Initialize components
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        shapeRenderer = new ShapeRenderer();
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 512, 384); // Cámara más cercana (reducida de 1024x768)
        
        // Initialize game objects with seed
        initializeGameObjects(mapSeed);
    }
    
    /**
     * Inicializa los objetos del juego (mapa y jugador)
     */
    private void initializeGameObjects(long mapSeed) {
        // Crear mapa de salas conectadas con seed específica
        gameMap = new GameMap(mapSeed);
        
        // Encontrar posición válida para el jugador
        Vector2 spawnPosition = gameMap.findValidSpawnPosition();
        player = new Player(spawnPosition.x, spawnPosition.y, gameMap); // Pasar referencia del mapa
        
        Gdx.app.log("GameplayScreen", "Objetos del juego inicializados con seed: " + mapSeed);
        Gdx.app.log("GameplayScreen", "Jugador spawneado en: " + spawnPosition.toString());
    }
    
    @Override
    public void show() {
        Gdx.app.log("GameplayScreen", localization.getText("game.starting"));
        
        // Imprimir mapa en consola para debug
        gameMap.printMapToConsole();
    }
    
    @Override
    public void render(float delta) {
        deltaTime = delta;
        
        // Update game objects
        updateGame(deltaTime);
        
        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Update camera
        updateCamera();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        // Render game
        renderGame();
        
        // Handle input
        handleInput();
    }
    
    /**
     * Actualiza la lógica del juego
     */
    private void updateGame(float deltaTime) {
        // Actualizar jugador
        player.update(deltaTime);
    }
    
    /**
     * Actualiza la cámara para seguir al jugador
     */
    private void updateCamera() {
        if (followPlayer) {
            Vector2 playerPos = player.getPosition();
            camera.position.set(playerPos.x, playerPos.y, 0);
            
            // Limitar cámara a los bordes del mapa
            float halfWidth = camera.viewportWidth / 2;
            float halfHeight = camera.viewportHeight / 2;
            
            camera.position.x = Math.max(halfWidth, 
                Math.min(gameMap.getTotalWidth() * gameMap.getTileSize() - halfWidth, camera.position.x));
            camera.position.y = Math.max(halfHeight, 
                Math.min(gameMap.getTotalHeight() * gameMap.getTileSize() - halfHeight, camera.position.y));
        }
    }
    
    /**
     * Renderiza todos los elementos del juego
     */
    private void renderGame() {
        // Renderizar mapa con ShapeRenderer
        shapeRenderer.setProjectionMatrix(camera.combined);
        gameMap.render(shapeRenderer);
        
        // Renderizar jugador con SpriteBatch
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch);
        batch.end();
        
        // Renderizar UI
        renderUI();
    }
    
    /**
     * Renderiza la interfaz de usuario
     */
    private void renderUI() {
        batch.begin();
        
        // Información del jugador
        font.draw(batch, "Estado: " + player.getCurrentState().toString(), 
                 camera.position.x - camera.viewportWidth/2 + 10, 
                 camera.position.y + camera.viewportHeight/2 - 20);
        
        font.draw(batch, "Posición: (" + (int)player.getPosition().x + ", " + (int)player.getPosition().y + ")", 
                 camera.position.x - camera.viewportWidth/2 + 10, 
                 camera.position.y + camera.viewportHeight/2 - 40);
        
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 
                 camera.position.x - camera.viewportWidth/2 + 10, 
                 camera.position.y + camera.viewportHeight/2 - 60);
        
        // Controles
        font.draw(batch, "WASD: Movimiento | K: Ataque | F5: Guardar | F9: Cargar | M: Menú", 
                 camera.position.x - camera.viewportWidth/2 + 10, 
                 camera.position.y - camera.viewportHeight/2 + 40);
        
        batch.end();
    }
    
    /**
     * Maneja la entrada del usuario
     */
    private void handleInput() {
        // Alternar seguimiento de cámara
        if (Gdx.input.isKeyJustPressed(Keys.C)) {
            followPlayer = !followPlayer;
            Gdx.app.log("GameplayScreen", "Seguimiento de cámara: " + followPlayer);
        }
        
        // Pausar juego con Escape
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            game.showPauseMenu();
        }
        
        // Guardar partida
        if (Gdx.input.isKeyJustPressed(Keys.F5)) {
            game.showSaveScreen();
        }
        
        // Cargar partida
        if (Gdx.input.isKeyJustPressed(Keys.F9)) {
            game.showLoadScreen();
        }
        
        // Volver al menú
        if (Gdx.input.isKeyJustPressed(Keys.M)) {
            game.showMainMenu();
        }
    }
    
    /**
     * Genera un nuevo mapa
     */
    private void generateNewMap() {
        Gdx.app.log("GameplayScreen", "Generando nuevo mapa...");
        
        // Regenerar el mapa de salas
        gameMap.regenerateMap();
        
        // Reposicionar jugador en nueva posición válida
        Vector2 newSpawnPosition = gameMap.findValidSpawnPosition();
        player.setPosition(newSpawnPosition.x, newSpawnPosition.y);
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
        if (batch != null) {
            batch.dispose();
        }
        if (font != null) {
            font.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (player != null) {
            player.dispose();
        }
        if (gameMap != null) {
            gameMap.dispose();
        }
    }
    
    /**
     * Obtiene la posición actual del jugador
     */
    public Vector2 getPlayerPosition() {
        if (player != null) {
            return new Vector2(player.getPosition());
        }
        return new Vector2(96, 96); // Posición por defecto
    }
    
    /**
     * Establece la posición del jugador
     */
    public void setPlayerPosition(Vector2 position) {
        if (player != null) {
            player.setPosition(position.x, position.y);
        }
    }
}