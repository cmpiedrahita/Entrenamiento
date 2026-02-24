# 🎓 CONCEPTOS TEÓRICOS - RESUMEN RÁPIDO

## 📚 TEMA 1: DISEÑO ORIENTADO A OBJETOS Y TIPADO SEGURO

### Estructuras de Java

**Clases**
- Definen tipo + implementación
- Pueden tener estado (atributos)
- Pueden tener comportamiento (métodos)
```java
public class AlphaVantageService {
    private String apiKey;  // Estado
    public String getData() { ... }  // Comportamiento
}
```

**Interfaces**
- Solo definen tipo (contrato)
- No tienen implementación (Java 8+ permite default methods)
- Permiten polimorfismo
```java
public interface StockService {
    String getDailyData(String symbol);  // Solo firma
}
```

**Clases Abstractas**
- Mezcla de ambos
- Pueden tener métodos abstractos y concretos
- No se pueden instanciar directamente
```java
public abstract class BaseService {
    public abstract String getData();  // Abstracto
    public void log() { ... }  // Concreto
}
```

### Polimorfismo

**Definición**: Un objeto puede comportarse como múltiples tipos.

```java
// AlphaVantageService ES UN StockService
StockService service = new AlphaVantageService();

// Puedes cambiarlo por otra implementación
service = new YahooFinanceService();

// El controlador no necesita cambiar
public StockController(StockService service) {
    this.service = service;  // Funciona con cualquier implementación
}
```

**Ventajas:**
- Flexibilidad
- Extensibilidad
- Facilita testing (mocks)

### Genéricos (Generics)

**Propósito**: Tipo seguro en tiempo de compilación.

```java
// Sin genéricos (viejo)
List list = new ArrayList();
list.add("texto");
list.add(123);
String s = (String) list.get(1);  // ❌ ClassCastException en runtime

// Con genéricos (moderno)
List<String> list = new ArrayList<>();
list.add("texto");
list.add(123);  // ❌ Error de compilación (detectado temprano)
```

**En el proyecto:**
```java
ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
// Garantiza que keys y values son String
```

### Wildcards (?)

**Uso**: Cuando el tipo exacto es desconocido.

```java
// Leer de cualquier lista de Shape o subclases
public void drawAll(List<? extends Shape> shapes) {
    for (Shape s : shapes) {
        s.draw();  // ✅ Puedo leer
    }
    shapes.add(new Circle());  // ❌ No puedo escribir (protección)
}

// Funciona con:
List<Circle> circles = ...;
drawAll(circles);  // Circle extends Shape
```

**Regla PECS**: Producer Extends, Consumer Super
- `? extends T`: Leer (producer)
- `? super T`: Escribir (consumer)

---

## 📚 TEMA 2: PROGRAMACIÓN FUNCIONAL Y LAMBDAS

### Expresiones Lambda

**Concepto**: Código como datos, funcionalidad como argumento.

```java
// Antes (clase anónima)
Runnable r = new Runnable() {
    @Override
    public void run() {
        System.out.println("Hola");
    }
};

// Ahora (lambda)
Runnable r = () -> System.out.println("Hola");
```

**En el proyecto:**
```java
executor.submit(() -> {
    // Código que se ejecuta en otro hilo
    makeHttpRequest();
});
```

### Interfaces Funcionales

**Definición**: Interface con un solo método abstracto.

**Principales:**

1. **Predicate<T>** - Prueba booleana
```java
Predicate<String> isEmpty = s -> s.isEmpty();
isEmpty.test("hola");  // false
```

2. **Consumer<T>** - Consume un valor
```java
Consumer<String> print = s -> System.out.println(s);
print.accept("hola");
```

3. **Function<T, R>** - Transforma T en R
```java
Function<String, Integer> length = s -> s.length();
length.apply("hola");  // 5
```

### Streams

**Propósito**: Procesar colecciones de forma declarativa.

```java
List<String> symbols = Arrays.asList("IBM", "MSFT", "AAPL", "GOOGL");

// Filtrar y transformar
symbols.stream()
    .filter(s -> s.length() <= 4)  // IBM, MSFT, AAPL
    .map(String::toLowerCase)       // ibm, msft, aapl
    .forEach(System.out::println);  // Imprimir cada uno
```

**Operaciones:**
- `filter()`: Filtrar elementos
- `map()`: Transformar elementos
- `forEach()`: Ejecutar acción en cada elemento
- `collect()`: Recolectar en colección
- `reduce()`: Reducir a un valor

---

## 📚 TEMA 3: CONCURRENCIA Y MANEJO DE HILOS

