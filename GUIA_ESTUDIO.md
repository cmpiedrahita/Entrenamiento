# 📚 GUÍA DE ESTUDIO - PARCIAL ARQUITECTURA WEB

## 🎯 RESUMEN EJECUTIVO DEL PROYECTO

Debes construir una aplicación web distribuida para consultar acciones de bolsa usando:
- **Frontend**: React (cliente asíncrono)
- **Backend**: Spring Boot (gateway/fachada)
- **API Externa**: Alpha Vantage
- **Cache**: Thread-safe con ConcurrentHashMap
- **Cliente de Pruebas**: Java con concurrencia
- **Despliegue**: AWS o Azure

---

## 📋 CHECKLIST DE REQUISITOS (100%)

### ✅ Componentes Obligatorios
- [ ] Cliente React asíncrono (10%)
- [ ] Gateway Spring Boot con REST (10%)
- [ ] Conexión a Alpha Vantage API (20%)
- [ ] Despliegue en AWS/Azure (10%)
- [ ] CI/CD en Azure (5%)
- [ ] Cliente Java para pruebas concurrentes (10%)
- [ ] Cache tolerante a concurrencia (10%)
- [ ] Diseño, repositorio y documentación (25-30%)

### 🎨 Criterios de Calidad del Diseño
- [ ] **Extensible**: Fácil agregar nuevos proveedores
- [ ] **Usa patrones**: Strategy, Gateway, Cache
- [ ] **Modular**: Separación clara de responsabilidades
- [ ] **Organizado**: Estructura de carpetas lógica
- [ ] **Documentado**: Javadoc y README completo

---

## 🏗️ ARQUITECTURA - CONCEPTOS CLAVE

### 1. Patrón Gateway/Fachada
```
Cliente → Gateway (Spring) → Servicios Externos
```

**¿Por qué?**
- Punto único de entrada
- Encapsula complejidad
- Centraliza lógica de negocio
- Facilita cambios sin afectar clientes

**Implementación:**
```java
@RestController
@RequestMapping("/api/stocks")
public class StockController {
    private final StockService stockService;
    
    @GetMapping("/{symbol}/daily")
    public ResponseEntity<String> getDaily(@PathVariable String symbol) {
        return ResponseEntity.ok(stockService.getDailyData(symbol));
    }
}
```

### 2. Patrón Strategy
```
StockService (interfaz)
    ↓
AlphaVantageService (implementación 1)
YahooFinanceService (implementación 2)
```

**¿Por qué?**
- Permite cambiar proveedor sin modificar controlador
- Cumple con Open/Closed Principle
- Facilita testing con mocks

**Implementación:**
```java
// Interfaz
public interface StockService {
    String getDailyData(String symbol);
}

// Implementación
@Service
public class AlphaVantageService implements StockService {
    @Override
    public String getDailyData(String symbol) {
        // Lógica específica de Alpha Vantage
    }
}
```

### 3. Cache Concurrente

**¿Por qué ConcurrentHashMap?**
- Thread-safe sin synchronized
- Mejor rendimiento que Hashtable
- Permite lecturas concurrentes
- Escrituras con lock granular

**Implementación:**
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
}
```

**Flujo con Cache:**
```
1. Cliente solicita datos de IBM
2. Gateway verifica cache: ¿existe "DAILY_IBM"?
   - SÍ → Retorna inmediatamente (10-50ms)
   - NO → Llama API → Guarda en cache → Retorna (500-1000ms)
3. Siguiente solicitud de IBM → Desde cache (rápido)
```

---

## 🔧 TECNOLOGÍAS - GUÍA RÁPIDA

### Spring Boot

**Anotaciones Clave:**
- `@SpringBootApplication`: Clase principal
- `@RestController`: Controlador REST
- `@Service`: Lógica de negocio
- `@Component`: Bean genérico
- `@GetMapping`: Endpoint GET
- `@PathVariable`: Parámetro de URL
- `@Value`: Inyectar propiedades

**Inyección de Dependencias:**
```java
// Spring crea y gestiona automáticamente
public StockController(StockService stockService) {
    this.stockService = stockService;
}
```

### React

**Conceptos Clave:**
- **Componentes**: Bloques reutilizables de UI
- **useState**: Manejo de estado
- **useEffect**: Efectos secundarios (llamadas API)
- **Async/Await**: Peticiones asíncronas

**Ejemplo:**
```javascript
const [data, setData] = useState(null);

