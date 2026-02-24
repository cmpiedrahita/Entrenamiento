package com.stockmarket.controller;

import com.stockmarket.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST que expone endpoints para consultar acciones
 * 
 * CONCEPTOS APLICADOS:
 * - Patrón Gateway/Fachada: Punto único de entrada para el cliente
 * - REST: Arquitectura basada en recursos y verbos HTTP
 * - Dependency Injection: Spring inyecta StockService
 * - Bajo Acoplamiento: Depende de interfaz (StockService), no de implementación
 * 
 * ARQUITECTURA:
 * Cliente (React) → HTTP/JSON → Este Controlador → StockService → API Externa
 * 
 * MEJORES PRÁCTICAS REST:
 * ✅ Recursos en plural: /stocks (no /stock)
 * ✅ Sustantivos, no verbos: /stocks/{id} (no /getStock)
 * ✅ GET no altera estado (solo lectura)
 * ✅ Códigos HTTP apropiados (200, 404, 500)
 * 
 * ============================================
 * MODIFICAR SI:
 * - Te piden agregar más endpoints
 * - Necesitas manejo de errores específico
 * - Te piden POST para crear alertas
 * - Necesitas autenticación/autorización
 * ============================================
 */
@RestController  // Combina @Controller + @ResponseBody (retorna JSON automáticamente)
@RequestMapping("/api/stocks")  // Prefijo para todos los endpoints
@CrossOrigin(origins = "*")  // Permite peticiones desde cualquier origen (CORS)
// ⚠️ PRODUCCIÓN: Cambiar "*" por URL específica del frontend
public class StockController {
    
    /**
     * Servicio de acciones inyectado por Spring
     * 
     * CONCEPTO: Dependency Injection + Polimorfismo
     * - Spring inyecta automáticamente una implementación de StockService
     * - Puede ser AlphaVantageService, YahooFinanceService, MockService, etc.
     * - El controlador NO sabe ni le importa cuál implementación es
     * 
     * VENTAJA: Cambiar proveedor sin modificar este código
     */
    private final StockService stockService;
    
    /**
     * Constructor con inyección de dependencias
     * 
     * Spring busca un bean que implemente StockService y lo inyecta aquí
     * 
     * ============================================
     * MODIFICAR SI:
     * Tienes múltiples implementaciones y quieres elegir una específica:
     * 
     * public StockController(
     *     @Qualifier("alphaVantageService") StockService stockService
     * ) { ... }
     * ============================================
     * 
     * @param stockService Servicio inyectado por Spring
     */
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }
    
    /**
     * Endpoint para obtener datos intradiarios (cada 5 minutos)
     * 
     * URL: GET /api/stocks/{symbol}/intraday
     * Ejemplo: GET /api/stocks/IBM/intraday
     * 
     * FLUJO:
     * 1. Cliente hace petición HTTP GET
     * 2. Spring extrae "symbol" de la URL
     * 3. Llama a stockService.getIntradayData(symbol)
     * 4. Retorna JSON con código 200 OK
     * 
     * @param symbol Símbolo de la acción (extraído de la URL)
     * @return ResponseEntity con JSON y código HTTP 200
     */
    @GetMapping("/{symbol}/intraday")
    public ResponseEntity<String> getIntraday(@PathVariable String symbol) {
        String data = stockService.getIntradayData(symbol);
        return ResponseEntity.ok(data);
        // ResponseEntity.ok() = código 200 + body
    }
    
    /**
     * Endpoint para obtener datos diarios
     * 
     * URL: GET /api/stocks/{symbol}/daily
     * Ejemplo: GET /api/stocks/MSFT/daily
     * 
     * @param symbol Símbolo de la acción
     * @return ResponseEntity con JSON
     */
    @GetMapping("/{symbol}/daily")
    public ResponseEntity<String> getDaily(@PathVariable String symbol) {
        String data = stockService.getDailyData(symbol);
        return ResponseEntity.ok(data);
    }
    
    /**
     * Endpoint para obtener datos semanales
     * 
     * URL: GET /api/stocks/{symbol}/weekly
     * Ejemplo: GET /api/stocks/AAPL/weekly
     */
    @GetMapping("/{symbol}/weekly")
    public ResponseEntity<String> getWeekly(@PathVariable String symbol) {
        String data = stockService.getWeeklyData(symbol);
        return ResponseEntity.ok(data);
    }
    
    /**
     * Endpoint para obtener datos mensuales
     * 
     * URL: GET /api/stocks/{symbol}/monthly
     * Ejemplo: GET /api/stocks/GOOGL/monthly
     */
    @GetMapping("/{symbol}/monthly")
    public ResponseEntity<String> getMonthly(@PathVariable String symbol) {
        String data = stockService.getMonthlyData(symbol);
        return ResponseEntity.ok(data);
    }
    
    /* ============================================
     * EXTENSIONES OPCIONALES
     * ============================================
     * 
     * Si te piden más funcionalidades:
     * 
     * // Comparar dos acciones
     * @GetMapping("/compare")
     * public ResponseEntity<String> compare(
     *     @RequestParam String symbol1,
     *     @RequestParam String symbol2
     * ) {
     *     // Lógica de comparación
     * }
     * 
     * // Buscar símbolos
     * @GetMapping("/search")
     * public ResponseEntity<String> search(@RequestParam String query) {
     *     // Lógica de búsqueda
     * }
     * 
     * // Crear alerta (POST)
     * @PostMapping("/alerts")
     * public ResponseEntity<String> createAlert(@RequestBody AlertRequest request) {
     *     // Lógica para crear alerta
     * }
     * 
     * ============================================
     * MANEJO DE ERRORES
     * ============================================
     * 
     * Para manejar errores específicos:
     * 
     * @GetMapping("/{symbol}/daily")
     * public ResponseEntity<String> getDaily(@PathVariable String symbol) {
     *     try {
     *         String data = stockService.getDailyData(symbol);
     *         return ResponseEntity.ok(data);
     *     } catch (Exception e) {
     *         return ResponseEntity
     *             .status(HttpStatus.INTERNAL_SERVER_ERROR)
     *             .body("{\"error\": \"" + e.getMessage() + "\"}");
     *     }
     * }
     * 
     * O crear un @ControllerAdvice para manejo global de errores
     */
}
