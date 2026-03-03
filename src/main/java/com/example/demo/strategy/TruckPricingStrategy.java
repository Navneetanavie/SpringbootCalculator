package com.example.demo.strategy;

import org.springframework.stereotype.Component;

/**
 * Truck strategy applied for cross-state distances.
 */
@Component
public class TruckPricingStrategy implements TransportPricingStrategy {

  private static final double RATE_PER_KM_PER_KG = 2.0;

  @Override
  public boolean isApplicable(double distanceKm) {
    return distanceKm > 100 && distanceKm <= 500;
  }

  @Override
  public String getTransportMode() {
    return "Truck";
  }

  @Override
  public double calculateCost(double distanceKm, double totalWeightKg) {
    return RATE_PER_KM_PER_KG * distanceKm * totalWeightKg;
  }
}