useEffect(() => {
    async function fetchData() {
        const response = await fetch(`/api/stocks/${symbol}/daily`);
        const json = await response.json();
        setData(json);
    }
    fetchData();
}, [symbol]);
```

### Maven

**Comandos Esenciales:**
```bash
mvn clean install    # Compila y genera JAR
mvn spring-boot:run  # Ejecuta aplicación
mvn package          # Genera JAR para despliegue
```

**pom.xml - Dependencias Clave:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

---

## 🌐 REST API - MEJORES PRÁCTICAS

### Principios REST
1. **Recursos como sustantivos** (no verbos)
   - ✅ `/api/stocks/IBM`
   - ❌ `/api/getStock?symbol=IBM`

2. **Verbos HTTP correctos**
   - GET: Leer
   - POST: Crear
   - PUT: Actualizar completo
   - DELETE: Eliminar

3. **Códigos de estado HTTP**
   - 200: OK
   - 404: No encontrado
   - 500: Error del servidor

4. **JSON como formato**
   ```json
   {
     "symbol": "IBM",
     "price": 150.25,
     "date": "2025-01-15"
   }
   ```

### Endpoints del Proyecto
```
GET /api/stocks/{symbol}/intraday   → Datos cada 5 min
GET /api/stocks/{symbol}/daily      → Datos diarios
GET /api/stocks/{symbol}/weekly     → Datos semanales
GET /api/stocks/{symbol}/monthly    → Datos mensuales
```

---

## 🧵 CONCURRENCIA - CONCEPTOS CLAVE

### ¿Por qué es importante?
- Aplicación multiusuario
- Múltiples peticiones simultáneas
- Cache compartido entre hilos

### Problemas sin Sincronización
```java
// ❌ NO THREAD-SAFE
HashMap<String, String> cache = new HashMap<>();

// Hilo 1: cache.put("IBM", "data1")
// Hilo 2: cache.put("MSFT", "data2")
// Resultado: Corrupción de datos
```

### Solución: ConcurrentHashMap
```java
// ✅ THREAD-SAFE
ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

// Múltiples hilos pueden leer/escribir sin problemas
```

### Cliente de Pruebas Concurrentes
```java
ExecutorService executor = Executors.newFixedThreadPool(20);

for (int i = 0; i < 20; i++) {
    executor.submit(() -> {
        // Hacer petición HTTP al gateway
        // Medir tiempo de respuesta
    });
}
```

**Objetivo:** Verificar que el cache funciona correctamente bajo carga.

---

## 🔄 CÓMO EXTENDER EL SISTEMA

### Agregar Nuevo Proveedor (Yahoo Finance)

**Paso 1:** Crear implementación
```java
@Service
public class YahooFinanceService implements StockService {
    @Override
    public String getDailyData(String symbol) {
        // Llamar API de Yahoo Finance
    }
}
```

**Paso 2:** Configurar en application.properties
```properties
stock.provider=yahoo
```

**Paso 3:** Usar @Qualifier en controlador
```java
@Autowired
@Qualifier("yahooFinanceService")
private StockService stockService;
```

### Agregar Nueva Funcionalidad

**Ejemplo: Análisis Técnico**
```java
// 1. Agregar método en interfaz
public interface StockService {
    String getTechnicalAnalysis(String symbol);
}

// 2. Implementar en servicio
@Override
public String getTechnicalAnalysis(String symbol) {
    // Lógica
}

// 3. Crear endpoint
@GetMapping("/{symbol}/analysis")
public ResponseEntity<String> getAnalysis(@PathVariable String symbol) {
    return ResponseEntity.ok(stockService.getTechnicalAnalysis(symbol));
}
```

---

## 📊 VISUALIZACIÓN DE DATOS

### Recharts (Recomendado)
```javascript
import { LineChart, Line, XAxis, YAxis } from 'recharts';

<LineChart data={stockData}>
    <XAxis dataKey="date" />
    <YAxis />
    <Line type="monotone" dataKey="price" stroke="#8884d8" />
</LineChart>
```

### Cambiar a Chart.js
```javascript
import { Line } from 'react-chartjs-2';

const chartData = {
    labels: dates,
    datasets: [{
        label: 'Stock Price',
        data: prices
    }]
};

<Line data={chartData} />
```

---

## ☁️ DESPLIEGUE EN AWS

### Backend (Elastic Beanstalk)
```bash
# 1. Crear JAR
mvn clean package

# 2. Inicializar EB
eb init -p java-17 stock-market-app

# 3. Crear ambiente
eb create stock-market-env

# 4. Desplegar
eb deploy
```

### Frontend (S3 + CloudFront)
```bash
# 1. Build
npm run build

# 2. Crear bucket S3
aws s3 mb s3://stock-market-frontend

# 3. Subir archivos
aws s3 sync build/ s3://stock-market-frontend

# 4. Configurar como sitio web
aws s3 website s3://stock-market-frontend --index-document index.html
```

### Variables de Entorno
```
ALPHAVANTAGE_APIKEY=tu_api_key
```

---

## 🎓 CONCEPTOS TEÓRICOS DEL CURSO

### 1. Diseño Orientado a Objetos

**Interfaces vs Clases Abstractas:**
- **Interface**: Solo define contrato (qué hacer)
- **Clase Abstracta**: Puede tener implementación parcial

**Polimorfismo:**
```java
StockService service = new AlphaVantageService();
// service puede ser cualquier implementación de StockService
```

**Genéricos:**
```java
ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
// Tipo seguro en tiempo de compilación
```

### 2. Programación Funcional

**Lambdas:**
```java
executor.submit(() -> {
    // Código a ejecutar en hilo
});
```

**Streams:**
```java
List<String> symbols = Arrays.asList("IBM", "MSFT", "AAPL");
symbols.stream()
    .filter(s -> s.startsWith("M"))
    .forEach(System.out::println);
