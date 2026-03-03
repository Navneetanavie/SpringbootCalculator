import { useState } from 'react';
import './App.css';

function App() {
  const [selectedService, setSelectedService] = useState('nearest');
  const [customerId, setCustomerId] = useState('');
  const [warehouseData, setWarehouseData] = useState(null);

  const [calcData, setCalcData] = useState({
    customerId: '', warehouseId: '', deliverySpeed: 'Standard', productId: 1, quantity: 1
  });
  const [calcResult, setCalcResult] = useState(null);

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

  const findNearestWarehouse = async () => {
    try {
      const res = await fetch(`${API_BASE_URL}/api/logistics/nearest-warehouse/${customerId}`);
      if (!res.ok) throw new Error("Not found");
      const data = await res.json();
      setWarehouseData(data);
    } catch (err) {
      console.error(err);
      alert('Failed to fetch nearest warehouse. Is the ID correct?');
    }
  };

  const calculateCharge = async (useNearest) => {
    try {
      const payload = {
        customerId: calcData.customerId ? parseInt(calcData.customerId) : null,
        deliverySpeed: calcData.deliverySpeed,
        items: [{
          productId: calcData.productId ? parseInt(calcData.productId) : null,
          quantity: calcData.quantity ? parseInt(calcData.quantity) : null
        }]
      };
      if (!useNearest) {
        payload.warehouseId = calcData.warehouseId ? parseInt(calcData.warehouseId) : null;
      }

      const endpoint = useNearest ? '/calculate-nearest-charge' : '/calculate-charge';
      const res = await fetch(`${API_BASE_URL}/api/logistics${endpoint}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      if (!res.ok) throw new Error("Calculation failed");
      const data = await res.json();
      setCalcResult(data);
    } catch (err) {
      console.error(err);
      alert('Failed to calculate shipping charge. Ensure all IDs are valid.');
    }
  };

  return (
    <div className="container">
      <header className="header">
        <h1>Logistics Shipping Calculator</h1>
        <p>Premium B2B E-Commerce Marketplace Support</p>
      </header>

      <div className="mode-selector">
        <label>Select Service</label>
        <select value={selectedService} onChange={e => {
          setSelectedService(e.target.value);
          setWarehouseData(null);
          setCalcResult(null);
        }}>
          <option value="nearest">Find Nearest Warehouse</option>
          <option value="calculate">Calculate Shipping Cost</option>
        </select>
      </div>

      <div className="content-area">
        {selectedService === 'nearest' && (
          <div className="card">
            <h2>Find Nearest Warehouse</h2>
            <div className="form-group">
              <label>Customer ID (e.g., 1, 2, 3)</label>
              <input type="number" value={customerId} onChange={e => setCustomerId(e.target.value)} />
            </div>
            <button className="primary-btn" onClick={findNearestWarehouse}>Search</button>

            {warehouseData && (
              <div className="result-box">
                <h3>Nearest Warehouse Found:</h3>
                <p>ID: {warehouseData.warehouseId}</p>
                <p>Location: Lat {warehouseData.warehouseLocation.lat}, Long {warehouseData.warehouseLocation.long}</p>
              </div>
            )}
          </div>
        )}

        {selectedService === 'calculate' && (
          <div className="card">
            <h2>Calculate Shipping Cost</h2>
            <div className="form-group">
              <label>Customer ID (e.g., 1, 2, 3)</label>
              <input type="number" value={calcData.customerId} onChange={e => setCalcData({ ...calcData, customerId: e.target.value })} />
            </div>
            <div className="form-group">
              <label>Warehouse ID (optional for Nearest, e.g., 1, 2)</label>
              <input type="number" value={calcData.warehouseId} onChange={e => setCalcData({ ...calcData, warehouseId: e.target.value })} />
            </div>
            <div className="form-group">
              <label>Delivery Speed</label>
              <select value={calcData.deliverySpeed} onChange={e => setCalcData({ ...calcData, deliverySpeed: e.target.value })}>
                <option value="Standard">Standard</option>
                <option value="Express">Express</option>
              </select>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label>Product ID (e.g., 1, 2)</label>
                <input type="number" value={calcData.productId} onChange={e => setCalcData({ ...calcData, productId: e.target.value })} />
              </div>
              <div className="form-group">
                <label>Quantity</label>
                <input type="number" value={calcData.quantity} onChange={e => setCalcData({ ...calcData, quantity: e.target.value })} />
              </div>
            </div>
            <div className="button-group">
              <button className="primary-btn outline calc-btn" onClick={() => calculateCharge(false)}>Calculate from Warehouse ID</button>
              <button className="primary-btn calc-btn" onClick={() => calculateCharge(true)}>Calculate from Nearest</button>
            </div>

            {calcResult && (
              <div className="result-card" style={{ marginTop: '30px' }}>
                <h2>Calculation Result</h2>
                <div className="result-grid">
                  <div className="result-item highlight total"><span>Total Shipping:</span> ₹{calcResult.shippingCharge}</div>
                  <div className="result-item"><span>Warehouse ID:</span> {calcResult.nearestWarehouse.warehouseId}</div>
                  <div className="result-item"><span>Location:</span> Lat {calcResult.nearestWarehouse.warehouseLocation.lat}, Long {calcResult.nearestWarehouse.warehouseLocation.long}</div>
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

export default App;
