package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.ArrayList;
import java.util.List;

/**
 * GameWorld manages the rooms and player interactions in the game
 */
public class GameWorld {
    private List<Room> rooms;
    private Room currentRoom;
    private Player player;
    
    // World constants
    private static final float ROOM_WIDTH = 800f;
    private static final float ROOM_HEIGHT = 600f;
    
    public GameWorld() {
        rooms = new ArrayList<>();
        
        // Create initial room
        Room startingRoom = new Room(0, 0, ROOM_WIDTH, ROOM_HEIGHT);
        rooms.add(startingRoom);
        currentRoom = startingRoom;
        
        // Create player in the center of the starting room
        player = new Player(ROOM_WIDTH / 2 - 16, 100); // Start above ground
    }
    
    /**
     * Update the game world
     */
    public void update(float deltaTime) {
        // Update player physics
        player.update(deltaTime);
        
        // Handle collisions with current room
        if (currentRoom != null) {
            currentRoom.handlePlayerCollision(player);
        }
        
        // Check for room transitions (for future expansion)
        checkRoomTransitions();
    }
    
    /**
     * Handle player movement input
     */
    public void handleInput(boolean moveLeft, boolean moveRight, boolean jump) {
        float direction = 0;
        if (moveLeft) direction -= 1;
        if (moveRight) direction += 1;
        
        player.moveHorizontal(direction);
        
        if (jump) {
            player.jump();
        }
    }
    
    /**
     * Render the game world
     */
    public void render(ShapeRenderer shapeRenderer) {
        // Render current room
        if (currentRoom != null) {
            currentRoom.render(shapeRenderer);
        }
        
        // Render player
        player.render(shapeRenderer);
    }
    
    /**
     * Check for room transitions (placeholder for future expansion)
     */
    private void checkRoomTransitions() {
        // This will be implemented when we add multiple connected rooms
        // For now, we just keep the player within the current room bounds
        if (currentRoom != null) {
            float playerX = player.getPosition().x;
            float playerY = player.getPosition().y;
            
            // Keep player within room bounds (basic boundary check)
            if (playerX < currentRoom.getX()) {
                player.setPosition(currentRoom.getX(), playerY);
            } else if (playerX + player.getBounds().width > currentRoom.getX() + currentRoom.getWidth()) {
                player.setPosition(currentRoom.getX() + currentRoom.getWidth() - player.getBounds().width, playerY);
            }
        }
    }
    
    /**
     * Add a room to the world
     */
    public void addRoom(Room room) {
        rooms.add(room);
    }
    
    /**
     * Switch to a different room
     */
    public void switchToRoom(int roomIndex) {
        if (roomIndex >= 0 && roomIndex < rooms.size()) {
            currentRoom = rooms.get(roomIndex);
        }
    }
    
    /**
     * Get the current room
     */
    public Room getCurrentRoom() {
        return currentRoom;
    }
    
    /**
     * Get the player
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Get all rooms
     */
    public List<Room> getRooms() {
        return rooms;
    }
    
    /**
     * Reset player to starting position
     */
    public void resetPlayer() {
        if (currentRoom != null) {
            player.setPosition(currentRoom.getX() + currentRoom.getWidth() / 2 - 16, 100);
            player.getVelocity().set(0, 0);
        }
    }
}