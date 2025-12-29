package com.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    private String firstName;

    private String surname;

    private LocalDate birthDate;

    private String email;

    private Boolean active;

}
