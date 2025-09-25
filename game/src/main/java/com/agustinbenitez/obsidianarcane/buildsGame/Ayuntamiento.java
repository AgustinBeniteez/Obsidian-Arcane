package com.agustinbenitez.obsidianarcane.buildsGame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Gdx;

/**
 * Clase Ayuntamiento - Edificio principal del pueblo
 * Representa el centro administrativo y punto focal de la aldea
 */
public class Ayuntamiento extends BuildItem {
    
    // Propiedades específicas del ayuntamiento
    private int population;
    private int maxPopulation;
    private float taxRate;
    private float goldGeneration;
    private float goldTimer;
    private int currentGold;
    
    // Propiedades visuales específicas
    private static final float DEFAULT_WIDTH = 96f;
    private static final float DEFAULT_HEIGHT = 96f;
    
    /**
     * Constructor del Ayuntamiento
     * @param x Posición X en el mundo
     * @param y Posición Y en el mundo
     */
    public Ayuntamiento(float x, float y) {
        super(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, "Ayuntamiento");
        
        // Configuración específica del ayuntamiento
        this.description = "Centro administrativo del pueblo - Edificio principal de la ciudad.";
        this.buildCost = 0; // Sin costo ya que es el edificio base
        this.buildTime = 0f; // Se construye instantáneamente
        
        // Propiedades del ayuntamiento
        this.population = 0;
        this.maxPopulation = 50;
        this.taxRate = 0.1f;
        this.goldGeneration = 2.0f; // Oro por segundo
        this.goldTimer = 0f;
        this.currentGold = 0;
        
        // Colores específicos del ayuntamiento
        this.baseColor = new Color(0.8f, 0.8f, 0.9f, 0.6f); // Gris azulado para planificación
        this.buildingColor = new Color(1.0f, 0.9f, 0.3f, 0.8f); // Amarillo dorado para construcción
        this.completedColor = new Color(0.7f, 0.5f, 0.3f, 1.0f); // Marrón para completado
        
        // Configurar sprite del ayuntamiento
        setSprite("buildsGame/src/cityHallSprite.png");
    }
    
    @Override
    protected void updateSpecific(float deltaTime) {
        // El ayuntamiento es solo un edificio base, sin funcionalidades automáticas
        // En el futuro se pueden agregar funcionalidades de gestión de ciudad
    }
    
    @Override
    protected void renderSpecific(ShapeRenderer shapeRenderer) {
        if (isBuilt) {
            // Renderizar detalles del ayuntamiento completado
            
            // Techo triangular
            float centerX = bounds.x + bounds.width / 2;
            float topY = bounds.y + bounds.height;
            float roofHeight = 20f;
            
            shapeRenderer.setColor(0.6f, 0.3f, 0.2f, 1.0f); // Marrón oscuro para el techo
            shapeRenderer.triangle(
                bounds.x, topY,
                centerX, topY + roofHeight,
                bounds.x + bounds.width, topY
            );
            
            // Puerta principal
            float doorWidth = 16f;
            float doorHeight = 24f;
            float doorX = centerX - doorWidth / 2;
            float doorY = bounds.y;
            
            shapeRenderer.setColor(0.3f, 0.2f, 0.1f, 1.0f); // Marrón muy oscuro para la puerta
            shapeRenderer.rect(doorX, doorY, doorWidth, doorHeight);
            
            // Ventanas
            float windowSize = 8f;
            shapeRenderer.setColor(0.8f, 0.9f, 1.0f, 1.0f); // Azul claro para ventanas
            
            // Ventana izquierda
            shapeRenderer.rect(bounds.x + 15f, bounds.y + bounds.height - 25f, windowSize, windowSize);
            
            // Ventana derecha
            shapeRenderer.rect(bounds.x + bounds.width - 23f, bounds.y + bounds.height - 25f, windowSize, windowSize);
            
            // Bandera en el techo (si está activo)
            if (isActive) {
                shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 1.0f); // Rojo para la bandera
                shapeRenderer.rect(centerX + 2f, topY + roofHeight - 5f, 12f, 8f);
                
                // Asta de la bandera
                shapeRenderer.setColor(0.4f, 0.3f, 0.2f, 1.0f); // Marrón para el asta
                shapeRenderer.rect(centerX, topY + roofHeight - 15f, 2f, 20f);
            }
        } else if (currentBuildTime > 0) {
            // Mostrar progreso de construcción
            float progress = getBuildProgress();
            float progressHeight = bounds.height * progress;
            
            shapeRenderer.setColor(0.9f, 0.9f, 0.9f, 0.8f); // Gris claro para estructura
            shapeRenderer.rect(bounds.x, bounds.y, bounds.width, progressHeight);
            
            // Andamios (líneas de construcción)
            shapeRenderer.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(0.6f, 0.4f, 0.2f, 1.0f); // Marrón para andamios
            
            for (int i = 1; i < 4; i++) {
                float lineY = bounds.y + (bounds.height / 4) * i;
                if (lineY <= bounds.y + progressHeight) {
                    shapeRenderer.line(bounds.x, lineY, bounds.x + bounds.width, lineY);
                }
            }
            
            shapeRenderer.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        }
    }
    
    @Override
    protected void renderSpecificBatch(SpriteBatch batch) {
        // Aquí se podría renderizar texto con información del ayuntamiento
        // Por ahora dejamos vacío, se puede implementar más adelante
    }
    
    @Override
    protected void onBuildComplete() {
        Gdx.app.log("Ayuntamiento", "¡Ayuntamiento construido! El pueblo ahora tiene un centro administrativo.");
        
        // Inicializar población base
        this.population = 10; // Población inicial
        
        // El ayuntamiento está listo para funcionar
        setActive(true);
    }
    
    @Override
    public String getInfo() {
        if (isBuilt) {
            return String.format("%s\nEdificio principal de la ciudad\nEstado: Construido", name);
        } else {
            return String.format("%s\nEdificio principal de la ciudad\nEstado: En construcción", name);
        }
    }
    
    // Métodos específicos del ayuntamiento
    
    /**
     * Añade población al pueblo
     * @param amount Cantidad de población a añadir
     * @return true si se pudo añadir, false si se alcanzó el límite
     */
    public boolean addPopulation(int amount) {
        if (population + amount <= maxPopulation) {
            population += amount;
            Gdx.app.log("Ayuntamiento", "Población aumentada a: " + population);
            return true;
        }
        return false;
    }
    
    /**
     * Retira oro del ayuntamiento
     * @param amount Cantidad de oro a retirar
     * @return true si se pudo retirar, false si no hay suficiente oro
     */
    public boolean withdrawGold(int amount) {
        if (currentGold >= amount) {
            currentGold -= amount;
            Gdx.app.log("Ayuntamiento", "Oro retirado: " + amount + ". Restante: " + currentGold);
            return true;
        }
        return false;
    }
    
    /**
     * Mejora la capacidad del ayuntamiento
     */
    public void upgrade() {
        if (isBuilt) {
            maxPopulation += 25;
            goldGeneration += 1.0f;
            Gdx.app.log("Ayuntamiento", "Ayuntamiento mejorado. Nueva capacidad: " + maxPopulation);
        }
    }
    
    // Getters específicos
    public int getPopulation() { return population; }
    public int getMaxPopulation() { return maxPopulation; }
    public float getTaxRate() { return taxRate; }
    public int getCurrentGold() { return currentGold; }
    public float getGoldGeneration() { return goldGeneration; }
    
    // Setters específicos
    public void setTaxRate(float taxRate) { 
        this.taxRate = Math.max(0.0f, Math.min(1.0f, taxRate)); 
    }
}