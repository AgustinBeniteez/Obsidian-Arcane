package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Player class with 2D physics for movement and jumping
 */
public class Player {
    // Physics constants
    private static final float GRAVITY = -800f; // Pixels per second squared
    private static final float JUMP_VELOCITY = 400f; // Pixels per second
    private static final float MOVE_SPEED = 200f; // Pixels per second
    private static final float MAX_FALL_SPEED = -500f; // Terminal velocity
    private static final float GROUND_FRICTION = 0.8f; // Friction when on ground
    private static final float AIR_FRICTION = 0.95f; // Air resistance
    
    // Player properties
    private Vector2 position;
    private Vector2 velocity;
    private Rectangle bounds;
    private boolean onGround;
    private boolean canJump;
    
    // Visual properties
    private static final float PLAYER_WIDTH = 32f;
    private static final float PLAYER_HEIGHT = 48f;
    private Color playerColor;
    
    public Player(float startX, float startY) {
        position = new Vector2(startX, startY);
        velocity = new Vector2(0, 0);
        bounds = new Rectangle(startX, startY, PLAYER_WIDTH, PLAYER_HEIGHT);
        onGround = false;
        canJump = true;
        playerColor = new Color(0.2f, 0.6f, 1.0f, 1.0f); // Blue color
    }
    
    /**
     * Update player physics and position
     */
    public void update(float deltaTime) {
        // Apply gravity
        if (!onGround) {
            velocity.y += GRAVITY * deltaTime;
            // Limit fall speed
            if (velocity.y < MAX_FALL_SPEED) {
                velocity.y = MAX_FALL_SPEED;
            }
        }
        
        // Apply friction
        if (onGround) {
            velocity.x *= GROUND_FRICTION;
        } else {
            velocity.x *= AIR_FRICTION;
        }
        
        // Update position
        position.x += velocity.x * deltaTime;
        position.y += velocity.y * deltaTime;
        
        // Update bounds
        bounds.setPosition(position.x, position.y);
        
        // Reset ground state (will be set by collision detection)
        onGround = false;
    }
    
    /**
     * Move player horizontally
     */
    public void moveHorizontal(float direction) {
        velocity.x += direction * MOVE_SPEED;
        
        // Limit horizontal speed
        float maxSpeed = MOVE_SPEED * 1.5f;
        if (velocity.x > maxSpeed) velocity.x = maxSpeed;
        if (velocity.x < -maxSpeed) velocity.x = -maxSpeed;
    }
    
    /**
     * Make player jump
     */
    public void jump() {
        if (onGround && canJump) {
            velocity.y = JUMP_VELOCITY;
            onGround = false;
            canJump = false;
        }
    }
    
    /**
     * Handle collision with ground
     */
    public void handleGroundCollision(float groundY) {
        if (velocity.y <= 0) { // Only if falling or stationary
            position.y = groundY;
            velocity.y = 0;
            onGround = true;
            canJump = true;
            updateBounds();
        }
    }
    
    /**
     * Handle collision with walls
     */
    public void handleWallCollision(float wallX, boolean isLeftWall) {
        if (isLeftWall && position.x <= wallX && velocity.x <= 0) {
            position.x = wallX;
            velocity.x = 0;
        } else if (!isLeftWall && position.x + PLAYER_WIDTH >= wallX && velocity.x >= 0) {
            position.x = wallX - PLAYER_WIDTH;
            velocity.x = 0;
        }
        bounds.setPosition(position.x, position.y);
    }
    
    /**
     * Handle collision with ceiling
     */
    public void handleCeilingCollision(float ceilingY) {
        if (position.y + PLAYER_HEIGHT >= ceilingY && velocity.y >= 0) {
            position.y = ceilingY - PLAYER_HEIGHT;
            velocity.y = 0;
            bounds.setPosition(position.x, position.y);
        }
    }
    
    /**
     * Render the player
     */
    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(playerColor);
        shapeRenderer.rect(position.x, position.y, PLAYER_WIDTH, PLAYER_HEIGHT);
        
        // Draw a simple face
        shapeRenderer.setColor(Color.WHITE);
        // Eyes
        shapeRenderer.rect(position.x + 8, position.y + PLAYER_HEIGHT - 15, 4, 4);
        shapeRenderer.rect(position.x + 20, position.y + PLAYER_HEIGHT - 15, 4, 4);
        // Mouth
        shapeRenderer.rect(position.x + 12, position.y + PLAYER_HEIGHT - 25, 8, 2);
    }
    
    // Getters and setters
    public Vector2 getPosition() { return position; }
    public Vector2 getVelocity() { return velocity; }
    public Rectangle getBounds() { return bounds; }
    public boolean isOnGround() { return onGround; }
    public void setOnGround(boolean onGround) { this.onGround = onGround; }
    
    public float getWidth() { return PLAYER_WIDTH; }
    public float getHeight() { return PLAYER_HEIGHT; }
    
    public void setPosition(float x, float y) {
        position.set(x, y);
        bounds.setPosition(x, y);
    }
    
    /**
     * Update the bounds rectangle to match the current position
     */
    public void updateBounds() {
        bounds.setPosition(position.x, position.y);
    }
}