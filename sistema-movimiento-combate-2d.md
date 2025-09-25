# Sistema de Movimiento y Combate 2D - Obsidian Arcane

## Introducción

Este documento describe el sistema de movimiento y combate para **Obsidian Arcane**, un Roguelike Dungeon Crawler 2D. El sistema implementa controles fluidos y responsivos con movimiento WASD y un sistema de combate direccional que permite ataques precisos en todas las direcciones.

## Contexto del Proyecto

**Obsidian Arcane** es un juego 2D desarrollado en Java con LibGDX que requiere:
- Movimiento fluido en 2D (horizontal y vertical)
- Sistema de combate direccional intuitivo
- Controles responsivos para gameplay rápido

---

## 1. Sistema de Movimiento WASD

### Descripción General

El sistema de movimiento utiliza las teclas **WASD** para controlar al personaje en un espacio 2D, permitiendo movimiento en 8 direcciones con física suave y responsiva.

### Controles de Movimiento

```
W - Movimiento hacia arriba
A - Movimiento hacia la izquierda  
S - Movimiento hacia abajo
D - Movimiento hacia la derecha
```

### Mecánicas de Movimiento

#### Movimiento Básico
- **Velocidad Base**: 200 píxeles por segundo
- **Aceleración**: Instantánea (estilo arcade)
- **Desaceleración**: Suave con fricción
- **Movimiento Diagonal**: Velocidad normalizada para mantener consistencia

#### Representación Visual del Movimiento

```
    ↑ W
    │
A ← ● → D
    │
    ↓ S

Direcciones Diagonales:
W+A = ↖  W+D = ↗
S+A = ↙  S+D = ↘
```

### Pseudocódigo del Sistema de Movimiento

```java
// Variables de movimiento
Vector2 velocity = new Vector2(0, 0);
Vector2 position = new Vector2(x, y);
float speed = 200f;
float friction = 0.85f;

// Actualización del movimiento
public void updateMovement(float deltaTime) {
    Vector2 inputDirection = new Vector2(0, 0);
    
    // Detectar entrada WASD
    if (Gdx.input.isKeyPressed(Keys.W)) inputDirection.y += 1;
    if (Gdx.input.isKeyPressed(Keys.S)) inputDirection.y -= 1;
    if (Gdx.input.isKeyPressed(Keys.A)) inputDirection.x -= 1;
    if (Gdx.input.isKeyPressed(Keys.D)) inputDirection.x += 1;
    
    // Normalizar para movimiento diagonal consistente
    if (inputDirection.len() > 0) {
        inputDirection.nor();
        velocity.set(inputDirection.scl(speed));
    } else {
        // Aplicar fricción cuando no hay entrada
        velocity.scl(friction);
    }
    
    // Actualizar posición
    position.add(velocity.x * deltaTime, velocity.y * deltaTime);
}
```

### Diagrama de Flujo del Movimiento

```
┌─────────────────┐
│   Entrada WASD  │
└─────────┬───────┘
          │
          ▼
┌─────────────────┐
│ ¿Hay entrada?   │
└─────┬─────┬─────┘
      │ Sí  │ No
      ▼     ▼
┌──────────┐ ┌──────────┐
│Normalizar│ │ Aplicar  │
│Dirección │ │Fricción  │
└─────┬────┘ └─────┬────┘
      │            │
      ▼            ▼
┌─────────────────────┐
│  Actualizar Posición │
└─────────────────────┘
```

---

## 2. Sistema de Combate Direccional

### Descripción General

El sistema de combate permite ataques direccionales combinando la tecla de ataque **K** con las teclas de movimiento WASD, creando un sistema intuitivo y preciso inspirado en Hollow Knight.

### Controles de Combate

```
K           - Ataque básico (dirección del último movimiento)
W + K       - Ataque hacia arriba
A + K       - Ataque hacia la izquierda
S + K       - Ataque hacia abajo
D + K       - Ataque hacia la derecha
W + A + K   - Ataque diagonal arriba-izquierda
W + D + K   - Ataque diagonal arriba-derecha
S + A + K   - Ataque diagonal abajo-izquierda
S + D + K   - Ataque diagonal abajo-derecha
```

### Mecánicas de Combate

#### Propiedades del Ataque
- **Daño Base**: 25 puntos
- **Alcance**: 64 píxeles
- **Duración**: 0.3 segundos
- **Cooldown**: 0.5 segundos
- **Área de Efecto**: Cono de 45° en la dirección del ataque

#### Representación Visual del Sistema de Combate

```
Ataques Direccionales:

    ↑ W+K
    │
A+K ← ● → D+K
    │
    ↓ S+K

Ataques Diagonales:
W+A+K = ↖  W+D+K = ↗
S+A+K = ↙  S+D+K = ↘

Área de Ataque (ejemplo hacia la derecha):
    ●───▶ ╱
         ╱
        ╱
```

### Pseudocódigo del Sistema de Combate

