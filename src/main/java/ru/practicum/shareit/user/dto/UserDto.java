package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {
    private Long id;
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @Email(message = "E-mail is not valid")
    private String email;
}
