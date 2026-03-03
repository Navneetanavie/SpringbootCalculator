package com.example.demo.service;

import com.example.demo.dto.CalculateChargeRequest;
import com.example.demo.dto.CalculateChargeResponse;
import com.example.demo.dto.NearestWarehouseResponse;
import com.example.demo.dto.CartItem;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Customer;
import com.example.demo.model.Product;
import com.example.demo.model.Warehouse;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * Service orchestrating the logistics capabilities like locating warehouses
 * and determining total shipping costs.
 */
@Service
@RequiredArgsConstructor
public class LogisticsService {

  private final CustomerRepository customerRepository;
  private final ProductRepository productRepository;
  private final WarehouseRepository warehouseRepository;
  private final com.example.demo.strategy.PricingStrategyFactory pricingStrategyFactory;

  private static final int EARTH_RADIUS_KM = 6371;

  /**
   * Identifies the closest warehouse dynamically utilizing geographical
   * calculation.
   * 
   * @param customerId ID of the respective customer requesting logistics.
   * @return Warehouse entity modeling the location details.
   */
  public Warehouse getNearestWarehouseEntity(Long customerId) {
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

    List<Warehouse> warehouses = warehouseRepository.findAll();
    if (warehouses.isEmpty()) {
      throw new ResourceNotFoundException("No warehouses available in the system.");
    }

    Warehouse nearest = null;
    double minDistance = Double.MAX_VALUE;

    for (Warehouse warehouse : warehouses) {
      double distance = calculateDistance(customer.getLatitude(), customer.getLongitude(),
          warehouse.getLatitude(), warehouse.getLongitude());
      if (distance < minDistance) {
        minDistance = distance;
        nearest = warehouse;
      }
    }

    return nearest;
  }

  /**
   * Retrieves the DTO matching nearest warehouse structure requested.
   * 
   * @param customerId ID of the respective customer.
   * @return NearestWarehouseResponse capturing explicit location structure.
   */
  @Cacheable("nearestWarehouse")
  public NearestWarehouseResponse getNearestWarehouse(Long customerId) {
    Warehouse nearest = getNearestWarehouseEntity(customerId);
    NearestWarehouseResponse response = new NearestWarehouseResponse();
    response.setWarehouseId(nearest.getWarehouseId());
    response.setWarehouseLocation(
        new NearestWarehouseResponse.WarehouseLocation(nearest.getLatitude(), nearest.getLongitude()));
    return response;
  }

  /**
   * Computes complex shipping and delivery fees leveraging warehouse proximity,
   * item quantity, and specified transportation modes.
   * 
   * @param request Valid payload capturing user requirements.
   * @return Encapsulated resulting response detailing cost.
   */
  public CalculateChargeResponse calculateShippingCharge(CalculateChargeRequest request) {
    Customer customer = customerRepository.findById(request.getCustomerId())
        .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + request.getCustomerId()));

    Warehouse warehouse;
    if (request.getWarehouseId() != null) {
      warehouse = warehouseRepository.findById(request.getWarehouseId())
          .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + request.getWarehouseId()));
    } else {
      warehouse = getNearestWarehouseEntity(customer.getCustomerId());
    }

    double distance = calculateDistance(customer.getLatitude(), customer.getLongitude(),
        warehouse.getLatitude(), warehouse.getLongitude());

    com.example.demo.strategy.TransportPricingStrategy strategy = pricingStrategyFactory
        .getStrategyForDistance(distance);

    double totalWeight = 0;

    for (CartItem item : request.getItems()) {
      Product product = productRepository.findById(item.getProductId())
          .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + item.getProductId()));

      totalWeight += (product.getWeight() * item.getQuantity());
    }

    double calculatedShippingCharge = strategy.calculateCost(distance, totalWeight);
    double deliverySpeedCost = 10.0; // Standard courier charge

    if ("Express".equalsIgnoreCase(request.getDeliverySpeed())) {
      deliverySpeedCost += (1.2 * totalWeight); // Extra express charge
    }

    double totalShippingCost = deliverySpeedCost + calculatedShippingCharge;

    CalculateChargeResponse response = new CalculateChargeResponse();
    response.setShippingCharge(Math.round(totalShippingCost * 100.0) / 100.0);

    CalculateChargeResponse.WarehouseLocation wLoc = new CalculateChargeResponse.WarehouseLocation(
        warehouse.getLatitude(), warehouse.getLongitude());
    CalculateChargeResponse.NearestWarehouse nestedW = new CalculateChargeResponse.NearestWarehouse(
        warehouse.getWarehouseId(), wLoc);

    response.setNearestWarehouse(nestedW);

    return response;
  }

  /**
   * Basic application of the Haversine formula mapping two geo coordinates on a
   * sphere.
   * 
   * @return The resulting separation distance computed in kilometers.
   */
  public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);

    lat1 = Math.toRadians(lat1);
    lat2 = Math.toRadians(lat2);

    double a = Math.pow(Math.sin(dLat / 2), 2) +
        Math.pow(Math.sin(dLon / 2), 2) *
            Math.cos(lat1) *
            Math.cos(lat2);
    double c = 2 * Math.asin(Math.sqrt(a));
    return EARTH_RADIUS_KM * c;
  }
}
