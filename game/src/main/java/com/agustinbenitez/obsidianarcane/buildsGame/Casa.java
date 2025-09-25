package com.agustinbenitez.obsidianarcane.buildsGame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Gdx;

/**
 * Clase Casa - Edificio residencial básico
 * Proporciona vivienda para los habitantes del pueblo
 */
public class Casa extends BuildItem {
    
    // Propiedades específicas de la casa
    private int residents;
    private int maxResidents;
    private float comfortLevel;
    private boolean hasElectricity;
    private boolean hasWater;
    
    // Propiedades visuales específicas
    private static final float DEFAULT_WIDTH = 64f;
    private static final float DEFAULT_HEIGHT = 64f;
    
    /**
     * Constructor de la Casa
     * @param x Posición X en el mundo
     * @param y Posición Y en el mundo
     */
    public Casa(float x, float y) {
        super(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, "Casa");
        
        // Configuración específica de la casa
        this.description = "Vivienda básica para los habitantes del pueblo.";
        this.buildCost = 150;
        this.buildTime = 3.0f;
        
        // Propiedades de la casa
        this.residents = 0;
        this.maxResidents = 4;
        this.comfortLevel = 0.5f;
        this.hasElectricity = false;
        this.hasWater = false;
        
        // Colores específicos de la casa
        this.baseColor = new Color(0.9f, 0.9f, 0.8f, 0.6f); // Beige claro para planificación
        this.buildingColor = new Color(1.0f, 0.7f, 0.4f, 0.8f); // Naranja para construcción
        this.completedColor = new Color(0.8f, 0.4f, 0.2f, 1.0f); // Marrón rojizo para completado
        
        // Configurar sprite de la casa
        setSprite("buildsGame/src/homeSprite.png");
    }
    
    @Override
    protected void updateSpecific(float deltaTime) {
        // Actualizar nivel de comodidad basado en servicios
        if (isBuilt) {
            float targetComfort = 0.5f;
            if (hasElectricity) targetComfort += 0.2f;
            if (hasWater) targetComfort += 0.2f;
            
            // Ajustar gradualmente el nivel de comodidad
            if (comfortLevel < targetComfort) {
                comfortLevel = Math.min(targetComfort, comfortLevel + deltaTime * 0.1f);
            } else if (comfortLevel > targetComfort) {
                comfortLevel = Math.max(targetComfort, comfortLevel - deltaTime * 0.1f);
            }
        }
    }
    
    @Override
    protected void renderSpecific(ShapeRenderer shapeRenderer) {
        // Solo renderizar formas si no usa sprite o durante construcción
        if (!useSprite || !isBuilt) {
            // Renderizar base de la casa
            shapeRenderer.setColor(getCurrentColor());
            shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
            
            // Si está en construcción, mostrar progreso
            if (!isBuilt && currentBuildTime > 0) {
                float progress = getBuildProgress();
                
                // Barra de progreso
                shapeRenderer.setColor(Color.GREEN);
                shapeRenderer.rect(bounds.x, bounds.y - 8, bounds.width * progress, 4);
                
                // Marco de la barra de progreso
                shapeRenderer.setColor(Color.WHITE);
                shapeRenderer.rect(bounds.x, bounds.y - 8, bounds.width, 4);
            }
            
            // Si está completada, mostrar detalles adicionales
            if (isBuilt) {
                // Ventanas (pequeños cuadrados blancos)
                shapeRenderer.setColor(Color.WHITE);
                float windowSize = 6f;
                shapeRenderer.rect(bounds.x + 8, bounds.y + bounds.height - 16, windowSize, windowSize);
                shapeRenderer.rect(bounds.x + bounds.width - 14, bounds.y + bounds.height - 16, windowSize, windowSize);
                
                // Puerta (rectángulo marrón)
                shapeRenderer.setColor(Color.BROWN);
                shapeRenderer.rect(bounds.x + bounds.width/2 - 6, bounds.y, 12, 20);
                
                // Indicadores de servicios
                if (hasElectricity) {
                    shapeRenderer.setColor(Color.YELLOW);
                    shapeRenderer.circle(bounds.x + bounds.width - 8, bounds.y + bounds.height - 8, 3);
                }
                if (hasWater) {
                    shapeRenderer.setColor(Color.BLUE);
                    shapeRenderer.circle(bounds.x + 8, bounds.y + bounds.height - 8, 3);
                }
            }
        }
    }
    
    @Override
    protected void renderSpecificBatch(SpriteBatch batch) {
        // Renderizado adicional con SpriteBatch si es necesario
        // Por ejemplo, texto de información o efectos especiales
    }
    
    @Override
    protected void onBuildComplete() {
        System.out.println("Casa completada en posición: " + position.x + ", " + position.y);
        
        // Activar la casa automáticamente al completarse
        setActive(true);
        
        // Configurar servicios básicos (se pueden mejorar más tarde)
        this.hasWater = true; // Agua básica disponible
    }
    
    @Override
    public String getInfo() {
        if (!isBuilt) {
            return name + " (En construcción: " + Math.round(getBuildProgress() * 100) + "%)";
        } else {
            return name + " - Residentes: " + residents + "/" + maxResidents + 
                   " | Comodidad: " + Math.round(comfortLevel * 100) + "%";
        }
    }
    
    // Métodos específicos de la casa
    
    /**
     * Añade residentes a la casa
     * @param amount Cantidad de residentes a añadir
     * @return true si se pudieron añadir todos los residentes
     */
    public boolean addResidents(int amount) {
        if (isBuilt && residents + amount <= maxResidents) {
            residents += amount;
            return true;
        }
        return false;
    }
    
    /**
     * Remueve residentes de la casa
     * @param amount Cantidad de residentes a remover
     * @return Cantidad de residentes realmente removidos
     */
    public int removeResidents(int amount) {
        int removed = Math.min(amount, residents);
        residents -= removed;
        return removed;
    }
    
    /**
     * Mejora la casa con electricidad
     */
    public void installElectricity() {
        if (isBuilt) {
            this.hasElectricity = true;
        }
    }
    
    /**
     * Mejora la casa con agua corriente
     */
    public void installWater() {
        if (isBuilt) {
            this.hasWater = true;
        }
    }
    
    // Getters
    public int getResidents() { return residents; }
    public int getMaxResidents() { return maxResidents; }
    public float getComfortLevel() { return comfortLevel; }
    public boolean hasElectricity() { return hasElectricity; }
    public boolean hasWater() { return hasWater; }
    public boolean isFull() { return residents >= maxResidents; }
    public int getAvailableSpace() { return maxResidents - residents; }
}