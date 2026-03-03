package com.example.demo.strategy;

import org.springframework.stereotype.Component;

/**
 * Aeroplane strategy applied for long-haul distances.
 */
@Component
public class AeroplanePricingStrategy implements TransportPricingStrategy {

  private static final double RATE_PER_KM_PER_KG = 1.0;

  @Override
  public boolean isApplicable(double distanceKm) {
    return distanceKm > 500;
  }

  @Override
  public String getTransportMode() {
    return "Aeroplane";
  }

  @Override
  public double calculateCost(double distanceKm, double totalWeightKg) {
    return RATE_PER_KM_PER_KG * distanceKm * totalWeightKg;
  }
}
