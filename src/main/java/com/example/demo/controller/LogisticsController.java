package com.example.demo.controller;

import com.example.demo.dto.CalculateChargeRequest;
import com.example.demo.dto.CalculateChargeResponse;
import com.example.demo.dto.NearestWarehouseResponse;
import com.example.demo.exception.InvalidRequestException;
import com.example.demo.service.LogisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * Controller exposing REST APIs for logistics shipping.
 */
@RestController
@RequestMapping("/api/logistics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LogisticsController {

  private final LogisticsService logisticsService;

  /**
   * Fetches the nearest warehouse details for a customer.
   * 
   * @param customerId ID of the customer.
   * @return NearestWarehouseResponse representing location information.
   */
  @GetMapping("/nearest-warehouse/{customerId}")
  public ResponseEntity<NearestWarehouseResponse> getNearestWarehouse(@PathVariable Long customerId) {
    if (customerId == null || customerId <= 0) {
      throw new InvalidRequestException("Invalid Customer ID.");
    }
    return ResponseEntity.ok(logisticsService.getNearestWarehouse(customerId));
  }

  /**
   * Calculates the shipping via an explicitly passed warehouse.
   * 
   * @param request Contains products and warehouse target.
   */
  @PostMapping("/calculate-charge")
  public ResponseEntity<CalculateChargeResponse> calculateCharge(@Valid @RequestBody CalculateChargeRequest request) {
    if (request.getWarehouseId() == null) {
      throw new InvalidRequestException("Warehouse ID is required for a direct calculation request.");
    }
    return ResponseEntity.ok(logisticsService.calculateShippingCharge(request));
  }

  /**
   * Automatically calculates shipping from the mathematically nearest warehouse.
   */
  @PostMapping("/calculate-nearest-charge")
  public ResponseEntity<CalculateChargeResponse> calculateNearestCharge(
      @Valid @RequestBody CalculateChargeRequest request) {
    request.setWarehouseId(null); // Force using nearest
    return ResponseEntity.ok(logisticsService.calculateShippingCharge(request));
  }
}
