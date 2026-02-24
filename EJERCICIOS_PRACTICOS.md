# 💪 EJERCICIOS PRÁCTICOS - ENTRENAMIENTO PARCIAL

## 🎯 OBJETIVO
Estos ejercicios te ayudarán a practicar los conceptos clave del parcial de forma incremental.

---

## 📝 EJERCICIO 1: Backend Básico (Spring Boot)

### Objetivo
Crear un servidor Spring Boot simple que exponga un endpoint REST.

### Pasos
1. Crear proyecto Maven con estructura:
```
ejercicio1/
├── src/main/java/com/ejercicio/
│   ├── Application.java
│   └── controller/
│       └── HelloController.java
└── pom.xml
```

2. Implementar clase principal:
```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

3. Crear controlador:
```java
@RestController
@RequestMapping("/api")
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }
}
```

4. Ejecutar y probar:
```bash
mvn spring-boot:run
curl http://localhost:8080/api/hello
```

### ✅ Criterio de éxito
- Servidor arranca sin errores
- Endpoint responde "Hello World!"

---

## 📝 EJERCICIO 2: Patrón Strategy

### Objetivo
Implementar el patrón Strategy para cambiar entre diferentes proveedores.

### Pasos
1. Crear interfaz:
```java
public interface MessageService {
    String getMessage();
}
```

2. Crear dos implementaciones:
```java
@Service
public class SpanishMessageService implements MessageService {
    @Override
    public String getMessage() {
        return "Hola Mundo";
    }
}

@Service
public class EnglishMessageService implements MessageService {
    @Override
    public String getMessage() {
        return "Hello World";
    }
}
```

3. Usar en controlador:
```java
@RestController
public class MessageController {
    private final MessageService messageService;
    
    // Spring inyecta automáticamente
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }
    
    @GetMapping("/message")
    public String getMessage() {
        return messageService.getMessage();
    }
}
```

4. Cambiar implementación con @Qualifier:
```java
public MessageController(@Qualifier("spanishMessageService") MessageService messageService) {
    this.messageService = messageService;
}
```

### ✅ Criterio de éxito
- Puedes cambiar entre español e inglés sin modificar el controlador
- Solo cambias el @Qualifier

### 🤔 Preguntas de reflexión
- ¿Por qué es útil este patrón?
- ¿Cómo aplicarías esto a Alpha Vantage vs Yahoo Finance?

---

## 📝 EJERCICIO 3: Cache Simple

### Objetivo
Implementar un cache básico para evitar operaciones repetidas.

### Pasos
1. Crear clase Cache:
```java
@Component
public class SimpleCache {
    private final HashMap<String, String> cache = new HashMap<>();
    
    public String get(String key) {
        return cache.get(key);
    }
    
    public void put(String key, String value) {
        cache.put(key, value);
    }
    
    public boolean contains(String key) {
        return cache.containsKey(key);
    }
}
```

2. Usar en servicio:
```java
@Service
public class DataService {
    private final SimpleCache cache;
    
    public DataService(SimpleCache cache) {
        this.cache = cache;
    }
    
    public String getData(String id) {
        if (cache.contains(id)) {
            System.out.println("Cache HIT: " + id);
            return cache.get(id);
        }
        
        System.out.println("Cache MISS: " + id);
        String data = "Data for " + id; // Simula llamada costosa
        cache.put(id, data);
        return data;
    }
}
```

3. Probar:
```java
@GetMapping("/data/{id}")
public String getData(@PathVariable String id) {
    return dataService.getData(id);
}
```

### ✅ Criterio de éxito
- Primera llamada: "Cache MISS"
- Segunda llamada: "Cache HIT"

### ⚠️ Problema
- ¿Qué pasa si múltiples usuarios acceden simultáneamente?
- HashMap NO es thread-safe

---

## 📝 EJERCICIO 4: Cache Concurrente

### Objetivo
Hacer el cache thread-safe usando ConcurrentHashMap.

### Pasos
1. Modificar SimpleCache:
```java
@Component
public class ConcurrentCache {
    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
    
    public String get(String key) {
        return cache.get(key);
    }
    
    public void put(String key, String value) {
        cache.put(key, value);
    }
    
