package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Color;

/**
 * Options Screen for Obsidian Arcane
 * Allows players to change game settings including language
 */
public class OptionsScreen implements Screen {
    
    private GameStateManager game;
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private LocalizationManager localization;

    // UI Elements
    private Table mainTable;
    private Label titleLabel;
    private Label languageLabel;
    private TextButton spanishButton;
    private TextButton englishButton;
    private TextButton backButton;
    
    public OptionsScreen(GameStateManager game) {
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
        // Create font
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        font.setUseIntegerPositions(false);
        font.getRegion().getTexture().setFilter(
            com.badlogic.gdx.graphics.Texture.TextureFilter.Linear, 
            com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
        );
        
        // Create styles
        Skin skin = new Skin();
        skin.add("default-font", font);
        
        // Label style
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        
        // Button style
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.YELLOW;
        buttonStyle.downFontColor = Color.GRAY;
        
        skin.add("default", labelStyle);
        skin.add("default", buttonStyle);
        
        // Create UI elements
        titleLabel = new Label(localization.getText("options.title"), labelStyle);
        languageLabel = new Label(localization.getText("options.language") + ":", labelStyle);
        
        spanishButton = new TextButton(localization.getText("options.spanish"), buttonStyle);
        englishButton = new TextButton(localization.getText("options.english"), buttonStyle);
        backButton = new TextButton(localization.getText("options.back"), buttonStyle);
        
        // Configure button listeners
        spanishButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                localization.setLanguage(LocalizationManager.Language.SPANISH);
                refreshUI();
                Gdx.app.log("OptionsScreen", "Idioma cambiado a Español");
            }
        });
        
        englishButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                localization.setLanguage(LocalizationManager.Language.ENGLISH);
                refreshUI();
                Gdx.app.log("OptionsScreen", "Language changed to English");
            }
        });
        
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.showMainMenu();
            }
        });
        
        // Create main table to organize elements
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();
        
        // Add elements to the table
        mainTable.add(titleLabel).padBottom(50).row();
        mainTable.add(languageLabel).padBottom(20).row();
        
        // Language buttons in a horizontal table
        Table languageTable = new Table();
        languageTable.add(spanishButton).width(200).height(60).padRight(20);
        languageTable.add(englishButton).width(200).height(60);
        
        mainTable.add(languageTable).padBottom(50).row();
        mainTable.add(backButton).width(200).height(60);
        
        // Add table to stage
        stage.addActor(mainTable);
    }
    
    /**
     * Refresh UI text after language change
     */
    private void refreshUI() {
        titleLabel.setText(localization.getText("options.title"));
        languageLabel.setText(localization.getText("options.language") + ":");
        spanishButton.setText(localization.getText("options.spanish"));
        englishButton.setText(localization.getText("options.english"));
        backButton.setText(localization.getText("options.back"));
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }
    
    @Override
    public void render(float delta) {
        // Clear screen with dark color
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Update and render stage
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
        font.dispose();
    }
}