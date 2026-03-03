# Logistics Shipping Calculator

A full-stack web application designed for B2B e-commerce to calculate shipping costs based on geographical distances, transport modes, and warehouse locations.

## Tech Stack Used
*   **Backend:** Java 17, Spring Boot 3.x, Spring Data JPA, Maven.
*   **Frontend:** React.js, Vite, Vanilla CSS.
*   **Testing:** JUnit 5, Mockito.
*   **Design Patterns:** Strategy Pattern (implemented for extensible transport pricing logic based on distance).

## Datastores Made
*   **H2 In-Memory Database:** Relational database used for state management.
*   **Core Entities:**
    *   `Customer` (ID, name, latitude, longitude, locationName, phoneNumber)
    *   `Product` (ID, sellerId, sellerName, productName, price, weight, dimension)
    *   `Warehouse` (ID, warehouseName, latitude, longitude, locationName)
*   **Data Seeding:** A `CommandLineRunner` automatically seeds dummy data for these 3 tables on application startup.

## Caching Used
*   **Spring Boot Cache (`ConcurrentMapCacheManager`)**: Activated globally using `@EnableCaching`.
*   The `@Cacheable("nearestWarehouse")` annotation is specifically applied to the `getNearestWarehouse` service method. This caches the geographically closest warehouse for a specific customer ID, drastically reducing expensive database queries and repeated mathematical Haversine calculations for static locations.

## Backend APIs & Test Cases
Comprehensive Unit Tests (`LogisticsServiceTest.java`) were written utilizing `Mockito` to isolate components. The following core API logic is thoroughly tested:
*   **`getNearestWarehouse(Long customerId)`**: 
    *   Tests successful DTO mapping and coordinate validation.
    *   Tests exception paths (e.g., throwing `ResourceNotFoundException` when Customer or Warehouse lists are empty).
*   **`calculateShippingCharge(CalculateChargeRequest request)`**: 
    *   Tests the pricing algorithms to ensure math calculates dynamically based on Standard vs. Express delivery speeds.
    *   Validates Strategy Pattern isolation by mocking the pricing strategies.
*   **`calculateDistance(...)`**: 
    *   Tests the Haversine formula implementation mapping coordinates on a sphere to guarantee distance bounds logic is accurate.

## Live Deployment
This architecture is proudly deployed on the cloud:

*   **Frontend (Vercel):** [https://springbootcalculator-o36z.vercel.app]([https://springboot-calculator.vercel.app](https://springboot-calculator.vercel.app)) *(Assumed based on Vercel's standard proxy domain generation)*
*   **Backend (Render):** `https://springbootcalculator-o36z.onrender.com`

*The Spring Boot Backend is containerized via Docker and orchestrated on Render. The frontend is built using Vite and statically served via Vercel's Edge network, communicating to the Render API securely utilizing custom Pre-Flight CORS definitions.*
