package com.service;


import com.dto.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class UserServiceClient {

    private final RestTemplate restTemplate;

    public UserServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "fallBackGetUserByEmail")
    public UserDto getUserInfoByEmail(String email) {

        String userServiceUrl = "http://localhost:8080/email/" + email;
        return restTemplate.getForObject(userServiceUrl, UserDto.class);
    }

    public UserDto fallBackGetUserByEmail(String email, Throwable throwable) {
        log.error("User service is currently unavailable. Please try again later.");
        return null;
    }



}
