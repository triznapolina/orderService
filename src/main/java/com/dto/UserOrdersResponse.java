package com.dto;


import com.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOrdersResponse {
    private UserDto user;
    private List<Order> orders;
}
