package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a room/area in the game world with platforms and boundaries
 */
public class Room {
    private float width;
    private float height;
    private float x, y; // Room position in world coordinates
    private List<Platform> platforms;
    private Rectangle bounds;
    private Color backgroundColor;
    private Color wallColor;
    
    // Room boundaries
    private static final float WALL_THICKNESS = 20f;
    
    public Room(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.platforms = new ArrayList<>();
        this.bounds = new Rectangle(x, y, width, height);
        this.backgroundColor = new Color(0.1f, 0.1f, 0.2f, 1.0f); // Dark blue background
        this.wallColor = new Color(0.4f, 0.4f, 0.5f, 1.0f); // Gray walls
        
        // Create default platforms for a simple room
        createDefaultPlatforms();
    }
    
    /**
     * Create default platforms for the room
     */
    private void createDefaultPlatforms() {
        // Ground platform
        platforms.add(new Platform(x, y, width, WALL_THICKNESS));
        
        // Left wall
        platforms.add(new Platform(x, y, WALL_THICKNESS, height));
        
        // Right wall
        platforms.add(new Platform(x + width - WALL_THICKNESS, y, WALL_THICKNESS, height));
        
        // Ceiling
        platforms.add(new Platform(x, y + height - WALL_THICKNESS, width, WALL_THICKNESS));
        
        // Add some floating platforms
        platforms.add(new Platform(x + 100, y + 150, 200, 20));
        platforms.add(new Platform(x + 400, y + 250, 150, 20));
        platforms.add(new Platform(x + 200, y + 350, 180, 20));
    }
    
    /**
     * Add a platform to the room
     */
    public void addPlatform(Platform platform) {
        platforms.add(platform);
    }
    
    /**
     * Check collision between player and all platforms in the room
     */
    public void handlePlayerCollision(Player player) {
        Rectangle playerBounds = player.getBounds();
        
        for (Platform platform : platforms) {
            if (playerBounds.overlaps(platform.getBounds())) {
                handlePlatformCollision(player, platform);
            }
        }
    }
    
    /**
     * Handle collision between player and a specific platform
     */
    private void handlePlatformCollision(Player player, Platform platform) {
        Rectangle playerBounds = player.getBounds();
        Rectangle platformBounds = platform.getBounds();
        
        // Calculate overlap on each axis
        float overlapX = Math.min(playerBounds.x + playerBounds.width - platformBounds.x,
                                 platformBounds.x + platformBounds.width - playerBounds.x);
        float overlapY = Math.min(playerBounds.y + playerBounds.height - platformBounds.y,
                                 platformBounds.y + platformBounds.height - playerBounds.y);
        
        // Resolve collision based on smallest overlap
        if (overlapX < overlapY) {
            // Horizontal collision (walls)
            if (playerBounds.x < platformBounds.x) {
                // Player hitting from left
                player.handleWallCollision(platformBounds.x, true);
            } else {
                // Player hitting from right
                player.handleWallCollision(platformBounds.x + platformBounds.width, false);
            }
        } else {
            // Vertical collision (ground/ceiling)
            if (playerBounds.y < platformBounds.y) {
                // Player hitting from below (ground)
                player.handleGroundCollision(platformBounds.y);
            } else {
                // Player hitting from above (ceiling)
                player.handleCeilingCollision(platformBounds.y + platformBounds.height);
            }
        }
    }
    
    /**
     * Render the room and all its platforms
     */
    public void render(ShapeRenderer shapeRenderer) {
        // Draw background
        shapeRenderer.setColor(backgroundColor);
        shapeRenderer.rect(x, y, width, height);
        
        // Draw platforms
        for (Platform platform : platforms) {
            platform.render(shapeRenderer);
        }
    }
    
    /**
     * Check if a point is inside the room
     */
    public boolean contains(float pointX, float pointY) {
        return bounds.contains(pointX, pointY);
    }
    
    // Getters
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public Rectangle getBounds() { return bounds; }
    public List<Platform> getPlatforms() { return platforms; }
    
    /**
     * Inner class representing a platform/solid surface
     */
    public static class Platform {
        private Rectangle bounds;
        private Color color;
        
        public Platform(float x, float y, float width, float height) {
            this.bounds = new Rectangle(x, y, width, height);
            this.color = new Color(0.4f, 0.4f, 0.5f, 1.0f); // Gray color
        }
        
        public Platform(float x, float y, float width, float height, Color color) {
            this.bounds = new Rectangle(x, y, width, height);
            this.color = color;
        }
        
        public void render(ShapeRenderer shapeRenderer) {
            shapeRenderer.setColor(color);
            shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
            
            // Add a slight highlight on top for better visibility
            shapeRenderer.setColor(color.r + 0.2f, color.g + 0.2f, color.b + 0.2f, color.a);
            shapeRenderer.rect(bounds.x, bounds.y + bounds.height - 2, bounds.width, 2);
        }
        
        public Rectangle getBounds() { return bounds; }
        public Color getColor() { return color; }
        public void setColor(Color color) { this.color = color; }
    }
}