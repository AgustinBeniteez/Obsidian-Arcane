package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import java.util.HashMap;
import java.util.Map;

/**
 * Representa una sala individual en el mapa del juego.
 * Cada sala tiene 4 paredes (arriba, abajo, izquierda, derecha) y puede tener
 * conexiones (túneles) hacia otras salas en cualquiera de las 4 direcciones.
 */
public class Room {
    
    public enum Direction {
        NORTH, SOUTH, EAST, WEST
    }
    
    public enum TileType {
        FLOOR, WALL, TUNNEL_ENTRANCE, TUNNEL_FLOOR, TUNNEL_WALL, PARKOUR_PLATFORM
    }
    
    // Propiedades de la sala
    private int x, y;                    // Posición de la sala en la grilla de salas
    private int width, height;           // Dimensiones de la sala en tiles
    private int worldX, worldY;          // Posición absoluta en el mundo
    private TileType[][] tiles;          // Mapa de tiles de la sala
    private GameMap gameMap;             // Referencia al mapa para verificar salas adyacentes
    
    // Conexiones con otras salas
    private Map<Direction, Room> connections;
    private Map<Direction, Boolean> hasConnection;
    
    // Configuración de túneles
    private static final int TUNNEL_WIDTH = 5;      // Túneles más anchos
    private static final int TUNNEL_HEIGHT = 12;    // Túneles más altos
    private static final int PARKOUR_PLATFORMS = 5; // Más plataformas de parkour
    
    public Room(int x, int y, int width, int height, GameMap gameMap) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.gameMap = gameMap;
        this.worldX = x * width;  // Será actualizado por setWorldPosition
        this.worldY = y * height; // Será actualizado por setWorldPosition
        
        this.connections = new HashMap<>();
        this.hasConnection = new HashMap<>();
        
        // Inicializar sin conexiones
        for (Direction dir : Direction.values()) {
            hasConnection.put(dir, false);
        }
        
