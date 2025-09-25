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
import com.badlogic.gdx.Input.Keys;

/**
 * Pantalla para ingresar el nombre de la aldea al crear un nuevo juego
 */
public class VillageNameInputScreen implements Screen {
    
    private GameStateManager game;
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private LocalizationManager localization;
    private int targetSlot; // Slot donde se guardará la nueva partida

    // UI Elements
    private Table mainTable;
    private Label titleLabel;
    private Label instructionLabel;
    private TextField villageNameField;
    private TextButton confirmButton;
    private TextButton cancelButton;
    
    public VillageNameInputScreen(GameStateManager game) {
        this(game, 1); // Slot por defecto
    }
    
    public VillageNameInputScreen(GameStateManager game, int slot) {
        this.game = game;
        this.targetSlot = slot;
        
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
        
        // Create skin with pixel texture
        Skin skin = new Skin();
        skin.add("default-font", font);
        
        // Create a 1x1 white pixel texture for drawables
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        com.badlogic.gdx.graphics.Texture whiteTexture = new com.badlogic.gdx.graphics.Texture(pixmap);
        pixmap.dispose();
        
        skin.add("white", whiteTexture);
        
        // Create label style
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        
        // Create text field style
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.background = skin.newDrawable("white", Color.DARK_GRAY);
        textFieldStyle.focusedBackground = skin.newDrawable("white", Color.GRAY);
        textFieldStyle.cursor = skin.newDrawable("white", Color.WHITE);
        textFieldStyle.selection = skin.newDrawable("white", Color.BLUE);
        
        // Create button style
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.YELLOW;
        buttonStyle.downFontColor = Color.GRAY;
        
        // Create UI elements
        titleLabel = new Label("Crear Nueva Aldea", labelStyle);
        instructionLabel = new Label("Ingresa el nombre de tu aldea:", labelStyle);
        
        villageNameField = new TextField("", textFieldStyle);
        villageNameField.setMessageText("Nombre de la aldea...");
        
        confirmButton = new TextButton("Crear", buttonStyle);
        cancelButton = new TextButton("Cancelar", buttonStyle);
        
        // Configure button listeners
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                createNewGame();
            }
        });
        
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.showLoadScreen();
            }
        });
        
        // Create main table to organize elements
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();
        
        // Add elements to the table
        mainTable.add(titleLabel).padBottom(30).row();
        mainTable.add(instructionLabel).padBottom(20).row();
        mainTable.add(villageNameField).width(400).height(60).padBottom(30).row();
        
        Table buttonTable = new Table();
        buttonTable.add(confirmButton).width(150).height(50).padRight(20);
        buttonTable.add(cancelButton).width(150).height(50);
        mainTable.add(buttonTable);
        
        // Add table to stage
        stage.addActor(mainTable);
    }
    
    private void createNewGame() {
        String villageName = villageNameField.getText().trim();
        
        if (villageName.isEmpty()) {
            villageName = "Mi Aldea"; // Nombre por defecto
        }
        
        // Crear nuevo juego con el nombre de aldea especificado
        game.startGameWithVillageName(villageName, targetSlot);
    }
    
    private void handleInput() {
        // Confirmar con Enter
        if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
            createNewGame();
        }
        
        // Cancelar con Escape
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            game.showLoadScreen();
        }
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        // Enfocar el campo de texto
        stage.setKeyboardFocus(villageNameField);
    }
    
    @Override
    public void render(float delta) {
        // Handle keyboard input
        handleInput();
        
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