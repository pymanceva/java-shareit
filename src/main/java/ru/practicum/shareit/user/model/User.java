package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class User {
    private Long id;
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @NotNull
    @Email(message = "E-mail is not valid")
    private String email;
}
