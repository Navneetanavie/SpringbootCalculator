package com.example.demo.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearestWarehouseResponse {
  private Long warehouseId;

  @JsonProperty("warehouseLocation")
  private WarehouseLocation warehouseLocation;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class WarehouseLocation {
    private Double lat;
    @JsonProperty("long")
    private Double _long;
  }
}
