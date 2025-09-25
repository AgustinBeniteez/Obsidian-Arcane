package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase que maneja la representación, renderizado y lógica del mapa del juego.
 * Ahora maneja un sistema de salas conectadas con túneles en lugar de mapas continuos.
 */
public class GameMap {
    
    // Constantes del mapa
    private static final float TILE_SIZE = 32.0f;
    private static final int BASE_ROOM_WIDTH = 20;
    private static final int BASE_ROOM_HEIGHT = 15;
    private static final int GRID_WIDTH = 5;  // Número de salas horizontalmente
    private static final int GRID_HEIGHT = 4; // Número de salas verticalmente
    
    // Tamaños posibles de salas (multiplicadores del tamaño base)
    private static final int[] ROOM_SIZE_MULTIPLIERS = {1, 2, 4}; // x1, x2, x4
    private static final float[] ROOM_SIZE_PROBABILITIES = {0.6f, 0.3f, 0.1f}; // 60%, 30%, 10%
    
    // Propiedades del mapa de salas
    private int totalWidth;
    private int totalHeight;
    private Room[][] roomGrid;
    private List<Room> rooms;
    private Random random;
    private long seed;

    public GameMap() {
        this(System.currentTimeMillis()); // Usar timestamp como seed por defecto
    }
    
    public GameMap(long seed) {
        this.seed = seed;
        this.totalWidth = GRID_WIDTH * BASE_ROOM_WIDTH * 4; // Máximo tamaño posible
        this.totalHeight = GRID_HEIGHT * BASE_ROOM_HEIGHT * 4;
        this.random = new Random(seed);
        this.rooms = new ArrayList<>();
        
        // Generar el mapa de salas
        generateRoomMap();
    }
    
    /**
     * Genera el mapa de salas conectadas con tamaños variables
     */
    private void generateRoomMap() {
        // Inicializar la grilla de salas
        roomGrid = new Room[GRID_WIDTH][GRID_HEIGHT];
        rooms.clear();
        
        int currentWorldX = 0;
        
        // Crear todas las salas con tamaños variables
        for (int x = 0; x < GRID_WIDTH; x++) {
            int currentWorldY = 0;
            int maxWidthInColumn = 0;
            
            for (int y = 0; y < GRID_HEIGHT; y++) {
                // Seleccionar tamaño aleatorio basado en probabilidades
                int sizeMultiplier = selectRoomSize();
                int roomWidth = BASE_ROOM_WIDTH * sizeMultiplier;
                int roomHeight = BASE_ROOM_HEIGHT * sizeMultiplier;
                
                // Crear sala con posición absoluta en el mundo
                Room room = new Room(x, y, roomWidth, roomHeight, this);
                room.setWorldPosition(currentWorldX, currentWorldY);
                
                roomGrid[x][y] = room;
                rooms.add(room);
                
                currentWorldY += roomHeight;
                maxWidthInColumn = Math.max(maxWidthInColumn, roomWidth);
            }
            
            currentWorldX += maxWidthInColumn;
        }
        
        // Conectar salas adyacentes aleatoriamente
        connectRooms();
        
        System.out.println("Mapa de salas generado: " + rooms.size() + " salas creadas");
    }
    
    /**
     * Selecciona un tamaño de sala basado en probabilidades
     */
    private int selectRoomSize() {
        float randomValue = random.nextFloat();
        float cumulativeProbability = 0.0f;
        
        for (int i = 0; i < ROOM_SIZE_MULTIPLIERS.length; i++) {
            cumulativeProbability += ROOM_SIZE_PROBABILITIES[i];
            if (randomValue <= cumulativeProbability) {
                return ROOM_SIZE_MULTIPLIERS[i];
            }
        }
        
        return ROOM_SIZE_MULTIPLIERS[0]; // Fallback al tamaño más pequeño
    }
    