```

### 3. Arquitectura SOA

**Principios:**
- **Bajo acoplamiento**: Servicios independientes
- **Abstracción**: Ocultar detalles de implementación
- **Stateless**: Sin estado entre peticiones
- **Descubrimiento**: Fácil encontrar servicios

**REST vs SOAP:**
- **REST**: Ligero, usa HTTP, JSON
- **SOAP**: Pesado, XML, WSDL

---

## 🚨 ERRORES COMUNES A EVITAR

### 1. Cache No Thread-Safe
❌ Usar HashMap
✅ Usar ConcurrentHashMap

### 2. No Usar Interfaz para Servicios
❌ Controlador depende directamente de AlphaVantageService
✅ Controlador depende de StockService (interfaz)

### 3. Hardcodear API Key
❌ `String apiKey = "ABC123";`
✅ `@Value("${alphavantage.apikey}") private String apiKey;`

### 4. No Documentar
❌ Código sin comentarios
✅ Javadoc en todos los métodos públicos

### 5. Endpoints Mal Diseñados
❌ `/getStockDaily?symbol=IBM`
✅ `/api/stocks/IBM/daily`

---

## 📝 ESTRUCTURA DEL README.md

Tu README debe incluir:

1. **Descripción**: Qué hace la aplicación
2. **Arquitectura**: Diagrama de componentes
3. **Tecnologías**: Lista de herramientas usadas
4. **Patrones**: Qué patrones implementaste y por qué
5. **Cómo ejecutar**: Comandos paso a paso
6. **Endpoints**: Lista de APIs REST
7. **Extensibilidad**: Cómo agregar proveedores/funcionalidades
8. **Despliegue**: URLs de AWS/Azure
9. **URLs**: GitHub, AWS, Azure

---

## ⏱️ PLAN DE ESTUDIO SUGERIDO

### Día 1-2: Entender la Arquitectura
- Revisar código de Personal_Proyect
- Entender flujo: Cliente → Gateway → API
- Identificar patrones usados

### Día 3-4: Practicar Backend
- Crear proyecto Spring Boot desde cero
- Implementar un endpoint simple
- Agregar cache

### Día 5-6: Practicar Frontend
- Crear app React básica
- Hacer petición a API
- Mostrar datos en tabla/gráfico

### Día 7: Integración
- Conectar frontend con backend
- Probar flujo completo
- Verificar cache funciona

### Día 8: Cliente Java y Concurrencia
- Crear cliente de pruebas
- Ejecutar 20 peticiones simultáneas
- Medir tiempos

### Día 9: Documentación
- Escribir README completo
- Agregar Javadoc
- Crear diagramas

### Día 10: Despliegue
- Subir a AWS/Azure
- Configurar variables de entorno
- Probar en producción

---

## 🎯 TIPS PARA EL PARCIAL

1. **Empieza por el backend**: Es la base de todo
2. **Usa la solución de referencia**: No reinventes la rueda
3. **Documenta mientras codificas**: No dejes para el final
4. **Prueba frecuentemente**: No esperes a tener todo listo
5. **Git commits frecuentes**: Guarda tu progreso
6. **API Key de Alpha Vantage**: Consíguela desde el día 1
7. **Cache es crítico**: 10% de la nota
8. **Diseño vale 25-30%**: Invierte tiempo en patrones y documentación
9. **Cliente Java**: No lo olvides (10%)
10. **Extensibilidad**: Demuestra que entiendes cómo agregar proveedores

---

## 📚 RECURSOS ÚTILES

- **Alpha Vantage**: https://www.alphavantage.co/documentation/
- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **React Docs**: https://react.dev/
- **Recharts**: https://recharts.org/
- **AWS Elastic Beanstalk**: https://aws.amazon.com/elasticbeanstalk/
- **Maven**: https://maven.apache.org/guides/

---

## ✅ AUTOEVALUACIÓN

Antes del parcial, verifica que puedes responder:

- [ ] ¿Qué es un Gateway y por qué lo usamos?
- [ ] ¿Qué patrón permite cambiar de proveedor fácilmente?
- [ ] ¿Por qué usamos ConcurrentHashMap y no HashMap?
- [ ] ¿Cómo funciona la inyección de dependencias en Spring?
- [ ] ¿Qué es REST y cuáles son sus principios?
- [ ] ¿Cómo se hace una petición asíncrona en React?
- [ ] ¿Qué hace el cliente Java de pruebas?
- [ ] ¿Cómo agregarías un nuevo proveedor de datos?
- [ ] ¿Qué comandos Maven necesitas para compilar y ejecutar?
- [ ] ¿Cómo despliegas en AWS?

---

**¡Éxito en tu parcial! 🚀**
