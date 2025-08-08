package com.example.digigoods.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.digigoods.exception.InvalidDiscountException;
import com.example.digigoods.model.Discount;
import com.example.digigoods.model.DiscountType;
import com.example.digigoods.repository.DiscountRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {

  @Mock
  private DiscountRepository discountRepository;

  @InjectMocks
  private DiscountService discountService;

  private Discount validDiscount;
  private Discount expiredDiscount;
  private Discount noUsesDiscount;

  @BeforeEach
  void setUp() {
    validDiscount = new Discount(1L, "VALID20", new BigDecimal("20.00"), DiscountType.GENERAL,
        LocalDate.now().minusDays(1), LocalDate.now().plusDays(30), 10, new HashSet<>());

    expiredDiscount = new Discount(2L, "EXPIRED10", new BigDecimal("10.00"), DiscountType.GENERAL,
        LocalDate.now().minusDays(30), LocalDate.now().minusDays(1), 5, new HashSet<>());

    noUsesDiscount = new Discount(3L, "NOUSES15", new BigDecimal("15.00"), DiscountType.GENERAL,
        LocalDate.now().minusDays(1), LocalDate.now().plusDays(30), 0, new HashSet<>());
  }

  @Test
  @DisplayName("Given empty discount codes, when validating discounts, then return empty list")
  void givenEmptyDiscountCodes_whenValidatingDiscounts_thenReturnEmptyList() {
    // Arrange
    List<String> discountCodes = List.of();

    // Act
    List<Discount> result = discountService.validateAndGetDiscounts(discountCodes);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given null discount codes, when validating discounts, then return empty list")
  void givenNullDiscountCodes_whenValidatingDiscounts_thenReturnEmptyList() {
    // Arrange
    List<String> discountCodes = null;

    // Act
    List<Discount> result = discountService.validateAndGetDiscounts(discountCodes);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given valid discount codes, when validating discounts, then return valid discounts")
  void givenValidDiscountCodes_whenValidatingDiscounts_thenReturnValidDiscounts() {
    // Arrange
    List<String> discountCodes = List.of("VALID20");
    when(discountRepository.findAllByCodeIn(discountCodes)).thenReturn(List.of(validDiscount));

    // Act
    List<Discount> result = discountService.validateAndGetDiscounts(discountCodes);

    // Assert
    assertEquals(1, result.size());
    assertEquals(validDiscount, result.get(0));
  }

  @Test
  @DisplayName("Given non-existent discount code, when validating discounts, "
      + "then throw InvalidDiscountException")
  void givenNonExistentDiscountCode_whenValidatingDiscounts_thenThrowInvalidDiscountException() {
    // Arrange
    List<String> discountCodes = List.of("NONEXISTENT");
    when(discountRepository.findAllByCodeIn(discountCodes)).thenReturn(List.of());

    // Act & Assert
    assertThrows(InvalidDiscountException.class,
        () -> discountService.validateAndGetDiscounts(discountCodes));
  }

  @Test
  @DisplayName("Given expired discount code, when validating discounts, "
      + "then throw InvalidDiscountException")
  void givenExpiredDiscountCode_whenValidatingDiscounts_thenThrowInvalidDiscountException() {
    // Arrange
    List<String> discountCodes = List.of("EXPIRED10");
    when(discountRepository.findAllByCodeIn(discountCodes)).thenReturn(List.of(expiredDiscount));

    // Act & Assert
    assertThrows(InvalidDiscountException.class,
        () -> discountService.validateAndGetDiscounts(discountCodes));
  }

  @Test
  @DisplayName("Given discount with no remaining uses, when validating discounts, "
      + "then throw InvalidDiscountException")
  void givenDiscountWithNoRemainingUses_whenValidatingDiscounts_thenThrowException() {
    // Arrange
    List<String> discountCodes = List.of("NOUSES15");
    when(discountRepository.findAllByCodeIn(discountCodes)).thenReturn(List.of(noUsesDiscount));

    // Act & Assert
    assertThrows(InvalidDiscountException.class,
        () -> discountService.validateAndGetDiscounts(discountCodes));
  }

  @Test
  @DisplayName("Given valid discounts, when updating discount usage, then decrement remaining uses")
  void givenValidDiscounts_whenUpdatingDiscountUsage_thenDecrementRemainingUses() {
    // Arrange
    List<Discount> discounts = List.of(validDiscount);
    int originalUses = validDiscount.getRemainingUses();

    // Act
    discountService.updateDiscountUsage(discounts);

    // Assert
    assertEquals(originalUses - 1, validDiscount.getRemainingUses());
    verify(discountRepository, times(1)).save(any(Discount.class));
  }
}
