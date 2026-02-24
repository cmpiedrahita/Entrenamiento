# ✅ CHECKLIST PARCIAL - DÍA DEL EXAMEN

## 🎯 ANTES DE EMPEZAR (15 minutos)

### Preparación del Entorno
- [ ] Verificar que Java 17 está instalado: `java -version`
- [ ] Verificar que Maven está instalado: `mvn -version`
- [ ] Verificar que Node.js está instalado: `node -version`
- [ ] Tener cuenta de GitHub lista
- [ ] Tener cuenta de AWS/Azure lista
- [ ] **CRÍTICO**: Obtener API Key de Alpha Vantage (https://www.alphavantage.co/support/#api-key)
- [ ] Tener IDE abierto (IntelliJ, VS Code, Eclipse)
- [ ] Tener navegador con pestañas útiles abiertas

### Recursos a Tener Abiertos
- [ ] Documentación de Spring Boot
- [ ] Documentación de React
- [ ] Documentación de Alpha Vantage
- [ ] Proyecto de referencia (Personal_Proyect)
- [ ] Esta guía de estudio

---

## 📂 PASO 1: Crear Repositorio GitHub (5 minutos)

- [ ] Ir a GitHub y crear nuevo repositorio
- [ ] Nombre: `stock-market-parcial` (o similar)
- [ ] Público o privado según instrucciones
- [ ] Inicializar con README
- [ ] Clonar localmente: `git clone <url>`
- [ ] Crear estructura de carpetas:
```
stock-market-parcial/
├── backend/
├── frontend/
├── java-client/
└── README.md
```

---

## 🔧 PASO 2: Backend - Spring Boot (60 minutos)

### 2.1 Crear Proyecto Maven (10 min)
- [ ] Crear carpeta `backend/`
- [ ] Crear `pom.xml` con dependencias:
  - spring-boot-starter-web
  - spring-boot-starter-webflux
- [ ] Verificar que compila: `mvn clean install`

### 2.2 Estructura de Paquetes (5 min)
```
backend/src/main/java/com/stockmarket/
├── StockMarketApplication.java
├── controller/
│   └── StockController.java
├── service/
│   ├── StockService.java (interfaz)
│   └── AlphaVantageService.java
└── cache/
    └── ConcurrentCache.java
```

### 2.3 Implementar Componentes (45 min)

#### Clase Principal (5 min)
- [ ] Crear `StockMarketApplication.java`
- [ ] Agregar `@SpringBootApplication`
- [ ] Agregar método `main`

#### Cache Concurrente (10 min)
- [ ] Crear `ConcurrentCache.java`
- [ ] Agregar `@Component`
- [ ] Usar `ConcurrentHashMap<String, String>`
- [ ] Métodos: `get()`, `put()`, `containsKey()`

#### Interfaz de Servicio (5 min)
- [ ] Crear `StockService.java` (interfaz)
- [ ] Métodos:
  - `String getIntradayData(String symbol)`
  - `String getDailyData(String symbol)`
  - `String getWeeklyData(String symbol)`
  - `String getMonthlyData(String symbol)`

#### Implementación Alpha Vantage (15 min)
- [ ] Crear `AlphaVantageService.java`
- [ ] Agregar `@Service`
- [ ] Implementar `StockService`
- [ ] Inyectar `ConcurrentCache`
- [ ] Crear `WebClient` con baseUrl
- [ ] Implementar cada método:
  1. Verificar cache
  2. Si existe, retornar
  3. Si no, llamar API
  4. Guardar en cache
  5. Retornar
- [ ] Usar `@Value("${alphavantage.apikey}")` para API key

#### Controlador REST (10 min)
- [ ] Crear `StockController.java`
- [ ] Agregar `@RestController`
- [ ] Agregar `@RequestMapping("/api/stocks")`
- [ ] Agregar `@CrossOrigin(origins = "*")`
- [ ] Inyectar `StockService`
- [ ] Crear endpoints:
  - `GET /{symbol}/intraday`
  - `GET /{symbol}/daily`
  - `GET /{symbol}/weekly`
  - `GET /{symbol}/monthly`

#### Configuración (5 min)
- [ ] Crear `src/main/resources/application.properties`
- [ ] Agregar: `alphavantage.apikey=TU_API_KEY`
- [ ] Agregar: `server.port=8080`

### 2.4 Probar Backend (10 min)
- [ ] Ejecutar: `mvn spring-boot:run`
- [ ] Probar en navegador: `http://localhost:8080/api/stocks/IBM/daily`
- [ ] Verificar que retorna JSON
- [ ] Probar segunda vez (debe ser más rápido por cache)

---

## ⚛️ PASO 3: Frontend - React (45 minutos)

### 3.1 Crear Proyecto (5 min)
- [ ] `npx create-react-app frontend`
- [ ] `cd frontend`
- [ ] `npm start` (verificar que funciona)

### 3.2 Instalar Dependencias (5 min)
- [ ] `npm install recharts`
- [ ] `npm install axios` (opcional, puedes usar fetch)

### 3.3 Implementar App.js (30 min)

#### Estado y Hooks (10 min)
- [ ] Importar `useState`, `useEffect`
- [ ] Estado para:
  - `symbol` (símbolo de acción)
  - `interval` (intraday, daily, weekly, monthly)
  - `data` (datos de la API)
  - `loading` (indicador de carga)

#### Función de Búsqueda (10 min)
- [ ] Crear función `fetchStockData()`
- [ ] Hacer petición a: `http://localhost:8080/api/stocks/${symbol}/${interval}`
- [ ] Actualizar estado con respuesta
- [ ] Manejar errores

#### UI (10 min)
- [ ] Input para símbolo
- [ ] Select para intervalo (intraday, daily, weekly, monthly)
- [ ] Botón de búsqueda
- [ ] Mostrar loading mientras carga
- [ ] Mostrar gráfico con datos

### 3.4 Gráfico con Recharts (10 min)
- [ ] Importar componentes de Recharts
- [ ] Transformar datos de Alpha Vantage a formato Recharts
- [ ] Crear `LineChart` con:
  - `XAxis` (fechas)
  - `YAxis` (precios)
  - `Line` (datos)
  - `Tooltip`
  - `CartesianGrid`

### 3.5 Estilos (5 min)
- [ ] Agregar CSS básico en `App.css`
- [ ] Centrar contenido
- [ ] Hacer inputs y botones atractivos
- [ ] Responsive (opcional)

### 3.6 Probar Frontend (5 min)
- [ ] Verificar que backend está corriendo
- [ ] Buscar "IBM" con intervalo "daily"
- [ ] Verificar que muestra gráfico
- [ ] Probar con otros símbolos

---

## ☕ PASO 4: Cliente Java (30 minutos)

### 4.1 Crear Proyecto Maven (5 min)
- [ ] Crear carpeta `java-client/`
- [ ] Crear `pom.xml` básico

### 4.2 Implementar Cliente (20 min)
- [ ] Crear `ConcurrentTestClient.java`
- [ ] Crear `ExecutorService` con 20 hilos
- [ ] Array de símbolos: `["IBM", "MSFT", "AAPL"]`
- [ ] Loop de 20 iteraciones:
  - Crear tarea que:
    1. Hace petición HTTP a backend
    2. Mide tiempo de respuesta
    3. Imprime resultado
- [ ] Shutdown del executor
- [ ] Esperar terminación

### 4.3 Probar Cliente (5 min)
- [ ] Compilar: `mvn clean compile`
- [ ] Ejecutar: `mvn exec:java -Dexec.mainClass="..."`
- [ ] Verificar salida:
  - Primeras peticiones: lentas (500-1000ms)
  - Siguientes: rápidas (10-50ms)
- [ ] Esto demuestra que el cache funciona

---

## 📝 PASO 5: Documentación (30 minutos)

### 5.1 Javadoc (15 min)
- [ ] Agregar comentarios Javadoc a:
  - Todas las clases públicas
  - Todos los métodos públicos
  - Parámetros con `@param`
  - Retornos con `@return`
- [ ] Generar Javadoc: `mvn javadoc:javadoc`

### 5.2 README.md (15 min)

Debe incluir:
- [ ] **Título y descripción** del proyecto
- [ ] **Diagrama de arquitectura** (puede ser ASCII art)
- [ ] **Tecnologías utilizadas**
- [ ] **Patrones de diseño implementados**:
  - Gateway/Fachada
  - Strategy
  - Cache
  - Dependency Injection
- [ ] **Estructura del proyecto** (árbol de carpetas)
- [ ] **Cómo ejecutar**:
  - Backend
  - Frontend
  - Cliente Java
- [ ] **Endpoints REST** (tabla)
- [ ] **Cómo extender**:
  - Agregar nuevo proveedor
  - Agregar nueva funcionalidad
- [ ] **Cache concurrente** (explicación)
- [ ] **Pruebas de concurrencia** (resultados)
- [ ] **URLs**:
  - GitHub
  - AWS/Azure backend
  - AWS/Azure frontend

---

## ☁️ PASO 6: Despliegue AWS/Azure (45 minutos)

### Opción A: AWS

#### Backend en Elastic Beanstalk (20 min)
- [ ] Crear JAR: `mvn clean package`
- [ ] Instalar AWS CLI (si no está)
- [ ] Instalar EB CLI: `pip install awsebcli`
- [ ] `eb init -p java-17 stock-market-app`
- [ ] `eb create stock-market-env`
- [ ] Configurar variable de entorno: `ALPHAVANTAGE_APIKEY`
- [ ] `eb deploy`
- [ ] Obtener URL: `eb status`
- [ ] Probar: `curl <url>/api/stocks/IBM/daily`

#### Frontend en S3 + CloudFront (25 min)
- [ ] Build de producción: `npm run build`
- [ ] Crear bucket S3: `aws s3 mb s3://stock-market-frontend-<tu-nombre>`
- [ ] Subir archivos: `aws s3 sync build/ s3://stock-market-frontend-<tu-nombre>`
- [ ] Configurar como sitio web:
  ```bash
  aws s3 website s3://stock-market-frontend-<tu-nombre> \
    --index-document index.html \
    --error-document index.html
  ```
- [ ] Hacer bucket público (política de bucket)
- [ ] Obtener URL del sitio web
- [ ] Actualizar URL del backend en React
- [ ] Re-build y re-subir
- [ ] Probar en navegador

### Opción B: Azure

#### Backend en App Service (20 min)
- [ ] Crear JAR: `mvn clean package`
- [ ] Instalar Azure CLI
- [ ] `az login`
- [ ] `az group create --name stock-market-rg --location eastus`
- [ ] `az appservice plan create --name stock-market-plan --resource-group stock-market-rg --sku B1 --is-linux`
- [ ] `az webapp create --resource-group stock-market-rg --plan stock-market-plan --name stock-market-backend-<tu-nombre> --runtime "JAVA:17-java17"`
- [ ] `az webapp config appsettings set --resource-group stock-market-rg --name stock-market-backend-<tu-nombre> --settings ALPHAVANTAGE_APIKEY=<tu-key>`
- [ ] `az webapp deploy --resource-group stock-market-rg --name stock-market-backend-<tu-nombre> --src-path target/stock-gateway-1.0.0.jar`
- [ ] Obtener URL y probar

#### Frontend en Static Web Apps (25 min)
- [ ] Build: `npm run build`
- [ ] `az staticwebapp create --name stock-market-frontend --resource-group stock-market-rg --source build/ --location eastus`
- [ ] Obtener URL
- [ ] Actualizar URL del backend en React
- [ ] Re-build y re-deploy

#### CI/CD con Azure DevOps (BONO - 30 min)
- [ ] Crear proyecto en Azure DevOps
- [ ] Conectar repositorio GitHub
- [ ] Crear pipeline YAML
- [ ] Configurar build y deploy automático
- [ ] Hacer commit y verificar que se despliega

---

## 🔍 PASO 7: Verificación Final (15 minutos)

### Funcionalidad
- [ ] Frontend desplegado funciona
- [ ] Backend desplegado funciona
- [ ] Se pueden buscar diferentes acciones
- [ ] Gráficos se muestran correctamente
- [ ] Cache mejora rendimiento

### Código
- [ ] Todo está en GitHub
- [ ] Commits tienen mensajes descriptivos
- [ ] Código está documentado (Javadoc)
- [ ] No hay API keys hardcodeadas

### Documentación
- [ ] README completo
- [ ] URLs de GitHub y AWS/Azure incluidas
- [ ] Instrucciones claras de ejecución
- [ ] Explicación de extensibilidad

### Capturas de Pantalla
- [ ] Aplicación funcionando en local
- [ ] Aplicación funcionando en AWS/Azure
- [ ] Cliente Java ejecutándose
- [ ] Resultados de pruebas de concurrencia

---

## 📊 DISTRIBUCIÓN DE TIEMPO SUGERIDA

| Tarea | Tiempo | Acumulado |
|-------|--------|-----------|
| Preparación | 15 min | 0:15 |
| GitHub | 5 min | 0:20 |
| Backend | 60 min | 1:20 |
| Frontend | 45 min | 2:05 |
| Cliente Java | 30 min | 2:35 |
| Documentación | 30 min | 3:05 |
| Despliegue | 45 min | 3:50 |
| Verificación | 15 min | 4:05 |
| **Buffer** | 25 min | 4:30 |

**Total: 4.5 horas** (ajustar según tiempo del parcial)

---

## 🚨 ERRORES COMUNES A EVITAR

### Backend
- [ ] ❌ Olvidar `@CrossOrigin` → Frontend no puede conectar
- [ ] ❌ Usar HashMap en vez de ConcurrentHashMap → No thread-safe
- [ ] ❌ Hardcodear API key → Mala práctica
- [ ] ❌ No verificar cache antes de llamar API → Desperdicio
- [ ] ❌ Olvidar `@Service` o `@Component` → Spring no inyecta

### Frontend
- [ ] ❌ No manejar estado de loading → Mala UX
- [ ] ❌ No manejar errores → App se rompe
- [ ] ❌ URL del backend hardcodeada → No funciona en producción
- [ ] ❌ No transformar datos para Recharts → Gráfico no se muestra

### General
- [ ] ❌ No hacer commits frecuentes → Pierdes trabajo
- [ ] ❌ No probar antes de desplegar → Errores en producción
- [ ] ❌ README incompleto → Pierdes puntos de documentación
- [ ] ❌ No explicar extensibilidad → Pierdes puntos de diseño

---

## 💡 TIPS DE ÚLTIMO MINUTO

1. **Si algo no funciona, usa Mock**:
   ```java
   @Service
   public class MockStockService implements StockService {
       public String getDailyData(String symbol) {
           return "{\"mock\":\"data\"}";
       }
   }
   ```

2. **Si Alpha Vantage falla, usa JSONPlaceholder**:
   - Más confiable para demos
   - No requiere API key

3. **Si el despliegue falla**:
   - Toma capturas de pantalla de local
   - Explica en README qué intentaste
   - Muestra que funciona localmente

4. **Prioriza lo que vale más puntos**:
   - Diseño y documentación: 25-30%
   - Conexión a API externa: 20%
   - Cache y cliente Java: 20%

5. **Commits frecuentes**:
   - Cada componente que funcione
   - Facilita volver atrás si algo se rompe

---

## ✅ CHECKLIST FINAL ANTES DE ENTREGAR

- [ ] Código en GitHub (público o privado según instrucciones)
- [ ] README.md completo con todas las secciones
- [ ] Backend desplegado y funcionando (o capturas de local)
- [ ] Frontend desplegado y funcionando (o capturas de local)
- [ ] Cliente Java implementado
- [ ] Javadoc generado
- [ ] URLs incluidas en README:
  - [ ] GitHub
  - [ ] Backend AWS/Azure
  - [ ] Frontend AWS/Azure
- [ ] Capturas de pantalla incluidas
- [ ] Código documentado
- [ ] Patrones de diseño explicados
- [ ] Extensibilidad demostrada

---

## 🎯 CRITERIOS DE AUTOEVALUACIÓN

### Excelente (90-100%)
- Todo funciona perfectamente
- Código limpio y bien documentado
- Patrones claramente implementados
- Extensibilidad demostrada
- Desplegado en la nube
- README profesional

### Bueno (75-89%)
- Funcionalidad completa
- Código funcional pero mejorable
- Patrones presentes
- Documentación adecuada
- Funciona en local

### Suficiente (60-74%)
- Funcionalidad básica
- Algunos componentes faltantes
- Documentación mínima
- Patrones no tan claros

### Insuficiente (<60%)
- No funciona
- Componentes críticos faltantes
- Sin documentación
- No demuestra comprensión

---

## 📞 RECURSOS DE EMERGENCIA

Si algo falla:
1. **Revisa Personal_Proyect** - Solución de referencia
2. **Busca en documentación oficial** - Spring, React
3. **Usa versión simplificada** - Mejor algo simple que funcione
4. **Documenta el problema** - Explica qué intentaste

---

**¡Mucha suerte! 🍀 Confía en tu preparación. 💪**
