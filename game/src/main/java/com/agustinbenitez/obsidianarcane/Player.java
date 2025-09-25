package com.agustinbenitez.obsidianarcane;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;

/**
 * Clase Player para Obsidian Arcane
 * Implementa sistema de movimiento WASD y combate direccional
 * Con sistema de hitboxes precisas y colisiones con el mapa de salas
 */
public class Player {
    
    // Propiedades del jugador
    private Vector2 position;
    private Vector2 velocity;
    private Vector2 lastDirection;
    private Rectangle bounds;
    private Rectangle collisionBox; // Hitbox más pequeña para colisiones precisas
    
    // Referencia al mapa para colisiones
    private GameMap gameMap;
    
    // Configuración de movimiento
    private float speed = 200f;
    private float friction = 0.85f;
    private float maxSpeed = 300f;
    
    // Configuración de combate
    private float attackCooldown = 0f;
    private float attackRange = 64f;
    private int attackDamage = 25;
    private boolean isAttacking = false;
    private float attackDuration = 0.3f;
    private float attackTimer = 0f;
    
    // Estados del jugador
    public enum PlayerState {
        IDLE, MOVING, ATTACKING
    }
    private PlayerState currentState = PlayerState.IDLE;
    
    // Renderizado
    private ShapeRenderer shapeRenderer;
    
    // Configuración de hitboxes
    private static final float PLAYER_WIDTH = 24f;
    private static final float PLAYER_HEIGHT = 24f;
    private static final float COLLISION_MARGIN = 4f; // Margen para colisiones más suaves
    
    /**
     * Constructor del Player
     * @param x Posición inicial X
     * @param y Posición inicial Y
     * @param gameMap Referencia al mapa del juego para colisiones
     */
    public Player(float x, float y, GameMap gameMap) {
        position = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        lastDirection = new Vector2(1, 0); // Dirección por defecto hacia la derecha
        
        // Hitbox visual (más grande)
        bounds = new Rectangle(x - PLAYER_WIDTH/2, y - PLAYER_HEIGHT/2, PLAYER_WIDTH, PLAYER_HEIGHT);
        
        // Hitbox de colisión (más pequeña para movimiento más fluido)
        float collisionWidth = PLAYER_WIDTH - COLLISION_MARGIN * 2;
        float collisionHeight = PLAYER_HEIGHT - COLLISION_MARGIN * 2;
        collisionBox = new Rectangle(
            x - collisionWidth/2, 
            y - collisionHeight/2, 
            collisionWidth, 
            collisionHeight
        );
        
        this.gameMap = gameMap;
        shapeRenderer = new ShapeRenderer();
    }
    
    /**
     * Actualización principal del jugador
     * @param deltaTime Tiempo transcurrido desde la última actualización
     */
    public void update(float deltaTime) {
        updateMovement(deltaTime);
        updateCombat(deltaTime);
        updateState();
        updateBounds();
    }
    
    /**
     * Sistema de movimiento WASD con colisiones precisas
     * @param deltaTime Tiempo delta
     */
    /**
     * Sistema de movimiento lateral con físicas (gravedad y salto)
     * @param deltaTime Tiempo delta
     */
    private void updateMovement(float deltaTime) {
        // Detectar entrada WASD
        float inputX = 0;
        float inputY = 0;
        
        if (Gdx.input.isKeyPressed(Keys.W)) {
            inputY = 1;
            lastDirection.set(0, 1);
        }
        if (Gdx.input.isKeyPressed(Keys.S)) {
            inputY = -1;
            lastDirection.set(0, -1);
        }
        if (Gdx.input.isKeyPressed(Keys.A)) {
            inputX = -1;
            lastDirection.set(-1, 0);
        }
        if (Gdx.input.isKeyPressed(Keys.D)) {
            inputX = 1;
            lastDirection.set(1, 0);
        }
        
        // Aplicar movimiento
        if (inputX != 0 || inputY != 0) {
            velocity.x = inputX * speed;
            velocity.y = inputY * speed;
        } else {
            // Aplicar fricción cuando no hay entrada
            velocity.x *= friction;
            velocity.y *= friction;
        }
        
        // Limitar velocidad máxima
        if (velocity.len() > maxSpeed) {
            velocity.nor().scl(maxSpeed);
        }
        
        // Aplicar movimiento con detección de colisiones
        applyMovementWithCollision(deltaTime);
    }
    
