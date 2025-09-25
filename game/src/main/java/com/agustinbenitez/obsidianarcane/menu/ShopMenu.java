package com.agustinbenitez.obsidianarcane.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.agustinbenitez.obsidianarcane.LocalizationManager;
import com.agustinbenitez.obsidianarcane.GameMap;
import com.agustinbenitez.obsidianarcane.buildsGame.Casa;
import com.agustinbenitez.obsidianarcane.buildsGame.BuildItem;

/**
 * Menú de tienda para comprar edificios
 */
public class ShopMenu {
    
    private Stage stage;
    private Table mainTable;
    private Table buildingsTable;
    private boolean isVisible = false;
    private BitmapFont font;
    private GameMap gameMap;
    private OrthographicCamera camera;
    
    // Estilos de UI
    private TextButton.TextButtonStyle buttonStyle;
    private Label.LabelStyle labelStyle;
    
    public ShopMenu(BitmapFont font, GameMap gameMap, OrthographicCamera camera) {
        this.font = font;
        this.gameMap = gameMap;
        this.camera = camera;
        
        initializeUI();
    }
    
    private void initializeUI() {
        stage = new Stage(new ScreenViewport());
        
        // Crear estilos
        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.YELLOW;
        buttonStyle.downFontColor = Color.GRAY;
        
        labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        
        // Crear tabla principal
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();
        
        // Título del menú
        Label titleLabel = new Label("TIENDA DE EDIFICIOS", labelStyle);
        mainTable.add(titleLabel).colspan(2).padBottom(20).row();
        
        // Crear tabla de edificios
        buildingsTable = new Table();
        createBuildingButtons();
        
        // Scroll pane para la lista de edificios
        ScrollPane scrollPane = new ScrollPane(buildingsTable);
        scrollPane.setFadeScrollBars(false);
        mainTable.add(scrollPane).width(400).height(300).padBottom(20).row();
        
        // Botón de cerrar
        TextButton closeButton = new TextButton("Cerrar", buttonStyle);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        mainTable.add(closeButton).width(100).height(40);
        
        stage.addActor(mainTable);
        mainTable.setVisible(false);
    }
    
    private void createBuildingButtons() {
        buildingsTable.clear();
        
        // Casa
        Label casaLabel = new Label("Casa Residencial", labelStyle);
        Label casaInfo = new Label("Costo: 150 oro | Tiempo: 3s", labelStyle);
        casaInfo.setColor(Color.LIGHT_GRAY);
        TextButton casaButton = new TextButton("Comprar", buttonStyle);
        
        casaButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                purchaseBuilding("Casa");
            }
        });
        
        buildingsTable.add(casaLabel).left().padRight(20);
        buildingsTable.add(casaInfo).left().padRight(20);
        buildingsTable.add(casaButton).width(80).height(30).row();
        buildingsTable.row().padBottom(10);
        
        // Aquí se pueden agregar más edificios en el futuro
        // Ejemplo: Tienda, Granja, etc.
        
        Label futureLabel = new Label("Más edificios próximamente...", labelStyle);
        futureLabel.setColor(Color.GRAY);
        buildingsTable.add(futureLabel).colspan(3).center().padTop(20);
    }
    
    private void purchaseBuilding(String buildingType) {
        switch (buildingType) {
            case "Casa":
                // Verificar si el jugador tiene suficiente oro (por ahora simulado)
                // En el futuro se puede integrar con un sistema de recursos
                
                // Crear nueva casa en una posición cerca del centro de la cámara
                float cameraX = camera.position.x;
                float cameraY = camera.position.y;
                
                // Buscar una posición libre cerca del centro de la vista
                float offsetX = 100f; // Offset inicial
                float offsetY = 0f;
                
                Casa newHouse = new Casa(cameraX + offsetX, cameraY + offsetY);
                
                // Agregar al mapa
                if (gameMap.addBuilding(newHouse)) {
                    // Iniciar construcción automáticamente
                    newHouse.startBuild();
                    System.out.println("Casa comprada y construcción iniciada!");
                    hide(); // Cerrar menú después de comprar
                } else {
                    System.out.println("No se pudo colocar la casa en esa posición");
                }
                break;
                
            default:
                System.out.println("Tipo de edificio no reconocido: " + buildingType);
                break;
        }
    }
    
    public void show() {
        isVisible = true;
        mainTable.setVisible(true);
    }
    
    public void hide() {
        isVisible = false;
        mainTable.setVisible(false);
    }
    
    public boolean isVisible() {
        return isVisible;
    }
    
    public void toggle() {
        if (isVisible) {
            hide();
        } else {
            show();
        }
    }
    
    public Stage getStage() {
        return stage;
    }
    
    public void act(float delta) {
        if (isVisible) {
            stage.act(delta);
        }
    }
    
    public void draw() {
        if (isVisible) {
            stage.draw();
        }
    }
    
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
    }
}