# Algoritmos de Generación de Mapas Roguelike

## Introducción

Los juegos Roguelike se caracterizan por su generación procedural de contenido, especialmente en la creación de mapas y dungeons. Este documento explica el algoritmo de Autómatas Celulares utilizado para generar mapas aleatorios en juegos Roguelike, con ejemplos específicos para el proyecto **Obsidian Arcane**.

## Contexto del Proyecto

**Obsidian Arcane** es un Roguelike Dungeon Crawler 2D que utiliza:
- Generación aleatoria de salas con orden variable
- Sistema de oleadas y jefes por sala
- Progresión basada en ítems y cartas
- Permadeath parcial (reinicio de sala)

---

## 1. Algoritmo de Autómatas Celulares

### Descripción
Genera mapas orgánicos usando reglas simples aplicadas a una grilla de celdas.

### Representación Visual del Proceso

```
Iteración 0 (Inicial - 45% densidad):
████░░██░░████░░██
░░██████░░░░██░░░░
██░░░░████░░░░████
░░████░░░░██████░░
████░░██░░████░░██

Iteración 1 (Aplicar reglas):
████░░░░░░████░░██
░░██████░░░░░░░░░░
██░░░░████░░░░████
░░████░░░░██████░░
████░░░░░░████░░██

Iteración 3 (Resultado final):
████░░░░░░░░░░░░██
░░░░░░░░░░░░░░░░░░
██░░░░░░░░░░░░████
░░░░░░░░░░░░░░░░░░
████░░░░░░░░░░░░██

Leyenda:
█ = Pared
░ = Suelo
```

### Proceso Paso a Paso

#### Paso 1: Inicialización
```
┌─────────────────────────────────┐
│ PARA cada celda (x, y):         │
│                                 │
│ ┌─────────────────────────────┐ │
│ │ ¿ALEATORIO() < 0.45?        │ │
│ └─────────┬───────────────────┘ │
│           │                     │
│    SÍ     │     NO              │
│     ┌─────▼─────┐ ┌─────────────┐│
│     │   PARED   │ │    SUELO    ││
│     └───────────┘ └─────────────┘│
└─────────────────────────────────┘
```

#### Paso 2: Reglas de Evolución
```
Para cada celda, contar vecinos tipo PARED:

┌───┬───┬───┐
│ ? │ ? │ ? │  Si vecinos_pared >= 4:
├───┼───┼───┤      celda = PARED
│ ? │ X │ ? │  Sino:
├───┼───┼───┤      celda = SUELO  
│ ? │ ? │ ? │
└───┴───┴───┘

Ejemplo de conteo:
┌───┬───┬───┐
│ █ │ █ │ ░ │  Vecinos pared = 5
├───┼───┼───┤  Resultado: PARED
│ █ │ X │ ░ │
├───┼───┼───┤
│ █ │ █ │ ░ │
└───┴───┴───┘
```

### Funcionamiento

#### Paso 1: Inicialización
```pseudocode
PARA cada celda (x, y) en grilla:
    SI ALEATORIO() < densidad_inicial:
        celda[x][y] = PARED
    SINO:
        celda[x][y] = SUELO
```

#### Paso 2: Iteraciones
```pseudocode
REPETIR iteraciones_maximas VECES:
    nueva_grilla = COPIAR(grilla_actual)
    
    PARA cada celda (x, y):
        vecinos_pared = CONTAR_VECINOS_PARED(x, y)
        
        SI vecinos_pared >= umbral_pared:
            nueva_grilla[x][y] = PARED
        SINO:
            nueva_grilla[x][y] = SUELO
    
    grilla_actual = nueva_grilla
```

### Parámetros Típicos
- **Densidad inicial**: 45-55%
- **Umbral de pared**: 4-5 vecinos
- **Iteraciones**: 3-7

### Ventajas
- Genera mapas con apariencia orgánica y natural
- Algoritmo simple y eficiente
- Fácil de ajustar mediante parámetros
- Crea cuevas y espacios interconectados realistas

### Desventajas
- Puede generar áreas desconectadas
- Menos control sobre la estructura específica del mapa
- Requiere post-procesamiento para garantizar conectividad

### Implementación en Java

```java
public class CellularAutomataGenerator {
    private int width, height;
    private float initialDensity = 0.45f;
    private int wallThreshold = 4;
    private int iterations = 5;
    
    public int[][] generateMap() {
        int[][] map = initializeMap();
        
        for (int i = 0; i < iterations; i++) {
            map = applyAutomataRules(map);
        }
        
        return map;
    }
    
    private int[][] initializeMap() {
        int[][] map = new int[width][height];
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (Math.random() < initialDensity) {
                    map[x][y] = 1; // Pared
                } else {
                    map[x][y] = 0; // Suelo
                }
            }
        }
        
        return map;
    }
    
    private int[][] applyAutomataRules(int[][] currentMap) {
        int[][] newMap = new int[width][height];
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int wallNeighbors = countWallNeighbors(currentMap, x, y);
                newMap[x][y] = (wallNeighbors >= wallThreshold) ? 1 : 0;
            }
        }
        
        return newMap;
    }
    
    private int countWallNeighbors(int[][] map, int x, int y) {
        int count = 0;
        
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue; // Skip center cell
                
                int neighborX = x + i;
                int neighborY = y + j;
                
                // Consider out-of-bounds as walls
                if (neighborX < 0 || neighborX >= width || 
                    neighborY < 0 || neighborY >= height) {
                    count++;
                } else if (map[neighborX][neighborY] == 1) {
                    count++;
                }
            }
        }
        
        return count;
    }
}
```

### Aplicación en Obsidian Arcane

Para integrar este algoritmo en **Obsidian Arcane**, se podría usar para generar:

1. **Salas de cuevas naturales**: Espacios orgánicos que contrasten con salas más estructuradas
2. **Áreas secretas**: Zonas ocultas con formas irregulares
3. **Variaciones de salas existentes**: Modificar salas predefinidas con elementos orgánicos

### Consideraciones de Implementación

- **Post-procesamiento**: Verificar conectividad y agregar pasillos si es necesario
- **Balanceo**: Ajustar parámetros según el tipo de experiencia deseada
- **Optimización**: Cachear resultados para mejorar rendimiento
- **Integración**: Combinar con el sistema actual de salas predefinidas

---

## Conclusión

El algoritmo de Autómatas Celulares es una excelente opción para generar mapas con apariencia natural y orgánica. Aunque requiere consideraciones adicionales para garantizar la conectividad, su simplicidad y flexibilidad lo convierten en una herramienta valiosa para la generación procedural de contenido en juegos Roguelike como **Obsidian Arcane**.