    public boolean contains(String key) {
        return cache.containsKey(key);
    }
}
```

2. Crear cliente de pruebas:
```java
public class ConcurrentTest {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        for (int i = 0; i < 10; i++) {
            final int id = i;
            executor.submit(() -> {
                // Hacer petición HTTP
                String url = "http://localhost:8080/data/" + (id % 3);
                // Medir tiempo
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }
}
```

### ✅ Criterio de éxito
- 10 hilos ejecutan simultáneamente sin errores
- Cache funciona correctamente

### 🤔 Preguntas de reflexión
- ¿Qué diferencia hay entre HashMap y ConcurrentHashMap?
- ¿Por qué no usar synchronized?

---

## 📝 EJERCICIO 5: Cliente HTTP (WebClient)

### Objetivo
Hacer llamadas a APIs externas usando WebClient de Spring.

### Pasos
1. Agregar dependencia en pom.xml:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

2. Crear servicio:
```java
@Service
public class ExternalApiService {
    private final WebClient webClient;
    
    public ExternalApiService() {
        this.webClient = WebClient.builder()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .build();
    }
    
    public String getPost(int id) {
        return webClient.get()
            .uri("/posts/" + id)
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }
}
```

3. Exponer endpoint:
```java
@GetMapping("/post/{id}")
public String getPost(@PathVariable int id) {
    return externalApiService.getPost(id);
}
```

### ✅ Criterio de éxito
- Endpoint retorna datos de JSONPlaceholder
- Puedes ver el JSON en el navegador

### 🎯 Desafío
- Agregar cache a este servicio
- Medir diferencia de tiempo con/sin cache

---

## 📝 EJERCICIO 6: React Básico

### Objetivo
Crear una app React que consuma tu API.

### Pasos
1. Crear proyecto:
```bash
npx create-react-app ejercicio6
cd ejercicio6
npm start
```

2. Modificar App.js:
```javascript
import { useState, useEffect } from 'react';

function App() {
  const [message, setMessage] = useState('');
  
  useEffect(() => {
    fetch('http://localhost:8080/api/hello')
      .then(response => response.text())
      .then(data => setMessage(data));
  }, []);
  
  return (
    <div>
      <h1>Mensaje del servidor:</h1>
      <p>{message}</p>
    </div>
  );
}

export default App;
```

3. Configurar CORS en Spring:
```java
@CrossOrigin(origins = "http://localhost:3000")
```

### ✅ Criterio de éxito
- React muestra el mensaje del servidor
- No hay errores de CORS

---

## 📝 EJERCICIO 7: React con Input

### Objetivo
Permitir al usuario ingresar un símbolo de acción.

### Pasos
1. Modificar App.js:
```javascript
function App() {
  const [symbol, setSymbol] = useState('IBM');
  const [data, setData] = useState(null);
  
  const fetchData = async () => {
    const response = await fetch(`http://localhost:8080/api/stocks/${symbol}/daily`);
    const json = await response.json();
    setData(json);
  };
  
  return (
    <div>
      <input 
        value={symbol} 
        onChange={(e) => setSymbol(e.target.value)} 
      />
      <button onClick={fetchData}>Buscar</button>
      {data && <pre>{JSON.stringify(data, null, 2)}</pre>}
    </div>
  );
}
```

### ✅ Criterio de éxito
- Usuario puede cambiar el símbolo
- Al hacer clic, se muestran los datos

---

## 📝 EJERCICIO 8: Gráficos con Recharts

### Objetivo
Visualizar datos de acciones en un gráfico.

### Pasos
1. Instalar Recharts:
```bash
npm install recharts
```

2. Crear componente:
```javascript
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip } from 'recharts';

function StockChart({ data }) {
  // Transformar datos de Alpha Vantage a formato Recharts
  const chartData = Object.entries(data['Time Series (Daily)'] || {})
    .slice(0, 30)
    .map(([date, values]) => ({
      date,
      price: parseFloat(values['4. close'])
    }))
    .reverse();
  
  return (
    <LineChart width={800} height={400} data={chartData}>
      <CartesianGrid strokeDasharray="3 3" />
      <XAxis dataKey="date" />
      <YAxis />
      <Tooltip />
      <Line type="monotone" dataKey="price" stroke="#8884d8" />
    </LineChart>
  );
}
```

### ✅ Criterio de éxito
- Gráfico muestra últimos 30 días
- Se ve profesional y limpio

---

## 📝 EJERCICIO 9: Cliente Java Concurrente

### Objetivo
Crear un cliente Java que haga múltiples peticiones simultáneas.

### Pasos
1. Crear clase:
```java
public class ConcurrentClient {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(20);
        String[] symbols = {"IBM", "MSFT", "AAPL"};
        
