package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Input.Keys;

/**
 * Simple game screen with 2D room-based gameplay and character physics
 */
public class SimpleGameScreen implements Screen {
    
    private GameStateManager game;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout glyphLayout;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private GameWorld gameWorld;
    
    // Pause menu state
    private boolean isPaused;
    private int selectedMenuOption;
    private String[] pauseMenuOptions = {"Reanudar", "Reiniciar", "Menú Principal"};
    
    public SimpleGameScreen(GameStateManager game) {
        this.game = game;
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        glyphLayout = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();
        
        // Setup camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        
        // Create game world
        gameWorld = new GameWorld();
        
        // Initialize pause state
        isPaused = false;
        selectedMenuOption = 0;
    }
    
    @Override
    public void show() {
    }
    
    @Override
    public void render(float delta) {
        // Clear screen with dark background
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Handle input
        handleInput();
        
        // Update game world only if not paused
        if (!isPaused) {
            gameWorld.update(delta);
        }
        
        // Update camera
        camera.update();
        
        // Render game world
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        gameWorld.render(shapeRenderer);
        shapeRenderer.end();
        
        // Render UI
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        if (!isPaused) {
            // Instructions in top-left corner
            font.getData().setScale(1.0f);
            font.setColor(Color.WHITE);
            font.draw(batch, "WASD/Arrow Keys: Move", 10, 590);
            font.draw(batch, "SPACE: Jump", 10, 570);
            font.draw(batch, "ESC: Pause", 10, 550);
        } else {
            // Render pause menu
            renderPauseMenu();
        }
        
        batch.end();
    }
    
    private void handleInput() {
        if (isPaused) {
            // Handle pause menu input
            if (Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.W)) {
                selectedMenuOption = (selectedMenuOption - 1 + pauseMenuOptions.length) % pauseMenuOptions.length;
            }
            if (Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.S)) {
                selectedMenuOption = (selectedMenuOption + 1) % pauseMenuOptions.length;
            }
            if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
                handlePauseMenuSelection();
            }
            if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
                isPaused = false; // Resume game
            }
        } else {
            // Normal game input
            boolean moveLeft = Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT);
            boolean moveRight = Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT);
            boolean jump = Gdx.input.isKeyPressed(Keys.SPACE) || Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP);
            
            // Pass input to game world
            gameWorld.handleInput(moveLeft, moveRight, jump);
            
            // Pause menu
            if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
                isPaused = true;
            }
            
            // Reset player position (for testing)
            if (Gdx.input.isKeyJustPressed(Keys.R)) {
                gameWorld.resetPlayer();
            }
        }
    }
    
    /**
     * Render the pause menu overlay
     */
    private void renderPauseMenu() {
        // End the current batch to draw shapes
        batch.end();
        
        // Semi-transparent background overlay
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f);
        shapeRenderer.rect(0, 0, 800, 600);
        shapeRenderer.end();
        
        // Menu background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.2f, 0.3f, 0.9f);
        shapeRenderer.rect(250, 200, 300, 200);
        shapeRenderer.end();
        
        // Menu border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(250, 200, 300, 200);
        shapeRenderer.end();
        
        // Restart batch for text rendering
        batch.begin();
        
        // Menu title
        font.getData().setScale(2.0f);
        font.setColor(Color.WHITE);
        glyphLayout.setText(font, "PAUSA");
        float titleWidth = glyphLayout.width;
        font.draw(batch, "PAUSA", 400 - titleWidth / 2, 370);
        
        // Menu options
        font.getData().setScale(1.5f);
        for (int i = 0; i < pauseMenuOptions.length; i++) {
            if (i == selectedMenuOption) {
                font.setColor(Color.YELLOW);
                font.draw(batch, "> " + pauseMenuOptions[i], 280, 320 - i * 40);
            } else {
                font.setColor(Color.WHITE);
                font.draw(batch, "  " + pauseMenuOptions[i], 280, 320 - i * 40);
            }
        }
        
        // Instructions
        font.getData().setScale(1.0f);
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "W/S: Navegar  ENTER/SPACE: Seleccionar  ESC: Reanudar", 260, 180);
    }
    
    /**
     * Handle pause menu selection
     */
    private void handlePauseMenuSelection() {
        switch (selectedMenuOption) {
            case 0: // Reanudar
                isPaused = false;
                break;
            case 1: // Reiniciar
                gameWorld.resetPlayer();
                isPaused = false;
                break;
            case 2: // Menú Principal
                game.showMainMenu();
                break;
        }
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
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}