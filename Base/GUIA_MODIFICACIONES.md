# 🔧 GUÍA RÁPIDA DE MODIFICACIONES

## 🎯 DÓNDE MODIFICAR SEGÚN LO QUE TE PIDAN

### 1️⃣ CAMBIAR API KEY
**Archivo:** `backend/src/main/resources/application.properties`
**Línea:** 9
```properties
alphavantage.apikey=TU_API_KEY_AQUI
```

---

### 2️⃣ AGREGAR NUEVO INTERVALO (ej: Horario)

**Paso 1 - Interfaz:**
`backend/src/main/java/com/stockmarket/service/StockService.java`
```java
String getHourlyData(String symbol);
```

**Paso 2 - Implementación:**
`backend/src/main/java/com/stockmarket/service/AlphaVantageService.java`
```java
@Override
public String getHourlyData(String symbol) {
    String cacheKey = "HOURLY_" + symbol;
    if (cache.containsKey(cacheKey)) return cache.get(cacheKey);
    
    String result = webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/query")
            .queryParam("function", "TIME_SERIES_HOURLY")
            .queryParam("symbol", symbol)
            .queryParam("apikey", apiKey)
            .build())
        .retrieve()
        .bodyToMono(String.class)
        .block();
    
    cache.put(cacheKey, result);
    return result;
}
```

**Paso 3 - Endpoint:**
`backend/src/main/java/com/stockmarket/controller/StockController.java`
```java
@GetMapping("/{symbol}/hourly")
public ResponseEntity<String> getHourly(@PathVariable String symbol) {
    return ResponseEntity.ok(stockService.getHourlyData(symbol));
}
```

**Paso 4 - Frontend:**
`frontend/src/App.js` - Línea 115
```javascript
<option value="hourly">Por Hora</option>
```

**Paso 5 - Transformación:**
`frontend/src/App.js` - Línea 65
```javascript
case 'hourly': timeSeriesKey = 'Time Series (60min)'; break;
```

---

### 3️⃣ AGREGAR NUEVO PROVEEDOR (ej: Yahoo Finance)

**Paso 1 - Crear Servicio:**
`backend/src/main/java/com/stockmarket/service/YahooFinanceService.java`
```java
package com.stockmarket.service;

import com.stockmarket.cache.ConcurrentCache;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class YahooFinanceService implements StockService {
    private final WebClient webClient;
    private final ConcurrentCache cache;
    
    public YahooFinanceService(ConcurrentCache cache) {
        this.webClient = WebClient.builder()
            .baseUrl("https://query1.finance.yahoo.com")
            .build();
        this.cache = cache;
    }
    
    @Override
    public String getDailyData(String symbol) {
        // Implementar con API de Yahoo
        return "{}";
    }
    
    // Implementar otros métodos...
}
```

**Paso 2 - Seleccionar Proveedor:**
`backend/src/main/java/com/stockmarket/controller/StockController.java`
```java
public StockController(@Qualifier("yahooFinanceService") StockService stockService) {
    this.stockService = stockService;
}
```

---

### 4️⃣ CAMBIAR LIBRERÍA DE GRÁFICOS (Chart.js)

**Paso 1 - Instalar:**
```bash
cd frontend
npm install react-chartjs-2 chart.js
```

**Paso 2 - Modificar App.js:**
```javascript
import { Line } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

// En el render, reemplazar <LineChart> por:
const chartData = {
  labels: data.map(d => d.date),
  datasets: [{
    label: 'Precio de Cierre',
    data: data.map(d => d.price),
    borderColor: 'rgb(75, 192, 192)',
    backgroundColor: 'rgba(75, 192, 192, 0.2)',
  }]
};

<Line data={chartData} />
```

---

### 5️⃣ AGREGAR COMPARACIÓN DE ACCIONES

**Backend - StockService.java:**
```java
String compareStocks(String symbol1, String symbol2);
```

**Backend - AlphaVantageService.java:**
```java
@Override
public String compareStocks(String symbol1, String symbol2) {
    String data1 = getDailyData(symbol1);
    String data2 = getDailyData(symbol2);
    // Combinar y retornar
    return "{\"stock1\":" + data1 + ",\"stock2\":" + data2 + "}";
}
```

