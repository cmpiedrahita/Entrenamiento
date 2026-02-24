# 🚀 PROYECTO BASE - STOCK MARKET VIEWER

## 📋 ESTRUCTURA DEL PROYECTO

```
Base/
├── backend/                    # Spring Boot (Gateway)
│   ├── src/main/java/com/stockmarket/
│   │   ├── StockMarketApplication.java
│   │   ├── controller/
│   │   │   └── StockController.java
│   │   ├── service/
│   │   │   ├── StockService.java
│   │   │   └── AlphaVantageService.java
│   │   └── cache/
│   │       └── ConcurrentCache.java
│   └── pom.xml
├── frontend/                   # React App
│   ├── src/
│   │   ├── App.js
│   │   ├── App.css
│   │   └── index.js
│   └── package.json
└── java-client/               # Cliente de pruebas
    └── src/main/java/com/stockmarket/client/
        └── ConcurrentTestClient.java
```

## ⚙️ CONFIGURACIÓN INICIAL

### 1. Backend (Spring Boot)

```bash
cd backend

# MODIFICAR: application.properties
# Cambiar: alphavantage.apikey=TU_API_KEY_AQUI

mvn clean install
mvn spring-boot:run
```

**Servidor corriendo en:** http://localhost:8080

### 2. Frontend (React)

```bash
cd frontend

# MODIFICAR: src/App.js línea 30
# Cambiar URL si despliegas en AWS/Azure

npm install
npm start
```

**App corriendo en:** http://localhost:3000

### 3. Cliente Java

```bash
cd java-client

# MODIFICAR: ConcurrentTestClient.java línea 40
# Cambiar BASE_URL si despliegas

mvn clean compile
mvn exec:java -Dexec.mainClass="com.stockmarket.client.ConcurrentTestClient"
```

## 🎯 PUNTOS CLAVE PARA MODIFICAR

### ⚠️ OBLIGATORIO MODIFICAR:

1. **API Key de Alpha Vantage**
   - Archivo: `backend/src/main/resources/application.properties`
   - Línea: `alphavantage.apikey=demo`
   - Obtener en: https://www.alphavantage.co/support/#api-key

2. **URL del Backend en Frontend**
   - Archivo: `frontend/src/App.js`
   - Línea 30: `const BACKEND_URL = 'http://localhost:8080/api/stocks';`
   - Cambiar cuando despliegues en AWS/Azure

3. **URL del Backend en Cliente Java**
   - Archivo: `java-client/src/main/java/com/stockmarket/client/ConcurrentTestClient.java`
   - Línea 40: `private static final String BASE_URL = "http://localhost:8080/api/stocks";`

### 🔧 MODIFICACIONES COMUNES:

#### Agregar Nuevo Intervalo

**Backend - StockService.java:**
```java
String getHourlyData(String symbol);
```

**Backend - AlphaVantageService.java:**
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

**Backend - StockController.java:**
```java
@GetMapping("/{symbol}/hourly")
public ResponseEntity<String> getHourly(@PathVariable String symbol) {
    return ResponseEntity.ok(stockService.getHourlyData(symbol));
}
```

**Frontend - App.js (línea 115):**
```javascript
<option value="hourly">Por Hora</option>
```

#### Agregar Nuevo Proveedor (Yahoo Finance)

**1. Crear YahooFinanceService.java:**
```java
@Service
public class YahooFinanceService implements StockService {
    // Implementar métodos con API de Yahoo
}
```

**2. Usar @Qualifier en StockController.java:**
```java
public StockController(@Qualifier("yahooFinanceService") StockService stockService) {
    this.stockService = stockService;
}
```

#### Cambiar Librería de Gráficos (Chart.js)

**Frontend - Instalar:**
```bash
npm install react-chartjs-2 chart.js
```

**Frontend - App.js:**
```javascript
import { Line } from 'react-chartjs-2';

const chartData = {
  labels: data.map(d => d.date),
  datasets: [{
    label: 'Precio',
    data: data.map(d => d.price),
    borderColor: 'rgb(75, 192, 192)',
  }]
};

<Line data={chartData} />
```

## 📊 CONCEPTOS APLICADOS

