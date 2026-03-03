package com.example.demo.strategy;

import org.springframework.stereotype.Component;

/**
 * Mini-Van pricing applied for hyper-local distances.
 */
@Component
public class MiniVanPricingStrategy implements TransportPricingStrategy {

  private static final double RATE_PER_KM_PER_KG = 3.0;

  @Override
  public boolean isApplicable(double distanceKm) {
    return distanceKm <= 100; // Anything up to 100km
  }

  @Override
  public String getTransportMode() {
    return "Mini Van";
  }

  @Override
  public double calculateCost(double distanceKm, double totalWeightKg) {
    return RATE_PER_KM_PER_KG * distanceKm * totalWeightKg;
  }
}
