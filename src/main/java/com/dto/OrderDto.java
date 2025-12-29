package com.dto;


import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

    private Long id;

    @NotNull(message = "Total price must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total price must be greater than 0")
    private BigDecimal totalPrice;

    @NotBlank(message = "Status must not be blank")
    private String status;


}
