package com.agustinbenitez.obsidianarcane.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.agustinbenitez.obsidianarcane.GameStateManager;
import com.agustinbenitez.obsidianarcane.LocalizationManager;
import com.agustinbenitez.obsidianarcane.GameConfig;

/**
 * Options Screen for Obsidian Arcane with keyboard navigation
 * Allows players to change game settings including language, resolution and display mode
 */
public class OptionsScreen implements Screen {
    
    public enum ScreenContext {
        MAIN_MENU,
        PAUSE_MENU
    }
    
    private enum MenuOption {
        SPANISH_BUTTON,
        ENGLISH_BUTTON,
        RESOLUTION_SELECT,
        FPS_SELECT,
        FULLSCREEN_TOGGLE,
        APPLY_BUTTON,
        BACK_BUTTON
    }
    
    private GameStateManager game;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont titleFont;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private GlyphLayout glyphLayout;
    private LocalizationManager localization;
    private GameConfig gameConfig;
    private ScreenContext context;
    
    // Navigation
    private MenuOption[] menuOptions;
    private int selectedOptionIndex = 0;
    
    // Settings state
    private int selectedResolutionIndex = 0;
    private int selectedFPSIndex = 0;
    private boolean fullscreenEnabled = false;
    
    // Visual constants
    private static final Color BACKGROUND_COLOR = new Color(0.05f, 0.05f, 0.15f, 1);
    private static final Color SELECTED_COLOR = new Color(1f, 0.8f, 0.2f, 1);
    private static final Color NORMAL_COLOR = new Color(0.8f, 0.8f, 0.8f, 1);
    private static final Color TITLE_COLOR = new Color(1f, 1f, 1f, 1);
    private static final Color SECTION_COLOR = new Color(0.9f, 0.9f, 0.9f, 1);
    
    public OptionsScreen(GameStateManager game) {
        this(game, ScreenContext.MAIN_MENU);
    }
    
    public OptionsScreen(GameStateManager game, ScreenContext context) {
        this.game = game;
        this.context = context;
        
        // Initialize localization and config
        this.localization = LocalizationManager.getInstance();
        this.gameConfig = GameConfig.getInstance();
        
        // Initialize graphics components
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        shapeRenderer = new ShapeRenderer();
        glyphLayout = new GlyphLayout();
        
        // Initialize fonts
        font = new BitmapFont();
        font.getData().setScale(1.2f);
        font.setColor(NORMAL_COLOR);
        
        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.0f);
        titleFont.setColor(TITLE_COLOR);
        
        // Initialize menu options
        menuOptions = MenuOption.values();
        