### Backend
- ✅ **Patrón Gateway/Fachada**: StockController
- ✅ **Patrón Strategy**: StockService (interfaz)
- ✅ **Patrón Cache**: ConcurrentCache
- ✅ **Dependency Injection**: Spring autowiring
- ✅ **Polimorfismo**: Múltiples implementaciones de StockService
- ✅ **Genéricos**: ConcurrentHashMap<String, String>
- ✅ **Concurrencia**: ConcurrentHashMap (thread-safe)
- ✅ **REST**: Endpoints con verbos HTTP
- ✅ **SOA**: Servicios independientes

### Frontend
- ✅ **Hooks**: useState
- ✅ **Async/Await**: Peticiones asíncronas
- ✅ **Programación Funcional**: Componentes funcionales
- ✅ **REST Client**: fetch API

### Cliente Java
- ✅ **Thread Pool**: ExecutorService
- ✅ **Lambdas**: Expresiones lambda
- ✅ **Concurrencia**: Múltiples hilos

## 🧪 PROBAR EL SISTEMA

### 1. Probar Backend
```bash
curl http://localhost:8080/api/stocks/IBM/daily
```

### 2. Probar Frontend
- Abrir http://localhost:3000
- Ingresar "IBM"
- Seleccionar "Diario"
- Clic en "Buscar"

### 3. Probar Cache
```bash
# Primera vez (lento)
curl http://localhost:8080/api/stocks/IBM/daily

# Segunda vez (rápido - desde cache)
curl http://localhost:8080/api/stocks/IBM/daily
```

### 4. Probar Concurrencia
```bash
cd java-client
mvn exec:java
```

**Resultado esperado:**
- Primeras peticiones: 500-1000ms (cache MISS)
- Siguientes: 10-50ms (cache HIT)

## 📝 COMANDOS ÚTILES

### Maven
```bash
mvn clean install      # Compilar
mvn spring-boot:run    # Ejecutar backend
mvn package           # Generar JAR
mvn javadoc:javadoc   # Generar documentación
```

### NPM
```bash
npm install           # Instalar dependencias
npm start            # Modo desarrollo
npm run build        # Build producción
```

### Git
```bash
git init
git add .
git commit -m "Initial commit"
git remote add origin <url>
git push -u origin main
```

## ☁️ DESPLIEGUE

### AWS - Backend (Elastic Beanstalk)
```bash
cd backend
mvn clean package
eb init -p java-17 stock-market
eb create stock-market-env
eb deploy
```

### AWS - Frontend (S3)
```bash
cd frontend
npm run build
aws s3 sync build/ s3://tu-bucket
```

### Azure - Backend (App Service)
```bash
cd backend
mvn clean package
az webapp deploy --resource-group rg --name app-name --src-path target/*.jar
```

## 🎓 PARA EL PARCIAL

### Checklist Antes de Entregar
- [ ] API Key configurada
- [ ] Backend funciona localmente
- [ ] Frontend funciona localmente
- [ ] Cliente Java ejecuta correctamente
- [ ] Cache demuestra mejora de rendimiento
- [ ] Código documentado (Javadoc)
- [ ] README completo
- [ ] Subido a GitHub
- [ ] Desplegado en AWS/Azure (si aplica)

### Archivos Importantes
- `application.properties` - Configuración
- `StockService.java` - Interfaz (extensibilidad)
- `ConcurrentCache.java` - Cache thread-safe
- `App.js` - Frontend React
- `ConcurrentTestClient.java` - Pruebas

## 🆘 SOLUCIÓN DE PROBLEMAS

### Backend no arranca
- Verificar Java 17: `java -version`
- Verificar Maven: `mvn -version`
- Limpiar: `mvn clean install`

### Frontend no conecta
- Verificar CORS en StockController
- Verificar URL en App.js línea 30
- Verificar backend está corriendo

### Cache no funciona
- Verificar ConcurrentHashMap (no HashMap)
- Verificar logs en consola
- Probar con curl dos veces

## 📚 RECURSOS

- Alpha Vantage: https://www.alphavantage.co/documentation/
- Spring Boot: https://spring.io/projects/spring-boot
- React: https://react.dev/
- Recharts: https://recharts.org/

---

**¡Listo para usar! Modifica según las instrucciones del parcial. 🚀**
