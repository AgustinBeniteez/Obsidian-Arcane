package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Input.Keys;

/**
 * Pantalla de créditos para Obsidian Arcane
 * Muestra información sobre el desarrollador y agradecimientos
 */
public class CreditsScreen implements Screen {
    
    private GameStateManager game;
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont font;
    private BitmapFont nameFont;
    private OrthographicCamera camera;
    private LocalizationManager localization;
    private GlyphLayout glyphLayout;
    
    // Configuración visual
    private static final Color BACKGROUND_COLOR = new Color(0.05f, 0.05f, 0.15f, 1f);
    private static final Color TITLE_COLOR = new Color(0.9f, 0.9f, 1f, 1f);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color NAME_COLOR = new Color(1f, 0.8f, 0.2f, 1f); // Dorado para el nombre
    private static final Color THANKS_COLOR = new Color(0.7f, 0.9f, 0.7f, 1f); // Verde claro
    
    public CreditsScreen(GameStateManager game) {
        this.game = game;
        this.localization = LocalizationManager.getInstance();
        
        // Inicializar componentes
        batch = new SpriteBatch();
        glyphLayout = new GlyphLayout();
        
        // Crear fuentes con diferentes tamaños
        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.5f);
        titleFont.setColor(TITLE_COLOR);
        titleFont.setUseIntegerPositions(false);
        titleFont.getRegion().getTexture().setFilter(
            com.badlogic.gdx.graphics.Texture.TextureFilter.Linear, 
            com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
        );
        
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        font.setColor(TEXT_COLOR);
        font.setUseIntegerPositions(false);
        font.getRegion().getTexture().setFilter(
            com.badlogic.gdx.graphics.Texture.TextureFilter.Linear, 
            com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
        );
        
        nameFont = new BitmapFont();
        nameFont.getData().setScale(2f);
        nameFont.setColor(NAME_COLOR);
        nameFont.setUseIntegerPositions(false);
        nameFont.getRegion().getTexture().setFilter(
            com.badlogic.gdx.graphics.Texture.TextureFilter.Linear, 
            com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
        );
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    
    @Override
    public void show() {
        Gdx.app.log("CreditsScreen", "Mostrando pantalla de créditos");
    }
    
    @Override
    public void render(float delta) {
        handleInput();
        
        // Limpiar pantalla
        Gdx.gl.glClearColor(BACKGROUND_COLOR.r, BACKGROUND_COLOR.g, BACKGROUND_COLOR.b, BACKGROUND_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        renderCredits();
    }
    
    private void handleInput() {
        // Volver al menú principal con Escape o Enter
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE) || 
            Gdx.input.isKeyJustPressed(Keys.ENTER) ||
            Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            game.showMainMenu();
        }
    }
    
    private void renderCredits() {
        batch.begin();
        
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float centerX = screenWidth / 2f;
        
        // Título
        String title = localization.getText("credits.title");
        glyphLayout.setText(titleFont, title);
        float titleWidth = glyphLayout.width;
        titleFont.draw(batch, title, centerX - titleWidth / 2f, screenHeight - 100);
        
        // Desarrollador
        String developerLabel = localization.getText("credits.developer") + ":";
        glyphLayout.setText(font, developerLabel);
        float developerWidth = glyphLayout.width;
        font.draw(batch, developerLabel, centerX - developerWidth / 2f, screenHeight - 200);
        
        // Nombre del desarrollador
        String developerName = localization.getText("credits.name");
        glyphLayout.setText(nameFont, developerName);
        float nameWidth = glyphLayout.width;
        nameFont.draw(batch, developerName, centerX - nameWidth / 2f, screenHeight - 250);
        
        // Agradecimientos
        String thanks = localization.getText("credits.thanks");
        glyphLayout.setText(font, thanks);
        float thanksWidth = glyphLayout.width;
        
        // Cambiar color temporalmente para el mensaje de agradecimiento
        font.setColor(THANKS_COLOR);
        font.draw(batch, thanks, centerX - thanksWidth / 2f, screenHeight - 350);
        font.setColor(TEXT_COLOR); // Restaurar color original
        
        // Instrucciones para volver
        String backInstructions = localization.getText("credits.back");
        glyphLayout.setText(font, backInstructions);
        float instructionsWidth = glyphLayout.width;
        font.draw(batch, backInstructions, centerX - instructionsWidth / 2f, 100);
        
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
        titleFont.dispose();
        font.dispose();
        nameFont.dispose();
    }
}