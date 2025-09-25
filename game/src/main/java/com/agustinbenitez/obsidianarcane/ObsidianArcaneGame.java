package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;

/**
 * Clase principal del juego Obsidian Arcane
 * Maneja el game loop y la lógica principal del roguelike
 */
public class ObsidianArcaneGame extends ApplicationAdapter {
    
    // Basic rendering components
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;
    
    // Variables del juego
    private float deltaTime;
    
    @Override
    public void create() {
        // Inicializar componentes de renderizado
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        
        // Configure camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1024, 768);
        
        // Mensaje de inicio
        Gdx.app.log("ObsidianArcane", "Juego iniciado correctamente!");
        Gdx.app.log("ObsidianArcane", "Roguelike Dungeon Crawler 2D");
    }
    
    @Override
    public void render() {
        deltaTime = Gdx.graphics.getDeltaTime();
        
        // Limpiar pantalla con color negro
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Update camera
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        // Renderizar
        batch.begin();
        
        // Texto temporal de desarrollo
        font.draw(batch, "OBSIDIAN ARCANE", 400, 400);
        font.draw(batch, "Roguelike Dungeon Crawler 2D", 350, 370);
        font.draw(batch, "Presiona ESC para salir", 380, 340);
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, 750);
        
        batch.end();
        
        // Basic input to close the game
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }
    
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }
    
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        Gdx.app.log("ObsidianArcane", "Recursos liberados correctamente");
    }
}