package com.agustinbenitez.obsidianarcane.menu;

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
import com.badlogic.gdx.Input.Keys;
import com.agustinbenitez.obsidianarcane.GameStateManager;
import com.agustinbenitez.obsidianarcane.LocalizationManager;

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
    private TextButton creditsButton;
    private TextButton exitButton;
    
    // Keyboard navigation
    private TextButton[] menuButtons;
    private int selectedButtonIndex = 0;
    private TextButton.TextButtonStyle normalStyle;
    private TextButton.TextButtonStyle selectedStyle;

    public MainMenuScreen(GameStateManager game) {
        this.game = game;
        
        // Initialize localization
        this.localization = LocalizationManager.getInstance();
        
        // Initialize graphics components
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        stage = new Stage(new ScreenViewport(camera));
        
        // Load title logo
        titleLogo = new Texture(Gdx.files.internal("title_logo.png"));
        
        Gdx.input.setInputProcessor(stage);
        
        createUI();
    }

    private void createUI() {
        // Create font using default BitmapFont instead of FreeType to avoid font file dependency
        buttonFont = new BitmapFont();
        buttonFont.getData().setScale(1.5f); // Make it a bit larger
        buttonFont.setColor(Color.WHITE);

        // Create button styles
        normalStyle = new TextButton.TextButtonStyle();
        normalStyle.font = buttonFont;
        normalStyle.fontColor = Color.WHITE;
        normalStyle.overFontColor = Color.YELLOW;
        normalStyle.downFontColor = Color.GRAY;

        selectedStyle = new TextButton.TextButtonStyle();
        selectedStyle.font = buttonFont;
        selectedStyle.fontColor = Color.YELLOW;
        selectedStyle.overFontColor = Color.YELLOW;
        selectedStyle.downFontColor = Color.GRAY;

        // Create main table
        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // Add title logo image instead of text
        titleImage = new Image(titleLogo);
        mainTable.add(titleImage).padBottom(50).row();

        // Create buttons
        playButton = new TextButton(localization.getText("menu.play"), normalStyle);
        playButton.getLabel().setAlignment(com.badlogic.gdx.utils.Align.left);
        
        optionsButton = new TextButton(localization.getText("menu.options"), normalStyle);
        optionsButton.getLabel().setAlignment(com.badlogic.gdx.utils.Align.left);
        
        creditsButton = new TextButton(localization.getText("menu.credits"), normalStyle);
        creditsButton.getLabel().setAlignment(com.badlogic.gdx.utils.Align.left);
        
        exitButton = new TextButton(localization.getText("menu.exit"), normalStyle);
        exitButton.getLabel().setAlignment(com.badlogic.gdx.utils.Align.left);
        
        // Initialize menu buttons array for keyboard navigation
        menuButtons = new TextButton[]{playButton, optionsButton, creditsButton, exitButton};
        updateButtonStyles();
        
        // Configure button listeners
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Mostrar pantalla de selección de partida en lugar de iniciar directamente
                game.showLoadScreen();
            }
        });
        
        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.showOptions();
            }
        });
        
        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.showCredits();
            }
        });
        
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Add buttons to table
        mainTable.add(playButton).width(200).height(50).padBottom(10).row();
        mainTable.add(optionsButton).width(200).height(50).padBottom(10).row();
        mainTable.add(creditsButton).width(200).height(50).padBottom(10).row();
        mainTable.add(exitButton).width(200).height(50).padBottom(10).row();
    }

    private void handleInput() {
        // Navigate with arrow keys
        if (Gdx.input.isKeyJustPressed(Keys.UP)) {
            selectedButtonIndex = (selectedButtonIndex - 1 + menuButtons.length) % menuButtons.length;
            updateButtonStyles();
        } else if (Gdx.input.isKeyJustPressed(Keys.DOWN)) {
            selectedButtonIndex = (selectedButtonIndex + 1) % menuButtons.length;
            updateButtonStyles();
        }
        
        // Select with Enter or Space
        if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            selectCurrentButton();
        }
        
        // Exit with Escape
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    private void updateButtonStyles() {
        for (int i = 0; i < menuButtons.length; i++) {
            if (i == selectedButtonIndex) {
                menuButtons[i].setStyle(selectedStyle);
            } else {
                menuButtons[i].setStyle(normalStyle);
            }
        }
    }

    private void selectCurrentButton() {
        switch (selectedButtonIndex) {
            case 0: // Play
                game.showLoadScreen();
                break;
            case 1: // Options
                game.showOptions();
                break;
            case 2: // Credits
                game.showCredits();
                break;
            case 3: // Exit
                Gdx.app.exit();
                break;
        }
    }

    /**
     * Refresh UI elements when language changes
     */
    public void refreshUI() {
        playButton.setText(localization.getText("menu.play"));
        optionsButton.setText(localization.getText("menu.options"));
        creditsButton.setText(localization.getText("menu.credits"));
        exitButton.setText(localization.getText("menu.exit"));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        // Refresh UI in case language changed
        refreshUI();
    }

    @Override
    public void render(float delta) {
        // Handle input
        handleInput();
        
        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Update and draw stage
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
        buttonFont.dispose();
        if (titleLogo != null) {
            titleLogo.dispose();
        }
    }
}