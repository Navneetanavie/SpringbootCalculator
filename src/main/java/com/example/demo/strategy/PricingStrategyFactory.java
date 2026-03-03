package com.example.demo.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Factory class mapping correct Pricing Strategy from available Spring
 * components based on runtime distance.
 */
@Component
public class PricingStrategyFactory {

  private final List<TransportPricingStrategy> strategies;

  @Autowired
  public PricingStrategyFactory(List<TransportPricingStrategy> strategies) {
    this.strategies = strategies;
  }

  /**
   * Evaluate and acquire target algorithm dependency.
   */
  public TransportPricingStrategy getStrategyForDistance(double distanceKm) {
    for (TransportPricingStrategy strategy : strategies) {
      if (strategy.isApplicable(distanceKm)) {
        return strategy;
      }
    }
    throw new IllegalStateException("No pricing strategy available for distance: " + distanceKm);
  }
}
