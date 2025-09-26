package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Input.Keys;

/**
 * Pantalla simple de juego que no hace nada más que mostrar un mensaje
 */
public class SimpleGameScreen implements Screen {
    
    private GameStateManager game;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout glyphLayout;
    
    public SimpleGameScreen(GameStateManager game) {
        this.game = game;
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2.0f);
        glyphLayout = new GlyphLayout();
    }
    
    @Override
    public void show() {
    }
    
    @Override
    public void render(float delta) {
        // Fondo negro
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        handleInput();
        
        batch.begin();
        
        // Mensaje principal
        font.setColor(Color.WHITE);
        String message = "Simple Game Screen";
        glyphLayout.setText(font, message);
        float messageWidth = glyphLayout.width;
        font.draw(batch, message, 
                 (Gdx.graphics.getWidth() - messageWidth) / 2, 
                 Gdx.graphics.getHeight() / 2 + 50);
        
        // Instrucciones
        font.getData().setScale(1.0f);
        font.setColor(Color.LIGHT_GRAY);
        String instructions = "Press ESC to return to main menu";
        glyphLayout.setText(font, instructions);
        float instructionsWidth = glyphLayout.width;
        font.draw(batch, instructions, 
                 (Gdx.graphics.getWidth() - instructionsWidth) / 2, 
                 Gdx.graphics.getHeight() / 2 - 50);
        
        batch.end();
    }
    
    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            game.showMainMenu();
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
    }
}