### Estrategias de Concurrencia

**1. Memoria Compartida (Java)**
- Múltiples hilos acceden a misma memoria
- Rápido pero requiere sincronización
- Riesgo: Race conditions

**2. Paso de Mensajes (Go)**
- Hilos se comunican por canales
- No hay memoria compartida
- Más seguro pero más lento

### Sincronización en Java

**synchronized**
```java
// Método sincronizado
public synchronized void increment() {
    count++;  // Solo un hilo a la vez
}

// Bloque sincronizado
public void increment() {
    synchronized(this) {
        count++;
    }
}
```

**Problema**: Puede causar deadlocks y es lento.

### wait() y notifyAll()

**Uso**: Coordinar hilos basados en condiciones.

```java
public synchronized void produce() {
    while (queue.isFull()) {
        wait();  // Esperar hasta que haya espacio
    }
    queue.add(item);
    notifyAll();  // Notificar a consumidores
}

public synchronized void consume() {
    while (queue.isEmpty()) {
        wait();  // Esperar hasta que haya items
    }
    Item item = queue.remove();
    notifyAll();  // Notificar a productores
}
```

### Executors y Thread Pools

**Problema**: Crear hilos es costoso.

**Solución**: Reutilizar hilos con pool.

```java
// Crear pool de 10 hilos
ExecutorService executor = Executors.newFixedThreadPool(10);

// Enviar tareas
for (int i = 0; i < 100; i++) {
    executor.submit(() -> {
        // Tarea
    });
}

// Cerrar pool
executor.shutdown();
executor.awaitTermination(1, TimeUnit.MINUTES);
```

**Ventajas:**
- Limita número de hilos
- Reutiliza hilos
- Mejor rendimiento

### Callable y Future

**Diferencia con Runnable**: Puede retornar valor.

```java
// Runnable: No retorna nada
Runnable task = () -> System.out.println("Hola");

// Callable: Retorna valor
Callable<String> task = () -> {
    return "Resultado";
};

// Obtener resultado
Future<String> future = executor.submit(task);
String result = future.get();  // Bloquea hasta que termine
```

### Inmutabilidad

**Concepto**: Objetos que no pueden cambiar después de crearse.

```java
public final class ImmutableStock {
    private final String symbol;
    private final double price;
    
    public ImmutableStock(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
    }
    
    public String getSymbol() { return symbol; }
    public double getPrice() { return price; }
    // No hay setters
}
```

**Ventajas:**
- Thread-safe automáticamente
- No necesita sincronización
- Más fácil de razonar

**En el proyecto:**
```java
// ConcurrentHashMap es thread-safe
// No necesitamos synchronized
private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
```

---

## 📚 TEMA 4: ARQUITECTURA Y ESTILOS DE COMUNICACIÓN

### Componentes y Conectores (C&C)

**Vista Arquitectónica**: Describe cómo se comunican las partes.

```
┌─────────────┐         ┌─────────────┐
│  Component  │ Puerto  │  Component  │
│      A      │◄───────►│      B      │
└─────────────┘         └─────────────┘
```

**Elementos:**
- **Componentes**: Unidades de computación (servicios)
- **Conectores**: Canales de comunicación (HTTP, mensajes)
- **Puertos**: Interfaces de entrada/salida

### SOA (Arquitectura Orientada a Servicios)

**Principios:**

1. **Bajo Acoplamiento**
   - Servicios independientes
   - Cambios en uno no afectan otros

2. **Abstracción**
   - Ocultar detalles de implementación
   - Solo exponer interfaz

3. **Stateless (Sin Estado)**
   - Cada petición es independiente
   - No guardar estado entre peticiones
   - Facilita escalabilidad

4. **Descubrimiento**
   - Fácil encontrar y usar servicios
   - Documentación clara (OpenAPI/Swagger)

**En el proyecto:**
```
React Client (Servicio 1)
    ↓ HTTP/JSON
Spring Gateway (Servicio 2)
    ↓ HTTP/JSON
Alpha Vantage API (Servicio 3)
```

### SOAP vs REST

#### SOAP (Simple Object Access Protocol)

**Características:**
- Protocolo (reglas estrictas)
- Basado en XML
- Usa "sobre" (Envelope)
- WSDL para descripción

**Estructura:**
```xml
<soap:Envelope>
    <soap:Header>
        <!-- Metadatos -->
    </soap:Header>
    <soap:Body>
        <!-- Datos -->
    </soap:Body>
</soap:Envelope>
```

