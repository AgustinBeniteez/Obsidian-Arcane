package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Color;

/**
 * Main Menu Screen for Obsidian Arcane
 */
public class MainMenuScreen implements Screen {
    
    private GameStateManager game;
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont buttonFont;
    private OrthographicCamera camera;
    private Texture titleLogo;
    private LocalizationManager localization;

    // UI Elements
    private Table mainTable;
    private Image titleImage;
    private TextButton playButton;
    private TextButton optionsButton;
    private TextButton exitButton;
    
    public MainMenuScreen(GameStateManager game) {
        this.game = game;
        
        // Initialize localization
        this.localization = LocalizationManager.getInstance();
        
        // Initialize components
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        stage = new Stage(new ScreenViewport(camera));
        
        // Configure input
        Gdx.input.setInputProcessor(stage);
        
        createUI();
    }
    
    private void createUI() {
        // Load title image
        titleLogo = new Texture(Gdx.files.internal("title_logo.png"));
        
        // Create sharper button font
        buttonFont = new BitmapFont();
        buttonFont.getData().setScale(1.8f);
        buttonFont.setColor(Color.WHITE);
        buttonFont.setUseIntegerPositions(false);
        buttonFont.getRegion().getTexture().setFilter(
            com.badlogic.gdx.graphics.Texture.TextureFilter.Linear, 
            com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
        );
        
        // Create button styles
        Skin skin = new Skin();
        skin.add("default-font", buttonFont);
        
        // Button style
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = buttonFont;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.YELLOW;
        buttonStyle.downFontColor = Color.GRAY;
        
        skin.add("default", buttonStyle);
        
        // Create UI elements
        titleImage = new Image(titleLogo);
        
        playButton = new TextButton(localization.getText("menu.play"), buttonStyle);
        playButton.getLabel().setAlignment(com.badlogic.gdx.utils.Align.left);
        
        optionsButton = new TextButton(localization.getText("menu.options"), buttonStyle);
        optionsButton.getLabel().setAlignment(com.badlogic.gdx.utils.Align.left);
        
        exitButton = new TextButton(localization.getText("menu.exit"), buttonStyle);
        exitButton.getLabel().setAlignment(com.badlogic.gdx.utils.Align.left);
        
        // Configure button listeners
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.startGame();
            }
        });
        
        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.showOptions();
            }
        });
        
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        
        // Create main table to organize elements
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.left(); // Align to the left
        mainTable.padLeft(100); // Add padding from the left
        
        // Add elements to the table
        mainTable.add(titleImage).width(800).padBottom(50).left().row();
        mainTable.add(playButton).width(300).height(60).padBottom(20).left().padLeft(10).row();
        mainTable.add(optionsButton).width(300).height(60).padBottom(20).left().padLeft(10).row();
        mainTable.add(exitButton).width(300).height(60).left().padLeft(10).row();
        
        // Add table to stage
        stage.addActor(mainTable);
    }

    /**
     * Refresh UI text after language change
     */
    public void refreshUI() {
        playButton.setText(localization.getText("menu.play"));
        optionsButton.setText(localization.getText("menu.options"));
        exitButton.setText(localization.getText("menu.exit"));
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        // Refresh UI when showing the screen to update language
        refreshUI();
    }
    
    @Override
    public void render(float delta) {
        // Limpiar pantalla con color oscuro
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Actualizar y renderizar stage
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    @Override
    public void pause() {}
    
    @Override
    public void resume() {}
    
    @Override
    public void hide() {}
    
    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        titleLogo.dispose();
        buttonFont.dispose();
    }
}