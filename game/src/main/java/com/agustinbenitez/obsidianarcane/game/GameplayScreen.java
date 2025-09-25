package com.agustinbenitez.obsidianarcane.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.agustinbenitez.obsidianarcane.menu.ShopMenu;
import com.agustinbenitez.obsidianarcane.GameStateManager;
import com.agustinbenitez.obsidianarcane.GameMap;
import com.agustinbenitez.obsidianarcane.LocalizationManager;

/**
 * Main gameplay screen for Obsidian Arcane
 * Vista de gestión sin personaje jugable - enfoque en construcción y administración
 */
public class GameplayScreen implements Screen {
    
    private GameStateManager game;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private LocalizationManager localization;
    private ShapeRenderer shapeRenderer;
    
    // UI Components
    private Stage uiStage;
    private TextButton shopButton;
    private boolean showShopMenu = false;
    private ShopMenu shopMenu;
    
    // Objetos del juego
    private GameMap gameMap;
    
    // Game variables
    private float deltaTime;
    
    // Camera settings - posición fija para vista de gestor
    private Vector2 cameraPosition;
    
    public GameplayScreen(GameStateManager game) {
        this.game = game;
        
        // Initialize localization
        this.localization = LocalizationManager.getInstance();
        
        // Initialize components
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        shapeRenderer = new ShapeRenderer();
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        // Posición inicial de la cámara en el centro del mapa
        cameraPosition = new Vector2(400, 300);
        
        // Initialize UI
        initializeUI();
        
        // Initialize game objects
        initializeGameObjects();
    }
    
    /**
     * Inicializa el sistema de interfaz de usuario
     */
    private void initializeUI() {
        // Crear Stage para la UI
        uiStage = new Stage(new ScreenViewport());
        
        // Crear skin básico para los botones
        Skin skin = new Skin();
        skin.add("default-font", font);
        
        // Estilo del botón
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.YELLOW;
        buttonStyle.downFontColor = Color.GRAY;
        
        skin.add("default", buttonStyle);
        
        // Crear botón de tienda
        shopButton = new TextButton("Tienda", buttonStyle);
        shopButton.setSize(100, 40);
        shopButton.setPosition(10, 10); // Esquina inferior izquierda
        
        // Agregar listener al botón
        shopButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleShopMenu();
            }
        });
        
        // Agregar botón al stage
        uiStage.addActor(shopButton);
        
        // El InputMultiplexer se configurará después de inicializar todos los objetos
    }
    
    /**
     * Alterna la visibilidad del menú de tienda
     */
    private void toggleShopMenu() {
        if (shopMenu != null) {
            shopMenu.toggle();
        }
        Gdx.app.log("GameplayScreen", "Menú de tienda: " + (shopMenu != null && shopMenu.isVisible() ? "Abierto" : "Cerrado"));
    }
    
    /**
     * Inicializa los objetos del juego (solo mapa)
     */
    private void initializeGameObjects() {
        // Crear mapa vacío simple
        gameMap = new GameMap();
        
        // Inicializar el menú de tienda con la cámara
        shopMenu = new ShopMenu(font, gameMap, camera);
        
        // Configurar InputMultiplexer con todos los stages
        setupInputProcessors();
        
        Gdx.app.log("GameplayScreen", "Objetos del juego inicializados - Vista de gestión");
    }
    
    private void setupInputProcessors() {
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        
        // Agregar el stage del menú de tienda primero (mayor prioridad)
        if (shopMenu != null) {
            inputMultiplexer.addProcessor(shopMenu.getStage());
        }
        
        // Agregar el stage de la UI principal
        inputMultiplexer.addProcessor(uiStage);
        
        Gdx.input.setInputProcessor(inputMultiplexer);
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
        
        // Update and render UI
        uiStage.act(delta);
        uiStage.draw();
        
        // Update and render shop menu
        if (shopMenu != null) {
            shopMenu.act(delta);
            shopMenu.draw();
        }
        
        // Handle input
        handleInput();
    }
    
    /**
     * Actualiza la lógica del juego
     */
    private void updateGame(float deltaTime) {
        // Actualizar mapa (construcciones)
        gameMap.update(deltaTime);
    }
    
    /**
     * Actualiza la cámara - posición fija para vista de gestor
     */
    private void updateCamera() {
        // Posicionar cámara en la posición fija
        camera.position.set(cameraPosition.x, cameraPosition.y, 0);
        
        // Limitar cámara a los bordes del mapa
        float halfWidth = camera.viewportWidth / 2;
        float halfHeight = camera.viewportHeight / 2;
        
        // Calcular las dimensiones totales del mapa en píxeles
        float mapWidthPixels = gameMap.getMapWidth() * gameMap.getTileSize();
        float mapHeightPixels = gameMap.getMapHeight() * gameMap.getTileSize();
        
        // Limitar la posición de la cámara para que no se salga del mapa
        camera.position.x = Math.max(halfWidth, 
            Math.min(mapWidthPixels - halfWidth, camera.position.x));
        camera.position.y = Math.max(halfHeight, 
            Math.min(mapHeightPixels - halfHeight, camera.position.y));
        
        camera.update();
    }
    
    /**
     * Renderiza todos los elementos del juego
     */
    private void renderGame() {
        // Renderizar mapa con ShapeRenderer
        shapeRenderer.setProjectionMatrix(camera.combined);
        gameMap.render(shapeRenderer);
        
        // Renderizar sprites de construcciones con SpriteBatch
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        // Renderizar sprites de construcciones
        gameMap.renderBuildings(batch);
        
        batch.end();
        
        // Renderizar UI
        renderUI();
    }
    
    /**
     * Renderiza la interfaz de usuario
     */
    private void renderUI() {
        batch.begin();
        
        // Información del gestor
        font.draw(batch, "Modo: Gestión", 
                 camera.position.x - camera.viewportWidth/2 + 10, 
                 camera.position.y + camera.viewportHeight/2 - 20);
        
        font.draw(batch, "Cámara: (" + (int)camera.position.x + ", " + (int)camera.position.y + ")", 
                 camera.position.x - camera.viewportWidth/2 + 10, 
                 camera.position.y + camera.viewportHeight/2 - 40);
        
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 
                 camera.position.x - camera.viewportWidth/2 + 10, 
                 camera.position.y + camera.viewportHeight/2 - 60);
        
        batch.end();
    }
    
    /**
     * Maneja la entrada del usuario - controles de cámara para gestor
     */
    private void handleInput() {
        // Movimiento de cámara con flechas
        float cameraSpeed = 200f * deltaTime;
        
        if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) {
            cameraPosition.x -= cameraSpeed;
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) {
            cameraPosition.x += cameraSpeed;
        }
        if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) {
            cameraPosition.y += cameraSpeed;
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)) {
            cameraPosition.y -= cameraSpeed;
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
    }
    
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        uiStage.getViewport().update(width, height, true);
        if (shopMenu != null) {
            shopMenu.resize(width, height);
        }
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
        if (uiStage != null) {
            uiStage.dispose();
        }
        if (shopMenu != null) {
            shopMenu.dispose();
        }
        if (gameMap != null) {
            gameMap.dispose();
        }
    }
    
    /**
     * Obtiene la posición actual de la cámara
     */
    public Vector2 getCameraPosition() {
        return new Vector2(cameraPosition);
    }
    
    /**
     * Establece la posición de la cámara
     */
    public void setCameraPosition(Vector2 position) {
        cameraPosition.set(position);
    }
}