        generateRoomLayout();
    }
    
    /**
     * Establece la posición absoluta de la sala en el mundo
     */
    public void setWorldPosition(int worldX, int worldY) {
        this.worldX = worldX;
        this.worldY = worldY;
    }
    
    /**
     * Genera el layout básico de la sala con paredes perimetrales
     */
    private void generateRoomLayout() {
        tiles = new TileType[width][height];
        
        // Llenar toda la sala con suelo
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tiles[i][j] = TileType.FLOOR;
            }
        }
        
        // Crear paredes perimetrales
        createPerimeterWalls();
    }
    
    /**
     * Crea las paredes del perímetro de la sala
     */
    private void createPerimeterWalls() {
        // Paredes horizontales (arriba y abajo)
        for (int i = 0; i < width; i++) {
            tiles[i][0] = TileType.WALL;           // Pared inferior
            tiles[i][height - 1] = TileType.WALL;  // Pared superior
        }
        
        // Paredes verticales (izquierda y derecha)
        for (int j = 0; j < height; j++) {
            tiles[0][j] = TileType.WALL;           // Pared izquierda
            tiles[width - 1][j] = TileType.WALL;   // Pared derecha
        }
    }
    
    /**
     * Conecta esta sala con otra en la dirección especificada
     */
    public void connectTo(Room otherRoom, Direction direction) {
        // Validar que la sala de destino no sea null
        if (otherRoom == null) {
            System.out.println("Advertencia: Intento de conectar con una sala null en dirección " + direction);
            return;
        }
        
        connections.put(direction, otherRoom);
        hasConnection.put(direction, true);
        
        // Crear túnel en esta sala
        createTunnel(direction, otherRoom);
        
        // Conectar la otra sala de vuelta
        Direction oppositeDir = getOppositeDirection(direction);
        otherRoom.connections.put(oppositeDir, this);
        otherRoom.hasConnection.put(oppositeDir, true);
        otherRoom.createTunnel(oppositeDir, this);
    }
    
    /**
     * Crea una conexión simple en el borde de la sala alineada con la sala conectada
     */
    private void createTunnel(Direction direction, Room connectedRoom) {
        // Verificar que existe una sala adyacente en esa dirección antes de crear la puerta
        int adjacentX = x;
        int adjacentY = y;
        
        switch (direction) {
            case NORTH:
                adjacentY = y + 1;
                break;
            case SOUTH:
                adjacentY = y - 1;
                break;
            case EAST:
                adjacentX = x + 1;
                break;
            case WEST:
                adjacentX = x - 1;
                break;
        }
        
        // Solo crear la puerta si existe una sala adyacente
        if (!gameMap.hasRoomAt(adjacentX, adjacentY)) {
            System.out.println("No se crea puerta en dirección " + direction + " desde sala [" + x + "," + y + "] - no hay sala adyacente");
            return;
        }
        
        int connectionWidth = 3; // Ancho de la conexión
        
        // Calcular la posición de la conexión basada en la alineación con la sala conectada
        int connectionStart = calculateConnectionPosition(direction, connectedRoom);
        
        switch (direction) {
            case NORTH:
                // Conexión en el borde superior
                for (int i = connectionStart; i < connectionStart + connectionWidth; i++) {
                    if (i >= 0 && i < width) {
                        tiles[i][height - 1] = TileType.TUNNEL_ENTRANCE;
                    }
                }
                break;
            case SOUTH:
                // Conexión en el borde inferior
                for (int i = connectionStart; i < connectionStart + connectionWidth; i++) {
                    if (i >= 0 && i < width) {
                        tiles[i][0] = TileType.TUNNEL_ENTRANCE;
                    }
                }
                break;
            case EAST:
                // Conexión en el borde derecho
                for (int j = connectionStart; j < connectionStart + connectionWidth; j++) {
                    if (j >= 0 && j < height) {
                        tiles[width - 1][j] = TileType.TUNNEL_ENTRANCE;
                    }
                }
                break;
            case WEST:
                // Conexión en el borde izquierdo
                for (int j = connectionStart; j < connectionStart + connectionWidth; j++) {
                    if (j >= 0 && j < height) {
                        tiles[0][j] = TileType.TUNNEL_ENTRANCE;
                    }
                }
                break;
        }
    }
    
    /**
     * Calcula la posición de inicio de la conexión para alinearla con la sala conectada
     */
    private int calculateConnectionPosition(Direction direction, Room connectedRoom) {
        int connectionWidth = 3;
        
        switch (direction) {
            case NORTH:
            case SOUTH:
                // Para conexiones horizontales, alinear basándose en la posición X
                int overlapStart = Math.max(this.worldX, connectedRoom.worldX);
                int overlapEnd = Math.min(this.worldX + this.width * 32, connectedRoom.worldX + connectedRoom.width * 32);
                int overlapCenter = (overlapStart + overlapEnd) / 2;
                int localCenter = (overlapCenter - this.worldX) / 32;
                return Math.max(0, Math.min(this.width - connectionWidth, localCenter - connectionWidth/2));
                
            case EAST:
            case WEST:
                // Para conexiones verticales, alinear basándose en la posición Y
                int overlapStartY = Math.max(this.worldY, connectedRoom.worldY);
                int overlapEndY = Math.min(this.worldY + this.height * 32, connectedRoom.worldY + connectedRoom.height * 32);
                int overlapCenterY = (overlapStartY + overlapEndY) / 2;
                int localCenterY = (overlapCenterY - this.worldY) / 32;
                return Math.max(0, Math.min(this.height - connectionWidth, localCenterY - connectionWidth/2));
                
            default:
                return 0;
        }
    }
    
    /**
     * Obtiene la dirección opuesta
     */
    private Direction getOppositeDirection(Direction direction) {
        switch (direction) {
            case NORTH: return Direction.SOUTH;
            case SOUTH: return Direction.NORTH;
            case EAST: return Direction.WEST;
            case WEST: return Direction.EAST;
            default: return direction;
        }
    }
    
    /**
     * Renderiza la sala
     */
    public void render(ShapeRenderer shapeRenderer, float tileSize) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                float renderX = (worldX + i) * tileSize;
                float renderY = (worldY + j) * tileSize;
                
                Color color = getTileColor(tiles[i][j]);
                shapeRenderer.setColor(color);
                shapeRenderer.rect(renderX, renderY, tileSize, tileSize);
            }
        }
    }
    
    /**
     * Obtiene el color para cada tipo de tile
     */
    private Color getTileColor(TileType tileType) {
        switch (tileType) {
            case FLOOR: return Color.LIGHT_GRAY;
            case WALL: return Color.DARK_GRAY;
            case TUNNEL_ENTRANCE: return Color.BROWN;
            case TUNNEL_FLOOR: return Color.CYAN;        // Color distintivo para túneles
            case TUNNEL_WALL: return Color.BLUE;         // Paredes de túnel más visibles
            case PARKOUR_PLATFORM: return Color.ORANGE; // Plataformas de parkour destacadas
            default: return Color.BLACK;
        }
    }
    
    /**
     * Verifica si una posición es sólida (no se puede atravesar)
     */
    public boolean isSolid(int localX, int localY) {
        if (localX < 0 || localX >= width || localY < 0 || localY >= height) {
            return true; // Fuera de los límites es sólido
        }
        
        TileType tile = tiles[localX][localY];
        return tile == TileType.WALL || tile == TileType.TUNNEL_WALL;
    }
    
    /**
     * Convierte coordenadas del mundo a coordenadas locales de la sala
     */
    public boolean isInRoom(int worldX, int worldY) {
        return worldX >= this.worldX && worldX < this.worldX + width &&
               worldY >= this.worldY && worldY < this.worldY + height;
    }
    
    public int getLocalX(int worldX) {
        return worldX - this.worldX;
    }
    
    public int getLocalY(int worldY) {
        return worldY - this.worldY;
    }
    
    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getWorldX() { return worldX; }
    public int getWorldY() { return worldY; }
    public TileType getTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return tiles[x][y];
        }
        return TileType.WALL;
    }
    public boolean hasConnection(Direction direction) {
        return hasConnection.get(direction);
    }
    public Room getConnection(Direction direction) {
        return connections.get(direction);
    }
}