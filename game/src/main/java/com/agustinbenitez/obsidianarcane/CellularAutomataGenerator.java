package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.Gdx;
import java.util.Random;

/**
 * Generador de mapas usando Autómatas Celulares
 * Implementa el algoritmo descrito en la documentación de algoritmos-generacion-mapas-roguelike
 * Genera mapas orgánicos para el juego Obsidian Arcane
 */
public class CellularAutomataGenerator {
    
    // Constantes del mapa
    public static final int WALL = 1;
    public static final int FLOOR = 0;
    
    // Parámetros del generador
    private int width;
    private int height;
    private float initialDensity = 0.45f;
    private int wallThreshold = 4;
    private int iterations = 5;
    private Random random;
    
    /**
     * Constructor del generador
     * @param width Ancho del mapa
     * @param height Alto del mapa
     */
    public CellularAutomataGenerator(int width, int height) {
        this.width = width;
        this.height = height;
        this.random = new Random();
    }
    
    /**
     * Constructor con semilla personalizada
     * @param width Ancho del mapa
     * @param height Alto del mapa
     * @param seed Semilla para la generación aleatoria
     */
    public CellularAutomataGenerator(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.random = new Random(seed);
    }
    
    /**
     * Genera un mapa usando el algoritmo de Autómatas Celulares
     * @return Matriz 2D representando el mapa (0 = suelo, 1 = pared)
     */
    public int[][] generateMap() {
        Gdx.app.log("CellularAutomata", "Iniciando generación de mapa " + width + "x" + height);
        
        // Paso 1: Inicialización
        int[][] map = initializeMap();
        
        // Paso 2: Aplicar iteraciones del algoritmo
        for (int i = 0; i < iterations; i++) {
            map = applyAutomataRules(map);
            Gdx.app.log("CellularAutomata", "Iteración " + (i + 1) + " completada");
        }
        
        // Paso 3: Post-procesamiento (opcional)
        map = postProcess(map);
        
        Gdx.app.log("CellularAutomata", "Generación de mapa completada");
        return map;
    }
    
    /**
     * Inicializa el mapa con distribución aleatoria
     * @return Mapa inicial con densidad configurada
     */
    private int[][] initializeMap() {
        int[][] map = new int[width][height];
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Bordes siempre son paredes
                if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    map[x][y] = WALL;
                } else {
                    // Distribución aleatoria basada en densidad inicial
                    if (random.nextFloat() < initialDensity) {
                        map[x][y] = WALL;
                    } else {
                        map[x][y] = FLOOR;
                    }
                }
            }
        }
        
        return map;
    }
    
    /**
     * Aplica las reglas de los Autómatas Celulares
     * @param currentMap Mapa actual
     * @return Nuevo mapa después de aplicar las reglas
     */
    private int[][] applyAutomataRules(int[][] currentMap) {
        int[][] newMap = new int[width][height];
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Los bordes siempre permanecen como paredes
                if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    newMap[x][y] = WALL;
                } else {
                    int wallNeighbors = countWallNeighbors(currentMap, x, y);
                    
                    // Regla principal: si hay 4 o más vecinos pared, la celda se convierte en pared
                    newMap[x][y] = (wallNeighbors >= wallThreshold) ? WALL : FLOOR;
                }
            }
        }
        
        return newMap;
    }
    
    /**
     * Cuenta los vecinos tipo pared de una celda
     * @param map Mapa actual
     * @param x Coordenada X de la celda
     * @param y Coordenada Y de la celda
     * @return Número de vecinos que son paredes
     */
    private int countWallNeighbors(int[][] map, int x, int y) {
        int count = 0;
        
        // Revisar los 8 vecinos (incluyendo diagonales)
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue; // Saltar la celda central
                
                int neighborX = x + i;
                int neighborY = y + j;
                
                // Considerar fuera de límites como paredes
                if (neighborX < 0 || neighborX >= width || 
                    neighborY < 0 || neighborY >= height) {
                    count++;
                } else if (map[neighborX][neighborY] == WALL) {
                    count++;
                }
            }
        }
        
        return count;
    }
    
    /**
     * Post-procesamiento del mapa para mejorar la conectividad
     * @param map Mapa generado
     * @return Mapa post-procesado
     */
    private int[][] postProcess(int[][] map) {
        // Asegurar que hay al menos un área de suelo conectada
        // Por simplicidad, esta implementación básica solo limpia áreas muy pequeñas
        
        int[][] processedMap = new int[width][height];
        
        // Copiar el mapa original
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                processedMap[x][y] = map[x][y];
            }
        }
        
        // Limpiar celdas de suelo aisladas (rodeadas completamente por paredes)
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                if (map[x][y] == FLOOR) {
                    int wallNeighbors = countWallNeighbors(map, x, y);
                    if (wallNeighbors == 8) {
                        processedMap[x][y] = WALL; // Convertir suelo aislado en pared
                    }
                }
            }
        }
        
        return processedMap;
    }
    
    /**
     * Genera un mapa con parámetros personalizados
     * @param density Densidad inicial de paredes (0.0 - 1.0)
     * @param threshold Umbral de vecinos para crear pared
     * @param iter Número de iteraciones
     * @return Mapa generado
     */
    public int[][] generateMapWithParams(float density, int threshold, int iter) {
        float oldDensity = this.initialDensity;
        int oldThreshold = this.wallThreshold;
        int oldIterations = this.iterations;
        
        this.initialDensity = density;
        this.wallThreshold = threshold;
        this.iterations = iter;
        
        int[][] result = generateMap();
        
        // Restaurar parámetros originales
        this.initialDensity = oldDensity;
        this.wallThreshold = oldThreshold;
        this.iterations = oldIterations;
        
        return result;
    }
    
    /**
     * Imprime el mapa en consola para debug
     * @param map Mapa a imprimir
     */
    public void printMap(int[][] map) {
        StringBuilder sb = new StringBuilder();
        sb.append("Mapa generado:\n");
        
        for (int y = height - 1; y >= 0; y--) { // Imprimir desde arriba
            for (int x = 0; x < width; x++) {
                if (map[x][y] == WALL) {
                    sb.append("█");
                } else {
                    sb.append("░");
                }
            }
            sb.append("\n");
        }
        
        Gdx.app.log("CellularAutomata", sb.toString());
    }
    
    /**
     * Calcula el porcentaje de paredes en el mapa
     * @param map Mapa a analizar
     * @return Porcentaje de paredes (0.0 - 1.0)
     */
    public float calculateWallPercentage(int[][] map) {
        int wallCount = 0;
        int totalCells = width * height;
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (map[x][y] == WALL) {
                    wallCount++;
                }
            }
        }
        
        return (float) wallCount / totalCells;
    }
    
    // Getters y Setters
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public float getInitialDensity() { return initialDensity; }
    public int getWallThreshold() { return wallThreshold; }
    public int getIterations() { return iterations; }
    
    public void setInitialDensity(float density) { 
        this.initialDensity = Math.max(0.0f, Math.min(1.0f, density)); 
    }
    
    public void setWallThreshold(int threshold) { 
        this.wallThreshold = Math.max(0, Math.min(8, threshold)); 
    }
    
    public void setIterations(int iterations) { 
        this.iterations = Math.max(1, iterations); 
    }
    
    public void setSeed(long seed) {
        this.random = new Random(seed);
    }
}