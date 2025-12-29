package com.mapper;

import com.dto.OrderDto;
import com.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    OrderDto convertToDTO(Order order);

    Order convertToEntity(OrderDto orderDTO);


}


