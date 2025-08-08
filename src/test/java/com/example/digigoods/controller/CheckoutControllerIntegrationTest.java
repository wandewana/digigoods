package com.example.digigoods.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.digigoods.dto.CheckoutRequest;
import com.example.digigoods.model.Discount;
import com.example.digigoods.model.DiscountType;
import com.example.digigoods.model.Product;
import com.example.digigoods.model.User;
import com.example.digigoods.repository.DiscountRepository;
import com.example.digigoods.repository.ProductRepository;
import com.example.digigoods.repository.UserRepository;
import com.example.digigoods.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class CheckoutControllerIntegrationTest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private DiscountRepository discountRepository;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  private User testUser;
  private Product product1;
  private Product product2;
  private Discount generalDiscount;
  private String jwtToken;

  @BeforeEach
  void setUp() {
    // Set up MockMvc
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    // Clear and set up test data
    // Create test user
    testUser = new User();
    testUser.setUsername("testuser");
    testUser.setPassword(passwordEncoder.encode("password"));
    testUser = userRepository.save(testUser);

    // Create test products
    product1 = new Product();
    product1.setName("Product 1");
    product1.setPrice(new BigDecimal("100.00"));
    product1.setStock(10);
    product1 = productRepository.save(product1);

    product2 = new Product();
    product2.setName("Product 2");
    product2.setPrice(new BigDecimal("50.00"));
    product2.setStock(5);
    product2 = productRepository.save(product2);

    // Create test discount
    generalDiscount = new Discount();
    generalDiscount.setCode("GENERAL20");
    generalDiscount.setPercentage(new BigDecimal("20.00"));
    generalDiscount.setType(DiscountType.GENERAL);
    generalDiscount.setValidFrom(LocalDate.now().minusDays(1));
    generalDiscount.setValidUntil(LocalDate.now().plusDays(30));
    generalDiscount.setRemainingUses(10);
    generalDiscount.setApplicableProducts(new HashSet<>());
    generalDiscount = discountRepository.save(generalDiscount);

    // Generate JWT token
    jwtToken = jwtService.generateToken(testUser.getId(), testUser.getUsername());
  }

  @Test
  @DisplayName("Given valid checkout request with JWT, when creating order, then return success")
  void givenValidCheckoutRequestWithJwt_whenCreatingOrder_thenReturnSuccess() throws Exception {
    // Arrange
    CheckoutRequest request = new CheckoutRequest(
        testUser.getId(),
        List.of(product1.getId(), product2.getId()),
        List.of("GENERAL20")
    );

    // Act & Assert
    mockMvc.perform(post("/orders")
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Order created successfully!"))
        .andExpect(jsonPath("$.finalPrice").value(120.00)); // (100 + 50) * 0.8 = 120
  }

  @Test
  @DisplayName("Given checkout request without JWT, when creating order, then return unauthorized")
  void givenCheckoutRequestWithoutJwt_whenCreatingOrder_thenReturnUnauthorized() throws Exception {
    // Arrange
    CheckoutRequest request = new CheckoutRequest(
        testUser.getId(),
        List.of(product1.getId()),
        List.of()
    );

    // Act & Assert
    mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("JWT token is missing or invalid"));
  }

  @Test
  @DisplayName("Given checkout request for different user, when creating order, "
      + "then return forbidden")
  void givenCheckoutRequestForDifferentUser_whenCreatingOrder_thenReturnForbidden()
      throws Exception {
    // Arrange
    CheckoutRequest request = new CheckoutRequest(
        999L, // Different user ID
        List.of(product1.getId()),
        List.of()
    );

    // Act & Assert
    mockMvc.perform(post("/orders")
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("User cannot place order for another user"));
  }

  @Test
  @DisplayName("Given checkout request with invalid product, when creating order, "
      + "then return not found")
  void givenCheckoutRequestWithInvalidProduct_whenCreatingOrder_thenReturnNotFound()
      throws Exception {
    // Arrange
    CheckoutRequest request = new CheckoutRequest(
        testUser.getId(),
        List.of(999L), // Non-existent product ID
        List.of()
    );

    // Act & Assert
    mockMvc.perform(post("/orders")
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  @DisplayName("Given checkout request with invalid discount, when creating order, "
      + "then return bad request")
  void givenCheckoutRequestWithInvalidDiscount_whenCreatingOrder_thenReturnBadRequest()
      throws Exception {
    // Arrange
    CheckoutRequest request = new CheckoutRequest(
        testUser.getId(),
        List.of(product1.getId()),
        List.of("INVALID_CODE")
    );

    // Act & Assert
    mockMvc.perform(post("/orders")
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists());
  }
}
