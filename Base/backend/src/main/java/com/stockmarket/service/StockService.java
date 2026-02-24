package com.stockmarket.service;

/**
 * Interfaz para servicios de consulta de acciones
 * 
 * CONCEPTOS APLICADOS:
 * - Diseño OO: Interface define el TIPO (contrato) sin implementación
 * - Polimorfismo: Permite múltiples implementaciones (AlphaVantage, Yahoo, Mock)
 * - Patrón Strategy: Cambia implementación sin modificar código cliente
 * - Bajo Acoplamiento: El controlador depende de la interfaz, no de implementación concreta
 * 
 * ¿POR QUÉ USAR INTERFAZ?
 * - Permite cambiar de proveedor fácilmente (Alpha Vantage → Yahoo Finance)
 * - Facilita testing (puedes crear MockStockService)
 * - Cumple con Dependency Inversion Principle (depender de abstracciones)
 * 
 * ============================================
 * MODIFICAR SI:
 * - Te piden agregar más intervalos (ej: getHourlyData)
 * - Necesitas análisis técnico (ej: getTechnicalAnalysis)
 * - Te piden comparación de acciones (ej: compareStocks)
 * - Necesitas datos históricos con rango de fechas
 * ============================================
 */
public interface StockService {
    
    /**
     * Obtiene datos intradiarios de una acción (cada 5 minutos)
     * 
     * @param symbol Símbolo de la acción (ej: "MSFT", "IBM", "AAPL")
     * @return JSON con los datos de la API
     * 
     * ============================================
     * EJEMPLO DE RESPUESTA:
     * {
     *   "Meta Data": {...},
     *   "Time Series (5min)": {
     *     "2025-01-15 16:00:00": {
     *       "1. open": "150.00",
     *       "2. high": "151.00",
     *       "3. low": "149.50",
     *       "4. close": "150.75",
     *       "5. volume": "1000000"
     *     }
     *   }
     * }
     * ============================================
     */
    String getIntradayData(String symbol);
    
    /**
     * Obtiene datos diarios de una acción
     * 
     * @param symbol Símbolo de la acción
     * @return JSON con los datos
     */
    String getDailyData(String symbol);
    
    /**
     * Obtiene datos semanales de una acción
     * 
     * @param symbol Símbolo de la acción
     * @return JSON con los datos
     */
    String getWeeklyData(String symbol);
    
    /**
     * Obtiene datos mensuales de una acción
     * 
     * @param symbol Símbolo de la acción
     * @return JSON con los datos
     */
    String getMonthlyData(String symbol);
    
    /* ============================================
     * EXTENSIONES OPCIONALES
     * ============================================
     * 
     * Si te piden más funcionalidades, agrega métodos aquí:
     * 
     * // Análisis técnico
     * String getTechnicalAnalysis(String symbol, String indicator);
     * 
     * // Comparación de acciones
     * String compareStocks(String symbol1, String symbol2);
     * 
     * // Datos con rango de fechas
     * String getDataByDateRange(String symbol, String startDate, String endDate);
     * 
     * // Búsqueda de acciones
     * String searchSymbol(String keywords);
     * 
     * // Información de la compañía
     * String getCompanyInfo(String symbol);
     */
}