        for (int i = 0; i < 20; i++) {
            final String symbol = symbols[i % 3];
            executor.submit(() -> {
                try {
                    long start = System.currentTimeMillis();
                    
                    URL url = new URL("http://localhost:8080/api/stocks/" + symbol + "/daily");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    
                    int responseCode = conn.getResponseCode();
                    long end = System.currentTimeMillis();
                    
                    System.out.println(symbol + " - " + responseCode + " - " + (end - start) + "ms");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.MINUTES);
    }
}
```

### ✅ Criterio de éxito
- 20 peticiones se ejecutan simultáneamente
- Primeras peticiones: lentas (llamada a API)
- Siguientes peticiones: rápidas (desde cache)

### 📊 Resultados esperados
```
IBM - 200 - 850ms    (primera vez, llama API)
MSFT - 200 - 920ms   (primera vez, llama API)
AAPL - 200 - 780ms   (primera vez, llama API)
IBM - 200 - 15ms     (desde cache)
MSFT - 200 - 12ms    (desde cache)
AAPL - 200 - 18ms    (desde cache)
...
```

---

## 📝 EJERCICIO 10: Extensibilidad

### Objetivo
Demostrar que puedes agregar un nuevo proveedor fácilmente.

### Pasos
1. Crear MockStockService:
```java
@Service
public class MockStockService implements StockService {
    @Override
    public String getDailyData(String symbol) {
        return "{\"symbol\":\"" + symbol + "\",\"price\":100.50,\"source\":\"mock\"}";
    }
    
    @Override
    public String getIntradayData(String symbol) {
        return "{\"symbol\":\"" + symbol + "\",\"price\":100.50,\"source\":\"mock\"}";
    }
    
    // ... otros métodos
}
```

2. Cambiar en controlador:
```java
public StockController(@Qualifier("mockStockService") StockService stockService) {
    this.stockService = stockService;
}
```

3. Probar:
```bash
curl http://localhost:8080/api/stocks/IBM/daily
# Debe retornar datos mock, no de Alpha Vantage
```

### ✅ Criterio de éxito
- Cambias de proveedor sin modificar el controlador
- Solo cambias @Qualifier
- Demuestra extensibilidad

---

## 🎯 EJERCICIO INTEGRADOR FINAL

### Objetivo
Construir una versión simplificada del proyecto completo.

### Requisitos
1. Backend Spring Boot con:
   - Endpoint `/api/stocks/{symbol}/daily`
   - Cache concurrente
   - Conexión a Alpha Vantage (o mock)

2. Frontend React con:
   - Input para símbolo
   - Botón de búsqueda
   - Gráfico con Recharts

3. Cliente Java:
   - 10 peticiones concurrentes
   - Medir tiempos

4. Documentación:
   - README con instrucciones
   - Explicar patrones usados

### ✅ Criterio de éxito
- Todo funciona end-to-end
- Cache mejora rendimiento
- Código está documentado

---

## 📊 AUTOEVALUACIÓN

Después de cada ejercicio, pregúntate:

- [ ] ¿Entiendo por qué funciona?
- [ ] ¿Podría explicarlo a alguien más?
- [ ] ¿Podría hacerlo de nuevo sin mirar?
- [ ] ¿Veo cómo se aplica al proyecto final?

---

## 🚀 PLAN DE PRÁCTICA SUGERIDO

### Semana 1
- Ejercicios 1-3: Backend básico
- Ejercicios 4-5: Cache y APIs externas

### Semana 2
- Ejercicios 6-8: React y visualización
- Ejercicio 9: Cliente concurrente

### Semana 3
- Ejercicio 10: Extensibilidad
- Ejercicio integrador final

---

## 💡 TIPS

1. **No copies y pegues**: Escribe el código tú mismo
2. **Experimenta**: Cambia cosas y ve qué pasa
3. **Rompe cosas**: Aprende de los errores
4. **Pregunta**: ¿Por qué funciona así?
5. **Documenta**: Escribe comentarios mientras codificas

---

**¡A practicar! 💪**
