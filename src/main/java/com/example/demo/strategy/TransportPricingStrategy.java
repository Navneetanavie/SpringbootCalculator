package com.example.demo.strategy;

/**
 * Strategy interface defining contract for computing a shipping cost based on
 * distance and weight.
 */
public interface TransportPricingStrategy {

  /**
   * Determines whether this strategy applies to the given transport distance.
   * 
   * @param distanceKm the computed distance in km.
   * @return true if the strategy accommodates this distance.
   */
  boolean isApplicable(double distanceKm);

  /**
   * @return the string name distinguishing the chosen transport.
   */
  String getTransportMode();

  /**
   * Compute underlying cost of the freight.
   * 
   * @param distanceKm    Geographic distance.
   * @param totalWeightKg Total weight of all cargo items.
   * @return Raw transport cost.
   */
  double calculateCost(double distanceKm, double totalWeightKg);
}