     /**
      * Aplica el movimiento verificando colisiones en ambos ejes por separado
      * @param deltaTime Tiempo delta
      */
     private void applyMovementWithCollision(float deltaTime) {
         Vector2 oldPosition = position.cpy();
         
         // Movimiento en X
         float newX = position.x + velocity.x * deltaTime;
         position.x = newX;
         updateCollisionBox();
         
         if (checkCollisionWithMap()) {
             position.x = oldPosition.x; // Revertir movimiento en X
             velocity.x = 0; // Detener velocidad en X
         }
         
         // Movimiento en Y
         float newY = position.y + velocity.y * deltaTime;
         position.y = newY;
         updateCollisionBox();
         
         if (checkCollisionWithMap()) {
             position.y = oldPosition.y; // Revertir movimiento en Y
             velocity.y = 0; // Detener velocidad en Y
         }
     }
    
    /**
     * Verifica colisiones con el mapa usando múltiples puntos de la hitbox
     * @return true si hay colisión
     */
    private boolean checkCollisionWithMap() {
        if (gameMap == null) return false;
        
        // Verificar múltiples puntos de la hitbox de colisión
        float left = collisionBox.x;
        float right = collisionBox.x + collisionBox.width;
        float bottom = collisionBox.y;
        float top = collisionBox.y + collisionBox.height;
        float centerX = collisionBox.x + collisionBox.width / 2;
        float centerY = collisionBox.y + collisionBox.height / 2;
        
        // Verificar esquinas y puntos medios
        return gameMap.isCollision(left, bottom) ||     // Esquina inferior izquierda
               gameMap.isCollision(right, bottom) ||    // Esquina inferior derecha
               gameMap.isCollision(left, top) ||        // Esquina superior izquierda
               gameMap.isCollision(right, top) ||       // Esquina superior derecha
               gameMap.isCollision(centerX, bottom) ||  // Punto medio inferior
               gameMap.isCollision(centerX, top) ||     // Punto medio superior
               gameMap.isCollision(left, centerY) ||    // Punto medio izquierdo
               gameMap.isCollision(right, centerY) ||   // Punto medio derecho
               gameMap.isCollision(centerX, centerY);   // Centro
    }
    
    /**
     * Actualiza la posición de la hitbox de colisión
     */
    private void updateCollisionBox() {
        collisionBox.setCenter(position.x, position.y);
    }
    
    /**
     * Sistema de combate direccional
     * @param deltaTime Tiempo delta
     */
    private void updateCombat(float deltaTime) {
        // Actualizar cooldown
        if (attackCooldown > 0) {
            attackCooldown -= deltaTime;
        }
        
        // Actualizar timer de ataque
        if (isAttacking) {
            attackTimer -= deltaTime;
            if (attackTimer <= 0) {
                isAttacking = false;
            }
        }
        
        // Detectar entrada de ataque
        if (Gdx.input.isKeyJustPressed(Keys.K) && attackCooldown <= 0) {
            Vector2 attackDirection = getAttackDirection();
            performAttack(attackDirection);
            attackCooldown = 0.5f; // Reiniciar cooldown
        }
    }
    
    /**
     * Obtiene la dirección del ataque basada en la entrada
     * @return Vector2 con la dirección del ataque
     */
    private Vector2 getAttackDirection() {
        Vector2 direction = new Vector2(0, 0);
        
        // Detectar dirección del ataque
        if (Gdx.input.isKeyPressed(Keys.W)) direction.y += 1;
        if (Gdx.input.isKeyPressed(Keys.S)) direction.y -= 1;
        if (Gdx.input.isKeyPressed(Keys.A)) direction.x -= 1;
        if (Gdx.input.isKeyPressed(Keys.D)) direction.x += 1;
        
        // Si no hay dirección específica, usar la última dirección
        if (direction.len() == 0) {
            return lastDirection.cpy();
        }
        
        // Normalizar y guardar como última dirección
        direction.nor();
        lastDirection.set(direction);
        return direction;
    }
    
