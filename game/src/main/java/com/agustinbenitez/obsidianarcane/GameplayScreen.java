package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;

/**
 * Main gameplay screen for Obsidian Arcane
 * Here all the roguelike logic will be developed
 */
public class GameplayScreen implements Screen {
    
    private GameStateManager game;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private LocalizationManager localization;
    
    // Game variables
    private float deltaTime;
    
    public GameplayScreen(GameStateManager game) {
        this.game = game;
        
        // Initialize localization
        this.localization = LocalizationManager.getInstance();
        
        // Initialize components
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    
    @Override
    public void show() {
        Gdx.app.log("GameplayScreen", localization.getText("game.starting"));
    }
    
    @Override
    public void render(float delta) {
        deltaTime = delta;
        
        // Clear screen
        Gdx.gl.glClearColor(0.2f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Update camera
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        // Render
        batch.begin();
        
        // Temporary development text
        font.draw(batch, localization.getText("game.title"), 
                 Gdx.graphics.getWidth() / 2 - 150, Gdx.graphics.getHeight() / 2 + 50);
        font.draw(batch, localization.getText("game.subtitle"), 
                 Gdx.graphics.getWidth() / 2 - 120, Gdx.graphics.getHeight() / 2);
        font.draw(batch, localization.getText("game.return_menu"), 
                 Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2 - 50);
        font.draw(batch, localization.getText("game.fps", Gdx.graphics.getFramesPerSecond()), 20, 
                 Gdx.graphics.getHeight() - 20);
        
        batch.end();
        
        // Input to return to menu
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.M)) {
            game.showMainMenu();
        }
        
        // ESC to exit
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            game.showMainMenu();
        }
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
    }
}