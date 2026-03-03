package com.example.demo.config;

import com.example.demo.model.Customer;
import com.example.demo.model.Product;
import com.example.demo.model.Warehouse;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

  private final CustomerRepository customerRepository;
  private final ProductRepository productRepository;
  private final WarehouseRepository warehouseRepository;

  @Bean
  public CommandLineRunner loadData() {
    return args -> {
      // Warehouses
      Warehouse w1 = new Warehouse();
      w1.setWarehouseName("Delhi Central Hub");
      w1.setLatitude(28.7041);
      w1.setLongitude(77.1025);
      w1.setLocationName("Delhi");
      warehouseRepository.save(w1);

      Warehouse w2 = new Warehouse();
      w2.setWarehouseName("Mumbai Port Area");
      w2.setLatitude(19.0760);
      w2.setLongitude(72.8777);
      w2.setLocationName("Mumbai");
      warehouseRepository.save(w2);

      Warehouse w3 = new Warehouse();
      w3.setWarehouseName("Bangalore South");
      w3.setLatitude(12.9716);
      w3.setLongitude(77.5946);
      w3.setLocationName("Bangalore");
      warehouseRepository.save(w3);

      // Customers
      Customer c1 = new Customer();
      c1.setName("Alice Sharma");
      c1.setLatitude(28.5355); // near Delhi Central Hub (less than 100km)
      c1.setLongitude(77.3910); // Noida
      c1.setLocationName("Noida");
      c1.setPhoneNumber("9876543210");
      customerRepository.save(c1);

      Customer c2 = new Customer();
      c2.setName("Bob Gupta");
      c2.setLatitude(26.9124); // Jaipur (between 100-500km from Delhi)
      c2.setLongitude(75.7873);
      c2.setLocationName("Jaipur");
      c2.setPhoneNumber("9988776655");
      customerRepository.save(c2);

      Customer c3 = new Customer();
      c3.setName("Charlie Iyer");
      c3.setLatitude(13.0827); // Chennai (>500km from Bangalore, nearest)
      c3.setLongitude(80.2707);
      c3.setLocationName("Chennai");
      c3.setPhoneNumber("9123456789");
      customerRepository.save(c3);

      // Products
      Product p1 = new Product();
      p1.setProductName("Laptop");
      p1.setPrice(50000.0);
      p1.setWeight(2.5);
      p1.setDimension("35x25x2 cm");
      p1.setSellerId(1L);
      p1.setSellerName("Tech Store");
      productRepository.save(p1);

      Product p2 = new Product();
      p2.setProductName("Desk Chair");
      p2.setPrice(5000.0);
      p2.setWeight(15.0);
      p2.setDimension("50x50x100 cm");
      p2.setSellerId(2L);
      p2.setSellerName("Furniture Co");
      productRepository.save(p2);
    };
  }
}
