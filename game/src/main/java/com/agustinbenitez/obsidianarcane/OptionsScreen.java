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
 * Allows players to change game settings including language, resolution and display mode
 */
public class OptionsScreen implements Screen {
    
    private GameStateManager game;
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private LocalizationManager localization;
    private GameConfig gameConfig;

    // UI Elements
    private Table mainTable;
    private Label titleLabel;
    private Label languageLabel;
    private TextButton spanishButton;
    private TextButton englishButton;
    
    // Display settings
    private Label displayLabel;
    private Label resolutionLabel;
    private SelectBox<GameConfig.Resolution> resolutionSelectBox;
    private CheckBox fullscreenCheckBox;
    private TextButton applyButton;
    private TextButton backButton;
    
    public OptionsScreen(GameStateManager game) {
        this.game = game;
        
        // Initialize localization and config
        this.localization = LocalizationManager.getInstance();
        this.gameConfig = GameConfig.getInstance();
        
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
        
        // Create a white pixel texture for UI backgrounds
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new com.badlogic.gdx.graphics.Texture(pixmap));
        pixmap.dispose();
        
        // Create larger checkbox texture (32x32 pixels) - unchecked state
        com.badlogic.gdx.graphics.Pixmap checkboxPixmap = new com.badlogic.gdx.graphics.Pixmap(32, 32, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        checkboxPixmap.setColor(Color.DARK_GRAY);
        checkboxPixmap.fill();
        // Draw border
        checkboxPixmap.setColor(Color.WHITE);
        checkboxPixmap.drawRectangle(0, 0, 32, 32);
        checkboxPixmap.drawRectangle(1, 1, 30, 30);
        skin.add("checkbox", new com.badlogic.gdx.graphics.Texture(checkboxPixmap));
        checkboxPixmap.dispose();
        
        // Create checked checkbox texture with checkmark
        com.badlogic.gdx.graphics.Pixmap checkboxCheckedPixmap = new com.badlogic.gdx.graphics.Pixmap(32, 32, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        checkboxCheckedPixmap.setColor(new Color(0.0f, 0.6f, 0.0f, 1.0f)); // Verde más oscuro
        checkboxCheckedPixmap.fill();
        // Draw border
        checkboxCheckedPixmap.setColor(Color.WHITE);
        checkboxCheckedPixmap.drawRectangle(0, 0, 32, 32);
        checkboxCheckedPixmap.drawRectangle(1, 1, 30, 30);
        // Draw checkmark (simple lines)
        checkboxCheckedPixmap.setColor(Color.WHITE);
        // Draw checkmark lines
        for (int i = 0; i < 3; i++) {
            checkboxCheckedPixmap.drawLine(8 + i, 16, 12 + i, 20);
            checkboxCheckedPixmap.drawLine(12 + i, 20, 22 + i, 10);
        }
        skin.add("checkbox-checked", new com.badlogic.gdx.graphics.Texture(checkboxCheckedPixmap));
        checkboxCheckedPixmap.dispose();
        
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
        
        // SelectBox style
        SelectBox.SelectBoxStyle selectBoxStyle = new SelectBox.SelectBoxStyle();
        selectBoxStyle.font = font;
        selectBoxStyle.fontColor = Color.WHITE;
        selectBoxStyle.background = skin.newDrawable("white", Color.DARK_GRAY);
        selectBoxStyle.backgroundOver = skin.newDrawable("white", Color.GRAY);
        selectBoxStyle.backgroundOpen = skin.newDrawable("white", Color.LIGHT_GRAY);
        
        // List style for SelectBox
        List.ListStyle listStyle = new List.ListStyle();
        listStyle.font = font;
        listStyle.fontColorSelected = Color.BLACK;
        listStyle.fontColorUnselected = Color.WHITE;
        listStyle.background = skin.newDrawable("white", Color.DARK_GRAY);
        listStyle.selection = skin.newDrawable("white", Color.YELLOW);
        selectBoxStyle.listStyle = listStyle;
        
        // ScrollPane style for SelectBox
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        selectBoxStyle.scrollStyle = scrollPaneStyle;
        
        // CheckBox style with larger size and visible checkmark
        CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle();
        checkBoxStyle.font = font;
        checkBoxStyle.fontColor = Color.WHITE;
        checkBoxStyle.checkboxOn = skin.getDrawable("checkbox-checked");
        checkBoxStyle.checkboxOff = skin.getDrawable("checkbox");
        checkBoxStyle.checkboxOver = skin.newDrawable("checkbox", Color.GRAY);
        checkBoxStyle.checkboxOnOver = skin.newDrawable("checkbox-checked", Color.LIGHT_GRAY);
        
        skin.add("default", labelStyle);
        skin.add("default", buttonStyle);
        skin.add("default", selectBoxStyle);
        skin.add("default", checkBoxStyle);
        
        // Create UI elements
        titleLabel = new Label(localization.getText("options.title"), labelStyle);
        
        // Language section
        languageLabel = new Label(localization.getText("options.language") + ":", labelStyle);
        spanishButton = new TextButton(localization.getText("options.spanish"), buttonStyle);
        englishButton = new TextButton(localization.getText("options.english"), buttonStyle);
        
        // Display section
        displayLabel = new Label(localization.getText("options.display") + ":", labelStyle);
        resolutionLabel = new Label(localization.getText("options.resolution") + ":", labelStyle);
        
        // Resolution SelectBox
        resolutionSelectBox = new SelectBox<>(selectBoxStyle);
        resolutionSelectBox.setItems(GameConfig.AVAILABLE_RESOLUTIONS);
        resolutionSelectBox.setSelected(gameConfig.getCurrentResolution());
        
        // Fullscreen CheckBox
        fullscreenCheckBox = new CheckBox(" " + localization.getText("options.fullscreen"), checkBoxStyle);
        fullscreenCheckBox.setChecked(gameConfig.isFullscreen());
        
        // Action buttons
        applyButton = new TextButton(localization.getText("options.apply"), buttonStyle);
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
        
        applyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applySettings();
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
        mainTable.add(titleLabel).colspan(2).padBottom(30).row();
        
        // Language section
        mainTable.add(languageLabel).colspan(2).padBottom(10).row();
        Table languageTable = new Table();
        languageTable.add(spanishButton).width(150).height(50).padRight(10);
        languageTable.add(englishButton).width(150).height(50);
        mainTable.add(languageTable).colspan(2).padBottom(30).row();
        
        // Display section
        mainTable.add(displayLabel).colspan(2).padBottom(10).row();
        
        // Resolution
        mainTable.add(resolutionLabel).padRight(20);
        mainTable.add(resolutionSelectBox).width(300).height(50).padBottom(10).row();
        
        // Fullscreen CheckBox with better layout and size
        fullscreenCheckBox = new CheckBox(" " + localization.getText("options.fullscreen"), checkBoxStyle);
        fullscreenCheckBox.setChecked(gameConfig.isFullscreen());
        mainTable.add(fullscreenCheckBox).colspan(2).height(50).padBottom(30).row();
        
        // Action buttons
        Table buttonTable = new Table();
        buttonTable.add(applyButton).width(150).height(50).padRight(10);
        buttonTable.add(backButton).width(150).height(50);
        mainTable.add(buttonTable).colspan(2);
        
        // Add table to stage
        stage.addActor(mainTable);
    }
    
    /**
     * Apply the selected settings
     */
    private void applySettings() {
        try {
            // Apply resolution
            GameConfig.Resolution selectedResolution = resolutionSelectBox.getSelected();
            gameConfig.setResolution(selectedResolution);
            
            // Apply fullscreen setting
            gameConfig.setFullscreen(fullscreenCheckBox.isChecked());
            
            Gdx.app.log("OptionsScreen", "Configuración aplicada: " + 
                selectedResolution.displayName + ", Pantalla completa: " + fullscreenCheckBox.isChecked());
                
        } catch (Exception e) {
            Gdx.app.error("OptionsScreen", "Error al aplicar configuración: " + e.getMessage());
        }
    }
    
    /**
     * Refresh UI text after language change
     */
    private void refreshUI() {
        titleLabel.setText(localization.getText("options.title"));
        languageLabel.setText(localization.getText("options.language") + ":");
        spanishButton.setText(localization.getText("options.spanish"));
        englishButton.setText(localization.getText("options.english"));
        displayLabel.setText(localization.getText("options.display") + ":");
        resolutionLabel.setText(localization.getText("options.resolution") + ":");
        // Update checkbox text directly
        fullscreenCheckBox.setText(" " + localization.getText("options.fullscreen"));
        applyButton.setText(localization.getText("options.apply"));
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