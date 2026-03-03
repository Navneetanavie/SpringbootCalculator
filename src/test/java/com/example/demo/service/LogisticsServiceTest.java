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
import com.example.demo.strategy.PricingStrategyFactory;
import com.example.demo.strategy.TransportPricingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LogisticsServiceTest {

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private WarehouseRepository warehouseRepository;

  @Mock
  private PricingStrategyFactory pricingStrategyFactory;

  @Mock
  private TransportPricingStrategy pricingStrategy;

  @InjectMocks
  private LogisticsService logisticsService;

  private Customer customer;
  private Warehouse warehouse;
  private Product product;

  @BeforeEach
  void setUp() {
    customer = new Customer();
    customer.setCustomerId(1L);
    customer.setLatitude(12.9716);
    customer.setLongitude(77.5946); // Bangalore

    warehouse = new Warehouse();
    warehouse.setWarehouseId(1L);
    warehouse.setLatitude(13.0827);
    warehouse.setLongitude(80.2707); // Chennai

    product = new Product();
    product.setProductId(1L);
    product.setWeight(10.0);
  }

  @Test
  void testGetNearestWarehouse_Success() {
    when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
    when(warehouseRepository.findAll()).thenReturn(Collections.singletonList(warehouse));

    NearestWarehouseResponse response = logisticsService.getNearestWarehouse(1L);

    assertNotNull(response);
    assertEquals(1L, response.getWarehouseId());
    assertEquals(13.0827, response.getWarehouseLocation().getLat());
    assertEquals(80.2707, response.getWarehouseLocation().get_long());
  }

  @Test
  void testGetNearestWarehouse_CustomerNotFound() {
    when(customerRepository.findById(2L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> logisticsService.getNearestWarehouse(2L));
  }

  @Test
  void testGetNearestWarehouse_NoWarehouses() {
    when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
    when(warehouseRepository.findAll()).thenReturn(Collections.emptyList());

    assertThrows(ResourceNotFoundException.class, () -> logisticsService.getNearestWarehouse(1L));
  }

  @Test
  void testCalculateDistance() {
    // Distance roughly ~284km from BLR to Chennai
    double distance = logisticsService.calculateDistance(12.9716, 77.5946, 13.0827, 80.2707);
    assertTrue(distance > 280 && distance < 350);
  }

  @Test
  void testCalculateShippingCharge_Success() {
    CalculateChargeRequest request = new CalculateChargeRequest();
    request.setCustomerId(1L);
    request.setWarehouseId(1L);
    request.setDeliverySpeed("Standard");

    CartItem item = new CartItem();
    item.setProductId(1L);
    item.setQuantity(2); // total weight 20kg
    request.setItems(Collections.singletonList(item));

    when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
    when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(pricingStrategyFactory.getStrategyForDistance(anyDouble())).thenReturn(pricingStrategy);

    // Mock Strategy response: mock it returning 500 flat for easy asserting
    when(pricingStrategy.calculateCost(anyDouble(), anyDouble())).thenReturn(500.0);

    CalculateChargeResponse response = logisticsService.calculateShippingCharge(request);

    assertNotNull(response);
    // Base standard speed = 10, calculateCost = 500 -> 510 total expected
    assertEquals(510.0, response.getShippingCharge());
    assertEquals(1L, response.getNearestWarehouse().getWarehouseId());
  }

  @Test
  void testCalculateShippingCharge_ExpressSpeed() {
    CalculateChargeRequest request = new CalculateChargeRequest();
    request.setCustomerId(1L);
    request.setWarehouseId(1L);
    request.setDeliverySpeed("Express");

    CartItem item = new CartItem();
    item.setProductId(1L);
    item.setQuantity(2); // total weight 20kg
    request.setItems(Collections.singletonList(item));

    when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
    when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(pricingStrategyFactory.getStrategyForDistance(anyDouble())).thenReturn(pricingStrategy);
    when(pricingStrategy.calculateCost(anyDouble(), anyDouble())).thenReturn(500.0);

    CalculateChargeResponse response = logisticsService.calculateShippingCharge(request);

    assertNotNull(response);
    // Base express = 10 + (1.2 * 20kg) = 34
    // CalculateCost = 500
    // Expected = 534
    assertEquals(534.0, response.getShippingCharge());
  }
}
