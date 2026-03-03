package com.example.demo.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {
  private Double lat;
  private Double _long; // Note: 'long' is a reserved keyword in Java
}
