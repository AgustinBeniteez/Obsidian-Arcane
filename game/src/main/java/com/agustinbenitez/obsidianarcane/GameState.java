package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.math.Vector2;
import java.io.Serializable;
import java.util.Date;

/**
 * Representa el estado completo de una partida guardada
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Información de la partida
    private String saveName;
    private Date saveDate;
    private String villageName;
    
    // Estado del jugador
    private float playerX;
    private float playerY;
    private int playerLevel;
    private int playerExperience;
    private int playerHealth;
    private int playerMaxHealth;
    
    // Tiempo de juego
    private float playTime;
    
    public GameState() {
        this.saveDate = new Date();
        this.saveName = "Partida " + System.currentTimeMillis();
        this.playerLevel = 1;
        this.playerExperience = 0;
        this.playerHealth = 100;
        this.playerMaxHealth = 100;
        this.playTime = 0f;
    }
    
    public GameState(String saveName, String villageName, Vector2 playerPosition) {
        this();
        this.saveName = saveName;
        this.villageName = villageName;
        this.playerX = playerPosition.x;
        this.playerY = playerPosition.y;
    }
    
    // Getters y Setters
    public String getSaveName() { return saveName; }
    public void setSaveName(String saveName) { this.saveName = saveName; }
    
    public Date getSaveDate() { return saveDate; }
    public void setSaveDate(Date saveDate) { this.saveDate = saveDate; }
    
    public String getVillageName() { return villageName; }
    public void setVillageName(String villageName) { this.villageName = villageName; }
    
    public float getPlayerX() { return playerX; }
    public void setPlayerX(float playerX) { this.playerX = playerX; }
    
    public float getPlayerY() { return playerY; }
    public void setPlayerY(float playerY) { this.playerY = playerY; }
    
    public Vector2 getPlayerPosition() { return new Vector2(playerX, playerY); }
    public void setPlayerPosition(Vector2 position) {
        this.playerX = position.x;
        this.playerY = position.y;
    }
    
    public int getPlayerLevel() { return playerLevel; }
    public void setPlayerLevel(int playerLevel) { this.playerLevel = playerLevel; }
    
    public int getPlayerExperience() { return playerExperience; }
    public void setPlayerExperience(int playerExperience) { this.playerExperience = playerExperience; }
    
    public int getPlayerHealth() { return playerHealth; }
    public void setPlayerHealth(int playerHealth) { this.playerHealth = playerHealth; }
    
    public int getPlayerMaxHealth() { return playerMaxHealth; }
    public void setPlayerMaxHealth(int playerMaxHealth) { this.playerMaxHealth = playerMaxHealth; }
    
    public float getPlayTime() { return playTime; }
    public void setPlayTime(float playTime) { this.playTime = playTime; }
    
    public void addPlayTime(float deltaTime) { this.playTime += deltaTime; }
    
    /**
     * Actualiza la fecha de guardado al momento actual
     */
    public void updateSaveDate() {
        this.saveDate = new Date();
    }
    
    /**
     * Crea una copia del estado actual del juego
     */
    public GameState copy() {
        GameState copy = new GameState();
        copy.saveName = this.saveName;
        copy.saveDate = new Date(this.saveDate.getTime());
        copy.villageName = this.villageName;
        copy.playerX = this.playerX;
        copy.playerY = this.playerY;
        copy.playerLevel = this.playerLevel;
        copy.playerExperience = this.playerExperience;
        copy.playerHealth = this.playerHealth;
        copy.playerMaxHealth = this.playerMaxHealth;
        copy.playTime = this.playTime;
        return copy;
    }
    
    @Override
    public String toString() {
        return String.format("GameState{name='%s', village='%s', pos=(%.1f,%.1f), level=%d, time=%.1fs}", 
                saveName, villageName, playerX, playerY, playerLevel, playTime);
    }
}