    /**
     * Ejecuta un ataque en la dirección especificada
     * @param direction Dirección del ataque
     */
    private void performAttack(Vector2 direction) {
        // Crear hitbox del ataque centrada en el jugador
        Rectangle attackHitbox = new Rectangle(
            position.x + direction.x * (PLAYER_WIDTH/2 + 8) - attackRange/2,
            position.y + direction.y * (PLAYER_HEIGHT/2 + 8) - attackRange/2,
            attackRange,
            attackRange
        );
        
        // TODO: Detectar enemigos en el área
        // for (Enemy enemy : enemies) {
        //     if (attackHitbox.overlaps(enemy.getBounds())) {
        //         enemy.takeDamage(attackDamage);
        //     }
        // }
        
        isAttacking = true;
        attackTimer = attackDuration;
        
        // Log del ataque para debug
        Gdx.app.log("Player", "Ataque ejecutado en dirección: " + direction.toString());
    }
    
    /**
     * Actualiza el estado del jugador
     */
    private void updateState() {
        if (isAttacking) {
            currentState = PlayerState.ATTACKING;
        } else if (velocity.len() > 10f) { // Umbral mínimo para considerar movimiento
            currentState = PlayerState.MOVING;
        } else {
            currentState = PlayerState.IDLE;
        }
    }
    
    /**
     * Actualiza los bounds del jugador
     */
    private void updateBounds() {
        bounds.setCenter(position.x, position.y);
        updateCollisionBox();
    }
    
    /**
     * Renderiza el jugador
     * @param batch SpriteBatch para renderizado
     */
    public void render(SpriteBatch batch) {
        // Finalizar el batch para usar ShapeRenderer
        batch.end();
        
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Renderizar jugador como rectángulo
        switch (currentState) {
            case IDLE:
                shapeRenderer.setColor(Color.BLUE);
                break;
            case MOVING:
                shapeRenderer.setColor(Color.GREEN);
                break;
            case ATTACKING:
                shapeRenderer.setColor(Color.RED);
                break;
        }
        
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        
        // Renderizar hitbox de colisión (para debug)
        shapeRenderer.setColor(1, 1, 1, 0.3f); // Blanco transparente
        shapeRenderer.rect(collisionBox.x, collisionBox.y, collisionBox.width, collisionBox.height);
        
        // Renderizar dirección (pequeña línea)
        shapeRenderer.setColor(Color.WHITE);
        float dirX = position.x + lastDirection.x * 20;
        float dirY = position.y + lastDirection.y * 20;
        shapeRenderer.line(position.x, position.y, dirX, dirY);
        
        // Renderizar área de ataque si está atacando
        if (isAttacking) {
            shapeRenderer.setColor(1, 1, 0, 0.3f); // Amarillo transparente
            float attackX = position.x + lastDirection.x * (PLAYER_WIDTH/2 + 8) - attackRange/2;
            float attackY = position.y + lastDirection.y * (PLAYER_HEIGHT/2 + 8) - attackRange/2;
            shapeRenderer.rect(attackX, attackY, attackRange, attackRange);
        }
        
        shapeRenderer.end();
        
        // Reiniciar el batch
        batch.begin();
    }
    
    /**
     * Maneja entrada de teclado
     * @param keycode Código de la tecla
     * @return true si la entrada fue procesada
     */
    public boolean handleKeyDown(int keycode) {
        // Manejar teclas especiales si es necesario
        return false;
    }
    
    /**
     * Maneja liberación de teclas
     * @param keycode Código de la tecla
     * @return true si la entrada fue procesada
     */
    public boolean handleKeyUp(int keycode) {
        // Manejar liberación de teclas si es necesario
        return false;
    }
    
    // Getters y Setters
    public Vector2 getPosition() { return position; }
    public Vector2 getVelocity() { return velocity; }
    public Rectangle getBounds() { return bounds; }
    public Rectangle getCollisionBox() { return collisionBox; }
    public PlayerState getCurrentState() { return currentState; }
    public boolean isAttacking() { return isAttacking; }
    public float getAttackCooldown() { return attackCooldown; }
    
    public void setPosition(float x, float y) {
        position.set(x, y);
        updateBounds();
    }
    
    public void setSpeed(float speed) { this.speed = speed; }
    public void setMaxSpeed(float maxSpeed) { this.maxSpeed = maxSpeed; }
    public void setAttackDamage(int damage) { this.attackDamage = damage; }
    public void setAttackRange(float range) { this.attackRange = range; }
    public void setGameMap(GameMap gameMap) { this.gameMap = gameMap; }
    
    /**
     * Libera recursos
     */
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}