**Backend - StockController.java:**
```java
@GetMapping("/compare")
public ResponseEntity<String> compare(
    @RequestParam String symbol1,
    @RequestParam String symbol2
) {
    return ResponseEntity.ok(stockService.compareStocks(symbol1, symbol2));
}
```

**Frontend - App.js:**
```javascript
const [symbol2, setSymbol2] = useState('MSFT');

const fetchComparison = async () => {
    const url = `${BACKEND_URL}/compare?symbol1=${symbol1}&symbol2=${symbol2}`;
    const response = await fetch(url);
    const data = await response.json();
    // Procesar y mostrar
};
```

---

### 6️⃣ AGREGAR ANÁLISIS CON IA (BONO)

**Backend - StockService.java:**
```java
String analyzeWithAI(String symbol1, String symbol2);
```

**Backend - Crear AIService.java:**
```java
@Service
public class AIService {
    public String analyze(String data1, String data2) {
        // Llamar a API de IA (OpenAI, etc.)
        return "Recomendación: Invertir en...";
    }
}
```

**Backend - StockController.java:**
```java
@Autowired
private AIService aiService;

@PostMapping("/analyze")
public ResponseEntity<String> analyze(@RequestBody AnalysisRequest request) {
    String data1 = stockService.getDailyData(request.getSymbol1());
    String data2 = stockService.getDailyData(request.getSymbol2());
    String analysis = aiService.analyze(data1, data2);
    return ResponseEntity.ok(analysis);
}
```

---

### 7️⃣ CAMBIAR NÚMERO DE HILOS EN CLIENTE JAVA

**Archivo:** `java-client/src/main/java/com/stockmarket/client/ConcurrentTestClient.java`
**Línea 40:**
```java
private static final int THREAD_POOL_SIZE = 50;  // Cambiar aquí
private static final int TOTAL_REQUESTS = 50;    // Cambiar aquí
```

---

### 8️⃣ AGREGAR EXPIRACIÓN AL CACHE (TTL)

**Archivo:** `backend/src/main/java/com/stockmarket/cache/ConcurrentCache.java`
```java
private static class CacheEntry {
    final String value;
    final long timestamp;
    
    CacheEntry(String value) {
        this.value = value;
        this.timestamp = System.currentTimeMillis();
    }
    
    boolean isExpired(long ttlMillis) {
        return System.currentTimeMillis() - timestamp > ttlMillis;
    }
}

private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
private static final long TTL = 5 * 60 * 1000; // 5 minutos

public String get(String key) {
    CacheEntry entry = cache.get(key);
    if (entry != null && !entry.isExpired(TTL)) {
        return entry.value;
    }
    cache.remove(key);
    return null;
}

public void put(String key, String value) {
    cache.put(key, new CacheEntry(value));
}
```

---

### 9️⃣ CAMBIAR URL PARA DESPLIEGUE

**Frontend - App.js línea 30:**
```javascript
const BACKEND_URL = 'https://tu-backend.elasticbeanstalk.com/api/stocks';
```

**Cliente Java - ConcurrentTestClient.java línea 40:**
```java
private static final String BASE_URL = "https://tu-backend.elasticbeanstalk.com/api/stocks";
```

---

### 🔟 AGREGAR MANEJO DE ERRORES

**Backend - StockController.java:**
```java
@GetMapping("/{symbol}/daily")
public ResponseEntity<String> getDaily(@PathVariable String symbol) {
    try {
        String data = stockService.getDailyData(symbol);
        return ResponseEntity.ok(data);
    } catch (Exception e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("{\"error\": \"" + e.getMessage() + "\"}");
    }
}
```

**O crear ControllerAdvice:**
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("{\"error\": \"" + e.getMessage() + "\"}");
    }
}
```

---

## 📍 RESUMEN DE ARCHIVOS CLAVE

| Archivo | Qué Modificar |
|---------|---------------|
| `application.properties` | API Key |
| `StockService.java` | Agregar métodos nuevos |
| `AlphaVantageService.java` | Implementar métodos |
| `StockController.java` | Agregar endpoints |
| `ConcurrentCache.java` | Modificar lógica de cache |
| `App.js` (línea 30) | URL del backend |
| `App.js` (línea 115) | Opciones de intervalo |
| `ConcurrentTestClient.java` (línea 40) | Configuración de pruebas |

---

**💡 TIP:** Busca los comentarios `MODIFICAR:` en el código para encontrar rápidamente los puntos de extensión.