        // Load current settings
        loadCurrentSettings();
    }
    
    private void loadCurrentSettings() {
        // Find current resolution index
        GameConfig.Resolution currentRes = gameConfig.getCurrentResolution();
        for (int i = 0; i < GameConfig.AVAILABLE_RESOLUTIONS.length; i++) {
            if (GameConfig.AVAILABLE_RESOLUTIONS[i].equals(currentRes)) {
                selectedResolutionIndex = i;
                break;
            }
        }
        
        // Find current FPS index
        GameConfig.FPSOption currentFPS = gameConfig.getCurrentFPSOption();
        for (int i = 0; i < GameConfig.AVAILABLE_FPS_OPTIONS.length; i++) {
            if (GameConfig.AVAILABLE_FPS_OPTIONS[i].equals(currentFPS)) {
                selectedFPSIndex = i;
                break;
            }
        }
        
        fullscreenEnabled = gameConfig.isFullscreen();
    }
    
    private void handleInput() {
        // Navigate with arrow keys or WASD
        if (Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.W)) {
            selectedOptionIndex = (selectedOptionIndex - 1 + menuOptions.length) % menuOptions.length;
        } else if (Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.S)) {
            selectedOptionIndex = (selectedOptionIndex + 1) % menuOptions.length;
        }
        
        // Handle horizontal navigation for select boxes
        if (Gdx.input.isKeyJustPressed(Keys.LEFT) || Gdx.input.isKeyJustPressed(Keys.A)) {
            handleLeftNavigation();
        } else if (Gdx.input.isKeyJustPressed(Keys.RIGHT) || Gdx.input.isKeyJustPressed(Keys.D)) {
            handleRightNavigation();
        }
        
        // Select with Enter or Space
        if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            selectCurrentOption();
        }
        
        // Back with Escape
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            goBack();
        }
    }
    
    private void handleLeftNavigation() {
        MenuOption currentOption = menuOptions[selectedOptionIndex];
        switch (currentOption) {
            case RESOLUTION_SELECT:
                selectedResolutionIndex = (selectedResolutionIndex - 1 + GameConfig.AVAILABLE_RESOLUTIONS.length) % GameConfig.AVAILABLE_RESOLUTIONS.length;
                break;
            case FPS_SELECT:
                selectedFPSIndex = (selectedFPSIndex - 1 + GameConfig.AVAILABLE_FPS_OPTIONS.length) % GameConfig.AVAILABLE_FPS_OPTIONS.length;
                break;
            case FULLSCREEN_TOGGLE:
                fullscreenEnabled = !fullscreenEnabled;
                break;
        }
    }
    
    private void handleRightNavigation() {
        MenuOption currentOption = menuOptions[selectedOptionIndex];
        switch (currentOption) {
            case RESOLUTION_SELECT:
                selectedResolutionIndex = (selectedResolutionIndex + 1) % GameConfig.AVAILABLE_RESOLUTIONS.length;
                break;
            case FPS_SELECT:
                selectedFPSIndex = (selectedFPSIndex + 1) % GameConfig.AVAILABLE_FPS_OPTIONS.length;
                break;
            case FULLSCREEN_TOGGLE:
                fullscreenEnabled = !fullscreenEnabled;
                break;
        }
    }
    
    private void selectCurrentOption() {
        MenuOption currentOption = menuOptions[selectedOptionIndex];
        switch (currentOption) {
            case SPANISH_BUTTON:
                localization.setLanguage(LocalizationManager.Language.SPANISH);
                Gdx.app.log("OptionsScreen", "Language changed to Spanish");
                break;
            case ENGLISH_BUTTON:
                localization.setLanguage(LocalizationManager.Language.ENGLISH);
                Gdx.app.log("OptionsScreen", "Language changed to English");
                break;
            case RESOLUTION_SELECT:
            case FPS_SELECT:
            case FULLSCREEN_TOGGLE:
                // These are handled by left/right navigation
                break;
            case APPLY_BUTTON:
                applySettings();
                break;
            case BACK_BUTTON:
                goBack();
                break;
        }
    }
    
    private void applySettings() {
        try {
            // Apply resolution
            GameConfig.Resolution selectedResolution = GameConfig.AVAILABLE_RESOLUTIONS[selectedResolutionIndex];
            gameConfig.setResolution(selectedResolution);
            
            // Apply FPS setting
            GameConfig.FPSOption selectedFPS = GameConfig.AVAILABLE_FPS_OPTIONS[selectedFPSIndex];
            gameConfig.setFPSOption(selectedFPS);
            
            // Apply fullscreen setting
            gameConfig.setFullscreen(fullscreenEnabled);
            
            Gdx.app.log("OptionsScreen", "Configuration applied: " + 
                selectedResolution.displayName + ", FPS: " + selectedFPS.displayName + 
                ", Fullscreen: " + fullscreenEnabled);
                
        } catch (Exception e) {
            Gdx.app.error("OptionsScreen", "Error applying configuration: " + e.getMessage());
        }
    }
    
    private void goBack() {
        switch (context) {
            case MAIN_MENU:
                game.showMainMenu();
                break;
            case PAUSE_MENU:
                game.resumeGame();
                break;
        }
    }
    
    @Override
    public void show() {
        // Screen is now active
    }
    
    @Override
    public void render(float delta) {
        handleInput();
        
        // Clear screen
        Gdx.gl.glClearColor(BACKGROUND_COLOR.r, BACKGROUND_COLOR.g, BACKGROUND_COLOR.b, BACKGROUND_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        // Draw background elements
        drawBackground();
        
        // Draw UI elements
        batch.begin();
        drawTitle();
        drawLanguageSection();
        drawDisplaySection();
        drawActionButtons();
        drawInstructions();
        batch.end();
    }
    
    private void drawBackground() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Draw selection highlight
        MenuOption currentOption = menuOptions[selectedOptionIndex];
        float highlightY = getOptionY(currentOption);
        float highlightHeight = getOptionHeight(currentOption);
        
        shapeRenderer.setColor(0.2f, 0.2f, 0.4f, 0.5f);
        shapeRenderer.rect(50, highlightY - highlightHeight/2, 700, highlightHeight);
        
        // Draw section separators
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 0.8f);
        shapeRenderer.rect(100, 420, 600, 2); // After language section
        shapeRenderer.rect(100, 220, 600, 2); // After display section
        
        shapeRenderer.end();
    }
    
    private void drawTitle() {
        String title = localization.getText("options.title");
        glyphLayout.setText(titleFont, title);
        float x = (800 - glyphLayout.width) / 2;
        titleFont.draw(batch, title, x, 550);
    }
    
    private void drawLanguageSection() {
        // Section title
        font.setColor(SECTION_COLOR);
        String languageTitle = localization.getText("options.language") + ":";
        font.draw(batch, languageTitle, 100, 480);
        
        // Language buttons
        drawOption(MenuOption.SPANISH_BUTTON, localization.getText("options.spanish"), 150, 450);
        drawOption(MenuOption.ENGLISH_BUTTON, localization.getText("options.english"), 350, 450);
    }
    
    private void drawDisplaySection() {
        // Section title
        font.setColor(SECTION_COLOR);
        String displayTitle = localization.getText("options.display") + ":";
        font.draw(batch, displayTitle, 100, 380);
        
        // Resolution
        String resolutionText = localization.getText("options.resolution") + ": " + 
            GameConfig.AVAILABLE_RESOLUTIONS[selectedResolutionIndex].displayName;
        drawOption(MenuOption.RESOLUTION_SELECT, resolutionText, 150, 350);
        
        // FPS
        String fpsText = localization.getText("options.fps") + ": " + 
            GameConfig.AVAILABLE_FPS_OPTIONS[selectedFPSIndex].displayName;
        drawOption(MenuOption.FPS_SELECT, fpsText, 150, 320);
        
        // Fullscreen
        String fullscreenText = localization.getText("options.fullscreen") + ": " + 
            (fullscreenEnabled ? "ON" : "OFF");
        drawOption(MenuOption.FULLSCREEN_TOGGLE, fullscreenText, 150, 290);
    }
    
    private void drawActionButtons() {
        drawOption(MenuOption.APPLY_BUTTON, localization.getText("options.apply"), 250, 180);
        drawOption(MenuOption.BACK_BUTTON, localization.getText("options.back"), 450, 180);
    }
    
    private void drawInstructions() {
        font.setColor(new Color(0.6f, 0.6f, 0.6f, 1));
        String instructions = "W/S: " + localization.getText("pause.instructions").split("  ")[0].split(": ")[1] + 
                            "  A/D: Cambiar  ENTER: Seleccionar  ESC: Volver";
        glyphLayout.setText(font, instructions);
        float x = (800 - glyphLayout.width) / 2;
        font.draw(batch, instructions, x, 50);
    }
    
    private void drawOption(MenuOption option, String text, float x, float y) {
        boolean isSelected = menuOptions[selectedOptionIndex] == option;
        font.setColor(isSelected ? SELECTED_COLOR : NORMAL_COLOR);
        
        if (isSelected) {
            // Draw selection indicator
            font.draw(batch, "> ", x - 30, y);
        }
        
        font.draw(batch, text, x, y);
    }
    
    private float getOptionY(MenuOption option) {
        switch (option) {
            case SPANISH_BUTTON: return 450;
            case ENGLISH_BUTTON: return 450;
            case RESOLUTION_SELECT: return 350;
            case FPS_SELECT: return 320;
            case FULLSCREEN_TOGGLE: return 290;
            case APPLY_BUTTON: return 180;
            case BACK_BUTTON: return 180;
            default: return 0;
        }
    }
    
    private float getOptionHeight(MenuOption option) {
        return 30; // Standard height for all options
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
        titleFont.dispose();
        shapeRenderer.dispose();
    }
}