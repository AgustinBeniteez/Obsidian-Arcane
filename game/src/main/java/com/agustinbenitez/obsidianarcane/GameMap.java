package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.agustinbenitez.obsidianarcane.buildsGame.BuildItem;
import com.agustinbenitez.obsidianarcane.buildsGame.Ayuntamiento;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que maneja un mapa básico vacío para tower defense y gestión de aldea.
 * Simplificado para permitir construcción libre sin generación procedural.
 */
public class GameMap {
    
    // Constantes del mapa
    private static final float TILE_SIZE = 32.0f;
    private static final int MAP_WIDTH = 50;  // Ancho del mapa en tiles
    private static final int MAP_HEIGHT = 30; // Alto del mapa en tiles (reducido de 40 a 30)
    
    // Propiedades del mapa
    private int totalWidth;
    private int totalHeight;
    private int[][] mapData; // 0 = vacío/construible, 1 = ocupado
    
    // Lista de construcciones en el mapa
    private List<BuildItem> buildings;
    
    public GameMap() {
        this.totalWidth = MAP_WIDTH;
        this.totalHeight = MAP_HEIGHT;
        
        // Inicializar lista de construcciones
        this.buildings = new ArrayList<>();
        
        // Inicializar mapa vacío
        initializeEmptyMap();
        
        // Crear ayuntamiento en el centro del mapa
        createTownHall();
        
        Gdx.app.log("GameMap", "Mapa vacío creado: " + MAP_WIDTH + "x" + MAP_HEIGHT + " tiles");
    }
    