    /**
     * Conecta las salas entre sí creando túneles
     */
    private void connectRooms() {
        // Primero, crear conexiones garantizadas para asegurar conectividad
        ensureConnectivity();
        
        // Luego, agregar conexiones adicionales aleatorias
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                Room currentRoom = roomGrid[x][y];
                
                // Solo procesar si la sala actual existe
                if (currentRoom == null) continue;
                
                // Conectar con la sala de la derecha (probabilidad adicional)
                if (x < GRID_WIDTH - 1 && random.nextFloat() > 0.6f) {
                    Room rightRoom = roomGrid[x + 1][y];
                    if (rightRoom != null && !currentRoom.hasConnection(Room.Direction.EAST)) {
                        currentRoom.connectTo(rightRoom, Room.Direction.EAST);
                    }
                }
                
                // Conectar con la sala de arriba (probabilidad adicional)
                if (y < GRID_HEIGHT - 1 && random.nextFloat() > 0.6f) {
                    Room topRoom = roomGrid[x][y + 1];
                    if (topRoom != null && !currentRoom.hasConnection(Room.Direction.NORTH)) {
                        currentRoom.connectTo(topRoom, Room.Direction.NORTH);
                    }
                }
            }
        }
    }
    
    /**
     * Asegura que todas las salas estén conectadas creando un camino mínimo garantizado
     */
    private void ensureConnectivity() {
        // Crear conexiones horizontales garantizadas (al menos una por fila)
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH - 1; x++) {
                Room currentRoom = roomGrid[x][y];
                Room rightRoom = roomGrid[x + 1][y];
                
                // Solo conectar si ambas salas existen
                if (currentRoom != null && rightRoom != null) {
                    currentRoom.connectTo(rightRoom, Room.Direction.EAST);
                }
            }
        }
        
        // Crear conexiones verticales garantizadas (al menos una por columna)
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT - 1; y++) {
                Room currentRoom = roomGrid[x][y];
                Room topRoom = roomGrid[x][y + 1];
                
                // Solo conectar si ambas salas existen
                if (currentRoom != null && topRoom != null) {
                    currentRoom.connectTo(topRoom, Room.Direction.NORTH);
                }
            }
        }
    }
    
    /**
     * Renderiza todo el mapa de salas
     */
    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        for (Room room : rooms) {
            room.render(shapeRenderer, TILE_SIZE);
        }
        
        shapeRenderer.end();
    }
    
    /**
     * Renderiza una región específica del mapa (optimización para cámaras)
     */
    public void renderRegion(ShapeRenderer shapeRenderer, float cameraX, float cameraY, 
                           float viewWidth, float viewHeight) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Renderizar todas las salas (simplificado para salas de tamaños variables)
        for (Room room : rooms) {
            if (room != null) {
                room.render(shapeRenderer, TILE_SIZE);
            }
        }
        
        shapeRenderer.end();
    }
    
    /**
     * Verifica si hay colisión en las coordenadas del mundo especificadas
     */
    public boolean isCollision(float worldX, float worldY) {
        int tileX = (int)(worldX / TILE_SIZE);
        int tileY = (int)(worldY / TILE_SIZE);
        
        // Encontrar la sala que contiene estas coordenadas
        Room room = getRoomAt(tileX, tileY);
        if (room == null) {
            return true; // Fuera del mapa es sólido
        }
        
        int localX = room.getLocalX(tileX);
        int localY = room.getLocalY(tileY);
        
        return room.isSolid(localX, localY);
    }
    
    /**
     * Obtiene la sala que contiene las coordenadas de tile especificadas
     */
    private Room getRoomAt(int tileX, int tileY) {
        // Buscar en todas las salas ya que ahora tienen tamaños variables
        for (Room room : rooms) {
            if (room != null && room.isInRoom(tileX, tileY)) {
                return room;
            }
        }
        
        return null;
    }
    
    /**
     * Encuentra una posición válida para generar el jugador
     */
    public Vector2 findValidSpawnPosition() {
        // Buscar en la primera sala (esquina inferior izquierda)
        Room spawnRoom = roomGrid[0][0];
        
        // Buscar una posición de suelo en el centro de la sala
        for (int attempts = 0; attempts < 100; attempts++) {
            int localX = random.nextInt(spawnRoom.getWidth() - 4) + 2;
            int localY = random.nextInt(spawnRoom.getHeight() - 4) + 2;
            
            if (!spawnRoom.isSolid(localX, localY)) {
                float worldX = (spawnRoom.getWorldX() + localX) * TILE_SIZE + TILE_SIZE / 2;
                float worldY = (spawnRoom.getWorldY() + localY) * TILE_SIZE + TILE_SIZE / 2;
                return new Vector2(worldX, worldY);
            }
        }
        
        // Posición por defecto si no se encuentra una válida
        return new Vector2(TILE_SIZE * 3, TILE_SIZE * 3);
    }
    
    /**
     * Regenera todo el mapa usando la misma seed
     */
    public void regenerateMap() {
        this.random = new Random(seed); // Reinicializar con la misma seed
        generateRoomMap();
        System.out.println("Mapa regenerado con seed: " + seed);
    }
    
    /**
     * Regenera el mapa con una nueva seed
     */
    public void regenerateMapWithNewSeed(long newSeed) {
        this.seed = newSeed;
        this.random = new Random(seed);
        generateRoomMap();
        System.out.println("Mapa regenerado con nueva seed: " + seed);
    }
    
    public long getSeed() {
        return seed;
    }
    
    // Métodos de utilidad y getters
    public int getTotalWidth() { return totalWidth; }
    public int getTotalHeight() { return totalHeight; }
    public float getTileSize() { return TILE_SIZE; }
    public int getGridWidth() { return GRID_WIDTH; }
    public int getGridHeight() { return GRID_HEIGHT; }
    public int getBaseRoomWidth() { return BASE_ROOM_WIDTH; }
    public int getBaseRoomHeight() { return BASE_ROOM_HEIGHT; }
    
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
        System.out.println("=== MAPA DE SALAS ===");
        System.out.println("Grilla: " + GRID_WIDTH + "x" + GRID_HEIGHT + " salas");
        System.out.println("Tamaño base de sala: " + BASE_ROOM_WIDTH + "x" + BASE_ROOM_HEIGHT + " tiles");
        System.out.println("Tamaño total: " + totalWidth + "x" + totalHeight + " tiles");
        System.out.println("Salas creadas: " + rooms.size());
        
        // Mostrar conexiones
        System.out.println("\nConexiones entre salas:");
        for (int y = GRID_HEIGHT - 1; y >= 0; y--) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                Room room = roomGrid[x][y];
                System.out.print("[" + x + "," + y + "]");
                if (room.hasConnection(Room.Direction.EAST)) System.out.print("-E");
                if (room.hasConnection(Room.Direction.NORTH)) System.out.print("-N");
                System.out.print(" ");
            }
            System.out.println();
        }
    }
    
    /**
     * Libera recursos
     */
    public void dispose() {
        // No hay recursos específicos que liberar en esta implementación
        System.out.println("GameMap disposed");
    }
    
    /**
     * Verifica si existe una sala en la posición especificada de la grilla
     */
    public boolean hasRoomAt(int gridX, int gridY) {
        if (gridX < 0 || gridX >= GRID_WIDTH || gridY < 0 || gridY >= GRID_HEIGHT) {
            return false;
        }
        return roomGrid[gridX][gridY] != null;
    }
}