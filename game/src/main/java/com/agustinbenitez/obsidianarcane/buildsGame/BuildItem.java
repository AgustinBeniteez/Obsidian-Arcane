package com.agustinbenitez.obsidianarcane.buildsGame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;

/**
 * Clase base para todas las construcciones del juego
 * Proporciona funcionalidad común para edificios como ayuntamiento, casas, etc.
 */
public abstract class BuildItem {
    
    // Propiedades básicas
    protected Vector2 position;
    protected Vector2 size;
    protected Rectangle bounds;
    protected String name;
    protected String description;
    
    // Propiedades de construcción
    protected boolean isBuilt;
    protected boolean isActive;
    protected int buildCost;
    protected float buildTime;
    protected float currentBuildTime;
    
    // Propiedades visuales
    protected Color baseColor;
    protected Color buildingColor;
    protected Color completedColor;
    
    // Propiedades de sprite
    protected Texture sprite;
    protected String spritePath;
    protected boolean useSprite;
    
    /**
     * Constructor base para BuildItem
     * @param x Posición X en el mundo
     * @param y Posición Y en el mundo
     * @param width Ancho de la construcción
     * @param height Alto de la construcción
     * @param name Nombre de la construcción
     */
    public BuildItem(float x, float y, float width, float height, String name) {
        this.position = new Vector2(x, y);
        this.size = new Vector2(width, height);
        this.bounds = new Rectangle(x - width/2, y - height/2, width, height);
        this.name = name;
        this.description = "";
        
        // Estados iniciales
        this.isBuilt = false;
        this.isActive = false;
        this.buildCost = 100;
        this.buildTime = 5.0f;
        this.currentBuildTime = 0f;
        
        // Colores por defecto
        this.baseColor = new Color(0.7f, 0.7f, 0.7f, 0.5f); // Gris transparente para planificación
        this.buildingColor = new Color(1.0f, 0.8f, 0.2f, 0.8f); // Amarillo para construcción
        this.completedColor = new Color(0.8f, 0.6f, 0.4f, 1.0f); // Marrón para completado
        
        // Propiedades de sprite
        this.sprite = null;
        this.spritePath = null;
        this.useSprite = false;
    }
    
    /**
     * Actualiza la lógica de la construcción
     * @param deltaTime Tiempo transcurrido desde la última actualización
     */
    public void update(float deltaTime) {
        if (!isBuilt && currentBuildTime < buildTime) {
            // Proceso de construcción
            currentBuildTime += deltaTime;
            if (currentBuildTime >= buildTime) {
                completeBuild();
            }
        }
        
        // Actualizar lógica específica del edificio
        if (isBuilt && isActive) {
            updateSpecific(deltaTime);
        }
    }
    
    /**
     * Renderiza la construcción usando ShapeRenderer
     * @param shapeRenderer Renderer para formas geométricas
     */
    public void render(ShapeRenderer shapeRenderer) {
        Color currentColor = getCurrentColor();
        
        // Asegurar que el ShapeRenderer esté en modo Filled
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(currentColor);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        
        // Renderizar borde
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();
        
        // Renderizar detalles específicos si es necesario
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderSpecific(shapeRenderer);
        shapeRenderer.end();
    }
    
    /**
     * Renderiza usando SpriteBatch (para texto o sprites)
     * @param batch SpriteBatch para renderizado
     */
    public void render(SpriteBatch batch) {
        // Si usa sprite y está construido, renderizar el sprite
        if (useSprite && sprite != null && isBuilt) {
            batch.draw(sprite, bounds.x, bounds.y, bounds.width, bounds.height);
        }
        
        // Renderizar detalles específicos de la subclase
        renderSpecificBatch(batch);
    }
    
    /**
     * Inicia el proceso de construcción
     */
    public void startBuild() {
        if (!isBuilt) {
            currentBuildTime = 0f;
            // Aquí se podría deducir recursos, etc.
        }
    }
    
    /**
     * Completa la construcción instantáneamente
     */
    public void completeBuild() {
        isBuilt = true;
        isActive = true;
        currentBuildTime = buildTime;
        onBuildComplete();
    }
    
    /**
     * Verifica si un punto está dentro de los límites de la construcción
     * @param x Coordenada X
     * @param y Coordenada Y
     * @return true si el punto está dentro
     */
    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }
    
    /**
     * Obtiene el color actual basado en el estado
     */
    protected Color getCurrentColor() {
        if (!isBuilt) {
            if (currentBuildTime > 0) {
                return buildingColor;
            } else {
                return baseColor;
            }
        } else {
            return completedColor;
        }
    }
    
    /**
     * Obtiene el progreso de construcción (0.0 a 1.0)
     */
    public float getBuildProgress() {
        if (buildTime <= 0) return 1.0f;
        return Math.min(currentBuildTime / buildTime, 1.0f);
    }
    
    // Métodos abstractos que deben implementar las subclases
    
    /**
     * Actualización específica del tipo de construcción
     * @param deltaTime Tiempo transcurrido
     */
    protected abstract void updateSpecific(float deltaTime);
    
    /**
     * Renderizado específico usando ShapeRenderer
     * @param shapeRenderer Renderer para formas
     */
    protected abstract void renderSpecific(ShapeRenderer shapeRenderer);
    
    /**
     * Renderizado específico usando SpriteBatch
     * @param batch SpriteBatch para renderizado
     */
    protected abstract void renderSpecificBatch(SpriteBatch batch);
    
    /**
     * Callback cuando se completa la construcción
     */
    protected abstract void onBuildComplete();
    
    /**
     * Obtiene información de la construcción para UI
     */
    public abstract String getInfo();
    
    // Getters y Setters
    public Vector2 getPosition() { return position.cpy(); }
    public Vector2 getSize() { return size.cpy(); }
    public Rectangle getBounds() { return new Rectangle(bounds); }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isBuilt() { return isBuilt; }
    public boolean isActive() { return isActive; }
    public int getBuildCost() { return buildCost; }
    public float getBuildTime() { return buildTime; }
    
    // Métodos adicionales para compatibilidad con GameMap
    public float getX() { return position.x; }
    public float getY() { return position.y; }
    public float getWidth() { return size.x; }
    public float getHeight() { return size.y; }
    
    public void setPosition(float x, float y) {
        this.position.set(x, y);
        this.bounds.setPosition(x - size.x/2, y - size.y/2);
    }
    
    public void setActive(boolean active) { this.isActive = active; }
    public void setDescription(String description) { this.description = description; }
    
    // Métodos para gestión de sprites
    public void setSprite(String spritePath) {
        this.spritePath = spritePath;
        this.useSprite = true;
        try {
            this.sprite = new Texture(Gdx.files.internal(spritePath));
        } catch (Exception e) {
            System.err.println("Error cargando sprite: " + spritePath);
            this.useSprite = false;
        }
    }
    
    public void dispose() {
        if (sprite != null) {
            sprite.dispose();
        }
    }
    
    public boolean isUsingSprite() { return useSprite; }
    public String getSpritePath() { return spritePath; }
}