    /**
     * Inicializa un mapa completamente vacío
     */
    private void initializeEmptyMap() {
        mapData = new int[MAP_WIDTH][MAP_HEIGHT];
        
        // Llenar todo con espacios vacíos (0)
        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                mapData[x][y] = 0; // Todo vacío y construible
            }
        }
    }
    
    /**
     * Crea el ayuntamiento en el centro del mapa
     */
    private void createTownHall() {
        // Calcular posición central del mapa
        float centerX = (MAP_WIDTH / 2) * TILE_SIZE;
        float centerY = (MAP_HEIGHT / 2) * TILE_SIZE;
        
        // Crear ayuntamiento
        Ayuntamiento townHall = new Ayuntamiento(centerX, centerY);
        
        // Añadir a la lista de construcciones
        buildings.add(townHall);
        
        // Marcar tiles como ocupados (el ayuntamiento ocupa 3x3 tiles)
        int centerTileX = MAP_WIDTH / 2;
        int centerTileY = MAP_HEIGHT / 2;
        
        for (int x = centerTileX - 1; x <= centerTileX + 1; x++) {
            for (int y = centerTileY - 1; y <= centerTileY + 1; y++) {
                if (x >= 0 && x < MAP_WIDTH && y >= 0 && y < MAP_HEIGHT) {
                    mapData[x][y] = 1; // Marcar como ocupado
                }
            }
        }
        
        // Iniciar construcción automáticamente (ya construido)
        townHall.completeBuild();
        
        Gdx.app.log("GameMap", "Ayuntamiento creado en el centro del mapa (" + centerX + ", " + centerY + ")");
    }
    
    /**
     * Renderiza el mapa (solo un fondo básico)
     */
    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Dibujar fondo verde claro para representar terreno construible
        shapeRenderer.setColor(0.6f, 0.8f, 0.4f, 1.0f); // Verde claro
        shapeRenderer.rect(0, 0, totalWidth * TILE_SIZE, totalHeight * TILE_SIZE);
        
        // Dibujar grilla opcional para visualizar tiles
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.5f, 0.7f, 0.3f, 0.3f); // Verde más oscuro, transparente
        
        // Líneas verticales
        for (int x = 0; x <= MAP_WIDTH; x++) {
            float worldX = x * TILE_SIZE;
            shapeRenderer.line(worldX, 0, worldX, totalHeight * TILE_SIZE);
        }
        
        // Líneas horizontales
        for (int y = 0; y <= MAP_HEIGHT; y++) {
            float worldY = y * TILE_SIZE;
            shapeRenderer.line(0, worldY, totalWidth * TILE_SIZE, worldY);
        }
        
        shapeRenderer.end();
        
        // Renderizar construcciones
        renderBuildings(shapeRenderer);
    }
    
    /**
     * Renderiza todas las construcciones del mapa
     */
    private void renderBuildings(ShapeRenderer shapeRenderer) {
        // No necesitamos begin/end aquí ya que BuildItem maneja su propio estado
        for (BuildItem building : buildings) {
            building.render(shapeRenderer);
        }
    }
    
    /**
     * Renderiza todas las construcciones del mapa con SpriteBatch (para sprites)
     */
    public void renderBuildings(SpriteBatch batch) {
        for (BuildItem building : buildings) {
            building.render(batch);
        }
    }
    
    /**
     * Actualiza todas las construcciones del mapa
     */
    public void update(float deltaTime) {
        for (BuildItem building : buildings) {
            building.update(deltaTime);
        }
    }
    
    /**
     * Renderiza una región específica del mapa (optimización para cámaras)
     */
    public void renderRegion(ShapeRenderer shapeRenderer, float cameraX, float cameraY, 
                           float viewWidth, float viewHeight) {
        // Para un mapa simple, renderizar todo
        render(shapeRenderer);
    }
    
    /**
     * Verifica si hay colisión en las coordenadas del mundo especificadas
     * En un mapa vacío, no hay colisiones por defecto
     */
    public boolean isCollision(float worldX, float worldY) {
        int tileX = (int)(worldX / TILE_SIZE);
        int tileY = (int)(worldY / TILE_SIZE);
        
        // Verificar límites del mapa
        if (tileX < 0 || tileX >= MAP_WIDTH || tileY < 0 || tileY >= MAP_HEIGHT) {
            return true; // Fuera del mapa es sólido
        }
        
        // En un mapa vacío, verificar si hay algo construido
        return mapData[tileX][tileY] == 1;
    }
    
    /**
     * Encuentra una posición válida para generar el jugador
     */
    public Vector2 findValidSpawnPosition() {
        // Posición central del mapa
        float centerX = (MAP_WIDTH / 2) * TILE_SIZE + TILE_SIZE / 2;
        float centerY = (MAP_HEIGHT / 2) * TILE_SIZE + TILE_SIZE / 2;
        
        return new Vector2(centerX, centerY);
    }
    
    /**
     * Marca una posición como ocupada (para futuras construcciones)
     */
    public void setTileOccupied(int tileX, int tileY, boolean occupied) {
        if (tileX >= 0 && tileX < MAP_WIDTH && tileY >= 0 && tileY < MAP_HEIGHT) {
            mapData[tileX][tileY] = occupied ? 1 : 0;
        }
    }
    
    /**
     * Verifica si una posición está ocupada
     */
    public boolean isTileOccupied(int tileX, int tileY) {
        if (tileX < 0 || tileX >= MAP_WIDTH || tileY < 0 || tileY >= MAP_HEIGHT) {
            return true; // Fuera del mapa se considera ocupado
        }
        return mapData[tileX][tileY] == 1;
    }
    
    // Métodos de utilidad y getters
    public int getTotalWidth() { return totalWidth; }
    public int getTotalHeight() { return totalHeight; }
    public float getTileSize() { return TILE_SIZE; }
    public int getMapWidth() { return MAP_WIDTH; }
    public int getMapHeight() { return MAP_HEIGHT; }
    
    /**
     * Convierte coordenadas del mundo a coordenadas de tile
     */
    public int worldToTileX(float worldX) {
        return (int)(worldX / TILE_SIZE);
    }
    
    public int worldToTileY(float worldY) {
        return (int)(worldY / TILE_SIZE);
    }
    
    /**
     * Convierte coordenadas de tile a coordenadas del mundo
     */
    public float tileToWorldX(int tileX) {
        return tileX * TILE_SIZE;
    }
    
    public float tileToWorldY(int tileY) {
        return tileY * TILE_SIZE;
    }
    
    /**
     * Imprime información del mapa en la consola (para debug)
     */
    public void printMapToConsole() {
        System.out.println("=== MAPA VACÍO PARA TOWER DEFENSE ===");
        System.out.println("Tamaño: " + MAP_WIDTH + "x" + MAP_HEIGHT + " tiles");
        System.out.println("Tamaño del tile: " + TILE_SIZE + " píxeles");
        System.out.println("Tamaño total: " + (MAP_WIDTH * TILE_SIZE) + "x" + (MAP_HEIGHT * TILE_SIZE) + " píxeles");
        System.out.println("Tipo: Mapa vacío construible");
    }
    
    /**
     * Libera recursos
     */
    public void dispose() {
        // Liberar recursos de las construcciones
        for (BuildItem building : buildings) {
            building.dispose();
        }
        buildings.clear();
        
        System.out.println("GameMap disposed");
    }
    
    // Métodos para gestionar construcciones
    
    /**
     * Añade una construcción al mapa
     */
    public boolean addBuilding(BuildItem building) {
        if (building != null) {
            buildings.add(building);
            
            // Marcar tiles como ocupados
            int tileX = worldToTileX(building.getX());
            int tileY = worldToTileY(building.getY());
            int tilesWidth = (int) Math.ceil(building.getWidth() / TILE_SIZE);
            int tilesHeight = (int) Math.ceil(building.getHeight() / TILE_SIZE);
            
            for (int x = tileX; x < tileX + tilesWidth; x++) {
                for (int y = tileY; y < tileY + tilesHeight; y++) {
                    setTileOccupied(x, y, true);
                }
            }
            
            return true;
        }
        return false;
    }
    
    /**
     * Remueve una construcción del mapa
     */
    public boolean removeBuilding(BuildItem building) {
        if (buildings.remove(building)) {
            // Liberar tiles ocupados
            int tileX = worldToTileX(building.getX());
            int tileY = worldToTileY(building.getY());
            int tilesWidth = (int) Math.ceil(building.getWidth() / TILE_SIZE);
            int tilesHeight = (int) Math.ceil(building.getHeight() / TILE_SIZE);
            
            for (int x = tileX; x < tileX + tilesWidth; x++) {
                for (int y = tileY; y < tileY + tilesHeight; y++) {
                    setTileOccupied(x, y, false);
                }
            }
            
            building.dispose();
            return true;
        }
        return false;
    }
    
    /**
     * Obtiene todas las construcciones del mapa
     */
    public List<BuildItem> getBuildings() {
        return new ArrayList<>(buildings);
    }
    
    /**
     * Obtiene el ayuntamiento (si existe)
     */
    public Ayuntamiento getTownHall() {
        for (BuildItem building : buildings) {
            if (building instanceof Ayuntamiento) {
                return (Ayuntamiento) building;
            }
        }
        return null;
    }
}