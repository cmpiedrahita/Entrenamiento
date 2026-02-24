package com.stockmarket.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Cliente Java para probar concurrencia del servidor
 * 
 * CONCEPTOS APLICADOS:
 * - Concurrencia: Múltiples hilos ejecutando simultáneamente
 * - Thread Pool: Reutiliza hilos en vez de crear nuevos
 * - Lambdas: Expresiones lambda para definir tareas
 * - Callable/Runnable: Tareas que se ejecutan en hilos
 * 
 * OBJETIVO:
 * - Verificar que el cache funciona correctamente bajo carga
 * - Probar que el servidor maneja múltiples peticiones simultáneas
 * - Medir diferencia de tiempo entre cache HIT y MISS
 * 
 * RESULTADOS ESPERADOS:
 * - Primera petición de cada símbolo: 500-1000ms (llama API)
 * - Siguientes peticiones: 10-50ms (desde cache)
 * 
 * ============================================
 * MODIFICAR SI:
 * - Te piden cambiar número de hilos
 * - Necesitas probar otros endpoints
 * - Te piden estadísticas más detalladas
 * ============================================
 */
public class ConcurrentTestClient {
    
    // ============================================
    // CONFIGURACIÓN - MODIFICAR SEGÚN NECESITES
    // ============================================
    
    /** URL base del servidor backend */
    private static final String BASE_URL = "http://localhost:8080/api/stocks";
    // ⚠️ MODIFICAR si despliegas en AWS/Azure
    
    /** Número de hilos en el pool */
    private static final int THREAD_POOL_SIZE = 20;
    // ⚠️ MODIFICAR si te piden más o menos hilos
    
    /** Número total de peticiones a realizar */
    private static final int TOTAL_REQUESTS = 20;
    // ⚠️ MODIFICAR según requisitos
    
    /** Símbolos de acciones a probar */
    private static final String[] SYMBOLS = {"IBM", "MSFT", "AAPL"};
    // ⚠️ MODIFICAR para probar otros símbolos
    
    /** Intervalos a probar */
    private static final String[] INTERVALS = {"daily", "weekly", "monthly"};
    // ⚠️ MODIFICAR para probar otros intervalos
    
