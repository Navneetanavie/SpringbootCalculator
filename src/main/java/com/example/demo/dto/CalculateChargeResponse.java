package com.example.demo.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculateChargeResponse {

  // Changing the structure completely to match the user's expected JSON:
  // {
  // "shippingCharge": 180.00,
  // "nearestWarehouse": {
  // "warehouseId": 789,
  // "warehouseLocation": { "lat": 12.99999, "long": 37.923273 }
  // }
  // }

  private Double shippingCharge;
  private NearestWarehouse nearestWarehouse;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class NearestWarehouse {
    private Long warehouseId;
    private WarehouseLocation warehouseLocation;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class WarehouseLocation {
    private Double lat;
    @JsonProperty("long")
    private Double _long;
  }
}