```java
// Variables de combate
Vector2 lastDirection = new Vector2(1, 0); // Dirección por defecto
float attackCooldown = 0f;
float attackDuration = 0.3f;
float attackRange = 64f;
boolean isAttacking = false;

public void updateCombat(float deltaTime) {
    // Actualizar cooldown
    if (attackCooldown > 0) {
        attackCooldown -= deltaTime;
    }
    
    // Detectar entrada de ataque
    if (Gdx.input.isKeyJustPressed(Keys.K) && attackCooldown <= 0) {
        Vector2 attackDirection = getAttackDirection();
        performAttack(attackDirection);
        attackCooldown = 0.5f; // Reiniciar cooldown
    }
}

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

private void performAttack(Vector2 direction) {
    // Crear hitbox del ataque
    Rectangle attackHitbox = new Rectangle(
        position.x + direction.x * 32,
        position.y + direction.y * 32,
        attackRange,
        attackRange
    );
    
    // Detectar enemigos en el área
    for (Enemy enemy : enemies) {
        if (attackHitbox.overlaps(enemy.getBounds())) {
            enemy.takeDamage(25);
        }
    }
    
    isAttacking = true;
    // Iniciar animación de ataque
}
```

### Diagrama de Flujo del Combate

```
┌─────────────────┐
│ Presionar K     │
└─────────┬───────┘
          │
          ▼
┌─────────────────┐
│ ¿Cooldown = 0?  │
└─────┬─────┬─────┘
      │ Sí  │ No
      ▼     ▼
┌──────────┐ ┌──────────┐
│Detectar  │ │ Ignorar  │
│Dirección │ │ Entrada  │
└─────┬────┘ └──────────┘
      │
      ▼
┌─────────────────┐
│ ¿Hay dirección? │
└─────┬─────┬─────┘
      │ Sí  │ No
      ▼     ▼
┌──────────┐ ┌──────────┐
│ Usar     │ │ Usar     │
│ Nueva    │ │ Última   │
│Dirección │ │Dirección │
└─────┬────┘ └─────┬────┘
      │            │
      ▼            ▼
┌─────────────────────┐
│   Ejecutar Ataque   │
└─────────────────────┘
```

---

## 3. Integración del Sistema Completo

### Clase Principal del Jugador

```java
public class Player {
    // Propiedades del jugador
    private Vector2 position;
    private Vector2 velocity;
    private Vector2 lastDirection;
    
    // Configuración de movimiento
    private float speed = 200f;
    private float friction = 0.85f;
    
    // Configuración de combate
    private float attackCooldown = 0f;
    private float attackRange = 64f;
    private int attackDamage = 25;
    private boolean isAttacking = false;
    
    public void update(float deltaTime) {
        updateMovement(deltaTime);
        updateCombat(deltaTime);
        updateAnimations(deltaTime);
    }
    
    private void updateMovement(float deltaTime) {
        // Implementación del movimiento WASD
    }
    
    private void updateCombat(float deltaTime) {
        // Implementación del combate direccional
    }
}
```

### Estados del Jugador

```
Estados Posibles:
┌─────────┐    ┌─────────┐    ┌─────────┐
│  IDLE   │───▶│ MOVING  │───▶│ATTACKING│
└─────────┘    └─────────┘    └─────────┘
     ▲              │              │
     └──────────────┴──────────────┘
```

### Ejemplo de Implementación en LibGDX

```java
public class GameplayScreen implements Screen {
    private Player player;
    private InputMultiplexer inputProcessor;
    
    @Override
    public void create() {
        player = new Player();
        inputProcessor = new InputMultiplexer();
        
        // Configurar procesador de entrada
        inputProcessor.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                return player.handleKeyDown(keycode);
            }
            
            @Override
            public boolean keyUp(int keycode) {
                return player.handleKeyUp(keycode);
            }
        });
        
        Gdx.input.setInputProcessor(inputProcessor);
    }
    
    @Override
    public void render(float delta) {
        player.update(delta);
        
        // Renderizar jugador y efectos
        batch.begin();
        player.render(batch);
        batch.end();
    }
}
```

---

## 4. Consideraciones de Implementación

### Optimización
- **Pool de Objetos**: Reutilizar objetos Vector2 para evitar garbage collection
- **Spatial Hashing**: Para detección eficiente de colisiones
- **Interpolación**: Suavizar movimiento entre frames

### Balanceo
- **Velocidad de Movimiento**: Ajustable según el tipo de sala
- **Cooldown de Ataque**: Balanceado para gameplay fluido
- **Alcance de Ataque**: Proporcional al tamaño de los enemigos

### Extensibilidad
- **Sistema de Mejoras**: Modificadores de velocidad y daño
- **Ataques Especiales**: Combos y habilidades desbloqueables
- **Animaciones**: Sistema flexible para diferentes tipos de ataque

---

## 5. Conclusión

Este sistema de movimiento y combate proporciona una base sólida para **Obsidian Arcane**, combinando la fluidez del movimiento WASD con un sistema de combate direccional intuitivo. La inspiración en Hollow Knight asegura controles responsivos y satisfactorios que complementan perfectamente el género roguelike del juego.

El diseño modular permite futuras expansiones y mejoras, manteniendo la simplicidad en los controles básicos mientras ofrece profundidad táctica a través del combate direccional.