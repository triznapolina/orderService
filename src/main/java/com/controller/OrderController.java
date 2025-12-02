package com.controller;

import com.dto.UserDto;
import com.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/order")
public class OrderController {

    private final UserService userService;

    public OrderController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/{email}")
    public UserDto getUserById(@PathVariable String email) {
        return userService.getUserInfoByEmail(email);
    }

}
