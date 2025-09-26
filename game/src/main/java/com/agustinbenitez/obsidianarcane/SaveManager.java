package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Maneja el guardado y carga de partidas con 4 slots disponibles
 */
public class SaveManager {
    private static final String SAVE_DIRECTORY = "saves/";
    private static final String SAVE_FILE_PREFIX = "slot_";
    private static final String SAVE_FILE_EXTENSION = ".sav";
    private static final int MAX_SAVE_SLOTS = 4;
    
    private static SaveManager instance;
    private Map<Integer, GameState> loadedSaves;
    
    private SaveManager() {
        loadedSaves = new HashMap<>();
        ensureSaveDirectoryExists();
        loadAllSaves();
    }
    
    public static SaveManager getInstance() {
        if (instance == null) {
            instance = new SaveManager();
        }
        return instance;
    }
    
    /**
     * Asegura que el directorio de guardado existe
     */
    private void ensureSaveDirectoryExists() {
        FileHandle saveDir = Gdx.files.local(SAVE_DIRECTORY);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
            Gdx.app.log("SaveManager", "Directorio de guardado creado: " + saveDir.path());
        }
    }
    
    /**
     * Guarda una partida en el slot especificado
     */
    public boolean saveGame(int slot, GameState gameState) {
        if (slot < 1 || slot > MAX_SAVE_SLOTS) {
            Gdx.app.error("SaveManager", "Invalid slot: " + slot + ". Must be between 1 and " + MAX_SAVE_SLOTS);
            return false;
        }
        
        try {
            gameState.updateSaveDate();
            String fileName = SAVE_DIRECTORY + SAVE_FILE_PREFIX + slot + SAVE_FILE_EXTENSION;
            FileHandle file = Gdx.files.local(fileName);
            
            // Serializar el estado del juego
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(gameState);
            oos.close();
            
            // Escribir al archivo
            file.writeBytes(baos.toByteArray(), false);
            
            // Actualizar cache
            loadedSaves.put(slot, gameState.copy());
            
            Gdx.app.log("SaveManager", "Partida guardada en slot " + slot + ": " + gameState.getSaveName());
            return true;
            
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Error al guardar partida en slot " + slot, e);
            return false;
        }
    }
    
    /**
     * Carga una partida del slot especificado
     */
    public GameState loadGame(int slot) {
        if (slot < 1 || slot > MAX_SAVE_SLOTS) {
            Gdx.app.error("SaveManager", "Invalid slot: " + slot + ". Must be between 1 and " + MAX_SAVE_SLOTS);
            return null;
        }
        
        // Primero verificar el cache
        if (loadedSaves.containsKey(slot)) {
            return loadedSaves.get(slot).copy();
        }
        
        try {
            String fileName = SAVE_DIRECTORY + SAVE_FILE_PREFIX + slot + SAVE_FILE_EXTENSION;
            FileHandle file = Gdx.files.local(fileName);
            
            if (!file.exists()) {
                Gdx.app.log("SaveManager", "No existe partida guardada en slot " + slot);
                return null;
            }
            
            // Leer y deserializar
            byte[] data = file.readBytes();
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bais);
            GameState gameState = (GameState) ois.readObject();
            ois.close();
            
            // Actualizar cache
            loadedSaves.put(slot, gameState.copy());
            
            Gdx.app.log("SaveManager", "Partida cargada del slot " + slot + ": " + gameState.getSaveName());
            return gameState.copy();
            
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Error al cargar partida del slot " + slot, e);
            return null;
        }
    }
    
    /**
     * Elimina una partida guardada del slot especificado
     */
    public boolean deleteSave(int slot) {
        if (slot < 1 || slot > MAX_SAVE_SLOTS) {
            Gdx.app.error("SaveManager", "Invalid slot: " + slot + ". Must be between 1 and " + MAX_SAVE_SLOTS);
            return false;
        }
        
        try {
            String fileName = SAVE_DIRECTORY + SAVE_FILE_PREFIX + slot + SAVE_FILE_EXTENSION;
            FileHandle file = Gdx.files.local(fileName);
            
            if (file.exists()) {
                file.delete();
                loadedSaves.remove(slot);
                Gdx.app.log("SaveManager", "Partida eliminada del slot " + slot);
                return true;
            } else {
                Gdx.app.log("SaveManager", "No hay partida para eliminar en slot " + slot);
                return false;
            }
            
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Error al eliminar partida del slot " + slot, e);
            return false;
        }
    }
    
    /**
     * Verifica si existe una partida guardada en el slot especificado
     */
    public boolean hasSave(int slot) {
        if (slot < 1 || slot > MAX_SAVE_SLOTS) {
            return false;
        }
        
        if (loadedSaves.containsKey(slot)) {
            return true;
        }
        
        String fileName = SAVE_DIRECTORY + SAVE_FILE_PREFIX + slot + SAVE_FILE_EXTENSION;
        FileHandle file = Gdx.files.local(fileName);
        return file.exists();
    }
    
    /**
     * Obtiene información básica de una partida guardada sin cargarla completamente
     */
    public GameState getSaveInfo(int slot) {
        if (loadedSaves.containsKey(slot)) {
            return loadedSaves.get(slot).copy();
        }
        
        return loadGame(slot); // Esto la cargará y la pondrá en cache
    }
    
    /**
     * Carga todas las partidas guardadas en memoria para acceso rápido
     */
    private void loadAllSaves() {
        for (int slot = 1; slot <= MAX_SAVE_SLOTS; slot++) {
            if (hasSave(slot)) {
                loadGame(slot); // Esto las pondrá en cache
            }
        }
    }
    
    /**
     * Obtiene el número máximo de slots disponibles
     */
    public int getMaxSaveSlots() {
        return MAX_SAVE_SLOTS;
    }
    
    /**
     * Obtiene un array con información de todos los slots
     */
    public GameState[] getAllSaveInfo() {
        GameState[] saves = new GameState[MAX_SAVE_SLOTS];
        for (int i = 0; i < MAX_SAVE_SLOTS; i++) {
            int slot = i + 1;
            if (hasSave(slot)) {
                saves[i] = getSaveInfo(slot);
            }
        }
        return saves;
    }
    
    /**
     * Limpia el cache de partidas cargadas
     */
    public void clearCache() {
        loadedSaves.clear();
    }
}