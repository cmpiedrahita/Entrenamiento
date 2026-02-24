package com.stockmarket.cache;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache thread-safe para almacenar respuestas de APIs externas
 * 
 * CONCEPTOS APLICADOS:
 * - Concurrencia: Usa ConcurrentHashMap para acceso seguro desde múltiples hilos
 * - Genéricos: ConcurrentHashMap<String, String> garantiza tipo seguro
 * - Inmutabilidad: El campo cache es final (no puede reasignarse)
 * - Patrón Cache: Evita operaciones costosas repetidas
 * 
 * ¿POR QUÉ ConcurrentHashMap Y NO HashMap?
 * - HashMap NO es thread-safe (puede corromperse con múltiples hilos)
 * - ConcurrentHashMap permite lecturas concurrentes sin bloqueo
 * - Escrituras usan lock granular (solo bloquea segmento afectado)
 * 
 * ============================================
 * MODIFICAR SI:
 * - Te piden implementar expiración de cache (TTL)
 * - Necesitas cache con tipos diferentes (usa genéricos)
 * - Te piden estadísticas (hits/misses)
 * ============================================
 */
@Component  // Spring gestiona esta clase como un bean singleton
public class ConcurrentCache {
    
    /**
     * Almacenamiento interno del cache
     * - final: No puede reasignarse (inmutabilidad)
     * - ConcurrentHashMap: Thread-safe para múltiples hilos
     * - <String, String>: Genéricos para tipo seguro
     */
    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
    
    /**
     * Obtiene un valor del cache
     * 
     * @param key Clave de búsqueda (ej: "DAILY_IBM")
     * @return Valor almacenado o null si no existe
     */
    public String get(String key) {
        return cache.get(key);
    }
    
    /**
     * Almacena un valor en el cache
     * 
     * NOTA: Este método es thread-safe gracias a ConcurrentHashMap
     * Múltiples hilos pueden llamarlo simultáneamente sin problemas
     * 
     * @param key Clave (ej: "DAILY_IBM")
     * @param value Valor a almacenar (JSON de la API)
     */
    public void put(String key, String value) {
        cache.put(key, value);
    }
    
    /**
     * Verifica si existe una clave en el cache
     * 
     * @param key Clave a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean containsKey(String key) {
        return cache.containsKey(key);
    }
    
    /**
     * Limpia todo el cache
     * 
     * ============================================
     * MODIFICAR SI:
     * Te piden agregar funcionalidad para limpiar cache
     * ============================================
     */
    public void clear() {
        cache.clear();
    }
    
    /**
     * Obtiene el tamaño del cache
     * 
     * @return Número de entradas en el cache
     */
    public int size() {
        return cache.size();
    }
    
    /* ============================================
     * EXTENSIÓN OPCIONAL: Cache con expiración (TTL)
     * ============================================
     * 
     * Si te piden implementar expiración:
     * 
     * 1. Cambiar a: ConcurrentHashMap<String, CacheEntry>
     * 2. Crear clase interna:
     * 
     * private static class CacheEntry {
     *     final String value;
     *     final long timestamp;
     *     
     *     CacheEntry(String value) {
     *         this.value = value;
     *         this.timestamp = System.currentTimeMillis();
     *     }
     *     
     *     boolean isExpired(long ttlMillis) {
     *         return System.currentTimeMillis() - timestamp > ttlMillis;
     *     }
     * }
     * 
     * 3. Modificar get() para verificar expiración
     */
}
