package com.stockmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Spring Boot
 * 
 * CONCEPTOS APLICADOS:
 * - Arquitectura SOA: Esta aplicación actúa como un servicio independiente
 * - Patrón Gateway: Punto de entrada único para el sistema
 * 
 * @SpringBootApplication incluye:
 * - @Configuration: Marca la clase como fuente de beans
 * - @EnableAutoConfiguration: Configura Spring automáticamente
 * - @ComponentScan: Escanea paquetes buscando componentes
 * 
 * ============================================
 * MODIFICAR SI:
 * - Te piden cambiar el nombre del paquete base
 * - Necesitas agregar configuraciones adicionales
 * ============================================
 */
@SpringBootApplication
public class StockMarketApplication {
    
    /**
     * Método principal que inicia la aplicación
     * 
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(StockMarketApplication.class, args);
        System.out.println("✅ Servidor iniciado en http://localhost:8080");
        System.out.println("📊 Endpoints disponibles:");
        System.out.println("   GET /api/stocks/{symbol}/intraday");
        System.out.println("   GET /api/stocks/{symbol}/daily");
        System.out.println("   GET /api/stocks/{symbol}/weekly");
        System.out.println("   GET /api/stocks/{symbol}/monthly");
    }
}
