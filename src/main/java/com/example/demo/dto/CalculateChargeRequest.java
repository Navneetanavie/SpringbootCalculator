package com.example.demo.dto;

import lombok.Data;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;

@Data
public class CalculateChargeRequest {
  @NotNull(message = "Customer ID is required")
  private Long customerId;

  private Long warehouseId;

  @NotNull(message = "Delivery speed must be specified")
  private String deliverySpeed; // "Standard" or "Express"

  @NotEmpty(message = "Cart items cannot be empty")
  @Valid
  private List<CartItem> items;
}