**Ventajas:**
- Estándar bien definido
- Seguridad integrada (WS-Security)
- Transacciones (WS-Transaction)

**Desventajas:**
- Pesado (mucho XML)
- Complejo
- Lento

#### REST (Representational State Transfer)

**Características:**
- Estilo arquitectónico (no protocolo)
- Usa HTTP explícitamente
- Basado en recursos
- Formato flexible (JSON, XML)

**Principios:**

1. **Recursos (Sustantivos)**
```
/api/stocks/IBM        ✅
/api/getStock?id=IBM   ❌
```

2. **Verbos HTTP**
- GET: Leer
- POST: Crear
- PUT: Actualizar
- DELETE: Eliminar

3. **Representaciones**
```json
{
  "symbol": "IBM",
  "price": 150.25
}
```

4. **Stateless**
- Cada petición tiene toda la info necesaria
- No sesiones en servidor

5. **Cacheable**
- Respuestas pueden cachearse
- Mejora rendimiento

**Mejores Prácticas:**

1. **Nombres en plural**
```
/api/stocks     ✅
/api/stock      ❌
```

2. **GET no altera estado**
```java
@GetMapping("/stocks/{id}")  // Solo lee
@PostMapping("/stocks")      // Crea
```

3. **Códigos HTTP correctos**
- 200: OK
- 201: Created
- 400: Bad Request
- 404: Not Found
- 500: Server Error

4. **Versionamiento**
```
/api/v1/stocks
/api/v2/stocks
```

### Comparación SOAP vs REST

| Aspecto | SOAP | REST |
|---------|------|------|
| Tipo | Protocolo | Estilo |
| Formato | XML | JSON, XML |
| Complejidad | Alta | Baja |
| Rendimiento | Lento | Rápido |
| Uso | Empresas grandes | Web, móviles |
| Seguridad | WS-Security | HTTPS, OAuth |

**En el proyecto usamos REST porque:**
- Más simple
- Más rápido
- JSON es más ligero
- Mejor para web

---

## 🎯 APLICACIÓN AL PROYECTO

### Diseño OO
- **Interfaces**: `StockService`
- **Polimorfismo**: Múltiples implementaciones
- **Genéricos**: `ConcurrentHashMap<String, String>`

### Programación Funcional
- **Lambdas**: En cliente concurrente
```java
executor.submit(() -> makeRequest());
```

### Concurrencia
- **Thread Pool**: `Executors.newFixedThreadPool(20)`
- **Thread-safe**: `ConcurrentHashMap`
- **Inmutabilidad**: Strings en cache

### Arquitectura
- **SOA**: Servicios independientes
- **REST**: API del gateway
- **C&C**: React → Spring → Alpha Vantage

---

## 📝 PREGUNTAS DE EXAMEN TÍPICAS

### 1. ¿Por qué usar interfaz StockService?
**R:** Permite cambiar de proveedor (Alpha Vantage → Yahoo Finance) sin modificar el controlador. Aplica el patrón Strategy y el principio Open/Closed.

### 2. ¿Por qué ConcurrentHashMap y no HashMap?
**R:** HashMap no es thread-safe. En aplicación multiusuario, múltiples hilos acceden al cache simultáneamente. ConcurrentHashMap permite acceso concurrente sin corrupción de datos.

### 3. ¿Qué patrón usa el controlador?
**R:** Gateway/Fachada. Actúa como punto único de entrada, encapsulando la complejidad de llamadas a servicios externos.

### 4. ¿Por qué REST y no SOAP?
**R:** REST es más simple, rápido y ligero. JSON es más fácil de parsear que XML. Mejor para aplicaciones web modernas.

### 5. ¿Cómo funciona la inyección de dependencias?
**R:** Spring crea y gestiona objetos automáticamente. Cuando el controlador necesita StockService, Spring inyecta la implementación disponible (AlphaVantageService).

### 6. ¿Qué ventaja tiene el cache?
**R:** Evita llamadas repetidas a la API externa, mejorando rendimiento (10-50ms vs 500-1000ms) y reduciendo costos (Alpha Vantage tiene límite de 5 llamadas/minuto).

### 7. ¿Cómo agregarías un nuevo proveedor?
**R:** 
1. Crear clase que implemente StockService
2. Agregar @Service
3. Usar @Qualifier en controlador
4. No modificar código existente

### 8. ¿Por qué usar ExecutorService?
**R:** Limita el número de hilos, reutiliza hilos existentes, y evita crear miles de hilos que sobrecargarían el sistema.

---

**¡Domina estos conceptos y estarás listo! 💪**
