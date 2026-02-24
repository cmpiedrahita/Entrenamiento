package com.stockmarket.service;

import com.stockmarket.cache.ConcurrentCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Implementación del servicio usando Alpha Vantage API
 * 
 * CONCEPTOS APLICADOS:
 * - Patrón Strategy: Implementa StockService (una de varias posibles implementaciones)
 * - Dependency Injection: Spring inyecta ConcurrentCache automáticamente
 * - Programación Reactiva: Usa WebClient (no bloqueante)
 * - Patrón Cache: Evita llamadas repetidas a la API
 * 
 * FLUJO DE CADA MÉTODO:
 * 1. Verificar si datos están en cache
 * 2. Si SÍ → Retornar inmediatamente (rápido: 10-50ms)
 * 3. Si NO → Llamar API externa (lento: 500-1000ms)
 * 4. Guardar resultado en cache
 * 5. Retornar resultado
 * 
 * ============================================
 * MODIFICAR SI:
 * - Te piden usar otro proveedor (crear YahooFinanceService)
 * - Necesitas manejar errores específicos
 * - Te piden logging de peticiones
 * - Necesitas transformar el JSON antes de retornar
 * ============================================
 */
@Service  // Spring gestiona esta clase como un bean
public class AlphaVantageService implements StockService {
    
    /**
     * Cliente HTTP reactivo para llamadas a API externa
     * - WebClient es no bloqueante (mejor rendimiento que RestTemplate)
     * - final: Inmutabilidad (no puede reasignarse)
     */
    private final WebClient webClient;
    
    /**
     * Cache para evitar llamadas repetidas
     * - Inyectado automáticamente por Spring (Dependency Injection)
     */
    private final ConcurrentCache cache;
    
    /**
     * API Key de Alpha Vantage
     * - @Value inyecta valor desde application.properties
     * - "demo" es valor por defecto si no está configurado
     * 
     * ============================================
     * IMPORTANTE: Cambiar "demo" por tu API key real
     * Obtener en: https://www.alphavantage.co/support/#api-key
     * ============================================
     */
    @Value("${alphavantage.apikey:demo}")
    private String apiKey;
    
    /**
     * Constructor con inyección de dependencias
     * 
     * CONCEPTO: Dependency Injection
     * - Spring crea ConcurrentCache automáticamente
     * - Lo inyecta en este constructor
     * - No necesitas hacer "new ConcurrentCache()"
     * 
     * @param cache Cache inyectado por Spring
     */
    public AlphaVantageService(ConcurrentCache cache) {
        // Configurar WebClient con URL base de Alpha Vantage
        this.webClient = WebClient.builder()
            .baseUrl("https://www.alphavantage.co")
            .build();
        this.cache = cache;
    }
    
    /**
     * Obtiene datos intradiarios (cada 5 minutos)
     * 
     * PATRÓN CACHE:
     * 1. Crear clave única: "INTRADAY_IBM"
     * 2. Verificar si existe en cache
     * 3. Si existe → retornar (rápido)
     * 4. Si no → llamar API → guardar → retornar
     * 
     * @param symbol Símbolo de la acción (ej: "IBM")
     * @return JSON con datos de la API
     */
    @Override
    public String getIntradayData(String symbol) {
        // Paso 1: Crear clave única para el cache
        String cacheKey = "INTRADAY_" + symbol;
        
        // Paso 2: Verificar si ya está en cache
        if (cache.containsKey(cacheKey)) {
            System.out.println("✅ Cache HIT: " + cacheKey);
            return cache.get(cacheKey);
        }
        
        System.out.println("❌ Cache MISS: " + cacheKey + " - Llamando API...");
        
        // Paso 3: Llamar API externa
        String result = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/query")
                .queryParam("function", "TIME_SERIES_INTRADAY")
                .queryParam("symbol", symbol)
                .queryParam("interval", "5min")
                .queryParam("apikey", apiKey)
                .build())
            .retrieve()
            .bodyToMono(String.class)
            .block();  // Bloquea hasta recibir respuesta
        
        // Paso 4: Guardar en cache
        cache.put(cacheKey, result);
        
        // Paso 5: Retornar resultado
        return result;
    }
    
    /**
     * Obtiene datos diarios
     * 
     * ============================================
     * NOTA: Mismo patrón que getIntradayData
     * Solo cambia la función de la API
     * ============================================
     */
    @Override
    public String getDailyData(String symbol) {
        String cacheKey = "DAILY_" + symbol;
        
        if (cache.containsKey(cacheKey)) {
            System.out.println("✅ Cache HIT: " + cacheKey);
            return cache.get(cacheKey);
        }
        
        System.out.println("❌ Cache MISS: " + cacheKey + " - Llamando API...");
        
        String result = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/query")
                .queryParam("function", "TIME_SERIES_DAILY")
                .queryParam("symbol", symbol)
                .queryParam("apikey", apiKey)
                .build())
            .retrieve()
            .bodyToMono(String.class)
            .block();
        
        cache.put(cacheKey, result);
        return result;
    }
    
    /**
     * Obtiene datos semanales
     */
    @Override
    public String getWeeklyData(String symbol) {
        String cacheKey = "WEEKLY_" + symbol;
        
        if (cache.containsKey(cacheKey)) {
            System.out.println("✅ Cache HIT: " + cacheKey);
            return cache.get(cacheKey);
        }
        
        System.out.println("❌ Cache MISS: " + cacheKey + " - Llamando API...");
        
        String result = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/query")
                .queryParam("function", "TIME_SERIES_WEEKLY")
                .queryParam("symbol", symbol)
                .queryParam("apikey", apiKey)
                .build())
            .retrieve()
            .bodyToMono(String.class)
            .block();
        
        cache.put(cacheKey, result);
        return result;
    }
    
    /**
     * Obtiene datos mensuales
     */
    @Override
    public String getMonthlyData(String symbol) {
        String cacheKey = "MONTHLY_" + symbol;
        
        if (cache.containsKey(cacheKey)) {
            System.out.println("✅ Cache HIT: " + cacheKey);
            return cache.get(cacheKey);
        }
        
        System.out.println("❌ Cache MISS: " + cacheKey + " - Llamando API...");
        
        String result = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/query")
                .queryParam("function", "TIME_SERIES_MONTHLY")
                .queryParam("symbol", symbol)
                .queryParam("apikey", apiKey)
                .build())
            .retrieve()
            .bodyToMono(String.class)
            .block();
        
        cache.put(cacheKey, result);
        return result;
    }
    
    /* ============================================
     * EXTENSIÓN: Agregar nuevo proveedor
     * ============================================
     * 
     * Para agregar Yahoo Finance u otro proveedor:
     * 
     * 1. Crear nueva clase YahooFinanceService
     * 2. Implementar StockService
     * 3. Cambiar URL base y parámetros
     * 4. En el controlador, usar @Qualifier:
     * 
     * public StockController(
     *     @Qualifier("yahooFinanceService") StockService service
     * ) { ... }
     * 
     * ============================================
     * EXTENSIÓN: Manejo de errores
     * ============================================
     * 
     * Para manejar errores de la API:
     * 
     * String result = webClient.get()
     *     .uri(...)
     *     .retrieve()
     *     .onStatus(
     *         status -> status.is4xxClientError(),
     *         response -> Mono.error(new RuntimeException("Error 4xx"))
     *     )
     *     .bodyToMono(String.class)
     *     .block();
     */
}
