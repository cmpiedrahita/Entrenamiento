import React, { useState } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import './App.css';

/**
 * CONCEPTOS: Hooks (useState), Async/Await, REST, Programación Funcional
 * 
 * MODIFICAR SI:
 * - Cambias URL del backend (línea 30)
 * - Agregas más intervalos (línea 24 y 115)
 * - Cambias librería de gráficos (línea 1 y 180)
 */
function App() {
  
  // ESTADO
  const [symbol, setSymbol] = useState('IBM');
  const [interval, setInterval] = useState('daily');
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  
  // CONFIGURACIÓN - MODIFICAR AQUÍ
  const BACKEND_URL = 'http://localhost:8080/api/stocks';
  // ⚠️ Cambiar cuando despliegues: 'https://tu-backend.com/api/stocks'
  
  // BUSCAR DATOS
  const fetchStockData = async () => {
    if (!symbol.trim()) {
      setError('Ingresa un símbolo');
      return;
    }
    
    setLoading(true);
    setError(null);
    
    try {
      const url = `${BACKEND_URL}/${symbol.toUpperCase()}/${interval}`;
      const response = await fetch(url);
      
      if (!response.ok) throw new Error(`Error ${response.status}`);
      
      const jsonData = await response.json();
      const chartData = transformData(jsonData, interval);
      setData(chartData);
      
    } catch (err) {
      setError(err.message);
      setData(null);
    } finally {
      setLoading(false);
    }
  };
  
  // TRANSFORMAR DATOS PARA RECHARTS
  const transformData = (jsonData, interval) => {
    let timeSeriesKey;
    switch(interval) {
      case 'intraday': timeSeriesKey = 'Time Series (5min)'; break;
      case 'daily': timeSeriesKey = 'Time Series (Daily)'; break;
      case 'weekly': timeSeriesKey = 'Weekly Time Series'; break;
      case 'monthly': timeSeriesKey = 'Monthly Time Series'; break;
      default: timeSeriesKey = 'Time Series (Daily)';
    }
    
    const timeSeries = jsonData[timeSeriesKey];
    if (!timeSeries) throw new Error('Formato inválido');
    
    return Object.entries(timeSeries)
      .slice(0, 30)
      .map(([date, values]) => ({
        date: date,
        price: parseFloat(values['4. close'])
      }))
      .reverse();
  };
  
  const handleKeyPress = (e) => {
    if (e.key === 'Enter') fetchStockData();
  };
  
  return (
    <div className="App">
      <header className="App-header">
        <h1>📈 Stock Market Viewer</h1>
        <p>Consulta el histórico de acciones</p>
      </header>
      
      <main className="App-main">
        <div className="search-section">
          <div className="input-group">
            <label>Símbolo:</label>
            <input
              type="text"
              value={symbol}
              onChange={(e) => setSymbol(e.target.value)}
              onKeyPress={handleKeyPress}
              placeholder="IBM, MSFT, AAPL"
              disabled={loading}
            />
          </div>
          
          <div className="input-group">
            <label>Intervalo:</label>
            <select
              value={interval}
              onChange={(e) => setInterval(e.target.value)}
              disabled={loading}
            >
              <option value="intraday">Intradiario (5min)</option>
              <option value="daily">Diario</option>
              <option value="weekly">Semanal</option>
              <option value="monthly">Mensual</option>
              {/* MODIFICAR: Agregar más opciones aquí */}
            </select>
          </div>
          
          <button onClick={fetchStockData} disabled={loading}>
            {loading ? '⏳ Cargando...' : '🔍 Buscar'}
          </button>
        </div>
        
        {error && <div className="error-message">❌ {error}</div>}
        
        {data && !loading && (
          <div className="chart-section">
            <h2>{symbol.toUpperCase()} - {interval}</h2>
            <ResponsiveContainer width="100%" height={400}>
              <LineChart data={data}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" angle={-45} textAnchor="end" height={80} />
                <YAxis label={{ value: 'Precio ($)', angle: -90, position: 'insideLeft' }} />
                <Tooltip />
                <Legend />
                <Line type="monotone" dataKey="price" stroke="#8884d8" strokeWidth={2} dot={false} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        )}
        
        {!data && !loading && !error && (
          <div className="instructions">
            <h3>💡 Instrucciones:</h3>
            <ol>
              <li>Ingresa símbolo (IBM, MSFT, AAPL)</li>
              <li>Selecciona intervalo</li>
              <li>Haz clic en Buscar</li>
            </ol>
          </div>
        )}
      </main>
      
      <footer className="App-footer">
        <p>Datos: Alpha Vantage API</p>
      </footer>
    </div>
  );
}

export default App;