    /**
     * Método principal que ejecuta las pruebas de concurrencia
     * 
     * FLUJO:
     * 1. Crear pool de hilos
     * 2. Enviar múltiples tareas al pool
     * 3. Cada tarea hace una petición HTTP
     * 4. Medir tiempo de respuesta
     * 5. Cerrar pool y esperar terminación
     * 
     * @param args Argumentos de línea de comandos (no usados)
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║  CLIENTE DE PRUEBAS CONCURRENTES - STOCK MARKET API  ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("📊 Configuración:");
        System.out.println("   - URL: " + BASE_URL);
        System.out.println("   - Hilos: " + THREAD_POOL_SIZE);
        System.out.println("   - Peticiones: " + TOTAL_REQUESTS);
        System.out.println("   - Símbolos: " + String.join(", ", SYMBOLS));
        System.out.println();
        System.out.println("🚀 Iniciando pruebas...");
        System.out.println("─────────────────────────────────────────────────────────");
        
        // CONCEPTO: Thread Pool
        // En vez de crear 20 hilos nuevos (costoso), reutilizamos un pool de hilos
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        
        long startTime = System.currentTimeMillis();
        
        // Enviar múltiples tareas al pool
        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            final int requestNumber = i + 1;
            
            // Rotar entre símbolos (IBM, MSFT, AAPL, IBM, MSFT, ...)
            final String symbol = SYMBOLS[i % SYMBOLS.length];
            
            // Rotar entre intervalos
            final String interval = INTERVALS[i % INTERVALS.length];
            
            // CONCEPTO: Lambda
            // Expresión lambda que define la tarea a ejecutar
            executor.submit(() -> {
                makeRequest(requestNumber, symbol, interval);
            });
        }
        
        // Cerrar el pool (no acepta más tareas)
        executor.shutdown();
        
        try {
            // Esperar hasta que todas las tareas terminen (máximo 2 minutos)
            boolean finished = executor.awaitTermination(2, TimeUnit.MINUTES);
            
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            
            System.out.println("─────────────────────────────────────────────────────────");
            if (finished) {
                System.out.println("✅ Todas las peticiones completadas");
            } else {
                System.out.println("⚠️ Timeout: Algunas peticiones no terminaron");
            }
            System.out.println("⏱️  Tiempo total: " + totalTime + "ms");
            System.out.println("📈 Promedio por petición: " + (totalTime / TOTAL_REQUESTS) + "ms");
            System.out.println();
            System.out.println("💡 ANÁLISIS:");
            System.out.println("   - Primeras peticiones de cada símbolo: LENTAS (cache MISS)");
            System.out.println("   - Siguientes peticiones: RÁPIDAS (cache HIT)");
            System.out.println("   - Esto demuestra que el cache funciona correctamente");
            
        } catch (InterruptedException e) {
            System.err.println("❌ Error: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Realiza una petición HTTP al servidor y mide el tiempo
     * 
     * CONCEPTO: HTTP Client básico
     * - Usa HttpURLConnection (incluido en Java)
     * - Mide tiempo de respuesta
     * - Imprime resultado
     * 
     * @param requestNumber Número de la petición (para logging)
     * @param symbol Símbolo de la acción
     * @param interval Intervalo (daily, weekly, monthly)
     */
    private static void makeRequest(int requestNumber, String symbol, String interval) {
        String urlString = BASE_URL + "/" + symbol + "/" + interval;
        
        try {
            // Medir tiempo de inicio
            long startTime = System.currentTimeMillis();
            
            // Crear conexión HTTP
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);  // Timeout de conexión: 5 segundos
            connection.setReadTimeout(10000);    // Timeout de lectura: 10 segundos
            
            // Obtener código de respuesta
            int responseCode = connection.getResponseCode();
            
            // Leer respuesta (opcional, pero simula uso real)
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
            );
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            // Medir tiempo de fin
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Determinar si fue cache HIT o MISS basado en tiempo
            String cacheStatus = duration < 100 ? "🟢 HIT " : "🔴 MISS";
            
            // Imprimir resultado
            System.out.printf("Petición #%02d | %s | %s | %d | %4dms | %s%n",
                requestNumber,
                symbol,
                interval,
                responseCode,
                duration,
                cacheStatus
            );
            
        } catch (Exception e) {
            System.err.printf("❌ Petición #%02d | %s | %s | ERROR: %s%n",
                requestNumber,
                symbol,
                interval,
                e.getMessage()
            );
        }
    }
    
    /* ============================================
     * EXTENSIONES OPCIONALES
     * ============================================
     * 
     * Si te piden más funcionalidades:
     * 
     * 1. ESTADÍSTICAS DETALLADAS:
     * 
     * private static int cacheHits = 0;
     * private static int cacheMisses = 0;
     * 
     * // En makeRequest():
     * if (duration < 100) {
     *     cacheHits++;
     * } else {
     *     cacheMisses++;
     * }
     * 
     * // Al final:
     * System.out.println("Cache Hits: " + cacheHits);
     * System.out.println("Cache Misses: " + cacheMisses);
     * 
     * 
     * 2. PROBAR DIFERENTES ENDPOINTS:
     * 
     * private static void testCompareEndpoint() {
     *     String url = BASE_URL + "/compare?symbol1=IBM&symbol2=MSFT";
     *     // Hacer petición...
     * }
     * 
     * 
     * 3. USAR Callable PARA RETORNAR RESULTADOS:
     * 
     * Callable<Long> task = () -> {
     *     long start = System.currentTimeMillis();
     *     makeRequest(...);
     *     return System.currentTimeMillis() - start;
     * };
     * 
     * Future<Long> future = executor.submit(task);
     * Long duration = future.get();  // Obtener resultado
     */
}
